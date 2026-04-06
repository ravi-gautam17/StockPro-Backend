# 📦 StockPro - Warehouse Service (UC2)

This module is part of the **StockPro Inventory Management System** and handles all warehouse-related operations including creation, management, and stock visibility.

---

## 🚀 Features

- Create and manage warehouses
- Update warehouse details
- Fetch warehouse information
- View stock inside a warehouse
- Assign warehouse manager
- Track capacity and usage
- Soft activation/deactivation support

---

## 🧱 API Endpoints

### 📍 Base URL
http://localhost:8080/api/v1/warehouses


---

### 🔹 1. Create Warehouse

**POST** `/api/v1/warehouses`

#### Request Body:
```json
{
  "name": "Main Warehouse",
  "location": "Bhopal",
  "address": "MP Nagar",
  "capacity": 1000,
  "usedCapacity": 200,
  "phone": "9876543210"
}
Response:
{
  "id": 1,
  "name": "Main Warehouse",
  "location": "Bhopal",
  "address": "MP Nagar",
  "capacity": 1000,
  "usedCapacity": 200,
  "phone": "9876543210",
  "active": true
}
```

### 2. Get All Warehouses
```
GET /api/v1/warehouses
```

### 3. Get Warehouse by ID
```
GET /api/v1/warehouses/{id}
```

### 4. Update Warehouse
```
PUT /api/v1/warehouses/{id}
```
### 5. Get Warehouse Stock
```
GET /api/v1/warehouses/{id}/stock
```
⚠️ Note: Stock will be empty if no inventory is assigned to the warehouse.


### Tech Stack
Java
Spring Boot
Spring Data JPA
Hibernate
MySQL / PostgreSQL
Postman (API Testing)


### How to Run

Clone the repository:
git clone https://github.com/ravi-gautam17/StockPro-Backend.git <br>
Navigate to project:
cd stockpro-backend  <br>
Configure database in application.properties <br>
Run the application:
mvn spring-boot:run <br>
Access APIs at:
http://localhost:8080


### Author
Ravi Gautam

### Future Enhancements
Add stock/inventory management APIs
Warehouse transfer system
Capacity alerts & analytics
Dashboard integration
