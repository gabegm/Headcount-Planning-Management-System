package com.gaucimaistre.headcount.model;

import com.gaucimaistre.headcount.model.enums.UserType;

public record User(
        int id,
        String email,
        String password,
        String passwordResetToken,
        UserType type,
        boolean active
) {}
