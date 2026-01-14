package com.springtest.distributedtransfercoordinator.core.event;

import com.springtest.distributedtransfercoordinator.services.TransferSagaOrchestrator;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventBusConfig {
    final EventBus eventBus;
    final TransferSagaOrchestrator transferSagaOrchestrator;

    public EventBusConfig(EventBus eventBus, TransferSagaOrchestrator transferSagaOrchestrator) {
        this.eventBus = eventBus;
        this.transferSagaOrchestrator = transferSagaOrchestrator;
    }

    @PostConstruct
    public void registerListeners() {
        eventBus.addListener(transferSagaOrchestrator);
    }
}
