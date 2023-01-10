package com.cross.chain.payments.service;

import com.cross.chain.payments.model.TransferEvent;
import com.cross.chain.payments.model.TransferState;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class ExecuteTravelerTransferAction implements Action<TransferState, TransferEvent> {

    @Transactional
    @Override
    public void execute(StateContext<TransferState, TransferEvent> context) {
        UUID paymentId = (UUID) context.getExtendedState().getVariables().get("TRANSFER_REQUEST_ID");
        StateMachine<TransferState, TransferEvent> stateMachine = context.getStateMachine();
        System.out.println("Transfer Request");
        System.out.println("Success");
        stateMachine.sendEvent(MessageBuilder
                .withPayload(TransferEvent.TRAVELER_TRANSFER_SUCCESS)
                .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, paymentId)
                .build());
    }
}
