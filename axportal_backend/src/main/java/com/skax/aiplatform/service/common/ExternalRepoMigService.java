package com.skax.aiplatform.service.common;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyItem;
import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.client.sktai.auth.service.SktaiAuthService;
import com.skax.aiplatform.client.sktai.externalKnowledge.service.SktaiExternalReposService;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.RepoExtImportRequest;
import com.skax.aiplatform.client.sktai.knowledge.dto.response.RepoImportResponse;
import com.skax.aiplatform.client.sktai.lineage.dto.ActionType;
import com.skax.aiplatform.client.sktai.lineage.dto.Direction;
import com.skax.aiplatform.client.sktai.lineage.dto.ObjectType;
import com.skax.aiplatform.client.sktai.lineage.dto.response.LineageRelationWithTypes;
import com.skax.aiplatform.client.sktai.lineage.service.SktaiLineageService;
import com.skax.aiplatform.client.sktai.model.dto.response.ModelEndpointRead;
import com.skax.aiplatform.client.sktai.model.dto.response.ModelEndpointsRead;
import com.skax.aiplatform.client.sktai.model.dto.response.ModelRead;
import com.skax.aiplatform.client.sktai.model.service.SktaiModelsService;
import com.skax.aiplatform.client.udp.dataiku.dto.request.DataikuExecutionRequest;
import com.skax.aiplatform.client.udp.dataiku.dto.response.DataikuExecutionResponse;
import com.skax.aiplatform.client.udp.dataiku.service.UdpDataikuService;
import com.skax.aiplatform.client.udp.dataset.dto.request.DatasetSearchRequest;
import com.skax.aiplatform.client.udp.dataset.dto.response.DatasetCardInfo;
import com.skax.aiplatform.client.udp.dataset.dto.response.DatasetSearchResponse;
import com.skax.aiplatform.client.udp.dataset.service.UdpDatasetService;
import com.skax.aiplatform.client.udp.elasticsearch.dto.response.IndexCreateResponse;
import com.skax.aiplatform.client.udp.elasticsearch.service.UdpElasticsearchService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.dto.data.request.DataCtlgExternalKnowledgeDeployImportReq;
import com.skax.aiplatform.dto.data.response.DataCtlgExternalKnowledgeDeployExportRes;
import com.skax.aiplatform.dto.data.response.DataCtlgExternalKnowledgeDeployImportRes;
import com.skax.aiplatform.dto.model.response.GetModelCtlgRes;
import com.skax.aiplatform.entity.GpoUsersMas;
import com.skax.aiplatform.entity.knowledge.GpoKwlgInfoMas;
import com.skax.aiplatform.entity.mapping.GpoAssetPrjMapMas;
import com.skax.aiplatform.mapper.model.ModelCtlgMapper;
import com.skax.aiplatform.repository.auth.GpoUsersMasRepository;
import com.skax.aiplatform.repository.knowledge.GpoChunkAlgoMasRepository;
import com.skax.aiplatform.repository.knowledge.GpoKwlgInfoMasRepository;
import com.skax.aiplatform.repository.mapping.GpoAssetPrjMapMasRepository;
import com.skax.aiplatform.service.admin.AdminAuthService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * External Repository 마이그레이션 서비스
 *
 * <p>External Repository 관련 Export, Import, JSON 파일 저장 기능을 제공합니다.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalRepoMigService {

    private final GpoKwlgInfoMasRepository gpoKwlgInfoMasRepository;
    private final GpoChunkAlgoMasRepository gpoChunkAlgoMasRepository;
    private final com.skax.aiplatform.repository.model.GpoModelEmbeddingMasRepository gpoModelEmbeddingMasRepository;
    private final SktaiExternalReposService sktaiExternalReposService;
    private final UdpDataikuService udpDataikuService;
    private final UdpDatasetService udpDatasetService;
    private final AdminAuthService adminAuthService;
    private final ObjectMapper objectMapper;
    private final UdpElasticsearchService udpElasticsearchService;


    private final SktaiModelsService sktaiModelService;
    private final ModelCtlgMapper modelCtlgMapper;
    private final SktaiLineageService sktaiLineageService;
    private final GpoAssetPrjMapMasRepository assetPrjMapMasRepository;
    private final SktaiAuthService sktaiAuthService;
    private final GpoUsersMasRepository gpoUsersMasRepository;


    /**
     * 1. Export 형태를 만드는 것
     *
     * <p>External Repository를 조회하고 Import 형식으로 변환합니다.</p>
     *
     * @param repoExtId External Repository ID
     * @return Import 형식의 JSON 문자열
     */
    public String exportToImportFormat(String repoExtId) {
        try {
            log.info("External Repository Export → Import 형식 변환 시작 - repoExtId: {}", repoExtId);

            //  기본지식인지 커스텀 지식인지 체크
            // 1. ex_kwlg_id로 DB에서 지식 정보 조회
            Optional<GpoKwlgInfoMas> knowledgeInfoOpt = gpoKwlgInfoMasRepository.findByExKwlgId(repoExtId);

            GpoKwlgInfoMas knowledgeInfo = null;
            DataCtlgExternalKnowledgeDeployExportRes.KnowledgeInfo knowledgeInfoDto = null;
            boolean isCustomKnowledge = false;

            if (knowledgeInfoOpt.isPresent()) {
                // DB에 있으면 기본지식
                knowledgeInfo = knowledgeInfoOpt.get();
                log.info("✅ 기본지식 확인 - kwlgId: {}, exKwlgId: {}, idxNm: {}",
                        knowledgeInfo.getKwlgId(), knowledgeInfo.getExKwlgId(), knowledgeInfo.getIdxNm());

                knowledgeInfoDto = DataCtlgExternalKnowledgeDeployExportRes.KnowledgeInfo.builder()
                        .kwlgId(knowledgeInfo.getKwlgId())
                        .kwlgNm(knowledgeInfo.getKwlgNm())
                        .exKwlgId(knowledgeInfo.getExKwlgId())
                        .chunkId(knowledgeInfo.getChunkId())
                        .prmtCtnt(knowledgeInfo.getPrmtCtnt())
                        .modelId(knowledgeInfo.getModelId())
                        .dataSetId(knowledgeInfo.getDataSetId())
                        .dataSetNm(knowledgeInfo.getDataSetNm())
                        .idxNm(knowledgeInfo.getIdxNm())
                        .consumerGrpNm(knowledgeInfo.getConsumerGrpNm())
//                        .fileLoadJinhgRt(knowledgeInfo.getFileLoadJinhgRt())
//                        .chunkJinhgRt(knowledgeInfo.getChunkJinhgRt())
//                        .dbLoadJinhgRt(knowledgeInfo.getDbLoadJinhgRt())
                        .dvlpSynchYn(knowledgeInfo.getDvlpSynchYn())
                        .unyungSynchYn(knowledgeInfo.getUnyungSynchYn())
//                        .kafkaCntrStatus(knowledgeInfo.getKafkaCntrStatus())
//                        .dataPipelineExeId(knowledgeInfo.getDataPipelineExeId())
//                        .dataPipelineLoadStatus(knowledgeInfo.getDataPipelineLoadStatus())
//                        .dataPipelineSynchStatus(knowledgeInfo.getDataPipelineSynchStatus())
//                        .idxMkSttAt(knowledgeInfo.getIdxMkSttAt())
//                        .idxMkEndAt(knowledgeInfo.getIdxMkEndAt())
                        .fstCreatedAt(knowledgeInfo.getFstCreatedAt())
                        .createdBy(knowledgeInfo.getCreatedBy())
                        .lstUpdatedAt(knowledgeInfo.getLstUpdatedAt())
                        .updatedBy(knowledgeInfo.getUpdatedBy())
                        .build();
            } else {
                // DB에 없으면 커스텀지식
                isCustomKnowledge = true;
                knowledgeInfoDto = null;
                log.info("✅ 커스텀지식 확인 - exKwlgId: {} (DB에 없음)", repoExtId);
            }


            // ADXP Map export
            Object responseObj = sktaiExternalReposService.getExternalRepo(repoExtId);
            if (responseObj == null) {
                throw new RuntimeException("External Repository를 찾을 수 없습니다: " + repoExtId);
            }
            // Object를 Map으로 변환
            @SuppressWarnings("unchecked")
            Map<String, Object> externalRepoMap = responseObj instanceof Map
                    ? (Map<String, Object>) responseObj
                    : objectMapper.convertValue(responseObj, Map.class);

            // Merge
            Map<String, Object> knowledgeExportMap = new HashMap<>();
            knowledgeExportMap.put("isCustomKnowledge", isCustomKnowledge);
            knowledgeExportMap.put("knowledgeInfoDto", knowledgeInfoDto);
            knowledgeExportMap.put("externalRepoMap", externalRepoMap);


            // Import 형식으로 변환
            String importJson = objectMapper.writeValueAsString(knowledgeExportMap);

            log.info("External Repository Export → Import 형식 변환 완료 - repoExtId: {}, jsonLength: {}", repoExtId, importJson.length());

            return importJson;

        } catch (JsonProcessingException e) {
            log.error("External Repository JSON 파싱 실패 - error: {}", e.getMessage(), e);
            throw new RuntimeException("External Repository Export → Import 형식 변환 실패: " + e.getMessage(), e);
        } catch (FeignException e) {
            log.error("External Repository API 호출 실패 - error: {}", e.getMessage(), e);
            throw new RuntimeException("External Repository Export → Import 형식 변환 실패: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("External Repository Export → Import 형식 변환 실패 - repoExtId: {}, error: {}", repoExtId, e.getMessage(), e);
            throw new RuntimeException("External Repository Export → Import 형식 변환 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 2. Export 형태를 Import 거래 날리는 것
     *
     * <p>Export 데이터를 Import 형식으로 변환한 후 Import API를 호출합니다.</p>
     *
     * @param repoExtId External Repository ID
     * @return Import 성공 여부 (true: 성공, false: 실패)
     */
    public boolean importFromExport(String repoExtId) {
        try {
            log.info("External Repository Export → Import 거래 시작 - repoExtId: {}", repoExtId);

            // 1. Export → Import 형식으로 변환
            String importJson = exportToImportFormat(repoExtId);

            // 2. JSON을 RepoExtImportRequest로 변환
            RepoExtImportRequest importRequest = objectMapper.readValue(importJson, RepoExtImportRequest.class);

            // 3. Import 거래 호출
            RepoImportResponse response = sktaiExternalReposService.importExternalRepo(importRequest);

            boolean success = response != null && response.getRepoId() != null;

            log.info("External Repository Export → Import 거래 완료 - repoExtId: {}, success: {}", repoExtId, success);

            return success;

        } catch (JsonProcessingException e) {
            log.error("External Repository JSON 파싱 실패 - error: {}", e.getMessage(), e);
            return false;
        } catch (FeignException e) {
            log.error("External Repository API 호출 실패 - error: {}", e.getMessage(), e);
            return false;
        } catch (RuntimeException e) {
            log.error("External Repository Export → Import 거래 실패 - repoExtId: {}, error: {}", repoExtId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * JSON 문자열로부터 Import 수행
     *
     * @param importJson Import 형식의 JSON 문자열
     * @param projectId  프로젝트 ID (선택사항, 사용하지 않을 수 있음)
     * @return Import 성공 여부
     */
    public boolean importFromJsonString(String importJson, Long projectId, Boolean isExist) {
        try {
            log.info("External Repository JSON 문자열에서 Import 시작");

            Map<String, Object> imports = objectMapper.readValue(importJson, Map.class);
            Map<String, Object> adxpImportRequest = (Map<String, Object>) imports.get("externalRepoMap");
            boolean isCustomknowledge = (Boolean) imports.getOrDefault("isCustomKnowledge", true);

            //  존재하면 update
            if (isExist) {
                return updateADXP(adxpImportRequest, projectId);
            }

            if (isCustomknowledge) {
                log.info("커스텀 지식 import 시작 - ADXP Import만 수행");
                return importCustomKnowledge(adxpImportRequest, projectId);
            } else {
                log.info("기본지식 Import 시작 - DB 저장 및 전체 프로세스 수행");
                Map<String, Object> knowledgeInfoDto = (Map<String, Object>) imports.get("knowledgeInfoDto");
                return importBasicKnowledge(knowledgeInfoDto, adxpImportRequest, projectId);
            }
        } catch (JsonProcessingException e) {
            log.error("External Repository JSON 파싱 실패 - error: {}", e.getMessage(), e);
            return false;
        } catch (FeignException e) {
            log.error("External Repository API 호출 실패 - error: {}", e.getMessage(), e);
            return false;
        } catch (RuntimeException e) {
            log.error("External Repository JSON 문자열에서 Import 실패 - error: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Embedding 모델명으로 dimension 조회
     *
     * @param embeddingModel Embedding 모델명 (model_nm)
     * @return dimension 값 (없으면 기본값 2048)
     */
    private Integer getDimensionFromEmbeddingModel(String embeddingModel) {
        if (embeddingModel == null || embeddingModel.isBlank()) {
            log.warn("⚠️ [Dimension 조회] embeddingModel이 null이거나 비어있음 - 기본값 2048 사용");
            return 2048;
        }

        try {
            // model_nm으로 조회
            com.skax.aiplatform.entity.model.GpoModelEmbeddingMas modelEmbedding = gpoModelEmbeddingMasRepository
                    .findByModelNm(embeddingModel)
                    .orElse(null);

            if (modelEmbedding == null) {
                log.warn("⚠️ [Dimension 조회] 모델 정보를 찾을 수 없음 - model_nm: {}, 기본값 2048 사용", embeddingModel);
                return 2048;
            }

            String prmtCtnt = modelEmbedding.getPrmtCtnt();
            if (prmtCtnt == null || prmtCtnt.isBlank()) {
                log.warn("⚠️ [Dimension 조회] prmt_ctnt가 null이거나 비어있음 - model_nm: {}, 기본값 2048 사용", embeddingModel);
                return 2048;
            }

            // JSON 파싱
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> jsonMap = objectMapper.readValue(prmtCtnt, Map.class);
                Object dimensionObj = jsonMap.get("dimension");

                if (dimensionObj == null) {
                    log.warn("⚠️ [Dimension 조회] dimension 필드가 없음 - model_nm: {}, 기본값 2048 사용", embeddingModel);
                    return 2048;
                }

                // Integer로 변환
                Integer dimension;
                try {
                    if (dimensionObj instanceof Integer) {
                        dimension = (Integer) dimensionObj;
                    } else if (dimensionObj instanceof Number) {
                        dimension = ((Number) dimensionObj).intValue();
                    } else {
                        dimension = Integer.parseInt(dimensionObj.toString());
                    }

                    log.info("✅ [Dimension 조회] 성공 - model_nm: {}, dimension: {}", embeddingModel, dimension);
                    return dimension;
                } catch (NumberFormatException e) {
                    log.error("❌ [Dimension 조회] 숫자 변환 실패 - model_nm: {}, dimension 값: {}, 오류: {}",
                            embeddingModel, dimensionObj, e.getMessage(), e);
                    return 2048;
                }

            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                log.error("❌ [Dimension 조회] JSON 파싱 실패 - model_nm: {}, prmt_ctnt: {}, 오류: {}",
                        embeddingModel, prmtCtnt, e.getMessage(), e);
                return 2048;
            }

        } catch (Exception e) {
            log.error("❌ [Dimension 조회] 예외 발생 - model_nm: {}, 오류: {}", embeddingModel, e.getMessage(), e);
            return 2048;
        }
    }

    private boolean importBasicKnowledge(Map<String, Object> knowledgeInfo, Map<String, Object> adxpImportRequest, Long projectId) {
        // DTO
        DataCtlgExternalKnowledgeDeployImportReq.KnowledgeInfo knowledgeInfoDto = objectMapper.convertValue(knowledgeInfo, DataCtlgExternalKnowledgeDeployImportReq.KnowledgeInfo.class);

        log.info("📚 기본지식 Import 처리 시작 - kwlgId: {}", knowledgeInfoDto.getKwlgId());

        // 1. 인덱스명으로 Elasticsearch 인덱스 생성 (기존 등록 로직과 동일)
        String indexName = knowledgeInfoDto.getIdxNm();
        if (indexName != null && !indexName.isEmpty()) {
            log.info("🔍 Elasticsearch Index 생성 시작 - indexName: {}", indexName);

            // dimension 조회 (modelId로 model_nm 조회)
            Integer dimension = getDimensionFromEmbeddingModel(knowledgeInfoDto.getModelId());
            log.info("📐 [인덱스 생성] dimension: {}", dimension);

            try {
                // Index 존재 여부 확인
                Boolean indexExists = udpElasticsearchService.indexExists(indexName);

                if (indexExists) {
                    log.info("⚠️ Elasticsearch Index가 이미 존재함 - indexName: {}", indexName);
                } else {
                    // 지식용 Index 생성 (dimension 파라미터 사용)
                    IndexCreateResponse indexResponse = udpElasticsearchService.createIndexForKnowledge(indexName, dimension);

                    if (indexResponse.getAcknowledged() != null && indexResponse.getAcknowledged()) {
                        log.info("✅ Elasticsearch Index 생성 성공 - indexName: {}, acknowledged: {}",
                                indexName, indexResponse.getAcknowledged());
                    } else {
                        log.warn("⚠️ Elasticsearch Index 생성 응답 확인 필요 - indexName: {}, acknowledged: {}",
                                indexName, indexResponse.getAcknowledged());
                    }
                }
            } catch (BusinessException esEx) {
                log.error("❌ Elasticsearch Index 생성 실패 (BusinessException) - indexName: {}, 오류: {}",
                        indexName, esEx.getMessage(), esEx);
                // Index 생성 실패해도 지식 Import는 성공으로 처리 (수동으로 Index 생성 가능)
                log.warn("⚠️ Index 생성 실패했지만 지식 Import는 계속 진행합니다 - 수동으로 Index를 생성해주세요.");
            } catch (FeignException esEx) {
                log.error("❌ Elasticsearch Index 생성 실패 (FeignException) - indexName: {}, 상태코드: {}, 오류: {}",
                        indexName, esEx.status(), esEx.getMessage(), esEx);
                // Index 생성 실패해도 지식 Import는 성공으로 처리 (수동으로 Index 생성 가능)
                log.warn("⚠️ Index 생성 실패했지만 지식 Import는 계속 진행합니다 - 수동으로 Index를 생성해주세요.");
            } catch (RuntimeException esEx) {
                log.error("❌ Elasticsearch Index 생성 실패 (RuntimeException) - indexName: {}, 오류: {}",
                        indexName, esEx.getMessage(), esEx);
                // Index 생성 실패해도 지식 Import는 성공으로 처리 (수동으로 Index 생성 가능)
                log.warn("⚠️ Index 생성 실패했지만 지식 Import는 계속 진행합니다 - 수동으로 Index를 생성해주세요.");
            } catch (Exception esEx) {
                log.error("❌ Elasticsearch Index 생성 실패 (Exception) - indexName: {}, 오류: {}",
                        indexName, esEx.getMessage(), esEx);
                // Index 생성 실패해도 지식 Import는 성공으로 처리 (수동으로 Index 생성 가능)
                log.warn("⚠️ Index 생성 실패했지만 지식 Import는 계속 진행합니다 - 수동으로 Index를 생성해주세요.");
            }
        }

        // 2. ADXP JSON을 request body로 POST /api/v1/knowledge/repos/external/import 호출
        importADXP(adxpImportRequest, projectId);

        // 3. DB에 정확히 같은 ROW를 INSERT
        log.info("💾 DB 저장 시작 - kwlgId: {}", knowledgeInfoDto.getKwlgId());

        GpoKwlgInfoMas newKnowledgeInfo = GpoKwlgInfoMas.builder()
                .kwlgId(knowledgeInfoDto.getKwlgId()) // 새로운 UUID로 생성해야 할 수도 있음
                .kwlgNm(knowledgeInfoDto.getKwlgNm())
                .exKwlgId(knowledgeInfoDto.getExKwlgId()) // ADXP Import 후에 설정됨
                .chunkId(knowledgeInfoDto.getChunkId())
                .prmtCtnt(knowledgeInfoDto.getPrmtCtnt())
                .modelId(knowledgeInfoDto.getModelId())
                .dataSetId(knowledgeInfoDto.getDataSetId())
                .dataSetNm(knowledgeInfoDto.getDataSetNm())
                .idxNm(knowledgeInfoDto.getIdxNm())
                .consumerGrpNm(knowledgeInfoDto.getConsumerGrpNm())
                .fileLoadJinhgRt(knowledgeInfoDto.getFileLoadJinhgRt())
                .chunkJinhgRt(knowledgeInfoDto.getChunkJinhgRt())
                .dbLoadJinhgRt(knowledgeInfoDto.getDbLoadJinhgRt())
                .dvlpSynchYn(knowledgeInfoDto.getDvlpSynchYn())
                .unyungSynchYn(knowledgeInfoDto.getUnyungSynchYn())
                .kafkaCntrStatus(knowledgeInfoDto.getKafkaCntrStatus())
                .dataPipelineExeId(knowledgeInfoDto.getDataPipelineExeId())
                .dataPipelineLoadStatus(knowledgeInfoDto.getDataPipelineLoadStatus())
                .dataPipelineSynchStatus(knowledgeInfoDto.getDataPipelineSynchStatus())
                .idxMkSttAt(knowledgeInfoDto.getIdxMkSttAt())
                .idxMkEndAt(knowledgeInfoDto.getIdxMkEndAt())
                .fstCreatedAt(knowledgeInfoDto.getFstCreatedAt())
                .createdBy(knowledgeInfoDto.getCreatedBy())
                .lstUpdatedAt(knowledgeInfoDto.getLstUpdatedAt())
                .updatedBy(knowledgeInfoDto.getUpdatedBy())
                .build();

        GpoKwlgInfoMas savedKnowledge = gpoKwlgInfoMasRepository.save(newKnowledgeInfo);
        log.info("✅ DB 저장 완료 - kwlgId: {}", savedKnowledge.getKwlgId());

        // 4. 데이터 이쿠 실행
        log.info("🚀 데이터 이쿠 실행 시작");
        String userUuid = adxpImportRequest.getOrDefault("created_by", "admin").toString();
        String knowledgeName = adxpImportRequest.getOrDefault("name", "이행지식").toString();
        executeDataikuForImport(savedKnowledge, userUuid, knowledgeName);
        log.info("✅ 데이터 이쿠 실행 완료");

        // 5. 응답 생성
        DataCtlgExternalKnowledgeDeployImportRes response = DataCtlgExternalKnowledgeDeployImportRes.builder()
                .kwlgId(savedKnowledge.getKwlgId())
                .exKwlgId(knowledgeInfoDto.getExKwlgId())
                .idxNm(savedKnowledge.getIdxNm())
                .build();

        log.info("✅ 기본지식 Import 완료 - kwlgId: {}, exKwlgId: {}",
                response.getKwlgId(), response.getExKwlgId());

        return response.getExKwlgId() != null;
    }

    private boolean importCustomKnowledge(Map<String, Object> adxpImportRequest, Long projectId) {
        return importADXP(adxpImportRequest, projectId);
    }

    private boolean importADXP(Map<String, Object> adxpImportRequest, Long projectId) {
        RepoExtImportRequest request = objectMapper.convertValue(adxpImportRequest, RepoExtImportRequest.class);
        RepoImportResponse response = sktaiExternalReposService.importExternalRepo(request);

        // ADXP 권한 부여

        // projectId가 있을 경우만, 권한 설정
        if (projectId != null) {
            log.info("External Repository JSON 문자열에서 Import - 권한 설정 시작 - projectId: {}", projectId);
            adminAuthService.setResourcePolicyByProjectSequence("/api/v1/knowledge/repos/" + request.getId(), projectId);
            log.info("External Repository JSON 문자열에서 Import - 권한 설정 완료");
        }

        return response != null && response.getRepoId() != null;
    }

    private boolean updateADXP(Map<String, Object> adxpImportRequest, Long projectId) {

        Object response = sktaiExternalReposService.updateExternalRepo(
                (String) adxpImportRequest.get("id"),
                (String) adxpImportRequest.get("name"),
                (String) adxpImportRequest.get("description"),
                (String) adxpImportRequest.get("script"),
                (String) adxpImportRequest.get("index_name")
        );

        // projectId가 있을 경우만, 권한 설정
        if (projectId != null) {
            log.info("External Repository JSON 문자열에서 Import - 권한 설정 시작 - projectId: {}", projectId);
            adminAuthService.setResourcePolicyByProjectSequence("/api/v1/knowledge/repos/" + adxpImportRequest.get("id"), projectId);
            log.info("External Repository JSON 문자열에서 Import - 권한 설정 완료");
        }

        return response != null;
    }


    /**
     * 3. Export 형태를 JSON 파일로 만드는 것
     *
     * <p>Export 데이터를 Import 형식으로 변환한 후 JSON 파일로 저장합니다.</p>
     *
     * @param repoExtId  External Repository ID
     * @param saveToFile 파일 저장 여부 (true: 파일 저장, false: JSON만 반환)
     * @return 파일 저장 시 저장된 파일 경로, 저장하지 않을 경우 null
     */
    public String exportToJsonFile(String repoExtId, boolean saveToFile) {
        try {
            log.info("External Repository Export → JSON 파일 저장 시작 - repoExtId: {}, saveToFile: {}", repoExtId, saveToFile);

            // 1. Export → Import 형식으로 변환
            String importJson = exportToImportFormat(repoExtId);

            // 2. JSON 파일로 저장 (조건 처리)
            if (!saveToFile) {
                log.info("파일 저장 옵션이 false이므로 파일 저장을 건너뜁니다.");
                return null;
            }

            // 저장 디렉토리 생성
            String baseDir = "data/exports";
            Path exportDir = Paths.get(baseDir);
            if (!Files.exists(exportDir)) {
                Files.createDirectories(exportDir);
            }

            // 파일명 생성: EXTERNAL_REPO_{id}_{timestamp}.json
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = String.format("EXTERNAL_REPO_%s_%s.json", repoExtId, timestamp);
            Path filePath = exportDir.resolve(fileName);

            // JSON 파일 저장
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                writer.write(importJson);
                writer.flush();
            }

            String absolutePath = filePath.toAbsolutePath().toString();
            log.info("External Repository Export → JSON 파일 저장 완료 - 경로: {}", absolutePath);

            return absolutePath;

        } catch (IOException e) {
            log.error("External Repository Export → JSON 파일 저장 실패 (IOException) - repoExtId: {}, error: {}", repoExtId, e.getMessage(), e);
            throw new RuntimeException("External Repository Export → JSON 파일 저장 실패: " + e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("External Repository Export → JSON 파일 저장 실패 - repoExtId: {}, error: {}", repoExtId, e.getMessage(), e);
            throw new RuntimeException("External Repository Export → JSON 파일 저장 실패: " + e.getMessage(), e);
        }
    }

    /**
     * External Repository Map을 RepoExtImportRequest로 변환
     */
    private RepoExtImportRequest convertExternalRepoToImportRequest(Map<String, Object> externalRepoMap, String repoExtId) {
        try {
            if (externalRepoMap == null || externalRepoMap.isEmpty()) {
                throw new IllegalArgumentException("External Repository 데이터가 null이거나 비어있습니다.");
            }

            // 메타데이터 필드 제거
            List<String> removedFields = new ArrayList<>();
            if (externalRepoMap.containsKey("created_by")) {
                externalRepoMap.remove("created_by");
                removedFields.add("created_by");
            }
            if (externalRepoMap.containsKey("created_at")) {
                externalRepoMap.remove("created_at");
                removedFields.add("created_at");
            }
            if (externalRepoMap.containsKey("updated_by")) {
                externalRepoMap.remove("updated_by");
                removedFields.add("updated_by");
            }
            if (externalRepoMap.containsKey("updated_at")) {
                externalRepoMap.remove("updated_at");
                removedFields.add("updated_at");
            }

            // Import Request에 필요한 필드 추출
            RepoExtImportRequest.RepoExtImportRequestBuilder builder = RepoExtImportRequest.builder();

            builder.id(repoExtId);
            builder.name((String) externalRepoMap.get("name"));
            builder.description((String) externalRepoMap.get("description"));

            // embedding_model_name 또는 embedding_model_id 확인
            String embeddingModelName = (String) externalRepoMap.get("embedding_model_name");
            if (embeddingModelName == null || embeddingModelName.isEmpty()) {
                embeddingModelName = (String) externalRepoMap.get("embedding_model_id");
            }
            builder.embeddingModelName(embeddingModelName);

            builder.vectorDbId((String) externalRepoMap.get("vector_db_id"));
            builder.indexName((String) externalRepoMap.get("index_name"));
            builder.script((String) externalRepoMap.get("script"));
            builder.scriptHash((String) externalRepoMap.get("script_hash"));

            return builder.build();

        } catch (RuntimeException e) {
            log.error("External Repository를 Import 형식으로 변환 실패 - error: {}", e.getMessage(), e);
            throw new RuntimeException("External Repository를 Import 형식으로 변환 실패: " + e.getMessage(), e);
        }
    }

    /**
     * KNOWLEDGE 타입 필드 추출
     *
     * @param jsonNode       JSON 노드
     * @param id             파일 ID
     * @param fields         추출할 필드 목록
     * @param getValueFromDb DB에서 값 조회하는 함수
     * @return 추출된 필드 Map
     */
    public Map<String, Object> extractFields(JsonNode jsonNode, String id, List<String> fields, Function<String, String> getValueFromDb) {
        Map<String, Object> result = new HashMap<>();

        for (String field : fields) {
            if (!jsonNode.has(field)) {
                continue;
            }

            String fileValue = jsonNode.get(field).asText();
            String dbValue = getValueFromDb.apply(field);

            Map<String, String> fieldMap = new HashMap<>();
            fieldMap.put("dev", fileValue != null ? fileValue : "");
            fieldMap.put("prod", dbValue != null ? dbValue : "");
            result.put(field, fieldMap);
        }

        return result;
    }

    /**
     * 데이터 이쿠 실행 (Import용)
     */
    private void executeDataikuForImport(GpoKwlgInfoMas knowledgeInfo, String userUuid, String knowledgeName) {
        try {

            Optional<GpoUsersMas> gpoUsersMas = gpoUsersMasRepository.findByUuid(userUuid);
            String userMemberId = (gpoUsersMas.isPresent()) ? gpoUsersMas.get().getMemberId() : userUuid;

            // DB 정보를 가져와서 비정형 API 호출

            if (knowledgeInfo.getDataSetId() == null || knowledgeInfo.getDataSetId().isEmpty()) {
                log.warn("⚠️ 데이터셋 ID가 없어 데이터 이쿠 실행을 건너뜁니다 - kwlgId: {}", knowledgeInfo.getKwlgId());
                return;
            }

            log.info("📋 데이터셋 카드 목록 조회 시작");

            // 데이터셋 카드 목록 조회 API 호출 (전체 조회)
            DatasetSearchRequest datasetRequest = DatasetSearchRequest.builder()
                    .datasetCardType("DATS") // 데이터셋 타입
                    .countPerPage(1000L) // 충분히 큰 값으로 전체 조회
                    .page(1L)
                    .build();

            DatasetSearchResponse datasetResponse = udpDatasetService.searchDataset(datasetRequest);

            if (datasetResponse == null || datasetResponse.getResultLists() == null ||
                    datasetResponse.getResultLists().isEmpty()) {
                log.warn("⚠️ 데이터셋 카드 목록을 찾을 수 없습니다");
                return;
            }

            log.info("✅ 데이터셋 카드 목록 조회 완료 - 데이터셋 카드 개수: {}", datasetResponse.getResultLists().size());

            // TODO: 필터 처리 필요 - datasetResponse.getResultLists()를 기반으로 필터링 후 file_list 생성
            // 필터 처리 후 file_list를 생성해야 함

            String datasetId = knowledgeInfo.getDataSetId();
            List<String> datasetIds = List.of(datasetId.split(","));

            // datasetCardList에서 knowledgeInfo.getDataSetId()를 기반으로 필터링하여 file_list 생성
            @SuppressWarnings("unused")
            List<DatasetCardInfo> datasetCardList = datasetResponse.getResultLists(); // 필터 처리 대상
            List<Map<String, Object>> fileList = new ArrayList<>(); // 필터 처리 후 채워질 예정

            fileList = datasetCardList.stream()
                    .filter(datasetCardInfo -> datasetIds.contains(String.format(
                            "%s|%s",
                            datasetCardInfo.getOriginSystemCd(),
                            datasetCardInfo.getDatasetCd())))
                    .map(card -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("datasetCardId", card.getDatasetCardId());
                        map.put("datasetCardName", card.getDatasetCardName());
                        map.put("datasetCd", card.getDatasetCd());
                        map.put("originSystemCd", card.getOriginSystemCd());
                        map.put("originSystemName", card.getOriginSystemName());
                        return map;
                    }).collect(Collectors.toList());

            // Dataiku 실행 파라미터 구성
            Map<String, Object> dataikuParams = buildDataikuParams(knowledgeInfo, fileList, userMemberId, knowledgeName);

            // Dataiku 실행
            DataikuExecutionRequest dataikuRequest = new DataikuExecutionRequest(dataikuParams);
            DataikuExecutionResponse dataikuResponse = udpDataikuService.executeDataiku(dataikuRequest);

            log.info("✅ 데이터 이쿠 실행 완료 - runId: {}",
                    dataikuResponse != null && dataikuResponse.getBody() != null
                            ? dataikuResponse.getBody().get("runId")
                            : "N/A");

            // DB 업데이트
            knowledgeInfo.setDataPipelineLoadStatus("running");
            knowledgeInfo.setDbLoadJinhgRt(java.math.BigDecimal.ZERO);
            knowledgeInfo.setIdxMkSttAt(java.time.LocalDateTime.now());
            if (dataikuResponse != null && dataikuResponse.getBody() != null) {
                Object runId = dataikuResponse.getBody().get("runId");
                if (runId != null) {
                    knowledgeInfo.setDataPipelineExeId(runId.toString());
                }
            }
            gpoKwlgInfoMasRepository.save(knowledgeInfo);

        } catch (BusinessException e) {
            log.error("❌ 데이터 이쿠 실행 실패 - kwlgId: {}, 오류: {}",
                    knowledgeInfo.getKwlgId(), e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("❌ 데이터 이쿠 실행 실패 - kwlgId: {}, 오류: {}",
                    knowledgeInfo.getKwlgId(), e.getMessage(), e);
        } catch (Exception e) {
            log.error("❌ 데이터 이쿠 실행 실패 - kwlgId: {}, 오류: {}",
                    knowledgeInfo.getKwlgId(), e.getMessage(), e);
            // 데이터 이쿠 실행 실패해도 Import는 성공으로 처리
        }
    }

    private Map<String, Object> buildDataikuParams(GpoKwlgInfoMas knowledgeInfo, List<Map<String, Object>> filesList, String userMemberId, String knowledgeName) {
        Map<String, Object> params = new HashMap<>();

        // 임베딩 모델 이름 (기본값: "kt")
        String embeddingModelName = "kt";
        if (knowledgeInfo.getModelId() != null && !knowledgeInfo.getModelId().isEmpty()) {
            try {
                embeddingModelName = knowledgeInfo.getModelId();
            } catch (BusinessException e) {
                log.warn("⚠️ 임베딩 모델 조회 실패 - modelId: {}, 기본값 사용: {}",
                        knowledgeInfo.getModelId(), embeddingModelName);
            } catch (RuntimeException e) {
                log.warn("⚠️ 임베딩 모델 조회 실패 - modelId: {}, 기본값 사용: {}",
                        knowledgeInfo.getModelId(), embeddingModelName);
            } catch (Exception e) {
                log.warn("⚠️ 임베딩 모델 조회 실패 - modelId: {}, 기본값 사용: {}",
                        knowledgeInfo.getModelId(), embeddingModelName);
            }
        }

        // 청킹 알고리즘 파라미터 조회
        String prmtCtnt = null;
        if (knowledgeInfo.getChunkId() != null && !knowledgeInfo.getChunkId().isEmpty()) {
            try {
                java.util.Optional<com.skax.aiplatform.entity.knowledge.GpoChunkAlgoMas> chunkAlgoOpt = gpoChunkAlgoMasRepository
                        .findById(knowledgeInfo.getChunkId());
                if (chunkAlgoOpt.isPresent()) {
                    prmtCtnt = chunkAlgoOpt.get().getPrmtCtnt();
                    log.info("📋 [Dataiku 파라미터] 청킹 알고리즘 파라미터 조회 성공 - chunkId: {}, prmtCtnt: {}",
                            knowledgeInfo.getChunkId(), prmtCtnt);
                } else {
                    log.warn("⚠️ [Dataiku 파라미터] 청킹 알고리즘 정보 없음 - chunkId: {}", knowledgeInfo.getChunkId());
                }
            } catch (BusinessException e) {
                log.warn("⚠️ [Dataiku 파라미터] 청킹 알고리즘 조회 실패 (BusinessException) - chunkId: {}, 오류: {}",
                        knowledgeInfo.getChunkId(), e.getMessage());
            } catch (RuntimeException e) {
                log.warn("⚠️ [Dataiku 파라미터] 청킹 알고리즘 조회 실패 (RuntimeException) - chunkId: {}, 오류: {}",
                        knowledgeInfo.getChunkId(), e.getMessage());
            } catch (Exception e) {
                log.warn("⚠️ [Dataiku 파라미터] 청킹 알고리즘 조회 실패 (Exception) - chunkId: {}, 오류: {}",
                        knowledgeInfo.getChunkId(), e.getMessage());
            }
        }

        // 메인 파라미터 설정
        params.put("chunking", knowledgeInfo.getChunkId() != null ? knowledgeInfo.getChunkId() : "kss");
        params.put("embedding_model", embeddingModelName);
        params.put("index_nm", knowledgeInfo.getIdxNm());
        params.put("chunk_created_by", knowledgeInfo.getCreatedBy() != null ? knowledgeInfo.getCreatedBy() : "admin");
        params.put("chunk_updated_by", knowledgeInfo.getUpdatedBy() != null ? knowledgeInfo.getUpdatedBy() : "admin");
        params.put("file_list", filesList);
        params.put("user_id", userMemberId);
        params.put("knowledge_name", knowledgeName);

        // 청킹 알고리즘 파라미터 추가
        if (prmtCtnt != null && !prmtCtnt.isEmpty()) {
            params.put("prmt_ctnt", prmtCtnt);
            log.info("📋 [Dataiku 파라미터] prmt_ctnt 추가 완료");
        }

        // 동기화 여부 파라미터 추가
        params.put("dvlp_synch_yn",
                knowledgeInfo.getDvlpSynchYn() != null ? knowledgeInfo.getDvlpSynchYn().intValue() : 0);
        params.put("unyung_synch_yn",
                knowledgeInfo.getUnyungSynchYn() != null ? knowledgeInfo.getUnyungSynchYn().intValue() : 0);

        return params;
    }

    public GetModelCtlgRes getModelCtlgById(String id) {
        log.info("모델 상세 조회 요청: {}", id);

        // 모델 상세 조회
        ModelRead response = sktaiModelService.readModel(id);
        log.info("========== 모델 상세 조회 성공: {}", response);

        GetModelCtlgRes modelCtlgRes = modelCtlgMapper.toGetModelCtlgRes(response, null);
        // 파인튜닝 모델 매핑 처리 (내부에서 모든 예외를 처리하므로 예외가 전파되지 않음)
        processFinetuningModelMapping(modelCtlgRes);

        // Lineage 조회 및 deployStatus 설정
        try {
            // 모델 ID로 Lineage 조회 (upstream 방향으로 - 모델에 들어오는 관계)
            List<LineageRelationWithTypes> lineageRelations = sktaiLineageService.getLineageByObjectKeyAndDirection(id, Direction.UPSTREAM, ActionType.USE.getValue(), 1);
            log.debug("모델 {} Lineage 조회 완료: {}건", id, lineageRelations != null ? lineageRelations.size() : 0);
            log.debug("모델 {} Lineage 정보: {}", id, lineageRelations);

            // source_type이 SERVING_MODEL인 것이 하나라도 있으면 deployStatus를 DEV로 설정
            if (lineageRelations != null && !lineageRelations.isEmpty()) {
                boolean hasServingModel = lineageRelations.stream().anyMatch(relation -> ObjectType.SERVING_MODEL.equals(relation.getSourceType()));
                if (hasServingModel) {
                    modelCtlgRes.setDeployStatus("DEV");
                    log.debug("모델 {} deployStatus를 DEV로 설정 (SERVING_MODEL Lineage 발견): {}", id, modelCtlgRes.getDeployStatus());
                }
            }
        } catch (BusinessException e) {
            log.warn("모델 {} Lineage 조회 실패 (BusinessException) - errorCode: {}", id, e.getErrorCode(), e);
            // Lineage 조회 실패 시에도 모델 정보는 정상 반환
        } catch (RuntimeException e) {
            log.warn("모델 {} Lineage 조회 실패 (RuntimeException): {}", id, e.getMessage(), e);
            // Lineage 조회 실패 시에도 모델 정보는 정상 반환
        }

        try {
            ModelEndpointsRead endpoints = sktaiModelService.readModelEndpoints(id, 1, 1, null, null, null);
            List<ModelEndpointRead> data = endpoints.getData();

            // 첫 번째 엔드포인트가 존재하는 경우에만 설정
            if (!data.isEmpty()) {
                ModelEndpointRead firstEndpoint = data.get(0);
                modelCtlgRes.setEndpointId(firstEndpoint.getId());
                modelCtlgRes.setUrl(firstEndpoint.getUrl());
                modelCtlgRes.setIdentifier(firstEndpoint.getIdentifier());
                modelCtlgRes.setKey(firstEndpoint.getKey());
            }
        } catch (BusinessException e) {
            log.warn("모델 엔드포인트 조회 실패 (BusinessException) - modelId: {}, errorCode: {}, 엔드포인트 정보 없이 진행", id, e.getErrorCode(), e);
            // 엔드포인트 조회 실패 시에도 모델 정보는 정상 반환
        } catch (RuntimeException e) {
            log.warn("모델 엔드포인트 조회 실패 (RuntimeException) - modelId: {}, 엔드포인트 정보 없이 진행: {}", id, e.getMessage(), e);
            // 엔드포인트 조회 실패 시에도 모델 정보는 정상 반환
        }

        return modelCtlgRes;
    }

    /**
     * 파인튜닝으로 생성된 모델의 매핑 정보를 처리합니다.
     * Policy에서 프로젝트 정보를 추출하여 GpoAssetPrjMapMas를 생성합니다.
     *
     * @param modelCtlg 모델 카탈로그 정보
     */
    private void processFinetuningModelMapping(GetModelCtlgRes modelCtlg) {
        try {
            if (modelCtlg.getTrainingId() == null) {
                return;
            }

            GpoAssetPrjMapMas existing = assetPrjMapMasRepository.findByAsstUrl("/api/v1/models/" + modelCtlg.getId()).orElse(null);
            if (existing != null) {
                return;
            }

            String resourceUrl = "/api/v1/backend-ai/finetuning/trainings/" + modelCtlg.getTrainingId();

            List<PolicyRequest> policy = sktaiAuthService.getPolicy(resourceUrl);

            // policy에서 조건에 맞는 pattern 추출 및 P 뒤 숫자 추출
            if (policy == null || policy.isEmpty()) {
                return;
            }

            for (PolicyRequest item : policy) {
                // scopes에 "GET", "POST", "PUT", "DELETE"가 포함되어 있는지 확인
                if (item.getScopes() == null || !item.getScopes().contains("GET") || !item.getScopes().contains("POST") || !item.getScopes().contains("PUT") || !item.getScopes().contains("DELETE")) {
                    continue;
                }

                // policies에서 type이 "regex", logic이 "POSITIVE", targetClaim이 "current_group"인 항목
                // 찾기
                if (item.getPolicies() == null) {
                    continue;
                }

                for (PolicyItem policyItem : item.getPolicies()) {
                    if (!"regex".equals(policyItem.getType()) || !"POSITIVE".equals(policyItem.getLogic()) || !"current_group".equals(policyItem.getTargetClaim())) {
                        continue;
                    }

                    String pattern = policyItem.getPattern();
                    if (pattern == null) {
                        continue;
                    }

                    // P 뒤에 오는 숫자 추출
                    // 케이스 1: "^/P\\-999_R\\-199$" (JSON) -> "^/P\-999_R\-199$" (Java String) ->
                    // "-999" 추출
                    // 케이스 2: "^/P211_R.+$" (JSON) -> "^/P211_R.+$" (Java String) -> "211" 추출
                    // 케이스 3: "^/P-999_R.+$" (이스케이프 없는 하이픈) -> "-999" 추출
                    // API에서 받은 JSON: "^/P\\-999_R\\-199$" (JSON에서 \\는 하나의 \)
                    // Jackson 역직렬화 후 Java String: "^/P\-999_R\-199$" (실제 문자열 값)
                    // 정규식에서 \-는 리터럴 하이픈이므로, P\-를 찾으려면 P\\- 패턴 사용
                    // Java 문자열 리터럴에서 \\\\-는 정규식 \\-가 되고, 이것은 리터럴 \-를 의미
                    // AdminAuthServiceImpl에서 음수일 때만 P\- 형태로 생성되므로, P\- 또는 P- 다음 숫자는 음수로 처리
                    Pattern numberPattern = Pattern.compile("P[\\\\-](-?\\d+)");
                    Matcher matcher = numberPattern.matcher(pattern);
                    boolean isNegative = false;

                    if (matcher.find()) {
                        // P\- 또는 P- 패턴으로 매칭 성공 (P\-999 또는 P-999 형태)
                        String extractedNumber = matcher.group(1);
                        // 추출된 숫자에 하이픈이 포함되어 있지 않으면 음수로 처리
                        // (하이픈이 포함되어 있으면 이미 음수로 파싱됨)
                        if (!extractedNumber.startsWith("-")) {
                            // P\- 또는 P- 다음 숫자는 음수로 처리
                            isNegative = true;
                        }
                    } else {
                        // P\- 또는 P- 패턴이 없으면 P 다음에 바로 숫자가 오는 경우 시도 (P211 형태)
                        numberPattern = Pattern.compile("P(-?\\d+)");
                        matcher = numberPattern.matcher(pattern);
                        if (!matcher.find()) {
                            log.warn("패턴에서 숫자를 추출할 수 없습니다. pattern: [{}]", pattern);
                            continue;
                        }
                    }

                    long projectSeq = Long.parseLong(matcher.group(1));
                    if (isNegative) {
                        projectSeq = -projectSeq;
                    }
                    log.info("추출된 숫자: {}", projectSeq);

                    String asstUrl = "/api/v1/models/" + modelCtlg.getId();

                    // GpoAssetPrjMapMas 생성
                    GpoAssetPrjMapMas mapping = GpoAssetPrjMapMas.builder().asstUrl(asstUrl).fstPrjSeq(Math.toIntExact(projectSeq)).lstPrjSeq(Math.toIntExact(projectSeq)).build();

                    // createdBy, updatedBy를 직접 설정 (JPA Auditing 우회)
                    String createdByMemberId = null;
                    String updatedByMemberId = null;

                    if (modelCtlg.getCreatedBy() != null) {
                        GpoUsersMas createdUser = gpoUsersMasRepository.findByUuid(modelCtlg.getCreatedBy()).orElse(null);
                        if (createdUser != null) {
                            createdByMemberId = createdUser.getMemberId();
                        }
                    }

                    if (modelCtlg.getUpdatedBy() != null) {
                        GpoUsersMas updatedUser = gpoUsersMasRepository.findByUuid(modelCtlg.getUpdatedBy()).orElse(null);
                        if (updatedUser != null) {
                            updatedByMemberId = updatedUser.getMemberId();
                        }
                    }

                    // Reflection을 사용하여 createdBy, updatedBy 필드 직접 설정
                    if (createdByMemberId != null) {
                        setFieldValue(mapping, "createdBy", createdByMemberId);
                    }
                    if (updatedByMemberId != null) {
                        setFieldValue(mapping, "updatedBy", updatedByMemberId);
                    }

                    assetPrjMapMasRepository.save(mapping);
                    return; // 첫 번째 매칭되는 항목만 처리하고 종료
                }
            }
        } catch (BusinessException e) {
            log.warn("파인튜닝 모델 매핑 처리 중 비즈니스 예외 발생 (계속 진행): modelId={}, errorCode={}, message={}", modelCtlg.getId(), e.getErrorCode(), e.getMessage());
        } catch (NumberFormatException e) {
            log.warn("파인튜닝 모델 매핑 처리 중 숫자 형식 변환 오류 (계속 진행): modelId={}, message={}", modelCtlg.getId(), e.getMessage());
        } catch (ArithmeticException e) {
            log.warn("파인튜닝 모델 매핑 처리 중 산술 연산 오류 (계속 진행): modelId={}, message={}", modelCtlg.getId(), e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("파인튜닝 모델 매핑 처리 중 잘못된 인자 오류 (계속 진행): modelId={}, message={}", modelCtlg.getId(), e.getMessage());
        } catch (NullPointerException e) {
            log.warn("파인튜닝 모델 매핑 처리 중 null 참조 오류 (계속 진행): modelId={}, message={}", modelCtlg.getId(), e.getMessage());
        } catch (DataAccessException e) {
            log.warn("파인튜닝 모델 매핑 처리 중 데이터 접근 오류 (계속 진행): modelId={}, message={}", modelCtlg.getId(), e.getMessage());
        } catch (RuntimeException e) {
            log.warn("파인튜닝 모델 매핑 처리 중 런타임 예외 발생 (계속 진행): modelId={}, message={}", modelCtlg.getId(), e.getMessage(), e);
        } catch (Exception e) {
            log.warn("파인튜닝 모델 매핑 처리 중 예상치 못한 오류 발생 (계속 진행): modelId={}, exceptionType={}, message={}", modelCtlg.getId(), e.getClass().getSimpleName(), e.getMessage(), e);
        }
    }

    /**
     * Reflection을 사용하여 엔티티의 필드 값을 설정합니다.
     * JPA Auditing을 우회하여 createdBy, updatedBy 등을 직접 설정할 때 사용합니다.
     *
     * @param entity    대상 엔티티 객체
     * @param fieldName 설정할 필드명
     * @param value     설정할 값
     */
    private void setFieldValue(Object entity, String fieldName, Object value) {
        try {
            Class<?> clazz = entity.getClass();
            // 상속 구조를 따라가며 필드 찾기 (AuditableEntity의 createdBy, updatedBy 필드)
            while (clazz != null) {
                try {
                    Field field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    field.set(entity, value);
                    return;
                } catch (NoSuchFieldException e) {
                    // 현재 클래스에 필드가 없으면 부모 클래스로 이동
                    clazz = clazz.getSuperclass();
                }
            }
            log.warn("필드를 찾을 수 없습니다: {}", fieldName);
        } catch (IllegalAccessException e) {
            log.error("필드 설정 중 접근 권한 오류 발생: fieldName={}, message={}", fieldName, e.getMessage(), e);
        } catch (SecurityException e) {
            log.error("필드 설정 중 보안 오류 발생: fieldName={}, message={}", fieldName, e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            log.error("필드 설정 중 잘못된 인자 오류 발생: fieldName={}, value={}, message={}", fieldName, value, e.getMessage(), e);
        } catch (NullPointerException e) {
            log.error("필드 설정 중 null 참조 오류 발생: fieldName={}, message={}", fieldName, e.getMessage(), e);
        } catch (ClassCastException e) {
            log.error("필드 설정 중 타입 캐스팅 오류 발생: fieldName={}, valueType={}, message={}", fieldName, value != null ? value.getClass().getSimpleName() : "null", e.getMessage(), e);
        } catch (RuntimeException e) {
            log.error("필드 설정 중 런타임 예외 발생: fieldName={}, message={}", fieldName, e.getMessage(), e);
        } catch (Exception e) {
            log.error("필드 설정 중 예상치 못한 오류 발생: fieldName={}, exceptionType={}, message={}", fieldName, e.getClass().getSimpleName(), e.getMessage(), e);
        }
    }

    /**
     * Knowledge(ExternalRepo) 존재 여부 확인
     *
     * @param knowledgeId Knowledge ID
     * @return 존재하면 true, 없으면 false
     */
    public boolean checkIfExists(String knowledgeId) {
        try {
            sktaiExternalReposService.getExternalRepo(knowledgeId);
            return true;
        } catch (Exception e) {
            log.debug("Knowledge 존재 확인 실패 (없는 것으로 간주) - id: {}, error: {}", knowledgeId, e.getMessage());
            return false;
        }
    }
}

