package com.qms.qms.dto.ticket;

import com.qms.qms.entity.QaTicketMeasurementImage;

import java.time.LocalDateTime;

public record QaTicketMeasurementImageResponse(Long id, String imageUrl, LocalDateTime uploadedAt) {
    public static QaTicketMeasurementImageResponse from(QaTicketMeasurementImage image) {
        return new QaTicketMeasurementImageResponse(image.getId(), image.getImageUrl(), image.getUploadedAt());
    }
}
