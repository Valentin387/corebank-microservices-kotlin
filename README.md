# CoreBank Microservices – Kotlin Fresh Start (v2.0)

**Engineering Requirements Document (ERD)**  
**corebank-microservices-kotlin**  
**Phase: Kotlin Microservices (Fresh Start)**

**Document Version:** 2.0  
**Status:** Active  
**Project Series:** CoreBank Modernization Journey

---

## 1. Executive Summary

The `corebank-microservices-kotlin` project is a **green-field implementation** of the CoreBank microservices architecture using **pure Kotlin**, **Coroutines**, and **R2DBC**. This represents an optimal async-first architecture built from scratch, removing all legacy blocking code and Reactor wrappers.

From a clean slate, we have:
- Two independently deployable microservices (`auth-service` + `core-service`)
- A shared `banking-commons` library (extracted common concerns)
- Full **Hexagonal Architecture (Ports & Adapters)** per service
- Secure, header-driven inter-service communication
- **Native Coroutines** (`suspend` functions) and **R2DBC** in the core domain

---

## 2. System Overview

**Purpose**  
Demonstrate a modern, idiomatic Kotlin microservices architecture using native coroutines and non-blocking I/O while maintaining the exact same external API contract as previous phases.

**Business Capabilities**
- Client authentication with JWT (`auth-service`)
- Aggregated product & balance information for authenticated clients (`core-service`)

**Non-Functional Goals**
- 80%+ test coverage enforced via Kotest
- True async database access via R2DBC
- Strict separation of business logic from infrastructure using Hexagonal Architecture

---

## 3. Architecture

### High-Level Flow
```mermaid
flowchart TD
    Client[Client / Insomnia] --> Auth[auth-service :8081<br/>POST /api/auth/login]
    Client --> Core[core-service :8082<br/>GET /api/home/{customerId}]
    Auth <--> Redis[(Redis Token Cache)]
    Core <--> Postgres[(PostgreSQL via R2DBC)]
    subgraph banking-commons [Shared Library]
        direction TB
        Commons[ResponseDTO<br/>JwtUtil<br/>ReactiveJwtFilter<br/>Shared DTOs]
    end
    Auth --> Commons
    Core --> Commons
```

### Hexagonal Architecture (per service)
```
domain/         → Entities, Value Objects (Kotlin Data Classes)
port/           → Input/Output Interfaces (Suspend Functions)
service/        → Application Use Cases (Coroutines)
adapter/        → Web Controllers & R2DBC Repositories
```

---

## 4. Technical Stack

| Category                  | Technology                              | Version       | Service                          |
|---------------------------|-----------------------------------------|---------------|----------------------------------|
| Language                  | Kotlin                                  | **2.2.21**    | All modules                      |
| Framework                 | Spring Boot                             | **4.0.6**     | All modules                      |
| Build                     | Gradle (Kotlin DSL)                     | 8.x+          | Multi-module root                |
| Architecture              | Hexagonal + DDD                         | -             | Both services                    |
| Web                       | Spring WebFlux                          | -             | Both services (reactive)         |
| Security                  | Spring Security + JJWT                  | 0.12.6        | Both + commons                   |
| Database                  | PostgreSQL + Spring Data R2DBC          | -             | core-service                     |
| Async Execution           | Kotlin Coroutines                       | 1.10+         | core-service orchestration       |
| Shared Library            | banking-commons                         | -             | Common DTOs, security, utils     |
| Container                 | Docker + Docker Compose                 | -             | Full ecosystem                   |
| Testing                   | Kotest + MockK                          | 5.7.0         | ≥80% JaCoCo per module           |

---

## 5. Project Structure

```
corebank-microservices-kotlin/
├── banking-commons/                      # Shared library
│   └── src/main/kotlin/com/corebank/commons/
│       ├── domain/Models.kt (Account, Balance, Card)
│       ├── dto/LoginRequestDTO.kt
│       ├── model/ResponseDTO.kt
│       └── security/{JwtUtil.kt,ReactiveJwtFilter.kt}
├── auth-service/                         # Authentication bounded context
│   └── src/main/kotlin/com/corebank/auth/
│       ├── config/SecurityConfig.kt
│       ├── service/AuthApplicationService.kt
│       └── controller/AuthController.kt
├── core-service/                         # Product/Home bounded context (R2DBC)
│   └── src/main/kotlin/com/corebank/core/
│       ├── port/AccountRepositoryPort.kt
│       ├── repository/AccountRepository.kt
│       ├── adapter/AccountRepositoryAdapter.kt
│       ├── service/AccountApplicationService.kt
│       └── controller/{AccountController.kt,HomeController.kt}
├── docker-compose.yml
├── build.gradle.kts
└── settings.gradle.kts
```

