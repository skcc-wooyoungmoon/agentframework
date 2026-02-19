package com.skax.aiplatform.client.sktai.agent.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * SKTAI Agent 프로파일 파일 삭제 응답 DTO
 * 
 * <p>SKTAI Agent 시스템에서 코드 프로파일 파일 삭제 작업의 결과를 담는 응답 데이터 구조입니다.
 * 삭제 성공/실패 정보, 메타데이터 등을 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>filename</strong>: 삭제된 파일명</li>
 *   <li><strong>success</strong>: 삭제 성공 여부</li>
 *   <li><strong>message</strong>: 삭제 결과 메시지</li>
 *   <li><strong>deleted_at</strong>: 삭제 처리 시간</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>프로파일 파일 삭제 작업 결과 확인</li>
 *   <li>파일 정리 작업 결과 추적</li>
 *   <li>삭제 실패 시 원인 파악</li>
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
    description = "SKTAI Agent 프로파일 파일 삭제 응답",
    example = """
        {
          "filename": "profile_20250822_001.json",
          "success": true,
          "message": "파일이 성공적으로 삭제되었습니다",
          "deleted_at": "2025-08-22T15:30:00"
        }
        """
)
public class ProfileFileDeleteResponse {
    
    /**
     * 삭제된 파일명
     * 
     * <p>삭제 작업이 수행된 프로파일 파일의 이름입니다.</p>
     */
    @JsonProperty("filename")
    @Schema(
        description = "삭제된 프로파일 파일명", 
        example = "profile_20250822_001.json"
    )
    private String filename;
    
    /**
     * 삭제 성공 여부
     * 
     * <p>파일 삭제 작업의 성공/실패 여부를 나타냅니다.</p>
     */
    @JsonProperty("success")
    @Schema(
        description = "삭제 성공 여부", 
        example = "true"
    )
    private Boolean success;
    
    /**
     * 삭제 결과 메시지
     * 
     * <p>삭제 작업의 결과에 대한 상세 메시지입니다.
     * 성공 시 확인 메시지, 실패 시 오류 원인을 포함합니다.</p>
     */
    @JsonProperty("message")
    @Schema(
        description = "삭제 결과 메시지", 
        example = "파일이 성공적으로 삭제되었습니다"
    )
    private String message;
    
    /**
     * 삭제 처리 시간
     * 
     * <p>파일 삭제 작업이 처리된 시간입니다.</p>
     */
    @JsonProperty("deleted_at")
    @Schema(
        description = "삭제 처리 시간", 
        example = "2025-08-22T15:30:00"
    )
    private LocalDateTime deletedAt;
}
