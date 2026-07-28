package com.qms.qms.dto.ticket;

import com.qms.qms.entity.enums.InspectionStage;
import com.qms.qms.entity.enums.TicketStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record QaTicketRequest(
        @NotNull Long staffId,
        @NotNull Long factoryId,
        @NotNull Long lineId,
        Long groupId,
        @NotNull InspectionStage inspectionStage,
        Long poId,
        Long styleId,
        @NotNull @Min(1) Integer inspectedQty,
        @NotNull Long customerId,
        @NotNull Long garmentTypeId,
        @NotNull TicketStatus status,
        @Valid List<QaTicketDefectRequest> defects,
        List<@NotBlank String> specImages
) {
}
