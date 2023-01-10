package com.cross.chain.payments.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransferState {

    NASCENT("NASCENT", false),
    INITIATED("INITIATED", false),
    PENDING_STEP_ONE("PENDING_STEP_ONE", false),
    ORIGINATOR_INFO_RECEIVED("ORIGINATOR_INFO_RECEIVED", false),
    PENDING_STEP_TWO("PENDING_STEP_TWO", false),
    BENEFICIARY_INFO_RECEIVED("BENEFICIARY_INFO_RECEIVED", false),
//    PENDING_STEP_ONE_FAILED("PENDING_STEP_ONE_FAILED", true),
//    PENDING_STEP_TWO_FAILED("PENDING_STEP_TWO_FAILED", true),
    TRANSFER_FAILED("TRANSFER_FAILED", true),
    TRANSFER_COMPLETED("COMPLETED", false);

    private final String name;
    private final boolean failedState;

}