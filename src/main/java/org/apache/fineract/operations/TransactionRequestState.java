package org.apache.fineract.operations;

public enum TransactionRequestState {

    IN_PROGRESS,
    INITIATED,
    RECEIVED,
    ACCEPTED,
    REJECTED,
    FAILED,
    SUCCESS,
    REQUEST_ACCEPTED;
}