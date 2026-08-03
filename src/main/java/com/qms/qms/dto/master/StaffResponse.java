package com.qms.qms.dto.master;

import com.qms.qms.entity.Staff;
import com.qms.qms.entity.enums.StaffLanguage;
import com.qms.qms.entity.enums.StaffRole;

public record StaffResponse(Long id, String code, String fullName, StaffRole role, Boolean active,
                             StaffLanguage language) {
    public static StaffResponse from(Staff s) {
        return new StaffResponse(s.getId(), s.getCode(), s.getFullName(), s.getRole(), s.getActive(),
                s.getLanguage());
    }
}
