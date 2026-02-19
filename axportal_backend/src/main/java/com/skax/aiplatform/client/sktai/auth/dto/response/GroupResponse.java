package com.skax.aiplatform.client.sktai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 그룹 정보 응답 DTO
 * 
 * <p>SKTAI Auth API에서 그룹 생성, 조회, 수정 시 반환되는 그룹 정보 데이터 구조입니다.
 * 그룹의 모든 기본 정보와 메타데이터를 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>기본 정보</strong>: ID, 이름, 설명, 유형</li>
 *   <li><strong>계층 정보</strong>: 상위 그룹, 하위 그룹</li>
 *   <li><strong>통계 정보</strong>: 멤버 수, 하위 그룹 수</li>
 *   <li><strong>메타데이터</strong>: 생성/수정 일시, 활성 상태</li>
 * </ul>
 * 
 * <h3>그룹 유형:</h3>
 * <ul>
 *   <li><strong>department</strong>: 부서/조직 그룹</li>
 *   <li><strong>project</strong>: 프로젝트 기반 그룹</li>
 *   <li><strong>role</strong>: 역할 기반 그룹</li>
 *   <li><strong>custom</strong>: 사용자 정의 그룹</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 그룹 정보 응답",
    example = """
        {
              "id": "e9d9e62d-2143-45c8-84e2-5c703aa00d90",
              "name": "ygrouptest2",
              "path": "/ygrouptest2"
        }
        """
)
public class GroupResponse {

    /**
     * 그룹 ID
     */
    @JsonProperty("id")
    @Schema(description = "그룹 고유 식별자", example = "group-123")
    private String id;

    /**
     * 그룹 이름
     */
    @JsonProperty("name")
    @Schema(description = "그룹 이름", example = "public")
    private String name;

    /**
     * 그룹 path
     */
    @JsonProperty("path")
    @Schema(description = "그룹 path", example = "/public")
    private String path;
}
