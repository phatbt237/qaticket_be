package com.qms.qms.controller;

import com.qms.qms.dto.aql.AqlSamplingPlanResponse;
import com.qms.qms.entity.enums.AqlLevel;
import com.qms.qms.service.AqlSamplingService;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/aql")
public class AqlSamplingController {

    private final AqlSamplingService aqlSamplingService;

    public AqlSamplingController(AqlSamplingService aqlSamplingService) {
        this.aqlSamplingService = aqlSamplingService;
    }

    @GetMapping("/lookup")
    public AqlSamplingPlanResponse lookup(@RequestParam String aqlLevel, @RequestParam @Min(1) Integer qtySize) {
        return AqlSamplingPlanResponse.from(aqlSamplingService.lookup(AqlLevel.fromCode(aqlLevel), qtySize));
    }
}
