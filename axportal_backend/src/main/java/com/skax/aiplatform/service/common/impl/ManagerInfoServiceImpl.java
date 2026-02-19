package com.skax.aiplatform.service.common.impl;

import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.dto.common.response.ManagerInfoRes;
import com.skax.aiplatform.entity.GpoUsersMas;
import com.skax.aiplatform.repository.auth.GpoUsersMasRepository;
import com.skax.aiplatform.service.common.ManagerInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 관리자 정보 조회 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ManagerInfoServiceImpl implements ManagerInfoService {

    private final GpoUsersMasRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public ManagerInfoRes getManagerInfo(String type, String value) {
        log.debug("관리자 정보 조회 요청 - type: {}, value: {}", type, value);
            


        // value로 사용자 정보 조회
        GpoUsersMas user = findUserByType(type, value);

        // createdBy 사용자 정보 조회
        // GpoUsersMas createdByUser = null;
        // if (createdBy != null && !createdBy.trim().isEmpty()) {
        //     createdByUser = findUserByType(type, createdBy.trim(), "createdBy");
        // }

        // // updatedBy 사용자 정보 조회
        // GpoUsersMas updatedByUser = null;
        // if (updatedBy != null && !updatedBy.trim().isEmpty()) {
        //     updatedByUser = findUserByType(type, updatedBy.trim(), "updatedBy");
        // }

        // Entity를 DTO로 변환
        ManagerInfoRes managerInfoRes = ManagerInfoRes.builder()
                .memberId(user.getMemberId())
                .uuid(user.getUuid())
                .jkwNm(user.getJkwNm())
                .deptNm(user.getDeptNm())
                .retrJkwYn(user.getRetrJkwYn())
                .build();

        log.debug("관리자 정보 조회 완료 - memberId: {}, uuid: {}, jkwNm: {}, deptNm: {}, retrJkwYn: {}", 
                user.getMemberId(), user.getUuid(), user.getJkwNm(), user.getDeptNm(), user.getRetrJkwYn());
        
        return managerInfoRes;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ManagerInfoRes> getManagerInfoBulk(String type, List<String> values) {
        log.debug("관리자 정보 bulk 조회 요청 - type: {}, values count: {}", type, values != null ? values.size() : 0);
        
        if (values == null || values.isEmpty()) {
            return new ArrayList<>();
        }

        // 각 값에 대해 조회 수행 (null 값도 포함하여 처리)
        List<ManagerInfoRes> managerInfoList = values.stream()
                .map(value -> {
                    // null이거나 빈 값이면 null ManagerInfoRes 반환
                    if (value == null || value.trim().isEmpty()) {
                        return ManagerInfoRes.builder()
                                .memberId(null)
                                .uuid(null)
                                .jkwNm(null)
                                .deptNm(null)
                                .retrJkwYn(null)
                                .build();
                    }
                    
                    // 조회 수행
                    Optional<GpoUsersMas> userOpt;
                    if ("memberId".equalsIgnoreCase(type)) {
                        userOpt = userRepository.findByMemberId(value);
                        // 대문자로도 조회 시도
                        if (!userOpt.isPresent()) {
                            userOpt = userRepository.findByMemberId(value.toUpperCase());
                        }
                    } else if ("uuid".equalsIgnoreCase(type)) {
                        userOpt = userRepository.findByUuid(value);
                    } else {
                        throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "지원하지 않는 조회 타입입니다: " + type + " (memberId 또는 uuid만 지원)");
                    }
                    
                    // 조회 결과가 있으면 Entity를 DTO로 변환, 없으면 null 필드로 DTO 생성
                    if (userOpt.isPresent()) {
                        GpoUsersMas user = userOpt.get();
                        return ManagerInfoRes.builder()
                                .memberId(user.getMemberId())
                                .uuid(user.getUuid())
                                .jkwNm(user.getJkwNm())
                                .deptNm(user.getDeptNm())
                                .retrJkwYn(user.getRetrJkwYn())
                                .build();
                    } else {
                        // 조회 결과가 없어도 null 필드로 DTO 반환
                        return ManagerInfoRes.builder()
                                .memberId(null)
                                .uuid(null)
                                .jkwNm(null)
                                .deptNm(null)
                                .retrJkwYn(null)
                                .build();
                    }
                })
                .collect(Collectors.toList());

        log.debug("관리자 정보 bulk 조회 완료 - 조회된 사용자 수: {}", managerInfoList.size());
        
        return managerInfoList;
    }

    /**
     * 타입에 따라 사용자 정보 조회
     */
    private GpoUsersMas findUserByType(String type, String value) {
        // TODO : createBy : admin, updatedBy : f676500c-1866-462a-ba8e-e7f76412b1dc 인 경우 조회 불가 처리를 위해 모든 경우 탐색
        if ("memberId".equalsIgnoreCase(type)) {
            // 원래 값으로 조회 시도
            Optional<GpoUsersMas> userOpt = userRepository.findByMemberId(value);
            if (userOpt.isPresent()) {
                return userOpt.get();
            }
            // 대문자로 조회 시도
            userOpt = userRepository.findByMemberId(value.toUpperCase());
            if (userOpt.isPresent()) {
                return userOpt.get();
            }
            // 소문자로 조회 시도
            userOpt = userRepository.findByUuid(value);
            return userOpt.orElse(null);
        } else if ("uuid".equalsIgnoreCase(type)) {
            // 원래 값으로 조회 시도
            Optional<GpoUsersMas> userOpt = userRepository.findByUuid(value);
            if (userOpt.isPresent()) {
                return userOpt.get();
            }

            // userOpt = userRepository.findByMemberId(value);
            // if (userOpt.isPresent()) {
            //     return userOpt.get();
            // }
            // // 대문자로 조회 시도
            // userOpt = userRepository.findByMemberId(value.toUpperCase());
            // if (userOpt.isPresent()) {
            //     return userOpt.get();
            // }
            
            return userOpt.orElse(null);
        } else {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "지원하지 않는 조회 타입입니다: " + type + " (memberId 또는 uuid만 지원)");
        }
    }

}

