package com.qms.qms.dto.ticket;

import com.qms.qms.dto.RefResponse;
import com.qms.qms.entity.QaTicket;
import com.qms.qms.entity.enums.InspectionStage;
import com.qms.qms.entity.enums.TicketStatus;

import java.time.LocalDateTime;
import java.util.List;

public record QaTicketResponse(
        Long id,
        String ticketCode,
        RefResponse staff,
        RefResponse factory,
        RefResponse line,
        RefResponse group,
        RefResponse purchaseOrder,
        RefResponse style,
        RefResponse customer,
        RefResponse garmentType,
        InspectionStage inspectionStage,
        Integer inspectedQty,
        TicketStatus status,
        boolean exported,
        LocalDateTime exportedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<QaTicketDefectResponse> defects,
        List<QaTicketSpecImageResponse> specImages
) {
    public static QaTicketResponse from(QaTicket t) {
        return new QaTicketResponse(
                t.getId(),
                t.getTicketCode(),
                new RefResponse(t.getStaff().getId(), t.getStaff().getFullName()),
                new RefResponse(t.getFactory().getId(), t.getFactory().getName()),
                new RefResponse(t.getLine().getId(), t.getLine().getName()),
                t.getGroup() != null ? new RefResponse(t.getGroup().getId(), t.getGroup().getName()) : null,
                t.getPurchaseOrder() != null ? new RefResponse(t.getPurchaseOrder().getId(), t.getPurchaseOrder().getPoCode()) : null,
                t.getPurchaseOrder() != null && t.getPurchaseOrder().getStyle() != null
                        ? new RefResponse(t.getPurchaseOrder().getStyle().getId(), t.getPurchaseOrder().getStyle().getCode())
                        : null,
                new RefResponse(t.getCustomer().getId(), t.getCustomer().getName()),
                new RefResponse(t.getGarmentType().getId(), t.getGarmentType().getName()),
                t.getInspectionStage(),
                t.getInspectedQty(),
                t.getStatus(),
                t.isExported(),
                t.getExportedAt(),
                t.getCreatedAt(),
                t.getUpdatedAt(),
                t.getDefects().stream().map(QaTicketDefectResponse::from).toList(),
                t.getSpecImages().stream().map(QaTicketSpecImageResponse::from).toList()
        );
    }
}
