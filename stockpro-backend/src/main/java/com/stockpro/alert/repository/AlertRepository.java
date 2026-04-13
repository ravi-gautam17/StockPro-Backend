package com.stockpro.alert.repository;

import com.stockpro.alert.domain.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByRecipient_IdOrderByCreatedAtDesc(Long recipientId);

    long countByRecipient_IdAndAcknowledgedFalse(Long recipientId);
}
