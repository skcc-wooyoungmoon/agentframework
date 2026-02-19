package com.skax.aiplatform.service.admin.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.util.MultipartFileHeaderChecker;
import com.skax.aiplatform.dto.admin.request.NoticeManagementCreateReq;
import com.skax.aiplatform.dto.admin.request.NoticeManagementSearchReq;
import com.skax.aiplatform.dto.admin.request.NoticeManagementUpdateReq;
import com.skax.aiplatform.dto.admin.response.NoticeFileRes;
import com.skax.aiplatform.dto.admin.response.NoticeManagementRes;
import com.skax.aiplatform.entity.GpoUsersMas;
import com.skax.aiplatform.entity.NoticeFile;
import com.skax.aiplatform.entity.NoticeManagement;
import com.skax.aiplatform.mapper.admin.NoticeManagementMapper;
import com.skax.aiplatform.repository.admin.NoticeFileRepository;
import com.skax.aiplatform.repository.admin.NoticeRepository;
import com.skax.aiplatform.repository.auth.GpoUsersMasRepository;
import com.skax.aiplatform.service.admin.NoticeManagementService;
import com.skax.aiplatform.service.auth.UserContextService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeManagementServiceImpl implements NoticeManagementService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf", "png", "jpg", "jpeg", "gif", "xlsx", "xls", "doc", "docx", "ppt", "pptx", "zip", "txt");

    private final NoticeRepository noticeRepository;
    private final NoticeFileRepository noticeFileRepository;
    private final NoticeManagementMapper noticeManagementMapper;
    private final UserContextService userContextService;
    private final GpoUsersMasRepository gpoUsersMasRepository; // 담당자 조회용
    private final String FILE_UPLOAD_PATH = "shbdat/notice";

    // 등록
    @Transactional
    public NoticeManagementRes createNotice(NoticeManagementCreateReq req) {
        if (req == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "공지사항 생성 요청 정보가 없습니다.");
        }

        String currentUsername = userContextService.getAuthUsername();
        log.info("공지사항 생성 시작 - 제목: {}, 타입: {}, expFrom: {}, expTo: {}, 생성자: {}",
                req.getTitle(), req.getType(), req.getExpFrom(), req.getExpTo(), currentUsername);

        // 게시 기간 설정 (사용자 입력값이 있으면 사용, 없으면 기본값)
        LocalDateTime expFrom = LocalDateTime.now(); // 기본: 현재
        LocalDateTime expTo = LocalDateTime.now().plusMonths(1); // 기본: 한 달 후

        if (req.getExpFrom() != null && !req.getExpFrom().trim().isEmpty()) {
            try {
                expFrom = parseDateTime(req.getExpFrom().trim());
                log.info("게시 시작일 파싱 성공: 입력값='{}', 파싱결과={}", req.getExpFrom(), expFrom);
            } catch (DateTimeParseException e) {
                // 날짜 파싱 실패 시 기본값 사용 (의도된 동작)
                log.warn("게시 시작일 파싱 실패, 기본값 사용: {} - {}", req.getExpFrom(), e.getMessage());
            }
        }

        if (req.getExpTo() != null && !req.getExpTo().trim().isEmpty()) {
            try {
                expTo = parseDateTime(req.getExpTo().trim());
                log.info("게시 종료일 파싱 성공: 입력값='{}', 파싱결과={}", req.getExpTo(), expTo);
            } catch (DateTimeParseException e) {
                // 날짜 파싱 실패 시 기본값 사용 (의도된 동작)
                log.warn("게시 종료일 파싱 실패, 기본값 사용: {} - {}", req.getExpTo(), e.getMessage());
            }
        }

        NoticeManagement.NoticeManagementBuilder builder = NoticeManagement.builder()
                .title(req.getTitle())
                .useYn(NoticeManagement.convertStringToNumber(req.getUseYn() != null ? req.getUseYn() : "Y"))
                .type(req.getType() != null ? req.getType() : "GENERAL")
                .expFrom(expFrom)
                .expTo(expTo)
                .createBy(currentUsername != null ? currentUsername : "SYSTEM")
                .updateBy(currentUsername != null ? currentUsername : "SYSTEM");

        // builder가 null이 아님을 명시적으로 보장
        NoticeManagement.NoticeManagementBuilder nonNullBuilder = java.util.Objects.requireNonNull(
                builder, "공지사항 빌더 생성에 실패했습니다.");

        applyMessageDetails(nonNullBuilder, req.getMsg());

        NoticeManagement notice = java.util.Objects.requireNonNull(
                nonNullBuilder.build(), "공지사항 객체 생성에 실패했습니다.");

        NoticeManagement savedNotice = noticeRepository.save(notice);
        if (savedNotice == null) {
            log.error("공지사항 저장 실패: 저장된 공지사항이 null입니다.");
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "공지사항 저장에 실패했습니다.");
        }

        log.info("공지사항 생성 완료 - ID: {}, 게시기간: {} ~ {}, 생성자: {}",
                savedNotice.getNotiId(), savedNotice.getExpFrom(), savedNotice.getExpTo(), savedNotice.getCreateBy());

        return mapToResponseOrThrow(savedNotice);
    }

    // 파일과 함께 등록
    @Transactional
    public NoticeManagementRes createNoticeWithFiles(NoticeManagementCreateReq req, MultipartFile[] files) {
        if (req == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "공지사항 생성 요청 정보가 없습니다.");
        }

        String currentUsername = userContextService.getAuthUsername();
        log.info("파일과 함께 공지사항 생성 시작 - 제목: {}, 타입: {}, 파일 수: {}, expFrom: {}, expTo: {}, 생성자: {}",
                req.getTitle(), req.getType(), files != null ? files.length : 0,
                req.getExpFrom(), req.getExpTo(), currentUsername);

        // 게시 기간 설정 (사용자 입력값이 있으면 사용, 없으면 기본값)
        LocalDateTime expFrom = LocalDateTime.now(); // 기본: 현재
        LocalDateTime expTo = LocalDateTime.now().plusMonths(1); // 기본: 한 달 후

        if (req.getExpFrom() != null && !req.getExpFrom().trim().isEmpty()) {
            try {
                expFrom = parseDateTime(req.getExpFrom().trim());
                log.info("게시 시작일 파싱 성공: 입력값='{}', 파싱결과={}", req.getExpFrom(), expFrom);
            } catch (DateTimeParseException e) {
                // 날짜 파싱 실패 시 기본값 사용 (의도된 동작)
                log.warn("게시 시작일 파싱 실패, 기본값 사용: {} - {}", req.getExpFrom(), e.getMessage());
            }
        }

        if (req.getExpTo() != null && !req.getExpTo().trim().isEmpty()) {
            try {
                expTo = parseDateTime(req.getExpTo().trim());
                log.info("게시 종료일 파싱 성공: 입력값='{}', 파싱결과={}", req.getExpTo(), expTo);
            } catch (DateTimeParseException e) {
                // 날짜 파싱 실패 시 기본값 사용 (의도된 동작)
                log.warn("게시 종료일 파싱 실패, 기본값 사용: {} - {}", req.getExpTo(), e.getMessage());
            }
        }

        // 공지사항 먼저 생성
        NoticeManagement.NoticeManagementBuilder builder = NoticeManagement.builder()
                .title(req.getTitle())
                .useYn(NoticeManagement.convertStringToNumber(req.getUseYn() != null ? req.getUseYn() : "Y"))
                .type(req.getType() != null ? req.getType() : "GENERAL")
                .expFrom(expFrom)
                .expTo(expTo)
                .createBy(currentUsername != null ? currentUsername : "SYSTEM")
                .updateBy(currentUsername != null ? currentUsername : "SYSTEM");

        // builder가 null이 아님을 명시적으로 보장
        NoticeManagement.NoticeManagementBuilder nonNullBuilder = java.util.Objects.requireNonNull(
                builder, "공지사항 빌더 생성에 실패했습니다.");

        applyMessageDetails(nonNullBuilder, req.getMsg());

        NoticeManagement notice = java.util.Objects.requireNonNull(
                nonNullBuilder.build(), "공지사항 객체 생성에 실패했습니다.");

        NoticeManagement savedNotice = noticeRepository.save(notice);
        if (savedNotice == null) {
            log.error("공지사항 저장 실패: 저장된 공지사항이 null입니다.");
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "공지사항 저장에 실패했습니다.");
        }

        log.info("공지사항 생성 완료 - ID: {}, 게시기간: {} ~ {}, 생성자: {}",
                savedNotice.getNotiId(), savedNotice.getExpFrom(), savedNotice.getExpTo(), savedNotice.getCreateBy());

        // 파일 업로드 처리 (BLOB 형태로 DB 저장)
        if (files != null && files.length > 0) {
            Long noticeId = savedNotice.getNotiId();
            if (noticeId == null) {
                log.error("공지사항 ID가 null입니다. 파일 저장을 건너뜁니다.");
            } else {
                saveFilesToDatabase(noticeId, files);
                log.info("파일 DB 저장 완료 - 공지사항 ID: {}, 파일 수: {}", noticeId, files.length);
            }
        }

        return mapToResponseOrThrow(savedNotice);
    }

    // 전체 조회
    public Page<NoticeManagementRes> getNotices(Pageable pageable, NoticeManagementSearchReq searchReq) {
        log.info("공지사항 조회 요청 - 페이지: {}, 크기: {}, 검색조건: {}",
                pageable.getPageNumber(), pageable.getPageSize(), searchReq);

        Pageable validatedPageable = validateAndFixPageable(pageable);
        Page<NoticeManagement> noticePage;

        // 검색 조건이 있는 경우
        if (searchReq != null && hasSearchConditions(searchReq)) {
            // "전체" 값을 빈 문자열로 변환
            normalizeSearchRequest(searchReq);

            // 날짜 범위 설정
            LocalDateTime fromDt = LocalDateTime.now().minusYears(1);
            LocalDateTime toDtPlus = LocalDateTime.now().plusYears(1);

            if (searchReq.getStartDate() != null && !searchReq.getStartDate().isEmpty()) {
                try {
                    fromDt = LocalDateTime.parse(searchReq.getStartDate() + "T00:00:00");
                } catch (DateTimeParseException e) {
                    // 날짜 파싱 실패 시 기본값 사용 (의도된 동작)
                    log.warn("시작일자 파싱 실패: {} - {}", searchReq.getStartDate(), e.getMessage());
                }
            }

            if (searchReq.getEndDate() != null && !searchReq.getEndDate().isEmpty()) {
                try {
                    toDtPlus = LocalDateTime.parse(searchReq.getEndDate() + "T23:59:59");
                } catch (DateTimeParseException e) {
                    // 날짜 파싱 실패 시 기본값 사용 (의도된 동작)
                    log.warn("종료일자 파싱 실패: {} - {}", searchReq.getEndDate(), e.getMessage());
                }
            }

            log.info(
                    "검색 조건 적용 (정규화 후) - dateType: {}, searchType: {}, searchKeyword: {}, type: {}, status: {}, fromDt: {}, toDtPlus: {}",
                    searchReq.getDateType(), searchReq.getSearchType(), searchReq.getSearchKeyword(),
                    searchReq.getType(), searchReq.getStatus(), fromDt, toDtPlus);

            noticePage = noticeRepository.findNoticesByAdminFilters(
                    searchReq.getDateType(),
                    searchReq.getSearchType(),
                    searchReq.getSearchKeyword(),
                    fromDt,
                    toDtPlus,
                    searchReq.getType(),
                    searchReq.getStatus(),
                    validatedPageable);
        } else {
            // 검색 조건이 없는 경우 전체 조회
            noticePage = noticeRepository.findAll(validatedPageable);
        }

        // 각 공지사항에 파일 정보 추가
        return noticePage.map(notice -> {
            NoticeManagementRes response = mapToResponseOrThrow(notice);
            response.setMsg(buildFullMessage(notice));

            // 파일 정보 조회
            List<NoticeFile> files = noticeFileRepository.findByNoticeIdAndUseYnOrderByUploadDateDesc(
                    notice.getNotiId(), 1); // Y -> 1로 변경

            // 파일 정보 추가
            if (files != null && !files.isEmpty()) {
                List<NoticeFileRes> fileResponses = files.stream()
                        .map(NoticeFileRes::fromEntity)
                        .toList();
                response.setFiles(fileResponses);
            }

            return response;
        });
    }

    /**
     * 검색 조건이 있는지 확인
     */
    private boolean hasSearchConditions(NoticeManagementSearchReq searchReq) {
        return (searchReq.getDateType() != null && !searchReq.getDateType().isEmpty()) ||
                (searchReq.getStartDate() != null && !searchReq.getStartDate().isEmpty()) ||
                (searchReq.getEndDate() != null && !searchReq.getEndDate().isEmpty()) ||
                (searchReq.getSearchType() != null && !searchReq.getSearchType().isEmpty()) ||
                (searchReq.getSearchKeyword() != null && !searchReq.getSearchKeyword().isEmpty()) ||
                (searchReq.getType() != null && !searchReq.getType().isEmpty()) ||
                (searchReq.getStatus() != null && !searchReq.getStatus().isEmpty());
    }

    /**
     * 검색 요청의 "전체" 값을 빈 문자열로 정규화
     */
    private void normalizeSearchRequest(NoticeManagementSearchReq searchReq) {
        if ("전체".equals(searchReq.getSearchType())) {
            searchReq.setSearchType("");
            log.debug("searchType '전체'를 빈 문자열로 변환");
        }
        if ("전체".equals(searchReq.getType())) {
            searchReq.setType("");
            log.debug("type '전체'를 빈 문자열로 변환");
        }
        if ("전체".equals(searchReq.getStatus())) {
            searchReq.setStatus("");
            log.debug("status '전체'를 빈 문자열로 변환");
        }
        if ("전체".equals(searchReq.getDateType())) {
            searchReq.setDateType("");
            log.debug("dateType '전체'를 빈 문자열로 변환");
        }
    }

    // 단건 조회
    public NoticeManagementRes getNotice(Long id) {
        NoticeManagement noticebyId = noticeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "공지사항을 찾을 수 없습니다. ID: " + id));

        // 디버깅: 엔티티에서 title 값 확인
        log.info("공지사항 조회 - ID: {}, Title: '{}', Msg: '{}', Type: '{}'",
                id, noticebyId.getTitle(), noticebyId.getMsg(), noticebyId.getType());

        // 파일 정보 조회
        List<NoticeFile> files = noticeFileRepository.findByNoticeIdAndUseYnOrderByUploadDateDesc(id, 1); // Y -> 1로 변경

        // 응답 DTO 생성
        NoticeManagementRes response = mapToResponseOrThrow(noticebyId);
        response.setMsg(buildFullMessage(noticebyId));

        // 디버깅: 매핑 후 DTO에서 title 값 확인
        log.info("매핑 후 DTO - Title: '{}', Msg: '{}', Type: '{}'",
                response.getTitle(), response.getMsg(), response.getType());

        // 파일 정보 추가
        if (files != null && !files.isEmpty()) {
            List<NoticeFileRes> fileResponses = files.stream()
                    .map(NoticeFileRes::fromEntity)
                    .toList();
            response.setFiles(fileResponses);
        }

        // 담당자 정보 조회 및 설정
        setUserInfo(response);

        return response;
    }

    /**
     * 담당자 정보 조회 및 설정
     */
    private void setUserInfo(NoticeManagementRes response) {
        // 생성자 사용자 정보 조회 및 설정
        String createdBy = response.getCreateBy();
        String updatedBy = response.getUpdateBy();
        if (createdBy != null && !createdBy.isBlank()) {
            try {
                GpoUsersMas createdByUser = gpoUsersMasRepository.findByMemberId(createdBy).orElse(null);
                if (createdByUser != null) {
                    response.setCreatedByName(createdByUser.getJkwNm());
                    response.setCreatedByDepts(createdByUser.getDeptNm());
                    response.setCreatedByPos(createdByUser.getJkgpNm());
                    log.debug("생성자 사용자 정보 설정 완료 - name: {}, dept: {}, pos: {}",
                            createdByUser.getJkwNm(), createdByUser.getDeptNm(), createdByUser.getJkgpNm());
                } else {
                    log.warn("생성자 사용자 정보를 찾을 수 없습니다 - uuid: {}", createdBy);
                }
            } catch (RuntimeException e) {
                // 사용자 정보 조회 실패 시 로그만 남기고 계속 진행 (부가 정보이므로 필수 아님)
                log.warn("생성자 사용자 정보 조회 실패 - uuid: {}, error: {}", createdBy, e.getMessage(), e);
            }
        }

        // 수정자 사용자 정보 조회 및 설정
        if (updatedBy != null && !updatedBy.isBlank()) {
            try {
                GpoUsersMas updatedByUser = gpoUsersMasRepository.findByMemberId(updatedBy).orElse(null);
                if (updatedByUser != null) {
                    response.setUpdatedByName(updatedByUser.getJkwNm());
                    response.setUpdatedByDepts(updatedByUser.getDeptNm());
                    response.setUpdatedByPos(updatedByUser.getJkgpNm());
                    log.debug("수정자 사용자 정보 설정 완료 - name: {}, dept: {}, pos: {}",
                            updatedByUser.getJkwNm(), updatedByUser.getDeptNm(), updatedByUser.getJkgpNm());
                } else {
                    log.warn("수정자 사용자 정보를 찾을 수 없습니다 - uuid: {}", updatedBy);
                }
            } catch (RuntimeException e) {
                // 사용자 정보 조회 실패 시 로그만 남기고 계속 진행 (부가 정보이므로 필수 아님)
                log.warn("수정자 사용자 정보 조회 실패 - uuid: {}, error: {}", updatedBy, e.getMessage(), e);
            }
        }
    }

    private void applyMessageDetails(NoticeManagement.NoticeManagementBuilder builder, String message) {
        if (builder == null) {
            return;
        }
        String[] parts = splitMessageIntoChunks(message, 1000, 4);

        // msg 필드는 저장하지 않음 (detail 필드만 저장)
        builder.firstDetail(parts[0]);
        builder.secondDetail(parts[1]);
        builder.thirdDetail(parts[2]);
        builder.fourthDetail(parts[3]);
    }

    private void applyMessageDetails(NoticeManagement notice, String message) {
        if (notice == null) {
            return;
        }
        String[] parts = splitMessageIntoChunks(message, 1000, 4);

        // msg 필드는 저장하지 않음 (detail 필드만 저장)
        notice.setFirstDetail(parts[0]);
        notice.setSecondDetail(parts[1]);
        notice.setThirdDetail(parts[2]);
        notice.setFourthDetail(parts[3]);
    }

    private String[] splitMessageIntoChunks(String message, int chunkSize, int chunkCount) {
        String[] result = new String[chunkCount];
        if (message == null || message.isEmpty() || chunkSize <= 0 || chunkCount <= 0) {
            return result;
        }

        int length = message.length();
        for (int i = 0; i < chunkCount; i++) {
            int start = i * chunkSize;
            if (start >= length) {
                break;
            }
            int end = Math.min(start + chunkSize, length);
            String part = message.substring(start, end);
            result[i] = part.isEmpty() ? null : part;
        }
        return result;
    }

    private String buildFullMessage(NoticeManagement notice) {
        if (notice == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        appendSegment(builder, notice.getFirstDetail());
        appendSegment(builder, notice.getSecondDetail());
        appendSegment(builder, notice.getThirdDetail());
        appendSegment(builder, notice.getFourthDetail());

        if (builder.length() > 0) {
            return builder.toString();
        }
        return notice.getMsg();
    }

    private void appendSegment(StringBuilder builder, String segment) {
        if (builder == null || segment == null || segment.isEmpty()) {
            return;
        }
        builder.append(segment);
    }

    // 공지사항 첨부파일 조회
    public NoticeFile getNoticeFile(Long noticeId, Long fileId) {
        NoticeFile file = noticeFileRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "파일을 찾을 수 없습니다. 파일 ID: " + fileId));

        // 공지사항 ID 검증
        Long fileNoticeId = file.getNoticeId();
        if (fileNoticeId == null || !fileNoticeId.equals(noticeId)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE,
                    "잘못된 공지사항 ID입니다. 공지사항 ID: " + noticeId + ", 파일의 공지사항 ID: " + fileNoticeId);
        }

        return file;
    }

    // 수정
    @Transactional
    public NoticeManagementRes updateNotice(Long id, NoticeManagementUpdateReq req) {
        String currentUsername = userContextService.getAuthUsername();
        log.info("공지사항 수정 시작 - ID: {}, 제목: {}, 수정자: {}", id, req.getTitle(), currentUsername);

        NoticeManagement notice = noticeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "공지사항을 찾을 수 없습니다. ID: " + id));

        // notice가 null이 아님을 명시적으로 보장
        NoticeManagement nonNullNotice = java.util.Objects.requireNonNull(notice, "공지사항 객체가 null입니다.");

        // 수정 전 상태 로깅
        log.debug("수정 전 - updateAt: {}, updateBy: {}", nonNullNotice.getUpdateAt(), nonNullNotice.getUpdateBy());

        // 모든 필드 업데이트
        nonNullNotice.setTitle(req.getTitle());
        applyMessageDetails(nonNullNotice, req.getMsg());
        nonNullNotice.setUpdateBy(currentUsername != null ? currentUsername : "SYSTEM");

        String type = req.getType();
        if (type != null) {
            nonNullNotice.setType(type);
        }

        String useYn = req.getUseYn();
        if (useYn != null) {
            nonNullNotice.setUseYn(NoticeManagement.convertStringToNumber(useYn));
        }

        if (req.getExpFrom() != null && !req.getExpFrom().trim().isEmpty()) {
            String expFromStr = req.getExpFrom();
            try {
                LocalDateTime expFrom = parseDateTime(expFromStr.trim());
                nonNullNotice.setExpFrom(expFrom);
                log.debug("만료 시작일 설정 성공: {}", expFrom);
            } catch (DateTimeParseException e) {
                log.error("만료 시작일 파싱 실패: {}, 오류: {}", expFromStr, e.getMessage());
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE,
                        "Invalid expiration start date format. Please use 'yyyy-MM-dd HH:mm:ss' or 'yyyy-MM-ddTHH:mm:ss' format.");
            }
        }

        if (req.getExpTo() != null && !req.getExpTo().trim().isEmpty()) {
            String expToStr = req.getExpTo();
            try {
                LocalDateTime expTo = parseDateTime(expToStr.trim());
                nonNullNotice.setExpTo(expTo);
                log.debug("만료 종료일 설정 성공: {}", expTo);
            } catch (DateTimeParseException e) {
                log.error("만료 종료일 파싱 실패: {}, 오류: {}", expToStr, e.getMessage());
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE,
                        "Invalid expiration end date format. Please use 'yyyy-MM-dd HH:mm:ss' or 'yyyy-MM-ddTHH:mm:ss' format.");
            }
        }

        // 만료일 유효성 검증
        LocalDateTime expFrom = nonNullNotice.getExpFrom();
        LocalDateTime expTo = nonNullNotice.getExpTo();
        if (expFrom != null && expTo != null && expFrom.isAfter(expTo)) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE,
                        "Expiration start date must be earlier than expiration end date.");
        }

        NoticeManagement updatedNotice = noticeRepository.save(nonNullNotice);

        // 수정 후 상태 로깅 (updateAt이 @PreUpdate로 자동 업데이트되는지 확인)
        log.info("공지사항 수정 완료 - ID: {}, updateAt: {}, updateBy: {}",
                updatedNotice.getNotiId(), updatedNotice.getUpdateAt(), updatedNotice.getUpdateBy());

        return mapToResponseOrThrow(updatedNotice);
    }

    // 파일과 함께 수정
    @Transactional
    public NoticeManagementRes updateNoticeWithFiles(Long id, NoticeManagementUpdateReq req, MultipartFile[] newFiles,
            Long[] deleteFileIds) {
        String currentUsername = userContextService.getAuthUsername();
        log.info("파일과 함께 공지사항 수정 시작 - ID: {}, 제목: {}, 새 파일 수: {}, 삭제 파일 수: {}, 수정자: {}",
                id, req.getTitle(), newFiles != null ? newFiles.length : 0,
                deleteFileIds != null ? deleteFileIds.length : 0, currentUsername);

        // 기존 공지사항 조회
        NoticeManagement notice = noticeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "공지사항을 찾을 수 없습니다. ID: " + id));

        // notice가 null이 아님을 명시적으로 보장
        NoticeManagement nonNullNotice = java.util.Objects.requireNonNull(notice, "공지사항 객체가 null입니다.");

        // 공지사항 기본 정보 수정
        nonNullNotice.setTitle(req.getTitle());
        applyMessageDetails(nonNullNotice, req.getMsg());
        nonNullNotice.setUpdateBy(currentUsername != null ? currentUsername : "SYSTEM");

        String type = req.getType();
        if (type != null) {
            nonNullNotice.setType(type);
        }

        String useYn = req.getUseYn();
        if (useYn != null) {
            nonNullNotice.setUseYn(NoticeManagement.convertStringToNumber(useYn));
        }

        String expFromStr = req.getExpFrom();
        if (expFromStr != null && !expFromStr.trim().isEmpty()) {
            try {
                LocalDateTime expFrom = parseDateTime(expFromStr.trim());
                nonNullNotice.setExpFrom(expFrom);
                log.debug("만료 시작일 설정 성공: {}", expFrom);
            } catch (DateTimeParseException e) {
                log.error("만료 시작일 파싱 실패: {}, 오류: {}", expFromStr, e.getMessage());
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE,
                        "Invalid expiration start date format. Please use 'yyyy-MM-dd HH:mm:ss' or 'yyyy-MM-ddTHH:mm:ss' format.");
            }
        }

        String expToStr = req.getExpTo();
        if (expToStr != null && !expToStr.trim().isEmpty()) {
            try {
                LocalDateTime expTo = parseDateTime(expToStr.trim());
                nonNullNotice.setExpTo(expTo);
                log.debug("만료 종료일 설정 성공: {}", expTo);
            } catch (DateTimeParseException e) {
                log.error("만료 종료일 파싱 실패: {}, 오류: {}", expToStr, e.getMessage());
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE,
                        "Invalid expiration end date format. Please use 'yyyy-MM-dd HH:mm:ss' or 'yyyy-MM-ddTHH:mm:ss' format.");
            }
        }

        // 만료일 유효성 검증
        LocalDateTime expFrom = nonNullNotice.getExpFrom();
        LocalDateTime expTo = nonNullNotice.getExpTo();
        if (expFrom != null && expTo != null && expFrom.isAfter(expTo)) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE,
                        "Expiration start date must be earlier than expiration end date.");
        }

        // 공지사항 저장
        NoticeManagement updatedNotice = noticeRepository.save(nonNullNotice);
        log.info("공지사항 기본 정보 수정 완료 - ID: {}, updateAt: {}, updateBy: {}",
                updatedNotice.getNotiId(), updatedNotice.getUpdateAt(), updatedNotice.getUpdateBy());

        // 기존 파일 삭제 처리 (명시적으로 삭제 요청된 파일만 삭제)
        if (deleteFileIds != null && deleteFileIds.length > 0) {
            deleteFilesFromDatabase(deleteFileIds);
            log.info("공지사항 수정 - {}개 파일 삭제 완료", deleteFileIds.length);
        }

        // 새 파일 업로드 처리
        if (newFiles != null && newFiles.length > 0) {
            saveFilesToDatabase(updatedNotice.getNotiId(), newFiles);
            log.info("공지사항 수정 - {}개 새 파일 업로드 완료", newFiles.length);
        }

        // 수정된 공지사항 조회 (파일 정보 포함)
        NoticeManagementRes response = getNotice(updatedNotice.getNotiId());
        log.info("파일과 함께 공지사항 수정 완료 - ID: {}", updatedNotice.getNotiId());

        return response;
    }

    // 삭제
    @Transactional
    public void deleteNotice(Long id) {
        log.info("공지사항 삭제 시작 - ID: {}", id);

        // 공지사항 존재 여부 확인
        if (!noticeRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "공지사항을 찾을 수 없습니다. ID: " + id);
        }

        // 공지사항에 연결된 모든 파일 조회
        List<NoticeFile> files = noticeFileRepository.findByNoticeIdOrderByUploadDateDesc(id);
        log.info("공지사항 삭제 - 연결된 파일 수: {}", files != null ? files.size() : 0);

        // 파일 삭제 처리
        Path noticeDir = null;
        if (files != null && !files.isEmpty()) {
            for (NoticeFile file : files) {
                if (file == null) {
                    continue;
                }

                Long fileId = file.getFileId();
                if (fileId == null) {
                    continue;
                }

                // 실제 파일 삭제
                String filePathStr = file.getFilePath();
                if (filePathStr != null && !filePathStr.trim().isEmpty()) {
                    try {
                        Path filePath = Paths.get(filePathStr);
                        if (Files.exists(filePath)) {
                            Files.delete(filePath);
                            log.info("공지사항 삭제 - 실제 파일 삭제 완료: {}", filePath);
                            
                            // 폴더 경로 저장 (나중에 폴더 삭제용)
                            if (noticeDir == null) {
                                noticeDir = filePath.getParent();
                            }
                        } else {
                            log.warn("공지사항 삭제 - 삭제할 파일이 존재하지 않음: {}", filePath);
                            // 파일이 없어도 폴더 경로는 저장
                            if (noticeDir == null) {
                                noticeDir = filePath.getParent();
                            }
                        }
                    } catch (IOException e) {
                        // 실제 파일 삭제 실패 시 로그만 남기고 계속 진행
                        log.warn("공지사항 삭제 - 실제 파일 삭제 실패: {} - {}", filePathStr, e.getMessage());
                    }
                } else {
                    log.warn("공지사항 삭제 - 파일 경로가 없어 실제 파일 삭제를 건너뜁니다 - 파일 ID: {}", fileId);
                }

                // DB에서 파일 정보 삭제 (물리 삭제)
                try {
                    noticeFileRepository.deleteById(fileId);
                    log.debug("공지사항 삭제 - 파일 DB 삭제 완료: {} (ID: {})", file.getOriginalFilename(), fileId);
                } catch (RuntimeException e) {
                    log.warn("공지사항 삭제 - 파일 DB 삭제 실패: {} - {}", fileId, e.getMessage());
                }
            }
            log.info("공지사항 삭제 - {}개 파일 삭제 처리 완료", files.size());
        }

        // 폴더 삭제 (파일이 모두 삭제된 경우)
        // 공지사항 ID별 폴더만 삭제 (shbdat/notice는 유지)
        if (noticeDir != null && Files.exists(noticeDir)) {
            try {
                // 공지사항 ID별 폴더인지 확인 (shbdat/notice/{id} 형태)
                Path basePath = Paths.get(FILE_UPLOAD_PATH);
                if (noticeDir.startsWith(basePath) && !noticeDir.equals(basePath)) {
                    // 폴더가 비어있는지 확인
                    boolean isEmpty = false;
                    try {
                        isEmpty = Files.list(noticeDir).findAny().isEmpty();
                    } catch (IOException e) {
                        log.warn("공지사항 삭제 - 폴더 내용 확인 실패: {} - {}", noticeDir, e.getMessage());
                    }

                    if (isEmpty) {
                        Files.delete(noticeDir);
                        log.info("공지사항 삭제 - 빈 폴더 삭제 완료: {} (공지사항 ID: {})", noticeDir, id);
                    } else {
                        log.debug("공지사항 삭제 - 폴더에 파일이 남아있어 삭제하지 않음: {}", noticeDir);
                    }
                } else {
                    log.warn("공지사항 삭제 - 기본 폴더({})는 삭제하지 않음: {}", basePath, noticeDir);
                }
            } catch (IOException e) {
                log.warn("공지사항 삭제 - 폴더 삭제 실패: {} - {}", noticeDir, e.getMessage());
            }
        } else if (noticeDir == null) {
            // 파일이 없었지만 공지사항 ID별 폴더가 존재할 수 있으므로 확인
            Path noticeIdDir = Paths.get(FILE_UPLOAD_PATH, String.valueOf(id));
            if (Files.exists(noticeIdDir)) {
                try {
                    boolean isEmpty = Files.list(noticeIdDir).findAny().isEmpty();
                    if (isEmpty) {
                        Files.delete(noticeIdDir);
                        log.info("공지사항 삭제 - 빈 폴더 삭제 완료: {} (공지사항 ID: {})", noticeIdDir, id);
                    }
                } catch (IOException e) {
                    log.warn("공지사항 삭제 - 폴더 삭제 실패: {} - {}", noticeIdDir, e.getMessage());
                }
            }
        }

        // 공지사항 삭제
        noticeRepository.deleteById(id);
        log.info("공지사항 삭제 완료 - ID: {}", id);
    }

    /**
     * Pageable 객체의 정렬 파라미터를 검증하고 안전한 정렬을 적용합니다.
     * 
     * @param pageable 원본 Pageable 객체
     * @return 검증된 Pageable 객체
     */
    private Pageable validateAndFixPageable(Pageable pageable) {
        // 허용되는 정렬 필드 목록 (공지사항 엔티티 필드)
        final String[] ALLOWED_SORT_FIELDS = {
                "notiId", "title", "msg", "type", "useYn",
                "expFrom", "expTo", "createdAt", "updatedAt", "createdBy", "updatedBy"
        };

        Sort validatedSort = Sort.by("notiId").descending(); // 기본 정렬

        if (pageable.getSort().isSorted()) {
            List<Sort.Order> validOrders = new ArrayList<>();

            for (Sort.Order order : pageable.getSort()) {
                String property = order.getProperty();

                // 허용된 필드인지 확인
                if (Arrays.asList(ALLOWED_SORT_FIELDS).contains(property)) {
                    validOrders.add(order);
                    log.debug("유효한 정렬 필드 적용: {} {}", property, order.getDirection());
                } else {
                    log.warn("허용되지 않은 정렬 필드 무시: {}", property);
                }
            }

            if (!validOrders.isEmpty()) {
                validatedSort = Sort.by(validOrders);
            }
        }

        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                validatedSort);
    }

    /**
     * 문자열을 LocalDateTime으로 파싱합니다.
     * 여러 날짜 형식을 지원합니다.
     * 
     * @param dateTimeStr 날짜시간 문자열
     * @return LocalDateTime 객체
     * @throws DateTimeParseException 파싱 실패 시
     */
    private LocalDateTime parseDateTime(String dateTimeStr) throws DateTimeParseException {
        // 1. yyyy-MM-dd HH:mm:ss 형식 시도
        try {
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException e) {
            log.debug("날짜 형식 파싱 실패 (yyyy-MM-dd HH:mm:ss): {} - 다음 형식 시도", dateTimeStr);
        }

        // 2. yyyy-MM-dd HH:mm 형식 시도
        try {
            return LocalDateTime.parse(dateTimeStr + ":00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException e) {
            log.debug("날짜 형식 파싱 실패 (yyyy-MM-dd HH:mm): {} - 다음 형식 시도", dateTimeStr);
        }

        // 3. yyyy-MM-dd 형식 시도
        try {
            return LocalDateTime.parse(dateTimeStr + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (DateTimeParseException e) {
            log.debug("날짜 형식 파싱 실패 (yyyy-MM-dd): {} - 다음 형식 시도", dateTimeStr);
        }

        // 4. ISO_LOCAL_DATE_TIME 형식 시도
        try {
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            log.debug("날짜 형식 파싱 실패 (ISO_LOCAL_DATE_TIME): {} - 다음 형식 시도", dateTimeStr);
        }

        // 5. ISO_LOCAL_DATE 형식 시도
        try {
            return LocalDateTime.parse(dateTimeStr + "T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            log.debug("날짜 형식 파싱 실패 (ISO_LOCAL_DATE): {} - 모든 형식 실패", dateTimeStr);
        }

        // 모든 포맷이 실패한 경우
        throw new DateTimeParseException(
                "Unsupported date format: " + dateTimeStr
                        + ". Supported formats: 'yyyy-MM-dd HH:mm:ss', 'yyyy-MM-dd HH:mm', 'yyyy-MM-dd'",
                dateTimeStr, 0);
    }

    /**
     * 파일들을 /data 폴더에 저장하고 파일 정보를 데이터베이스에 저장합니다.
     * 
     * @param noticeId 공지사항 ID
     * @param files    업로드할 파일들
     */
    private void saveFilesToDatabase(Long noticeId, MultipartFile[] files) {
        log.info("=== 파일 저장 시작 ===");
        log.info("공지사항 ID: {}", noticeId);
        log.info("전달받은 파일 수: {}", files != null ? files.length : 0);

        if (files == null || files.length == 0) {
            log.warn("저장할 파일이 없습니다.");
            return;
        }

        try {
            // 공지사항 ID별 디렉토리 생성 (프로젝트 루트 기준)
            Path noticeDir = Paths.get(FILE_UPLOAD_PATH, String.valueOf(noticeId));
            if (!Files.exists(noticeDir)) {
                Files.createDirectories(noticeDir);
                log.info("공지사항 ID별 디렉토리 생성 완료: {}", noticeDir.toAbsolutePath());
            }

            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                if (file == null) {
                    log.warn("파일 {}이 null입니다. 건너뜀", i + 1);
                    continue;
                }

                String originalFilename = file.getOriginalFilename();
                if (originalFilename == null || originalFilename.trim().isEmpty()) {
                    log.warn("파일 {}의 파일명이 없습니다. 건너뜀", i + 1);
                    continue;
                }

                log.info("파일 {} 처리 시작: {}", i + 1, originalFilename);
                log.info("파일 크기: {} bytes, Content-Type: {}", file.getSize(), file.getContentType());
                log.info("파일 비어있음 여부: {}", file.isEmpty());

                if (file.isEmpty()) {
                    log.warn("파일 {}이 비어있어 건너뜀: {}", i + 1, originalFilename);
                    continue;
                }

                validateFileIntegrity(file);

                // 저장용 파일명 생성
                String storedFilename = generateStoredFilename(originalFilename);
                Path filePath = noticeDir.resolve(storedFilename);

                // 파일을 data 폴더에 저장
                Files.copy(file.getInputStream(), filePath);
                log.info("파일 물리 저장 완료: {} -> {}", originalFilename, filePath);

                // contentType을 100자로 제한 (DB 컬럼 크기 제약)
                String contentType = file.getContentType();
                if (contentType != null && contentType.length() > 100) {
                    contentType = contentType.substring(0, 100);
                    log.debug("contentType 길이 제한: {} → {}", file.getContentType(), contentType);
                }

                // 파일 정보 생성
                NoticeFile noticeFile = NoticeFile.builder()
                        .noticeId(noticeId)
                        .originalFilename(originalFilename)
                        .storedFilename(storedFilename)
                        .fileSize(String.valueOf(file.getSize()))
                        .contentType(contentType)
                        .filePath(filePath.toString()) // 실제 파일 경로
                        .uploadDate(LocalDateTime.now())
                        .useYn(1) // Y -> 1로 변경
                        .build();

                // 파일 정보만 데이터베이스에 저장
                NoticeFile savedFile = noticeFileRepository.save(noticeFile);
                Long fileId = savedFile != null ? savedFile.getFileId() : null;
                log.info("파일 DB 저장 완료: {} (ID: {})", originalFilename, fileId);
            }

            log.info("{}개 파일을 data 폴더에 저장 완료", files.length);

        } catch (IOException e) {
            log.error("파일 저장 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR, "파일 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 저장용 파일명 생성 (UUID 기반)
     */
    private String generateStoredFilename(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

    private void validateFileIntegrity(MultipartFile file) {
        if (file == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "파일이 null입니다.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "파일명이 없습니다.");
        }

        MultipartFileHeaderChecker.FileCheckResult headerCheckResult = MultipartFileHeaderChecker.validate(file);
        if (headerCheckResult == null || !headerCheckResult.ok()) {
            String reason = headerCheckResult != null ? headerCheckResult.message() : "파일 검증에 실패했습니다.";
            log.warn("공지사항 파일 헤더 검증 실패 - fileName: {}, reason: {}", originalFilename, reason);
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, reason);
        }
        log.debug("공지사항 파일 헤더 검증 완료 - fileName: {}, mime: {}, type: {}",
                originalFilename, headerCheckResult.mimeType(), headerCheckResult.fileType());

        String extension = extractExtension(originalFilename);
        if (!StringUtils.hasText(extension) || !ALLOWED_EXTENSIONS.contains(extension)) {
            log.warn("허용되지 않은 확장자: {}", originalFilename);
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "허용되지 않은 파일 형식입니다.");
        }
    }

    private String extractExtension(String filename) {
        if (!StringUtils.hasText(filename) || !filename.contains(".")) {
            return null;
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    /**
     * 지정된 파일들을 /data 폴더와 데이터베이스에서 삭제합니다.
     * 
     * @param fileIds 삭제할 파일 ID들
     */
    private void deleteFilesFromDatabase(Long[] fileIds) {
        if (fileIds == null || fileIds.length == 0) {
            log.info("삭제할 파일 ID가 없습니다.");
            return;
        }

        try {
            Set<Path> noticeDirs = new HashSet<>(); // 삭제된 파일들의 폴더 경로 저장

            for (Long fileId : fileIds) {
                if (fileId == null) {
                    continue;
                }

                NoticeFile file = noticeFileRepository.findById(fileId).orElse(null);

                if (file == null) {
                    log.warn("삭제할 파일을 찾을 수 없음: {}", fileId);
                    continue;
                }

                // 실제 파일 삭제
                String filePathStr = file.getFilePath();
                if (filePathStr != null && !filePathStr.trim().isEmpty()) {
                    try {
                        Path filePath = Paths.get(filePathStr);
                        if (Files.exists(filePath)) {
                            Files.delete(filePath);
                            log.debug("실제 파일 삭제 완료: {}", filePath);
                            
                            // 폴더 경로 저장 (나중에 폴더 삭제용)
                            Path parentDir = filePath.getParent();
                            if (parentDir != null) {
                                noticeDirs.add(parentDir);
                            }
                        } else {
                            log.warn("삭제할 파일이 존재하지 않음: {}", filePath);
                            // 파일이 없어도 폴더 경로는 저장
                            Path parentDir = filePath.getParent();
                            if (parentDir != null) {
                                noticeDirs.add(parentDir);
                            }
                        }
                    } catch (IOException e) {
                        // 실제 파일 삭제 실패 시 로그만 남기고 계속 진행 (논리 삭제는 수행)
                        log.warn("실제 파일 삭제 실패: {} - {}", filePathStr, e.getMessage());
                        // 파일 삭제 실패해도 논리 삭제는 계속 진행하므로 예외를 다시 던지지 않음
                    }
                } else {
                    log.warn("파일 경로가 없어 실제 파일 삭제를 건너뜁니다 - 파일 ID: {}", fileId);
                }

                // 논리 삭제 (useYn = 'N'으로 설정)
                file.setUseYn(0); // N -> 0으로 변경
                noticeFileRepository.save(file);
                log.debug("파일 논리 삭제 완료: {} (ID: {})", file.getOriginalFilename(), fileId);
            }

            log.info("{}개 파일 삭제 처리 완료", fileIds.length);

            // 폴더 삭제 (파일이 모두 삭제된 경우)
            // 공지사항 ID별 폴더만 삭제 (shbdat/notice는 유지)
            Path basePath = Paths.get(FILE_UPLOAD_PATH);
            for (Path noticeDir : noticeDirs) {
                if (noticeDir != null && Files.exists(noticeDir)) {
                    try {
                        // 공지사항 ID별 폴더인지 확인 (shbdat/notice/{id} 형태)
                        if (noticeDir.startsWith(basePath) && !noticeDir.equals(basePath)) {
                            // 폴더가 비어있는지 확인
                            boolean isEmpty = false;
                            try {
                                isEmpty = Files.list(noticeDir).findAny().isEmpty();
                            } catch (IOException e) {
                                log.warn("파일 삭제 - 폴더 내용 확인 실패: {} - {}", noticeDir, e.getMessage());
                            }

                            if (isEmpty) {
                                Files.delete(noticeDir);
                                log.info("파일 삭제 - 빈 폴더 삭제 완료: {}", noticeDir);
                            } else {
                                log.debug("파일 삭제 - 폴더에 파일이 남아있어 삭제하지 않음: {}", noticeDir);
                            }
                        } else {
                            log.warn("파일 삭제 - 기본 폴더({})는 삭제하지 않음: {}", basePath, noticeDir);
                        }
                    } catch (IOException e) {
                        log.warn("파일 삭제 - 폴더 삭제 실패: {} - {}", noticeDir, e.getMessage());
                    }
                }
            }

        } catch (RuntimeException e) {
            log.error("파일 삭제 처리 실패: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR, "파일 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * NoticeManagement 엔티티를 안전하게 응답 DTO로 변환합니다.
     * 매핑 결과가 null이면 예외를 발생시켜 NPE를 방지합니다.
     *
     * @param notice 응답으로 변환할 공지사항 엔티티
     * @return 매핑된 NoticeManagementRes
     */
    private NoticeManagementRes mapToResponseOrThrow(NoticeManagement notice) {
        NoticeManagementRes response = noticeManagementMapper.toResponse(notice);
        if (response == null) {
            Long noticeId = notice != null ? notice.getNotiId() : null;
            log.error("공지사항 응답 매핑 실패 - noticeId: {}", noticeId);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "공지사항 응답 변환에 실패했습니다.");
        }
        return response;
    }
}
