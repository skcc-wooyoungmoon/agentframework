package com.skax.aiplatform.client.sktai.data.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 데이터셋 소스 아카이브 응답 DTO
 * 
 * <p>데이터셋의 원본 파일을 압축한 아카이브 다운로드를 위한 응답 구조입니다.
 * 실제로는 파일 스트림(ZIP/TAR)이 반환되므로, 이 DTO는 메타데이터 용도로 사용됩니다.</p>
 * 
 * <h3>주요 특징:</h3>
 * <ul>
 *   <li><strong>파일 다운로드</strong>: HTTP 응답으로 바이너리 파일 스트림 반환</li>
 *   <li><strong>압축 포맷</strong>: ZIP 또는 TAR 형식</li>
 *   <li><strong>지원 데이터셋 타입</strong>: model_benchmark, rag_evaluation, custom</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 *   <li>데이터셋 원본 파일 일괄 다운로드</li>
 *   <li>데이터셋 백업 및 아카이빙</li>
 *   <li>외부 시스템으로 데이터셋 전송</li>
 *   <li>로컬 환경에서 원본 데이터 분석</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * // Feign Client는 ResponseEntity&lt;Resource&gt;를 반환
 * ResponseEntity&lt;Resource&gt; response = client.getDatasetSourceArchive(datasetId);
 * Resource resource = response.getBody();
 * // resource를 파일로 저장
 * </pre>
 * 
 * <h3>주의사항:</h3>
 * <ul>
 *   <li>대용량 데이터셋의 경우 압축 시간이 소요될 수 있습니다</li>
 *   <li>네트워크 대역폭과 저장 공간을 고려해야 합니다</li>
 *   <li>지원되는 데이터셋 타입만 아카이브 다운로드가 가능합니다</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-11-11
 * @version 1.0
 * @see Dataset 데이터셋 기본 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = """
        SKTAI 데이터셋 소스 아카이브 다운로드 응답
        
        실제 응답은 파일 스트림(application/zip 또는 application/x-tar)입니다.
        이 DTO는 메타데이터 및 문서화 목적으로 사용됩니다.
        """,
    example = """
        {
          "contentType": "application/zip",
          "filename": "dataset-123e4567.zip",
          "size": 10485760
        }
        """
)
public class DatasetSourceArchive {
    
    /**
     * 콘텐츠 타입
     * 
     * <p>다운로드되는 아카이브 파일의 MIME 타입입니다.
     * 일반적으로 "application/zip" 또는 "application/x-tar"입니다.</p>
     */
    @Schema(
        description = "아카이브 파일의 MIME 타입", 
        example = "application/zip",
        allowableValues = {"application/zip", "application/x-tar", "application/gzip"}
    )
    private String contentType;
    
    /**
     * 파일명
     * 
     * <p>다운로드될 아카이브 파일의 이름입니다.
     * 일반적으로 "dataset-{uuid}.zip" 형태로 제공됩니다.</p>
     */
    @Schema(
        description = "다운로드 파일명", 
        example = "dataset-123e4567-e89b-12d3-a456-426614174000.zip"
    )
    private String filename;
    
    /**
     * 파일 크기
     * 
     * <p>다운로드될 아카이브 파일의 전체 크기를 바이트 단위로 나타냅니다.</p>
     */
    @Schema(
        description = "아카이브 파일 크기 (바이트)", 
        example = "10485760",
        minimum = "0"
    )
    private Long size;
}
