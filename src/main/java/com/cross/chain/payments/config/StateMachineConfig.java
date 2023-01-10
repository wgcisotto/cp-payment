package com.cross.chain.payments.config;

import com.cross.chain.payments.model.TransferEvent;
import com.cross.chain.payments.model.TransferState;
import com.cross.chain.payments.service.SendTransferCompletedAction;
import com.cross.chain.payments.service.NotifyBeneficiaryAction;
import com.cross.chain.payments.service.NotifyOriginatorAction;
import com.cross.chain.payments.service.ExecuteTravelerTransferAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<TransferState, TransferEvent> {

    private final NotifyOriginatorAction notifyOriginatorAction;
    private final NotifyBeneficiaryAction notifyBeneficiaryAction;
    private final ExecuteTravelerTransferAction executeTravelerTransferAction;
    private final SendTransferCompletedAction sendTransferCompletedAction;

    @Override
    public void configure(StateMachineStateConfigurer<TransferState, TransferEvent> states) throws Exception {
        states.withStates()
                .initial(TransferState.NASCENT) //NASCENT
                .state(TransferState.INITIATED, notifyOriginatorAction)
                .state(TransferState.ORIGINATOR_INFO_RECEIVED, notifyBeneficiaryAction)
                .state(TransferState.BENEFICIARY_INFO_RECEIVED, executeTravelerTransferAction)
                .state(TransferState.TRANSFER_COMPLETED, sendTransferCompletedAction)
                .states(EnumSet.allOf(TransferState.class))
                //.states(Set.of(PaymentState.values()))
                .end(TransferState.TRANSFER_COMPLETED)
                .end(TransferState.TRANSFER_FAILED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<TransferState, TransferEvent> transitions) throws Exception {
        transitions
                .withExternal().source(TransferState.NASCENT).target(TransferState.INITIATED).event(TransferEvent.RECEIVED_TRANSFER)
                .and()
                .withExternal().source(TransferState.NASCENT).target(TransferState.TRANSFER_FAILED).event(TransferEvent.NOTIFY_ORIGINATOR_FAILED)
                .and()
                .withExternal().source(TransferState.INITIATED).target(TransferState.ORIGINATOR_INFO_RECEIVED).event(TransferEvent.RECEIVED_ORIGINATOR_INFO)
                .and()
                .withExternal().source(TransferState.INITIATED).target(TransferState.TRANSFER_FAILED).event(TransferEvent.NOTIFY_BENEFICIARY_FAILED)
                .and()
                .withExternal().source(TransferState.ORIGINATOR_INFO_RECEIVED).target(TransferState.BENEFICIARY_INFO_RECEIVED).event(TransferEvent.RECEIVED_BENEFICIARY_INFO)
                .and()
                .withExternal().source(TransferState.ORIGINATOR_INFO_RECEIVED).target(TransferState.TRANSFER_FAILED).event(TransferEvent.TRAVELER_TRANSFER_FAILED)
                .and()
                .withExternal().source(TransferState.BENEFICIARY_INFO_RECEIVED).target(TransferState.TRANSFER_COMPLETED).event(TransferEvent.TRAVELER_TRANSFER_SUCCESS);

    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<TransferState, TransferEvent> config) throws Exception {
        //TODO: move to an appropriated clas
        StateMachineListenerAdapter<TransferState, TransferEvent> adapter = new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<TransferState, TransferEvent> from, State<TransferState, TransferEvent> to) {
                System.out.printf("stateChanged(from: %s, to: %s)%n", from != null ? from.getId() : null, to.getId());
            }
        };
        config.withConfiguration()
                .listener(adapter);
    }

}
