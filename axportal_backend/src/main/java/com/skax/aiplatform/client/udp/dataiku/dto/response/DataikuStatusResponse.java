package com.skax.aiplatform.client.udp.dataiku.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Dataiku 시나리오 실행 상태 응답 DTO
 * 
 * <p>Dataiku 시나리오의 현재 실행 상태 정보를 담는 응답 데이터 구조입니다.</p>
 * 
 * <h3>상태 정보:</h3>
 * <ul>
 *   <li><strong>기본 정보</strong>: 실행 ID, 시나리오 ID, 현재 상태</li>
 *   <li><strong>진행 정보</strong>: 진행률, 현재 단계, 예상 완료 시간</li>
 *   <li><strong>시간 정보</strong>: 시작/종료 시간, 경과 시간</li>
 *   <li><strong>결과 정보</strong>: 성공/실패 여부, 오류 메시지, 로그</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-10-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "Dataiku 시나리오 실행 상태 정보",
    example = """
        {
          "runId": "run_12345",
          "scenarioId": "scenario_001",
          "projectKey": "PROJECT_KEY",
          "status": "RUNNING",
          "progress": 65.5,
          "currentStep": "데이터 전처리",
          "totalSteps": 5,
          "completedSteps": 3,
          "startedAt": "2025-10-15T10:30:00Z",
          "estimatedCompletionAt": "2025-10-15T11:00:00Z",
          "elapsedSeconds": 900,
          "message": "데이터 전처리 중입니다."
        }
        """
)
public class DataikuStatusResponse {

    /**
     * 시나리오 실행 ID
     */
    @Schema(
        description = "시나리오 실행 고유 식별자",
        example = "run_12345"
    )
    private String runId;

    /**
     * 시나리오 ID
     */
    @Schema(
        description = "실행 중인 시나리오 ID",
        example = "scenario_001"
    )
    private String scenarioId;

    /**
     * 프로젝트 키
     */
    @Schema(
        description = "Dataiku 프로젝트 키",
        example = "PROJECT_KEY"
    )
    private String projectKey;

    /**
     * 실행 상태
     * 
     * <p>시나리오의 현재 실행 상태입니다.</p>
     * 
     * @implNote 가능한 상태: QUEUED, RUNNING, SUCCESS, FAILED, ABORTED
     */
    @Schema(
        description = "시나리오 실행 상태",
        example = "RUNNING",
        allowableValues = {"QUEUED", "RUNNING", "SUCCESS", "FAILED", "ABORTED"}
    )
    private String status;

    /**
     * 진행률 (퍼센트)
     * 
     * <p>시나리오 실행의 전체 진행률입니다. 0.0 ~ 100.0 범위입니다.</p>
     */
    @Schema(
        description = "시나리오 실행 진행률 (0.0 ~ 100.0)",
        example = "65.5"
    )
    private Double progress;

    /**
     * 현재 단계
     * 
     * <p>현재 실행 중인 단계의 이름 또는 설명입니다.</p>
     */
    @Schema(
        description = "현재 실행 중인 단계",
        example = "데이터 전처리"
    )
    private String currentStep;

    /**
     * 전체 단계 수
     * 
     * <p>시나리오의 총 단계 수입니다.</p>
     */
    @Schema(
        description = "시나리오 총 단계 수",
        example = "5"
    )
    private Integer totalSteps;

    /**
     * 완료된 단계 수
     * 
     * <p>현재까지 완료된 단계의 수입니다.</p>
     */
    @Schema(
        description = "완료된 단계 수",
        example = "3"
    )
    private Integer completedSteps;

    /**
     * 실행 시작 시간
     */
    @Schema(
        description = "시나리오 실행 시작 시간 (ISO 8601 형식)",
        example = "2025-10-15T10:30:00Z"
    )
    private String startedAt;

    /**
     * 실행 종료 시간
     * 
     * <p>시나리오가 완료되거나 실패한 경우의 종료 시간입니다.</p>
     */
    @Schema(
        description = "시나리오 실행 종료 시간 (ISO 8601 형식)",
        example = "2025-10-15T11:15:00Z"
    )
    private String finishedAt;

    /**
     * 예상 완료 시간
     * 
     * <p>현재 진행률을 기반으로 예상되는 완료 시간입니다.</p>
     */
    @Schema(
        description = "예상 완료 시간 (ISO 8601 형식)",
        example = "2025-10-15T11:00:00Z"
    )
    private String estimatedCompletionAt;

    /**
     * 경과 시간 (초)
     * 
     * <p>시나리오 실행 시작 후 경과된 시간(초)입니다.</p>
     */
    @Schema(
        description = "실행 시작 후 경과 시간 (초)",
        example = "900"
    )
    private Integer elapsedSeconds;

    /**
     * 상태 메시지
     * 
     * <p>현재 상태에 대한 설명 메시지입니다.</p>
     */
    @Schema(
        description = "현재 상태 설명 메시지",
        example = "데이터 전처리 중입니다."
    )
    private String message;

    /**
     * 오류 메시지
     * 
     * <p>실행 실패 시의 오류 메시지입니다.</p>
     */
    @Schema(
        description = "오류 메시지 (실패 시)",
        example = "데이터 소스 연결 실패: 타임아웃"
    )
    private String errorMessage;

    /**
     * 실행 로그
     * 
     * <p>시나리오 실행 중 생성된 로그 메시지들입니다.</p>
     */
    @Schema(
        description = "시나리오 실행 로그",
        example = """
            [
              "2025-10-15T10:30:00Z - 시나리오 시작",
              "2025-10-15T10:32:00Z - 데이터 로딩 완료",
              "2025-10-15T10:35:00Z - 데이터 전처리 시작"
            ]
            """
    )
    private List<String> logs;

    /**
     * 실행 결과 메타데이터
     * 
     * <p>시나리오 실행 결과와 관련된 추가 메타데이터입니다.</p>
     */
    @Schema(
        description = "실행 결과 메타데이터",
        example = """
            {
              "processedRecords": 10000,
              "outputDatasetSize": "250MB",
              "memoryUsage": "1.5GB"
            }
            """
    )
    private Map<String, Object> metadata;

    /**
     * 재시도 횟수
     * 
     * <p>실패 후 재시도된 횟수입니다.</p>
     */
    @Schema(
        description = "재시도 횟수",
        example = "1"
    )
    private Integer retryCount;

    /**
     * 최대 재시도 횟수
     * 
     * <p>허용된 최대 재시도 횟수입니다.</p>
     */
    @Schema(
        description = "최대 재시도 횟수",
        example = "3"
    )
    private Integer maxRetries;
}