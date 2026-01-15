package com.springtest.distributedtransfercoordinator.DTO;

import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.util.UUID;

@Embeddable
public class TransferPayload {
    private UUID escrowId;
    private UUID sellerId;
    private BigDecimal amount;

    public TransferPayload(UUID escrowId, UUID sellerId, BigDecimal amount) {
        this.escrowId = escrowId;
        this.sellerId = sellerId;
        this.amount = amount;
    }

    public TransferPayload() {
    }

    public UUID getEscrowId() {
        return escrowId;
    }

    public void setEscrowId(UUID escrowId) {
        this.escrowId = escrowId;
    }

    public UUID getSellerId() {
        return sellerId;
    }

    public void setSellerId(UUID sellerId) {
        this.sellerId = sellerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
