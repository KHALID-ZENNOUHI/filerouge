package org.dev.filerouge.domain.Enum;

import java.util.Set;
public enum Role {
    ROLE_STUDENT(Set.of("CAN_VIEW_GRADE", "CAN_VIEW_ABSENT")),
    ROLE_TEACHER(Set.of("CAN_ADD_GRADE", "CAN_VIEW_GRADE", "CAN_ADD_ABSENT", "CAN_VIEW_ABSENT")),
    ROLE_PARENT(Set.of("CAN_VIEW_GRADE", "CAN_VIEW_ABSENT")),
    ROLE_ADMINISTRATOR(Set.of("CAN_ADD_GRADE", "CAN_VIEW_GRADE", "CAN_ADD_ABSENT", "CAN_VIEW_ABSENT", "CAN_ADD_TEACHER", "CAN_VIEW_TEACHER","CAN_ADD_STUDENT", "CAN_VIEW_STUDENT"));

    private final Set<String> permissions;

    Role(Set<String> permissions) {
        this.permissions = permissions;
    }

    public Set<String> getPermissions() {
        return permissions;
    }
}

