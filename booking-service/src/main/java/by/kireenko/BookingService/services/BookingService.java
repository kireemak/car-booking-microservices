package by.kireenko.BookingService.services;


import by.kireenko.BookingService.client.CarServiceClient;
import by.kireenko.BookingService.dto.BookingDto;
import by.kireenko.BookingService.dto.CarDto;
import by.kireenko.BookingService.dto.CreateBookingRequestDto;
import by.kireenko.BookingService.dto.UpdateBookingRequestDto;
import by.kireenko.BookingService.error.NotValidResourceState;
import by.kireenko.BookingService.error.ResourceNotFoundException;
import by.kireenko.BookingService.kafka.BookingEventPublisher;
import by.kireenko.BookingService.models.Booking;
import by.kireenko.BookingService.models.UserView;
import by.kireenko.BookingService.repositories.BookingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class BookingService {
    private final CarServiceClient carServiceClient;
    private final BookingRepository bookingRepository;
    private final UserViewService userViewService;
    private final BookingEventPublisher bookingEventPublisher;

    @Autowired
    public BookingService(CarServiceClient carServiceClient, BookingRepository bookingRepository,
                          UserViewService userViewService, BookingEventPublisher bookingEventPublisher) {
        this.carServiceClient = carServiceClient;
        this.bookingRepository = bookingRepository;
        this.userViewService = userViewService;
        this.bookingEventPublisher = bookingEventPublisher;
    }

    public List<Booking> getAllBookings() {
        log.info("Fetching all bookings");
        return bookingRepository.findAll();
    }

    public List<BookingDto> getAllBookingsDto() {
        List<Booking> bookings = getAllBookings();
        if (bookings.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> carIds = bookings.stream()
                .map(Booking::getCarId)
                .distinct()
                .collect(Collectors.toList());

        List<CarDto> carDtos = carServiceClient.getCarsByIds(carIds);
        Map<Long, CarDto> carDtoMap = carDtos.stream().collect(Collectors.toMap(CarDto::getId, car -> car));

        return bookings.stream()
                .map(booking -> {
                    CarDto carDto = carDtoMap.get(booking.getCarId());
                    return new BookingDto(booking, carDto);
                })
                .collect(Collectors.toList());
    }

    public List<Booking> getCurrentUserBookings() {
        UserView userView = userViewService.getCurrentUserView();
        return bookingRepository.findByUserViewId(userView.getId());
    }

    public List<BookingDto> getCurrentUserBookingsDto() {
        List<Booking> bookingList = getCurrentUserBookings();

        if (bookingList.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> carIds = bookingList.stream()
                .map(Booking::getCarId)
                .distinct()
                .toList();

        List<CarDto> carDtos = carServiceClient.getCarsByIds(carIds);

        Map<Long, CarDto> carDtoMap = carDtos.stream().collect(Collectors.toMap(CarDto::getId, car -> car));

        return bookingList.stream()
                .map(booking -> {
                    CarDto carDto = carDtoMap.get(booking.getCarId());
                    return new BookingDto(booking, carDto);
                })
                .toList();
    }

    public Booking getBookingById(Long id) {
        UserView userView = userViewService.getCurrentUserView();
        Booking booking = bookingRepository.findById(id).orElseThrow(
                () -> {
                    log.warn("Booking with id {} not found", id);
                    return new ResourceNotFoundException("Booking", "id", id);
                }
        );

        if (validateAccess(booking, userView)) {
            log.warn("Access denied for user {} to get booking {}", userView.getName(), id);
            throw new AccessDeniedException("You are not allowed to view this booking");
        }

        return booking;
    }

    @Transactional
    public Booking getBookingWithLockById(Long id) {
        UserView userView = userViewService.getCurrentUserView();
        Booking booking = bookingRepository.findAndLockById(id).orElseThrow(
                () -> {
                    log.warn("Booking with id {} not found", id);
                    return new ResourceNotFoundException("Booking", "id", id);
                }
        );

        if (validateAccess(booking, userView)) {
            log.warn("Access denied for user {} to get booking {}", userView.getName(), id);
            throw new AccessDeniedException("You are not allowed to view this booking");
        }

        return booking;
    }

    @Transactional
    public Booking createBooking(CreateBookingRequestDto bookingRequestDto) {
        UserView userView = userViewService.getCurrentUserView();
        CarDto carDto = carServiceClient.getCarById(bookingRequestDto.getCarId());
        if (carDto == null) {
            throw new ResourceNotFoundException("Car", "id", bookingRequestDto.getCarId());
        }

        Booking booking = new Booking();
        booking.setCarId(bookingRequestDto.getCarId());
        booking.setUserView(userView);
        booking.setStartDate(bookingRequestDto.getStartDate());
        booking.setEndDate(bookingRequestDto.getEndDate());
        booking.setStatus("Created");

        Booking createdBooking = bookingRepository.save(booking);

        bookingEventPublisher.sendBookingCreatedEvent(createdBooking);

        return createdBooking;
    }

    @Transactional(readOnly = false)
    public Booking updateBooking(Long id, UpdateBookingRequestDto updatedBookingRequest) {
        UserView userView = userViewService.getCurrentUserView();
        Booking existingBooking = getBookingById(id);

        if (validateAccess(existingBooking, userView)) {
            log.error("Access denied for user {} to update booking {}", userView.getName(), id);
            throw new AccessDeniedException("You can't update this booking");
        }

        if (!existingBooking.getStatus().equals("Created")) {
            log.error("Booking {} status is not valid: {}", id, existingBooking.getStatus());
            throw new IllegalStateException("Only bookings with status CREATED can be updated");
        }

        if (updatedBookingRequest.getStartDate() != null)
            existingBooking.setStartDate(updatedBookingRequest.getStartDate());
        if (updatedBookingRequest.getEndDate() != null)
            existingBooking.setEndDate(updatedBookingRequest.getEndDate());
        if (updatedBookingRequest.getStatus() != null)
            existingBooking.setStatus(updatedBookingRequest.getStatus());

        Booking updatedBooking = bookingRepository.save(existingBooking);

        bookingEventPublisher.sendBookingUpdatedEvent(updatedBooking);

        return updatedBooking;
    }

    @Transactional(readOnly = false)
    public void deleteBooking(Long id) {
        UserView userView = userViewService.getCurrentUserView();
        Booking booking = getBookingById(id);

        if (validateAccess(booking, userView)) {
            log.error("Access denied for user {} to delete booking {}", userView.getName(), id);
            throw new AccessDeniedException("You can't delete this booking");
        }

        if (!(booking.getStatus().equals("Created") || booking.getStatus().equals("Cancelled"))) {
            log.error("Booking {} status is not valid: {}", id, booking.getStatus());
            throw new IllegalStateException("Only CREATED or CANCELLED bookings can be deleted");
        }

        bookingEventPublisher.sendBookingDeletedEvent(id);

        bookingRepository.deleteById(id);
    }

    @Transactional(readOnly = false)
    public Booking createBookingWithCheck(CreateBookingRequestDto bookingRequestDto) {
        try {
            carServiceClient.reserveCar(bookingRequestDto.getCarId());
        } catch (WebClientResponseException.Forbidden e) {
            log.warn("Failed to reserve car {} because it was not available.", bookingRequestDto.getCarId());
            throw new NotValidResourceState("Car is not available for booking.");
        }

        try {
            return createBooking(bookingRequestDto);
        } catch(Exception e) {
            log.error("Error creating booking. Executing compensation: Releasing car {}", bookingRequestDto.getCarId(), e);
            carServiceClient.releaseCar(bookingRequestDto.getCarId());
            throw e;
        }
    }

    @Transactional(readOnly = false)
    public Booking completeBooking(Long bookingId) {
        UserView userView = userViewService.getCurrentUserView();
        Booking booking = getBookingWithLockById(bookingId);

        if (validateAccess(booking, userView)) {
            log.warn("Access denied for user {} to complete booking {}", userView.getId(), bookingId);
            throw new AccessDeniedException("You can't complete this booking");
        }

        carServiceClient.releaseCar(booking.getCarId());

        booking.setStatus("Completed");
        Booking completedBooking = bookingRepository.save(booking);
        bookingEventPublisher.sendBookingUpdatedEvent(completedBooking);

        return completedBooking;
    }

    private boolean validateAccess(Booking booking, UserView userView) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return !isAdmin && !userView.getId().equals(booking.getUserView().getId());
    }
}