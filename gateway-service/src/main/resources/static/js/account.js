async function loadAccountInfo() {
    const token = localStorage.getItem('jwtToken');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    try {
        const response = await fetch('http://localhost:8080/api/account/me', {
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
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const userInfo = await response.json();

        document.getElementById('username').textContent = userInfo.name;
        document.getElementById('email').textContent = userInfo.email;
        document.getElementById('phoneNumber').textContent = userInfo.phoneNumber;

    } catch (error) {
        console.error('Error loading user information:', error);
    }
}

function logout() {
    localStorage.removeItem('jwtToken');
    window.location.href = 'login.html';
}

document.addEventListener('DOMContentLoaded', loadAccountInfo);
