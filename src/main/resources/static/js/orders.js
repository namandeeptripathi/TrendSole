/*
 * ================================================
 * orders.js - Order History & Details logic
 * ================================================
 */

const API_BASE = window.location.origin + '/api';
let myOrdersList = [];

document.addEventListener('DOMContentLoaded', function () {
    updateCartCount();
    fetchMyOrders();
});

function updateCartCount() {
    fetch(API_BASE + '/cart')
        .then(res => res.json())
        .then(items => {
            let total = 0;
            items.forEach(item => total += item.quantity);
            document.getElementById('cartCount').textContent = total;
        })
        .catch(() => {
            document.getElementById('cartCount').textContent = '0';
        });
}

function fetchMyOrders() {
    fetch(API_BASE + '/orders/my-orders', {
        method: 'GET',
        credentials: 'include'
    })
    .then(response => {
        if (response.status === 401) {
            window.location.href = 'login.html';
            return null;
        }
        if (!response.ok) {
            throw new Error('Failed to load orders');
        }
        return response.json();
    })
    .then(orders => {
        if (!orders) return;
        myOrdersList = orders;
        
        document.getElementById('ordersLoading').style.display = 'none';

        if (orders.length === 0) {
            document.getElementById('ordersEmpty').style.display = 'block';
            document.getElementById('ordersList').style.display = 'none';
        } else {
            document.getElementById('ordersEmpty').style.display = 'none';
            document.getElementById('ordersList').style.display = 'block';
            renderOrdersList(orders);
        }
    })
    .catch(error => {
        console.error('Error fetching orders:', error);
        document.getElementById('ordersLoading').innerHTML = `
            <p style="color: var(--accent-rose);">Failed to load order history. Please log in or try again later.</p>
        `;
    });
}

function renderOrdersList(orders) {
    const container = document.getElementById('ordersList');
    let html = '';

    orders.forEach(order => {
        const orderDateFormatted = formatDate(order.orderDate);
        const itemCount = (order.orderItems && order.orderItems.length > 0)
            ? order.orderItems.reduce((acc, item) => acc + item.quantity, 0)
            : 0;
        
        const statusBadge = getStatusBadge(order.status);

        html += `
            <div class="order-card">
                <div class="order-header">
                    <div>
                        <span class="order-number">#${order.orderNumber || order.id}</span>
                        <div class="order-date mt-1"><i class="bi bi-clock me-1"></i>${orderDateFormatted}</div>
                    </div>
                    <div>
                        ${statusBadge}
                    </div>
                </div>

                <div class="order-body-meta mt-3">
                    <div>
                        <small class="text-muted d-block" style="font-size:0.8rem;">TOTAL AMOUNT</small>
                        <span class="fs-5 font-outfit fw-bold text-accent">₹${Number(order.totalAmount || 0).toLocaleString('en-IN')}</span>
                    </div>
                    <div>
                        <small class="text-muted d-block" style="font-size:0.8rem;">ITEMS</small>
                        <span class="fw-semibold">${itemCount} ${itemCount === 1 ? 'item' : 'items'}</span>
                    </div>
                    <div>
                        <button class="btn-primary-custom" onclick="openOrderDetails(${order.id})">
                            <i class="bi bi-eye me-1"></i> View Details
                        </button>
                    </div>
                </div>
            </div>
        `;
    });

    container.innerHTML = html;
}

function openOrderDetails(orderId) {
    const order = myOrdersList.find(o => o.id === orderId);
    if (!order) return;

    document.getElementById('modalOrderNumber').textContent = `Order #${order.orderNumber || order.id}`;
    document.getElementById('modalOrderDate').textContent = `Placed on ${formatDate(order.orderDate)}`;
    document.getElementById('modalShippingAddress').textContent = order.address || 'N/A';
    document.getElementById('modalPaymentMethod').textContent = order.paymentMethod || 'Cash on Delivery';
    document.getElementById('modalOrderStatusBadge').innerHTML = getStatusBadge(order.status);
    document.getElementById('modalTotalAmount').textContent = `₹${Number(order.totalAmount || 0).toLocaleString('en-IN')}`;

    // Render Order Items
    const itemsContainer = document.getElementById('modalOrderItems');
    let itemsHtml = '';

    if (order.orderItems && order.orderItems.length > 0) {
        order.orderItems.forEach(item => {
            const prod = item.product || {};
            const imgUrl = prod.imageUrl || 'https://via.placeholder.com/60';
            const itemTotal = (item.price || prod.price || 0) * item.quantity;

            itemsHtml += `
                <div class="order-item-row">
                    <div class="d-flex align-items-center gap-3">
                        <img src="${imgUrl}" alt="${prod.name || 'Product'}" 
                             style="width: 54px; height: 54px; object-fit: cover; border-radius: var(--radius-md);">
                        <div>
                            <p class="mb-1 font-outfit fw-bold" style="font-size: 0.92rem;">${prod.name || 'Product'}</p>
                            <small class="text-muted" style="font-size: 0.82rem;">
                                ₹${Number(item.price || prod.price || 0).toLocaleString('en-IN')} × ${item.quantity}
                            </small>
                        </div>
                    </div>
                    <div class="text-end font-outfit fw-bold" style="font-size: 0.95rem;">
                        ₹${Number(itemTotal).toLocaleString('en-IN')}
                    </div>
                </div>
            `;
        });
    } else {
        itemsHtml = `<p class="text-muted small">No item details recorded for this order.</p>`;
    }

    itemsContainer.innerHTML = itemsHtml;

    const modal = new bootstrap.Modal(document.getElementById('orderDetailsModal'));
    modal.show();
}

function getStatusBadge(status) {
    const st = (status || 'Pending').toLowerCase().trim();
    let statusClass = 'status-pending';
    let icon = 'bi-clock-history';
    let displayTitle = status || 'Pending';

    if (st.includes('confirm')) {
        statusClass = 'status-confirmed';
        icon = 'bi-check2-square';
        displayTitle = 'Confirmed';
    } else if (st.includes('ship')) {
        statusClass = 'status-shipped';
        icon = 'bi-truck';
        displayTitle = 'Shipped';
    } else if (st.includes('deliver')) {
        statusClass = 'status-delivered';
        icon = 'bi-check-circle-fill';
        displayTitle = 'Delivered';
    } else if (st.includes('cancel')) {
        statusClass = 'status-cancelled';
        icon = 'bi-x-circle-fill';
        displayTitle = 'Cancelled';
    } else if (st.includes('process')) {
        statusClass = 'status-processing';
        icon = 'bi-gear-fill';
        displayTitle = 'Processing';
    } else {
        displayTitle = 'Pending';
    }

    return `<span class="status-badge ${statusClass}"><i class="bi ${icon}"></i>${displayTitle}</span>`;
}

function formatDate(dateStr) {
    if (!dateStr) return 'N/A';
    try {
        const d = new Date(dateStr);
        return d.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    } catch (e) {
        return dateStr;
    }
}
