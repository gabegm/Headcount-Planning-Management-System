package com.gaucimaistre.gatekeeping.service;

import com.gaucimaistre.gatekeeping.model.*;
import com.gaucimaistre.gatekeeping.model.Function;
import com.gaucimaistre.gatekeeping.repository.PositionRepository;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.CSVReaderHeaderAwareBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvImportService {

    private final LookupService lookupService;
    private final PositionRepository positionRepository;

    public record ImportResult(int imported, int skipped, List<String> errors) {}

    public ImportResult importPositions(MultipartFile file, boolean isBudget) {
        if (file == null || file.isEmpty()) {
            return new ImportResult(0, 0, List.of("File is empty or missing"));
        }

        String filename = file.getOriginalFilename();
        if (filename != null) {
            String lower = filename.toLowerCase();
            if (!lower.endsWith(".csv") && !lower.endsWith(".txt") && !lower.endsWith(".tsv")) {
                return new ImportResult(0, 0, List.of("Unsupported file type. Only csv, txt, tsv are allowed."));
            }
        }

        // Load all lookup maps once before processing rows
        Map<String, Integer> statusMap = lookupService.findAllPositionStatuses().stream()
                .collect(Collectors.toMap(s -> s.name().toLowerCase(), PositionStatus::id, (a, b) -> a));
        Map<String, Integer> recruitmentStatusMap = lookupService.findAllRecruitmentStatuses().stream()
                .collect(Collectors.toMap(s -> s.name().toLowerCase(), RecruitmentStatus::id, (a, b) -> a));
        Map<String, Integer> pillarMap = lookupService.findAllPillars().stream()
                .collect(Collectors.toMap(p -> p.name().toLowerCase(), Pillar::id, (a, b) -> a));
        Map<String, Integer> companyMap = lookupService.findAllCompanies().stream()
                .collect(Collectors.toMap(c -> c.name().toLowerCase(), Company::id, (a, b) -> a));
        Map<String, Integer> departmentMap = lookupService.findAllDepartments().stream()
                .collect(Collectors.toMap(d -> d.name().toLowerCase(), Department::id, (a, b) -> a));
        Map<String, Integer> functionMap = lookupService.findAllFunctions().stream()
                .collect(Collectors.toMap(f -> f.name().toLowerCase(), Function::id, (a, b) -> a));

        int imported = 0;
        int skipped = 0;
        List<String> errors = new ArrayList<>();

        try (InputStreamReader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {
            char separator = filename != null && filename.toLowerCase().endsWith(".tsv") ? '\t' : ',';
            CSVReaderHeaderAware csvReader = new CSVReaderHeaderAwareBuilder(reader)
                    .withCSVParser(new CSVParserBuilder().withSeparator(separator).build())
                    .build();

            Map<String, String> row;
            int rowNum = 1;
            while ((row = csvReader.readMap()) != null) {
                rowNum++;
                try {
                    String number = required(row, "number", rowNum, errors);
                    if (number == null) { skipped++; continue; }

                    String statusName = get(row, "status");
                    Integer statusId = lookupId(statusMap, statusName, "status", rowNum, errors);
                    if (statusId == null) { skipped++; continue; }

                    String recruitmentStatusName = get(row, "recruitment_status");
                    Integer recruitmentStatusId = lookupId(recruitmentStatusMap, recruitmentStatusName,
                            "recruitment_status", rowNum, errors);
                    if (recruitmentStatusId == null) { skipped++; continue; }

                    String pillarName = get(row, "pillar");
                    Integer pillarId = lookupId(pillarMap, pillarName, "pillar", rowNum, errors);
                    if (pillarId == null) { skipped++; continue; }

                    String companyName = get(row, "company");
                    Integer companyId = lookupId(companyMap, companyName, "company", rowNum, errors);
                    if (companyId == null) { skipped++; continue; }

                    String departmentName = get(row, "department");
                    Integer departmentId = lookupId(departmentMap, departmentName, "department", rowNum, errors);
                    if (departmentId == null) { skipped++; continue; }

                    String functionName = get(row, "function");
                    Integer functionId = lookupId(functionMap, functionName, "function", rowNum, errors);
                    if (functionId == null) { skipped++; continue; }

                    String title = get(row, "title");
                    String functionalReportingLine = get(row, "functional_reporting_line");
                    String disciplinaryReportingLine = get(row, "disciplinary_reporting_line");
                    String holder = get(row, "holder");

                    Integer hours = parseInteger(row, "hours", rowNum, errors);
                    LocalDate startDate = parseDate(row, "start_date", rowNum, errors);
                    LocalDate endDate = parseDate(row, "end_date", rowNum, errors);
                    BigDecimal salary = parseDecimal(row, "salary");
                    BigDecimal fringeBenefit = parseDecimal(row, "fringe_benefit");
                    BigDecimal socialSecurityContribution = parseDecimal(row, "social_security_contribution");
                    BigDecimal performanceBonus = parseDecimal(row, "performance_bonus");
                    BigDecimal superBonus = parseDecimal(row, "super_bonus");
                    BigDecimal managementBonus = parseDecimal(row, "management_bonus");

                    Position position = new Position(
                            0, statusId, recruitmentStatusId, number, pillarId, companyId,
                            departmentId, functionId, isBudget, title,
                            functionalReportingLine, disciplinaryReportingLine,
                            holder, hours, startDate, endDate,
                            salary, fringeBenefit, socialSecurityContribution,
                            performanceBonus, superBonus, managementBonus
                    );
                    positionRepository.save(position);
                    imported++;
                } catch (Exception e) {
                    String msg = "Row %d: unexpected error: %s".formatted(rowNum, e.getMessage());
                    errors.add(msg);
                    log.error(msg, e);
                    skipped++;
                }
            }
        } catch (Exception e) {
            log.error("CsvImportService: failed to read CSV file", e);
            errors.add("Failed to read file: " + e.getMessage());
        }

        return new ImportResult(imported, skipped, errors);
    }

    private String get(Map<String, String> row, String col) {
        String val = row.get(col);
        return (val == null || val.isBlank()) ? null : val.trim();
    }

    private String required(Map<String, String> row, String col, int rowNum, List<String> errors) {
        String val = get(row, col);
        if (val == null) {
            errors.add("Row %d: missing required column '%s'".formatted(rowNum, col));
            return null;
        }
        return val;
    }

    private Integer lookupId(Map<String, Integer> map, String name, String col, int rowNum, List<String> errors) {
        if (name == null || name.isBlank()) {
            errors.add("Row %d: missing required column '%s'".formatted(rowNum, col));
            return null;
        }
        Integer id = map.get(name.toLowerCase());
        if (id == null) {
            errors.add("Row %d: unknown %s '%s'".formatted(rowNum, col, name));
            return null;
        }
        return id;
    }

    private Integer parseInteger(Map<String, String> row, String col, int rowNum, List<String> errors) {
        String val = get(row, col);
        if (val == null) return null;
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            errors.add("Row %d: invalid integer for '%s': %s".formatted(rowNum, col, val));
            return null;
        }
    }

    private LocalDate parseDate(Map<String, String> row, String col, int rowNum, List<String> errors) {
        String val = get(row, col);
        if (val == null) return null;
        try {
            return LocalDate.parse(val);
        } catch (Exception e) {
            errors.add("Row %d: invalid date for '%s': %s".formatted(rowNum, col, val));
            return null;
        }
    }

    private BigDecimal parseDecimal(Map<String, String> row, String col) {
        String val = get(row, col);
        if (val == null) return null;
        try {
            return new BigDecimal(val);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
