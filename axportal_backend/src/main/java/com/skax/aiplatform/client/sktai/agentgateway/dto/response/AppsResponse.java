package com.skax.aiplatform.client.sktai.agentgateway.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Agent Gateway Apps 목록 응답 DTO
 * 
 * <p>SKTAI Agent Gateway에서 사용 가능한 애플리케이션 목록을 담는 응답 데이터 구조입니다.
 * 각 애플리케이션의 기본 정보와 메타데이터를 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>apps</strong>: 애플리케이션 목록</li>
 *   <li><strong>total_count</strong>: 전체 애플리케이션 수</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>사용 가능한 에이전트 애플리케이션 목록 조회</li>
 *   <li>애플리케이션 선택을 위한 정보 제공</li>
 *   <li>관리자 대시보드에서 앱 현황 표시</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-22
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Agent Gateway Apps 목록 응답",
    example = """
        {
          "apps": [
            {
              "id": "app-123",
              "name": "Customer Service Agent",
              "description": "고객 서비스를 위한 AI 에이전트",
              "status": "active"
            }
          ],
          "total_count": 1
        }
        """
)
public class AppsResponse {
    
    /**
     * 애플리케이션 목록
     * 
     * <p>사용 가능한 에이전트 애플리케이션들의 목록입니다.
     * 각 앱의 기본 정보와 상태를 포함합니다.</p>
     */
    @JsonProperty("apps")
    @Schema(
        description = "애플리케이션 목록"
    )
    private List<AppInfo> apps;
    
    /**
     * 전체 애플리케이션 수
     * 
     * <p>현재 시스템에 등록된 전체 애플리케이션의 개수입니다.</p>
     */
    @JsonProperty("total_count")
    @Schema(
        description = "전체 애플리케이션 수", 
        example = "1"
    )
    private Integer totalCount;
    
    /**
     * 애플리케이션 정보 내부 클래스
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "애플리케이션 상세 정보")
    public static class AppInfo {
        
        /**
         * 애플리케이션 ID
         */
        @JsonProperty("id")
        @Schema(
            description = "애플리케이션 고유 식별자", 
            example = "app-123"
        )
        private String id;
        
        /**
         * 애플리케이션 이름
         */
        @JsonProperty("name")
        @Schema(
            description = "애플리케이션 이름", 
            example = "Customer Service Agent"
        )
        private String name;
        
        /**
         * 애플리케이션 설명
         */
        @JsonProperty("description")
        @Schema(
            description = "애플리케이션 설명", 
            example = "고객 서비스를 위한 AI 에이전트"
        )
        private String description;
        
        /**
         * 애플리케이션 상태
         */
        @JsonProperty("status")
        @Schema(
            description = "애플리케이션 상태", 
            example = "active",
            allowableValues = {"active", "inactive", "maintenance"}
        )
        private String status;
    }
}
