package com.skax.aiplatform.service.prompt;
  
import java.util.List;

import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.prompt.request.FewShotCreateReq;
import com.skax.aiplatform.dto.prompt.request.FewShotUpdateReq;
import com.skax.aiplatform.dto.prompt.response.FewShotCreateRes;
import com.skax.aiplatform.dto.prompt.response.FewShotItemRes;
import com.skax.aiplatform.dto.prompt.response.FewShotLineageRes;
import com.skax.aiplatform.dto.prompt.response.FewShotRes;
import com.skax.aiplatform.dto.prompt.response.FewShotTagListRes;
import com.skax.aiplatform.dto.prompt.response.FewShotTagRes;
import com.skax.aiplatform.dto.prompt.response.FewShotVerRes;

/**
* Few-Shot 관리 서비스 인터페이스
 * 
 * <p>Few-Shot 예제 데이터 관리를 위한 비즈니스 로직을 정의합니다.
 * SKTAX Agent Few-Shots API와 연동하여 작동합니다.</p>
 * 
 * @author gyuHeeHwang
 * @since 2025-08-11
 * @version 1.0.0
 */
public interface FewShotService {

    /**
     * Few-Shot 목록 조회
     * 
     * @param projectId 프로젝트 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param sort 정렬 조건
     * @param filter 필터 조건
     * @param search 검색 키워드
     * @return Few-Shot 응답 (페이지네이션 포함)
     */
    PageResponse<FewShotRes> getFewShotList(String projectId, Integer page, Integer size, 
                          String sort, String filter, String search, Boolean release_only);

                          
    /**
     * Few-Shot 상세 정보 조회
     * 
     * @param fewShotUuid Few-Shot UUID
     * @return Few-Shot 상세 정보
     */
    FewShotRes getFewShotById(String fewShotUuid);

        /**
     * Few-Shot 최신 버전 조회
     * 
     * @param fewShotUuid Few-Shot UUID
     * @return 최신 버전 정보
     */
    FewShotVerRes getLtstFewShotVerById(String fewShotUuid);

    /**
     * Few-Shot 버전 목록 조회
     * 
     * @param fewShotUuid Few-Shot UUID
     * @return 버전 목록
     */
    List<FewShotVerRes> getFewShotVerListById(String fewShotUuid);

    /**
     * Few-Shot 아이템 목록 조회
     * 
     * @param versionId 버전 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param sort 정렬 조건
     * @param filter 필터 조건
     * @param search 검색 키워드
     * @return Few-Shot 아이템 목록
     */
    List<FewShotItemRes> getFewShotItemListById(String versionId, Integer page, Integer size,
                                  String sort, String filter, String search);
                                  
    /**
     * Few-Shot 태그 조회 (버전별)
     * 
     * @param versionId 버전 ID
     * @return 태그 목록
     */
    List<FewShotTagRes> getFewShotTagsByVerId(String versionId);

    /**
     * Few-Shot 태그 목록 조회
     * 
     * @param projectId 프로젝트 ID
     * @param filter 필터 조건
     * @return 모든 태그 목록
     */
    FewShotTagListRes getFewShotTagList();

    /**
     * 새로운 Few-Shot 생성
     * 
     * @param request Few-Shot 생성 요청
     * @return 생성된 Few-Shot 정보
     */
    FewShotCreateRes createFewShot(FewShotCreateReq request);

    /**
     * Few-Shot 정보 수정
     * 
     * @param fewShotUuid Few-Shot UUID
     * @param request Few-Shot 수정 요청
     * @return 수정된 Few-Shot 정보
     */
    void updateFewShotById(String fewShotUuid, FewShotUpdateReq request);

    /**
     * Few-Shot 삭제
     * 
     * @param fewShotUuid Few-Shot UUID
     */
    void deleteFewShotById(String fewShotUuid);

    /**
     * Few-Shot의 Lineage 관계 조회 (페이징 처리)
     * 
     * @param fewShotUuid Few-Shot UUID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기 (기본 6개)
     * @return Few-Shot의 Lineage 관계 목록 (페이징 포함)
     */
    PageResponse<FewShotLineageRes> getFewShotLineageRelations(String fewShotUuid, Integer page, Integer size);


    /**
     * Few-Shot Policy 설정
     * 
     * @param fewShotUuid Few-Shot UUID
     * @param memberId 사용자 ID
     * @param projectName 프로젝트명
     * @return List<PolicyRequest> 설정된 Policy 목록
     */
    List<PolicyRequest> setFewShotPolicy(String fewShotUuid, String memberId, String projectName);
} 