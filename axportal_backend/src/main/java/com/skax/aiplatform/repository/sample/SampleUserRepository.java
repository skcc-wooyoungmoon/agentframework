package com.skax.aiplatform.repository.sample;

import com.skax.aiplatform.entity.sample.SampleUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 샘플 사용자 리포지토리
 * 
 * <p>샘플 사용자 엔티티에 대한 데이터 액세스 레이어입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-03
 * @version 2.0.0
 */
@Repository
public interface SampleUserRepository extends JpaRepository<SampleUser, Long> {
    
    /**
     * 사용자명으로 샘플 사용자 조회
     * 
     * @param username 사용자명
     * @return 샘플 사용자 (Optional)
     */
    Optional<SampleUser> findByUsername(String username);
    
    /**
     * 이메일로 샘플 사용자 조회
     * 
     * @param email 이메일
     * @return 샘플 사용자 (Optional)
     */
    Optional<SampleUser> findByEmail(String email);
    
    /**
     * 사용자명 중복 확인
     * 
     * @param username 사용자명
     * @return 존재 여부
     */
    boolean existsByUsername(String username);
    
    /**
     * 이메일 중복 확인
     * 
     * @param email 이메일
     * @return 존재 여부
     */
    boolean existsByEmail(String email);
    
    /**
     * 활성화 상태별 사용자 목록 조회 (페이징)
     * 
     * @param isActive 활성화 여부
     * @param pageable 페이징 정보
     * @return 사용자 목록 (Page)
     */
    Page<SampleUser> findByIsActive(Boolean isActive, Pageable pageable);
    
    /**
     * 부서별 사용자 목록 조회 (페이징)
     * 
     * @param department 부서
     * @param pageable 페이징 정보
     * @return 사용자 목록 (Page)
     */
    Page<SampleUser> findByDepartmentContainingIgnoreCase(String department, Pageable pageable);
    
    /**
     * 이름으로 사용자 검색 (페이징)
     * 
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @return 사용자 목록 (Page)
     */
    @Query("SELECT u FROM SampleUser u WHERE " +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<SampleUser> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 활성화된 사용자 수 조회
     * 
     * @return 활성화된 사용자 수
     */
    @Query("SELECT COUNT(u) FROM SampleUser u WHERE u.isActive = true")
    long countActiveUsers();
}
