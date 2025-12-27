# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Parfumerie Parfylia is a full-stack e-commerce application for a perfume store with:
- **Backend**: Spring Boot 3.2.1 (Java 17) at `Backend/parfilya/parfilya/`
- **Frontend**: React 19 with React Router v7 at `Frontend/parfylia-f/`
- **Multi-database architecture**: MySQL (users, products, orders), MongoDB (reviews, order history), Neo4j (brands, categories, product graph)

## Build and Run Commands

### Backend (Spring Boot)

```bash
cd Backend/parfilya/parfilya

# Build
mvnw.cmd clean package

# Run (port 8081)
mvnw.cmd spring-boot:run

# Run with custom JWT secret
mvnw.cmd spring-boot:run -Dspring-boot.run.arguments="--JWT_SECRET=your-256-bit-secret"
```

### Frontend (React)

```bash
cd Frontend/parfylia-f

# Install dependencies
npm install

# Development server (port 3000)
npm start

# Production build
npm run build

# Run tests
npm test
```

### Prerequisites

- Java 17+
- Node.js 18+
- MySQL on localhost:3306 (db: parfylia, user: root/root)
- MongoDB on localhost:27017 (db: parfylia_db)
- Neo4j on localhost:7687 (user: neo4j/Neo4j123)

## Architecture

### Backend Structure

```
Backend/parfilya/parfilya/src/main/java/parfumerie/parfilya/
├── config/          # CORS, Security, Database configs
├── controllers/     # REST endpoints (/api/*)
├── services/        # Business logic
├── models/          # Entities (mysql/, mongo/, neo4j/)
├── repositories/    # Data access (mysql/, mongo/, neo4j/)
├── security/        # JWT filter and utilities
├── dto/             # Request/Response objects
└── exceptions/      # Global exception handler
```

### Frontend Structure

```
Frontend/parfylia-f/src/
├── components/      # Reusable UI (user/, admin/)
├── context/         # AuthContext, CartContext, UserContext
├── pages/           # Page components (admin/)
├── routes/          # PrivateRoute, AdminRoute
├── services/        # API layer (authService, productService, etc.)
└── utils/           # axiosConfig with JWT interceptor
```

### API Endpoints

- **Public**: `/api/auth/**`, `/api/products/**`
- **User (ROLE_USER)**: `/api/cart/**`, `/api/orders/**`, `/api/profile/**`, `/api/address/**`
- **Admin (ROLE_ADMIN)**: `/api/admin/**`

### Authentication Flow

1. JWT-based with 24-hour expiration
2. Frontend stores token in localStorage
3. Axios interceptor adds `Authorization: Bearer {token}` header
4. Backend JwtFilter validates tokens on protected routes

### Key Configuration Files

- Backend: `Backend/parfilya/parfilya/src/main/resources/application.yaml`
- Frontend API URLs: Hardcoded in `src/services/*.js` and `src/utils/axiosConfig.js` (base: `http://localhost:8081/api`)
- CORS: Configured in backend SecurityConfig to allow `localhost:3000`

## Testing

Frontend authentication testing guide available at `Frontend/parfylia-f/TEST_FRONTEND.md`
