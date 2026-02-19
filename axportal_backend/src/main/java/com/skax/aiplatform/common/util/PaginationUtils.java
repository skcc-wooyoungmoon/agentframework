package com.skax.aiplatform.common.util;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.skax.aiplatform.client.sktai.common.dto.Pagination;
import com.skax.aiplatform.client.sktai.common.dto.PaginationLink;
import com.skax.aiplatform.client.sktai.common.dto.Payload;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.common.response.PageableInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * 페이지네이션 변환 유틸리티 클래스
 * 
 * <p>ADXP(SKTAI) API의 Pagination 응답(1-based)을 Spring Data Page 및 PageResponse(0-based)로 변환합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li>ADXP의 1-based pagination을 Spring의 0-based Page로 자동 변환</li>
 *   <li>Null-safe 처리 보장</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * // 패턴 1: Payload와 Content 사용 (가장 일반적)
 * ServingsResponse servings = sktaiService.getServings(...);
 * List&lt;ServingResponse&gt; content = Optional.ofNullable(servings)
 *     .map(ServingsResponse::getData)
 *     .orElseGet(Collections::emptyList);
 * 
 * return PaginationUtils.toPageResponseFromAdxp(
 *     servings != null ? servings.getPayload() : null,
 *     content
 * );
 * 
 * // 패턴 2: Pagination 직접 사용
 * Pagination pagination = response.getPayload().getPagination();
 * return PaginationUtils.toPageResponseFromAdxp(pagination, content);
 * 
 * // 패턴 3: 데이터 변환 포함
 * return PaginationUtils.toPageResponseFromAdxp(
 *     sktaiResponse.getPayload(),
 *     sktaiResponse.getData(),
 *     mapper::toModelRes
 * );
 * </pre>
 *
 * @author System
 * @since 2025-01-27
 */
@Slf4j
public final class PaginationUtils {

    private PaginationUtils() {
        throw new UnsupportedOperationException("이 클래스는 유틸리티 클래스이므로 인스턴스화할 수 없습니다.");
    }

    /**
     * ADXP Payload와 데이터 목록을 PageResponse로 변환
     * 
     * <p>가장 일반적인 사용 패턴입니다. ADXP(SKTAI) API 응답의 Payload와 변환된 데이터 목록을 받아 PageResponse를 생성합니다.</p>
     * 
     * @param <T> 데이터 타입
     * @param payload ADXP Payload (pagination 정보 포함, null 가능)
     * @param content 변환된 데이터 목록
     * @return PageResponse 객체
     */
    public static <T> PageResponse<T> toPageResponseFromAdxp(Payload payload, List<T> content) {
        if (content == null) {
            content = Collections.emptyList();
        }

        Pagination pagination = Optional.ofNullable(payload)
            .map(Payload::getPagination)
            .orElse(null);

        return toPageResponseFromAdxp(pagination, content);
    }

    /**
     * ADXP Pagination과 데이터 목록을 PageResponse로 변환
     * 
     * <p>ADXP Pagination 객체와 변환된 데이터 목록을 받아 PageResponse를 생성합니다.</p>
     * 
     * @param <T> 데이터 타입
     * @param pagination ADXP Pagination (null 가능)
     * @param content 변환된 데이터 목록
     * @return PageResponse 객체
     */
    public static <T> PageResponse<T> toPageResponseFromAdxp(Pagination pagination, List<T> content) {
        if (content == null) {
            content = Collections.emptyList();
        }

        // Pagination 정보 추출 및 변환 (1-based → 0-based)
        int pageNumber = pagination != null && pagination.getPage() != null
            ? Math.max(pagination.getPage() - 1, 0)
            : 0;
        int pageSize = pagination != null && pagination.getItemsPerPage() != null
            ? pagination.getItemsPerPage()
            : (content.size() > 0 ? content.size() : 10);
        long total = pagination != null && pagination.getTotal() != null
            ? pagination.getTotal()
            : content.size();
        
        // totalPages 계산 (lastPage 사용, 없으면 계산)
        int totalPages = pagination != null && pagination.getLastPage() != null
            ? pagination.getLastPage()
            : (pageSize > 0 ? (int) Math.ceil((double) total / pageSize) : 0);

        // ADXP 로직에 따른 hasNext/hasPrevious 계산
        boolean hasNext = calculateHasNext(pagination, totalPages);
        // boolean hasPrevious = calculateHasPrevious(pagination, totalPages);
        
        // first/last 계산
        boolean first = pageNumber == 0;
        boolean last = totalPages > 0 && pageNumber >= totalPages - 1;

        // PageableInfo 생성
        PageableInfo pageableInfo = PageableInfo.builder()
            .page(pageNumber)
            .size(pageSize)
            .sort("")
            .build();

        // PageResponse 직접 빌드 (ADXP 로직 적용)
        return PageResponse.<T>builder()
            .content(content)
            .pageable(pageableInfo)
            .totalElements(total)
            .totalPages(totalPages)
            .first(first)
            .last(last)
            .hasNext(hasNext)
            // .hasPrevious(hasPrevious)
            .build();
    }

