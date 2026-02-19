package com.skax.aiplatform.controller.data;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.data.request.DataCtlgDataSetTagUpdateReq;
import com.skax.aiplatform.dto.data.request.DataCtlgDataSetUpdateReq;
import com.skax.aiplatform.dto.data.request.DataCtlgDataSourceFileDownloadReq;
import com.skax.aiplatform.dto.data.request.DataCtlgTrainingDatasetCreateFromFilesReq;
import com.skax.aiplatform.dto.data.response.DataCtlgCustomTrainingDataCreateRes;
import com.skax.aiplatform.dto.data.response.DataCtlgDataSetByIdRes;
import com.skax.aiplatform.dto.data.response.DataCtlgDataSetListRes;
import com.skax.aiplatform.dto.data.response.DataCtlgDataSetUpdateRes;
import com.skax.aiplatform.dto.data.response.DataCtlgDataSourceByIdRes;
import com.skax.aiplatform.dto.data.response.DataCtlgDataSourceFileRes;
import com.skax.aiplatform.dto.data.response.DataCtlgDataSourceUdpRegisterRes;
import com.skax.aiplatform.dto.data.response.DataCtlgTrainingDataCreateRes;
import com.skax.aiplatform.dto.data.response.DataCtlgUploadFilesRes;
import com.skax.aiplatform.service.data.DataCtlgDataSetService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 데이터셋 컨트롤러
 * 
 * <p>
 * 데이터셋 관련 조회, 상세조회, 추가, 수정, 삭제를 관리하는 컨트롤러입니다.
 * </p>
 * 
 * @author HyeleeLee
 * @since 2025-08-19
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/data-catalog")
@RequiredArgsConstructor
@Validated
@Tag(name = "Dataset Management", description = "데이터셋 관리 API")
public class DataCtlgDataSetController {

        private final DataCtlgDataSetService dataCtlgDataSetService;

