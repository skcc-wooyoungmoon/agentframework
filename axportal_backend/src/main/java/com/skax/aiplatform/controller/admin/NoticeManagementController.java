package com.skax.aiplatform.controller.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.admin.request.NoticeManagementCreateReq;
import com.skax.aiplatform.dto.admin.request.NoticeManagementSearchReq;
import com.skax.aiplatform.dto.admin.request.NoticeManagementUpdateReq;
import com.skax.aiplatform.dto.admin.response.NoticeManagementRes;
import com.skax.aiplatform.entity.NoticeFile;
import com.skax.aiplatform.service.admin.NoticeManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController
@RequestMapping("/admin/notices")
@RequiredArgsConstructor
@Tag(name = "NoticeManagementController", description = "공지사항 관리 API")
public class NoticeManagementController {

        private final NoticeManagementService noticeManagementService;

        @GetMapping
        @Operation(summary = "공지사항 목록 조회", description = "공지사항 목록을 조회합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "공지사항 목록 조회 성공"),
                        @ApiResponse(responseCode = "500", description = "서버 오류")
        })
        public AxResponseEntity<PageResponse<NoticeManagementRes>> getNotices(
                        @PageableDefault(size = 20) Pageable pageable,
                        @RequestParam(value = "dateType", required = false) String dateType,
                        @RequestParam(value = "startDate", required = false) String startDate,
                        @RequestParam(value = "endDate", required = false) String endDate,
                        @RequestParam(value = "searchType", required = false) String searchType,
                        @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
                        @RequestParam(value = "type", required = false) String type,
                        @RequestParam(value = "status", required = false) String status) {

                log.info("공지사항 목록 조회 요청: page={}, size={}, sort={}, dateType={}, startDate={}, endDate={}, searchType={}, searchKeyword={}, type={}, status={}",
                        pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort(),
                        dateType, startDate, endDate, searchType, searchKeyword, type, status);

                NoticeManagementSearchReq searchReq = NoticeManagementSearchReq.builder()
                        .dateType(dateType)
                        .startDate(startDate)
                        .endDate(endDate)
                        .searchType(searchType)
                        .searchKeyword(searchKeyword)
                        .type(type)
                        .status(status)
                        .build();

                Page<NoticeManagementRes> notices = noticeManagementService.getNotices(pageable, searchReq);
                if (notices == null) {
                        log.error("공지사항 목록 조회 실패: 서비스에서 null을 반환했습니다.");
                        throw new RuntimeException("공지사항 목록을 조회할 수 없습니다.");
                }
                log.info("공지사항 목록 조회 성공: 총 {}개, 현재 페이지 {}개",
                        notices.getTotalElements(), notices.getNumberOfElements());
                return AxResponseEntity.okPage(notices, "공지사항 목록을 조회했습니다.");
        }

        @GetMapping("/{id}")
        @Operation(summary = "ID로 공지사항 조회", description = "ID로 공지사항 조회")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "공지사항 조회 성공"),
                        @ApiResponse(responseCode = "404", description = "공지사항 조회 실패")
        })
        public AxResponseEntity<NoticeManagementRes> getNoticeById(
                        @PathVariable @Parameter(description = "공지사항 ID", example = "1") Long id) {

                NoticeManagementRes notice = noticeManagementService.getNotice(id);
                if (notice == null) {
                        log.error("공지사항 조회 실패: 서비스에서 null을 반환했습니다. ID: {}", id);
                        throw new RuntimeException("공지사항을 조회할 수 없습니다.");
                }
                return AxResponseEntity.ok(notice, "공지사항을 조회했습니다.");
        }

        @PostMapping
        @Operation(summary = "새로운 공지사항 등록", description = "새로운 공지사항을 등록합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "공지 등록 성공"),
                        @ApiResponse(responseCode = "400", description = "공지 등록 실패, 잘못된 데이터"),
                        @ApiResponse(responseCode = "409", description = "중복 데이터")
        })
        public AxResponseEntity<NoticeManagementRes> createNotice(
                        @Valid @RequestBody NoticeManagementCreateReq createReq) {
                if (createReq == null) {
                        log.error("공지사항 생성 요청 실패: 요청 데이터가 null입니다.");
                        throw new RuntimeException("공지사항 생성 요청 데이터가 없습니다.");
                }
                log.info("공지사항 생성 요청: {}", createReq.toString());
                NoticeManagementRes notice = noticeManagementService.createNotice(createReq);
                if (notice == null) {
                        log.error("공지사항 생성 실패: 서비스에서 null을 반환했습니다.");
                        throw new RuntimeException("공지사항 생성에 실패했습니다.");
                }
                log.info("공지사항 생성 성공: {}", notice.getNotiId());
                return AxResponseEntity.created(notice, "공지사항이 생성되었습니다.");
        }

        @PostMapping("/with-files")
        @Operation(summary = "파일과 함께 공지사항 등록", description = "파일과 함께 새로운 공지사항을 등록합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "공지사항 및 파일 등록 성공"),
                        @ApiResponse(responseCode = "400", description = "공지사항 등록 실패, 잘못된 데이터"),
                        @ApiResponse(responseCode = "409", description = "중복 데이터"),
                        @ApiResponse(responseCode = "413", description = "파일 크기 초과"),
                        @ApiResponse(responseCode = "415", description = "지원하지 않는 파일 형식")
        })
        public AxResponseEntity<NoticeManagementRes> createNoticeWithFiles(
                        @RequestParam("notice") String noticeJson,
                        @RequestParam(value = "files", required = false) MultipartFile[] files) {
                log.info("파일과 함께 공지사항 생성 요청 시작");
                log.info("공지사항 JSON: {}", noticeJson);
                log.info("파일 수: {}", files != null ? files.length : 0);

                // JSON 문자열을 NoticeManagementCreateReq 객체로 파싱
                NoticeManagementCreateReq createReq;
                try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        createReq = objectMapper.readValue(noticeJson, NoticeManagementCreateReq.class);
                        if (createReq == null) {
                                log.error("공지사항 JSON 파싱 실패: 파싱 결과가 null입니다.");
                                throw new RuntimeException("공지사항 데이터 파싱에 실패했습니다.");
                        }
                        log.info("공지사항 정보 파싱 성공: {}", createReq.toString());
                } catch (JsonProcessingException e) {
                        log.error("공지사항 JSON 파싱 실패: {}", e.getMessage(), e);
                        throw new RuntimeException("잘못된 공지사항 데이터 형식입니다: " + e.getMessage(), e);
                }

                if (files != null) {
                        for (int i = 0; i < files.length; i++) {
                                MultipartFile file = files[i];
                                if (file != null && !file.isEmpty()) {
                                        log.info("파일 {}: {} (크기: {} bytes, 타입: {})",
                                                        i + 1, file.getOriginalFilename(), file.getSize(),
                                                        file.getContentType());
                                }
                        }
                }

                NoticeManagementRes notice = noticeManagementService.createNoticeWithFiles(createReq, files);
                if (notice == null) {
                        log.error("파일과 함께 공지사항 생성 실패: 서비스에서 null을 반환했습니다.");
                        throw new RuntimeException("공지사항 생성에 실패했습니다.");
                }
                log.info("파일과 함께 공지사항 생성 성공: {}", notice.getNotiId());
                return AxResponseEntity.created(notice, "공지사항과 파일이 성공적으로 등록되었습니다.");
        }

        @PutMapping("/{id}")
        @Operation(summary = "공지사항 수정", description = "공지사항 내용을 수정합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "공지사항 수정 성공"),
                        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
                        @ApiResponse(responseCode = "404", description = "공지사항을 찾을 수 없음"),
                        @ApiResponse(responseCode = "409", description = "잘못된 데이터")
        })
        public AxResponseEntity<NoticeManagementRes> updateNotice(
                        @PathVariable @Parameter(description = "공지사항 ID", example = "1") Long id,
                        @Valid @RequestBody NoticeManagementUpdateReq updateReq) {
                if (updateReq == null) {
                        log.error("공지사항 수정 요청 실패: 요청 데이터가 null입니다.");
                        throw new RuntimeException("공지사항 수정 요청 데이터가 없습니다.");
                }
                log.info("공지사항 수정 요청: ID={}, 제목={}, 내용={}, 타입={}, 사용여부={}",
                                id, updateReq.getTitle(), updateReq.getMsg(), updateReq.getType(),
                                updateReq.getUseYn());

                NoticeManagementRes notice = noticeManagementService.updateNotice(id, updateReq);
                if (notice == null) {
                        log.error("공지사항 수정 실패: 서비스에서 null을 반환했습니다.");
                        throw new RuntimeException("공지사항 수정에 실패했습니다.");
                }

                log.info("공지사항 수정 성공: ID={}, 제목={}", notice.getNotiId(), notice.getTitle());
                return AxResponseEntity.ok(notice, "공지를 성공적으로 수정했습니다.");
        }

        @PutMapping("/{id}/with-files")
        @Operation(summary = "파일과 함께 공지사항 수정", description = "파일과 함께 공지사항 내용을 수정합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "공지사항 및 파일 수정 성공"),
                        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
                        @ApiResponse(responseCode = "404", description = "공지사항을 찾을 수 없음"),
                        @ApiResponse(responseCode = "409", description = "잘못된 데이터"),
                        @ApiResponse(responseCode = "413", description = "파일 크기 초과"),
                        @ApiResponse(responseCode = "415", description = "지원하지 않는 파일 형식")
        })
        public AxResponseEntity<NoticeManagementRes> updateNoticeWithFiles(
                        @PathVariable @Parameter(description = "공지사항 ID", example = "1") Long id,
                        @RequestParam(value = "notice", required = true) String noticeJson,
                        @RequestParam(value = "newFiles", required = false) MultipartFile[] newFiles,
                        @RequestParam(value = "deleteFileIds", required = false) String deleteFileIdsJson) {

                log.info("파일과 함께 공지사항 수정 요청 시작 - ID: {}", id);
                log.info("공지사항 JSON: {}", noticeJson);
                log.info("새 파일 수: {}", newFiles != null ? newFiles.length : 0);
                log.info("삭제 파일 ID JSON: {}", deleteFileIdsJson);

                // 모든 요청 파라미터 로깅
                log.info("=== 모든 요청 파라미터 확인 ===");
                log.info("id: {}", id);
                log.info("noticeJson: {}", noticeJson);
                log.info("deleteFileIdsJson: {}", deleteFileIdsJson);

                // JSON 문자열을 NoticeManagementUpdateReq 객체로 파싱
                NoticeManagementUpdateReq updateReq;
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                        updateReq = objectMapper.readValue(noticeJson, NoticeManagementUpdateReq.class);
                        if (updateReq == null) {
                                log.error("공지사항 JSON 파싱 실패: 파싱 결과가 null입니다.");
                                throw new RuntimeException("공지사항 데이터 파싱에 실패했습니다.");
                        }
                        log.info("공지사항 수정 정보 파싱 성공: {}", updateReq.toString());
                } catch (JsonProcessingException e) {
                        log.error("공지사항 JSON 파싱 실패: {}", e.getMessage(), e);
                        throw new RuntimeException("잘못된 공지사항 데이터 형식입니다: " + e.getMessage(), e);
                }

                // 삭제할 파일 ID 배열 파싱
                Long[] deleteFileIds = null;
                if (deleteFileIdsJson != null && !deleteFileIdsJson.trim().isEmpty()) {
                        try {
                                deleteFileIds = objectMapper.readValue(deleteFileIdsJson, Long[].class);
                                log.info("삭제할 파일 ID 파싱 성공: {}개", deleteFileIds.length);
                        } catch (JsonProcessingException e) {
                                log.error("삭제 파일 ID JSON 파싱 실패: {}", e.getMessage(), e);
                                throw new RuntimeException("잘못된 삭제 파일 ID 형식입니다: " + e.getMessage(), e);
                        }
                }

                // 새 파일 정보 로깅
                log.info("=== 파일 정보 상세 로깅 ===");
                log.info("newFiles 길이: {}", newFiles != null ? newFiles.length : "null");

                if (newFiles != null) {
                        for (int i = 0; i < newFiles.length; i++) {
                                MultipartFile file = newFiles[i];
                                log.info("파일 {}: {}", i + 1, file);
                                if (file != null) {
                                        log.info("  - 파일명: {}", file.getOriginalFilename());
                                        log.info("  - 크기: {} bytes", file.getSize());
                                        log.info("  - 타입: {}", file.getContentType());
                                        log.info("  - 비어있음: {}", file.isEmpty());
                                        log.info("  - 필드명: {}", file.getName());
                                } else {
                                        log.warn("  - 파일이 null입니다!");
                                }
                        }
                } else {
                        log.warn("newFiles가 null입니다!");
                }

                NoticeManagementRes notice = noticeManagementService.updateNoticeWithFiles(id, updateReq, newFiles,
                                deleteFileIds);
                if (notice == null) {
                        log.error("파일과 함께 공지사항 수정 실패: 서비스에서 null을 반환했습니다.");
                        throw new RuntimeException("공지사항 수정에 실패했습니다.");
                }
                log.info("파일과 함께 공지사항 수정 성공: ID={}", notice.getNotiId());
                return AxResponseEntity.ok(notice, "공지사항과 파일이 성공적으로 수정되었습니다.");
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "공지사항 삭제", description = "공지사항 내용을 삭제합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "공지사항 삭제 성공"),
                        @ApiResponse(responseCode = "404", description = "공지사항을 찾을 수 없음")
        })
        public AxResponseEntity<Void> deleteNotice(
                        @PathVariable @Parameter(description = "공지사항 ID", example = "1") Long id) {

                log.info("공지사항 삭제 요청: ID={}", id);

                noticeManagementService.deleteNotice(id);

                log.info("공지사항 삭제 성공: ID={}", id);

                return AxResponseEntity.deleted("공지사항이 성공적으로 삭제되었습니다.");
        }

        @GetMapping("/{noticeId}/files/{fileId}")
        @Operation(summary = "공지사항 첨부파일 다운로드", description = "공지사항에 첨부된 파일을 다운로드합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "파일 다운로드 성공"),
                        @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음"),
                        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                        @ApiResponse(responseCode = "500", description = "서버 오류")
        })
        public ResponseEntity<StreamingResponseBody> downloadFile(
                        @PathVariable @Parameter(description = "공지사항 ID", example = "1") Long noticeId,
                        @PathVariable @Parameter(description = "파일 ID", example = "1") Long fileId) {

                log.info("파일 다운로드 요청: 공지사항 ID={}, 파일 ID={}", noticeId, fileId);

                try {
                        // 파일 정보 조회
                        NoticeFile fileInfo = noticeManagementService.getNoticeFile(noticeId, fileId);
                        if (fileInfo == null) {
                                log.error("파일 정보를 찾을 수 없음: 공지사항 ID={}, 파일 ID={}", noticeId, fileId);
                                return ResponseEntity.notFound().build();
                        }

                        // 파일 경로 검증
                        String filePathStr = fileInfo.getFilePath();
                        if (filePathStr == null || filePathStr.trim().isEmpty()) {
                                log.error("파일 경로가 비어있음: 공지사항 ID={}, 파일 ID={}", noticeId, fileId);
                                return ResponseEntity.badRequest().build();
                        }

                        // 실제 파일 경로 확인
                        Path filePath = Paths.get(filePathStr);
                        if (!Files.exists(filePath)) {
                                log.error("파일이 존재하지 않음: {}", filePath);
                                return ResponseEntity.notFound().build();
                        }

                        // 파일 크기 조회
                        long fileSize = Files.size(filePath);

                        // HTTP 헤더 설정
                        HttpHeaders headers = new HttpHeaders();

                        // Content-Type 설정 (null 체크 및 유효성 검증 포함)
                        String contentType = fileInfo.getContentType();
                        if (contentType != null && !contentType.trim().isEmpty() && contentType.contains("/")) {
                                try {
                                        headers.setContentType(MediaType.parseMediaType(contentType));
                                } catch (IllegalArgumentException e) {
                                        log.warn("잘못된 MIME 타입, 기본 타입 사용: {} - {}", contentType, e.getMessage());
                                        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                                }
                        } else {
                                // 기본 Content-Type 설정
                                log.warn("유효하지 않은 MIME 타입, 기본 타입 사용: {}", contentType);
                                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                        }

                        // 파일명 인코딩 처리 (한글 파일명 지원)
                        String filename = fileInfo.getOriginalFilename();
                        if (filename != null) {
                            // UTF-8로 인코딩하여 Content-Disposition 헤더 설정
                            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8)
                                            .replaceAll("\\+", "%20");
                            headers.set(HttpHeaders.CONTENT_DISPOSITION,
                                            "attachment; filename*=UTF-8''" + encodedFilename);
                        }

                        // 파일 크기 설정
                        headers.setContentLength(fileSize);

                        // 캐시 방지 헤더
                        headers.setCacheControl("no-cache, no-store, must-revalidate");
                        headers.setPragma("no-cache");
                        headers.setExpires(0);

                        log.info("파일 다운로드 시작: {} (크기: {} bytes, 타입: {})",
                                        filename != null ? filename : "알 수 없음", fileSize,
                                        contentType != null ? contentType : "알 수 없음");

                        // StreamingResponseBody를 사용하여 파일을 청크 단위로 스트리밍 (OOM 방지)
                        StreamingResponseBody responseBody = (OutputStream outputStream) -> {
                                try (InputStream inputStream = Files.newInputStream(filePath)) {
                                        byte[] buffer = new byte[8192];
                                        int bytesRead;
                                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                                                outputStream.write(buffer, 0, bytesRead);
                                                outputStream.flush();
                                        }
                                } catch (IOException e) {
                                        log.error("파일 스트리밍 중 오류 발생: {}", e.getMessage(), e);
                                        throw e;
                                }
                        };

                        return ResponseEntity.ok()
                                        .headers(headers)
                                        .body(responseBody);

                } catch (NoSuchFileException e) {
                        log.error("파일을 찾을 수 없음: 공지사항 ID={}, 파일 ID={}, 경로: {}",
                                        noticeId, fileId, e.getMessage(), e);
                        return ResponseEntity.notFound().build();
                } catch (IOException e) {
                        log.error("파일 읽기 실패: 공지사항 ID={}, 파일 ID={}, 오류: {}",
                                        noticeId, fileId, e.getMessage(), e);
                        return ResponseEntity.internalServerError().build();
                } catch (RuntimeException e) {
                        log.error("파일 다운로드 실패: 공지사항 ID={}, 파일 ID={}, 오류: {}",
                                        noticeId, fileId, e.getMessage(), e);
                        return ResponseEntity.internalServerError().build();
                }
        }

}
