package com.qms.qms.repository;

import com.qms.qms.entity.RefreshToken;
import com.qms.qms.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByStaffAndRevokedFalse(Staff staff);
}
