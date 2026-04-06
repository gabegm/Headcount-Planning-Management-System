package com.gaucimaistre.gatekeeping.service;

import com.gaucimaistre.gatekeeping.model.*;
import com.gaucimaistre.gatekeeping.model.Function;
import com.gaucimaistre.gatekeeping.repository.PositionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CsvImportServiceTest {

    @Mock
    private LookupService lookupService;

    @Mock
    private PositionRepository positionRepository;

    @InjectMocks
    private CsvImportService csvImportService;

    @Test
    void importPositions_withEmptyFile_returnsErrorResult() {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "positions.csv", "text/csv", new byte[0]);

        CsvImportService.ImportResult result = csvImportService.importPositions(emptyFile, false);

        assertThat(result.imported()).isZero();
        assertThat(result.errors()).isNotEmpty();
        assertThat(result.errors().get(0)).containsIgnoringCase("empty");
    }

    @Test
    void importPositions_withInvalidFileType_returnsErrorResult() {
        MockMultipartFile xlsxFile = new MockMultipartFile(
                "file", "positions.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "some bytes".getBytes());

        CsvImportService.ImportResult result = csvImportService.importPositions(xlsxFile, false);

        assertThat(result.imported()).isZero();
        assertThat(result.errors()).isNotEmpty();
        assertThat(result.errors().get(0)).containsIgnoringCase("Unsupported file type");
    }

    @Test
    void importPositions_withValidCsv_importsRows() {
        // Stub lookup maps with the seed-data values
        PositionStatus occupiedStatus = new PositionStatus(1, "Occupied");
        RecruitmentStatus contracted = new RecruitmentStatus(1, "Contracted");
        Pillar pillar = new Pillar(1, "Business Framework");
        Company company = new Company(1, 1, "GM Malta");
        Department department = new Department(1, "Tech");
        Function function = new Function(1, "Tech - Infrastructure");

        given(lookupService.findAllPositionStatuses()).willReturn(List.of(occupiedStatus));
        given(lookupService.findAllRecruitmentStatuses()).willReturn(List.of(contracted));
        given(lookupService.findAllPillars()).willReturn(List.of(pillar));
        given(lookupService.findAllCompanies()).willReturn(List.of(company));
        given(lookupService.findAllDepartments()).willReturn(List.of(department));
        given(lookupService.findAllFunctions()).willReturn(List.of(function));

        String csv = """
                number,status,recruitment_status,pillar,company,department,function,title,holder,hours
                POS-001,Occupied,Contracted,Business Framework,GM Malta,Tech,Tech - Infrastructure,Engineer,John Doe,40
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file", "positions.csv", "text/csv", csv.getBytes());

        CsvImportService.ImportResult result = csvImportService.importPositions(file, false);

        assertThat(result.imported()).isEqualTo(1);
        assertThat(result.skipped()).isZero();
        assertThat(result.errors()).isEmpty();
    }
}
