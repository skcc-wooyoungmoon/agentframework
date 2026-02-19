package com.skax.aiplatform.client.sktai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI 사용자 할당 가능한 역할 목록 응답 DTO (페이징)
 *
 * <p>GET /api/v1/users/{user_id}/role-available 응답 구조를 그대로 모델링합니다.
 * 예시 응답의 data/payload.pagination 구조를 지원합니다.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 할당 가능한 역할 목록 응답 (role-available)")
public class RoleAvailablePageResponseDto {

    @Schema(description = "역할/프로젝트 아이템 목록")
    private List<Item> data;

    @Schema(description = "부가 메타데이터 (페이징 등)")
    private Payload payload;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "응답 데이터 아이템")
    public static class Item {
        @Schema(description = "프로젝트 정보")
        private Project project;

        @Schema(description = "역할 정보")
        private Role role;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "프로젝트 정보")
    public static class Project {
        @Schema(description = "프로젝트 ID", example = "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5")
        private String id;

        @Schema(description = "프로젝트 이름", example = "default")
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "역할 정보")
    public static class Role {
        @Schema(description = "역할 ID", example = "9ed2877b-36b0-4d4c-ba29-1c45bf7699a1")
        private String id;

        @Schema(description = "역할 이름", example = "ai_work_admin")
        private String name;

        @Schema(description = "역할 설명", example = "ai_work_admin")
        private String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "응답 부가 데이터")
    public static class Payload {
        @Schema(description = "페이징 정보")
        private Pagination pagination;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "페이징 정보")
    public static class Pagination {
        @Schema(description = "현재 페이지", example = "1")
        private Integer page;

        @JsonProperty("first_page_url")
        @Schema(description = "첫 페이지 URL")
        private String firstPageUrl;

        @JsonProperty("from_")
        @Schema(description = "현재 페이지의 시작 인덱스", example = "1")
        private Integer from;

        @JsonProperty("last_page")
        @Schema(description = "마지막 페이지 번호", example = "1")
        private Integer lastPage;

        @Schema(description = "페이지 네비게이션 링크 목록")
        private List<Link> links;

        @JsonProperty("next_page_url")
        @Schema(description = "다음 페이지 URL")
        private String nextPageUrl;

        @JsonProperty("items_per_page")
        @Schema(description = "페이지당 아이템 수", example = "10")
        private Integer itemsPerPage;

        @JsonProperty("prev_page_url")
        @Schema(description = "이전 페이지 URL")
        private String prevPageUrl;

        @Schema(description = "현재 페이지의 마지막 인덱스", example = "4")
        private Integer to;

        @Schema(description = "총 아이템 수", example = "4")
        private Integer total;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "페이지 네비게이션 링크")
    public static class Link {
        @Schema(description = "링크 URL")
        private String url;

        @Schema(description = "링크 라벨")
        private String label;

        @Schema(description = "활성 여부")
        private Boolean active;

        @Schema(description = "대상 페이지 번호")
        private Integer page;
    }
}
