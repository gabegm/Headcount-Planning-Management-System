package com.gaucimaistre.gatekeeping.service;

import com.gaucimaistre.gatekeeping.model.RecruitmentStatus;
import com.gaucimaistre.gatekeeping.repository.RecruitmentStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecruitmentStatusService {

    private final RecruitmentStatusRepository recruitmentStatusRepository;

    public List<RecruitmentStatus> findAll() {
        return recruitmentStatusRepository.findAll();
    }

    public Optional<RecruitmentStatus> findById(int id) {
        return recruitmentStatusRepository.findById(id);
    }
}
