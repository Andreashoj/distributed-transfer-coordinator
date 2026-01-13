package com.springtest.distributedtransfercoordinator.db.escrow;

import com.springtest.distributedtransfercoordinator.db.escrow.models.Escrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EscrowRepo extends JpaRepository<Escrow, UUID> {
}
