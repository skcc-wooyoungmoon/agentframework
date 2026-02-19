package com.skax.aiplatform.client.datumo.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Datumo Task 목록 조회 요청 DTO
 * 
 * <p>Datumo 시스템에서 Task 목록을 조회하기 위한 요청 데이터 구조입니다.
 * 프로젝트별, 카테고리별로 Task를 조회하며, 페이징과 검색 기능을 지원합니다.</p>
 * 
 * <h3>필수 정보:</h3>
 * <ul>
 *   <li><strong>projectId</strong>: 조회할 프로젝트 ID</li>
 *   <li><strong>category</strong>: Task 카테고리 (예: JUDGE)</li>
 *   <li><strong>page</strong>: 페이지 번호 (1부터 시작)</li>
 *   <li><strong>pageSize</strong>: 페이지당 항목 수</li>
 * </ul>
 * 
 * <h3>선택 정보:</h3>
 * <ul>
 *   <li><strong>search</strong>: 검색어 (Task 이름이나 설명에서 검색)</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * TaskListRequest request = TaskListRequest.builder()
 *     .projectId(1L)
 *     .category("JUDGE")
 *     .page(1)
 *     .pageSize(12)
 *     .search("RAGAS")
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 * @see com.skax.aiplatform.client.datumo.api.dto.response.TaskListResponse Task 목록 응답
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "Datumo Task 목록 조회 요청 정보",
    example = """
        {
          "projectId": 1,
          "category": "JUDGE",
          "page": 1,
          "pageSize": 12,
          "search": "RAGAS"
        }
        """
)
public class TaskListRequest {
    
    /**
     * 프로젝트 식별자
     * 
     * <p>Task 목록을 조회할 프로젝트의 고유 식별자입니다.</p>
     * 
     * @apiNote 유효한 프로젝트 ID여야 하며, 사용자가 해당 프로젝트에 대한 접근 권한을 가져야 합니다.
     */
    @Schema(
        description = "조회할 프로젝트 ID", 
        example = "1",
        required = true,
        minimum = "1"
    )
    @NotNull(message = "프로젝트 ID는 필수입니다")
    @Min(value = 1, message = "프로젝트 ID는 1 이상이어야 합니다")
    private Long projectId;
    
    /**
     * Task 카테고리
     * 
     * <p>조회할 Task의 카테고리입니다.
     * 시스템에서 정의된 카테고리 값을 사용해야 합니다.</p>
     * 
     * @implNote 일반적으로 "JUDGE", "EVALUATION" 등의 값을 사용합니다.
     */
    @Schema(
        description = "Task 카테고리", 
        example = "JUDGE",
        required = true,
        allowableValues = {"JUDGE", "EVALUATION", "ANALYSIS"}
    )
    @NotNull(message = "카테고리는 필수입니다")
    private String category;
    
    /**
     * 페이지 번호
     * 
     * <p>조회할 페이지 번호입니다. 1부터 시작합니다.</p>
     */
    @Schema(
        description = "페이지 번호 (1부터 시작)", 
        example = "1",
        required = true,
        minimum = "1"
    )
    @NotNull(message = "페이지 번호는 필수입니다")
    @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다")
    private Integer page;
    
    /**
     * 페이지당 항목 수
     * 
     * <p>한 페이지에 포함될 Task의 최대 개수입니다.</p>
     */
    @Schema(
        description = "페이지당 항목 수", 
        example = "12",
        required = true,
        minimum = "1",
        maximum = "100"
    )
    @NotNull(message = "페이지 크기는 필수입니다")
    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
    private Integer pageSize;
    
    /**
     * 검색어
     * 
     * <p>Task 이름이나 설명에서 검색할 키워드입니다.
     * 선택적 파라미터로, 제공되지 않으면 전체 목록을 조회합니다.</p>
     * 
     * @apiNote 대소문자를 구분하지 않는 부분 일치 검색을 수행합니다.
     */
    @Schema(
        description = "검색어 (Task 이름이나 설명에서 검색)", 
        example = "RAGAS",
        maxLength = 100
    )
    private String search;
}