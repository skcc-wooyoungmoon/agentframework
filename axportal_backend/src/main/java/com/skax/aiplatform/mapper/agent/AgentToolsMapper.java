package com.skax.aiplatform.mapper.agent;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.skax.aiplatform.client.sktai.agent.dto.request.ToolRequest;
import com.skax.aiplatform.client.sktai.agent.dto.response.ToolCreateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.ToolResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.ToolUpdateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.ToolsResponse;
import com.skax.aiplatform.dto.agent.request.AgentToolReq;
import com.skax.aiplatform.dto.agent.response.AgentToolCreateRes;
import com.skax.aiplatform.dto.agent.response.AgentToolRes;
import com.skax.aiplatform.dto.agent.response.AgentToolUpdateRes;

/**
 * Agent Tools 매퍼
 * 
 * <p>내부 DTO와 외부 API DTO 간의 변환을 담당하는 MapStruct 매퍼입니다.</p>
 * 
 * @author gyuHeeHwang
 * @since 2025-08-21
 * @version 1.0.0
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AgentToolsMapper {
    
    /**
     * ToolResponse를 AgentToolRes로 변환
     */
    default AgentToolRes from(ToolResponse toolResponse) {
        if (toolResponse == null || toolResponse.getData() == null) {
            return null;
        }
        
        ToolResponse.ToolsDetail detail = toolResponse.getData();
        
        AgentToolRes.AgentToolResBuilder builder = AgentToolRes.builder()
                .id(detail.getId())
                .name(detail.getName())
                .displayName(detail.getDisplayName())
                .description(detail.getDescription())
                .toolType(detail.getToolType())
                .code(detail.getCode())
                .projectId(detail.getProjectId())
                .createdAt(detail.getCreatedAt())
                .updatedAt(detail.getUpdatedAt())
                .createdBy(detail.getCreatedBy())
                .updatedBy(detail.getUpdatedBy())
                .tags(convertTags(detail.getTags()));
        
        // toolType에 따라 조건부로 필드 설정
        if ("custom_api".equals(detail.getToolType())) {
            // custom_api 타입: serverUrl, method, apiParam 설정
            builder.serverUrl(detail.getServerUrl())
                   .method(detail.getMethod())
                   .apiParam(detail.getApiParam());
        } else if ("custom_code".equals(detail.getToolType())) {
            // custom_code 타입: inputKeys 설정
            builder.inputKeys(convertInputKeys(detail.getInputKeys()));
        }
        
        return builder.build();
    }
    
    /**
     * ToolsResponse.ToolsDetail을 AgentToolRes로 변환
     */
    default AgentToolRes from(ToolsResponse.ToolsDetail detail) {
        if (detail == null) {
            return null;
        }
        
        AgentToolRes.AgentToolResBuilder builder = AgentToolRes.builder()
                .id(detail.getId())
                .name(detail.getName())
                .displayName(detail.getDisplayName())
                .description(detail.getDescription())
                .toolType(detail.getToolType())
                .code(detail.getCode())
                .projectId(detail.getProjectId())
                .createdAt(detail.getCreatedAt())
                .updatedAt(detail.getUpdatedAt())
                .tags(convertTagsFromToolsResponse(detail.getTags()));
        
        // createdBy와 updatedBy는 AuditorInfo 타입이므로 여기서는 설정하지 않음
        
        // toolType에 따라 조건부로 필드 설정
        if ("custom_api".equals(detail.getToolType())) {
            // custom_api 타입: serverUrl, method, apiParam 설정
            builder.serverUrl(detail.getServerUrl())
                   .method(detail.getMethod())
                   .apiParam(detail.getApiParam());
        } else if ("custom_code".equals(detail.getToolType())) {
            // custom_code 타입: inputKeys 설정
            builder.inputKeys(convertInputKeysFromToolsResponse(detail.getInputKeys()));
        }
        
        return builder.build();
    }
    
    /**
     * ToolsResponse를 AgentToolRes 리스트로 변환 (목록 조회용)
     */
    default List<AgentToolRes> fromList(ToolsResponse toolsResponse) {
        if (toolsResponse == null || toolsResponse.getData() == null) {
            return List.of();
        }
        
        return toolsResponse.getData().stream()
                .map(this::from)
                .collect(Collectors.toList());
    }
    
    /**
     * AgentToolReq를 ToolRequest로 변환 (생성/수정용)
     */
    default ToolRequest toRequest(AgentToolReq request) {
        if (request == null) {
            return null;
        }
        
        return ToolRequest.builder()
                .name(request.getName())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .projectId(request.getProjectId())
                .toolType(request.getToolType())
                .code(request.getCode())
                .serverUrl(request.getServerUrl())
                .method(request.getMethod())
                .apiParam(request.getApiParam())
                .tags(request.getTags())
                .build();
    }
    
    /**
     * ToolCreateResponse를 AgentToolCreateRes로 변환
     */
    default AgentToolCreateRes toCreateResponse(ToolCreateResponse response) {
        if (response == null || response.getData() == null) {
            return null;
        }
        
        return AgentToolCreateRes.builder()
                .agentToolUuid(response.getData().getId())
                .build();
    }
    
    /**
     * ToolUpdateResponse를 AgentToolUpdateRes로 변환
     */
    default AgentToolUpdateRes toUpdateResponse(ToolUpdateResponse response) {
        if (response == null || response.getData() == null) {
            return null;
        }
        
        ToolUpdateResponse.ToolsDetail detail = response.getData();
        
        return AgentToolUpdateRes.builder()
                .id(detail.getId())
                .name(detail.getName())
                .displayName(detail.getDisplayName())
                .description(detail.getDescription())
                .toolType(detail.getToolType())
                .code(detail.getCode())
                .projectId(detail.getProjectId())
                .createdAt(detail.getCreatedAt() != null ? detail.getCreatedAt().toString() : null)
                .updatedAt(detail.getUpdatedAt() != null ? detail.getUpdatedAt().toString() : null)
                .createdBy(detail.getCreatedBy())
                .updatedBy(detail.getUpdatedBy())
                .inputKeys(detail.getInputKeys())
                .serverUrl(detail.getServerUrl())
                .method(detail.getMethod())
                .apiParam(detail.getApiParam())
                .tags(detail.getTags())
                .build();
    }
    
    /**
     * InputKey 리스트 변환 (ToolResponse.ToolsDetail.InputKey → AgentToolRes.InputKey)
     */
    default List<AgentToolRes.InputKey> convertInputKeys(List<ToolResponse.InputKey> inputKeys) {
        if (inputKeys == null) {
            return List.of();
        }
        
        return inputKeys.stream()
                .map(inputKey -> AgentToolRes.InputKey.builder()
                        .key(inputKey.getKey())
                        .comment(inputKey.getComment())
                        .required(inputKey.getRequired())
                        .type(inputKey.getType())
                        .defaultValue(inputKey.getDefaultValue())
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * InputKey 리스트 변환 (ToolsResponse.ToolsDetail.InputKey → AgentToolRes.InputKey)
     */
    default List<AgentToolRes.InputKey> convertInputKeysFromToolsResponse(List<ToolsResponse.InputKey> inputKeys) {
        if (inputKeys == null) {
            return List.of();
        }
        
        return inputKeys.stream()
                .map(inputKey -> AgentToolRes.InputKey.builder()
                        .key(inputKey.getKey())
                        .comment(inputKey.getComment())
                        .required(inputKey.getRequired())
                        .type(inputKey.getType())
                        .defaultValue(inputKey.getDefaultValue())
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * Tag 리스트 변환 (ToolResponse.Tag → String)
     */
    default List<String> convertTags(List<ToolResponse.Tag> tags) {
        if (tags == null) {
            return List.of();
        }
        
        return tags.stream()
                .map(tag -> tag != null ? tag.getName() : null)
                .filter(name -> name != null)
                .collect(Collectors.toList());
    }
    
    /**
     * ToolsResponse.Tag 리스트를 String 리스트로 변환
     */
    default List<String> convertTagsFromToolsResponse(List<ToolsResponse.Tag> tags) {
        if (tags == null) {
            return List.of();
        }
        
        return tags.stream()
                .map(tag -> tag != null ? tag.getName() : null)
                .filter(name -> name != null)
                .collect(Collectors.toList());
    }
}