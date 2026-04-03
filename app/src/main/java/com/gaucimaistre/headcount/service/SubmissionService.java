package com.gaucimaistre.headcount.service;

import com.gaucimaistre.headcount.model.Submission;
import com.gaucimaistre.headcount.model.SubmissionChange;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubmissionService {

    public List<Submission> findBySubmitterId(int submitterId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Optional<Submission> findById(int id) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void save(Submission submission) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public List<SubmissionChange> getChangesBySubmissionId(int submissionId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
