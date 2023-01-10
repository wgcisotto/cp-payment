package com.cross.chain.payments.repository;

import com.cross.chain.payments.persistence.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Transfer, UUID> {
}
