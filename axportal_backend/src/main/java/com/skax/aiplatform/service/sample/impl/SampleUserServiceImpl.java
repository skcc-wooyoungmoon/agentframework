package com.skax.aiplatform.service.sample.impl;

import com.skax.aiplatform.dto.sample.request.SampleUserCreateReq;
import com.skax.aiplatform.dto.sample.request.SampleUserUpdateReq;
import com.skax.aiplatform.dto.sample.response.SampleUserRes;
import com.skax.aiplatform.entity.sample.SampleUser;
import com.skax.aiplatform.mapper.sample.SampleUserMapper;
import com.skax.aiplatform.repository.sample.SampleUserRepository;
import com.skax.aiplatform.service.sample.SampleUserService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 샘플 사용자 서비스 구현체
 * 
 * <p>샘플 사용자 관련 비즈니스 로직을 구현하는 서비스 클래스입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-03
 * @version 2.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SampleUserServiceImpl implements SampleUserService {
    
    private final SampleUserRepository sampleUserRepository;
    private final SampleUserMapper sampleUserMapper;
    
    @Override
    public Page<SampleUserRes> getAllUsers(Pageable pageable) {
        log.info("모든 샘플 사용자 조회 요청 - 페이지: {}, 크기: {}", 
                pageable.getPageNumber(), pageable.getPageSize());
        
        // 정렬 파라미터 검증 및 안전한 정렬 적용
        Pageable validatedPageable = validateAndFixPageable(pageable);
        
        Page<SampleUser> userPage = sampleUserRepository.findAll(validatedPageable);
        
        log.info("샘플 사용자 조회 완료 - 총 {}명, 현재 페이지 {}명", 
                userPage.getTotalElements(), userPage.getNumberOfElements());
        
        return userPage.map(sampleUserMapper::toResponse);
    }
    
    @Override
    public SampleUserRes getUserById(Long id) {
        log.info("ID로 샘플 사용자 조회: {}", id);
        
        SampleUser user = findSampleUserById(id);
        
        log.info("샘플 사용자 조회 성공: {}", user.getUsername());
        return sampleUserMapper.toResponse(user);
    }
    
    @Override
    public SampleUserRes getUserByUsername(String username) {
        log.info("사용자명으로 샘플 사용자 조회: {}", username);
        
        SampleUser user = sampleUserRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        log.info("샘플 사용자 조회 성공: {}", user.getUsername());
        return sampleUserMapper.toResponse(user);
    }
    
    @Override
    @Transactional
    public SampleUserRes createUser(SampleUserCreateReq createReq) {
        log.info("새로운 샘플 사용자 생성 요청: {}", createReq.getUsername());
        
        // 중복 검증
        validateDuplicateUser(createReq.getUsername(), createReq.getEmail());
        
        // 엔티티 생성 및 저장
        SampleUser user = sampleUserMapper.toEntity(createReq);
        SampleUser savedUser = sampleUserRepository.save(user);
        
        log.info("샘플 사용자 생성 완료: ID={}, 사용자명={}", 
                savedUser.getId(), savedUser.getUsername());
        
        return sampleUserMapper.toResponse(savedUser);
    }
    
    @Override
    @Transactional
    public SampleUserRes updateUser(Long id, SampleUserUpdateReq updateReq) {
        log.info("샘플 사용자 정보 수정 요청: ID={}", id);
        
        SampleUser user = findSampleUserById(id);
        
        // 이메일 중복 검증 (기존 이메일과 다른 경우만)
        if (updateReq.getEmail() != null && !updateReq.getEmail().equals(user.getEmail())) {
            if (sampleUserRepository.existsByEmail(updateReq.getEmail())) {
                throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }
        }
        
        // 엔티티 업데이트
        sampleUserMapper.updateEntity(updateReq, user);
        SampleUser updatedUser = sampleUserRepository.save(user);
        
        log.info("샘플 사용자 정보 수정 완료: ID={}, 사용자명={}", 
                updatedUser.getId(), updatedUser.getUsername());
        
        return sampleUserMapper.toResponse(updatedUser);
    }
    
    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("샘플 사용자 삭제 요청: ID={}", id);
        
        SampleUser user = findSampleUserById(id);
        sampleUserRepository.delete(user);
        
        log.info("샘플 사용자 삭제 완료: ID={}, 사용자명={}", 
                user.getId(), user.getUsername());
    }
    
    @Override
    public Page<SampleUserRes> getUsersByActiveStatus(Boolean isActive, Pageable pageable) {
        log.info("활성화 상태별 샘플 사용자 조회: isActive={}", isActive);
        
        // 정렬 파라미터 검증 및 안전한 정렬 적용
        Pageable validatedPageable = validateAndFixPageable(pageable);
        
        Page<SampleUser> userPage = sampleUserRepository.findByIsActive(isActive, validatedPageable);
        
        log.info("활성화 상태별 샘플 사용자 조회 완료: 총 {}명", userPage.getTotalElements());
        
        return userPage.map(sampleUserMapper::toResponse);
    }
    
    @Override
    public Page<SampleUserRes> getUsersByDepartment(String department, Pageable pageable) {
        log.info("부서별 샘플 사용자 조회: department={}", department);
        
        // 정렬 파라미터 검증 및 안전한 정렬 적용
        Pageable validatedPageable = validateAndFixPageable(pageable);
        
        Page<SampleUser> userPage = sampleUserRepository.findByDepartmentContainingIgnoreCase(department, validatedPageable);
        
        log.info("부서별 샘플 사용자 조회 완료: 총 {}명", userPage.getTotalElements());
        
        return userPage.map(sampleUserMapper::toResponse);
    }
    
    @Override
    public Page<SampleUserRes> searchUsers(String keyword, Pageable pageable) {
        log.info("키워드로 샘플 사용자 검색: keyword={}", keyword);
        
        // 정렬 파라미터 검증 및 안전한 정렬 적용
        Pageable validatedPageable = validateAndFixPageable(pageable);
        
        Page<SampleUser> userPage = sampleUserRepository.searchByKeyword(keyword, validatedPageable);
        
        log.info("키워드 검색 완료: 총 {}명", userPage.getTotalElements());
        
        return userPage.map(sampleUserMapper::toResponse);
    }
    
    @Override
    public long getActiveUserCount() {
        log.info("활성화된 샘플 사용자 수 조회");
        
        long count = sampleUserRepository.countActiveUsers();
        
        log.info("활성화된 샘플 사용자 수: {}명", count);
        
        return count;
    }
    
    /**
     * ID로 샘플 사용자 엔티티 조회 (내부 메서드)
     * 
     * @param id 사용자 ID
     * @return 샘플 사용자 엔티티
     * @throws BusinessException 사용자를 찾을 수 없는 경우
     */
    private SampleUser findSampleUserById(Long id) {
        return sampleUserRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
    
    /**
     * 사용자명과 이메일 중복 검증
     * 
     * @param username 사용자명
     * @param email 이메일
     * @throws BusinessException 중복된 사용자명 또는 이메일이 존재하는 경우
     */
    private void validateDuplicateUser(String username, String email) {
        if (sampleUserRepository.existsByUsername(username)) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        
        if (sampleUserRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }
    
    /**
     * Pageable 객체의 정렬 파라미터를 검증하고 안전한 정렬을 적용합니다.
     * 
     * @param pageable 원본 Pageable 객체
     * @return 검증된 Pageable 객체
     */
    private Pageable validateAndFixPageable(Pageable pageable) {
        // 허용되는 정렬 필드 목록 (SampleUser 엔티티 필드)
        final String[] ALLOWED_SORT_FIELDS = {
            "id", "username", "email", "fullName", "department", 
            "isActive", "createdAt", "updatedAt", "createdBy", "updatedBy"
        };
        
        Sort validatedSort = Sort.by("id").descending(); // 기본 정렬
        
        if (pageable.getSort().isSorted()) {
            List<Sort.Order> validOrders = new ArrayList<>();
            
            for (Sort.Order order : pageable.getSort()) {
                String property = order.getProperty();
                
                // 허용된 필드인지 확인
                if (Arrays.asList(ALLOWED_SORT_FIELDS).contains(property)) {
                    validOrders.add(order);
                    log.debug("유효한 정렬 필드 적용: {} {}", property, order.getDirection());
                } else {
                    log.warn("허용되지 않은 정렬 필드 무시: {}", property);
                }
            }
            
            if (!validOrders.isEmpty()) {
                validatedSort = Sort.by(validOrders);
            }
        }
        
        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                validatedSort
        );
    }
}