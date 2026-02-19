package com.skax.aiplatform.mapper.deploy;

import java.util.List;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import com.skax.aiplatform.client.elastic.search.dto.request.SearchRequest;
import com.skax.aiplatform.client.elastic.search.dto.response.SearchResponse;
import com.skax.aiplatform.client.sktai.agent.dto.request.AppCreateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.AppUpdateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.response.AppApiKeysResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.AppCreateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.AppDeploymentResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.AppDeploymentsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.AppResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.AppUpdateOrDeleteResponse;
import com.skax.aiplatform.client.sktai.agentgateway.dto.request.StreamRequest;
// import com.skax.aiplatform.client.sktai.agentgateway.dto.response.*;
import com.skax.aiplatform.client.sktai.serving.dto.response.AgentServingResponse;
import com.skax.aiplatform.dto.deploy.request.AgentSysLogSearchReq;
import com.skax.aiplatform.dto.deploy.request.AppCreateReq;
import com.skax.aiplatform.dto.deploy.request.AppUpdateReq;
import com.skax.aiplatform.dto.deploy.request.StreamReq;
import com.skax.aiplatform.dto.deploy.response.AgentAppRes;
import com.skax.aiplatform.dto.deploy.response.AgentDeployRes;
import com.skax.aiplatform.dto.deploy.response.AgentDeployUpdateOrDeleteRes;
import com.skax.aiplatform.dto.deploy.response.AgentServingRes;
import com.skax.aiplatform.dto.deploy.response.AppApiKeysRes;
import com.skax.aiplatform.dto.deploy.response.AppCreateRes;
import com.skax.aiplatform.dto.deploy.response.AppUpdateRes;

