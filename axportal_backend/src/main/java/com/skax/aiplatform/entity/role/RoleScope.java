package com.skax.aiplatform.entity.role;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 역할 범위
 */
@Getter
@RequiredArgsConstructor
public enum RoleScope {

    PORTAL("포탈 전역 역할"),
    PROJECT("프로젝트별 역할");

    private final String description;
}
