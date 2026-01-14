package com.springtest.distributedtransfercoordinator.services;

import com.springtest.distributedtransfercoordinator.DTO.TransferPayload;
import com.springtest.distributedtransfercoordinator.core.event.Event;
import com.springtest.distributedtransfercoordinator.core.event.EventBus;
import com.springtest.distributedtransfercoordinator.core.event.Listener;
import com.springtest.distributedtransfercoordinator.db.escrow.repos.EscrowRepo;
import com.springtest.distributedtransfercoordinator.db.seller.repos.SellerRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TransferSagaOrchestrator implements Listener {
    private static final Logger log = LoggerFactory.getLogger(TransferSagaOrchestrator.class);
    final EventBus eventBus;
    final EscrowRepo escrowRepo;
    final SellerRepo sellerRepo;

    public TransferSagaOrchestrator(EventBus eventBus, EscrowRepo escrowRepo, SellerRepo sellerRepo) {
        this.eventBus = eventBus;
        this.escrowRepo = escrowRepo;
        this.sellerRepo = sellerRepo;
    }

    @Override
    public void handler(Event event) {
        TransferPayload payload = (TransferPayload) event.getPayload();
        System.out.println(payload.getAmount());

        switch (event.getEventTypeId()) {
            case TRANSFER_REQUESTED -> {
                try {
                    escrowRepo.findById(payload.getEscrowId())
                            .orElseThrow();
                    sellerRepo.findById(payload.getSellerId())
                            .orElseThrow();

                    logInfoStatus(event);
                    var debitEscrowEvent = new Event(TransferSagaEventType.DEBIT_ESCROW, event.getSagaId(), event.getPayload());
                    eventBus.addEvent(debitEscrowEvent);
                } catch (Exception e) {
                    logErrorStatus(event);
                    var requestFailedEvent = new Event(TransferSagaEventType.TRANSFER_REQUESTED_FAILED, event.getSagaId(), event.getPayload());
                    eventBus.addEvent(requestFailedEvent);
                }
            }
            case TRANSFER_REQUESTED_FAILED -> {

            }
            case DEBIT_ESCROW -> {
                // Get escrow record
                // Subtract amount from it
                // Save updated to DB
                // Ensure escrow amount isn't under 0
                // If so, publish failed event
                // If not, proceed
            }
            case DEBIT_ESCROW_FAILED -> {
                // Ensure escrow amount is reset
                //
            }
        }
    }

    public static void logInfoStatus(Event event) {
        log.info("SAGA Transfer ID: {} === EVENT TYPE: {} === SUCCEEDED", event.getSagaId(), event.getEventTypeId());
    }

    public static void logErrorStatus(Event event) {
        log.error("SAGA Transfer ID: {} === EVENT TYPE: {} === SUCCEEDED", event.getSagaId(), event.getEventTypeId());
    }
}
