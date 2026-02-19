package com.skax.aiplatform.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
 
/**
 * 페이지네이션 응답 래퍼 클래스
 * 
 * <p>Spring Data Page를 기반으로 하는 표준화된 페이지네이션 응답 형식입니다.
 * copilot-instructions.md의 최신 페이지네이션 응답 구조를 따릅니다.</p>
 * 
 * @param <T> 페이지 데이터 타입
 * @author ByounggwanLee
 * @since 2025-08-11
 * @version 1.0
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "페이지네이션 응답")
public class PageResponse<T> {
    
    /**
     * 페이지 컨텐츠 목록
     */
    @Schema(description = "페이지 컨텐츠 목록", example = "[{\"id\":1,\"name\":\"홍길동\"},{\"id\":2,\"name\":\"김철수\"}]")
    private List<T> content;
    
    /**
     * 페이지 정보
     */
    @Schema(description = "페이지 정보")
    private PageableInfo pageable;
    
    /**
     * 전체 요소 수
     */
    @Schema(description = "전체 요소 수", example = "100")
    private long totalElements;
    
    /**
     * 전체 페이지 수
     */
    @Schema(description = "전체 페이지 수", example = "5")
    private int totalPages;
    
    /**
     * 첫 번째 페이지 여부
     */
    @Schema(description = "첫 번째 페이지 여부", example = "true")
    private boolean first;
    
    /**
     * 마지막 페이지 여부
     */
    @Schema(description = "마지막 페이지 여부", example = "false")
    private boolean last;
    
    /**
     * 다음 페이지 존재 여부
     */
    @Schema(description = "다음 페이지 존재 여부", example = "true")
    private boolean hasNext;
    
    /**
     * 이전 페이지 존재 여부
     */
    @Schema(description = "이전 페이지 존재 여부", example = "false")
    private boolean hasPrevious;
    
    /**
     * Spring Data Page 객체로부터 PageResponse 생성
     * 
     * @param page Spring Data Page 객체
     * @param <T> 페이지 데이터 타입
     * @return PageResponse 객체
     */
    public static <T> PageResponse<T> from(Page<T> page) {
        String sortInfo = page.getSort().isEmpty() ? "" : page.getSort().toString();
        
        PageableInfo pageableInfo = PageableInfo.builder()
                .page(page.getNumber()) 
                .size(page.getSize())
                .sort(sortInfo)
                .build();
        
        return PageResponse.<T>builder()
                .content(page.getContent())
                .pageable(pageableInfo)
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}
