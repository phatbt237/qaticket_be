package com.qms.qms.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank String oldPassword,
        @NotBlank @Size(min = 6, max = 100) String newPassword
) {
}
