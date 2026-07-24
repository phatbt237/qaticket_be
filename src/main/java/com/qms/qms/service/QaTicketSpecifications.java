package com.qms.qms.service;

import com.qms.qms.entity.QaTicket;
import com.qms.qms.entity.enums.TicketStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class QaTicketSpecifications {

    private QaTicketSpecifications() {
    }

    public static Specification<QaTicket> factoryId(Long factoryId) {
        return (root, query, cb) -> factoryId == null ? null : cb.equal(root.get("factory").get("id"), factoryId);
    }

    public static Specification<QaTicket> lineId(Long lineId) {
        return (root, query, cb) -> lineId == null ? null : cb.equal(root.get("line").get("id"), lineId);
    }

    public static Specification<QaTicket> staffId(Long staffId) {
        return (root, query, cb) -> staffId == null ? null : cb.equal(root.get("staff").get("id"), staffId);
    }

    public static Specification<QaTicket> status(TicketStatus status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<QaTicket> exported(Boolean exported) {
        return (root, query, cb) -> exported == null ? null : cb.equal(root.get("exported"), exported);
    }

    public static Specification<QaTicket> dateFrom(LocalDate dateFrom) {
        return (root, query, cb) -> dateFrom == null ? null
                : cb.greaterThanOrEqualTo(root.get("createdAt"), dateFrom.atStartOfDay());
    }

    public static Specification<QaTicket> dateTo(LocalDate dateTo) {
        return (root, query, cb) -> dateTo == null ? null
                : cb.lessThan(root.get("createdAt"), dateTo.plusDays(1).atStartOfDay());
    }

    /** Keyset cursor: only rows strictly older (lower id) than the last-seen id, for descending-by-id pagination. */
    public static Specification<QaTicket> idBefore(Long cursor) {
        return (root, query, cb) -> cursor == null ? null : cb.lessThan(root.get("id"), cursor);
    }
}
