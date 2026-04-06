package com.gaucimaistre.gatekeeping.model;

import com.gaucimaistre.gatekeeping.model.enums.UserType;

public record User(
        int id,
        String email,
        String password,
        String passwordResetToken,
        UserType type,
        boolean active
) {}
