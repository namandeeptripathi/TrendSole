/*
 * ================================================
 * auth.js — Authentication State Manager for TrendSole
 * ================================================
 *
 * This file is loaded on EVERY page to manage the navbar auth state.
 *
 * What it does:
 * 1. On page load, calls GET /api/auth/me to check if the user is logged in.
 * 2. If logged in → shows "My Profile / Logout" in the navbar.
 * 3. If not logged in → shows "Login / Register" in the navbar.
 * 4. Provides the handleLogout() function for the Logout button.
 *
 * How the navbar switching works:
 * - Guest items have class "auth-guest" (Login, Register links)
 * - Logged-in items have class "auth-user" (Profile dropdown with Logout)
 * - By default, auth-guest is visible and auth-user is hidden (display:none)
 * - This script toggles their visibility based on the auth state.
 */

(function () {
    const API_BASE = window.location.origin + '/api';

    // Run on page load
    document.addEventListener('DOMContentLoaded', function () {
        checkAuthState();
    });

    /**
     * Check if the user is currently logged in by calling /api/auth/me.
     * Updates the navbar accordingly.
     */
    function checkAuthState() {
        fetch(API_BASE + '/auth/me', {
            method: 'GET',
            credentials: 'include' // Include session cookie (JSESSIONID)
        })
        .then(function (response) {
            if (response.ok) {
                return response.json();
            }
            throw new Error('Not authenticated');
        })
        .then(function (user) {
            // User IS logged in — switch navbar to authenticated state
            showAuthenticatedNav(user);
        })
        .catch(function () {
            // User is NOT logged in — keep default guest navbar
            showGuestNav();
        });
    }

    /**
     * Show the authenticated navbar (My Profile + Logout).
     * Hide the guest navbar (Login + Register).
     */
    function showAuthenticatedNav(user) {
        // Hide guest items (Login, Register)
        var guestItems = document.querySelectorAll('.auth-guest');
        guestItems.forEach(function (el) {
            el.style.display = 'none';
        });

        // Show authenticated items (Profile dropdown)
        var userItems = document.querySelectorAll('.auth-user');
        userItems.forEach(function (el) {
            el.style.display = '';
        });

        // Set the user's name in the navbar
        var nameElements = document.querySelectorAll('.nav-user-name');
        nameElements.forEach(function (el) {
            // Show first name only for a cleaner navbar
            var firstName = user.fullName ? user.fullName.split(' ')[0] : 'Profile';
            el.textContent = firstName;
        });
    }

    /**
     * Show the guest navbar (Login + Register).
     * Hide the authenticated navbar (My Profile + Logout).
     * This is the default state — auth-user items are hidden via inline style.
     */
    function showGuestNav() {
        var guestItems = document.querySelectorAll('.auth-guest');
        guestItems.forEach(function (el) {
            el.style.display = '';
        });

        var userItems = document.querySelectorAll('.auth-user');
        userItems.forEach(function (el) {
            el.style.display = 'none';
        });
    }

    /**
     * Handle Logout — called when the user clicks "Logout" in the navbar dropdown.
     * 1. Calls POST /api/auth/logout to invalidate the session.
     * 2. Shows a toast notification.
     * 3. Redirects to the homepage.
     */
    window.handleLogout = function () {
        fetch(API_BASE + '/auth/logout', {
            method: 'POST',
            credentials: 'include'
        })
        .then(function () {
            // Show toast if available
            if (typeof showToast === 'function') {
                showToast('👋 Logged out successfully!');
            }
            // Redirect to homepage after a brief delay
            setTimeout(function () {
                window.location.href = 'index.html';
            }, 1000);
        })
        .catch(function () {
            // Even if the API call fails, clear client state and redirect
            window.location.href = 'index.html';
        });
    };
})();
