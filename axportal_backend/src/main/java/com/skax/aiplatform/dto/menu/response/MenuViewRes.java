package com.skax.aiplatform.dto.menu.response;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 프론트엔드 메뉴 응답 DTO
 * 
 * <p>
 * 프론트엔드 메뉴 구조에 맞춘 응답 DTO입니다.
 * 프론트엔드의 UILnbMenuItemType과 동일한 구조를 가집니다.
 * </p>
 */
@Schema(description = "프론트엔드 메뉴 응답 DTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuViewRes {

    @Schema(description = "메뉴 ID (프론트엔드 id와 매핑, code 값 사용)", example = "home")
    @JsonProperty("id")
    private String id;

    @Schema(description = "메뉴 라벨 (프론트엔드 label과 매핑, name 값 사용)", example = "홈")
    @JsonProperty("label")
    private String label;

    @Schema(description = "아이콘 이름", example = "ico-lnb-menu-32-default-home")
    @JsonProperty("icon")
    private String icon;

    @Schema(description = "React Router 경로", example = "home")
    @JsonProperty("path")
    private String path;

    @Schema(description = "권한 코드 (별도 관리, 현재는 null)", example = "AUTH_KEY.MENU.ALL")
    @JsonProperty("auth")
    private String auth;

    @Schema(description = "외부 링크 URL", example = "https://www.google.com")
    @JsonProperty("href")
    private String href;

    @Schema(description = "하위 메뉴 목록")
    @JsonProperty("children")
    @Builder.Default
    private List<MenuViewRes> children = new ArrayList<>();
}

