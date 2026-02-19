package com.skax.aiplatform.service.lineage.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.skax.aiplatform.client.sktai.lineage.SktaiLineageClient;
import com.skax.aiplatform.client.sktai.lineage.dto.ActionType;
import com.skax.aiplatform.client.sktai.lineage.dto.Direction;
import com.skax.aiplatform.client.sktai.lineage.dto.request.LineageCreate;
import com.skax.aiplatform.client.sktai.lineage.dto.response.LineageRelationWithTypes;
import com.skax.aiplatform.dto.lineage.request.LineageCreateReq;
import com.skax.aiplatform.dto.lineage.request.LineageSearchReq;
import com.skax.aiplatform.dto.lineage.response.LineageRelationRes;
import com.skax.aiplatform.mapper.lineage.LineageMapper;
import com.skax.aiplatform.service.lineage.LineageService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Lineage 관리 서비스 구현체
 * 
 * <p>Lineage 관계 관리를 위한 비즈니스 로직을 구현합니다.
 * SKTAI Lineage API와의 연동을 통해 객체 간의 의존성과 데이터 흐름을 관리합니다.</p>
 * 
 * @author gyuHeeHwang
 * @since 2025-10-19
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LineageServiceImpl implements LineageService {
    
    private final SktaiLineageClient sktaiLineageClient;
    private final LineageMapper lineageMapper;
    
    @Override
    public void createLineage(LineageCreateReq request) {
        try {
            log.debug("Lineage 생성 요청: {}", request);
            
            LineageCreate lineageCreate = lineageMapper.toLineageCreate(request);
            sktaiLineageClient.createLineage(lineageCreate);
            
            log.debug("Lineage 생성 완료: sourceKey={}, targetKey={}", 
                     request.getSourceKey(), request.getTargetKey());
                     
        } catch (FeignException e) {
            log.error("Lineage 생성 실패: sourceKey={}, targetKey={}, 에러={}", request.getSourceKey(), request.getTargetKey(), e.getMessage());
            log.debug("Lineage 생성 실패 상세: {}", e.contentUTF8());
            throw new RuntimeException("Lineage 생성에 실패했습니다: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<LineageRelationRes> getLineage(LineageSearchReq request) {
        try {
            log.debug("Lineage 조회 요청: objectKey={}, direction={}, action={}, depth={}", 
                     request.getObjectKey(), request.getDirection(), request.getAction(), request.getDepth());
            
            Direction direction = lineageMapper.stringToDirection(request.getDirection());
            String directionValue = direction != null ? direction.getValue() : null;
            String action = request.getAction() != null ? request.getAction() : ActionType.USE.getValue();
            List<LineageRelationWithTypes> relations = sktaiLineageClient.getLineageByObjectKeyAndDirection(
                    request.getObjectKey(), directionValue, action, request.getDepth());
            
            List<LineageRelationRes> result = lineageMapper.toLineageRelationResList(relations);
            
            log.debug("Lineage 조회 완료: objectKey={}, direction={}, action={}, relationsCount={}", 
                     request.getObjectKey(), request.getDirection(), request.getAction(), result.size());
                     
            return result;
            
        } catch (FeignException e) {
            log.error("Lineage 조회 실패: objectKey={}, direction={}, error={}", 
                     request.getObjectKey(), request.getDirection(), e.getMessage(), e);
            log.debug("Lineage 조회 실패 상세: {}", e.contentUTF8());
            throw new RuntimeException("Lineage 관계 조회에 실패했습니다: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<LineageRelationRes> getSharedLineage(List<String> objectKeys) {
        try {
            log.debug("공유 Lineage 조회 요청: objectKeys={}", objectKeys);
            
            // TODO: 공유 Lineage 조회를 위한 별도의 DTO가 필요합니다.
            // 현재 LineageObjectCreate는 객체 생성용이므로 공유 조회에는 적합하지 않습니다.
            throw new UnsupportedOperationException("공유 Lineage 조회 기능은 아직 구현되지 않았습니다.");
            
        } catch (FeignException e) {
            log.error("공유 Lineage 조회 실패: objectKeys={}, error={}", 
                     objectKeys, e.getMessage(), e);
            throw new RuntimeException("공유 Lineage 관계 조회에 실패했습니다: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteLineage(String sourceKey) {
        try {
            log.debug("Lineage 삭제 요청: sourceKey={}", sourceKey);
            
            sktaiLineageClient.deleteLineage(sourceKey);
            
            log.debug("Lineage 삭제 완료: sourceKey={}", sourceKey);
            
        } catch (FeignException e) {
            log.error("Lineage 삭제 실패: sourceKey={}, error={}", 
                     sourceKey, e.getMessage(), e);
            throw new RuntimeException("Lineage 관계 삭제에 실패했습니다: " + e.getMessage(), e);
        }
    }
    
}
