package com.springtest.distributedtransfercoordinator.db.eventstore.models;

import com.springtest.distributedtransfercoordinator.DTO.TransferPayload;
import com.springtest.distributedtransfercoordinator.services.TransferSagaEventType;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class SagaEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Embedded
    private TransferPayload payload;
    private TransferSagaEventType eventType;
    private LocalDateTime createdAt = LocalDateTime.now();

    public SagaEvent() {
    }

    public SagaEvent(UUID id, TransferSagaEventType eventType, UUID sagaId, UUID escrowId, UUID sellerId, LocalDateTime createdAt, TransferPayload payload) {
        this.id = id;
        this.eventType = eventType;
        this.payload = payload;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public TransferSagaEventType getEventType() {
        return eventType;
    }

    public void setEventType(TransferSagaEventType eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public TransferPayload getPayload() {
        return payload;
    }

    public void setPayload(TransferPayload payload) {
        this.payload = payload;
    }
}