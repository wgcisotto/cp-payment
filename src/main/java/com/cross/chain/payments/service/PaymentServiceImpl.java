package com.cross.chain.payments.service;

import com.cross.chain.payments.config.PaymentStateChangeInterceptor;
import com.cross.chain.payments.model.TransferEvent;
import com.cross.chain.payments.model.TransferState;
import com.cross.chain.payments.persistence.Transfer;
import com.cross.chain.payments.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

    public static final String PAYMENT_ID_HEADER = "payment_id";
    private final PaymentRepository paymentRepository;
    private final StateMachineFactory<TransferState, TransferEvent> stateMachineFactory;
    private final PaymentStateChangeInterceptor paymentStateChangeInterceptor;

    @Transactional
    @Override
    public Transfer transferInitiate(Transfer transfer) {
        Transfer transferSaved = paymentRepository.save(transfer);
        StateMachine<TransferState, TransferEvent> stateMachine = build(transferSaved.getId());
        sendEvent(transferSaved.getId(), stateMachine, TransferEvent.RECEIVED_TRANSFER);
        return transferSaved;
    }

    @Transactional
    @Override
    public StateMachine<TransferState, TransferEvent> updateOriginatorData(UUID paymentId) {
        StateMachine<TransferState, TransferEvent> stateMachine = build(paymentId);
        sendEvent(paymentId, stateMachine, TransferEvent.RECEIVED_ORIGINATOR_INFO);
        return stateMachine;
    }


    @Transactional
    @Override
    public StateMachine<TransferState, TransferEvent> updateBeneficiaryData(UUID paymentId) {
        StateMachine<TransferState, TransferEvent> stateMachine = build(paymentId);
        sendEvent(paymentId, stateMachine, TransferEvent.RECEIVED_BENEFICIARY_INFO);
        return stateMachine;
    }

    @Transactional
    @Override
    public StateMachine<TransferState, TransferEvent> transfer(UUID paymentId) {
        StateMachine<TransferState, TransferEvent> stateMachine = build(paymentId);
        //sendEvent(paymentId, stateMachine, TransferEvent.ORIGINATOR_REQUEST);
        return stateMachine;
    }

    private void sendEvent(UUID paymentId, StateMachine<TransferState, TransferEvent> stateMachine, TransferEvent event) {
        Message msg = MessageBuilder.withPayload(event)
                .setHeader(PAYMENT_ID_HEADER, paymentId)
                .build();
        stateMachine.sendEvent(msg);

    }

    private StateMachine<TransferState, TransferEvent> build(UUID paymentId) {
        Transfer transfer = paymentRepository.getReferenceById(paymentId);
        StateMachine<TransferState, TransferEvent> stateMachine = stateMachineFactory.getStateMachine(transfer.getId());
        stateMachine.stop();
        stateMachine.getStateMachineAccessor()
                .doWithAllRegions(sm -> {
                    sm.addStateMachineInterceptor(paymentStateChangeInterceptor);
                    sm.resetStateMachine(new DefaultStateMachineContext<>(transfer.getState(), null, null, null));
                });
        stateMachine.start();
        return stateMachine;
    }

}
