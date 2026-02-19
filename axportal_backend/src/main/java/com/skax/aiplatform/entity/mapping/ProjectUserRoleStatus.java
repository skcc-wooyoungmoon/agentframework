package com.skax.aiplatform.entity.mapping;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProjectUserRoleStatus {

    ACTIVE("활성"),
    INACTIVE("비활성");

    private final String description;
}
