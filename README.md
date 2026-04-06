# 🧾 StockPro - Supplier Service (UC4)

This module is part of the **StockPro Inventory Management System** and manages supplier/vendor-related operations including creation, updates, filtering, rating, and lifecycle management.

---

## 🚀 Features

- Create and manage suppliers
- Update supplier details
- Fetch supplier information
- Filter suppliers by name, city, and country
- Add/update supplier rating
- Soft delete (deactivate supplier)
- Track supplier performance metrics

---

## 🧱 API Endpoints

### 📍 Base URL

```
http://localhost:8080/api/v1/suppliers
```


---

### 🔹 1. Create Supplier

**POST** `/api/v1/suppliers`

#### Request Body:

```json
{
  "name": "Tech Supplies Pvt Ltd",
  "contactPerson": "Amit Verma",
  "email": "amit@techsupplies.com",
  "phone": "9876543210",
  "address": "Sector 62, Noida",
  "city": "Noida",
  "country": "India",
  "taxId": "GSTIN12345",
  "paymentTerms": "Net 30",
  "leadTimeDays": 5,
  "rating": 4
}
```
### 2. Get All Suppliers
```
GET /api/v1/suppliers

Optional Query Parameters:
?q=Tech
?city=Noida
?country=India
```
### 3. Get Supplier by ID
```
GET /api/v1/suppliers/{id}
```

### 4. Update Supplier
```
PUT /api/v1/suppliers/{id}
```
### 5. Deactivate Supplier
```
POST /api/v1/suppliers/{id}/deactivate
⚠️ Performs soft delete by setting active = false
```

### 6. Update Supplier Rating
```
POST /api/v1/suppliers/{id}/rating?score=4.5
```
### Authentication

All APIs require JWT authentication.
```
Header:
Authorization: Bearer <JWT_TOKEN>
```
### 🧠 Key Concepts

- Suppliers represent external vendors providing products
- Supports filtering and search for easy lookup
- Rating system helps evaluate supplier performance
- Uses soft delete instead of permanent deletion

Supplier management is a critical component of inventory systems, enabling efficient procurement and vendor evaluation.

### ⚠️Known Limitations
Rating logic uses simple averaging (can be improved with rating count)
No supplier-product mapping yet
No purchase order integration (planned)
No historical rating tracking

### 🛠️ Tech Stack
Java
Spring Boot
Spring Data JPA
Hibernate
MySQL / PostgreSQL
Postman (API Testing)

### ▶️ How to Run
Clone the repository:
```
git clone https://github.com/ravi-gautam17/StockPro-Backend.git
```
Navigate to project:
```
cd stockpro-backend
```
Configure database in application.properties<br>

Run the application:
```
mvn spring-boot:run
```
Access APIs at:
```
http://localhost:8080
```
### 👨‍💻 Author

Ravi Gautam

### ⭐ Future Enhancements
Supplier-product mapping
Purchase order management
Advanced rating system (with rating count & reviews)
Supplier analytics dashboard
Performance-based supplier ranking



