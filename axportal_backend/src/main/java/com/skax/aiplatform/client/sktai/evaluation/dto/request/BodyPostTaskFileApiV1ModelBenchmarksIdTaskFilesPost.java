package com.skax.aiplatform.client.sktai.evaluation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Model Benchmark 작업 파일 업로드 요청 DTO
 * 
 * <p>SKTAI Evaluation 시스템에서 Model Benchmark의 작업 파일을 업로드하기 위한 요청 데이터 구조입니다.
 * 멀티파트 폼 데이터로 전송되는 파일들을 포함합니다.</p>
 * 
 * <h3>업로드 제한사항:</h3>
 * <ul>
 *   <li>허용되지 않는 확장자의 파일은 업로드되지 않습니다</li>
 *   <li>파일 크기 제한이 있을 수 있습니다</li>
 *   <li>지원되는 파일 형식만 업로드 가능합니다</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * // 실제 파일 업로드는 MultipartFile을 사용하여 처리됩니다
 * // 이 DTO는 OpenAPI 문서화를 위한 구조 정의입니다
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Model Benchmark 작업 파일 업로드 요청 정보 (멀티파트 폼 데이터)",
    example = """
        {
          "files": ["[binary file data]"]
        }
        """
)
public class BodyPostTaskFileApiV1ModelBenchmarksIdTaskFilesPost {
    
    /**
     * 업로드할 파일 목록
     * 
     * <p>Model Benchmark 작업에 사용할 파일들입니다.
     * 바이너리 형태로 전송되며, 허용된 확장자의 파일만 업로드됩니다.</p>
     * 
     * @apiNote 실제 구현에서는 MultipartFile[] 형태로 처리됩니다.
     */
    @JsonProperty("files")
    @Schema(
        description = "업로드할 바이너리 파일 목록", 
        example = "[\"[binary file data]\"]",
        required = true,
        format = "binary"
    )
    private List<String> files;
}
