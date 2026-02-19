package com.skax.aiplatform.client.lablup.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.skax.aiplatform.client.lablup.api.deserializer.JsonStringDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Lablup 스케일링 그룹 조회 응답 DTO
 * 
 * <p>
 * Backend.AI 리소스 그룹별 자원 할당량 조회 결과를 담는 GraphQL 응답 데이터 구조입니다.
 * scaling_groups 쿼리의 결과로 각 리소스 그룹의 상세 정보와 자원 현황을 제공합니다.
 * </p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 * <li><strong>그룹 기본 정보</strong>: 이름, 설명, 활성화 상태</li>
 * <li><strong>드라이버/스케줄러 설정</strong>: 자원 관리 방식</li>
 * <li><strong>네트워크 설정</strong>: 호스트 네트워크 사용 여부</li>
 * <li><strong>자원 슬롯 현황</strong>: 상태별 총 자원 할당량</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Lablup 스케일링 그룹 조회 응답 정보 (GraphQL)", example = """
        {
          "data": {
            "scaling_groups": [
              {
                "name": "default",
                "description": "Default scaling group",
                "is_active": true,
                "created_at": "2025-01-01T00:00:00Z",
                "driver": "static",
                "driver_opts": {},
                "scheduler": "fifo",
                "scheduler_opts": {},
                "use_host_network": false,
                "wsproxy_addr": "127.0.0.1:5050",
                "wsproxy_api_token": "secret-token",
                "agent_total_resource_slots_by_status": {
                  "ALIVE": {
                    "cpu": 32,
                    "mem": "64g",
                    "cuda.device": 4
                  }
                }
              }
            ]
          }
        }
        """)
public class GetScalingGroupsResponse {

    /**
     * 스케일링 그룹 목록
     */
    @JsonProperty("scaling_groups")
    @Schema(description = "스케일링 그룹 목록")
    private List<ScalingGroup> scalingGroups;

    /**
     * GraphQL 오류 정보
     */
    @Schema(description = "GraphQL 오류 정보")
    private List<Error> errors;

    /**
     * 스케일링 그룹 정보
     */
    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "스케일링 그룹 상세 정보")
    public static class ScalingGroup {

        /**
         * 스케일링 그룹 이름
         */
        @Schema(description = "스케일링 그룹 이름", example = "default")
        private String name;

        /**
         * 스케일링 그룹 설명
         */
        @Schema(description = "스케일링 그룹 설명", example = "Default scaling group")
        private String description;

        /**
         * 활성화 상태
         */
        @JsonProperty("is_active")
        @Schema(description = "활성화 상태", example = "true")
        private Boolean isActive;

        /**
         * 생성일시
         */
        @JsonProperty("created_at")
        @Schema(description = "생성일시", example = "2025-01-01T00:00:00Z")
        private String createdAt;

        /**
         * 드라이버 타입
         */
        @Schema(description = "드라이버 타입", example = "static")
        private String driver;

        /**
         * 드라이버 옵션
         */
        @JsonProperty("driver_opts")
        @JsonDeserialize(using = JsonStringDeserializer.class)
        @Schema(description = "드라이버 옵션")
        private Map<String, Object> driverOpts;

        /**
         * 스케줄러 타입
         */
        @Schema(description = "스케줄러 타입", example = "fifo")
        private String scheduler;

        /**
         * 스케줄러 옵션
         */
        @JsonProperty("scheduler_opts")
        @JsonDeserialize(using = JsonStringDeserializer.class)
        @Schema(description = "스케줄러 옵션")
        private Map<String, Object> schedulerOpts;

        /**
         * 호스트 네트워크 사용 여부
         */
        @JsonProperty("use_host_network")
        @Schema(description = "호스트 네트워크 사용 여부", example = "false")
        private Boolean useHostNetwork;

        /**
         * 웹소켓 프록시 주소
         */
        @JsonProperty("wsproxy_addr")
        @Schema(description = "웹소켓 프록시 주소", example = "127.0.0.1:5050")
        private String wsproxyAddr;

        /**
         * 웹소켓 프록시 API 토큰
         */
        @JsonProperty("wsproxy_api_token")
        @Schema(description = "웹소켓 프록시 API 토큰", example = "secret-token")
        private String wsproxyApiToken;

        /**
         * 상태별 총 자원 슬롯 현황
         * 
         * <p>
         * 에이전트 상태별로 그룹화된 총 자원 할당량입니다.
         * occupied_slots, available_slots 등의 키를 가진 Map입니다.
         * </p>
         */
        @JsonProperty("agent_total_resource_slots_by_status")
        @JsonDeserialize(using = JsonStringDeserializer.class)
        @Schema(description = "상태별 총 자원 슬롯 현황")
        private Map<String, Object> agentTotalResourceSlotsByStatus;
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
        @Schema(description = "오류 메시지", example = "Field 'unknown_field' doesn't exist on type 'ScalingGroup'")
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