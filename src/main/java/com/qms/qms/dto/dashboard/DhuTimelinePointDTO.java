package com.qms.qms.dto.dashboard;

import java.time.LocalDate;

public record DhuTimelinePointDTO(LocalDate date, double dhuPercent) {
}
