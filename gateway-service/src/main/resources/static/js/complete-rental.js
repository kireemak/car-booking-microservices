document.getElementById('completeRentalForm')?.addEventListener('submit', async (event) => {
    event.preventDefault();

    const token = localStorage.getItem('jwtToken');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    const bookingId = document.getElementById('bookingId').value;
    const result = document.getElementById('rentalResult');

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

        result.textContent = `Rental completed successfully! Booking ID: ${updatedBooking.id}`;
        result.style.color = "green";

    } catch (error) {
        console.error('Error completing rental:', error);
        result.textContent = error.message || "Failed to complete rental.";
        result.style.color = "red";
    }
});