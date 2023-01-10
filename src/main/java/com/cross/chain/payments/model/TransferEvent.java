package com.cross.chain.payments.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransferEvent {

    RECEIVED_TRANSFER("RECEIVED_TRANSFER"),
    RECEIVED_ORIGINATOR_INFO("RECEIVED_ORIGINATOR_INFO"),
    STEP_ONE_SUCCESS("STEP_ONE_SUCCESS"),
    NOTIFY_ORIGINATOR_FAILED("NOTIFY_ORIGINATOR_FAILED"),

    ORIGINATOR_REQUEST("INITIATION_REQUEST"),
    RECEIVED_BENEFICIARY_INFO("RECEIVED_BENEFICIARY_INFO"),
    NOTIFY_BENEFICIARY_FAILED("NOTIFY_BENEFICIARY_FAILED"),
    TRAVELER_TRANSFER_SUCCESS("TRAVELER_TRANSFER_SUCCESS"),

    TRAVELER_TRANSFER_FAILED("TRAVELER_TRANSFER_FAILED"),
    SUCCESS("SUCCESS");

    private final String name;

}
