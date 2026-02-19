package com.skax.aiplatform.client.udp.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UDP API 공통 페이지네이션 응답 DTO
 * 
 * <p>UDP API의 페이지네이션된 응답에서 공통으로 사용되는 구조입니다.
 * 표준 페이징 정보를 제공하여 클라이언트에서 일관된 페이징 처리가 가능합니다.</p>
 * 
 * <h3>페이징 정보:</h3>
 * <ul>
 *   <li><strong>totalCount</strong>: 검색 조건에 맞는 전체 결과 수</li>
 *   <li><strong>page</strong>: 현재 페이지 번호 (1부터 시작)</li>
 *   <li><strong>size</strong>: 페이지당 결과 수</li>
 *   <li><strong>totalPages</strong>: 전체 페이지 수</li>
 *   <li><strong>first/last</strong>: 첫/마지막 페이지 여부</li>
 *   <li><strong>hasNext/hasPrevious</strong>: 다음/이전 페이지 존재 여부</li>
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
@Schema(description = "UDP API 공통 페이지네이션 정보")
public class UdpPaginationResponse {
    
    /**
     * 전체 결과 수
     * 
     * <p>검색 조건에 맞는 전체 결과의 개수입니다.</p>
     */
    @Schema(description = "전체 결과 수", example = "125")
    private Integer totalCount;
    
    /**
     * 현재 페이지 번호
     * 
     * <p>현재 조회 중인 페이지 번호입니다. 1부터 시작합니다.</p>
     */
    @Schema(description = "현재 페이지 번호 (1부터 시작)", example = "1")
    private Integer page;
    
    /**
     * 페이지당 결과 수
     * 
     * <p>한 페이지에 포함된 결과의 개수입니다.</p>
     */
    @Schema(description = "페이지당 결과 수", example = "10")
    private Integer size;
    
    /**
     * 전체 페이지 수
     * 
     * <p>전체 결과를 페이지 크기로 나눈 총 페이지 수입니다.</p>
     */
    @Schema(description = "전체 페이지 수", example = "13")
    private Integer totalPages;
    
    /**
     * 첫 페이지 여부
     * 
     * <p>현재 페이지가 첫 번째 페이지인지 여부입니다.</p>
     */
    @Schema(description = "첫 페이지 여부", example = "true")
    private Boolean first;
    
    /**
     * 마지막 페이지 여부
     * 
     * <p>현재 페이지가 마지막 페이지인지 여부입니다.</p>
     */
    @Schema(description = "마지막 페이지 여부", example = "false")
    private Boolean last;
    
    /**
     * 다음 페이지 존재 여부
     * 
     * <p>다음 페이지가 존재하는지 여부입니다.</p>
     */
    @Schema(description = "다음 페이지 존재 여부", example = "true")
    private Boolean hasNext;
    
    /**
     * 이전 페이지 존재 여부
     * 
     * <p>이전 페이지가 존재하는지 여부입니다.</p>
     */
    @Schema(description = "이전 페이지 존재 여부", example = "false")
    private Boolean hasPrevious;
}