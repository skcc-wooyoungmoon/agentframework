package com.skax.aiplatform.client.sktai.knowledge.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Tool Type 정의 응답 DTO
 * 
 * <p>각 Tool 타입의 스키마 정의를 담는 응답 데이터 구조입니다.
 * Tool 생성 시 필요한 connection_info_args의 구조와 지원하는 파일 확장자 정보를 포함합니다.</p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 *   <li><strong>기본 정보</strong>: 타입, 표시명, 활성화 상태</li>
 *   <li><strong>연결 정보 스키마</strong>: connection_info_args의 동적 구조 (Object로 처리)</li>
 *   <li><strong>지원 파일 확장자</strong>: 해당 Tool이 처리할 수 있는 파일 형식</li>
 * </ul>
 * 
 * <h3>Tool 타입 예시:</h3>
 * <ul>
 *   <li><strong>default</strong>: 기본 로더, connection_info_args가 null</li>
 *   <li><strong>AzureDocumentIntelligence</strong>: Azure AI 서비스, endpoint, key 필요</li>
 *   <li><strong>SynapsoftDA</strong>: Synapsoft DocuAnalyzer, 다양한 옵션 지원</li>
 *   <li><strong>SKTDocumentInsight</strong>: SKT Document Insight, deployment_name 등 필요</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "Tool Type 정의 정보"
)
public class ArgsResponse {
  
    @JsonProperty("data")
    @Schema(
        description = "Tool 타입 식별자",
        example = "AzureDocumentIntelligence",
        allowableValues = {
            "default",
            "AzureDocumentIntelligence", 
            "NaverOCR",
            "SynapsoftDA",
            "Docling",
            "SKTDocumentInsight"
        }
    )
    private List<ArgResponse> data;
}
