# 👟 TrendSole - E-Commerce Shoe Store

TrendSole is a modern, fully-functional **E-Commerce Web Application** designed specifically for a premium Shoe Store. The application features a robust **Spring Boot REST API** backend, a persistent **MySQL Database**, and an interactive, responsive **HTML5/CSS3/JavaScript Frontend** integrated seamlessly to deliver a smooth shopping experience.

---

## 🚀 Features

### **Backend (Spring Boot)**
*   **RESTful APIs**: Organized endpoints for product catalog, shopping cart operations, and checkout/order placement.
*   **Data Persistence**: Built with Spring Data JPA & Hibernate for seamless interaction with MySQL.
*   **Automatic Database Synchronization**: Automatically manages tables schema and inserts seed data.
*   **CORS Enabled**: Configured to allow cross-origin requests for flexible deployment.
*   **Robust Exception Handling**: Custom resource exception management (e.g., `ResourceNotFoundException`).

### **Frontend (HTML5, CSS3 & JavaScript)**
*   **Interactive Product Catalog**: Dynamic product listing with category filtering and keyword search.
*   **Real-time Shopping Cart**: Add, update quantities, remove items, and see live total price calculation.
*   **Secure Checkout**: Place order form that clears the cart upon success and routes to a thank you screen.
*   **Modern Premium UI**: Sleek, responsive design styled with vanilla CSS, custom color palettes, and responsive grids.

---

## 🛠️ Tech Stack

*   **Backend**: Java 21, Spring Boot 3.3.0 (Web, Data JPA, DevTools)
*   **Database**: MySQL 8.x
*   **Frontend**: HTML5, CSS3, JavaScript (Fetch API for AJAX)
*   **ORM**: Hibernate / Spring Data JPA
*   **Build Tool**: Maven
*   **Utilities**: Lombok

---

## 📂 Project Structure

```text
Trendsole/
├── src/
│   ├── main/
│   │   ├── java/com/trendsole/
│   │   │   ├── config/             # CORS and Security Configurations
│   │   │   ├── controller/         # REST API Controllers (Product, Cart, Order)
│   │   │   ├── exception/          # Custom Exception Handlers
│   │   │   ├── model/              # JPA Entities (Product, CartItem, Order)
│   │   │   ├── repository/         # Database repositories (JPA Interfaces)
│   │   │   ├── service/            # Business Logic Layer
│   │   │   └── TrendSoleApplication.java # Spring Boot Main Class
│   │   └── resources/
│   │       ├── static/             # Frontend Assets (HTML, CSS, JS)
│   │       │   ├── css/            # Style sheets
│   │       │   ├── js/             # Frontend script files
│   │       │   ├── cart.html       # Cart view
│   │       │   ├── checkout.html   # Checkout/Order form
│   │       │   ├── index.html      # Store landing page
│   │       │   ├── products.html   # Product listing
│   │       │   └── thankyou.html   # Post-checkout confirmation
│   │       ├── application.properties # Database & Application configs
│   │       └── schema.sql          # Predefined database schemas & Seed data
├── pom.xml                         # Maven dependencies & configurations
└── .gitignore                      # Git exclusion rules
```

---

## 📡 API Endpoints

### 1. **Products API (`/api/products`)**
| HTTP Method | Endpoint | Description |
| :--- | :--- | :--- |
| **GET** | `/api/products` | Retrieve all products in the catalog |
| **GET** | `/api/products/{id}` | Retrieve a specific product details |
| **GET** | `/api/products/category/{category}` | Filter products by category (e.g., Sneakers) |
| **GET** | `/api/products/search?name={name}` | Search products containing the name |
| **POST** | `/api/products` | Create a new product |
| **PUT** | `/api/products/{id}` | Edit product details |
| **DELETE** | `/api/products/{id}` | Remove a product from the database |

### 2. **Cart API (`/api/cart`)**
| HTTP Method | Endpoint | Description |
| :--- | :--- | :--- |
| **GET** | `/api/cart` | View all items in the shopping cart |
| **POST** | `/api/cart/add?productId={id}&quantity={qty}` | Add or increment product in cart |
| **PUT** | `/api/cart/{id}?quantity={qty}` | Update cart item quantity |
| **DELETE** | `/api/cart/{id}` | Remove specific item from cart |
| **DELETE** | `/api/cart/clear` | Empty the entire cart |
| **GET** | `/api/cart/total` | Get total price of all cart items |

### 3. **Orders API (`/api/orders`)**
| HTTP Method | Endpoint | Description |
| :--- | :--- | :--- |
| **GET** | `/api/orders` | View list of all orders placed |
| **GET** | `/api/orders/{id}` | Get order details by ID |
| **GET** | `/api/orders/email/{email}` | View orders history of a customer by email |
| **POST** | `/api/orders` | Place a new order |
| **DELETE** | `/api/orders/{id}` | Delete/Cancel an order |

---

## ⚙️ Setup & Installation

### **Prerequisites**
*   **Java JDK 21** installed
*   **MySQL Server** running
*   **Maven** installed (or use the IDE Maven wrapper)

### **Step 1: Database Setup**
1. Open your MySQL client and run the following command to create the database:
   ```sql
   CREATE DATABASE ecommerce_db;
   ```
2. The application will automatically execute the schema definitions and load mock products from [schema.sql](file:///Users/apple/Desktop/Namandeep%20Tripathi/Trendsole/src/main/resources/schema.sql) when it runs for the first time.

### **Step 2: Configure Database Credentials**
Open [application.properties](file:///Users/apple/Desktop/Namandeep%20Tripathi/Trendsole/src/main/resources/application.properties) and update the database username and password with your MySQL details:
```properties
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### **Step 3: Run the Application**
Navigate to the root directory and start the Spring Boot application using Maven:
```bash
mvn spring-boot:run
```
Or run the main method in `TrendSoleApplication.java` using your favorite IDE (IntelliJ IDEA, Eclipse, VS Code).

### **Step 4: Access Frontend**
Once the application starts successfully, open your browser and navigate to:
```text
http://localhost:8080
```
You will be greeted by the gorgeous TrendSole landing page where you can browse shoes, manage the cart, and place orders!

---

## 🗄️ Database Schema Details

The application uses three primary tables inside `ecommerce_db`:
1.  **`products`**: Stores details like name, description, price, category, stock, and image URL.
2.  **`cart`**: Keeps track of product IDs and quantities selected by users.
3.  **`orders`**: Stores order details (customer name, email, delivery address, order date, and total price).

---

## 🤝 Contribution Guidelines

1. Fork the Project.
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`).
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the Branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request.

---

*Made with ❤️ by Namandeep Tripathi*
