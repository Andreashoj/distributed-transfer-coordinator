# Distributed Transfer Coordinator

A Spring Boot service that orchestrates escrow-based money transfers using the saga pattern with event sourcing.

## Overview

This service handles the coordination of transfers between buyers and sellers through an escrow system. It uses a
choreography-based saga to manage distributed transactions across multiple services, ensuring consistency even if
individual steps fail.

## How It Works

The transfer flow follows these steps:

1. **TRANSFER_REQUESTED** - Validates that both escrow and seller exist
2. **DEBIT_ESCROW** - Deducts funds from the escrow account
3. **SELLER_COMPENSATE** - Credits the seller with the transferred amount
4. **COMPLETE_TRANSFER** - Marks the transfer as complete

If any step fails, the saga triggers compensation events to roll back changes and maintain consistency.

## Architecture

- **Event Sourcing** - All saga steps are persisted as immutable events in the event store
- **Event Bus** - Asynchronous event publishing and handling between saga steps
- **Saga Pattern** - Distributed transaction coordination with compensating transactions
- **Multiple Data Sources** - Separate PostgreSQL connections for escrow, seller, and event store data

## Key Components

- `TransferSagaOrchestrator` - Main saga orchestrator that handles event routing and business logic
- `SagaEvent` - Event entity stored in the event store
- `EventBus` - Manages event publishing and listener registration
- Event Handlers - Listen for specific event types and trigger the next saga step

## Testing

The project includes unit tests for the saga orchestrator covering:

- Happy path scenarios
- Insufficient funds handling
- Missing entity handling (seller/escrow deleted)
- State verification after operations

Run tests with:

```bash
mvn test
```

## Local Development

### Prerequisites

The service requires three separate PostgreSQL databases running. Start them with Docker:

```bash
docker-compose up
```

This spins up three PostgreSQL instances:

- **Escrow DB** - Port 5432
- **Seller DB** - Port 5433
- **Event Store DB** - Port 5434

Each database is created automatically on startup and migrations are applied via Flyway.

### Running the Service

```bash
mvn spring-boot:run
```

The service starts on port 8080 and connects to all three databases configured in `application.properties`.

## Tech Stack

- Spring Boot 4.0.1
- Spring Data JPA
- Spring Retry (for compensation retries with exponential backoff)
- PostgreSQL
- Flyway (for database migrations)
- JUnit 5 & Mockito (for testing)