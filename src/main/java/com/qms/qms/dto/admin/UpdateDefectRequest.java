package com.qms.qms.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateDefectRequest(
        @Size(max = 50) String code,
        @Size(max = 150) String nameEn,
        @NotBlank @Size(max = 150) String nameVi
) {
}
