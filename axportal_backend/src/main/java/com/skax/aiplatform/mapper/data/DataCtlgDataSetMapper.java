package com.skax.aiplatform.mapper.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.web.multipart.MultipartFile;

import com.skax.aiplatform.client.sktai.data.dto.request.DataSetUpdate;
import com.skax.aiplatform.client.sktai.data.dto.request.DatasetCreate;
import com.skax.aiplatform.client.sktai.data.dto.request.DatasetTags;
import com.skax.aiplatform.client.sktai.data.dto.request.DatasourceCreate;
import com.skax.aiplatform.client.sktai.data.dto.response.DataSetDetail;
import com.skax.aiplatform.client.sktai.data.dto.response.DataSetList;
import com.skax.aiplatform.client.sktai.data.dto.response.DataSourceCreateResponse;
import com.skax.aiplatform.client.sktai.data.dto.response.Dataset;
import com.skax.aiplatform.client.sktai.data.dto.response.DatasetCreateResponse;
import com.skax.aiplatform.client.sktai.data.dto.response.DatasetTag;
import com.skax.aiplatform.client.sktai.data.dto.response.DatasetUpdateResponse;
import com.skax.aiplatform.client.sktai.data.dto.response.DatasourceDetail;
import com.skax.aiplatform.client.sktai.data.dto.response.DatasourceFile;
import com.skax.aiplatform.client.sktai.data.dto.response.DatasourceFileList;
import com.skax.aiplatform.client.sktai.common.dto.Pagination;
import com.skax.aiplatform.client.sktai.common.dto.PaginationLink;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.common.response.PageableInfo;
import com.skax.aiplatform.dto.data.request.DataCtlgDataSetCreateReq;
import com.skax.aiplatform.dto.data.request.DataCtlgDataSetTag;
import com.skax.aiplatform.dto.data.request.DataCtlgDataSetUpdateReq;
import com.skax.aiplatform.dto.data.request.DataCtlgDataSourceCreateReq;
import com.skax.aiplatform.dto.data.request.DataCtlgDatasetUploadReq;
import com.skax.aiplatform.dto.data.request.DataCtlgTrainingDatasetCreateFromFilesReq;
import com.skax.aiplatform.dto.data.response.DataCtlgCustomTrainingDataCreateRes;
import com.skax.aiplatform.dto.data.response.DataCtlgDataSetByIdRes;
import com.skax.aiplatform.dto.data.response.DataCtlgDataSetCreateRes;
import com.skax.aiplatform.dto.data.response.DataCtlgDataSetListRes;
import com.skax.aiplatform.dto.data.response.DataCtlgDataSetUpdateRes;
import com.skax.aiplatform.dto.data.response.DataCtlgDataSourceByIdRes;
import com.skax.aiplatform.dto.data.response.DataCtlgDataSourceCreateRes;
import com.skax.aiplatform.dto.data.response.DataCtlgDataSourceFileListPaginationLinkRes;
import com.skax.aiplatform.dto.data.response.DataCtlgDataSourceFileListPaginationRes;
import com.skax.aiplatform.dto.data.response.DataCtlgDataSourceFileRes;
import com.skax.aiplatform.dto.data.response.DataCtlgDatasetTagRes;

