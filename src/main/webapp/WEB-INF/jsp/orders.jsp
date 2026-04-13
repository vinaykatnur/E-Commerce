<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Your Orders | ElectroMart India</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/assets/css/app.css" rel="stylesheet">
</head>
<body class="page-shell">
<%@ include file="fragments/header.jspf" %>
<main class="container py-5">
    <%@ include file="fragments/messages.jspf" %>
    <div id="ordersFeedback" class="mb-3"></div>
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="fw-bold mb-1">Your Orders</h2>
            <p class="text-secondary mb-0">Track every purchase made through ElectroMart India.</p>
        </div>
        <a href="/products" class="btn btn-outline-dark">Shop More</a>
    </div>
    <c:choose>
        <c:when test="${empty orders}">
            <div class="empty-state shadow-sm">
                <h4 class="fw-semibold">No orders yet</h4>
                <p class="text-secondary mb-3">Your upcoming purchases will appear here with payment and delivery updates.</p>
                <a href="/products" class="btn btn-dark">Start Shopping</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="vstack gap-4">
                <c:forEach items="${orders}" var="order">
                    <div class="order-card shadow-sm" data-order-card="${order.orderNumber}">
                        <div class="d-flex flex-column flex-md-row justify-content-between gap-3">
                            <div>
                                <div class="small text-secondary">Order Number</div>
                                <h5 class="mb-1">${order.orderNumber}</h5>
                                <div class="small text-secondary">Status: <span data-order-status="${order.orderNumber}">${order.status}</span> | Payment: <span data-payment-status="${order.orderNumber}">${order.paymentStatus}</span></div>
                            </div>
                            <div class="text-md-end">
                                <div class="price-tag">&#8377;<fmt:formatNumber value="${order.totalAmount}" pattern="#,##0"/></div>
                                <div class="small text-secondary">${order.createdAt}</div>
                                <c:if test="${order.status ne 'CANCELLED'}">
                                    <button class="btn btn-sm btn-outline-danger mt-2 cancel-order-btn" data-order-number="${order.orderNumber}" type="button">
                                        Cancel Order
                                    </button>
                                </c:if>
                            </div>
                        </div>
                        <c:choose>
                            <c:when test="${not empty order.address}">
                                <div class="small text-secondary mt-3">
                                    Deliver to: ${order.address.fullName}, ${order.address.city}, ${order.address.state} - ${order.address.pincode}
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="small text-secondary mt-3">
                                    Delivery address not available for this older order.
                                </div>
                            </c:otherwise>
                        </c:choose>
                        <div class="row g-3 mt-2">
                            <c:forEach items="${order.items}" var="item">
                                <div class="col-md-6">
                                    <div class="mini-order-item">
                                        <img src="${item.productImageUrl}" alt="${item.productName}">
                                        <div>
                                            <div class="fw-semibold">${item.productName}</div>
                                            <div class="small text-secondary">Qty ${item.quantity} | &#8377;<fmt:formatNumber value="${item.totalPrice}" pattern="#,##0"/></div>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</main>
<%@ include file="fragments/footer.jspf" %>
<script>
    function showOrdersFeedback(type, message) {
        const feedback = document.getElementById('ordersFeedback');
        feedback.innerHTML = '<div class="alert alert-' + type + ' shadow-sm mb-0">' + message + '</div>';
    }

    document.querySelectorAll('.cancel-order-btn').forEach(function (button) {
        button.addEventListener('click', async function () {
            const orderNumber = button.dataset.orderNumber;
            button.disabled = true;
            button.textContent = 'Cancelling...';
            try {
                const response = await fetch('/order/cancel/' + orderNumber, {
                    method: 'PUT',
                    credentials: 'same-origin',
                    headers: {
                        'Accept': 'application/json',
                        'X-Requested-With': 'XMLHttpRequest'
                    }
                });
                const contentType = response.headers.get('content-type') || '';
                const payload = contentType.includes('application/json')
                    ? await response.json().catch(function () { return null; })
                    : await response.text().catch(function () { return ''; });
                console.log('Cancel order response', { status: response.status, payload: payload });

                if (response.ok) {
                    const statusNode = document.querySelector('[data-order-status="' + orderNumber + '"]');
                    if (statusNode && payload && payload.status) {
                        statusNode.textContent = payload.status;
                    }
                    button.remove();
                    showOrdersFeedback('success', 'Order cancelled successfully.');
                    return;
                }

                if (response.status === 401) {
                    showOrdersFeedback('danger', 'Please log in again to manage your orders.');
                    setTimeout(function () { window.location.href = '/login'; }, 1200);
                    return;
                }

                if (response.status >= 500) {
                    showOrdersFeedback('danger', 'We could not update your order right now. Please try again in a moment.');
                    return;
                }

                const message = payload && typeof payload === 'object' && payload.message
                    ? payload.message
                    : 'Unable to cancel the order.';
                showOrdersFeedback('danger', message);
            } catch (error) {
                console.error('Cancel order failed', error);
                showOrdersFeedback('danger', 'We could not reach the server. Please try again.');
            } finally {
                if (document.body.contains(button)) {
                    button.disabled = false;
                    button.textContent = 'Cancel Order';
                }
            }
        });
    });
</script>
</body>
</html>
