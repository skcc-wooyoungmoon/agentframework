package com.skax.aiplatform.dto.common.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * fileDocument 요청 DTO
 * 
 * <p>
 * 프론트엔드에서 전달받은 fileDocument 담는 요청 DTO입니다.
 * </p>
 * 
 * @author Generated
 * @since 2025-01-XX
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "fileDocument 요청")
public class FileDocumentRequest {

    @NotBlank(message = "fileDocument 필수입니다.")
    @Size(max = 4000, message = "fileDocument 최대 4,000자까지 입력 가능합니다.")
    @Schema(description = "fileDocument", example = "SELECT * FROM users LIMIT 10", required = true)
    private String fileDocument;
}
