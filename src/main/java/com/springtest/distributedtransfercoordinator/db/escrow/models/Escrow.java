package com.springtest.distributedtransfercoordinator.db.escrow.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class Escrow {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private BigDecimal amount;
    @Column(name = "buyer_id")
    private UUID buyerId;
    @Enumerated(EnumType.STRING)
    private EscrowStatus status;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public UUID getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(UUID buyerId) {
        this.buyerId = buyerId;
    }

    public EscrowStatus getStatus() {
        return status;
    }

    public void setStatus(EscrowStatus status) {
        this.status = status;
    }

    public void debitAmount(BigDecimal a) {
        if (a.compareTo(amount) > 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        amount = amount.subtract(a);
    }

    public void compensateCredit(BigDecimal a) {
        amount = amount.add(a);
    }
}