<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Checkout | ElectroMart India</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/assets/css/app.css" rel="stylesheet">
</head>
<body class="page-shell">
<%@ include file="fragments/header.jspf" %>
<main class="container py-5">
    <%@ include file="fragments/messages.jspf" %>
    <div class="row g-4">
        <div class="col-lg-7">
            <div class="summary-card shadow-sm">
                <div class="small text-secondary">Order Number</div>
                <h2 class="fw-bold">${order.orderNumber}</h2>
                <p class="text-secondary">Complete payment securely in INR using Razorpay.</p>
                <c:if test="${not empty order.address}">
                    <div class="border rounded-4 p-3 bg-light mt-3">
                        <div class="small text-secondary">Delivery Address</div>
                        <div class="fw-semibold">${order.address.fullName}</div>
                        <div>${order.address.addressLine1}</div>
                        <c:if test="${not empty order.address.addressLine2}">
                            <div>${order.address.addressLine2}</div>
                        </c:if>
                        <div>${order.address.city}, ${order.address.state} - ${order.address.pincode}</div>
                        <div>Phone: ${order.address.phoneNumber}</div>
                    </div>
                </c:if>
                <div class="vstack gap-3 mt-4">
                    <c:forEach items="${order.items}" var="item">
                        <div class="mini-order-item border rounded-4 p-2">
                            <img src="${item.productImageUrl}" alt="${item.productName}">
                            <div class="flex-grow-1">
                                <div class="fw-semibold">${item.productName}</div>
                                <div class="small text-secondary">Qty ${item.quantity}</div>
                            </div>
                            <div class="fw-semibold">&#8377;<fmt:formatNumber value="${item.totalPrice}" pattern="#,##0"/></div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
        <div class="col-lg-5">
            <div class="summary-card shadow-sm">
                <h4 class="fw-bold">Payment</h4>
                <div class="d-flex justify-content-between mt-3">
                    <span>Total Payable</span>
                    <span class="price-tag">&#8377;<fmt:formatNumber value="${order.totalAmount}" pattern="#,##0"/></span>
                </div>
                <c:choose>
                    <c:when test="${payment.enabled}">
                        <p class="small text-secondary mt-3 mb-3">Live Razorpay mode is enabled. Use your Razorpay test or live credentials via environment variables.</p>
                        <button class="btn btn-warning btn-lg w-100" id="rzp-button">Pay with Razorpay</button>
                        <form action="/payments/verify" method="post" id="paymentForm" class="d-none">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                            <input type="hidden" name="appOrderNumber" value="${payment.orderNumber}">
                            <input type="hidden" name="razorpay_payment_id" id="razorpay_payment_id">
                            <input type="hidden" name="razorpay_order_id" id="razorpay_order_id">
                            <input type="hidden" name="razorpay_signature" id="razorpay_signature">
                        </form>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-info mt-3">Razorpay keys are not configured, so the app is running in demo payment mode for local testing.</div>
                        <form action="/payments/demo-confirm/${order.orderNumber}" method="post" class="mt-3">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                            <button class="btn btn-dark btn-lg w-100" type="submit">Complete Demo Payment</button>
                        </form>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</main>
<%@ include file="fragments/footer.jspf" %>
<script src="https://checkout.razorpay.com/v1/checkout.js"></script>
<script>
    const payButton = document.getElementById('rzp-button');
    if (payButton) {
        payButton.addEventListener('click', function () {
            const options = {
                key: '${payment.keyId}',
                amount: '${payment.amountInPaise}',
                currency: '${payment.currency}',
                name: 'ElectroMart India',
                description: 'Order ${payment.orderNumber}',
                order_id: '${payment.razorpayOrderId}',
                handler: function (response) {
                    document.getElementById('razorpay_payment_id').value = response.razorpay_payment_id;
                    document.getElementById('razorpay_order_id').value = response.razorpay_order_id;
                    document.getElementById('razorpay_signature').value = response.razorpay_signature;
                    document.getElementById('paymentForm').submit();
                },
                theme: { color: '#f59e0b' }
            };
            new Razorpay(options).open();
        });
    }
</script>
</body>
</html>
