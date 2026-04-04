package com.gaucimaistre.headcount.service;

import com.gaucimaistre.headcount.model.SubmissionStatus;
import com.gaucimaistre.headcount.repository.SubmissionStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionStatusService {

    private final SubmissionStatusRepository submissionStatusRepository;

    public List<SubmissionStatus> findAll() {
        return submissionStatusRepository.findAll();
    }

    public Optional<SubmissionStatus> findById(int id) {
        return submissionStatusRepository.findById(id);
    }

    public void create(SubmissionStatus status) {
        submissionStatusRepository.save(status);
    }

    public void update(int id, SubmissionStatus status) {
        submissionStatusRepository.update(new SubmissionStatus(id, status.name()));
    }

    public void delete(int id) {
        submissionStatusRepository.delete(id);
    }
}
