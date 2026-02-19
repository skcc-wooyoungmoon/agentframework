package com.skax.aiplatform.service.common;

import com.skax.aiplatform.dto.common.response.ManagerInfoRes;

import java.util.List;

/**
 * 관리자 정보 조회 서비스
 */
public interface ManagerInfoService {

    /**
     * 관리자 정보 조회
     * 
     * @param type 조회 타입 ("memberId" 또는 "uuid")
     * @param value 조회 값 (memberId 또는 uuid)
     * @return 관리자 정보
     */
    ManagerInfoRes getManagerInfo(String type, String value);

    /**
     * 관리자 정보 bulk 조회
     * 
     * @param type 조회 타입 ("memberId" 또는 "uuid")
     * @param values 조회 값 목록 (memberId 또는 uuid 목록)
     * @return 관리자 정보 목록
     */
    List<ManagerInfoRes> getManagerInfoBulk(String type, List<String> values);

}