        /**
         * 데이터셋 목록 조회
         * 
         * @param request 조회 요청 파라미터
         * @return 데이터셋 목록 응답
         */
        @GetMapping("/datasets")
        @Operation(summary = "데이터셋 목록 조회", description = "등록된 데이터셋들의 목록을 페이징하여 조회합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "데이터셋 목록 조회 성공", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DataCtlgDataSetListRes.class))),
                        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
                        @ApiResponse(responseCode = "401", description = "인증 실패"),
                        @ApiResponse(responseCode = "403", description = "권한 없음"),
                        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        })
        public AxResponseEntity<PageResponse<DataCtlgDataSetListRes>> getDatasets(
                        // @Valid DataCtlgDataSetListReq request)
                        @RequestParam(value = "page", defaultValue = "1") @Parameter(description = "페이지 번호", example = "1") Integer page,
                        @RequestParam(value = "size", defaultValue = "10") @Parameter(description = "페이지 크기", example = "10") Integer size,
                        @RequestParam(value = "sort", required = false) @Parameter(description = "정렬 조건") String sort,
                        @RequestParam(value = "filter", required = false) @Parameter(description = "필터 조건") String filter,
                        @RequestParam(value = "search", required = false) @Parameter(description = "검색 키워드") String search
        // @PageableDefault(size = 20) Pageable pageable
        ) {

                log.info("데이터셋 목록 조회 API 호출 - 페이지: {}, 크기: {}, 정렬: {}, 필터: {}, 검색: {}",
                                page, size, sort, filter, search);

                PageResponse<DataCtlgDataSetListRes> pageDataCtlgDataSetsResponse = dataCtlgDataSetService.getDatasets(
                                page,
                                size, sort, filter, search);
                // PageResponse<DataCtlgDataSetListRes> pageDataCtlgDataSetListRes =
                // dataCtlgDataSetService.getDatasets(page, size, sort, filter, search,
                // pageable);
                log.info("데이터셋 목록 조회 API 완료 - 총 {}개 데이터셋", pageDataCtlgDataSetsResponse.getTotalElements());

                return AxResponseEntity.okPage(pageDataCtlgDataSetsResponse, "데이터셋 목록 조회가 완료되었습니다.");
        }

        /**
         * 데이터셋 상세 조회
         * 
         * @param datasetId 데이터셋 ID
         * @return 데이터셋 상세 정보
         */
        @GetMapping("/datasets/{datasetId}")
        @Operation(summary = "데이터셋 상세 조회", description = "지정된 ID의 데이터셋 상세 정보를 조회합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "데이터셋 상세 조회 성공", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DataCtlgDataSetByIdRes.class))),
                        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
                        @ApiResponse(responseCode = "404", description = "데이터셋을 찾을 수 없음"),
                        @ApiResponse(responseCode = "401", description = "인증 실패"),
                        @ApiResponse(responseCode = "403", description = "권한 없음"),
                        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        })
        public AxResponseEntity<DataCtlgDataSetByIdRes> getDatasetById(
                        @Parameter(description = "데이터셋 ID", example = "123e4567-e89b-12d3-a456-426614174000", required = true) @PathVariable(name = "datasetId") UUID datasetId) {

                log.info("데이터셋 상세 조회 API 호출 - datasetId: {}", datasetId);

                DataCtlgDataSetByIdRes dataSetByIdResponse = dataCtlgDataSetService.getDatasetById(datasetId);

                log.info("데이터셋 상세 조회 API 완료 - datasetId: {}, name: {}", datasetId, dataSetByIdResponse.getName());
                return AxResponseEntity.ok(dataSetByIdResponse, "데이터셋 상세 정보 조회가 완료되었습니다");
        }

        /**
         * 데이터셋 생성
         * 
         * @param request 데이터셋 생성 요청
         * @return 생성된 데이터셋 정보
         */
        // @PostMapping("/datasets")
        // @Operation(summary = "데이터셋 생성", description = "새로운 데이터셋을 생성합니다.")
        // @ApiResponses({
        // @ApiResponse(responseCode = "201", description = "데이터셋 생성 성공", content =
        // @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =
        // @Schema(implementation = DataCtlgDataSetCreateRes.class))),
        // @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        // @ApiResponse(responseCode = "401", description = "인증 실패"),
        // @ApiResponse(responseCode = "403", description = "권한 없음"),
        // @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        // })
        // public AxResponseEntity<DataCtlgDataSetCreateRes> createDataset(
        // @Parameter(description = "데이터셋 생성 요청", required = true) @Valid @RequestBody
        // DataCtlgDataSetCreateReq request) {

        // log.info(">>>>>>>>>>>>>>데이터셋 생성 API 호출 - request: {}", request);

        // DataCtlgDataSetCreateRes response =
        // dataCtlgDataSetService.createDataset(request);

        // log.info("데이터셋 생성 API 완료 - response: {}", response);
        // return AxResponseEntity.created(response, "데이터셋 생성이 완료되었습니다");
        // }

        /**
         * 데이터셋 수정
         * 
         * @param datasetId 데이터셋 ID
         * @param request   데이터셋 수정 요청
         * @return 수정된 데이터셋 정보
         */
        @PutMapping("/datasets/{datasetId}")
        @Operation(summary = "데이터셋 수정", description = "지정된 ID의 데이터셋 정보를 수정합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "데이터셋 수정 성공", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DataCtlgDataSetUpdateRes.class))),
                        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
                        @ApiResponse(responseCode = "404", description = "데이터셋을 찾을 수 없음"),
                        @ApiResponse(responseCode = "401", description = "인증 실패"),
                        @ApiResponse(responseCode = "403", description = "권한 없음"),
                        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        })
        public AxResponseEntity<DataCtlgDataSetUpdateRes> updateDataset(
                        @Parameter(description = "데이터셋 ID", example = "e3b4e453-293e-4a4b-867a-cdd36e212180", required = true) @PathVariable(name = "datasetId") UUID datasetId,
                        @Parameter(description = "데이터셋 수정 요청", required = true) @Valid @RequestBody DataCtlgDataSetUpdateReq request) {

                log.info("데이터셋 수정 API 호출 - datasetId: {}, request: {}", datasetId, request);

                DataCtlgDataSetUpdateRes response = dataCtlgDataSetService.updateDataset(datasetId, request);

                log.info("데이터셋 수정 API 완료 - datasetId: {}, name: {}", datasetId, response.getName());
                return AxResponseEntity.ok(response, "데이터셋 수정이 완료되었습니다");
        }

        /**
         * 데이터셋 삭제
         * 
         * @param datasetId    데이터셋 ID
         * @param dataSourceId 데이터소스 ID
         * @return 데이터셋/데이터소스 삭제 정보
         */

        @DeleteMapping("/datasets/{datasetId}")
        @Operation(summary = "데이터셋 삭제", description = "지정된 ID의 데이터셋을 삭제합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "데이터셋 삭제 성공", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DataCtlgDataSetByIdRes.class))),
                        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
                        @ApiResponse(responseCode = "404", description = "데이터셋을 찾을 수 없음"),
                        @ApiResponse(responseCode = "401", description = "인증 실패"),
                        @ApiResponse(responseCode = "403", description = "권한 없음"),
                        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        })
        public AxResponseEntity<Void> deleteDataSet(
                        @Parameter(description = "데이터셋 ID", example = "123e4567-e89b-12d3-a456-426614174000", required = true) @PathVariable(name = "datasetId") UUID datasetId,
                        @Parameter(description = "데이터소스 ID", example = "123e4567-e89b-12d3-a456-426614174000", required = true) @RequestParam(name = "dataSourceId") UUID dataSourceId) {

                log.info("데이터셋/데이터소스 삭제 API 호출 - datasetId: {}, datasourceId: {}", datasetId, dataSourceId);

                dataCtlgDataSetService.deleteDataSet(datasetId, dataSourceId);

                log.info("데이터셋/데이터소스 삭제 API 완료 - datasetId: {}, datasourceId: {}", datasetId, dataSourceId);

                return AxResponseEntity.ok(null, "데이터셋 삭제가 완료되었습니다");

        }

        /**
         * 커스텀 학습데이터셋 삭제
         * 
         * @param datasetId 데이터셋 ID
         * @return 커스텀 학습데이터셋 삭제 정보
         */

        @DeleteMapping("/datasets/{datasetId}/custom")
        @Operation(summary = "데이터셋 삭제", description = "지정된 ID의 데이터셋을 삭제합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "데이터셋 삭제 성공", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DataCtlgDataSetByIdRes.class))),
                        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
                        @ApiResponse(responseCode = "404", description = "데이터셋을 찾을 수 없음"),
                        @ApiResponse(responseCode = "401", description = "인증 실패"),
                        @ApiResponse(responseCode = "403", description = "권한 없음"),
                        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        })
        public AxResponseEntity<Void> deleteCustomDataSet(
                        @Parameter(description = "데이터셋 ID", example = "123e4567-e89b-12d3-a456-426614174000", required = true) @PathVariable(name = "datasetId") UUID datasetId) {

                log.info("커스텀 학습데이터셋 삭제 API 호출 - datasetId: {}", datasetId);

                dataCtlgDataSetService.deleteCustomDataSet(datasetId);

                log.info("커스텀 학습데이터셋 삭제 API 완료 - datasetId: {}", datasetId);

                return AxResponseEntity.ok(null, "커스텀 학습데이터셋 삭제가 완료되었습니다");

        }

        /**
         * 데이터소스 상세 조회
         * 
         * @param datasourceId 데이터소스 ID
         * @return 데이터소스 상세 정보
         */

        @GetMapping("/datasources/{datasourceId}")
        @Operation(summary = "데이터소스 상세 조회", description = "지정된 ID의 데이터소스 상세 정보를 조회합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "데이터소스 상세 조회 성공", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DataCtlgDataSetByIdRes.class))),
                        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
                        @ApiResponse(responseCode = "404", description = "데이터셋을 찾을 수 없음"),
                        @ApiResponse(responseCode = "401", description = "인증 실패"),
                        @ApiResponse(responseCode = "403", description = "권한 없음"),
                        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        })
        public AxResponseEntity<DataCtlgDataSourceByIdRes> getDataSourceById(
                        @Parameter(description = "데이터소스 ID", example = "14fba495-2970-46e3-933c-11fd08439e8d", required = true) @PathVariable(name = "datasourceId") UUID datasourceId) {

                log.info("데이터소스 상세 조회 API 호출 - datasourceId: {}", datasourceId);

                DataCtlgDataSourceByIdRes dataSourceByIdResponse = dataCtlgDataSetService
                                .getDataSourceById(datasourceId);

                log.info("데이터소스 상세 조회 API 완료 - datasourceId: {}, name: {}", datasourceId,
                                dataSourceByIdResponse.getName());
                return AxResponseEntity.ok(dataSourceByIdResponse, "데이터소스 상세 정보 조회가 완료되었습니다");
        }

        /**
         * 데이터소스 생성
         * 
         * @param request 데이터소스 생성 요청
         * @return 생성된 데이터소스 정보
         */
        // @PostMapping("/datasources")
        // @Operation(summary = "데이터소스 생성", description = "새로운 데이터소스를 생성합니다.")
        // @ApiResponses({
        // @ApiResponse(responseCode = "201", description = "데이터소스 생성 성공", content =
        // @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema =
        // @Schema(implementation = DataCtlgDataSourceCreateRes.class))),
        // @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        // @ApiResponse(responseCode = "401", description = "인증 실패"),
        // @ApiResponse(responseCode = "403", description = "권한 없음"),
        // @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        // })
        // public AxResponseEntity<DataCtlgDataSourceCreateRes> createDataSource(
        // @Parameter(description = "데이터소스 생성 요청", required = true) @Valid @RequestBody
        // DataCtlgDataSourceCreateReq request) {

        // log.info("데이터소스 생성 API 호출 - request: {}", request);

        // DataCtlgDataSourceCreateRes response =
        // dataCtlgDataSetService.createDataSource(request);

        // log.info("데이터소스 생성 API 완료 - response: {}", response);
        // return AxResponseEntity.created(response, "데이터소스 생성이 완료되었습니다");
        // }

        /**
         * 데이터셋 태그 수정
         * 
         * @param datasetId 데이터셋 ID
         * @param tags      수정할 태그 목록
         * @return 수정된 데이터셋 정보
         */
        @PutMapping("/datasets/{datasetId}/tags")
        @Operation(summary = "데이터셋 태그 수정", description = "지정된 ID의 데이터셋 태그를 수정합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "데이터셋 태그 수정 성공", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DataCtlgDataSetUpdateRes.class))),
                        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
                        @ApiResponse(responseCode = "404", description = "데이터셋을 찾을 수 없음"),
                        @ApiResponse(responseCode = "401", description = "인증 실패"),
                        @ApiResponse(responseCode = "403", description = "권한 없음"),
                        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        })
        public AxResponseEntity<DataCtlgDataSetUpdateRes> updateDatasetTags(
                        @Parameter(description = "데이터셋 ID", example = "e3b4e453-293e-4a4b-867a-cdd36e212180", required = true) @PathVariable(name = "datasetId") UUID datasetId,
                        @Parameter(description = "수정할 태그 목록", required = true) @RequestBody DataCtlgDataSetTagUpdateReq request) {

                log.info("데이터셋 태그 수정 API 호출 - datasetId: {}, request: {}", datasetId, request);
                // log.info(">>>>>>>>>>>Tags: {}", request.getTags());

                DataCtlgDataSetUpdateRes response = dataCtlgDataSetService.updateDatasetTags(datasetId,
                                request.getTags());

                log.info("데이터셋 태그 수정 API 완료 - datasetId: {}, updatedTags: {}", datasetId, response.getTags());
                return AxResponseEntity.ok(response, "데이터셋 태그 수정이 완료되었습니다");
                // return AxResponseEntity.ok(null, "데이터셋 태그 수정이 완료되었습니다");

        }

        /**
         * 데이터셋 태그 삭제
         * 
         * @param datasetId 데이터셋 ID
         * @param tags      삭제할 태그 목록
         * @return 수정된 데이터셋 정보
         */
        @DeleteMapping("/datasets/{datasetId}/tags")
        @Operation(summary = "데이터셋 태그 삭제", description = "지정된 ID의 데이터셋에서 태그를 삭제합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "데이터셋 태그 삭제 성공", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = DataCtlgDataSetUpdateRes.class))),
                        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
                        @ApiResponse(responseCode = "404", description = "데이터셋을 찾을 수 없음"),
                        @ApiResponse(responseCode = "401", description = "인증 실패"),
                        @ApiResponse(responseCode = "403", description = "권한 없음"),
                        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        })
        public AxResponseEntity<DataCtlgDataSetUpdateRes> deleteDatasetTags(
                        @Parameter(description = "데이터셋 ID", example = "e3b4e453-293e-4a4b-867a-cdd36e212180", required = true) @PathVariable(name = "datasetId") UUID datasetId,
                        @Parameter(description = "삭제할 태그 목록", required = true) @RequestBody DataCtlgDataSetTagUpdateReq request) {

                log.info("데이터셋 태그 삭제 API 호출 - datasetId: {}, request: {}", datasetId, request);

                log.info(">>>>>>>>>>>Delete Tags: {}", request.getTags());

                DataCtlgDataSetUpdateRes response = dataCtlgDataSetService.deleteDatasetTag(datasetId,
                                request.getTags());

                log.info("데이터셋 태그 삭제 API 완료 - datasetId: {}, remainingTags: {}", datasetId, response.getTags());
                return AxResponseEntity.ok(response, "데이터셋 태그 삭제가 완료되었습니다");
        }

        /**
         * 데이터소스 파일 업로드
         * 
         * @param datasetId 데이터셋 ID
         * @param files     업로드할 파일 목록
         * @return 업로드된 파일 정보
         */

        @PostMapping(value = "/datasources/upload/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @Operation(summary = "데이터소스 파일 업로드", description = "수신한 파일들을 외부 DataSources API로 전달합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "파일 업로드 성공"),
                        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                        @ApiResponse(responseCode = "401", description = "인증 실패"),
                        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        })
        public AxResponseEntity<DataCtlgUploadFilesRes> uploadDatasetFiles(
                        @Parameter(description = "업로드할 파일 배열", required = true) @RequestPart("files") List<MultipartFile> files) {
                if (files == null || files.isEmpty()) {
                        throw new IllegalArgumentException("files가 비어 있습니다.");
                }
                log.info("데이터소스 파일 업로드 요청 - count: {}, names: {}",
                                files.size(),
                                files.stream().map(MultipartFile::getOriginalFilename).collect(Collectors.toList()));

                DataCtlgUploadFilesRes res = dataCtlgDataSetService.uploadDatasetFile(files);
                return AxResponseEntity.created(res, "데이터소스 파일 업로드가 완료되었습니다");
        }

        /**
         * 데이터소스 파일 목록 조회
         *
         * @param datasourceId 데이터소스 ID
         * @param page         페이지 번호
         * @param size         페이지 크기
         * @return 데이터소스 파일 목록 응답
         */
        @GetMapping("/datasources/{datasourceId}/files")
        @Operation(summary = "데이터소스 파일 목록 조회", description = "지정된 데이터소스의 파일 목록을 페이징하여 조회합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "데이터소스 파일 목록 조회 성공", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PageResponse.class))),
                        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
                        @ApiResponse(responseCode = "404", description = "데이터소스를 찾을 수 없음"),
                        @ApiResponse(responseCode = "401", description = "인증 실패"),
                        @ApiResponse(responseCode = "403", description = "권한 없음"),
                        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        })
        public AxResponseEntity<PageResponse<DataCtlgDataSourceFileRes>> getDataSourceFiles(
                        @Parameter(description = "데이터소스 ID", example = "454c67ff-99f6-4295-8cfb-810be4345467", required = true) @PathVariable(name = "datasourceId") String datasourceId,
                        @RequestParam(value = "page", defaultValue = "1") @Parameter(description = "페이지 번호", example = "1") Integer page,
                        @RequestParam(value = "size", defaultValue = "20") @Parameter(description = "페이지 크기", example = "20") Integer size) {

                log.info(">>> 데이터소스 파일 목록 조회 API 호출 - datasourceId: {}, page: {}, size: {}",
                                datasourceId, page, size);

                PageResponse<DataCtlgDataSourceFileRes> response = dataCtlgDataSetService.getDataSourceFiles(
                                datasourceId, page, size);

                log.info(">>> 데이터소스 파일 목록 조회 API 완료 - datasourceId: {}, 총 파일 수: {}",
                                datasourceId, response.getContent().size());

                return AxResponseEntity.okPage(response, "데이터소스 파일 목록 조회가 완료되었습니다");
        }

        /**
         * 
         * 비정형 등록 (다운로드, s3업로드, es 메타 저장)
         * 데이터셋 소스 아카이브 다운로드 후 데이터 저장소에 등록 (s3업로드, es 메타 저장)
         * 
         * @param datasetId 데이터셋 ID
         * @param request   파일 처리 요청 DTO
         * @return 파일 리소스 또는 처리 결과
         */
        @PostMapping("/datasets/files/{dataId}/ozone-register")
        @Operation(summary = "데이터 저장소 등록을 위한 파일 처리", description = "데이터 저장소 등록을 위한 지정된 데이터셋 파일을 임시 파일로 다운로드하고 S3에 업로드하고 ES에 메타 정보를 저장하여 데이터 저장소에 등록합니다. 각 단계별 처리 결과를 포함하여 반환하며, 처리 완료 후 임시 파일은 자동으로 정리됩니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "UDP 등록 처리 완료 (각 단계별 성공/실패 여부 포함)"),
                        @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음"),
                        @ApiResponse(responseCode = "401", description = "인증 실패"),
                        @ApiResponse(responseCode = "403", description = "권한 없음"),
                        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        })
        public AxResponseEntity<DataCtlgDataSourceUdpRegisterRes> registerDataSetFileToOzone(
                        @Parameter(description = "데이터셋 ID", example = "file-123e4567-e89b-12d3-a456-426614174000", required = true) @PathVariable(name = "dataId") String dataId,
                        @Parameter(description = "커스텀 타입 여부", example = "true", required = true) @RequestParam(name = "isCustomType") boolean isCustomType,
                        @Parameter(description = "Ozone 등록 요청", required = true) @Valid @RequestBody DataCtlgDataSourceFileDownloadReq request) {

                log.info(">>> Ozone 등록을 위한 파일 처리 API 호출 - dataId: {}, request: {}",
                                dataId, request);

                DataCtlgDataSourceUdpRegisterRes response = dataCtlgDataSetService
                                .registerDataSetFileToOzone(dataId, isCustomType, request);

                // 응답에 따라 적절한 메시지 설정
                String message = response.getSuccess() ? "UDP 등록이 성공적으로 완료되었습니다" : "UDP 등록 처리 중 일부 단계에서 실패가 발생했습니다";

                log.info(">>> Ozone 등록 완료 - dataId: {}, success: {}", dataId, response.getSuccess());

                return AxResponseEntity.ok(response, message);
        }

        /**
         * 학습 데이터 생성 (커스텀이 아닌 경우)
         *
         * @param request 학습 데이터 생성 요청 (파일명 리스트 포함)
         * @return 학습 데이터 생성 결과
         */
        @PostMapping("/create-train-data/not-custom")
        @Operation(summary = "학습 데이터 생성 (커스텀이 아닌 경우)", description = "파일명 리스트를 받아서 복사 → 임시 버킷 생성 → 학습 데이터를 생성합니다. 각 단계별 처리 결과를 포함하여 반환합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "학습 데이터 생성 처리 완료 (각 단계별 성공/실패 여부 포함)"),
                        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
                        @ApiResponse(responseCode = "401", description = "인증 실패"),
                        @ApiResponse(responseCode = "403", description = "권한 없음"),
                        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        })
        public AxResponseEntity<DataCtlgTrainingDataCreateRes> createTrainingDataNotCustom(
                        @Parameter(description = "학습 데이터 생성 요청", required = true) @RequestBody DataCtlgTrainingDatasetCreateFromFilesReq request) {

                log.info(">>> 학습 데이터 생성 API 호출 (커스텀이 아닌 경우) - request: {}", request);

                DataCtlgTrainingDataCreateRes response = dataCtlgDataSetService.createTrainingDataNotCustom(request);

                // 응답에 따라 적절한 메시지 설정
                String message = response.getSuccess() ? "학습 데이터 생성이 성공적으로 완료되었습니다" : "학습 데이터 생성 중 일부 단계에서 실패가 발생했습니다";

                log.info(">>> 학습 데이터 생성 완료 (커스텀이 아닌 경우) - sourceBucket: {}, fileNames: {}, datasetName: {}, success: {}, trainingDataId: {}",
                                request.getSourceBucketName(), request.getFileNames(), request.getName(),
                                response.getSuccess(), response.getTrainingDataId());

                return AxResponseEntity.ok(response, message);
        }

        /**
         * 학습 데이터 생성 (커스텀인 경우)
         * 
         * @param file        업로드할 파일 (type이 'file'인 경우 필수)
         * @param projectId   프로젝트 ID
         * @param name        데이터셋 이름
         * @param type        데이터소스 타입 ('s3' 또는 'file')
         * @param datasetType 데이터셋 타입 (기본값: "custom")
         * @param fileName    파일명 (type이 's3'인 경우 필수)
         * @param createdBy   생성자
         * @param payload     페이로드
         * @param status      상태
         * @param tags        태그
         * @param updatedBy   수정자
         * @param description 설명
         * @return 데이터셋 업로드 결과
         */
        @PostMapping(value = "/create-train-data/custom", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @Operation(summary = "학습 데이터 생성 (커스텀인 경우)", description = "파일을 직접 업로드하여 학습 데이터를 생성합니다. SKT AI API 호출 결과를 포함하여 반환합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "학습 데이터 생성 처리 완료 (SKT AI API 호출 결과 포함)"),
                        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                        @ApiResponse(responseCode = "401", description = "인증 실패"),
                        @ApiResponse(responseCode = "422", description = "유효성 검증 오류"),
                        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
        })
        public com.skax.aiplatform.common.response.AxResponseEntity<DataCtlgCustomTrainingDataCreateRes> createTrainingDataCustom(
                        @Parameter(description = "업로드할 파일 (type이 'file'인 경우 필수)") @RequestPart(value = "file", required = false) MultipartFile file,
                        @Parameter(description = "프로젝트 ID") @RequestPart(value = "project_id", required = false) String projectId,
                        @Parameter(description = "데이터셋 이름", required = true) @RequestPart("name") String name,
                        @Parameter(description = "데이터소스 타입 ('s3' 또는 'file')", required = true) @RequestPart("type") String type,
                        @Parameter(description = "데이터셋 타입", required = false) @RequestPart(value = "dataset_type", required = false) String datasetType,
                        @Parameter(description = "파일명 (type이 's3'인 경우 필수, 예: test.zip)") @RequestPart(value = "file_name", required = false) String fileName,
                        @Parameter(description = "생성자") @RequestPart(value = "created_by", required = false) String createdBy,
                        @Parameter(description = "페이로드") @RequestPart(value = "payload", required = false) String payload,
                        @Parameter(description = "상태") @RequestPart(value = "status", required = false) String status,
                        @Parameter(description = "태그") @RequestPart(value = "tags", required = false) String tags,
                        @Parameter(description = "수정자") @RequestPart(value = "updated_by", required = false) String updatedBy,
                        @Parameter(description = "설명") @RequestPart(value = "description", required = false) String description) {

                DataCtlgCustomTrainingDataCreateRes response = dataCtlgDataSetService.createCustomTrainingDataset(
                                file, projectId, name, datasetType, type, fileName, createdBy, payload, status, tags,
                                updatedBy, description);

                return AxResponseEntity.ok(response, response.getMessage());
        }

        /**
         * 데이터소스 Policy 설정
         *
         * @param datasourceId 데이터소스 ID (필수)
         * @param memberId     사용자 ID (필수)
         * @param projectName  프로젝트명 (필수)
         * @return List<PolicyRequest> 설정된 Policy 목록
         */

        @PostMapping("/datasources/{datasource_id}/policy")
        @Operation(summary = "데이터소스 Policy 설정", description = "데이터소스의 Policy를 설정합니다.")
        @ApiResponses({ @ApiResponse(responseCode = "200", description = "데이터소스 Policy 설정 성공"),
                        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                        @ApiResponse(responseCode = "500", description = "서버 오류") })
        public AxResponseEntity<List<PolicyRequest>> setDataSourcePolicy(
                        @PathVariable(value = "datasource_id", required = true) @Parameter(description = "데이터소스 ID", required = true, example = "f3bab54d-f683-4775-b570-81c94e5bdf0f") String datasourceId,
                        @RequestParam(value = "member_id", required = true) @Parameter(description = "사용자 ID", required = true) String memberId,
                        @RequestParam(value = "project_name", required = true) @Parameter(description = "프로젝트명", required = true) String projectName) {
                log.info("데이터소스 Policy 설정 요청 - datasourceId: {}, memberId: {}, projectName: {}", datasourceId, memberId,
                                projectName);
                List<PolicyRequest> policy = dataCtlgDataSetService.setDataSourcePolicy(datasourceId, memberId,
                                projectName);
                return AxResponseEntity.ok(policy, "데이터소스 Policy가 성공적으로 설정되었습니다.");
        }

        /**
         * 학습 데이터셋 Policy 설정
         *
         * @param datasetId   학습 데이터셋 ID (필수)
         * @param memberId    사용자 ID (필수)
         * @param projectName 프로젝트명 (필수)
         * @return List<PolicyRequest> 설정된 Policy 목록
         */

        @PostMapping("/datasets/{dataset_id}/policy")
        @Operation(summary = "데이터셋 Policy 설정", description = "데이터셋 Policy를 설정합니다.")
        @ApiResponses({ @ApiResponse(responseCode = "200", description = "데이터셋 Policy 설정 성공"),
                        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                        @ApiResponse(responseCode = "500", description = "서버 오류") })
        public AxResponseEntity<List<PolicyRequest>> setDatasetPolicy(
                        @PathVariable(value = "dataset_id", required = true) @Parameter(description = "데이터셋 ID", required = true, example = "f3bab54d-f683-4775-b570-81c94e5bdf0f") String datasetId,
                        @RequestParam(value = "member_id", required = true) @Parameter(description = "사용자 ID", required = true) String memberId,
                        @RequestParam(value = "project_name", required = true) @Parameter(description = "프로젝트명", required = true) String projectName) {
                log.info("데이터셋 Policy 설정 요청 - datasetId: {}, memberId: {}, projectName: {}", datasetId, memberId,
                                projectName);
                List<PolicyRequest> policy = dataCtlgDataSetService.setDatasetPolicy(datasetId, memberId,
                                projectName);
                return AxResponseEntity.ok(policy, "데이터셋 Policy가 성공적으로 설정되었습니다.");
        }

}
