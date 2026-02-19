package com.skax.aiplatform.client.sktai.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * SKTAI Model Custom Runtime 수정 요청 DTO
 * 
 * <p>기존 모델의 커스텀 런타임 구성을 수정하기 위한 요청 데이터 구조입니다.
 * Docker 이미지, 실행 명령어, 인수 등의 런타임 설정을 변경할 수 있습니다.</p>
 * 
 * <h3>주요 수정 가능 항목:</h3>
 * <ul>
 *   <li><strong>image_url</strong>: 새로운 Docker 이미지 URL</li>
 *   <li><strong>use_bash</strong>: bash 사용 여부 변경</li>
 *   <li><strong>command</strong>: 컨테이너 실행 명령어 변경</li>
 *   <li><strong>args</strong>: 컨테이너 실행 인수 변경</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * ModelCustomRuntimeUpdate request = ModelCustomRuntimeUpdate.builder()
 *     .imageUrl("registry.example.com/my-model:v2.0")
 *     .useBash(true)
 *     .command(List.of("python", "-m", "model_server"))
 *     .args(List.of("--port", "8080", "--workers", "4"))
 *     .build();
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-09-01
 * @version 1.0
 * @see ModelCustomRuntimeCreate 커스텀 런타임 생성 요청
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "SKTAI Model Custom Runtime 수정 요청 정보",
    example = """
        {
          "image_url": "registry.example.com/my-model:v2.0",
          "use_bash": true,
          "command": ["python", "-m", "model_server"],
          "args": ["--port", "8080", "--workers", "4"]
        }
        """
)
public class ModelCustomRuntimeUpdate {
    
    /**
     * 커스텀 Docker 이미지 URL
     * 
     * <p>수정할 Docker 이미지의 전체 URL입니다.
     * 레지스트리 주소, 이미지명, 태그를 포함한 완전한 URL을 제공해야 합니다.</p>
     * 
     * @implNote 이미지는 SKTAI 플랫폼에서 접근 가능한 레지스트리에 있어야 합니다.
     */
    @JsonProperty("image_url")
    @Schema(
        description = "수정할 커스텀 Docker 이미지 URL", 
        example = "registry.example.com/my-model:v2.0",
        maxLength = 1000
    )
    private String imageUrl;
    
    /**
     * bash 사용 여부
     * 
     * <p>컨테이너 실행 시 bash 셸을 사용할지 여부를 결정합니다.
     * true로 설정하면 bash를 통해 명령어를 실행하고, false면 직접 실행합니다.</p>
     * 
     * @implNote 복잡한 셸 스크립트나 환경 변수 설정이 필요한 경우 true로 설정하는 것이 좋습니다.
     */
    @JsonProperty("use_bash")
    @Schema(
        description = "bash 사용 여부 (복잡한 명령어 실행 시 권장)", 
        example = "true"
    )
    private Boolean useBash;
    
    /**
     * 컨테이너 실행 명령어 배열
     * 
     * <p>컨테이너가 시작될 때 실행할 명령어를 배열 형태로 지정합니다.
     * 각 배열 요소는 하나의 명령어 부분을 나타냅니다.</p>
     * 
     * @apiNote 예: ["python", "-m", "model_server"] 또는 ["./start.sh"]
     */
    @JsonProperty("command")
    @Schema(
        description = "컨테이너 실행 명령어 배열",
        example = """
            ["python", "-m", "model_server"]
            """
    )
    private List<String> command;
    
    /**
     * 컨테이너 실행 인수 배열
     * 
     * <p>명령어에 전달할 인수들을 배열 형태로 지정합니다.
     * 포트, 워커 수, 설정 파일 경로 등의 런타임 파라미터를 설정할 수 있습니다.</p>
     * 
     * @apiNote 예: ["--port", "8080", "--workers", "4"]
     */
    @JsonProperty("args")
    @Schema(
        description = "컨테이너 실행 인수 배열",
        example = """
            ["--port", "8080", "--workers", "4"]
            """
    )
    private List<String> args;
}
