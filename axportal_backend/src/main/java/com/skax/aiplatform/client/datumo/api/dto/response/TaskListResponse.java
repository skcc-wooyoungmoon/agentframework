package com.skax.aiplatform.client.datumo.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Datumo Task 목록 조회 응답 DTO
 * 
 * <p>Datumo 시스템에서 Task 목록 조회 결과를 담는 구조입니다.
 * 페이징 정보와 함께 Task 목록을 제공합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>totalDataCount</strong>: 전체 데이터 개수</li>
 *   <li><strong>totalPageCount</strong>: 전체 페이지 수</li>
 *   <li><strong>tasks</strong>: Task 목록</li>
 * </ul>
 * 
 * <h3>Task 정보:</h3>
 * <ul>
 *   <li><strong>id</strong>: Task 고유 식별자</li>
 *   <li><strong>displayId</strong>: 화면 표시용 ID</li>
 *   <li><strong>name</strong>: Task 이름</li>
 *   <li><strong>description</strong>: Task 설명</li>
 *   <li><strong>createdAt</strong>: 생성 일시</li>
 *   <li><strong>redirectUrl</strong>: 리다이렉트 URL</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 * @see com.skax.aiplatform.client.datumo.api.dto.request.TaskListRequest Task 목록 요청
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "Datumo Task 목록 조회 응답 정보",
    example = """
        {
          "totalDataCount": 151,
          "totalPageCount": 16,
          "tasks": [
            {
              "id": "EVALUATION-435",
              "displayId": "1",
              "name": "테스트 Task",
              "description": "테스트 Task에 대한 설명입니다.",
              "createdAt": "2025-09-19T09:40:11.291286",
              "redirectUrl": "http://eval-public-shinhan.datumo.com/evaluation-task/?projectId=435&isModelProject=true"
            }
          ]
        }
        """
)
public class TaskListResponse {
    
    /**
     * 전체 데이터 개수
     * 
     * <p>검색 조건에 맞는 전체 Task의 개수입니다.</p>
     */
    @JsonProperty("totalDataCount")
    @Schema(
        description = "전체 데이터 개수", 
        example = "151",
        minimum = "0"
    )
    private Integer totalDataCount;
    
    /**
     * 전체 페이지 수
     * 
     * <p>전체 데이터를 페이지 크기로 나눈 총 페이지 수입니다.</p>
     */
    @JsonProperty("totalPageCount")
    @Schema(
        description = "전체 페이지 수", 
        example = "16",
        minimum = "0"
    )
    private Integer totalPageCount;
    
    /**
     * Task 목록
     * 
     * <p>현재 페이지에 포함된 Task 목록입니다.</p>
     */
    @JsonProperty("tasks")
    @Schema(description = "Task 목록")
    private List<TaskInfo> tasks;
    
    /**
     * Task 정보 DTO
     * 
     * <p>개별 Task의 상세 정보를 담는 내부 클래스입니다.</p>
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Task 상세 정보")
    public static class TaskInfo {
        
        /**
         * Task 고유 식별자
         * 
         * <p>시스템에서 Task를 구분하는 고유한 식별자입니다.</p>
         */
        @JsonProperty("id")
        @Schema(
            description = "Task 고유 식별자", 
            example = "EVALUATION-435"
        )
        private String id;
        
        /**
         * 화면 표시용 ID
         * 
         * <p>사용자 인터페이스에서 표시되는 간단한 ID입니다.</p>
         */
        @JsonProperty("displayId")
        @Schema(
            description = "화면 표시용 ID", 
            example = "1"
        )
        private String displayId;
        
        /**
         * Task 이름
         * 
         * <p>Task의 이름 또는 제목입니다.</p>
         */
        @JsonProperty("name")
        @Schema(
            description = "Task 이름", 
            example = "테스트 Task"
        )
        private String name;
        
        /**
         * Task 설명
         * 
         * <p>Task에 대한 상세 설명입니다.</p>
         */
        @JsonProperty("description")
        @Schema(
            description = "Task 설명", 
            example = "테스트 Task에 대한 설명입니다."
        )
        private String description;
        
        /**
         * 생성 일시
         * 
         * <p>Task가 생성된 일시입니다.</p>
         */
        @JsonProperty("createdAt")
        @Schema(
            description = "생성 일시", 
            example = "2025-09-19T09:40:11.291286",
            format = "date-time"
        )
        private LocalDateTime createdAt;
        
        /**
         * 리다이렉트 URL
         * 
         * <p>Task 상세 화면으로 이동하는 URL입니다.</p>
         */
        @JsonProperty("redirectUrl")
        @Schema(
            description = "Task 상세 화면 URL", 
            example = "http://eval-public-shinhan.datumo.com/evaluation-task/?projectId=435&isModelProject=true"
        )
        private String redirectUrl;

        @JsonProperty("isPublic")
        @Schema(
                description = "Task public여부",
                example = "true"
        )
        private Boolean isPublic;
    }
}