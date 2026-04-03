package com.gaucimaistre.headcount.service;

import com.gaucimaistre.headcount.model.Position;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class AdminPositionService {

    public List<Position> findAll() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Optional<Position> findById(int id) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void save(Position position) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void update(int id, Position position) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void delete(int id) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public String importFromCsv(MultipartFile file) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
