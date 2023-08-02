package com.arnabk.bgapp.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

    ADMIN_READ("admin_read"),

    ADMIN_UPDATE("admin_update"),

    ADMIN_CREATE("admin_create"),

    ADMIN_DELETE("admin_delete"),

    USER_READ("user_read");

    @Getter
    private final String permission;
}
