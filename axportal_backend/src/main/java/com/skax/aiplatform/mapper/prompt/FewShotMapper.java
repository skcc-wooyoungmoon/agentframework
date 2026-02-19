package com.skax.aiplatform.mapper.prompt;

import com.skax.aiplatform.dto.prompt.request.FewShotCreateReq;
import com.skax.aiplatform.dto.prompt.request.FewShotUpdateReq;
import com.skax.aiplatform.dto.prompt.response.FewShotRes;
import com.skax.aiplatform.dto.prompt.response.FewShotVerRes;   
import com.skax.aiplatform.dto.prompt.response.FewShotItemRes;
import com.skax.aiplatform.dto.prompt.response.FewShotCreateRes;
import com.skax.aiplatform.client.sktai.agent.dto.request.FewShotCreateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.request.FewShotUpdateRequest;
import com.skax.aiplatform.client.sktai.agent.dto.response.CommonResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotCreateResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotTagListResponse;
import com.skax.aiplatform.dto.prompt.response.FewShotTagListRes;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotTagsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotVersionsResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotVersionResponse;
import com.skax.aiplatform.client.sktai.agent.dto.response.FewShotItemsResponse;
import com.skax.aiplatform.dto.prompt.response.FewShotTagRes;
import java.lang.ClassCastException;
import java.time.format.DateTimeParseException;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
/**
 * Few-Shot 매퍼
 * 
 * <p>내부 DTO와 외부 API DTO 간의 변환을 담당하는 MapStruct 매퍼입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-11
 * @version 1.0.0
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface FewShotMapper {
    

    
    /**
     * FewShotSummary를 FewShotRes로 변환
     */
    default FewShotRes from(FewShotsResponse.FewShotSummary summary) {
        if (summary == null) {
            return null;
        }
        
        return FewShotRes.builder()
                .uuid(summary.getUuid())
                .name(summary.getName())
                .dependency(summary.getDependency())
                .createdAt(summary.getCreatedAt())
                .releaseVersion(summary.getReleaseVersion())
                .latestVersion(summary.getLatestVersion())
                .tags(summary.getTags())
                .hitRate(summary.getHitRate())
                .build();
    }
    
    /**
     * FewShotSummary 리스트를 FewShotRes 리스트로 변환
     */
    default List<FewShotRes> toFewShotResList(List<FewShotsResponse.FewShotSummary> summaryList) {
        if (summaryList == null) {
            return List.of();
        }
        
        return summaryList.stream()
                .map(this::from)
                .filter(item -> item != null)
                .collect(Collectors.toList());
    }
    
    /**
     * FewShotsResponse를 Page<FewShotRes>로 변환
     */
    default Page<FewShotRes> toNewResponse(FewShotsResponse fewShotsResponse) {
        if (fewShotsResponse == null || fewShotsResponse.getData() == null) {
            return new PageImpl<>(List.of());
        }
        
        List<FewShotRes> fewShotList = toFewShotResList(fewShotsResponse.getData());
        return new PageImpl<>(fewShotList);
    }
    
    /**
     * CommonResponse를 Page<FewShotRes>로 변환 (간단한 구현)
     */
    default Page<FewShotRes> toNewResponse(CommonResponse commonResponse) {
        if (commonResponse == null || commonResponse.getData() == null) {
            return new PageImpl<>(List.of());
        }
        
        try {
            // CommonResponse의 data가 FewShotSummary 리스트인 경우
            if (commonResponse.getData() instanceof List) {
                @SuppressWarnings("unchecked")
                List<FewShotsResponse.FewShotSummary> summaryList = (List<FewShotsResponse.FewShotSummary>) commonResponse.getData();
                List<FewShotRes> fewShotList = toFewShotResList(summaryList);
                return new PageImpl<>(fewShotList);
            }
        } catch (ClassCastException e) {
            // 타입 캐스팅 실패 시 빈 페이지 반환
            return new PageImpl<>(List.of());
        }
        
        return new PageImpl<>(List.of());
    }

    
    /**
     * FewShotResponse를 FewShotByIdRes로 변환 (상세 조회용)
     * 
     * @param fewShotResponse 외부 API 응답
     * @return FewShotByIdRes
     */
    default FewShotRes toNewResponseForById(FewShotResponse fewShotResponse) {
        if (fewShotResponse == null || fewShotResponse.getData() == null) {
            return null;
        }

        FewShotResponse.FewShotDetail detail = fewShotResponse.getData();
        
        return FewShotRes.builder()
                .uuid(detail.getUuid())
                .name(detail.getName())
                .dependency(detail.getDependency())
                .createdAt(detail.getCreatedAt() != null ? detail.getCreatedAt().toString() : null)
                .releaseVersion(detail.getReleaseVersion())
                .latestVersion(detail.getLatestVersion())
                .tags(detail.getTags())
                .hitRate(detail.getHitRate())
                .createdBy(detail.getCreatedBy())
                .build();
    }

    /**
     * 태그 리스트를 String 리스트로 변환
     */
    default List<String> parseTagsList(Object tagsObj) {
        if (tagsObj == null) {
            return List.of();
        }
        
        try {
            if (tagsObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> tagsList = (List<String>) tagsObj;
                return tagsList;
            }
        } catch (ClassCastException e) {
            // 타입 캐스팅 실패 시 빈 리스트 반환
            return List.of();
        }
        
        return List.of();
    }
    
    /**
     * 날짜 문자열을 LocalDateTime으로 변환하는 헬퍼 메서드
     */
    default LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        
        try {
            // ISO 8601 형식 파싱 (예: "2025-04-28T19:43:06.451495Z")
            return ZonedDateTime.parse(dateTimeStr).toLocalDateTime();
        } catch (DateTimeParseException e) {
            // 날짜 파싱 실패 시 null 반환
            return null;
        }
    }
    
    /**
     * FewShotVersionResponse를 FewShotVerByIdRes로 변환
     */
    default FewShotVerRes toNewResponse(FewShotVersionResponse response) {
        if (response == null || response.getData() == null) {
            return null;
        }

        FewShotVersionResponse.FewShotVersionDetail detail = response.getData();
        
        return FewShotVerRes.builder()
                .version(detail.getVersion())
                .release(detail.getRelease())
                .deleteFlag(detail.getDeleteFlag())
                .createdBy(detail.getCreatedBy())
                .createdAt(detail.getCreatedAt())
                .versionId(detail.getVersionId())
                .uuid(detail.getUuid())
                .build();
    }

    /**
     * FewShotVersionResponse를 FewShotVerByIdRes로 변환
     */
    default FewShotVerRes toNewResponseForVersion(FewShotVersionResponse response) {
        if (response == null || response.getData() == null) {
            return null;
        }

        FewShotVersionResponse.FewShotVersionDetail detail = response.getData();

        return FewShotVerRes.builder()
                .version(detail.getVersion())
                .release(detail.getRelease())
                .deleteFlag(detail.getDeleteFlag())
                .createdBy(detail.getCreatedBy())
                .createdAt(detail.getCreatedAt())
                .versionId(detail.getVersionId())
                .uuid(detail.getUuid())
                .build();
    }

    /**
     * FewShotVersionsResponse를 List<FewShotVerRes>로 변환
     */
    default List<FewShotVerRes> toNewResponseForVersions(FewShotVersionsResponse response) {
        if (response == null || response.getData() == null) {
            return List.of();
        }
        
        return response.getData().stream()
                .map(detail -> FewShotVerRes.builder()
                        .version(detail.getVersion())
                        .release(detail.getRelease())
                        .deleteFlag(detail.getDeleteFlag())
                        .createdBy(detail.getCreatedBy())
                        .createdAt(detail.getCreatedAt())
                        .versionId(detail.getVersionId())
                        .uuid(detail.getUuid())
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * FewShotCreateResponse를 FewShotCreateRes로 변환
     */
    default FewShotCreateRes toNewCreateResponse(FewShotCreateResponse response) {
        if (response == null) {
            return null;
        }
        
        return FewShotCreateRes.builder()
                .fewShotUuid(response.getData().getFewShotUuid())
                .build();
    }
    
    /**
     * FewShotCreateReq를 FewShotCreateRequest로 변환
     */
    default FewShotCreateRequest toNewCreateRequest(FewShotCreateReq request) {
        if (request == null) {
            return null;
        }
        
        return FewShotCreateRequest.builder()
                .name(request.getName())
                .projectId(request.getProjectId())
                .release(request.getRelease())
                .items(convertItems(request.getItems()))
                .tags(convertTags(request.getTags()))
                .build();
    }

    /**
     * FewShotUpdateResponse를 FewShotUpdateRes 변환
     */
    // default FewShotUpdateRes toNewUpdateResponse(FewShotUpdateResponse response) {
    //     if (response == null) {
    //         return null;
    //     }
        
    //     return FewShotUpdateRes.builder()
    //             .result(response.getData().isResult())
    //             .build();
    // }
    
    /**
     * FewShotCreateReq를 FewShotUpdateRequest로 변환
     */
    default FewShotUpdateRequest toNewUpdateRequest(String fewShotUuid, FewShotUpdateReq request) {
        if (request == null) {
            return null;
        }
        
        return FewShotUpdateRequest.builder()
                .newName(request.getNewName())
                .items(convertUpdateItems(request.getItems()))
                .release(request.getRelease())
                .tags(convertUpdateTags(request.getTags()))  
                .build();
    }
    
    /**
     * FewShotItem 리스트를 FewShotExample 리스트로 변환 (공통 메서드)
     */
    default List<FewShotCreateRequest.FewShotItem> convertItems(List<FewShotCreateReq.FewShotItem> items) {
        if (items == null) {
            return List.of();
        }
        
        return items.stream()
                .map(item -> FewShotCreateRequest.FewShotItem.builder()
                        .itemQuery(item.getItemQuery())
                        .itemAnswer(item.getItemAnswer())
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * FewShotUpdateReq.FewShotItem 리스트를 FewShotUpdateRequest.FewShotItems 리스트로 변환
     */
    default List<FewShotUpdateRequest.FewShotItem> convertUpdateItems(List<FewShotUpdateReq.FewShotItem> items) {
        if (items == null) {
            return List.of();
        }
        
        return items.stream()
                .map(item -> FewShotUpdateRequest.FewShotItem.builder()
                        .itemAnswer(item.getItemAnswer())
                        .itemQuery(item.getItemQuery())
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * FewShotTag 리스트를 FewShotTag 리스트로 변환 (공통 메서드)
     */
    default List<FewShotCreateRequest.FewShotTag> convertTags(List<FewShotCreateReq.FewShotTag> tags) {
        if (tags == null) {
            return List.of();
        }
        
        return tags.stream()
                .map(tag -> FewShotCreateRequest.FewShotTag.builder()
                        .tag(tag.getTag())
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * FewShotUpdateReq.FewShotTag 리스트를 FewShotUpdateRequest.FewShotTag 리스트로 변환
     */
    default List<FewShotUpdateRequest.FewShotTag> convertUpdateTags(List<FewShotUpdateReq.FewShotTag> tags) {
        if (tags == null) {
            return List.of();
        }
        
        return tags.stream()
                .map(tag -> FewShotUpdateRequest.FewShotTag.builder()
                        .tag(tag.getTag())
                        .build())
                .collect(Collectors.toList());
    }

        
    /**
     * FewShotVersionsResponse를 List<FewShotVerRes>로 변환
     */
    default List<FewShotItemRes> toNewResponseForItems(FewShotItemsResponse response) {
        if (response == null || response.getData() == null) {
            return List.of();
        }
        
        return response.getData().stream()
                .map(detail -> FewShotItemRes.builder()
                        .uuid(detail.getUuid())
                        .itemSequence(detail.getItemSequence())
                        .item(detail.getItem())
                        .versionId(detail.getVersionId())
                        .itemType(detail.getItemType())
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * FewShotVersionsResponse를 List<FewShotVerRes>로 변환
     */
    default List<FewShotTagRes> toNewResponseForTags(FewShotTagsResponse response) {
        if (response == null || response.getData() == null) {
            return List.of();
        }
        
        return response.getData().stream()
                .map(detail -> FewShotTagRes.builder()
                        .tagUuid(detail.getTagUuid())
                        .tag(detail.getTag())
                        .fewShotUuid(detail.getFewShotUuid())
                        .versionId(detail.getVersionId())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * FewShotTagListResponse를 FewShotTagListRes로 변환
     */
    default FewShotTagListRes toNewResponseForTagsList(FewShotTagListResponse response) {
        if (response == null) {
            return null;
        }
        
        return FewShotTagListRes.builder()
                .tags(response.getData() == null ? List.of() : response.getData())
                .build();
    }
}