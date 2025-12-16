document.getElementById('bookingForm')?.addEventListener('submit', async (event) => {
    event.preventDefault();

    const bookingData = {
        carId: document.getElementById('carId').value,
        startDate: document.getElementById('startDate').value,
        endDate: document.getElementById('endDate').value,
    };

    if (new Date(bookingData.endDate) <= new Date(bookingData.startDate)) {
        alert("End date must be after start date!");
        return;
    }

    const token = localStorage.getItem('jwtToken');
    if (!token) {
        showError('Please login to view cars');
        window.location.href = '/login.html';
        return;
    }

    try {
        const response = await fetch('http://localhost:8080/api/bookings/create-with-check', {
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
        document.getElementById('bookingResult').textContent =
            `Booking created! ID: ${booking.id}`;
    } catch (error) {
        console.error('Error creating booking:', error);
        const result = document.getElementById('bookingResult');
        result.textContent = "Failed to create booking.";
        result.style.color = "red";
    }
});