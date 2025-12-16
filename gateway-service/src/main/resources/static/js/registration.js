document.getElementById("registrationForm").addEventListener("submit", async function(event) {
    event.preventDefault();

    const name = document.getElementById("regName").value;
    const password = document.getElementById("regPassword").value;
    const confirmPassword = document.getElementById("regConfirmPassword").value;
    const email = document.getElementById("regEmail").value;
    const phone = document.getElementById("regPhone").value;

    const requestBody = {
        name,
        password,
        confirmPassword,
        email,
        phone
    };

    try {
        const response = await fetch("http://localhost:8080/api/auth/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestBody)
        });

        if (response.status === 400) {
            const error = await response.json();
            alert("Registration failed: " + error.message);
            return;
        }

        if (response.status === 409) {
            alert("User with this username already exists.");
            return;
        }

        if (response.ok) {
            alert("Registration successful! You can now log in.");
            window.location.href = "login.html";
        }

    } catch (error) {
        console.error("Error during registration:", error);
        alert("Unable to register. Please try again later.");
    }
});
