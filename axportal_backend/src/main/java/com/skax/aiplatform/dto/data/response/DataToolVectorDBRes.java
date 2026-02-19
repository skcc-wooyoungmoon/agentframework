package com.skax.aiplatform.dto.data.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 벡터 데이터베이스 응답 클래스
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "벡터 데이터베이스 응답 클래스")
public class DataToolVectorDBRes {
        @Schema(description = "벡터 데이터베이스 이름")
        private String name;
        
        @Schema(description = "벡터 데이터베이스 타입")
        private String type;
        
        @Schema(description = "벡터 데이터베이스 ID")
        private String id;
        
        @Schema(description = "프로젝트 ID")
        private String projectId;

        @Schema(description = "생성 시간")
        private String createdAt;

        @Schema(description = "생성자")
        private String createdBy;

        @Schema(description = "생성 시간")
        private String updatedAt;

        @Schema(description = "수정자")
        private String updatedBy;

        @Schema(description = "삭제 여부")
        private Boolean isDeleted;

        @Schema(description = "기본 여부")
        private Boolean isDefault;
}
