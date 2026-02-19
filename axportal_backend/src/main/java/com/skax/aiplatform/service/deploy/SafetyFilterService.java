package com.skax.aiplatform.service.deploy;

import java.util.List;

import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.deploy.request.SafetyFilterCreateReq;
import com.skax.aiplatform.dto.deploy.request.SafetyFilterDeleteReq;
import com.skax.aiplatform.dto.deploy.request.SafetyFilterListReq;
import com.skax.aiplatform.dto.deploy.request.SafetyFilterUpdateReq;
import com.skax.aiplatform.dto.deploy.response.SafetyFilterCreateRes;
import com.skax.aiplatform.dto.deploy.response.SafetyFilterDeleteRes;
import com.skax.aiplatform.dto.deploy.response.SafetyFilterDetailRes;
import com.skax.aiplatform.dto.deploy.response.SafetyFilterRes;
import com.skax.aiplatform.dto.deploy.response.SafetyFilterUpdateRes;

/**
 * 세이프티 필터 서비스 인터페이스
 */
public interface SafetyFilterService {

    /**
     * 세이프티 필터 목록 조회
     */
    PageResponse<SafetyFilterRes> getSafetyFilterList(SafetyFilterListReq request);

    /**
     * 세이프티 필터 상세 조회
     */
    SafetyFilterDetailRes getSafetyFilterDetail(String filterGroupId);

    /**
     * 세이프티 필터 생성
     */
    SafetyFilterCreateRes createSafetyFilter(SafetyFilterCreateReq request);

    /**
     * 세이프티 필터 수정
     */
    SafetyFilterUpdateRes updateSafetyFilter(String groupId, SafetyFilterUpdateReq request);

    /**
     * 세이프티 필터 복수 삭제
     */
    SafetyFilterDeleteRes deleteSafetyFilterBulk(SafetyFilterDeleteReq request);

    /**
     * 세이프티 필터 Policy 설정
     *
     * @param filterGroupId 세이프티 필터 그룹 ID
     * @param memberId      사용자 ID
     * @param projectName   프로젝트명
     * @return List<PolicyRequest> 설정된 Policy 목록
     */
    List<PolicyRequest> setSafetyFilterPolicy(String filterGroupId, String memberId, String projectName);

}

