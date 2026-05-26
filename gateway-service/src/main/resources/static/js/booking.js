const bookingForm = document.querySelector('[data-ui="booking-form"]');
const resultDiv = document.querySelector('[data-target="booking-result"]');

// Fetch available cars to populate the dropdown on load
async function loadCarOptions() {
    const token = localStorage.getItem('jwtToken');
    if (!token) {
        window.location.href = '/login.html';
        return;
    }

    try {
        const response = await fetch('http://localhost:8080/api/cars/available', {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!response.ok) throw new Error("Failed to fetch cars");

        const cars = await response.json();
        const carSelect = bookingForm.elements['carId'];

        carSelect.innerHTML = '<option value="" disabled selected>Select a car</option>';

        cars.forEach(car => {
            const option = document.createElement('option');
            option.value = car.id;
            option.textContent = `${car.brand} ${car.model} (${car.year}) - $${car.rentalPrice}/day`;
            carSelect.appendChild(option);
        });

    } catch (error) {
        console.error('Error loading car options:', error);
        bookingForm.elements['carId'].innerHTML = '<option value="" disabled>Error loading cars</option>';
    }
}

// Handle Form Submission
bookingForm?.addEventListener('submit', async (event) => {
    event.preventDefault();

    const bookingData = {
        carId: bookingForm.elements['carId'].value,
        startDate: bookingForm.elements['startDate'].value,
        endDate: bookingForm.elements['endDate'].value,
    };

    if (new Date(bookingData.endDate) <= new Date(bookingData.startDate)) {
        alert("End date must be after start date!");
        return;
    }

    const token = localStorage.getItem('jwtToken');

    try {
        const response = await fetch('http://localhost:8080/api/bookings', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(bookingData),
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || "Booking failed");
        }

        const booking = await response.json();
        resultDiv.textContent = `Booking created! ID: ${booking.id}`;
        resultDiv.style.color = "green";

        // Reload cars to remove the booked one from the dropdown
        loadCarOptions();
        bookingForm.reset();

    } catch (error) {
        console.error('Error creating booking:', error);
        resultDiv.textContent = error.message || "Failed to create booking.";
        resultDiv.style.color = "red";
    }
});

// Initialize
document.addEventListener('DOMContentLoaded', loadCarOptions);