package com.qms.qms.controller;

import com.qms.qms.dto.auth.LoginRequest;
import com.qms.qms.dto.auth.LoginResponse;
import com.qms.qms.dto.auth.RefreshRequest;
import com.qms.qms.entity.RefreshToken;
import com.qms.qms.security.JwtService;
import com.qms.qms.security.RefreshTokenService;
import com.qms.qms.security.StaffPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService,
                           RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
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
                staff.getId(), staff.getCode(), staff.getFullName(), staff.getRole());
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(@Valid @RequestBody RefreshRequest request) {
        RefreshToken rotated = refreshTokenService.rotate(request.refreshToken());
        var staff = rotated.getStaff();

        String accessToken = jwtService.generateToken(staff);

        return new LoginResponse(accessToken, rotated.getToken(), "Bearer", jwtService.getExpirationMs(),
                staff.getId(), staff.getCode(), staff.getFullName(), staff.getRole());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest request) {
        refreshTokenService.revoke(request.refreshToken());
        return ResponseEntity.noContent().build();
    }
}
