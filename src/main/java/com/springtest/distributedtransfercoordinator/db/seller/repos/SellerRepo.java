package com.springtest.distributedtransfercoordinator.db.seller.repos;

import com.springtest.distributedtransfercoordinator.db.seller.models.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SellerRepo extends JpaRepository<Seller, UUID> {
}
