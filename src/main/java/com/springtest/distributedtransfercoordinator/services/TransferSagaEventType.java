package com.springtest.distributedtransfercoordinator.services;

public enum TransferSagaEventType {
    TRANSFER_REQUESTED,
    TRANSFER_REQUESTED_FAILED,
    DEBIT_ESCROW,
    DEBIT_ESCROW_FAILED,
    SELLER_COMPENSATE,
    SELLER_COMPENSATE_FAILED,
    COMPLETE_TRANSFER,
    COMPLETE_TRANSFER_FAILED
}
