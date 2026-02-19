package com.skax.aiplatform.mapper.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.skax.aiplatform.client.udp.dataset.dto.request.DatasetSearchRequest;
import com.skax.aiplatform.client.udp.dataset.dto.response.DatasetCardInfo;
import com.skax.aiplatform.client.udp.dataset.dto.response.DatasetSearchResponse;
import com.skax.aiplatform.client.udp.document.dto.request.DocumentSearchRequest;
import com.skax.aiplatform.client.udp.document.dto.response.DocumentInfo;
import com.skax.aiplatform.client.udp.document.dto.response.DocumentSearchResponse;
import com.skax.aiplatform.client.udp.elasticsearch.dto.request.SearchRequest;
import com.skax.aiplatform.client.udp.elasticsearch.dto.response.SearchResponse;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.common.response.PageableInfo;
import com.skax.aiplatform.dto.data.request.DataStorDatasetSearchReq;
import com.skax.aiplatform.dto.data.request.DataStorDocumentSearchReq;
import com.skax.aiplatform.dto.data.request.DataStorTrainEvalSearchReq;
import com.skax.aiplatform.dto.data.response.DataStorDatasetRes;
import com.skax.aiplatform.dto.data.response.DataStorDocumentRes;
import com.skax.aiplatform.dto.data.response.DataStorTrainEvalRes;

