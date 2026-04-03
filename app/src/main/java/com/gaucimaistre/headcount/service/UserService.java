package com.gaucimaistre.headcount.service;

import com.gaucimaistre.headcount.model.User;
import com.gaucimaistre.headcount.model.enums.UserType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    public List<User> findAll() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Optional<User> findById(int id) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void update(int id, UserType type, boolean active, List<Integer> functionIds) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void delete(int id) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
