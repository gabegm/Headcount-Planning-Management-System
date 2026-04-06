package com.gaucimaistre.gatekeeping.service;

import com.gaucimaistre.gatekeeping.model.Function;
import com.gaucimaistre.gatekeeping.model.enums.UserType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HomeService {

    private final FunctionService functionService;
    private final UserService userService;

    public List<Function> getFunctionsForUser(int userId, UserType userType) {
        if (userType == UserType.ADMIN) {
            return functionService.findAll();
        }
        List<Integer> functionIds = userService.getFunctionIds(userId);
        return functionService.findAll().stream()
                .filter(f -> functionIds.contains(f.id()))
                .toList();
    }
}
