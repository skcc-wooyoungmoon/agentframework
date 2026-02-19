package com.skax.aiplatform.entity.role;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleType {
    DEFAULT("사전 정의"),
    CUSTOM("사용자 정의");

    private final String description;
}
