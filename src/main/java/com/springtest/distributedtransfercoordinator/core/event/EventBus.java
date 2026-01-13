package com.springtest.distributedtransfercoordinator.core.event;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class EventBus {
    private static final Logger log = LoggerFactory.getLogger(EventBus.class);
    private BlockingQueue<Event> events = new LinkedBlockingQueue<Event>();
    private List<Listener> listeners = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void start() {
        new Thread(this::handle).start();
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void addEvent(Event event) {
        events.add(event);
    }


    private void handle() {
        while (true) {
            try {
                var event = events.take();
                for (Listener listener : listeners) {
                    listener.handler(event);
                }
            } catch (InterruptedException e) {
                log.error("Interrupted event bus handler with error: {}", e);
                break;
            }
        }
    }
}