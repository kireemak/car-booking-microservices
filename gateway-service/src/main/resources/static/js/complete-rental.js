const rentalForm = document.querySelector('[data-ui="complete-rental-form"]');
const resultDiv = document.querySelector('[data-target="rental-result"]');

// Fetch user's bookings to populate the dropdown
async function loadBookingOptions() {
    const token = localStorage.getItem('jwtToken');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    try {
        const response = await fetch('http://localhost:8080/api/bookings', {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!response.ok) throw new Error("Failed to fetch bookings");

        const bookings = await response.json();
        const bookingSelect = rentalForm.elements['bookingId'];

        // Filter for active bookings (assuming 'CONFIRMED' or similar status exists)
        // If your API returns all, you might want to filter out completed ones so users don't try to complete them twice.
        const activeBookings = bookings.filter(b => b.status !== 'Completed' && b.status !== 'Cancelled');

        if (activeBookings.length === 0) {
            bookingSelect.innerHTML = '<option value="" disabled selected>No active bookings found</option>';
            return;
        }

        bookingSelect.innerHTML = '<option value="" disabled selected>Select a booking to complete</option>';

        activeBookings.forEach(booking => {
            const option = document.createElement('option');
            option.value = booking.id;
            const carName = booking.car ? booking.car.model : 'Unknown Car';
            option.textContent = `Booking #${booking.id} - ${carName} (Starts: ${new Date(booking.startDate).toLocaleDateString()})`;
            bookingSelect.appendChild(option);
        });

    } catch (error) {
        console.error('Error loading booking options:', error);
        rentalForm.elements['bookingId'].innerHTML = '<option value="" disabled>Error loading bookings</option>';
    }
}

// Handle Form Submission
rentalForm?.addEventListener('submit', async (event) => {
    event.preventDefault();

    const token = localStorage.getItem('jwtToken');
    const bookingId = rentalForm.elements['bookingId'].value;

    try {
        const response = await fetch(`http://localhost:8080/api/bookings/${bookingId}/complete`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            if (response.status === 401) {
                localStorage.removeItem('jwtToken');
                window.location.href = 'login.html';
                return;
            }
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const updatedBooking = await response.json();

        resultDiv.textContent = `Rental completed successfully! Booking ID: ${updatedBooking.id}`;
        resultDiv.style.color = "green";

        // Reload dropdown to remove the completed booking
        loadBookingOptions();
        rentalForm.reset();

    } catch (error) {
        console.error('Error completing rental:', error);
        resultDiv.textContent = error.message || "Failed to complete rental.";
        resultDiv.style.color = "red";
    }
});

// Initialize
document.addEventListener('DOMContentLoaded', loadBookingOptions);