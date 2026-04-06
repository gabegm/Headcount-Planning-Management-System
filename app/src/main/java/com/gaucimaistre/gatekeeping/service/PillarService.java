package com.gaucimaistre.gatekeeping.service;

import com.gaucimaistre.gatekeeping.model.Pillar;
import com.gaucimaistre.gatekeeping.repository.PillarRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PillarService {

    private final PillarRepository pillarRepository;

    public List<Pillar> findAll() {
        return pillarRepository.findAll();
    }

    public Optional<Pillar> findById(int id) {
        return pillarRepository.findById(id);
    }

    public int save(Pillar pillar) {
        return pillarRepository.save(pillar);
    }

    public void update(Pillar pillar) {
        pillarRepository.update(pillar);
    }

    public void update(int id, Pillar pillar) {
        pillarRepository.update(pillar);
    }

    public void delete(int id) {
        pillarRepository.delete(id);
    }
}
