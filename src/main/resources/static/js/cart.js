/*
 * ================================================
 * cart.js - JavaScript for the Shopping Cart Page
 * ================================================
 *
 * What this file does:
 * - Fetches all cart items from the backend.
 * - Displays each item with product image, name, price, and quantity.
 * - Handles quantity increase/decrease buttons.
 * - Handles removing individual items and clearing the entire cart.
 * - Calculates and displays subtotal, tax, and total.
 *
 * API Endpoints Used:
 * - GET    /api/cart              → Get all cart items
 * - PUT    /api/cart/{id}?quantity= → Update item quantity
 * - DELETE /api/cart/{id}          → Remove one item
 * - DELETE /api/cart/clear         → Clear entire cart
 * - GET    /api/cart/total         → Get cart total
 */

// Base URL of our Spring Boot backend (dynamically resolved)
const API_BASE = window.location.origin + '/api';

// ==========================================
// Run this when the page loads
// ==========================================
document.addEventListener('DOMContentLoaded', function () {
    loadCartItems();   // Load all cart items
    updateCartCount(); // Update cart badge in navbar
});

// ==========================================
// Load all cart items from the backend
// ==========================================
function loadCartItems() {
    fetch(API_BASE + '/cart')
        .then(function (response) {
            return response.json();
        })
        .then(function (cartItems) {
            displayCartItems(cartItems);
            calculateTotals(cartItems);
        })
        .catch(function (error) {
            console.error('Error loading cart:', error);
            document.getElementById('cartItemsContainer').innerHTML = `
                <div class="empty-state">
                    <div class="empty-icon">⚠️</div>
                    <h4>Unable to Load Cart</h4>
                    <p>Make sure the backend server is running on port 8080.</p>
                </div>`;
        });
}

// ==========================================
// Display cart items on the page
// ==========================================
function displayCartItems(cartItems) {
    var container = document.getElementById('cartItemsContainer');
    var clearBtn = document.getElementById('clearCartBtn');
    var summary = document.getElementById('cartSummary');

    // If cart is empty
    if (cartItems.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-icon">🛒</div>
                <h4>Your Cart is Empty</h4>
                <p>Looks like you haven't added any products yet.</p>
                <a href="products.html" class="btn-primary-custom">
                    <i class="bi bi-bag-plus me-1"></i> Start Shopping
                </a>
            </div>`;
        clearBtn.style.display = 'none';
        summary.style.display = 'none';
        return;
    }

    // Show clear button and summary
    clearBtn.style.display = 'block';
    summary.style.display = 'block';

    // Build HTML for each cart item
    var html = '';
    for (var i = 0; i < cartItems.length; i++) {
        var item = cartItems[i];
        var product = item.product;
        var itemTotal = product.price * item.quantity;

        html += `
            <div class="cart-item">
                <div class="row align-items-center">
                    <!-- Product Image -->
                    <div class="col-md-2 col-3">
                        <img src="${product.imageUrl || 'https://via.placeholder.com/100?text=No+Image'}" 
                             alt="${product.name}">
                    </div>

                    <!-- Product Info -->
                    <div class="col-md-4 col-9">
                        <h6 class="item-name">${product.name}</h6>
                        <p class="item-category mb-1">${product.category || 'General'}</p>
                        <p class="item-price mb-0">₹${product.price.toLocaleString('en-IN')}</p>
                    </div>

                    <!-- Quantity Controls -->
                    <div class="col-md-3 col-6 mt-2 mt-md-0">
                        <div class="quantity-control">
                            <button class="btn-qty" onclick="decreaseQty(${item.id}, ${item.quantity})">−</button>
                            <span class="qty-value">${item.quantity}</span>
                            <button class="btn-qty" onclick="increaseQty(${item.id}, ${item.quantity})">+</button>
                        </div>
                    </div>

                    <!-- Item Total & Remove -->
                    <div class="col-md-3 col-6 mt-2 mt-md-0 text-end">
                        <p style="font-family:'Outfit',sans-serif; font-weight:700; margin-bottom:8px;">
                            ₹${itemTotal.toLocaleString('en-IN')}
                        </p>
                        <button class="btn-remove" onclick="removeItem(${item.id})">
                            <i class="bi bi-trash me-1"></i> Remove
                        </button>
                    </div>
                </div>
            </div>`;
    }

    container.innerHTML = html;
}

// ==========================================
// Calculate subtotal, tax, and total
// ==========================================
function calculateTotals(cartItems) {
    var subtotal = 0;

    // Calculate subtotal: sum of (price × quantity) for each item
    for (var i = 0; i < cartItems.length; i++) {
        subtotal += cartItems[i].product.price * cartItems[i].quantity;
    }

    // Calculate tax (GST 18%)
    var tax = subtotal * 0.18;

    // Calculate total
    var total = subtotal + tax;

    // Display values on the page
    document.getElementById('subtotal').textContent = '₹' + subtotal.toLocaleString('en-IN');
    document.getElementById('tax').textContent = '₹' + tax.toLocaleString('en-IN', { maximumFractionDigits: 2 });
    document.getElementById('totalAmount').textContent = '₹' + total.toLocaleString('en-IN', { maximumFractionDigits: 2 });

    // Save total to localStorage (used on checkout page)
    localStorage.setItem('cartSubtotal', subtotal);
    localStorage.setItem('cartTax', tax);
    localStorage.setItem('cartTotal', total);
}

// ==========================================
// Increase quantity by 1
// ==========================================
function increaseQty(cartItemId, currentQty) {
    var newQty = currentQty + 1;
    updateQuantity(cartItemId, newQty);
}

// ==========================================
// Decrease quantity by 1 (minimum is 1)
// ==========================================
function decreaseQty(cartItemId, currentQty) {
    if (currentQty <= 1) {
        // If quantity is 1, ask if they want to remove it
        removeItem(cartItemId);
        return;
    }
    var newQty = currentQty - 1;
    updateQuantity(cartItemId, newQty);
}

// ==========================================
// Update quantity via API
// ==========================================
function updateQuantity(cartItemId, newQty) {
    fetch(API_BASE + '/cart/' + cartItemId + '?quantity=' + newQty, {
        method: 'PUT'
    })
        .then(function (response) {
            return response.json();
        })
        .then(function (data) {
            loadCartItems();   // Reload cart to show updated data
            updateCartCount(); // Update navbar badge
        })
        .catch(function (error) {
            console.error('Error updating quantity:', error);
            showToast('❌ Failed to update quantity.');
        });
}

// ==========================================
// Remove a single item from the cart
// ==========================================
function removeItem(cartItemId) {
    fetch(API_BASE + '/cart/' + cartItemId, {
        method: 'DELETE'
    })
        .then(function (response) {
            showToast('🗑️ Item removed from cart.');
            loadCartItems();   // Reload cart
            updateCartCount(); // Update navbar badge
        })
        .catch(function (error) {
            console.error('Error removing item:', error);
            showToast('❌ Failed to remove item.');
        });
}

// ==========================================
// Clear the entire cart
// ==========================================
function clearCart() {
    // Confirm before clearing
    if (!confirm('Are you sure you want to clear the entire cart?')) {
        return;
    }

    fetch(API_BASE + '/cart/clear', {
        method: 'DELETE'
    })
        .then(function (response) {
            showToast('🛒 Cart cleared!');
            loadCartItems();   // Reload cart (will show empty state)
            updateCartCount(); // Update navbar badge
        })
        .catch(function (error) {
            console.error('Error clearing cart:', error);
            showToast('❌ Failed to clear cart.');
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
