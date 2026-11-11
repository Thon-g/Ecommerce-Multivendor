# Ecommerce Multivendor System - Backend API

A production-ready, highly modular RESTful backend platform for a multi-vendor e-commerce marketplace built with **Spring Boot 3.x**, following **Clean Architecture** and **CQRS (Command Query Responsibility Segregation)** principles.

---

## 🛠️ Tech Stack & Dependencies

| Layer | Technology | Details |
|---|---|---|
| **Language** | Java 17 | LTS version |
| **Framework** | Spring Boot 3.2.x | Core backend framework |
| **Security & Auth** | Spring Security 6, JJWT | Dual auth: HttpOnly Cookie (`jwt`) & Bearer Token |
| **Database** | MySQL 8.x | Relational database (InnoDB) |
| **Caching & Session** | Redis 7.x | Token blacklist, caching, atomic operations |
| **ORM / Persistence** | Spring Data JPA / Hibernate | Repository & Adapter pattern |
| **Build & Dependency** | Apache Maven | Project build tool |
| **Testing** | JUnit 5, Mockito, AssertJ | 175+ Unit tests (100% pass rate) |
| **Integration Testing** | Testcontainers | Real MySQL Docker container testing |
| **API Testing** | Postman | Automated Postman collection runner with tests |

---

## 🏛️ System Architecture

The application adopts **Clean Architecture** combined with **CQRS-lite** to decouple write (Command) and read (Query) operations, maintaining high cohesion and strict dependency inversion:

```
src/main/java/com/abs/app/
├── presentation/controller/   <- HTTP REST Controllers (Thin presentation layer)
│   ├── admin/                 <- Admin endpoints (reports, users, categories, banners, deals, sellers)
│   ├── auth/                  <- Authentication & password management
│   ├── publicapi/             <- Public endpoints (home data, products, categories, reviews)
│   ├── seller/                <- Seller portal (profile, products, orders, reports)
│   └── user/                  <- Customer portal (cart, orders, addresses, info, reviews, wishlist)
├── application/               <- Use Case layer (CQRS commands, queries, handlers, DTOs)
├── domain/                    <- Core Business domain (Entities, Enums, Domain Services, Repository Ports)
├── infrastructure/            <- Adapters (JPA implementations, Mappers, Security, File storage)
└── common/                    <- Cross-cutting concerns (Constants, Exceptions, Response wrappers)
```

### Architectural Principles Enforced:
1. **CQRS (Command Query Responsibility Segregation):** Dedicated Command and Query objects with separate Handlers.
2. **Zero Magic Strings:** All system messages, error texts, and validation messages reside in centralized `Constant` classes.
3. **Clean DTOs:** Every DTO represents a distinct business slice (e.g. `SellerOwnerDto`, `SellerBusinessDto`, `SellerBankDto`, `SellerPickupAddressDto`). No manual constructors when Lombok annotations are used.
4. **Platform Safety:** Strict domain service barriers preventing inactive sellers from participating in marketplace transactions.

---

## 🌐 API Overview & Endpoints

All endpoints are served under the context path: **`http://localhost:8080/api`**.

For detailed request payloads, validation annotations, and response schemas, refer to [`docs/api_endpoints.md`](docs/api_endpoints.md).

### 1. Authentication (`/api/auth`)
| Method | Endpoint | Description | Access |
|---|---|---|---|
| `POST` | `/api/auth/register` | Register customer account | Public |
| `POST` | `/api/auth/login` | Login (issues HttpOnly JWT cookie & Bearer token) | Public |
| `POST` | `/api/auth/send-otp` | Request email verification OTP | Public |
| `POST` | `/api/auth/verify-otp` | Verify OTP for account activation | Public |
| `POST` | `/api/auth/change-password` | Change account password | Authenticated |
| `POST` | `/api/auth/forgot-password` | Send password reset token to email | Public |
| `POST` | `/api/auth/reset-password` | Set new password with reset token | Public |
| `POST` | `/api/auth/refresh-token` | Rotate and refresh access token | Public (Cookie) |
| `POST` | `/api/auth/logout` | Logout current session | Authenticated |
| `POST` | `/api/auth/logout-all` | Invalidate all active sessions via Redis | Authenticated |

### 2. Admin Portal (`/api/admin/...`)
*Requires `hasRole('ADMIN')`*

