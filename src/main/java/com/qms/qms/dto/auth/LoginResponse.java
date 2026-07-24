package com.qms.qms.dto.auth;

import com.qms.qms.entity.enums.StaffRole;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInMs,
        Long staffId,
        String code,
        String fullName,
        StaffRole role
) {
}
