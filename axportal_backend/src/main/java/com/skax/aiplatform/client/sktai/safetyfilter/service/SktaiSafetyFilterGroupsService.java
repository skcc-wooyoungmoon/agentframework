package com.skax.aiplatform.client.sktai.safetyfilter.service;

import com.skax.aiplatform.client.sktai.safetyfilter.SktaiSafetyFilterGroupsClient;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.request.SafetyFilterGroupImportRequest;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.request.SktSafetyFilterGroupCreateReq;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.request.SktSafetyFilterGroupUpdateReq;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.SafetyFilterGroupImportResponse;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.SktSafetyFilterGroupUpdateRes;
import com.skax.aiplatform.client.sktai.safetyfilter.dto.response.SktSafetyFilterGroupsRes;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SKTAI SafetyFilter 그룹 관리 서비스
 *
 * <p>
 * SafetyFilter 그룹의 CRUD 작업을 담당하는 서비스 계층입니다.
 * Feign Client를 래핑하여 비즈니스 로직과 예외 처리를 제공합니다.
 * </p>
 *
 * <h3>제공 기능:</h3>
 * <ul>
 * <li><strong>그룹 생성</strong>: 새로운 SafetyFilter 그룹 생성</li>
 * <li><strong>그룹 목록 조회</strong>: 페이지네이션, 정렬, 필터링, 검색 지원</li>
 * <li><strong>그룹 상세 조회</strong>: 특정 그룹의 상세 정보 조회</li>
 * <li><strong>그룹 수정</strong>: 그룹 정보 업데이트</li>
 * <li><strong>그룹 삭제</strong>: 그룹 및 관련 불용어 삭제</li>
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
public class SktaiSafetyFilterGroupsService {

    private final SktaiSafetyFilterGroupsClient safetyFilterGroupsClient;

