package com.skax.aiplatform.client.sktai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Payload;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI 그룹 목록 응답 DTO
 * 
 * <p>SKTAI Auth API에서 그룹 목록을 반환할 때 사용하는 페이징된 응답 DTO입니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "그룹 목록 응답")
public class GroupsResponse {
    @JsonProperty("data")
    @Schema(
            description = "그룹 목록",
            example = """
            [
                {
                  "id": "e9d9e62d-2143-45c8-84e2-5c703aa00d90",
                  "name": "ygrouptest2",
                  "path": "/ygrouptest2"
                },
                {
                  "id": "a64de1a7-48c8-4bde-a9fa-1c00a44de787",
                  "name": "ygrouptest1",
                  "path": "/ygrouptest1"
                },
                {
                  "id": "ad3920c4-b9fd-40ae-b15a-3a798abad328",
                  "name": "public",
                  "path": "/public"
                }
            ]
            """
    )
    private List<GroupResponse> groupList;

    /**
     * 페이징 정보
     *
     * <p>현재 조회 결과의 페이징 관련 메타데이터입니다.
     * 전체 사용자 수, 현재 페이지, 페이지 크기, 총 페이지 수 등을 포함합니다.</p>
     *
     * @apiNote 대용량 사용자 데이터를 효율적으로 처리하기 위한 페이징 정보를 제공합니다.
     */
    @JsonProperty("payload")
    @Schema(
            description = "페이로드",
            required = true
    )
    private Payload payload;
}
