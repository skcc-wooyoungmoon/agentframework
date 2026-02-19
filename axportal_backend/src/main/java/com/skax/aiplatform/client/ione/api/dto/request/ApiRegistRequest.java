package com.skax.aiplatform.client.ione.api.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 등록 요청 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiRegistRequest {
    
    /**
     * API ID
     */
    @JsonProperty("apiId")
    private String apiId;
    
    /**
     * API 명
     */
    @JsonProperty("apiName")
    private String apiName;
    
    /**
     * API 설명
     */
    @JsonProperty("apiDesc")
    private String apiDesc;
    
    /**
     * API 업무 코드
     */
    @JsonProperty("apiTaskId")
    private String apiTaskId;
    
    /**
     * API 조건 설정
     */
    @JsonProperty("predicates")
    private Predicates predicates;


    
    @JsonProperty("mediator")
    private List<Mediator> mediator;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Mediator {
        /**
         * 필터 이름
         */
        @JsonProperty("filterName")
        private String filterName;
        /**
         * 필터 키
         */
        @JsonProperty("filterKey")
        private String filterKey;   
        /**
         * 필터 값
         */
        @JsonProperty("filterValue")
        private String filterValue;
        /**
         * 인코딩 여부
         */
        @JsonProperty("encode")
        private boolean encode;
    }
    
    /**
     * 라우팅 설정
     */
    @JsonProperty("route")
    private Route route;
    
    /**
     * 인증 설정
     */
    @JsonProperty("auth")
    private Auth auth;
    
    /**
     * 트래픽 설정
     */
    @JsonProperty("traffic")
    private Traffic traffic;
    
    /**
     * API 조건 설정 클래스
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Predicates {
        /**
         * API 경로
         */
        @JsonProperty("path")
        private String path;
    }
    
    /**
     * 라우팅 설정 클래스
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Route {
        /**
         * API 서버 그룹 ID
         */
        @JsonProperty("apiSvrGrpId")
        private String apiSvrGrpId;
        
        /**
         * 백엔드 API 경로
         */
        @JsonProperty("path")
        private String path;
        
        /**
         * 포트 번호
         */
        @JsonProperty("port")
        private String port;


        /**
         * 리라이트 경로
         */
        @JsonProperty("rewritePath")
        private String rewritePath;
        
        /**
         * 스트립 접두사
         */
        @JsonProperty("stripPrefix")
        private Integer stripPrefix;
        
        /**
         * 접두사 경로
         */
        @JsonProperty("prefixPath")
        private String prefixPath;
        
    }
    
    /**
     * 인증 설정 클래스
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Auth {
        /**
         * 인증 타입
         */
        @JsonProperty("type")
        private Integer type;
    }
    
    /**
     * 트래픽 설정 클래스
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Traffic {
        /**
         * 연결 타임아웃 (ms)
         */
        @JsonProperty("connectionTimeout")
        private Integer connectionTimeout;
        
        /**
         * 응답 타임아웃 (ms)
         */
        @JsonProperty("responseTimeout")
        private Integer responseTimeout;
    }
}