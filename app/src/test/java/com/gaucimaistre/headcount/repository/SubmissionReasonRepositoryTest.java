package com.gaucimaistre.headcount.repository;

import com.gaucimaistre.headcount.AbstractIntegrationTest;
import com.gaucimaistre.headcount.model.SubmissionReason;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class SubmissionReasonRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private SubmissionReasonRepository submissionReasonRepository;

    @Test
    void findAll_returnsAllSeededReasons() {
        List<SubmissionReason> reasons = submissionReasonRepository.findAll();
        // Seed has 8 reasons: Backfill, New Approved Position, Salary Change,
        // Job Title Change, Reporting Line Change, Working Hours Change,
        // New Unapproved Position, Deactivate Position
        assertThat(reasons).hasSize(8);
    }

    @Test
    void findAll_containsExpectedReasonNames() {
        List<SubmissionReason> reasons = submissionReasonRepository.findAll();

        assertThat(reasons).extracting(SubmissionReason::name)
                .contains("Backfill", "New Approved Position", "Salary Change",
                        "Job Title Change", "Reporting Line Change",
                        "Working Hours Change", "New Unapproved Position",
                        "Deactivate Position");
    }

    @Test
    void findById_returnsBackfillReason() {
        Optional<SubmissionReason> result = submissionReasonRepository.findById(1);

        assertThat(result).isPresent();
        assertThat(result.get().name()).isEqualTo("Backfill");
    }

    @Test
    void findById_nonExistent_returnsEmpty() {
        Optional<SubmissionReason> result = submissionReasonRepository.findById(9999);
        assertThat(result).isEmpty();
    }
}
