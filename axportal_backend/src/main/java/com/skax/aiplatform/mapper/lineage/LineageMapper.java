package com.skax.aiplatform.mapper.lineage;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.skax.aiplatform.client.sktai.lineage.dto.ActionType;
import com.skax.aiplatform.client.sktai.lineage.dto.Direction;
import com.skax.aiplatform.client.sktai.lineage.dto.ObjectType;
import com.skax.aiplatform.client.sktai.lineage.dto.request.LineageCreate;
import com.skax.aiplatform.client.sktai.lineage.dto.response.LineageRelationWithTypes;
import com.skax.aiplatform.dto.lineage.request.LineageCreateReq;
import com.skax.aiplatform.dto.lineage.response.LineageRelationRes;

/**
 * Lineage 매퍼
 * 
 * <p>내부 DTO와 외부 API DTO 간의 변환을 담당하는 MapStruct 매퍼입니다.</p>
 * 
 * @author gyuHeeHwang
 * @since 2025-10-19
 * @version 1.0
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface LineageMapper {
    
    /**
     * LineageCreateReq를 LineageCreate로 변환
     */
    default LineageCreate toLineageCreate(LineageCreateReq request) {
        if (request == null) {
            return null;
        }
        
        // 단일 관계를 배열로 변환
        LineageCreate.LineageItem item = LineageCreate.LineageItem.builder()
                .sourceKey(request.getSourceKey())
                .sourceType(request.getSourceType() != null ? stringToObjectType(request.getSourceType()) : ObjectType.FEW_SHOT) // 사용자 지정 또는 기본값
                .targetKey(request.getTargetKey())
                .targetType(request.getTargetType() != null ? stringToObjectType(request.getTargetType()) : ObjectType.FEW_SHOT) // 사용자 지정 또는 기본값
                .action(stringToActionType(request.getAction())) // ActionType enum으로 변환
                .build();
        
        return LineageCreate.builder()
                .lineages(List.of(item))
                .build();
    }
    
    /**
     * LineageRelationWithTypes를 LineageRelationRes로 변환
     */
    default LineageRelationRes toLineageRelationRes(LineageRelationWithTypes relation) {
        if (relation == null) {
            return null;
        }
        
        return LineageRelationRes.builder()
                .sourceKey(relation.getSourceKey())
                .targetKey(relation.getTargetKey())
                .action(actionTypeToString(relation.getAction()))
                .depth(relation.getDepth())
                .sourceType(objectTypeToString(relation.getSourceType()))
                .targetType(objectTypeToString(relation.getTargetType()))
                .build();
    }
    
    /**
     * LineageRelationWithTypes 리스트를 LineageRelationRes 리스트로 변환
     */
    default List<LineageRelationRes> toLineageRelationResList(List<LineageRelationWithTypes> relations) {
        if (relations == null || relations.isEmpty()) {
            return Collections.emptyList();
        }
        
        return relations.stream()
                .map(this::toLineageRelationRes)
                .collect(Collectors.toList());
    }
    
    /**
     * 문자열을 Direction enum으로 변환
     */
    default Direction stringToDirection(String direction) {
        if (direction == null || direction.trim().isEmpty()) {
            return null;
        }
        
        String normalizedDirection = direction.trim().toLowerCase();
        
        // upstream/downstream을 UPSTREAM/DOWNSTREAM으로 변환
        switch (normalizedDirection) {
            case "upstream":
                return Direction.UPSTREAM;
            case "downstream":
                return Direction.DOWNSTREAM;
            case "up":
                return Direction.UPSTREAM; // 하위 호환성
            case "down":
                return Direction.DOWNSTREAM; // 하위 호환성
            default:
                throw new IllegalArgumentException("Invalid direction: '" + direction + "'. Must be upstream/downstream or up/down.");
        }
    }
    
    /**
     * 문자열을 ActionType enum으로 변환
     */
    default ActionType stringToActionType(String action) {
        if (action == null || action.trim().isEmpty()) {
            return null;
        }
        
        try {
            return ActionType.valueOf(action.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid action: '" + action + "'. Must be USE or CREATE.");
        }
    }
    
    /**
     * ActionType enum을 문자열로 변환
     */
    default String actionTypeToString(ActionType action) {
        if (action == null) {
            return null;
        }
        
        return action.name();
    }
    
    /**
     * ObjectType enum을 문자열로 변환
     */
    default String objectTypeToString(ObjectType objectType) {
        if (objectType == null) {
            return null;
        }
        
        return objectType.name();
    }
    
    /**
     * 문자열을 ObjectType enum으로 변환
     */
    default ObjectType stringToObjectType(String objectType) {
        if (objectType == null || objectType.trim().isEmpty()) {
            return ObjectType.FEW_SHOT; // 기본값
        }
        
        try {
            return ObjectType.valueOf(objectType.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ObjectType.FEW_SHOT; // 기본값으로 fallback
        }
    }
}
