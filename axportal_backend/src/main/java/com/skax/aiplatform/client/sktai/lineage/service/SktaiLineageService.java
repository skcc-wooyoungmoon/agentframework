package com.skax.aiplatform.client.sktai.lineage.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.skax.aiplatform.client.sktai.lineage.SktaiLineageClient;
import com.skax.aiplatform.client.sktai.lineage.dto.ActionType;
import com.skax.aiplatform.client.sktai.lineage.dto.Direction;
import com.skax.aiplatform.client.sktai.lineage.dto.request.LineageCreate;
import com.skax.aiplatform.client.sktai.lineage.dto.request.LineageObjectCreate;
import com.skax.aiplatform.client.sktai.lineage.dto.response.LineageRelationWithTypes;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import feign.FeignException;

import java.util.List;

/**
 * SKTAI Lineage 관리 서비스
 * 
 * <p>SKTAI Lineage API와의 통신을 담당하는 서비스 계층입니다.
 * 객체 간의 의존성과 데이터 흐름 관계를 관리하고 추적하는 비즈니스 로직을 제공합니다.</p>
 * 
 * <h3>핵심 기능:</h3>
 * <ul>
 *   <li><strong>관계 생성</strong>: 새로운 Lineage 관계 생성 및 검증</li>
 *   <li><strong>관계 삭제</strong>: 기존 Lineage 관계 삭제 및 정리</li>
 *   <li><strong>의존성 탐색</strong>: BFS 기반 관계 탐색 및 분석</li>
 *   <li><strong>공유 관계</strong>: 다중 객체 간 공통 관계 조회</li>
 * </ul>
 * 
 * <h3>예외 처리:</h3>
 * <ul>
 *   <li>API 통신 오류 시 BusinessException 발생</li>
 *   <li>유효성 검증 실패 시 상세 오류 정보 제공</li>
 *   <li>네트워크 오류 시 재시도 로직 적용</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-10-17
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SktaiLineageService {
    
    private final SktaiLineageClient sktaiLineageClient;
    
    /**
     * Lineage 관계 생성
     * 
     * <p>소스 객체와 타겟 객체 간의 새로운 Lineage 관계를 생성합니다.
     * 액션 타입에 따라 의존성 방향과 성격이 결정됩니다.</p>
     * 
     * @param request Lineage 생성 요청 (소스키, 타겟키, 액션 타입 포함)
     * @throws BusinessException API 호출 실패 시
     */
    public void createLineage(LineageCreate request) {
        if (request.getLineages() == null || request.getLineages().isEmpty()) {
            log.warn("Lineage 생성 요청에 lineages가 비어있습니다.");
            return;
        }
        
        LineageCreate.LineageItem firstItem = request.getLineages().get(0);
        log.debug("Lineage 생성 요청 - source: {}, target: {}, action: {}", 
                 firstItem.getSourceKey(), firstItem.getTargetKey(), firstItem.getAction());
        
        try {
            sktaiLineageClient.createLineage(request);
            log.debug("Lineage 생성 성공 - source: {}, target: {}", 
                     firstItem.getSourceKey(), firstItem.getTargetKey());
        } catch (FeignException e) {
            log.error("Lineage 생성 실패 - source: {}, target: {}", 
                     firstItem.getSourceKey(), firstItem.getTargetKey(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "Lineage 생성에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * Lineage 관계 삭제
     * 
     * <p>특정 소스 키에 연결된 모든 Lineage 관계를 삭제합니다.
     * 해당 객체가 소스로 참여하는 모든 의존성이 제거됩니다.</p>
     * 
     * @param sourceKey 삭제할 소스 객체의 고유 키
     * @throws BusinessException API 호출 실패 시
     */
    public void deleteLineage(String sourceKey) {
        log.debug("Lineage 삭제 요청 - sourceKey: {}", sourceKey);
        
        try {
            sktaiLineageClient.deleteLineage(sourceKey);
            log.debug("Lineage 삭제 성공 - sourceKey: {}", sourceKey);
        } catch (FeignException e) {
            log.error("Lineage 삭제 실패 - sourceKey: {}", sourceKey, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "Lineage 삭제에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * BFS 기반 Lineage 탐색
     * 
     * <p>지정된 객체를 시작점으로 BFS 알고리즘을 사용하여 
     * 연결된 모든 Lineage 관계를 방향별로 탐색합니다.</p>
     * 
     * @param objectKey 탐색할 객체의 고유 키
     * @param direction 탐색 방향 (UP: 상위 의존성, DOWN: 하위 의존성)
     * @param depth 탐색 깊이 (null인 경우 전체 탐색)
     * @return 탐색된 Lineage 관계 목록 (타입 정보 포함)
     * @throws BusinessException API 호출 실패 시
     */
    public List<LineageRelationWithTypes> getLineageByObjectKeyAndDirection(
            String objectKey, Direction direction, String action, Integer depth) {
        
        log.debug("Lineage 탐색 요청 - objectKey: {}, direction: {}, direction.value: {}, direction.toString(): {}, depth: {}", 
                 objectKey, direction, direction != null ? direction.getValue() : null, direction, depth);
        
        try {
            String directionValue = direction != null ? direction.getValue() : null;
            String actionValue = action != null ? action : ActionType.USE.getValue();
            List<LineageRelationWithTypes> result = sktaiLineageClient
                .getLineageByObjectKeyAndDirection(objectKey, directionValue, actionValue, depth);
            
            log.debug("Lineage 탐색 성공 - objectKey: {}, 결과 개수: {}", 
                     objectKey, result != null ? result.size() : 0);
            return result;
        } catch (FeignException e) {
            log.error("Lineage 탐색 실패 - objectKey: {}, direction: {}, direction.value: {}", 
                     objectKey, direction, direction != null ? direction.getValue() : null, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "Lineage 탐색에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 공유 Lineage 관계 조회
     * 
     * <p>여러 객체 간의 공통 Lineage 관계를 조회합니다.
     * 지정된 객체들이 공유하는 의존성이나 공통 조상/후손을 찾습니다.</p>
     * 
     * @param request 공유 Lineage 조회 요청
     * @return 공유되는 Lineage 관계 목록
     * @throws BusinessException API 호출 실패 시
     */
    public List<LineageRelationWithTypes> getSharedLineage(LineageObjectCreate request) {
        log.debug("공유 Lineage 조회 요청 - source: {}, target: {}", 
                 request.getSourceKey(), request.getTargetKey());
        
        try {
            List<LineageRelationWithTypes> result = sktaiLineageClient.getSharedLineage(request);
            log.debug("공유 Lineage 조회 성공 - 결과 개수: {}", 
                     result != null ? result.size() : 0);
            return result;
        } catch (FeignException e) {
            log.error("공유 Lineage 조회 실패 - source: {}, target: {}", 
                     request.getSourceKey(), request.getTargetKey(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, 
                                      "공유 Lineage 조회에 실패했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 상위 의존성 조회 (편의 메서드)
     * 
     * <p>특정 객체의 상위 의존성(부모/소스)을 조회하는 편의 메서드입니다.</p>
     * 
     * @param objectKey 조회할 객체의 고유 키
     * @param depth 탐색 깊이 (선택적)
     * @return 상위 의존성 목록
     */
    public List<LineageRelationWithTypes> getUpstreamLineage(String objectKey, Integer depth) {
        return getLineageByObjectKeyAndDirection(objectKey, Direction.UPSTREAM, ActionType.USE.getValue(), depth);
    }
    
    /**
     * 하위 의존성 조회 (편의 메서드)
     * 
     * <p>특정 객체의 하위 의존성(자식/타겟)을 조회하는 편의 메서드입니다.</p>
     * 
     * @param objectKey 조회할 객체의 고유 키
     * @param depth 탐색 깊이 (선택적)
     * @return 하위 의존성 목록
     */
    public List<LineageRelationWithTypes> getDownstreamLineage(String objectKey, Integer depth) {
        return getLineageByObjectKeyAndDirection(objectKey, Direction.DOWNSTREAM, ActionType.USE.getValue(), depth);
    }
    
    /**
     * 전체 Lineage 조회 (편의 메서드)
     * 
     * <p>특정 객체의 상위와 하위 의존성을 모두 조회하는 편의 메서드입니다.</p>
     * 
     * @param objectKey 조회할 객체의 고유 키
     * @param depth 탐색 깊이 (선택적)
     * @return 전체 의존성 목록 (상위 + 하위)
     */
    public List<LineageRelationWithTypes> getFullLineage(String objectKey, Integer depth) {
        List<LineageRelationWithTypes> upstreamLineage = getUpstreamLineage(objectKey, depth);
        List<LineageRelationWithTypes> downstreamLineage = getDownstreamLineage(objectKey, depth);
        
        upstreamLineage.addAll(downstreamLineage);
        return upstreamLineage;
    }
}