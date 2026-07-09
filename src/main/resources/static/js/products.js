/*
 * ================================================
 * products.js - JavaScript for the Products Page
 * ================================================
 *
 * What this file does:
 * - Fetches all products from the backend REST API.
 * - Displays products as cards on the page.
 * - Handles search (by name) and category filter.
 * - Handles "Add to Cart" button clicks.
 * - Handles wishlist toggling (localStorage).
 * - Handles wishlist view mode.
 * - Updates the cart badge count in the navbar.
 *
 * API Endpoints Used:
 * - GET  /api/products                → Get all products
 * - GET  /api/products/search?name=   → Search by name
 * - GET  /api/products/category/{cat} → Filter by category
 * - POST /api/cart/add?productId=&quantity= → Add to cart
 * - GET  /api/cart                    → Get cart items (for badge count)
 */

// Base URL of our Spring Boot backend (dynamically resolved)
const API_BASE = window.location.origin + '/api';

// Variable to store all products (used for filtering)
let allProducts = [];

// Whether we're currently in wishlist view
let isWishlistView = false;

// ==========================================
// Run this when the page loads
// ==========================================
document.addEventListener('DOMContentLoaded', function () {
    loadProducts();    // Load all products from the API
    updateCartCount(); // Update cart badge in navbar
    checkUrlParams();  // Check if category or wishlist view was passed in URL
});

// ==========================================
// Load all products from the backend
// ==========================================
function loadProducts() {
    fetch(API_BASE + '/products')
        .then(function (response) {
            return response.json(); // Convert response to JSON
        })
        .then(function (products) {
            allProducts = products; // Save products for later filtering

            // Check if we should show wishlist view
            var urlParams = new URLSearchParams(window.location.search);
            if (urlParams.get('view') === 'wishlist') {
                showWishlistView();
            } else {
                displayProducts(products); // Show products on the page
            }
        })
        .catch(function (error) {
            console.error('Error loading products:', error);
            document.getElementById('productsContainer').innerHTML = `
                <div class="empty-state">
                    <div class="empty-icon">⚠️</div>
                    <h4>Unable to Load Products</h4>
                    <p>Make sure the backend server is running on port 8080.</p>
                </div>`;
            document.getElementById('productCount').textContent = '';
        });
}

