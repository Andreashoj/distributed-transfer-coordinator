Distributed Transfer Coordinator - Project Spec
Overview
A Java CLI tool that coordinates money transfers from a Platform Escrow account to external Seller accounts using the Saga pattern, Event Sourcing, and CQRS.
The system handles distributed transactions where the two services have independent databases and cannot participate in a shared transaction. Domain Context 
Platform Escrow Service: Holds buyer payments in escrow before transferring to sellers (your database)
Seller Service: Manages seller account balances (simulated as external/separate database)
Problem: Transfer money from escrow to seller safely, handling failures with compensating transactions

Core Patterns

Event Sourcing: All state changes logged as immutable events
CQRS: Separate write path (commands → events) and read path (denormalized views)
Saga Pattern: Distributed transactions coordinated through compensating transactions

Architecture
Two independent PostgreSQL databases (Docker)
One event store (SQLite or similar) logging all events
In-memory event bus for coordinating between services
Java CLI with no web framework

CLI Commands to Build
transfer --from escrow --to-seller <seller_id> --amount <amount>
balance --escrow
balance --seller <seller_id>
status --transfer-id <id>
transfers
seed
Transfer Saga Flow
Happy path:

TransferRequested event
Debit escrow account → EscrowDebited event
Credit seller account → SellerCredited event
TransferCompleted event

Failure path (seller credit fails):

TransferRequested event
EscrowDebited event (success)
SellerCreditFailed event
Compensate: Credit escrow back → EscrowRefunded event
TransferFailed event

Success Criteria

Seed command creates initial balances
Transfer command initiates saga, returns transfer_id
Happy path: escrow debited, seller credited, balances update
Failure compensation: if seller credit fails, escrow is refunded
Status command shows full event history of transfer
System recovers from crashes by replaying events