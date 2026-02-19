package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SKTAI Agent 코드 프로파일 파일 목록 응답 DTO
 * 
 * <p>SKTAI Agent 시스템에서 관리하는 코드 프로파일 파일들의 목록 정보를 담는 응답 데이터 구조입니다.
 * 프로파일링 세션, 성능 분석 데이터, 메타데이터 등을 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>total_count</strong>: 전체 프로파일 파일 수</li>
 *   <li><strong>files</strong>: 프로파일 파일 목록</li>
 *   <li><strong>last_updated</strong>: 마지막 업데이트 시간</li>
 * </ul>
 * 
 * <h3>프로파일 파일 정보:</h3>
 * <ul>
 *   <li>파일명, 크기, 생성일시</li>
 *   <li>프로파일링 타입 및 상태</li>
 *   <li>메타데이터 정보</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>코드 프로파일 파일 목록 조회</li>
 *   <li>성능 분석 데이터 관리</li>
 *   <li>프로파일링 세션 이력 확인</li>
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
    description = "SKTAI Agent 코드 프로파일 파일 목록 응답",
    example = """
        {
          "total_count": 5,
          "files": [
            {
              "filename": "profile_20250822_001.json",
              "size": 1024000,
              "created_at": "2025-08-22T10:30:00",
              "type": "performance",
              "status": "completed"
            }
          ],
          "last_updated": "2025-08-22T15:30:00"
        }
        """
)
public class ProfileFilesResponse {
    
    /**
     * 전체 프로파일 파일 수
     * 
     * <p>현재 시스템에 저장된 전체 프로파일 파일의 개수입니다.</p>
     */
    @JsonProperty("total_count")
    @Schema(
        description = "전체 프로파일 파일 수", 
        example = "5"
    )
    private Integer totalCount;
    
    /**
     * 프로파일 파일 목록
     * 
     * <p>각 프로파일 파일의 상세 정보를 포함하는 목록입니다.</p>
     */
    @JsonProperty("files")
    @Schema(
        description = "프로파일 파일 목록"
    )
    private List<ProfileFileInfo> files;
    
    /**
     * 마지막 업데이트 시간
     * 
     * <p>프로파일 파일 목록이 마지막으로 업데이트된 시간입니다.</p>
     */
    @JsonProperty("last_updated")
    @Schema(
        description = "마지막 업데이트 시간", 
        example = "2025-08-22T15:30:00"
    )
    private LocalDateTime lastUpdated;
    
    /**
     * 프로파일 파일 정보 내부 클래스
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "프로파일 파일 상세 정보")
    public static class ProfileFileInfo {
        
        /**
         * 파일명
         */
        @JsonProperty("filename")
        @Schema(
            description = "프로파일 파일명", 
            example = "profile_20250822_001.json"
        )
        private String filename;
        
        /**
         * 파일 크기 (바이트)
         */
        @JsonProperty("size")
        @Schema(
            description = "파일 크기 (바이트)", 
            example = "1024000"
        )
        private Long size;
        
        /**
         * 파일 생성 시간
         */
        @JsonProperty("created_at")
        @Schema(
            description = "파일 생성 시간", 
            example = "2025-08-22T10:30:00"
        )
        private LocalDateTime createdAt;
        
        /**
         * 프로파일 타입
         */
        @JsonProperty("type")
        @Schema(
            description = "프로파일링 타입", 
            example = "performance",
            allowableValues = {"performance", "memory", "cpu", "network"}
        )
        private String type;
        
        /**
         * 프로파일 상태
         */
        @JsonProperty("status")
        @Schema(
            description = "프로파일 상태", 
            example = "completed",
            allowableValues = {"running", "completed", "failed", "cancelled"}
        )
        private String status;
    }
}
