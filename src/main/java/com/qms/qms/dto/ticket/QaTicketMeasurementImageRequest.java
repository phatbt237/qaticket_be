package com.qms.qms.dto.ticket;

import jakarta.validation.constraints.NotBlank;

public record QaTicketMeasurementImageRequest(
        @NotBlank String imageUrl
) {
}
