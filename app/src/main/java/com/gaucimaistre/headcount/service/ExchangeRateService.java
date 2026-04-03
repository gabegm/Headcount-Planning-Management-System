package com.gaucimaistre.headcount.service;

import com.gaucimaistre.headcount.model.ExchangeRate;
import com.gaucimaistre.headcount.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;

    public List<ExchangeRate> findAll() {
        return exchangeRateRepository.findAll();
    }

    public Optional<ExchangeRate> findById(int id) {
        return exchangeRateRepository.findById(id);
    }

    public int save(ExchangeRate exchangeRate) {
        return exchangeRateRepository.save(exchangeRate);
    }

    public void update(ExchangeRate exchangeRate) {
        exchangeRateRepository.update(exchangeRate);
    }

    public void update(int id, ExchangeRate exchangeRate) {
        exchangeRateRepository.update(exchangeRate);
    }

    public void delete(int id) {
        exchangeRateRepository.delete(id);
    }
}
