package com.stockpro.report.web;

import com.stockpro.report.dto.InventorySnapshotRequestDTO;
import com.stockpro.report.dto.InventorySnapshotResponseDTO;
import com.stockpro.report.dto.ValuationResponseDTO;
import com.stockpro.report.service.ReportService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@SecurityRequirement(name = "bearer")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/valuation")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ValuationResponseDTO valuation() {
        return reportService.totalInventoryValuation();
    }

    @PostMapping("/snapshots")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public List<InventorySnapshotResponseDTO> getSnapshots(
            @RequestBody InventorySnapshotRequestDTO request
    ) {
        return reportService.getSnapshots(request);
    }
}