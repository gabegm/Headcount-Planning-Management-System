package com.gaucimaistre.headcount.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminBudgetService {

    private final CsvImportService csvImportService;

    public String importBudget(MultipartFile file) {
        CsvImportService.ImportResult result = csvImportService.importPositions(file, true);
        return "Imported: %d, Skipped: %d, Errors: %d".formatted(
                result.imported(), result.skipped(), result.errors().size());
    }
}
