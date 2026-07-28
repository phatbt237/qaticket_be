package com.qms.qms.dto.ticket;

import com.qms.qms.entity.QaTicketSpecImage;
import com.qms.qms.entity.enums.SpecImageType;

import java.time.LocalDateTime;

public record QaTicketSpecImageResponse(Long id, SpecImageType type, String imageUrl, LocalDateTime uploadedAt) {
    public static QaTicketSpecImageResponse from(QaTicketSpecImage image) {
        return new QaTicketSpecImageResponse(image.getId(), image.getType(), image.getImageUrl(), image.getUploadedAt());
    }
}
