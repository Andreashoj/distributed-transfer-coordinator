package com.springtest.distributedtransfercoordinator.core.event;

public class Transfer implements Event {
    public String getString() {
        return "from event handler!";
    }
}
