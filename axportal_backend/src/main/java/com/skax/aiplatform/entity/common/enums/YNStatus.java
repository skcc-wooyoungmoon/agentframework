package com.skax.aiplatform.entity.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Y/N 상태 공통 Enum
 */
@Getter
@RequiredArgsConstructor
public enum YNStatus {

    Y("활성"),
    N("비활성");

    private final String description;
}
