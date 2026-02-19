package com.skax.aiplatform.client.lablup.api.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.skax.aiplatform.client.lablup.api.LablupSessionClient;
import com.skax.aiplatform.client.lablup.api.dto.request.GetEndpointRequest;
import com.skax.aiplatform.client.lablup.api.dto.response.GetEndpointResponse;
import com.skax.aiplatform.client.lablup.api.dto.response.GetSessionLogResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Lablup 세션 관리 서비스
 * 
 * <p>
 * Lablup Backend.AI 시스템의 세션(컨테이너) 관리를 위한 비즈니스 로직을 제공합니다.
 * Feign Client를 래핑하여 예외 처리, 로깅, 데이터 변환 등의 공통 기능을 담당합니다.
 * </p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 * <li>컨테이너 로그 조회 및 분석</li>
 * <li>세션 상태 모니터링</li>
 * <li>멀티 노드 세션 관리</li>
 * <li>엔드포인트 정보 조회</li>
 * <li>오류 처리 및 로깅</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LablupSessionService {

    private final LablupSessionClient lablupSessionClient;

    /**
     * 컨테이너 로그 조회
     * 
     * <p>
     * 지정된 세션 ID의 컨테이너 로그를 조회합니다.
     * API 호출 전후로 로깅을 수행하고, 예외 발생 시 적절한 비즈니스 예외로 변환합니다.
     * </p>
     * 
     * <h3>처리 과정:</h3>
     * <ol>
     * <li>요청 파라미터 검증 및 로깅</li>
     * <li>Lablup API 호출</li>
     * <li>응답 데이터 검증 및 로깅</li>
     * <li>예외 발생 시 적절한 변환</li>
     * </ol>
     * 
     * @param sessionId      조회할 세션 ID
     * @param ownerAccessKey 다른 사용자 세션 조회를 위한 해당 사용자의 액세스 키 (선택사항)
     * @param kernelId       멀티 노드 세션에서 특정 서브 컨테이너(커널) ID (선택사항)
     * @return 세션 로그 조회 결과
     * @throws BusinessException 세션을 찾을 수 없거나 API 호출에 실패한 경우
     */
    public GetSessionLogResponse getSessionLog(String sessionId, String ownerAccessKey, String kernelId) {
        try {
            log.info("🔴 Lablup 세션 로그 조회 요청 - sessionId: {}, ownerAccessKey: {}, kernelId: {}",
                    sessionId,
                    ownerAccessKey != null ? "***" : null,
                    kernelId);

            GetSessionLogResponse response = lablupSessionClient.getSessionLog(sessionId, ownerAccessKey, kernelId);

            if (response != null && response.getResult() != null && response.getResult().getLogs() != null) {
                log.info("🔴 Lablup 세션 로그 조회 성공 - sessionId: {}, log: {}",
                        sessionId,
                        response.getResult().getLogs());
            } else {
                log.warn("🔴 Lablup 세션 로그 조회 결과가 비어있음 - sessionId: {}", sessionId);
            }

            return response;

        } catch (BusinessException e) {
            log.error("🔴 Lablup 세션 로그 조회 실패 - sessionId: {}, BusinessException: {}", sessionId, e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🔴 Lablup 세션 로그 조회 실패 - sessionId: {}, 예상치 못한 오류", sessionId, e);

            // 특정 예외 타입에 따른 세분화된 에러 처리
            if (e.getMessage() != null && e.getMessage().contains("404")) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        String.format("세션을 찾을 수 없습니다: %s", sessionId));
            } else if (e.getMessage() != null && e.getMessage().contains("403")) {
                throw new BusinessException(ErrorCode.FORBIDDEN,
                        "세션 로그에 접근할 권한이 없습니다");
            } else if (e.getMessage() != null && e.getMessage().contains("401")) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED,
                        "Lablup API 인증에 실패했습니다");
            }

            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Lablup 세션 로그 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 다른 사용자 세션 로그 조회
     * 
     * <p>
     * 관리자 권한으로 다른 사용자의 세션 로그를 조회합니다.
     * 해당 사용자의 access key를 사용하여 권한을 확인합니다.
     * </p>
     * 
     * @param sessionId      조회할 세션 ID
     * @param ownerAccessKey 해당 사용자의 액세스 키
     * @return 세션 로그 조회 결과
     * @throws BusinessException 권한이 없거나 세션을 찾을 수 없는 경우
     */
    public GetSessionLogResponse getOtherUserSessionLog(String sessionId, String ownerAccessKey) {
        log.debug("다른 사용자 세션 로그 조회 - sessionId: {}", sessionId);
        return getSessionLog(sessionId, ownerAccessKey, null);
    }

    /**
     * 멀티 노드 세션의 특정 커널 로그 조회
     * 
     * <p>
     * 멀티 노드 세션에서 특정 커널(서브 컨테이너)의 로그를 조회합니다.
     * 클러스터 환경에서 개별 노드의 로그를 확인할 때 사용합니다.
     * </p>
     * 
     * @param sessionId 조회할 세션 ID
     * @param kernelId  특정 커널 ID
     * @return 해당 커널의 로그 조회 결과
     * @throws BusinessException 세션이나 커널을 찾을 수 없는 경우
     */
    public GetSessionLogResponse getMultiNodeSessionLog(String sessionId, String kernelId) {
        log.debug("멀티 노드 세션 커널 로그 조회 - sessionId: {}, kernelId: {}", sessionId, kernelId);
        return getSessionLog(sessionId, null, kernelId);
    }

    /**
     * 기본 세션 로그 조회
     * 
     * <p>
     * 추가 옵션 없이 기본적인 세션 로그를 조회합니다.
     * 자신의 단일 노드 세션 로그를 확인할 때 사용합니다.
     * </p>
     * 
     * @param sessionId 조회할 세션 ID
     * @return 세션 로그 조회 결과
     * @throws BusinessException 세션을 찾을 수 없거나 API 호출에 실패한 경우
     */
    public GetSessionLogResponse getBasicSessionLog(String sessionId) {
        log.debug("기본 세션 로그 조회 - sessionId: {}", sessionId);
        return getSessionLog(sessionId, null, null);
    }

    /**
     * 엔드포인트 정보 조회
     * 
     * <p>
     * GraphQL 쿼리를 사용하여 특정 엔드포인트의 상세 정보를 조회합니다.
     * API 호출 전후로 로깅을 수행하고, 예외 발생 시 적절한 비즈니스 예외로 변환합니다.
     * </p>
     * 
     * <h3>처리 과정:</h3>
     * <ol>
     * <li>GraphQL 쿼리 생성</li>
     * <li>엔드포인트 ID를 변수로 설정</li>
     * <li>Lablup API 호출</li>
     * <li>응답 데이터 검증 및 로깅</li>
     * <li>예외 발생 시 적절한 변환</li>
     * </ol>
     * 
     * @param servingId 조회할 엔드포인트 ID (serving_id)
     * @return 엔드포인트 상세 정보
     * @throws BusinessException 엔드포인트를 찾을 수 없거나 API 호출에 실패한 경우
     */
    public GetEndpointResponse getEndpoint(String servingId) {
        try {
            log.info("🔴 Lablup 엔드포인트 조회 요청 - servingId: {}", servingId);

            // GraphQL 쿼리 생성
            String query = """
                    query($endpoint_id: UUID!) {
                        endpoint(endpoint_id: $endpoint_id) {
                            endpoint_id
                            name
                            replicas
                            status
                            image_object {registry project base_image_name tag name}
                            model_definition_path
                            url
                            open_to_public
                            created_user
                            created_at
                            runtime_variant {name}
                            routings {routing_id session status traffic_ratio}
                            resource_slots
                        }
                    }
                    """;

            // GraphQL 변수 설정
            Map<String, Object> variables = new HashMap<>();
            variables.put("endpoint_id", servingId);

            // 요청 생성
            GetEndpointRequest request = GetEndpointRequest.builder()
                    .query(query.trim())
                    .variables(variables)
                    .build();

            // API 호출
            GetEndpointResponse response = lablupSessionClient.getEndpoint(request);

            if (response != null && response.getEndpoint() != null) {
                log.info("🔴 Lablup 엔드포인트 조회 성공 - servingId: {}, name: {}, status: {}",
                        servingId,
                        response.getEndpoint().getName(),
                        response.getEndpoint().getStatus());
            } else {
                log.warn("🔴 Lablup 엔드포인트 조회 결과가 비어있음 - servingId: {}", servingId);
            }

            // response null 체크 후 GraphQL 오류 체크
            if (response != null && response.getErrors() != null && !response.getErrors().isEmpty()) {
                log.error("🔴 Lablup GraphQL 쿼리 오류 - servingId: {}, errors: {}", servingId, response.getErrors());
                throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                        "Lablup GraphQL 쿼리 오류: " + response.getErrors().get(0).getMessage());
            }

            return response;

        } catch (BusinessException e) {
            log.error("🔴 Lablup 엔드포인트 조회 실패 - servingId: {}, BusinessException: {}", servingId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("🔴 Lablup 엔드포인트 조회 실패 - servingId: {}, 예상치 못한 오류", servingId, e);

            if (e.getMessage() != null && e.getMessage().contains("404")) {
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        String.format("엔드포인트를 찾을 수 없습니다: %s", servingId));
            } else if (e.getMessage() != null && e.getMessage().contains("403")) {
                throw new BusinessException(ErrorCode.FORBIDDEN,
                        "엔드포인트 정보에 접근할 권한이 없습니다");
            } else if (e.getMessage() != null && e.getMessage().contains("401")) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED,
                        "Lablup API 인증에 실패했습니다");
            }

            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "Lablup 엔드포인트 조회에 실패했습니다: " + e.getMessage());
        }
    }
}