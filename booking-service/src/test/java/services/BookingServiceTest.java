package services;

import by.kireenko.BookingService.client.CarServiceClient;
import by.kireenko.BookingService.dto.CarDto;
import by.kireenko.BookingService.dto.CreateBookingRequestDto;
import by.kireenko.BookingService.dto.event.BookingRequestedEvent;
import by.kireenko.BookingService.error.NotValidResourceState;
import by.kireenko.BookingService.kafka.BookingEventPublisher;
import by.kireenko.BookingService.models.Booking;
import by.kireenko.BookingService.models.UserView;
import by.kireenko.BookingService.repositories.BookingRepository;
import by.kireenko.BookingService.services.BookingService;
import by.kireenko.BookingService.services.UserViewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock private UserViewService userViewService;
    @Mock private CarServiceClient carServiceClient;
    @Mock private BookingEventPublisher bookingEventPublisher;

    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private BookingService bookingService;

    private void mockSecurityContext(boolean isAdmin) {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        List<SimpleGrantedAuthority> authorities = isAdmin
                ? List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                : List.of(new SimpleGrantedAuthority("ROLE_USER"));

        doReturn(authorities).when(authentication).getAuthorities();
    }

    @Test
    public void getBookingById_WhenBookingExistsAndUserIsOwner_ShouldReturnBooking() {
        UserView user = new UserView(10L, "test", "test@test.com", "123");
        Booking booking = new Booking();
        booking.setId(11L);
        booking.setUserView(user);

        when(userViewService.getCurrentUserView()).thenReturn(user);
        when(bookingRepository.findById(11L)).thenReturn(Optional.of(booking));
        mockSecurityContext(false);

        Booking result = bookingService.getBookingById(11L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(11L);
        verify(bookingRepository).findById(11L);
    }

    @Test
    public void getBookingById_WhenUserIsNotOwner_ShouldThrowException() {
        UserView owner = new UserView(10L, "owner", "o@t.com", "1");
        UserView hacker = new UserView(22L, "hacker", "h@t.com", "2");

        Booking booking = new Booking();
        booking.setId(11L);
        booking.setUserView(owner);

        when(userViewService.getCurrentUserView()).thenReturn(hacker);
        when(bookingRepository.findById(11L)).thenReturn(Optional.of(booking));
        mockSecurityContext(false);

        assertThrows(AccessDeniedException.class, () -> bookingService.getBookingById(11L));
    }

    @Test
    public void createBookingWithCheck_ShouldSavePendingBookingAndPublishEvent() {
        Long carId = 1L;
        CreateBookingRequestDto request = new CreateBookingRequestDto();
        request.setCarId(carId);
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(2));

        UserView user = new UserView(1L, "user", "mail", "phone");

        when(userViewService.getCurrentUserView()).thenReturn(user);

        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking b = invocation.getArgument(0);
            b.setId(100L);
            return b;
        });

        Booking result = bookingService.createBookingWithCheck(request);

        assertThat(result).isNotNull();
        assertThat(result.getCarId()).isEqualTo(carId);
        assertThat(result.getStatus()).isEqualTo("PENDING");

        verify(bookingRepository).save(any(Booking.class));
        verify(bookingEventPublisher).sendBookingRequestedEvent(any(BookingRequestedEvent.class));
        verifyNoInteractions(carServiceClient);
    }
}