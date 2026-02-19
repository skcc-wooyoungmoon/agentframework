package com.skax.aiplatform.client.ione.ratelimit.service;

import com.skax.aiplatform.client.ione.ratelimit.IoneRatelimitClient;
import com.skax.aiplatform.client.ione.ratelimit.dto.request.IntfApiKeyPolicyConfigRequest;
import com.skax.aiplatform.client.ione.ratelimit.dto.request.IntfApiKeyPolicyReplenishRequest;
import com.skax.aiplatform.client.ione.ratelimit.dto.request.IntfRateLimitPolicyRequest;
import com.skax.aiplatform.client.ione.ratelimit.dto.response.IntfRatelimitPolicyVo;
import com.skax.aiplatform.client.ione.ratelimit.dto.response.IntfRatelimitUpdateResult;
import com.skax.aiplatform.client.ione.ratelimit.dto.response.PaginatedPolicyResult;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * iONE Ratelimit 서비스
 * 
 * <p>iONE Ratelimit 클라이언트를 래핑하여 비즈니스 로직과 예외 처리를 담당하는 서비스입니다.
 * Ratelimit 정책 관리 관련 5개 API에 대한 서비스 메서드를 제공합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-17
 * @version 2.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IoneRatelimitService {

    private final IoneRatelimitClient ioneRatelimitClient;

    /**
     * Ratelimit정책 추가/수정/삭제
     */
    public IntfRatelimitUpdateResult updatePolicy(IntfRateLimitPolicyRequest request) {
        try {
            log.info("🟣 iONE Ratelimit 정책 업데이트 요청 - operation: {}, policyId: {}", 
                    request.getOperation(), request.getPolicyId());
            IntfRatelimitUpdateResult result = ioneRatelimitClient.updatePolicy(request);
            log.info("🟣 iONE Ratelimit 정책 업데이트 성공 - success: {}, policyId: {}", 
                    result.getSuccess(), request.getPolicyId());
            return result;
        } catch (BusinessException e) {
            log.error("🟣 iONE Ratelimit 정책 업데이트 실패 - operation: {}, policyId: {}, BusinessException: {}", 
                    request.getOperation(), request.getPolicyId(), e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE Ratelimit 정책 업데이트 실패 - operation: {}, policyId: {}, 예상치 못한 오류", 
                    request.getOperation(), request.getPolicyId(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "Ratelimit 정책 업데이트에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * API KEY 정책 추가/수정/삭제
     */
    public IntfRatelimitUpdateResult configApiKeyPolicy(IntfApiKeyPolicyConfigRequest request) {
        try {
            log.info("🟣 iONE API KEY 정책 설정 요청 - operation: {}, openApiKey: {}, policyId: {}", 
                    request.getOperation(), request.getOpenApiKey(), request.getPolicyId());
            IntfRatelimitUpdateResult result = ioneRatelimitClient.configApiKeyPolicy(request);
            log.info("🟣 iONE API KEY 정책 설정 성공 - success: {}, openApiKey: {}", 
                    result.getSuccess(), request.getOpenApiKey());
            return result;
        } catch (BusinessException e) {
            log.error("🟣 iONE API KEY 정책 설정 실패 - operation: {}, openApiKey: {}, policyId: {}, BusinessException: {}", 
                    request.getOperation(), request.getOpenApiKey(), request.getPolicyId(), e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE API KEY 정책 설정 실패 - operation: {}, openApiKey: {}, policyId: {}, 예상치 못한 오류", 
                    request.getOperation(), request.getOpenApiKey(), request.getPolicyId(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "API KEY 정책 설정에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * API KEY 정책 limit 충전
     */
    public IntfRatelimitUpdateResult replenishApiKeyPolicy(IntfApiKeyPolicyReplenishRequest request) {
        try {
            log.info("🟣 iONE API KEY 정책 limit 충전 요청 - openApiKey: {}, replenishCount: {}", 
                    request.getOpenApiKey(), request.getReplenishCount());
            IntfRatelimitUpdateResult result = ioneRatelimitClient.replenishApiKeyPolicy(request);
            log.info("🟣 iONE API KEY 정책 limit 충전 성공 - success: {}, openApiKey: {}", 
                    result.getSuccess(), request.getOpenApiKey());
            return result;
        } catch (BusinessException e) {
            log.error("🟣 iONE API KEY 정책 limit 충전 실패 - openApiKey: {}, replenishCount: {}, BusinessException: {}", 
                    request.getOpenApiKey(), request.getReplenishCount(), e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE API KEY 정책 limit 충전 실패 - openApiKey: {}, replenishCount: {}, 예상치 못한 오류", 
                    request.getOpenApiKey(), request.getReplenishCount(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "API KEY 정책 limit 충전에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Ratelimit정책 목록 조회
     */
    public List<IntfRatelimitPolicyVo> selectPolicyList() {
        try {
            log.info("🟣 iONE Ratelimit 정책 목록 조회 요청");
            List<IntfRatelimitPolicyVo> result = ioneRatelimitClient.selectPolicyList();
            log.info("🟣 iONE Ratelimit 정책 목록 조회 성공 - 조회 건수: {}", result.size());
            return result;
        } catch (BusinessException e) {
            log.error("🟣 iONE Ratelimit 정책 목록 조회 실패 - BusinessException: {}", e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE Ratelimit 정책 목록 조회 실패 - 예상치 못한 오류", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "Ratelimit 정책 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Ratelimit정책 목록 Pagination 조회
     */
    public PaginatedPolicyResult getPolicyWithPagination(Integer pageNum, Integer pageSize, String policyId) {
        try {
            log.info("🟣 iONE Ratelimit 정책 Pagination 조회 요청 - pageNum: {}, pageSize: {}, policyId: {}", 
                    pageNum, pageSize, policyId);
            PaginatedPolicyResult result = ioneRatelimitClient.getPolicyWithPagination(pageNum, pageSize, policyId);
            log.info("🟣 iONE Ratelimit 정책 Pagination 조회 성공 - 조회 건수: {}", 
                    result.getListCount());
            return result;
        } catch (BusinessException e) {
            log.error("🟣 iONE Ratelimit 정책 Pagination 조회 실패 - pageNum: {}, pageSize: {}, policyId: {}, BusinessException: {}", 
                    pageNum, pageSize, policyId, e.getMessage());
            throw e; // ErrorDecoder에서 생성된 BusinessException 그대로 전파
        } catch (Exception e) {
            log.error("🟣 iONE Ratelimit 정책 Pagination 조회 실패 - pageNum: {}, pageSize: {}, policyId: {}, 예상치 못한 오류", 
                    pageNum, pageSize, policyId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                    "Ratelimit 정책 Pagination 조회에 실패했습니다: " + e.getMessage());
        }
    }
}
