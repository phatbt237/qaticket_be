package com.qms.qms.dto.auth;

import com.qms.qms.entity.enums.StaffLanguage;
import jakarta.validation.constraints.NotNull;

public record ChangeLanguageRequest(
        @NotNull StaffLanguage language
) {
}
