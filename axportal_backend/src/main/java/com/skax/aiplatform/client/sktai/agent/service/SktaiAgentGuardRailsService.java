package com.skax.aiplatform.client.sktai.agent.service;

import org.springframework.stereotype.Service;

import com.skax.aiplatform.client.sktai.agent.SktaiAgentGuardRailsClient;
import com.skax.aiplatform.client.sktai.agent.dto.request.SktGuardRailCreateReq;
import com.skax.aiplatform.client.sktai.agent.dto.request.SktGuardRailUpdateReq;
import com.skax.aiplatform.client.sktai.agent.dto.response.SktGuardRailCreateRes;
import com.skax.aiplatform.client.sktai.agent.dto.response.SktGuardRailDetailRes;
import com.skax.aiplatform.client.sktai.agent.dto.response.SktGuardRailUpdateRes;
import com.skax.aiplatform.client.sktai.agent.dto.response.SktGuardRailsRes;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SKT AI Agent GuardRails 서비스
 *
 * <p>
 * SKT AI Agent GuardRails API 호출을 래핑하는 서비스 클래스입니다.
 * 가드레일의 생성, 조회, 수정, 삭제 기능을 제공합니다.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiAgentGuardRailsService {

    private final SktaiAgentGuardRailsClient sktaiAgentGuardRailsClient;

    /**
     * 가드레일 목록 조회
     *
     * @param projectId 프로젝트 ID
     * @param page      페이지 번호
     * @param size      페이지 크기
     * @param sort      정렬 조건
     * @param filter    필터 조건
     * @param search    검색어
     * @return 가드레일 목록 응답
     */
    public SktGuardRailsRes getGuardRails(String projectId, Integer page, Integer size, String sort, String filter,
            String search) {
        try {
            log.info("SKT AI 가드레일 목록 조회 요청 - projectId: {}, page: {}, size: {}, sort: {}, filter: {}, search: {}",
                    projectId, page, size, sort, filter, search);

            SktGuardRailsRes response = sktaiAgentGuardRailsClient.getGuardRails(projectId, page, size, sort, filter,
                    search);

            int guardRailCount = response.getData() != null ? response.getData().size() : 0;

            log.info("SKT AI 가드레일 목록 조회 완료 - projectId: {}, count: {}", projectId, guardRailCount);

            return response;
        } catch (BusinessException e) { // ErrorDecoder가 이미 적절한 ErrorCode로 변환했으므로 그대로 전파
            log.error("SKT AI 가드레일 목록 조회 실패 - projectId: {}, errorCode: {}",
                    projectId, e.getErrorCode(), e);

            throw e;
        } catch (Exception e) { // ErrorDecoder를 거치지 않은 예상치 못한 예외만 처리
            log.error("SKT AI 가드레일 목록 조회 중 예상치 못한 오류 - projectId: {}", projectId, e);

            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "가드레일 목록 조회 중 예상치 못한 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 가드레일 생성
     *
     * @param request 가드레일 생성 요청
     * @return 가드레일 생성 응답 (가드레일 ID 포함)
     */
    public SktGuardRailCreateRes createGuardRail(SktGuardRailCreateReq request) {
        try {
            log.info("SKT AI 가드레일 생성 요청 - name: {}, projectId: {}", request.getName(), request.getProjectId());

            SktGuardRailCreateRes response = sktaiAgentGuardRailsClient.createGuardRail(request);

            log.info("SKT AI 가드레일 생성 완료 - guardrailsId: {}", response.getData().getGuardrailsId());

            return response;
        } catch (BusinessException e) { // ErrorDecoder가 이미 적절한 ErrorCode로 변환했으므로 그대로 전파
            log.error("SKT AI 가드레일 생성 실패 - name: {}, projectId: {}, errorCode: {}",
                    request.getName(), request.getProjectId(), e.getErrorCode(), e);

            throw e;
        } catch (Exception e) { // ErrorDecoder를 거치지 않은 예상치 못한 예외만 처리
            log.error("SKT AI 가드레일 생성 중 예상치 못한 오류 - name: {}, projectId: {}",
                    request.getName(), request.getProjectId(), e);

            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "가드레일 생성 중 예상치 못한 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 가드레일 상세 조회 (ID로 조회)
     *
     * @param guardrailsId 가드레일 ID
     * @return 가드레일 상세 정보
     */
    public SktGuardRailDetailRes getGuardRailById(String guardrailsId) {
        try {
            log.info("SKT AI 가드레일 상세 조회 - guardrailsId: {}", guardrailsId);

            SktGuardRailDetailRes response = sktaiAgentGuardRailsClient.getGuardRail(guardrailsId);

            log.info("SKT AI 가드레일 상세 조회 완료 - name: {}",
                    response.getData() != null ? response.getData().getName() : null);

            return response;
        } catch (BusinessException e) { // ErrorDecoder가 이미 적절한 ErrorCode로 변환했으므로 그대로 전파
            log.error("SKT AI 가드레일 상세 조회 실패 - guardrailsId: {}, errorCode: {}",
                    guardrailsId, e.getErrorCode(), e);

            throw e;
        } catch (Exception e) { // ErrorDecoder를 거치지 않은 예상치 못한 예외만 처리
            log.error("SKT AI 가드레일 상세 조회 중 예상치 못한 오류 - guardrailsId: {}", guardrailsId, e);

            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "가드레일 조회 중 예상치 못한 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 가드레일 수정
     *
     * @param guardrailsId 가드레일 ID
     * @param request      가드레일 수정 요청
     * @return 가드레일 수정 응답
     */
    public SktGuardRailUpdateRes updateGuardRail(String guardrailsId, SktGuardRailUpdateReq request) {
        try {
            log.info("SKT AI 가드레일 수정 요청 - guardrailsId: {}, request: {}", guardrailsId, request);

            SktGuardRailUpdateRes response = sktaiAgentGuardRailsClient.updateGuardRail(guardrailsId, request);

            log.info("SKT AI 가드레일 수정 완료 - guardrailsId: {}", guardrailsId);

            return response;
        } catch (BusinessException e) { // ErrorDecoder가 이미 적절한 ErrorCode로 변환했으므로 그대로 전파
            log.error("SKT AI 가드레일 수정 실패 - guardrailsId: {}, errorCode: {}",
                    guardrailsId, e.getErrorCode(), e);

            throw e;
        } catch (Exception e) { // ErrorDecoder를 거치지 않은 예상치 못한 예외만 처리
            log.error("SKT AI 가드레일 수정 중 예상치 못한 오류 - guardrailsId: {}", guardrailsId, e);

            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "가드레일 수정 중 예상치 못한 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 가드레일 삭제
     *
     * @param guardrailsId 가드레일 ID
     */
    public void deleteGuardRail(String guardrailsId) {
        try {
            log.info("SKT AI 가드레일 삭제 요청 - guardrailsId: {}", guardrailsId);

            sktaiAgentGuardRailsClient.deleteGuardRail(guardrailsId);

            log.info("SKT AI 가드레일 삭제 완료 - guardrailsId: {}", guardrailsId);
        } catch (BusinessException e) { // ErrorDecoder가 이미 적절한 ErrorCode로 변환했으므로 그대로 전파
            log.error("SKT AI 가드레일 삭제 실패 - guardrailsId: {}, errorCode: {}",
                    guardrailsId, e.getErrorCode(), e);

            throw e;
        } catch (Exception e) { // ErrorDecoder를 거치지 않은 예상치 못한 예외만 처리
            log.error("SKT AI 가드레일 삭제 중 예상치 못한 오류 - guardrailsId: {}", guardrailsId, e);

            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "가드레일 삭제 중 예상치 못한 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * Guard Rail Import (JSON)
     * 
     * <p>JSON 문자열을 받아서 Guard Rail을 생성합니다.
     * 마이그레이션 등에서 사용됩니다.</p>
     * 
     * @param guardrailsId Guard Rail ID
     * @param json JSON 문자열
     * @return 생성된 Guard Rail 정보
     */
    public SktGuardRailCreateRes importGuardRail(String guardrailsId, String json) {
        try {
            log.info("Guard Rail Import 요청 - guardrailsId: {}, jsonLength: {}", guardrailsId, json != null ? json.length() : 0);
            
            // JSON 문자열을 Object로 변환
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Object jsonData = objectMapper.readValue(json, Object.class);
            
            SktGuardRailCreateRes response = sktaiAgentGuardRailsClient.importGuardRail(guardrailsId, jsonData);
            log.info("Guard Rail Import 성공");
            return response;
        } catch (BusinessException e) {
            log.error("Guard Rail Import 실패 (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Guard Rail Import 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "Guard Rail Import에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Guard Rail 하드 삭제
     * 
     * <p>삭제 마크된 모든 Guard Rail들을 데이터베이스에서 완전히 삭제합니다.</p>
     * 
     * @apiNote 이 작업은 되돌릴 수 없으므로 주의해서 사용해야 합니다.
     */
    public void hardDeleteGuardRails() {
        try {
            log.debug("Guard Rail 하드 삭제 요청");
            sktaiAgentGuardRailsClient.hardDeleteGuardRails();
            log.debug("Guard Rail 하드 삭제 성공");
        } catch (BusinessException e) {
            log.error("Guard Rail 하드 삭제 실패 (BusinessException) - message: {}", e.getMessage());
            throw e;
        }
    }
}
