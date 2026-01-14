package com.springtest.distributedtransfercoordinator.services;

import com.springtest.distributedtransfercoordinator.DTO.TransferPayload;
import com.springtest.distributedtransfercoordinator.core.event.Event;
import com.springtest.distributedtransfercoordinator.core.event.EventBus;
import com.springtest.distributedtransfercoordinator.db.escrow.models.Escrow;
import com.springtest.distributedtransfercoordinator.db.escrow.repos.EscrowRepo;
import com.springtest.distributedtransfercoordinator.db.seller.models.Seller;
import com.springtest.distributedtransfercoordinator.db.seller.repos.SellerRepo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class TransferService {
    final EscrowRepo escrowRepo;
    final SellerRepo sellerRepo;
    final EventBus eventBus;

    public TransferService(EscrowRepo escrowRepo, SellerRepo sellerRepo, EventBus eventBus) {
        this.escrowRepo = escrowRepo;
        this.sellerRepo = sellerRepo;
        this.eventBus = eventBus;
    }

    public List<Seller> getSellers() {
        return sellerRepo.findAll();
    }

    public List<Escrow> getEscrows() {
        return escrowRepo.findAll();
    }

    public void initiateTransfer(UUID escrowID, UUID sellerID, BigDecimal amount) {
        var payload = new TransferPayload(escrowID, sellerID, amount);
        var event = new Event(TransferSagaEventType.TRANSFER_REQUESTED, UUID.randomUUID(), payload);
        this.eventBus.addEvent(event);
    }
}