| Group | Method | Endpoint | Description |
|---|---|---|---|
| **Reports** | `GET` | `/api/admin/reports/dashboard` | Platform financial summary (earnings, sales, refunds, tax, orders, sellers, users) |
| | `GET` | `/api/admin/reports/transactions` | System-wide transaction audit log (paged) |
| **Users** | `GET` | `/api/admin/users` | List users with keyword search & status filter (`ACTIVE`, `BANNED`) |
| | `GET` | `/api/admin/users/{userId}` | Detailed user account profile with roles and addresses |
| | `PATCH` | `/api/admin/users/{userId}/status` | Ban or reactivate user account (cascades to seller status) |
| **Categories** | `GET` | `/api/admin/categories` | Paged category list with level filter |
| | `GET` | `/api/admin/categories/tree` | Hierarchical category tree for UI menus |
| | `GET` | `/api/admin/categories/{id}` | Category detail with parent and subcategories |
| | `POST` | `/api/admin/categories` | Create category with marketplace `commissionRate` (%) |
| | `PUT` | `/api/admin/categories/{id}` | Update category name and commission rate |
| | `DELETE` | `/api/admin/categories/{id}` | Safe deletion (blocked if category contains products/subcategories) |
| **Banners** | `GET` | `/api/admin/home-categories` | List banners filtered by section (`GRID`, `SHOP_BY_CATEGORIES`, etc.) |
| | `POST` | `/api/admin/home-categories` | Create banner with image upload (`multipart/form-data`) |
| | `PUT` | `/api/admin/home-categories/{id}` | Update banner info and optional new image |
| | `DELETE` | `/api/admin/home-categories/{id}` | Delete homepage banner section |
| **Deals** | `GET` | `/api/admin/deals` | List all homepage promotional deals |
| | `POST` | `/api/admin/deals` | Create deal with discount percentage on a banner category |
| | `DELETE` | `/api/admin/deals/{id}` | Delete promotional deal |
| **Sellers & KYC** | `GET` | `/api/admin/sellers` | List sellers with multi-field search and status filter |
| | `GET` | `/api/admin/sellers/{sellerId}` | **Full KYC Dossier**: Owner info, legal business details, bank details, pickup address, report |
| | `GET` | `/api/admin/sellers/{sellerId}/report` | Standalone seller financial and order metrics |
| | `GET` | `/api/admin/sellers/{sellerId}/products` | Inspect all products belonging to a seller |
| | `PATCH` | `/api/admin/sellers/{sellerId}/status` | Approve KYC (`ACTIVE`), suspend (`SUSPENDED`), reactivate (`ACTIVE`), ban (`BANNED`) |

### 3. Seller Portal (`/api/seller/...`)
| Group | Method | Endpoint | Description | Access |
|---|---|---|---|---|
| **Profile** | `POST` | `/api/seller/register` | Register new seller shop (starts in `PENDING_VERIFICATION`) | `CUSTOMER` |
| | `GET` | `/api/seller/current` | Get authenticated seller profile | `SELLER` |
| | `PUT` | `/api/seller/update` | Full profile update (business, bank, pickup address) | `SELLER` |
| | `PATCH` | `/api/seller/profile` | Update business details only | `SELLER` |
| | `PATCH` | `/api/seller/bank` | Update banking payout details only | `SELLER` |
| | `PATCH` | `/api/seller/address` | Update warehouse pickup address only | `SELLER` |
| **Products** | `GET` | `/api/seller/products` | Paged products list of current seller | `SELLER` |
| | `POST` | `/api/seller/products` | Create product with image uploads & SKUs (`multipart/form-data`) | `SELLER` |
| | `PUT` | `/api/seller/products/{id}` | Update product, variants, and images | `SELLER` |
| | `DELETE` | `/api/seller/products/{id}` | Delete product | `SELLER` |
| **Orders** | `GET` | `/api/seller/orders` | List incoming customer orders assigned to seller | `SELLER` |
| | `PATCH` | `/api/seller/orders/{orderId}/status` | Update fulfillment status (`CONFIRMED`, `SHIPPED`, `DELIVERED`) | `SELLER` |
| | `PATCH` | `/api/seller/orders/{orderId}/cancel` | Cancel order with reason | `SELLER` |
| **Reports** | `GET` | `/api/seller/reports/dashboard` | Seller revenue, net earnings, order count dashboard | `SELLER` |
| | `GET` | `/api/seller/reports/transactions` | Paged seller wallet transaction history | `SELLER` |

### 4. Customer Portal (`/api/users`, `/api/user`, `/api/orders`)
*Requires `hasRole('USER')` or `isAuthenticated()`*

