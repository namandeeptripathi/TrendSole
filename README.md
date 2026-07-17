<p align="center">
  <img src="logo/trendsole-logo.png" alt="TrendSole Logo" width="250"/>
</p>

<h1 align="center">TrendSole</h1>

<p align="center">
  <strong>Production-Grade Fashion E-Commerce Platform</strong>
</p>

<p align="center">
A modern e-commerce application built with <b>Spring Boot</b>, <b>Spring Security</b>, <b>JWT Authentication</b>, <b>MySQL</b>, and <b>Docker</b>, featuring complete shopping workflows, secure authentication, order lifecycle management, invoice generation, returns, exchanges, and RESTful APIs.
</p>

<p align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=springboot)
![JWT](https://img.shields.io/badge/JWT-Secure-black?style=for-the-badge&logo=jsonwebtokens)
![MySQL](https://img.shields.io/badge/MySQL-8.x-4479A1?style=for-the-badge&logo=mysql)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-85EA2D?style=for-the-badge&logo=swagger)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)

</p>

---

# 🚀 Overview

TrendSole is a **production-inspired fashion e-commerce platform** designed to demonstrate modern backend engineering practices using the Spring ecosystem.

Rather than focusing only on CRUD operations, the project models real-world shopping workflows including secure authentication, product management, shopping cart, wishlist, address management, order lifecycle tracking, invoice generation, returns, exchanges, role-based administration, and email notifications.

The application follows a clean layered architecture with RESTful APIs, making it scalable, maintainable, and suitable as a portfolio project for backend development.

---

# ✨ Features

## 🔐 Authentication & Authorization

- JWT-based Authentication
- Secure Login & Registration
- Role-Based Access Control
- Customer & Admin Roles
- Password Encryption
- Protected REST APIs
- Token Validation
- Spring Security Integration

---

## 👤 User Management

- User Registration
- Login
- Profile Management
- Update User Details
- Change Password
- Address Management
- Multiple Shipping Addresses

---

## 🛍 Product Management

- Product CRUD Operations
- Category Management
- Product Search
- Product Filtering
- Product Images
- Inventory Tracking
- Stock Validation

---

## 🛒 Shopping Experience

- Shopping Cart
- Wishlist
- Add / Remove Products
- Quantity Updates
- Price Calculation
- Checkout
- Order Placement

---

## 📦 Order Management

- Create Orders
- View Order History
- Order Details
- Order Timeline
- Order Status Updates
- Order Cancellation
- Customer Order Tracking

---

## 🧾 Invoice Management

- PDF Invoice Generation
- Company Information
- Customer Details
- Order Summary
- Itemized Billing
- Indian Currency Formatting
- Download Invoice API

---

## 🔄 Return Management

- Return Requests
- Return Images Upload
- Admin Approval
- Inspection Workflow
- Refund Workflow
- Timeline Tracking
- Email Notifications

---

## 🔁 Exchange Management

- Exchange Requests
- Size & Color Exchange
- Stock Validation
- Admin Approval
- Inventory Reservation
- Exchange Status Tracking
- Email Notifications

---

## ⚙ Admin Features

- Product Management
- Category Management
- Inventory Management
- Order Processing
- Return Processing
- Exchange Processing
- Customer Management
- Secure Admin APIs

---

## 📧 Notifications

- Order Confirmation
- Return Updates
- Exchange Updates
- Refund Notifications
- Completion Notifications

---

## 🛡 Security Features

- JWT Authentication
- BCrypt Password Hashing
- Role-Based Authorization
- Request Validation
- Secure API Access
- Global Exception Handling
- Input Validation
- Centralized Error Responses

---

# 📚 Table of Contents

- 🚀 Overview
- ✨ Features
- 🛠 Tech Stack
- 🏗 System Architecture
- 📂 Project Structure
- ⚙ Installation
- 🔑 Environment Variables
- 📖 API Overview
- 📸 Screenshots
- 🛣 Future Roadmap
- 🤝 Contributing
- 📄 License
- 👨‍💻 Author

---

# 🛠 Tech Stack

| Category | Technologies |
|-----------|--------------|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.x |
| **Security** | Spring Security, JWT |
| **Database** | MySQL 8 |
| **ORM** | Spring Data JPA, Hibernate |
| **Build Tool** | Maven |
| **Documentation** | Swagger / OpenAPI |
| **PDF Generation** | OpenPDF |
| **Email Service** | Spring Mail |
| **Containerization** | Docker |
| **Utilities** | Lombok, Validation API |

---

# 🏗 System Architecture

TrendSole follows a layered architecture that separates responsibilities across different components.

```
                Client / Frontend
                       │
                       ▼
              Spring Boot REST APIs
                       │
        ┌──────────────┼──────────────┐
        ▼              ▼              ▼
 Authentication    Business Logic   Validation
                       │
                       ▼
                 Service Layer
                       │
                       ▼
                Repository Layer
                       │
                       ▼
                    MySQL Database
```

### Architecture Layers

- **Controller Layer** – Handles HTTP requests and responses.
- **Service Layer** – Implements business rules and workflows.
- **Repository Layer** – Performs database operations using Spring Data JPA.
- **Entity Layer** – Maps Java objects to database tables.
- **Security Layer** – Protects endpoints using JWT authentication and role-based authorization.

---

# 📂 Project Structure

```text
TrendSole
│
├── src
│   ├── main
│   │
│   ├── java
│   │   └── com.trendsole
│   │       ├── config
│   │       ├── controller
│   │       ├── dto
│   │       ├── entity
│   │       ├── exception
│   │       ├── repository
│   │       ├── security
│   │       ├── service
│   │       └── util
│   │
│   └── resources
│       ├── application.properties
│       ├── static
│       └── templates
│
├── uploads
├── Dockerfile
├── pom.xml
└── README.md
```

---

# ⚙ Installation

## Prerequisites

Before running the project, ensure the following are installed:

- Java 21
- Maven 3.9+
- MySQL 8+
- Git
- Docker (Optional)

---

## Clone Repository

```bash
git clone https://github.com/namandeeptripathi/TrendSole.git

cd TrendSole
```

---

## Configure Database

Create a MySQL database.

```sql
CREATE DATABASE trendsole;
```

Update `application.properties`.

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/trendsole

spring.datasource.username=YOUR_USERNAME

spring.datasource.password=YOUR_PASSWORD
```

---

## Build Project

```bash
mvn clean install
```

---

## Run Application

```bash
mvn spring-boot:run
```

Application will start at:

```
http://localhost:8080
```

---

# 🐳 Docker

Build Docker image

```bash
docker build -t trendsole .
```

Run container

```bash
docker run -p 8080:8080 trendsole
```

---

# 📖 API Documentation

Swagger UI

```
http://localhost:8080/swagger-ui/index.html
```

OpenAPI Specification

```
http://localhost:8080/v3/api-docs
```

---

# 🔑 Configuration

Important application properties include:

- Database Configuration
- JWT Secret
- JWT Expiration
- Mail Configuration
- Company Information
- File Upload Directory
- Multipart File Limits

Example:

```properties
spring.datasource.url=

spring.datasource.username=

spring.datasource.password=

jwt.secret=

jwt.expiration=

spring.mail.username=

spring.mail.password=

trendsole.company.name=

trendsole.company.email=

trendsole.company.phone=

trendsole.company.website=
```

---

# 🔒 Security

TrendSole secures its REST APIs using Spring Security and JWT Authentication.

Implemented security features include:

- JWT Authentication
- Stateless Sessions
- Password Encryption (BCrypt)
- Role-Based Authorization
- Protected Admin Endpoints
- Request Validation
- Secure File Upload Validation
- Global Exception Handling

---

# ⚡ Business Workflows

The project models complete e-commerce workflows rather than isolated CRUD operations.

### Customer Workflow

```
Register
      │
      ▼
Login
      │
      ▼
Browse Products
      │
      ▼
Add to Cart
      │
      ▼
Checkout
      │
      ▼
Place Order
      │
      ▼
Download Invoice
      │
      ▼
Return / Exchange (Optional)
```

### Admin Workflow

```
Manage Products
        │
        ▼
Process Orders
        │
        ▼
Approve Returns
        │
        ▼
Verify Inspection
        │
        ▼
Process Refund
        │
        ▼
Complete Request
```

# 📖 API Modules

TrendSole exposes RESTful APIs for customer and admin operations.

| Module | Description |
|----------|-------------|
| 🔐 Authentication | Registration, Login, JWT Authentication |
| 👤 Users | Profile & Address Management |
| 📦 Products | Product CRUD, Search & Filters |
| 🗂 Categories | Category Management |
| 🛒 Cart | Shopping Cart Operations |
| ❤️ Wishlist | Wishlist Management |
| 📦 Orders | Checkout, Order Placement & Tracking |
| 🧾 Invoice | PDF Invoice Generation |
| 🔄 Returns | Return Request Workflow |
| 🔁 Exchanges | Exchange Request Workflow |
| ⚙ Admin | Product, Order & Customer Management |

---

# 📸 Screenshots

> Replace these images with your latest project screenshots.

| Feature | Screenshot |
|----------|------------|
| Home Page | `screenshots/home.png` |
| Products | `screenshots/products.png` |
| Product Details | `screenshots/product-details.png` |
| Shopping Cart | `screenshots/cart.png` |
| Wishlist | `screenshots/wishlist.png` |
| Checkout | `screenshots/checkout.png` |
| Order History | `screenshots/orders.png` |
| Swagger Documentation | `screenshots/swagger.png` |
| Invoice PDF | `screenshots/invoice.png` |
| Return Management | `screenshots/returns.png` |
| Exchange Management | `screenshots/exchanges.png` |
| Admin Dashboard | `screenshots/admin.png` |

---

# 🚀 Future Improvements

Although TrendSole already implements a complete shopping workflow, several enhancements can be added in future versions.

- Payment Gateway Integration (Stripe/Razorpay)
- Product Reviews & Ratings
- Coupons & Discount Engine
- Product Recommendations
- Order Analytics Dashboard
- Cloud Image Storage (AWS S3 / Cloudinary)
- Elasticsearch Integration
- Redis Caching
- Docker Compose
- CI/CD Pipeline using GitHub Actions
- Kubernetes Deployment
- Microservices Migration

---

# 📚 Learning Outcomes

Developing TrendSole provided hands-on experience with:

- Spring Boot Application Development
- Spring Security & JWT Authentication
- REST API Design
- Role-Based Authorization
- Spring Data JPA & Hibernate
- Database Design
- Business Workflow Implementation
- PDF Generation
- Email Integration
- File Upload Handling
- Exception Handling
- Validation
- Docker Fundamentals
- API Documentation using Swagger
- Layered Architecture & Clean Code Principles

---

# 🤝 Contributing

Contributions are welcome.

If you'd like to improve TrendSole:

1. Fork the repository.
2. Create a new feature branch.

```bash
git checkout -b feature/your-feature
```

3. Commit your changes.

```bash
git commit -m "feat: add your feature"
```

4. Push to your branch.

```bash
git push origin feature/your-feature
```

5. Open a Pull Request.

---

# ⭐ Support

If you found this project useful:

- ⭐ Star the repository
- 🍴 Fork the project
- 🐛 Report issues
- 💡 Suggest new features

Your support helps improve the project.

---

# 📄 License

This project is licensed under the **MIT License**.

Feel free to use, modify, and distribute this project for educational and personal purposes.

---

# 👨‍💻 Author

### Naman Deep Tripathi

Backend Developer | Java & Spring Boot Enthusiast

📍 Mathura, Uttar Pradesh, India

- GitHub: https://github.com/namandeeptripathi
- LinkedIn: https://www.linkedin.com/in/namandeeptripathi
- Email: namandeeptripathi@gmail.com

---

<div align="center">

### ⭐ If you like this project, don't forget to Star the repository!

Made with ❤️ using **Java**, **Spring Boot**, **Spring Security**, **JWT**, **Hibernate**, **MySQL**, and **Docker**.

</div>
