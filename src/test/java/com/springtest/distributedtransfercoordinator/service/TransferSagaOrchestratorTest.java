package com.springtest.distributedtransfercoordinator.service;

import com.springtest.distributedtransfercoordinator.DTO.TransferPayload;
import com.springtest.distributedtransfercoordinator.core.event.Event;
import com.springtest.distributedtransfercoordinator.core.event.EventBus;
import com.springtest.distributedtransfercoordinator.db.escrow.models.Escrow;
import com.springtest.distributedtransfercoordinator.db.escrow.repos.EscrowRepo;
import com.springtest.distributedtransfercoordinator.db.eventstore.models.SagaEvent;
import com.springtest.distributedtransfercoordinator.db.eventstore.repos.SagaEventRepo;
import com.springtest.distributedtransfercoordinator.db.seller.models.Seller;
import com.springtest.distributedtransfercoordinator.db.seller.repos.SellerRepo;
import com.springtest.distributedtransfercoordinator.services.TransferSagaEventType;
import com.springtest.distributedtransfercoordinator.services.TransferSagaOrchestrator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

public class TransferSagaOrchestratorTest {
    private TransferSagaOrchestrator orchestrator;
    @Mock
    private EventBus eventBus;
    @Mock
    private EscrowRepo escrowRepo;
    @Mock
    private SellerRepo sellerRepo;
    @Mock
    private SagaEventRepo sagaEventRepo;

