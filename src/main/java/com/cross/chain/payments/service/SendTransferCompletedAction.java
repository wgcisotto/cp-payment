package com.cross.chain.payments.service;

import com.cross.chain.payments.model.TransferEvent;
import com.cross.chain.payments.model.TransferState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class SendTransferCompletedAction implements Action<TransferState, TransferEvent> {
    @Override
    public void execute(StateContext<TransferState, TransferEvent> context) {
        UUID paymentId = (UUID) context.getExtendedState().getVariables().get("TRANSFER_REQUEST_ID");
        System.out.println("Notification completed for PaymentID" + paymentId);
    }
}
