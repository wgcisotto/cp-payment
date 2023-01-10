package com.cross.chain.payments.service;

import com.cross.chain.payments.model.TransferEvent;
import com.cross.chain.payments.model.TransferState;
import com.cross.chain.payments.persistence.Transfer;
import org.springframework.statemachine.StateMachine;

import java.util.UUID;

public interface PaymentService {

    Transfer transferInitiate(Transfer transfer);

    StateMachine<TransferState, TransferEvent> updateOriginatorData(UUID transferId);

    StateMachine<TransferState, TransferEvent> updateBeneficiaryData(UUID transferId);

    StateMachine<TransferState, TransferEvent> transfer(UUID transferId);

}
