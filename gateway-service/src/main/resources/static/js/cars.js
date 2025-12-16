const modal = document.getElementById('detailsModal');
const closeModalButton = document.getElementById('closeModalButton');
const reviewForm = document.getElementById('reviewForm');

async function loadCars() {
    const token = localStorage.getItem('jwtToken');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    try {
        const response = await fetch('http://localhost:8080/api/cars', {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

        const cars = await response.json();
        const tableBody = document.getElementById('carsTableBody');
        tableBody.innerHTML = '';

        cars.forEach(car => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${car.id}</td>
                <td>${car.brand}</td>
                <td>${car.model}</td>
                <td>${car.year}</td>
                <td>$${car.rentalPrice}</td>
                <td>${car.status}</td>
            `;
            row.style.cursor = 'pointer';
            row.addEventListener('click', () => openDetailsModal(car.id));
            tableBody.appendChild(row);
        });
    } catch (error) {
        console.error('Error loading cars:', error);
    }
}

async function openDetailsModal(carId) {
    const token = localStorage.getItem('jwtToken');
    try {
        const response = await fetch(`http://localhost:8080/api/cars/${carId}/details`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!response.ok) throw new Error('Could not fetch details');

        const details = await response.json();

        document.getElementById('modalCarTitle').textContent = `Details for Car #${carId}`;

        // Отображаем основную информацию
        const detailsContainer = document.getElementById('modalCarDetails');
        detailsContainer.innerHTML = `<p><b>Description:</b> ${details.description || 'No description available'}</p>`;
        if (details.features) {
            detailsContainer.innerHTML += '<b>Features:</b><ul>' +
                Object.entries(details.features).map(([key, value]) => `<li>${key}: ${value}</li>`).join('') +
                '</ul>';
        }

        // Отображаем отзывы
        const reviewsContainer = document.getElementById('modalCarReviews');
        if (details.reviews && details.reviews.length > 0) {
            reviewsContainer.innerHTML = '<ul>' +
                details.reviews.map(r => `<li><b>${r.username}</b> (Rating: ${r.rating}/5): ${r.comment}</li>`).join('') +
                '</ul>';
        } else {
            reviewsContainer.innerHTML = '<p>No reviews yet.</p>';
        }

        document.getElementById('reviewCarId').value = carId;
        modal.style.display = 'block';
    } catch (error) {
        console.error('Error loading car details:', error);
        alert('Failed to load car details.');
    }
}

closeModalButton.onclick = () => {
    modal.style.display = 'none';
};

window.onclick = (event) => {
    if (event.target == modal) {
        modal.style.display = 'none';
    }
};

reviewForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const carId = document.getElementById('reviewCarId').value;
    const token = localStorage.getItem('jwtToken');

    const reviewData = {
        rating: parseInt(document.getElementById('rating').value, 10),
        comment: document.getElementById('comment').value
    };

    try {
        const response = await fetch(`http://localhost:8080/api/cars/${carId}/details`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(reviewData)
        });
        if (!response.ok) throw new Error('Failed to submit review');

        alert('Thank you for your review!');
        reviewForm.reset();
        openDetailsModal(carId);

    } catch (error) {
        console.error('Error submitting review:', error);
        alert('Failed to submit review.');
    }
});

document.addEventListener('DOMContentLoaded', loadCars);