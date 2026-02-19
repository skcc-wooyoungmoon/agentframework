package com.skax.aiplatform.controller.common;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.dto.common.request.FileDocumentRequest;
import com.skax.aiplatform.dto.common.response.FileDocumentResponse;
import com.skax.aiplatform.service.common.FileDocumentExecuteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * fileDocument 수신 및 실행 컨트롤러
 * 
 * <p>
 * 프론트엔드에서 전달받은 fileDocument 수신하고 실행하는 API를 제공합니다.
 * </p>
 * 
 * @author Generated
 * @since 2025-01-XX
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/fileDocument")
@RequiredArgsConstructor
@Tag(name = "fileDocument", description = "파일 다큐먼트 수신 및 실행 API")
public class FileDocumentController {

        private final FileDocumentExecuteService fileDocumentExecuteService;

        /**
         * fileDocument 실행
         * 
         * <p>
         * 프론트엔드에서 전달받은 파일 다큐먼트를 실행하고 결과를 반환합니다.
         * </p>
         * 
         * @param request fileDocument 실행 요청
         * @return fileDocument 실행 결과
         */
        @PostMapping("/execute")
        @Operation(summary = "파일 다큐먼트 저장", description = "위험한 키워드는 차단됩니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "fileDocument 실행 성공", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FileDocumentResponse.class))),
                        @ApiResponse(responseCode = "400", description = "잘못된 fileDocument"),
                        @ApiResponse(responseCode = "401", description = "인증 실패"),
                        @ApiResponse(responseCode = "403", description = "권한 없음"),
                        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        })
        @SecurityRequirement(name = "bearerAuth")
        public AxResponseEntity<FileDocumentResponse> executeFileDocument(
                        @Valid @RequestBody FileDocumentRequest request) {
                log.info("🔍 실행 요청 - fileDocument 길이: {}자",
                                request.getFileDocument() != null ? request.getFileDocument().length() : 0);

                FileDocumentResponse response = fileDocumentExecuteService.executeFileDocument(request);

                log.info("✅ 실행 완료 - 결과 행 수: {}, 실행 시간: {}ms",
                                response.getRowCount(), response.getExecutionTimeMs());

                return AxResponseEntity.ok(response, "실행이 완료되었습니다.");
        }
}
