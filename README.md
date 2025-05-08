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

**🔔 Notification System**
- Telegram notifications for:
  - New rentals
  - Overdue rentals
  - Successful payments
- Scheduled daily checks for overdue rentals

## 📊 Database Schema

![image](https://github.com/user-attachments/assets/7ca5d4c0-4331-419a-bd03-fd238edc5cff)


## 🚀 Getting Started
