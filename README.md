# Ecommerce Multivendor System

A RESTful backend API for managing a multi-vendor e-commerce platform, built with Spring Boot following a CQRS-lite and Hexagonal Architecture pattern.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 4.1.0 |
| Database | MySQL |
| Cache | Redis |
| Auth | JWT (JSON Web Tokens) |
| Security | Spring Security |
| ORM | Spring Data JPA |
| Build | Maven |
| Testing | JUnit 5, Testcontainers |

---

## Architecture

The project follows a Command Query Responsibility Segregation (CQRS) approach inside a Hexagonal Architecture structure:

```
presentation/       <- HTTP controllers (REST endpoints)
application/        <- Command & Query handlers (use cases)
domain/             <- Entities, Repository interfaces, Domain models
infrastructure/     <- JPA implementations, Security, Mappers, Adapters
common/             <- Shared constants, exceptions, response wrappers, utils
```

The system separates responsibilities by roles (User, Seller, Admin, Public), ensuring that each module handles its own specific set of commands and queries.

---

## Module Overview

### Controllers by role

| Role | Base Path | Responsibilities |
|---|---|---|
| **User** | `/api/users` | View profile, manage account |
| **User** | `/api/cart` | Manage shopping cart, add/remove items |
| **User** | `/api/orders` | Place orders, view order history |
| **User** | `/api/addresses` | Manage shipping addresses |
| **User** | `/api/reviews` | Create product reviews |
| **User** | `/api/wishlist` | Manage wishlist |
| **Seller** | `/api/seller/profile` | Manage seller business details |
| **Seller** | `/api/seller/products` | Create, update, and manage products and SKUs |
| **Seller** | `/api/seller/orders` | Manage incoming orders from users |
| **Seller** | `/api/seller/report` | View seller revenue and statistics |
| **Admin** | `/api/admin/categories` | Manage system-wide categories and commission rates |
| **Admin** | `/api/admin/sellers` | Approve, block, or manage sellers |
| **Public** | `/api/public/categories` | Browse categories |
| **Public** | `/api/public/products` | Browse and search products |
| **Public** | `/api/public/reviews` | View product reviews |
| **Auth** | `/api/auth` | Register, login, token generation |

### Domain Entities

`User` - `Role` - `Address` - `Seller` - `Category` - `Product` - `ProductSku` - `ProductImage` - `Cart` - `CartItem` - `Order` - `OrderItem` - `PaymentOrder` - `Transaction` - `Review` - `Wishlist` - `Coupon` - `AdminReport` - `SellerReport` - `HomeCategory`

---

## Getting Started

### Prerequisites

- Java 17+
- Docker (for local infrastructure testing)
- Maven

### 1. Start local infrastructure

If you are using Testcontainers, the database will automatically spin up during integration testing. For normal development, ensure you have a local MySQL and Redis server running.

### 2. Configure environment

Configure the application in `src/main/resources/application.yml` or through environment variables:

```bash
# Database
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/ecommerce_db
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root

# Redis
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379

# JWT
SECURITY_JWT_SECRET=your-secure-jwt-secret-key
```

### 3. Run the application

```bash
./mvnw spring-boot:run
```

---

## Key Business Rules

- **Multi-vendor**: Users can buy products from multiple sellers in a single cart. The system tracks the origin of each item.
- **Commission Rates**: Categories have a `commissionRate`. When an order is checked out, the system calculates a `platformFee` that the seller owes to the platform.
- **Product SKUs**: Products are managed with specific SKUs (Stock Keeping Units) representing variants (size, color, quantity).
- **Cart Lifecycle**: Cart items are associated with specific SKUs. Prices are locked in at the time of checkout.

---

## Project Structure

```
Ecommerce-Multivendor/
├── src/main/java/com/abs/app/
│   ├── application/                  # CQRS handlers (commands & queries)
│   │   ├── admin/
│   │   ├── auth/
│   │   ├── seller/
│   │   └── user/
│   ├── common/                       # Shared utilities and configurations
│   ├── domain/                       # Core domain models and interfaces
│   │   ├── entity/
│   │   └── repository/               # Port interfaces
│   ├── infrastructure/               # Implementation adapters
│   │   ├── persistence/              # JPA repos & adapters
│   │   └── security/                 # JWT configuration
│   └── presentation/                 # Presentation layer
│       └── controller/               # REST controllers
├── src/test/                         # Integration and Unit tests
│   └── java/com/abs/app/integration/ # Testcontainers setup
├── pom.xml                           # Maven dependencies
└── README.md                         # Project documentation
```

---

## Testing

The project utilizes `Testcontainers` for integration testing to ensure true data integrity with a real MySQL instance.

```bash
./mvnw test
```

Integration tests are located in `src/test/java/com/abs/app/integration/`.
