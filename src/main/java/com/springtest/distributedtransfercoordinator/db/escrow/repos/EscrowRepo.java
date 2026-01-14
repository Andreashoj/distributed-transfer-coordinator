package com.springtest.distributedtransfercoordinator.db.escrow.repos;

import com.springtest.distributedtransfercoordinator.db.escrow.models.Escrow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EscrowRepo extends JpaRepository<Escrow, UUID> {
}
