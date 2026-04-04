package com.gaucimaistre.headcount.controller;

import com.gaucimaistre.headcount.security.AppUserDetails;
import com.gaucimaistre.headcount.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard/render/{functionId}")
    @ResponseBody
    public Map<String, Object> renderChart(@PathVariable int functionId,
                                           @AuthenticationPrincipal AppUserDetails principal) {
        log.debug("Rendering chart data for function {}", functionId);
        var data = dashboardService.getChartData(principal.getUserId(), principal.getUserType(), functionId);
        return Map.of(
                "budgetFte", data.budgetFte(),
                "actualFte", data.actualFte(),
                "budgetCost", data.budgetCost(),
                "actualCost", data.actualCost()
        );
    }
}
