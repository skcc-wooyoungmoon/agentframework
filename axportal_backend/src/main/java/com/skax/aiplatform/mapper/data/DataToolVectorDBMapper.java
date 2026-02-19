package com.skax.aiplatform.mapper.data;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.skax.aiplatform.client.sktai.knowledge.dto.request.VectorDBCreate;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.VectorDBUpdate;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.ArgResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.ArgsResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.VectorDBCreateResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.VectorDBDetailResponse;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.VectorDbsResponse;
import com.skax.aiplatform.dto.data.request.DataToolVectorDBCreateReq;
import com.skax.aiplatform.dto.data.request.DataToolVectorDBUpdateReq;
import com.skax.aiplatform.dto.data.response.DataArgRes;
import com.skax.aiplatform.dto.data.response.DataArgsRes;
import com.skax.aiplatform.dto.data.response.DataToolVectorDBCreateRes;
import com.skax.aiplatform.dto.data.response.DataToolVectorDBDetailRes;
import com.skax.aiplatform.dto.data.response.DataToolVectorDBRes;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DataToolVectorDBMapper {

    /**
     * VectorDBDetailResponse를 DataToolVectorDBDetailRes로 변환
     */
    default DataToolVectorDBDetailRes from(VectorDBDetailResponse vectorDBDetailResponse) {
        if (vectorDBDetailResponse == null) {
            return null;
        }

        return DataToolVectorDBDetailRes.builder()
                .projectId(vectorDBDetailResponse.getProjectId())
                .name(vectorDBDetailResponse.getName())
                .type(vectorDBDetailResponse.getType())
                .isDefault(vectorDBDetailResponse.getIsDefault())
                .connectionInfo(createConnectionInfo(vectorDBDetailResponse.getConnectionInfo()))
                .createdAt(vectorDBDetailResponse.getCreatedAt())
                .createdBy(vectorDBDetailResponse.getCreatedBy())
                .updatedAt(vectorDBDetailResponse.getUpdatedAt())
                .updatedBy(vectorDBDetailResponse.getUpdatedBy())
                .build();
    }

    /**
     * VectorDBsSummary를 DataToolVectorDBRes로 변환
     */
    default DataToolVectorDBRes from(VectorDbsResponse.VectorDBsSummary vectorDBsSummary) {
        if (vectorDBsSummary == null) {
            return null;
        }

        return DataToolVectorDBRes.builder()
                .id(vectorDBsSummary.getId())
                .name(vectorDBsSummary.getName())
                .type(vectorDBsSummary.getType())
                .projectId(vectorDBsSummary.getProjectId())
                .createdAt(vectorDBsSummary.getCreatedAt())
                .createdBy(vectorDBsSummary.getCreatedBy())
                .updatedAt(vectorDBsSummary.getUpdatedAt())
                .updatedBy(vectorDBsSummary.getUpdatedBy())
                .isDeleted(vectorDBsSummary.getIsDeleted())
                .isDefault(vectorDBsSummary.getIsDefault())
                .build();
    }

    /**
     * VectorDBsSummary 리스트를 DataToolVectorDBRes 리스트로 변환
     */
    default List<DataToolVectorDBRes> toDataToolVectorDBResList(
            List<VectorDbsResponse.VectorDBsSummary> vectorDBsSummaryList) {
        if (vectorDBsSummaryList == null) {
            return List.of();
        }

        return vectorDBsSummaryList.stream()
                .map(this::from)
                .filter(item -> item != null)
                .collect(Collectors.toList());
    }

    /**
     * ArgsResponse를 DataArgsRes로 변환
     */
    default DataArgsRes from(ArgsResponse argsResponse) {
        if (argsResponse == null) {
            return null;
        }

        // ArgsResponse의 data 리스트를 DataArgsRes의 data로 변환
        List<DataArgRes> dataArgResList = argsResponse.getData() != null
                ? argsResponse.getData().stream()
                        .map(this::fromArg)
                        .filter(item -> item != null)
                        .collect(Collectors.toList())
                : List.of();

        return DataArgsRes.builder()
                .data(dataArgResList)
                .build();
    }

    /**
     * ArgResponse를 DataArgRes로 변환
     */
    default DataArgRes fromArg(ArgResponse argResponse) {
        if (argResponse == null) {
            return null;
        }

        return DataArgRes.builder()
                .type(argResponse.getType())
                .displayName(argResponse.getDisplayName())
                .connectionInfoArgs(argResponse.getConnectionInfoArgs())
                .supportedFileExtensions(argResponse.getSupportedFileExtensions())
                .enable(argResponse.isEnable())
                .build();
    }

    /**
     * ArgResponse 리스트를 DataArgRes 리스트로 변환
     */
    default List<DataArgRes> toDataArgResList(List<ArgResponse> argResponseList) {
        if (argResponseList == null) {
            return List.of();
        }

        return argResponseList.stream()
                .map(this::fromArg)
                .filter(item -> item != null)
                .collect(Collectors.toList());
    }

    /**
     * ArgsResponse 리스트를 DataArgsRes 리스트로 변환
     */
    default List<DataArgsRes> toDataArgsResList(List<ArgsResponse> argsResponseList) {
        if (argsResponseList == null) {
            return List.of();
        }

        return argsResponseList.stream()
                .map(this::from)
                .filter(item -> item != null)
                .collect(Collectors.toList());
    }

    /**
     * ConnectionInfo 생성 헬퍼 메서드
     */
    private DataToolVectorDBDetailRes.ConnectionInfo createConnectionInfo(
            VectorDBDetailResponse.ConnectionInfo connectionInfo) {
        if (connectionInfo == null) {
            return null;
        }

        return DataToolVectorDBDetailRes.ConnectionInfo.builder()
                .endpoint(connectionInfo.getEndpoint())
                .key(connectionInfo.getKey())
                .host(connectionInfo.getHost())
                .port(connectionInfo.getPort())
                .user(connectionInfo.getUser())
                .password(connectionInfo.getPassword())
                .secure(connectionInfo.getSecure())
                .dbName(connectionInfo.getDbName())
                .apiKey(connectionInfo.getApiKey())
                .build();
    }

    DataToolVectorDBCreateRes toDataToolVectorDBCreateRes(VectorDBCreateResponse toolCreateResponse);

    /**
     * DataToolVectorDBCreateReq를 VectorDBCreate로 변환
     * is_default 필드를 문자열로 그대로 전달
     */
    default VectorDBCreate toDataToolVectorDBCreateReq(DataToolVectorDBCreateReq vectorDBCreateReq) {
        if (vectorDBCreateReq == null) {
            return null;
        }

        return VectorDBCreate.builder()
                .name(vectorDBCreateReq.getName())
                .type(vectorDBCreateReq.getType())
                .isDefault(vectorDBCreateReq.getIsDefault())
                .connectionInfo(convertConnectionInfo(vectorDBCreateReq.getConnectionInfo()))
                .build();
    }

    /**
     * DataToolVectorDBUpdateReq를 VectorDBUpdate로 변환
     * is_default 필드를 문자열로 그대로 전달
     */
    default VectorDBUpdate toDataToolVectorDBUpdateReq(DataToolVectorDBUpdateReq vectorDBUpdateReq) {
        if (vectorDBUpdateReq == null) {
            return null;
        }

        return VectorDBUpdate.builder()
                .name(vectorDBUpdateReq.getName())
                .type(vectorDBUpdateReq.getType())
                .isDefault(vectorDBUpdateReq.getIsDefault())
                .connectionInfo(convertUpdateConnectionInfo(vectorDBUpdateReq.getConnectionInfo()))
                .build();
    }

    /**
     * Update용 ConnectionInfo 변환 헬퍼 메서드
     */
    default VectorDBUpdate.ConnectionInfo convertUpdateConnectionInfo(
            DataToolVectorDBUpdateReq.ConnectionInfo connectionInfo) {
        if (connectionInfo == null) {
            return null;
        }

        return VectorDBUpdate.ConnectionInfo.builder()
                .key(connectionInfo.getKey())
                .endpoint(connectionInfo.getEndpoint())
                .host(connectionInfo.getHost())
                .port(connectionInfo.getPort())
                .user(connectionInfo.getUser())
                .password(connectionInfo.getPassword())
                .secure(connectionInfo.getSecure())
                .dbName(connectionInfo.getDbName())
                .apiKey(connectionInfo.getApiKey())
                .build();
    }

    /**
     * ConnectionInfo 변환 헬퍼 메서드
     */
    default VectorDBCreate.ConnectionInfo convertConnectionInfo(
            DataToolVectorDBCreateReq.ConnectionInfo connectionInfo) {
        if (connectionInfo == null) {
            return null;
        }

        return VectorDBCreate.ConnectionInfo.builder()
                .key(connectionInfo.getKey())
                .endpoint(connectionInfo.getEndpoint())
                .host(connectionInfo.getHost())
                .port(connectionInfo.getPort())
                .user(connectionInfo.getUser())
                .password(connectionInfo.getPassword())
                .secure(connectionInfo.getSecure())
                .dbName(connectionInfo.getDbName())
                .apiKey(connectionInfo.getApiKey())
                .build();
    }
}
