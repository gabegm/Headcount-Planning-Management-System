package com.gaucimaistre.headcount.service;

import com.gaucimaistre.headcount.model.Function;
import com.gaucimaistre.headcount.model.enums.UserType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomeService {

    public List<Function> getFunctionsForUser(int userId, UserType userType) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