    /**
     * ADXP Pagination의 hasNext 계산
     * 
     * <p>JavaScript 로직을 Java로 변환:
     * <pre>
     * const lazy_mode = pageCount < 0;
     * const hasNext = lazy_mode ? (pagination?.links?.[1]?.url != null) || true : (page != lastPage);
     * </pre>
     * 
     * @param pagination ADXP Pagination
     * @param totalPages 전체 페이지 수 (lazy mode 감지용: totalPages < 0이면 lazy mode)
     * @return 다음 페이지 존재 여부
     */
    private static boolean calculateHasNext(Pagination pagination, int totalPages) {
        if (pagination == null) {
            return false;
        }

        // lazy mode 감지: totalPages < 0
        boolean lazyMode = totalPages < 0;

        if (lazyMode) {
            // lazy mode: links[1]?.url != null || true
            // JavaScript의 || true는 항상 true이므로, 실제로는 links[1]?.url != null만 체크
            List<PaginationLink> links = pagination.getLinks();
            if (links != null && links.size() > 1) {
                PaginationLink nextLink = links.get(1);
                return nextLink != null && nextLink.getUrl() != null;
            }
            // links가 없거나 크기가 1 이하인 경우 false
            return false;
        } else {
            // 일반 모드: page != lastPage (1-based)
            Integer currentPage = pagination.getPage();
            Integer lastPage = pagination.getLastPage();
            
            if (currentPage == null || lastPage == null) {
                return false;
            }
            
            return !currentPage.equals(lastPage);
        }
    }

    /**
     * ADXP Pagination의 hasPrevious 계산
     * 
     * <p>lazy mode와 일반 모드에 따라 이전 페이지 존재 여부를 계산합니다.</p>
     * 
     * @param pagination ADXP Pagination
     * @param totalPages 전체 페이지 수 (lazy mode 감지용: totalPages < 0이면 lazy mode)
     * @return 이전 페이지 존재 여부
     */
    private static boolean calculateHasPrevious(Pagination pagination, int totalPages) {
        if (pagination == null) {
            return false;
        }

        // lazy mode 감지: totalPages < 0
        boolean lazyMode = totalPages < 0;

        if (lazyMode) {
            // lazy mode: links[0]?.url != null 또는 page > 1
            List<PaginationLink> links = pagination.getLinks();
            boolean hasPrevLink = links != null 
                && links.size() > 0 
                && links.get(0) != null 
                && links.get(0).getUrl() != null;
            
            boolean hasPrevPage = pagination.getPage() != null && pagination.getPage() > 1;
            
            return hasPrevLink || hasPrevPage;
        } else {
            // 일반 모드: page > 1 (1-based)
            Integer currentPage = pagination.getPage();
            return currentPage != null && currentPage > 1;
        }
    }

    /**
     * ADXP 응답 데이터를 변환하여 PageResponse로 생성
     * 
     * <p>ADXP(SKTAI) 응답의 Payload와 원본 데이터 목록을 받아, 변환 함수를 적용한 후 PageResponse를 생성합니다.</p>
     * 
     * @param <S> ADXP 원본 데이터 타입
     * @param <T> 변환된 데이터 타입
     * @param payload ADXP Payload (pagination 정보 포함, null 가능)
     * @param adxpDataList ADXP 원본 데이터 목록
     * @param converter 데이터 변환 함수 (ADXP 데이터 → 내부 DTO)
     * @return PageResponse 객체
     */
    public static <S, T> PageResponse<T> toPageResponseFromAdxp(
            Payload payload,
            List<S> adxpDataList,
            Function<S, T> converter) {
        if (adxpDataList == null) {
            adxpDataList = Collections.emptyList();
        }

        // 데이터 변환
        List<T> content = adxpDataList.stream()
            .filter(Objects::nonNull)
            .map(converter)
            .collect(Collectors.toList());

        return toPageResponseFromAdxp(payload, content);
    }
}