/**
 * Agent 배포 매퍼
 * 
 * @author ByounggwanLee
 * @since 2025-08-30
 * @version 1.0
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AgentDeployMapper {

    /**
     * AppDeploymentResponse를 AgentDeployRes로 변환
     */
    @Mapping(target = "appId", source = "data.appId")
    @Mapping(target = "description", source = "data.description")
    @Mapping(target = "servingType", source = "data.servingType")
    @Mapping(target = "imageTag", source = "data.imageTag")
    @Mapping(target = "outputType", source = "data.outputType")
    @Mapping(target = "deployedDt", source = "data.deployedDt")
    @Mapping(target = "createdBy", source = "data.createdBy")
    @Mapping(target = "servingId", source = "data.servingId")
    @Mapping(target = "targetId", source = "data.targetId")
    @Mapping(target = "targetType", source = "data.targetType")
    @Mapping(target = "version", source = "data.version")
    @Mapping(target = "id", source = "data.id")
    @Mapping(target = "status", source = "data.status")
    @Mapping(target = "endpoint", source = "data.endpoint")
    @Mapping(target = "deploymentConfigPath", source = "data.deploymentConfigPath")
    @Mapping(target = "deleteFlag", source = "data.deleteFlag")
    @Mapping(target = "inputKeys", source = "data.inputKeys", qualifiedByName = "mapInputKeys")
    @Mapping(target = "outputKeys", source = "data.outputKeys", qualifiedByName = "mapOutputKeys")
    AgentDeployRes toDeployRes(AppDeploymentResponse response);
    
    /**
     * AppResponse를 AgentAppRes로 변환
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "targetId", source = "targetId")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "deploymentVersion", source = "deploymentVersion")
    @Mapping(target = "deploymentStatus", source = "deploymentStatus")
    @Mapping(target = "servingType", source = "servingType")
    @Mapping(target = "deployments", source = "deployments", qualifiedByName = "mapDeployments")
    @Mapping(target = "inputKeys", source = "inputKeys", qualifiedByName = "mapInputKeysFromAppsForAgentApp")
    @Mapping(target = "outputKeys", source = "outputKeys", qualifiedByName = "mapOutputKeysFromAppsForAgentApp")
    @Mapping(target = "outputType", source = "outputType")
    AgentAppRes toDeployResFromApps(AppResponse app);
    
    /**
     * AppsResponse의 data 필드 내 AppResponse를 AgentAppRes로 변환
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "targetId", source = "targetId")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "deploymentVersion", source = "deploymentVersion")
    @Mapping(target = "deploymentStatus", source = "deploymentStatus")
    @Mapping(target = "servingType", source = "servingType")
    @Mapping(target = "deployments", source = "deployments", qualifiedByName = "mapDeployments")
    @Mapping(target = "inputKeys", source = "inputKeys", qualifiedByName = "mapInputKeysFromAppsForAgentApp")
    @Mapping(target = "outputKeys", source = "outputKeys", qualifiedByName = "mapOutputKeysFromAppsForAgentApp")
    @Mapping(target = "outputType", source = "outputType")
    AgentAppRes toDeployResFromAppsData(AppResponse app);

    
    /**
     * AppDeploymentsResponse를 AgentDeployRes 리스트로 변환
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "appId", source = "appId")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "servingType", source = "servingType")
    @Mapping(target = "imageTag", source = "imageTag")
    @Mapping(target = "outputType", source = "outputType")
    @Mapping(target = "deployedDt", source = "deployedDt")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "servingId", source = "servingId")
    @Mapping(target = "targetId", source = "targetId")
    @Mapping(target = "targetType", source = "targetType")
    @Mapping(target = "version", source = "version")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "deploymentConfigPath", source = "deploymentConfigPath")
    @Mapping(target = "deleteFlag", source = "deleteFlag")
    @Mapping(target = "inputKeys", source = "inputKeys", qualifiedByName = "mapInputKeysFromAppDeployments")
    @Mapping(target = "outputKeys", source = "outputKeys", qualifiedByName = "mapOutputKeysFromAppDeployments")
    AgentDeployRes toDeployResFromAppDeployment(AppDeploymentsResponse.AppDeploymentInfo deployment);
    
//     /**
//      * AppCreateResponse를 AgentDeployRes로 변환
//      */
//     @Mapping(target = "appId", source = "appId")
//     @Mapping(target = "description", constant = "")
//     @Mapping(target = "status", constant = "ACTIVE")
//     AgentDeployRes toDeployResFromAppCreateResponse(AppCreateResponse response);
    
    /**
     * InputKey 변환을 위한 Named 메서드
     */
    @Named("mapInputKeys")
    default List<AgentDeployRes.InputKey> mapInputKeys(List<AppDeploymentResponse.AppDeploymentInfo.InputKey> inputKeys) {
        if (inputKeys == null) return null;
        return inputKeys.stream()
                .map(inputKey -> AgentDeployRes.InputKey.builder()
                        .name(inputKey.getName())
                        .required(inputKey.getRequired())
                        .keytableId(inputKey.getKeytableId())
                        .build())
                .toList();
    }
    
    /**
     * OutputKey 변환을 위한 Named 메서드
     */
    @Named("mapOutputKeys")
    default List<AgentDeployRes.OutputKey> mapOutputKeys(List<AppDeploymentResponse.AppDeploymentInfo.OutputKey> outputKeys) {
        if (outputKeys == null) return null;
        return outputKeys.stream()
                .map(outputKey -> AgentDeployRes.OutputKey.builder()
                        .name(outputKey.getName())
                        .keytableId(outputKey.getKeytableId())
                        .build())
                .toList();
    }
    
    /**
     * AppDeploymentsResponse InputKey 변환을 위한 Named 메서드
     */
    @Named("mapInputKeysFromAppDeployments")
    default List<AgentDeployRes.InputKey> mapInputKeysFromAppDeployments(List<AppDeploymentsResponse.InputKey> inputKeys) {
        if (inputKeys == null) return null;
        return inputKeys.stream()
                .map(inputKey -> AgentDeployRes.InputKey.builder()
                        .name(inputKey.getName())
                        .required(inputKey.getRequired())
                        .keytableId(inputKey.getKeytableId())
                        .build())
                .toList();
    }
    
    /**
     * AppDeploymentsResponse OutputKey 변환을 위한 Named 메서드
     */
    @Named("mapOutputKeysFromAppDeployments")
    default List<AgentDeployRes.OutputKey> mapOutputKeysFromAppDeployments(List<AppDeploymentsResponse.OutputKey> outputKeys) {
        if (outputKeys == null) return null;
        return outputKeys.stream()
                .map(outputKey -> AgentDeployRes.OutputKey.builder()
                        .name(outputKey.getName())
                        .keytableId(outputKey.getKeytableId())
                        .build())
                .toList();
    }
    
    /**
     * AppResponse InputKey를 AgentAppRes.InputKey로 변환하는 Named 메서드
     */
    @Named("mapInputKeysFromAppsForAgentApp")
    default List<AgentAppRes.InputKey> mapInputKeysFromAppsForAgentApp(List<AppResponse.InputKey> inputKeys) {
        if (inputKeys == null) return null;
        return inputKeys.stream()
                .map(inputKey -> AgentAppRes.InputKey.builder()
                        .name(inputKey.getName())
                        .required(inputKey.getRequired())
                        .keytableId(inputKey.getKeytableId())
                        .description(inputKey.getDescription())
                        .fixedValue(inputKey.getFixedValue())
                        .build())
                .toList();
    }
    
    /**
     * AppResponse OutputKey를 AgentAppRes.OutputKey로 변환하는 Named 메서드
     */
    @Named("mapOutputKeysFromAppsForAgentApp")
    default List<AgentAppRes.OutputKey> mapOutputKeysFromAppsForAgentApp(List<AppResponse.OutputKey> outputKeys) {
        if (outputKeys == null) return null;
        return outputKeys.stream()
                .map(outputKey -> AgentAppRes.OutputKey.builder()
                        .name(outputKey.getName())
                        .keytableId(outputKey.getKeytableId())
                        .build())
                .toList();
    }
    
    /**
     * AppResponse.DeploymentInfo를 AgentAppRes.DeploymentInfo로 변환하는 Named 메서드
     */
    @Named("mapDeployments")
    default List<AgentAppRes.DeploymentInfo> mapDeployments(List<AppResponse.DeploymentInfo> deployments) {
        if (deployments == null) return null;
        return deployments.stream()
                .map(deployment -> AgentAppRes.DeploymentInfo.builder()
                        .description(deployment.getDescription())
                        .servingType(deployment.getServingType())
                        .imageTag(deployment.getImageTag())
                        .inputKeys(mapInputKeysFromAppsForAgentApp(deployment.getInputKeys()))
                        .outputType(deployment.getOutputType())
                        .deployedDt(deployment.getDeployedDt())
                        .createdBy(deployment.getCreatedBy())
                        .servingId(deployment.getServingId())
                        .targetId(deployment.getTargetId())
                        .id(deployment.getId())
                        .targetType(deployment.getTargetType())
                        .version(deployment.getVersion())
                        .status(deployment.getStatus())
                        .outputKeys(mapOutputKeysFromAppsForAgentApp(deployment.getOutputKeys()))
                        .deploymentConfigPath(deployment.getDeploymentConfigPath())
                        .build())
                .toList();
    }
    
    /**
     * AppResponse 리스트를 AgentAppRes 리스트로 변환
     */
    default List<AgentAppRes> toDeployResListFromApps(List<AppResponse> apps) {
        if (apps == null) return null;
        return apps.stream()
                .map(this::toDeployResFromApps)
                .toList();
    }
    
    /**
     * AppDeploymentsResponse를 AgentDeployRes 리스트로 변환
     */
    default List<AgentDeployRes> toDeployResListFromAppDeployments(AppDeploymentsResponse response) {
        if (response == null || response.getData() == null) return null;
        
        return response.getData().stream()
                .map(this::toDeployResFromAppDeployment)
                .toList();
    }
    
    /**
     * AppUpdateOrDeleteResponse를 AgentDeployUpdateOrDeleteRes로 변환
     */
    @Mapping(target = "appUuid", source = "appUuid")
    @Mapping(target = "success", source = "success")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "updatedAt", source = "updatedAt")
    AgentDeployUpdateOrDeleteRes toDeployResFromAppUpdateOrDeleteResponse(AppUpdateOrDeleteResponse response);

    /**
     * AppCreateReq를 AppCreateRequest로 변환
     */
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "targetId", source = "targetId")
    @Mapping(target = "targetType", source = "targetType")
    @Mapping(target = "servingType", source = "servingType")
    @Mapping(target = "cpuLimit", source = "cpuLimit")
    @Mapping(target = "cpuRequest", source = "cpuRequest")
    @Mapping(target = "gpuLimit", source = "gpuLimit")
    @Mapping(target = "gpuRequest", source = "gpuRequest")
    @Mapping(target = "memLimit", source = "memLimit")
    @Mapping(target = "memRequest", source = "memRequest")
    @Mapping(target = "maxReplicas", source = "maxReplicas")
    @Mapping(target = "minReplicas", source = "minReplicas")
    @Mapping(target = "workersPerCore", source = "workersPerCore")
    @Mapping(target = "versionDescription", source = "versionDescription")
    @Mapping(target = "safetyFilterOptions", source = "safetyFilterOptions")
    AppCreateRequest toAppCreateReq(AppCreateReq request);

    /**
     * AppUpdateReq를 AppUpdateRequest로 변환
     */
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    AppUpdateRequest toAppUpdateReq(AppUpdateReq request);

    /**
     * AppCustomDeployReq를 AppCustomDeployRequest로 변환
     */
    // @Mapping(target = "name", source = "name")
    // @Mapping(target = "description", source = "description")
    // @Mapping(target = "versionDescription", source = "versionDescription")
    // @Mapping(target = "targetType", source = "targetType")
    // @Mapping(target = "modelList", source = "modelList")
    // @Mapping(target = "imageUrl", source = "imageUrl")
    // @Mapping(target = "useExternalRegistry", source = "useExternalRegistry")
    // @Mapping(target = "cpuRequest", source = "cpuRequest")
    // @Mapping(target = "cpuLimit", source = "cpuLimit")
    // @Mapping(target = "memRequest", source = "memRequest")
    // @Mapping(target = "memLimit", source = "memLimit")
    // @Mapping(target = "minReplicas", source = "minReplicas")
    // @Mapping(target = "maxReplicas", source = "maxReplicas")
    // @Mapping(target = "workersPerCore", source = "workersPerCore")
    // @Mapping(target = "safetyFilterOptions", source = "safetyFilterOptions")
    // AppCustomDeployRequest toAppCustomDeployRequest(AppCustomDeployReq request);

    /**
     * AppCreateResponse를 AppCreateRes로 변환
     */
    @Mapping(target = "agentServingId", source = "data.agentServingId")
    @Mapping(target = "deploymentName", source = "data.deploymentName")
    @Mapping(target = "isvcName", source = "data.isvcName")
    @Mapping(target = "description", source = "data.description")
    @Mapping(target = "kserveYaml", source = "data.kserveYaml")
    @Mapping(target = "projectId", source = "data.projectId")
    @Mapping(target = "namespace", source = "data.namespace")
    @Mapping(target = "appId", source = "data.appId")
    @Mapping(target = "appVersion", source = "data.appVersion")
    @Mapping(target = "status", source = "data.status")
    @Mapping(target = "cpuRequest", source = "data.cpuRequest")
    @Mapping(target = "cpuLimit", source = "data.cpuLimit")
    @Mapping(target = "gpuRequest", source = "data.gpuRequest")
    @Mapping(target = "gpuLimit", source = "data.gpuLimit")
    @Mapping(target = "memRequest", source = "data.memRequest")
    @Mapping(target = "memLimit", source = "data.memLimit")
    @Mapping(target = "createdBy", source = "data.createdBy")
    @Mapping(target = "updatedBy", source = "data.updatedBy")
    @Mapping(target = "isDeleted", source = "data.isDeleted")
    @Mapping(target = "createdAt", source = "data.createdAt")
    @Mapping(target = "updatedAt", source = "data.updatedAt")
    @Mapping(target = "safetyFilterInput", source = "data.safetyFilterInput")
    @Mapping(target = "safetyFilterOutput", source = "data.safetyFilterOutput")
    @Mapping(target = "modelList", source = "data.modelList")
    @Mapping(target = "endpoint", source = "data.endpoint")
    // @Mapping(target = "agentParams", source = "data.agentParams")
    @Mapping(target = "servingType", source = "data.servingType")
    @Mapping(target = "agentAppImage", source = "data.agentAppImage")
    @Mapping(target = "agentAppImageRegistry", source = "data.agentAppImageRegistry")
    @Mapping(target = "deploymentId", source = "data.deploymentId")
    @Mapping(target = "errorMessage", source = "data.errorMessage")
    @Mapping(target = "gpuType", source = "data.gpuType")
    @Mapping(target = "safetyFilterInputGroups", source = "data.safetyFilterInputGroups")
    @Mapping(target = "safetyFilterOutputGroups", source = "data.safetyFilterOutputGroups")
    @Mapping(target = "dataMaskingInput", source = "data.dataMaskingInput")
    @Mapping(target = "dataMaskingOutput", source = "data.dataMaskingOutput")
    @Mapping(target = "minReplicas", source = "data.minReplicas")
    @Mapping(target = "maxReplicas", source = "data.maxReplicas")
    @Mapping(target = "autoscalingClass", source = "data.autoscalingClass")
    @Mapping(target = "autoscalingMetric", source = "data.autoscalingMetric")
    @Mapping(target = "target", source = "data.target")
    @Mapping(target = "externalEndpoint", source = "data.externalEndpoint")
    @Mapping(target = "appConfigFilePath", source = "data.appConfigFilePath")
    @Mapping(target = "sharedBackendId", source = "data.sharedBackendId")
    AppCreateRes toAppCreateRes(AppCreateResponse response);

    /**
     * AppUpdateOrDeleteResponse를 AppUpdateRes로 변환
     */
    @Mapping(target = "appUuid", source = "appUuid")
    @Mapping(target = "success", source = "success")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "updatedAt", source = "updatedAt")
    AppUpdateRes toAppUpdateRes(AppUpdateOrDeleteResponse response);

    /**
     * AgentServingResponse를 AgentServingRes로 변환
     */
    @Mapping(target = "agentServingId", source = "agentServingId")
    @Mapping(target = "agentServingName", source = "agentServingName")
    @Mapping(target = "agentId", source = "agentId")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "endpoint", source = "endpoint")
    @Mapping(target = "chatEndpointUrl", source = "chatEndpointUrl")
    @Mapping(target = "isCustom", source = "isCustom")
    @Mapping(target = "agentServingParams", source = "agentServingParams")
    @Mapping(target = "cpuRequest", source = "cpuRequest")
    @Mapping(target = "cpuLimit", source = "cpuLimit")
    @Mapping(target = "gpuRequest", source = "gpuRequest")
    @Mapping(target = "gpuLimit", source = "gpuLimit")
    @Mapping(target = "memRequest", source = "memRequest")
    @Mapping(target = "memLimit", source = "memLimit")
    @Mapping(target = "gpuType", source = "gpuType")
    @Mapping(target = "currentReplicas", source = "currentReplicas")
    @Mapping(target = "minReplicas", source = "minReplicas")
    @Mapping(target = "maxReplicas", source = "maxReplicas")
    @Mapping(target = "autoscalingClass", source = "autoscalingClass")
    @Mapping(target = "autoscalingMetric", source = "autoscalingMetric")
    @Mapping(target = "target", source = "target")
    @Mapping(target = "safetyFilterInput", source = "safetyFilterInput")
    @Mapping(target = "safetyFilterOutput", source = "safetyFilterOutput")
    @Mapping(target = "dataMaskingInput", source = "dataMaskingInput")
    @Mapping(target = "dataMaskingOutput", source = "dataMaskingOutput")
    @Mapping(target = "activeSessions", source = "activeSessions")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "isvcName", source = "isvcName")
    @Mapping(target = "errorMessage", source = "errorMessage")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "agentAppImageRegistry", source = "agentAppImageRegistry")
    @Mapping(target = "appConfigFilePath", source = "appConfigFilePath")
    @Mapping(target = "kserveYaml", source = "kserveYaml")
    @Mapping(target = "isDeleted", source = "isDeleted")
    @Mapping(target = "servingType", source = "servingType")
    @Mapping(target = "projectId", source = "projectId")
    @Mapping(target = "modelList", source = "modelList")
    @Mapping(target = "sharedBackendId", source = "sharedBackendId")
    @Mapping(target = "namespace", source = "namespace")
    @Mapping(target = "appId", source = "appId")
    @Mapping(target = "appVersion", source = "appVersion")
    @Mapping(target = "deploymentName", source = "deploymentName")
    // @Mapping(target = "agentParams", source = "agentParams")
    @Mapping(target = "agentAppImage", source = "agentAppImage")
    AgentServingRes toAgentServingRes(AgentServingResponse response);

    /**
     * AppApiKeysResponse를 AppApiKeysRes로 변환
     */
    @Mapping(target = "apiKeys", source = "data")
    AppApiKeysRes toAppApiKeysRes(AppApiKeysResponse response);

    
    StreamRequest toStreamReq(StreamReq request);

    /**
     * AgentSysLogSearchReq를 SearchRequest로 변환 (동일한 구조)
     */
    default SearchRequest toSearchRequest(AgentSysLogSearchReq request) {
        if (request == null) return null;
        
        // _source가 null이면 기본 필드 제공
        String[] source = request.getSource();
        if (source == null) {
            source = new String[]{"@timestamp", "log", "kubernetes.pod_name", "kubernetes.namespace_name"};
        }
        
        // query가 null이면 기본 match_all 쿼리 제공
        Object query = request.getQuery();
        if (query == null) {
            query = java.util.Map.of("match_all", java.util.Map.of());
        }
        
        // sort가 null이면 기본 정렬 제공 (최신순)
        List<Map<String, Object>> sort = request.getSort();
        if (sort == null) {
            sort = List.of(Map.of("@timestamp", Map.of("order", "desc")));
        }
        
        return SearchRequest.builder()
                .source(source)
                .query(query)
                .sort(sort)
                .from(request.getFrom() != null ? request.getFrom() : 0)  // null이면 0으로 설정
                .size(request.getSize() != null ? request.getSize() : 10)  // null이면 10으로 설정
                .build();
    }

    /**
     * SearchResponse를 String으로 변환 (모든 히트의 message 필드 추출)
     */
    default String toSearchResponse(SearchResponse response) {
        if (response == null) {
            return "";
        }
        
        if (response.getHits() == null) {
            return "";
        }
        
        List<SearchResponse.Hit> hits = response.getHits().getHits();
        if (hits == null || hits.isEmpty()) {
            return "";
        }
        
        // 모든 히트에서 _source의 message 필드 추출하여 개행으로 구분
        StringBuilder result = new StringBuilder();
        int processedCount = 0;
        for (SearchResponse.Hit hit : hits) {
            if (hit == null) {
                continue;
            }
            
            Map<String, Object> source = hit.getSource();
            if (source == null) {
                continue;
            }
            
            // message 필드 우선, 없으면 log 필드 확인
            Object messageValue = source.get("message");
            if (messageValue == null) {
                messageValue = source.get("log");
            }
            
            if (messageValue != null) {
                if (result.length() > 0) {
                    result.append("\n");
                }
                result.append(messageValue.toString());
                processedCount++;
            }
        }
        
        return result.toString();
    }
}
