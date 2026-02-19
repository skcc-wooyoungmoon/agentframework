package com.skax.aiplatform.controller.notice;

import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.notice.request.GetNoticeReq;
import com.skax.aiplatform.dto.notice.response.GetNoticeByIdRes;
import com.skax.aiplatform.dto.notice.response.GetNoticeRes;
import com.skax.aiplatform.service.notice.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

/**
 * 공지사항 컨트롤러 (AxResponseEntity 적용)
 *
 * <p>공지사항를 조회합니다.
 * AxResponseEntity를 통해 통합된 응답 형식을 제공합니다.</p>
 *
 * @author yunyoseob
 * @version 0.0.1
 * @since 2025-09-29
 */
@Slf4j
@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
@Tag(name = "Notices", description = "공지사항 API")
public class NoticeController {
    private final NoticeService noticeService;

    /**
     * 공지사항 목록 조회
     * <p>
     *
     * @param pageable    페이징 정보
     * @param searchType  검색어 유형
     * @param searchValue 검색어
     * @param dateFrom    조회 시작일
     * @param dateTo      조회 종료일
     * @param condition   조회 조건
     * @param noticeType  유형
     * @param sort        정렬 조건
     * @return 데이터셋 목록
     */
    @GetMapping
    @Operation(summary = "공지사항 목록", description = "공지사항 목록을 조회한다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "공지사항 목록 조회 성공")})
    public AxResponseEntity<PageResponse<GetNoticeRes>> getNoticeList(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchValue", required = false) String searchValue,
            @RequestParam(value = "dateFrom", required = false) String dateFrom,
            @RequestParam(value = "dateTo", required = false) String dateTo,
            @RequestParam(value = "condition", required = false) String condition,
            @RequestParam(value = "noticeType", required = false) String noticeType,
            @RequestParam(value = "sort", defaultValue = "modifiedDate,desc") String sort
    ) {
        log.info("Controller: 공지사항 목록 조회 API 호출 - page: {}, size: {}, searchType: {}, searchValue: {}, dateFrom: {}, dateTo: {}, condition: {}, noticeType: {}, sort: {}",
                pageable.getPageNumber(), pageable.getPageSize(), searchType, searchValue, dateFrom, dateTo, condition, noticeType, sort);

        GetNoticeReq req = GetNoticeReq.builder()
                .searchType(searchType)
                .searchValue(searchValue)
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .condition(condition)
                .noticeType(noticeType)
                .sort(sort)
                .build();

        PageResponse<GetNoticeRes> noticeList = noticeService.getNoticeList(pageable, req);
        return AxResponseEntity.okPage(noticeList, "공지사항 목록을 성공적으로 조회했습니다.");
    }

    /**
     * 공지사항 목록 조회
     * <p>
     *
     * @param notiId 페이징 정보
     * @return 데이터셋 목록
     */
    @GetMapping("/{notiId}")
    @Operation(summary = "공지사항 목록", description = "공지사항 목록을 조회한다.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "공지사항 목록 조회 성공")})
    public AxResponseEntity<GetNoticeByIdRes> getNoticeById(
            @Parameter(description = "공지사항 ID", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @PathVariable("notiId") String notiId
    ) {
        log.info("Controller: 공지사항 상세 조회 API 호출 - notiId: {}", notiId);
        GetNoticeByIdRes getNoticeByIdRes = noticeService.getNoticeById(notiId);
        return AxResponseEntity.ok(getNoticeByIdRes, "공지사항 목록을 성공적으로 조회했습니다.");
    }
}
