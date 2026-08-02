package com.qms.qms.security;

import com.qms.qms.entity.RefreshToken;
import com.qms.qms.entity.Staff;
import com.qms.qms.exception.InvalidRefreshTokenException;
import com.qms.qms.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshExpirationMs;
    private final SecureRandom random = new SecureRandom();

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                                @Value("${jwt.refresh-expiration-ms}") long refreshExpirationMs) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    @Transactional
    public RefreshToken issue(Staff staff) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setStaff(staff);
        refreshToken.setToken(generateSecureToken());
        refreshToken.setExpiresAt(LocalDateTime.now().plusNanos(refreshExpirationMs * 1_000_000));
        return refreshTokenRepository.save(refreshToken);
    }

    /** Validates the given token, revokes it, and issues a fresh one for the same staff (rotation). */
    @Transactional
    public RefreshToken rotate(String tokenValue) {
        RefreshToken existing = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));
        if (!existing.isValid()) {
            throw new InvalidRefreshTokenException("Refresh token expired or revoked");
        }
        existing.setRevoked(true);
        refreshTokenRepository.save(existing);
        return issue(existing.getStaff());
    }

    @Transactional
    public void revoke(String tokenValue) {
        refreshTokenRepository.findByToken(tokenValue).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
    }

    /** Revokes every still-valid refresh token for the staff, e.g. when an admin locks their account. */
    @Transactional
    public void revokeAllForStaff(Staff staff) {
        List<RefreshToken> tokens = refreshTokenRepository.findByStaffAndRevokedFalse(staff);
        tokens.forEach(rt -> rt.setRevoked(true));
        refreshTokenRepository.saveAll(tokens);
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[48];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
