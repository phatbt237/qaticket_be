package com.qms.qms.dto.ticket;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record QaTicketDefectRequest(
        @NotNull Long defectId,
        String note,
        @Valid List<QaTicketDefectLocationRequest> locations
) {
}
