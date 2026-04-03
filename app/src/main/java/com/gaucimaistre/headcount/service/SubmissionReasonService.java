package com.gaucimaistre.headcount.service;

import com.gaucimaistre.headcount.model.SubmissionReason;
import com.gaucimaistre.headcount.repository.SubmissionReasonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionReasonService {

    private final SubmissionReasonRepository submissionReasonRepository;

    public List<SubmissionReason> findAll() {
        return submissionReasonRepository.findAll();
    }

    public Optional<SubmissionReason> findById(int id) {
        return submissionReasonRepository.findById(id);
    }
}
