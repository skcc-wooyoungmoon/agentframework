package com.skax.aiplatform.mapper.log;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.skax.aiplatform.client.sktai.common.dto.Pagination;
import com.skax.aiplatform.client.sktai.common.dto.PaginationLink;
import com.skax.aiplatform.client.sktai.common.dto.Payload;
import com.skax.aiplatform.client.sktai.history.dto.response.ModelHistoryRead;
import com.skax.aiplatform.client.sktai.history.dto.response.ModelHistoryRecord;
import com.skax.aiplatform.dto.log.response.ModelHistoryPaginationRes;
import com.skax.aiplatform.dto.log.response.ModelHistoryPayloadRes;
import com.skax.aiplatform.dto.log.response.ModelHistoryRecordRes;
import com.skax.aiplatform.dto.log.response.ModelHistoryRes;

/**
 * 모델 로그 관련 MapStruct 매퍼
 * 
 * <p>
 * SKTAI History API 응답을 카멜케이스 응답 DTO로 변환하는 매퍼입니다.
 * snake_case에서 camelCase로의 변환을 담당합니다.
 * </p>
 * 
 * @author System
 * @since 2025-01-27
 * @version 1.1.0
 */
@Mapper(componentModel = "spring")
public interface ModelLogMapper {

    /**
     * ModelHistoryRead를 ModelHistoryRes로 변환
     * 
     * @param source 원본 ModelHistoryRead 객체
     * @return 변환된 ModelHistoryRes 객체
     */
    @Mapping(source = "data", target = "data")
    @Mapping(source = "payload", target = "payload")
    ModelHistoryRes toModelHistoryRes(ModelHistoryRead source);

    /**
     * Payload를 ModelHistoryPayloadRes로 변환
     * 
     * @param source 원본 Payload 객체
     * @return 변환된 ModelHistoryPayloadRes 객체
     */
    @Mapping(source = "pagination", target = "pagination")
    ModelHistoryPayloadRes toModelHistoryPayloadRes(Payload source);

    /**
     * Pagination를 ModelHistoryPaginationRes로 변환
     * 
     * @param source 원본 Pagination 객체
     * @return 변환된 ModelHistoryPaginationRes 객체
     */
    @Mapping(source = "firstPageUrl", target = "firstPageUrl")
    @Mapping(source = "from", target = "from")
    @Mapping(source = "lastPage", target = "lastPage")
    @Mapping(source = "links", target = "links")
    @Mapping(source = "nextPageUrl", target = "nextPageUrl")
    @Mapping(source = "itemsPerPage", target = "itemsPerPage")
    @Mapping(source = "prevPageUrl", target = "prevPageUrl")
    @Mapping(source = "to", target = "to")
    @Mapping(source = "total", target = "total")
    ModelHistoryPaginationRes toModelHistoryPaginationRes(Pagination source);

    /**
     * PaginationLink를
     * ModelHistoryPaginationRes.PaginationLinkRes로 변환
     * 
     * @param source 원본 PaginationLink 객체
     * @return 변환된 PaginationLinkRes 객체
     */
    @Mapping(source = "url", target = "url")
    @Mapping(source = "label", target = "label")
    @Mapping(source = "active", target = "active")
    @Mapping(source = "page", target = "page")
    ModelHistoryPaginationRes.PaginationLinkRes toPaginationLinkRes(PaginationLink source);

    /**
     * ModelHistoryRecord를 ModelHistoryRecordRes로 변환
     * 
     * @param source 원본 ModelHistoryRecord 객체
     * @return 변환된 ModelHistoryRecordRes 객체
     */
    @Mapping(source = "requestTime", target = "requestTime")
    @Mapping(source = "responseTime", target = "responseTime")
    @Mapping(source = "elapsedTime", target = "elapsedTime")
    @Mapping(source = "endpoint", target = "endpoint")
    @Mapping(source = "modelName", target = "modelName")
    @Mapping(source = "modelIdentifier", target = "modelIdentifier")
    @Mapping(source = "modelId", target = "modelId")
    @Mapping(source = "modelType", target = "modelType")
    @Mapping(source = "modelServingId", target = "modelServingId")
    @Mapping(source = "modelServingName", target = "modelServingName")
    @Mapping(source = "objectType", target = "objectType")
    @Mapping(source = "apiKey", target = "apiKey")
    @Mapping(source = "modelKey", target = "modelKey")
    @Mapping(source = "inputJson", target = "inputJson")
    @Mapping(source = "outputJson", target = "outputJson")
    @Mapping(source = "completionTokens", target = "completionTokens")
    @Mapping(source = "promptTokens", target = "promptTokens")
    @Mapping(source = "totalTokens", target = "totalTokens")
    @Mapping(source = "projectId", target = "projectId")
    @Mapping(source = "user", target = "user")
    @Mapping(source = "transactionId", target = "transactionId")
    @Mapping(source = "appId", target = "appId")
    @Mapping(source = "agentAppServingId", target = "agentAppServingId")
    @Mapping(source = "company", target = "company")
    @Mapping(source = "department", target = "department")
    @Mapping(source = "chatId", target = "chatId")
    @Mapping(target = "requestId", source = "transactionId") // transactionId를 requestId로 매핑
    @Mapping(target = "tokenCount", source = "totalTokens") // totalTokens를 tokenCount로 매핑
    @Mapping(target = "status", constant = "success") // 기본값으로 success 설정
    @Mapping(source = "errorMessage", target = "errorMessage")
    ModelHistoryRecordRes toModelHistoryRecordRes(ModelHistoryRecord source);
}