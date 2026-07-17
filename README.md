<p align="center">
  <img src="screenshots/trendsole-logo.png" width="220" alt="TrendSole Logo">
</p>

<h1 align="center">TrendSole</h1>

<p align="center">
Enterprise-Inspired Fashion E-Commerce Platform built with Spring Boot
</p>

<p align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=springboot)
![JWT](https://img.shields.io/badge/JWT-Authentication-black?style=for-the-badge&logo=jsonwebtokens)
![MySQL](https://img.shields.io/badge/MySQL-8.x-4479A1?style=for-the-badge&logo=mysql)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-85EA2D?style=for-the-badge&logo=swagger)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker)

![GitHub Repo stars](https://img.shields.io/github/stars/namandeeptripathi/TrendSole?style=for-the-badge)
![GitHub Forks](https://img.shields.io/github/forks/namandeeptripathi/TrendSole?style=for-the-badge)
![GitHub Last Commit](https://img.shields.io/github/last-commit/namandeeptripathi/TrendSole?style=for-the-badge)
![GitHub Issues](https://img.shields.io/github/issues/namandeeptripathi/TrendSole?style=for-the-badge)
![GitHub License](https://img.shields.io/github/license/namandeeptripathi/TrendSole?style=for-the-badge)

</p>
---

## 🚀 Overview

TrendSole is a production-inspired fashion e-commerce platform developed using **Java**, **Spring Boot**, **Spring Security**, **JWT**, **Hibernate**, and **MySQL**.

The project focuses on implementing real-world e-commerce workflows rather than basic CRUD operations. It includes secure authentication, product management, shopping cart, wishlist, checkout, order lifecycle management, invoice generation, returns, exchanges, email notifications, and role-based administration.

The application follows a layered architecture with RESTful APIs and demonstrates modern backend engineering practices.

---

## 🌟 Project Highlights

| Feature | Status |
|----------|:------:|
| 🔐 JWT Authentication & Authorization | ✅ |
| 👥 Role-Based Access Control | ✅ |
| 🛍 Product & Category Management | ✅ |
| 🛒 Shopping Cart & Wishlist | ✅ |
| 📍 Address Management | ✅ |
| 📦 Complete Order Lifecycle | ✅ |
| 🧾 PDF Invoice Generation | ✅ |
| 🔄 Return Management Workflow | ✅ |
| 🔁 Exchange Management Workflow | ✅ |
| 📧 Email Notifications | ✅ |
| 📂 File Upload Support | ✅ |
| 📖 Swagger API Documentation | ✅ |
| 🐳 Docker Support | ✅ |
| 🛡 Global Exception Handling | ✅ |
| 🗄 MySQL + Spring Data JPA | ✅ |

---

# ✨ Key Features

| Module | Highlights |
|---------|------------|
| Authentication | JWT Login, Registration, Role-Based Access |
| Users | Profile & Address Management |
| Products | CRUD, Categories, Search, Inventory |
| Cart | Add, Update, Remove, Checkout |
| Wishlist | Save & Manage Products |
| Orders | Order Placement, History, Timeline |
| Invoice | PDF Invoice Generation |
| Returns | Request, Inspection, Refund Workflow |
| Exchanges | Exchange Workflow & Stock Validation |
| Email | Order & Return Notifications |
| Admin | Products, Orders, Returns & Exchanges |
| Documentation | Swagger / OpenAPI |

---

# 🛠 Tech Stack

| Category | Technology |
|-----------|------------|
| Language | Java 21 |
| Framework | Spring Boot 3.x |
| Security | Spring Security + JWT |
| ORM | Spring Data JPA + Hibernate |
| Database | MySQL |
| Build Tool | Maven |
| API Docs | Swagger |
| PDF | OpenPDF |
| Email | Spring Mail |
| Container | Docker |

---

# 🏗️ System Architecture

```text
                        Client
                          │
                          ▼
                 Spring Boot REST APIs
                          │
        ┌─────────────────┼─────────────────┐
        ▼                 ▼                 ▼
 Authentication      Business Logic     Validation
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

### Request Flow

```text
Client
   │
HTTP Request
   │
Controller
   │
Service
   │
Repository
   │
MySQL
   │
Repository
   │
Service
   │
Controller
   │
HTTP Response

```

The project follows a clean layered architecture with clear separation between controllers, services, repositories, and entities.

---

# 📂 Project Structure

```text
TrendSole
│
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.trendsole
│   │   │       ├── config
│   │   │       ├── controller
│   │   │       ├── dto
│   │   │       ├── entity
│   │   │       ├── repository
│   │   │       ├── security
│   │   │       ├── service
│   │   │       └── exception
│   │   └── resources
│   ├── test
│
├── uploads
├── Dockerfile
├── pom.xml
└── README.md
```

---

# 🔐 Security

- JWT Authentication
- Spring Security
- BCrypt Password Encryption
- Role-Based Authorization
- Stateless Authentication
- Protected Admin APIs
- Global Exception Handling
- Request Validation
- Secure File Upload Validation

---

# ⚙️ Installation

## Prerequisites

Before running TrendSole, install:

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

## Create Database

```sql
CREATE DATABASE trendsole;
```

Update `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/trendsole
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

---

## Build

```bash
mvn clean install
```

---

## Run

```bash
mvn spring-boot:run
```

Application URL

```
http://localhost:8080
```

---

# 🐳 Docker

Build Docker Image

```bash
docker build -t trendsole .
```

Run Container

```bash
docker run -p 8080:8080 trendsole
```

---

# 📖 API Documentation

After starting the application:

**Swagger UI**

```
http://localhost:8080/swagger-ui/index.html
```

**OpenAPI Docs**

```
http://localhost:8080/v3/api-docs
```

---

# 📡 API Modules

| Module | Description |
|---------|-------------|
| Authentication | Login, Registration, JWT Authentication |
| Users | Profile & Address Management |
| Products | Product CRUD, Search & Categories |
| Cart | Cart Operations |
| Wishlist | Wishlist Operations |
| Orders | Checkout, Order History & Timeline |
| Invoice | PDF Invoice Download |
| Returns | Return Workflow |
| Exchanges | Exchange Workflow |
| Admin | Products, Orders, Inventory Management |

---

# 🔄 Business Workflow

```text
Customer
───────────────

Register
     │
Login
     │
Browse Products
     │
Add to Cart
     │
Checkout
     │
Place Order
     │
Download Invoice
     │
Return / Exchange


Admin
───────────────

Manage Products
       │
Manage Inventory
       │
Process Orders
       │
Approve Returns
       │
Inspection
       │
Refund
       │
Complete Request
```

---

# 📸 Screenshots

## 🏠 Home

![Home](screenshots/home.png)

---

## 📂 Categories

![Categories](screenshots/categories.png)

---

## 🛍 Products

![Products](screenshots/products.png)

---

## 🛒 Shopping Cart

![Cart](screenshots/cart.png)

---

## 💳 Checkout

![Checkout](screenshots/checkout.png)



```

---

# 🚀 Future Enhancements

- Payment Gateway Integration
- Product Reviews & Ratings
- Coupon & Discount Engine
- Product Recommendations
- Redis Caching
- Elasticsearch
- Cloud Storage Integration
- CI/CD with GitHub Actions
- Kubernetes Deployment

---

# 🤝 Contributing

Contributions are welcome.

```bash
git checkout -b feature/your-feature

git commit -m "feat: add your feature"

git push origin feature/your-feature
```

Open a Pull Request after pushing your branch.

---

# 📄 License

This project is licensed under the **MIT License**.

---

# 👨‍💻 Author

**Naman Deep Tripathi**

Backend Developer | Java & Spring Boot

📍 Mathura, Uttar Pradesh, India

**GitHub**

https://github.com/namandeeptripathi

**LinkedIn**

https://www.linkedin.com/in/namandeeptripathi

**Email**

namandeeptripathi@gmail.com

---

<div align="center">

### ⭐ If you found this project useful, please consider giving it a Star.

Built with ❤️ using Java, Spring Boot, Spring Security, JWT, Hibernate, MySQL, Swagger, Maven & Docker.

</div>
