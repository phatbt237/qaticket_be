package com.qms.qms.controller;

import com.qms.qms.dto.dashboard.QaDashboardResponse;
import com.qms.qms.service.QaDashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/qa")
public class QaDashboardController {

    private final QaDashboardService qaDashboardService;

    public QaDashboardController(QaDashboardService qaDashboardService) {
        this.qaDashboardService = qaDashboardService;
    }

    /**
     * Aggregated dashboard (stage counts, DHU timeline, defect Pareto, DHU by stage) across every
     * SUBMITTED ticket currently in the database — no filters yet, add query params here later
     * if scoping (e.g. by PO, factory, date range) turns out to be needed.
     */
    @GetMapping("/dashboard")
    public QaDashboardResponse dashboard() {
        return qaDashboardService.getDashboard();
    }
}
