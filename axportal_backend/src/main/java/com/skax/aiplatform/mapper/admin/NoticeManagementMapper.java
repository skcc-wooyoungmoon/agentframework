package com.skax.aiplatform.mapper.admin;

import com.skax.aiplatform.dto.admin.request.NoticeManagementCreateReq;
import com.skax.aiplatform.dto.admin.request.NoticeManagementUpdateReq;
import com.skax.aiplatform.dto.admin.response.NoticeManagementRes;
import com.skax.aiplatform.entity.NoticeManagement;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * 공지사항 매퍼
 * 
 * <p>
 * 공지사항 엔티티와 DTO 간의 변환을 담당하는 MapStruct 매퍼입니다.
 * </p>
 * 
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NoticeManagementMapper {

    /**
     * 생성 요청 DTO를 엔티티로 변환
     * 
     * @param createReq 생성 요청 DTO
     * @return 공지사항 엔티티
     */
    @Mapping(target = "notiId", ignore = true)
    @Mapping(target = "title", source = "title")
    @Mapping(target = "msg", source = "msg")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "useYn", expression = "java(com.skax.aiplatform.entity.NoticeManagement.convertStringToNumber(createReq.getUseYn()))")
    @Mapping(target = "expFrom", ignore = true)
    @Mapping(target = "expTo", ignore = true)
    NoticeManagement toEntity(NoticeManagementCreateReq createReq);

    /**
     * 엔티티를 응답 DTO로 변환
     * 
     * @param noticeManagement 공지사항 엔티티
     * @return 응답 DTO
     */
    @Mapping(target = "files", ignore = true)
    @Mapping(target = "useYn", expression = "java(com.skax.aiplatform.entity.NoticeManagement.convertNumberToString(noticeManagement.getUseYn()))")
    NoticeManagementRes toResponse(NoticeManagement noticeManagement);

    /**
     * 수정 요청 DTO로 기존 엔티티 업데이트
     * 
     * @param updateReq  수정 요청 DTO
     * @param noticeManagement 기존 엔티티
     */
    @Mapping(target = "notiId", ignore = true)
    @Mapping(target = "title", source = "title")
    @Mapping(target = "msg", source = "msg")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "useYn", expression = "java(com.skax.aiplatform.entity.NoticeManagement.convertStringToNumber(updateReq.getUseYn()))")
    @Mapping(target = "expFrom", ignore = true)
    @Mapping(target = "expTo", ignore = true)
    void updateEntity(NoticeManagementUpdateReq updateReq, @MappingTarget NoticeManagement noticeManagement);
}
