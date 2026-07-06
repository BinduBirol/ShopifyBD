# Commerce Platform

A modular e-commerce platform built with **Spring Boot** and **Microservices** architecture.

## Tech Stack

* Java 21
* Spring Boot
* Spring Security
* Spring Data JPA
* MySQL
* JWT Authentication
* Maven
* Hibernate
* REST API
* Internationalization (i18n)
* JUnit & Mockito (in progress)

---

## Project Structure

```text
commerce-parent
│
├── commerce-common
│   ├── Common API response
│   ├── Global exception handling
│   ├── i18n
│   ├── Utilities
│   ├── Security helpers
│   └── Shared annotations
│
├── commerce-domain
│   ├── Shared entities
│   ├── BaseEntity
│   ├── Common enums
│   └── Shared domain models
│
├── commerce-auth-service
│   ├── Authentication
│   ├── User registration
│   ├── Login
│   ├── JWT generation
│   ├── Role management
│   └── User management
│
├── commerce-product-service
│
├── commerce-order-service
│
├── commerce-payment-service
│
└── commerce-gateway
```

---

## Current Features

* User Registration
* User Login
* JWT Authentication
* BCrypt Password Encryption
* Role-based Authentication
* Global Exception Handling
* Standardized API Response
* Request Validation
* Localization (English & Bangla)
* Rate Limiting
* Shared Domain Module
* Shared Common Module

---

## API Response Format

Successful Response

```json
{
  "success": true,
  "data": {},
  "timestamp": "...",
  "path": "...",
  "version": "v1"
}
```

Error Response

```json
{
  "success": false,
  "error": {
    "code": "...",
    "message": "...",
    "status": 400,
    "fieldErrors": {}
  },
  "timestamp": "...",
  "path": "...",
  "version": "v1"
}
```

---

## Internationalization (i18n)

The project supports multiple languages using the `Accept-Language` request header.

Example:

```
Accept-Language: en
```

or

```
Accept-Language: bn
```

---

## Planned Services

* Product Service
* Inventory Service
* Cart Service
* Order Service
* Payment Service
* Notification Service
* API Gateway
* Service Discovery
* Config Server

---

## Testing

Planned testing strategy includes:

* Unit Tests
* Controller Tests
* Repository Tests
* Integration Tests
* Security Tests
* Testcontainers
* Performance Testing

---

## Goals

This project is being developed as a learning-oriented, production-style microservices application to practice:

* Spring Boot
* Microservices Architecture
* Security
* JWT
* REST API Design
* Clean Architecture
* Testing
* CI/CD
* Docker
* Kubernetes (future)
* Cloud Deployment (future)

---

## Status

🚧 Active development.
