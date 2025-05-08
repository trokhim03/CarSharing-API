# 🚗 Car Sharing Service

[![CI](https://github.com/your-org/car-sharing-app/actions/workflows/ci.yml/badge.svg)](https://github.com/your-org/car-sharing-app/actions/workflows/ci.yml)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

Modern car sharing platform that automates rental processes with secure payments and real-time notifications.

## 🌟 Features

- **User Management**
  - JWT authentication & role-based access (MANAGER | CUSTOMER)
  - Profile management

- **Car Inventory**
  - CRUD operations for cars (MANAGER only)
  - Public catalog with availability tracking

- **Rental System**
  - Book cars with automatic inventory adjustment
  - Rental history with filters (active/returned)
  - Secure return process

- **Payments**
  - Stripe integration for credit card payments
  - Automatic fee calculation (rentals & fines)
  - Payment session tracking

- **Notifications**
  - Telegram bot for real-time alerts
  - Daily overdue rental checks

## 🛠 Tech Stack

**Backend:**  
![Java](https://img.shields.io/badge/Java-21-red?logo=java)  
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.1-green?logo=spring)  

**Database:**  
![MySQL](https://img.shields.io/badge/MySQL-8.0.33-blue?logo=mysql)  

**Security:**  
![JWT](https://img.shields.io/badge/JWT-0.12.6-black?logo=jsonwebtokens)  

**Payments:**  
![Stripe](https://img.shields.io/badge/Stripe-API-v28.3.0-blueviolet?logo=stripe)  

**Other:**  
![Liquibase](https://img.shields.io/badge/Liquibase-4.29.2-lightgrey)  
![Docker](https://img.shields.io/badge/Docker-✓-blue?logo=docker)  

## 🚀 Quick Start

### Prerequisites
- Java 21
- MySQL 8.0+
- Maven 3.10+
- Docker (optional)

### Local Setup
1. Clone the repo:
   ```sh
   git clone https://github.com/your-org/car-sharing-app.git
   cd car-sharing-app
