package com.gaucimaistre.headcount.service;

import com.gaucimaistre.headcount.model.Department;
import com.gaucimaistre.headcount.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    public Optional<Department> findById(int id) {
        return departmentRepository.findById(id);
    }

    public int save(Department department) {
        return departmentRepository.save(department);
    }

    public void update(Department department) {
        departmentRepository.update(department);
    }

    public void update(int id, Department department) {
        departmentRepository.update(department);
    }

    public void delete(int id) {
        departmentRepository.delete(id);
    }
}
