package com.stockpro.purchase.mapper;

import com.stockpro.purchase.domain.*;
import com.stockpro.purchase.dto.*;

import java.util.stream.Collectors;

public class PurchaseOrderMapper {

    public static PurchaseOrderDto toDto(PurchaseOrder po) {
        PurchaseOrderDto dto = new PurchaseOrderDto();

        dto.setId(po.getId());
        dto.setSupplierId(po.getSupplier().getId());
        dto.setSupplierName(po.getSupplier().getName());

        dto.setWarehouseId(po.getWarehouse().getId());
        dto.setWarehouseName(po.getWarehouse().getName());

        dto.setStatus(po.getStatus().name());
        dto.setTotalAmount(po.getTotalAmount());

        dto.setOrderDate(po.getOrderDate());
        dto.setExpectedDate(po.getExpectedDate());
        dto.setReceivedDate(po.getReceivedDate());

        dto.setNotes(po.getNotes());

        dto.setLineItems(
                po.getLineItems().stream()
                        .map(PurchaseOrderMapper::toLineDto)
                        .collect(Collectors.toList())
        );

        return dto;
    }

    public static POLineItemDto toLineDto(POLineItem li) {
        POLineItemDto dto = new POLineItemDto();

        dto.setId(li.getId());
        dto.setProductId(li.getProduct().getId());
        dto.setProductName(li.getProduct().getName());

        dto.setQuantity(li.getQuantity());
        dto.setReceivedQty(li.getReceivedQty());

        dto.setUnitCost(li.getUnitCost());
        dto.setTotalCost(li.getTotalCost());

        return dto;
    }
}