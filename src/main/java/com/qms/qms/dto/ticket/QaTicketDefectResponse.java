package com.qms.qms.dto.ticket;

import com.qms.qms.dto.RefResponse;
import com.qms.qms.entity.DefectItem;
import com.qms.qms.entity.QaTicketDefect;

import java.util.List;

public record QaTicketDefectResponse(
        Long id,
        RefResponse defect,
        RefResponse defectItem,
        String severity,
        String note,
        List<QaTicketDefectLocationResponse> locations
) {
    public static QaTicketDefectResponse from(QaTicketDefect defect) {
        DefectItem item = defect.getDefectItem();
        RefResponse defectRef = new RefResponse(item.getDefect().getId(), item.getDefect().getNameVi());
        RefResponse defectItemRef = new RefResponse(item.getId(), item.getNameVi());
        return new QaTicketDefectResponse(
                defect.getId(),
                defectRef,
                defectItemRef,
                defect.getSeverity().name(),
                defect.getNote(),
                defect.getLocations().stream().map(QaTicketDefectLocationResponse::from).toList()
        );
    }
}