    /**
     * SafetyFilter 그룹 목록 조회
     *
     * <p>
     * 등록된 안전 필터 그룹 목록을 조회합니다.
     * </p>
     *
     * @param page                페이지 번호
     * @param size                페이지 크기
     * @param sort                정렬 조건
     * @param filter              필터 조건
     * @param search              검색 키워드
     * @param groupId             특정 그룹 ID
     * @param includeUnclassified 미분류 그룹 포함 여부
     * @return 그룹 목록과 페이지네이션 정보
     * @throws BusinessException 그룹 목록 조회 실패 시
     */
    public SktSafetyFilterGroupsRes getSafetyFilterGroups(Integer page, Integer size, String sort,
            String filter, String search, String groupId,
            Boolean includeUnclassified) {
        log.debug("🛡️ SafetyFilter 그룹 목록 조회 - page: {}, size: {}", page, size);

        try {
            SktSafetyFilterGroupsRes response = safetyFilterGroupsClient.getSafetyFilterGroups(
                    page, size, sort, filter, search, groupId, includeUnclassified);

            log.debug("🛡️ SafetyFilter 그룹 목록 조회 성공 - 조회된 그룹 수: {}", response.getData().size());

            return response;
        } catch (BusinessException e) {
            log.error("🛡️ SafetyFilter 그룹 목록 조회 실패 - page: {}, size: {}, error: {}",
                    page, size, e.getMessage(), e);

            throw e;
        } catch (Exception e) {
            log.error("🛡️ SafetyFilter 그룹 목록 조회 중 예상치 못한 오류 - page: {}, size: {}",
                    page, size, e);

            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "SafetyFilter 그룹 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * SafetyFilter 그룹 상세 조회
     *
     * <p>
     * 특정 안전 필터 그룹의 상세 정보를 조회합니다.
     * </p>
     *
     * @param groupId 조회할 그룹 ID
     * @return 그룹 상세 정보
     * @throws BusinessException 그룹 조회 실패 시
     */
    public SktSafetyFilterGroupUpdateRes getSafetyFilterGroup(String groupId) {
        log.debug("🛡️ SafetyFilter 그룹 상세 조회 - groupId: {}", groupId);

        try {
            SktSafetyFilterGroupUpdateRes response = safetyFilterGroupsClient.getSafetyFilterGroup(groupId);
            log.debug("🛡️ SafetyFilter 그룹 상세 조회 성공 - groupId: {}, name: {}",
                    response.getId(), response.getName());
            return response;
        } catch (BusinessException e) {
            log.error("🛡️ SafetyFilter 그룹 상세 조회 실패 - groupId: {}, error: {}",
                    groupId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("🛡️ SafetyFilter 그룹 상세 조회 중 예상치 못한 오류 - groupId: {}",
                    groupId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "SafetyFilter 그룹 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * SafetyFilter 그룹 생성
     *
     * <p>
     * 새로운 안전 필터 그룹을 생성합니다.
     * </p>
     *
     * @param request 그룹 생성 요청 정보
     * @return 생성된 그룹 정보
     * @throws BusinessException 그룹 생성 실패 시
     */
    public SktSafetyFilterGroupUpdateRes createSafetyFilterGroup(SktSafetyFilterGroupCreateReq request) {
        log.debug("🛡️ SafetyFilter 그룹 생성 요청 - name: {}", request.getName());

        try {
            SktSafetyFilterGroupUpdateRes response = safetyFilterGroupsClient.createSafetyFilterGroup(request);

            log.debug("🛡️ SafetyFilter 그룹 생성 성공 - groupId: {}, name: {}",
                    response.getId(), response.getName());

            return response;
        } catch (BusinessException e) {
            log.error("🛡️ SafetyFilter 그룹 생성 실패 - name: {}, error: {}",
                    request.getName(), e.getMessage(), e);

            throw e;
        } catch (Exception e) {
            log.error("🛡️ SafetyFilter 그룹 생성 중 예상치 못한 오류 - name: {}",
                    request.getName(), e);

            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "SafetyFilter 그룹 생성에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * SafetyFilter 그룹 수정
     *
     * <p>
     * 기존 안전 필터 그룹의 정보를 수정합니다.
     * </p>
     *
     * @param groupId 수정할 그룹 ID
     * @param request 그룹 수정 요청 정보
     * @return 수정된 그룹 정보
     * @throws BusinessException 그룹 수정 실패 시
     */
    public SktSafetyFilterGroupUpdateRes updateSafetyFilterGroup(String groupId,
            SktSafetyFilterGroupUpdateReq request) {
        log.debug("🛡️ SafetyFilter 그룹 수정 요청 - groupId: {}, newName: {}",
                groupId, request.getName());

        try {
            SktSafetyFilterGroupUpdateRes response = safetyFilterGroupsClient.updateSafetyFilterGroup(groupId, request);
            log.debug("🛡️ SafetyFilter 그룹 수정 성공 - groupId: {}, newName: {}",
                    response.getId(), response.getName());

            return response;
        } catch (BusinessException e) {
            log.error("🛡️ SafetyFilter 그룹 수정 실패 - groupId: {}, newName: {}, error: {}",
                    groupId, request.getName(), e.getMessage(), e);

            throw e;
        } catch (Exception e) {
            log.error("🛡️ SafetyFilter 그룹 수정 중 예상치 못한 오류 - groupId: {}, newName: {}",
                    groupId, request.getName(), e);

            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "SafetyFilter 그룹 수정에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * SafetyFilter 그룹 삭제
     *
     * <p>
     * 안전 필터 그룹을 삭제합니다. 그룹에 속한 모든 불용어도 함께 삭제됩니다.
     * </p>
     *
     * @param groupId 삭제할 그룹 ID
     * @throws BusinessException 그룹 삭제 실패 시
     */
    public void deleteSafetyFilterGroup(String groupId) {
        log.debug("🛡️ SafetyFilter 그룹 삭제 요청 - groupId: {}", groupId);

        try {
            safetyFilterGroupsClient.deleteSafetyFilterGroup(groupId);

            log.debug("🛡️ SafetyFilter 그룹 삭제 성공 - groupId: {}", groupId);
        } catch (BusinessException e) {
            log.error("🛡️ SafetyFilter 그룹 삭제 실패 - groupId: {}, error: {}",
                    groupId, e.getMessage(), e);

            throw e;
        } catch (Exception e) {
            log.error("🛡️ SafetyFilter 그룹 삭제 중 예상치 못한 오류 - groupId: {}",
                    groupId, e);

            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "SafetyFilter 그룹 삭제에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * SafetyFilter 그룹 Import
     *
     * <p>
     * Export된 안전 필터 그룹을 Import합니다.
     * 기존 그룹이 존재하면 업데이트하고, 없으면 새로 생성합니다.
     * </p>
     *
     * @param request Import할 그룹 정보
     * @return Import 결과와 그룹 정보
     * @throws BusinessException 그룹 Import 실패 시
     */
    public SafetyFilterGroupImportResponse importSafetyFilterGroup(SafetyFilterGroupImportRequest request) {
        log.debug("🛡️ SafetyFilter 그룹 Import 요청 - id: {}, name: {}",
                request.getId(), request.getName());

        try {
            SafetyFilterGroupImportResponse response = safetyFilterGroupsClient.importSafetyFilterGroup(request);

            log.debug("🛡️ SafetyFilter 그룹 Import 성공 - id: {}, status: {}, name: {}",
                    response.getId(), response.getStatus(),
                    response.getGroup() != null ? response.getGroup().getName() : "N/A");

            return response;
        } catch (BusinessException e) {
            log.error("🛡️ SafetyFilter 그룹 Import 실패 - id: {}, name: {}, error: {}",
                    request.getId(), request.getName(), e.getMessage(), e);

            throw e;
        } catch (Exception e) {
            log.error("🛡️ SafetyFilter 그룹 Import 중 예상치 못한 오류 - id: {}, name: {}",
                    request.getId(), request.getName(), e);

            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "SafetyFilter 그룹 Import에 실패했습니다: " + e.getMessage());
        }
    }

}
