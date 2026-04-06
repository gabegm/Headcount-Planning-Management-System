package com.gaucimaistre.gatekeeping.service;

import com.gaucimaistre.gatekeeping.model.Company;
import com.gaucimaistre.gatekeeping.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyService {

    private final CompanyRepository companyRepository;

    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    public Optional<Company> findById(int id) {
        return companyRepository.findById(id);
    }

    public int save(Company company) {
        return companyRepository.save(company);
    }

    public void update(Company company) {
        companyRepository.update(company);
    }

    public void update(int id, Company company) {
        companyRepository.update(company);
    }

    public void delete(int id) {
        companyRepository.delete(id);
    }
}
