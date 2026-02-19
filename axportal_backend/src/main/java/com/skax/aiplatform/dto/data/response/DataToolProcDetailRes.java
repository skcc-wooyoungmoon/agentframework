package com.skax.aiplatform.dto.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 데이터 툴 프로세서 상세 응답 DTO (Public Contract)
 * 실제 응답 스키마에 맞게 필드 구성
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DataToolProcDetailRes {
    private UUID id;
    private String name;
    private String description;
    private String type;
    private String dataType;
    private String rulePattern;
    private String ruleValue;
    private String code;
    private String defaultKey;
    private String projectId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}