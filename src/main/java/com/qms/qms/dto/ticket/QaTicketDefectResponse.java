package com.qms.qms.dto.ticket;

import com.qms.qms.dto.RefResponse;
import com.qms.qms.entity.QaTicketDefect;

import java.util.List;

public record QaTicketDefectResponse(
        Long id,
        RefResponse defect,
        String note,
        List<QaTicketDefectLocationResponse> locations
) {
    public static QaTicketDefectResponse from(QaTicketDefect defect) {
        RefResponse defectRef = new RefResponse(defect.getDefect().getId(), defect.getDefect().getNameVi());
        return new QaTicketDefectResponse(
                defect.getId(),
                defectRef,
                defect.getNote(),
                defect.getLocations().stream().map(QaTicketDefectLocationResponse::from).toList()
        );
    }
}
