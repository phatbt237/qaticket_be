package com.qms.qms.service;

import com.qms.qms.entity.Staff;
import org.springframework.data.jpa.domain.Specification;

public final class StaffSpecifications {

    private StaffSpecifications() {
    }

    public static Specification<Staff> fullNameContains(String name) {
        return (root, query, cb) -> (name == null || name.isBlank()) ? null
                : cb.like(cb.lower(root.get("fullName")), "%" + name.toLowerCase() + "%");
    }

    /** Keyset cursor: only rows strictly older (lower id) than the last-seen id, for descending-by-id pagination. */
    public static Specification<Staff> idBefore(Long cursor) {
        return (root, query, cb) -> cursor == null ? null : cb.lessThan(root.get("id"), cursor);
    }
}
