package com.gaucimaistre.headcount.controller;

import com.gaucimaistre.headcount.model.Submission;
import com.gaucimaistre.headcount.security.AppUserDetails;
import com.gaucimaistre.headcount.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/submissions")
@RequiredArgsConstructor
public class SubmissionController {

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
    public String create(@ModelAttribute Submission submission,
                         @AuthenticationPrincipal AppUserDetails principal,
                         RedirectAttributes redirectAttributes) {
        try {
            submissionService.save(submission);
            redirectAttributes.addFlashAttribute("successMessage", "Submission created successfully.");
        } catch (Exception e) {
            log.error("Failed to create submission for user {}", principal.getUserId(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create submission.");
        }
        return "redirect:/submissions";
    }

    @GetMapping("/{id}/change")
    public String changes(@PathVariable int id, Model model) {
        submissionService.findById(id).ifPresent(s -> model.addAttribute("submission", s));
        model.addAttribute("changes", submissionService.getChangesBySubmissionId(id));
        return "submission/change";
    }
}
