package com.skax.aiplatform.client.sktai.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 프로젝트 생성 완료 응답 DTO
 * 
 * <p>새로운 프로젝트 생성이 완료된 후 반환되는 응답 데이터 구조입니다.
 * 생성된 프로젝트와 네임스페이스의 기본 정보를 포함합니다.</p>
 * 
 * <h3>생성 프로세스:</h3>
 * <ol>
 *   <li><strong>프로젝트 생성</strong>: 기본 프로젝트 정보 등록</li>
 *   <li><strong>네임스페이스 할당</strong>: 리소스 할당량 설정</li>
 *   <li><strong>응답 반환</strong>: 생성 완료 정보 제공</li>
 * </ol>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>프로젝트 생성 완료 확인</li>
 *   <li>생성된 리소스 정보 표시</li>
 *   <li>프로젝트 대시보드로 리다이렉션</li>
 *   <li>초기 설정 가이드 제공</li>
 * </ul>
 * 
 * <h3>응답 예시:</h3>
 * <pre>
 * {
 *   "id": "proj-new-123",
 *   "name": "NewProject",
 *   "namespace_id": "ns-new-456"
 * }
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 * @see ProjectPayload 프로젝트 기본 정보
 * @see Namespace 네임스페이스 상세 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 프로젝트 생성 완료 응답",
    example = """
        {
          "id": "proj-new-123",
          "name": "NewProject",
          "namespace_id": "ns-new-456"
        }
        """
)
public class CreatedClientRead {
    
    /**
     * 생성된 프로젝트 고유 식별자
     * 
     * <p>새로 생성된 프로젝트의 고유 ID입니다.
     * 이 ID는 향후 프로젝트 관련 모든 API 호출에서 사용됩니다.</p>
     * 
     * @implNote 프로젝트 ID는 시스템에서 자동 생성되며 변경되지 않습니다.
     */
    @JsonProperty("id")
    @Schema(
        description = "생성된 프로젝트 고유 식별자",
        example = "proj-new-123",
        required = true
    )
    private String id;
    
    /**
     * 생성된 프로젝트 이름
     * 
     * <p>사용자가 지정한 프로젝트의 표시명입니다.
     * 생성 요청 시 제공된 이름이 그대로 반환됩니다.</p>
     * 
     * @apiNote 프로젝트 이름은 생성 후에도 수정 가능합니다.
     */
    @JsonProperty("name")
    @Schema(
        description = "생성된 프로젝트 이름",
        example = "NewProject",
        required = true
    )
    private String name;
    
    /**
     * 할당된 네임스페이스 식별자
     * 
     * <p>프로젝트에 할당된 네임스페이스의 고유 ID입니다.
     * 이 네임스페이스를 통해 리소스 관리와 접근 제어가 이루어집니다.</p>
     * 
     * @apiNote 네임스페이스는 프로젝트 생성 시 자동으로 할당되거나 기존 네임스페이스가 연결됩니다.
     */
    @JsonProperty("namespace_id")
    @Schema(
        description = "프로젝트에 할당된 네임스페이스 식별자",
        example = "ns-new-456",
        required = true
    )
    private String namespaceId;
}
