package com.skax.aiplatform.service.data.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.skax.aiplatform.common.util.PaginationUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.client.sktai.auth.service.SktaiAuthService;
import com.skax.aiplatform.client.sktai.externalKnowledge.dto.response.ExternalRepoListResponse;
import com.skax.aiplatform.client.sktai.externalKnowledge.service.SktaiExternalReposService;
import com.skax.aiplatform.client.sktai.knowledge.dto.request.RepoExtTestRequest;
import com.skax.aiplatform.client.udp.dataiku.dto.request.DataikuExecutionRequest;
import com.skax.aiplatform.client.udp.dataiku.dto.response.DataikuExecutionResponse;
import com.skax.aiplatform.client.udp.dataiku.service.UdpDataikuService;
import com.skax.aiplatform.client.udp.elasticsearch.dto.response.IndexCreateResponse;
import com.skax.aiplatform.client.udp.elasticsearch.service.UdpElasticsearchService;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.dto.data.request.DataCtlgExternalKnowledgeCreateReq;
import com.skax.aiplatform.dto.data.request.DataCtlgExternalKnowledgeTestReq;
import com.skax.aiplatform.dto.data.request.DataCtlgExternalKnowledgeUpdateReq;
import com.skax.aiplatform.dto.data.request.ExternalKnowledgeChunksReq;
import com.skax.aiplatform.dto.data.request.ExternalKnowledgeFilesReq;
import com.skax.aiplatform.dto.data.response.DataCtlgExternalKnowledgeCreateRes;
import com.skax.aiplatform.dto.data.response.ExternalKnowledgeChunksRes;
import com.skax.aiplatform.dto.data.response.ExternalKnowledgeFilesRes;
import com.skax.aiplatform.dto.data.response.ExternalKnowledgeTestResult;
import com.skax.aiplatform.entity.knowledge.GpoKwlgInfoMas;
import com.skax.aiplatform.entity.mapping.GpoAssetPrjMapMas;
import com.skax.aiplatform.repository.knowledge.GpoKwlgInfoMasRepository;
import com.skax.aiplatform.repository.mapping.GpoAssetPrjMapMasRepository;
import com.skax.aiplatform.service.admin.AdminAuthService;
import com.skax.aiplatform.service.data.DataCtlgExternalKnowledgeService;

