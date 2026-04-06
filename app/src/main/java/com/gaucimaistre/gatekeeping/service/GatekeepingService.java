package com.gaucimaistre.gatekeeping.service;

import com.gaucimaistre.gatekeeping.model.Gatekeeping;
import com.gaucimaistre.gatekeeping.repository.GatekeepingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GatekeepingService {

    private final GatekeepingRepository gatekeepingRepository;

    public List<Gatekeeping> findAll() {
        return gatekeepingRepository.findAll();
    }

    public Optional<Gatekeeping> findById(int id) {
        return gatekeepingRepository.findById(id);
    }

    public Optional<Gatekeeping> findCurrent() {
        return gatekeepingRepository.findCurrent();
    }

    public boolean isSubmissionOpen() {
        return gatekeepingRepository.findCurrent().isPresent();
    }

    public int create(Gatekeeping g) {
        return gatekeepingRepository.save(g);
    }

    public void save(Gatekeeping g) {
        gatekeepingRepository.save(g);
    }

    public void update(Gatekeeping g) {
        gatekeepingRepository.update(g);
    }

    public void update(int id, Gatekeeping g) {
        gatekeepingRepository.update(g);
    }

    public void delete(int id) {
        gatekeepingRepository.delete(id);
    }
}
