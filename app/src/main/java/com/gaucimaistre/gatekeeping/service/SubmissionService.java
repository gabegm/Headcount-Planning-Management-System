package com.gaucimaistre.gatekeeping.service;

import com.gaucimaistre.gatekeeping.model.Position;
import com.gaucimaistre.gatekeeping.model.Submission;
import com.gaucimaistre.gatekeeping.model.SubmissionChange;
import com.gaucimaistre.gatekeeping.model.SubmissionStatus;
import com.gaucimaistre.gatekeeping.model.SubmissionView;
import com.gaucimaistre.gatekeeping.repository.PositionRepository;
import com.gaucimaistre.gatekeeping.repository.SubmissionChangeRepository;
import com.gaucimaistre.gatekeeping.repository.SubmissionRepository;
import com.gaucimaistre.gatekeeping.repository.SubmissionStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final SubmissionChangeRepository submissionChangeRepository;
    private final PositionRepository positionRepository;
    private final SubmissionStatusRepository submissionStatusRepository;
    private final AuditService auditService;

    public List<Submission> findAll() {
        return submissionRepository.findAll();
    }

    public List<Submission> findByUser(int userId) {
        return submissionRepository.findBySubmitterId(userId);
    }

    public List<Submission> findBySubmitterId(int userId) {
        return submissionRepository.findBySubmitterId(userId);
    }

    public List<SubmissionView> findViewsBySubmitterId(int userId) {
        return submissionRepository.findViewsBySubmitterId(userId);
    }

    public List<SubmissionView> findAllViews() {
        return submissionRepository.findAllViews();
    }

    public Optional<Submission> findById(int id) {
        return submissionRepository.findById(id);
    }

    @Transactional
    public int create(Submission submission, List<SubmissionChange> changes) {
        int submissionId = submissionRepository.save(submission);
        if (changes != null) {
            for (SubmissionChange c : changes) {
                SubmissionChange withId = new SubmissionChange(0, submissionId, null, c.field(), c.change());
                submissionChangeRepository.save(withId);
            }
        }
        return submissionId;
    }

    public void save(Submission submission) {
        submissionRepository.save(submission);
    }

    public void updateStatus(int id, int statusId, String comment) {
        submissionRepository.updateStatus(id, statusId, comment);
    }

    public void delete(int id) {
        submissionRepository.delete(id);
    }

    public List<SubmissionChange> getChanges(int submissionId) {
        return submissionChangeRepository.findBySubmissionId(submissionId);
    }

    public List<SubmissionChange> getChangesBySubmissionId(int submissionId) {
        return submissionChangeRepository.findBySubmissionId(submissionId);
    }

    public List<Submission> findEffectiveDateToday() {
        return submissionRepository.findEffectiveDateToday();
    }

    public void applyEffectiveDateSubmissions() {
        List<Submission> todaySubmissions = submissionRepository.findEffectiveDateToday();
        if (todaySubmissions.isEmpty()) {
            return;
        }

        List<SubmissionStatus> allStatuses = submissionStatusRepository.findAll();
        Optional<SubmissionStatus> approvedStatus = allStatuses.stream()
                .filter(s -> s.name().toUpperCase().equals("APPROVED"))
                .findFirst();

        if (approvedStatus.isEmpty()) {
            log.warn("applyEffectiveDateSubmissions: no APPROVED status found in database");
            return;
        }
        int approvedStatusId = approvedStatus.get().id();

        for (Submission submission : todaySubmissions) {
            if (submission.statusId() != approvedStatusId) {
                continue;
            }
            try {
                Optional<Position> maybePosition = positionRepository.findByNumber(submission.positionId(), false);
                if (maybePosition.isEmpty()) {
                    log.warn("applyEffectiveDateSubmissions: position not found: {}", submission.positionId());
                    continue;
                }
                Position position = maybePosition.get();
                List<SubmissionChange> changes = submissionChangeRepository.findBySubmissionId(submission.id());
                for (SubmissionChange change : changes) {
                    try {
                        position = applyChange(position, change);
                    } catch (Exception e) {
                        log.error("applyEffectiveDateSubmissions: failed to apply change {} on submission {}",
                                change.field(), submission.id(), e);
                    }
                }
                positionRepository.update(position);
            } catch (Exception e) {
                log.error("applyEffectiveDateSubmissions: failed to process submission {}", submission.id(), e);
            }
        }
    }

    private Position applyChange(Position position, SubmissionChange change) {
        String value = change.change();
        return switch (change.field()) {
            case "title" -> new Position(position.id(), position.statusId(), position.recruitmentStatusId(),
                    position.number(), position.pillarId(), position.companyId(), position.departmentId(),
                    position.functionId(), position.isBudget(), value,
                    position.functionalReportingLine(), position.disciplinaryReportingLine(),
                    position.holder(), position.hours(), position.startDate(), position.endDate(),
                    position.salary(), position.fringeBenefit(), position.socialSecurityContribution(),
                    position.performanceBonus(), position.superBonus(), position.managementBonus());
            case "holder" -> new Position(position.id(), position.statusId(), position.recruitmentStatusId(),
                    position.number(), position.pillarId(), position.companyId(), position.departmentId(),
                    position.functionId(), position.isBudget(), position.title(),
                    position.functionalReportingLine(), position.disciplinaryReportingLine(),
                    value, position.hours(), position.startDate(), position.endDate(),
                    position.salary(), position.fringeBenefit(), position.socialSecurityContribution(),
                    position.performanceBonus(), position.superBonus(), position.managementBonus());
            case "hours" -> new Position(position.id(), position.statusId(), position.recruitmentStatusId(),
                    position.number(), position.pillarId(), position.companyId(), position.departmentId(),
                    position.functionId(), position.isBudget(), position.title(),
                    position.functionalReportingLine(), position.disciplinaryReportingLine(),
                    position.holder(), Integer.parseInt(value), position.startDate(), position.endDate(),
                    position.salary(), position.fringeBenefit(), position.socialSecurityContribution(),
                    position.performanceBonus(), position.superBonus(), position.managementBonus());
            case "salary" -> new Position(position.id(), position.statusId(), position.recruitmentStatusId(),
                    position.number(), position.pillarId(), position.companyId(), position.departmentId(),
                    position.functionId(), position.isBudget(), position.title(),
                    position.functionalReportingLine(), position.disciplinaryReportingLine(),
                    position.holder(), position.hours(), position.startDate(), position.endDate(),
                    new BigDecimal(value), position.fringeBenefit(), position.socialSecurityContribution(),
                    position.performanceBonus(), position.superBonus(), position.managementBonus());
            case "fringe_benefit" -> new Position(position.id(), position.statusId(), position.recruitmentStatusId(),
                    position.number(), position.pillarId(), position.companyId(), position.departmentId(),
                    position.functionId(), position.isBudget(), position.title(),
                    position.functionalReportingLine(), position.disciplinaryReportingLine(),
                    position.holder(), position.hours(), position.startDate(), position.endDate(),
                    position.salary(), new BigDecimal(value), position.socialSecurityContribution(),
                    position.performanceBonus(), position.superBonus(), position.managementBonus());
            case "social_security_contribution" -> new Position(position.id(), position.statusId(),
                    position.recruitmentStatusId(), position.number(), position.pillarId(), position.companyId(),
                    position.departmentId(), position.functionId(), position.isBudget(), position.title(),
                    position.functionalReportingLine(), position.disciplinaryReportingLine(),
                    position.holder(), position.hours(), position.startDate(), position.endDate(),
                    position.salary(), position.fringeBenefit(), new BigDecimal(value),
                    position.performanceBonus(), position.superBonus(), position.managementBonus());
            case "performance_bonus" -> new Position(position.id(), position.statusId(),
                    position.recruitmentStatusId(), position.number(), position.pillarId(), position.companyId(),
                    position.departmentId(), position.functionId(), position.isBudget(), position.title(),
                    position.functionalReportingLine(), position.disciplinaryReportingLine(),
                    position.holder(), position.hours(), position.startDate(), position.endDate(),
                    position.salary(), position.fringeBenefit(), position.socialSecurityContribution(),
                    new BigDecimal(value), position.superBonus(), position.managementBonus());
            case "super_bonus" -> new Position(position.id(), position.statusId(),
                    position.recruitmentStatusId(), position.number(), position.pillarId(), position.companyId(),
                    position.departmentId(), position.functionId(), position.isBudget(), position.title(),
                    position.functionalReportingLine(), position.disciplinaryReportingLine(),
                    position.holder(), position.hours(), position.startDate(), position.endDate(),
                    position.salary(), position.fringeBenefit(), position.socialSecurityContribution(),
                    position.performanceBonus(), new BigDecimal(value), position.managementBonus());
            case "management_bonus" -> new Position(position.id(), position.statusId(),
                    position.recruitmentStatusId(), position.number(), position.pillarId(), position.companyId(),
                    position.departmentId(), position.functionId(), position.isBudget(), position.title(),
                    position.functionalReportingLine(), position.disciplinaryReportingLine(),
                    position.holder(), position.hours(), position.startDate(), position.endDate(),
                    position.salary(), position.fringeBenefit(), position.socialSecurityContribution(),
                    position.performanceBonus(), position.superBonus(), new BigDecimal(value));
            case "functional_reporting_line" -> new Position(position.id(), position.statusId(),
                    position.recruitmentStatusId(), position.number(), position.pillarId(), position.companyId(),
                    position.departmentId(), position.functionId(), position.isBudget(), position.title(),
                    value, position.disciplinaryReportingLine(),
                    position.holder(), position.hours(), position.startDate(), position.endDate(),
                    position.salary(), position.fringeBenefit(), position.socialSecurityContribution(),
                    position.performanceBonus(), position.superBonus(), position.managementBonus());
            case "disciplinary_reporting_line" -> new Position(position.id(), position.statusId(),
                    position.recruitmentStatusId(), position.number(), position.pillarId(), position.companyId(),
                    position.departmentId(), position.functionId(), position.isBudget(), position.title(),
                    position.functionalReportingLine(), value,
                    position.holder(), position.hours(), position.startDate(), position.endDate(),
                    position.salary(), position.fringeBenefit(), position.socialSecurityContribution(),
                    position.performanceBonus(), position.superBonus(), position.managementBonus());
            case "start_date" -> new Position(position.id(), position.statusId(),
                    position.recruitmentStatusId(), position.number(), position.pillarId(), position.companyId(),
                    position.departmentId(), position.functionId(), position.isBudget(), position.title(),
                    position.functionalReportingLine(), position.disciplinaryReportingLine(),
                    position.holder(), position.hours(), LocalDate.parse(value), position.endDate(),
                    position.salary(), position.fringeBenefit(), position.socialSecurityContribution(),
                    position.performanceBonus(), position.superBonus(), position.managementBonus());
            case "end_date" -> new Position(position.id(), position.statusId(),
                    position.recruitmentStatusId(), position.number(), position.pillarId(), position.companyId(),
                    position.departmentId(), position.functionId(), position.isBudget(), position.title(),
                    position.functionalReportingLine(), position.disciplinaryReportingLine(),
                    position.holder(), position.hours(), position.startDate(), LocalDate.parse(value),
                    position.salary(), position.fringeBenefit(), position.socialSecurityContribution(),
                    position.performanceBonus(), position.superBonus(), position.managementBonus());
            case "status_id" -> new Position(position.id(), Integer.parseInt(value),
                    position.recruitmentStatusId(), position.number(), position.pillarId(), position.companyId(),
                    position.departmentId(), position.functionId(), position.isBudget(), position.title(),
                    position.functionalReportingLine(), position.disciplinaryReportingLine(),
                    position.holder(), position.hours(), position.startDate(), position.endDate(),
                    position.salary(), position.fringeBenefit(), position.socialSecurityContribution(),
                    position.performanceBonus(), position.superBonus(), position.managementBonus());
            case "recruitment_status_id" -> new Position(position.id(), position.statusId(),
                    Integer.parseInt(value), position.number(), position.pillarId(), position.companyId(),
                    position.departmentId(), position.functionId(), position.isBudget(), position.title(),
                    position.functionalReportingLine(), position.disciplinaryReportingLine(),
                    position.holder(), position.hours(), position.startDate(), position.endDate(),
                    position.salary(), position.fringeBenefit(), position.socialSecurityContribution(),
                    position.performanceBonus(), position.superBonus(), position.managementBonus());
            default -> {
                log.warn("applyChange: unknown field '{}', skipping", change.field());
                yield position;
            }
        };
    }
}
