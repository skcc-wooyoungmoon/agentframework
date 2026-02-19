package com.skax.aiplatform.mapper.model;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.skax.aiplatform.client.sktai.model.dto.request.ModelCreate;
import com.skax.aiplatform.client.sktai.model.dto.request.ModelUpdate;
import com.skax.aiplatform.client.sktai.model.dto.response.ModelEndpointsResponse;
import com.skax.aiplatform.client.sktai.model.dto.response.ModelProviderRead;
import com.skax.aiplatform.client.sktai.model.dto.response.ModelRead;
import com.skax.aiplatform.client.sktai.model.dto.response.ModelTagsResponse;
import com.skax.aiplatform.dto.model.request.CreateModelCtlgReq;
import com.skax.aiplatform.dto.model.request.GetUpdateModelCtlgReq;
import com.skax.aiplatform.dto.model.response.GetModelCtlgRes;
import com.skax.aiplatform.dto.model.response.GetModelPrvdRes;
import com.skax.aiplatform.dto.model.response.GetModelTagsRes;

/**
 * 모델 관련 매퍼
 * 
 * <p>모델 관련 DTO들 간의 변환을 담당하는 MapStruct 매퍼입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0.0
 */
@Mapper(    
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ModelCtlgMapper {
    /////////////////// Request DTO ///////////////////
    /**
     * CreateModelCtlgReq를 ModelCreate로 변환
     * 
     * @param createReq 모델 카탈로그 생성 요청 DTO
     * @return SKTAI Model 생성 요청 DTO
     */
    ModelCreate toModelCreate(CreateModelCtlgReq createReq);
    
    /**
     * GetUpdateModelCtlgReq를 ModelUpdate로 변환
     * 
     * @param updateReq 모델 카탈로그 수정 요청 DTO
     * @return SKTAI Model 수정 요청 DTO
     */
    ModelUpdate toModelUpdate(GetUpdateModelCtlgReq updateReq);
    
    /////////////////// Response DTO ///////////////////
    /**
     * ModelRead를 GetModelCtlgRes로 변환
     * 
     * @param modelRead SKTAI Model 응답 DTO
     * @return 모델 카탈로그 응답 DTO
     */
    GetModelCtlgRes toGetModelCtlgRes(ModelRead modelRead, ModelEndpointsResponse endpoints);

    /**
     * ModelProviderRead를 GetModelPrvdRes로 변환
     * 
     * @param modelProvider SKTAI Model 제공자 응답 DTO
     * @return GetModelPrvdRes
     */
    GetModelPrvdRes toGetModelPrvdRes(ModelProviderRead modelProvider);

    /**
     * SKTAI ModelTagsResponse.ModelTag을 GetModelTagsRes로 변환
     * 
     * @param sktaiTags SKTAI 모델 태그 응답 DTO
     * @return GetModelTagsRes
     */
    GetModelTagsRes toGetModelTagsRes(ModelTagsResponse.ModelTag sktaiTags);
    
    /////////////////// Helper Methods ///////////////////
    /**
     * List<Object>를 List<GetModelCtlgRes.Tag>로 변환
     * 
     * @param objects 원본 객체 리스트
     * @return 변환된 태그 리스트
     */
    default List<GetModelCtlgRes.Tag> mapObjectListToTagList(List<ModelRead.Tag> objects) {
        if (objects == null) {
            return null;
        }
        List<GetModelCtlgRes.Tag> tags = new ArrayList<>();
        for (ModelRead.Tag obj : objects) {
            if (obj != null) {
                GetModelCtlgRes.Tag tag = new GetModelCtlgRes.Tag();
                // Object의 toString()을 사용하여 기본값 설정
                tag.setId(obj.getId());
                tag.setName(obj.getName());
                tag.setCreated_at(obj.getCreated_at());
                tag.setUpdated_at(obj.getUpdated_at());
                
                tags.add(tag);
            }
        }
        return tags;
    }
} 