package com.gaucimaistre.gatekeeping.service;

import com.gaucimaistre.gatekeeping.model.Position;
import com.gaucimaistre.gatekeeping.model.enums.UserType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private PositionService positionService;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void getChartData_withNoPositions_returnsEmptyMaps() {
        given(positionService.findAll(1, UserType.ADMIN)).willReturn(List.of());

        DashboardData result = dashboardService.getChartData(1, UserType.ADMIN, null);

        assertThat(result.budgetFte()).isEmpty();
        assertThat(result.actualFte()).isEmpty();
        assertThat(result.budgetCost()).isEmpty();
        assertThat(result.actualCost()).isEmpty();
    }

    @Test
    void getChartData_positionWithNullStartDate_isSkipped() {
        Position noDate = buildPosition("POS-001", false, null, 40, null, null, null);
        given(positionService.findAll(1, UserType.ADMIN)).willReturn(List.of(noDate));

        DashboardData result = dashboardService.getChartData(1, UserType.ADMIN, null);

        assertThat(result.actualFte()).isEmpty();
        assertThat(result.actualCost()).isEmpty();
    }

    @Test
    void getChartData_separatesBudgetFromActual() {
        LocalDate date = LocalDate.of(2026, 1, 1);
        Position actual = buildPosition("POS-ACT", false, date, 40, BigDecimal.valueOf(50000), null, null);
        Position budget = buildPosition("POS-BUD", true,  date, 40, BigDecimal.valueOf(60000), null, null);
        given(positionService.findAll(1, UserType.ADMIN)).willReturn(List.of(actual, budget));

        DashboardData result = dashboardService.getChartData(1, UserType.ADMIN, null);

        assertThat(result.actualFte()).containsKey("2026-01-01");
        assertThat(result.budgetFte()).containsKey("2026-01-01");
        assertThat(result.actualFte()).doesNotContainKey("2026-02-01");
    }

    @Test
    void getChartData_fteCalculation_fullTime() {
        LocalDate date = LocalDate.of(2026, 3, 1);
        Position fullTime = buildPosition("POS-FT", false, date, 40, null, null, null);
        given(positionService.findAll(1, UserType.ADMIN)).willReturn(List.of(fullTime));

        DashboardData result = dashboardService.getChartData(1, UserType.ADMIN, null);

        assertThat(result.actualFte().get("2026-03-01")).isEqualTo(1.0);
    }

    @Test
    void getChartData_fteCalculation_partTime() {
        LocalDate date = LocalDate.of(2026, 3, 1);
        Position partTime = buildPosition("POS-PT", false, date, 20, null, null, null);
        given(positionService.findAll(1, UserType.ADMIN)).willReturn(List.of(partTime));

        DashboardData result = dashboardService.getChartData(1, UserType.ADMIN, null);

        assertThat(result.actualFte().get("2026-03-01")).isEqualTo(0.5);
    }

    @Test
    void getChartData_aggregatesCostComponents() {
        LocalDate date = LocalDate.of(2026, 4, 1);
        Position pos = buildPosition("POS-COST", false, date, 40,
                BigDecimal.valueOf(60000),
                BigDecimal.valueOf(5000),
                BigDecimal.valueOf(3000));
        given(positionService.findAll(1, UserType.ADMIN)).willReturn(List.of(pos));

        DashboardData result = dashboardService.getChartData(1, UserType.ADMIN, null);

        assertThat(result.actualCost().get("2026-04-01")).isEqualTo(68000.0);
    }

    @Test
    void getChartData_aggregatesMultiplePositionsSameMonth() {
        LocalDate date = LocalDate.of(2026, 5, 1);
        Position p1 = buildPosition("POS-A", false, date, 40, BigDecimal.valueOf(40000), null, null);
        Position p2 = buildPosition("POS-B", false, date, 40, BigDecimal.valueOf(50000), null, null);
        given(positionService.findAll(1, UserType.ADMIN)).willReturn(List.of(p1, p2));

        DashboardData result = dashboardService.getChartData(1, UserType.ADMIN, null);

        assertThat(result.actualFte().get("2026-05-01")).isEqualTo(2.0);
        assertThat(result.actualCost().get("2026-05-01")).isEqualTo(90000.0);
    }

    @Test
    void getChartData_filtersByFunctionId() {
        LocalDate date = LocalDate.of(2026, 6, 1);
        Position match    = buildPositionWithFunction("POS-F1", false, date, 40, 1);
        Position noMatch  = buildPositionWithFunction("POS-F2", false, date, 40, 2);
        given(positionService.findAll(1, UserType.ADMIN)).willReturn(List.of(match, noMatch));

        DashboardData result = dashboardService.getChartData(1, UserType.ADMIN, 1);

        assertThat(result.actualFte().get("2026-06-01")).isEqualTo(1.0);
    }

    private Position buildPosition(String number, boolean isBudget, LocalDate startDate,
                                   Integer hours, BigDecimal salary,
                                   BigDecimal fringeBenefit, BigDecimal socialSecurity) {
        return new Position(1, 1, 1, number, 1, 1, 1, 1, isBudget, "Title",
                "CEO", "CEO", "John Doe", hours, startDate, null,
                salary, fringeBenefit, socialSecurity, null, null, null);
    }

    private Position buildPositionWithFunction(String number, boolean isBudget, LocalDate startDate,
                                               Integer hours, int functionId) {
        return new Position(1, 1, 1, number, 1, 1, 1, functionId, isBudget, "Title",
                "CEO", "CEO", "John Doe", hours, startDate, null,
                null, null, null, null, null, null);
    }
}
