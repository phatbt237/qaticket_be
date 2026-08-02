package com.qms.qms.dto.dashboard;

import java.util.List;

public record QaDashboardResponse(
        int totalInspections,
        List<StageSummaryDTO> stageSummary,
        List<DhuTimelinePointDTO> dhuTimeline,
        List<ParetoDefectGroupDTO> paretoDefectGroups,
        List<DhuByStageDTO> dhuByStage
) {
}
