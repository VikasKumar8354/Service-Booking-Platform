# Service Booking Platform - Backend API

A complete, production-ready REST API backend for a service booking platform built with Spring Boot.

## 📋 Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Quick Start](#quick-start)
- [Database Configuration](#database-configuration)
- [API Documentation](#api-documentation)
- [Authentication & Security](#authentication--security)
- [Core Modules](#core-modules)
- [Hardcoded Values](#hardcoded-values)
- [Testing with Postman](#testing-with-postman)
- [Expected Responses](#expected-responses)

## ✨ Features

### Complete Backend Modules

1. **Authentication & User Management** - Registration, Login, OTP, Password Recovery
2. **Customer Profile & Address Management** - Profile management, multiple addresses
3. **Service Catalog Management** - Categories and service items
4. **Booking Management** - Complete booking lifecycle with status tracking
5. **Payment Service** - Cash and online payment support
6. **Ratings & Reviews** - Provider ratings and feedback
7. **Notification Service** - Real-time notifications
8. **Provider Management** - Registration, approval, job management
9. **Admin Dashboard** - Analytics, reports, user management
10. **Dynamic Search & Filter** - Advanced booking filters
11. **Reporting & Analytics** - Monthly reports, revenue tracking

### Key Features

- ✅ JWT-based authentication
- ✅ Role-based access control (CUSTOMER, PROVIDER, ADMIN)
- ✅ Pagination support on all list endpoints
- ✅ Dynamic filtering and sorting
- ✅ Global exception handling
- ✅ Input validation
- ✅ Swagger/OpenAPI documentation
- ✅ Structured JSON responses
- ✅ Hardcoded OTP for testing

## 🛠 Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Security**: Spring Security + JWT
- **Database**: H2 (development) / MySQL (production)
- **ORM**: JPA/Hibernate
- **Documentation**: SpringDoc OpenAPI (Swagger)
- **Build Tool**: Maven
- **Additional**: Lombok, Validation

## 📁 Project Structure

```
service-booking-platform/
├── src/
│   └── main/
│       ├── java/com/servicebooking/
│       │   ├── ServiceBookingPlatformApplication.java
│       │   ├── entity/          # Database entities
│       │   ├── dto/
│       │   │   ├── request/     # Request DTOs
│       │   │   └── response/    # Response DTOs
│       │   ├── repository/      # JPA repositories
│       │   ├── service/         # Business logic
│       │   ├── controller/      # REST controllers
│       │   ├── config/          # Configuration classes
│       │   ├── security/        # JWT & security
│       │   ├── exception/       # Custom exceptions
│       │   ├── util/            # Utility classes
│       │   └── enums/           # Enumerations
│       └── resources/
│           └── application.properties
├── pom.xml
├── README.md
└── ServiceBookingPlatform.postman_collection.json
```

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- (Optional) MySQL 8.0+

### Installation & Running

1. **Extract the project**
   ```bash
   unzip service-booking-platform.zip
   cd service-booking-platform
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

   Or run the JAR:
   ```bash
   java -jar target/service-booking-platform-1.0.0.jar
   ```

4. **Access the application**
   - API Base URL: `http://localhost:8080/api`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - H2 Console: `http://localhost:8080/h2-console`

## 🗄 Database Configuration

### H2 Database (Default - Development)

The application runs with H2 in-memory database by default. Configuration is in `application.properties`:

```properties
spring.datasource.url=jdbc:h2:mem:servicebookingdb
spring.datasource.username=sa
spring.datasource.password=
```

**H2 Console Access:**
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:servicebookingdb`
- Username: `sa`
- Password: (leave empty)

### MySQL Database (Production)

To use MySQL, update `application.properties`:

```properties
# Comment out H2 configuration
# spring.datasource.url=jdbc:h2:mem:servicebookingdb

# Uncomment MySQL configuration
spring.datasource.url=jdbc:mysql://localhost:3306/servicebookingdb?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

## 📚 API Documentation

### Swagger/OpenAPI

Access interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

### API Endpoints Overview

#### Authentication (`/api/auth`)
- `POST /register` - Register new user
- `POST /login` - User login
- `POST /send-otp` - Send OTP
- `POST /verify-otp` - Verify OTP
- `POST /recover-password` - Recover password
- `POST /logout` - Logout

#### Users (`/api/users`)
- `GET /profile` - Get current user profile
- `PUT /profile` - Update profile
- `DELETE /account` - Delete account
- `GET /` - Get all users (Admin)

#### Service Catalog (`/api/services`)
- `POST /categories` - Add category (Admin)
- `GET /categories` - Get all categories
- `PUT /categories/{id}` - Update category (Admin)
- `DELETE /categories/{id}` - Delete category (Admin)
- `POST /items` - Add service item (Admin)
- `GET /search` - Search services
- `PUT /items/{id}` - Update service (Admin)
- `DELETE /items/{id}` - Delete service (Admin)

#### Bookings (`/api/bookings`)
- `POST /` - Create booking
- `PUT /{id}/assign-provider` - Assign provider (Admin)
- `PUT /{id}/status` - Update status
- `GET /customer` - Get customer bookings
- `GET /provider` - Get provider bookings
- `POST /filter` - Filter bookings (Dynamic)
- `DELETE /{id}` - Cancel booking

#### Addresses (`/api/customer/addresses`)
- `POST /` - Add address
- `GET /` - Get all addresses
- `PUT /{id}` - Update address
- `DELETE /{id}` - Delete address

#### Payments (`/api/payments`)
- `POST /` - Record payment
- `PUT /{id}/complete` - Mark complete
- `GET /history` - Payment history

#### Ratings (`/api/ratings`)
- `POST /` - Submit rating
- `GET /provider/{id}` - Get provider ratings

#### Notifications (`/api/notifications`)
- `GET /` - Get notifications
- `PUT /{id}/read` - Mark as read

#### Providers (`/api/provider`)
- `PUT /profile` - Update profile
- `PUT /status` - Update status (ONLINE/OFFLINE)
- `PUT /{id}/approve` - Approve provider (Admin)
- `PUT /{id}/reject` - Reject provider (Admin)
- `GET /pending` - Get pending providers (Admin)

#### Admin (`/api/admin`)
- `GET /dashboard` - Dashboard stats
- `GET /reports/monthly` - Monthly report

## 🔐 Authentication & Security

### JWT Authentication

1. **Register or Login** to get JWT token
2. **Include token** in subsequent requests:
   ```
   Authorization: Bearer <your-jwt-token>
   ```

### User Roles

- **CUSTOMER**: Can create bookings, manage addresses, submit ratings
- **PROVIDER**: Can view assigned bookings, update status, manage profile
- **ADMIN**: Full access to all endpoints, manage users, approve providers

### Protected Endpoints

All endpoints except `/api/auth/**` require authentication.

## 🧩 Core Modules

### 1. Authentication & User Service

Handles user registration, login, OTP verification, and password recovery.

**Key Entities**: User

### 2. Customer Profile & Address Service

Manages customer profiles and multiple delivery addresses.

**Key Entities**: CustomerProfile, Address

### 3. Service Catalog

Manages service categories and individual service items.

**Key Entities**: ServiceCategory, ServiceItem

### 4. Booking Management

Complete booking lifecycle from creation to completion.

**Status Flow**: PENDING → ACCEPTED → COMPLETED / CANCELLED

**Key Entities**: Booking

### 5. Payment Service

Records and tracks payments for bookings.

**Key Entities**: Payment

### 6. Ratings & Reviews

Customer feedback and provider ratings.

**Key Entities**: Rating

### 7. Notification Service

Automated notifications for booking updates.

**Key Entities**: Notification

### 8. Provider Management

Provider registration, approval, and job management.

**Key Entities**: ProviderProfile

### 9. Admin Dashboard

Statistics, analytics, and system management.

### 10. Dynamic Filter Module

Advanced filtering with multiple criteria:
- Customer name
- Provider name
- Service category
- Status
- Date range
- Sorting (ASC/DESC)

## 🔑 Hardcoded Values

For testing and development:

### OTP Value
```
Hardcoded OTP: 123456
```

Use this OTP when calling `/api/auth/verify-otp`

### Recovery PIN
```
Recovery PIN: 9999
```

Use this PIN when calling `/api/auth/recover-password`

### Configuration
These values are in `application.properties`:
```properties
otp.hardcoded.value=123456
otp.recovery.pin=9999
```

## 📮 Testing with Postman

### Import Collection

1. Open Postman
2. Click **Import**
3. Select `ServiceBookingPlatform.postman_collection.json`
4. Collection will be imported with all endpoints

### Setup Environment

1. Create new environment in Postman
2. Add variables:
   - `base_url`: `http://localhost:8080/api`
   - `jwt_token`: (will be auto-populated on login)

### Testing Workflow

1. **Register a Customer**
   - Use "Register Customer" request
   - Sample mobile: `9876543210`

2. **Login**
   - Use "Login" request
   - JWT token will be automatically saved to environment

3. **Create Service Category** (Login as Admin first)
   - Register admin with role: `ADMIN`
   - Login as admin
   - Create category

4. **Create Booking** (Login as Customer)
   - First create service items
   - Then create booking with service ID

5. **Test Other Endpoints**
   - All requests use `{{jwt_token}}` automatically
   - Token is sent in Authorization header

## 📊 Expected Responses

### Success Response Format
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { /* response data */ }
}
```

### Error Response Format
```json
{
  "success": false,
  "message": "Error description",
  "data": null
}
```

### Validation Error Format
```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "fieldName": "error message"
  }
}
```

### Paginated Response Format
```json
{
  "success": true,
  "message": "Data fetched successfully",
  "data": {
    "content": [ /* array of items */ ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 100,
    "totalPages": 10,
    "last": false
  }
}
```

## 🔍 Sample Request/Response Examples

### 1. Register User

**Request:**
```http
POST /api/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "mobileNumber": "9876543210",
  "password": "password123",
  "role": "CUSTOMER"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "userId": 1,
    "name": "John Doe",
    "mobileNumber": "9876543210",
    "role": "CUSTOMER"
  }
}
```

### 2. Create Booking

**Request:**
```http
POST /api/bookings
Authorization: Bearer <token>
Content-Type: application/json

{
  "serviceId": 1,
  "bookingDateTime": "2026-03-15T10:00:00",
  "location": "123 Main St, City"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Booking created successfully",
  "data": {
    "id": 1,
    "customer": { /* customer details */ },
    "service": { /* service details */ },
    "bookingDateTime": "2026-03-15T10:00:00",
    "location": "123 Main St, City",
    "status": "PENDING",
    "amount": 500.00,
    "createdAt": "2026-02-11T14:30:00"
  }
}
```

### 3. Filter Bookings

**Request:**
```http
POST /api/bookings/filter?page=0&size=10
Authorization: Bearer <token>
Content-Type: application/json

{
  "status": "COMPLETED",
  "fromDate": "2026-02-01",
  "toDate": "2026-02-28",
  "sortBy": "createdAt",
  "sortOrder": "DESC"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Bookings fetched successfully",
  "data": {
    "content": [ /* array of bookings */ ],
    "pageNumber": 0,
    "pageSize": 10,
    "totalElements": 25,
    "totalPages": 3,
    "last": false
  }
}
```

## 🎯 Features Highlight

### Pagination
All list endpoints support pagination:
```
?page=0&size=10
```

### Filtering
Dynamic filtering on bookings:
- By customer name, provider name
- By service category
- By status (PENDING, ACCEPTED, COMPLETED, CANCELLED)
- By date range
- With sorting options

### Sorting
Supports ascending and descending order:
```json
{
  "sortBy": "bookingDate",
  "sortOrder": "ASC"
}
```

### Role-Based Access
- Customers can only see their own bookings
- Providers can only see assigned bookings
- Admins can see all bookings and manage the system

## 🐛 Troubleshooting

### Port Already in Use
If port 8080 is busy, change it in `application.properties`:
```properties
server.port=8081
```

### Database Connection Issues
For MySQL, ensure:
- MySQL server is running
- Database exists or `createDatabaseIfNotExist=true` is set
- Credentials are correct

### JWT Token Issues
- Ensure token is sent in Authorization header
- Format: `Bearer <token>`
- Token expires after 24 hours (configured in properties)

## 📝 Development Notes

### Adding New Endpoints
1. Create entity in `entity/` package
2. Create repository in `repository/`
3. Create service in `service/`
4. Create controller in `controller/`
5. Add request/response DTOs in `dto/`

### Custom Exceptions
Use provided exception classes:
- `ResourceNotFoundException` - 404 errors
- `BadRequestException` - 400 errors
- `UnauthorizedException` - 401 errors

### Validation
Use Jakarta validation annotations:
```java
@NotBlank(message = "Field is required")
@Email(message = "Invalid email")
@Min(value = 1, message = "Must be at least 1")
```

## 🔐 Security Best Practices

- All passwords are encrypted using BCrypt
- JWT secret should be changed in production
- HTTPS should be enabled in production
- Database credentials should use environment variables

## 📞 Support

For issues or questions about this project, please check:
1. Swagger documentation at `/swagger-ui.html`
2. This README file
3. Application logs for error details

## 📄 License

This is a demonstration project for educational purposes.

---

**Project Version**: 1.0.0  
**Last Updated**: February 2026  
**Author**: Service Booking Platform Team
