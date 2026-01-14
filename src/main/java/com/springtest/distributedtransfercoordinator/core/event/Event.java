package com.springtest.distributedtransfercoordinator.core.event;

import com.springtest.distributedtransfercoordinator.services.TransferSagaEventType;

import java.time.LocalDateTime;
import java.util.UUID;

public class Event {
    private TransferSagaEventType eventTypeId;
    private UUID sagaId;
    private Object payload;
    private LocalDateTime datetime = LocalDateTime.now();

    public Event(TransferSagaEventType eventTypeId, UUID sagaId, Object payload) {
        this.eventTypeId = eventTypeId;
        this.sagaId = sagaId;
        this.payload = payload;
    }

    public TransferSagaEventType getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(TransferSagaEventType eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public UUID getSagaId() {
        return sagaId;
    }

    public void setSagaId(UUID sagaId) {
        this.sagaId = sagaId;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}

