package com.qms.qms.dto.po;

import com.qms.qms.entity.PurchaseOrder;
import com.qms.qms.entity.Style;

import java.time.LocalDate;

public record PurchaseOrderResponse(
        Long id,
        Long styleId,
        String styleCode,
        String styleName,
        Long customerId,
        String customerName,
        String poCode,
        Integer poQuantity,
        LocalDate dateStart,
        LocalDate dateShipment
) {
    public static PurchaseOrderResponse from(PurchaseOrder po) {
        Style style = po.getStyle();
        return new PurchaseOrderResponse(
                po.getId(),
                style != null ? style.getId() : null,
                style != null ? style.getCode() : null,
                style != null ? style.getName() : null,
                po.getCustomer().getId(),
                po.getCustomer().getName(),
                po.getPoCode(),
                po.getPoQuantity(),
                po.getDateStart(),
                po.getDateShipment()
        );
    }
}
