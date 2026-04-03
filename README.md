# 📦 StockPro – Inventory Management Platform

![Java](https://img.shields.io/badge/Java-17+-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![Security](https://img.shields.io/badge/Auth-JWT-blue)
![Database](https://img.shields.io/badge/Database-MySQL%208-blue)
![Build](https://img.shields.io/badge/Build-Maven-red)
![Status](https://img.shields.io/badge/Status-Active-success)

---

## 🚀 Overview

**StockPro** is a scalable **inventory management system** for mid-size businesses operating across multiple warehouses.

It provides:
- 📊 Real-time stock visibility  
- 🧾 Structured procurement workflows  
- 🔍 Full inventory audit trail  
- 🚨 Intelligent alerts & automation  

---

## 🧩 Architecture

StockPro is designed as a **microservices-ready system** (currently implemented as a backend monolith).

### Services (Planned / Evolving)
- Authentication & User Management  
- Product Catalogue  
- Warehouse & Inventory  
- Purchase Orders  
- Supplier Management  
- Audit & Tracking  
- Alerts & Notifications  
- Analytics & Reporting  

---

## 👥 User Roles

| Role | Responsibilities |
|------|----------------|
| 🏭 Warehouse Staff | Stock operations, transfers, audits |
| 📊 Inventory Manager | Inventory health, reporting |
| 🧾 Purchase Officer | Procurement & suppliers |
| ⚙️ Admin | User management & system config |

---

## ⚙️ Features

### 🔐 Authentication & Security
- JWT-based authentication  
- Stateless session management  
- Role-based access control (RBAC)  
- BCrypt password hashing  

---

### 📦 Inventory Management
- Multi-warehouse stock tracking  
- Real-time stock updates  
- Inter-warehouse transfers  

---

### 🧾 Procurement
- Purchase order lifecycle  
- Supplier integration  
- Goods receipt tracking  

---

### 📊 Audit Trail
- Stock movement tracking  
- User activity logs  
- PO status tracking  

---

### 🚨 Alerts
- Low stock  
- Overstock  
- Pending approvals  
- Overdue receipts  

---

## 🛠️ Tech Stack

| Layer | Technology |
|------|-----------|
| Backend | Spring Boot 3 |
| Security | Spring Security + JWT |
| ORM | Spring Data JPA |
| DB | MySQL 8 |
| Migration | Flyway |
| Build | Maven |

---

## 📁 Project Structure

```

stockpro-backend/
├── src/
│ ├── main/
│ │ ├── java/
│ │ │ └── com/
│ │ │ └── stockpro/
│ │ │ ├── auth/
│ │ │ │ ├── domain/
│ │ │ │ ├── dto/
│ │ │ │ ├── repository/
│ │ │ │ ├── service/
│ │ │ │ └── web/
│ │ │ ├── config/
│ │ │ └── StockproBackendApplication.java
│ │ └── resources/
│ │ ├── application.yml
│ │ └── db/
│ │ └── migration/
│ │ ├── V1__initial_schema.sql
│ │ └── V2__assignments_and_capacity_backfill.sql
│ │
│ └── test/
│ └── java/
│ └── com/
│ └── stockpro/
│ └── StockproBackendApplicationTests.java
│
├── target/
├── .gitattributes
├── .gitignore
├── HELP.md
├── mvnw
├── mvnw.cmd
└── pom.xml

```
---
## ⚙️ Configuration

Update the following file:


src/main/resources/application.yml


### Example

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/stockpro
    username: root
    password: your_password

stockpro:
  jwt:
    secret: your-secret-key
▶️ Run the Project
1️⃣ Start MySQL
2️⃣ Run Backend
mvn clean spring-boot:run
3️⃣ Access API
http://localhost:8080
🧪 API Testing
🔑 Login
POST /api/v1/auth/login
{
  "email": "admin@stockpro.local",
  "password": "Admin@123"
}
🔐 Authorization Header
Authorization: Bearer <token>
👤 Get Current User
GET /api/v1/auth/me
🗄️ Database (Flyway)
Version-controlled migrations
Located in:
db/migration/
Example Files
V1__initial_schema.sql
V2__assignments_and_capacity_backfill.sql

⚠️ Important:
Never modify existing migrations. Always create a new version (V3__...).

✅ Current Status
✔ JWT Authentication implemented
✔ Secure APIs
✔ Flyway DB migrations
✔ Role-based structure
🔜 Roadmap
Phase 3
Global exception handling
Swagger security improvements
HTTP status standardisation
Future Enhancements
Inventory module
Purchase order workflows
Notifications system
Analytics dashboard
Microservices architecture
🤝 Contributing
Fork the repository
Create a feature branch
Commit your changes
Open a Pull Request

