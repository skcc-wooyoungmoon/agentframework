package com.skax.aiplatform.entity.project;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProjectStatus {
    REQUESTED("요청"),
    ONGOING("진행중"),
    COMPLETED("종료"),
    REJECTED("반려");

    private final String description;
}