    @BeforeEach()
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        orchestrator = new TransferSagaOrchestrator(eventBus, escrowRepo, sellerRepo, sagaEventRepo);
    }

    @Test
    public void testTransferRequest_Success() {
        var escrowID = UUID.randomUUID();
        var sellerID = UUID.randomUUID();
        var payload = new TransferPayload(escrowID, sellerID, BigDecimal.valueOf(1000));
        var event = new Event(TransferSagaEventType.TRANSFER_REQUESTED, UUID.randomUUID(), payload);

        when(escrowRepo.findById(escrowID)).thenReturn(Optional.of(new Escrow()));
        when(sellerRepo.findById(sellerID)).thenReturn(Optional.of(new Seller()));

        orchestrator.handler(event);

        verify(sagaEventRepo).save(any(SagaEvent.class));
        verify(eventBus).addEvent(argThat(e ->
                e.getEventTypeId() == TransferSagaEventType.DEBIT_ESCROW
        ));
    }

    @Test
    public void testTransferRequest_UnknownSellerID() {
        var escrowID = UUID.randomUUID();
        var sellerID = UUID.randomUUID();
        var payload = new TransferPayload(escrowID, sellerID, BigDecimal.valueOf(1000));
        var event = new Event(TransferSagaEventType.TRANSFER_REQUESTED, UUID.randomUUID(), payload);

        when(escrowRepo.findById(escrowID)).thenReturn(Optional.empty());
        when(sellerRepo.findById(sellerID)).thenReturn(Optional.of(new Seller()));

        orchestrator.handler(event);

        // Verify saga event was saved
        verify(sagaEventRepo).save(any(SagaEvent.class));
        // Verify transfer request failed event was emitted
        verify(eventBus).addEvent(argThat(e ->
                e.getEventTypeId() == TransferSagaEventType.TRANSFER_REQUESTED_FAILED
        ));
    }

    @Test
    public void testTransferRequest_UnknownEscrowID() {
        var escrowID = UUID.randomUUID();
        var sellerID = UUID.randomUUID();
        var payload = new TransferPayload(escrowID, sellerID, BigDecimal.valueOf(1000));
        var event = new Event(TransferSagaEventType.TRANSFER_REQUESTED, UUID.randomUUID(), payload);

        when(escrowRepo.findById(escrowID)).thenReturn(Optional.of(new Escrow()));
        when(sellerRepo.findById(sellerID)).thenReturn(Optional.empty());

        orchestrator.handler(event);

        // Verify saga event was saved
        verify(sagaEventRepo).save(any(SagaEvent.class));
        // Verify transfer request failed event was emitted
        verify(eventBus).addEvent(argThat(e ->
                e.getEventTypeId() == TransferSagaEventType.TRANSFER_REQUESTED_FAILED
        ));
    }

    @Test
    public void testDebitEscrow_Success() {
        var escrowID = UUID.randomUUID();
        var sellerID = UUID.randomUUID();
        var payload = new TransferPayload(escrowID, sellerID, BigDecimal.valueOf(1000));
        var event = new Event(TransferSagaEventType.DEBIT_ESCROW, UUID.randomUUID(), payload);

        var escrow = new Escrow();
        escrow.setAmount(BigDecimal.valueOf(1500));
        when(escrowRepo.findById(escrowID)).thenReturn(Optional.of(escrow));

        orchestrator.handler(event);

        // Verify that the debited escrow was saved
        verify(escrowRepo).save(any(Escrow.class));
        // Verify that the saga event is saved
        verify(sagaEventRepo).save(any(SagaEvent.class));
        // Verify that debit escrow event was called
        verify(eventBus).addEvent(argThat(e ->
                e.getEventTypeId() == TransferSagaEventType.SELLER_COMPENSATE
        ));
    }

    @Test
    public void testDebitEscrow_InsufficientFundsFail() {
        var escrowID = UUID.randomUUID();
        var sellerID = UUID.randomUUID();
        var payload = new TransferPayload(escrowID, sellerID, BigDecimal.valueOf(1000));
        var event = new Event(TransferSagaEventType.DEBIT_ESCROW, UUID.randomUUID(), payload);

        var escrow = new Escrow();
        escrow.setAmount(BigDecimal.valueOf(500));
        when(escrowRepo.findById(escrowID)).thenReturn(Optional.of(escrow));

        orchestrator.handler(event);

        // Verify that the debited repo is NOT saved
        verify(escrowRepo, never()).save(any(Escrow.class));
        // Verify that the saga event is saved
        verify(sagaEventRepo).save(any(SagaEvent.class));
        // Verify that debit escrow event was called
        verify(eventBus).addEvent(argThat(e ->
                e.getEventTypeId() == TransferSagaEventType.DEBIT_ESCROW_FAILED
        ));
    }

    @Test
    public void testCompensateSeller_Success() {
        var escrowID = UUID.randomUUID();
        var sellerID = UUID.randomUUID();
        var payload = new TransferPayload(escrowID, sellerID, BigDecimal.valueOf(1000));
        var event = new Event(TransferSagaEventType.SELLER_COMPENSATE, UUID.randomUUID(), payload);

        var seller = new Seller();
        seller.setBalance(BigDecimal.valueOf(0));
        when(sellerRepo.findById(sellerID)).thenReturn(Optional.of(seller));

        orchestrator.handler(event);

        // Verify that the seller is saved with updated balance
        var captor = ArgumentCaptor.forClass(Seller.class);
        verify(sellerRepo).save(captor.capture());
        var savedSeller = captor.getValue();
        assertEquals(BigDecimal.valueOf(1000), savedSeller.getBalance());

        // Verify that the saga event is saved
        verify(sagaEventRepo).save(any(SagaEvent.class));
        // Verify that debit escrow event was called
        verify(eventBus).addEvent(argThat(e ->
                e.getEventTypeId() == TransferSagaEventType.COMPLETE_TRANSFER
        ));
    }

    @Test
    public void testCompensateSeller_SellerNotFound() {
        var escrowID = UUID.randomUUID();
        var sellerID = UUID.randomUUID();
        var payload = new TransferPayload(escrowID, sellerID, BigDecimal.valueOf(1000));
        var event = new Event(TransferSagaEventType.SELLER_COMPENSATE, UUID.randomUUID(), payload);

        when(sellerRepo.findById(sellerID)).thenReturn(Optional.empty());

        orchestrator.handler(event);

        // Verify that the seller is saved with updated balance
        verify(sellerRepo, never()).save(any(Seller.class));
        // Verify that the saga event is saved
        verify(sagaEventRepo).save(any(SagaEvent.class));
        // Verify that debit escrow event was called
        verify(eventBus).addEvent(argThat(e ->
                e.getEventTypeId() == TransferSagaEventType.SELLER_COMPENSATE_FAILED
        ));
    }

    @Test
    public void testCompensateSellerFailed() {
        var escrowID = UUID.randomUUID();
        var sellerID = UUID.randomUUID();
        var payload = new TransferPayload(escrowID, sellerID, BigDecimal.valueOf(1000));
        var event = new Event(TransferSagaEventType.SELLER_COMPENSATE_FAILED, UUID.randomUUID(), payload);

        var escrow = new Escrow();
        escrow.setAmount(BigDecimal.valueOf(0));
        when(escrowRepo.findById(escrowID)).thenReturn(Optional.of(escrow));

        orchestrator.handler(event);

        // Verify that the escrow is recompensated
        var captor = ArgumentCaptor.forClass(Escrow.class);
        verify(escrowRepo).save(captor.capture());
        var savedEscrow = captor.getValue();
        assertEquals(BigDecimal.valueOf(1000), savedEscrow.getAmount());

        // Verify that the saga event is saved
        verify(sagaEventRepo).save(any(SagaEvent.class));
    }
}
