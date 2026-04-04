package com.gaucimaistre.headcount.repository;

import com.gaucimaistre.headcount.AbstractIntegrationTest;
import com.gaucimaistre.headcount.model.Submission;
import com.gaucimaistre.headcount.model.SubmissionView;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class SubmissionRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private SubmissionRepository submissionRepository;

    // Seed data constants
    private static final int ADMIN_USER_ID = 1;
    private static final int MANAGER_USER_ID = 2;
    private static final int GATEKEEPING_ID = 2;      // Q1 2026
    private static final int STATUS_ON_HOLD = 1;
    private static final int STATUS_APPROVED = 2;
    private static final int STATUS_REJECTED = 3;
    private static final int REASON_BACKFILL = 1;

    @Test
    void findAll_returnsAllSeededSubmissions() {
        List<Submission> submissions = submissionRepository.findAll();
        // Seed data has 4 submissions
        assertThat(submissions).hasSize(4);
    }

    @Test
    void findViewsBySubmitterId_returnsSubmissionsForAdmin() {
        List<SubmissionView> views = submissionRepository.findViewsBySubmitterId(ADMIN_USER_ID);
        // Admin (id=1) submitted submissions 1 and 3
        assertThat(views).hasSize(2);
        assertThat(views).allMatch(v -> v.submitterId() == ADMIN_USER_ID);
    }

    @Test
    void findViewsBySubmitterId_returnsSubmissionsForManager() {
        List<SubmissionView> views = submissionRepository.findViewsBySubmitterId(MANAGER_USER_ID);
        // Manager (id=2) submitted submissions 2 and 4
        assertThat(views).hasSize(2);
        assertThat(views).allMatch(v -> v.submitterId() == MANAGER_USER_ID);
    }

    @Test
    void findAllViews_returnsAllSubmissionsWithResolvedNames() {
        List<SubmissionView> views = submissionRepository.findAllViews();

        assertThat(views).hasSize(4);
        assertThat(views).allMatch(v -> v.statusName() != null && !v.statusName().isBlank());
        assertThat(views).allMatch(v -> v.reasonName() != null && !v.reasonName().isBlank());
    }

    @Test
    void save_andFindById_returnsInsertedSubmission() {
        Submission submission = new Submission(0, ADMIN_USER_ID, GATEKEEPING_ID, "BE001",
                STATUS_ON_HOLD, REASON_BACKFILL,
                "Test rationale for backfill", LocalDate.of(2026, 6, 1), null, null);

        int id = submissionRepository.save(submission);

        assertThat(id).isPositive();
        Optional<Submission> found = submissionRepository.findById(id);
        assertThat(found).isPresent();
        assertThat(found.get().positionId()).isEqualTo("BE001");
        assertThat(found.get().submitterId()).isEqualTo(ADMIN_USER_ID);
        assertThat(found.get().statusId()).isEqualTo(STATUS_ON_HOLD);
        assertThat(found.get().rationale()).isEqualTo("Test rationale for backfill");
    }

    @Test
    void updateStatus_changesStatusAndSetsComment() {
        Submission submission = new Submission(0, ADMIN_USER_ID, GATEKEEPING_ID, "FE001",
                STATUS_ON_HOLD, REASON_BACKFILL,
                "Needs review", LocalDate.of(2026, 7, 1), null, null);
        int id = submissionRepository.save(submission);

        submissionRepository.updateStatus(id, STATUS_APPROVED, "Approved by committee");

        Optional<Submission> updated = submissionRepository.findById(id);
        assertThat(updated).isPresent();
        assertThat(updated.get().statusId()).isEqualTo(STATUS_APPROVED);
        assertThat(updated.get().comment()).isEqualTo("Approved by committee");
    }

    @Test
    void findById_nonExistent_returnsEmpty() {
        Optional<Submission> result = submissionRepository.findById(9999);
        assertThat(result).isEmpty();
    }
}
