package com.qms.qms.dto.ticket;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record QaTicketDefectLocationRequest(
        Long garmentLocationId,
        @NotBlank String locationText,
        @NotNull @Min(1) Integer quantity,
        List<@NotBlank String> images
) {
}
