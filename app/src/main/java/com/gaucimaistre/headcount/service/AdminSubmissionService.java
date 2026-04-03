package com.gaucimaistre.headcount.service;

import com.gaucimaistre.headcount.model.Submission;
import com.gaucimaistre.headcount.model.SubmissionChange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminSubmissionService {

    private final SubmissionService submissionService;

    public List<Submission> findAll() {
        return submissionService.findAll();
    }

    public Optional<Submission> findById(int id) {
        return submissionService.findById(id);
    }

    public void updateStatus(int id, int statusId, String comment) {
        submissionService.updateStatus(id, statusId, comment);
    }

    public void delete(int id) {
        submissionService.delete(id);
    }

    public List<SubmissionChange> getChangesBySubmissionId(int submissionId) {
        return submissionService.getChangesBySubmissionId(submissionId);
    }
}
