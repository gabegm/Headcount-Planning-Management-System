package com.gaucimaistre.headcount.service;

import com.gaucimaistre.headcount.model.Position;
import com.gaucimaistre.headcount.model.enums.UserType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminPositionService {

    private final PositionService positionService;
    private final CsvImportService csvImportService;

    public List<Position> findAll() {
        return positionService.findAll(0, UserType.ADMIN);
    }

    public Optional<Position> findById(int id) {
        return positionService.findById(id);
    }

    public void save(Position position) {
        positionService.save(position);
    }

    public void update(int id, Position position) {
        positionService.update(position);
    }

    public void delete(int id) {
        positionService.delete(id);
    }

    public String importFromCsv(MultipartFile file) {
        CsvImportService.ImportResult result = csvImportService.importPositions(file, false);
        return "Imported: %d, Skipped: %d, Errors: %d".formatted(
                result.imported(), result.skipped(), result.errors().size());
    }
}
