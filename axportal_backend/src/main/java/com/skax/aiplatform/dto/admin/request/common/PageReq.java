package com.skax.aiplatform.dto.admin.request.common;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 페이지 검색 요청 기본 DTO
 *
 * @author 권두현
 * @version 1.0.0
 * @since 2025-08-25
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public abstract class PageReq {

    @Schema(description = "페이지 번호", example = "1", defaultValue = "1")
    private int page = 1;

    @Schema(description = "페이지 크기", example = "12", defaultValue = "12")
    private int size = 12;

    /**
     * Pageable 객체로 변환
     * 프론트에서 1부터 시작하는 페이지를 0부터 시작하는 Spring Data 페이지로 변환
     *
     * @return Pageable 객체
     */
    public Pageable toPageable() {
        return PageRequest.of(Math.max(0, page - 1), Math.max(1, size));
    }

}
