package com.skax.aiplatform.service.lineage;

import java.util.List;

import com.skax.aiplatform.dto.lineage.request.LineageCreateReq;
import com.skax.aiplatform.dto.lineage.request.LineageSearchReq;
import com.skax.aiplatform.dto.lineage.response.LineageRelationRes;

/**
 * Lineage 관리 서비스 인터페이스
 * 
 * <p>Lineage 관계 관리를 위한 비즈니스 로직을 정의합니다.
 * 객체 간의 의존성과 데이터 흐름을 추적하고 관리하는 기능을 제공합니다.</p>
 * 
 * @author gyuHeeHwang
 * @since 2025-10-19
 * @version 1.0
 */
public interface LineageService {
    
    /**
     * Lineage 관계 생성
     * 
     * @param request Lineage 생성 요청 정보
     * @throws BusinessException 생성 실패 시
     */
    void createLineage(LineageCreateReq request);
    
    /**
     * Lineage 관계 조회 (BFS 탐색)
     * 
     * @param request Lineage 조회 요청 정보
     * @return 탐색된 Lineage 관계 목록
     * @throws BusinessException 조회 실패 시
     */
    List<LineageRelationRes> getLineage(LineageSearchReq request);
    
    /**
     * 공유 Lineage 조회
     * 
     * @param objectKeys 객체 키 목록
     * @return 공유되는 Lineage 관계 목록
     * @throws BusinessException 조회 실패 시
     */
    List<LineageRelationRes> getSharedLineage(List<String> objectKeys);
    
    /**
     * Lineage 관계 삭제
     * 
     * @param sourceKey 삭제할 소스 객체 키
     * @throws BusinessException 삭제 실패 시
     */
    void deleteLineage(String sourceKey);
    
}