// ==========================================
// Display products on the page
// ==========================================
function displayProducts(products) {
    var container = document.getElementById('productsContainer');
    var countElement = document.getElementById('productCount');

    // If no products found
    if (products.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-icon">🔍</div>
                <h4>No Products Found</h4>
                <p>Try a different search term or category.</p>
                <button class="btn-primary-custom" onclick="loadProducts()">
                    <i class="bi bi-arrow-clockwise me-1"></i> Show All Products
                </button>
            </div>`;
        countElement.textContent = '0 products found';
        return;
    }

    // Update product count text
    countElement.textContent = 'Showing ' + products.length + ' product' + (products.length !== 1 ? 's' : '');

    // Build HTML for all product cards
    var html = '';
    for (var i = 0; i < products.length; i++) {
        var product = products[i];
        html += createProductCard(product, i);
    }

    container.innerHTML = html;

    // Re-observe new elements for reveal animation
    container.querySelectorAll('.reveal').forEach(function (el) {
        var observer = new IntersectionObserver(function (entries) {
            entries.forEach(function (entry) {
                if (entry.isIntersecting) {
                    entry.target.classList.add('revealed');
                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.1 });
        observer.observe(el);
    });
}

// ==========================================
// Create HTML for a single product card
// ==========================================
function createProductCard(product, index) {
    // Determine stock status text and color
    var stockClass = 'in-stock';
    var stockText = 'In Stock';

    if (product.stock <= 0) {
        stockClass = 'out-of-stock';
        stockText = 'Out of Stock';
    } else if (product.stock <= 10) {
        stockClass = 'low-stock';
        stockText = 'Only ' + product.stock + ' left';
    }

    var isWishlisted = Wishlist.has(product.id);
    var delay = (index || 0) * 0.04;

    // Enhanced metadata from ProductMeta
    var brand = (typeof ProductMeta !== 'undefined') ? ProductMeta.extractBrand(product.name) : '';
    var discount = (typeof ProductMeta !== 'undefined') ? ProductMeta.getDiscount(product.category, product.id) : 0;
    var rating = (typeof ProductMeta !== 'undefined') ? ProductMeta.getRating(product.id) : 0;
    var originalPrice = (typeof ProductMeta !== 'undefined') ? ProductMeta.getOriginalPrice(product.price, discount) : null;
    var starsHtml = (typeof ProductMeta !== 'undefined' && rating > 0) ? ProductMeta.renderStars(rating) : '';

    // Build and return the product card HTML
    return `
        <div class="col-lg-4 col-md-6 reveal" style="transition-delay: ${delay}s;">
            <div class="product-card">
                <div class="card-img-wrapper">
                    <img src="${product.imageUrl || 'https://via.placeholder.com/400x300?text=No+Image'}" 
                         alt="${product.name}" loading="lazy">
                    ${brand ? '<span class="brand-tag">' + brand + '</span>' : ''}
                    <span class="category-badge">${product.category || 'General'}</span>
                    ${discount > 0 ? '<span class="discount-badge">-' + discount + '%</span>' : ''}
                    <button class="wishlist-btn ${isWishlisted ? 'active' : ''}" 
                            data-product-id="${product.id}"
                            onclick="toggleWishlist(${product.id}, event)">
                        <i class="bi bi-heart${isWishlisted ? '-fill' : ''}"></i>
                    </button>
                </div>
                <div class="card-body">
                    <h5 class="product-name">${product.name}</h5>
                    ${starsHtml ? '<div class="rating-stars">' + starsHtml + '</div>' : ''}
                    <p class="product-desc">${product.description || 'No description available.'}</p>
                    <div class="price-row">
                        <span class="product-price">₹${product.price.toLocaleString('en-IN')}</span>
                        ${originalPrice ? '<span class="original-price">₹' + originalPrice.toLocaleString('en-IN') + '</span>' : ''}
                        <span class="stock-info ${stockClass}">${stockText}</span>
                    </div>
                    <button class="btn-add-cart" 
                            onclick="addToCart(${product.id})" 
                            ${product.stock <= 0 ? 'disabled' : ''}>
                        <i class="bi bi-bag-plus me-1"></i> 
                        ${product.stock <= 0 ? 'Out of Stock' : 'Add to Cart'}
                    </button>
                </div>
            </div>
        </div>`;
}

// ==========================================
// Search products by name
// Called when user types in the search bar
// ==========================================
function searchProducts() {
    var searchTerm = document.getElementById('searchInput').value.trim();

    // Exit wishlist view if searching
    if (isWishlistView) {
        isWishlistView = false;
        document.getElementById('wishlistBanner').style.display = 'none';
    }

    if (searchTerm === '') {
        // If search is empty, show all products
        displayProducts(allProducts);
        return;
    }

    // Call the search API
    fetch(API_BASE + '/products/search?name=' + encodeURIComponent(searchTerm))
        .then(function (response) {
            return response.json();
        })
        .then(function (products) {
            displayProducts(products);
        })
        .catch(function (error) {
            console.error('Error searching products:', error);
        });
}

// ==========================================
// Filter products by category
// Called when user clicks a category button
// ==========================================
function filterByCategory(category, buttonElement) {
    // Update active button style
    var allButtons = document.querySelectorAll('.filter-btn');
    for (var i = 0; i < allButtons.length; i++) {
        allButtons[i].classList.remove('active');
    }
    buttonElement.classList.add('active');

    // Clear search input
    document.getElementById('searchInput').value = '';

    // Exit wishlist view
    if (isWishlistView) {
        isWishlistView = false;
        document.getElementById('wishlistBanner').style.display = 'none';
    }

    if (category === 'All') {
        // Show all products
        displayProducts(allProducts);
        return;
    }

    // Call the category filter API
    fetch(API_BASE + '/products/category/' + encodeURIComponent(category))
        .then(function (response) {
            return response.json();
        })
        .then(function (products) {
            displayProducts(products);
        })
        .catch(function (error) {
            console.error('Error filtering products:', error);
        });
}

// ==========================================
// Wishlist View
// ==========================================
function showWishlistView() {
    isWishlistView = true;
    document.getElementById('wishlistBanner').style.display = 'block';

    var wishlistIds = Wishlist.getAll();
    if (wishlistIds.length === 0) {
        document.getElementById('productsContainer').innerHTML = `
            <div class="empty-state">
                <div class="empty-icon">❤️</div>
                <h4>Your Wishlist is Empty</h4>
                <p>Start adding products to your wishlist by clicking the heart icon.</p>
                <button class="btn-primary-custom" onclick="clearWishlistView()">
                    <i class="bi bi-grid me-1"></i> Browse Products
                </button>
            </div>`;
        document.getElementById('productCount').textContent = '0 wishlist items';
        return;
    }

    var wishlistProducts = allProducts.filter(function (p) {
        return wishlistIds.includes(p.id);
    });

    document.getElementById('productCount').textContent = 'Showing ' + wishlistProducts.length + ' wishlist item' + (wishlistProducts.length !== 1 ? 's' : '');
    displayProducts(wishlistProducts);
}

function clearWishlistView() {
    isWishlistView = false;
    document.getElementById('wishlistBanner').style.display = 'none';
    // Remove view param from URL
    window.history.replaceState({}, '', 'products.html');
    displayProducts(allProducts);

    // Reset filter buttons
    var allButtons = document.querySelectorAll('.filter-btn');
    allButtons.forEach(function (btn) { btn.classList.remove('active'); });
    if (allButtons.length > 0) allButtons[0].classList.add('active');
}

// ==========================================
// Add a product to the cart
// ==========================================
function addToCart(productId) {
    fetch(API_BASE + '/cart/add?productId=' + productId + '&quantity=1', {
        method: 'POST'
    })
        .then(function (response) {
            return response.json();
        })
        .then(function (data) {
            showToast('✅ Product added to cart!');
            updateCartCount(); // Refresh cart count in navbar
        })
        .catch(function (error) {
            console.error('Error adding to cart:', error);
            showToast('❌ Failed to add product to cart.');
        });
}

// ==========================================
// Update cart item count in the navbar badge
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
// Check URL parameters (for category links from footer)
// Example: products.html?category=Sneakers
// ==========================================
function checkUrlParams() {
    var urlParams = new URLSearchParams(window.location.search);
    var category = urlParams.get('category');

    if (category) {
        // Find and click the matching category button
        var buttons = document.querySelectorAll('.filter-btn');
        for (var i = 0; i < buttons.length; i++) {
            if (buttons[i].textContent.trim() === category) {
                buttons[i].click();
                return;
            }
        }
    }
}

// ==========================================
// Show toast notification (bottom-right popup)
// ==========================================
function showToast(message) {
    var toast = document.getElementById('toast');
    toast.textContent = message;
    toast.classList.add('show');

    // Hide after 3 seconds
    setTimeout(function () {
        toast.classList.remove('show');
    }, 3000);
}
