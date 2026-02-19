package com.skax.aiplatform.client.sktai.serving.dto.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 커스텀 서빙 파라미터 DTO
 * 
 * <p>SKTAI Serving 시스템에서 커스텀 모델 서빙을 위한 파라미터입니다.
 * 사용자 정의 Docker 이미지와 컨테이너 실행 설정을 포함합니다.</p>
 * 
 * <h3>설정 요소:</h3>
 * <ul>
 *   <li><strong>이미지 URL</strong>: 모델 서빙에 사용할 Docker 이미지</li>
 *   <li><strong>실행 설정</strong>: Bash 사용 여부, 명령어, 인수</li>
 *   <li><strong>엔트리포인트</strong>: 컨테이너 시작 시 실행할 명령어</li>
 *   <li><strong>명령어 인수</strong>: 엔트리포인트에 전달할 인수들</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * CustomServingParams params = CustomServingParams.builder()
 *     .imageUrl("my-custom-model:latest")
 *     .useBash(false)
 *     .command(List.of("uvicorn", "main:app"))
 *     .args(List.of("--host=0.0.0.0", "--port=8080"))
 *     .build();
 * </pre>
 * 
 * <h3>기본값:</h3>
 * <ul>
 *   <li><strong>use_bash</strong>: false</li>
 *   <li><strong>command</strong>: ["uvicorn", "main:app"]</li>
 *   <li><strong>args</strong>: ["--host=0.0.0.0", "--port=8080"]</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 * @see ServingParams 서빙 파라미터
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI 커스텀 서빙 파라미터",
    example = """
        {
          "image_url": "my-custom-model:latest",
          "use_bash": false,
          "command": ["uvicorn", "main:app"],
          "args": ["--host=0.0.0.0", "--port=8080"]
        }
        """
)
public class CustomServingParams {
    
    /**
     * Docker 이미지 URL
     * 
     * <p>모델 서빙에 사용할 Docker 이미지의 경로입니다.
     * 레지스트리 URL과 태그를 포함한 전체 이미지 경로를 지정합니다.</p>
     */
    @JsonProperty("image_url")
    @Schema(
        description = "모델 서빙에 사용할 Docker 이미지 경로",
        example = "my-custom-model:latest"
    )
    private String imageUrl;
    
    /**
     * Bash 쉘 사용 여부
     * 
     * <p>명령어를 Bash 쉘로 실행할지 여부입니다.
     * true로 설정하면 /bin/bash -c로 명령어를 감싸서 실행합니다.</p>
     */
    @JsonProperty("use_bash")
    @Schema(
        description = "Bash 쉘로 명령어 실행 여부",
        example = "false",
        defaultValue = "false"
    )
    private Boolean useBash;
    
    /**
     * 엔트리포인트 명령어
     * 
     * <p>컨테이너 시작 시 실행할 엔트리포인트 명령어입니다.
     * 배열 형태로 명령어와 기본 옵션들을 지정합니다.</p>
     */
    @JsonProperty("command")
    @Schema(
        description = "컨테이너 시작 시 실행할 엔트리포인트 명령어",
        example = "[\"uvicorn\", \"main:app\"]",
        defaultValue = "[\"uvicorn\", \"main:app\"]"
    )
    private List<String> command;
    
    /**
     * 명령어 인수
     * 
     * <p>컨테이너의 엔트리포인트 명령어에 전달할 인수들입니다.
     * 호스트, 포트, 로그 레벨 등의 실행 옵션을 지정합니다.</p>
     */
    @JsonProperty("args")
    @Schema(
        description = "엔트리포인트 명령어에 전달할 인수들",
        example = "[\"--host=0.0.0.0\", \"--port=8080\"]",
        defaultValue = "[\"--host=0.0.0.0\", \"--port=8080\"]"
    )
    private List<String> args;
}