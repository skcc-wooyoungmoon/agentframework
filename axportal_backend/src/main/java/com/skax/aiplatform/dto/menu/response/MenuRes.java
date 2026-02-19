package com.skax.aiplatform.dto.menu.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 메뉴 응답 DTO
 * 
 * <p>
 * 메뉴 정보를 반환하는 DTO입니다.
 * 계층 구조를 지원하기 위해 children 필드를 포함합니다.
 * </p>
 */
@Schema(description = "메뉴 응답 DTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuRes {

    @Schema(description = "메뉴 ID", example = "1")
    private Long id;

    @Schema(description = "부모 메뉴 ID", example = "1")
    private Long parentId;

    @Schema(description = "메뉴 코드 (프론트엔드 id와 매핑)", example = "home")
    private String code;

    @Schema(description = "메뉴명", example = "홈")
    private String name;

    @Schema(description = "React Router 경로", example = "home")
    private String path;

    @Schema(description = "아이콘 이름", example = "ico-lnb-menu-32-default-home")
    private String icon;

    @Schema(description = "정렬 순서", example = "0")
    private Integer orderNo;

    @Schema(description = "깊이 (0=최상위)", example = "0")
    private Integer depth;

    @Schema(description = "외부 링크 여부", example = "false")
    private Boolean isExternal;

    @Schema(description = "표시 여부", example = "true")
    private Boolean visible;

    @Schema(description = "활성화 여부", example = "true")
    private Boolean active;

    @Schema(description = "설명", example = "홈 메뉴입니다")
    private String description;

    @Schema(description = "하위 메뉴 목록")
    @Builder.Default
    private List<MenuRes> children = new ArrayList<>();

    // 공통 필드
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "생성일시", example = "2025-01-01 10:30:00")
    private LocalDateTime createAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "수정일시", example = "2025-01-01 10:30:00")
    private LocalDateTime updateAt;

    @Schema(description = "생성자", example = "admin")
    private String createBy;

    @Schema(description = "수정자", example = "admin")
    private String updateBy;
}

