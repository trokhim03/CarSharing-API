# 🚗 Car-Sharing API

This API is designed to manage a car-sharing service, offering features such as car inventory management, rental processing, user registration, JWT authentication, payment integration with Stripe, and Telegram notifications. It provides a complete solution for building a modern car-sharing platform with role-based access control for users and administrators.

## 🛠️ Technologies & Tools
- **Core Language**: Java 17
- **Framework**: Spring Boot 3.4.4 (with Spring Web, Spring Data JPA, Spring Security)
- **Database**: MySQL 8.0 (with Liquibase for schema migrations)
- **Payment Processing**: Stripe API
- **Notifications**: Telegram Bot API
- **Testing**: JUnit 5, MockMvc, Testcontainers
- **API Documentation**: Swagger/OpenAPI
- **Dependency Management**: Maven
- **Containerization**: Docker, Docker Compose
- **Object Mapping**: MapStruct 1.6.3
- **Validation**: Jakarta Validation 3.4.5

## ⚡ Functionality
The project provides a comprehensive set of features for managing cars, users, rentals, and payments:

**👤 User Management** (`AuthController`, `UsersController`)
- User registration and authentication with JWT
- Role-based access control (MANAGER/CUSTOMER)
- Profile management

**🚗 Car Management** (`CarsController`)
- CRUD operations for car inventory
- Car type classification (SEDAN, SUV, HATCHBACK, UNIVERSAL)
- Inventory tracking
- Daily fee management

**📅 Rental Management** (`RentalsController`)
- Create new rentals with inventory checks
- Return management with inventory updates
- Rental status tracking (active/returned)
- Filtering by user and status

**💳 Payment Processing** (`PaymentsController`)
- Integration with Stripe payment system
- Payment session management
- Success/cancel payment handlers
- Fine calculation for overdue rentals

**🔔 Telegram notifications**
- New rentals
- Overdue rentals
- Successful payments

## 📊 Database Schema

![image](https://github.com/user-attachments/assets/7ca5d4c0-4331-419a-bd03-fd238edc5cff)


## 🚀 Getting Started
## 📖 API Documentation

Explore the API endpoints with Swagger UI:

**🔗 [Swagger UI](http://localhost:8080/swagger-ui/index.html)**

## 🔒 Security
- JWT authentication for all endpoints

- Role-based authorization

- Password encryption

- Secure payment processing with Stripe

- All sensitive data stored in environment variables
  
## 📌 Example API Requests
**Register a new user:**

```bash
curl -X POST "http://localhost:8080/api/auth/registration" \
-H "Content-Type: application/json" \
-d '{
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "password": "securePassword123",
    "repeatPassword": "securePassword123"
}'
```

**Get available cars:**

```bash
curl -X GET "http://localhost:8080/api/cars" \
-H "Authorization: Bearer your.jwt.token"
```
