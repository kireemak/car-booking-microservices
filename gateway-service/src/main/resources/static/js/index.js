async function loadAvailableCars() {
    const token = localStorage.getItem('jwtToken');

    if (!token) {
        console.error("No token found. Redirecting to login.");
        window.location.href = 'login.html';
        return;
    }

    try {
        const response = await fetch('http://localhost:8080/api/cars/available', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token
            }
        });

        if (response.status === 401) {
            console.warn('Unauthorized access. Redirecting to login.');
            window.location.href = 'login.html';
            return;
        }

        const cars = await response.json();
        const tableBody = document.getElementById('carsTable').querySelector('tbody');
        tableBody.innerHTML = '';

        cars.forEach(car => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${car.id}</td>
                <td>${car.brand}</td>
                <td>${car.model}</td>
                <td>${car.year}</td>
                <td>$${car.rentalPrice}</td>
            `;
            tableBody.appendChild(row);
        });
    } catch (error) {
        console.error('Error loading available cars:', error);
        alert('Failed to load cars. Please try again later.');
    }
}

document.addEventListener('DOMContentLoaded', loadAvailableCars);
