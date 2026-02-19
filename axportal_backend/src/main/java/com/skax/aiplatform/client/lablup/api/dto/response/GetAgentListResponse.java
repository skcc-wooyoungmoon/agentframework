package com.skax.aiplatform.client.lablup.api.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;
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
 * Lablup 에이전트 목록 조회 응답 DTO
 * 
 * <p>
 * Backend.AI 노드별 자원 할당량 조회 결과를 담는 응답 데이터 구조입니다.
 * agent_list 쿼리의 결과로 각 에이전트(노드)의 자원 상태를 제공합니다.
 * </p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 * <li><strong>에이전트 기본 정보</strong>: ID, 주소, 상태</li>
 * <li><strong>스케일링 그룹</strong>: 소속 리소스 그룹</li>
 * <li><strong>스케줄링 설정</strong>: 스케줄링 가능 여부</li>
 * <li><strong>자원 슬롯 현황</strong>: 사용 가능/점유된 자원량</li>
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
@Schema(description = "Lablup 에이전트 목록 조회 응답 정보", example = """
        {
          "agent_list": {
            "items": [
              {
                "id": "agent-001",
                "addr": "192.168.1.100:6001",
                "status": "ALIVE",
                "scaling_group": "default",
                "schedulable": true,
                "available_slots": "{\\"cpu\\": \\"8\\", \\"mem\\": \\"16g\\"}",
                "occupied_slots": "{\\"cpu\\": \\"2\\", \\"mem\\": \\"4g\\"}"
              }
            ],
            "total_count": 10
          }
        }
        """)
public class GetAgentListResponse {

    /**
     * 에이전트 목록 정보
     */
    @JsonProperty("agent_list")
    @Schema(description = "에이전트 목록 정보")
    private AgentList agentList;

    /**
     * 에이전트 목록 구조
     */
    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "에이전트 목록 구조")
    public static class AgentList {

        /**
         * 에이전트 목록
         */
        @Schema(description = "에이전트 목록")
        private List<Agent> items;

        /**
         * 총 에이전트 개수
         */
        @JsonProperty("total_count")
        @Schema(description = "총 에이전트 개수", example = "10")
        private Integer totalCount;
    }

    /**
     * 에이전트 정보
     */
    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "에이전트 상세 정보")
    public static class Agent {

        /**
         * 에이전트 ID
         */
        @Schema(description = "에이전트 고유 식별자", example = "agent-001")
        private String id;

        /**
         * 에이전트 주소
         */
        @Schema(description = "에이전트 네트워크 주소", example = "192.168.1.100:6001")
        private String addr;

        /**
         * 에이전트 상태
         */
        @Schema(description = "에이전트 현재 상태", example = "ALIVE", allowableValues = { "ALIVE", "TERMINATED", "LOST",
                "RESTARTING" })
        private String status;

        /**
         * 소속 스케일링 그룹
         * 
         * <p>
         * 역직렬화 시: JSON의 "scaling_group" 키를 매핑
         * 직렬화 시: 필드명 "scalingGroup"을 카멜케이스로 사용
         * </p>
         */
        @JsonAlias("scaling_group")
        @Schema(description = "소속 스케일링 그룹", example = "default")
        private String scalingGroup;

        /**
         * 스케줄링 가능 여부
         */
        @Schema(description = "작업 스케줄링 가능 여부", example = "true")
        private Boolean schedulable;

        /**
         * 사용 가능한 자원 슬롯
         * 
         * <p>
         * 해당 에이전트에서 현재 사용 가능한 총 자원량입니다.
         * CPU, 메모리, GPU 등의 자원 타입별로 제공됩니다.
         * </p>
         * 
         * <p>
         * 역직렬화 시: JSON의 "available_slots" 키를 매핑
         * 직렬화 시: 필드명 "availableSlots"를 카멜케이스로 사용
         * </p>
         */
        @JsonAlias("available_slots")
        @JsonDeserialize(using = JsonStringDeserializer.class)
        @Schema(description = "사용 가능한 자원 슬롯 (총 보유 자원량)", example = "{\"cpu\": \"31\", \"mem\": \"133194317824\"}")
        private Map<String, Object> availableSlots;

        /**
         * 현재 점유된 자원 슬롯
         * 
         * <p>
         * 해당 에이전트에서 현재 실행 중인 세션들이 점유하고 있는 자원량입니다.
         * available_slots에서 occupied_slots을 빼면 실제 할당 가능한 자원량을 계산할 수 있습니다.
         * </p>
         * 
         * <p>
         * 역직렬화 시: JSON의 "occupied_slots" 키를 매핑
         * 직렬화 시: 필드명 "occupiedSlots"를 카멜케이스로 사용
         * </p>
         */
        @JsonAlias("occupied_slots")
        @JsonDeserialize(using = JsonStringDeserializer.class)
        @Schema(description = "현재 점유된 자원 슬롯 (현재 할당된 자원량)", example = "{\"cpu\": \"2\", \"mem\": \"4g\"}")
        private Map<String, Object> occupiedSlots;
    }
}