package com.skax.aiplatform.mapper.agent;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.skax.aiplatform.client.sktai.mcp.dto.request.McpCatalogCreateRequest;
import com.skax.aiplatform.client.sktai.mcp.dto.request.McpCatalogUpdateRequest;
import com.skax.aiplatform.client.sktai.mcp.dto.request.McpTestConnectionRequest;
import com.skax.aiplatform.client.sktai.mcp.dto.response.McpCatalogCreateResponse;
import com.skax.aiplatform.client.sktai.mcp.dto.response.McpCatalogInfo;
import com.skax.aiplatform.client.sktai.mcp.dto.response.McpCatalogListResponse;
import com.skax.aiplatform.client.sktai.mcp.dto.response.McpCatalogPingResponse;
import com.skax.aiplatform.client.sktai.mcp.dto.response.McpCatalogResponse;
import com.skax.aiplatform.client.sktai.mcp.dto.response.McpCatalogToolsResponse;
import com.skax.aiplatform.client.sktai.mcp.dto.response.McpTestConnectionResponse;
import com.skax.aiplatform.client.sktai.mcp.dto.response.McpTool;
import com.skax.aiplatform.dto.agent.request.McpCatalogCreateReq;
import com.skax.aiplatform.dto.agent.request.McpCatalogUpdateReq;
import com.skax.aiplatform.dto.agent.request.McpTestConnectionReq;
import com.skax.aiplatform.dto.agent.response.McpCatalogCreateRes;
import com.skax.aiplatform.dto.agent.response.McpCatalogDetailRes;
import com.skax.aiplatform.dto.agent.response.McpCatalogInfoRes;
import com.skax.aiplatform.dto.agent.response.McpCatalogListRes;
import com.skax.aiplatform.dto.agent.response.McpCatalogPingRes;
import com.skax.aiplatform.dto.agent.response.McpCatalogToolsRes;
import com.skax.aiplatform.dto.agent.response.McpCatalogUpdateRes;
import com.skax.aiplatform.dto.agent.response.McpTestConnectionRes;
import com.skax.aiplatform.dto.agent.response.McpToolRes;

