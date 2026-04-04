package com.gaucimaistre.headcount.model.enums;

public enum PositionStatusType {
    OCCUPIED("Occupied"),
    VACANT("Vacant"),
    EXTERNAL("External"),
    LEAVER("Leaver"),
    PENDING_APPROVAL("Pending Approval");

    private final String displayName;

    PositionStatusType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
