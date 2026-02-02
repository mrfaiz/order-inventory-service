# Order & Inventory Service

A backend service that manages product inventory and order processing with a strong focus on
transactional consistency, concurrency safety, and idempotent APIs.

The project demonstrates production-grade backend patterns such as database migrations,
row-level locking, idempotent request handling, and integration testing against a real
PostgreSQL database.

## ✨ Features

- **Transactional order processing**
    - Orders and order lines are created atomically within a single database transaction

- **Inventory consistency under concurrency**
    - Inventory rows are protected using pessimistic locking to prevent overselling

- **Idempotent order creation**
    - Repeated `POST /orders` requests with the same idempotency key return the same order
    - Inventory is deducted only once, even under retries

- **Safe order cancellation**
    - Orders can be cancelled idempotently
    - Inventory is restored within a transactional boundary

- **Read APIs with DTO boundaries**
    - Products, inventory, and orders are exposed via DTOs instead of JPA entities
    - Prevents lazy-loading issues and infinite serialization loops

- **Database migrations**
    - Schema and seed data are managed via Flyway migrations

- **Realistic integration testing**
    - All critical flows are validated against a real PostgreSQL database using Testcontainers

## 🛠 Tech Stack

- **Java 21**
- **Spring Boot**
    - Spring Web (REST APIs)
    - Spring Data JPA
    - Spring Validation
    - Spring Security (basic configuration)
- **PostgreSQL**
    - UUID-based identifiers
    - Transactional and locking behavior
- **Flyway**
    - Versioned database migrations
- **JUnit 5**
    - Integration and application-level tests
- **Testcontainers**
    - Ephemeral PostgreSQL instances for testing
- **Docker**
    - Local development database

## 🧪 Testing Approach

This project focuses on **testing real database behavior**, not just application logic.

### Why this matters
The core business logic (order placement, inventory deduction, cancellation, idempotency) depends on:
- database transactions
- row-level locking
- constraints and indexes
- correct behavior under retries

These aspects **cannot be reliably tested with mocks or in-memory databases**.

### Integration tests with real PostgreSQL
All critical flows are tested using **integration tests** against a **real PostgreSQL database**, running in a Docker container via **Testcontainers**.

Key characteristics:
- each test suite starts a fresh PostgreSQL container
- Flyway migrations are applied automatically
- the schema used in tests is identical to production

This ensures:
- transactional behavior is correct
- locking behaves as expected
- constraints (FK, unique indexes) are enforced
- no environment-specific surprises

### What is tested
The integration tests verify:

- **Order placement**
    - inventory is deducted inside a transaction
    - order and order lines are persisted atomically

- **Idempotency**
    - retrying `POST /orders` with the same idempotency key does not create duplicate orders
    - inventory is deducted only once, even under retries

- **Order cancellation**
    - order status transitions correctly
    - inventory is restored
    - cancellation is idempotent

- **Read APIs**
    - orders, products, and inventory are fetched correctly
    - DTOs are returned instead of entities (prevents infinite serialization loops)

### Tools used
- **JUnit 5** — test framework
- **Spring Boot test support** — full application context
- **Testcontainers** — ephemeral PostgreSQL instances
- **Flyway** — schema and seed data migrations

No repositories or database interactions are mocked.

### Summary
This testing strategy prioritizes **correctness, realism, and confidence** over speed.  
The goal is to ensure the system behaves correctly under real-world conditions such as retries, concurrency, and partial failures.

## 🚀 Running the Service & API Usage

### How to Run

#### Prerequisites
- Java 21
- Docker + Docker Compose
- Maven (`mvn`)

#### Start PostgreSQL
From the project root:

```bash
docker compose up -d
```
If port 5432 is already in use, stop the existing PostgreSQL instance or update the exposed port in docker-compose.yml.

#### Run the application
```bash
mvn spring-boot:run
```
On startup, Flyway automatically applies all database migrations (schema + seed data).


#### Run the test
```bash
mvn test
```

### API Examples

#### List products
```bash
curl http://localhost:8080/products
```

#### Get inventory for a product
```bash
curl http://localhost:8080/inventory/11111111-1111-1111-1111-111111111111
```

#### Place an order (idempotent)
The `Idempotency-Key` header ensures retries do not create duplicate orders.
```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: demo-order-1" \
  -d '{
    "key": "demo-order-1",
    "lines": [
      { "productId": "11111111-1111-1111-1111-111111111111", "quantity": 2 }
    ]
  }'
```

#### Fetch an order (with order lines)
Replace `<ORDER_ID>` with the UUID returned from POST `/orders`.
```bash
curl http://localhost:8080/orders/<ORDER_ID>
```

#### Cancel an order (idempotent)
Calling this endpoint multiple times is safe.
```bash
curl -X POST http://localhost:8080/orders/<ORDER_ID>/cancel
```

#### Verify inventory after order or cancellation
```bash
curl http://localhost:8080/inventory/11111111-1111-1111-1111-111111111111
```

