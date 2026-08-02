package com.qms.qms.controller;

import com.qms.qms.dto.dashboard.QaDashboardResponse;
import com.qms.qms.service.QaDashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
     * SUBMITTED ticket currently in the database. {@code staffId}/{@code factoryId} are optional
     * filters; omit both to see every ticket.
     */
    @GetMapping("/dashboard")
    public QaDashboardResponse dashboard(@RequestParam(required = false) Long staffId,
                                          @RequestParam(required = false) Long factoryId) {
        return qaDashboardService.getDashboard(staffId, factoryId);
    }
}