/**
 * 데이터셋 매퍼
 * 
 * <p>
 * SKT AI API 응답과 내부 DTO 간의 변환을 담당하는 MapStruct 매퍼입니다.
 * </p>
 * 
 * @author HyeleeLee
 * @since 2025-08-19
 * @version 1.0
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DataCtlgDataSetMapper {

        /**
         * SKT AI DataSetList를 PageResponse로 변환 (DataSetRes용)
         * 
         * @param sktaiResponse SKT AI API 응답
         * @return PageResponse 객체
         */
        default PageResponse<DataCtlgDataSetListRes> toPageResponseDataSetRes(DataSetList sktaiResponse) {
                if (sktaiResponse == null) {
                        return null;
                }

                List<DataCtlgDataSetListRes> content = toDataSetResList(sktaiResponse.getData());
                PageableInfo pageableInfo = toPageableInfo(sktaiResponse.getPayload().getPagination());

                return PageResponse.<DataCtlgDataSetListRes>builder()
                                .content(content)
                                .pageable(pageableInfo)
                                .totalElements(sktaiResponse.getPayload().getPagination().getTotal().longValue())
                                .totalPages(sktaiResponse.getPayload().getPagination().getLastPage())
                                .first(sktaiResponse.getPayload().getPagination().getPage() == 1)
                                .last(sktaiResponse.getPayload().getPagination().getPage()
                                                .equals(sktaiResponse.getPayload().getPagination().getLastPage()))
                                .hasNext(sktaiResponse.getPayload().getPagination().getNextPageUrl() != null)
                                .hasPrevious(sktaiResponse.getPayload().getPagination().getPrevPageUrl() != null)
                                .build();
        }

        /**
         * SKT DataSetList에 공개설정이 추가된 데이터셋 목록을 PageResponse로 변환
         * 
         * @param sktaiResponse SKT AI API 응답
         * @param content       공개설정이 추가된 데이터셋 목록
         * @return PageResponse 객체
         */
        default PageResponse<DataCtlgDataSetListRes> toPageResponseDataSetResWithContent(
                        DataSetList sktaiResponse,
                        List<DataCtlgDataSetListRes> content) {
                if (sktaiResponse == null) {
                        return null;
                }

                PageableInfo pageableInfo = toPageableInfo(sktaiResponse.getPayload().getPagination());

                return PageResponse.<DataCtlgDataSetListRes>builder()
                                .content(content != null ? content : new ArrayList<>())
                                .pageable(pageableInfo)
                                .totalElements(sktaiResponse.getPayload().getPagination().getTotal().longValue())
                                .totalPages(sktaiResponse.getPayload().getPagination().getLastPage())
                                .first(sktaiResponse.getPayload().getPagination().getPage() == 1)
                                .last(sktaiResponse.getPayload().getPagination().getPage()
                                                .equals(sktaiResponse.getPayload().getPagination().getLastPage()))
                                .hasNext(sktaiResponse.getPayload().getPagination().getNextPageUrl() != null)
                                .hasPrevious(sktaiResponse.getPayload().getPagination().getPrevPageUrl() != null)
                                .build();
        }

        /**
         * SKT AI Dataset을 DataSetRes로 변환
         * 
         * @param sktaiDataset SKT AI Dataset
         * @return DataSetRes
         */
        DataCtlgDataSetListRes toDatasetListRes(Dataset sktaiDataset);

        /**
         * SKT AI Dataset 목록을 DataSetRes 목록으로 변환
         * 
         * @param sktaiDatasets SKT AI Dataset 목록
         * @return DataSetRes 목록
         */
        List<DataCtlgDataSetListRes> toDataSetResList(List<Dataset> sktaiDatasets);

        /**
         * SKT AI Pagination을 PageableInfo로 변환
         * 
         * @param sktaiPagination SKT AI 페이징 정보
         * @return PageableInfo
         */
        @Mapping(target = "page", expression = "java(sktaiPagination.getPage() - 1)") // 1-based를 0-based로 변환
        @Mapping(target = "size", source = "itemsPerPage")
        @Mapping(target = "sort", constant = "") // 기본값으로 빈 문자열 설정
        PageableInfo toPageableInfo(Pagination sktaiPagination);

        /**
         * SKT AI DatasourceFileListPagination을 PageableInfo로 변환
         * 
         * @param sktaiPagination SKT AI 데이터소스 파일 페이징 정보
         * @return PageableInfo
         */
        @Mapping(target = "page", expression = "java(sktaiPagination.getPage() - 1)") // 1-based를 0-based로 변환
        @Mapping(target = "size", source = "itemsPerPage")
        @Mapping(target = "sort", constant = "") // 기본값으로 빈 문자열 설정
        PageableInfo toPageableInfoForDataSourceFile(Pagination sktaiPagination);

        /**
         * sktaiTag를 DataCtlgDatasetTagRes로 변환
         * 
         * @param sktaiTag SKT AI 태그
         * @return 내부 태그
         */
        DataCtlgDatasetTagRes toDatasetTag(DatasetTag sktaiTag);

        /**
         * SKT AI 태그 List을 List<DataCtlgDatasetTagRes>으로 변환
         * 
         * @param sktaiTags SKT AI 태그 목록
         * @return 내부 태그 목록
         */
        List<DataCtlgDatasetTagRes> toDatasetTagList(List<DatasetTag> sktaiTags);

        /**
         * 태그 목록 변환 (Controller → SKT AI)
         * 
         * @param tags 태그 목록
         * @return SKT AI 태그 목록
         */
        List<DatasetTag> toSktaiTags(List<DataCtlgDataSetTag> tags);

        /**
         * 개별 태그 변환 (Controller → SKT AI)
         * 
         * @param tag 태그
         * @return SKT AI 태그
         */
        @Mapping(target = "name", source = "name")
        DatasetTag toSktaiTag(DataCtlgDataSetTag tag);

        /**
         * SKT AI DataSetDetail을 DataCtlgDataSetDetailRes로 변환
         * 
         * @param sktaiDataset SKT AI DataSetDetail
         * @return DataCtlgDataSetByIdRes
         */
        @Mapping(target = "tags", source = "tags")
        DataCtlgDataSetByIdRes toDatasetByIdRes(DataSetDetail sktaiDataset);

        /**
         * SKT AI DatasourceDetail을 DataCtlgDataSourceByIdRes로 변환
         * 
         * @param datasourceDetail SKT AI DatasourceDetail
         * @return DataCtlgDataSourceByIdRes
         */
        DataCtlgDataSourceByIdRes toDatasourceByIdRes(DatasourceDetail datasourceDetail);

        /**
         * SKTAI DatasetUpdateResponse를 DataCtlgDataSetUpdateRes로 변환
         * 
         * @param sktaiResponse SKTAI DatasetUpdateResponse
         * @return DataCtlgDataSetUpdateRes
         */

        DataCtlgDataSetUpdateRes toUpdateResponse(DatasetUpdateResponse sktaiResponse);

        /**
         * Controller 요청을 SKTAI 요청으로 변환
         * 
         * @param request DataCtlgDataSetUpdateReq
         * @return DataSetUpdate
         */
        // @Mapping(target = "description", source = "description")
        // @Mapping(target = "projectId", source = "projectId")
        // @Mapping(target = "policy", source = "policy")
        DataSetUpdate toSktaiUpdateRequest(DataCtlgDataSetUpdateReq request);

        /**
         * SKTAI Dataset을 DataCtlgDataSetUpdateRes로 변환
         * 
         * @param sktaiDataset SKTAI Dataset
         * @return DataCtlgDataSetUpdateRes
         */

        DataCtlgDataSetUpdateRes toDatasetUpdateRes(DatasetUpdateResponse sktaiDataset);

        /**
         * DataCtlgDataSetCreateReq를 DatasetCreateRequest로 변환 (생성용)
         * 
         * @param request DataCtlgDataSetCreateReq
         * @return DatasetCreateRequest
         */
        @Mapping(target = "name", source = "name")
        @Mapping(target = "description", source = "description")
        @Mapping(target = "projectId", source = "projectId")
        @Mapping(target = "tags", source = "tags")
        @Mapping(target = "policy", source = "policy")
        @Mapping(target = "processor", source = "processor")
        @Mapping(target = "type", source = "type")
        @Mapping(target = "status", source = "status")
        @Mapping(target = "isDeleted", source = "isDeleted")
        @Mapping(target = "datasourceId", source = "datasourceId")
        @Mapping(target = "createdBy", source = "createdBy")
        @Mapping(target = "updatedBy", source = "updatedBy")
        DatasetCreate toSktaiCreateRequest(DataCtlgDataSetCreateReq request);

        /**
         * SKT AI Dataset을 DataCtlgDataSetCreateRes로 변환
         * 
         * @param sktaiResponse SKT AI DatasetCreateResponse
         * @return DataCtlgDataSetCreateRes
         */
        @Mapping(target = "id", source = "id")
        @Mapping(target = "name", source = "name")
        @Mapping(target = "description", source = "description")
        @Mapping(target = "projectId", source = "projectId")
        @Mapping(target = "tags", source = "tags")
        @Mapping(target = "createdAt", source = "createdAt")
        @Mapping(target = "updatedAt", source = "updatedAt")
        @Mapping(target = "createdBy", source = "createdBy")
        @Mapping(target = "updatedBy", source = "updatedBy")
        @Mapping(target = "isDeleted", source = "isDeleted")
        @Mapping(target = "type", source = "type")
        @Mapping(target = "status", source = "status")
        @Mapping(target = "datasourceId", source = "datasourceId")
        @Mapping(target = "datasourceFiles", source = "datasourceFiles")
        @Mapping(target = "processor", source = "processor")
        DataCtlgDataSetCreateRes toDatasetCreateRes(DatasetCreateResponse sktaiResponse);

        /**
         * DataCtlgDataSetTag를 DatasetTag로 변환 (생성용)
         * 
         * @param tag DataCtlgDataSetTag
         * @return DatasetTag
         */
        @Mapping(target = "name", source = "name")
        DatasetTags toSktaiTagForCreate(DataCtlgDataSetTag tag);

        // /**
        // * DataCtlgDataSourceCreateReq를 DatasourceCreate로 변환 (생성용)
        // *
        // * @param request DataCtlgDataSourceCreateReq
        // * @return DatasourceCreate
        // */
        // @Mapping(target = "projectId", source = "projectId")
        // @Mapping(target = "name", source = "name")
        // @Mapping(target = "type", source = "type")
        // @Mapping(target = "createdBy", source = "createdBy")
        // @Mapping(target = "updatedBy", source = "updatedBy")
        // @Mapping(target = "description", source = "description")
        // @Mapping(target = "s3Config", source = "s3Config")
        // @Mapping(target = "isDeleted", source = "isDeleted")
        // @Mapping(target = "tempFiles", source = "tempFiles")
        // @Mapping(target = "policy", source = "policy")
        // DatasourceCreate toSktaiDataSourceCreateRequest(DataCtlgDataSourceCreateReq
        // request);

        /**
         * DataCtlgDataSourceCreateReq를 DatasourceCreate로 변환 (생성용)
         * 
         * @param request DataCtlgDataSourceCreateReq
         * @return DatasourceCreate
         */
        @Mapping(target = "projectId", source = "projectId")
        @Mapping(target = "name", source = "name")
        @Mapping(target = "type", source = "type")
        @Mapping(target = "createdBy", source = "createdBy")
        @Mapping(target = "updatedBy", source = "updatedBy")
        @Mapping(target = "description", source = "description")
        @Mapping(target = "s3Config", source = "s3Config")
        @Mapping(target = "isDeleted", source = "isDeleted")
        @Mapping(target = "tempFiles", source = "tempFiles")
        @Mapping(target = "policy", source = "policy")
        @Mapping(target = "scope", expression = "java(\"private_physical\")")
        // // @Mapping(target = "s3Config.bucketName", source = "s3Config.bucketName")
        // @Mapping(target = "s3Config.accessKey", source = "s3Config.accessKey")
        // @Mapping(target = "s3Config.secretKey", source = "s3Config.secretKey")
        // @Mapping(target = "s3Config.region", source = "s3Config.region")
        // @Mapping(target = "s3Config.prefix", source = "s3Config.prefix")
        // @Mapping(target = "s3Config.endpoint", source = "s3Config.endpoint")
        // @Mapping(target = "tempFiles[].fileName", source = "tempFiles[].fileName")
        // @Mapping(target = "tempFiles[].tempFilePath", source =
        // "tempFiles[].tempFilePath")
        // @Mapping(target = "tempFiles[].fileMetadata", source =
        // "tempFiles[].fileMetadata")
        // @Mapping(target = "tempFiles[].knowledgeConfig", source =
        // "tempFiles[].knowledgeConfig")
        DatasourceCreate toSktaiDataSourceCreateRequest(DataCtlgDataSourceCreateReq request);

        /**
         * Datasource를 DataCtlgDataSourceCreateRes로 변환
         * 
         * @param sktaiResponse DataSourceCreateResponse
         * @return DataCtlgDataSourceCreateRes
         */
        @Mapping(target = "id", source = "id")
        @Mapping(target = "name", source = "name")
        @Mapping(target = "type", source = "type")
        @Mapping(target = "description", source = "description")
        @Mapping(target = "projectId", source = "projectId")
        @Mapping(target = "s3Config", source = "s3Config")
        @Mapping(target = "createdBy", source = "createdBy")
        @Mapping(target = "updatedBy", source = "updatedBy")
        @Mapping(target = "createdAt", source = "createdAt")
        @Mapping(target = "updatedAt", source = "updatedAt")
        @Mapping(target = "status", source = "status")
        @Mapping(target = "bucketName", source = "bucketName")
        DataCtlgDataSourceCreateRes toDataSourceCreateRes(DataSourceCreateResponse sktaiResponse);

        /**
         * SKT AI DatasourceFileList를 PageResponse로 변환 (DataSourceFileRes용)
         * 
         * @param sktaiResponse SKT AI DatasourceFileList 응답
         * @return PageResponse 객체
         */
        default PageResponse<DataCtlgDataSourceFileRes> toPageResponseDataSourceFileRes(
                        DatasourceFileList sktaiResponse) {
                if (sktaiResponse == null) {
                        return null;
                }

                List<DataCtlgDataSourceFileRes> content = toDataSourceFileResList(sktaiResponse.getData());
                PageableInfo pageableInfo = toPageableInfoForDataSourceFile(sktaiResponse.getPayload().getPagination());

                return PageResponse.<DataCtlgDataSourceFileRes>builder()
                                .content(content)
                                .pageable(pageableInfo)
                                .totalElements(sktaiResponse.getPayload().getPagination().getTotal().longValue())
                                .totalPages(sktaiResponse.getPayload().getPagination().getLastPage())
                                .first(sktaiResponse.getPayload().getPagination().getPage() == 1)
                                .last(sktaiResponse.getPayload().getPagination().getPage()
                                                .equals(sktaiResponse.getPayload().getPagination().getLastPage()))
                                .hasNext(sktaiResponse.getPayload().getPagination().getNextPageUrl() != null)
                                .hasPrevious(sktaiResponse.getPayload().getPagination().getPrevPageUrl() != null)
                                .build();
        }

        /**
         * SKT AI DatasourceFile을 DataCtlgDataSourceFileRes로 변환
         * 
         * @param sktaiFile SKT AI DatasourceFile
         * @return DataCtlgDataSourceFileRes
         */
        @Mapping(target = "datasourceId", source = "datasourceId")
        @Mapping(target = "fileName", source = "fileName")
        @Mapping(target = "filePath", source = "filePath")
        @Mapping(target = "fileSize", source = "fileSize")
        @Mapping(target = "isDeleted", source = "isDeleted")
        @Mapping(target = "createdAt", source = "createdAt")
        @Mapping(target = "updatedAt", source = "updatedAt")
        @Mapping(target = "createdBy", source = "createdBy")
        @Mapping(target = "updatedBy", source = "updatedBy")
        @Mapping(target = "s3Etag", source = "s3Etag")
        @Mapping(target = "fileMetadata", source = "fileMetadata")
        @Mapping(target = "knowledgeConfig", source = "knowledgeConfig")
        DataCtlgDataSourceFileRes toDataSourceFileRes(DatasourceFile sktaiFile);

        /**
         * SKT AI DatasourceFile 목록을 DataCtlgDataSourceFileRes 목록으로 변환
         * 
         * @param sktaiFiles SKT AI DatasourceFile 목록
         * @return DataCtlgDataSourceFileRes 목록
         */
        List<DataCtlgDataSourceFileRes> toDataSourceFileResList(List<DatasourceFile> sktaiFiles);

        /**
         * SKT AI DatasourceFileListPagination을 DataCtlgDataSourceFileListPaginationRes로
         * 변환
         * 
         * @param sktaiPagination SKT AI 페이징 정보
         * @return DataCtlgDataSourceFileListPaginationRes
         */
        @Mapping(target = "firstPageUrl", source = "firstPageUrl")
        @Mapping(target = "from", source = "from")
        @Mapping(target = "lastPage", source = "lastPage")
        @Mapping(target = "links", source = "links")
        @Mapping(target = "nextPageUrl", source = "nextPageUrl")
        @Mapping(target = "itemsPerPage", source = "itemsPerPage")
        @Mapping(target = "prevPageUrl", source = "prevPageUrl")
        @Mapping(target = "to", source = "to")
        @Mapping(target = "total", source = "total")
        DataCtlgDataSourceFileListPaginationRes toDataSourceFileListPaginationRes(
                        Pagination sktaiPagination);

        /**
         * SKT AI DatasourceFileListPaginationLink을
         * DataCtlgDataSourceFileListPaginationLinkRes로 변환
         * 
         * @param sktaiLink SKT AI 페이징 링크
         * @return DataCtlgDataSourceFileListPaginationLinkRes
         */
        @Mapping(target = "url", source = "url")
        @Mapping(target = "label", source = "label")
        @Mapping(target = "active", source = "active")
        @Mapping(target = "page", source = "page")
        DataCtlgDataSourceFileListPaginationLinkRes toDataSourceFileListPaginationLinkRes(
                        PaginationLink sktaiLink);

        /**
         * SKT AI DatasourceFileListPaginationLink 목록을
         * DataCtlgDataSourceFileListPaginationLinkRes 목록으로 변환
         * 
         * @param sktaiLinks SKT AI 페이징 링크 목록
         * @return DataCtlgDataSourceFileListPaginationLinkRes 목록
         */
        List<DataCtlgDataSourceFileListPaginationLinkRes> toDataSourceFileListPaginationLinkResList(
                        List<PaginationLink> sktaiLinks);

        /**
         * 컨트롤러 파라미터를 DataCtlgDatasetUploadReq로 변환
         * 
         * @param file        업로드할 파일
         * @param projectId   프로젝트 ID
         * @param name        데이터셋 이름
         * @param type        데이터셋 타입
         * @param createdBy   생성자
         * @param payload     페이로드
         * @param status      상태
         * @param tags        태그
         * @param updatedBy   수정자
         * @param description 설명
         * @return DataCtlgDatasetUploadReq
         */
        default DataCtlgDatasetUploadReq toDatasetUploadReq(
                        MultipartFile file,
                        String projectId,
                        String name,
                        String type,
                        String createdBy,
                        String payload,
                        String status,
                        String tags,
                        String updatedBy,
                        String description) {

                return DataCtlgDatasetUploadReq.builder()
                                .name(name)
                                .type(type)
                                .projectId(projectId) // null이면 서비스에서 기본값 설정
                                .createdBy(createdBy) // null이면 서비스에서 현재 사용자로 설정
                                .updatedBy(updatedBy) // null이면 서비스에서 현재 사용자로 설정
                                .payload(payload)
                                .status(status)
                                .tags(tags) // JSON 배열 형태로 전달됨
                                .description(description)
                                .build();
        }

        /**
         * DataCtlgTrainingDatasetCreateFromFilesReq.TempFileDto를
         * DatasourceCreate.TempFileDto로 변환
         * 
         * @param tempFileDto DataCtlgTrainingDatasetCreateFromFilesReq.TempFileDto
         * @return DatasourceCreate.TempFileDto
         */
        default DatasourceCreate.TempFileDto toDatasourceCreateTempFileDto(
                        DataCtlgTrainingDatasetCreateFromFilesReq.TempFileDto tempFileDto) {
                if (tempFileDto == null) {
                        return null;
                }

                return DatasourceCreate.TempFileDto.builder()
                                .fileName(tempFileDto.getFileName() != null ? tempFileDto.getFileName() : "")
                                .tempFilePath(tempFileDto.getTempFilePath() != null ? tempFileDto.getTempFilePath()
                                                : "")
                                .fileMetadata(tempFileDto.getFileMetadata() != null ? tempFileDto.getFileMetadata()
                                                : new HashMap<>())
                                .knowledgeConfig(
                                                tempFileDto.getKnowledgeConfig() != null
                                                                ? tempFileDto.getKnowledgeConfig()
                                                                : new HashMap<>())
                                .build();
        }

        /**
         * DataCtlgTrainingDatasetCreateFromFilesReq.TempFileDto 목록을
         * DatasourceCreate.TempFileDto 목록으로 변환
         * 
         * @param tempFileDtos DataCtlgTrainingDatasetCreateFromFilesReq.TempFileDto 목록
         * @return DatasourceCreate.TempFileDto 목록
         */
        default List<DatasourceCreate.TempFileDto> toDatasourceCreateTempFileDtoList(
                        List<DataCtlgTrainingDatasetCreateFromFilesReq.TempFileDto> tempFileDtos) {
                if (tempFileDtos == null) {
                        return new ArrayList<>();
                }

                List<DatasourceCreate.TempFileDto> result = new ArrayList<>();
                for (DataCtlgTrainingDatasetCreateFromFilesReq.TempFileDto tempFileDto : tempFileDtos) {
                        result.add(toDatasourceCreateTempFileDto(tempFileDto));
                }
                return result;
        }

        /**
         * DataCtlgTrainingDatasetCreateFromFilesReq를 DatasourceCreate로 변환 (파일 타입용)
         * 
         * @param request    DataCtlgTrainingDatasetCreateFromFilesReq
         * @param projectId  프로젝트 ID
         * @param createdBy  생성자
         * @param updatedBy  수정자
         * @param policyList 정책 목록
         * @return DatasourceCreate
         */
        default DatasourceCreate toDatasourceCreateForFileType(
                        DataCtlgTrainingDatasetCreateFromFilesReq request,
                        String projectId,
                        String createdBy,
                        String updatedBy,
                        List<Object> policyList) {

                if (request == null) {
                        return null;
                }

                return DatasourceCreate.builder()
                                .name(request.getName() != null ? request.getName() : "훈련 데이터셋")
                                .type("file")
                                .description(request.getDescription() != null ? request.getDescription()
                                                : "파일 기반 훈련 데이터셋")
                                .projectId(projectId)
                                .s3Config(null) // 파일 타입은 S3 설정 없음
                                .createdBy(createdBy)
                                .updatedBy(updatedBy)
                                .isDeleted(request.getIsDeleted() != null ? request.getIsDeleted() : false)
                                .scope(request.getScope() != null ? request.getScope() : "public")
                                .tempFiles(toDatasourceCreateTempFileDtoList(request.getTempFiles()))
                                .policy(policyList)
                                .build();
        }

        /**
         * SKT AI Dataset을 DataCtlgCustomTrainingDataCreateRes로 변환
         * 
         * @param dataset SKT AI Dataset
         * @return DataCtlgCustomTrainingDataCreateRes
         */
        default DataCtlgCustomTrainingDataCreateRes toCustomTrainingDataCreateRes(
                        com.skax.aiplatform.client.sktai.data.dto.response.Dataset dataset) {
                if (dataset == null) {
                        return null;
                }

                return DataCtlgCustomTrainingDataCreateRes.builder()
                                .id(dataset.getId())
                                .name(dataset.getName())
                                .type(dataset.getType())
                                .description(dataset.getDescription())
                                .tags(dataset.getTags() != null ? dataset.getTags().stream()
                                                .map(tag -> DataCtlgCustomTrainingDataCreateRes.DatasetTag.builder()
                                                                .name(tag.getName())
                                                                .build())
                                                .collect(Collectors.toList()) : new ArrayList<>())
                                .status(dataset.getStatus())
                                .projectId(dataset.getProjectId())
                                .isDeleted(dataset.getIsDeleted())
                                .createdAt(dataset.getCreatedAt())
                                .updatedAt(dataset.getUpdatedAt())
                                .createdBy(dataset.getCreatedBy())
                                .updatedBy(dataset.getUpdatedBy())
                                .datasourceId(dataset.getDatasourceId())
                                .datasourceFiles(dataset.getDatasourceFiles())
                                .processor(dataset.getProcessor())
                                .filePath(dataset.getFilePath())
                                .build();
        }

}
