package com.gaucimaistre.headcount.controller;

import com.gaucimaistre.headcount.model.*;
import com.gaucimaistre.headcount.model.enums.UserType;
import com.gaucimaistre.headcount.security.AppUserDetails;
import com.gaucimaistre.headcount.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final FunctionService functionService;
    private final CompanyService companyService;
    private final ExchangeRateService exchangeRateService;
    private final PillarService pillarService;
    private final DepartmentService departmentService;
    private final AdminPositionService adminPositionService;
    private final AdminBudgetService adminBudgetService;
    private final GatekeepingService gatekeepingService;
    private final AdminSubmissionService adminSubmissionService;
    private final SubmissionStatusService submissionStatusService;
    private final PositionStatusService positionStatusService;
    private final HelpService helpService;
    private final FaqService faqService;
    private final AuditService auditService;

    // -------------------------------------------------------------------------
    // Dashboard
    // -------------------------------------------------------------------------

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("dashboard", adminService.getDashboardData());
        return "admin/index";
    }

    // -------------------------------------------------------------------------
    // User management
    // -------------------------------------------------------------------------

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("functions", functionService.findAll());
        return "admin/user";
    }

    @GetMapping("/users/{id}/edit-form")
    public String userForm(@PathVariable int id, Model model) {
        userService.findById(id).ifPresent(u -> model.addAttribute("user", u));
        model.addAttribute("functions", functionService.findAll());
        model.addAttribute("userFunctions", userService.getFunctionIds(id));
        return "admin/user :: editForm";
    }

    @PostMapping("/users/{id}/update")
    public String updateUser(@PathVariable int id,
                             @RequestParam UserType type,
                             @RequestParam boolean active,
                             @RequestParam(required = false) java.util.List<Integer> functionIds,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.update(id, type, active,
                    functionIds != null ? functionIds : java.util.List.of());
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully.");
        } catch (Exception e) {
            log.error("Failed to update user {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update user.");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            userService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted.");
        } catch (Exception e) {
            log.error("Failed to delete user {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete user.");
        }
        return "redirect:/admin/users";
    }

    // -------------------------------------------------------------------------
    // Company
    // -------------------------------------------------------------------------

    @GetMapping("/companies")
    public String companies(Model model) {
        model.addAttribute("companies", companyService.findAll());
        model.addAttribute("exchangeRates", exchangeRateService.findAll());
        return "admin/company";
    }

    @PostMapping("/companies")
    public String createCompany(@ModelAttribute Company company, RedirectAttributes redirectAttributes) {
        try {
            companyService.save(company);
            redirectAttributes.addFlashAttribute("successMessage", "Company created.");
        } catch (Exception e) {
            log.error("Failed to create company", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create company.");
        }
        return "redirect:/admin/companies";
    }

    @GetMapping("/companies/{id}/edit-form")
    public String companyForm(@PathVariable int id, Model model) {
        companyService.findById(id).ifPresent(c -> model.addAttribute("company", c));
        model.addAttribute("exchangeRates", exchangeRateService.findAll());
        return "admin/company :: editForm";
    }

    @PostMapping("/companies/{id}/update")
    public String updateCompany(@PathVariable int id,
                                @ModelAttribute Company company,
                                RedirectAttributes redirectAttributes) {
        try {
            companyService.update(id, company);
            redirectAttributes.addFlashAttribute("successMessage", "Company updated.");
        } catch (Exception e) {
            log.error("Failed to update company {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update company.");
        }
        return "redirect:/admin/companies";
    }

    @PostMapping("/companies/{id}/delete")
    public String deleteCompany(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            companyService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Company deleted.");
        } catch (Exception e) {
            log.error("Failed to delete company {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete company.");
        }
        return "redirect:/admin/companies";
    }

    // -------------------------------------------------------------------------
    // Pillar
    // -------------------------------------------------------------------------

    @GetMapping("/pillars")
    public String pillars(Model model) {
        model.addAttribute("pillars", pillarService.findAll());
        return "admin/pillar";
    }

    @PostMapping("/pillars")
    public String createPillar(@ModelAttribute Pillar pillar, RedirectAttributes redirectAttributes) {
        try {
            pillarService.save(pillar);
            redirectAttributes.addFlashAttribute("successMessage", "Pillar created.");
        } catch (Exception e) {
            log.error("Failed to create pillar", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create pillar.");
        }
        return "redirect:/admin/pillars";
    }

    @GetMapping("/pillars/{id}/edit-form")
    public String pillarForm(@PathVariable int id, Model model) {
        pillarService.findById(id).ifPresent(p -> model.addAttribute("pillar", p));
        return "admin/pillar :: editForm";
    }

    @PostMapping("/pillars/{id}/update")
    public String updatePillar(@PathVariable int id,
                               @ModelAttribute Pillar pillar,
                               RedirectAttributes redirectAttributes) {
        try {
            pillarService.update(id, pillar);
            redirectAttributes.addFlashAttribute("successMessage", "Pillar updated.");
        } catch (Exception e) {
            log.error("Failed to update pillar {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update pillar.");
        }
        return "redirect:/admin/pillars";
    }

    @PostMapping("/pillars/{id}/delete")
    public String deletePillar(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            pillarService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Pillar deleted.");
        } catch (Exception e) {
            log.error("Failed to delete pillar {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete pillar.");
        }
        return "redirect:/admin/pillars";
    }

    // -------------------------------------------------------------------------
    // Department
    // -------------------------------------------------------------------------

    @GetMapping("/departments")
    public String departments(Model model) {
        model.addAttribute("departments", departmentService.findAll());
        return "admin/department";
    }

    @PostMapping("/departments")
    public String createDepartment(@ModelAttribute Department department,
                                   RedirectAttributes redirectAttributes) {
        try {
            departmentService.save(department);
            redirectAttributes.addFlashAttribute("successMessage", "Department created.");
        } catch (Exception e) {
            log.error("Failed to create department", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create department.");
        }
        return "redirect:/admin/departments";
    }

    @GetMapping("/departments/{id}/edit-form")
    public String departmentForm(@PathVariable int id, Model model) {
        departmentService.findById(id).ifPresent(d -> model.addAttribute("department", d));
        return "admin/department :: editForm";
    }

    @PostMapping("/departments/{id}/update")
    public String updateDepartment(@PathVariable int id,
                                   @ModelAttribute Department department,
                                   RedirectAttributes redirectAttributes) {
        try {
            departmentService.update(id, department);
            redirectAttributes.addFlashAttribute("successMessage", "Department updated.");
        } catch (Exception e) {
            log.error("Failed to update department {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update department.");
        }
        return "redirect:/admin/departments";
    }

    @PostMapping("/departments/{id}/delete")
    public String deleteDepartment(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            departmentService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Department deleted.");
        } catch (Exception e) {
            log.error("Failed to delete department {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete department.");
        }
        return "redirect:/admin/departments";
    }

    // -------------------------------------------------------------------------
    // Function
    // -------------------------------------------------------------------------

    @GetMapping("/functions")
    public String functions(Model model) {
        model.addAttribute("functions", functionService.findAll());
        return "admin/function";
    }

    @PostMapping("/functions")
    public String createFunction(@ModelAttribute Function function,
                                 RedirectAttributes redirectAttributes) {
        try {
            functionService.save(function);
            redirectAttributes.addFlashAttribute("successMessage", "Function created.");
        } catch (Exception e) {
            log.error("Failed to create function", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create function.");
        }
        return "redirect:/admin/functions";
    }

    @GetMapping("/functions/{id}/edit-form")
    public String functionForm(@PathVariable int id, Model model) {
        functionService.findById(id).ifPresent(f -> model.addAttribute("function", f));
        return "admin/function :: editForm";
    }

    @PostMapping("/functions/{id}/update")
    public String updateFunction(@PathVariable int id,
                                 @ModelAttribute Function function,
                                 RedirectAttributes redirectAttributes) {
        try {
            functionService.update(id, function);
            redirectAttributes.addFlashAttribute("successMessage", "Function updated.");
        } catch (Exception e) {
            log.error("Failed to update function {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update function.");
        }
        return "redirect:/admin/functions";
    }

    @PostMapping("/functions/{id}/delete")
    public String deleteFunction(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            functionService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Function deleted.");
        } catch (Exception e) {
            log.error("Failed to delete function {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete function.");
        }
        return "redirect:/admin/functions";
    }

    // -------------------------------------------------------------------------
    // Exchange Rate
    // -------------------------------------------------------------------------

    @GetMapping("/exchange-rates")
    public String exchangeRates(Model model) {
        model.addAttribute("exchangeRates", exchangeRateService.findAll());
        return "admin/exchange-rate";
    }

    @PostMapping("/exchange-rates")
    public String createExchangeRate(@ModelAttribute ExchangeRate exchangeRate,
                                     RedirectAttributes redirectAttributes) {
        try {
            exchangeRateService.save(exchangeRate);
            redirectAttributes.addFlashAttribute("successMessage", "Exchange rate created.");
        } catch (Exception e) {
            log.error("Failed to create exchange rate", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create exchange rate.");
        }
        return "redirect:/admin/exchange-rates";
    }

    @GetMapping("/exchange-rates/{id}/edit-form")
    public String exchangeRateForm(@PathVariable int id, Model model) {
        exchangeRateService.findById(id).ifPresent(e -> model.addAttribute("exchangeRate", e));
        return "admin/exchange-rate :: editForm";
    }

    @PostMapping("/exchange-rates/{id}/update")
    public String updateExchangeRate(@PathVariable int id,
                                     @ModelAttribute ExchangeRate exchangeRate,
                                     RedirectAttributes redirectAttributes) {
        try {
            exchangeRateService.update(id, exchangeRate);
            redirectAttributes.addFlashAttribute("successMessage", "Exchange rate updated.");
        } catch (Exception e) {
            log.error("Failed to update exchange rate {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update exchange rate.");
        }
        return "redirect:/admin/exchange-rates";
    }

    @PostMapping("/exchange-rates/{id}/delete")
    public String deleteExchangeRate(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            exchangeRateService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Exchange rate deleted.");
        } catch (Exception e) {
            log.error("Failed to delete exchange rate {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete exchange rate.");
        }
        return "redirect:/admin/exchange-rates";
    }

    // -------------------------------------------------------------------------
    // Admin Position
    // -------------------------------------------------------------------------

    @GetMapping("/positions")
    public String adminPositions(Model model) {
        model.addAttribute("positions", adminPositionService.findAllViews());
        return "admin/position";
    }

    @PostMapping("/positions")
    public String createAdminPosition(@ModelAttribute Position position,
                                      RedirectAttributes redirectAttributes) {
        try {
            adminPositionService.save(position);
            redirectAttributes.addFlashAttribute("successMessage", "Position created.");
        } catch (Exception e) {
            log.error("Failed to create position", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create position.");
        }
        return "redirect:/admin/positions";
    }

    @GetMapping("/positions/{id}/edit-form")
    public String adminPositionForm(@PathVariable int id, Model model) {
        adminPositionService.findById(id).ifPresent(p -> model.addAttribute("position", p));
        model.addAttribute("positionStatuses", positionStatusService.findAll());
        return "admin/position :: editForm";
    }

    @PostMapping("/positions/{id}/update")
    public String updateAdminPosition(@PathVariable int id,
                                      @ModelAttribute Position position,
                                      RedirectAttributes redirectAttributes) {
        try {
            adminPositionService.update(id, position);
            redirectAttributes.addFlashAttribute("successMessage", "Position updated.");
        } catch (Exception e) {
            log.error("Failed to update position {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update position.");
        }
        return "redirect:/admin/positions";
    }

    @PostMapping("/positions/{id}/delete")
    public String deleteAdminPosition(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            adminPositionService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Position deleted.");
        } catch (Exception e) {
            log.error("Failed to delete position {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete position.");
        }
        return "redirect:/admin/positions";
    }

    @GetMapping("/positions/upload")
    public String positionUpload() {
        return "admin/position-upload";
    }

    @PostMapping("/positions/upload")
    public String uploadPositions(@RequestParam("file") MultipartFile file,
                                  RedirectAttributes redirectAttributes) {
        try {
            String result = adminPositionService.importFromCsv(file);
            redirectAttributes.addFlashAttribute("successMessage", result);
        } catch (Exception e) {
            log.error("Failed to import positions from CSV", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Import failed: " + e.getMessage());
        }
        return "redirect:/admin/positions";
    }

    // -------------------------------------------------------------------------
    // Budget
    // -------------------------------------------------------------------------

    @GetMapping("/budget")
    public String budget(Model model) {
        model.addAttribute("positions", adminPositionService.findBudgetViews());
        return "admin/budget";
    }

    @GetMapping("/budget/upload")
    public String budgetUpload() {
        return "admin/budget-upload";
    }

    @PostMapping("/budget/upload")
    public String uploadBudget(@RequestParam("file") MultipartFile file,
                               RedirectAttributes redirectAttributes) {
        try {
            String result = adminBudgetService.importBudget(file);
            redirectAttributes.addFlashAttribute("successMessage", result);
        } catch (Exception e) {
            log.error("Failed to import budget", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Budget import failed: " + e.getMessage());
        }
        return "redirect:/admin/budget";
    }

    // -------------------------------------------------------------------------
    // Gatekeeping
    // -------------------------------------------------------------------------

    @GetMapping("/gatekeeping")
    public String gatekeeping(Model model) {
        model.addAttribute("gatekeepings", gatekeepingService.findAll());
        return "admin/gatekeeping";
    }

    @PostMapping("/gatekeeping/create")
    public String createGatekeeping(@ModelAttribute Gatekeeping gatekeeping,
                                    RedirectAttributes redirectAttributes) {
        try {
            gatekeepingService.save(gatekeeping);
            redirectAttributes.addFlashAttribute("successMessage", "Gatekeeping period created.");
        } catch (Exception e) {
            log.error("Failed to create gatekeeping", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create gatekeeping period.");
        }
        return "redirect:/admin/gatekeeping";
    }

    @GetMapping("/gatekeeping/{id}/edit-form")
    public String gatekeepingForm(@PathVariable int id, Model model) {
        gatekeepingService.findById(id).ifPresent(g -> model.addAttribute("gatekeeping", g));
        return "admin/gatekeeping :: editForm";
    }

    @PostMapping("/gatekeeping/{id}/update")
    public String updateGatekeeping(@PathVariable int id,
                                    @ModelAttribute Gatekeeping gatekeeping,
                                    RedirectAttributes redirectAttributes) {
        try {
            gatekeepingService.update(id, gatekeeping);
            redirectAttributes.addFlashAttribute("successMessage", "Gatekeeping period updated.");
        } catch (Exception e) {
            log.error("Failed to update gatekeeping {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update gatekeeping period.");
        }
        return "redirect:/admin/gatekeeping";
    }

    @PostMapping("/gatekeeping/{id}/delete")
    public String deleteGatekeeping(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            gatekeepingService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Gatekeeping period deleted.");
        } catch (Exception e) {
            log.error("Failed to delete gatekeeping {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete gatekeeping period.");
        }
        return "redirect:/admin/gatekeeping";
    }

    // -------------------------------------------------------------------------
    // Submission review
    // -------------------------------------------------------------------------

    @GetMapping("/submissions")
    public String submissions(Model model) {
        model.addAttribute("submissions", adminSubmissionService.findAll());
        return "admin/submission";
    }

    @GetMapping("/submissions/{id}/edit-form")
    public String submissionForm(@PathVariable int id, Model model) {
        adminSubmissionService.findById(id).ifPresent(s -> model.addAttribute("submission", s));
        model.addAttribute("statuses", submissionStatusService.findAll());
        return "admin/submission :: editForm";
    }

    @PostMapping("/submissions/{id}/update")
    public String updateSubmission(@PathVariable int id,
                                   @RequestParam int statusId,
                                   @RequestParam(required = false) String comment,
                                   RedirectAttributes redirectAttributes) {
        try {
            adminSubmissionService.updateStatus(id, statusId, comment);
            redirectAttributes.addFlashAttribute("successMessage", "Submission updated.");
        } catch (Exception e) {
            log.error("Failed to update submission {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update submission.");
        }
        return "redirect:/admin/submissions";
    }

    @PostMapping("/submissions/{id}/delete")
    public String deleteSubmission(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            adminSubmissionService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Submission deleted.");
        } catch (Exception e) {
            log.error("Failed to delete submission {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete submission.");
        }
        return "redirect:/admin/submissions";
    }

    @GetMapping("/submission-changes")
    public String submissionChanges(@RequestParam int submissionId, Model model) {
        adminSubmissionService.findById(submissionId).ifPresent(s -> model.addAttribute("submission", s));
        model.addAttribute("changes", adminSubmissionService.getChangesBySubmissionId(submissionId));
        return "admin/submission-change";
    }

    // -------------------------------------------------------------------------
    // Status config (read-only)
    // -------------------------------------------------------------------------

    @GetMapping("/submission-statuses")
    public String submissionStatuses(Model model) {
        model.addAttribute("statuses", submissionStatusService.findAll());
        return "admin/submission-status";
    }

    @PostMapping("/submission-statuses")
    public String createSubmissionStatus(@ModelAttribute SubmissionStatus status,
                                         RedirectAttributes redirectAttributes) {
        try {
            submissionStatusService.create(status);
            redirectAttributes.addFlashAttribute("successMessage", "Submission status created.");
        } catch (Exception e) {
            log.error("Failed to create submission status", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create submission status.");
        }
        return "redirect:/admin/submission-statuses";
    }

    @GetMapping("/submission-statuses/{id}/edit-form")
    public String submissionStatusEditForm(@PathVariable int id, Model model) {
        submissionStatusService.findById(id).ifPresent(s -> model.addAttribute("status", s));
        return "admin/submission-status :: editForm";
    }

    @PostMapping("/submission-statuses/{id}/update")
    public String updateSubmissionStatus(@PathVariable int id,
                                         @ModelAttribute SubmissionStatus status,
                                         RedirectAttributes redirectAttributes) {
        try {
            submissionStatusService.update(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Submission status updated.");
        } catch (Exception e) {
            log.error("Failed to update submission status {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update submission status.");
        }
        return "redirect:/admin/submission-statuses";
    }

    @PostMapping("/submission-statuses/{id}/delete")
    public String deleteSubmissionStatus(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            submissionStatusService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Submission status deleted.");
        } catch (Exception e) {
            log.error("Failed to delete submission status {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete submission status.");
        }
        return "redirect:/admin/submission-statuses";
    }

    @GetMapping("/position-statuses")
    public String positionStatuses(Model model) {
        model.addAttribute("statuses", positionStatusService.findAll());
        return "admin/position-status";
    }

    @PostMapping("/position-statuses")
    public String createPositionStatus(@ModelAttribute PositionStatus status,
                                       RedirectAttributes redirectAttributes) {
        try {
            positionStatusService.create(status);
            redirectAttributes.addFlashAttribute("successMessage", "Position status created.");
        } catch (Exception e) {
            log.error("Failed to create position status", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create position status.");
        }
        return "redirect:/admin/position-statuses";
    }

    @GetMapping("/position-statuses/{id}/edit-form")
    public String positionStatusEditForm(@PathVariable int id, Model model) {
        positionStatusService.findById(id).ifPresent(s -> model.addAttribute("status", s));
        return "admin/position-status :: editForm";
    }

    @PostMapping("/position-statuses/{id}/update")
    public String updatePositionStatus(@PathVariable int id,
                                       @ModelAttribute PositionStatus status,
                                       RedirectAttributes redirectAttributes) {
        try {
            positionStatusService.update(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Position status updated.");
        } catch (Exception e) {
            log.error("Failed to update position status {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update position status.");
        }
        return "redirect:/admin/position-statuses";
    }

    @PostMapping("/position-statuses/{id}/delete")
    public String deletePositionStatus(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            positionStatusService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Position status deleted.");
        } catch (Exception e) {
            log.error("Failed to delete position status {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete position status.");
        }
        return "redirect:/admin/position-statuses";
    }

    // -------------------------------------------------------------------------
    // CMS — Help
    // -------------------------------------------------------------------------

    @GetMapping("/help")
    public String adminHelp(Model model) {
        helpService.getHelpPage().ifPresent(p -> model.addAttribute("page", p));
        model.addAttribute("quillEnabled", true);
        return "admin/help";
    }

    @PostMapping("/help")
    public String saveHelp(@RequestParam("body") String body, RedirectAttributes redirectAttributes) {
        try {
            helpService.upsert(body);
            redirectAttributes.addFlashAttribute("successMessage", "Help page saved.");
        } catch (Exception e) {
            log.error("Failed to save help page", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to save help page.");
        }
        return "redirect:/admin/help";
    }

    // -------------------------------------------------------------------------
    // CMS — FAQ
    // -------------------------------------------------------------------------

    @GetMapping("/faq")
    public String adminFaq(Model model) {
        model.addAttribute("faqs", faqService.findAll());
        model.addAttribute("quillEnabled", true);
        return "admin/faq";
    }

    @PostMapping("/faq/create")
    public String createFaq(@ModelAttribute Page page, RedirectAttributes redirectAttributes) {
        try {
            faqService.save(page);
            redirectAttributes.addFlashAttribute("successMessage", "FAQ entry created.");
        } catch (Exception e) {
            log.error("Failed to create FAQ entry", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create FAQ entry.");
        }
        return "redirect:/admin/faq";
    }

    @GetMapping("/faq/{id}/edit-form")
    public String faqForm(@PathVariable int id, Model model) {
        faqService.findById(id).ifPresent(f -> model.addAttribute("faq", f));
        return "admin/faq :: editForm";
    }

    @PostMapping("/faq/{id}/update")
    public String updateFaq(@PathVariable int id,
                            @ModelAttribute Page page,
                            RedirectAttributes redirectAttributes) {
        try {
            faqService.update(id, page);
            redirectAttributes.addFlashAttribute("successMessage", "FAQ entry updated.");
        } catch (Exception e) {
            log.error("Failed to update FAQ entry {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update FAQ entry.");
        }
        return "redirect:/admin/faq";
    }

    @PostMapping("/faq/{id}/delete")
    public String deleteFaq(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            faqService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "FAQ entry deleted.");
        } catch (Exception e) {
            log.error("Failed to delete FAQ entry {}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete FAQ entry.");
        }
        return "redirect:/admin/faq";
    }

    // -------------------------------------------------------------------------
    // Audit
    // -------------------------------------------------------------------------

    @GetMapping("/audit")
    public String audit(Model model) {
        model.addAttribute("auditLogs", auditService.findAll());
        return "admin/audit";
    }
}
