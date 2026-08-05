package com.qms.qms.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateGarmentLocationRequest(
        @NotNull Long garmentTypeId,
        @NotBlank @Size(max = 100) String name
) {
}
