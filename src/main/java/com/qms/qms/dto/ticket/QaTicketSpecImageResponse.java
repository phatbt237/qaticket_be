package com.qms.qms.dto.ticket;

import com.qms.qms.entity.QaTicketSpecImage;

import java.time.LocalDateTime;

public record QaTicketSpecImageResponse(Long id, String imageUrl, LocalDateTime uploadedAt) {
    public static QaTicketSpecImageResponse from(QaTicketSpecImage image) {
        return new QaTicketSpecImageResponse(image.getId(), image.getImageUrl(), image.getUploadedAt());
    }
}
