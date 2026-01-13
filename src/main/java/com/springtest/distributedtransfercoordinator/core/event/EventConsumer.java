package com.springtest.distributedtransfercoordinator.core.event;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class EventConsumer {
    final EventBus eventBus;

    public EventConsumer(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @PostConstruct
    public void start() {
        var event = new Transfer();
        var listener = new EventHandler();
        var listener2 = new EventHandler2();
        this.eventBus.addListener(listener);
        this.eventBus.addListener(listener2);
        this.eventBus.addEvent(event);
    }
}
