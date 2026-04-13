package com.stockpro.alert.service;

import com.stockpro.alert.domain.Alert;
import com.stockpro.alert.domain.AlertSeverity;
import com.stockpro.alert.domain.AlertType;
import com.stockpro.alert.repository.AlertRepository;
import com.stockpro.auth.domain.UserRole;
import com.stockpro.auth.repository.UserRepository;
import com.stockpro.product.domain.Product;
import com.stockpro.warehouse.domain.Warehouse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumSet;

/**
 * In-app alerts + optional email for CRITICAL (JavaMailSender; configure SMTP in application.yml for prod).
 */
@Service
public class AlertService {

    private static final Logger log = LoggerFactory.getLogger(AlertService.class);

    private final AlertRepository alertRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    public AlertService(AlertRepository alertRepository,
                        UserRepository userRepository,
                        JavaMailSender mailSender) {
        this.alertRepository = alertRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    /**
     * Called after stock changes to create low/over stock notifications for Managers and Admins.
     */
    @Transactional
    public void evaluateThresholds(Product product, Warehouse warehouse, int newQty) {
        var recipients = userRepository.findByRoleInAndActiveTrue(
                EnumSet.of(UserRole.MANAGER, UserRole.ADMIN));

        if (newQty < product.getReorderLevel()) {
            for (var user : recipients) {
                Alert a = new Alert();
                a.setRecipient(user);
                a.setAlertType(AlertType.LOW_STOCK);
                a.setSeverity(AlertSeverity.CRITICAL);
                a.setTitle("Low stock: " + product.getName());
                a.setMessage("SKU " + product.getSku() + " at " + warehouse.getName()
                        + " is below reorder level (" + newQty + " < " + product.getReorderLevel() + ").");
                a.setRelatedProductId(product.getId());
                a.setRelatedWarehouseId(warehouse.getId());
                a.setChannel("IN_APP");
                alertRepository.save(a);
                dispatchEmailIfCritical(a, user.getEmail());
            }
        }

        if (product.getMaxStockLevel() > 0 && newQty > product.getMaxStockLevel()) {
            for (var user : recipients) {
                Alert a = new Alert();
                a.setRecipient(user);
                a.setAlertType(AlertType.OVERSTOCK);
                a.setSeverity(AlertSeverity.WARNING);
                a.setTitle("Overstock: " + product.getName());
                a.setMessage("SKU " + product.getSku() + " at " + warehouse.getName()
                        + " exceeds maximum level (" + newQty + " > " + product.getMaxStockLevel() + ").");
                a.setRelatedProductId(product.getId());
                a.setRelatedWarehouseId(warehouse.getId());
                a.setChannel("IN_APP");
                alertRepository.save(a);
            }
        }
    }

    @Transactional
    public void notifyOverdueReceipt(Long poId, String reference, LocalDate asOf) {
        var recipients = userRepository.findByRoleInAndActiveTrue(
                EnumSet.of(UserRole.MANAGER, UserRole.ADMIN, UserRole.OFFICER));
        String ref = reference != null ? reference : "#" + poId;
        for (var user : recipients) {
            Alert a = new Alert();
            a.setRecipient(user);
            a.setAlertType(AlertType.OVERDUE_RECEIPT);
            a.setSeverity(AlertSeverity.WARNING);
            a.setTitle("Overdue PO receipt");
            a.setMessage("PO " + ref + " — expected date before " + asOf + "; complete GRN or update dates.");
            a.setRelatedPoId(poId);
            a.setChannel("IN_APP");
            alertRepository.save(a);
        }
    }

    @Transactional
    public void notifyPoPendingApprovers(Long poId, String reference) {
        var recipients = userRepository.findByRoleInAndActiveTrue(
                EnumSet.of(UserRole.MANAGER, UserRole.ADMIN));
        for (var user : recipients) {
            Alert a = new Alert();
            a.setRecipient(user);
            a.setAlertType(AlertType.PO_PENDING);
            a.setSeverity(AlertSeverity.INFO);
            a.setTitle("Purchase order pending approval");
            a.setMessage("PO " + reference + " requires approval.");
            a.setRelatedPoId(poId);
            a.setChannel("IN_APP");
            alertRepository.save(a);
        }
    }

    private void dispatchEmailIfCritical(Alert alert, String email) {
        if (alert.getSeverity() != AlertSeverity.CRITICAL || email == null || email.isBlank()) {
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(email);
            msg.setSubject("[StockPro] " + alert.getTitle());
            msg.setText(alert.getMessage());
            mailSender.send(msg);
        } catch (Exception ex) {
            log.warn("Email dispatch failed (check spring.mail): {}", ex.getMessage());
        }
    }
}
