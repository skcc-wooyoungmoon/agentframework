package com.skax.aiplatform.mapper.agent;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.skax.aiplatform.client.sktai.agent.dto.response.AppUpdateOrDeleteResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphAppResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.GraphResponse;
import com.skax.aiplatform.dto.agent.response.AgentAppInfoRes;
import com.skax.aiplatform.dto.agent.response.AgentBuilderRes;
import com.skax.aiplatform.dto.deploy.response.AgentDeployUpdateOrDeleteRes;

/**
 * 에이전트 빌더 매퍼
 * 
 * <p>
 * SKT AI Platform의 GraphResponse를 AgentBuilderRes로 변환하는 매퍼입니다.
 * </p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-19
 * @version 1.0.0
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface AgentBuilderMapper {

    /**
     * GraphResponse를 AgentBuilderRes로 변환
     * 
     * @param graphResponse SKT AI Platform 그래프 응답
     * @return 에이전트 빌더 응답
     */
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "category", target = "category")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    @Mapping(source = "createdBy", target = "createdBy")
    @Mapping(source = "updatedBy", target = "updatedBy")
    @Mapping(source = "nodes", target = "nodeCount", qualifiedByName = "listToCount")
    @Mapping(source = "edges", target = "edgeCount", qualifiedByName = "listToCount")
    @Mapping(source = "nodes", target = "nodes", qualifiedByName = "objectListToMapList")
    @Mapping(source = "edges", target = "edges", qualifiedByName = "objectListToMapList")
    @Mapping(target = "phoenixProjectId", ignore = true)
    AgentBuilderRes toAgentBuilderRes(GraphResponse graphResponse);

    /**
     * GraphResponse 리스트를 AgentBuilderRes 리스트로 변환
     * 
     * @param graphResponses SKT AI Platform 그래프 응답 리스트
     * @return 에이전트 빌더 응답 리스트
     */
    List<AgentBuilderRes> toAgentBuilderResList(List<GraphResponse> graphResponses);

    /**
     * 문자열을 LocalDateTime으로 변환 (스마트 시간대 처리)
     * 
     * @param dateTimeString 날짜 시간 문자열
     * @return LocalDateTime
     */
    @Named("stringToLocalDateTime")
    default LocalDateTime stringToLocalDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return null;
        }

        // 🔥 디버깅용 로깅 추가
        System.out.println("🕐 [AgentBuilderMapper] 시간 변환 시작: " + dateTimeString);

        try {
            // 1. 시간대 정보가 포함된 ISO 8601 형식 파싱 시도 (예: "2024-11-27T04:00:00Z" 또는
            // "2024-11-27T04:00:00+00:00")
            if (dateTimeString.contains("Z") || dateTimeString.contains("+") || dateTimeString.contains("-")) {
                System.out.println("🕐 [AgentBuilderMapper] UTC 시간으로 인식: " + dateTimeString);
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME);
                LocalDateTime result = zonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDateTime();
                System.out.println("🕐 [AgentBuilderMapper] UTC → KST 변환 결과: " + result);
                return result;
            } else {
                // 2. 시간대 정보가 없는 경우 - 그대로 사용 (이미 로컬 시간으로 간주)
                System.out.println("🕐 [AgentBuilderMapper] 로컬 시간으로 인식: " + dateTimeString);
                LocalDateTime result = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_DATE_TIME);
                System.out.println("🕐 [AgentBuilderMapper] 로컬 시간 파싱 결과: " + result);
                return result;
            }
        } catch (DateTimeParseException e) {
            try {
                // 3. 다른 형식 시도 (예: "2024-11-27 04:00:00")
                System.out.println("🕐 [AgentBuilderMapper] 대체 형식으로 파싱 시도: " + dateTimeString);
                LocalDateTime result = LocalDateTime.parse(dateTimeString,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                System.out.println("🕐 [AgentBuilderMapper] 대체 형식 파싱 결과: " + result);
                return result;
            } catch (DateTimeParseException ex) {
                // 파싱 실패 시 null 반환 (의도된 동작)
                System.out.println("🕐 [AgentBuilderMapper] 시간 파싱 실패: " + dateTimeString);
                return null;
            }
        }
    }

    /**
     * 리스트의 크기를 반환
     * 
     * @param list 리스트
     * @return 리스트 크기
     */
    @Named("listToCount")
    default Integer listToCount(List<?> list) {
        return list != null ? list.size() : 0;
    }

    /**
     * Object 리스트를 Map 리스트로 변환
     * 
     * @param objectList Object 리스트
     * @return Map 리스트
     */
    @Named("objectListToMapList")
    @SuppressWarnings("unchecked")
    default List<Map<String, Object>> objectListToMapList(List<Object> objectList) {
        if (objectList == null) {
            return null;
        }
        return objectList.stream()
                .filter(obj -> obj instanceof Map)
                .map(obj -> (Map<String, Object>) obj)
                .collect(Collectors.toList());
    }

    @Mapping(source = "appUuid", target = "appUuid")
    @Mapping(source = "success", target = "success")
    @Mapping(source = "message", target = "message")
    @Mapping(source = "updatedAt", target = "updatedAt")
    AgentDeployUpdateOrDeleteRes toDeployResFromAppUpdateOrDeleteResponse(
            AppUpdateOrDeleteResponse appUpdateOrDeleteResponse);

    /**
     * GraphAppResponse를 AgentAppInfoRes로 변환
     * 
     * @param graphAppResponse SKT AI Platform 그래프 앱 응답
     * @return 에이전트 앱 정보 응답
     */
    @Mapping(source = "data.id", target = "id")
    @Mapping(source = "data.name", target = "name")
    @Mapping(source = "data.description", target = "description")
    AgentAppInfoRes toAgentAppInfoRes(GraphAppResponse graphAppResponse);
}