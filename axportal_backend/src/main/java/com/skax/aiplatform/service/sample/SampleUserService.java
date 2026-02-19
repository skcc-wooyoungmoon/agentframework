package com.skax.aiplatform.service.sample;

import com.skax.aiplatform.dto.sample.request.SampleUserCreateReq;
import com.skax.aiplatform.dto.sample.request.SampleUserUpdateReq;
import com.skax.aiplatform.dto.sample.response.SampleUserRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 샘플 사용자 서비스 인터페이스
 * 
 * <p>샘플 사용자 관련 비즈니스 로직을 정의하는 서비스 인터페이스입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-03
 * @version 2.0.0
 */
public interface SampleUserService {
    
    /**
     * 모든 샘플 사용자 조회 (페이징)
     * 
     * @param pageable 페이징 정보
     * @return 사용자 목록 (Page)
     */
    Page<SampleUserRes> getAllUsers(Pageable pageable);
    
    /**
     * ID로 샘플 사용자 조회
     * 
     * @param id 사용자 ID
     * @return 사용자 정보
     */
    SampleUserRes getUserById(Long id);
    
    /**
     * 사용자명으로 샘플 사용자 조회
     * 
     * @param username 사용자명
     * @return 사용자 정보
     */
    SampleUserRes getUserByUsername(String username);
    
    /**
     * 새로운 샘플 사용자 생성
     * 
     * @param createReq 생성 요청 데이터
     * @return 생성된 사용자 정보
     */
    SampleUserRes createUser(SampleUserCreateReq createReq);
    
    /**
     * 샘플 사용자 정보 수정
     * 
     * @param id 사용자 ID
     * @param updateReq 수정 요청 데이터
     * @return 수정된 사용자 정보
     */
    SampleUserRes updateUser(Long id, SampleUserUpdateReq updateReq);
    
    /**
     * 샘플 사용자 삭제
     * 
     * @param id 사용자 ID
     */
    void deleteUser(Long id);
    
    /**
     * 활성화 상태별 사용자 조회 (페이징)
     * 
     * @param isActive 활성화 여부
     * @param pageable 페이징 정보
     * @return 사용자 목록 (Page)
     */
    Page<SampleUserRes> getUsersByActiveStatus(Boolean isActive, Pageable pageable);
    
    /**
     * 부서별 사용자 조회 (페이징)
     * 
     * @param department 부서
     * @param pageable 페이징 정보
     * @return 사용자 목록 (Page)
     */
    Page<SampleUserRes> getUsersByDepartment(String department, Pageable pageable);
    
    /**
     * 키워드로 사용자 검색 (페이징)
     * 
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @return 사용자 목록 (Page)
     */
    Page<SampleUserRes> searchUsers(String keyword, Pageable pageable);
    
    /**
     * 활성화된 사용자 수 조회
     * 
     * @return 활성화된 사용자 수
     */
    long getActiveUserCount();
}