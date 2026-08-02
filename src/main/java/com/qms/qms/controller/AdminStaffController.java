package com.qms.qms.controller;

import com.qms.qms.dto.admin.CreateStaffRequest;
import com.qms.qms.dto.master.StaffResponse;
import com.qms.qms.entity.Staff;
import com.qms.qms.entity.enums.StaffRole;
import com.qms.qms.exception.ResourceNotFoundException;
import com.qms.qms.repository.StaffRepository;
import com.qms.qms.security.RefreshTokenService;
import com.qms.qms.security.StaffPrincipal;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/staff")
public class AdminStaffController {

    private final StaffRepository staffRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    public AdminStaffController(StaffRepository staffRepository, RefreshTokenService refreshTokenService,
                                 PasswordEncoder passwordEncoder) {
        this.staffRepository = staffRepository;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @CacheEvict(cacheNames = "staff", allEntries = true)
    @PostMapping
    public ResponseEntity<StaffResponse> create(@Valid @RequestBody CreateStaffRequest request,
                                                 @AuthenticationPrincipal StaffPrincipal principal) {
        requireAdmin(principal);
        if (staffRepository.findByCodeIgnoreCase(request.code()).isPresent()) {
            throw new IllegalArgumentException("Mã nhân viên đã tồn tại: " + request.code());
        }
        Staff staff = new Staff();
        staff.setCode(request.code());
        staff.setFullName(request.fullName());
        staff.setPassword(passwordEncoder.encode(request.password()));
        staff.setRole(request.role());
        staff.setActive(true);
        staff = staffRepository.save(staff);
        return ResponseEntity.status(HttpStatus.CREATED).body(StaffResponse.from(staff));
    }

    /** Locking blocks login immediately (Staff.active backs StaffPrincipal#isEnabled) and revokes
     * every refresh token the staff currently holds, so an already-open session can't refresh either. */
    @CacheEvict(cacheNames = "staff", allEntries = true)
    @PatchMapping("/{id}/lock")
    public ResponseEntity<Void> lock(@PathVariable Long id, @AuthenticationPrincipal StaffPrincipal principal) {
        if (principal.getStaff().getId().equals(id)) {
            throw new IllegalArgumentException("Admin không thể tự khóa chính mình");
        }
        Staff target = requireTarget(principal, id);
        target.setActive(false);
        staffRepository.save(target);
        refreshTokenService.revokeAllForStaff(target);
        return ResponseEntity.noContent().build();
    }

    @CacheEvict(cacheNames = "staff", allEntries = true)
    @PatchMapping("/{id}/unlock")
    public ResponseEntity<Void> unlock(@PathVariable Long id, @AuthenticationPrincipal StaffPrincipal principal) {
        Staff target = requireTarget(principal, id);
        target.setActive(true);
        staffRepository.save(target);
        return ResponseEntity.noContent().build();
    }

    private Staff requireTarget(StaffPrincipal principal, Long targetId) {
        requireAdmin(principal);
        return staffRepository.findById(targetId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found: " + targetId));
    }

    private void requireAdmin(StaffPrincipal principal) {
        if (principal.getStaff().getRole() != StaffRole.ADMIN) {
            throw new AccessDeniedException("Only admins can manage staff accounts");
        }
    }
}
