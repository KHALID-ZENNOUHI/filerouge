package org.dev.filerouge.domain.Enum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum Role {
    ADMINISTRATOR(initAdminPermissions()),
    TEACHER(initTeacherPermissions()),
    STUDENT(initStudentPermissions()),
    PARENT(initParentPermissions());

    @Getter
    private final Set<String> permissions;

    Role(Set<String> permissions) {
        this.permissions = permissions;
    }

    @JsonCreator
    public static Role fromString(String value) {
        if (value == null) {
            return null;
        }

        try {
            return Role.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // Or throw a custom exception if you want to be strict
        }
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }

    private static Set<String> initAdminPermissions() {
        Set<String> permissions = new HashSet<>();
        permissions.add("user:read");
        permissions.add("user:write");
        permissions.add("user:delete");
        permissions.add("class:read");
        permissions.add("class:write");
        permissions.add("class:delete");
        permissions.add("course:read");
        permissions.add("course:write");
        permissions.add("course:delete");
        permissions.add("grade:read");
        permissions.add("grade:write");
        permissions.add("grade:delete");
        permissions.add("attendance:read");
        permissions.add("attendance:write");
        permissions.add("attendance:delete");
        permissions.add("schedule:read");
        permissions.add("schedule:write");
        permissions.add("schedule:delete");
        permissions.add("report:read");
        permissions.add("report:write");
        permissions.add("system:configure");
        return Collections.unmodifiableSet(permissions);
    }

    private static Set<String> initTeacherPermissions() {
        Set<String> permissions = new HashSet<>();
        permissions.add("class:read");
        permissions.add("course:read");
        permissions.add("course:write");
        permissions.add("grade:read");
        permissions.add("grade:write");
        permissions.add("attendance:read");
        permissions.add("attendance:write");
        permissions.add("schedule:read");
        permissions.add("student:read");
        permissions.add("report:read");
        permissions.add("report:write");
        permissions.add("announcement:write");
        return Collections.unmodifiableSet(permissions);
    }

    private static Set<String> initStudentPermissions() {
        Set<String> permissions = new HashSet<>();
        permissions.add("course:read");
        permissions.add("grade:read");
        permissions.add("attendance:read");
        permissions.add("schedule:read");
        permissions.add("assignment:read");
        permissions.add("assignment:submit");
        permissions.add("announcement:read");
        return Collections.unmodifiableSet(permissions);
    }

    private static Set<String> initParentPermissions() {
        Set<String> permissions = new HashSet<>();
        permissions.add("student:read");
        permissions.add("grade:read");
        permissions.add("attendance:read");
        permissions.add("schedule:read");
        permissions.add("announcement:read");
        permissions.add("teacher:contact");
        return Collections.unmodifiableSet(permissions);
    }
}