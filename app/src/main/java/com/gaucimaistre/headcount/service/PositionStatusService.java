package com.gaucimaistre.headcount.service;

import com.gaucimaistre.headcount.model.PositionStatus;
import com.gaucimaistre.headcount.repository.PositionStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PositionStatusService {

    private final PositionStatusRepository positionStatusRepository;

    public List<PositionStatus> findAll() {
        return positionStatusRepository.findAll();
    }

    public Optional<PositionStatus> findById(int id) {
        return positionStatusRepository.findById(id);
    }

    public void create(PositionStatus status) {
        positionStatusRepository.save(status);
    }

    public void update(int id, PositionStatus status) {
        positionStatusRepository.update(new PositionStatus(id, status.name()));
    }

    public void delete(int id) {
        positionStatusRepository.delete(id);
    }
}
