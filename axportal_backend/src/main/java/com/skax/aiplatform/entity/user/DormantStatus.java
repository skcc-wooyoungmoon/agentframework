package com.skax.aiplatform.entity.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 계정 상태
 */
@Getter
@RequiredArgsConstructor
public enum DormantStatus {

    ACTIVE("활성"),
    DORMANT("휴면"),
    WITHDRAW("탈퇴");

    private final String description;
}
