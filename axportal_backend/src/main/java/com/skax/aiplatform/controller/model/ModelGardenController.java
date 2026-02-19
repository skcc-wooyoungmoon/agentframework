package com.skax.aiplatform.controller.model;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.AxResponseEntity;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.model.common.ModelGardenInfo;
import com.skax.aiplatform.dto.model.request.CreateModelGardenReq;
import com.skax.aiplatform.dto.model.request.FileImportCompleteReq;
import com.skax.aiplatform.dto.model.request.GetAvailableModelReq;
import com.skax.aiplatform.dto.model.request.GetModelGardenReq;
import com.skax.aiplatform.dto.model.request.PostInProcessStatusReq;
import com.skax.aiplatform.dto.model.request.UpdateModelGardenReq;
import com.skax.aiplatform.dto.model.response.GetAvailableModelRes;
import com.skax.aiplatform.dto.model.response.GetVaccineCheckResultRes;
import com.skax.aiplatform.service.model.ModelGardenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/modelGarden")
@RequiredArgsConstructor
@Tag(name = "모델 가든", description = "모델 가든 API")
public class ModelGardenController {

        private final ModelGardenService modelGardenService;

        @GetMapping
        @Operation(summary = "모델 가든 등록 상세 정보 목록 조회", description = "모델 가든 등록 상세 정보 목록을 조회합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "모델 가든 등록 상세 정보 목록 조회 성공"),
        })
        public AxResponseEntity<PageResponse<ModelGardenInfo>> getModelGardens(GetModelGardenReq modelCtlgReq) {
                log.info("모델 가든 목록 조회 요청: {}", modelCtlgReq);

                PageResponse<ModelGardenInfo> response = modelGardenService.getModelGardens(modelCtlgReq);
                log.info("모델 가든 목록 조회 성공: {}", response);
                return AxResponseEntity.okPage(response, "모델 가든 목록 조회 성공");
        }

        @PostMapping
        @Operation(summary = "모델 가든 등록 상세 정보 생성", description = "모델 가든 등록 상세 정보를 생성합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "모델 가든 등록 상세 정보 생성 성공"),
        })
        public AxResponseEntity<ModelGardenInfo> createModelGarden(@RequestBody CreateModelGardenReq request) {
                log.info("모델 가든 생성 요청: {}", request);
                ModelGardenInfo response = null;

                try {
                        response = modelGardenService.createModelGarden(request);
                        log.info("모델 가든 생성 성공: {}", response);

                        return AxResponseEntity.ok(response, "모델 가든 생성 성공");

                } catch (BusinessException e) {
                        if (e.getErrorCode() == ErrorCode.MODEL_GARDEN_DUPLICATE_NAME) {
                                log.info("중복 모델 가든 조회 실패: {}", e.getMessage());
                                // 중복 모델 가든 조회 실패
                                return AxResponseEntity.warning(null, ErrorCode.MODEL_GARDEN_DUPLICATE_NAME.getCode());
                        }
                        throw e;
                }
        }

        @GetMapping("/{id}")
        @Operation(summary = "모델 가든 등록 상세 정보 조회", description = "모델 가든 등록 상세 정보를 조회합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "모델 가든 등록 상세 정보 조회 성공"),
        })
        public AxResponseEntity<ModelGardenInfo> getModelGardenById(@PathVariable String id) {

                log.info("모델 가든 상세 조회 요청: {}", id);
                ModelGardenInfo response = modelGardenService.getModelGardenById(id);
                log.info("모델 가든 상세 조회 성공: {}", response);
                return AxResponseEntity.ok(response, "모델 가든 상세 조회 성공");
        }

        @GetMapping("/{id}/check-result")
        @Operation(summary = "모델 가든 백신검사 결과 조회", description = "모델 가든의 백신검사 결과를 조회합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "백신검사 결과 조회 성공"),
                        @ApiResponse(responseCode = "404", description = "백신검사 결과를 찾을 수 없음")
        })
        public AxResponseEntity<GetVaccineCheckResultRes> getModelGardenVaccineCheckResult(@PathVariable String id) {
                log.info("모델 가든 백신검사 결과 조회 요청: {}", id);
                GetVaccineCheckResultRes response = modelGardenService.getModelGardenVaccineCheckResult(id);
                log.info("모델 가든 백신검사 결과 조회 성공: {}", response);
                return AxResponseEntity.ok(response, "모델 가든 백신검사 결과 조회 성공");
        }

        @PutMapping("/{id}")
        @Operation(summary = "모델 가든 등록 상세 정보 수정", description = "모델 가든 등록 상세 정보를 수정합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "모델 가든 등록 상세 정보 수정 성공"),
        })
        public AxResponseEntity<ModelGardenInfo> updateModelGarden(@PathVariable String id,
                        @RequestBody UpdateModelGardenReq request) {
                log.info("모델 가든 수정 요청: {}", request);
                ModelGardenInfo response = modelGardenService.updateModelGarden(ModelGardenService.UPDATE_FIND_TYPE.ID, id, request);
                log.info("모델 가든 수정 성공: {}", response);
                return AxResponseEntity.ok(response, "모델 가든 수정 성공");
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "모델 가든 등록 상세 정보 삭제", description = "모델 가든 등록 상세 정보를 삭제합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "모델 가든 등록 상세 정보 삭제 성공"),
        })
        public AxResponseEntity<Void> deleteModelGarden(@PathVariable String id) {
                log.info("모델 가든 삭제 요청: {}", id);
                modelGardenService.deleteModelGarden(id);
                return AxResponseEntity.ok(null, "모델 가든 삭제 성공");
        }

        @PostMapping("/file-import-complete") // 1
        @Operation(summary = "모델 파일 반입 완료", description = "모델 파일 반입이 완료되었음을 처리합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "모델 파일 반입 완료 처리 성공"),
        })
        public AxResponseEntity<ModelGardenInfo> completeModelImport(@RequestBody PostInProcessStatusReq request) {
                log.info("모델 파일 반입 완료 요청: {}", request);
                ModelGardenInfo response = modelGardenService.completeModelImport(request);
                log.info("모델 파일 반입 완료 처리 성공: {}", response);
                return AxResponseEntity.ok(response, "모델 파일 반입 완료 처리 성공");
        }

        // complete -> import-complete 로 변경 (백신검사)
        @PostMapping(value = "/import-complete", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
        @Operation(summary = "모델 반입 완료", description = "모델 반입이 완료되었음을 처리합니다. (form-urlencoded 지원)")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "모델 반입 완료 처리 성공"),
        })
        public ResponseEntity<String> completeModelImportGeneral(
                        @ModelAttribute FileImportCompleteReq request) {
                log.info("모델 반입 완료 요청 (form-urlencoded): {}", request);
                
                String xmlResponse = """
                                <CommonDto>
                                    <error_code>0</error_code>
                                    <message>SUCCESS</message>
                                    <etcData>
                                        <msg_no>1</msg_no>
                                        <send_cnt>1</send_cnt>
                                    </etcData>
                                </CommonDto>""";
                try {
                        ModelGardenInfo response = modelGardenService.completeInternalNetworkImport(request);
                        log.info("모델 반입 완료 처리 성공: {}", response);
                } catch (BusinessException e) {
                        // 오류가 나도 응답 처리
                        return ResponseEntity.ok()
                                        .contentType(MediaType.APPLICATION_XML)
                                        .body(xmlResponse);
                }
                return ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_XML)
                                .body(xmlResponse);
        }

        @PostMapping("/vaccine-complete") // v
        @Operation(summary = "모델 백신검사 완료", description = "모델 백신검사가 완료되었음을 처리합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "모델 백신검사 완료 처리 성공"),
        }) 
        public AxResponseEntity<ModelGardenInfo> completeVaccineScan(@RequestBody PostInProcessStatusReq request) {
                log.info("모델 백신검사 완료 요청: {}", request);
                ModelGardenInfo response = modelGardenService.completeVaccineScan(request);
                log.info("모델 백신검사 완료 처리 성공: {}", response);
                return AxResponseEntity.ok(response, "모델 백신검사 완료 처리 성공");
        }

        @PostMapping("/vulnerability-complete") // v
        @Operation(summary = "모델 취약점점검 완료", description = "모델 취약점점검이 완료되었음을 처리합니다.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "모델 취약점점검 완료 처리 성공"),
        })
        public AxResponseEntity<ModelGardenInfo> completeVulnerabilityCheck(
                        @RequestBody PostInProcessStatusReq request) {
                log.info("모델 취약점점검 완료 요청: {}", request);
                ModelGardenInfo response = modelGardenService.completeVulnerabilityCheck(request);
                log.info("모델 취약점점검 완료 처리 성공: {}", response);
                return AxResponseEntity.ok(response, "모델 취약점점검 완료 처리 성공");
        }
        
        @GetMapping("/available")
        public AxResponseEntity<GetAvailableModelRes> getAvailableModel(GetAvailableModelReq request) {
                log.info("모델 가든 사용 가능 모델 조회 요청: {}", request);

                GetAvailableModelRes response = modelGardenService.getAvailableModel(request);

                log.info("모델 가든 사용 가능 모델 조회 성공: {}", response);
                return AxResponseEntity.ok(response, "모델 가든 사용 가능 모델 조회 성공");
        }

}
