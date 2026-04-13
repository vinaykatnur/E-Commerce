<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Your Cart | ElectroMart India</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/assets/css/app.css" rel="stylesheet">
</head>
<body class="page-shell">
<%@ include file="fragments/header.jspf" %>
<main class="container py-5">
    <%@ include file="fragments/messages.jspf" %>
    <div id="addressFeedback" class="mb-3"></div>
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="fw-bold mb-1">Shopping Cart</h2>
            <p class="text-secondary mb-0">One row per product, with quantity controls.</p>
        </div>
        <a href="/products" class="btn btn-outline-dark">Continue Shopping</a>
    </div>

    <c:choose>
        <c:when test="${empty cart.items}">
            <div class="empty-state shadow-sm">
                <h4 class="fw-semibold">Your cart is empty</h4>
                <p class="text-secondary">Browse phones, laptops, and earphones to get started.</p>
                <a href="/products" class="btn btn-dark">Explore Products</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="row g-4">
                <div class="col-lg-8">
                    <div class="vstack gap-3">
                        <c:forEach items="${cart.items}" var="item">
                            <div class="cart-card shadow-sm">
                                <img src="${item.imageUrl}" alt="${item.productName}">
                                <div class="flex-grow-1">
                                    <div class="d-flex justify-content-between gap-3">
                                        <div>
                                            <h5 class="mb-1">${item.productName}</h5>
                                            <p class="small text-secondary mb-2">${item.brand}</p>
                                            <div class="price-tag">&#8377;<fmt:formatNumber value="${item.unitPrice}" pattern="#,##0"/></div>
                                        </div>
                                        <div class="text-end">
                                            <div class="fw-semibold mb-2">&#8377;<fmt:formatNumber value="${item.totalPrice}" pattern="#,##0"/></div>
                                            <form action="/cart/remove/${item.cartItemId}" method="post">
                                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                                <button class="btn btn-sm btn-outline-danger" type="submit">Remove</button>
                                            </form>
                                        </div>
                                    </div>
                                    <div class="d-flex align-items-center gap-2 mt-3">
                                        <form action="/cart/decrease/${item.cartItemId}" method="post">
                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                            <button class="btn btn-outline-dark btn-sm" type="submit">-</button>
                                        </form>
                                        <span class="qty-pill">${item.quantity}</span>
                                        <form action="/cart/increase/${item.cartItemId}" method="post">
                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                            <button class="btn btn-outline-dark btn-sm" type="submit">+</button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
                <div class="col-lg-4">
                    <div class="summary-card shadow-sm">
                        <h4 class="fw-bold">Order Summary</h4>
                        <div class="d-flex justify-content-between mt-3">
                            <span>Total Items</span>
                            <span>${cart.totalItems}</span>
                        </div>
                        <div class="d-flex justify-content-between mt-2">
                            <span>Subtotal</span>
                            <span>&#8377;<fmt:formatNumber value="${cart.subtotal}" pattern="#,##0"/></span>
                        </div>
                        <hr>
                        <div class="d-flex justify-content-between fw-bold fs-5">
                            <span>Total</span>
                            <span>&#8377;<fmt:formatNumber value="${cart.subtotal}" pattern="#,##0"/></span>
                        </div>
                        <div class="d-flex justify-content-between align-items-center mt-4">
                            <label class="form-label fw-semibold mb-0">Delivery Address</label>
                            <button class="btn btn-sm btn-outline-dark" data-bs-toggle="modal" data-bs-target="#addressModal" id="addAddressButton" type="button">Add Address</button>
                        </div>
                        <c:if test="${empty googleMapsApiKey}">
                            <div class="alert alert-warning mt-3 mb-0 small">
                                Google Maps autocomplete is ready, but the API key is not configured yet. Add `GOOGLE_MAPS_API_KEY` to enable place search.
                            </div>
                        </c:if>
                        <div class="address-list mt-3" id="addressList">
                            <c:choose>
                                <c:when test="${empty addresses}">
                                    <div class="alert alert-info mb-0" id="emptyAddressState">
                                        Add your first delivery address to continue to checkout.
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach items="${addresses}" var="address">
                                        <label class="address-option ${address.isDefault ? 'selected' : ''}" data-address-card="${address.id}">
                                            <input class="form-check-input" type="radio" name="selectedAddress" value="${address.id}" <c:if test="${address.isDefault}">checked</c:if>>
                                            <div class="d-flex justify-content-between align-items-start gap-3">
                                                <div>
                                                    <div class="fw-semibold">
                                                        ${address.fullName}
                                                        <c:if test="${address.isDefault}">
                                                            <span class="badge text-bg-warning ms-2">Default</span>
                                                        </c:if>
                                                    </div>
                                                    <div class="small text-secondary">${address.phoneNumber}</div>
                                                    <div class="small text-secondary">${address.addressLine1}</div>
                                                    <c:if test="${not empty address.addressLine2}">
                                                        <div class="small text-secondary">${address.addressLine2}</div>
                                                    </c:if>
                                                    <div class="small text-secondary">${address.city}, ${address.state} - ${address.pincode}</div>
                                                </div>
                                                <div class="d-flex gap-2">
                                                    <button class="btn btn-sm btn-outline-dark edit-address-btn" type="button"
                                                            data-address-id="${address.id}"
                                                            data-full-name="${address.fullName}"
                                                            data-phone-number="${address.phoneNumber}"
                                                            data-address-line1="${address.addressLine1}"
                                                            data-address-line2="${address.addressLine2}"
                                                            data-city="${address.city}"
                                                            data-state="${address.state}"
                                                            data-pincode="${address.pincode}"
                                                            data-latitude="${address.latitude}"
                                                            data-longitude="${address.longitude}"
                                                            data-is-default="${address.isDefault}">
                                                        Edit
                                                    </button>
                                                    <button class="btn btn-sm btn-outline-danger delete-address-btn" type="button" data-address-id="${address.id}">
                                                        Delete
                                                    </button>
                                                </div>
                                            </div>
                                        </label>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <form action="/orders/checkout" method="post" class="mt-4" id="checkoutForm">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                            <input type="hidden" id="addressId" name="addressId" value="">
                            <button class="btn btn-warning btn-lg w-100" id="checkoutButton" type="submit">Proceed to Checkout</button>
                        </form>
                    </div>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</main>

