package com.gaucimaistre.headcount.service;

import com.gaucimaistre.headcount.model.Position;
import com.gaucimaistre.headcount.model.enums.UserType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PositionService {

    public List<Position> findAllByUserAccess(int userId, UserType userType) {
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
}
