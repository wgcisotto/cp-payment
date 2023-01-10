package com.cross.chain.payments.config;

import com.cross.chain.payments.model.TransferEvent;
import com.cross.chain.payments.model.TransferState;
import com.cross.chain.payments.persistence.Transfer;
import com.cross.chain.payments.repository.PaymentRepository;
import com.cross.chain.payments.service.PaymentServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentStateChangeInterceptor extends StateMachineInterceptorAdapter<TransferState, TransferEvent> {

    private final PaymentRepository paymentRepository;

    @Override
    public void preStateChange(State<TransferState, TransferEvent> state, Message<TransferEvent> message,
                               Transition<TransferState, TransferEvent> transition, StateMachine<TransferState, TransferEvent> stateMachine, StateMachine<TransferState, TransferEvent> rootStateMachine) {
        //System.out.println("interceptor");
        Optional.ofNullable(message).flatMap(msg -> Optional.ofNullable((UUID) msg.getHeaders().getOrDefault(PaymentServiceImpl.PAYMENT_ID_HEADER, -1L))).ifPresent(paymentId -> {
            Transfer transfer = paymentRepository.getReferenceById(paymentId);
            stateMachine.getExtendedState().getVariables().put("TRANSFER_REQUEST_ID", paymentId);
            transfer.setState(state.getId());
            paymentRepository.save(transfer);
        });
    }
}
