package com.skax.aiplatform.client.lablup.api.dto.response;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.skax.aiplatform.client.lablup.api.deserializer.JsonStringDeserializer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lablup 엔드포인트 조회 응답 DTO
 * 
 * <p>
 * Backend.AI 엔드포인트 조회 결과를 담는 GraphQL 응답 데이터 구조입니다.
 * endpoint 쿼리의 결과로 특정 엔드포인트의 상세 정보와 라우팅 정보를 제공합니다.
 * </p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 * <li><strong>엔드포인트 기본 정보</strong>: ID, 이름, 레플리카 수, 상태</li>
 * <li><strong>이미지 정보</strong>: 레지스트리, 프로젝트, 기본 이미지명, 태그</li>
 * <li><strong>모델 정보</strong>: 모델 정의 경로, URL</li>
 * <li><strong>공개 설정</strong>: 공개 여부</li>
 * <li><strong>생성 정보</strong>: 생성 사용자, 생성 시각</li>
 * <li><strong>런타임 정보</strong>: 런타임 variant</li>
 * <li><strong>라우팅 정보</strong>: 라우팅 ID, 세션, 상태, 트래픽 비율</li>
 * <li><strong>자원 슬롯 정보</strong>: CPU, 메모리, GPU 등의 자원 할당량</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-01-27
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Lablup 엔드포인트 조회 응답 정보 (GraphQL)", example = """
        {
          "endpoint": {
            "endpoint_id": "a83e9259-d642-4158-ac4d-06ffc5095017",
            "name": "DeployModelTest1",
            "replicas": 1,
            "status": "DEGRADED",
            "image_object": {
              "registry": "bai-repo:7080",
              "project": "bai",
              "base_image_name": "vllm",
              "tag": "0.10.1-cuda12.8-ubuntu24.04",
              "name": "bai/vllm"
            },
            "model_definition_path": "model-definition.yaml",
            "url": "http://106.252.63.221:21342/",
            "open_to_public": true,
            "created_user": "f38dea23-50fa-42a0-b5ae-338f5f4693f4",
            "created_at": "2025-10-20T00:16:49.427037+00:00",
            "runtime_variant": {
              "name": "vllm"
            },
            "routings": [
              {
                "routing_id": "310ae76d-17a9-4dcf-931f-477cf5a639ea",
                "session": "c182a9d3-147e-4ee1-a465-c3646d7a1758",
                "status": "UNHEALTHY",
                "traffic_ratio": 1.0
              }
            ],
            "resource_slots": {
              "cpu": "2",
              "mem": "4294967296",
              "cuda.device": "1"
            }
          }
        }
        """)
public class GetEndpointResponse {

    /**
     * 엔드포인트 정보
     */
    @Schema(description = "엔드포인트 상세 정보")
    private Endpoint endpoint;

    /**
     * GraphQL 오류 정보
     */
    @Schema(description = "GraphQL 오류 정보")
    private List<Error> errors;

    /**
     * GraphQL 응답 데이터 구조
     */
    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "GraphQL 데이터 섹션")
    public static class Data {

        /**
         * 엔드포인트 정보
         */
        @Schema(description = "엔드포인트 상세 정보")
        private Endpoint endpoint;
    }

    /**
     * 엔드포인트 정보 구조
     */
    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "엔드포인트 상세 정보")
    public static class Endpoint {

        /**
         * 엔드포인트 ID
         */
        @Schema(description = "엔드포인트 고유 식별자 (UUID)", example = "a83e9259-d642-4158-ac4d-06ffc5095017")
        private String endpointId;

        /**
         * 엔드포인트 이름
         */
        @Schema(description = "엔드포인트 이름", example = "DeployModelTest1")
        private String name;

        /**
         * 레플리카 수
         */
        @Schema(description = "레플리카 수", example = "1")
        private Integer replicas;

        /**
         * 상태
         */
        @Schema(description = "엔드포인트 현재 상태", example = "DEGRADED", allowableValues = { "RUNNING", "DEGRADED",
                "TERMINATING", "TERMINATED", "UNHEALTHY" })
        private String status;

        /**
         * 이미지 객체 정보
         */
        @Schema(description = "엔드포인트에 사용된 이미지 정보")
        private ImageObject imageObject;

        /**
         * 모델 정의 경로
         */
        @Schema(description = "모델 정의 파일 경로", example = "model-definition.yaml")
        private String modelDefinitionPath;

        /**
         * URL
         */
        @Schema(description = "엔드포인트 URL", example = "http://106.252.63.221:21342/")
        private String url;

        /**
         * 공개 여부
         */
        @Schema(description = "공개된 엔드포인트 여부", example = "true")
        private Boolean openToPublic;

        /**
         * 생성 사용자
         */
        @Schema(description = "엔드포인트를 생성한 사용자 ID", example = "f38dea23-50fa-42a0-b5ae-338f5f4693f4")
        private String createdUser;

        /**
         * 생성 시각
         */
        @Schema(description = "엔드포인트 생성 시각", example = "2025-10-20T00:16:49.427037+00:00")
        private String createdAt;

        /**
       * 런타임 variant 정보
         */
        @Schema(description = "런타임 variant 정보")
        private RuntimeVariant runtimeVariant;

        /**
         * 라우팅 정보 목록
         */
        @Schema(description = "라우팅 정보 목록")
        private List<Routing> routings;

        /**
         * 자원 슬롯 정보
         * 
         * <p>
         * 엔드포인트에 할당된 자원 정보입니다.
         * CPU, 메모리, GPU 등의 자원 할당량을 포함합니다.
         * </p>
         */
        @JsonProperty("resource_slots")
        @JsonDeserialize(using = JsonStringDeserializer.class)
        @Schema(description = "자원 슬롯 정보 (cpu, mem, cuda.device 등)", example = """
                {
                  "cpu": "2",
                  "mem": "4294967296",
                  "cuda.shares": "1"
                }
                """)
        private Map<String, Object> resourceSlots;
    }

    /**
     * 이미지 객체 정보
     */
    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "이미지 객체 정보")
    public static class ImageObject {

        /**
         * 레지스트리
         */
        @Schema(description = "이미지 레지스트리 주소", example = "bai-repo:7080")
        private String registry;

        /**
         * 프로젝트
         */
        @Schema(description = "프로젝트 이름", example = "bai")
        private String project;

        /**
         * 기본 이미지 이름
         */
        @Schema(description = "기본 이미지 이름", example = "vllm")
        private String baseImageName;

        /**
         * 태그
         */
        @Schema(description = "이미지 태그", example = "0.10.1-cuda12.8-ubuntu24.04")
        private String tag;

        /**
         * 이미지 이름
         */
        @Schema(description = "이미지 전체 이름", example = "bai/vllm")
        private String name;
    }

    /**
     * 런타임 variant 정보
     */
    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "런타임 variant 정보")
    public static class RuntimeVariant {

        /**
         * variant 이름
         */
        @Schema(description = "런타임 variant 이름", example = "vllm")
        private String name;
    }

    /**
     * 라우팅 정보
     */
    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "라우팅 정보")
    public static class Routing {

        /**
         * 라우팅 ID
         */
        @Schema(description = "라우팅 고유 식별자 (UUID)", example = "310ae76d-17a9-4dcf-931f-477cf5a639ea")
        private String routingId;

        /**
         * 세션 ID
         */
        @Schema(description = "세션 ID (UUID)", example = "c182a9d3-147e-4ee1-a465-c3646d7a1758")
        private String session;

        /**
         * 상태
         */
        @Schema(description = "라우팅 현재 상태", example = "UNHEALTHY", allowableValues = { "HEALTHY", "UNHEALTHY",
                "TERMINATING", "TERMINATED" })
        private String status;

        /**
         * 트래픽 비율
         */
        @Schema(description = "트래픽 비율 (0.0 ~ 1.0)", example = "1.0")
        private Double trafficRatio;
    }

    /**
     * GraphQL 오류 정보
     */
    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "GraphQL 오류 정보")
    public static class Error {

        /**
         * 오류 메시지
         */
        @Schema(description = "오류 메시지", example = "Field 'unknown_field' doesn't exist on type 'Endpoint'")
        private String message;

        /**
         * 오류 위치 정보
         */
        @Schema(description = "오류 위치 정보")
        private List<Location> locations;

        /**
         * 오류 경로
         */
        @Schema(description = "오류 경로")
        private List<String> path;
    }

    /**
     * GraphQL 오류 위치 정보
     */
    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "GraphQL 오류 위치")
    public static class Location {

        /**
         * 라인 번호
         */
        @Schema(description = "라인 번호", example = "2")
        private Integer line;

        /**
         * 컬럼 번호
         */
        @Schema(description = "컬럼 번호", example = "10")
        private Integer column;
    }
}
