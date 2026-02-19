package com.skax.aiplatform.dto.data.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 데이터소스 파일 목록 조회 응답 DTO
 * 
 * @author HyeleeLee
 * @since 2025-01-13
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataCtlgDataSourceFileListRes {
    
    /**
     * 데이터소스 파일 목록
     */
    private List<DataCtlgDataSourceFileRes> data;
    
    /**
     * 페이로드 정보
     */
    private Payload payload;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payload {
        /**
         * 페이지네이션 정보
         */
        private DataCtlgDataSourceFileListPaginationRes pagination;
    }
}