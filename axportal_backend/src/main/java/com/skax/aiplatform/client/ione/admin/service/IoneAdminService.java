package com.skax.aiplatform.client.ione.admin.service;

import com.skax.aiplatform.client.ione.admin.IoneAdminClient;
import com.skax.aiplatform.client.ione.admin.dto.request.IntfAdminUserCreateRequest;
import com.skax.aiplatform.client.ione.admin.dto.request.IntfAdminUserUpdateRequest;
import com.skax.aiplatform.client.ione.admin.dto.request.IntfAdminUserDeleteRequest;
import com.skax.aiplatform.client.ione.admin.dto.response.IntfAdminUserVo;
import com.skax.aiplatform.client.ione.admin.dto.response.IntfAdminUserDetailVo;
import com.skax.aiplatform.client.ione.admin.dto.response.IntfAdminUserResult;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * iONE 어드민 사용자 관리 서비스
 * 
 * <p>iONE 어드민 사용자 관리 클라이언트를 래핑하여 비즈니스 로직과 예외 처리를 담당하는 서비스입니다.
 * 어드민 사용자 관련 5개 API에 대한 서비스 메서드를 제공합니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IoneAdminService {
    
    private final IoneAdminClient ioneAdminClient;
    
    /**
     * [API-ADM-001] 사용자 목록 조회
     * 
     * <p>등록된 모든 어드민 사용자 목록을 조회합니다.</p>
     * 
     * @return 어드민 사용자 목록
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public List<IntfAdminUserVo> getAdminUserList() {
        try {
            log.info("iONE 어드민 사용자 목록 조회 요청");
            List<IntfAdminUserVo> result = ioneAdminClient.getAdminUserList();
            log.info("iONE 어드민 사용자 목록 조회 성공 - 조회 건수: {}", result.size());
            return result;
        } catch (BusinessException e) {
            // BusinessException인 경우 IoneErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("iONE 어드민 사용자 목록 조회 실패 (BusinessException) - message: {}", e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("iONE 어드민 사용자 목록 조회 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "어드민 사용자 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * [API-ADM-002] 사용자 정보 조회
     * 
     * <p>특정 어드민 사용자의 상세 정보를 조회합니다.</p>
     * 
     * @param id 조회할 사용자 ID
     * @return 어드민 사용자 상세 정보
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public IntfAdminUserDetailVo getAdminUserDetails(String id) {
        try {
            log.info("iONE 어드민 사용자 상세 정보 조회 요청 - userId: {}", id);
            IntfAdminUserDetailVo result = ioneAdminClient.getAdminUserDetails(id);
            log.info("iONE 어드민 사용자 상세 정보 조회 성공 - userId: {}", id);
            return result;
        } catch (BusinessException e) {
            // BusinessException인 경우 IoneErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("iONE 어드민 사용자 상세 정보 조회 실패 (BusinessException) - userId: {}, message: {}", id, e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("iONE 어드민 사용자 상세 정보 조회 실패 (예상치 못한 오류) - userId: {}", id, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "어드민 사용자 상세 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * [API-ADM-003] 관리자 생성
     * 
     * <p>새로운 어드민 사용자를 생성합니다.
     * 사용자 기본 정보와 권한 정보를 설정할 수 있습니다.</p>
     * 
     * @param request 관리자 생성 요청
     * @return 관리자 생성 결과
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public IntfAdminUserResult createAdminUser(IntfAdminUserCreateRequest request) {
        try {
            log.info("iONE 어드민 사용자 생성 요청 - userId: {}, userName: {}, roleCode: {}", 
                    request.getUserId(), request.getUserName(), request.getRoleCode());
            IntfAdminUserResult result = ioneAdminClient.createAdminUser(request);
            log.info("iONE 어드민 사용자 생성 성공 - success: {}, userId: {}", 
                    result.getSuccess(), result.getUserId());
            return result;
        } catch (BusinessException e) {
            // BusinessException인 경우 IoneErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("iONE 어드민 사용자 생성 실패 (BusinessException) - userId: {}, userName: {}, message: {}", 
                    request.getUserId(), request.getUserName(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("iONE 어드민 사용자 생성 실패 (예상치 못한 오류) - userId: {}, userName: {}", 
                    request.getUserId(), request.getUserName(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "어드민 사용자 생성에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * [API-ADM-004] 관리자 수정
     * 
     * <p>기존 어드민 사용자의 정보를 수정합니다.
     * 사용자 정보, 권한, 상태 등을 변경할 수 있습니다.</p>
     * 
     * @param request 관리자 수정 요청
     * @return 관리자 수정 결과
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public IntfAdminUserResult updateAdminUser(IntfAdminUserUpdateRequest request) {
        try {
            log.info("iONE 어드민 사용자 수정 요청 - userId: {}, userName: {}, roleCode: {}", 
                    request.getUserId(), request.getUserName(), request.getRoleCode());
            IntfAdminUserResult result = ioneAdminClient.updateAdminUser(request);
            log.info("iONE 어드민 사용자 수정 성공 - success: {}, userId: {}", 
                    result.getSuccess(), result.getUserId());
            return result;
        } catch (BusinessException e) {
            // BusinessException인 경우 IoneErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("iONE 어드민 사용자 수정 실패 (BusinessException) - userId: {}, userName: {}, message: {}", 
                    request.getUserId(), request.getUserName(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("iONE 어드민 사용자 수정 실패 (예상치 못한 오류) - userId: {}, userName: {}", 
                    request.getUserId(), request.getUserName(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "어드민 사용자 수정에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * [API-ADM-005] 관리자 삭제
     * 
     * <p>어드민 사용자를 삭제합니다.
     * 실제 삭제 또는 비활성화 처리가 가능합니다.</p>
     * 
     * @param request 관리자 삭제 요청
     * @return 관리자 삭제 결과
     * @throws BusinessException 외부 API 호출 실패 시
     */
    public IntfAdminUserResult deleteAdminUser(IntfAdminUserDeleteRequest request) {
        try {
            log.info("iONE 어드민 사용자 삭제 요청 - userId: {}, deleteType: {}", 
                    request.getUserId(), request.getDeleteType());
            IntfAdminUserResult result = ioneAdminClient.deleteAdminUser(request);
            log.info("iONE 어드민 사용자 삭제 성공 - success: {}, userId: {}", 
                    result.getSuccess(), result.getUserId());
            return result;
        } catch (BusinessException e) {
            // BusinessException인 경우 IoneErrorDecoder에서 이미 상세한 메시지가 설정됨
            log.error("iONE 어드민 사용자 삭제 실패 (BusinessException) - userId: {}, deleteType: {}, message: {}", 
                    request.getUserId(), request.getDeleteType(), e.getMessage());
            throw e; // 원본 예외를 그대로 전파하여 상세 메시지 유지
        } catch (Exception e) {
            log.error("iONE 어드민 사용자 삭제 실패 (예상치 못한 오류) - userId: {}, deleteType: {}", 
                    request.getUserId(), request.getDeleteType(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "어드민 사용자 삭제에 실패했습니다: " + e.getMessage());
        }
    }
}