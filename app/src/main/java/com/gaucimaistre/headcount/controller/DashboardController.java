package com.gaucimaistre.headcount.controller;

import com.gaucimaistre.headcount.service.DashboardService;
import com.gaucimaistre.headcount.service.FunctionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final FunctionService functionService;

    @GetMapping("/dashboard/{functionId}")
    public String dashboard(@PathVariable int functionId, Model model) {
        functionService.findById(functionId).ifPresent(f -> model.addAttribute("function", f));
        model.addAttribute("chartData", dashboardService.getChartData(functionId));
        return "index";
    }

    @GetMapping("/dashboard/render/{functionId}")
    @ResponseBody
    public Map<String, Object> renderChart(@PathVariable int functionId) {
        log.debug("Rendering chart data for function {}", functionId);
        return dashboardService.getChartData(functionId);
    }
}
