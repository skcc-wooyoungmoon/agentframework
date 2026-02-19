package com.skax.aiplatform.entity.role;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleStatus {
    ACTIVE("사용중"),
    INACTIVE("미사용");

    private final String description;
}
