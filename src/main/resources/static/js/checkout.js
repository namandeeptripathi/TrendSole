/*
 * ================================================
 * checkout.js - JavaScript for the Checkout Page
 * ================================================
 *
 * What this file does:
 * - Loads cart items and displays them in the order summary.
 * - Shows subtotal, tax, and total.
 * - Handles the checkout form submission.
 * - Sends the order to the backend REST API.
 * - Saves order details to localStorage for the Thank You page.
 * - Redirects to thankyou.html after successful order.
 *
 * API Endpoints Used:
 * - GET  /api/cart         → Get cart items for order summary
 * - GET  /api/cart/total   → Get cart total
 * - POST /api/orders       → Place a new order
 */

// Base URL of our Spring Boot backend (dynamically resolved)
const API_BASE = window.location.origin + '/api';

// ==========================================
// Run this when the page loads
// ==========================================
document.addEventListener('DOMContentLoaded', function () {
    loadOrderSummary(); // Load cart items into the order summary
    updateCartCount();  // Update cart badge in navbar
});

// ==========================================
// Load cart items and display in order summary
// ==========================================
function loadOrderSummary() {
    fetch(API_BASE + '/cart')
        .then(function (response) {
            return response.json();
        })
        .then(function (cartItems) {
            displayOrderItems(cartItems);
            calculateTotals(cartItems);

            // If cart is empty, redirect to cart page
            if (cartItems.length === 0) {
                alert('Your cart is empty! Please add products first.');
                window.location.href = 'cart.html';
            }
        })
        .catch(function (error) {
            console.error('Error loading order summary:', error);
            document.getElementById('orderItems').innerHTML = `
                <p style="color: var(--accent-rose); font-size:0.88rem;">Unable to load order summary. Make sure the backend is running.</p>`;
        });
}

// ==========================================
// Display cart items in the order summary sidebar
// ==========================================
function displayOrderItems(cartItems) {
    var container = document.getElementById('orderItems');
    var html = '';

    for (var i = 0; i < cartItems.length; i++) {
        var item = cartItems[i];
        var product = item.product;
        var itemTotal = product.price * item.quantity;

        html += `
            <div class="d-flex justify-content-between align-items-center mb-3" 
                 style="padding-bottom:12px; border-bottom:1px solid var(--glass-border);">
                <div class="d-flex align-items-center gap-3">
                    <img src="${product.imageUrl || 'https://via.placeholder.com/50'}" 
                         alt="${product.name}" 
                         style="width:50px; height:50px; object-fit:cover; border-radius:var(--radius-sm);">
                    <div>
                        <p style="font-family:'Outfit',sans-serif; font-weight:600; margin-bottom:2px; font-size:0.85rem;">${product.name}</p>
                        <small style="color:var(--text-muted);">Qty: ${item.quantity}</small>
                    </div>
                </div>
                <span style="font-family:'Outfit',sans-serif; font-weight:700; font-size:0.9rem;">₹${itemTotal.toLocaleString('en-IN')}</span>
            </div>`;
    }

    container.innerHTML = html;
}

// ==========================================
// Calculate and display totals
// ==========================================
function calculateTotals(cartItems) {
    var subtotal = 0;

    for (var i = 0; i < cartItems.length; i++) {
        subtotal += cartItems[i].product.price * cartItems[i].quantity;
    }

    var tax = subtotal * 0.18;    // GST 18%
    var total = subtotal + tax;

    // Update the summary display
    document.getElementById('subtotal').textContent = '₹' + subtotal.toLocaleString('en-IN');
    document.getElementById('tax').textContent = '₹' + tax.toLocaleString('en-IN', { maximumFractionDigits: 2 });
    document.getElementById('totalAmount').textContent = '₹' + total.toLocaleString('en-IN', { maximumFractionDigits: 2 });

    // Save total for order placement
    localStorage.setItem('checkoutTotal', total.toFixed(2));
}

// ==========================================
// Place order (called when form is submitted)
// ==========================================
function placeOrder(event) {
    // Prevent the form from refreshing the page
    event.preventDefault();

    // Get form values
    var customerName = document.getElementById('customerName').value.trim();
    var email = document.getElementById('email').value.trim();
    var address = document.getElementById('address').value.trim();
    var city = document.getElementById('city').value.trim();
    var pincode = document.getElementById('pincode').value.trim();
    var totalAmount = parseFloat(localStorage.getItem('checkoutTotal')) || 0;

    // Get selected payment method
    var paymentMethod = document.querySelector('input[name="payment"]:checked').value;

    // Build full address string
    var fullAddress = address;
    if (city) fullAddress += ', ' + city;
    if (pincode) fullAddress += ' - ' + pincode;

    // Validate required fields
    if (!customerName || !email || !address) {
        showToast('❌ Please fill in all required fields.');
        return;
    }

    if (totalAmount <= 0) {
        showToast('❌ Cart is empty. Cannot place order.');
        return;
    }

    // Disable the button to prevent double-clicks
    var submitBtn = document.getElementById('placeOrderBtn');
    submitBtn.disabled = true;
    submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span> Placing Order...';

    // Create order request object
    var orderData = {
        customerName: customerName,
        email: email,
        address: fullAddress,
        paymentMethod: paymentMethod
    };

    // Send POST request to create the order
    fetch(API_BASE + '/orders', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify(orderData)
    })
        .then(function (response) {
            if (!response.ok) {
                throw new Error('Failed to place order');
            }
            return response.json();
        })
        .then(function (order) {
            // Order placed successfully!

            // Save order details to localStorage (for thankyou.html page)
            localStorage.setItem('orderId', order.id);
            localStorage.setItem('orderNumber', order.orderNumber || order.id);
            localStorage.setItem('orderName', customerName);
            localStorage.setItem('orderEmail', email);
            localStorage.setItem('orderTotal', order.totalAmount || totalAmount);
            localStorage.setItem('orderPayment', paymentMethod);


            // Clean up checkout data
            localStorage.removeItem('checkoutTotal');
            localStorage.removeItem('cartSubtotal');
            localStorage.removeItem('cartTax');
            localStorage.removeItem('cartTotal');

            // Redirect to Thank You page
            window.location.href = 'thankyou.html';
        })
        .catch(function (error) {
            console.error('Error placing order:', error);
            showToast('❌ Failed to place order. Please try again.');

            // Re-enable the button
            submitBtn.disabled = false;
            submitBtn.innerHTML = '<i class="bi bi-bag-check"></i> Place Order';
        });
}

// ==========================================
// Update cart badge count in navbar
// ==========================================
function updateCartCount() {
    fetch(API_BASE + '/cart')
        .then(function (response) {
            return response.json();
        })
        .then(function (cartItems) {
            var totalItems = 0;
            for (var i = 0; i < cartItems.length; i++) {
                totalItems += cartItems[i].quantity;
            }
            document.getElementById('cartCount').textContent = totalItems;
        })
        .catch(function (error) {
            document.getElementById('cartCount').textContent = '0';
        });
}

// ==========================================
// Show toast notification
// ==========================================
function showToast(message) {
    var toast = document.getElementById('toast');
    toast.textContent = message;
    toast.classList.add('show');
    setTimeout(function () {
        toast.classList.remove('show');
    }, 3000);
}
