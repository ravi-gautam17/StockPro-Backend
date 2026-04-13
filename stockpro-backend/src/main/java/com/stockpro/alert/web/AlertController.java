package com.stockpro.alert.web;

import com.stockpro.alert.dto.AlertResponseDTO;
import com.stockpro.alert.mapper.AlertMapper;
import com.stockpro.alert.repository.AlertRepository;
import com.stockpro.auth.service.CurrentUserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alerts")
@SecurityRequirement(name = "bearer")
public class AlertController {

    private final AlertRepository alertRepository;
    private final CurrentUserService currentUserService;

    public AlertController(AlertRepository alertRepository,
                           CurrentUserService currentUserService) {
        this.alertRepository = alertRepository;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/mine")
    public List<AlertResponseDTO> mine() {
        long uid = currentUserService.requireCurrentUser().getId();

        return alertRepository.findByRecipient_IdOrderByCreatedAtDesc(uid)
                .stream()
                .map(AlertMapper::toDTO)
                .toList();
    }

    @PostMapping("/{id}/acknowledge")
    @Transactional
    public void acknowledge(@PathVariable Long id) {
        long uid = currentUserService.requireCurrentUser().getId();

        var alert = alertRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found"));

        if (!alert.getRecipient().getId().equals(uid)) {
            throw new IllegalStateException("Not your alert");
        }

        alert.setAcknowledged(true);
        alert.setOpened(true);
    }

    @GetMapping("/unacknowledged-count")
    public long unacknowledgedCount() {
        long uid = currentUserService.requireCurrentUser().getId();
        return alertRepository.countByRecipient_IdAndAcknowledgedFalse(uid);
    }
}