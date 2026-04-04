package com.gaucimaistre.headcount.controller;

import com.gaucimaistre.headcount.model.Submission;
import com.gaucimaistre.headcount.model.SubmissionChange;
import com.gaucimaistre.headcount.security.AppUserDetails;
import com.gaucimaistre.headcount.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private static final int DEFAULT_STATUS_ID = 1; // On-hold

    private final SubmissionService submissionService;
    private final GatekeepingService gatekeepingService;
    private final FunctionService functionService;
    private final SubmissionReasonService submissionReasonService;

    @GetMapping
    public String index(@AuthenticationPrincipal AppUserDetails principal, Model model) {
        model.addAttribute("submissions",
                submissionService.findViewsBySubmitterId(principal.getUserId()));
        return "submission/index";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("gatekeepings", gatekeepingService.findAll());
        model.addAttribute("functions", functionService.findAll());
        model.addAttribute("reasons", submissionReasonService.findAll());
        return "submission/create";
    }

    @PostMapping("/create")
    public String create(
            @RequestParam String positionId,
            @RequestParam int reasonId,
            @RequestParam String rationale,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate effectiveDate,
            @RequestParam(defaultValue = "0") int gatekeepingId,
            @RequestParam(required = false) String positionTitleField,
            @RequestParam(required = false) String functionalReportingLineField,
            @RequestParam(required = false) String disciplinaryReportingLineField,
            @RequestParam(required = false) String positionHoursField,
            @RequestParam(required = false) String annualSalaryField,
            @RequestParam(required = false) String performanceBonusField,
            @RequestParam(required = false) String superBonusField,
            @RequestParam(required = false) String managementBonusField,
            @RequestParam(required = false) String startDateField,
            @AuthenticationPrincipal AppUserDetails principal,
            RedirectAttributes redirectAttributes) {
        try {
            OffsetDateTime now = OffsetDateTime.now();
            Submission submission = new Submission(
                    0, principal.getUserId(), gatekeepingId, positionId,
                    DEFAULT_STATUS_ID, reasonId, rationale, effectiveDate, now, null);

            List<SubmissionChange> changes = new ArrayList<>();
            addChange(changes, 0, now, "title",                      positionTitleField);
            addChange(changes, 0, now, "functional_reporting_line",  functionalReportingLineField);
            addChange(changes, 0, now, "disciplinary_reporting_line",disciplinaryReportingLineField);
            addChange(changes, 0, now, "hours",                      positionHoursField);
            addChange(changes, 0, now, "salary",                     annualSalaryField);
            addChange(changes, 0, now, "performance_bonus",          performanceBonusField);
            addChange(changes, 0, now, "super_bonus",                superBonusField);
            addChange(changes, 0, now, "management_bonus",           managementBonusField);
            addChange(changes, 0, now, "start_date",                 startDateField);

            submissionService.create(submission, changes.isEmpty() ? null : changes);
            redirectAttributes.addFlashAttribute("successMessage", "Submission created successfully.");
        } catch (Exception e) {
            log.error("Failed to create submission for user {}", principal.getUserId(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create submission.");
        }
        return "redirect:/submissions";
    }

    private void addChange(List<SubmissionChange> list, int submissionId,
                           OffsetDateTime submitted, String field, String value) {
        if (value != null && !value.isBlank()) {
            list.add(new SubmissionChange(0, submissionId, submitted, field, value));
        }
    }

    @GetMapping("/{id}/change")
    public String changes(@PathVariable int id, Model model) {
        submissionService.findById(id).ifPresent(s -> model.addAttribute("submission", s));
        model.addAttribute("changes", submissionService.getChangesBySubmissionId(id));
        return "submission/change";
    }
}
