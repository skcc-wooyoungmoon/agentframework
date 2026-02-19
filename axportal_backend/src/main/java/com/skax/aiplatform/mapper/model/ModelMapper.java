package com.skax.aiplatform.mapper.model;

import com.skax.aiplatform.client.sktai.model.dto.response.ModelRead;
import com.skax.aiplatform.dto.model.response.ModelDetailRes;
import com.skax.aiplatform.dto.model.response.ModelRes;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Model DTO 매핑 인터페이스
 * 
 * <p>SKTAI Model DTO와 내부 Model DTO 간의 변환을 담당합니다.</p>
 *
 * @author ByounggwanLee
 * @since 2025-01-16
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface ModelMapper {
    
    ModelMapper INSTANCE = Mappers.getMapper(ModelMapper.class);
    
    /**
     * SKTAI ModelRead를 내부 ModelRes로 변환
     * 
     * @param modelRead SKTAI 모델 응답 DTO
     * @return 내부 모델 기본 응답 DTO
     */
    ModelRes toModelRes(ModelRead modelRead);
    
    /**
     * SKTAI ModelRead를 내부 ModelDetailRes로 변환
     * 
     * @param modelRead SKTAI 모델 응답 DTO
     * @return 내부 모델 상세 응답 DTO
     */
    ModelDetailRes toModelDetailRes(ModelRead modelRead);
    
    /**
     * SKTAI ModelRead 리스트를 내부 ModelRes 리스트로 변환
     * 
     * @param modelReadList SKTAI 모델 응답 DTO 리스트
     * @return 내부 모델 기본 응답 DTO 리스트
     */
    List<ModelRes> toModelResList(List<ModelRead> modelReadList);
    
    /**
     * SKTAI ModelRead 리스트를 내부 ModelDetailRes 리스트로 변환
     * 
     * @param modelReadList SKTAI 모델 응답 DTO 리스트
     * @return 내부 모델 상세 응답 DTO 리스트
     */
    List<ModelDetailRes> toModelDetailResList(List<ModelRead> modelReadList);
}
