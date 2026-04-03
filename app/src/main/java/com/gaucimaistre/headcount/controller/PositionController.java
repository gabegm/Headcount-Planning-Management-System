package com.gaucimaistre.headcount.controller;

import com.gaucimaistre.headcount.model.Position;
import com.gaucimaistre.headcount.security.AppUserDetails;
import com.gaucimaistre.headcount.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;
    private final CompanyService companyService;
    private final PillarService pillarService;
    private final DepartmentService departmentService;
    private final FunctionService functionService;
    private final PositionStatusService positionStatusService;
    private final RecruitmentStatusService recruitmentStatusService;

    @GetMapping
    public String index(@AuthenticationPrincipal AppUserDetails principal, Model model) {
        model.addAttribute("positions",
                positionService.findAllByUserAccess(principal.getUserId(), principal.getUserType()));
        return "position/index";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        populateFormLookups(model);
        return "position/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Position position, RedirectAttributes redirectAttributes) {
        try {
            positionService.save(position);
            redirectAttributes.addFlashAttribute("successMessage", "Position created successfully.");
        } catch (Exception e) {
            log.error("Failed to create position", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create position.");
        }
        return "redirect:/positions";
    }

    @GetMapping("/view/{id}")
    @ResponseBody
    public Map<String, Object> view(@PathVariable int id) {
        return positionService.findById(id)
                .map(p -> Map.<String, Object>of("position", p))
                .orElse(Map.of("error", "Position not found"));
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable int id, Model model) {
        positionService.findById(id).ifPresent(p -> model.addAttribute("position", p));
        populateFormLookups(model);
        return "position/edit-form :: form";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable int id,
                         @ModelAttribute Position position,
                         RedirectAttributes redirectAttributes) {
        try {
            positionService.update(id, position);
            redirectAttributes.addFlashAttribute("successMessage", "Position updated successfully.");
        } catch (Exception e) {
            log.error("Failed to update position {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update position.");
        }
        return "redirect:/positions";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            positionService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Position deleted successfully.");
        } catch (Exception e) {
            log.error("Failed to delete position {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete position.");
        }
        return "redirect:/positions";
    }

    private void populateFormLookups(Model model) {
        model.addAttribute("companies", companyService.findAll());
        model.addAttribute("pillars", pillarService.findAll());
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("functions", functionService.findAll());
        model.addAttribute("positionStatuses", positionStatusService.findAll());
        model.addAttribute("recruitmentStatuses", recruitmentStatusService.findAll());
    }
}
