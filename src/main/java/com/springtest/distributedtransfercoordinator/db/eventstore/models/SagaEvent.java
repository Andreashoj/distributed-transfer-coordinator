package com.springtest.distributedtransfercoordinator.db.eventstore.models;

import com.springtest.distributedtransfercoordinator.DTO.TransferPayload;
import com.springtest.distributedtransfercoordinator.services.TransferSagaEventType;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "saga_event")
public class SagaEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Embedded
    private TransferPayload payload;
    @Column(name = "event_type")
    @Enumerated(EnumType.STRING)
    private TransferSagaEventType eventType;
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public SagaEvent() {
    }

    public SagaEvent(TransferSagaEventType eventType, TransferPayload payload) {
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