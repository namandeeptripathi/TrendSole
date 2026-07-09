/*
 * ================================================
 * product-meta.js — Frontend Product Metadata
 * ================================================
 *
 * Since the backend Product model only has:
 * name, description, price, category, imageUrl, stock
 *
 * This file provides client-side metadata for:
 * - Brand extraction (from product name)
 * - Discount percentages (by category)
 * - Star ratings (random realistic range)
 */

const ProductMeta = {

    // Extract brand from product name (first word or known brand)
    extractBrand: function(name) {
        if (!name) return '';
        var knownBrands = [
            'Nike', 'Adidas', 'Puma', 'Reebok', 'Converse', 'Vans',
            'New Balance', 'Asics', 'Jordan', 'Timberland', 'Clarks',
            'Dr. Martens', 'Levi\'s', 'Zara', 'H&M', 'Uniqlo',
            'Ralph Lauren', 'Calvin Klein', 'Tommy Hilfiger', 'Gap',
            'Mango', 'Ray-Ban', 'Casio', 'Titan', 'Fossil',
            'Daniel Wellington', 'Oakley', 'Swatch', 'Samsonite',
            'Wildcraft', 'Herschel', 'American Tourister', 'Safari',
            'Skybags', 'Under Armour'
        ];
        for (var i = 0; i < knownBrands.length; i++) {
            if (name.indexOf(knownBrands[i]) === 0) {
                return knownBrands[i];
            }
        }
        return name.split(' ')[0];
    },

    // Category-based discount (some categories get discounts)
    getDiscount: function(category, productId) {
        // Use product ID to create varied discounts
        var id = productId || 0;
        var discountMap = {
            'Footwear': [0, 10, 15, 0, 20, 0, 10, 0, 15, 0, 25, 10],
            'Clothing': [10, 0, 15, 20, 0, 10, 0, 25, 15, 0, 10, 0],
            'Accessories': [0, 15, 0, 10, 0, 20, 0, 10, 15, 0, 0, 10],
            'Bags': [10, 0, 15, 0, 20, 0, 10, 15, 0, 25, 0, 10],
            'Activewear': [0, 10, 0, 15, 0, 20, 10, 0, 0, 15, 0, 10]
        };
        var discounts = discountMap[category] || [0];
        return discounts[id % discounts.length];
    },

    // Generate realistic rating based on product ID
    getRating: function(productId) {
        var ratings = [4.5, 4.3, 4.7, 4.1, 4.8, 4.2, 4.6, 4.4, 4.9, 4.0, 4.5, 4.3];
        return ratings[(productId || 0) % ratings.length];
    },

    // Generate star HTML from rating
    renderStars: function(rating) {
        var fullStars = Math.floor(rating);
        var hasHalf = (rating - fullStars) >= 0.5;
        var html = '';
        for (var i = 0; i < fullStars; i++) {
            html += '<i class="bi bi-star-fill"></i>';
        }
        if (hasHalf) {
            html += '<i class="bi bi-star-half"></i>';
        }
        var empty = 5 - fullStars - (hasHalf ? 1 : 0);
        for (var j = 0; j < empty; j++) {
            html += '<i class="bi bi-star"></i>';
        }
        html += '<span class="rating-value">' + rating.toFixed(1) + '</span>';
        return html;
    },

    // Calculate original price from discounted price
    getOriginalPrice: function(price, discount) {
        if (!discount || discount <= 0) return null;
        return Math.round(price / (1 - discount / 100));
    }
};
