<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>ElectroMart India</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/assets/css/app.css" rel="stylesheet">
</head>
<body class="page-shell">
<%@ include file="fragments/header.jspf" %>
<main>
    <section class="hero-section">
        <div class="container py-5">
            <%@ include file="fragments/messages.jspf" %>
            <div class="row align-items-center g-4">
                <div class="col-lg-6">
                    <span class="eyebrow">India's Electronics Storefront</span>
                    <h1 class="display-4 fw-bold mb-3">Phones, laptops, and audio gear curated for modern India.</h1>
                    <p class="lead text-secondary mb-4">Shop premium tech, track orders, and pay in INR with an integrated Razorpay checkout flow.</p>
                    <div class="d-flex gap-3 flex-wrap">
                        <a class="btn btn-warning btn-lg px-4" href="#catalogue">Shop Now</a>
                        <a class="btn btn-outline-dark btn-lg px-4" href="/products?category=laptops">View Laptops</a>
                    </div>
                </div>
                <div class="col-lg-6">
                    <div class="hero-panel shadow-lg">
                        <div class="row g-3">
                            <c:forEach items="${featuredProducts}" var="item">
                                <div class="col-6">
                                    <div class="mini-card">
                                        <img src="${item.imageUrl}" alt="${item.name}">
                                        <div class="p-3">
                                            <div class="small text-muted">${item.brand}</div>
                                            <div class="fw-semibold">${item.name}</div>
                                            <div class="price-tag">&#8377;<fmt:formatNumber value="${item.price}" pattern="#,##0"/></div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <section class="container py-5" id="catalogue">
        <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3 mb-4">
            <div>
                <h2 class="fw-bold mb-1">Electronics Catalogue</h2>
                <p class="text-secondary mb-0">40 seeded products across phones, laptops, and earphones.</p>
            </div>
            <div class="d-flex gap-2 flex-wrap">
                <a class="btn btn-sm ${empty selectedCategory ? 'btn-dark' : 'btn-outline-dark'}" href="/products">All</a>
                <a class="btn btn-sm ${selectedCategory eq 'phones' ? 'btn-dark' : 'btn-outline-dark'}" href="/products?category=phones">Phones</a>
                <a class="btn btn-sm ${selectedCategory eq 'laptops' ? 'btn-dark' : 'btn-outline-dark'}" href="/products?category=laptops">Laptops</a>
                <a class="btn btn-sm ${selectedCategory eq 'earphones' ? 'btn-dark' : 'btn-outline-dark'}" href="/products?category=earphones">Earphones</a>
            </div>
        </div>
        <div class="row g-4">
            <c:forEach items="${products}" var="product">
                <div class="col-md-6 col-xl-3">
                    <div class="card product-card h-100 border-0 shadow-sm">
                        <img class="card-img-top product-image" src="${product.imageUrl}" alt="${product.name}">
                        <div class="card-body d-flex flex-column">
                            <div class="d-flex justify-content-between align-items-start mb-2">
                                <span class="badge text-bg-light border">${product.categoryName}</span>
                                <c:if test="${product.featured}">
                                    <span class="badge text-bg-warning">Featured</span>
                                </c:if>
                            </div>
                            <h5 class="card-title">${product.name}</h5>
                            <p class="small text-secondary mb-2">${product.brand}</p>
                            <p class="card-text text-secondary flex-grow-1">${product.description}</p>
                            <div class="d-flex justify-content-between align-items-center mt-3">
                                <div>
                                    <div class="price-tag">&#8377;<fmt:formatNumber value="${product.price}" pattern="#,##0"/></div>
                                    <div class="small text-secondary">Stock: ${product.stock}</div>
                                </div>
                                <c:choose>
                                    <c:when test="${isAuthenticated}">
                                        <form action="/cart/add/${product.id}" method="post">
                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                            <button class="btn btn-dark" type="submit">Add to Cart</button>
                                        </form>
                                    </c:when>
                                    <c:otherwise>
                                        <a class="btn btn-dark" href="/login">Login to Buy</a>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </section>
</main>
<%@ include file="fragments/footer.jspf" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
