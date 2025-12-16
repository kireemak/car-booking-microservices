async function loadBookings() {
    const tableBody = document.getElementById('bookingsTable')?.querySelector('tbody');
    if (!tableBody) {
        console.error('Table body not found');
        return;
    }

    const token = localStorage.getItem('jwtToken');
    if (!token) {
        showError('Please login to view cars');
        window.location.href = '/login.html';
        return;
    }

    try {
        const response = await fetch('http://localhost:8080/api/bookings', {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.status === 401) {
            localStorage.removeItem('jwtToken');
            window.location.href = '/login.html';
            return;
        }

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const bookings = await response.json();

        tableBody.innerHTML = '';

        bookings.forEach(booking => {
            const row = document.createElement('tr');

            const cells = [
                booking.id,
                (booking.user && booking.user.name) ? booking.user.name : 'Unknown User',
                (booking.car && booking.car.model) ? booking.car.model : 'Unknown Car',
                new Date(booking.startDate).toLocaleDateString(),
                new Date(booking.endDate).toLocaleDateString(),
                booking.status
            ];

            cells.forEach(text => {
                const td = document.createElement('td');
                td.textContent = text;
                row.appendChild(td);
            });

            tableBody.appendChild(row);
        });
    } catch (error) {
        console.error('Error loading bookings:', error);
        tableBody.innerHTML = '<tr><td colspan="6">Error loading bookings. Please try again.</td></tr>';
    }
}

document.addEventListener('DOMContentLoaded', loadBookings);
