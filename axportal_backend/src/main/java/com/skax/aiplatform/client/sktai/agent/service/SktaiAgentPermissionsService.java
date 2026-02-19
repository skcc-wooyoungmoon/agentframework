package com.skax.aiplatform.client.sktai.agent.service;

import com.skax.aiplatform.client.sktai.agent.SktaiAgentPermissionsClient;
import com.skax.aiplatform.client.sktai.agent.dto.request.PermissionCreateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.response.PermissionsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PermissionCreateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.PermissionResponse;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * SKTAI Agent Permissions API 서비스
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiAgentPermissionsService {
    
    private final SktaiAgentPermissionsClient sktaiAgentPermissionsClient;
    
    public PermissionsResponse getPermissions(Integer page, Integer size, String sort, String filter, String search) {
        try {
            log.debug("Permissions 목록 조회 요청 - page: {}, size: {}", page, size);
            PermissionsResponse response = sktaiAgentPermissionsClient.getPermissions(page, size, sort, filter, search);
            log.debug("Permissions 목록 조회 성공");
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Permissions 목록 조회 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Permissions 목록 조회 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Permissions 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    public PermissionCreateResponse createPermission(PermissionCreateRequest request) {
        try {
            log.debug("Permission 생성 요청");
            PermissionCreateResponse response = sktaiAgentPermissionsClient.createPermission(request);
            log.debug("Permission 생성 성공");
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Permission 생성 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Permission 생성 실패", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Permission 생성에 실패했습니다: " + e.getMessage());
        }
    }
    
    public PermissionResponse getPermission(String permissionId) {
        try {
            log.debug("Permission 상세 조회 요청 - permissionId: {}", permissionId);
            PermissionResponse response = sktaiAgentPermissionsClient.getPermission(permissionId);
            log.debug("Permission 상세 조회 성공 - permissionId: {}", permissionId);
            return response;
        } catch (BusinessException e) {
            // BusinessException인 경우 SktaiErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("Permission 상세 조회 실패 (BusinessException) - permissionId: {}, message: {}", 
                    permissionId, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("Permission 상세 조회 실패 - permissionId: {}", permissionId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "Permission 조회에 실패했습니다: " + e.getMessage());
        }
    }
}
