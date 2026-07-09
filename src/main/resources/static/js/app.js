/*
 * ================================================
 * app.js — Shared Utilities for TrendSole
 * ================================================
 *
 * This file provides shared functionality used across all pages:
 * - Navbar scroll effect (shrink + darken)
 * - Scroll-reveal animations (IntersectionObserver)
 * - Wishlist management (localStorage)
 * - Stats counter animation
 * - Newsletter form handler
 * - Loading skeleton generator
 */

// ==========================================
// NAVBAR SCROLL EFFECT
// ==========================================
(function () {
    const navbar = document.querySelector('.navbar');
    if (!navbar) return;

    let lastScroll = 0;
    window.addEventListener('scroll', function () {
        const currentScroll = window.pageYOffset;
        if (currentScroll > 50) {
            navbar.classList.add('scrolled');
        } else {
            navbar.classList.remove('scrolled');
        }
        lastScroll = currentScroll;
    }, { passive: true });
})();

// ==========================================
// SCROLL REVEAL — IntersectionObserver
// ==========================================
(function () {
    const revealElements = document.querySelectorAll('.reveal');
    if (revealElements.length === 0) return;

    const observer = new IntersectionObserver(function (entries) {
        entries.forEach(function (entry) {
            if (entry.isIntersecting) {
                entry.target.classList.add('revealed');
                observer.unobserve(entry.target);
            }
        });
    }, {
        threshold: 0.1,
        rootMargin: '0px 0px -40px 0px'
    });

    revealElements.forEach(function (el) {
        observer.observe(el);
    });
})();

// ==========================================
// WISHLIST MANAGEMENT (localStorage)
// ==========================================
const Wishlist = {
    KEY: 'trendsole_wishlist',

    getAll: function () {
        try {
            return JSON.parse(localStorage.getItem(this.KEY)) || [];
        } catch (e) {
            return [];
        }
    },

    has: function (productId) {
        return this.getAll().includes(Number(productId));
    },

    toggle: function (productId) {
        productId = Number(productId);
        var list = this.getAll();
        var index = list.indexOf(productId);
        if (index > -1) {
            list.splice(index, 1);
        } else {
            list.push(productId);
        }
        localStorage.setItem(this.KEY, JSON.stringify(list));
        this.updateBadge();
        return index === -1; // Returns true if added, false if removed
    },

    updateBadge: function () {
        var badge = document.getElementById('wishlistCount');
        if (badge) {
            var count = this.getAll().length;
            badge.textContent = count;
            badge.style.display = count > 0 ? 'inline' : 'none';
        }
    }
};

// Initialize wishlist badge on every page
document.addEventListener('DOMContentLoaded', function () {
    Wishlist.updateBadge();
});

// Global function for wishlist toggling (called from product cards)
function toggleWishlist(productId, event) {
    if (event) {
        event.stopPropagation();
        event.preventDefault();
    }
    var added = Wishlist.toggle(productId);
    var btn = document.querySelector('.wishlist-btn[data-product-id="' + productId + '"]');
    if (btn) {
        if (added) {
            btn.classList.add('active');
            btn.innerHTML = '<i class="bi bi-heart-fill"></i>';
            showToast('❤️ Added to wishlist!');
        } else {
            btn.classList.remove('active');
            btn.innerHTML = '<i class="bi bi-heart"></i>';
            showToast('Removed from wishlist');
        }
    }
}

// ==========================================
// ANIMATED COUNTER
// ==========================================
function animateCounters() {
    var counters = document.querySelectorAll('[data-count]');
    counters.forEach(function (counter) {
        if (counter.dataset.animated === 'true') return;
        counter.dataset.animated = 'true';

        var target = parseInt(counter.getAttribute('data-count'));
        var suffix = counter.getAttribute('data-suffix') || '';
        var duration = 2000;
        var startTime = null;

        function updateCounter(timestamp) {
            if (!startTime) startTime = timestamp;
            var progress = Math.min((timestamp - startTime) / duration, 1);
            // Ease out cubic
            var eased = 1 - Math.pow(1 - progress, 3);
            var current = Math.floor(eased * target);
            counter.textContent = current.toLocaleString() + suffix;
            if (progress < 1) {
                requestAnimationFrame(updateCounter);
            } else {
                counter.textContent = target.toLocaleString() + suffix;
            }
        }

        requestAnimationFrame(updateCounter);
    });
}

// Observe counters for scroll-triggered animation
(function () {
    var counterSection = document.querySelector('.hero-stats');
    if (!counterSection) return;

    var observer = new IntersectionObserver(function (entries) {
        entries.forEach(function (entry) {
            if (entry.isIntersecting) {
                animateCounters();
                observer.unobserve(entry.target);
            }
        });
    }, { threshold: 0.3 });

    observer.observe(counterSection);
})();

// ==========================================
// NEWSLETTER FORM HANDLER
// ==========================================
function handleNewsletter(event) {
    event.preventDefault();
    var input = document.getElementById('newsletterEmail');
    if (!input) return;

    var email = input.value.trim();
    if (!email || !email.includes('@')) {
        showToast('⚠️ Please enter a valid email address');
        return;
    }

    // Simulate subscription
    showToast('🎉 Thank you for subscribing!');
    input.value = '';
}

// ==========================================
// SKELETON LOADER GENERATOR
// ==========================================
function generateSkeletons(count) {
    var html = '';
    for (var i = 0; i < count; i++) {
        html += '<div class="col-lg-4 col-md-6">' +
            '<div class="skeleton-card">' +
            '<div class="skeleton-img"></div>' +
            '<div class="skeleton-body">' +
            '<div class="skeleton-line lg w-80"></div>' +
            '<div class="skeleton-line w-full"></div>' +
            '<div class="skeleton-line w-60"></div>' +
            '<div style="margin-top:20px"><div class="skeleton-line lg w-40"></div></div>' +
            '<div class="skeleton-btn"></div>' +
            '</div></div></div>';
    }
    return html;
}

// ==========================================
// TOAST (global fallback if page doesn't define it)
// ==========================================
if (typeof showToast !== 'function') {
    function showToast(message) {
        var toast = document.getElementById('toast');
        if (!toast) return;
        toast.textContent = message;
        toast.classList.add('show');
        setTimeout(function () {
            toast.classList.remove('show');
        }, 3000);
    }
}
