package com.springtest.distributedtransfercoordinator.services;

import com.springtest.distributedtransfercoordinator.DTO.TransferPayload;
import com.springtest.distributedtransfercoordinator.core.event.Event;
import com.springtest.distributedtransfercoordinator.core.event.EventBus;
import com.springtest.distributedtransfercoordinator.core.event.Listener;
import org.springframework.stereotype.Service;

@Service
public class TransferSagaOrchestrator implements Listener {
    final EventBus eventBus;

    public TransferSagaOrchestrator(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void handler(Event event) {
        TransferPayload payload = (TransferPayload) event.getPayload();
        System.out.println(payload.getAmount());
    }
}
