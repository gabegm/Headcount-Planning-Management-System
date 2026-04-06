package com.gaucimaistre.gatekeeping.service;

import com.gaucimaistre.gatekeeping.model.Function;
import com.gaucimaistre.gatekeeping.repository.FunctionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FunctionService {

    private final FunctionRepository functionRepository;

    public List<Function> findAll() {
        return functionRepository.findAll();
    }

    public Optional<Function> findById(int id) {
        return functionRepository.findById(id);
    }

    public int save(Function function) {
        return functionRepository.save(function);
    }

    public void update(Function function) {
        functionRepository.update(function);
    }

    public void update(int id, Function function) {
        functionRepository.update(function);
    }

    public void delete(int id) {
        functionRepository.delete(id);
    }
}
