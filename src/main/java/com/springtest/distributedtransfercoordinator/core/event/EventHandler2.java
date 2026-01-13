package com.springtest.distributedtransfercoordinator.core.event;

import org.springframework.stereotype.Component;

@Component
public class EventHandler2 implements Listener {
    @Override
    public void handler(Event event) {
        System.out.println("from other hander!?: " + event.getString());
    }
}