/**
 * Data Stor 매퍼
 * 
 * <p>
 * UDP API 응답과 내부 DTO 간의 변환을 담당하는 MapStruct 매퍼입니다.
 * 데이터 카탈로그와 동일한 패턴으로 DTO 변환을 자동화합니다.
 * </p>
 * 
 * @author 장지원
 * @since 2025-10-18
 * @version 1.0
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DataStorMapper {

    /**
     * 내부 요청 DTO를 UDP 요청 DTO로 변환
     * 
     * @param request 내부 검색 요청
     * @return UDP 검색 요청
     */
    default DatasetSearchRequest toUdpRequest(DataStorDatasetSearchReq request) {
        if (request == null) {
            return null;
        }

        // 실제 UDP API 호출용 매핑 (search_word로 전송)
        return DatasetSearchRequest.builder()
                .searchWord(request.getSearchWord()) // searchWord -> search_word
                .originSystemCd(request.getOriginSystemCd()) // originSystemCd -> origin_system_cd
                .countPerPage(request.getCountPerPage() != null ? request.getCountPerPage() : 20L)
                .page(request.getPage() != null ? request.getPage() : 1L)
                .build();
    }

    /**
     * UDP DatasetCardInfo를 DataStorDatasetRes로 변환
     * 
     * @param udpData UDP 데이터셋 카드 정보
     * @return DataStorDatasetRes DTO
     */
    default DataStorDatasetRes toDataStorDatasetRes(DatasetCardInfo udpData) {
        if (udpData == null) {
            return null;
        }

        return DataStorDatasetRes.builder()
                .datasetCardId(udpData.getDatasetCardId())
                .datasetCardName(udpData.getDatasetCardName())
                .datasetCd(udpData.getDatasetCd())
                .datasetName(udpData.getDatasetName())
                .originSystemCd(udpData.getOriginSystemCd())
                .originSystemName(udpData.getOriginSystemName())
                .datasetCardType(udpData.getDatasetCardType())
                .datasetCardSummary(udpData.getDatasetCardSummary())
                .preview(udpData.getPreview())
                .metadata(udpData.getMetadata())
                .downloadPath(udpData.getDownloadPath())
                .build();
    }

    /**
     * UDP DatasetCardInfo 목록을 DataStorDatasetRes 목록으로 변환
     * 
     * @param udpDataList UDP 데이터셋 카드 정보 목록
     * @return DataStorDatasetRes DTO 목록
     */
    List<DataStorDatasetRes> toDataStorDatasetResList(List<DatasetCardInfo> udpDataList);

    /**
     * UDP DatasetSearchResponse를 PageResponse<DataStorDatasetRes>로 변환
     * 
     * @param udpResponse UDP API 응답
     * @param request     원본 요청 (페이징 정보용)
     * @return PageResponse<DataStorDatasetRes> 객체
     */
    default PageResponse<DataStorDatasetRes> toPageResponse(DatasetSearchResponse udpResponse,
            DataStorDatasetSearchReq request) {
        if (udpResponse == null) {
            return null;
        }

        List<DataStorDatasetRes> content = toDataStorDatasetResList(udpResponse.getResultLists());

        // 페이징 정보 계산
        int pageNumber = request.getPage() != null ? request.getPage().intValue() - 1 : 0;
        int pageSize = request.getCountPerPage() != null ? request.getCountPerPage().intValue() : 20;
        long totalElements = udpResponse.getTotalCount() != null ? udpResponse.getTotalCount() : 0;
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);

        PageableInfo pageableInfo = PageableInfo.builder()
                .page(pageNumber)
                .size(pageSize)
                .sort("")
                .build();

        return PageResponse.<DataStorDatasetRes>builder()
                .content(content)
                .pageable(pageableInfo)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(pageNumber == 0)
                .last(pageNumber + 1 >= totalPages)
                .hasNext(pageNumber + 1 < totalPages)
                .hasPrevious(pageNumber > 0)
                .build();
    }

    /**
     * 내부 문서 요청 DTO를 UDP 문서 요청 DTO로 변환
     * 
     * @param request 내부 문서 검색 요청
     * @return UDP 문서 검색 요청
     */
    default DocumentSearchRequest toUdpDocumentRequest(DataStorDocumentSearchReq request) {
        if (request == null) {
            return null;
        }

        return DocumentSearchRequest.builder()
                .datasetCd(request.getDatasetCd())
                .docModStart("19000101") // 전체 조회
                .docModEnd("99991231") // 전체 조회
                .originMetadataYn("Y") // 기본값
                .searchWord(request.getSearchWord())
                .docUuid(request.getUuid()) // 특정 문서 조회
                .countPerPage(request.getCountPerPage() != null ? request.getCountPerPage() : 20L)
                .page(request.getPage() != null ? request.getPage() : 1L)
                .build();
    }

    /**
     * UDP DocumentInfo를 DataStorDocumentRes로 변환
     * 
     * @param udpDocInfo UDP 문서 정보
     * @return DataStorDocumentRes DTO
     */
    @Mapping(target = "datasetCd", source = "datasetCd")
    @Mapping(target = "datasetName", source = "datasetName")
    @Mapping(target = "docUuid", source = "docUuid")
    @Mapping(target = "docTitle", source = "docTitle")
    @Mapping(target = "docSummary", source = "docSummary")
    @Mapping(target = "docCreateDay", source = "docCreateDay")
    @Mapping(target = "docMdfcnDay", source = "docMdfcnDay")
    @Mapping(target = "docPathAnonyMd", source = "docPathAnonyMd")
    @Mapping(target = "attachParentDocUuid", source = "attachParentDocUuid")
    @Mapping(target = "docArrayKeywords", source = "docArrayKeywords")
    @Mapping(target = "originMetadata", source = "originMetadata")
    DataStorDocumentRes toDataStorDocumentRes(DocumentInfo udpDocInfo);

    /**
     * UDP DocumentInfo 목록을 DataStorDocumentRes 목록으로 변환
     * 
     * @param udpDocInfoList UDP 문서 정보 목록
     * @return DataStorDocumentRes DTO 목록
     */
    List<DataStorDocumentRes> toDataStorDocumentResList(List<DocumentInfo> udpDocInfoList);

    /**
     * UDP DocumentSearchResponse를 PageResponse<DataStorDocumentRes>로 변환
     * 
     * @param udpResponse UDP API 응답
     * @param request     원본 요청 (페이징 정보용)
     * @return PageResponse<DataStorDocumentRes> 객체
     */
    default PageResponse<DataStorDocumentRes> toDocumentPageResponse(DocumentSearchResponse udpResponse,
            DataStorDocumentSearchReq request) {
        if (udpResponse == null) {
            return null;
        }

        List<DataStorDocumentRes> content = udpResponse.getResultLists() == null
                ? new ArrayList<>()
                : toDataStorDocumentResList(udpResponse.getResultLists());

        // 페이징 정보 계산
        int pageNumber = request.getPage() != null ? request.getPage().intValue() - 1 : 0;
        int pageSize = request.getCountPerPage() != null ? request.getCountPerPage().intValue() : 20;
        long totalElements = udpResponse.getTotalCount() != null ? udpResponse.getTotalCount() : 0;
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);

        PageableInfo pageableInfo = PageableInfo.builder()
                .page(pageNumber)
                .size(pageSize)
                .sort("")
                .build();

        return PageResponse.<DataStorDocumentRes>builder()
                .content(content)
                .pageable(pageableInfo)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(pageNumber == 0)
                .last(pageNumber + 1 >= totalPages)
                .hasNext(pageNumber + 1 < totalPages)
                .hasPrevious(pageNumber > 0)
                .build();
    }

    /**
     * Elasticsearch 검색 결과의 단일 hit을 DataStorTrainEvalRes로 변환
     * 
     * @param hit Elasticsearch hit 맵
     * @return DataStorTrainEvalRes DTO
     */
    default DataStorTrainEvalRes toDataStorTrainEvalRes(Map<String, Object> hit) {
        if (hit == null) {
            return null;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> source = (Map<String, Object>) hit.get("_source");
        if (source == null) {
            return null;
        }

        return DataStorTrainEvalRes.builder()
                .title(getStringValue(source, "TITLE"))
                .datasetCat01(getStringValue(source, "DATASET_CAT01"))
                .datasetCat02(getStringValue(source, "DATASET_CAT02"))
                .datasetCat03(getStringValue(source, "DATASET_CAT03"))
                .datasetCat04(getStringValue(source, "DATASET_CAT04"))
                .datasetCat05(getStringValue(source, "DATASET_CAT05"))
                .descCtnt(getStringValue(source, "DESC_CTNT"))
                .ozonePath(getStringValue(source, "OZONE_PATH"))
                .tags(getStringValue(source, "TAGS"))
                .createdBy(getStringValue(source, "CREATED_BY"))
                .updatedBy(getStringValue(source, "UPDATED_BY"))
                .fstCreatedAt(getStringValue(source, "FST_CREATED_AT"))
                .lstUpdatedAt(getStringValue(source, "LST_UPDATED_AT"))
                .build();
    }

    /**
     * Elasticsearch 검색 결과 hits 목록을 DataStorTrainEvalRes 목록으로 변환
     * 
     * @param hits Elasticsearch hits 목록
     * @return DataStorTrainEvalRes DTO 목록
     */
    default List<DataStorTrainEvalRes> toDataStorTrainEvalResList(List<Map<String, Object>> hits) {
        if (hits == null || hits.isEmpty()) {
            return new ArrayList<>();
        }

        return hits.stream()
                .map(this::toDataStorTrainEvalRes)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * SearchResponse를 PageResponse<DataStorTrainEvalRes>로 변환 (DTO 변환만)
     * 
     * @param elasticResponse Elasticsearch API 응답
     * @param trainDataList   이미 필터링된 데이터 목록 (서비스에서 처리)
     * @param request         원본 요청 (페이징 정보용)
     * @return PageResponse<DataStorTrainEvalRes> 객체
     */
    default PageResponse<DataStorTrainEvalRes> toTrainEvalDataPageResponse(SearchResponse elasticResponse,
            List<DataStorTrainEvalRes> trainDataList, DataStorTrainEvalSearchReq request) {
        if (elasticResponse == null) {
            return null;
        }

        // 페이징 정보 설정
        int page = request.getPage() != null ? request.getPage().intValue() : 1;
        int countPerPage = request.getCountPerPage() != null ? request.getCountPerPage().intValue() : 20;

        // TITLE 필터링이 적용된 경우 필터링 후의 실제 결과 개수를 사용
        // TITLE 필터링이 없는 경우 Elasticsearch의 전체 개수를 사용
        long totalElements;
        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            // TITLE 필터링이 적용된 경우: 필터링된 결과 개수 사용
            totalElements = trainDataList != null ? trainDataList.size() : 0;
        } else {
            // TITLE 필터링이 없는 경우: Elasticsearch의 전체 개수 사용
            totalElements = elasticResponse.getTotalHits() != null ? elasticResponse.getTotalHits() : 0;
        }

        int totalPages = (int) Math.ceil((double) totalElements / countPerPage);

        PageableInfo pageableInfo = PageableInfo.builder()
                .page(page - 1) // Spring Page는 0부터 시작
                .size(countPerPage)
                .sort("")
                .build();

        return PageResponse.<DataStorTrainEvalRes>builder()
                .content(trainDataList)
                .pageable(pageableInfo)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .first(page == 1)
                .last(page == totalPages)
                .hasNext(page < totalPages)
                .hasPrevious(page > 1)
                .build();
    }

    /**
     * Map에서 String 값을 안전하게 추출하는 헬퍼 메서드
     * 
     * @param source 소스 맵
     * @param key    키
     * @return String 값 또는 null
     */
    default String getStringValue(Map<String, Object> source, String key) {
        Object value = source.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * DataStorTrainEvalSearchReq를 SearchRequest(feign client) 로 변환 (DTO 변환만)
     * 
     * @param esIndexName Elasticsearch 인덱스 이름
     * @param queryBody   이미 생성된 쿼리 바디 (서비스에서 생성)
     * @return SearchRequest 객체
     */
    default SearchRequest convertToElasticsearchRequest(String esIndexName, Map<String, Object> queryBody) {
        return SearchRequest.builder()
                .indexName(esIndexName)
                .queryBody(queryBody)
                .build();
    }

}