package com.skax.aiplatform.mapper.sample;

import com.skax.aiplatform.dto.sample.request.SampleUserCreateReq;
import com.skax.aiplatform.dto.sample.request.SampleUserUpdateReq;
import com.skax.aiplatform.dto.sample.response.SampleUserRes;
import com.skax.aiplatform.entity.sample.SampleUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * 샘플 사용자 매퍼
 * 
 * <p>샘플 사용자 엔티티와 DTO 간의 변환을 담당하는 MapStruct 매퍼입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-03
 * @version 2.0.0
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface SampleUserMapper {
    
    /**
     * 생성 요청 DTO를 엔티티로 변환
     * 
     * @param createReq 생성 요청 DTO
     * @return 샘플 사용자 엔티티
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    SampleUser toEntity(SampleUserCreateReq createReq);
    
    /**
     * 엔티티를 응답 DTO로 변환
     * 
     * @param sampleUser 샘플 사용자 엔티티
     * @return 응답 DTO
     */
    SampleUserRes toResponse(SampleUser sampleUser);
    
    /**
     * 수정 요청 DTO로 기존 엔티티 업데이트
     * 
     * @param updateReq 수정 요청 DTO
     * @param sampleUser 기존 엔티티
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    void updateEntity(SampleUserUpdateReq updateReq, @MappingTarget SampleUser sampleUser);
}