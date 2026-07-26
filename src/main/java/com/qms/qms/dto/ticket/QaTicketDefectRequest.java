package com.qms.qms.dto.ticket;

import com.qms.qms.entity.enums.Severity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record QaTicketDefectRequest(
        @NotNull Long defectItemId,
        Severity severity,
        String note,
        @Valid List<QaTicketDefectLocationRequest> locations
) {
}
