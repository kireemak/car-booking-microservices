document.getElementById("loginForm").addEventListener("submit", async function(event) {
    event.preventDefault();

    const name = document.getElementById("loginUsername").value;
    const password = document.getElementById("loginPassword").value;

    try {
        const response = await fetch("http://localhost:8080/api/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ name, password })
        });

        if (!response.ok) {
            const error = await response.json();
            alert("Login failed: " + error.message);
            return;
        }

        const data = await response.json();
        localStorage.setItem("jwtToken", data.token);
        alert("Login successful!");
        window.location.href = "index.html";

    } catch (error) {
        console.error("Error during login:", error);
        alert("An error occurred during login.");
    }
});
