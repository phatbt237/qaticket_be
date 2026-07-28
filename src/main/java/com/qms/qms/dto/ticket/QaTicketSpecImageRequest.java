package com.qms.qms.dto.ticket;

import com.qms.qms.entity.enums.SpecImageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QaTicketSpecImageRequest(
        @NotNull SpecImageType type,
        @NotBlank String imageUrl
) {
}
