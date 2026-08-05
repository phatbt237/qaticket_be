package com.qms.qms.dto.admin;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateDefectItemRequest(
        @NotNull Long defectId,
        @Size(max = 50) String code,
        @Size(max = 150) String nameEn,
        @NotNull @Size(max = 150) String nameVi,
        Boolean allowMinor,
        Boolean allowMajor
) {
}
