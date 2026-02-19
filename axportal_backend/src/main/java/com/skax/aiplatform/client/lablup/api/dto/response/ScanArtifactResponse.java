package com.skax.aiplatform.client.lablup.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 아티팩트 스캔 응답 DTO
 * 
 * <p>
 * 아티팩트 스캔 작업의 결과를 담는 응답 정보입니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-02
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanArtifactResponse {

    /**
     * 아티팩트 목록
     */
    private List<Artifact> artifacts;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Artifact {
        /**
         * 아티팩트 ID (UUID)
         */
        private String id;

        /**
         * 아티팩트 이름
         */
        private String name;

        /**
         * 아티팩트 유형 (MODEL, PACKAGE, IMAGE)
         */
        private String type;

        /**
         * 아티팩트 설명 (허깅페이스의 경우 null)
         */
        private String description;

        /**
         * 레지스트리 ID (UUID)
         */
        private String registryId;

        /**
         * 원본 레지스트리 ID (UUID)
         */
        private String sourceRegistryId;

        /**
         * 레지스트리 유형 (huggingface, reservoir)
         */
        private String registryType;

        /**
         * 원본 레지스트리 유형
         */
        private String sourceRegistryType;

        /**
         * 가용성 상태 (ALIVE, DEAD 등)
         */
        private String availability;

        /**
         * 스캔 일시
         */
        private LocalDateTime scannedAt;

        /**
         * 업데이트 일시
         */
        private LocalDateTime updatedAt;

        /**
         * 읽기 전용 여부
         */
        private Boolean readonly;

        /**
         * 리비전 목록
         */
        private List<Revision> revisions;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Revision {
        /**
         * 리비전 ID (UUID)
         */
        private String id;

        /**
         * 아티팩트 ID (UUID)
         */
        private String artifactId;

        /**
         * 버전명
         */
        private String version;

        /**
         * 크기 (바이트)
         */
        private Long size;

        /**
         * 상태 (SCANNED, SCANNING 등)
         */
        private String status;

        /**
         * 원격 상태
         */
        private String remoteStatus;

        /**
         * 생성 일시
         */
        private LocalDateTime createdAt;

        /**
         * 업데이트 일시
         */
        private LocalDateTime updatedAt;
    }
}