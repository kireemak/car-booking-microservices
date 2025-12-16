async function loadUsers() {
    const token = localStorage.getItem('jwtToken');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    try {
        const response = await fetch('http://localhost:8080/api/users', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.status === 401) {
            localStorage.removeItem('jwtToken');
            window.location.href = 'login.html';
            return;
        }

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const users = await response.json();

        const tableBody = document.getElementById('usersTable').querySelector('tbody');
        if (!tableBody) return;

        tableBody.innerHTML = '';

        users.forEach(user => {
            const row = document.createElement('tr');
            const cells = [
                user.id,
                user.name || 'N/A',
                user.email || 'N/A',
                user.phoneNumber || 'N/A'
            ];

            cells.forEach(text => {
                const td = document.createElement('td');
                td.textContent = text;
                row.appendChild(td);
            });

            tableBody.appendChild(row);
        });

    } catch (error) {
        console.error('Error loading users:', error);
        const tableBody = document.getElementById('usersTable')?.querySelector('tbody');
        if (tableBody) {
            tableBody.innerHTML = '<tr><td colspan="4">Error loading users. Please try again later.</td></tr>';
        }
    }
}

document.addEventListener('DOMContentLoaded', loadUsers);