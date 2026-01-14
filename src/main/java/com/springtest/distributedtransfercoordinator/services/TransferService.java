package com.springtest.distributedtransfercoordinator.services;

import com.springtest.distributedtransfercoordinator.db.escrow.models.Escrow;
import com.springtest.distributedtransfercoordinator.db.escrow.repos.EscrowRepo;
import com.springtest.distributedtransfercoordinator.db.seller.models.Seller;
import com.springtest.distributedtransfercoordinator.db.seller.repos.SellerRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransferService {
    final EscrowRepo escrowRepo;
    final SellerRepo sellerRepo;

    public TransferService(EscrowRepo escrowRepo, SellerRepo sellerRepo) {
        this.escrowRepo = escrowRepo;
        this.sellerRepo = sellerRepo;
    }

    public List<Seller> getSellers() {
        return sellerRepo.findAll();
    }

    public List<Escrow> getEscrows() {
        return escrowRepo.findAll();
    }
}
