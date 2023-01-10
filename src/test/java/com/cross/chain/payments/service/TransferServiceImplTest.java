package com.cross.chain.payments.service;

import com.cross.chain.payments.persistence.Transfer;
import com.cross.chain.payments.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
class TransferServiceImplTest {

    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentRepository paymentRepository;

    Transfer transfer;

    @BeforeEach
    void setUp() {
        transfer = Transfer.builder().amount(new BigDecimal("10.99")).build();
    }

    @Transactional
    @Test
    void initiated() throws InterruptedException {
        Transfer savedTransfer = paymentService.transferInitiate(transfer);
        Transfer transferInitialState = paymentRepository.getReferenceById(savedTransfer.getId());
        System.out.println(transferInitialState.getState().getName());
        System.out.println("PaymentID: " + savedTransfer.getId());

        System.out.println("-----");
        System.out.println("Received Put from Originator");
        paymentService.updateOriginatorData(savedTransfer.getId());
        Transfer pendingStepOne = paymentRepository.getReferenceById(savedTransfer.getId());
        System.out.println(pendingStepOne.getState().getName());

        System.out.println("-----");
        System.out.println("RECEIVED Put from Beneficiary");
        paymentService.updateBeneficiaryData(savedTransfer.getId());
        Transfer stepOneCompleted = paymentRepository.getReferenceById(savedTransfer.getId());
        System.out.println(stepOneCompleted.getState().getName());

        System.out.println("-----");

        Thread.sleep(10000);
//        Payment completed = paymentRepository.getOne(savedPayment.getId());
//        System.out.println(completed.getState().getName());
//        System.out.println(msCompleted.isComplete());
    }


}