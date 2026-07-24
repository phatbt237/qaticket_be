package com.qms.qms.dto.po;

import com.qms.qms.entity.PurchaseOrder;

import java.time.LocalDate;

public record PurchaseOrderResponse(
        Long id,
        Long styleId,
        String styleCode,
        Long customerId,
        String customerName,
        String poCode,
        Integer poQuantity,
        LocalDate dateStart,
        LocalDate dateShipment
) {
    public static PurchaseOrderResponse from(PurchaseOrder po) {
        return new PurchaseOrderResponse(
                po.getId(),
                po.getStyle() != null ? po.getStyle().getId() : null,
                po.getStyle() != null ? po.getStyle().getCode() : null,
                po.getCustomer().getId(),
                po.getCustomer().getName(),
                po.getPoCode(),
                po.getPoQuantity(),
                po.getDateStart(),
                po.getDateShipment()
        );
    }
}
