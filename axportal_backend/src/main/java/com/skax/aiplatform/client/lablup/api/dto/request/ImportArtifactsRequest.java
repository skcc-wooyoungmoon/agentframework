package com.skax.aiplatform.client.lablup.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 아티팩트 가져오기 요청 DTO
 * 
 * <p>외부 저장소나 레지스트리에서 아티팩트를 가져오기 위한 요청 정보입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportArtifactsRequest {

    @JsonProperty("artifact_revision_ids")
    @Schema(description = "아티팩트 리비전 ID 목록", example = "[\"artifact-revision-1\", \"artifact-revision-2\"]", required = true)
    private String[] artifactRevisionIds;
    
    // /**
    //  * 소스 정보
    //  */
    // private SourceInfo source;
    
    // /**
    //  * 대상 정보
    //  */
    // private TargetInfo target;
    
    // /**
    //  * 가져오기 옵션
    //  */
    // private ImportOptions options;
    
    // @Data
    // @NoArgsConstructor
    // @AllArgsConstructor
    // @Builder
    // public static class SourceInfo {
    //     /**
    //      * 소스 타입 (registry, storage, git 등)
    //      */
    //     private String type;
        
    //     /**
    //      * 소스 URL
    //      */
    //     private String url;
        
    //     /**
    //      * 인증 정보
    //      */
    //     private AuthenticationInfo authentication;
        
    //     /**
    //      * 소스별 메타데이터
    //      */
    //     private Map<String, Object> metadata;
    // }
    
    // @Data
    // @NoArgsConstructor
    // @AllArgsConstructor
    // @Builder
    // public static class TargetInfo {
    //     /**
    //      * 대상 레지스트리
    //      */
    //     private String registry;
        
    //     /**
    //      * 대상 네임스페이스
    //      */
    //     private String namespace;
        
    //     /**
    //      * 대상 이름
    //      */
    //     private String name;
        
    //     /**
    //      * 대상 태그
    //      */
    //     private String tag;
    // }
    
    // @Data
    // @NoArgsConstructor
    // @AllArgsConstructor
    // @Builder
    // public static class AuthenticationInfo {
    //     /**
    //      * 인증 타입 (basic, oauth, token 등)
    //      */
    //     private String type;
        
    //     /**
    //      * 사용자명
    //      */
    //     private String username;
        
    //     /**
    //      * 비밀번호 또는 토큰
    //      */
    //     private String credential;
    // }
    
    // @Data
    // @NoArgsConstructor
    // @AllArgsConstructor
    // @Builder
    // public static class ImportOptions {
    //     /**
    //      * 기존 아티팩트 덮어쓰기 여부
    //      */
    //     private boolean overwrite;
        
    //     /**
    //      * 스캔 포함 여부
    //      */
    //     private boolean includeScan;
        
    //     /**
    //      * 태그 복사 여부
    //      */
    //     private boolean copyTags;
        
    //     /**
    //      * 병렬 처리 여부
    //      */
    //     private boolean parallel;
    // }
}