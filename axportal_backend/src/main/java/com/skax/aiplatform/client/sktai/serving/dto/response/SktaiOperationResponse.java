package com.skax.aiplatform.client.sktai.serving.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI Serving API 일반 작업 응답 DTO
 * 
 * <p>SKTAI Serving 시스템에서 시작, 중지, 업데이트 등의 작업 요청에 대한 응답 구조입니다.
 * 대부분의 작업 API가 빈 응답 또는 간단한 상태 메시지를 반환하므로 공통으로 사용됩니다.</p>
 * 
 * <h3>사용 API:</h3>
 * <ul>
 *   <li><strong>Start/Stop Serving</strong>: 서빙 시작/중지 작업</li>
 *   <li><strong>Start/Stop Agent Serving</strong>: 에이전트 서빙 시작/중지 작업</li>
 *   <li><strong>Start/Stop MCP Serving</strong>: MCP 서빙 시작/중지 작업</li>
 *   <li><strong>Update MCP Serving</strong>: MCP 서빙 업데이트 작업</li>
 *   <li><strong>Shared Backend Version Up</strong>: 공유 백엔드 버전업 작업</li>
 * </ul>
 * 
 * <h3>응답 형태:</h3>
 * <ul>
 *   <li><strong>성공 시</strong>: 빈 객체 {} 또는 간단한 메시지</li>
 *   <li><strong>실패 시</strong>: 에러 정보 포함</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * SktaiOperationResponse response = servingService.startServing(servingId);
 * if (response.isSuccess()) {
 *     log.info("서빙 시작 성공: {}", response.getMessage());
 * }
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-09-03
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Serving API 일반 작업 응답 정보",
    example = """
        {
          "success": true,
          "message": "작업이 성공적으로 완료되었습니다",
          "status": "Accepted"
        }
        """
)
public class SktaiOperationResponse {
    
    /**
     * 작업 성공 여부
     * 
     * <p>작업이 성공적으로 처리되었는지를 나타냅니다.</p>
     * 
     * @implNote 대부분의 작업 API가 202 Accepted 또는 200 OK를 반환하므로 기본값은 true입니다.
     */
    @JsonProperty("success")
    @Schema(
        description = "작업 성공 여부", 
        example = "true",
        defaultValue = "true"
    )
    @Builder.Default
    private Boolean success = true;
    
    /**
     * 작업 결과 메시지
     * 
     * <p>작업 처리 결과에 대한 설명 메시지입니다.
     * 성공/실패 상황에 따라 적절한 메시지가 설정됩니다.</p>
     */
    @JsonProperty("message")
    @Schema(
        description = "작업 결과 메시지", 
        example = "작업이 성공적으로 완료되었습니다"
    )
    private String message;
    
    /**
     * 작업 상태
     * 
     * <p>작업의 현재 상태를 나타냅니다.
     * HTTP 상태 코드에 따른 텍스트 표현입니다.</p>
     * 
     * @apiNote 202 Accepted, 200 OK 등의 상태를 텍스트로 표현합니다.
     */
    @JsonProperty("status")
    @Schema(
        description = "작업 상태 (HTTP 상태 기반)", 
        example = "Accepted",
        allowableValues = {"OK", "Accepted", "Created", "No Content"}
    )
    private String status;
    
    /**
     * 작업 세부 정보
     * 
     * <p>작업과 관련된 추가 세부 정보입니다.
     * 필요에 따라 작업 ID, 타임스탬프 등의 정보가 포함될 수 있습니다.</p>
     */
    @JsonProperty("details")
    @Schema(
        description = "작업 세부 정보 (선택사항)", 
        example = "작업 ID: abc123, 처리 시간: 2025-09-03T10:30:00Z"
    )
    private String details;
    
    /**
     * 성공 응답 생성
     * 
     * @param message 성공 메시지
     * @return 성공 응답 객체
     */
    public static SktaiOperationResponse success(String message) {
        return SktaiOperationResponse.builder()
            .success(true)
            .message(message)
            .status("OK")
            .build();
    }
    
    /**
     * 처리 중 응답 생성 (202 Accepted)
     * 
     * @param message 처리 메시지
     * @return 처리 중 응답 객체
     */
    public static SktaiOperationResponse accepted(String message) {
        return SktaiOperationResponse.builder()
            .success(true)
            .message(message)
            .status("Accepted")
            .build();
    }
    
    /**
     * 성공 여부 확인
     * 
     * @return 성공 여부
     */
    public boolean isSuccess() {
        return Boolean.TRUE.equals(success);
    }
}
