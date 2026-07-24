package com.qms.qms.dto.ticket;

import com.qms.qms.entity.QaTicketDefectImage;

import java.time.LocalDateTime;

public record QaTicketDefectImageResponse(Long id, String imageUrl, LocalDateTime uploadedAt) {
    public static QaTicketDefectImageResponse from(QaTicketDefectImage image) {
        return new QaTicketDefectImageResponse(image.getId(), image.getImageUrl(), image.getUploadedAt());
    }
}
