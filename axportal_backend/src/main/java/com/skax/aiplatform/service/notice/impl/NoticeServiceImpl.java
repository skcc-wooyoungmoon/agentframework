package com.skax.aiplatform.service.notice.impl;

import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.notice.request.GetNoticeReq;
import com.skax.aiplatform.dto.notice.response.GetNoticeByIdRes;
import com.skax.aiplatform.dto.notice.response.GetNoticeRes;
import com.skax.aiplatform.entity.GpoUsersMas;
import com.skax.aiplatform.entity.NoticeFile;
import com.skax.aiplatform.entity.NoticeManagement;
import com.skax.aiplatform.repository.admin.NoticeFileRepository;
import com.skax.aiplatform.repository.admin.NoticeRepository;
import com.skax.aiplatform.repository.auth.GpoUsersMasRepository;
import com.skax.aiplatform.service.notice.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeFileRepository noticeFileRepository;
    private final GpoUsersMasRepository gpoUsersMasRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<GetNoticeRes> getNoticeList(Pageable pageable, GetNoticeReq req) {

        log.info("-----------------------------------------------------------------------------------------");
        log.info("[ Execute Service NoticeServiceImpl.getNoticeList ]");
        log.info("Page                  : {}", pageable.getPageNumber());
        log.info("Size                  : {}", pageable.getPageSize());
        log.info("Sort                  : {}", req != null ? req.getSort() : null);
        log.info("SearchType            : {}", req != null ? req.getSearchType() : null);
        log.info("SearchValue           : {}", req != null ? req.getSearchValue() : null);
        log.info("NoticeType            : {}", req != null ? req.getNoticeType() : null);
        log.info("DateType              : {}", req != null ? req.getDateType() : null);
        log.info("DateFrom ~ To         : {} ~ {}", req != null ? req.getDateFrom() : null, req != null ? req.getDateTo() : null);
        log.info("-----------------------------------------------------------------------------------------");

        try {
            if (req == null) throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "요청이 비어있습니다.");

            // 정렬 우선 적용
            int rawPage = pageable.getPageNumber();
            int size = pageable.getPageSize();
            Sort sortObj = pageable.getSort();
            String sort = req.getSort();
            if (sort != null && !sort.isBlank()) {
                String[] parts = sort.split(",", 2);
                String key = parts[0].trim().toLowerCase();
                String dir = parts.length == 2 ? parts[1].trim().toLowerCase() : "asc";
                String prop = mapSortKey(key);
                if (prop != null)
                    sortObj = "desc".equals(dir) ? Sort.by(Sort.Order.desc(prop)) : Sort.by(Sort.Order.asc(prop));
            }
            pageable = PageRequest.of(Math.max(0, rawPage - 1), size, sortObj);

            // 기간 정규화: null 금지
            LocalDate fromDate = parseYmd(req.getDateFrom());
            LocalDate toDate = parseYmd(req.getDateTo());
            if (fromDate == null) fromDate = LocalDate.now().minusMonths(1);
            if (toDate == null) toDate = LocalDate.now();
            LocalDateTime fromDt = fromDate.atStartOfDay();
            LocalDateTime toDtPlus = toDate.plusDays(1).atStartOfDay(); // [from, to+1)

            // 필터 정규화
            String dateType = req.getDateType() == null ? "updated" : req.getDateType();
            boolean useCreated = "created".equalsIgnoreCase(dateType);
            boolean useUpdated = !useCreated;

            String searchType = "전체".equalsIgnoreCase(nz(req.getSearchType())) ? null : nz(req.getSearchType());
            String searchValue = nz(req.getSearchValue());
            String noticeType = "전체".equalsIgnoreCase(nz(req.getNoticeType())) ? null : nz(req.getNoticeType());

            Page<NoticeManagement> page = noticeRepository.findNoticesByFilters(
                    useUpdated, useCreated,
                    searchType, searchValue,
                    fromDt, toDtPlus,
                    noticeType,
                    pageable
            );

            List<GetNoticeRes> content = page.getContent().stream().map(this::toDto).toList();
            Page<GetNoticeRes> mapped = new PageImpl<>(content, page.getPageable(), page.getTotalElements());

            return PageResponse.from(mapped);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("공지사항 목록 조회 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "공지사항 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    @Override
    public GetNoticeByIdRes getNoticeById(String notiId) {
        log.info("-----------------------------------------------------------------------------------------");
        log.info("[ Execute Service NoticeServiceImpl.getNoticeById ]");
        log.info("notiId                  : {}", notiId);
        log.info("-----------------------------------------------------------------------------------------");
        try {
            NoticeManagement notice = noticeRepository.findById(Long.valueOf(notiId))
                    .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "공지사항이 존재하지 않습니다."));

            List<NoticeFile> files = noticeFileRepository.findByNoticeIdAndUseYnOrderByUploadDateDesc(
                    notice.getNotiId(), 1 // Y -> 1로 변경
            );

            return GetNoticeByIdRes.builder()
                    .notiId(notice.getNotiId())
                    .title(notice.getTitle())
                    .msg(buildFullMessage(notice))
                    .type(notice.getType())
                    .useYn(NoticeManagement.convertNumberToString(notice.getUseYn())) // Integer를 String으로 변환
                    .expFrom(notice.getExpFrom())
                    .expTo(notice.getExpTo())
                    .createAt(notice.getCreateAt())
                    .createBy(notice.getCreateBy())
                    .createByName(getUserInfoText(notice.getCreateBy()))
                    .updateAt(notice.getUpdateAt())
                    .updateBy(notice.getUpdateBy())
                    .updateByName(getUserInfoText(notice.getUpdateBy()))
                    .files(files.stream().map(f -> GetNoticeByIdRes.FileRes.builder()
                            .fileId(f.getFileId())
                            .originalFilename(f.getOriginalFilename())
                            .storedFilename(f.getStoredFilename())
                            .contentType(f.getContentType())
                            .filePath(f.getFilePath())
                            .fileSize(f.getFileSize())
                            .build()).toList())
                    .build();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("공지사항 상세 조회 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "공지사항 상세 조회에 실패했습니다: " + e.getMessage());
        }
    }

    private String getUserInfoText(String memberId) {
        Optional<GpoUsersMas> userOpt = gpoUsersMasRepository.findByMemberId(memberId);

        if (userOpt.isEmpty()) {
            return "";
        }

        String name = userOpt.get().getJkwNm();
        String dept = userOpt.get().getDeptNm();

        return "%s | %s".formatted(name, dept);
    }

    /* Helper 함수 모음 */
    private String mapSortKey(String key) {
        return switch (key) {
            case "modifieddate", "modified_at", "updatedat", "updated_at" -> "updateAt";
            case "createddate", "created_at" -> "createAt";
            case "title" -> "title";
            case "type" -> "type";
            default -> null;
        };
    }

    private LocalDate parseYmd(String ymd) {
        if (ymd == null || ymd.isBlank()) return null;
        return LocalDate.parse(ymd.trim().replace('.', '-')); // 2025.07.30 → 2025-07-30
    }

    private String nz(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    private GetNoticeRes toDto(NoticeManagement n) {
        return GetNoticeRes.builder()
                .id(String.valueOf(n.getNotiId()))
                .title(n.getTitle())
                .type(n.getType())
                .content(buildFullMessage(n))
                .createdDate(n.getCreateAt() != null ? n.getCreateAt().toString() : null)
                .modifiedDate(n.getUpdateAt() != null ? n.getUpdateAt().toString() : null)
                .build();
    }

    /**
     * NoticeManagement의 detail 필드들을 조합하여 전체 메시지 생성
     * NoticeManagementServiceImpl의 buildFullMessage 로직 참조
     *
     * @param notice 공지사항 엔티티
     * @return 조합된 전체 메시지
     */
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

    /**
     * 세그먼트를 StringBuilder에 추가
     *
     * @param builder StringBuilder
     * @param segment 추가할 세그먼트
     */
    private void appendSegment(StringBuilder builder, String segment) {
        if (builder == null || segment == null || segment.isEmpty()) {
            return;
        }
        builder.append(segment);
    }
}