| Group | Method | Endpoint | Description |
|---|---|---|---|
| **Profile** | `GET` | `/api/users/me` | Current user profile information |
| | `PUT` | `/api/users/update-profile` | Update personal details (name, phone, gender) |
| | `PUT` | `/api/users/update-image` | Upload user avatar (`multipart/form-data`) |
| **Address** | `GET` | `/api/users/addresses` | List saved delivery addresses |
| | `POST` | `/api/users/addresses` | Add new delivery address |
| | `PUT` | `/api/users/addresses/{id}` | Update delivery address |
| | `DELETE` | `/api/users/addresses/{id}` | Delete delivery address |
| **Cart** | `GET` | `/api/user/cart` | Get current shopping cart (with discounts & breakdown) |
| | `POST` | `/api/user/cart/items` | Add product SKU to cart |
| | `PUT` | `/api/user/cart/items/{cartItemId}` | Update item quantity |
| | `DELETE` | `/api/user/cart/items/{cartItemId}` | Remove item from cart |
| | `POST` | `/api/user/cart/coupon` | Apply promotional coupon code |
| | `DELETE` | `/api/user/cart/coupon` | Remove applied coupon code |
| **Orders** | `POST` | `/api/orders/checkout` | Multi-vendor checkout (splits orders per seller automatically) |
| | `GET` | `/api/orders/user` | Order purchase history |
| **Wishlist** | `GET` | `/api/user/wishlist` | User's wishlist |
| | `POST` | `/api/user/wishlist/products/{id}` | Add product to wishlist |
| | `DELETE` | `/api/user/wishlist/products/{id}` | Remove product from wishlist |
| **Reviews** | `POST` | `/api/user/reviews` | Submit product review & rating (1-5 stars) |

### 5. Public APIs (`/api/public/...`)
*No authentication required*

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/public/home` | Single-call homepage aggregator (banners, categories, deals) |
| `GET` | `/api/public/products` | Browse & search products with keyword and category filter |
| `GET` | `/api/public/products/{id}` | Product detail with variants, images, and shop info |
| `GET` | `/api/public/categories` | Paged category list |
| `GET` | `/api/public/categories/tree` | Hierarchical category tree for public navigation |
| `GET` | `/api/public/categories/{id}` | Category detail |
| `GET` | `/api/public/reviews/product/{productId}` | Public product reviews and ratings |

---

## ⚙️ Key Business Rules & Workflows

1. **Multi-Vendor Cart & Checkout Partitioning:**
   - Customers can add items from multiple distinct sellers into a single shopping cart.
   - During checkout (`/api/orders/checkout`), the system automatically splits the order into sub-orders grouped by seller, calculating platform fees and commissions independently.
2. **Inactive Seller Validation Barrier:**
   - Products belonging to sellers in `PENDING_VERIFICATION`, `SUSPENDED`, `BANNED`, `DEACTIVATED`, or `CLOSED` status are strictly blocked from being checked out.
   - If an inactive seller's product is in a user's cart, checkout throws a business validation exception with clear localized messaging.
3. **Category Commission Rates & Accounting:**
   - Every category can define a `commissionRate` (e.g. 8.5%).
   - When an order completes, the platform computes `platformFee = sellingPrice * (commissionRate / 100)` for accurate marketplace accounting.
4. **Seller KYC Lifecycle & Role Synchronization:**
   - Registration creates a seller with `PENDING_VERIFICATION` (user has role `CUSTOMER`).
   - Admin approves KYC (`ACTIVE`) -> User is automatically granted `ROLE_SELLER`.
   - If Admin suspends (`SUSPENDED`) or bans (`BANNED`) the seller -> `ROLE_SELLER` is automatically revoked, protecting the platform.

---

## 🚀 Getting Started

### Prerequisites
- JDK 17+
- MySQL 8.x
- Redis 7.x
- Maven 3.8+

### Environment Configuration
Configure environment variables in `.env` or `src/main/resources/application-local.yml`:

```env
SERVER_PORT=8080
DB_URL=jdbc:mysql://localhost:3306/ecommerce_db?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
REDIS_HOST=localhost
REDIS_PORT=6379
JWT_SECRET=your-secure-jwt-secret-key-minimum-256-bits-long
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_USERNAME=your-email@gmail.com
SPRING_MAIL_PASSWORD=your-google-app-password
```

### Build & Run
```bash
# Run unit tests
mvn test -Dtest="com.abs.app.unit.**"

# Start the Spring Boot application
mvn spring-boot:run
```

---

## 🧪 Testing & Verification

- **Unit Tests:** 175 tests with JUnit 5 & Mockito covering commands, queries, and business rules (`100% PASS`).
- **Postman Collection:** Ready-to-run collection at [`docs/postman/Ecommerce-Multivendor.postman_collection.json`](docs/postman/Ecommerce-Multivendor.postman_collection.json) with automated assertion scripts.
- **API Documentation:** Comprehensive Frontend specification in [`docs/api_endpoints.md`](docs/api_endpoints.md).
