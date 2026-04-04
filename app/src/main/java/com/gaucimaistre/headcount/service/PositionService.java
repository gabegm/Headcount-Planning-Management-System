package com.gaucimaistre.headcount.service;

import com.gaucimaistre.headcount.model.Position;
import com.gaucimaistre.headcount.model.PositionView;
import com.gaucimaistre.headcount.model.enums.UserType;
import com.gaucimaistre.headcount.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PositionService {

    private final PositionRepository positionRepository;
    private final UserService userService;

    public List<Position> findAll(int userId, UserType userType) {
        if (userType == UserType.ADMIN) {
            return positionRepository.findAll();
        }
        List<Integer> functionIds = userService.getFunctionIds(userId);
        if (functionIds.isEmpty()) {
            return List.of();
        }
        return positionRepository.findAllByFunctionIds(functionIds);
    }

    public List<Position> findAllByUserAccess(int userId, UserType userType) {
        return findAll(userId, userType);
    }

    public Optional<Position> findByNumber(String number) {
        return positionRepository.findByNumber(number, false);
    }

    public Optional<Position> findById(int id) {
        return positionRepository.findById(id);
    }

    public List<Position> findBudget() {
        return positionRepository.findBudget();
    }

    public List<PositionView> findAllViews() {
        return positionRepository.findAllViews();
    }

    public List<PositionView> findBudgetViews() {
        return positionRepository.findBudgetViews();
    }

    public int create(Position position) {
        return positionRepository.save(position);
    }

    public void save(Position position) {
        positionRepository.save(position);
    }

    public void update(Position position) {
        positionRepository.update(position);
    }

    public void update(int id, Position position) {
        positionRepository.update(position);
    }

    public void delete(int id) {
        positionRepository.delete(id);
    }

    public boolean canUserAccessPosition(int userId, UserType userType, String positionNumber) {
        if (userType == UserType.ADMIN) {
            return true;
        }
        Optional<Position> maybePosition = positionRepository.findByNumber(positionNumber, false);
        if (maybePosition.isEmpty()) {
            return false;
        }
        List<Integer> functionIds = userService.getFunctionIds(userId);
        return functionIds.contains(maybePosition.get().functionId());
    }
}
