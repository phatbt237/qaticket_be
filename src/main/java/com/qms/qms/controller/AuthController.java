package com.qms.qms.controller;

import com.qms.qms.dto.auth.ChangeLanguageRequest;
import com.qms.qms.dto.auth.ChangePasswordRequest;
import com.qms.qms.dto.auth.LoginRequest;
import com.qms.qms.dto.auth.LoginResponse;
import com.qms.qms.dto.auth.RefreshRequest;
import com.qms.qms.dto.auth.ResetPasswordRequest;
import com.qms.qms.entity.RefreshToken;
import com.qms.qms.entity.enums.StaffRole;
import com.qms.qms.exception.ResourceNotFoundException;
import com.qms.qms.repository.StaffRepository;
import com.qms.qms.security.JwtService;
import com.qms.qms.security.RefreshTokenService;
import com.qms.qms.security.StaffPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
                           RefreshTokenService refreshTokenService, StaffRepository staffRepository,
                           PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.staffRepository = staffRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(request.username(), request.password());
        var authentication = authenticationManager.authenticate(authToken);
        var principal = (StaffPrincipal) authentication.getPrincipal();
        var staff = principal.getStaff();

        String accessToken = jwtService.generateToken(principal);
        RefreshToken refreshToken = refreshTokenService.issue(staff);

        return new LoginResponse(accessToken, refreshToken.getToken(), "Bearer", jwtService.getExpirationMs(),
                staff.getId(), staff.getCode(), staff.getFullName(), staff.getRole(), staff.getLanguage());
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(@Valid @RequestBody RefreshRequest request) {
        RefreshToken rotated = refreshTokenService.rotate(request.refreshToken());
        var staff = rotated.getStaff();

        String accessToken = jwtService.generateToken(staff);

        return new LoginResponse(accessToken, rotated.getToken(), "Bearer", jwtService.getExpirationMs(),
                staff.getId(), staff.getCode(), staff.getFullName(), staff.getRole(), staff.getLanguage());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest request) {
        refreshTokenService.revoke(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                                @AuthenticationPrincipal StaffPrincipal principal) {
        var staff = principal.getStaff();
        if (!passwordEncoder.matches(request.oldPassword(), staff.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu cũ không đúng");
        }
        staff.setPassword(passwordEncoder.encode(request.newPassword()));
        staffRepository.save(staff);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/language")
    public ResponseEntity<Void> changeLanguage(@Valid @RequestBody ChangeLanguageRequest request,
                                                @AuthenticationPrincipal StaffPrincipal principal) {
        var staff = principal.getStaff();
        staff.setLanguage(request.language());
        staffRepository.save(staff);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/staff/{staffId}/reset-password")
    public ResponseEntity<Void> resetPassword(@PathVariable Long staffId,
                                               @Valid @RequestBody ResetPasswordRequest request,
                                               @AuthenticationPrincipal StaffPrincipal principal) {
        if (principal.getStaff().getRole() != StaffRole.ADMIN) {
            throw new AccessDeniedException("Only admins can reset another staff's password");
        }
        var staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + staffId));
        staff.setPassword(passwordEncoder.encode(request.newPassword()));
        staffRepository.save(staff);
        return ResponseEntity.noContent().build();
    }
}
