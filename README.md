# financial-transaction-service

A small, self-contained Spring Boot 3 microservice that models a financial transaction API: submit a transaction, have it auto-approved or held for review based on amount, and fetch it back later.

This is a reference/demo implementation, not a production system. It's built to run with zero external setup (no database server, no message broker) while still showing a realistic layout: layered packages, JWT-secured endpoints, validation, pagination, and a clean seam for plugging in real infrastructure later.

## What it actually does

- `POST /api/transactions` — submit a transaction (`accountId`, `amount`, `currency`, `type`). Amounts at or above $10,000 are held as `PENDING_REVIEW`; everything else is `APPROVED`.
- `GET /api/transactions/{id}` — fetch a single transaction.
- `GET /api/transactions?accountId=...` — paginated list, optionally filtered by account.
- `POST /api/auth/login` — exchanges a demo credential (`demo` / `demo-password`) for a JWT used as a Bearer token on the endpoints above.
- `GET /actuator/health` — health check.

## Tech stack (what's really in the repo)

| Layer | Choice |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.3.4 (Web, Data JPA, Security, Validation, Actuator) |
| Persistence | H2, in-memory |
| Auth | Stateless JWT (HS256, `io.jsonwebtoken`/jjwt) via a custom filter |
| Events | A `TransactionEventPublisher` interface with a logging-only default implementation |
| Tests | JUnit 5 + Mockito |
| Build | Maven |

## Project structure

```
financial-transaction-service/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/shashireddy/fintx/
    │   │   ├── FinancialTransactionServiceApplication.java
    │   │   ├── model/Transaction.java
    │   │   ├── repository/TransactionRepository.java
    │   │   ├── dto/TransactionDtos.java
    │   │   ├── event/TransactionEventPublisher.java
    │   │   ├── service/TransactionService.java
    │   │   ├── security/JwtService.java
    │   │   ├── security/SecurityConfig.java
    │   │   └── controller/
    │   │       ├── AuthController.java
    │   │       └── TransactionController.java
    │   └── resources/application.yml
    └── test/java/com/shashireddy/fintx/service/TransactionServiceTest.java
```

## Running it locally

```bash
./mvnw spring-boot:run
```

The service starts on port 8080 with an in-memory H2 database — no external services required.

```bash
# get a token
curl -X POST localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"demo-password"}'

# use it
curl -X POST localhost:8080/api/transactions \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"accountId":"acct-1","amount":250.00,"currency":"usd","type":"DEPOSIT"}'
```

Run the tests with:

```bash
./mvnw test
```

## What's simplified, and how it maps to a real deployment

This repo intentionally trades production concerns for something that's honest, runnable, and easy to read end-to-end:

- **Database**: H2 in-memory instead of Postgres/MySQL. The JPA layer doesn't care — pointing `application.yml` at a real datasource is the only change needed.
- **Events**: `TransactionEventPublisher` is a logging stub. It's the integration seam where a `KafkaTemplate`-backed implementation (guarded by a `kafka` Spring profile) would publish to a topic like `transactions.processed` for downstream consumers.
- **Auth**: a single hard-coded demo credential issues a real JWT, so the rest of the API is genuinely JWT-secured. A real deployment swaps `AuthController` for a call to an actual identity provider (Okta, Auth0, etc.) — `JwtService` and the filter chain stay the same either way.
- **Risk rules**: the $10,000 auto-approval threshold is a placeholder for a real risk/fraud rules engine.

## License

MIT
