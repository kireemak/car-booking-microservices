document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('jwtToken');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    // Initial data load
    loadUsers();
    loadCars();
    loadBookings();
});

function openTab(evt, tabName) {
    const tabContents = document.getElementsByClassName("tab-content");
    for (let i = 0; i < tabContents.length; i++) {
        tabContents[i].style.display = "none";
    }
    const tabLinks = document.getElementsByClassName("tab-link");
    for (let i = 0; i < tabLinks.length; i++) {
        tabLinks[i].className = tabLinks[i].className.replace(" active", "");
    }
    document.getElementById(tabName).style.display = "block";
    evt.currentTarget.className += " active";
}

const API_BASE_URL = 'http://localhost:8080/api';
const headers = {
    'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`,
    'Content-Type': 'application/json'
};

// --- User Management ---
async function loadUsers() {
    try {
        const response = await fetch(`${API_BASE_URL}/users`, { headers });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const users = await response.json();
        const tableBody = document.getElementById('usersTable').querySelector('tbody');
        tableBody.innerHTML = users.map(user => `
            <tr>
                <td>${user.id}</td>
                <td>${user.name}</td>
                <td>${user.email}</td>
                <td>${user.phoneNumber}</td>
                <td><button onclick="deleteUser(${user.id})">Delete</button></td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Error loading users:', error);
        alert('Failed to load users. You might not have admin rights.');
    }
}

async function deleteUser(id) {
    if (!confirm(`Are you sure you want to delete user with ID ${id}?`)) return;
    try {
        const response = await fetch(`${API_BASE_URL}/users/${id}`, { method: 'DELETE', headers });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        loadUsers(); // Refresh table
    } catch (error) {
        console.error('Error deleting user:', error);
        alert('Failed to delete user.');
    }
}

// --- Car Management ---
async function loadCars() {
    try {
        const response = await fetch(`${API_BASE_URL}/cars`, { headers });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const cars = await response.json();
        const tableBody = document.getElementById('carsTable').querySelector('tbody');
        tableBody.innerHTML = cars.map(car => `
            <tr>
                <td>${car.id}</td>
                <td>${car.brand}</td>
                <td>${car.model}</td>
                <td>${car.year}</td>
                <td>$${car.rentalPrice}</td>
                <td>${car.status}</td>
                <td>
                    <button onclick="openCarModal(${car.id})">Edit</button>
                    <button onclick="deleteCar(${car.id})">Delete</button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Error loading cars:', error);
    }
}

async function deleteCar(id) {
    if (!confirm(`Are you sure you want to delete car with ID ${id}?`)) return;
    try {
        const response = await fetch(`${API_BASE_URL}/cars/${id}`, { method: 'DELETE', headers });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        loadCars();
    } catch (error) {
        console.error('Error deleting car:', error);
        alert('Failed to delete car.');
    }
}

// --- Booking Management ---
async function loadBookings() {
    try {
        const response = await fetch(`${API_BASE_URL}/bookings/all`, { headers });
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const bookings = await response.json();
        const tableBody = document.getElementById('bookingsTable').querySelector('tbody');
        tableBody.innerHTML = bookings.map(b => `
            <tr>
                <td>${b.id}</td>
                <td>${b.user ? b.user.id : 'N/A'}</td>
                <td>${b.car ? b.car.id : 'N/A'}</td>
                <td>${new Date(b.startDate).toLocaleDateString()}</td>
                <td>${new Date(b.endDate).toLocaleDateString()}</td>
                <td>${b.status}</td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Error loading bookings:', error);
    }
}

// --- Car Modal Logic ---
const modal = document.getElementById('carModal');
const carForm = document.getElementById('carForm');

async function openCarModal(id) {
    carForm.reset();
    document.getElementById('carId').value = '';
    if (id) {
        // Edit mode
        document.getElementById('modalTitle').textContent = 'Edit Car';
        try {
            const response = await fetch(`${API_BASE_URL}/cars/${id}`, { headers });
            if (!response.ok) throw new Error('Car not found');
            const car = await response.json();
            document.getElementById('carId').value = car.id;
            document.getElementById('carBrand').value = car.brand;
            document.getElementById('carModel').value = car.model;
            document.getElementById('carYear').value = car.year;
            document.getElementById('carPrice').value = car.rentalPrice;
            document.getElementById('carStatus').value = car.status;
        } catch (error) {
            alert('Could not fetch car details.');
            return;
        }
    } else {
        // Add mode
        document.getElementById('modalTitle').textContent = 'Add New Car';
    }
    modal.style.display = 'block';
}

function closeCarModal() {
    modal.style.display = 'none';
}

carForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const carId = document.getElementById('carId').value;
    const carData = {
        brand: document.getElementById('carBrand').value,
        model: document.getElementById('carModel').value,
        year: parseInt(document.getElementById('carYear').value, 10),
        rentalPrice: parseFloat(document.getElementById('carPrice').value),
        status: document.getElementById('carStatus').value
    };

    const isNew = !carId;
    const url = isNew ? `${API_BASE_URL}/cars` : `${API_BASE_URL}/cars/${carId}`;
    const method = isNew ? 'POST' : 'PUT';

    try {
        const response = await fetch(url, { method, headers, body: JSON.stringify(carData) });
        if (!response.ok) throw new Error('Failed to save car');
        closeCarModal();
        loadCars();
    } catch (error) {
        console.error('Error saving car:', error);
        alert('Error saving car.');
    }
});

window.onclick = function(event) {
    if (event.target == modal) {
        closeCarModal();
    }
}