<div class="modal fade" id="addressModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-centered">
        <div class="modal-content border-0 rounded-4 shadow-lg">
            <div class="modal-header border-0 pb-0">
                <div>
                    <h5 class="modal-title fw-bold" id="addressModalTitle">Add Delivery Address</h5>
                    <p class="text-secondary mb-0 small">Save a complete address before placing your order.</p>
                </div>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body pt-3">
                <form id="addressForm" class="row g-3">
                    <input type="hidden" id="addressFormId">
                    <input type="hidden" id="latitude" name="latitude">
                    <input type="hidden" id="longitude" name="longitude">
                    <div class="col-12">
                        <label class="form-label" for="placeSearch">Search with Google Maps</label>
                        <input class="form-control" id="placeSearch" type="text" placeholder="Search your area, apartment, or landmark">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label" for="fullName">Full Name</label>
                        <input class="form-control" id="fullName" name="fullName" type="text" required>
                        <div class="invalid-feedback"></div>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label" for="phoneNumber">Phone Number</label>
                        <input class="form-control" id="phoneNumber" name="phoneNumber" type="text" required>
                        <div class="invalid-feedback"></div>
                    </div>
                    <div class="col-12">
                        <label class="form-label" for="addressLine1">Address Line 1</label>
                        <input class="form-control" id="addressLine1" name="addressLine1" type="text" required>
                        <div class="invalid-feedback"></div>
                    </div>
                    <div class="col-12">
                        <label class="form-label" for="addressLine2">Address Line 2</label>
                        <input class="form-control" id="addressLine2" name="addressLine2" type="text">
                        <div class="invalid-feedback"></div>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label" for="city">City</label>
                        <input class="form-control" id="city" name="city" type="text" required>
                        <div class="invalid-feedback"></div>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label" for="state">State</label>
                        <input class="form-control" id="state" name="state" type="text" required>
                        <div class="invalid-feedback"></div>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label" for="pincode">Pincode</label>
                        <input class="form-control" id="pincode" name="pincode" type="text" required>
                        <div class="invalid-feedback"></div>
                    </div>
                    <div class="col-12">
                        <div class="form-check">
                            <input class="form-check-input" id="isDefault" name="isDefault" type="checkbox">
                            <label class="form-check-label" for="isDefault">Make this my default delivery address</label>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer border-0 pt-0">
                <button type="button" class="btn btn-outline-dark" data-bs-dismiss="modal">Close</button>
                <button type="button" class="btn btn-dark" id="saveAddressButton">Save Address</button>
            </div>
        </div>
    </div>
