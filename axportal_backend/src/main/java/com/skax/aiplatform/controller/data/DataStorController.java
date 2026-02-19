package com.skax.aiplatform.controller.data;

import com.skax.aiplatform.dto.data.request.DataStorDatasetSearchReq;
import com.skax.aiplatform.dto.data.request.DataStorTrainEvalSearchReq;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.data.request.DataStorDocumentSearchReq;
import com.skax.aiplatform.dto.data.response.DataStorDatasetRes;
import com.skax.aiplatform.dto.data.response.DataStorDocumentRes;
import com.skax.aiplatform.client.udp.dataset.dto.response.UdpEsDatasetAggregationResponse;
import com.skax.aiplatform.dto.data.response.DataStorTrainEvalRes;
import com.skax.aiplatform.service.data.DataStorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 데이터 -> 데이터 저장소 컨트롤러
 *
 * @author 장지원
 * @version 1.0.0
 * @since 2025-10-18
 */
@Slf4j
@RestController
@RequestMapping("/data-stor/dataset")
@RequiredArgsConstructor
@Validated
@Tag(name = "Data Storage MD Package Management", description = "데이터 저장소 MD 패키지 관리 API")
public class DataStorController {

    private final DataStorService dataStorService;

    /**
     * 데이터셋(dataset) 검색 API
     *
     * @param searchReq 검색 조건
     * @return 데이터셋 검색 결과 (페이지네이션 포함)
     */
    @GetMapping()
    @Operation(
            summary = "Dataset 검색 API",
            description = "데이터셋을 검색하여 페이징 결과를 반환합니다. API Key는 YAML 설정에서 자동으로 로드됩니다.",
            parameters = {
                    @Parameter(name = "datasetCd", description = "데이터셋코드", example = "rgl"),
                    @Parameter(name = "originSystemCd", description = "원천시스템코드 (value1,value2,value3 형식, 공백 없이)", example = "sb,crm,mis"),
                    @Parameter(name = "countPerPage", description = "페이지당 표시수", example = "20"),
                    @Parameter(name = "page", description = "페이지", example = "1")
            }
    )
    @ApiResponse(responseCode = "200", description = "데이터셋 검색 성공")
    public AxResponseEntity<PageResponse<DataStorDatasetRes>> getDatasets(DataStorDatasetSearchReq searchReq) {
        log.info(">> 데이터 저장소 컨트롤러 - Dataset 검색 요청 << 요청 정보 {}", searchReq);
        log.info(">> 컨트롤러 파라미터 상세 - searchWord: {}, originSystemCd: {}, page: {}, countPerPage: {}", 
                searchReq.getSearchWord(), searchReq.getOriginSystemCd(), searchReq.getPage(), searchReq.getCountPerPage());
        
        PageResponse<DataStorDatasetRes> response = dataStorService.getDatasets(searchReq);
        
        return AxResponseEntity.okPage(response, "데이터셋을 성공적으로 조회했습니다.");
    }

    /**
     * 도큐먼트(document) 검색 API
     *
     * @param searchReq 문서 검색 조건
     * @return 문서 검색 결과 (페이지네이션 포함)
     */
    @GetMapping("/documents")
    @Operation(
            summary = "문서 검색 API",
            description = "데이터셋 내 문서를 검색하여 페이징 결과를 반환합니다.",
            parameters = {
                    @Parameter(name = "datasetCd", description = "데이터셋코드 (필수)", example = "rgl", required = true),
                    @Parameter(name = "searchWord", description = "검색어", example = "규정"),
                    @Parameter(name = "countPerPage", description = "페이지당표시수", example = "20"),
                    @Parameter(name = "page", description = "페이지", example = "1")
            }
    )
    @ApiResponse(responseCode = "200", description = "도큐먼트 검색 성공")
    public AxResponseEntity<PageResponse<DataStorDocumentRes>> getDatasetsDocuments(DataStorDocumentSearchReq searchReq) {
        log.info(">>> 데이터 저장소 컨트롤러 - Documenet 검색 요청 << 요청 정보 {}", searchReq);

        PageResponse<DataStorDocumentRes> response = dataStorService.getDatasetsDocuments(searchReq);

        return AxResponseEntity.okPage(response, "문서 검색을 성공적으로 완료했습니다.");
    }

    /**
     * 데이터셋 검색 API (학습/평가 통합)
     *
     * @param searchReq 데이터셋 검색 조건
     * @return 데이터셋 검색 결과 (페이지네이션 포함)
     */
    @GetMapping("/train-eval")
    @Operation(
            summary = "학습/평가 데이터셋 검색 API",
            description = "학습데이터와 평가데이터를 통합하여 검색합니다. CAT01으로 '학습' 또는 '평가'를 구분하고, CAT02로 세부 카테고리를 필터링할 수 있습니다.",
            parameters = {
                    @Parameter(name = "page", description = "페이지", example = "1"),
                    @Parameter(name = "countPerPage", description = "페이지당 표시수", example = "20"),
                    @Parameter(name = "cat01", description = "카테고리1 (학습/평가)", example = "학습"),
                    @Parameter(name = "cat02", description = "카테고리2 - 학습: SUPERVISED,UNSUPERVISED,DPO,CUSTOM / 평가: QUERY_SET,RESPONSE_SET,HUMAN_EVALUATION_RESULT_MANUAL,HUMAN_EVALUATION_RESULT_INTERACTIVE", example = "SUPERVISED"),
                    @Parameter(name = "title", description = "제목 검색어", example = "데이터 제목")
            }
    )
    @ApiResponse(responseCode = "200", description = "데이터셋 검색 성공")
    public AxResponseEntity<PageResponse<DataStorTrainEvalRes>> getTrainEvalDatasets(DataStorTrainEvalSearchReq searchReq) {
        log.info(">>> 데이터 저장소 컨트롤러 - 학습/평가 검색 요청 << 요청 정보 {}", searchReq);
        
        PageResponse<DataStorTrainEvalRes> response = dataStorService.getTrainEvalData(searchReq);
        
        return AxResponseEntity.okPage(response, "데이터셋 검색을 성공적으로 완료했습니다.");
    }

    /**
     * 원천 시스템 목록 조회 API
     *
     * @return 원천 시스템 목록 (에러 시 빈 리스트 반환)
     */
    @GetMapping("/origin-systems")
    @Operation(
            summary = "원천 시스템 목록 조회 API",
            description = "UDP ES를 통해 데이터셋의 원천 시스템 목록을 조회합니다. 에러 발생 시 빈 리스트를 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "원천 시스템 조회 성공")
    public AxResponseEntity<UdpEsDatasetAggregationResponse> getOriginSystems() {
        UdpEsDatasetAggregationResponse response = dataStorService.getOriginSystems();
        return AxResponseEntity.ok(response, "원천 시스템 목록을 성공적으로 조회했습니다.");
    }
}