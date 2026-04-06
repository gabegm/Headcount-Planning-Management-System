package com.gaucimaistre.gatekeeping.repository;

import com.gaucimaistre.gatekeeping.AbstractIntegrationTest;
import com.gaucimaistre.gatekeeping.model.Position;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class PositionRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private PositionRepository positionRepository;

    // Seed data IDs (all lookup tables start from 1 in V2 seed)
    private static final int STATUS_ID = 1;           // 'Occupied'
    private static final int RECRUITMENT_STATUS_ID = 1; // 'Contracted'
    private static final int PILLAR_ID = 1;           // 'Business Framework'
    private static final int COMPANY_ID = 1;          // 'GM Malta'
    private static final int DEPARTMENT_ID = 1;       // 'Tech'
    private static final int FUNCTION_ID = 1;         // 'Tech - Infrastructure'

    @Test
    void findAll_returnsSeededPositions() {
        List<Position> positions = positionRepository.findAll();
        // Seed data: 13 actual (is_budget=FALSE) + 14 budget (is_budget=TRUE)
        assertThat(positions).hasSize(27);
    }

    @Test
    void save_andFindByNumber() {
        Position position = buildPosition("POS-001", false);
        positionRepository.save(position);

        Optional<Position> found = positionRepository.findByNumber("POS-001", false);
        assertThat(found).isPresent();
        assertThat(found.get().number()).isEqualTo("POS-001");
        assertThat(found.get().isBudget()).isFalse();
        assertThat(found.get().title()).isEqualTo("Test Position");
    }

    @Test
    void findBudget_returnsOnlyBudgetPositions() {
        positionRepository.save(buildPosition("POS-BUDGET-1", true));
        positionRepository.save(buildPosition("POS-REGULAR-1", false));

        List<Position> budgetPositions = positionRepository.findBudget();
        // Seed has 14 budget positions; we add 1 more above
        assertThat(budgetPositions).hasSize(15);
        assertThat(budgetPositions).anyMatch(p -> p.number().equals("POS-BUDGET-1") && p.isBudget());
        assertThat(budgetPositions).noneMatch(p -> p.number().equals("POS-REGULAR-1") && p.isBudget());
    }

    private Position buildPosition(String number, boolean isBudget) {
        return new Position(
                0, STATUS_ID, RECRUITMENT_STATUS_ID, number,
                PILLAR_ID, COMPANY_ID, DEPARTMENT_ID, FUNCTION_ID,
                isBudget, "Test Position",
                "CEO", "CEO", "John Doe",
                40, null, null,
                null, null, null, null, null, null
        );
    }
}
