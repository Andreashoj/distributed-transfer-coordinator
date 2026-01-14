package com.springtest.distributedtransfercoordinator.services;

public enum TransferSagaEventType {
    TRANSFER_REQUESTED,
    TRANSFER_REQUESTED_FAILED,
    DEBIT_ESCROW,
    DEBIT_ESCROW_FAILED,
}
