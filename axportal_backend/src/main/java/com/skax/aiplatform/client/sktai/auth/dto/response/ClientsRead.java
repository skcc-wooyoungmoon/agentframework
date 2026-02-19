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
 * SKTAI 프로젝트 목록 조회 응답 DTO
 * 
 * <p>프로젝트 목록 조회 API의 응답을 담는 데이터 구조입니다.
 * 페이징된 프로젝트 목록과 페이징 메타데이터를 포함합니다.</p>
 * 
 * <h3>응답 구조:</h3>
 * <ul>
 *   <li><strong>data</strong>: 프로젝트 목록 배열</li>
 *   <li><strong>payload</strong>: 페이징 정보 (페이지 번호, 전체 개수 등)</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>사용자가 접근 가능한 프로젝트 목록 표시</li>
 *   <li>프로젝트 선택 드롭다운 구성</li>
 *   <li>관리자의 전체 프로젝트 관리 화면</li>
 * </ul>
 * 
 * <h3>응답 예시:</h3>
 * <pre>
 * {
 *   "data": [
 *     {
 *       "project": {"id": "proj-1", "name": "Project A"},
 *       "namespace": {...}
 *     }
 *   ],
 *   "payload": {
 *     "pagination": {
 *       "page": 1,
 *       "total": 25,
 *       "items_per_page": 10
 *     }
 *   }
 * }
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see ClientRead 개별 프로젝트 정보
 * @see Payload 페이징 메타데이터
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 프로젝트 목록 조회 응답",
    example = """
        {
          "data": [
            {
              "project": {
                "id": "proj-123",
                "name": "MyProject"
              },
              "namespace": {
                "id": "ns-456",
                "cpu_quota": 4.0,
                "mem_quota": 8.0,
                "gpu_quota": 1.0
              }
            }
          ],
          "payload": {
            "pagination": {
              "page": 1,
              "total": 25,
              "items_per_page": 10
            }
          }
        }
        """
)
public class ClientsRead {
    
    /**
     * 프로젝트 목록
     * 
     * <p>사용자가 접근 가능한 프로젝트들의 배열입니다.
     * 각 항목은 프로젝트 정보와 네임스페이스 정보를 포함합니다.</p>
     * 
     * @implNote 사용자의 권한에 따라 조회 가능한 프로젝트가 필터링됩니다.
     */
    @JsonProperty("data")
    @Schema(
        description = "프로젝트 목록 배열",
        required = true
    )
    private List<ClientRead> data;
    
    /**
     * 페이징 메타데이터
     * 
     * <p>페이징 처리를 위한 메타데이터를 포함합니다.
     * 현재 페이지, 전체 페이지 수, 총 항목 수 등의 정보가 담겨 있습니다.</p>
     */
    @JsonProperty("payload")
    @Schema(
        description = "페이징 메타데이터",
        required = true
    )
    private Payload payload;
}