---

## 6. Domain Capabilities

**Authentication Domain (`auth-service`)**
- Authentication with username / password (mock).
- JWT generation + custom claims.
- Configured using native WebFlux `SecurityWebFilterChain`.

**Homepage / Product Domain (`core-service`)**
- Aggregated view of accounts, cards, and balances.
- Reactive, non-blocking data retrieval using Coroutines (`suspend`).
- Native asynchronous database operations via R2DBC (`CoroutineCrudRepository`).

---

## 7. API Endpoints (Identical Contract)

| Endpoint              | Service        | Port | Method | Description                  | Required Headers                      |
|-----------------------|----------------|------|--------|------------------------------|---------------------------------------|
| `/api/auth/login`     | auth-service   | 8081 | POST   | Authenticate + issue JWT     | -                                     |
| `/api/home/{id}`      | core-service   | 8082 | GET    | Aggregated homepage data     | `Authorization: Bearer <token>`       |
| `/api/accounts/{id}`  | core-service   | 8082 | GET    | Account records              | `Authorization: Bearer <token>`       |

---

## 8. How to Run (Local Development)

### Prerequisites
- JDK 21
- Docker & Docker Compose

### Start Infrastructure
```bash
docker compose up -d   # Postgres + Redis
```

### Build & Run
```bash
./gradlew clean build

# In separate terminals:
./gradlew :auth-service:bootRun
./gradlew :core-service:bootRun
```

### Stop
```bash
docker compose down -v
```

---

## 9. Security

- Shared `ReactiveJwtFilter` implemented in `banking-commons` secures WebFlux apps using `WebFilter`.
- Extracts token and validates signature.
- All business endpoints require a valid JWT except for login and actuator.

---

## 10. Infrastructure & Deployment

- Local infrastructure powered by Docker Compose.
- **PostgreSQL** runs on port `5432` for `core-service`.
- **Redis** runs on port `6379` (currently placeholder for auth-service).
- Spring Data R2DBC initializes the DB using `schema.sql` and `data.sql` natively.

---

## 11. Testing

**Coverage Target**: ≥ 80% per module.

**Commands**:
```bash
./gradlew clean build test
```
Tests strictly adhere to idiomatic Kotlin testing patterns utilizing **Kotest** and **MockK**.

---

## 12. Key Differences from Java Phase

| Aspect                  | Phase 2 (Java)              | Kotlin Fresh Start                        |
|-------------------------|-----------------------------|-------------------------------------------|
| Language                | Java 21                     | Kotlin 2.2.21                             |
| Async Model             | Project Reactor (Flux/Mono) | Coroutines (`suspend`)                    |
| Database Access         | Blocking Spring Data JPA    | Non-blocking Spring Data R2DBC            |
| Data Objects            | Lombok `@Data`              | Kotlin Data Classes                       |
| Testing                 | JUnit 5 + Mockito           | Kotest + MockK                            |

---

## 13. Development Guidelines

- **Architecture Rules:** Business logic strictly belongs in the `service` and `domain` layers. Spring/R2DBC code strictly belongs in `adapter` / `repository`. 
- **Dependencies:** Use interfaces (ports) to communicate between core layers and external services/DBs.
- **Shared Commons:** Cross-cutting concerns go in `banking-commons`. Do not duplicate DTOs, Security classes, or models.
- **Testing:** Utilize `runTest` and Kotest features to natively test suspend functions.

---

## 14. How to Test the Endpoints with curl

##### 1. Login (Get JWT Token)

```bash
# Login (auth-service :8081)
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"password"}'
```

##### 2. Get Aggregated Balance (Protected Endpoint)

```bash
# Home Balance (core-service :8082 — replace <token> with the token from above)
curl http://localhost:8082/api/home/123456789 \
  -H "Authorization: Bearer <token>"
```
