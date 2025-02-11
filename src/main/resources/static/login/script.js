document.getElementById("loginForm").addEventListener("submit", async function (event) {
    event.preventDefault(); // Prevent page reload

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const errorMessage = document.getElementById("errorMessage");

    try {
        const response = await fetch("http://localhost:8081/api/v1/auth/user/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": "Bearer " + localStorage.getItem("adminToken") // If needed
            },
            body: JSON.stringify({ uucms_id: email, password: password })
        });

        if (response.status === 202) {
            const data = await response.json();
            localStorage.setItem("adminToken", data.token); // Store token
            alert("Login Successful! Redirecting...");
            window.location.href = "dashboard.html"; // Redirect after login
        } else {
            errorMessage.textContent = "Invalid Credentials!";
        }
    } catch (error) {
        errorMessage.textContent = "Login Failed. Try Again!";
    }
});


