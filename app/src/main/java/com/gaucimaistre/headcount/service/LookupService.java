package com.gaucimaistre.headcount.service;

import com.gaucimaistre.headcount.model.*;
import com.gaucimaistre.headcount.model.Function;
import com.gaucimaistre.headcount.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LookupService {

    private final CompanyRepository companyRepository;
    private final PillarRepository pillarRepository;
    private final DepartmentRepository departmentRepository;
    private final FunctionRepository functionRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final PositionStatusRepository positionStatusRepository;
    private final RecruitmentStatusRepository recruitmentStatusRepository;
    private final SubmissionStatusRepository submissionStatusRepository;
    private final SubmissionReasonRepository submissionReasonRepository;

    // --- Company ---
    public List<Company> findAllCompanies() { return companyRepository.findAll(); }
    public Optional<Company> findCompanyById(int id) { return companyRepository.findById(id); }
    public int createCompany(Company c) { return companyRepository.save(c); }
    public void updateCompany(Company c) { companyRepository.update(c); }
    public void deleteCompany(int id) { companyRepository.delete(id); }

    // --- Pillar ---
    public List<Pillar> findAllPillars() { return pillarRepository.findAll(); }
    public int createPillar(Pillar p) { return pillarRepository.save(p); }
    public void updatePillar(Pillar p) { pillarRepository.update(p); }
    public void deletePillar(int id) { pillarRepository.delete(id); }

    // --- Department ---
    public List<Department> findAllDepartments() { return departmentRepository.findAll(); }
    public int createDepartment(Department d) { return departmentRepository.save(d); }
    public void updateDepartment(Department d) { departmentRepository.update(d); }
    public void deleteDepartment(int id) { departmentRepository.delete(id); }

    // --- Function ---
    public List<Function> findAllFunctions() { return functionRepository.findAll(); }
    public int createFunction(Function f) { return functionRepository.save(f); }
    public void updateFunction(Function f) { functionRepository.update(f); }
    public void deleteFunction(int id) { functionRepository.delete(id); }

    // --- ExchangeRate ---
    public List<ExchangeRate> findAllExchangeRates() { return exchangeRateRepository.findAll(); }
    public int createExchangeRate(ExchangeRate e) { return exchangeRateRepository.save(e); }
    public void updateExchangeRate(ExchangeRate e) { exchangeRateRepository.update(e); }
    public void deleteExchangeRate(int id) { exchangeRateRepository.delete(id); }

    // --- Read-only lookups ---
    public List<PositionStatus> findAllPositionStatuses() { return positionStatusRepository.findAll(); }
    public List<RecruitmentStatus> findAllRecruitmentStatuses() { return recruitmentStatusRepository.findAll(); }
    public List<SubmissionStatus> findAllSubmissionStatuses() { return submissionStatusRepository.findAll(); }
    public List<SubmissionReason> findAllSubmissionReasons() { return submissionReasonRepository.findAll(); }
}
