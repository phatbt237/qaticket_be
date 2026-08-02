package com.qms.qms.dto.admin;

import com.qms.qms.entity.enums.StaffRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateStaffRequest(
        @NotBlank @Size(max = 50) String code,
        @NotBlank @Size(max = 150) String fullName,
        @NotBlank @Size(min = 6, max = 100) String password,
        @NotNull StaffRole role
) {
}
