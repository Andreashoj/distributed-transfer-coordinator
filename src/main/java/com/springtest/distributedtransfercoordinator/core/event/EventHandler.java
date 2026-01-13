package com.springtest.distributedtransfercoordinator.core.event;

import org.springframework.stereotype.Component;

@Component
public class EventHandler implements Listener {
    @Override
    public void handler(Event event) {
        System.out.println(event.getString());
    }
}

