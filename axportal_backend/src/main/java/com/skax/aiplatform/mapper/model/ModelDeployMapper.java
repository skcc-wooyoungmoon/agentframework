package com.skax.aiplatform.mapper.model;

import java.util.Map;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.sktai.serving.dto.request.BackendAiServingCreate;
import com.skax.aiplatform.client.sktai.serving.dto.request.ServingCreate;
import com.skax.aiplatform.client.sktai.serving.dto.request.ServingParams;
import com.skax.aiplatform.client.sktai.serving.dto.request.ServingUpdate;
import com.skax.aiplatform.client.sktai.serving.dto.response.ServingResponse;
import com.skax.aiplatform.dto.model.request.CreateBackendAiModelDeployReq;
import com.skax.aiplatform.dto.model.request.CreateModelDeployReq;
import com.skax.aiplatform.dto.model.request.PutModelDeployReq;
import com.skax.aiplatform.dto.model.response.GetModelDeployRes;

/**
 * 모델 관련 매퍼
 *
 * <p>
 * 모델 관련 DTO들 간의 변환을 담당하는 MapStruct 매퍼입니다.
 * </p>
 *
 * @author ByounggwanLee
 * @version 1.0.0
 * @since 2025-08-15
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ModelDeployMapper {
    /// //////////////// Request DTO ///////////////////

    @Mapping(target = "servingParams", source = "servingParams", qualifiedByName = "objectToServingParams")
    ServingCreate toServingCreate(CreateModelDeployReq request);

    @Mapping(target = "servingParams", source = "servingParams", qualifiedByName = "objectToServingParams")
    ServingUpdate toServingUpdate(PutModelDeployReq request);

    /**
     * CreateBackendAiModelDeployReq를 BackendAiServingCreate로 변환
     *
     * <p>
     * servingParams는 원본 Object를 그대로 전달하여 요청에 포함된 모든 필드가 유지되도록 합니다.
     * ServingParams 객체로 변환하지 않아 trash 같은 추가 필드도 포함됩니다.
     * </p>
     *
     * @param request Backend.AI 모델 배포 생성 요청
     * @return BackendAiServingCreate
     */
    @Mapping(target = "servingParams", source = "servingParams", qualifiedByName = "objectToObject")
    BackendAiServingCreate toBackendAiServingCreate(CreateBackendAiModelDeployReq request);

    /////////////////// Response DTO ///////////////////

    /**
     * ServingsResponse.ServingInfo를 GetModelDeployRes로 변환
     *
     * @param serving SKTAI 모델 배포 응답 DTO
     * @return GetModelDeployRes
     */
    GetModelDeployRes toGetModelListDeployRes(ServingResponse serving);

    GetModelDeployRes toGetModelDetailDeployRes(ServingResponse serving);

    /////////////////// Helper Methods ///////////////////

    /**
     * Object를 ServingParams로 변환
     * 
     * <p>
     * 다양한 타입의 Object를 ServingParams로 안전하게 변환합니다.
     * </p>
     * 
     * @param value Object 타입의 서빙 파라미터
     * @return ServingParams 객체 또는 null
     */
    @Named("objectToServingParams")
    default ServingParams objectToServingParams(Object value) {
        if (value == null) {
            return null;
        }

        // 이미 ServingParams 타입인 경우
        if (value instanceof ServingParams) {
            return (ServingParams) value;
        }

        // Map이나 다른 객체인 경우 ObjectMapper를 사용하여 변환
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // 알 수 없는 속성 무시 설정 추가
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            // null 값 허용
            objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);

            if (value instanceof String) {
                // JSON 문자열인 경우 직접 파싱
                return objectMapper.readValue((String) value, ServingParams.class);
            } else if (value instanceof Map) {
                // Map에서 ServingParams로 변환
                return objectMapper.convertValue(value, ServingParams.class);
            } else {
                // 다른 객체 타입에서 ServingParams로 변환
                String json = objectMapper.writeValueAsString(value);
                return objectMapper.readValue(json, ServingParams.class);
            }
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            LoggerFactory.getLogger(ModelDeployMapper.class).warn("ServingParams JSON 변환 실패");
            return null;
        } catch (IllegalArgumentException | IllegalStateException e) {
            LoggerFactory.getLogger(ModelDeployMapper.class).warn("ServingParams 변환 실패 (잘못된 입력값)");
            return null;
        } catch (Exception e) {
            LoggerFactory.getLogger(ModelDeployMapper.class).error("ServingParams 변환 실패 (예상치 못한 오류)");
            return null;
        }
    }

    /**
     * Object를 그대로 반환 (변환 없음)
     *
     * <p>
     * BackendAiServingCreate의 servingParams는 원본 Object를 그대로 전달하여
     * 요청에 포함된 모든 필드(trash 같은 추가 필드 포함)가 유지되도록 합니다.
     * </p>
     *
     * @param value Object 타입의 서빙 파라미터
     * @return 원본 Object (변환 없음)
     */
    @Named("objectToObject")
    default Object objectToObject(Object value) {
        return value;
    }
}