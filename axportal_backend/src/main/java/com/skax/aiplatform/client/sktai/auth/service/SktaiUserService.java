package com.skax.aiplatform.client.sktai.auth.service;

import com.skax.aiplatform.client.sktai.auth.SktaiUserClient;
import com.skax.aiplatform.client.sktai.auth.dto.request.RegisterUserPayload;
import com.skax.aiplatform.client.sktai.auth.dto.request.UpdateUserPayload;
import com.skax.aiplatform.client.sktai.auth.dto.request.UserRoleMappingUpdateItemDto;
import com.skax.aiplatform.client.sktai.auth.dto.response.*;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.mapper.auth.UserRoleMappingMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * SKTAI 사용자 관리 비즈니스 로직 서비스
 * 
 * <p>SKTAI Auth API의 사용자 관리 기능을 위한 완전한 비즈니스 로직을 제공하는 서비스입니다.
 * Feign Client를 래핑하여 예외 처리, 로깅, 데이터 변환 등의 추가 기능을 제공합니다.</p>
 * 
 * <h3>주요 기능:</h3>
 * <ul>
 *   <li><strong>현재 사용자 관리</strong>: 로그인한 사용자 정보 조회</li>
 *   <li><strong>사용자 CRUD</strong>: 생성, 조회, 수정, 삭제</li>
 *   <li><strong>사용자 조회</strong>: 시스템 사용자 목록 조회 및 검색</li>
 *   <li><strong>필터링</strong>: 활성 상태, 역할 기반 사용자 필터링</li>
 *   <li><strong>검색</strong>: 사용자명, 이메일 기반 검색</li>
 *   <li><strong>페이징</strong>: 대용량 사용자 데이터 효율적 처리</li>
 *   <li><strong>통합 에러 처리</strong>: 외부 API 오류를 내부 예외로 변환</li>
 *   <li><strong>상세 로깅</strong>: 모든 API 호출과 결과를 추적</li>
 * </ul>
 * 
 * <h3>사용자 생명주기 관리:</h3>
 * <ul>
 *   <li><strong>계정 생성</strong>: 새로운 사용자 계정 생성</li>
 *   <li><strong>프로필 관리</strong>: 사용자 정보 수정</li>
 *   <li><strong>권한 관리</strong>: 역할 및 그룹 할당</li>
 *   <li><strong>계정 비활성화</strong>: 사용자 계정 삭제/비활성화</li>
 * </ul>
 * 
 * <h3>검색 기능:</h3>
 * <ul>
 *   <li><strong>사용자명 검색</strong>: username 필드 기반 부분 일치</li>
 *   <li><strong>이메일 검색</strong>: email 필드 기반 부분 일치</li>
 *   <li><strong>전체명 검색</strong>: full_name 필드 기반 부분 일치</li>
 *   <li><strong>통합 검색</strong>: 여러 필드에서 동시 검색</li>
 * </ul>
 * 
 * <h3>에러 처리 전략:</h3>
 * <ul>
 *   <li><strong>401/403 오류</strong>: 인증/권한 관련 BusinessException 발생</li>
 *   <li><strong>422 오류</strong>: 유효성 검증 실패 BusinessException 발생</li>
 *   <li><strong>404 오류</strong>: 사용자 없음 BusinessException 발생</li>
 *   <li><strong>기타 오류</strong>: 일반적인 외부 API 오류로 처리</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * // 현재 사용자 정보 조회
 * MeResponse currentUser = sktaiUserService.getCurrentUser();
 * 
 * // 새 사용자 등록
 * RegisterUserPayload request = RegisterUserPayload.builder()
 *     .username("john.doe")
 *     .email("john@company.com")
 *     .build();
 * UserRepresentation newUser = sktaiUserService.registerUser(request);
 * 
 * // 기본 사용자 목록 조회
 * UsersRead users = sktaiUserService.getUsers(1, 20, null, null, null);
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-08-16
 * @version 2.0
 * @see SktaiUserClient 사용자 관리 Feign Client
 * @see MeResponse 현재 사용자 정보 DTO
 * @see UserBase 사용자 상세 정보 DTO
 * @see UsersRead 사용자 목록 응답 DTO
 * @see RegisterUserPayload 사용자 등록 요청 DTO
 * @see UpdateUserPayload 사용자 수정 요청 DTO
 * @see UserRepresentation 사용자 등록 응답 DTO
 * @see ErrorCode 에러 코드 정의
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SktaiUserService {
    
    private final SktaiUserClient sktaiUserClient;
    private final UserRoleMappingMapper userRoleMappingMapper;
    
    /**
     * 현재 로그인한 사용자의 정보를 조회합니다.
     * 
     * <p>JWT 토큰을 통해 인증된 현재 사용자의 상세 정보를 SKTAI Auth API에서 조회합니다.
     * 사용자 프로필, 권한, 그룹 정보 등이 포함된 완전한 사용자 데이터를 반환합니다.</p>
     * 
     * <h3>주요 기능:</h3>
     * <ul>
     *   <li><strong>토큰 기반 인증</strong>: Authorization 헤더의 JWT 토큰 자동 사용</li>
     *   <li><strong>완전한 프로필</strong>: 사용자 기본정보, 권한, 그룹 정보 모두 포함</li>
     *   <li><strong>실시간 정보</strong>: 최신 사용자 상태 반영</li>
     * </ul>
     * 
     * <h3>응답 정보:</h3>
     * <ul>
     *   <li><strong>기본 정보</strong>: username, email, full_name 등</li>
     *   <li><strong>계정 상태</strong>: 활성/비활성 상태</li>
     *   <li><strong>권한 정보</strong>: 할당된 역할과 권한</li>
     *   <li><strong>그룹 정보</strong>: 소속 그룹 목록</li>
     *   <li><strong>생성/수정 시간</strong>: 계정 관리 이력</li>
     * </ul>
     * 
     * <h3>에러 처리:</h3>
     * <ul>
     *   <li><strong>401 Unauthorized</strong>: 유효하지 않은 토큰, 토큰 만료</li>
     *   <li><strong>403 Forbidden</strong>: 사용자 계정 비활성화, 권한 부족</li>
     *   <li><strong>404 Not Found</strong>: 사용자 계정 삭제됨</li>
     * </ul>
     * 
     * <h3>사용 시나리오:</h3>
     * <ul>
     *   <li><strong>프로필 화면</strong>: 사용자 대시보드에서 현재 사용자 정보 표시</li>
     *   <li><strong>권한 확인</strong>: 현재 사용자의 권한 기반 기능 제어</li>
     *   <li><strong>감사 로그</strong>: 작업 수행자 정보 기록</li>
     *   <li><strong>개인화</strong>: 사용자별 맞춤 설정 적용</li>
     * </ul>
     * 
     * @return 현재 사용자의 상세 정보 (기본정보, 권한, 그룹 포함)
     * @throws BusinessException 인증 실패, 사용자 조회 실패 시
     * @apiNote JWT 토큰이 Authorization 헤더에 포함되어야 함
     * @implNote Feign Client의 RequestInterceptor에서 자동으로 토큰을 헤더에 추가
     * @since 2.0
     */
    public MeResponse getCurrentUser() {
        log.debug("현재 사용자 정보 조회 요청");
        
        try {
            MeResponse currentUser = sktaiUserClient.getCurrentUser();
            log.debug("현재 사용자 정보 조회 성공 - username: {}, email: {}", 
                     currentUser.getUsername(), currentUser.getEmail());
            return currentUser;
        } catch (BusinessException e) {
            log.error("현재 사용자 정보 조회 실패 (BusinessException) - message: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("현재 사용자 정보 조회 실패 (예상치 못한 오류)", e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "현재 사용자 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 특정 사용자의 상세 정보를 조회합니다.
     * 
     * <p>사용자 ID를 통해 특정 사용자의 완전한 프로필 정보를 SKTAI Auth API에서 조회합니다.
     * 관리자나 적절한 권한을 가진 사용자만 다른 사용자의 정보를 조회할 수 있습니다.</p>
     * 
     * <h3>주요 기능:</h3>
     * <ul>
     *   <li><strong>개별 사용자 조회</strong>: UUID를 통한 정확한 사용자 식별</li>
     *   <li><strong>완전한 프로필</strong>: 사용자 기본정보, 권한, 그룹 정보 모두 포함</li>
     *   <li><strong>권한 기반 접근</strong>: 적절한 권한 확인 후 정보 제공</li>
     * </ul>
     * 
     * <h3>응답 정보:</h3>
     * <ul>
     *   <li><strong>기본 정보</strong>: username, email, full_name 등</li>
     *   <li><strong>계정 상태</strong>: 활성/비활성 상태</li>
     *   <li><strong>권한 정보</strong>: 할당된 역할과 권한</li>
     *   <li><strong>그룹 정보</strong>: 소속 그룹 목록</li>
     *   <li><strong>생성/수정 시간</strong>: 계정 관리 이력</li>
     * </ul>
     * 
     * <h3>권한 요구사항:</h3>
     * <ul>
     *   <li><strong>본인 정보</strong>: 모든 사용자가 자신의 정보 조회 가능</li>
     *   <li><strong>타인 정보</strong>: 관리자 권한 또는 특별 권한 필요</li>
     *   <li><strong>민감 정보</strong>: 권한에 따라 일부 정보 제한될 수 있음</li>
     * </ul>
     * 
     * <h3>에러 처리:</h3>
     * <ul>
     *   <li><strong>401 Unauthorized</strong>: 유효하지 않은 토큰</li>
     *   <li><strong>403 Forbidden</strong>: 해당 사용자 조회 권한 부족</li>
     *   <li><strong>404 Not Found</strong>: 존재하지 않는 사용자 ID</li>
     * </ul>
     * 
     * <h3>사용 시나리오:</h3>
     * <ul>
     *   <li><strong>사용자 관리</strong>: 관리자가 특정 사용자 정보 확인</li>
     *   <li><strong>프로필 조회</strong>: 사용자 상세 페이지 표시</li>
     *   <li><strong>권한 확인</strong>: 특정 사용자의 권한 검증</li>
     *   <li><strong>감사 조회</strong>: 사용자 계정 상태 확인</li>
     * </ul>
     * 
     * @param userId 조회할 사용자의 고유 식별자 (UUID)
     * @return 요청한 사용자의 상세 정보 (기본정보, 권한, 그룹 포함)
     * @throws BusinessException 사용자 조회 실패, 권한 부족, 사용자 없음 시
     * @apiNote 유효한 UUID 형식의 사용자 ID가 필요함
     * @implNote 권한 검증은 SKTAI Auth API에서 수행됨
     * @since 2.0
     */
    public UserBase getUser(String userId) {
        log.debug("사용자 상세 정보 조회 요청 - userId: {}", userId);
        
        try {
            UserBase user = sktaiUserClient.getUser(userId);
            log.debug("사용자 상세 정보 조회 성공 - userId: {}, username: {}", 
                     userId, user.getUsername());
            return user;
        } catch (BusinessException e) {
            log.error("사용자 상세 정보 조회 실패 (BusinessException) - userId: {}, message: {}", 
                     userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("사용자 상세 정보 조회 실패 (예상치 못한 오류) - userId: {}", userId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "사용자 상세 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 새로운 사용자를 생성합니다.
     * 
     * <p>제공된 사용자 정보를 바탕으로 SKTAI Auth 시스템에 새로운 사용자 계정을 생성합니다.
     * 관리자 권한이 있는 사용자만 새로운 사용자를 생성할 수 있습니다.</p>
     * 
     * <h3>주요 기능:</h3>
     * <ul>
     *   <li><strong>계정 생성</strong>: 고유한 사용자명과 이메일로 계정 생성</li>
     *   <li><strong>유효성 검증</strong>: 사용자 정보 형식 및 중복 검증</li>
     *   <li><strong>권한 설정</strong>: 기본 권한 및 그룹 할당</li>
     *   <li><strong>즉시 활성화</strong>: 생성과 동시에 활성 상태로 설정</li>
     * </ul>
     * 
     * <h3>필수 정보:</h3>
     * <ul>
     *   <li><strong>username</strong>: 고유한 사용자명 (3-50자)</li>
     *   <li><strong>email</strong>: 유효한 이메일 주소</li>
     *   <li><strong>password</strong>: 보안 정책에 맞는 비밀번호</li>
     *   <li><strong>full_name</strong>: 사용자 전체 이름</li>
     * </ul>
     * 
     * <h3>선택적 정보:</h3>
     * <ul>
     *   <li><strong>roles</strong>: 할당할 역할 목록</li>
     *   <li><strong>groups</strong>: 소속시킬 그룹 목록</li>
     *   <li><strong>attributes</strong>: 추가 사용자 속성</li>
     * </ul>
     * 
     * <h3>권한 요구사항:</h3>
     * <ul>
     *   <li><strong>관리자 권한</strong>: USER_ADMIN 또는 SYSTEM_ADMIN 역할 필요</li>
     *   <li><strong>그룹 관리자</strong>: 특정 그룹의 관리자 권한으로 해당 그룹 사용자만 생성 가능</li>
     * </ul>
     * 
     * <h3>에러 처리:</h3>
     * <ul>
     *   <li><strong>400 Bad Request</strong>: 잘못된 사용자 정보 형식</li>
     *   <li><strong>401 Unauthorized</strong>: 인증되지 않은 요청</li>
     *   <li><strong>403 Forbidden</strong>: 사용자 생성 권한 부족</li>
     *   <li><strong>409 Conflict</strong>: 중복된 사용자명 또는 이메일</li>
     *   <li><strong>422 Unprocessable Entity</strong>: 유효성 검증 실패</li>
     * </ul>
     * 
     * <h3>사용 시나리오:</h3>
     * <ul>
     *   <li><strong>신규 직원 등록</strong>: 조직에 새로 합류한 직원 계정 생성</li>
     *   <li><strong>서비스 계정</strong>: 시스템 간 연동을 위한 서비스 계정 생성</li>
     *   <li><strong>임시 계정</strong>: 외부 협력업체나 임시 사용자 계정 생성</li>
     *   <li><strong>테스트 계정</strong>: 개발 및 테스트 목적의 계정 생성</li>
     * </ul>
     * 
     * @param request 등록할 사용자 정보 (필수: username, email, password, first_name, last_name)
     * @return 등록된 사용자의 상세 정보 (ID, 기본정보, 할당된 권한 포함)
     * @throws BusinessException 사용자 등록 실패, 권한 부족, 중복 정보, 유효성 검증 실패 시
     * @apiNote 등록 요청자는 관리자 권한을 가져야 함
     * @implNote 비밀번호는 자동으로 암호화되어 저장됨
     * @since 2.0
     */
    public UserRepresentation registerUser(RegisterUserPayload request) {
        log.debug("새 사용자 등록 요청 - username: {}, email: {}", 
                 request.getUsername(), request.getEmail());
        
        try {
            UserRepresentation newUser = sktaiUserClient.registerUser(request);
            log.info("새 사용자 생성 성공 - userId: {}, username: {}", 
                    newUser.getId(), newUser.getUsername());
            return newUser;
        } catch (BusinessException e) {
            log.error("새 사용자 생성 실패 (BusinessException) - username: {}, email: {}, message: {}", 
                     request.getUsername(), request.getEmail(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("새 사용자 생성 실패 (예상치 못한 오류) - username: {}, email: {}", 
                     request.getUsername(), request.getEmail(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "새 사용자 생성에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 기존 사용자 정보를 수정합니다.
     * 
     * <p>지정된 사용자 ID의 계정 정보를 업데이트합니다.
     * 사용자는 본인의 정보를 수정할 수 있으며, 관리자는 모든 사용자의 정보를 수정할 수 있습니다.</p>
     * 
     * <h3>주요 기능:</h3>
     * <ul>
     *   <li><strong>프로필 수정</strong>: 이름, 이메일, 개인정보 업데이트</li>
     *   <li><strong>비밀번호 변경</strong>: 보안 정책에 맞는 새 비밀번호 설정</li>
     *   <li><strong>권한 변경</strong>: 역할 및 그룹 할당/해제 (관리자만)</li>
     *   <li><strong>계정 상태 변경</strong>: 활성/비활성 상태 토글 (관리자만)</li>
     *   <li><strong>부분 업데이트</strong>: 변경할 필드만 선택적으로 업데이트</li>
     * </ul>
     * 
     * <h3>수정 가능한 정보:</h3>
     * <ul>
     *   <li><strong>email</strong>: 새로운 이메일 주소 (중복 검증)</li>
     *   <li><strong>full_name</strong>: 사용자 전체 이름</li>
     *   <li><strong>password</strong>: 새로운 비밀번호</li>
     *   <li><strong>enabled</strong>: 계정 활성/비활성 상태 (관리자만)</li>
     *   <li><strong>roles</strong>: 할당된 역할 목록 (관리자만)</li>
     *   <li><strong>groups</strong>: 소속 그룹 목록 (관리자만)</li>
     *   <li><strong>attributes</strong>: 추가 사용자 속성</li>
     * </ul>
     * 
     * <h3>권한 요구사항:</h3>
     * <ul>
     *   <li><strong>본인 정보</strong>: 모든 사용자가 자신의 기본 정보 수정 가능</li>
     *   <li><strong>타인 정보</strong>: 관리자 권한 필요</li>
     *   <li><strong>권한 변경</strong>: USER_ADMIN 또는 SYSTEM_ADMIN 역할 필요</li>
     *   <li><strong>계정 상태</strong>: 관리자만 활성/비활성 상태 변경 가능</li>
     * </ul>
     * 
     * <h3>에러 처리:</h3>
     * <ul>
     *   <li><strong>400 Bad Request</strong>: 잘못된 사용자 정보 형식</li>
     *   <li><strong>401 Unauthorized</strong>: 인증되지 않은 요청</li>
     *   <li><strong>403 Forbidden</strong>: 해당 사용자 수정 권한 부족</li>
     *   <li><strong>404 Not Found</strong>: 존재하지 않는 사용자 ID</li>
     *   <li><strong>409 Conflict</strong>: 중복된 이메일</li>
     *   <li><strong>422 Unprocessable Entity</strong>: 유효성 검증 실패</li>
     * </ul>
     * 
     * <h3>사용 시나리오:</h3>
     * <ul>
     *   <li><strong>프로필 업데이트</strong>: 사용자가 본인의 프로필 정보 수정</li>
     *   <li><strong>비밀번호 변경</strong>: 보안을 위한 정기적 비밀번호 변경</li>
     *   <li><strong>권한 조정</strong>: 관리자가 사용자 권한 및 그룹 조정</li>
     *   <li><strong>계정 관리</strong>: 관리자가 사용자 계정 상태 관리</li>
     * </ul>
     * 
     * @param userId 수정할 사용자의 고유 식별자 (UUID)
     * @param request 수정할 사용자 정보 (변경할 필드만 포함)
     * @return 수정된 사용자의 상세 정보 (최신 상태 반영)
     * @throws BusinessException 사용자 수정 실패, 권한 부족, 사용자 없음, 유효성 검증 실패 시
     * @apiNote 요청자는 본인이거나 관리자 권한을 가져야 함
     * @implNote null이 아닌 필드만 업데이트됨 (부분 업데이트)
     * @since 2.0
     */
    public UserBase updateUser(String userId, UpdateUserPayload request) {
        log.debug("사용자 정보 수정 요청 - userId: {}, email: {}", 
                 userId, request.getEmail());
        
        try {
            UserBase updatedUser = sktaiUserClient.updateUser(userId, request);
            log.info("사용자 정보 수정 성공 - userId: {}, username: {}", 
                    userId, updatedUser.getUsername());
            return updatedUser;
        } catch (BusinessException e) {
            log.error("사용자 정보 수정 실패 (BusinessException) - userId: {}, message: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("사용자 정보 수정 실패 (예상치 못한 오류) - userId: {}", userId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "사용자 정보 수정에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 사용자 계정을 삭제합니다.
     * 
     * <p>지정된 사용자 ID의 계정을 시스템에서 완전히 삭제합니다.
     * 이 작업은 되돌릴 수 없으므로 매우 신중하게 수행해야 하며, 관리자 권한이 필요합니다.</p>
     * 
     * <h3>주요 기능:</h3>
     * <ul>
     *   <li><strong>완전 삭제</strong>: 사용자 계정과 관련 데이터 영구 삭제</li>
     *   <li><strong>권한 정리</strong>: 할당된 모든 역할과 그룹에서 자동 제거</li>
     *   <li><strong>세션 무효화</strong>: 해당 사용자의 모든 활성 세션 즉시 종료</li>
     *   <li><strong>감사 로그</strong>: 삭제 작업에 대한 상세 로그 기록</li>
     * </ul>
     * 
     * <h3>삭제 대상 데이터:</h3>
     * <ul>
     *   <li><strong>사용자 기본 정보</strong>: username, email, full_name 등</li>
     *   <li><strong>인증 정보</strong>: 비밀번호, 토큰 등</li>
     *   <li><strong>권한 할당</strong>: 모든 역할과 그룹 매핑</li>
     *   <li><strong>세션 정보</strong>: 활성 로그인 세션</li>
     *   <li><strong>사용자 속성</strong>: 추가 사용자 정의 속성</li>
     * </ul>
     * 
     * <h3>권한 요구사항:</h3>
     * <ul>
     *   <li><strong>관리자 권한</strong>: USER_ADMIN 또는 SYSTEM_ADMIN 역할 필수</li>
     *   <li><strong>본인 삭제 금지</strong>: 사용자는 본인 계정을 삭제할 수 없음</li>
     *   <li><strong>슈퍼 관리자</strong>: 최고 관리자 계정은 삭제할 수 없음</li>
     * </ul>
     * 
     * <h3>에러 처리:</h3>
     * <ul>
     *   <li><strong>401 Unauthorized</strong>: 인증되지 않은 요청</li>
     *   <li><strong>403 Forbidden</strong>: 사용자 삭제 권한 부족</li>
     *   <li><strong>404 Not Found</strong>: 존재하지 않는 사용자 ID</li>
     *   <li><strong>409 Conflict</strong>: 삭제할 수 없는 계정 (본인, 슈퍼 관리자 등)</li>
     * </ul>
     * 
     * <h3>주의사항:</h3>
     * <ul>
     *   <li><strong>되돌릴 수 없음</strong>: 삭제된 계정과 데이터는 복구 불가능</li>
     *   <li><strong>의존성 확인</strong>: 다른 시스템에서 참조하는 사용자 확인 필요</li>
     *   <li><strong>대안 고려</strong>: 삭제 대신 계정 비활성화 고려</li>
     *   <li><strong>백업</strong>: 중요한 사용자의 경우 삭제 전 데이터 백업 권장</li>
     * </ul>
     * 
     * <h3>사용 시나리오:</h3>
     * <ul>
     *   <li><strong>퇴사 처리</strong>: 퇴사한 직원의 계정 완전 삭제</li>
     *   <li><strong>테스트 계정 정리</strong>: 개발/테스트용 임시 계정 정리</li>
     *   <li><strong>보안 사고</strong>: 보안 위험이 있는 계정 즉시 삭제</li>
     *   <li><strong>정책 위반</strong>: 정책 위반으로 인한 계정 삭제</li>
     * </ul>
     * 
     * @param userId 삭제할 사용자의 고유 식별자 (UUID)
     * @throws BusinessException 사용자 삭제 실패, 권한 부족, 사용자 없음, 삭제 불가 계정 시
     * @apiNote 관리자 권한이 필수이며, 작업 전 충분한 검토가 필요함
     * @implNote 삭제 작업은 즉시 수행되며, 모든 관련 데이터가 함께 삭제됨
     * @since 2.0
     */
    public void deleteUser(String userId) {
        log.warn("사용자 계정 삭제 요청 - userId: {}", userId);
        
        try {
            sktaiUserClient.deleteUser(userId);
            log.warn("사용자 계정 삭제 완료 - userId: {}", userId);
        } catch (BusinessException e) {
            log.error("사용자 계정 삭제 실패 (BusinessException) - userId: {}, message: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("사용자 계정 삭제 실패 (예상치 못한 오류) - userId: {}", userId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "사용자 계정 삭제에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 사용자 목록 조회
     * 
     * <p>시스템의 사용자 목록을 페이징하여 조회합니다.
     * 검색어, 필터, 정렬 옵션을 통해 원하는 사용자를 찾을 수 있습니다.</p>
     * 
     * <h3>검색 기능 지원:</h3>
     * <ul>
     *   <li><strong>사용자명</strong>: username 필드에서 부분 일치 검색</li>
     *   <li><strong>이메일</strong>: email 필드에서 부분 일치 검색</li>
     *   <li><strong>전체명</strong>: full_name 필드에서 부분 일치 검색</li>
     * </ul>
     * 
     * <h3>필터링 옵션:</h3>
     * <ul>
     *   <li><strong>active</strong>: 활성 사용자만 조회</li>
     *   <li><strong>inactive</strong>: 비활성 사용자만 조회</li>
     *   <li><strong>admin</strong>: 관리자 권한 사용자만 조회</li>
     *   <li><strong>user</strong>: 일반 사용자만 조회</li>
     * </ul>
     * 
     * <h3>정렬 옵션:</h3>
     * <ul>
     *   <li><strong>username</strong>: 사용자명 오름차순</li>
     *   <li><strong>created_at desc</strong>: 생성일 내림차순</li>
     *   <li><strong>last_login desc</strong>: 최근 로그인 내림차순</li>
     * </ul>
     * 
     * @param page 페이지 번호 (1부터 시작, 기본값: 1)
     * @param size 페이지당 항목 수 (기본값: 10, 최대값: 100)
     * @param sort 정렬 조건 (예: "username", "created_at desc")
     * @param filter 필터 조건 (active, inactive, admin, user 등)
     * @param search 검색어 (사용자명, 이메일, 전체명에서 검색)
     * @return 페이징된 사용자 목록과 메타데이터
     * @throws BusinessException 사용자 목록 조회 실패 시
     * 
     * @implNote 민감한 개인정보는 마스킹되어 반환됩니다.
     * @apiNote 사용자 관리 권한이 필요한 API입니다.
     */
    public UsersRead getUsers(Integer page, Integer size, String sort, String filter, String search) {
        log.debug("SKTAI 사용자 목록 조회 - page: {}, size: {}, sort: {}, filter: {}, search: {}",
                page, size, sort, filter, search);
        
        try {
            UsersRead response = sktaiUserClient.getUsers(page, size, sort, filter, search);
            log.debug("SKTAI 사용자 목록 조회 성공 - 총 {}명의 사용자",
                    response.getPayload().getPagination().getTotal());
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 사용자 목록 조회 실패 (BusinessException) - page: {}, size: {}, filter: {}, search: {}, message: {}",
                    page, size, filter, search, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI 사용자 목록 조회 실패 (예상치 못한 오류) - page: {}, size: {}, filter: {}, search: {}",
                    page, size, filter, search, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "사용자 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 활성 사용자 목록 조회
     * 
     * <p>활성 상태인 사용자들만 조회하는 편의 메서드입니다.
     * 프로젝트 멤버 초대나 권한 할당 시 주로 사용됩니다.</p>
     * 
     * @param page 페이지 번호
     * @param size 페이지당 항목 수
     * @param search 검색어 (선택사항)
     * @return 활성 사용자 목록
     * @throws BusinessException 조회 실패 시
     */
    public UsersRead getActiveUsers(Integer page, Integer size, String search) {
        log.debug("SKTAI 활성 사용자 목록 조회 - page: {}, size: {}, search: {}", page, size, search);
        
        try {
            UsersRead response = sktaiUserClient.getUsers(page, size, null, "active", search);
            log.debug("SKTAI 활성 사용자 목록 조회 성공 - 총 {}명의 활성 사용자",
                    response.getPayload().getPagination().getTotal());
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 활성 사용자 목록 조회 실패 (BusinessException) - page: {}, size: {}, search: {}, message: {}",
                    page, size, search, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI 활성 사용자 목록 조회 실패 (예상치 못한 오류) - page: {}, size: {}, search: {}",
                    page, size, search, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "활성 사용자 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 사용자 검색
     * 
     * <p>특정 검색어로 사용자를 검색하는 편의 메서드입니다.
     * 사용자명, 이메일, 전체명에서 검색어와 일치하는 사용자를 찾습니다.</p>
     * 
     * @param searchKeyword 검색어
     * @param page 페이지 번호
     * @param size 페이지당 항목 수
     * @return 검색 결과
     * @throws BusinessException 검색 실패 시
     */
    public UsersRead searchUsers(String searchKeyword, Integer page, Integer size) {
        log.debug("SKTAI 사용자 검색 - keyword: {}, page: {}, size: {}", searchKeyword, page, size);
        
        if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
            log.warn("검색어가 비어있어 전체 사용자 목록을 조회합니다");
            return getUsers(page, size, null, null, null);
        }
        
        try {
            UsersRead response = sktaiUserClient.getUsers(page, size, null, null, searchKeyword.trim());
            log.debug("SKTAI 사용자 검색 성공 - keyword: {}, 총 {}명 검색됨",
                    searchKeyword, response.getPayload().getPagination().getTotal());
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 사용자 검색 실패 (BusinessException) - keyword: {}, message: {}", searchKeyword, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI 사용자 검색 실패 (예상치 못한 오류) - keyword: {}", searchKeyword, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "사용자 검색에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 관리자 사용자 목록 조회
     * 
     * <p>관리자 권한을 가진 사용자들만 조회하는 편의 메서드입니다.
     * 시스템 관리나 권한 관리 시 주로 사용됩니다.</p>
     * 
     * @param page 페이지 번호
     * @param size 페이지당 항목 수
     * @return 관리자 사용자 목록
     * @throws BusinessException 조회 실패 시
     */
    public UsersRead getAdminUsers(Integer page, Integer size) {
        log.debug("SKTAI 관리자 사용자 목록 조회 - page: {}, size: {}", page, size);
        
        try {
            UsersRead response = sktaiUserClient.getUsers(page, size, null, "admin", null);
            log.debug("SKTAI 관리자 사용자 목록 조회 성공 - 총 {}명의 관리자",
                    response.getPayload().getPagination().getTotal());
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 관리자 사용자 목록 조회 실패 (BusinessException) - page: {}, size: {}, message: {}", page, size, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI 관리자 사용자 목록 조회 실패 (예상치 못한 오류) - page: {}, size: {}", page, size, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "관리자 사용자 목록 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 최근 로그인 순으로 사용자 조회
     * 
     * <p>최근 로그인한 사용자들을 내림차순으로 정렬하여 조회합니다.
     * 활성 사용자 현황 파악이나 사용 통계 분석에 활용됩니다.</p>
     * 
     * @param page 페이지 번호
     * @param size 페이지당 항목 수
     * @return 최근 로그인 순 사용자 목록
     * @throws BusinessException 조회 실패 시
     */
    public UsersRead getUsersByRecentLogin(Integer page, Integer size) {
        log.debug("SKTAI 최근 로그인 순 사용자 조회 - page: {}, size: {}", page, size);
        
        try {
            UsersRead response = sktaiUserClient.getUsers(page, size, "last_login desc", "active", null);
            log.debug("SKTAI 최근 로그인 순 사용자 조회 성공 - 총 {}명",
                    response.getPayload().getPagination().getTotal());
            return response;
        } catch (BusinessException e) {
            log.error("SKTAI 최근 로그인 순 사용자 조회 실패 (BusinessException) - page: {}, size: {}, message: {}", page, size, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("SKTAI 최근 로그인 순 사용자 조회 실패 (예상치 못한 오류) - page: {}, size: {}", page, size, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                    "최근 로그인 순 사용자 조회에 실패했습니다: " + e.getMessage());
        }
    }

    // ==================== 추가 사용자 관리 서비스 ====================

    /**
     * 사용자 역할 매핑 조회
     * 
     * @param userId 사용자 ID
     * @return 사용자 역할 매핑 목록
     * @throws BusinessException 조회 실패 시
     */
    public Object getUserRoleMappings(String userId) {
        log.debug("사용자 역할 매핑 조회 요청 - userId: {}", userId);
        
        try {
            Object roleMappings = sktaiUserClient.getUserRoleMappings(userId);
            log.debug("사용자 역할 매핑 조회 성공 - userId: {}", userId);
            return roleMappings;
        } catch (BusinessException e) {
            log.error("사용자 역할 매핑 조회 실패 (BusinessException) - userId: {}, message: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("사용자 역할 매핑 조회 실패 (예상치 못한 오류) - userId: {}", userId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "사용자 역할 매핑 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 사용자에게 역할 할당
     * 
     * @param userId 사용자 ID
     * @param request 역할 할당 요청
     * @return 할당 결과
     * @throws BusinessException 할당 실패 시
     */
    public Object assignUserRole(String userId, Object request) {
        log.debug("사용자 역할 할당 요청 - userId: {}", userId);
        
        try {
            Object result = sktaiUserClient.assignUserRole(userId, request);
            log.info("사용자 역할 할당 성공 - userId: {}", userId);
            return result;
        } catch (BusinessException e) {
            log.error("사용자 역할 할당 실패 (BusinessException) - userId: {}, message: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("사용자 역할 할당 실패 (예상치 못한 오류) - userId: {}", userId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "사용자 역할 할당에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 사용자 역할 제거
     * 
     * @param userId 사용자 ID
     * @param roleId 역할 ID
     * @throws BusinessException 제거 실패 시
     */
    public void removeUserRole(String userId, String roleId) {
        log.debug("사용자 역할 제거 요청 - userId: {}, roleId: {}", userId, roleId);
        
        try {
            sktaiUserClient.removeUserRole(userId, roleId);
            log.info("사용자 역할 제거 성공 - userId: {}, roleId: {}", userId, roleId);
        } catch (BusinessException e) {
            log.error("사용자 역할 제거 실패 (BusinessException) - userId: {}, roleId: {}, message: {}", userId, roleId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("사용자 역할 제거 실패 (예상치 못한 오류) - userId: {}, roleId: {}", userId, roleId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "사용자 역할 제거에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 사용자 그룹 매핑 조회
     * 
     * @param userId 사용자 ID
     * @return 사용자 그룹 매핑 목록
     * @throws BusinessException 조회 실패 시
     */
    public Object getUserGroupMappings(String userId) {
        log.debug("사용자 그룹 매핑 조회 요청 - userId: {}", userId);
        
        try {
            Object groupMappings = sktaiUserClient.getUserGroupMappings(userId);
            log.debug("사용자 그룹 매핑 조회 성공 - userId: {}", userId);
            return groupMappings;
        } catch (BusinessException e) {
            log.error("사용자 그룹 매핑 조회 실패 (BusinessException) - userId: {}, message: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("사용자 그룹 매핑 조회 실패 (예상치 못한 오류) - userId: {}", userId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "사용자 그룹 매핑 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 사용자를 그룹에 추가
     * 
     * @param userId 사용자 ID
     * @param groupId 그룹 ID
     * @return 추가 결과
     * @throws BusinessException 추가 실패 시
     */
    public Object addUserToGroup(String userId, String groupId) {
        log.debug("사용자 그룹 추가 요청 - userId: {}, groupId: {}", userId, groupId);
        
        try {
            Object result = sktaiUserClient.addUserToGroup(userId, groupId);
            log.info("사용자 그룹 추가 성공 - userId: {}, groupId: {}", userId, groupId);
            return result;
        } catch (BusinessException e) {
            log.error("사용자 그룹 추가 실패 (BusinessException) - userId: {}, groupId: {}, message: {}", userId, groupId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("사용자 그룹 추가 실패 (예상치 못한 오류) - userId: {}, groupId: {}", userId, groupId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "사용자 그룹 추가에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 사용자를 그룹에서 제거
     * 
     * @param userId 사용자 ID
     * @param groupId 그룹 ID
     * @throws BusinessException 제거 실패 시
     */
    public void removeUserFromGroup(String userId, String groupId) {
        log.debug("사용자 그룹 제거 요청 - userId: {}, groupId: {}", userId, groupId);
        
        try {
            sktaiUserClient.removeUserFromGroup(userId, groupId);
            log.info("사용자 그룹 제거 성공 - userId: {}, groupId: {}", userId, groupId);
        } catch (BusinessException e) {
            log.error("사용자 그룹 제거 실패 (BusinessException) - userId: {}, groupId: {}, message: {}", userId, groupId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("사용자 그룹 제거 실패 (예상치 못한 오류) - userId: {}, groupId: {}", userId, groupId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "사용자 그룹 제거에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 사용자 비밀번호 변경
     * 
     * @param userId 사용자 ID
     * @param request 비밀번호 변경 요청
     * @throws BusinessException 변경 실패 시
     */
    public void resetUserPassword(String userId, Object request) {
        log.debug("사용자 비밀번호 변경 요청 - userId: {}", userId);
        
        try {
            sktaiUserClient.resetUserPassword(userId, request);
            log.info("사용자 비밀번호 변경 성공 - userId: {}", userId);
        } catch (BusinessException e) {
            log.error("사용자 비밀번호 변경 실패 (BusinessException) - userId: {}, message: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("사용자 비밀번호 변경 실패 (예상치 못한 오류) - userId: {}", userId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "사용자 비밀번호 변경에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 사용자의 할당 가능한 역할 목록을 조회합니다.
     *
     * <p>특정 사용자가 새롭게 할당받을 수 있는 역할 목록을 페이징하여 조회합니다.
     * 사용자 권한 관리 시 활용됩니다.</p>
     *
     * @param userId 사용자 ID
     * @return 할당 가능한 역할 목록과 페이징 정보
     * @throws BusinessException 조회 실패 시
     */
    public RoleAvailablePageResponseDto getUserAvailableRoles(String userId) {
        return sktaiUserClient.getUserAvailableRoles(userId, 1, 10);
    }

    /**
     * 사용자 역할 매핑 일괄 수정 (서비스 래퍼)
     *
     * @param userId 사용자 ID
     * @param items  PUT /role-mappings 요청 항목 리스트
     */
    public void updateUserRoleMappings(String userId, List<UserRoleMappingUpdateItemDto> items) {
        log.debug("사용자 역할 매핑 일괄 수정 요청 - userId: {}, items: {}", userId, (items == null ? 0 : items.size()));
        try {
            sktaiUserClient.updateUserRoleMappings(userId, items);
            log.info("사용자 역할 매핑 일괄 수정 성공 - userId: {}", userId);
        } catch (BusinessException e) {
            log.error("사용자 역할 매핑 일괄 수정 실패 (BusinessException) - userId: {}, message: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("사용자 역할 매핑 일괄 수정 실패 (예상치 못한 오류) - userId: {}", userId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "사용자 역할 매핑 일괄 수정에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * role-available 응답 아이템 리스트를 그대로 전달하여 역할 매핑을 갱신합니다.
     * 내부에서 PUT 요청 형식으로 변환합니다.
     *
     * @param userId 사용자 ID
     * @param availableItems GET /role-available 응답 데이터 아이템 리스트
     */
    public void updateUserRoleMappingsFromAvailable(String userId, List<RoleAvailablePageResponseDto.Item> availableItems) {
        if (availableItems == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "availableItems가 null 입니다");
        }
        List<UserRoleMappingUpdateItemDto> items = userRoleMappingMapper.toUpdateItems(availableItems);
        updateUserRoleMappings(userId, items);
    }
}
