package com.gaucimaistre.headcount.service;

import com.gaucimaistre.headcount.model.Submission;
import com.gaucimaistre.headcount.model.SubmissionChange;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminSubmissionService {

    public List<Submission> findAll() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Optional<Submission> findById(int id) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void updateStatus(int id, int statusId, String comment) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void delete(int id) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public List<SubmissionChange> getChangesBySubmissionId(int submissionId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