/**
 * Agent MCP 매퍼
 * 
 * @since 2025-10-01
 * @version 1.0
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AgentMcpMapper {
    
    /**
     * String을 LocalDateTime으로 변환
     */
    default LocalDateTime stringToLocalDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return null;
        }
        try {
            // ISO 8601 형식 파싱 (예: "2025-09-15T05:21:29.606174Z")
            return LocalDateTime.parse(dateTimeString.replace("Z", ""));
        } catch (DateTimeParseException e) {
            // 파싱 실패 시 null 반환 (의도된 동작)
            // 날짜 형식이 맞지 않는 경우 정상적인 상황일 수 있으므로 에러를 무시하고 계속 진행
            return null;
        }
    }
    
    // ==================== Request 매핑 ====================
    
    /**
     * McpCatalogCreateReq를 McpCatalogCreateRequest로 변환
     */
    default McpCatalogCreateRequest toMcpCatalogCreateRequest(McpCatalogCreateReq request) {
        if (request == null) {
            return null;
        }
        
        // tags 변환 (McpCatalogCreateReq.Tag -> McpCatalogCreateRequest.Tag)
        List<McpCatalogCreateRequest.Tag> tagList = request.getTags() != null
                ? request.getTags().stream()
                    .map(tag -> McpCatalogCreateRequest.Tag.builder()
                            .name(tag.getName())
                            .build())
                    .collect(Collectors.toList())
                : Collections.emptyList();
        
        return McpCatalogCreateRequest.builder()
                .authType(request.getAuthType())
                .authConfig(request.getAuthConfig())
                .description(request.getDescription())
                .displayName(request.getDisplayName())
                .name(request.getName())
                .serverUrl(request.getServerUrl())
                .tags(tagList)
                .transportType(request.getTransportType())
                .type(request.getType())
                .additionalProperties(request.getAdditionalProperties())
                .build();
    }
    
    /**
     * McpCatalogUpdateReq를 McpCatalogUpdateRequest로 변환
     */
    default McpCatalogUpdateRequest toMcpCatalogUpdateRequest(McpCatalogUpdateReq request) {
        if (request == null) {
            return null;
        }
        
        // tags 변환 (McpCatalogUpdateReq.Tag -> McpCatalogUpdateRequest.Tag)
        List<McpCatalogUpdateRequest.Tag> tagList = request.getTags() != null
                ? request.getTags().stream()
                    .map(tag -> McpCatalogUpdateRequest.Tag.builder()
                            .name(tag.getName())
                            .build())
                    .collect(Collectors.toList())
                : Collections.emptyList();
        
        // additionalProperties에서 mcpId 제거 (경로 변수로 받는 값이므로 요청 body에 포함되면 안됨)
        Map<String, Object> filteredAdditionalProperties = new HashMap<>();
        if (request.getAdditionalProperties() != null) {
            request.getAdditionalProperties().forEach((key, value) -> {
                // mcpId, id 필드는 제외 (경로 변수로 받거나 테이블에 없는 필드)
                if (!"mcpId".equals(key) && !"id".equals(key) && !"mcp_id".equals(key)) {
                    filteredAdditionalProperties.put(key, value);
                }
            });
        }
        
        // enabled가 null이면 true로 기본값 설정 (NOT NULL 제약조건 방지)
        Boolean enabled = request.getEnabled() != null ? request.getEnabled() : true;
        
        return McpCatalogUpdateRequest.builder()
                .name(request.getName())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .type(request.getType())
                .serverUrl(request.getServerUrl())
                .authType(request.getAuthType())
                .authConfig(request.getAuthConfig())
                .tags(tagList)
                .transportType(request.getTransportType())
                .enabled(enabled)
                .additionalProperties(filteredAdditionalProperties)
                .build();
    }
    
    /**
     * McpTestConnectionReq를 McpTestConnectionRequest로 변환
     */
    McpTestConnectionRequest toMcpTestConnectionRequest(McpTestConnectionReq request);
    
    // ==================== Response 매핑 ====================
    
    /**
     * McpCatalogCreateResponse를 McpCatalogCreateRes로 변환
     */
    default McpCatalogCreateRes toMcpCatalogCreateRes(McpCatalogCreateResponse response) {
        if (response == null || response.getData() == null) {
            return null;
        }

        McpCatalogInfo detail = response.getData();
        
        List<McpCatalogCreateRes.McpTool> toolList = detail.getTools() == null
                ? Collections.emptyList()
                : detail.getTools().stream().map(this::toMcpCatalogCreateTool).collect(Collectors.toList());

        // tags 변환 (List<Tag> -> List<String>)
        List<String> tagList = detail.getTags() == null
                ? Collections.emptyList()
                : detail.getTags().stream()
                    .map(McpCatalogInfo.Tag::getName)
                    .collect(Collectors.toList());

        return McpCatalogCreateRes.builder()
                .id(detail.getId())
                .name(detail.getName())
                .displayName(detail.getDisplayName())
                .description(detail.getDescription())
                .type(detail.getType())
                .serverUrl(detail.getServerUrl())
                .authType(detail.getAuthType())
                .authConfig(detail.getAuthConfig())
                .enabled(detail.getEnabled())
                .mcpServingId(detail.getMcpServingId())
                .gatewayEndpoint(detail.getGatewayEndpoint())
                .tags(tagList)
                .transportType(detail.getTransportType())
                .createdAt(stringToLocalDateTime(detail.getCreatedAt()))
                .updatedAt(stringToLocalDateTime(detail.getUpdatedAt()))
                .tools(toolList)
                .build();
    }
    
    /**
     * McpTool을 McpCatalogCreateRes.McpTool로 변환
     */
    default McpCatalogCreateRes.McpTool toMcpCatalogCreateTool(McpTool tool) {
        if (tool == null) {
            return null;
        }
        
        return McpCatalogCreateRes.McpTool.builder()
                .name(tool.getName())
                .title(tool.getTitle())
                .description(tool.getDescription())
                .inputSchema(tool.getInputSchema())
                .outputSchema(tool.getOutputSchema())
                .annotations(tool.getAnnotations())
                .meta(tool.getMeta())
                .build();
    }
    
    /**
     * McpCatalogResponse를 McpCatalogUpdateRes로 변환
     */
    default McpCatalogUpdateRes toMcpCatalogUpdateRes(McpCatalogResponse response) {
        if (response == null || response.getData() == null) {
            return null;
        }

        McpCatalogInfo detail = response.getData();
        
        List<McpCatalogUpdateRes.McpTool> toolList = detail.getTools() == null
                ? Collections.emptyList()
                : detail.getTools().stream().map(this::toMcpTool).collect(Collectors.toList());

        // tags 변환 (List<Tag> -> List<String>)
        List<String> tagList = detail.getTags() == null
                ? Collections.emptyList()
                : detail.getTags().stream()
                    .map(McpCatalogInfo.Tag::getName)
                    .collect(Collectors.toList());

        return McpCatalogUpdateRes.builder()
                .id(detail.getId())
                .name(detail.getName())
                .displayName(detail.getDisplayName())
                .description(detail.getDescription())
                .type(detail.getType())
                .serverUrl(detail.getServerUrl())
                .authType(detail.getAuthType())
                .authConfig(detail.getAuthConfig())
                .enabled(detail.getEnabled())
                .mcpServingId(detail.getMcpServingId())
                .gatewayEndpoint(detail.getGatewayEndpoint())
                .tags(tagList)
                .transportType(detail.getTransportType())
                .createdAt(detail.getCreatedAt())
                .updatedAt(detail.getUpdatedAt())
                .tools(toolList)
                .build();
    }
    
    /**
     * McpTool을 McpCatalogUpdateRes.McpTool로 변환
     */
    default McpCatalogUpdateRes.McpTool toMcpTool(McpTool tool) {
        if (tool == null) {
            return null;
        }
        
        return McpCatalogUpdateRes.McpTool.builder()
                .name(tool.getName())
                .title(tool.getTitle())
                .description(tool.getDescription())
                .inputSchema(tool.getInputSchema())
                .outputSchema(tool.getOutputSchema())
                .annotations(tool.getAnnotations())
                .meta(tool.getMeta())
                .build();
    }

    /**
     * McpCatalogListResponse를 McpCatalogListRes로 변환
     */
    default McpCatalogListRes toMcpCatalogListRes(McpCatalogListResponse response) {
        if (response == null) {
            return null;
        }
        
        // 카탈로그 목록 변환
        List<McpCatalogInfoRes> catalogList = response.getData() == null
                ? Collections.emptyList()
                : response.getData().stream()
                    .map(this::toMcpCatalogInfoResFromDetail)
                    .collect(Collectors.toList());
        
        // 페이로드 정보 변환 (Object로 직접 전달)
        Object payloadInfo = response.getPayload();
        
        return McpCatalogListRes.builder()
                .timestamp(response.getTimestamp())
                .code(response.getCode())
                .detail(response.getDetail())
                .traceId(response.getTraceId())
                .data(catalogList)
                .payload(payloadInfo)
                .build();
    }
    
    
    /**
     * McpCatalogResponse를 McpCatalogDetailRes로 변환
     */
    McpCatalogDetailRes toMcpCatalogDetailRes(McpCatalogResponse response);
    
    /**
     * McpCatalogToolsResponse를 McpCatalogToolsRes로 변환
     */
    McpCatalogToolsRes toMcpCatalogToolsRes(McpCatalogToolsResponse response);
    
    /**
     * McpTestConnectionResponse를 McpTestConnectionRes로 변환
     */
    default McpTestConnectionRes toMcpTestConnectionRes(McpTestConnectionResponse response) {
        if (response == null || response.getData() == null) {
            return null;
        }
        
        McpTestConnectionResponse.ConnectionTestResult data = response.getData();
        
        // 외부 API에서 is_connected가 null로 오는 경우 처리
        Boolean isConnected = data.getIsConnected();
        if (isConnected == null) {
            // code가 1이면 성공으로 간주
            isConnected = (response.getCode() != null && response.getCode() == 1);
        }
        
        return McpTestConnectionRes.builder()
                .isConnected(isConnected)
                .errorMessage(data.getErrorMessage())
                .build();
    }

    /**
     * McpCatalogPingResponse를 McpCatalogPingRes로 변환
     */
    default McpCatalogPingRes toMcpCatalogPingRes(McpCatalogPingResponse response) {
        if (response == null || response.getData() == null) {
            return null;
        }
        
        McpCatalogPingResponse.McpCatalogPing detail = response.getData();
        
        return McpCatalogPingRes.builder()
                .isConnected(detail.getIsConnected())
                .errorMessage(detail.getErrorMessage())
                .build();
    }

    /**
     * McpCatalogResponse를 McpCatalogInfoRes로 변환
     */
    default McpCatalogInfoRes toMcpCatalogInfoRes(McpCatalogResponse mcpCatalogResponse) {
        if (mcpCatalogResponse == null) {
            return null;
        }

        McpCatalogInfo detail = mcpCatalogResponse.getData();
        
        List<McpCatalogInfoRes.McpTool> toolList = detail.getTools() == null
                ? Collections.emptyList()
                : detail.getTools().stream().map(this::toMcpCatalogInfoTool).collect(Collectors.toList());

        // tags 변환
        List<McpCatalogInfoRes.McpTag> tagList = detail.getTags() == null
                ? Collections.emptyList()
                : detail.getTags().stream()
                    .map(tag -> McpCatalogInfoRes.McpTag.builder()
                            .name(tag.getName())
                            .build())
                    .collect(Collectors.toList());

        return McpCatalogInfoRes.builder()
                .id(detail.getId())
                .name(detail.getName())
                .displayName(detail.getDisplayName())
                .description(detail.getDescription())
                .type(detail.getType())
                .serverUrl(detail.getServerUrl())
                .authType(detail.getAuthType())
                .authConfig(detail.getAuthConfig())
                .enabled(detail.getEnabled())
                .mcpServingId(detail.getMcpServingId())
                .gatewayEndpoint(detail.getGatewayEndpoint())
                .tags(tagList)
                .transportType(detail.getTransportType())
                .createdAt(detail.getCreatedAt())
                .updatedAt(detail.getUpdatedAt())
                .createdBy(detail.getCreatedBy())
                .updatedBy(detail.getUpdatedBy())
                .tools(toolList)
                .build();
    }
    
    /**
     * McpTool을 McpCatalogInfoRes.McpTool로 변환
     */
    default McpCatalogInfoRes.McpTool toMcpCatalogInfoTool(McpTool tool) {
        if (tool == null) {
            return null;
        }
        
        return McpCatalogInfoRes.McpTool.builder()
                .name(tool.getName())
                .title(tool.getTitle())
                .description(tool.getDescription())
                .inputSchema(tool.getInputSchema())
                .outputSchema(tool.getOutputSchema())
                .annotations(tool.getAnnotations())
                .meta(tool.getMeta())
                .build();
    }

    /**
     * McpCatalogInfo (list item) 를 McpCatalogInfoRes로 변환
     */
    default McpCatalogInfoRes toMcpCatalogInfoResFromDetail(McpCatalogInfo detail) {
        if (detail == null) {
            return null;
        }
        
        // tools 목록 변환
        List<McpCatalogInfoRes.McpTool> toolList = detail.getTools() == null
                ? Collections.emptyList()
                : detail.getTools().stream().map(this::toMcpCatalogInfoTool).collect(Collectors.toList());
        
        // tags 변환
        List<McpCatalogInfoRes.McpTag> tagList = detail.getTags() == null
                ? Collections.emptyList()
                : detail.getTags().stream()
                    .map(tag -> McpCatalogInfoRes.McpTag.builder()
                            .name(tag.getName())
                            .build())
                    .collect(Collectors.toList());

        return McpCatalogInfoRes.builder()
                .id(detail.getId())
                .name(detail.getName())
                .displayName(detail.getDisplayName())
                .description(detail.getDescription())
                .type(detail.getType())
                .serverUrl(detail.getServerUrl())
                .authType(detail.getAuthType())
                .authConfig(detail.getAuthConfig())
                .enabled(detail.getEnabled())
                .mcpServingId(detail.getMcpServingId())
                .gatewayEndpoint(detail.getGatewayEndpoint())
                .tags(tagList)
                .transportType(detail.getTransportType())
                .createdAt(detail.getCreatedAt())
                .updatedAt(detail.getUpdatedAt())
                .createdBy(detail.getCreatedBy())
                .updatedBy(detail.getUpdatedBy())
                .tools(toolList)
                .build();
    }

    /**
     * McpTool를 McpToolRes로 변환
     */
    default McpToolRes toMcpToolRes(McpTool tool) {
        if (tool == null) {
            return null;
        }
        return McpToolRes.builder()
                .name(tool.getName())
                .title(tool.getTitle())
                .description(tool.getDescription())
                .inputSchema(tool.getInputSchema()) // Object 타입으로 직접 매핑
                .outputSchema(tool.getOutputSchema())
                .annotations(tool.getAnnotations()) // Object 타입으로 직접 매핑
                .meta(tool.getMeta())
                .build();
    }
} 