<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Error | ElectroMart India</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/assets/css/app.css" rel="stylesheet">
</head>
<body class="auth-shell">
<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-lg-6">
            <div class="auth-card shadow-lg">
                <h2 class="fw-bold mb-3">We hit a temporary problem</h2>
                <p class="text-secondary mb-4">${errorMessage}</p>
                <a href="/" class="btn btn-dark">Back to Home</a>
            </div>
        </div>
    </div>
</div>
</body>
</html>
