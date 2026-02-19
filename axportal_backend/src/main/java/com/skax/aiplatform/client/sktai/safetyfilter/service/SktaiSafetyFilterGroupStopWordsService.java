package com.skax.aiplatform.client.sktai.safetyfilter.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.sktai.safetyfilter.SktaiSafetyFilterGroupStopwordsClient;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.request.SafetyFilterGroupStopwordsDelete;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.request.SktSafetyFilterGroupKeywordsUpdateReq;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.request.SktSafetyFilterGroupStopWordsCreateReq;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.GroupStopwordsBatchImportResponse;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.OperationResponse;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.SktSafetyFilterGroupStopWordUpdateRes;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.SktSafetyFilterGroupsStopWordRes;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SKT AI SafetyFilter 그룹 불용어 관리 서비스
 *
 * <p>
 * SafetyFilter 그룹의 불용어(stopword) 관리를 담당하는 서비스 계층입니다.
 * Feign Client를 래핑하여 그룹별 불용어 CRUD 작업의 비즈니스 로직과 예외 처리를 제공합니다.
 * </p>
 *
 * <h3>제공 기능:</h3>
 * <ul>
 * <li><strong>그룹 불용어 조회</strong>: 그룹별 불용어 목록 및 통계 조회</li>
 * <li><strong>불용어 완전 교체</strong>: 기존 불용어를 모두 삭제하고 새 목록으로 교체</li>
 * <li><strong>불용어 추가</strong>: 기존 불용어를 유지하면서 새로운 불용어 추가</li>
 * <li><strong>불용어 삭제</strong>: 특정 불용어들을 선택적으로 삭제</li>
 * </ul>
 *
 * <h3>예외 처리:</h3>
 * <ul>
 * <li>SktaiErrorDecoder를 통한 HTTP 오류 자동 변환</li>
 * <li>BusinessException dual catch 패턴 적용</li>
 * <li>상세한 로깅 및 오류 추적</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @since 2025-10-17
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiSafetyFilterGroupStopWordsService {

    private final SktaiSafetyFilterGroupStopwordsClient groupStopWordsClient;

    /**
     * SafetyFilter 그룹 불용어 목록 조회
     *
     * <p>
     * SafetyFilter 그룹별로 분류된 불용어 목록을 조회합니다.
     * </p>
     *
     * @param page                페이지 번호
     * @param size                페이지 크기
     * @param sort                정렬 조건
     * @param filter              필터 조건
     * @param search              검색 키워드
     * @param groupId             특정 그룹 ID
     * @param includeUnclassified 미분류 그룹 포함 여부
     * @return 그룹별 불용어 목록과 통계 정보
     * @throws BusinessException 그룹 불용어 목록 조회 실패 시
     */
    public SktSafetyFilterGroupsStopWordRes getSafetyFilterGroupsStopWords(
            Integer page, Integer size, String sort, String filter,
            String search, String groupId, Boolean includeUnclassified) {
        log.debug("🛡️ SafetyFilter 그룹 불용어 목록 조회 - page: {}, size: {}, groupId: {}",
                page, size, groupId);

        try {
            SktSafetyFilterGroupsStopWordRes response = groupStopWordsClient.getSafetyFilterGroupsStopwords(
                    page, size, sort, filter, search, groupId, includeUnclassified);

            log.debug("🛡️ SafetyFilter 그룹 불용어 목록 조회 성공 - 그룹 수: {}",
                    response.getData().size());
            log.info("£ {}", response);

            return response;
        } catch (BusinessException e) {
            log.error("🛡️ SafetyFilter 그룹 불용어 목록 조회 실패 - page: {}, size: {}, error: {}",
                    page, size, e.getMessage(), e);

            throw e;
        } catch (Exception e) {
            log.error("🛡️ SafetyFilter 그룹 불용어 목록 조회 중 예상치 못한 오류 - page: {}, size: {}",
                    page, size, e);

            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "SafetyFilter 그룹 불용어 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * SafetyFilter 그룹 키워드 완전 교체
     *
     * <p>
     * 그룹의 모든 불용어를 새로운 목록으로 완전히 교체합니다.
     * </p>
     *
     * @param groupId 대상 그룹 ID
     * @param request 새로운 불용어 목록
     * @return 업데이트 작업 결과 및 통계
     * @throws BusinessException 키워드 교체 실패 시
     */
    public SktSafetyFilterGroupStopWordUpdateRes updateSafetyFilterGroupKeywords(String groupId,
            SktSafetyFilterGroupKeywordsUpdateReq request) {
        log.debug("🛡️ SafetyFilter 그룹 키워드 완전 교체 요청 - groupId: {}, 새 불용어 수: {}",
                groupId, request.getStopWords() != null ? request.getStopWords().size() : 0);

        try {
            SktSafetyFilterGroupStopWordUpdateRes response = groupStopWordsClient
                    .updateSafetyFilterGroupKeywords(groupId, request);

            log.debug("🛡️ SafetyFilter 그룹 키워드 완전 교체 성공 - groupId: {}, 생성: {}, 삭제: {}, 최종: {}",
                    response.getGroupId(), response.getCreatedCount(),
                    response.getDeletedCount(), response.getTotalCount());

            return response;
        } catch (BusinessException e) {
            log.error("🛡️ SafetyFilter 그룹 키워드 완전 교체 실패 - groupId: {}, error: {}",
                    groupId, e.getMessage(), e);

            throw e;
        } catch (Exception e) {
            log.error("🛡️ SafetyFilter 그룹 키워드 완전 교체 중 예상치 못한 오류 - groupId: {}",
                    groupId, e);

            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "SafetyFilter 그룹 키워드 교체에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * SafetyFilter 그룹 키워드 추가
     *
     * <p>
     * 그룹에 새로운 불용어들을 추가합니다. 기존 불용어는 유지됩니다.
     * </p>
     *
     * @param groupId 대상 그룹 ID
     * @param request 추가할 불용어 목록
     * @return 업데이트 작업 결과 및 통계
     * @throws BusinessException 키워드 추가 실패 시
     */
    public SktSafetyFilterGroupStopWordUpdateRes appendSafetyFilterGroupKeywords(
            String groupId,
            SktSafetyFilterGroupStopWordsCreateReq request) {
        log.debug("🛡️ SafetyFilter 그룹 키워드 추가 요청 - groupId: {}, 추가할 불용어 수: {}",
                groupId, request.getStopWords().size());

        try {
            SktSafetyFilterGroupStopWordUpdateRes response = groupStopWordsClient
                    .appendSafetyFilterGroupKeywords(groupId, request);

            log.debug("🛡️ SafetyFilter 그룹 키워드 추가 성공 - groupId: {}, 추가된 수: {}, 최종: {}",
                    response.getGroupId(), response.getCreatedCount(), response.getTotalCount());

            return response;
        } catch (BusinessException e) {
            log.error("🛡️ SafetyFilter 그룹 키워드 추가 실패 - groupId: {}, error: {}",
                    groupId, e.getMessage(), e);

            throw e;
        } catch (Exception e) {
            log.error("🛡️ SafetyFilter 그룹 키워드 추가 중 예상치 못한 오류 - groupId: {}",
                    groupId, e);

            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "SafetyFilter 그룹 키워드 추가에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * SafetyFilter 그룹 불용어 삭제
     *
     * <p>
     * 그룹에서 특정 불용어들을 삭제합니다.
     * </p>
     *
     * @param groupId 대상 그룹 ID
     * @param request 삭제할 불용어 목록
     * @return 삭제 작업 결과
     * @throws BusinessException 불용어 삭제 실패 시
     */
    public OperationResponse deleteSafetyFilterGroupStopwords(String groupId,
            SafetyFilterGroupStopwordsDelete request) {
        log.debug("🛡️ SafetyFilter 그룹 불용어 삭제 요청 - groupId: {}, 삭제할 불용어 수: {}",
                groupId, request.getStopwords() != null ? request.getStopwords().size() : 0);

        try {
            OperationResponse response = groupStopWordsClient.deleteSafetyFilterGroupStopwords(groupId,
                    request);
            log.debug("🛡️ SafetyFilter 그룹 불용어 삭제 성공 - groupId: {}, 처리시간: {}ms",
                    groupId, response.getResponseTimeMs());
            return response;
        } catch (BusinessException e) {
            log.error("🛡️ SafetyFilter 그룹 불용어 삭제 실패 - groupId: {}, error: {}",
                    groupId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("🛡️ SafetyFilter 그룹 불용어 삭제 중 예상치 못한 오류 - groupId: {}",
                    groupId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "SafetyFilter 그룹 불용어 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * SafetyFilter 그룹 Stopwords 배치 Import
     *
     * <p>
     * 여러 그룹의 Stopwords를 한 번에 Import합니다.
     * 각 그룹별로 Import 결과가 개별적으로 반환됩니다.
     * </p>
     *
     * @param json Import할 그룹 Stopwords 목록
     * @return 배치 Import 결과 (성공/실패 카운트, 상세 정보)
     * @throws BusinessException 배치 Import 실패 시
     */
    public GroupStopwordsBatchImportResponse importGroupStopwordsBatch(String json) {
        log.debug("🛡️ SafetyFilter 그룹 Stopwords 배치 Import 요청 - {}", json);

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Object jsonData = objectMapper.readValue(json, Object.class);

            GroupStopwordsBatchImportResponse response = groupStopWordsClient.importGroupStopwordsBatch(jsonData);

            log.debug("🛡️ SafetyFilter 그룹 Stopwords 배치 Import 완료 - 상태: {}, 전체: {}, 성공: {}, 실패: {}",
                    response.getStatus(), response.getTotalCount(),
                    response.getSuccessCount(), response.getFailureCount());

            return response;
        } catch (BusinessException e) {
            log.error("🛡️ SafetyFilter 그룹 Stopwords 배치 Import 실패 - {}, error: {}",
                    json, e.getMessage(), e);

            throw e;
        } catch (Exception e) {
            log.error("🛡️ SafetyFilter 그룹 Stopwords 배치 Import 중 예상치 못한 오류 - {}",
                    json, e);

            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "SafetyFilter 그룹 Stopwords 배치 Import에 실패했습니다: " + e.getMessage());
        }
    }

}
