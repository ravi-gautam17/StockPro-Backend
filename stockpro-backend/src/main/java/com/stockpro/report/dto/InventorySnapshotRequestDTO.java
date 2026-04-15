package com.stockpro.report.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class InventorySnapshotRequestDTO {

    private LocalDate fromDate;
    private LocalDate toDate;


}