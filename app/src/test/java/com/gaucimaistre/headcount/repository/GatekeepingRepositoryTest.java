package com.gaucimaistre.headcount.repository;

import com.gaucimaistre.headcount.AbstractIntegrationTest;
import com.gaucimaistre.headcount.model.Gatekeeping;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class GatekeepingRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private GatekeepingRepository gatekeepingRepository;

    @Test
    void findAll_returnsSeededGatekeepingCycles() {
        List<Gatekeeping> cycles = gatekeepingRepository.findAll();
        // Seed data has 5 gatekeeping cycles (Q4 2025 through Q4 2026)
        assertThat(cycles).hasSize(5);
    }

    @Test
    void findAll_isOrderedByDateDescending() {
        List<Gatekeeping> cycles = gatekeepingRepository.findAll();
        assertThat(cycles.get(0).notes()).isEqualTo("Q4 2026 Gatekeeping");
        assertThat(cycles.get(cycles.size() - 1).notes()).isEqualTo("Q4 2025 Gatekeeping");
    }

    @Test
    void save_andFindById_returnsInsertedCycle() {
        Gatekeeping cycle = new Gatekeeping(0,
                LocalDate.of(2027, 1, 31),
                LocalDate.of(2027, 1, 17),
                "Q1 2027 Gatekeeping");

        int id = gatekeepingRepository.save(cycle);

        assertThat(id).isPositive();
        Optional<Gatekeeping> found = gatekeepingRepository.findById(id);
        assertThat(found).isPresent();
        assertThat(found.get().notes()).isEqualTo("Q1 2027 Gatekeeping");
        assertThat(found.get().date()).isEqualTo(LocalDate.of(2027, 1, 31));
        assertThat(found.get().submissionDeadline()).isEqualTo(LocalDate.of(2027, 1, 17));
    }

    @Test
    void delete_removesGatekeepingCycle() {
        Gatekeeping cycle = new Gatekeeping(0,
                LocalDate.of(2027, 4, 30),
                LocalDate.of(2027, 4, 16),
                "Q2 2027 Gatekeeping");
        int id = gatekeepingRepository.save(cycle);
        assertThat(gatekeepingRepository.findById(id)).isPresent();

        gatekeepingRepository.delete(id);

        assertThat(gatekeepingRepository.findById(id)).isEmpty();
    }

    @Test
    void findById_nonExistent_returnsEmpty() {
        Optional<Gatekeeping> result = gatekeepingRepository.findById(9999);
        assertThat(result).isEmpty();
    }
}
