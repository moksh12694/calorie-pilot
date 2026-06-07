# CaloriePilot

A production-style, full-stack fitness tracking application that lets users track steps, log food and macros, monitor water and weight, view analytics, maintain streaks, and receive smart push notifications.

The project is built as a **Spring Boot 3 + PostgreSQL** backend with a **React Native (Expo) + TypeScript** mobile client and is fully containerized for one-command local startup.

---

## Table of Contents

1. [Features](#features)
2. [Architecture](#architecture)
3. [Tech Stack](#tech-stack)
4. [Repository Layout](#repository-layout)
5. [Getting Started](#getting-started)
6. [Configuration](#configuration)
7. [API Documentation](#api-documentation)
8. [Database & Migrations](#database--migrations)
9. [Testing](#testing)
10. [Module Status](#module-status)
11. [Contributing](#contributing)
12. [License](#license)

---

## Features

- **Authentication** — Email/password signup & login secured with JWT access & refresh tokens.
- **Step Tracking** — Live step counts via `expo-pedometer`, configurable goals (5k / 10k / 15k / custom), daily history.
- **Smart Notifications** — Expo push notifications at 80%, 90%, 95% and 100% goal milestones with idempotency to prevent duplicates.
- **Food & Macros** — Searchable food catalog, meal logging (Breakfast / Lunch / Dinner / Snacks), daily calorie + protein/carbs/fat roll-ups.
- **Water Tracking** — Log intake in ml, see daily progress against goal.
- **Weight Tracking** — Body weight log with historical trends.
- **Analytics** — Daily / weekly / monthly time-series for calories, steps, water, and weight.
- **Streaks & Achievements** — Goal-completion streaks and badge awards.
- **Operational** — Docker Compose stack, Flyway migrations with seed data, Postman collection, Swagger UI.

---

## Architecture

```
 ┌──────────────────────────┐       HTTPS / JSON       ┌──────────────────────────┐
 │   Mobile (Expo / RN)     │ ───────────────────────▶ │  Spring Boot 3 REST API  │
 │  Redux Toolkit (auth)    │                          │  Layered (Controller →   │
 │  React Query (server)    │ ◀─────── JWT ─────────── │   Service → Repository)  │
 │  Expo Pedometer / Push   │                          │  Spring Security + JWT   │
 └──────────────────────────┘                          └──────────┬───────────────┘
              │                                                   │  JPA
              │  Expo Push Service                                ▼
              ▼                                          ┌────────────────────┐
       device push token                                 │   PostgreSQL 15    │
                                                         │  (Flyway managed)  │
                                                         └────────────────────┘
```

The backend follows a **modular, layered architecture**. Each domain (`auth`, `steps`, `food`, `water`, `weight`, `notifications`, `analytics`, `achievements`, `user`) is self-contained under `modules/` with its own Controller, Service, Repository, Entity and DTOs. Cross-cutting concerns (security, exception handling, base entities, configuration) live in `common/`, `config/`, and `security/`.

---

## Tech Stack

### Backend


| Concern            | Choice                                    |
| ------------------ | ----------------------------------------- |
| Language / Runtime | Java 17                                   |
| Framework          | Spring Boot 3.2.x                         |
| Security           | Spring Security + JWT (`jjwt` 0.12.x)     |
| Persistence        | Spring Data JPA, Hibernate                |
| Database           | PostgreSQL 15                             |
| Migrations         | Flyway                                    |
| Validation         | Jakarta Bean Validation                   |
| Mapping            | MapStruct                                 |
| Boilerplate        | Lombok                                    |
| API Docs           | Springdoc OpenAPI (Swagger UI)            |
| Tests              | JUnit 5, Spring Boot Test, Testcontainers |


### Mobile


| Concern        | Choice                                                      |
| -------------- | ----------------------------------------------------------- |
| Framework      | Expo SDK 50, React Native, TypeScript                       |
| Client state   | Redux Toolkit (auth/session)                                |
| Server state   | React Query (caching, retries, invalidation)                |
| Navigation     | React Navigation (stack + tabs)                             |
| Native modules | `expo-pedometer`, `expo-notifications`, `expo-secure-store` |
| HTTP           | Axios with auth interceptor                                 |


### Infrastructure

- Docker + docker-compose (Postgres + API)
- Postman collection + environment
- SQL seed data shipped with Flyway

---

## Repository Layout

```
calorie-pilot/
├── backend/                Spring Boot service
│   ├── src/main/java/com/caloriepilot/api/
│   │   ├── common/         DTOs, base entity, exception handling, utils
│   │   ├── config/         CORS, OpenAPI configuration
│   │   ├── modules/        Domain modules (auth, steps, food, water, ...)
│   │   └── security/       JWT filter, JwtService, SecurityConfig
│   ├── src/main/resources/
│   │   ├── application*.yml
│   │   └── db/migration/   Flyway SQL migrations + seeds
│   ├── Dockerfile
│   └── pom.xml
│
├── mobile/                 Expo / React Native app
│   ├── src/
│   │   ├── api/            Axios client + per-domain API helpers
│   │   ├── components/     Reusable UI
│   │   ├── hooks/          useAuth, usePedometer, usePushRegistration
│   │   ├── navigation/     Root / Auth / Tabs navigators
│   │   ├── screens/        Dashboard, FoodLog, Water, Weight, Profile, ...
│   │   ├── store/          Redux store + slices
│   │   └── utils/          Secure storage helpers
│   ├── App.tsx
│   └── package.json
│
├── docs/                   Module-by-module deep dives
├── postman/                Collection + environment
├── docker-compose.yml      Postgres + API
├── .env.example
└── README.md
```

---

## Getting Started

### Prerequisites

- Docker Desktop (or Docker Engine + Compose)
- Node.js 18+ and npm
- Expo Go on your phone, or an iOS / Android simulator
- JDK 17 and Maven (only required for running the backend outside Docker)

### 1. Clone & configure

```bash
git clone <repository-url>
cd calorie-pilot
cp .env.example .env
# edit .env and set a strong JWT_SECRET (>= 32 bytes) before any non-local run
```

### 2. Start backend + database with Docker

```bash
docker-compose up --build
```

- API: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/api/swagger-ui.html`
- Postgres: `localhost:5432` (credentials from `.env`)

### 3. Start the mobile app

```bash
cd mobile
npm install
npm start
```

Then scan the QR code with Expo Go, or press `i` / `a` to launch the iOS / Android simulator.

> Note: on a physical device, point the mobile app at your machine's LAN IP (not `localhost`). The API base URL is configured in `mobile/src/api/client.ts`.

---

## Configuration

All runtime configuration is driven by environment variables. See `.env.example` for the full list; the most important ones:


| Variable                                              | Default                                | Description                                     |
| ----------------------------------------------------- | -------------------------------------- | ----------------------------------------------- |
| `POSTGRES_DB` / `POSTGRES_USER` / `POSTGRES_PASSWORD` | `caloriepilot`                         | Database credentials                            |
| `SPRING_PROFILES_ACTIVE`                              | `dev`                                  | Spring profile (`dev` or `prod`)                |
| `JWT_SECRET`                                          | *(must override)*                      | HMAC signing key, **must be at least 32 bytes** |
| `JWT_ACCESS_TTL`                                      | `60`                                   | Access token TTL in minutes                     |
| `JWT_REFRESH_TTL`                                     | `30`                                   | Refresh token TTL in days                       |
| `CORS_ORIGINS`                                        | `*`                                    | Allowed origins (comma-separated)               |
| `EXPO_PUSH_ENDPOINT`                                  | `https://exp.host/--/api/v2/push/send` | Expo push service endpoint                      |


---

## API Documentation

- **Swagger UI**: `http://localhost:8080/api/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/api/v3/api-docs`
- **Postman**: import `postman/CaloriePilot.postman_collection.json` and `postman/CaloriePilot.postman_environment.json`. The collection contains a request for every endpoint and uses environment variables for the access token returned by `/auth/login`.

Authentication is **JWT Bearer**. After `/auth/login` (or `/auth/signup`), include the access token as:

```
Authorization: Bearer <accessToken>
```

The endpoint surface is grouped by module (see `docs/` for per-module deep dives). Representative endpoints:


| Module       | Endpoint(s)                                                    |
| ------------ | -------------------------------------------------------------- |
| Auth         | `POST /auth/signup`, `POST /auth/login`, `POST /auth/refresh`  |
| User         | `GET /users/me`, `PUT /users/me/goal`                          |
| Steps        | `POST /steps/sync`, `GET /steps/today`, `GET /steps?from=&to=` |
| Food         | `GET /foods/search`, `POST /meals`, `GET /meals/daily-summary` |
| Water        | `POST /water`, `GET /water/today`                              |
| Weight       | `POST /weight`, `GET /weight`                                  |
| Analytics    | `GET /analytics?metric=&range=`                                |
| Achievements | `GET /achievements`, `GET /streaks`                            |
| Push         | `POST /push/devices`                                           |


---

## Database & Migrations

Schema is managed exclusively through **Flyway**. Migrations live in `backend/src/main/resources/db/migration/`:

```
V1__baseline.sql   — extensions, base setup
V2__schema.sql     — all domain tables, indexes, constraints
V3__seed.sql       — reference data (foods, achievement templates, ...)
```

On startup, Spring Boot applies any pending migrations before the application context finishes loading. Never edit a migration that has already been applied to a shared environment — add a new versioned migration instead.

---

## Testing

### Backend

```bash
cd backend
./mvnw test
```

Integration tests use **Testcontainers** to spin up a disposable PostgreSQL instance, so they exercise real SQL, real Flyway migrations and real Spring Security — no mocks at the data layer.

### Mobile

```bash
cd mobile
npm test
```

Jest is configured for the mobile app (`mobile/jest.config.js`). Sample suite: `mobile/src/__tests__/secureStorage.test.ts`.

---

