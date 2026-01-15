package com.springtest.distributedtransfercoordinator.services;

import com.springtest.distributedtransfercoordinator.DTO.TransferPayload;
import com.springtest.distributedtransfercoordinator.core.event.Event;
import com.springtest.distributedtransfercoordinator.core.event.EventBus;
import com.springtest.distributedtransfercoordinator.core.event.Listener;
import com.springtest.distributedtransfercoordinator.db.escrow.models.EscrowStatus;
import com.springtest.distributedtransfercoordinator.db.escrow.repos.EscrowRepo;
import com.springtest.distributedtransfercoordinator.db.seller.repos.SellerRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

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
                } catch (NoSuchElementException e) {
                    logErrorStatus(event, e);
                }
            }
            case DEBIT_ESCROW -> {
                try {
                    var escrow = escrowRepo
                            .findById(payload.getEscrowId())
                            .orElseThrow();

                    escrow.debitAmount(payload.getAmount());
                    escrowRepo.save(escrow);

                    logInfoStatus(event);
                    var sellerCompensateEvent = new Event(TransferSagaEventType.SELLER_COMPENSATE, event.getSagaId(), payload);
                    eventBus.addEvent(sellerCompensateEvent);
                } catch (Exception e) {
                    logErrorStatus(event, e);
                    var debitEscrowFailedEvent = new Event(TransferSagaEventType.DEBIT_ESCROW_FAILED, event.getSagaId(), payload);
                    eventBus.addEvent(debitEscrowFailedEvent);
                }
            }
            case SELLER_COMPENSATE -> {
                try {
                    var seller = sellerRepo.findById(payload.getSellerId())
                            .orElseThrow();
                    seller.compensateSale(payload.getAmount());
                    sellerRepo.save(seller);
                    logInfoStatus(event);
                    var completeTransfer = new Event(TransferSagaEventType.COMPLETE_TRANSFER, event.getSagaId(), payload);
                    eventBus.addEvent(completeTransfer);
                } catch (Exception e) {
                    logErrorStatus(event, e);
                    var sellerCompensateFailedEvent = new Event(TransferSagaEventType.SELLER_COMPENSATE_FAILED, event.getSagaId(), payload);
                    eventBus.addEvent(sellerCompensateFailedEvent);
                }
            }
            case SELLER_COMPENSATE_FAILED -> {
                try {
                    compensateEscrow(payload);
                    logInfoStatus(event);
                } catch (Exception e) {
                    logErrorStatus(event, e); // Important log - will need careful monitoring to avoid impartial compensation
                }
            }
            case COMPLETE_TRANSFER -> {
                try {
                    var escrow = escrowRepo.findById(payload.getEscrowId())
                            .orElseThrow();
                    escrow.setStatus(EscrowStatus.COMPLETE);
                    escrowRepo.save(escrow);
                } catch (Exception e) {
                    var completeTransferFailedEvent = new Event(TransferSagaEventType.SELLER_COMPENSATE_FAILED, event.getSagaId(), payload);
                    eventBus.addEvent(completeTransferFailedEvent);
                }
            }
            case COMPLETE_TRANSFER_FAILED -> {
                try {
                    var escrow = escrowRepo.findById(payload.getEscrowId())
                            .orElseThrow();
                    escrow.setStatus(EscrowStatus.FAILED);
                    escrowRepo.save(escrow);
                } catch (Exception e) {
                    logErrorStatus(event, e);
                }
            }
        }
    }

    @Retryable(
            maxRetries = 3
    )
    private void compensateEscrow(TransferPayload payload) {
        var escrow = escrowRepo.findById(payload.getEscrowId())
                .orElseThrow();
        escrow.compensateCredit(payload.getAmount());
        escrowRepo.save(escrow);
    }

    public static void logInfoStatus(Event event) {
        log.info("SAGA Transfer ID: {} === EVENT TYPE: {} === SUCCEEDED", event.getSagaId(), event.getEventTypeId());
    }

    public static void logErrorStatus(Event event, Exception e) {
        log.error("SAGA Transfer ID: {} === EVENT TYPE: {} === ERROR: {}", event.getSagaId(), event.getEventTypeId(), e.getMessage(), e);
    }
}
