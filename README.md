# 🛍️ StockPro - Product Service (UC3)

This module is part of the **StockPro Inventory Management System** and is responsible for managing product-related operations including creation, updates, search, and lifecycle management.

---

## 🚀 Features

- Create and manage products
- Update product details
- Fetch product information
- Search products by keyword
- Get product using barcode
- Soft delete (deactivate product)
- Track pricing and inventory thresholds

---

## 🧱 API Endpoints

### 📍 Base URL

```
http://localhost:8080/api/v1/products
```
---

### 🔹 1. Create Product

**POST** `/api/v1/products`

#### Request Body:
```json
{
  "sku": "PROD-001",
  "name": "Laptop",
  "description": "Dell i5 Laptop",
  "category": "Electronics",
  "brand": "Dell",
  "unitOfMeasure": "Piece",
  "costPrice": 40000,
  "sellingPrice": 50000,
  "reorderLevel": 5,
  "maxStockLevel": 50,
  "leadTimeDays": 7,
  "imageUrl": "https://example.com/laptop.jpg",
  "barcode": "1234567890"
}
```
### 2. Get All Products

```
GET /api/v1/products
```

Optional Query:
```
?search=Laptop
```
### 3. Get Product by ID
```
GET /api/v1/products/{id}
```

### 4. Update Product
```
PUT /api/v1/products/{id}
```

### 5. Deactivate Product
```
POST /api/v1/products/{id}/deactivate
⚠️ This performs a soft delete by setting active = false
```
### 6. Get Product by Barcode
```
GET /api/v1/products/barcode/{barcode}
```

### Authentication
```
All APIs require JWT authentication.
Header:
Authorization: Bearer <JWT_TOKEN>
```
### Known Limitations
No direct stock management (handled by inventory/warehouse module)<br>
No supplier-product mapping yet<br>
No product variants (size, colour, etc.)<br>

### Tech Stack
Java
Spring Boot
Spring Data JPA
Hibernate
MySQL / PostgreSQL
Postman (API Testing)

### How to Run
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
### Author

Ravi Gautam


## ⭐ Future Enhancements
- Product variants (size, colour, etc.)
- Supplier-product mapping
- Bulk import/export APIs
- Image upload support
- Product analytics & reporting
