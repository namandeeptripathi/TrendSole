-- ================================================
-- TrendSole E-Commerce Database Schema
-- Database Name: ecommerce_db
-- ================================================

-- Step 1: Create the database (if it doesn't exist)
CREATE DATABASE IF NOT EXISTS ecommerce_db;

-- Step 2: Select the database to use
USE ecommerce_db;

-- ================================================
-- Table 1: products
-- Stores all product information (shoes)
-- ================================================
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,   -- Unique ID for each product (auto-generated)
    name VARCHAR(255) NOT NULL,             -- Product name (e.g., "Nike Air Max")
    description TEXT,                        -- Detailed description of the product
    price DOUBLE NOT NULL,                   -- Price of the product
    category VARCHAR(100),                   -- Category (e.g., "Sneakers", "Formal", "Sports")
    image_url VARCHAR(500),                  -- URL/path of the product image
    stock INT NOT NULL DEFAULT 0             -- Number of items available in stock
);

-- ================================================
-- Table 2: cart
-- Stores items added to the shopping cart
-- ================================================
CREATE TABLE IF NOT EXISTS cart (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,   -- Unique ID for each cart entry
    product_id BIGINT NOT NULL,              -- Which product is in the cart (links to products table)
    quantity INT NOT NULL DEFAULT 1,          -- How many of this product the user wants
    FOREIGN KEY (product_id) REFERENCES products(id)  -- Foreign key to products table
);

-- ================================================
-- Table 3: orders
-- Stores completed order details
-- ================================================
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,   -- Unique ID for each order
    customer_name VARCHAR(255) NOT NULL,     -- Name of the customer
    email VARCHAR(255) NOT NULL,             -- Customer's email address
    address TEXT NOT NULL,                    -- Delivery address
    total_amount DOUBLE NOT NULL,             -- Total price of the order
    order_date DATETIME DEFAULT CURRENT_TIMESTAMP  -- When the order was placed
);

-- ================================================
-- Insert some sample products (for testing)
-- ================================================
INSERT INTO products (name, description, price, category, image_url, stock) VALUES
('Nike Air Max 90', 'Classic Nike sneakers with Air cushioning for all-day comfort.', 12999.00, 'Sneakers', 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400', 25),
('Adidas Ultraboost', 'Responsive Boost midsole for energy return on every step.', 15999.00, 'Running', 'https://images.unsplash.com/photo-1608231387042-66d1773070a5?w=400', 18),
('Puma RS-X', 'Retro-inspired chunky sneakers with bold colors.', 8999.00, 'Sneakers', 'https://images.unsplash.com/photo-1605348532760-6753d2c43329?w=400', 30),
('Woodland Boots', 'Durable leather boots for outdoor adventures.', 5499.00, 'Boots', 'https://images.unsplash.com/photo-1638247025967-b4e38f787b76?w=400', 15),
('Bata Formal Shoes', 'Elegant formal shoes for office and special occasions.', 3299.00, 'Formal', 'https://images.unsplash.com/photo-1614252235316-8c857d38b5f4?w=400', 40),
('Reebok Classic', 'Timeless Reebok design with soft leather upper.', 6999.00, 'Sneakers', 'https://images.unsplash.com/photo-1606107557195-0e29a4b5b4aa?w=400', 22);