import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * DataCtlg External Knowledge 서비스 구현체
 *
 * <p>
 * External Knowledge Repository 관련 비즈니스 로직을 구현하는 서비스 클래스입니다.
 * </p>
 *
 * @author ByounggwanLee
 * @version 2.0
 * @since 2025-10-11
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DataCtlgExternalKnowledgeServiceImpl implements DataCtlgExternalKnowledgeService {

    private final SktaiExternalReposService sktaiExternalReposService;
    private final GpoKwlgInfoMasRepository gpoKwlgInfoMasRepository;
    private final com.skax.aiplatform.repository.knowledge.GpoChunkAlgoMasRepository gpoChunkAlgoMasRepository;
    private final com.skax.aiplatform.repository.model.GpoModelEmbeddingMasRepository gpoModelEmbeddingMasRepository;
    private final ObjectMapper objectMapper;
    private final UdpElasticsearchService udpElasticsearchService;
    private final UdpDataikuService udpDataikuService;
    private final AdminAuthService adminAuthService;
    private final GpoAssetPrjMapMasRepository assetPrjMapMasRepository;
    private final SktaiAuthService sktaiAuthService;
    private final com.skax.aiplatform.service.model.ModelServingService modelServingService;

    @Value("${spring.profiles.active:elocal}")
    private String activeProfile;

    /**
     * 공통 예외 처리 메서드
     *
     * @param operation 작업 설명
     * @param e         발생한 예외
     * @return RuntimeException (BusinessException으로 변환)
     */
    private RuntimeException handleException(String operation, Exception e) {
        if (e instanceof BusinessException) {
            log.error("❌ ADXP External Knowledge Repository {} 중 BusinessException 발생 - 오류: {}",
                    operation, e.getMessage(), e);
            return (BusinessException) e;
        } else if (e instanceof FeignException) {
            FeignException feignEx = (FeignException) e;
            log.error("❌ ADXP External Knowledge Repository {} 중 FeignException 발생 - 상태코드: {}, 오류: {}, 응답본문: {}",
                    operation, feignEx.status(), feignEx.getMessage(), feignEx.contentUTF8(), feignEx);
            return new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    String.format("ADXP API 호출 중 오류가 발생했습니다: HTTP %d - %s", feignEx.status(), feignEx.getMessage()));
        } else if (e instanceof RuntimeException) {
            log.error("❌ ADXP External Knowledge Repository {} 중 런타임 오류 발생 - 오류: {}",
                    operation, e.getMessage(), e);
            return new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "ADXP API 호출 중 오류가 발생했습니다: " + e.getMessage());
        } else {
            log.error("❌ ADXP External Knowledge Repository {} 중 예상치 못한 오류 발생 - 오류: {}",
                    operation, e.getMessage(), e);
            return new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "ADXP API 호출 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * BusinessException에서 detail 메시지를 추출합니다.
     * SktaiErrorDecoder에서 "SKTAI API 잘못된 요청: {detail}" 형식으로 메시지를 생성하므로
     * ": " 뒤의 부분을 추출합니다.
     *
     * @param e BusinessException
     * @return detail 메시지
     */
    private String extractDetailFromBusinessException(BusinessException e) {
        String message = e.getMessage();
        if (message == null) {
            return "알 수 없는 오류가 발생했습니다.";
        }

        // "SKTAI API 잘못된 요청: " 뒤의 부분 추출
        int colonIndex = message.indexOf(": ");
        if (colonIndex >= 0 && colonIndex < message.length() - 2) {
            return message.substring(colonIndex + 2);
        }

        // 형식이 맞지 않으면 전체 메시지 반환
        return message;
    }

    /**
     * FeignException에서 detail 메시지를 추출합니다.
     * 응답 본문을 JSON으로 파싱하여 "detail" 필드를 추출합니다.
     *
     * @param e FeignException
     * @return detail 메시지
     */
    private String extractDetailFromFeignException(FeignException e) {
        try {
            String content = e.contentUTF8();
            if (content == null || content.trim().isEmpty()) {
                return "알 수 없는 오류가 발생했습니다.";
            }

            ObjectMapper objectMapper = new ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(content);

            // "detail" 필드 추출
            if (jsonNode.has("detail")) {
                com.fasterxml.jackson.databind.JsonNode detailNode = jsonNode.get("detail");
                if (detailNode.isTextual()) {
                    return detailNode.asText();
                } else {
                    return objectMapper.writeValueAsString(detailNode);
                }
            }

            // detail 필드가 없으면 전체 응답 반환 (최대 500자)
            return content.length() > 500 ? content.substring(0, 500) + "..." : content;
        } catch (Exception ex) {
            log.warn("FeignException에서 detail 추출 실패: {}", ex.getMessage());
            return e.getMessage() != null ? e.getMessage() : "알 수 없는 오류가 발생했습니다.";
        }
    }

    /**
     * External Knowledge Repository 목록 조회
     *
     * @param page   페이지 번호
     * @param size   페이지 크기
     * @param sort   정렬 조건
     * @param filter 필터 조건
     * @param search 검색 키워드
     * @return External Knowledge Repository 목록
     */
    @Override
    public ExternalRepoListResponse getExternalRepos(Integer page, Integer size, String sort, String filter,
                                                     String search) {
        log.info("🔍 External Knowledge Repository 목록 조회 요청 - page: {}, size: {}, sort: {}, filter: {}, search: {}",
                page, size, sort, filter, search);

        try {
            // 1️⃣ SktaiExternalReposService를 통한 External Repository 목록 조회 (ADXP API)
            ExternalRepoListResponse response = sktaiExternalReposService.getExternalRepos(page, size, sort, filter,
                    search);

            // 2️⃣ DB에서 추가 정보 조회하여 병합
            if (response.getData() != null && !response.getData().isEmpty()) {
                for (com.skax.aiplatform.client.sktai.externalKnowledge.dto.response.ExternalRepoInfo repo : response
                        .getData()) {
                    // ex_kwlg_id로 DB 조회
                    java.util.Optional<GpoKwlgInfoMas> dbInfoOpt = gpoKwlgInfoMasRepository
                            .findByExKwlgId(repo.getId());

                    // 공개 여부 설정 값 가져오기 (거api/v1/knowledge/repos/external 이지만, adxp 가이드 대로 external 제
                    // )
                    GpoAssetPrjMapMas existing = assetPrjMapMasRepository
                            .findByAsstUrl("/api/v1/knowledge/repos/" + repo.getId())
                            .orElse(null);
                    String publicStatus;
                    if (existing != null && existing.getLstPrjSeq() != null) {
                        publicStatus = existing.getLstPrjSeq() < 0 ? "전체공유" : "내부공유";
                    } else {
                        publicStatus = "전체공유";
                    }
                    repo.setPublicStatus(publicStatus);

                    log.info("🔍 공개 여부 설정 값 가져오기 - publicStatus: {}", publicStatus);

                    // 최초 project seq, 최종 project seq 값 가져오기
                    int fstPrjSeq = -999;
                    int lstPrjSeq = -999;

                    if (existing != null) {
                        fstPrjSeq = existing.getFstPrjSeq();
                        lstPrjSeq = existing.getLstPrjSeq();
                    }

                    repo.setFstPrjSeq(fstPrjSeq);
                    repo.setLstPrjSeq(lstPrjSeq);

                    if (dbInfoOpt.isPresent()) {
                        // DB에 있음 - 기본지식
                        GpoKwlgInfoMas dbInfo = dbInfoOpt.get();
                        repo.setKnwId(dbInfo.getKwlgId());
                        repo.setRagChunkIndexNm(dbInfo.getIdxNm());
                        repo.setIsCustomKnowledge(false); // 기본지식

                        // 모든 DB 정보 병합
                        repo.setChunkId(dbInfo.getChunkId());
                        repo.setModelId(dbInfo.getModelId());
                        repo.setDataSetId(dbInfo.getDataSetId());
                        repo.setDataSetNm(dbInfo.getDataSetNm());
                        repo.setConsumerGrpNm(dbInfo.getConsumerGrpNm());
                        repo.setFileLoadProgress(dbInfo.getFileLoadJinhgRt());
                        repo.setChunkProgress(dbInfo.getChunkJinhgRt());
                        repo.setDbLoadProgress(dbInfo.getDbLoadJinhgRt());
                        repo.setDvlpSyncYn(dbInfo.getDvlpSynchYn());
                        repo.setProdSyncYn(dbInfo.getUnyungSynchYn());
                        repo.setKafkaConnectorStatus(dbInfo.getKafkaCntrStatus());
                        repo.setDataPipelineLoadStatus(dbInfo.getDataPipelineLoadStatus());
                        repo.setDataPipelineSyncStatus(dbInfo.getDataPipelineSynchStatus());

                        // 청킹 알고리즘 테이블 JOIN해서 청킹명 가져오기
                        if (dbInfo.getChunkId() != null && !dbInfo.getChunkId().isEmpty()) {
                            java.util.Optional<com.skax.aiplatform.entity.knowledge.GpoChunkAlgoMas> chunkInfoOpt = gpoChunkAlgoMasRepository
                                    .findById(dbInfo.getChunkId());
                            if (chunkInfoOpt.isPresent()) {
                                repo.setChunkNm(chunkInfoOpt.get().getAlgoNm());
                                log.debug("📋 청킹 알고리즘 정보 병합 완료 - chunkId: {}, algoNm: {}",
                                        dbInfo.getChunkId(), chunkInfoOpt.get().getAlgoNm());
                            } else {
                                log.warn("⚠️ 청킹 알고리즘 정보 없음 - chunkId: {}", dbInfo.getChunkId());
                            }
                        }

                        log.debug("📋 DB 정보 병합 완료 (기본지식) - exKwlgId: {}, kwlgId: {}, idxNm: {}",
                                repo.getId(), dbInfo.getKwlgId(), dbInfo.getIdxNm());
                    } else {
                        // DB에 없음 - 사용자 정의 지식
                        repo.setIsCustomKnowledge(true); // 사용자 정의 지식
                        log.debug("📋 DB에 없음 (사용자 정의 지식) - expKnwId: {}", repo.getId());
                    }
                }
            }

            // hasNext를 ExternalRepoListResponse에 추가
            response.setHasNext(
                    PaginationUtils.toPageResponseFromAdxp(
                            response.getPayload(),
                            response.getData()
                    ).isHasNext()
            );

            log.info("✅ External Knowledge Repository 목록 조회 성공 - 데이터 개수: {}, hasNext: {}",
                    response.getData() != null ? response.getData().size() : 0, response.getHasNext());
            return response;

        } catch (BusinessException e) {
            throw handleException("External Knowledge Repository 목록 조회", e);
        } catch (FeignException e) {
            throw handleException("External Knowledge Repository 목록 조회", e);
        } catch (RuntimeException e) {
            throw handleException("External Knowledge Repository 목록 조회", e);
        } catch (Exception e) {
            throw handleException("External Knowledge Repository 목록 조회", e);
        }
    }

    /**
     * External Knowledge 상세 조회 (DB + ADXP 통합)
     *
     * @param id 지식 UUID (knwId 또는 expKnwId)
     * @return External Knowledge 상세 정보
     */
    @Override
    public Object getExternalKnowledge(String id) {
        log.info("🔍 External Knowledge 상세 조회 시작 - id: {}", id);

        try {
            // 1️⃣ DB에서 지식 정보 조회 (knwId 또는 exKwlgId로 시도)
            java.util.Optional<GpoKwlgInfoMas> knowledgeInfoOpt = gpoKwlgInfoMasRepository.findById(id);

            java.util.Map<String, Object> response = new java.util.HashMap<>();

            if (knowledgeInfoOpt.isPresent()) {
                // 2️⃣ DB에 있는 경우 - 기본지식
                GpoKwlgInfoMas knowledgeInfo = knowledgeInfoOpt.get();
                log.info("✅ DB 조회 성공 - kwlgId: {}, exKwlgId: {}",
                        knowledgeInfo.getKwlgId(), knowledgeInfo.getExKwlgId());

                response.put("knwId", knowledgeInfo.getKwlgId());
                response.put("knwNm", knowledgeInfo.getKwlgNm());
                response.put("expKnwId", knowledgeInfo.getExKwlgId());
                response.put("chunkId", knowledgeInfo.getChunkId());
                response.put("modelId", knowledgeInfo.getModelId());
                response.put("dataSetId", knowledgeInfo.getDataSetId());
                response.put("dataSetNm", knowledgeInfo.getDataSetNm());
                response.put("ragChunkIndexNm", knowledgeInfo.getIdxNm());
                response.put("consumerGrpNm", knowledgeInfo.getConsumerGrpNm());
                response.put("fileLoadProgress", knowledgeInfo.getFileLoadJinhgRt());
                response.put("chunkProgress", knowledgeInfo.getChunkJinhgRt());
                response.put("dbLoadProgress", knowledgeInfo.getDbLoadJinhgRt());
                response.put("dvlpSyncYn", knowledgeInfo.getDvlpSynchYn());
                response.put("prodSyncYn", knowledgeInfo.getUnyungSynchYn());
                response.put("kafkaConnectorStatus", knowledgeInfo.getKafkaCntrStatus());
                response.put("dataPipelineLoadStatus", knowledgeInfo.getDataPipelineLoadStatus());
                response.put("dataPipelineSyncStatus", knowledgeInfo.getDataPipelineSynchStatus());
                response.put("idxMkSttAt", knowledgeInfo.getIdxMkSttAt());
                response.put("idxMkEndAt", knowledgeInfo.getIdxMkEndAt());
                // response.put("createdBy", knowledgeInfo.getCreatedBy());
                // response.put("fstCreatedAt", knowledgeInfo.getFstCreatedAt());
                // response.put("updatedBy", knowledgeInfo.getUpdatedBy());
                // response.put("lstUpdatedAt", knowledgeInfo.getLstUpdatedAt());
                response.put("is_custom_knowledge", false); // 기본지식

                // 2-1️⃣ SKTAI API 호출하여 script 정보 가져오기
                try {
                    String expKnwId = knowledgeInfo.getExKwlgId();
                    if (expKnwId != null && !expKnwId.isEmpty()) {
                        log.info("📋 기본지식 script 조회를 위해 SKTAI API 호출 - expKnwId: {}", expKnwId);
                        Object repoDetailObj = sktaiExternalReposService.getExternalRepo(expKnwId);

                        if (repoDetailObj != null && repoDetailObj instanceof java.util.Map) {
                            @SuppressWarnings("unchecked")
                            java.util.Map<String, Object> repoDetail = (java.util.Map<String, Object>) repoDetailObj;

                            // script, name, description 등 SKTAI에만 있는 정보 추가
                            if (repoDetail.containsKey("script")) {
                                response.put("script", repoDetail.get("script"));
                                log.info("✅ script 정보 추가 완료");
                            }
                            if (repoDetail.containsKey("name")) {
                                response.put("name", repoDetail.get("name"));
                            }
                            if (repoDetail.containsKey("description")) {
                                response.put("description", repoDetail.get("description"));
                            }
                            if (repoDetail.containsKey("embedding_model_name")) {
                                response.put("embedding_model_name", repoDetail.get("embedding_model_name"));
                            }
                            if (repoDetail.containsKey("vector_db_name")) {
                                response.put("vector_db_name", repoDetail.get("vector_db_name"));
                            }
                            if (repoDetail.containsKey("index_name")) {
                                response.put("index_name", repoDetail.get("index_name"));
                            }
                            if (repoDetail.containsKey("is_active")) {
                                response.put("is_active", repoDetail.get("is_active"));
                            }

                            if (repoDetail.containsKey("created_by")) {
                                response.put("created_by", repoDetail.get("created_by"));
                            }
                            if (repoDetail.containsKey("created_at")) {
                                response.put("created_at", repoDetail.get("created_at"));
                            }
                            if (repoDetail.containsKey("updated_by")) {
                                response.put("updated_by", repoDetail.get("updated_by"));
                            }
                            if (repoDetail.containsKey("updated_at")) {
                                response.put("updated_at", repoDetail.get("updated_at"));
                            }

                            // 청킹 알고리즘 테이블 JOIN해서 청킹명 가져오기
                            if (knowledgeInfo.getChunkId() != null && !knowledgeInfo.getChunkId().isEmpty()) {
                                java.util.Optional<com.skax.aiplatform.entity.knowledge.GpoChunkAlgoMas> chunkInfoOpt = gpoChunkAlgoMasRepository
                                        .findById(knowledgeInfo.getChunkId());
                                if (chunkInfoOpt.isPresent()) {
                                    response.put("chunk_nm", chunkInfoOpt.get().getAlgoNm());
                                    log.debug("📋 청킹 알고리즘 정보 병합 완료 - chunkId: {}, algoNm: {}",
                                            knowledgeInfo.getChunkId(), chunkInfoOpt.get().getAlgoNm());
                                } else {
                                    log.warn("⚠️ 청킹 알고리즘 정보 없음 - chunkId: {}", knowledgeInfo.getChunkId());
                                }
                            }
                        }
                    }
                } catch (BusinessException e) {
                    log.warn("⚠️ SKTAI API script 조회 실패 (BusinessException, 무시하고 계속) - expKnwId: {}, 오류: {}",
                            knowledgeInfo.getExKwlgId(), e.getMessage());
                    // script 조회 실패해도 기본 DB 정보는 반환
                } catch (FeignException e) {
                    log.warn("⚠️ SKTAI API script 조회 실패 (FeignException, 무시하고 계속) - expKnwId: {}, 상태코드: {}, 오류: {}",
                            knowledgeInfo.getExKwlgId(), e.status(), e.getMessage());
                    // script 조회 실패해도 기본 DB 정보는 반환
                } catch (RuntimeException e) {
                    log.warn("⚠️ SKTAI API script 조회 실패 (RuntimeException, 무시하고 계속) - expKnwId: {}, 오류: {}",
                            knowledgeInfo.getExKwlgId(), e.getMessage());
                    // script 조회 실패해도 기본 DB 정보는 반환
                } catch (Exception e) {
                    log.warn("⚠️ SKTAI API script 조회 실패 (Exception, 무시하고 계속) - expKnwId: {}, 오류: {}",
                            knowledgeInfo.getExKwlgId(), e.getMessage());
                    // script 조회 실패해도 기본 DB 정보는 반환
                }

                // 최초 project seq, 최종 project seq 값 가져오기
                int fstPrjSeq = -999;
                int lstPrjSeq = -999;

                GpoAssetPrjMapMas existing = assetPrjMapMasRepository
                        .findByAsstUrl("/api/v1/knowledge/repos/" + knowledgeInfo.getExKwlgId())
                        .orElse(null);

                if (existing != null) {
                    fstPrjSeq = existing.getFstPrjSeq();
                    lstPrjSeq = existing.getLstPrjSeq();
                }

                response.put("fst_prj_seq", fstPrjSeq);
                response.put("lst_prj_seq", lstPrjSeq);

                log.info("🎉 External Knowledge 상세 조회 완료 (기본지식) - knwId: {}", id);
                return response;
            } else {
                // 3️⃣ DB에 없는 경우 - 사용자 정의 지식 (ADXP에만 있음)
                log.info("📋 DB에 없음, ADXP에서 조회 시도 - expKnwId: {}", id);

                // ADXP 상세 조회 API 호출 (script 포함)
                try {
                    Object repoDetailObj = sktaiExternalReposService.getExternalRepo(id);

                    if (repoDetailObj == null) {
                        throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                                "지식 정보를 찾을 수 없습니다: " + id);
                    }

                    log.info("📋 DB에 없음, ADXP에서 조회 시도 - repoDetailObj: {}", repoDetailObj);

                    // RepoResponse 타입이지만 실제로는 External API 응답이므로 Map으로 처리
                    // SKTAI API가 반환하는 필드를 직접 사용
                    // 사용자 정의 지식인 경우 is_custom_knowledge를 true로 설정
                    if (repoDetailObj instanceof java.util.Map) {
                        @SuppressWarnings("unchecked")
                        java.util.Map<String, Object> repoDetailMap = (java.util.Map<String, Object>) repoDetailObj;
                        repoDetailMap.put("is_custom_knowledge", true); // 사용자 정의 지식
                    }
                    return repoDetailObj;

                } catch (BusinessException e) {
                    // 403 에러(EXTERNAL_API_FORBIDDEN)는 데이터 없음으로 처리
                    if (e.getErrorCode() == ErrorCode.EXTERNAL_API_FORBIDDEN) {
                        log.debug("External Knowledge 조회 - 403 에러 발생 (데이터 없음으로 처리) - id: {}", id);
                        throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                                "지식 정보를 찾을 수 없습니다: " + id);
                    }
                    throw e;
                } catch (FeignException.Forbidden e) {
                    // 403 에러는 데이터 없음으로 처리
                    log.debug("External Knowledge 조회 - 403 에러 발생 (데이터 없음으로 처리) - id: {}", id);
                    throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                            "지식 정보를 찾을 수 없습니다: " + id);
                } catch (FeignException e) {
                    throw handleException("ADXP External Knowledge 조회", e);
                } catch (RuntimeException e) {
                    throw handleException("ADXP External Knowledge 조회", e);
                } catch (Exception e) {
                    throw handleException("ADXP External Knowledge 조회", e);
                }
            }

        } catch (BusinessException e) {
            throw e;
        } catch (FeignException e) {
            throw handleException("External Knowledge 상세 조회", e);
        } catch (RuntimeException e) {
            throw handleException("External Knowledge 상세 조회", e);
        } catch (Exception e) {
            throw handleException("External Knowledge 상세 조회", e);
        }
    }

    /**
     * External Knowledge 데이터 적재 현황 조회
     *
     * @param id 지식 UUID (knwId 또는 expKnwId)
     * @return External Knowledge 데이터 적재 현황 (fileLoadProgress,
     * dataPipelineLoadStatus 포함)
     */
    @Override
    public Object getExternalKnowledgeProgress(String id) {
        log.info("🔍 External Knowledge 데이터 적재 현황 조회 시작 - id: {}", id);

        try {
            // 1️⃣ DB에서 지식 정보 조회 (knwId 또는 exKwlgId로 시도)
            java.util.Optional<GpoKwlgInfoMas> knowledgeInfoOpt = gpoKwlgInfoMasRepository.findById(id);

            // knwId로 못 찾으면 exKwlgId로 시도
            if (knowledgeInfoOpt.isEmpty()) {
                knowledgeInfoOpt = gpoKwlgInfoMasRepository.findByExKwlgId(id);
            }

            java.util.Map<String, Object> response = new java.util.HashMap<>();

            if (knowledgeInfoOpt.isPresent()) {
                // 2️⃣ DB에 있는 경우 - 기본지식
                GpoKwlgInfoMas knowledgeInfo = knowledgeInfoOpt.get();
                log.info("✅ DB 조회 성공 - kwlgId: {}, exKwlgId: {}",
                        knowledgeInfo.getKwlgId(), knowledgeInfo.getExKwlgId());

                // fileLoadProgress와 dataPipelineLoadStatus 포함하여 응답
                response.put("dbLoadProgress", knowledgeInfo.getDbLoadJinhgRt());
                response.put("dataPipelineLoadStatus", knowledgeInfo.getDataPipelineLoadStatus());

                log.info(
                        "🎉 External Knowledge 데이터 적재 현황 조회 완료 - knwId: {}, fileLoadProgress: {}, dataPipelineLoadStatus: {}",
                        id, knowledgeInfo.getFileLoadJinhgRt(), knowledgeInfo.getDataPipelineLoadStatus());
                return response;
            } else {
                // 3️⃣ DB에 없는 경우 - 사용자 정의 지식 (ADXP에만 있음)
                log.warn("⚠️ DB에 지식 정보가 없음 - expKnwId: {}", id);

                // 사용자 정의 지식의 경우 fileLoadProgress와 dataPipelineLoadStatus를 null로 설정
                response.put("fileLoadProgress", null);
                response.put("dataPipelineLoadStatus", null);

                log.info("🎉 External Knowledge 데이터 적재 현황 조회 완료 (사용자 정의 지식) - expKnwId: {}", id);
                return response;
            }

        } catch (BusinessException e) {
            throw e;
        } catch (FeignException e) {
            throw handleException("External Knowledge 데이터 적재 현황 조회", e);
        } catch (RuntimeException e) {
            throw handleException("External Knowledge 데이터 적재 현황 조회", e);
        } catch (Exception e) {
            throw handleException("External Knowledge 데이터 적재 현황 조회", e);
        }
    }

    /**
     * External Knowledge 생성
     *
     * @param request External Knowledge 생성 요청
     * @return External Knowledge 생성 응답
     */
    @Override
    @Transactional
    public DataCtlgExternalKnowledgeCreateRes createExternalKnowledge(DataCtlgExternalKnowledgeCreateReq request) {
        log.info("🚀 External Knowledge 생성 요청 - knwId: {}, knwNm: {}, type: {}",
                request.getKnwId(), request.getKnwNm(), request.getKnowledgeType());

        // 🔀 지식 유형에 따라 분기 처리
        if ("custom".equalsIgnoreCase(request.getKnowledgeType())) {
            return createCustomKnowledge(request);
        } else {
            return createExternalKnowledgeWithDbAndIndex(request);
        }
    }

    /**
     * 사용자 정의 지식 생성 (ADXP만 호출)
     *
     * @param request 지식 생성 요청
     * @return 지식 생성 응답
     */
    private DataCtlgExternalKnowledgeCreateRes createCustomKnowledge(DataCtlgExternalKnowledgeCreateReq request) {
        log.info("🎨 사용자 정의 지식 생성 시작 - knwId: {}, knwNm: {}", request.getKnwId(), request.getKnwNm());

        try {
            // 1️⃣ ADXP API 호출만 수행 (DB/ES 제외)
            log.info("📡 ADXP External Repository 생성 API 호출 - embModelName: {}, vectorDbId: {}, indexName: {}",
                    request.getEmbeddingModel(), request.getVectorDbId(), request.getRagChunkIndexNm());

            com.skax.aiplatform.client.sktai.knowledge.dto.request.RepoExtCreateRequest adxpRequest = com.skax.aiplatform.client.sktai.knowledge.dto.request.RepoExtCreateRequest
                    .builder()
                    .name(request.getKnwNm())
                    .description(request.getDescription() != null ? request.getDescription() : "")
                    .embeddingModelName(request.getEmbeddingModel())
                    .vectorDbId(request.getVectorDbId())
                    .indexName(request.getRagChunkIndexNm())
                    .script(request.getScript() != null ? request.getScript() : "")
                    .createdBy(request.getCreatedBy() != null ? request.getCreatedBy() : "system") // 생성자 (사용자 이름)
                    .build();

            com.skax.aiplatform.client.sktai.knowledge.dto.response.RepoResponse adxpResponse = sktaiExternalReposService
                    .createExternalRepo(adxpRequest);

            String expKnwId = adxpResponse.getRepoId();
            log.info("✅ ADXP External Repository 생성 성공 - repoId: {}", expKnwId);

            // ADXP 권한 부여
            String resourcePath = "/api/v1/knowledge/repos/" + expKnwId;
            adminAuthService.setResourcePolicyByCurrentGroup(resourcePath);
            log.info("🔐 External Knowledge ADXP 권한 부여 완료 - resourcePath: {}", resourcePath);

            // 2️⃣ 응답 DTO 생성 (DB 저장 없이 바로 반환)
            DataCtlgExternalKnowledgeCreateRes response = DataCtlgExternalKnowledgeCreateRes.builder()
                    .knwId(request.getKnwId())
                    .knwNm(request.getKnwNm())
                    .expKnwId(expKnwId)
                    .chunkId(request.getChunkId())
                    .embModelId(request.getEmbModelId())
                    .ragChunkIndexNm(request.getRagChunkIndexNm())
                    .devSyncYn("N")
                    .prodSyncYn("N")
                    .createdBy("system")
                    .build();

            log.info("🎉 사용자 정의 지식 생성 완료 - knwId: {}, expKnwId: {}",
                    response.getKnwId(), response.getExpKnwId());

            return response;

        } catch (BusinessException e) {
            throw handleException("사용자 정의 지식 생성", e);
        } catch (FeignException e) {
            throw handleException("사용자 정의 지식 생성", e);
        } catch (RuntimeException e) {
            throw handleException("사용자 정의 지식 생성", e);
        } catch (Exception e) {
            throw handleException("사용자 정의 지식 생성", e);
        }
    }

    /**
     * External Knowledge 생성 (ADXP + DB + Elasticsearch)
     *
     * @param request 지식 생성 요청
     * @return 지식 생성 응답
     */
    private DataCtlgExternalKnowledgeCreateRes createExternalKnowledgeWithDbAndIndex(
            DataCtlgExternalKnowledgeCreateReq request) {
        log.info("🌐 External Knowledge 생성 시작 (DB + ES 포함) - knwId: {}, knwNm: {}",
                request.getKnwId(), request.getKnwNm());

        // 5️⃣ Elasticsearch Index 생성 (지식용)
        String indexName = request.getRagChunkIndexNm();
        log.info("🔍 Elasticsearch Index 생성 시작 - indexName: {}", indexName);

        // dimension 조회 (embeddingModel로 model_nm 조회)
        Integer dimension = getDimensionFromEmbeddingModel(request.getEmbeddingModel());
        log.info("📐 [인덱스 생성] dimension: {}", dimension);

        try {
            // Index 존재 여부 확인
            Boolean indexExists = udpElasticsearchService.indexExists(indexName);

            if (indexExists) {
                log.info("⚠️ Elasticsearch Index가 이미 존재함 - indexName: {}", indexName);
            } else {
                // 지식용 Index 생성 (dimension 파라미터 사용)
                IndexCreateResponse indexResponse = createIndexForKnowledge(indexName, dimension);

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
            // Index 생성 실패해도 지식 생성은 성공으로 처리 (수동으로 Index 생성 가능)
            log.warn("⚠️ Index 생성 실패했지만 지식 생성은 완료됨 - 수동으로 Index를 생성해주세요.");
        } catch (FeignException esEx) {
            log.error("❌ Elasticsearch Index 생성 실패 (FeignException) - indexName: {}, 상태코드: {}, 오류: {}",
                    indexName, esEx.status(), esEx.getMessage(), esEx);
            // Index 생성 실패해도 지식 생성은 성공으로 처리 (수동으로 Index 생성 가능)
            log.warn("⚠️ Index 생성 실패했지만 지식 생성은 완료됨 - 수동으로 Index를 생성해주세요.");
        } catch (RuntimeException esEx) {
            log.error("❌ Elasticsearch Index 생성 실패 (RuntimeException) - indexName: {}, 오류: {}",
                    indexName, esEx.getMessage(), esEx);
            // Index 생성 실패해도 지식 생성은 성공으로 처리 (수동으로 Index 생성 가능)
            log.warn("⚠️ Index 생성 실패했지만 지식 생성은 완료됨 - 수동으로 Index를 생성해주세요.");
        } catch (Exception esEx) {
            log.error("❌ Elasticsearch Index 생성 실패 (Exception) - indexName: {}, 오류: {}",
                    indexName, esEx.getMessage(), esEx);
            // Index 생성 실패해도 지식 생성은 성공으로 처리 (수동으로 Index 생성 가능)
            log.warn("⚠️ Index 생성 실패했지만 지식 생성은 완료됨 - 수동으로 Index를 생성해주세요.");
        }

        try {
            // 1️⃣ ADXP API 호출 - External Repository 생성
            log.info("📡 ADXP External Repository 생성 API 호출 시작 - embModelName: {}, vectorDbId: {}, indexName: {}",
                    request.getEmbeddingModel(), request.getVectorDbId(), request.getRagChunkIndexNm());
            log.info("📄 Script 길이: {} characters", request.getScript() != null ? request.getScript().length() : 0);

            com.skax.aiplatform.client.sktai.knowledge.dto.request.RepoExtCreateRequest adxpRequest = com.skax.aiplatform.client.sktai.knowledge.dto.request.RepoExtCreateRequest
                    .builder()
                    .name(request.getKnwNm())
                    .description(request.getDescription() != null ? request.getDescription() : "")
                    .embeddingModelName(request.getEmbeddingModel()) // 프론트에서 받은 임베딩 모델 이름
                    .vectorDbId(request.getVectorDbId()) // 프론트에서 받은 벡터DB ID
                    .indexName(request.getRagChunkIndexNm()) // 프론트에서 받은 인덱스명
                    .script(request.getScript() != null ? request.getScript() : "") // 프론트에서 받은 스크립트 (Service에서
                    // MultipartFile로 변환됨)
                    .createdBy(request.getCreatedBy() != null ? request.getCreatedBy() : "system") // 생성자 (사용자 이름)
                    .build();

            com.skax.aiplatform.client.sktai.knowledge.dto.response.RepoResponse adxpResponse = sktaiExternalReposService
                    .createExternalRepo(adxpRequest);

            String expKnwId = adxpResponse.getRepoId();
            log.info("✅ ADXP External Repository 생성 성공 - repoId: {}", expKnwId);

            String resourcePath = "/api/v1/knowledge/repos/" + expKnwId;
            adminAuthService.setResourcePolicyByCurrentGroup(resourcePath);
            log.info("🔐 External Knowledge ADXP 권한 부여 완료 - resourcePath: {}", resourcePath);

            // 2️⃣ GpoKwlgInfoMas 엔티티 생성
            log.info("📝 DB 저장 준비 - kwlgId: {}, kwlgNm: {}, idxNm: {}",
                    request.getKnwId(), request.getKnwNm(), request.getRagChunkIndexNm());

            // 동기화 여부 설정
            java.math.BigDecimal dvlpSynchYn = java.math.BigDecimal.ZERO; // 개발계 동기화 여부 (기본값: 0)
            java.math.BigDecimal unyungSynchYn = java.math.BigDecimal.ZERO; // 운영계 동기화 여부 (기본값: 0)

            if (request.getSyncEnabled() != null && request.getSyncEnabled()) {
                if (request.getSyncTargets() != null) {
                    // 개발계 체크시 dvlp_synch_yn을 1로
                    if (request.getSyncTargets().contains("option1")) {
                        dvlpSynchYn = java.math.BigDecimal.ONE;
                    }
                    // 운영계 체크시 unyung_synch_yn을 1로
                    if (request.getSyncTargets().contains("option2")) {
                        unyungSynchYn = java.math.BigDecimal.ONE;
                    }
                }
            }

            // 청킹 size, overlapsize를 DB에 저장하기 위한 format으로 변환
            // ex) {"chunk_size": 300, "sentence_overlap": 0}
            Map<String, Object> chunkParams = new HashMap<>();
            chunkParams.put("chunk_size", request.getChunkSize() != null ? request.getChunkSize().toString() : "300");
            chunkParams.put("sentence_overlap",
                    request.getSentenceOverlap() != null ? request.getSentenceOverlap().toString() : "0");

            GpoKwlgInfoMas knowledgeInfo = GpoKwlgInfoMas.builder()
                    .kwlgId(request.getKnwId()) // kwlgId (PK)
                    .kwlgNm(request.getKnwNm()) // kwlgNm
                    .exKwlgId(expKnwId) // exKwlgId (ADXP에서 반환된 repo_id 저장)
                    .chunkId(request.getChunkId()) // chunkId
                    .prmtCtnt(objectMapper.writeValueAsString(chunkParams)) // prmtCtnt
                    .modelId(request.getEmbeddingModel()) // modelId (임베딩 모델 ID)
                    .idxNm(request.getRagChunkIndexNm()) // idxNm (인덱스명)
                    .consumerGrpNm(request.getRagChunkIndexNm()) // consumerGrpNm (idxNm과 같은 값)
                    .dvlpSynchYn(dvlpSynchYn) // 개발계 동기화 여부
                    .unyungSynchYn(unyungSynchYn) // 운영계 동기화 여부
                    .createdBy(request.getCreatedBy() != null ? request.getCreatedBy() : "system") // 생성자 (사용자 이름)
                    .updatedBy(request.getCreatedBy() != null ? request.getCreatedBy() : "system") // 수정자 (사용자 이름)
                    .build();

            // 4️⃣ DB에 저장
            GpoKwlgInfoMas savedKnowledge = gpoKwlgInfoMasRepository.save(knowledgeInfo);

            log.info("✅ External Knowledge DB 저장 성공 - kwlgId: {}, exKwlgId: {}, idxNm: {}",
                    savedKnowledge.getKwlgId(), savedKnowledge.getExKwlgId(), savedKnowledge.getIdxNm());

            // 6️⃣ 응답 DTO 생성
            DataCtlgExternalKnowledgeCreateRes response = DataCtlgExternalKnowledgeCreateRes.builder()
                    .knwId(savedKnowledge.getKwlgId())
                    // .knwNm(savedKnowledge.getKwlgNm())
                    .expKnwId(savedKnowledge.getExKwlgId())
                    // .chunkId(savedKnowledge.getChunkId())
                    // .embModelId(savedKnowledge.getModelId())
                    // .ragChunkIndexNm(savedKnowledge.getIdxNm())
                    // .devSyncYn("N") // 새 테이블에는 dev_sync_yn 컬럼 없음
                    // .prodSyncYn("N") // 새 테이블에는 prod_sync_yn 컬럼 없음
                    // .createdBy(savedKnowledge.getUpdatedBy())
                    // .fstCreatedAt(savedKnowledge.getFstCreatedAt())
                    // .lstUpdatedAt(savedKnowledge.getLstUpdatedAt())
                    .build();

            log.info("🎉 External Knowledge 생성 완료 - knwId: {}",
                    response.getKnwId(), response.getExpKnwId(), response.getRagChunkIndexNm());

            // 📋 응답 JSON 로그 출력 (프론트엔드 디버깅용)
            try {
                String responseJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
                log.info("📋 [프론트엔드 응답 데이터]\n{}", responseJson);
            } catch (RuntimeException jsonEx) {
                log.warn("응답 JSON 변환 실패 (RuntimeException)", jsonEx);
            } catch (Exception jsonEx) {
                log.warn("응답 JSON 변환 실패 (Exception)", jsonEx);
            }

            return response;

        } catch (BusinessException e) {
            throw handleException("External Knowledge 생성", e);
        } catch (FeignException e) {
            throw handleException("External Knowledge 생성", e);
        } catch (RuntimeException e) {
            throw handleException("External Knowledge 생성", e);
        } catch (Exception e) {
            throw handleException("External Knowledge 생성", e);
        }
    }

    /**
     * External Knowledge 테스트
     *
     * @param request External Knowledge 테스트 요청
     * @return 테스트 결과
     */
    @Override
    public ExternalKnowledgeTestResult testExternalKnowledge(DataCtlgExternalKnowledgeTestReq request) {
        log.info("🧪 External Knowledge 테스트 시작 - embeddingModel: {}, vectorDBId: {}, indexName: {}",
                request.getEmbeddingModel(), request.getVectorDbId(), request.getIndexName());

        log.info("External Knowledge 테스트 Script : {}", request.getScript());

        try {
            if (request.getScript() == null || request.getScript().isBlank()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "Retrieval Script가 비어있습니다.");
            }
            if (request.getVectorDbId() == null || request.getVectorDbId().isBlank()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "Vector DB ID가 누락되었습니다.");
            }
            if (request.getEmbeddingModel() == null || request.getEmbeddingModel().isBlank()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "임베딩 모델명이 누락되었습니다.");
            }
            if (request.getIndexName() == null || request.getIndexName().isBlank()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "인덱스명이 누락되었습니다.");
            }

            String query = request.getQuery();
            if (query == null || query.isBlank()) {
                query = "sample query";
            }

            String retrievalOptions = request.getRetrievalOptions();
            if (retrievalOptions == null || retrievalOptions.isBlank()) {
                retrievalOptions = "{\"top_k\":3}";
            }

            // ADXP API 테스트 요청 생성
            RepoExtTestRequest adxpRequest = RepoExtTestRequest.builder()
                    .embeddingModelName(request.getEmbeddingModel())
                    .vectorDbId(request.getVectorDbId())
                    .indexName(request.getIndexName())
                    .script(request.getScript())
                    .query(query)
                    .retrievalOptions(retrievalOptions)
                    .build();

            log.info("📡 ADXP External Repository 테스트 API 호출 - embeddingModelName: {}, vectorDbId: {}, indexName: {}",
                    adxpRequest.getEmbeddingModelName(), adxpRequest.getVectorDbId(), adxpRequest.getIndexName());

            // ADXP API 호출
            ExternalKnowledgeTestResult response = sktaiExternalReposService.testExternalRepo(adxpRequest);

            log.info("✅ ADXP External Repository 테스트 성공 - status: {}, message: {}",
                    response.getStatus(), response.getMessage());

            return response;

        } catch (BusinessException e) {
            // 400 에러 (EXTERNAL_API_BAD_REQUEST)인 경우 detail을 추출하여 200 응답으로 반환
            if (ErrorCode.EXTERNAL_API_BAD_REQUEST.equals(e.getErrorCode())) {
                String detail = extractDetailFromBusinessException(e);
                log.info("⚠️ ADXP External Repository 테스트 400 에러 - detail: {}", detail);
                return ExternalKnowledgeTestResult.builder()
                        .success(false)
                        .status("error")
                        .message("테스트 실패: " + detail)
                        .detail(detail)
                        .build();
            }
            // 그 외 BusinessException은 그대로 던짐
            throw new BusinessException(ErrorCode.KWLG_TEST_FAILED, e.getMessage());
        } catch (FeignException e) {
            // FeignException에서 400 에러인 경우 detail 추출하여 200 응답으로 반환
            if (e.status() == 400) {
                String detail = extractDetailFromFeignException(e);
                log.info("⚠️ ADXP External Repository 테스트 400 에러 (FeignException) - detail: {}", detail);
                return ExternalKnowledgeTestResult.builder()
                        .success(false)
                        .status("error")
                        .message("테스트 실패: " + detail)
                        .detail(detail)
                        .build();
            }
            throw new BusinessException(ErrorCode.KWLG_TEST_FAILED, e.getMessage());
        } catch (RuntimeException e) {
            throw handleException("External Knowledge 테스트", e);
        } catch (Exception e) {
            throw handleException("External Knowledge 테스트", e);
        }
    }

    /**
     * External Knowledge 수정
     *
     * @param id      지식 ID (knwId 또는 expKnwId)
     * @param request 수정할 정보 (이름, 설명, 스크립트, 인덱스명)
     * @return 수정 결과
     */
    @Override
    @Transactional
    public Object updateExternalKnowledge(String id, DataCtlgExternalKnowledgeUpdateReq request) {
        log.info("✏️ External Knowledge 수정 시작 - id: {}", id);
        log.info("  - request: name={}, description={}, script={}, indexName={}",
                request.getName(), request.getDescription(),
                request.getScript() != null ? request.getScript().length() + "자" : "null",
                request.getIndexName());

        try {
            // 1️⃣ DB 조회로 기본지식/사용자정의지식 구분
            java.util.Optional<GpoKwlgInfoMas> knowledgeInfoOpt = gpoKwlgInfoMasRepository.findById(id);
            if (knowledgeInfoOpt.isEmpty()) {
                knowledgeInfoOpt = gpoKwlgInfoMasRepository.findByExKwlgId(id);
            }

            if (knowledgeInfoOpt.isPresent()) {
                // 2️⃣ 기본지식 수정 (DB + SKTAI API)
                GpoKwlgInfoMas knowledgeInfo = knowledgeInfoOpt.get();
                String expKnwId = knowledgeInfo.getExKwlgId();

                log.info("📋 기본지식 수정 - kwlgId: {}, expKnwId: {}", knowledgeInfo.getKwlgId(), expKnwId);
                log.info("📤 전송할 필드 - name: {}, description: {}, script: {}, indexName: {}",
                        request.getName() != null ? "있음" : "없음",
                        request.getDescription() != null ? "있음" : "없음",
                        request.getScript() != null ? request.getScript().length() + "자" : "없음",
                        "기본지식은 수정불가");

                // 2-1) DB 업데이트 (이름만 DB에 저장)
                knowledgeInfo.setKwlgNm(request.getName());
                gpoKwlgInfoMasRepository.save(knowledgeInfo);

                log.info("✅ DB 업데이트 완료 - kwlgNm: {}", request.getName());

                // 2-2) SKTAI API 호출 (이름, 설명, 스크립트 수정)
                log.info("⏱️ SKTAI API 호출 시작 - expKnwId: {}", expKnwId);
                long startTime = System.currentTimeMillis();

                Object sktaiResponse = sktaiExternalReposService.updateExternalRepo(
                        expKnwId,
                        request.getName(),
                        request.getDescription(),
                        request.getScript(),
                        null // 기본지식은 인덱스명 수정 불가
                );

                long duration = System.currentTimeMillis() - startTime;
                log.info("✅ SKTAI API 업데이트 완료 - expKnwId: {}, 소요시간: {}ms", expKnwId, duration);
                log.info("🎉 기본지식 수정 완료 - kwlgId: {}", knowledgeInfo.getKwlgId());

                return sktaiResponse;

            } else {
                // 3️⃣ 사용자 정의 지식 수정 (SKTAI API만)
                log.info("📋 사용자 정의 지식 수정 - expKnwId: {}", id);
                log.info("📤 전송할 필드 - name: {}, description: {}, script: {}, indexName: {}",
                        request.getName() != null ? "있음" : "없음",
                        request.getDescription() != null ? "있음" : "없음",
                        request.getScript() != null ? request.getScript().length() + "자" : "없음",
                        request.getIndexName() != null ? request.getIndexName() : "없음");

                // SKTAI API 호출 (이름, 설명, 스크립트, 인덱스명 수정)
                log.info("⏱️ SKTAI API 호출 시작 - expKnwId: {}", id);
                long startTime = System.currentTimeMillis();

                Object sktaiResponse = sktaiExternalReposService.updateExternalRepo(
                        id,
                        request.getName(),
                        request.getDescription(),
                        request.getScript(),
                        request.getIndexName());

                long duration = System.currentTimeMillis() - startTime;
                log.info("✅ SKTAI API 업데이트 완료 - expKnwId: {}, 소요시간: {}ms", id, duration);
                log.info("🎉 사용자 정의 지식 수정 완료 - expKnwId: {}", id);

                return sktaiResponse;
            }

        } catch (BusinessException e) {
            throw e;
        } catch (FeignException e) {
            throw handleException("External Knowledge 수정", e);
        } catch (RuntimeException e) {
            throw handleException("External Knowledge 수정", e);
        } catch (Exception e) {
            throw handleException("External Knowledge 수정", e);
        }
    }

    /**
     * External Knowledge 삭제 (상세 정보 포함)
     *
     * <p>
     * Frontend에서 전달받은 knwId, expKnwId, ragChunkIndexNm을 사용하여 삭제합니다.
     * DB 조회 없이 바로 삭제할 수 있어 성능이 향상됩니다.
     * </p>
     *
     * @param knwId           지식 UUID (DB PK)
     * @param expKnwId        External Knowledge repo id (ADXP)
     * @param ragChunkIndexNm RAG chunk index명 (Elasticsearch)
     */
    @Override
    @Transactional
    public void deleteExternalKnowledgeWithInfo(String knwId, String expKnwId, String ragChunkIndexNm) {
        log.info("🗑️ External Knowledge 삭제 시작 - knwId: {}, expKnwId: {}, indexName: {}",
                knwId, expKnwId, ragChunkIndexNm);

        // 1️⃣ ADXP API 호출 - External Repository 삭제 (expKnwId가 있을 경우에만)
        if (expKnwId != null && !expKnwId.isEmpty()) {
            log.info("📡 ADXP External Repository 삭제 시도 - expKnwId: {}", expKnwId);
            try {
                sktaiExternalReposService.deleteExternalRepo(expKnwId);
                log.info("✅ ADXP External Repository 삭제 완료 - expKnwId: {}", expKnwId);
            } catch (BusinessException e) {
                throw handleException("ADXP External Repository 삭제", e);
            } catch (FeignException e) {
                throw handleException("ADXP External Repository 삭제", e);
            } catch (RuntimeException e) {
                throw handleException("ADXP External Repository 삭제", e);
            } catch (Exception e) {
                throw handleException("ADXP External Repository 삭제", e);
            }
        } else {
            log.info("⏭️ ADXP External Repository 삭제 SKIP - expKnwId가 없음");
        }

        // 2️⃣ DB에서 삭제 (kwlgId가 있을 경우에만 : 기본지식)
        if (knwId != null && !knwId.isEmpty() && !knwId.equals(expKnwId)) {
            // 지식 정보 조회 및 동기화 대상 확인 (실패해도 에러 발생하지 않음)
            try {
                String indexNm = ragChunkIndexNm;
                if (expKnwId != null && !expKnwId.isEmpty()) {
                    // 데이터 이쿠 API 호출 (GAR_RAG_DELETE 시나리오)

                    log.info("📡 데이터 이쿠 동기화 삭제 실행 시도 - indexNm: {}", indexNm);
                    try {
                        Map<String, Object> dataikuParams = new HashMap<>();
                        dataikuParams.put("index_nm", indexNm);

                        DataikuExecutionRequest dataikuRequest = new DataikuExecutionRequest(dataikuParams);
                        DataikuExecutionResponse dataikuResponse = udpDataikuService
                                .executeDataikuWithScenario("GAF_RAG_DELETE", dataikuRequest);

                        log.info("✅ 데이터 이쿠 동기화 삭제 실행 완료 - indexNm: {}, runId: {}",
                                indexNm,
                                dataikuResponse != null && dataikuResponse.getBody() != null
                                        ? dataikuResponse.getBody().get("runId")
                                        : "N/A");
                    } catch (BusinessException e) {
                        log.error("❌ 데이터 이쿠 동기화 삭제 실행 실패 (BusinessException) - indexNm: {}, 오류: {}",
                                indexNm, e.getMessage(), e);
                        // 데이터 이쿠 실행 실패해도 삭제는 계속 진행
                    } catch (FeignException e) {
                        log.error("❌ 데이터 이쿠 동기화 삭제 실행 실패 (FeignException) - indexNm: {}, 상태코드: {}, 오류: {}",
                                indexNm, e.status(), e.getMessage(), e);
                        // 데이터 이쿠 실행 실패해도 삭제는 계속 진행
                    } catch (RuntimeException e) {
                        log.error("❌ 데이터 이쿠 동기화 삭제 실행 실패 (RuntimeException) - indexNm: {}, 오류: {}",
                                indexNm, e.getMessage(), e);
                        // 데이터 이쿠 실행 실패해도 삭제는 계속 진행
                    } catch (Exception e) {
                        log.error("❌ 데이터 이쿠 동기화 삭제 실행 실패 (Exception) - indexNm: {}, 오류: {}",
                                indexNm, e.getMessage(), e);
                        // 데이터 이쿠 실행 실패해도 삭제는 계속 진행
                    }
                }
            } catch (Exception e) {
                log.warn("⚠️ 동기화 대상 확인 및 데이터 이쿠 실행 중 오류 발생 - 오류: {}, 삭제는 계속 진행됩니다", e.getMessage());
                // 동기화 확인 실패해도 삭제는 계속 진행
            }

            log.info("📡 DB에서 External Knowledge 삭제 시도 - kwlgId: {}", knwId);
            try {
                gpoKwlgInfoMasRepository.deleteById(knwId);
                log.info("✅ DB에서 External Knowledge 삭제 완료 - kwlgId: {}", knwId);
            } catch (BusinessException e) {
                throw handleException("DB에서 External Knowledge 삭제", e);
            } catch (RuntimeException e) {
                throw handleException("DB에서 External Repository 삭제", e);
            } catch (Exception e) {
                throw handleException("DB에서 External Repository 삭제", e);
            }

            // 3️⃣ UDP Elasticsearch Index 삭제 (ragChunkIndexNm이 있을 경우에만)
            if (ragChunkIndexNm != null && !ragChunkIndexNm.isEmpty()) {
                log.info("📡 UDP Elasticsearch Index 삭제 시도 - indexName: {}", ragChunkIndexNm);
                try {
                    udpElasticsearchService.deleteIndex(ragChunkIndexNm);
                    log.info("✅ UDP Elasticsearch Index 삭제 완료 - indexName: {}", ragChunkIndexNm);
                } catch (BusinessException e) {
                    throw handleException("UDP Elasticsearch Index 삭제", e);
                } catch (FeignException e) {
                    throw handleException("UDP Elasticsearch Index 삭제", e);
                } catch (RuntimeException e) {
                    throw handleException("UDP Elasticsearch Index 삭제", e);
                } catch (Exception e) {
                    throw handleException("UDP Elasticsearch Index 삭제", e);
                }
            } else {
                log.info("⏭️ Elasticsearch Index 삭제 SKIP - ragChunkIndexNm이 없음");
            }
        } else {
            log.info("⏭️ DB 삭제 SKIP - kwlgId가 없음");
        }

        log.info("🎉 External Knowledge 삭제 처리 완료 - knwId: {}, expKnwId: {}, indexName: {}",
                knwId, expKnwId, ragChunkIndexNm);
    }

    /**
     * Elasticsearch Index 생성 이벤트
     *
     * <p>
     * External Knowledge 생성 후 Elasticsearch 인덱스를 생성하기 위한 이벤트 클래스입니다.
     * </p>
     */
    @Data
    @Builder
    @AllArgsConstructor
    public static class IndexCreationEvent {
        private final String indexName;
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
                if (dimensionObj instanceof Integer) {
                    dimension = (Integer) dimensionObj;
                } else if (dimensionObj instanceof Number) {
                    dimension = ((Number) dimensionObj).intValue();
                } else {
                    dimension = Integer.parseInt(dimensionObj.toString());
                }

                log.info("✅ [Dimension 조회] 성공 - model_nm: {}, dimension: {}", embeddingModel, dimension);
                return dimension;

            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                log.error("❌ [Dimension 조회] JSON 파싱 실패 - model_nm: {}, prmt_ctnt: {}, 오류: {}",
                        embeddingModel, prmtCtnt, e.getMessage(), e);
                return 2048;
            } catch (NumberFormatException e) {
                log.error("❌ [Dimension 조회] 숫자 변환 실패 - model_nm: {}, 오류: {}",
                        embeddingModel, e.getMessage(), e);
                return 2048;
            }

        } catch (Exception e) {
            log.error("❌ [Dimension 조회] 예외 발생 - model_nm: {}, 오류: {}", embeddingModel, e.getMessage(), e);
            return 2048;
        }
    }

    /**
     * 지식용 Elasticsearch 인덱스 생성
     *
     * @param indexName 생성할 인덱스명
     * @param dimension Dense vector 차원 수 (기본값: 2048)
     * @return 인덱스 생성 응답
     */
    @Transactional
    public IndexCreateResponse createIndexForKnowledge(String indexName, Integer dimension) {
        // dimension이 null이면 기본값 2048 사용
        if (dimension == null) {
            dimension = 2048;
        }
        log.info("[ExternalKnowledge] 지식용 인덱스 생성 요청 - indexName: {}, dimension: {}", indexName, dimension);
        try {
            IndexCreateResponse response = udpElasticsearchService.createIndexForKnowledge(indexName, dimension);
            log.info("[ExternalKnowledge] 지식용 인덱스 생성 완료 - indexName: {}, acknowledged: {}",
                    indexName, response.getAcknowledged());
            return response;
        } catch (BusinessException e) {
            log.error("[ExternalKnowledge] 지식용 인덱스 생성 실패 (BusinessException) - indexName: {}", indexName, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "지식용 인덱스 생성 실패: " + e.getMessage());
        } catch (FeignException e) {
            log.error("[ExternalKnowledge] 지식용 인덱스 생성 실패 (FeignException) - indexName: {}, 상태코드: {}", indexName,
                    e.status(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    String.format("지식용 인덱스 생성 실패: HTTP %d - %s", e.status(), e.getMessage()));
        } catch (RuntimeException e) {
            log.error("[ExternalKnowledge] 지식용 인덱스 생성 실패 (RuntimeException) - indexName: {}", indexName, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "지식용 인덱스 생성 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("[ExternalKnowledge] 지식용 인덱스 생성 실패 (Exception) - indexName: {}", indexName, e);
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "지식용 인덱스 생성 실패: " + e.getMessage());
        }
    }

    /**
     * Dataiku 실행
     *
     * @param request Dataiku 실행 입력 (knowledgeId, selectedDatasets 포함)
     * @return Dataiku 실행 결과 (응답 JSON 전체)
     */
    @Override
    @Transactional(readOnly = false)
    public DataikuExecutionResponse executeDataiku(DataikuExecutionRequest request) {

        Map<String, Object> requestBody = request.getBody();
        String knowledgeId = (String) requestBody.get("knowledgeId");

        try {
            log.info("🚀 [Dataiku 실행] 시작");

            // 1. 요청 데이터에서 지식 정보 추출
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> selectedDatasets = (List<Map<String, Object>>) requestBody
                    .get("selectedDatasets");

            log.info("📋 [Dataiku 실행] knowledgeId: {}", knowledgeId);
            log.info("📋 [Dataiku 실행] selectedDatasets count: {}",
                    selectedDatasets != null ? selectedDatasets.size() : 0);

            if (selectedDatasets == null || selectedDatasets.isEmpty()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE,
                        "선택된 데이터셋이 없습니다.");
            }

            // 2. 지식 정보 조회 (청킹, 임베딩 모델 등 설정값 획득)
            GpoKwlgInfoMas knowledgeInfo = gpoKwlgInfoMasRepository.findByExKwlgId(knowledgeId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                            "지식 정보를 찾을 수 없습니다. knowledgeId: " + knowledgeId));

            log.info("✅ [Dataiku 실행] 지식 정보 조회 완료 - knwNm: {}, idxNm: {}",
                    knowledgeInfo.getKwlgNm(), knowledgeInfo.getIdxNm());

            // 4. Dataiku 요청 파라미터 구성
            Map<String, Object> dataikuParams = buildDataikuParams(knowledgeInfo, selectedDatasets);

            log.info("📤 [Dataiku 실행] 최종 파라미터 구성 완료 - file_list size: {}",
                    selectedDatasets.size());

            // 5. Dataiku 실행
            DataikuExecutionRequest dataikuRequest = new DataikuExecutionRequest(dataikuParams);
            DataikuExecutionResponse response = udpDataikuService.executeDataiku(dataikuRequest);

            log.info("✅ [Dataiku 실행] 완료");

            // 6. DB 즉시 업데이트 (실행 직후 상태 반영)
            knowledgeInfo.setDataPipelineLoadStatus("running");
            knowledgeInfo.setDbLoadJinhgRt(java.math.BigDecimal.ZERO);
            knowledgeInfo.setIdxMkSttAt(java.time.LocalDateTime.now());

            // 6-1. dataset_cd 조합: origin_system_cd|dataset_cd 형식으로 연결하고, 항목들을 ,로 연결
            String datasetCd = buildDatasetCdString(selectedDatasets);
            if (datasetCd != null && !datasetCd.isEmpty()) {
                knowledgeInfo.setDataSetId(datasetCd);
                log.info("📋 [Dataiku 실행 후 DB 업데이트] dataset_cd 저장: {}", datasetCd);
            }

            gpoKwlgInfoMasRepository.save(knowledgeInfo);

            log.info("✅ [Dataiku 실행 후 DB 업데이트] knowledgeId: {}, status: running, progress: 0",
                    knowledgeId);

            return response;

        } catch (BusinessException e) {
            try {
                //  오류발생시
                GpoKwlgInfoMas knowledgeInfo = gpoKwlgInfoMasRepository.findByExKwlgId(knowledgeId).get();
                knowledgeInfo.setDataPipelineLoadStatus("error");
                gpoKwlgInfoMasRepository.save(knowledgeInfo);
                throw e;
            } catch (BusinessException e1) {
                throw e;
            } catch (FeignException e1) {
                throw e;
            } catch (RuntimeException e1) {
                throw e;
            } catch (Exception e1) {
                throw e;
            }

        } catch (FeignException e) {
            throw handleException("Dataiku 실행", e);
        } catch (RuntimeException e) {
            throw handleException("Dataiku 실행", e);
        } catch (Exception e) {
            throw handleException("Dataiku 실행", e);
        }
    }

    /**
     * Dataset CD 문자열 구성
     * origin_system_cd|dataset_cd 형식으로 각 항목을 연결하고, 항목들을 ,로 연결
     *
     * @param selectedDatasets 선택된 데이터셋 목록
     * @return 조합된 dataset_cd 문자열 (예: "SB|RGL,SB|WHE")
     */
    private String buildDatasetCdString(List<Map<String, Object>> selectedDatasets) {
        if (selectedDatasets == null || selectedDatasets.isEmpty()) {
            return null;
        }

        List<String> datasetCdList = new java.util.ArrayList<>();

        for (Map<String, Object> dataset : selectedDatasets) {
            // origin_system_cd 또는 originSystemCd 추출
            String originSystemCd = null;
            if (dataset.containsKey("origin_system_cd")) {
                originSystemCd = (String) dataset.get("origin_system_cd");
            } else if (dataset.containsKey("originSystemCd")) {
                originSystemCd = (String) dataset.get("originSystemCd");
            }

            // dataset_cd 또는 datasetCd 추출
            String datasetCd = null;
            if (dataset.containsKey("dataset_cd")) {
                datasetCd = (String) dataset.get("dataset_cd");
            } else if (dataset.containsKey("datasetCd")) {
                datasetCd = (String) dataset.get("datasetCd");
            }

            // 둘 다 있으면 |로 연결
            if (originSystemCd != null && !originSystemCd.isEmpty()
                    && datasetCd != null && !datasetCd.isEmpty()) {
                datasetCdList.add(originSystemCd + "|" + datasetCd);
            } else if (datasetCd != null && !datasetCd.isEmpty()) {
                // origin_system_cd가 없으면 dataset_cd만 사용
                datasetCdList.add(datasetCd);
            }
        }

        // 항목들을 ,로 연결
        if (datasetCdList.isEmpty()) {
            return null;
        }

        return String.join(",", datasetCdList);
    }

    /**
     * Dataiku 요청 파라미터 구성
     *
     * @param knowledgeInfo 지식 정보
     * @param filesList     파일 목록
     * @return Dataiku 요청 파라미터
     */
    private Map<String, Object> buildDataikuParams(GpoKwlgInfoMas knowledgeInfo, List<Map<String, Object>> filesList) {
        Map<String, Object> params = new HashMap<>();

        // 지식명 추가
        if (!Objects.isNull(knowledgeInfo)) {
            params.put("knowledge_name", knowledgeInfo.getKwlgNm());
        }

        // 임베딩 모델 이름 조회 (Model Catalog에서)
        String embeddingModelName = "kt"; // 기본값
        if (knowledgeInfo.getModelId() != null && !knowledgeInfo.getModelId().isEmpty()) {
            try {
                embeddingModelName = knowledgeInfo.getModelId();
                log.info("📋 [Dataiku 파라미터] 임베딩 모델 조회 성공 - modelId: {}, name: {}",
                        knowledgeInfo.getModelId(), embeddingModelName);
            } catch (BusinessException e) {
                log.warn("⚠️ [Dataiku 파라미터] 임베딩 모델 조회 실패 (BusinessException) - modelId: {}, 기본값 사용: {}, 오류: {}",
                        knowledgeInfo.getModelId(), embeddingModelName, e.getMessage());
            } catch (FeignException e) {
                log.warn("⚠️ [Dataiku 파라미터] 임베딩 모델 조회 실패 (FeignException) - modelId: {}, 상태코드: {}, 기본값 사용: {}, 오류: {}",
                        knowledgeInfo.getModelId(), e.status(), embeddingModelName, e.getMessage());
            } catch (RuntimeException e) {
                log.warn("⚠️ [Dataiku 파라미터] 임베딩 모델 조회 실패 (RuntimeException) - modelId: {}, 기본값 사용: {}, 오류: {}",
                        knowledgeInfo.getModelId(), embeddingModelName, e.getMessage());
            } catch (Exception e) {
                log.warn("⚠️ [Dataiku 파라미터] 임베딩 모델 조회 실패 (Exception) - modelId: {}, 기본값 사용: {}, 오류: {}",
                        knowledgeInfo.getModelId(), embeddingModelName, e.getMessage());
            }
        }

        // 청킹 알고리즘 파라미터 조회
        String prmtCtnt = knowledgeInfo.getPrmtCtnt() != null ? knowledgeInfo.getPrmtCtnt() : null;
        // String prmtCtnt = null;
        if (knowledgeInfo.getChunkId() != null && !knowledgeInfo.getChunkId().isEmpty()
                && knowledgeInfo.getPrmtCtnt() == null) {
            // if (knowledgeInfo.getChunkId() != null &&
            // !knowledgeInfo.getChunkId().isEmpty()) {
            try {
                java.util.Optional<com.skax.aiplatform.entity.knowledge.GpoChunkAlgoMas> chunkAlgoOpt = gpoChunkAlgoMasRepository
                        .findById(knowledgeInfo.getChunkId());
                if (chunkAlgoOpt.isPresent()) {
                    prmtCtnt = chunkAlgoOpt.get().getPrmtCtnt();
                    log.info("📋 [Dataiku 파라미터] 청킹 알고리즘 파라미터 조회 성공 - chunkId: {}, prmtCtnt: {}",
                            knowledgeInfo.getChunkId(), prmtCtnt);
                } else {
                    log.warn("⚠️ [Dataiku 파라미터] 청킹 알고리즘 정보 없음 - chunkId: {}",
                            knowledgeInfo.getChunkId());
                }
            } catch (BusinessException e) {
                log.warn("⚠️ [Dataiku 파라미터] 청킹 알고리즘 조회 실패 (BusinessException) - chunkId: {}, 오류: {}",
                        knowledgeInfo.getChunkId(), e.getMessage());
            } catch (FeignException e) {
                log.warn("⚠️ [Dataiku 파라미터] 청킹 알고리즘 조회 실패 (FeignException) - chunkId: {}, 상태코드: {}, 오류: {}",
                        knowledgeInfo.getChunkId(), e.status(), e.getMessage());
            } catch (RuntimeException e) {
                log.warn("⚠️ [Dataiku 파라미터] 청킹 알고리즘 조회 실패 (RuntimeException) - chunkId: {}, 오류: {}",
                        knowledgeInfo.getChunkId(), e.getMessage());
            } catch (Exception e) {
                log.warn("⚠️ [Dataiku 파라미터] 청킹 알고리즘 조회 실패 (Exception) - chunkId: {}, 오류: {}",
                        knowledgeInfo.getChunkId(), e.getMessage());
            }
        }

        // 현재 사용자 사번 조회
        String currentMemberId = getCurrentUser();
        log.debug("📋 [Dataiku 파라미터] 현재 사용자 사번: {}", currentMemberId);

        // 메인 파라미터 설정
        params.put("chunking", knowledgeInfo.getChunkId() != null ? knowledgeInfo.getChunkId() : "kss");
        params.put("embedding_model", embeddingModelName);
        params.put("index_nm", knowledgeInfo.getIdxNm());
        params.put("user_id", currentMemberId);
        params.put("chunk_created_by", knowledgeInfo.getCreatedBy() != null ? knowledgeInfo.getCreatedBy() : "admin");
        params.put("chunk_updated_by", knowledgeInfo.getUpdatedBy() != null ? knowledgeInfo.getUpdatedBy() : "admin");
        params.put("file_list", filesList);

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

        log.info(
                "📋 [Dataiku 파라미터] chunking: {}, embedding_model: {}, index_nm: {}, prmt_ctnt: {}, dvlp_synch_yn: {}, unyung_synch_yn: {}, chunk_created_by: {}, chunk_updated_by: {}",
                params.get("chunking"), params.get("embedding_model"), params.get("index_nm"),
                params.get("prmt_ctnt") != null ? "있음" : "없음",
                params.get("dvlp_synch_yn"), params.get("unyung_synch_yn"),
                params.get("chunk_created_by"), params.get("chunk_updated_by"));

        return params;
    }

    /**
     * 파일 목록 조회 (지식 데이터)
     *
     * @param request 파일 목록 조회 요청 DTO (인덱스명, 페이지, 페이지크기)
     * @return 파일 목록 페이징 응답 DTO
     */
    @Override
    public ExternalKnowledgeFilesRes getFiles(
            ExternalKnowledgeFilesReq request) {
        log.info(">>> [UDP Elasticsearch] 파일 목록 조회 (페이징) - request: {}", request);
        try {
            return udpElasticsearchService.searchFilesAggregated(request);
        } catch (BusinessException e) {
            throw handleException("Elasticsearch 파일 목록 조회", e);
        } catch (FeignException e) {
            throw handleException("Elasticsearch 파일 목록 조회", e);
        } catch (RuntimeException e) {
            throw handleException("Elasticsearch 파일 목록 조회", e);
        } catch (Exception e) {
            throw handleException("Elasticsearch 파일 목록 조회", e);
        }
    }

    /**
     * 특정 파일의 청크 목록 조회
     *
     * @param request 파일별 청크 조회 요청 DTO (인덱스명, 파일명, 페이지, 페이지크기)
     * @return 파일 청크 페이징 응답 DTO
     */
    @Override
    public ExternalKnowledgeChunksRes getFileChunks(
            ExternalKnowledgeChunksReq request) {
        log.info(">>> [UDP Elasticsearch] 파일별 청크 조회 (페이징) - request: {}", request);
        try {
            return udpElasticsearchService.searchChunksByFile(request);
        } catch (BusinessException e) {
            throw handleException("Elasticsearch 파일별 청크 조회", e);
        } catch (FeignException e) {
            throw handleException("Elasticsearch 파일별 청크 조회", e);
        } catch (RuntimeException e) {
            throw handleException("Elasticsearch 파일별 청크 조회", e);
        } catch (Exception e) {
            throw handleException("Elasticsearch 파일별 청크 조회", e);
        }
    }

    @Override
    public List<PolicyRequest> setKnowledgePolicy(String knowledgeId, String memberId, String projectName) {
        log.info("지식 Policy 설정 요청 - knowledgeId: {}, memberId: {}, projectName: {}", knowledgeId, memberId,
                projectName);

        // fewShotUuid 검증
        if (!StringUtils.hasText(knowledgeId)) {
            log.error("지식 Policy 설정 실패 - knowledgeId null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "Few-Shot UUID는 필수입니다");
        }

        // memberId 검증
        if (!StringUtils.hasText(memberId)) {
            log.error("지식 Policy 설정 실패 - memberId가 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "사용자 ID는 필수입니다");
        }

        // projectName 검증
        if (!StringUtils.hasText(projectName)) {
            log.error("지식 Policy 설정 실패 - projectName이 null이거나 비어있음");
            throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "프로젝트명은 필수입니다");
        }

        try {
            // Policy 설정
            adminAuthService.setResourcePolicyByMemberIdAndProjectName("/api/v1/knowledge/repos/" + knowledgeId,
                    memberId, projectName);

            log.info("지식 Policy 설정 완료 - knowledgeId: {}, memberId: {}, projectName: {}", knowledgeId, memberId,
                    projectName);

            // 설정된 Policy 조회
            List<PolicyRequest> policy = sktaiAuthService.getPolicy("/api/v1/knowledge/repos/" + knowledgeId);

            // policies에 type이 "role"인 항목이 하나라도 있는 PolicyRequest 객체는 policy 리스트에서 제외
            List<PolicyRequest> filteredPolicy = policy.stream()
                    .filter(policyReq -> {
                        if (policyReq.getPolicies() != null) {
                            // policies에 type이 "role"인 항목이 있는지 확인
                            return policyReq.getPolicies().stream()
                                    .noneMatch(p -> "role".equals(p.getType()));
                        }
                        return true; // policies가 null이면 포함
                    })
                    .collect(Collectors.toList());

            log.info("지식 Policy 설정 완료 - knowledgeId: {}, policy 개수: {} (필터링 전: {}, 필터링 후: {})", knowledgeId,
                    filteredPolicy.size(), policy.size(), filteredPolicy.size());
            return filteredPolicy;

        } catch (BusinessException e) {
            log.error("지식 Policy 설정 실패 (BusinessException) - knowledgeId: {}, errorCode: {}", knowledgeId,
                    e.getErrorCode(), e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "지식 Policy 설정에 실패했습니다: " + e.getMessage());
        } catch (RuntimeException e) {
            log.error("지식 Policy 설정 실패 (RuntimeException) - knowledgeId: {}, error: {}", knowledgeId, e.getMessage(),
                    e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "지식 Policy 설정에 실패했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("지식 Policy 설정 실패 (Exception) - knowledgeId: {}", knowledgeId, e);
            throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "지식 Policy 설정에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * External Knowledge 상세 조회 V2 (External Repo ID 기반)
     *
     * <p>
     * External Repo ID(exKwlgId)를 기반으로 External Knowledge의 상세 정보를 조회합니다.
     * 먼저 DB에서 조회하고, 없으면 ADXP에서 조회합니다.
     * </p>
     *
     * @param externalRepoId External Knowledge Repository ID (exKwlgId)
     * @return External Knowledge 상세 정보
     * @throws BusinessException 지식 정보를 찾을 수 없거나 외부 서비스 오류 발생 시
     */
    @Override
    public Object getExternalKnowledgeByExternalKnowledgeId(String externalRepoId) {
        log.info("🔍 External Knowledge 상세 조회 V2 시작 - externalRepoId: {}", externalRepoId);

        try {
            // 1️⃣ DB에서 지식 정보 조회 (exKwlgId로 조회)
            java.util.Optional<GpoKwlgInfoMas> knowledgeInfoOpt = gpoKwlgInfoMasRepository
                    .findByExKwlgId(externalRepoId);

            java.util.Map<String, Object> response = new java.util.HashMap<>();

            if (knowledgeInfoOpt.isPresent()) {
                // 2️⃣ DB에 있는 경우 - 기본지식
                GpoKwlgInfoMas knowledgeInfo = knowledgeInfoOpt.get();
                log.info("✅ DB 조회 성공 - kwlgId: {}, exKwlgId: {}",
                        knowledgeInfo.getKwlgId(), knowledgeInfo.getExKwlgId());

                response.put("knwId", knowledgeInfo.getKwlgId());
                response.put("name", knowledgeInfo.getKwlgNm());
                response.put("embedding_model_name", knowledgeInfo.getModelId()); // 사실은 Model Name 임
                response.put("is_custom_knowledge", false); // 기본지식

                log.info("🎉 External Knowledge 상세 조회 V2 완료 (기본지식) - externalRepoId: {}", externalRepoId);
                return response;

            } else {
                // 3️⃣ DB에 없는 경우 - 사용자 정의 지식 (ADXP에만 있음)
                log.info("📋 DB에 없음, ADXP에서 조회 시도 - externalRepoId: {}", externalRepoId);

                // ADXP 상세 조회 API 호출
                Object repoDetailObj = sktaiExternalReposService.getExternalRepo(externalRepoId);

                if (repoDetailObj == null) {
                    throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                            "지식 정보를 찾을 수 없습니다: " + externalRepoId);
                }

                log.info("📋 ADXP 조회 성공 - externalRepoId: {}", externalRepoId);

                // RepoResponse 타입이지만 실제로는 External API 응답이므로 Map으로 처리
                // SKTAI API가 반환하는 필드를 직접 사용
                // 사용자 정의 지식인 경우 is_custom_knowledge를 true로 설정
                if (repoDetailObj instanceof java.util.Map) {
                    @SuppressWarnings("unchecked")
                    java.util.Map<String, Object> repoDetailMap = (java.util.Map<String, Object>) repoDetailObj;
                    repoDetailMap.put("is_custom_knowledge", true); // 사용자 정의 지식
                }

                log.info("🎉 External Knowledge 상세 조회 V2 완료 (사용자 정의 지식) - externalRepoId: {}", externalRepoId);
                return repoDetailObj;
            }

        } catch (BusinessException e) {
            // 403 에러(EXTERNAL_API_FORBIDDEN)는 데이터 없음으로 처리
            if (e.getErrorCode() == ErrorCode.EXTERNAL_API_FORBIDDEN) {
                log.debug("External Knowledge 조회 - 403 에러 발생 (데이터 없음으로 처리) - id: {}", externalRepoId);
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "지식 정보를 찾을 수 없습니다: " + externalRepoId);
            }
            throw e;
        } catch (FeignException e) {
            throw handleException("External Knowledge 상세 조회 V2", e);
        } catch (RuntimeException e) {
            throw handleException("External Knowledge 상세 조회 V2", e);
        } catch (Exception e) {
            throw handleException("External Knowledge 상세 조회 V2", e);
        }
    }

    /**
     * 임베딩 모델 목록 조회
     *
     * <p>
     * 지식 생성 시 사용할 임베딩 모델 목록을 조회합니다.
     * ModelServingService를 통해 임베딩 모델 목록을 조회하고,
     * selectAll 옵션이 없는 경우 gpo_model_embedding_mas 테이블에 등록된 모델만 필터링합니다.
     * </p>
     *
     * @param request 페이지 및 필터 정보
     * @return 임베딩 모델 목록 (type:embedding 필터 적용)
     */
    @Override
    public com.skax.aiplatform.common.response.PageResponse<com.skax.aiplatform.client.sktai.serving.dto.response.ServingResponse> getEmbeddingModels(
            com.skax.aiplatform.dto.model.request.GetModelServingReq request) {
        log.info("📋 임베딩 모델 목록 조회 요청 - page: {}, size: {}, filter: {}",
                request.getPage(), request.getSize(), request.getFilter());

        try {
            // 1️⃣ filter에 type:embedding 추가 또는 설정
            String filter = request.getFilter();
            if (filter == null || filter.isBlank()) {
                filter = "type:embedding";
            } else if (!filter.contains("type:embedding")) {
                // 기존 filter가 있으면 type:embedding과 AND 조건으로 결합
                filter = filter + " AND type:embedding";
            }
            request.setFilter(filter);

            // 2️⃣ filter에 "selectAll"이 포함되어 있는지 확인
            boolean selectAll = filter != null && (filter.contains("selectAll") || filter.contains("select_all"));
            log.debug("📋 selectAll 옵션: {}", selectAll);

            // 3️⃣ ModelServingService를 통해 임베딩 모델 목록 조회
            com.skax.aiplatform.common.response.PageResponse<com.skax.aiplatform.client.sktai.serving.dto.response.ServingResponse> response = modelServingService
                    .getServingModels(request);

            log.info("📋 ModelServingService 조회 완료 - 총 {}건",
                    response.getContent() != null ? response.getContent().size() : 0);

            // 4️⃣ selectAll이 아닐 때만 gpo_model_embedding_mas 테이블에 등록된 모델만 필터링
            if (!selectAll && response.getContent() != null && !response.getContent().isEmpty()) {
                try {
                    // DB에서 모든 등록된 모델명을 한 번에 조회 (N+1 문제 방지)
                    Set<String> registeredModelNames = gpoModelEmbeddingMasRepository.findAll().stream()
                            .map(com.skax.aiplatform.entity.model.GpoModelEmbeddingMas::getModelNm)
                            .filter(modelNm -> modelNm != null && !modelNm.isBlank())
                            .collect(java.util.stream.Collectors.toSet());

                    log.debug("📋 DB에 등록된 임베딩 모델 수: {}", registeredModelNames.size());

                    // 메모리의 Set에서 확인하여 필터링 (DB 호출 없음)
                    List<com.skax.aiplatform.client.sktai.serving.dto.response.ServingResponse> filteredContent = response
                            .getContent().stream()
                            .filter(model -> {
                                if (model.getName() == null || model.getName().isBlank()) {
                                    return false;
                                }
                                // 메모리에 있는 Set에서 확인 (DB 호출 없음)
                                return registeredModelNames.contains(model.getName());
                            })
                            .collect(java.util.stream.Collectors.toList());

                    log.debug("📋 필터링 후 임베딩 모델 수: {} (원본: {})",
                            filteredContent.size(), response.getContent().size());

                    // 필터링된 결과로 새로운 PageResponse 생성
                    org.springframework.data.domain.Page<com.skax.aiplatform.client.sktai.serving.dto.response.ServingResponse> filteredPage = new org.springframework.data.domain.PageImpl<>(
                            filteredContent,
                            org.springframework.data.domain.PageRequest.of(
                                    response.getPageable() != null ? response.getPageable().getPage() : 0,
                                    response.getPageable() != null ? response.getPageable().getSize()
                                            : filteredContent.size()),
                            filteredContent.size());
                    response = com.skax.aiplatform.common.response.PageResponse.from(filteredPage);

                } catch (BusinessException e) {
                    log.error("❌ 임베딩 모델 필터링 중 BusinessException 발생 - 오류: {}", e.getMessage(), e);
                    // 필터링 실패해도 전체 모델 반환 (기본 동작)
                    log.warn("⚠️ 필터링 실패했지만 전체 모델 반환 - 원본 데이터 반환");
                } catch (RuntimeException e) {
                    log.error("❌ 임베딩 모델 필터링 중 RuntimeException 발생 - 오류: {}", e.getMessage(), e);
                    // 필터링 실패해도 전체 모델 반환 (기본 동작)
                    log.warn("⚠️ 필터링 실패했지만 전체 모델 반환 - 원본 데이터 반환");
                } catch (Exception e) {
                    log.error("❌ 임베딩 모델 필터링 중 예외 발생 - 오류: {}", e.getMessage(), e);
                    // 필터링 실패해도 전체 모델 반환 (기본 동작)
                    log.warn("⚠️ 필터링 실패했지만 전체 모델 반환 - 원본 데이터 반환");
                }
            }

            log.info("✅ 임베딩 모델 목록 조회 완료 - 총 {}건 ({})",
                    response.getContent() != null ? response.getContent().size() : 0,
                    selectAll ? "전체 모델" : "DB 등록 모델만 필터링");

            return response;

        } catch (BusinessException e) {
            // 403 에러(EXTERNAL_API_FORBIDDEN)는 데이터 없음으로 처리
            if (e.getErrorCode() == ErrorCode.EXTERNAL_API_FORBIDDEN) {
                log.debug("임베딩 모델 목록 조회 - 403 에러 발생 (데이터 없음으로 처리) ");
                throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND,
                        "임베딩 모델을 찾을 수 없습니다: ");
            }
            throw e;
        } catch (FeignException e) {
            throw handleException("임베딩 모델 목록 조회", e);
        } catch (RuntimeException e) {
            throw handleException("임베딩 모델 목록 조회", e);
        } catch (Exception e) {
            throw handleException("임베딩 모델 목록 조회", e);
        }
    }

    /**
     * 현재 사용자 정보 조회
     *
     * <p>
     * SecurityContext에서 현재 인증된 사용자의 사번을 조회합니다.
     * </p>
     *
     * @return 현재 사용자 사번 (인증되지 않은 경우 "admin" 반환)
     */
    private String getCurrentUser() {
        try {
            org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated() &&
                    !"anonymousUser".equals(authentication.getName())) {
                String memberId = authentication.getName();
                log.debug("📋 현재 사용자 사번 조회 성공: {}", memberId);
                return memberId;
            }
        } catch (SecurityException e) {
            log.warn("⚠️ 현재 사용자 정보를 가져올 수 없습니다 (SecurityException): {}", e.getMessage());
        } catch (RuntimeException e) {
            log.warn("⚠️ 현재 사용자 정보를 가져올 수 없습니다 (RuntimeException): {}", e.getMessage());
        } catch (Exception e) {
            log.warn("⚠️ 현재 사용자 정보를 가져올 수 없습니다 (Exception): {}", e.getMessage());
        }

        log.debug("📋 현재 사용자 정보 없음 - 기본값 'admin' 사용");
        return "admin"; // 기본값
    }
}
