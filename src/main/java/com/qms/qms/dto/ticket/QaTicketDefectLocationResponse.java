package com.qms.qms.dto.ticket;

import com.qms.qms.dto.RefResponse;
import com.qms.qms.entity.QaTicketDefectLocation;

import java.util.List;

public record QaTicketDefectLocationResponse(
        Long id,
        RefResponse garmentLocation,
        String locationText,
        Integer quantity,
        List<QaTicketDefectImageResponse> images
) {
    public static QaTicketDefectLocationResponse from(QaTicketDefectLocation loc) {
        RefResponse garmentLocationRef = loc.getGarmentLocation() != null
                ? new RefResponse(loc.getGarmentLocation().getId(), loc.getGarmentLocation().getName())
                : null;
        return new QaTicketDefectLocationResponse(
                loc.getId(),
                garmentLocationRef,
                loc.getLocationText(),
                loc.getQuantity(),
                loc.getImages().stream().map(QaTicketDefectImageResponse::from).toList()
        );
    }
}
