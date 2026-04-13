<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Create Account | ElectroMart India</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/assets/css/app.css" rel="stylesheet">
</head>
<body class="auth-shell">
<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-lg-5">
            <div class="auth-card shadow-lg">
                <h2 class="fw-bold mb-2">Create your ElectroMart account</h2>
                <p class="text-secondary mb-4">Start shopping premium electronics in INR.</p>
                <%@ include file="fragments/messages.jspf" %>
                <form:form action="/register" method="post" modelAttribute="registerRequest" class="vstack gap-3" acceptCharset="UTF-8">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                    <div>
                        <label class="form-label">Full Name</label>
                        <form:input path="fullName" cssClass="form-control form-control-lg"/>
                        <form:errors path="fullName" cssClass="text-danger small mt-1 d-block"/>
                    </div>
                    <div>
                        <label class="form-label">Email</label>
                        <form:input path="email" type="email" cssClass="form-control form-control-lg"/>
                        <form:errors path="email" cssClass="text-danger small mt-1 d-block"/>
                    </div>
                    <div>
                        <label class="form-label">Password</label>
                        <form:password path="password" cssClass="form-control form-control-lg"/>
                        <form:errors path="password" cssClass="text-danger small mt-1 d-block"/>
                    </div>
                    <button class="btn btn-warning btn-lg w-100" type="submit">Create Account</button>
                </form:form>
                <p class="mb-0 mt-4 text-secondary">Already registered? <a href="/login">Sign in</a></p>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
