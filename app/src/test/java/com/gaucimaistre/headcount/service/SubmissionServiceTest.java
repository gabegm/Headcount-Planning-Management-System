package com.gaucimaistre.headcount.service;

import com.gaucimaistre.headcount.model.Submission;
import com.gaucimaistre.headcount.model.SubmissionChange;
import com.gaucimaistre.headcount.model.SubmissionStatus;
import com.gaucimaistre.headcount.model.SubmissionView;
import com.gaucimaistre.headcount.repository.PositionRepository;
import com.gaucimaistre.headcount.repository.SubmissionChangeRepository;
import com.gaucimaistre.headcount.repository.SubmissionRepository;
import com.gaucimaistre.headcount.repository.SubmissionStatusRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private SubmissionChangeRepository submissionChangeRepository;

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private SubmissionStatusRepository submissionStatusRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private SubmissionService submissionService;

    @Test
    void create_savesSubmissionAndChanges() {
        Submission submission = buildSubmission(0);
        List<SubmissionChange> changes = List.of(
                new SubmissionChange(0, 0, null, "title", "Engineer"),
                new SubmissionChange(0, 0, null, "holder", "Jane Doe")
        );
        given(submissionRepository.save(submission)).willReturn(10);

        int id = submissionService.create(submission, changes);

        assertThat(id).isEqualTo(10);
        verify(submissionRepository).save(submission);
        verify(submissionChangeRepository).save(new SubmissionChange(0, 10, null, "title", "Engineer"));
        verify(submissionChangeRepository).save(new SubmissionChange(0, 10, null, "holder", "Jane Doe"));
    }

    @Test
    void create_withNullChanges_savesSubmissionOnly() {
        Submission submission = buildSubmission(0);
        given(submissionRepository.save(submission)).willReturn(5);

        int id = submissionService.create(submission, null);

        assertThat(id).isEqualTo(5);
        verify(submissionRepository).save(submission);
        verify(submissionChangeRepository, never()).save(any());
    }

    @Test
    void findViewsBySubmitterId_delegatesToRepository() {
        int userId = 1;
        SubmissionView view = buildSubmissionView(1, userId);
        given(submissionRepository.findViewsBySubmitterId(userId)).willReturn(List.of(view));

        List<SubmissionView> result = submissionService.findViewsBySubmitterId(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).submitterId()).isEqualTo(userId);
        verify(submissionRepository).findViewsBySubmitterId(userId);
    }

    @Test
    void applyEffectiveDateSubmissions_withNoSubmissions_returnsEarly() {
        given(submissionRepository.findEffectiveDateToday()).willReturn(Collections.emptyList());

        submissionService.applyEffectiveDateSubmissions();

        verify(submissionRepository).findEffectiveDateToday();
        verify(submissionStatusRepository, never()).findAll();
    }

    @Test
    void applyEffectiveDateSubmissions_withNonApprovedSubmission_skipsProcessing() {
        int onHoldStatusId = 1;
        Submission onHoldSubmission = new Submission(
                1, 1, 1, "POS-001", onHoldStatusId, 1,
                "Some rationale", LocalDate.now(), null, null
        );
        given(submissionRepository.findEffectiveDateToday()).willReturn(List.of(onHoldSubmission));
        SubmissionStatus approvedStatus = new SubmissionStatus(2, "APPROVED");
        given(submissionStatusRepository.findAll()).willReturn(List.of(
                new SubmissionStatus(onHoldStatusId, "ON-HOLD"),
                approvedStatus
        ));

        submissionService.applyEffectiveDateSubmissions();

        verify(positionRepository, never()).findByNumber(anyString(), anyBoolean());
    }

    private Submission buildSubmission(int id) {
        return new Submission(id, 1, 1, "POS-001", 1, 1,
                "Test rationale", LocalDate.of(2026, 1, 1), null, null);
    }

    private SubmissionView buildSubmissionView(int id, int submitterId) {
        return new SubmissionView(id, submitterId, "user@example.com", 1, "POS-001", 2, "Approved",
                1, "Backfill", "Test rationale", LocalDate.of(2026, 1, 1), null, null);
    }
}