</div>
<%@ include file="fragments/footer.jspf" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<c:if test="${not empty googleMapsApiKey}">
    <script async defer src="https://maps.googleapis.com/maps/api/js?key=${googleMapsApiKey}&libraries=places"></script>
</c:if>
<script>
    const addressModalElement = document.getElementById('addressModal');
    const addressModal = new bootstrap.Modal(addressModalElement);
    const addressForm = document.getElementById('addressForm');
    const addressList = document.getElementById('addressList');
    const addressFeedback = document.getElementById('addressFeedback');
    const checkoutForm = document.getElementById('checkoutForm');
    const addressIdInput = document.getElementById('addressId');
    const saveAddressButton = document.getElementById('saveAddressButton');
    const addressModalTitle = document.getElementById('addressModalTitle');
    const placeSearchInput = document.getElementById('placeSearch');

    function showAddressFeedback(type, message) {
        addressFeedback.innerHTML = '<div class="alert alert-' + type + ' shadow-sm mb-0">' + message + '</div>';
    }

    function clearFieldErrors() {
        addressForm.querySelectorAll('.form-control').forEach(function (input) {
            input.classList.remove('is-invalid');
        });
        addressForm.querySelectorAll('.invalid-feedback').forEach(function (node) {
            node.textContent = '';
        });
    }

    function escapeHtml(value) {
        return String(value || '')
            .replaceAll('&', '&amp;')
            .replaceAll('<', '&lt;')
            .replaceAll('>', '&gt;')
            .replaceAll('"', '&quot;')
            .replaceAll("'", '&#39;');
    }

    function setSelectedAddress(addressId) {
        addressIdInput.value = addressId || '';
        document.querySelectorAll('[data-address-card]').forEach(function (card) {
            const selected = String(card.dataset.addressCard) === String(addressId);
            card.classList.toggle('selected', selected);
            const radio = card.querySelector('input[type="radio"]');
            if (radio) {
                radio.checked = selected;
            }
        });
    }

    function resetAddressForm() {
        addressForm.reset();
        clearFieldErrors();
        document.getElementById('addressFormId').value = '';
        document.getElementById('latitude').value = '';
        document.getElementById('longitude').value = '';
        addressModalTitle.textContent = 'Add Delivery Address';
    }

    function fillAddressForm(button) {
        document.getElementById('addressFormId').value = button.dataset.addressId || '';
        document.getElementById('fullName').value = button.dataset.fullName || '';
        document.getElementById('phoneNumber').value = button.dataset.phoneNumber || '';
        document.getElementById('addressLine1').value = button.dataset.addressLine1 || '';
        document.getElementById('addressLine2').value = button.dataset.addressLine2 || '';
        document.getElementById('city').value = button.dataset.city || '';
        document.getElementById('state').value = button.dataset.state || '';
        document.getElementById('pincode').value = button.dataset.pincode || '';
        document.getElementById('latitude').value = button.dataset.latitude || '';
        document.getElementById('longitude').value = button.dataset.longitude || '';
        document.getElementById('isDefault').checked = button.dataset.isDefault === 'true';
        addressModalTitle.textContent = 'Edit Delivery Address';
        clearFieldErrors();
    }

    function collectAddressPayload() {
        return {
            fullName: document.getElementById('fullName').value.trim(),
            phoneNumber: document.getElementById('phoneNumber').value.trim(),
            addressLine1: document.getElementById('addressLine1').value.trim(),
            addressLine2: document.getElementById('addressLine2').value.trim(),
            city: document.getElementById('city').value.trim(),
            state: document.getElementById('state').value.trim(),
            pincode: document.getElementById('pincode').value.trim(),
            latitude: document.getElementById('latitude').value || null,
            longitude: document.getElementById('longitude').value || null,
            isDefault: document.getElementById('isDefault').checked
        };
    }

    function renderAddresses(addresses) {
        if (!addresses.length) {
            addressList.innerHTML = '<div class="alert alert-info mb-0" id="emptyAddressState">Add your first delivery address to continue to checkout.</div>';
            setSelectedAddress('');
            return;
        }

        addressList.innerHTML = addresses.map(function (address) {
            return '<label class="address-option ' + (address.isDefault ? 'selected' : '') + '" data-address-card="' + address.id + '">'
                + '<input class="form-check-input" type="radio" name="selectedAddress" value="' + address.id + '" ' + (address.isDefault ? 'checked' : '') + '>'
                + '<div class="d-flex justify-content-between align-items-start gap-3">'
                + '<div>'
                + '<div class="fw-semibold">' + escapeHtml(address.fullName) + (address.isDefault ? '<span class="badge text-bg-warning ms-2">Default</span>' : '') + '</div>'
                + '<div class="small text-secondary">' + escapeHtml(address.phoneNumber) + '</div>'
                + '<div class="small text-secondary">' + escapeHtml(address.addressLine1) + '</div>'
                + (address.addressLine2 ? '<div class="small text-secondary">' + escapeHtml(address.addressLine2) + '</div>' : '')
                + '<div class="small text-secondary">' + escapeHtml(address.city) + ', ' + escapeHtml(address.state) + ' - ' + escapeHtml(address.pincode) + '</div>'
                + '</div>'
                + '<div class="d-flex gap-2">'
                + '<button class="btn btn-sm btn-outline-dark edit-address-btn" type="button"'
                + ' data-address-id="' + address.id + '"'
                + ' data-full-name="' + escapeHtml(address.fullName) + '"'
                + ' data-phone-number="' + escapeHtml(address.phoneNumber) + '"'
                + ' data-address-line1="' + escapeHtml(address.addressLine1) + '"'
                + ' data-address-line2="' + escapeHtml(address.addressLine2 || '') + '"'
                + ' data-city="' + escapeHtml(address.city) + '"'
                + ' data-state="' + escapeHtml(address.state) + '"'
                + ' data-pincode="' + escapeHtml(address.pincode) + '"'
                + ' data-latitude="' + escapeHtml(address.latitude || '') + '"'
                + ' data-longitude="' + escapeHtml(address.longitude || '') + '"'
                + ' data-is-default="' + address.isDefault + '">Edit</button>'
                + '<button class="btn btn-sm btn-outline-danger delete-address-btn" type="button" data-address-id="' + address.id + '">Delete</button>'
                + '</div>'
                + '</div>'
                + '</label>';
        }).join('');
        const selected = addresses.find(function (address) { return address.isDefault; }) || addresses[0];
        setSelectedAddress(selected.id);
    }

    async function loadAddresses(message) {
        const response = await fetch('/address', {
            credentials: 'same-origin',
            headers: {
                'Accept': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            }
        });
        const payload = await response.json().catch(function () { return []; });
        console.log('Address list response', { status: response.status, payload: payload });
        if (!response.ok) {
            throw payload;
        }
        renderAddresses(Array.isArray(payload) ? payload : []);
        if (message) {
            showAddressFeedback('success', message);
        }
    }

    async function saveAddress() {
        clearFieldErrors();
        const id = document.getElementById('addressFormId').value;
        const method = id ? 'PUT' : 'POST';
        const url = id ? '/address/' + id : '/address';
        saveAddressButton.disabled = true;
        saveAddressButton.textContent = 'Saving...';

        try {
            const response = await fetch(url, {
                method: method,
                credentials: 'same-origin',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: JSON.stringify(collectAddressPayload())
            });
            const payload = await response.json().catch(function () { return null; });
            console.log('Save address response', { status: response.status, payload: payload });

            if (!response.ok) {
                if (response.status === 401) {
                    showAddressFeedback('danger', 'Please log in again to manage addresses.');
                    setTimeout(function () { window.location.href = '/login'; }, 1200);
                    return;
                }
                const message = payload && payload.message ? payload.message : 'We could not save this address.';
                showAddressFeedback('danger', message);
                const pincodeField = document.getElementById('pincode');
                pincodeField.classList.add('is-invalid');
                pincodeField.parentElement.querySelector('.invalid-feedback').textContent = message;
                return;
            }

            addressModal.hide();
            resetAddressForm();
            await loadAddresses(id ? 'Address updated successfully.' : 'Address saved successfully.');
        } catch (error) {
            console.error('Save address failed', error);
            showAddressFeedback('danger', 'We could not reach the server. Please try again.');
        } finally {
            saveAddressButton.disabled = false;
            saveAddressButton.textContent = 'Save Address';
        }
    }

    async function deleteAddress(addressId) {
        if (!window.confirm('Delete this address?')) {
            return;
        }
        try {
            const response = await fetch('/address/' + addressId, {
                method: 'DELETE',
                credentials: 'same-origin',
                headers: {
                    'Accept': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest'
                }
            });
            let payload = null;
            const contentType = response.headers.get('content-type') || '';
            if (contentType.includes('application/json')) {
                payload = await response.json().catch(function () { return null; });
            }
            console.log('Delete address response', { status: response.status, payload: payload });
            if (!response.ok) {
                const message = payload && payload.message ? payload.message : 'We could not delete this address.';
                showAddressFeedback('danger', message);
                return;
            }
            await loadAddresses('Address removed successfully.');
        } catch (error) {
            console.error('Delete address failed', error);
            showAddressFeedback('danger', 'We could not reach the server. Please try again.');
        }
    }

    document.getElementById('addAddressButton').addEventListener('click', function () {
        resetAddressForm();
    });

    saveAddressButton.addEventListener('click', saveAddress);

    addressList.addEventListener('change', function (event) {
        if (event.target.name === 'selectedAddress') {
            setSelectedAddress(event.target.value);
        }
    });

    addressList.addEventListener('click', function (event) {
        const editButton = event.target.closest('.edit-address-btn');
        const deleteButton = event.target.closest('.delete-address-btn');
        if (editButton) {
            fillAddressForm(editButton);
            addressModal.show();
        }
        if (deleteButton) {
            deleteAddress(deleteButton.dataset.addressId);
        }
    });

    checkoutForm.addEventListener('submit', function (event) {
        if (!addressIdInput.value) {
            event.preventDefault();
            showAddressFeedback('danger', 'Please select a delivery address before checkout.');
        }
    });

    window.initGoogleAddressAutocomplete = function () {
        if (!window.google || !window.google.maps || !window.google.maps.places) {
            return;
        }
        const autocomplete = new google.maps.places.Autocomplete(placeSearchInput, {
            componentRestrictions: { country: 'in' },
            fields: ['address_components', 'geometry', 'formatted_address'],
            types: ['address']
        });
        autocomplete.addListener('place_changed', function () {
            const place = autocomplete.getPlace();
            console.log('Google place selected', place);
            if (!place || !place.address_components) {
                return;
            }
            const components = {};
            place.address_components.forEach(function (component) {
                component.types.forEach(function (type) {
                    components[type] = component.long_name;
                });
            });
            document.getElementById('addressLine1').value = [components.subpremise, components.premise, components.route].filter(Boolean).join(', ') || place.formatted_address || '';
            document.getElementById('city').value = components.locality || components.sublocality_level_1 || components.administrative_area_level_2 || '';
            document.getElementById('state').value = components.administrative_area_level_1 || '';
            document.getElementById('pincode').value = components.postal_code || '';
            if (place.geometry && place.geometry.location) {
                document.getElementById('latitude').value = place.geometry.location.lat();
                document.getElementById('longitude').value = place.geometry.location.lng();
            }
        });
    };

    window.addEventListener('load', function () {
        const selected = document.querySelector('input[name="selectedAddress"]:checked');
        if (selected) {
            setSelectedAddress(selected.value);
        }
        if ('${googleMapsApiKey}') {
            const waitForGoogle = window.setInterval(function () {
                if (window.google && window.google.maps && window.google.maps.places) {
                    window.clearInterval(waitForGoogle);
                    window.initGoogleAddressAutocomplete();
                }
            }, 300);
        }
    });
</script>
</body>
</html>
