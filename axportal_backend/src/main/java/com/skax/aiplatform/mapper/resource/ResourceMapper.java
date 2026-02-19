package com.skax.aiplatform.mapper.resource;

import com.skax.aiplatform.client.lablup.api.dto.response.GetScalingGroupsResponse;
import com.skax.aiplatform.client.sktai.resource.dto.response.TaskPolicyResponse;
import com.skax.aiplatform.client.sktai.resource.dto.response.TaskResourceResponse;
import com.skax.aiplatform.dto.resource.response.ScalingGroupsResponse;
import com.skax.aiplatform.dto.resource.response.TaskPolicyRes;
import com.skax.aiplatform.dto.resource.response.TaskResourceRes;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * 리소스 관련 매퍼
 *
 * <p>
 * 리소스 관련 DTO들 간의 변환을 담당하는 MapStruct 매퍼입니다.
 * </p>
 *
 * @author System
 * @version 1.0.0
 * @since 2025-01-27
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResourceMapper {

    /**
     * TaskResourceResponse를 TaskResourceRes로 변환
     *
     * @param taskResourceResponse SKTAI 태스크 리소스 응답 DTO
     * @return TaskResourceRes
     */
    @Mapping(target = "nodeResource", source = "nodeResource")
    @Mapping(target = "namespaceResource", source = "namespaceResource")
    @Mapping(target = "taskPolicy", source = "taskPolicy")
    @Mapping(target = "taskQuota", source = "taskQuota")
    TaskResourceRes toTaskResourceRes(TaskResourceResponse taskResourceResponse);

    /**
     * SKTAI NodeResource를 TaskResourceRes.NodeResourceInfo로 변환
     *
     * @param nodeResource SKTAI 노드 리소스 DTO
     * @return TaskResourceRes.NodeResourceInfo
     */
    @Mapping(target = "nodeName", source = "nodeName")
    @Mapping(target = "nodeLabel", source = "nodeLabel")
    @Mapping(target = "cpuQuota", expression = "java(nodeResource.getCpuQuota() != null ? nodeResource.getCpuQuota().doubleValue() : null)")
    @Mapping(target = "memQuota", expression = "java(nodeResource.getMemQuota() != null ? nodeResource.getMemQuota().doubleValue() : null)")
    @Mapping(target = "gpuQuota", expression = "java(nodeResource.getGpuQuota() != null ? nodeResource.getGpuQuota().doubleValue() : null)")
    @Mapping(target = "cpuUsed", expression = "java(nodeResource.getCpuUsed() != null ? nodeResource.getCpuUsed().doubleValue() : null)")
    @Mapping(target = "memUsed", expression = "java(nodeResource.getMemUsed() != null ? nodeResource.getMemUsed().doubleValue() : null)")
    @Mapping(target = "gpuUsed", expression = "java(nodeResource.getGpuUsed() != null ? nodeResource.getGpuUsed().doubleValue() : null)")
    @Mapping(target = "cpuUsable", expression = "java(nodeResource.getCpuUsable() != null ? nodeResource.getCpuUsable().doubleValue() : null)")
    @Mapping(target = "memUsable", expression = "java(nodeResource.getMemUsable() != null ? nodeResource.getMemUsable().doubleValue() : null)")
    @Mapping(target = "gpuUsable", expression = "java(nodeResource.getGpuUsable() != null ? nodeResource.getGpuUsable().doubleValue() : null)")
    TaskResourceRes.NodeResourceInfo toNodeResourceInfo(
            com.skax.aiplatform.client.sktai.resource.dto.response.NodeResource nodeResource);

    /**
     * SKTAI NamespaceResource를 TaskResourceRes.NamespaceResourceInfo로 변환
     *
     * @param namespaceResource SKTAI 네임스페이스 리소스 DTO
     * @return TaskResourceRes.NamespaceResourceInfo
     */
    @Mapping(target = "cpuQuota", expression = "java(namespaceResource.getCpuQuota() != null ? namespaceResource.getCpuQuota().doubleValue() : null)")
    @Mapping(target = "memQuota", expression = "java(namespaceResource.getMemQuota() != null ? namespaceResource.getMemQuota().doubleValue() : null)")
    @Mapping(target = "gpuQuota", expression = "java(namespaceResource.getGpuQuota() != null ? namespaceResource.getGpuQuota().doubleValue() : null)")
    @Mapping(target = "cpuUsed", expression = "java(namespaceResource.getCpuUsed() != null ? namespaceResource.getCpuUsed().doubleValue() : null)")
    @Mapping(target = "memUsed", expression = "java(namespaceResource.getMemUsed() != null ? namespaceResource.getMemUsed().doubleValue() : null)")
    @Mapping(target = "gpuUsed", expression = "java(namespaceResource.getGpuUsed() != null ? namespaceResource.getGpuUsed().doubleValue() : null)")
    @Mapping(target = "cpuUsable", expression = "java(namespaceResource.getCpuUsable() != null ? namespaceResource.getCpuUsable().doubleValue() : null)")
    @Mapping(target = "memUsable", expression = "java(namespaceResource.getMemUsable() != null ? namespaceResource.getMemUsable().doubleValue() : null)")
    @Mapping(target = "gpuUsable", expression = "java(namespaceResource.getGpuUsable() != null ? namespaceResource.getGpuUsable().doubleValue() : null)")
    TaskResourceRes.NamespaceResourceInfo toNamespaceResourceInfo(
            com.skax.aiplatform.client.sktai.resource.dto.response.NamespaceResource namespaceResource);

    /**
     * SKTAI TaskPolicy를 TaskResourceRes.TaskPolicyInfo로 변환
     *
     * @param taskPolicy SKTAI 태스크 정책 DTO
     * @return TaskResourceRes.TaskPolicyInfo
     */
    @Mapping(target = "small", source = "small")
    @Mapping(target = "medium", source = "medium")
    @Mapping(target = "large", source = "large")
    @Mapping(target = "max", source = "max")
    TaskResourceRes.TaskPolicyInfo toTaskPolicyInfo(
            com.skax.aiplatform.client.sktai.resource.dto.response.TaskPolicy taskPolicy);

    /**
     * SKTAI TaskPolicy.ResourceSpec를
     * TaskResourceRes.TaskPolicyInfo.ResourceSpecInfo로 변환
     *
     * @param resourceSpec SKTAI 리소스 사양 DTO
     * @return TaskResourceRes.TaskPolicyInfo.ResourceSpecInfo
     */
    @Mapping(target = "cpuQuota", expression = "java(resourceSpec.getCpuQuota() != null ? resourceSpec.getCpuQuota().doubleValue() : null)")
    @Mapping(target = "memQuota", expression = "java(resourceSpec.getMemQuota() != null ? resourceSpec.getMemQuota().doubleValue() : null)")
    @Mapping(target = "gpuQuota", expression = "java(resourceSpec.getGpuQuota() != null ? resourceSpec.getGpuQuota().doubleValue() : null)")
    TaskResourceRes.TaskPolicyInfo.ResourceSpecInfo toResourceSpecInfo(
            com.skax.aiplatform.client.sktai.resource.dto.response.TaskPolicy.ResourceSpec resourceSpec);

    /**
     * SKTAI TaskQuota를 TaskResourceRes.TaskQuotaInfo로 변환
     *
     * @param taskQuota SKTAI 태스크 할당량 DTO
     * @return TaskResourceRes.TaskQuotaInfo
     */
    @Mapping(target = "quota", source = "quota")
    @Mapping(target = "used", source = "used")
    TaskResourceRes.TaskQuotaInfo toTaskQuotaInfo(
            com.skax.aiplatform.client.sktai.resource.dto.response.TaskQuota taskQuota);

    /**
     * SKTAI TaskPolicyResponse를 TaskPolicyRes로 변환
     *
     * @param taskPolicyResponse SKTAI 태스크 정책 응답 DTO
     * @return TaskPolicyRes
     */
    @Mapping(target = "taskType", source = "taskType")
    @Mapping(target = "size", source = "size")
    @Mapping(target = "cpu", source = "cpu")
    @Mapping(target = "memory", source = "memory")
    @Mapping(target = "gpu", source = "gpu")
    TaskPolicyRes toTaskPolicyResponse(TaskPolicyResponse taskPolicyResponse);

    /**
     * Lablup GetScalingGroupsResponse를 ScalingGroupsResponse로 변환
     *
     * @param getScalingGroupsResponse Lablup 스케일링 그룹 응답 DTO
     * @return ScalingGroupsResponse
     */
    @Mapping(target = "scalingGroups", source = "scalingGroups")
    @Mapping(target = "errors", source = "errors")
    ScalingGroupsResponse toScalingGroupsResponse(GetScalingGroupsResponse getScalingGroupsResponse);
}