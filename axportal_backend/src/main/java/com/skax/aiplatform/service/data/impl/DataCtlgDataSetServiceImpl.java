package com.skax.aiplatform.service.data.impl;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.skax.aiplatform.batch.BucketDeleteBatch;
import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.client.sktai.auth.dto.response.ClientRead;
import com.skax.aiplatform.client.sktai.auth.dto.response.ClientsRead;
import com.skax.aiplatform.client.sktai.auth.dto.response.MeResponse;
import com.skax.aiplatform.client.sktai.auth.service.SktaiAuthService;
import com.skax.aiplatform.client.sktai.auth.service.SktaiProjectService;
import com.skax.aiplatform.client.sktai.data.dto.request.DataSetUpdate;
import com.skax.aiplatform.client.sktai.data.dto.request.DataSourceS3Config;
import com.skax.aiplatform.client.sktai.data.dto.request.DatasetCreate;
import com.skax.aiplatform.client.sktai.data.dto.request.DatasourceCreate;
import com.skax.aiplatform.client.sktai.data.dto.request.ProcessorParam;
import com.skax.aiplatform.client.sktai.data.dto.response.DataSetDetail;
import com.skax.aiplatform.client.sktai.data.dto.response.DataSetList;
import com.skax.aiplatform.client.sktai.data.dto.response.DataSourceCreateResponse;
import com.skax.aiplatform.client.sktai.data.dto.response.DatasetCreateResponse;
import com.skax.aiplatform.client.sktai.data.dto.response.DatasetTag;
import com.skax.aiplatform.client.sktai.data.dto.response.DatasetTaskResponse;
import com.skax.aiplatform.client.sktai.data.dto.response.DatasetUpdateResponse;
import com.skax.aiplatform.client.sktai.data.dto.response.DatasourceDetail;
import com.skax.aiplatform.client.sktai.data.dto.response.DatasourceFileList;
import com.skax.aiplatform.client.sktai.data.service.SktaiDataDatasetsService;
import com.skax.aiplatform.client.sktai.data.service.SktaiDataDatasourcesService;
import com.skax.aiplatform.client.udp.elasticsearch.dto.response.IndexResponse;
import com.skax.aiplatform.client.udp.elasticsearch.service.UdpElasticsearchService;
import com.skax.aiplatform.common.config.S3Config;
import com.skax.aiplatform.common.exception.BusinessException;
import com.skax.aiplatform.common.exception.ErrorCode;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.common.util.MultipartFileHeaderChecker;
import com.skax.aiplatform.common.util.PaginationUtils;
import com.skax.aiplatform.common.util.S3Util;
import com.skax.aiplatform.common.util.TokenInfo;
import com.skax.aiplatform.dto.data.request.DataCtlgDataSetTag;
import com.skax.aiplatform.dto.data.request.DataCtlgDataSetUpdateReq;
import com.skax.aiplatform.dto.data.request.DataCtlgDataSourceFileDownloadReq;
import com.skax.aiplatform.dto.data.request.DataCtlgDatasetUploadReq;
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
import com.skax.aiplatform.dto.data.response.StepResult;
import com.skax.aiplatform.entity.mapping.GpoAssetPrjMapMas;
import com.skax.aiplatform.mapper.data.DataCtlgDataSetMapper;
import com.skax.aiplatform.repository.mapping.GpoAssetPrjMapMasRepository;
import com.skax.aiplatform.service.admin.AdminAuthService;
import com.skax.aiplatform.service.auth.UsersService;
import com.skax.aiplatform.service.data.DataCtlgDataSetService;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 데이터셋 서비스 구현체
 * 
 * <p>
 * 데이터셋 관련 비즈니스 로직을 구현하는 서비스 클래스입니다.
 * </p>
 * 
 * @author HyeleeLee
 * @since 2025-08-19
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DataCtlgDataSetServiceImpl implements DataCtlgDataSetService {

        private final SktaiDataDatasetsService sktaiDataDatasetsService;
        private final SktaiDataDatasourcesService sktaiDataDatasourcesService;
        private final DataCtlgDataSetMapper dataCtlgDataSetMapper;
        private final UdpElasticsearchService udpElasticsearchService;
        private final S3Util s3Util;
        private final TokenInfo tokenInfo;
        private final SktaiProjectService sktaiProjectService;
        private final S3Config s3Config;
        private final UsersService usersService;
        private final AdminAuthService adminAuthService;
        private final SktaiAuthService sktaiAuthService;
        private final ApplicationEventPublisher eventPublisher;

        private final GpoAssetPrjMapMasRepository assetPrjMapMasRepository;
        private final String FILE_UPLOAD_PATH = "shbdat/finetuning/tempfiles";
        private static final String ADMIN_USERNAME = "admin";

        /**
         * 공통 예외 처리 메서드
         *
         * @param operation 작업명
         * @param e         발생한 예외
         * @return 처리된 RuntimeException (BusinessException)
         */
        private RuntimeException handleException(String operation, Exception e) {
                if (e instanceof BusinessException) {
                        log.error("❌ Dataset {} 중 BusinessException 발생 - 오류: {}",
                                        operation, e.getMessage(), e);
                        return (BusinessException) e;
                } else if (e instanceof FeignException) {
                        FeignException feignEx = (FeignException) e;
                        log.error("❌ Dataset {} 중 FeignException 발생 - 상태코드: {}, 오류: {}, 응답본문: {}",
                                        operation, feignEx.status(), feignEx.getMessage(), feignEx.contentUTF8(),
                                        feignEx);
                        return new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                                        String.format("Dataset API 호출 중 오류가 발생했습니다: HTTP %d - %s", feignEx.status(),
                                                        feignEx.getMessage()));
                } else if (e instanceof RuntimeException) {
                        log.error("❌ Dataset {} 중 런타임 오류 발생 - 오류: {}",
                                        operation, e.getMessage(), e);
                        return new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                                        "Dataset API 호출 중 오류가 발생했습니다: " + e.getMessage());
                } else {
                        log.error("❌ Dataset {} 중 예상치 못한 오류 발생 - 오류: {}",
                                        operation, e.getMessage(), e);
                        return new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                                        "Dataset 처리 중 오류가 발생했습니다: " + e.getMessage());
                }
        }

        /**
         * 데이터셋 목록 조회
         * 
         * @param page   페이지 번호
         * @param size   페이지 크기
         * @param sort   정렬 기준
         * @param filter 필터 조건
         * @param search 검색어
         * @return 데이터셋 목록
         */
        @Override
        public PageResponse<DataCtlgDataSetListRes> getDatasets(Integer page, Integer size, String sort, String filter,
                        String search) {
                log.info("데이터셋 목록 조회 요청: page={}, size={}, sort={}, filter={}, search={}",
                                page, size, sort, filter, search);

                try {
                        // SKT AI Service를 통한 API 호출
                        DataSetList sktaiResponse = sktaiDataDatasetsService.getDatasets(
                                        page,
                                        size,
                                        sort,
                                        filter,
                                        search);

                        log.info("SKT AI API 호출 성공 - 총 {}개 데이터셋 조회",
                                        sktaiResponse.getPayload().getPagination().getTotal());

                        log.info("sktaiResponse: {}", sktaiResponse);

                        // dataset 목록 변환
                        List<DataCtlgDataSetListRes> datasetList = sktaiResponse.getData().stream()
                                        .map(dataCtlgDataSetMapper::toDatasetListRes)
                                        .collect(Collectors.toList());
                        for (DataCtlgDataSetListRes dataset : datasetList) {
                                // 공개 여부 설정 값 가져오기
                                GpoAssetPrjMapMas existing = assetPrjMapMasRepository
                                                .findByAsstUrl("/datasets/" + dataset.getId())
                                                .orElse(null);
                                String publicStatus = null;
                                if (existing != null && existing.getLstPrjSeq() != null) {
                                        // 음수면 "전체공유", 양수면 "내부공유"
                                        publicStatus = existing.getLstPrjSeq() < 0 ? "전체공유" : "내부공유";
                                } else {
                                        publicStatus = "전체공유"; // null 인 경우 전체공유로 설정
                                }
                                dataset.setPublicStatus(publicStatus);

                                // 최초 project seq, 최종 project seq 값 가져오기
                                int fstPrjSeq = -999;
                                int lstPrjSeq = -999;

                                if (existing != null) {
                                        fstPrjSeq = existing.getFstPrjSeq();
                                        lstPrjSeq = existing.getLstPrjSeq();
                                }

                                dataset.setFstPrjSeq(fstPrjSeq);
                                dataset.setLstPrjSeq(lstPrjSeq);

                        }

                        // 공개범위가 추가된 datasetList를 사용하여 PageResponse 생성
                        // PageResponse<DataCtlgDataSetListRes> pageDataSetListResponse =
                        // dataCtlgDataSetMapper
                        // .toPageResponseDataSetResWithContent(sktaiResponse, datasetList);
                        // log.info("pageDataSetListResponse: {}",
                        // pageDataSetListResponse.getContent());

                        PageResponse<DataCtlgDataSetListRes> pageDataSetListResponse = PaginationUtils
                                        .toPageResponseFromAdxp(sktaiResponse.getPayload(), datasetList);

                        return pageDataSetListResponse;

                } catch (BusinessException e) {
                        throw handleException("데이터셋 목록 조회", e);
                } catch (FeignException e) {
                        throw handleException("데이터셋 목록 조회", e);
                } catch (RuntimeException e) {
                        throw handleException("데이터셋 목록 조회", e);
                } catch (Exception e) {
                        throw handleException("데이터셋 목록 조회", e);
                }
        }

        /**
         * 데이터셋 상세 조회
         * 
         * @param datasetId 데이터셋 ID
         * @return 데이터셋 상세 정보
         */
        @Override
        public DataCtlgDataSetByIdRes getDatasetById(UUID datasetId) {
                log.info("데이터셋 상세 조회");
                log.info("데이터셋 상세 조회 요청: datasetId={}", datasetId);

                try {
                        // 데이터셋 정보 조회 (SktaiDataDatasetsService 사용)
                        DataSetDetail sktaiDatasetResponse = sktaiDataDatasetsService.getDataset(datasetId);
                        DataCtlgDataSetByIdRes datasetByIdResponse = dataCtlgDataSetMapper
                                        .toDatasetByIdRes(sktaiDatasetResponse);

                        // 공개 여부 설정 값 가져오기
                        GpoAssetPrjMapMas existing = assetPrjMapMasRepository
                                        .findByAsstUrl("/datasets/" + datasetByIdResponse.getId())
                                        .orElse(null);
                        String publicStatus = null;
                        if (existing != null && existing.getLstPrjSeq() != null) {
                                // 음수면 "전체공유", 양수면 "내부공유"
                                publicStatus = existing.getLstPrjSeq() < 0 ? "전체공유" : "내부공유";
                        } else {
                                publicStatus = "전체공유"; // null 인 경우 전체공유로 설정
                        }
                        datasetByIdResponse.setPublicStatus(publicStatus);

                        // 최초 project seq, 최종 project seq 값 가져오기
                        int fstPrjSeq = -999;
                        int lstPrjSeq = -999;

                        if (existing != null) {
                                fstPrjSeq = existing.getFstPrjSeq();
                                lstPrjSeq = existing.getLstPrjSeq();
                        }

                        datasetByIdResponse.setFstPrjSeq(fstPrjSeq);
                        datasetByIdResponse.setLstPrjSeq(lstPrjSeq);

                        log.info("데이터셋 정보 조회 완료 - datasetId: {}, datasetByIdResponse", datasetId,
                                        datasetByIdResponse);

                        return datasetByIdResponse;

                } catch (BusinessException e) {
                        throw handleException("데이터셋 상세 조회", e);
                } catch (FeignException e) {
                        throw handleException("데이터셋 상세 조회", e);
                } catch (RuntimeException e) {
                        throw handleException("데이터셋 상세 조회", e);
                } catch (Exception e) {
                        throw handleException("데이터셋 상세 조회", e);
                }
        }

        /**
         * 데이터셋 생성
         * 
         * @param request 데이터셋 생성 요청
         * @return 생성된 데이터셋 정보
         */
        // @Override
        // @Transactional
        // public DataCtlgDataSetCreateRes createDataset(DataCtlgDataSetCreateReq
        // request) {
        // log.info("데이터셋 생성 요청 데이터: {}", request);

        // try {
        // DatasetCreate sktaiRequest =
        // dataCtlgDataSetMapper.toSktaiCreateRequest(request);
        // DatasetCreateResponse sktaiResponse =
        // sktaiDataDatasetsService.createDataset(sktaiRequest);
        // log.info("SKTAI API 호출 성공 - 데이터셋 ID: {}", sktaiResponse.getId());
        // DataCtlgDataSetCreateRes response =
        // dataCtlgDataSetMapper.toDatasetCreateRes(sktaiResponse);

        // // Dataset ADXP 권한부여
        // adminAuthService.setResourcePolicyByCurrentGroup(
        // "/datasets/" + response.getId());
        // adminAuthService.setResourcePolicyByCurrentGroup(
        // "/datasets/" + response.getId() + "/tags");

        // log.info("데이터셋 생성 성공 - datasetId: {}", response.getId());
        // return response;

        // } catch (BusinessException e) {
        // throw handleException("데이터셋 생성", e);
        // } catch (FeignException e) {
        // throw handleException("데이터셋 생성", e);
        // } catch (RuntimeException e) {
        // throw handleException("데이터셋 생성", e);
        // } catch (Exception e) {
        // throw handleException("데이터셋 생성", e);
        // }
        // }

        /**
         * 데이터셋 수정
         * 
         * @param request 데이터셋 수정 요청
         * @return 수정된 데이터셋 정보
         */
        @Override
        public DataCtlgDataSetUpdateRes updateDataset(UUID datasetId, DataCtlgDataSetUpdateReq request) {
                log.info("데이터셋 수정 요청 - request: {}", request);

                try {
                        DataSetUpdate sktaiRequest = dataCtlgDataSetMapper.toSktaiUpdateRequest(request);
                        DatasetUpdateResponse sktaiResponse = (DatasetUpdateResponse) sktaiDataDatasetsService
                                        .updateDataset(datasetId, sktaiRequest);

                        DataCtlgDataSetUpdateRes response = dataCtlgDataSetMapper.toUpdateResponse(sktaiResponse);
                        log.info("데이터셋 수정 성공 - datasetId: {}", datasetId);
                        return response;

                } catch (BusinessException e) {
                        throw handleException("데이터셋 수정", e);
                } catch (FeignException e) {
                        throw handleException("데이터셋 수정", e);
                } catch (RuntimeException e) {
                        throw handleException("데이터셋 수정", e);
                } catch (Exception e) {
                        throw handleException("데이터셋 수정", e);
                }
        }

        /**
         * 데이터셋 삭제 & 데이터 소스 삭제
         * 
         * <p>
         * 데이터셋 삭제 시 데이터소스 삭제 후 데이터셋 하드 삭제
         * </p>
         * 
         * <p>
         * 데이터셋 하드 삭제 시 데이터소스 하드 삭제
         * </p>
         * 
         * @param datasetId    데이터셋 ID
         * @param datasourceId 데이터소스 ID
         */
        @Override
        @Transactional
        public void deleteDataSet(UUID datasetId, UUID datasourceId) {
                log.info("데이터셋 삭제 요청 - datasetId: {}", datasetId);

                try {
                        sktaiDataDatasetsService.deleteDataset(datasetId);
                        log.info("데이터셋 삭제 성공 - datasetId: {}", datasetId);

                        log.info("데이터소스 삭제 시작 - datasourceId: {}", datasourceId);
                        sktaiDataDatasourcesService.deleteDatasource(datasourceId);
                        log.info("데이터소스 삭제 성공 - datasourceId: {}", datasourceId);

                        log.info("데이터셋 하드 삭제 시작 - datasetId: {}", datasetId);
                        sktaiDataDatasetsService.hardDeleteAllDatasets();
                        log.info("데이터셋 하드 삭제 성공 - datasetId: {}", datasetId);

                        log.info("데이터소스 하드 삭제 시작 - datasourceId: {}", datasourceId);
                        sktaiDataDatasourcesService.hardDeleteAllDatasources();
                        log.info("데이터소스 하드 삭제 성공 - datasourceId: {}", datasourceId);

                } catch (BusinessException e) {
                        throw handleException("데이터셋/데이터소스 삭제", e);
                } catch (FeignException e) {
                        throw handleException("데이터셋/데이터소스 삭제", e);
                } catch (RuntimeException e) {
                        throw handleException("데이터셋/데이터소스 삭제", e);
                } catch (Exception e) {
                        throw handleException("데이터셋/데이터소스 삭제", e);
                }
        }

        /**
         * 커스텀 학습데이터셋 삭제
         * 
         * @param datasetId 데이터셋 ID
         */
        @Override
        @Transactional
        public void deleteCustomDataSet(UUID datasetId) {
                log.info("커스텀 학습데이터셋 삭제 요청 - datasetId: {}", datasetId);

                try {
                        sktaiDataDatasetsService.deleteDataset(datasetId);
                        log.info("커스텀 학습데이터셋 삭제 성공 - datasetId: {}", datasetId);
                        log.info("커스텀 학습데이터셋 하드 삭제 시작 - datasetId: {}", datasetId);
                        sktaiDataDatasetsService.hardDeleteAllDatasets();
                        log.info("커스텀 학습데이터셋 하드 삭제 성공 - datasetId: {}", datasetId);

                } catch (BusinessException e) {
                        throw handleException("커스텀 학습데이터셋 삭제", e);
                } catch (FeignException e) {
                        throw handleException("커스텀 학습데이터셋 삭제", e);
                } catch (RuntimeException e) {
                        throw handleException("커스텀 학습데이터셋 삭제", e);
                } catch (Exception e) {
                        throw handleException("커스텀 학습데이터셋 삭제", e);
                }
        }

        /**
         * 데이터소스 상세 조회
         * 
         * @param datasourceId 데이터소스 ID
         * @return 데이터소스 상세 정보
         */
        @Override
        public DataCtlgDataSourceByIdRes getDataSourceById(UUID datasourceId) {
                log.info("데이터소스 상세 조회");
                log.info("데이터소스 상세 조회 요청: datasourceId={}", datasourceId);

                try {

                        // 1. 데이터소스 정보 조회 (SktaiDataDatasetsService 사용)
                        DatasourceDetail sktaiDataSourceResponse = sktaiDataDatasourcesService
                                        .getDatasource(datasourceId);
                        DataCtlgDataSourceByIdRes dataSourceByIdResponse = dataCtlgDataSetMapper
                                        .toDatasourceByIdRes(sktaiDataSourceResponse);

                        log.info("데이터소스 정보 조회 완료 - datasourceId: {}, name: {}",
                                        datasourceId, dataSourceByIdResponse.getName());

                        log.debug(">>>>>>>>>dataSourceInfo: {}", dataSourceByIdResponse);
                        log.debug(">>>>>>>>dataSourceInfo.getFiles(): {}", dataSourceByIdResponse.getFiles());

                        log.info("데이터소스 상세 조회 완료 - datasourceId: {}, name: {}, ",
                                        datasourceId, dataSourceByIdResponse.getName());

                        return dataSourceByIdResponse;

                } catch (BusinessException e) {
                        throw handleException("데이터소스 상세 조회", e);
                } catch (FeignException e) {
                        throw handleException("데이터소스 상세 조회", e);
                } catch (RuntimeException e) {
                        throw handleException("데이터소스 상세 조회", e);
                } catch (Exception e) {
                        throw handleException("데이터소스 상세 조회", e);
                }
        }

        /**
         * 데이터소스 생성
         * 
         * @param request 데이터소스 생성 요청
         * @return 생성된 데이터소스 정보
         */
        // @Override
        // @Transactional
        // public DataCtlgDataSourceCreateRes
        // createDataSource(DataCtlgDataSourceCreateReq request) {
        // log.info("데이터소스 생성 요청 데이터: {}", request);

        // try {
        // DatasourceCreate sktaiRequest =
        // dataCtlgDataSetMapper.toSktaiDataSourceCreateRequest(request);

        // // 기본값 설정
        // if (sktaiRequest.getTempFiles() == null) {
        // // 기본 temp_files 설정
        // List<DatasourceCreate.TempFileDto> defaultTempFiles = new
        // java.util.ArrayList<>();
        // DatasourceCreate.TempFileDto tempFile =
        // DatasourceCreate.TempFileDto.builder()
        // .fileName("")
        // .tempFilePath("")
        // .fileMetadata(new java.util.HashMap<>())
        // .knowledgeConfig(new java.util.HashMap<>())
        // .build();
        // defaultTempFiles.add(tempFile);
        // sktaiRequest.setTempFiles(defaultTempFiles);
        // }

        // if (sktaiRequest.getPolicy() == null) {
        // // 기본 정책 설정
        // List<Object> defaultPolicy = new java.util.ArrayList<>();
        // Map<String, Object> policyItem = new java.util.HashMap<>();
        // policyItem.put("cascade", false);
        // policyItem.put("decision_strategy", "UNANIMOUS");
        // policyItem.put("logic", "POSITIVE");

        // List<Object> policies = new java.util.ArrayList<>();
        // Map<String, Object> policyDetail = new java.util.HashMap<>();
        // policyDetail.put("logic", "POSITIVE");
        // policyDetail.put("names", List.of("admin"));
        // policyDetail.put("type", "user");
        // policies.add(policyDetail);

        // policyItem.put("policies", policies);
        // policyItem.put("scopes", List.of("GET", "POST", "PUT", "DELETE"));

        // defaultPolicy.add(policyItem);
        // sktaiRequest.setPolicy(defaultPolicy);
        // }

        // log.info("SKT AI API 호출 전 - sktaiRequest: {}", sktaiRequest);
        // DataSourceCreateResponse sktaiResponse = sktaiDataDatasourcesService
        // .createDatasource(sktaiRequest);
        // log.info("SKT AI API 호출 성공 - 데이터 소스 ID: {}", sktaiResponse.getId());
        // DataCtlgDataSourceCreateRes response = dataCtlgDataSetMapper
        // .toDataSourceCreateRes(sktaiResponse);
        // log.info("데이터소스 생성 성공 - datasourceId: {}", response.getId());

        // // Dataset ADXP 권한부여
        // adminAuthService.setResourcePolicyByCurrentGroup(
        // "/datasources/" + response.getId());
        // adminAuthService.setResourcePolicyByCurrentGroup(
        // "/datasources/" + response.getId() + "/files");

        // return response;

        // } catch (BusinessException e) {
        // throw handleException("데이터소스 생성", e);
        // } catch (FeignException e) {
        // throw handleException("데이터소스 생성", e);
        // } catch (RuntimeException e) {
        // throw handleException("데이터소스 생성", e);
        // } catch (Exception e) {
        // throw handleException("데이터소스 생성", e);
        // }
        // }

        /**
         * 데이터셋 태그 수정
         * 
         * @param request 데이터셋 태그 수정
         * @return 데이터셋 상세 정보
         */

        @Override
        public DataCtlgDataSetUpdateRes updateDatasetTags(UUID datasetId, List<DataCtlgDataSetTag> tags) {
                log.info("데이터셋 태그 수정 요청 - datasetId: {}, tags: {}", datasetId, tags);

                try {
                        // 1. Controller 요청을 SKTAI 요청으로 변환
                        List<DatasetTag> sktaiTags = dataCtlgDataSetMapper.toSktaiTags(tags);

                        // 2. SKTAI API 호출
                        DatasetUpdateResponse sktaiResponse = sktaiDataDatasetsService.updateTags(datasetId, sktaiTags);

                        // 3. SKTAI 응답을 내부 응답으로 변환
                        DataCtlgDataSetUpdateRes response = dataCtlgDataSetMapper.toDatasetUpdateRes(sktaiResponse);

                        log.info("데이터셋 태그 수정 성공 - datasetId: {}, updatedTags: {}",
                                        datasetId, response.getTags());

                        return response;

                } catch (BusinessException e) {
                        throw handleException("데이터셋 태그 수정", e);
                } catch (FeignException e) {
                        throw handleException("데이터셋 태그 수정", e);
                } catch (RuntimeException e) {
                        throw handleException("데이터셋 태그 수정", e);
                } catch (Exception e) {
                        throw handleException("데이터셋 태그 수정", e);
                }
        }

        /**
         * 데이터셋 태그 삭제
         * 
         * @param request 데이터셋 태그 삭제
         * @return 데이터셋 상세 정보
         */

        @Override
        public DataCtlgDataSetUpdateRes deleteDatasetTag(UUID datasetId, List<DataCtlgDataSetTag> tags) {
                log.info("데이터셋 태그 삭제 요청 - datasetId: {}, tags: {}", datasetId, tags);

                try {
                        // 1. Controller 요청을 SKTAI 요청으로 변환
                        List<DatasetTag> sktaiTags = dataCtlgDataSetMapper.toSktaiTags(tags);

                        // 2. SKTAI API 호출
                        DatasetUpdateResponse sktaiResponse = sktaiDataDatasetsService.deleteTags(datasetId, sktaiTags);

                        // 3. SKTAI 응답을 내부 응답으로 변환
                        DataCtlgDataSetUpdateRes response = dataCtlgDataSetMapper.toDatasetUpdateRes(sktaiResponse);

                        log.info("데이터셋 태그 삭제 성공 - datasetId: {}, deletedTags: {}",
                                        datasetId, response.getTags());

                        return response;

                } catch (BusinessException e) {
                        throw handleException("데이터셋 태그 삭제", e);
                } catch (FeignException e) {
                        throw handleException("데이터셋 태그 삭제", e);
                } catch (RuntimeException e) {
                        throw handleException("데이터셋 태그 삭제", e);
                } catch (Exception e) {
                        throw handleException("데이터셋 태그 삭제", e);
                }
        }

        @Override
        public DataCtlgUploadFilesRes uploadDatasetFile(List<MultipartFile> files) {
                try {

                        // 데이터 인코딩 검증 포함 MultipartFileHeaderChecker.validate() 사용

                        for (MultipartFile file : files) {
                                MultipartFileHeaderChecker.FileCheckResult validationResult = MultipartFileHeaderChecker
                                                .validate(file, true);
                                if (validationResult == null || !validationResult.ok()) {
                                        String reason = validationResult != null ? validationResult.message()
                                                        : "파일 검증에 실패했습니다.";
                                        log.warn("데이터셋 파일 UTF-8 인코딩 검증 실패 - fileName: {}, reason: {}",
                                                        file.getOriginalFilename(),
                                                        reason);
                                        throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, reason);
                                }
                                log.debug("데이터셋 파일 UTF-8 인코딩 검증 완료 - fileName: {}, mime: {}, type: {}",
                                                file.getOriginalFilename(), validationResult.mimeType(),
                                                validationResult.fileType());
                        }

                        // 업스트림 응답: { "data": [ {file_name, temp_file_path, ...}, ... ] }

                        Map<String, Object> result = sktaiDataDatasourcesService.uploadFiles(files);

                        if (result == null || result.isEmpty()) {
                                return DataCtlgUploadFilesRes.builder().data(List.of()).build();
                        }

                        List<Map<String, Object>> dataList = extractDataList(result.get("data"));
                        if (dataList.isEmpty()) {
                                return DataCtlgUploadFilesRes.builder().data(List.of()).build();
                        }

                        List<DataCtlgUploadFilesRes.Item> items = dataList.stream()
                                        .map(m -> DataCtlgUploadFilesRes.Item.builder()
                                                        .fileName(Objects.toString(m.get("file_name"), null))
                                                        .tempFilePath(Objects.toString(m.get("temp_file_path"), ""))
                                                        .fileMetadata(castToMap(m.get("file_metadata")))
                                                        .knowledgeConfig(castToMap(m.get("knowledge_config")))
                                                        .build())
                                        .collect(Collectors.toList());

                        return DataCtlgUploadFilesRes.builder().data(items).build();

                } catch (BusinessException e) {
                        throw handleException("데이터 소스 > 데이터 업로드", e);
                } catch (FeignException e) {
                        throw handleException("데이터 소스 > 데이터 업로드", e);
                } catch (RuntimeException e) {
                        throw handleException("데이터 소스 > 데이터 업로드", e);
                } catch (Exception e) {
                        throw handleException("데이터 소스 > 데이터 업로드", e);
                }
        }

        /**
         * 데이터 소스 파일 목록 조회
         * 
         * @param datasourceId 데이터소스 ID
         * @param page         페이지 번호
         * @param size         페이지 크기
         * @return
         */
        @Override
        public PageResponse<DataCtlgDataSourceFileRes> getDataSourceFiles(String datasourceId, Integer page,
                        Integer size) {
                try {
                        log.info(">>> 데이터소스 파일 목록 조회 시작 - datasourceId: {}, page: {}, size: {}",
                                        datasourceId, page, size);

                        // SKT AI API 호출
                        DatasourceFileList sktaiResponse = sktaiDataDatasourcesService.listDatasourceFiles(
                                        datasourceId, page, size);

                        if (sktaiResponse == null) {
                                log.warn(">>> SKT AI API 응답이 null입니다 - datasourceId: {}", datasourceId);
                                return PageResponse.<DataCtlgDataSourceFileRes>builder()
                                                .content(List.of())
                                                .totalElements(0L)
                                                .totalPages(0)
                                                .first(true)
                                                .last(true)
                                                .hasNext(false)
                                                .hasPrevious(false)
                                                .build();
                        }

                        // 매퍼를 통해 PageResponse 형태로 변환
                        // PageResponse<DataCtlgDataSourceFileRes> response = dataCtlgDataSetMapper
                        // .toPageResponseDataSourceFileRes(sktaiResponse);

                        List<DataCtlgDataSourceFileRes> datasourceFileList = sktaiResponse.getData().stream()
                                        .map(dataCtlgDataSetMapper::toDataSourceFileRes)
                                        .collect(Collectors.toList());

                        PageResponse<DataCtlgDataSourceFileRes> response = PaginationUtils
                                        .toPageResponseFromAdxp(sktaiResponse.getPayload(),
                                                        datasourceFileList);
                        log.info(">>> 데이터소스 파일 목록 조회 완료 - datasourceId: {}, 총 파일 수: {}",
                                        datasourceId, response.getContent().size());

                        return response;

                } catch (BusinessException e) {
                        throw handleException("데이터소스 파일 목록 조회", e);
                } catch (FeignException e) {
                        throw handleException("데이터소스 파일 목록 조회", e);
                } catch (RuntimeException e) {
                        throw handleException("데이터소스 파일 목록 조회", e);
                } catch (Exception e) {
                        throw handleException("데이터소스 파일 목록 조회", e);
                }
        }

        @SuppressWarnings("unchecked")
        private List<Map<String, Object>> extractDataList(Object node) {
                if (node instanceof List<?> list) {
                        return list.stream()
                                        .filter(Map.class::isInstance)
                                        .map(m -> (Map<String, Object>) m)
                                        .collect(Collectors.toList());
                }
                if (node instanceof Map) {
                        return List.of((Map<String, Object>) node);
                }
                return List.of();
        }

        /**
         * 데이터 저장소 등록 (custom 타입 여부)
         *
         * @param dataId       데이터셋 ID
         * @param isCustomType 커스텀 타입 여부
         * @param request      파일 처리 요청
         * @return 파일 처리 결과
         */
        @Override
        public DataCtlgDataSourceUdpRegisterRes registerDataSetFileToOzone(String dataId, boolean isCustomType,
                        DataCtlgDataSourceFileDownloadReq request) {
                log.info(">>> 데이터 저장소 등록을 위한 파일 처리 시작 - dataId: {}", dataId);

                long startTime = System.currentTimeMillis();
                File tempFile = null;
                // 각 단계별 결과를 담을 객체들
                StepResult downloadStep = null;
                StepResult s3UploadStep = null;
                StepResult esMetaStep = null;

                try {
                        // 1. 임시 디렉토리 생성 및 파일 다운로드
                        long stepStartTime = System.currentTimeMillis();
                        try {
                                tempFile = downloadFileToTempStorage(dataId, isCustomType);
                                log.info(">>> 임시 파일 다운로드 완료 - dataId: {}, tempFile: {}", dataId,
                                                tempFile.getAbsolutePath());

                                Map<String, Object> downloadResult = new HashMap<>();
                                downloadResult.put("tempFilePath", tempFile.getAbsolutePath());
                                downloadResult.put("fileSize", tempFile.length());
                                downloadResult.put("fileName", tempFile.getName());

                                downloadStep = StepResult.builder()
                                                .success(true)
                                                .message("임시 파일 다운로드가 성공적으로 완료되었습니다")
                                                .result(downloadResult)
                                                .processingTimeMs(System.currentTimeMillis() - stepStartTime)
                                                .build();

                        } catch (RuntimeException e) {
                                log.error(">>> 임시 파일 다운로드 실패 (RuntimeException) - datasetId: {}", dataId, e);
                                Map<String, Object> error = new HashMap<>();
                                error.put("errorCode", "DOWNLOAD_FAILED");
                                error.put("errorMessage", e.getMessage());

                                downloadStep = StepResult.builder()
                                                .success(false)
                                                .message("임시 파일 다운로드에 실패했습니다")
                                                .error(error)
                                                .processingTimeMs(System.currentTimeMillis() - stepStartTime)
                                                .build();
                                throw e;
                        } catch (Exception e) {
                                log.error(">>> 임시 파일 다운로드 실패 (Exception) - datasetId: {}", dataId, e);
                                Map<String, Object> error = new HashMap<>();
                                error.put("errorCode", "DOWNLOAD_FAILED");
                                error.put("errorMessage", e.getMessage());

                                downloadStep = StepResult.builder()
                                                .success(false)
                                                .message("임시 파일 다운로드에 실패했습니다")
                                                .error(error)
                                                .processingTimeMs(System.currentTimeMillis() - stepStartTime)
                                                .build();
                                throw e;
                        }

                        // 2. S3 업로드 (요청된 경우에만)
                        if (Boolean.TRUE.equals(request.getUploadToS3())) {
                                stepStartTime = System.currentTimeMillis();
                                try {
                                        Map<String, Object> s3Result = uploadTempFileToS3(tempFile, dataId,
                                                        request.getTitle());
                                        log.info(">>> S3 업로드 완료 - dataId: {}, s3Key: {}",
                                                        dataId, s3Result.get("s3Key"));

                                        s3UploadStep = StepResult.builder()
                                                        .success(true)
                                                        .message("S3 업로드가 성공적으로 완료되었습니다")
                                                        .result(s3Result)
                                                        .processingTimeMs(System.currentTimeMillis() - stepStartTime)
                                                        .build();

                                } catch (RuntimeException e) {
                                        log.error(">>> S3 업로드 실패 (RuntimeException) - datasourceFileId: {}", dataId, e);
                                        Map<String, Object> error = new HashMap<>();
                                        error.put("errorCode", "S3_UPLOAD_FAILED");
                                        error.put("errorMessage", e.getMessage());

                                        s3UploadStep = StepResult.builder()
                                                        .success(false)
                                                        .message("S3 업로드에 실패했습니다")
                                                        .error(error)
                                                        .processingTimeMs(System.currentTimeMillis() - stepStartTime)
                                                        .build();
                                        throw e;
                                } catch (Exception e) {
                                        log.error(">>> S3 업로드 실패 (Exception) - datasourceFileId: {}", dataId, e);
                                        Map<String, Object> error = new HashMap<>();
                                        error.put("errorCode", "S3_UPLOAD_FAILED");
                                        error.put("errorMessage", e.getMessage());

                                        s3UploadStep = StepResult.builder()
                                                        .success(false)
                                                        .message("S3 업로드에 실패했습니다")
                                                        .error(error)
                                                        .processingTimeMs(System.currentTimeMillis() - stepStartTime)
                                                        .build();
                                        throw e;
                                }
                        } else {
                                s3UploadStep = StepResult.builder()
                                                .success(true)
                                                .message("S3 업로드가 요청되지 않아 건너뛰었습니다")
                                                .result(new HashMap<>())
                                                .processingTimeMs(0L)
                                                .build();
                        }

                        // 3. ES 메타 정보 저장 (요청된 경우에만)
                        if (Boolean.TRUE.equals(request.getSaveToEs())) {
                                stepStartTime = System.currentTimeMillis();
                                try {
                                        Map<String, Object> esResult = saveFileMetaToEs(
                                                        dataId,
                                                        s3UploadStep != null ? s3UploadStep.getResult() : null,
                                                        getCurrentUser(),
                                                        request.getDatasetCat01(),
                                                        request.getDatasetCat02(),
                                                        request.getDatasetCat03(),
                                                        request.getDatasetCat04(),
                                                        request.getDatasetCat05(),
                                                        request.getDescCtnt(),
                                                        request.getTitle(),
                                                        request.getTags(),
                                                        getCurrentUser());
                                        log.info(">>> ES 메타 정보 저장 완료 - dataId: {}", dataId);

                                        esMetaStep = StepResult.builder()
                                                        .success(true)
                                                        .message("ES 메타 정보 저장이 성공적으로 완료되었습니다")
                                                        .result(esResult)
                                                        .processingTimeMs(System.currentTimeMillis() - stepStartTime)
                                                        .build();

                                } catch (BusinessException e) {
                                        log.error(">>> ES 메타 정보 저장 실패 (BusinessException) - dataId: {}", dataId, e);
                                        Map<String, Object> error = new HashMap<>();
                                        error.put("errorCode", "ES_SAVE_FAILED");
                                        error.put("errorMessage", e.getMessage());

                                        esMetaStep = StepResult.builder()
                                                        .success(false)
                                                        .message("ES 메타 정보 저장에 실패했습니다")
                                                        .error(error)
                                                        .processingTimeMs(System.currentTimeMillis() - stepStartTime)
                                                        .build();
                                        throw e;
                                } catch (FeignException e) {
                                        log.error(">>> ES 메타 정보 저장 실패 (FeignException) - dataId: {}, 상태코드: {}", dataId,
                                                        e.status(), e);
                                        Map<String, Object> error = new HashMap<>();
                                        error.put("errorCode", "ES_SAVE_FAILED");
                                        error.put("errorMessage", e.getMessage());

                                        esMetaStep = StepResult.builder()
                                                        .success(false)
                                                        .message("ES 메타 정보 저장에 실패했습니다")
                                                        .error(error)
                                                        .processingTimeMs(System.currentTimeMillis() - stepStartTime)
                                                        .build();
                                        throw e;
                                } catch (RuntimeException e) {
                                        log.error(">>> ES 메타 정보 저장 실패 (RuntimeException) - dataId: {}", dataId, e);
                                        Map<String, Object> error = new HashMap<>();
                                        error.put("errorCode", "ES_SAVE_FAILED");
                                        error.put("errorMessage", e.getMessage());

                                        esMetaStep = StepResult.builder()
                                                        .success(false)
                                                        .message("ES 메타 정보 저장에 실패했습니다")
                                                        .error(error)
                                                        .processingTimeMs(System.currentTimeMillis() - stepStartTime)
                                                        .build();
                                        throw e;
                                } catch (Exception e) {
                                        log.error(">>> ES 메타 정보 저장 실패 (Exception) - dataId: {}", dataId, e);
                                        Map<String, Object> error = new HashMap<>();
                                        error.put("errorCode", "ES_SAVE_FAILED");
                                        error.put("errorMessage", e.getMessage());

                                        esMetaStep = StepResult.builder()
                                                        .success(false)
                                                        .message("ES 메타 정보 저장에 실패했습니다")
                                                        .error(error)
                                                        .processingTimeMs(System.currentTimeMillis() - stepStartTime)
                                                        .build();
                                        throw e;
                                }
                        } else {
                                esMetaStep = StepResult.builder()
                                                .success(true)
                                                .message("ES 메타 정보 저장이 요청되지 않아 건너뛰었습니다")
                                                .result(new HashMap<>())
                                                .processingTimeMs(0L)
                                                .build();
                        }

                        // 전체 성공 여부 확인
                        boolean overallSuccess = downloadStep.getSuccess() &&
                                        (s3UploadStep == null || s3UploadStep.getSuccess()) &&
                                        (esMetaStep == null || esMetaStep.getSuccess());

                        // 결과 반환
                        DataCtlgDataSourceUdpRegisterRes result = DataCtlgDataSourceUdpRegisterRes.builder()
                                        .success(overallSuccess)
                                        .message(overallSuccess ? "UDP 등록 파일 처리가 성공적으로 완료되었습니다" : "일부 단계에서 실패가 발생했습니다")
                                        .datasourceFileId(dataId)
                                        .udpRegistrationId(UUID.randomUUID().toString()) // 실제 구현에서는 적절한 ID 생성
                                        .processingTimeMs(System.currentTimeMillis() - startTime)
                                        .downloadStep(downloadStep)
                                        .s3UploadStep(s3UploadStep)
                                        .esMetaStep(esMetaStep)
                                        .build();

                        log.info(">>> UDP 등록 파일 처리 완료 - dataId: {}, overallSuccess: {}, fileSize: {} bytes",
                                        dataId, overallSuccess, tempFile != null ? tempFile.length() : 0);
                        return result;

                } catch (BusinessException e) {
                        log.error(">>> UDP 등록 파일 처리 실패 - dataId: {}, error: {}",
                                        dataId, e.getMessage());
                        throw e;
                } catch (FeignException e) {
                        log.error(">>> UDP 등록 파일 처리 중 FeignException 발생 - dataId: {}, 상태코드: {}", dataId, e.status(), e);
                        throw e;
                } catch (RuntimeException e) {
                        log.error(">>> UDP 등록 파일 처리 중 RuntimeException 발생 - dataId: {}", dataId, e);
                        throw e;
                } catch (Exception e) {
                        log.error(">>> UDP 등록 파일 처리 중 예외 발생 - dataId: {}", dataId, e);
                        throw e;
                } finally {
                        // 임시 파일 정리
                        if (tempFile != null && tempFile.exists()) {
                                cleanupTempFile(tempFile);
                        }
                }
        }

        public Map<String, Object> saveFileMetaToEs(
                        String datasourceFileId,
                        Map<String, Object> s3Result,
                        String createdBy,
                        String datasetCat01,
                        String datasetCat02,
                        String datasetCat03,
                        String datasetCat04,
                        String datasetCat05,
                        String descCtnt,
                        String title,
                        String tags,
                        String updatedBy) {

                log.info(">>> 파일 메타 정보 ES 저장 시작 - datasourceFileId: {}, title: {}", datasourceFileId, title);

                try {
                        // ES 저장용 문서 생성
                        Map<String, Object> document = buildFileMetaDocument(
                                        datasourceFileId,
                                        s3Result,
                                        createdBy,
                                        datasetCat01,
                                        datasetCat02,
                                        datasetCat03,
                                        datasetCat04,
                                        datasetCat05,
                                        descCtnt,
                                        title,
                                        tags,
                                        updatedBy);

                        // Elasticsearch에 데이터 삽입
                        IndexResponse response = udpElasticsearchService.insertData("gaf_datasets", document);

                        // 결과 반환
                        Map<String, Object> result = new HashMap<>();
                        result.put("success", true);
                        result.put("indexName", "gaf_datasets");
                        result.put("documentId", response.getId());
                        result.put("result", response.getResult());
                        result.put("version", response.getVersion());
                        result.put("timestamp", LocalDateTime.now().toString());

                        log.info(">>> 파일 메타 정보 ES 저장 완료 - datasourceFileId: {}, documentId: {}, result: {}",
                                        datasourceFileId, response.getId(), response.getResult());

                        return result;

                } catch (BusinessException e) {
                        log.error(">>> 파일 메타 정보 ES 저장 실패 (BusinessException) - datasourceFileId: {}", datasourceFileId,
                                        e);
                        throw e;
                } catch (FeignException e) {
                        log.error(">>> 파일 메타 정보 ES 저장 실패 (FeignException) - datasourceFileId: {}, 상태코드: {}",
                                        datasourceFileId, e.status(), e);
                        throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                                        String.format("파일 메타 정보 ES 저장 중 오류가 발생했습니다: HTTP %d - %s", e.status(),
                                                        e.getMessage()));
                } catch (RuntimeException e) {
                        log.error(">>> 파일 메타 정보 ES 저장 실패 (RuntimeException) - datasourceFileId: {}", datasourceFileId,
                                        e);
                        throw e;
                } catch (Exception e) {
                        log.error(">>> 파일 메타 정보 ES 저장 실패 (Exception) - datasourceFileId: {}", datasourceFileId, e);
                        throw e;
                }
        }

        /**
         * 파일 메타 정보 문서 생성 (ES 인덱스 스키마에 맞춤, 필드 순서 지정)
         */
        private Map<String, Object> buildFileMetaDocument(
                        String datasourceFileId,
                        Map<String, Object> s3Result,
                        String createdBy,
                        String datasetCat01,
                        String datasetCat02,
                        String datasetCat03,
                        String datasetCat04,
                        String datasetCat05,
                        String descCtnt,
                        String title,
                        String tags,
                        String updatedBy) {

                // LinkedHashMap을 사용하여 필드 순서 보장
                Map<String, Object> document = new LinkedHashMap<>();

                // 현재 시간 정보 (ES 스키마 형식에 맞춤)
                String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"));

                // 현재 사용자 정보 안전하게 가져오기
                String currentUser = getCurrentUser();

                // 1. CREATED_BY
                document.put("CREATED_BY", currentUser);

                // 2. DATASET_CAT01
                document.put("DATASET_CAT01", datasetCat01 != null ? datasetCat01 : "");

                // 3. DATASET_CAT02
                document.put("DATASET_CAT02", datasetCat02 != null ? datasetCat02 : "");

                // 4. DATASET_CAT03
                document.put("DATASET_CAT03", datasetCat03 != null ? datasetCat03 : "");

                // 5. DATASET_CAT04
                document.put("DATASET_CAT04", datasetCat04 != null ? datasetCat04 : "");

                // 6. DATASET_CAT05
                document.put("DATASET_CAT05", datasetCat05 != null ? datasetCat05 : "");

                // 7. DESC_CTNT
                document.put("DESC_CTNT", descCtnt != null ? descCtnt : "");

                // 8. FST_CREATED_AT (현재 시간)
                document.put("FST_CREATED_AT", currentTime);

                // 9. LST_UPDATED_AT (현재 시간)
                document.put("LST_UPDATED_AT", currentTime);

                // 10. OZONE_PATH
                if (s3Result != null && s3Result.get("s3Url") != null) {
                        document.put("OZONE_PATH", s3Result.get("s3Url"));
                } else {
                        document.put("OZONE_PATH", "");
                }

                // 11. TAGS
                document.put("TAGS", tags != null ? tags : "");

                // 12. TITLE
                document.put("TITLE", title != null ? title : "파일명");

                // 13. UPDATED_BY (현재 사용자)
                document.put("UPDATED_BY", currentUser);

                return document;
        }

        @SuppressWarnings("unchecked")
        private Map<String, Object> castToMap(Object obj) {
                return (obj instanceof Map) ? (Map<String, Object>) obj : Map.of();
        }

        /**
         * 학습 데이터 생성 (커스텀이 아닌 경우)
         * 
         * @param request 학습 데이터 생성 요청 (파일명 리스트 포함)
         * @return 학습 데이터 생성 결과
         */
        @Override
        @Transactional
        public DataCtlgTrainingDataCreateRes createTrainingDataNotCustom(
                        DataCtlgTrainingDatasetCreateFromFilesReq request) {
                return createTrainingDatasetFromFiles(request);
        }

        /**
         * 임시 디렉토리를 생성하고 검증합니다.
         *
         * @return 임시 디렉토리 Path
         */
        private Path createAndValidateTempDirectory() {
                Path tempDir = Paths.get(FILE_UPLOAD_PATH);
                log.info(">>> 임시 디렉토리 경로 결정: {}", tempDir.toAbsolutePath());

                // 디렉토리 존재 여부 및 권한 확인
                if (!Files.exists(tempDir)) {
                        try {
                                Files.createDirectories(tempDir);
                                log.info(">>> 임시 디렉토리 생성 완료: {}", tempDir.toAbsolutePath());
                        } catch (IOException e) {
                                log.error(">>> 임시 디렉토리 생성 실패: {}, error: {}", tempDir.toAbsolutePath(), e.getMessage(),
                                                e);
                                throw new BusinessException(
                                                ErrorCode.EXTERNAL_API_ERROR,
                                                "임시 디렉토리 생성에 실패했습니다: " + e.getMessage());
                        }
                } else {
                        // 디렉토리가 존재하지만 쓰기 권한이 있는지 확인
                        if (!Files.isWritable(tempDir)) {
                                log.error(">>> 임시 디렉토리 쓰기 권한 없음: {}", tempDir.toAbsolutePath());
                                throw new BusinessException(
                                                ErrorCode.EXTERNAL_API_ERROR,
                                                "임시 디렉토리에 쓰기 권한이 없습니다: " + tempDir.toAbsolutePath());
                        }
                        log.info(">>> 임시 디렉토리 확인 완료: {}", tempDir.toAbsolutePath());
                }
                return tempDir;
        }

        /**
         * 고유한 임시 파일을 생성합니다.
         * 
         * @param tempDir 임시 디렉토리
         * @param prefix  파일명 접두사
         * @return 생성된 임시 파일
         */
        private File createUniqueTempFile(Path tempDir, String prefix) {
                // 임시 파일 생성 (안전한 방식)
                String fileName = "temp_" + prefix + "_" + System.currentTimeMillis();
                File tempFile = new File(tempDir.toFile(), fileName);

                // 임시 파일이 이미 존재하는지 확인 (중복 방지)
                int counter = 1;
                while (tempFile.exists()) {
                        fileName = "temp_" + prefix + "_" + System.currentTimeMillis() + "_" + counter;
                        tempFile = new File(tempDir.toFile(), fileName);
                        counter++;
                        if (counter > 100) { // 무한 루프 방지
                                throw new BusinessException(
                                                ErrorCode.EXTERNAL_API_ERROR,
                                                "고유한 임시 파일명을 생성할 수 없습니다.");
                        }
                }

                log.info(">>> 임시 파일 생성 - path: {}", tempFile.getAbsolutePath());
                return tempFile;
        }

        /**
         * SKT AI API를 통해 파일을 다운로드하여 임시 저장소에 저장합니다.
         * 
         * @param datasourceFileId 데이터소스 파일 ID
         * @param isCustomType     커스텀 타입 여부
         * @return 다운로드된 임시 파일
         */
        private File downloadFileToTempStorage(String datasourceFileId, boolean isCustomType) {
                log.info(">>> 임시 파일 다운로드 시작 - datasourceFileId: {}", datasourceFileId);

                try {
                        // 임시 디렉토리 생성 및 검증
                        Path tempDir = createAndValidateTempDirectory();
                        Resource resource = null;

                        // 커스텀 타입일 때
                        if (isCustomType) {
                                // SKT AI API를 통한 데이터 소스 파일 다운로드
                                ResponseEntity<Resource> downloadResponse = sktaiDataDatasetsService
                                                .getDatasetSourceArchive(UUID.fromString(datasourceFileId));
                                resource = downloadResponse.getBody();
                                // 커스텀 타입이 아닐 때
                        } else {
                                // SKT AI API를 통한 데이터 소스 파일 다운로드
                                ResponseEntity<Resource> downloadResponse = sktaiDataDatasourcesService
                                                .downloadFile(datasourceFileId);
                                resource = downloadResponse.getBody();
                        }

                        if (resource == null || !resource.exists()) {
                                log.error(">>> 다운로드할 파일을 찾을 수 없음 - datasourceFileId: {}", datasourceFileId);
                                throw new IllegalArgumentException("다운로드할 파일을 찾을 수 없습니다: " + datasourceFileId);
                        }

                        // 고유한 임시 파일 생성
                        File tempFile = createUniqueTempFile(tempDir, datasourceFileId);

                        // 파일 다운로드 (스트리밍 방식)
                        try (InputStream inputStream = resource.getInputStream();
                                        FileOutputStream outputStream = new FileOutputStream(tempFile)) {

                                byte[] buffer = new byte[8192]; // 8KB 버퍼
                                int bytesRead;
                                long totalBytes = 0;

                                while ((bytesRead = inputStream.read(buffer)) != -1) {
                                        outputStream.write(buffer, 0, bytesRead);
                                        totalBytes += bytesRead;

                                        // 진행률 로깅 (10MB마다)
                                        if (totalBytes % (10 * 1024 * 1024) == 0) {
                                                log.info(">>> 다운로드 진행률 - datasourceFileId: {}, downloaded: {} MB",
                                                                datasourceFileId, totalBytes / (1024 * 1024));
                                        }
                                }

                                log.info(">>> 임시 파일 다운로드 완료 - datasourceFileId: {}, fileSize: {} bytes",
                                                datasourceFileId, totalBytes);
                        }

                        // 다운로드된 파일 검증
                        if (!tempFile.exists() || tempFile.length() == 0) {
                                log.error(">>> 다운로드된 파일이 존재하지 않거나 크기가 0 - path: {}", tempFile.getAbsolutePath());
                                throw new BusinessException(
                                                ErrorCode.EXTERNAL_API_ERROR,
                                                "파일 다운로드가 완료되지 않았습니다.");
                        }

                        log.info(">>> 임시 파일 다운로드 및 검증 완료 - datasourceFileId: {}, fileSize: {} bytes",
                                        datasourceFileId, tempFile.length());

                        return tempFile;

                } catch (IOException e) {
                        log.error(">>> 임시 파일 다운로드 실패 - datasourceFileId: {}", datasourceFileId, e);
                        throw new BusinessException(
                                        ErrorCode.EXTERNAL_API_ERROR,
                                        "임시 파일 다운로드에 실패했습니다: " + e.getMessage());
                } catch (Exception e) {
                        log.error(">>> 임시 파일 다운로드 실패 - datasourceFileId: {}", datasourceFileId, e);
                        throw new BusinessException(
                                        ErrorCode.EXTERNAL_API_ERROR,
                                        "임시 파일 다운로드에 실패했습니다: " + e.getMessage());
                }
        }

        /**
         * S3에서 파일명으로 파일을 다운로드하여 임시 저장소에 저장합니다.
         * 
         * @param fileName   파일명
         * @param bucketName 버킷 이름 (null이면 기본 버킷 사용)
         * @return 다운로드 결과 정보 (tempFile 포함)
         */
        private synchronized Map<String, Object> downloadFileFromS3ToTempStorage(String fileName, String bucketName) {
                log.info(">>> S3에서 임시 파일 다운로드 시작 - fileName: {}", fileName);

                try {
                        // 임시 디렉토리 생성 및 검증
                        Path tempDir = createAndValidateTempDirectory();

                        // 원본 파일명 그대로 사용
                        File tempFile = new File(tempDir.toFile(), fileName);
                        String absoluteFilePath = tempFile.getAbsolutePath();
                        log.info(">>> 임시 파일 생성 경로 - fileName: {}, 절대경로: {}", fileName, absoluteFilePath);

                        // 파일이 이미 존재하는 경우 삭제
                        if (tempFile.exists()) {
                                log.warn(">>> 기존 파일 삭제 시도 - 절대경로: {}", absoluteFilePath);
                                // boolean deleted = tempFile.delete();
                                boolean deleted = Files.deleteIfExists(tempFile.toPath());
                                if (deleted) {
                                        log.info(">>> 기존 파일 삭제 성공 - 절대경로: {}", absoluteFilePath);
                                } else {
                                        log.warn(">>> 기존 파일 삭제 실패 - 절대경로: {}", absoluteFilePath);
                                }
                        }

                        // S3에서 파일 다운로드
                        log.info(">>> S3에서 파일 다운로드 시작 - fileName: {}, 저장경로: {}", fileName, absoluteFilePath);
                        Map<String, Object> downloadResult = s3Util.downloadFileByFileName(
                                        fileName, bucketName, tempFile);

                        // 다운로드된 파일 검증
                        if (!tempFile.exists() || tempFile.length() == 0) {
                                log.error(">>> 다운로드된 파일이 존재하지 않거나 크기가 0 - 절대경로: {}", absoluteFilePath);
                                throw new BusinessException(
                                                ErrorCode.EXTERNAL_API_ERROR,
                                                "파일 다운로드가 완료되지 않았습니다.");
                        }

                        // 결과에 임시 파일 정보 추가
                        downloadResult.put("tempFile", tempFile);
                        downloadResult.put("tempFilePath", absoluteFilePath);

                        log.info(">>> S3에서 임시 파일 다운로드 및 검증 완료 - fileName: {}, 절대경로: {}, fileSize: {} bytes",
                                        fileName, absoluteFilePath, tempFile.length());

                        return downloadResult;

                } catch (BusinessException e) {
                        throw e;
                } catch (FeignException e) {
                        log.error(">>> S3에서 임시 파일 다운로드 실패 (FeignException) - fileName: {}, 상태코드: {}", fileName,
                                        e.status(), e);
                        throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                                        String.format("S3에서 임시 파일 다운로드 중 오류가 발생했습니다: HTTP %d - %s", e.status(),
                                                        e.getMessage()));
                } catch (RuntimeException e) {
                        log.error(">>> S3에서 임시 파일 다운로드 실패 (RuntimeException) - fileName: {}", fileName, e);
                        throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                                        "S3에서 임시 파일 다운로드에 실패했습니다: " + e.getMessage());
                } catch (Exception e) {
                        log.error(">>> S3에서 임시 파일 다운로드 실패 (Exception) - fileName: {}", fileName, e);
                        throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                                        "S3에서 임시 파일 다운로드에 실패했습니다: " + e.getMessage());
                }
        }

        /**
         * 임시 파일을 S3에 업로드
         * 
         * @param tempFile         임시 파일
         * @param datasourceFileId 데이터소스 파일 ID
         * @param originalFileName 원본 파일명
         * @return S3 업로드 결과
         */
        private Map<String, Object> uploadTempFileToS3(File tempFile, String datasourceFileId,
                        String originalFileName) {
                log.info(">>> 임시 파일 S3 업로드 시작 - tempFile: {}, datasourceFileId: {}, originalFileName: {}",
                                tempFile.getAbsolutePath(), datasourceFileId, originalFileName);

                try {
                        // S3 키 생성
                        String s3Key = "file_" + datasourceFileId + "_" + System.currentTimeMillis();
                        if (originalFileName != null && !originalFileName.isEmpty()) {
                                s3Key += "__gaf__" + originalFileName;
                        }
                        // S3 키 URL 인코딩
                        s3Key = URLEncoder.encode(s3Key, "UTF-8");

                        // 파일 크기 확인
                        long fileSize = tempFile.length();
                        String contentType = Files.probeContentType(tempFile.toPath());
                        if (contentType == null) {
                                contentType = "application/octet-stream";
                        }

                        // S3Util을 사용하여 파일 업로드
                        Map<String, Object> s3Result = s3Util.uploadFile(tempFile, s3Key, contentType);

                        // 결과에 추가 정보 포함
                        Map<String, Object> result = new HashMap<>();
                        result.put("success", true);
                        result.put("s3Bucket", s3Result.get("bucketName"));
                        result.put("s3Key", s3Result.get("s3Key"));
                        result.put("s3Url", "s3://" + s3Result.get("bucketName") + "/" + s3Result.get("s3Key"));
                        result.put("etag", s3Result.get("etag"));
                        result.put("contentType", contentType);
                        result.put("contentLength", fileSize);
                        result.put("uploadTimestamp", LocalDateTime.now().toString());
                        result.put("originalFileName", originalFileName);
                        result.put("tempFilePath", tempFile.getAbsolutePath());

                        log.info(">>> 임시 파일 S3 업로드 완료 - s3Key: {}, etag: {}, fileSize: {} bytes",
                                        s3Key, s3Result.get("etag"), fileSize);
                        return result;

                } catch (BusinessException e) {
                        throw e;
                } catch (FeignException e) {
                        log.error(">>> 임시 파일 S3 업로드 실패 (FeignException) - tempFile: {}, 상태코드: {}",
                                        tempFile.getAbsolutePath(), e.status(), e);
                        throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                                        String.format("임시 파일 S3 업로드 중 오류가 발생했습니다: HTTP %d - %s", e.status(),
                                                        e.getMessage()));
                } catch (RuntimeException e) {
                        log.error(">>> 임시 파일 S3 업로드 실패 (RuntimeException) - tempFile: {}", tempFile.getAbsolutePath(),
                                        e);
                        throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                                        "임시 파일 S3 업로드에 실패했습니다: " + e.getMessage());
                } catch (Exception e) {
                        log.error(">>> 임시 파일 S3 업로드 실패 (Exception) - tempFile: {}", tempFile.getAbsolutePath(), e);
                        throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                                        "임시 파일 S3 업로드에 실패했습니다: " + e.getMessage());
                }
        }

        /**
         * 경로 기반으로 임시 파일 정리 (단순화된 삭제)
         * 
         * @param filePath 삭제할 파일의 절대 경로
         */
        private synchronized void cleanupTempFileByPath(String filePath) {
                if (filePath == null || filePath.trim().isEmpty()) {
                        log.warn(">>> 삭제할 파일 경로가 null이거나 비어있습니다");
                        return;
                }

                log.info(">>> 임시 파일 삭제 시작 - 절대경로: {}", filePath);

                try {
                        // 경로를 기반으로 새 File 객체 생성
                        File fileToDelete = new File(filePath);
                        Path filePathNormalized = fileToDelete.toPath().toAbsolutePath().normalize();

                        // 안전성 검증: 삭제할 파일이 임시 디렉토리 내부인지 확인
                        Path tempDir = createAndValidateTempDirectory();
                        Path tempDirNormalized = tempDir.toAbsolutePath().normalize();

                        if (!filePathNormalized.startsWith(tempDirNormalized)) {
                                log.error(">>> 보안 위험: 삭제하려는 파일 경로가 임시 디렉토리 밖입니다 - tempDir: {}, filePath: {}",
                                                tempDirNormalized, filePathNormalized);
                                throw new SecurityException("파일 경로가 허용된 디렉토리 밖입니다. 삭제를 거부합니다.");
                        }

                        // 파일 존재 여부 확인
                        if (!fileToDelete.exists()) {
                                log.warn(">>> 삭제할 파일이 존재하지 않음 - 절대경로: {}", filePath);
                                return;
                        }

                        // 파일 정보 확인
                        long fileSize = fileToDelete.length();
                        boolean isFile = fileToDelete.isFile();

                        log.info(">>> 파일 정보 확인 - 절대경로: {}, 파일크기: {} bytes, isFile: {}",
                                        filePath, fileSize, isFile);

                        if (!isFile) {
                                log.warn(">>> 파일이 아닌 경로입니다 - 절대경로: {}", filePath);
                                return;
                        }

                        boolean deleted = Files.deleteIfExists(fileToDelete.toPath());
                        // 파일 삭제 시도
                        // boolean deleted = fileToDelete.delete();

                        if (deleted) {
                                // 삭제 확인
                                if (fileToDelete.exists()) {
                                        log.warn(">>> 파일 삭제 후에도 여전히 존재함 - 절대경로: {}", filePath);
                                } else {
                                        log.info(">>> 임시 파일 삭제 성공 - 절대경로: {}, 파일크기: {} bytes", filePath, fileSize);
                                }
                        } else {
                                // 삭제 실패 시 Files.deleteIfExists() 재시도
                                try {
                                        deleted = Files.deleteIfExists(fileToDelete.toPath());
                                        if (deleted) {
                                                log.info(">>> Files.deleteIfExists()로 파일 삭제 성공 - 절대경로: {}, 파일크기: {} bytes",
                                                                filePath, fileSize);
                                        } else {
                                                log.error(">>> 임시 파일 삭제 실패 - 절대경로: {}, 파일크기: {} bytes", filePath,
                                                                fileSize);
                                        }
                                } catch (IOException e) {
                                        log.error(">>> 임시 파일 삭제 실패 - 절대경로: {}, 파일크기: {} bytes, error: {}",
                                                        filePath, fileSize, e.getMessage());
                                }
                        }

                } catch (SecurityException e) {
                        log.error(">>> 임시 파일 정리 중 보안 예외 발생 - 절대경로: {}, error: {}", filePath, e.getMessage(), e);
                } catch (RuntimeException e) {
                        log.error(">>> 임시 파일 정리 중 런타임 예외 발생 - 절대경로: {}, error: {}", filePath, e.getMessage(), e);
                } catch (Exception e) {
                        log.error(">>> 임시 파일 정리 중 예상치 못한 오류 발생 - 절대경로: {}, error: {}", filePath, e.getMessage(), e);
                }
        }

        /**
         * 임시 파일 정리
         * 
         * @param tempFile 정리할 임시 파일
         */
        private synchronized void cleanupTempFile(File tempFile) {
                if (tempFile == null) {
                        log.warn(">>> 삭제할 임시 파일이 null입니다");
                        return;
                }

                String filePath = tempFile.getAbsolutePath();
                log.info(">>> 임시 파일 삭제 시작 - 절대경로: {}", filePath);

                try {
                        // 경로를 기반으로 새 File 객체 생성 (안전하게)
                        File fileToDelete = new File(filePath);

                        // 파일 존재 여부 확인
                        if (!fileToDelete.exists()) {
                                log.warn(">>> 삭제할 파일이 존재하지 않음 - 절대경로: {}", filePath);
                                return;
                        }

                        // 파일 정보 확인
                        long fileSize = fileToDelete.length();
                        boolean isFile = fileToDelete.isFile();
                        boolean canWrite = fileToDelete.canWrite();

                        log.info(">>> 파일 정보 확인 - 절대경로: {}, 파일크기: {} bytes, isFile: {}, canWrite: {}",
                                        filePath, fileSize, isFile, canWrite);

                        if (!isFile) {
                                log.warn(">>> 파일이 아닌 경로입니다 - 절대경로: {}", filePath);
                                return;
                        }

                        boolean deleted = Files.deleteIfExists(fileToDelete.toPath());
                        // // 파일 삭제 시도 (여러 방법 시도)
                        // boolean deleted = false;

                        // // 방법 1: File.delete() 사용
                        // deleted = fileToDelete.delete();

                        if (!deleted) {
                                // 방법 2: Files.deleteIfExists() 사용
                                try {
                                        deleted = Files.deleteIfExists(fileToDelete.toPath());
                                        if (deleted) {
                                                log.info(">>> Files.deleteIfExists()로 파일 삭제 성공 - 절대경로: {}", filePath);
                                        }
                                } catch (IOException e) {
                                        log.warn(">>> Files.deleteIfExists() 실패 - 절대경로: {}, error: {}", filePath,
                                                        e.getMessage());
                                }
                        }

                        if (deleted) {
                                // 삭제 확인
                                if (fileToDelete.exists()) {
                                        log.warn(">>> 파일 삭제 후에도 여전히 존재함 - 절대경로: {}", filePath);
                                } else {
                                        log.info(">>> 임시 파일 삭제 성공 - 절대경로: {}, 파일크기: {} bytes", filePath, fileSize);
                                }
                        } else {
                                log.error(">>> 임시 파일 삭제 실패 - 절대경로: {}, 파일크기: {} bytes, canWrite: {}",
                                                filePath, fileSize, canWrite);

                                // 삭제 실패 원인 분석
                                if (!canWrite) {
                                        log.error(">>> 파일 쓰기 권한 없음 - 다른 프로세스에서 사용 중일 수 있음, 절대경로: {}", filePath);
                                } else {
                                        log.error(">>> 파일이 쓰기 가능하지만 삭제되지 않음 - 권한 문제일 수 있음, 절대경로: {}", filePath);
                                }
                        }
                } catch (NoSuchFileException e) {
                        // 파일이 이미 삭제된 경우 (정상)
                        log.debug(">>> 파일이 이미 존재하지 않음 - 절대경로: {}", filePath);
                } catch (DirectoryNotEmptyException e) {
                        log.warn(">>> 디렉토리가 비어있지 않아 삭제할 수 없음 - 절대경로: {}", filePath);
                } catch (SecurityException e) {
                        log.error(">>> 임시 파일 정리 중 보안 예외 발생 - 절대경로: {}, error: {}", filePath, e.getMessage(), e);
                } catch (RuntimeException e) {
                        log.error(">>> 임시 파일 정리 중 런타임 예외 발생 - 절대경로: {}, error: {}", filePath, e.getMessage(), e);
                } catch (Exception e) {
                        log.error(">>> 임시 파일 정리 중 예상치 못한 오류 발생 - 절대경로: {}, error: {}", filePath, e.getMessage(), e);
                }
        }

        /**
         * 임시 버킷 삭제
         * 
         * @param tempBucketName 삭제할 임시 버킷 이름
         * @return 삭제 결과
         */
        public Map<String, Object> deleteTempBucket(String tempBucketName) {
                log.info("임시 버킷 삭제 서비스 호출 - tempBucketName: {}", tempBucketName);

                try {
                        Map<String, Object> deleteObjectResult = s3Util.deleteObject(tempBucketName); // 버킷 내 파일 삭제
                        Map<String, Object> deleteBucketResult = s3Util.deleteBucket(tempBucketName); // 버킷 삭제

                        Map<String, Object> result = new HashMap<>(deleteBucketResult);
                        result.put("tempBucketName", tempBucketName);
                        result.put("deletedObjectCount", deleteObjectResult.get("deletedObjectCount"));
                        result.put("objectDeleteResult", deleteObjectResult);

                        log.info(">>> 임시 버킷 삭제 완료 - tempBucketName: {}, success: {}, deletedObjectCount: {}",
                                        tempBucketName, result.get("success"), result.get("deletedObjectCount"));

                        return result;

                } catch (BusinessException e) {
                        throw handleException("임시 버킷 삭제", e);
                } catch (FeignException e) {
                        throw handleException("임시 버킷 삭제", e);
                } catch (RuntimeException e) {
                        throw handleException("임시 버킷 삭제", e);
                } catch (Exception e) {
                        throw handleException("임시 버킷 삭제", e);
                }
        }

        /**
         * 에러 발생 시 임시 버킷 삭제 시도 (공통 메서드)
         *
         * @param tempBucketName 삭제할 임시 버킷 이름
         */
        private void cleanupTempBucketOnError(String tempBucketName) {
                if (tempBucketName == null) {
                        return;
                }

                try {
                        log.warn("에러 발생으로 인한 임시 버킷 삭제 시도 - tempBucket: {}", tempBucketName);
                        Map<String, Object> deleteResult = deleteTempBucket(tempBucketName);
                        if (deleteResult != null && Boolean.TRUE.equals(deleteResult.get("success"))) {
                                log.info("에러 발생 시 임시 버킷 삭제 성공 - tempBucket: {}", tempBucketName);
                        } else {
                                log.warn("에러 발생 시 임시 버킷 삭제 실패 - tempBucket: {}, result: {}",
                                                tempBucketName, deleteResult);
                        }
                } catch (BusinessException deleteException) {
                        log.error("에러 발생 시 임시 버킷 삭제 중 오류 (BusinessException) - tempBucket: {}, error: {}",
                                        tempBucketName, deleteException.getMessage(), deleteException);
                } catch (FeignException deleteException) {
                        log.error("에러 발생 시 임시 버킷 삭제 중 오류 (FeignException) - tempBucket: {}, 상태코드: {}, error: {}",
                                        tempBucketName, deleteException.status(), deleteException.getMessage(),
                                        deleteException);
                } catch (RuntimeException deleteException) {
                        log.error("에러 발생 시 임시 버킷 삭제 중 오류 (RuntimeException) - tempBucket: {}, error: {}",
                                        tempBucketName, deleteException.getMessage(), deleteException);
                } catch (Exception deleteException) {
                        log.error("에러 발생 시 임시 버킷 삭제 중 오류 (Exception) - tempBucket: {}, error: {}",
                                        tempBucketName, deleteException.getMessage(), deleteException);
                }
        }

        /**
         * 원본 파일명으로 임시 버킷으로 복사
         * 
         * @param bucketName 원본 버킷 이름
         * @param fileNames  검색할 원본 파일명들 (쉼표로 구분)
         * @return 복사 결과
         */
        public Map<String, Object> copyFilesByFileName(String bucketName, String fileNames) {
                log.info(">>> 원본 파일명으로 복사(S3 copy_object) 서비스 호출 - bucketName: {}, fileNames: {}", bucketName,
                                fileNames);

                try {
                        // 1. 파일명들을 쉼표로 분리
                        String[] fileNameArray = fileNames.split(",");
                        List<String> targetFileNames = new ArrayList<>();
                        for (String fileName : fileNameArray) {
                                if (fileName != null && !fileName.trim().isEmpty()) {
                                        targetFileNames.add(fileName.trim());
                                }
                        }

                        // 2. 전체 객체 목록 조회
                        Map<String, Object> allObjects = s3Util.listBucketObjects(bucketName, null, null);
                        List<Map<String, Object>> objects = (List<Map<String, Object>>) allObjects.get("objects");

                        // 3. originalFileName이 있으면 그대로 비교, 없으면 key에서 '__gaf__' 뒤를 즉석 파싱하여 비교 (fallback)
                        List<String> foundKeys = new ArrayList<>();
                        List<String> unmatchedInputFileNames = new ArrayList<>();
                        if (objects != null) {
                                log.info(">>> 총 {}개 객체에서 {}개 파일명 검색 중...", objects.size(), targetFileNames.size());
                                for (Map<String, Object> object : objects) {
                                        String originalFileName = (String) object.get("originalFileName");
                                        String key = (String) object.get("key");

                                        // 비교용 후보명: 우선 originalFileName, 없으면 key에서 '__gaf__' 뒤를 파싱, 그것도 없으면 전체 key
                                        String candidateName = originalFileName;
                                        if (candidateName == null) {
                                                if (key != null && key.contains("__gaf__")) {
                                                        candidateName = key.substring(key.lastIndexOf("__gaf__") + 7);
                                                } else {
                                                        candidateName = key; // 최후 fallback
                                                }
                                        }

                                        if (candidateName != null) {
                                                for (String targetFileName : targetFileNames) {
                                                        String normalizedInput = Normalizer.normalize(targetFileName,
                                                                        Normalizer.Form.NFC);
                                                        String normalizedStored = Normalizer.normalize(candidateName,
                                                                        Normalizer.Form.NFC);

                                                        log.debug("파일명 비교 - 입력: '{}' → 후보: '{}' | 일치 여부: {}",
                                                                        targetFileName, candidateName,
                                                                        normalizedStored.equals(normalizedInput));

                                                        if (normalizedStored.equals(normalizedInput)) {
                                                                log.info(">>> 매칭된 객체 발견 - key: {}, candidateName: {}",
                                                                                key,
                                                                                candidateName);
                                                                foundKeys.add(key);
                                                                break; // 중복 방지를 위해 break
                                                        }
                                                }
                                        }
                                }
                        }

                        log.info(">>>검색 결과: {}개 객체 발견", foundKeys.size());
                        // unmatched 계산
                        for (String inputName : targetFileNames) {
                                boolean matched = false;
                                for (String k : foundKeys) {
                                        String candidate = k;
                                        if (candidate != null && candidate.contains("__gaf__")) {
                                                candidate = candidate.substring(candidate.lastIndexOf("__gaf__") + 7);
                                        }
                                        String normalizedInput = Normalizer.normalize(inputName, Normalizer.Form.NFC);
                                        String normalizedStored = Normalizer.normalize(candidate, Normalizer.Form.NFC);
                                        if (normalizedStored.equals(normalizedInput)) {
                                                matched = true;
                                                break;
                                        }
                                }
                                if (!matched) {
                                        unmatchedInputFileNames.add(inputName);
                                }
                        }

                        if (foundKeys.isEmpty()) {
                                log.info(">>> 일치하는 파일명이 없습니다. 빈 임시 버킷을 생성합니다.");

                                // 빈 임시 버킷 생성
                                Map<String, Object> tempBucketResult = s3Util.createTempBucket("temp-copy");
                                String tempBucketName = (String) tempBucketResult.get("tempBucketName");

                                Map<String, Object> result = new HashMap<>();
                                result.put("sourceBucket", bucketName);
                                result.put("fileNames", targetFileNames);
                                result.put("matchedKeys", new ArrayList<>());
                                result.put("matchedCount", 0);
                                result.put("unmatchedInputFileNames", targetFileNames);
                                result.put("totalFiles", 0);
                                result.put("successCount", 0);
                                result.put("failureCount", 0);
                                result.put("copiedFiles", new ArrayList<>());
                                result.put("failedFiles", new ArrayList<>());
                                result.put("tempBucketName", tempBucketName);
                                result.put("tempBucketCreatedAt", tempBucketResult.get("createdAt"));
                                result.put("tempBucket", tempBucketName);
                                result.put("message", "해당 원본 파일명들을 가진 객체를 찾을 수 없어 빈 임시 버킷을 생성했습니다.");

                                log.info(">>> 빈 임시 버킷 생성 완료 - tempBucket: {}", tempBucketName);
                                return result;
                        }

                        // 4. 임시 버킷 생성
                        Map<String, Object> tempBucketResult = s3Util.createTempBucket("temp-copy");
                        String tempBucketName = (String) tempBucketResult.get("tempBucketName");

                        // 5. 파일들을 S3 copy_object로 직접 복사
                        List<Map<String, Object>> copiedFiles = new ArrayList<>();
                        List<String> failedFiles = new ArrayList<>();

                        for (String foundKey : foundKeys) {
                                try {
                                        log.info(">>> 파일 복사 시작 - sourceKey: {}", foundKey);

                                        // foundKey에서 원본 파일명 추출 (targetKey는 __gaf__ 뒤의 원본 파일명만 사용)
                                        String originalFileName = foundKey;
                                        if (foundKey != null && foundKey.contains("__gaf__")) {
                                                originalFileName = foundKey
                                                                .substring(foundKey.lastIndexOf("__gaf__") + 7);
                                        }

                                        // 복사할 대상 키 결정 (__gaf__ 뒤의 원본 파일명만 사용)
                                        String targetKey = originalFileName;

                                        // S3 copy_object로 직접 복사
                                        log.info(">>> S3 객체 복사 시작 - sourceBucket: {}, sourceKey: {}, targetBucket: {}, targetKey: {}",
                                                        bucketName, foundKey, tempBucketName, targetKey);

                                        Map<String, Object> copyResult = s3Util.copyObject(
                                                        bucketName, foundKey, tempBucketName, targetKey);

                                        // copyObject의 리턴값 전체를 포함
                                        Map<String, Object> fileResult = new HashMap<>(copyResult);
                                        fileResult.put("fileName", originalFileName);
                                        fileResult.put("sourceKey", foundKey);
                                        fileResult.put("targetKey", targetKey);

                                        copiedFiles.add(fileResult);

                                } catch (RuntimeException e) {
                                        log.error("파일 복사 실패 (RuntimeException) - foundKey: {}, error: {}", foundKey,
                                                        e.getMessage(), e);
                                        String fileName = foundKey;
                                        if (foundKey != null && foundKey.contains("__gaf__")) {
                                                fileName = foundKey.substring(foundKey.lastIndexOf("__gaf__") + 7);
                                        }
                                        failedFiles.add(fileName);
                                } catch (Exception e) {
                                        log.error("파일 복사 실패 (Exception) - foundKey: {}, error: {}", foundKey,
                                                        e.getMessage(), e);
                                        String fileName = foundKey;
                                        if (foundKey != null && foundKey.contains("__gaf__")) {
                                                fileName = foundKey.substring(foundKey.lastIndexOf("__gaf__") + 7);
                                        }
                                        failedFiles.add(fileName);
                                }
                        }

                        // 6. 복사 완료 후 임시 버킷의 객체 목록 조회
                        Map<String, Object> tempBucketObjects = null;
                        try {
                                log.info(">>> 임시 버킷 객체 목록 조회 시작 - tempBucket: {}", tempBucketName);
                                tempBucketObjects = s3Util.listBucketObjects(tempBucketName, null, null);
                                log.info(">>> 임시 버킷 객체 목록 조회 완료 - tempBucket: {}, 객체 수: {}",
                                                tempBucketName,
                                                tempBucketObjects.get("totalCount") != null
                                                                ? tempBucketObjects.get("totalCount")
                                                                : 0);
                                // 객체 목록 조회 실패해도 결과는 반환
                        } catch (BusinessException e) {
                                log.error(">>> 임시 버킷 객체 목록 조회 실패 - tempBucket: {}, error: {}",
                                                tempBucketName, e.getMessage(), e);
                                // 객체 목록 조회 실패해도 결과는 반환
                        } catch (RuntimeException e) {
                                log.error(">>> 임시 버킷 객체 목록 조회 실패 - tempBucket: {}, error: {}",
                                                tempBucketName, e.getMessage(), e);
                                // 객체 목록 조회 실패해도 결과는 반환
                        } catch (Exception e) {
                                log.error(">>> 임시 버킷 객체 목록 조회 실패 - tempBucket: {}, error: {}",
                                                tempBucketName, e.getMessage(), e);

                        }

                        // 7. 결과 구성
                        Map<String, Object> result = new HashMap<>();
                        result.put("sourceBucket", bucketName);
                        result.put("targetBucket", tempBucketName);
                        result.put("totalFiles", foundKeys.size());
                        result.put("copiedFiles", copiedFiles);
                        result.put("failedFiles", failedFiles);
                        result.put("successCount", copiedFiles.size());
                        result.put("failureCount", failedFiles.size());
                        result.put("success", failedFiles.isEmpty());
                        result.put("tempBucketName", tempBucketName);
                        result.put("tempBucketCreatedAt", tempBucketResult.get("createdAt"));
                        result.put("fileNames", targetFileNames);
                        result.put("matchedKeys", foundKeys);
                        result.put("matchedCount", foundKeys.size());
                        result.put("unmatchedInputFileNames", unmatchedInputFileNames);
                        result.put("tempBucket", tempBucketName);

                        // 임시 버킷의 객체 목록 전체 결과 추가
                        if (tempBucketObjects != null) {
                                result.put("tempBucketObjects", tempBucketObjects);
                                result.put("tempBucketObjectCount", tempBucketObjects.get("totalCount"));
                                result.put("tempBucketObjectsList", tempBucketObjects.get("objects"));
                        } else {
                                result.put("tempBucketObjects", null);
                                result.put("tempBucketObjectCount", 0);
                                result.put("tempBucketObjectsList", new ArrayList<>());
                        }

                        log.info(">>> 원본 파일명으로 복사 완료 - bucketName: {}, fileNames: {}, tempBucket: {}, successCount: {}, tempBucketObjectCount: {}",
                                        bucketName, targetFileNames, tempBucketName, copiedFiles.size(),
                                        tempBucketObjects != null ? tempBucketObjects.get("totalCount") : 0);

                        return result;

                } catch (BusinessException e) {
                        throw handleException("원본 파일명으로 복사", e);
                } catch (FeignException e) {
                        throw handleException("원본 파일명으로 복사", e);
                } catch (RuntimeException e) {
                        throw handleException("원본 파일명으로 복사", e);
                } catch (Exception e) {
                        throw handleException("원본 파일명으로 복사", e);
                }
        }

        /**
         * 파일명 리스트로부터 훈련 데이터셋 생성 (학습데이터 생성 - 커스텀아닌경우)
         * 
         * @param request 훈련 데이터셋 생성 요청 (파일명 리스트 포함)
         * @return 훈련 데이터셋 생성 결과
         */
        public DataCtlgTrainingDataCreateRes createTrainingDatasetFromFiles(
                        DataCtlgTrainingDatasetCreateFromFilesReq request) {
                log.info("파일명 리스트로부터 훈련 데이터셋 생성 서비스 호출 - request: {}", request);

                long startTime = System.currentTimeMillis();

                // 각 단계별 결과를 담을 객체들
                StepResult preparationStep = null;
                StepResult datasourceCreationStep = null;
                StepResult datasetCreationStep = null;

                try {
                        // type에 따른 분기 처리
                        String type = request.getType();
                        if (type == null || type.trim().isEmpty()) {
                                type = "s3"; // 기본값
                        }

                        log.info("처리 타입: {}", type);

                        DataCtlgTrainingDataCreateRes result;
                        Map<String, Object> s3CopyResult = null; // S3 타입인 경우 복사 결과 저장
                        String resolvedSourceBucketName = request.getSourceBucketName();
                        if (resolvedSourceBucketName == null || resolvedSourceBucketName.trim().isEmpty()) {
                                resolvedSourceBucketName = s3Config.getBucketName();
                        }

                        if ("file".equals(type)) {
                                // 원래 로직 호출
                                result = createTrainingDatasetFromFileType(request);
                        } else {
                                // 원래 로직 호출
                                result = createTrainingDatasetFromS3Type(request);
                                if (result != null) {
                                        s3CopyResult = result.getCopyResult();
                                }
                        }

                        log.info("@@@@ API 호출 후 result: {}", result);

                        if (result != null && result.getDatasetId() != null) {
                                // Dataset Task 조회
                                DatasetTaskResponse datasetTaskResponse = sktaiDataDatasetsService
                                                .getDatasetTask(UUID.fromString(result.getDatasetId()));

                                log.info("@@@@ datasetTaskResponse: {}", datasetTaskResponse);

                                result.setStatus(datasetTaskResponse.getStatus());
                                result.setErrorMessage(datasetTaskResponse.getErrorMessage());
                        }
                        // 성공한 경우 단계별 결과를 생성하여 응답에 추가
                        if (result != null && result.getTrainingDataId() != null) {
                                // 전체 성공으로 간주하고 단계별 결과 생성
                                Map<String, Object> preparationResult = new HashMap<>();
                                preparationResult.put("type", type);
                                preparationResult.put("requestName", request.getName());

                                // S3 타입인 경우 복사 결과 정보 추가
                                if ("s3".equals(type) && result.getTempBucketName() != null && s3CopyResult != null) {
                                        // S3 복사 결과 정보를 preparationStep에 포함
                                        preparationResult.put("tempBucketName", result.getTempBucketName());
                                        preparationResult.put("matchedCount", result.getMatchedCount());
                                        preparationResult.put("matchedKeys", result.getMatchedKeys());
                                        preparationResult.put("inputFileNames", result.getInputFileNames());
                                        preparationResult.put("processedFileCount", result.getProcessedFileCount());
                                        preparationResult.put("processedFiles", result.getProcessedFiles());

                                        // S3 복사 관련 상세 정보 추가
                                        preparationResult.put("sourceBucket", resolvedSourceBucketName);
                                        preparationResult.put("copy_result", s3CopyResult);
                                }

                                preparationStep = StepResult.builder()
                                                .success(true)
                                                .message("사전 준비가 성공적으로 완료되었습니다")
                                                .result(preparationResult)
                                                .processingTimeMs(0L)
                                                .build();

                                datasourceCreationStep = StepResult.builder()
                                                .success(true)
                                                .message("데이터소스 생성이 성공적으로 완료되었습니다")
                                                .result(Map.of("datasourceCreated", true))
                                                .processingTimeMs(0L)
                                                .build();

                                datasetCreationStep = StepResult.builder()
                                                .success(true)
                                                .message("데이터셋 생성이 성공적으로 완료되었습니다")
                                                .result(Map.of("trainingDataId", result.getTrainingDataId(),
                                                                "datasetName", result.getName()))
                                                .processingTimeMs(0L)
                                                .build();

                                // 응답에 단계별 결과 추가
                                result.setSuccess(true);
                                result.setMessage("학습 데이터 생성이 성공적으로 완료되었습니다");
                                result.setCreationTimeMs(System.currentTimeMillis() - startTime);
                                result.setPreparationStep(preparationStep);
                                result.setDatasourceCreationStep(datasourceCreationStep);
                                result.setDatasetCreationStep(datasetCreationStep);

                                log.info("@@@@ 응답에 단계별 결과 추가 후 result: {}", result);
                        }

                        return result;

                } catch (BusinessException e) {
                        log.error("파일명 리스트로부터 훈련 데이터셋 생성 실패 (BusinessException) - sourceBucket: {}, fileNames: {}, datasetName: {}, error: {}",
                                        request.getSourceBucketName(), request.getFileNames(), request.getName(),
                                        e.getMessage(), e);
                        throw e;
                } catch (FeignException e) {
                        log.error("파일명 리스트로부터 훈련 데이터셋 생성 실패 (FeignException) - sourceBucket: {}, fileNames: {}, datasetName: {}, 상태코드: {}, error: {}",
                                        request.getSourceBucketName(), request.getFileNames(), request.getName(),
                                        e.status(), e.getMessage(), e);
                        throw e;
                } catch (RuntimeException e) {
                        log.error("파일명 리스트로부터 훈련 데이터셋 생성 실패 (RuntimeException) - sourceBucket: {}, fileNames: {}, datasetName: {}, error: {}",
                                        request.getSourceBucketName(), request.getFileNames(), request.getName(),
                                        e.getMessage(), e);
                        throw e;
                } catch (Exception e) {
                        log.error("파일명 리스트로부터 훈련 데이터셋 생성 실패 (Exception) - sourceBucket: {}, fileNames: {}, datasetName: {}, error: {}",
                                        request.getSourceBucketName(), request.getFileNames(), request.getName(),
                                        e.getMessage(), e);
                        throw e;
                }
        }

        /**
         * S3 타입 훈련 데이터셋 생성 (커스텀아닌경우) (기존 로직)
         */
        private DataCtlgTrainingDataCreateRes createTrainingDatasetFromS3Type(
                        DataCtlgTrainingDatasetCreateFromFilesReq request) {
                log.info("S3 타입 훈련 데이터셋 생성 시작");

                String tempBucketName = null;
                Map<String, Object> copyResult = null;
                try {
                        // 1. 소스 버킷명 기본값 설정 (YAML에서 가져오기)
                        String sourceBucketName = request.getSourceBucketName();
                        if (sourceBucketName == null || sourceBucketName.trim().isEmpty()) {
                                sourceBucketName = s3Config.getBucketName(); // YAML에서 기본 소스 버킷명 가져오기
                                log.info("소스 버킷명이 입력되지 않아 YAML 설정값 사용: {}", sourceBucketName);
                        }

                        // 2. 파일명 리스트로부터 파일 복사 및 임시 버킷 생성
                        copyResult = copyFilesByFileName(sourceBucketName, request.getFileNames());
                        tempBucketName = (String) copyResult.get("tempBucketName");

                        if (tempBucketName == null) {
                                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "임시 버킷 생성에 실패했습니다.");
                        }

                        // 3. S3 설정 정보 구성 (생성된 임시 버킷 사용)
                        DataSourceS3Config s3ConfigDto = DataSourceS3Config.builder()
                                        .bucketName(tempBucketName) // 생성된 임시 버킷명 사용
                                        .accessKey(s3Config.getAccessKey())
                                        .secretKey(s3Config.getSecretKey())
                                        .region(s3Config.getRegion())
                                        .prefix("") // 빈 프리픽스
                                        .endpoint(s3Config.getEndpoint())
                                        .build();

                        // 4. 사용자 정보 자동 설정
                        String projectId = request.getProjectId();
                        String createdBy = request.getCreatedBy();
                        String updatedBy = request.getUpdatedBy();

                        if (projectId == null || createdBy == null || updatedBy == null) {
                                try {
                                        MeResponse currentUser = usersService.getMe();

                                        if (projectId == null) {
                                                projectId = currentUser.getProject() != null
                                                                ? currentUser.getProject().getId()
                                                                : "default";
                                        }
                                        if (createdBy == null) {
                                                createdBy = currentUser.getUsername() != null
                                                                ? currentUser.getUsername()
                                                                : "system";
                                        }
                                        if (updatedBy == null) {
                                                updatedBy = currentUser.getUsername() != null
                                                                ? currentUser.getUsername()
                                                                : "system";
                                        }

                                        log.info("현재 사용자 정보로 자동 설정 - projectId: {}, createdBy: {}, updatedBy: {}",
                                                        projectId, createdBy, updatedBy);
                                } catch (BusinessException e) {
                                        log.warn("현재 사용자 정보 조회 실패 (BusinessException), 기본값 사용 - error: {}",
                                                        e.getMessage());
                                } catch (FeignException e) {
                                        log.warn("현재 사용자 정보 조회 실패 (FeignException), 기본값 사용 - 상태코드: {}, error: {}",
                                                        e.status(), e.getMessage());
                                } catch (RuntimeException e) {
                                        log.warn("현재 사용자 정보 조회 실패 (RuntimeException), 기본값 사용 - error: {}",
                                                        e.getMessage());
                                } catch (Exception e) {
                                        log.warn("현재 사용자 정보 조회 실패 (Exception), 기본값 사용 - error: {}", e.getMessage());
                                } finally {
                                        // 예외 발생 시 null인 경우 기본값 설정
                                        projectId = projectId != null ? projectId : "default";
                                        createdBy = createdBy != null ? createdBy : "system";
                                        updatedBy = updatedBy != null ? updatedBy : "system";
                                }
                        }

                        // 5. 데이터소스 및 데이터셋 생성
                        DataCtlgTrainingDataCreateRes result = createDatasourceAndDataset(request, s3ConfigDto,
                                        projectId, createdBy, updatedBy,
                                        copyResult, tempBucketName);
                        if (result != null) {
                                result.setCopyResult(copyResult);
                        }

                        return result;

                } catch (BusinessException e) {
                        cleanupTempBucketOnError(tempBucketName);
                        throw e;
                } catch (FeignException e) {
                        cleanupTempBucketOnError(tempBucketName);
                        log.error("S3 타입 훈련 데이터셋 생성 실패 (FeignException) - sourceBucket: {}, fileNames: {}, datasetName: {}, 상태코드: {}, error: {}",
                                        request.getSourceBucketName(), request.getFileNames(), request.getName(),
                                        e.status(), e.getMessage(), e);
                        throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                                        String.format("S3 타입 훈련 데이터셋 생성에 실패했습니다: HTTP %d - %s", e.status(),
                                                        e.getMessage()));
                } catch (RuntimeException e) {
                        cleanupTempBucketOnError(tempBucketName);
                        log.error("S3 타입 훈련 데이터셋 생성 실패 (RuntimeException) - sourceBucket: {}, fileNames: {}, datasetName: {}, error: {}",
                                        request.getSourceBucketName(), request.getFileNames(), request.getName(),
                                        e.getMessage(), e);
                        throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                                        "S3 타입 훈련 데이터셋 생성에 실패했습니다: " + e.getMessage());
                } catch (Exception e) {
                        cleanupTempBucketOnError(tempBucketName);
                        log.error("S3 타입 훈련 데이터셋 생성 실패 (Exception) - sourceBucket: {}, fileNames: {}, datasetName: {}, error: {}",
                                        request.getSourceBucketName(), request.getFileNames(), request.getName(),
                                        e.getMessage(), e);
                        throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                                        "S3 타입 훈련 데이터셋 생성에 실패했습니다: " + e.getMessage());
                }
        }

        /**
         * 파일 타입 훈련 데이터셋 생성 - file 타입
         */
        private DataCtlgTrainingDataCreateRes createTrainingDatasetFromFileType(
                        DataCtlgTrainingDatasetCreateFromFilesReq request) {
                log.info(">>> 파일 타입 훈련 데이터셋 생성 시작");

                try {
                        // 1. 사용자 정보 자동 설정
                        String projectId = request.getProjectId();
                        String createdBy = request.getCreatedBy();
                        String updatedBy = request.getUpdatedBy();

                        if (projectId == null || createdBy == null || updatedBy == null) {
                                try {
                                        MeResponse currentUser = usersService.getMe();

                                        if (projectId == null) {
                                                projectId = currentUser.getProject() != null
                                                                ? currentUser.getProject().getId()
                                                                : "default";
                                        }
                                        if (createdBy == null) {
                                                createdBy = currentUser.getUsername() != null
                                                                ? currentUser.getUsername()
                                                                : "system";
                                        }
                                        if (updatedBy == null) {
                                                updatedBy = currentUser.getUsername() != null
                                                                ? currentUser.getUsername()
                                                                : "system";
                                        }

                                        log.info(">>> 현재 사용자 정보로 자동 설정 - projectId: {}, createdBy: {}, updatedBy: {}",
                                                        projectId, createdBy, updatedBy);
                                } catch (BusinessException e) {
                                        log.warn(">>> 현재 사용자 정보 조회 실패 (BusinessException), 기본값 사용 - error: {}",
                                                        e.getMessage());
                                } catch (FeignException e) {
                                        log.warn(">>> 현재 사용자 정보 조회 실패 (FeignException), 기본값 사용 - 상태코드: {}, error: {}",
                                                        e.status(), e.getMessage());
                                } catch (RuntimeException e) {
                                        log.warn(">>> 현재 사용자 정보 조회 실패 (RuntimeException), 기본값 사용 - error: {}",
                                                        e.getMessage());
                                } catch (Exception e) {
                                        log.warn(">>> 현재 사용자 정보 조회 실패 (Exception), 기본값 사용 - error: {}", e.getMessage());
                                } finally {
                                        // 예외 발생 시 null인 경우 기본값 설정
                                        projectId = projectId != null ? projectId : "default";
                                        createdBy = createdBy != null ? createdBy : "system";
                                        updatedBy = updatedBy != null ? updatedBy : "system";
                                }
                        }

                        // 2. 정책 구성
                        List<Object> policyList = request.getPolicy();
                        if (policyList == null || policyList.isEmpty()) {
                                policyList = createDefaultPolicy();
                        }

                        // 3. 데이터소스 생성 요청 구성 (매퍼 사용)
                        DatasourceCreate datasourceCreate = dataCtlgDataSetMapper.toDatasourceCreateForFileType(
                                        request, projectId, createdBy, updatedBy, policyList);

                        // 4. 데이터소스 생성
                        log.info("=== 파일 타입 데이터소스 생성 요청 ===");
                        log.info("전체 요청: {}", datasourceCreate);
                        log.info("tempFiles 개수: {}",
                                        datasourceCreate.getTempFiles() != null ? datasourceCreate.getTempFiles().size()
                                                        : 0);
                        log.info("========================");

                        DataSourceCreateResponse response = sktaiDataDatasourcesService
                                        .createDatasource(datasourceCreate);

                        log.info("=== 파일 타입 데이터소스 생성 응답 ===");
                        log.info("데이터소스 ID: {}", response.getId());
                        log.info("데이터소스 이름: {}", response.getName());
                        log.info("========================");

                        log.info("@@@@ datasource create response: {}", response);
                        log.info("@@@@ Datasource ADXP 권한부여 전 - datasourceId: {}", response.getId());

                        // Datasource ADXP 권한부여
                        adminAuthService.setResourcePolicyByCurrentGroup(
                                        "/datasources/" + response.getId());
                        adminAuthService.setResourcePolicyByCurrentGroup(
                                        "/datasources/" + response.getId() + "/files");
                        log.info("@@@@ Datasource ADXP 권한부여 후 - datasourceId: {}", response.getId());
                        log.info("@@@@ Datasource ADXP 권한부여 완료 - datasourceId: {}", response.getId());

                        // 5. 데이터셋 생성
                        DataCtlgTrainingDataCreateRes createDatasetRes = createDatasetFromDatasource(request, response,
                                        projectId, createdBy, updatedBy);

                        return createDatasetRes;

                } catch (BusinessException e) {
                        log.error("파일 타입 훈련 데이터셋 생성 실패 (BusinessException) - datasetName: {}, error: {}",
                                        request.getName(), e.getMessage(), e);
                        throw e;
                } catch (FeignException e) {
                        log.error("파일 타입 훈련 데이터셋 생성 실패 (FeignException) - datasetName: {}, 상태코드: {}, error: {}",
                                        request.getName(), e.status(), e.getMessage(), e);
                        throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                                        String.format("파일 타입 훈련 데이터셋 생성에 실패했습니다: HTTP %d - %s", e.status(),
                                                        e.getMessage()));
                } catch (RuntimeException e) {
                        log.error("파일 타입 훈련 데이터셋 생성 실패 (RuntimeException) - datasetName: {}, error: {}",
                                        request.getName(), e.getMessage(), e);
                        throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                                        "파일 타입 훈련 데이터셋 생성에 실패했습니다: " + e.getMessage());
                } catch (Exception e) {
                        log.error("파일 타입 훈련 데이터셋 생성 실패 (Exception) - datasetName: {}, error: {}",
                                        request.getName(), e.getMessage(), e);
                        throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                                        "파일 타입 훈련 데이터셋 생성에 실패했습니다: " + e.getMessage());
                }
        }

        /**
         * 데이터소스 및 데이터셋 생성 (공통 로직) - (커스텀이 아닌 경우, s3 타입)
         */
        private DataCtlgTrainingDataCreateRes createDatasourceAndDataset(
                        DataCtlgTrainingDatasetCreateFromFilesReq request,
                        DataSourceS3Config s3ConfigDto, String projectId, String createdBy, String updatedBy,
                        Map<String, Object> copyResult, String tempBucketName) {

                // 정책 구성
                List<Object> policyList = request.getPolicy();
                if (policyList == null || policyList.isEmpty()) {
                        policyList = createDefaultPolicy();
                }

                // 임시 파일 구성 (매퍼 사용)
                List<DatasourceCreate.TempFileDto> tempFiles = dataCtlgDataSetMapper
                                .toDatasourceCreateTempFileDtoList(request.getTempFiles());
                if (tempFiles == null || tempFiles.isEmpty()) {
                        // 기본 임시 파일 구성
                        DatasourceCreate.TempFileDto tempFile = DatasourceCreate.TempFileDto.builder()
                                        .fileName("")
                                        .tempFilePath("")
                                        .fileMetadata(new HashMap<>())
                                        .knowledgeConfig(new HashMap<>())
                                        .build();
                        tempFiles = new ArrayList<>();
                        tempFiles.add(tempFile);
                }

                // 데이터소스 생성 요청 구성
                DatasourceCreate datasourceCreate = DatasourceCreate.builder()
                                .name(request.getName() != null ? request.getName() : "훈련 데이터셋")
                                .type(request.getType() != null ? request.getType() : "s3")
                                .description(request.getDescription() != null ? request.getDescription()
                                                : "훈련용 데이터셋: " + tempBucketName)
                                .projectId(projectId)
                                .s3Config(s3ConfigDto)
                                .createdBy(createdBy)
                                .updatedBy(updatedBy)
                                .isDeleted(request.getIsDeleted() != null ? request.getIsDeleted() : false)
                                .scope(request.getScope() != null ? request.getScope() : "public")
                                .tempFiles(tempFiles)
                                .policy(policyList)
                                .build();

                // SKTAI API를 통해 데이터소스 생성
                log.info("=== Feign Client 전송 데이터 ===");
                log.info("전체 요청: {}", datasourceCreate);
                log.info("s3Config 상세: {}", s3ConfigDto);
                log.info("========================");

                DataSourceCreateResponse response = sktaiDataDatasourcesService.createDatasource(datasourceCreate);

                log.info("@@@@ datasource create response: {}", response);
                log.info("@@@@ Datasource ADXP 권한부여 전 - datasourceId: {}", response.getId());

                // Datasource ADXP 권한부여
                adminAuthService.setResourcePolicyByCurrentGroup(
                                "/datasources/" + response.getId());
                adminAuthService.setResourcePolicyByCurrentGroup(
                                "/datasources/" + response.getId() + "/files");

                log.info("@@@@Datasource ADXP 권한부여 완료 - datasourceId: {}", response.getId());

                log.info("=== Feign Client 응답 데이터 ===");
                log.info("응답 s3Config: {}", response.getS3Config());
                log.info("응답 bucketName: {}", response.getBucketName());
                log.info("========================");

                // 데이터셋 생성
                DataCtlgTrainingDataCreateRes createDatasetRes = createDatasetFromDatasource(request, response,
                                projectId, createdBy, updatedBy, copyResult,
                                tempBucketName);

                log.info("@@@@ createDatasetRes: {}", createDatasetRes);
                return createDatasetRes;
        }

        /**
         * 데이터소스로부터 데이터셋 생성
         */
        private DataCtlgTrainingDataCreateRes createDatasetFromDatasource(
                        DataCtlgTrainingDatasetCreateFromFilesReq request,
                        DataSourceCreateResponse response, String projectId, String createdBy, String updatedBy) {
                return createDatasetFromDatasource(request, response, projectId, createdBy, updatedBy, null, null);
        }

        /**
         * 데이터소스로부터 데이터셋 생성 (S3 타입용)
         */
        private DataCtlgTrainingDataCreateRes createDatasetFromDatasource(
                        DataCtlgTrainingDatasetCreateFromFilesReq request,
                        DataSourceCreateResponse response, String projectId, String createdBy, String updatedBy,
                        Map<String, Object> copyResult, String tempBucketName) {

                // 데이터셋 생성
                log.info("=== 데이터셋 생성 시작 ===");
                log.info("데이터소스 ID: {}", response.getId());

                // 기본 정책 구성
                List<Object> datasetPolicyList = createDefaultPolicy();

                // 프로세서 파라미터 구성 (사용자 입력 또는 기본값)
                ProcessorParam processorParam;
                if (request.getProcessor() != null) {
                        // 사용자가 입력한 processor 파라미터 사용
                        processorParam = ProcessorParam.builder()
                                        .ids(request.getProcessor().getIds() != null ? request.getProcessor().getIds()
                                                        : new ArrayList<>())
                                        .duplicateSubsetColumns(
                                                        request.getProcessor().getDuplicateSubsetColumns() != null
                                                                        ? request.getProcessor()
                                                                                        .getDuplicateSubsetColumns()
                                                                        : new ArrayList<>())
                                        .regularExpression(request.getProcessor().getRegularExpression() != null
                                                        ? request.getProcessor().getRegularExpression()
                                                        : new ArrayList<>())
                                        .build();
                        log.info("사용자 입력 processor 파라미터 사용: {}", processorParam);
                } else {
                        // 기본 프로세서 파라미터 구성
                        processorParam = ProcessorParam.builder()
                                        .ids(new ArrayList<>())
                                        .duplicateSubsetColumns(new ArrayList<>())
                                        .regularExpression(new ArrayList<>())
                                        .build();
                        log.info("기본 processor 파라미터 사용: {}", processorParam);
                }

                // 데이터셋 생성 요청 구성
                DatasetCreate datasetCreate = DatasetCreate.builder()
                                .name(request.getName() != null ? request.getName() : "훈련 데이터셋")
                                .type(request.getDatasetType() != null ? request.getDatasetType()
                                                : "unsupervised_finetuning")
                                .description(request.getDescription() != null ? request.getDescription()
                                                : (tempBucketName != null ? "훈련용 데이터셋: " + tempBucketName
                                                                : "파일 기반 훈련 데이터셋"))
                                .projectId(projectId)
                                .datasourceId(response.getId()) // 데이터소스 ID 사용
                                .createdBy(createdBy)
                                .updatedBy(updatedBy)
                                .isDeleted(false)
                                .status("processing")
                                .tags(request.getTags() != null ? request.getTags() : new ArrayList<>()) // 태그 추가
                                .processor(processorParam) // 프로세서 파라미터 추가
                                .policy(datasetPolicyList)
                                .build();

                log.info("=== 데이터셋 생성 요청 데이터 ===");
                log.info("전체 요청: {}", datasetCreate);
                log.info("datasourceId: {}", datasetCreate.getDatasourceId());
                log.info("========================");

                DatasetCreateResponse datasetResponse = sktaiDataDatasetsService.createDataset(datasetCreate);

                log.info("=== 데이터셋 생성 응답 데이터 ===");
                log.info("데이터셋 ID: {}", datasetResponse.getId());
                log.info("데이터셋 이름: {}", datasetResponse.getName());
                log.info("데이터셋 상태: {}", datasetResponse.getStatus());
                log.info("========================");
                // Dataset ADXP 권한부여
                adminAuthService.setResourcePolicyByCurrentGroup(
                                "/datasets/" + datasetResponse.getId());
                adminAuthService.setResourcePolicyByCurrentGroup(
                                "/datasets/" + datasetResponse.getId() + "/tags");

                log.info("@@@@ dataset create response: {}", datasetResponse);
                log.info("@@@@ Dataset ADXP 권한부여 후 - datasetId: {}", datasetResponse.getId());

                // 통합 결과 반환
                Map<String, Object> result = new HashMap<>();

                // S3 타입인 경우 복사 결과 정보 추가
                if (copyResult != null && tempBucketName != null) {
                        result.put("source_bucket", request.getSourceBucketName());
                        result.put("file_names", request.getFileNames());
                        result.put("temp_bucket_name", tempBucketName);
                        result.put("copied_files", copyResult.get("copiedFiles"));
                        result.put("success_count", copyResult.get("successCount"));
                        result.put("failure_count", copyResult.get("failureCount"));
                        result.put("matched_keys", copyResult.get("matchedKeys"));
                        result.put("matched_count", copyResult.get("matchedCount"));
                        result.put("unmatched_input_file_names", copyResult.get("unmatchedInputFileNames"));

                        // S3 복사 관련 상세 정보 추가
                        result.put("temp_bucket_objects", copyResult.get("tempBucketObjects"));
                        result.put("temp_bucket_object_count", copyResult.get("tempBucketObjectCount"));
                        result.put("temp_bucket_objects_list", copyResult.get("tempBucketObjectsList"));
                        result.put("total_files", copyResult.get("totalFiles"));
                        result.put("failed_files", copyResult.get("failedFiles"));
                }

                // 파일 타입인 경우 temp_files 정보 추가
                if (request.getTempFiles() != null && !request.getTempFiles().isEmpty()) {
                        result.put("temp_files", request.getTempFiles());
                        result.put("temp_files_count", request.getTempFiles().size());
                }

                // 데이터소스 생성 결과
                result.put("datasource_id", response.getId());
                result.put("datasource_name", response.getName());
                result.put("datasource_type", response.getType());
                result.put("datasource_created_by", response.getCreatedBy());
                result.put("datasource_updated_by", response.getUpdatedBy());
                result.put("datasource_description", response.getDescription());
                result.put("s3_config", response.getS3Config());
                result.put("datasource_is_deleted", response.getIsDeleted());
                result.put("datasource_scope", response.getScope());
                result.put("datasource_created_at", response.getCreatedAt());
                result.put("datasource_updated_at", response.getUpdatedAt());
                result.put("datasource_status", response.getStatus());
                result.put("bucket_name", response.getBucketName());

                // 데이터셋 생성 결과
                result.put("dataset_id", datasetResponse.getId());
                result.put("dataset_name", datasetResponse.getName());
                result.put("dataset_type", datasetResponse.getType());
                result.put("dataset_description", datasetResponse.getDescription());
                result.put("dataset_status", datasetResponse.getStatus());
                result.put("dataset_project_id", datasetResponse.getProjectId());
                result.put("dataset_created_by", datasetResponse.getCreatedBy());
                result.put("dataset_updated_by", datasetResponse.getUpdatedBy());
                result.put("dataset_is_deleted", datasetResponse.getIsDeleted());
                result.put("dataset_created_at", datasetResponse.getCreatedAt());
                result.put("dataset_updated_at", datasetResponse.getUpdatedAt());
                result.put("dataset_datasource_id", datasetResponse.getDatasourceId());
                result.put("dataset_datasource_files", datasetResponse.getDatasourceFiles());
                // SKT AI API 응답의 processor와 우리가 전송한 원본 processor 모두 포함
                result.put("dataset_processor", datasetResponse.getProcessor());
                result.put("original_processor", processorParam);
                result.put("dataset_tags", datasetResponse.getTags());

                log.info("훈련 데이터셋 생성 완료 - datasourceId: {}, datasetId: {}",
                                response.getId(), datasetResponse.getId());

                // 6. 임시 버킷 삭제 스케줄러 시작 (datasource id, temp bucket name)
                // BucketDeleteBatch(TaskScheduler 기반)에서 상태를 주기적으로 확인하고 삭제 조건이 충족되면 삭제 후 스케줄을
                // 종료합니다.
                if (tempBucketName != null) {
                        String username = tokenInfo.getUserName() != null ? tokenInfo.getUserName() : "system";
                        eventPublisher.publishEvent(
                                        new BucketDeleteBatch.TempBucketScheduleEvent(
                                                        response.getId().toString(),
                                                        tempBucketName,
                                                        username));
                        log.info("6단계: 임시 버킷 삭제 스케줄러 시작 이벤트 발행 - datasourceId: {}, tempBucket: {}, username: {}",
                                        response.getId(), tempBucketName, username);
                }

                // Map<String, Object>를 DataCtlgTrainingDataCreateRes로 변환
                Object datasetIdObj = result.get("dataset_id");
                String datasetId = null;
                if (datasetIdObj != null) {
                        if (datasetIdObj instanceof UUID) {
                                datasetId = ((UUID) datasetIdObj).toString();
                        } else {
                                datasetId = datasetIdObj.toString();
                        }
                }

                Object datasourceIdObj = result.get("datasource_id");
                String datasourceId = null;
                if (datasourceIdObj != null) {
                        if (datasourceIdObj instanceof UUID) {
                                datasourceId = ((UUID) datasourceIdObj).toString();
                        } else {
                                datasourceId = datasourceIdObj.toString();
                        }
                }

                Object successCountObj = result.get("success_count");
                Integer successCount = 0;
                if (successCountObj instanceof Integer) {
                        successCount = (Integer) successCountObj;
                } else if (successCountObj instanceof Number) {
                        successCount = ((Number) successCountObj).intValue();
                }

                // matched count 우선 사용, 없으면 successCount 사용
                Integer matchedCount = successCount;
                Object matchedCountObj = result.get("matched_count");
                if (matchedCountObj instanceof Integer) {
                        matchedCount = (Integer) matchedCountObj;
                } else if (matchedCountObj instanceof Number) {
                        matchedCount = ((Number) matchedCountObj).intValue();
                }

                Object fileNamesObj = result.get("file_names");
                List<String> fileNames = new ArrayList<>();
                if (fileNamesObj instanceof List) {
                        fileNames = (List<String>) fileNamesObj;
                } else if (fileNamesObj instanceof String) {
                        String s = (String) fileNamesObj;
                        for (String part : s.split(",")) {
                                if (part != null && !part.trim().isEmpty()) {
                                        fileNames.add(part.trim());
                                }
                        }
                }

                // matchedKeys 추출
                List<String> matchedKeys = new ArrayList<>();
                Object matchedKeysObj = result.get("matched_keys");
                if (matchedKeysObj instanceof List) {
                        matchedKeys = (List<String>) matchedKeysObj;
                }

                return DataCtlgTrainingDataCreateRes.builder()
                                .success(true)
                                .trainingDataId(datasetId)
                                .name((String) result.get("dataset_name"))
                                .type((String) result.get("dataset_type"))
                                .processedFileCount(successCount)
                                .processedFiles(fileNames)
                                .failedFiles(new ArrayList<>())
                                .tempBucketName((String) result.get("temp_bucket_name"))
                                .inputFileNames(fileNames)
                                .matchedCount(matchedCount)
                                .matchedKeys(matchedKeys)
                                .message("학습 데이터 생성이 성공적으로 완료되었습니다")
                                .creationTimeMs(System.currentTimeMillis())
                                .datasourceId(datasourceId)
                                .datasetId(datasetId)
                                .build();

        }

        /**
         * 기본 정책 생성
         */
        private List<Object> createDefaultPolicy() {
                List<Object> policyList = new ArrayList<>();
                Map<String, Object> policyItem = new HashMap<>();
                policyItem.put("cascade", false);
                policyItem.put("decision_strategy", "UNANIMOUS");
                policyItem.put("logic", "POSITIVE");

                List<Object> policies = new ArrayList<>();
                Map<String, Object> policyDetail = new HashMap<>();
                policyDetail.put("logic", "POSITIVE");
                policyDetail.put("names", List.of("admin"));
                policyDetail.put("type", "user");
                policies.add(policyDetail);

                policyItem.put("policies", policies);
                policyItem.put("scopes", List.of("GET", "POST", "PUT", "DELETE"));
                policyList.add(policyItem);

                return policyList;
        }

        /**
         * 학습 데이터셋 생성 (커스텀인 경우)
         * 
         * @param file        업로드할 파일 (sourceType이 'file'인 경우)
         * @param projectId   프로젝트 ID
         * @param name        데이터셋 이름
         * @param type        데이터셋 타입
         * @param sourceType  데이터소스 타입 ('s3' 또는 'file')
         * @param fileName    파일명 (sourceType이 's3'인 경우 필수, 예: test.zip)
         * @param createdBy   생성자
         * @param payload     페이로드
         * @param status      상태
         * @param tags        태그
         * @param updatedBy   수정자
         * @param description 설명
         * @return 데이터셋 업로드 결과
         */
        @Transactional
        @Override
        public DataCtlgCustomTrainingDataCreateRes createCustomTrainingDataset(
                        MultipartFile file,
                        String projectId,
                        String name,
                        String type,
                        String sourceType,
                        String fileName,
                        String createdBy,
                        String payload,
                        String status,
                        String tags,
                        String updatedBy,
                        String description) {

                // 데이터셋 타입 기본값 설정
                String actualDatasetType = type;
                if (actualDatasetType == null || actualDatasetType.trim().isEmpty()) {
                        actualDatasetType = "custom";
                }

                // sourceType 검증 및 기본값 설정
                String actualSourceType = sourceType;
                if (actualSourceType == null || actualSourceType.trim().isEmpty()) {
                        // file 파라미터가 있으면 "file", 없으면 "s3"
                        if (file != null && !file.isEmpty()) {
                                actualSourceType = "file";
                        } else {
                                actualSourceType = "s3"; // 기본값: s3
                        }
                }

                // sourceType이 'file'인 경우 file 필수 체크
                if ("file".equals(actualSourceType) && (file == null || file.isEmpty())) {
                        throw new IllegalArgumentException(">>> 업로드할 파일이 비어 있습니다. (type이 'file'인 경우 필수)");
                }

                // sourceType이 's3'인 경우 fileName 필수 체크
                if ("s3".equals(actualSourceType) && (fileName == null || fileName.trim().isEmpty())) {
                        throw new IllegalArgumentException(">>> 파일명이 비어 있습니다. (type이 's3'인 경우 필수)");
                }

                log.info("커스텀 학습 데이터셋 생성 시작 - fileName: {}, datasetName: {}, datasetType: {}, sourceType: {}, s3FileName: {}",
                                file != null ? file.getOriginalFilename() : null, name, actualDatasetType,
                                actualSourceType, fileName);

                long startTime = System.currentTimeMillis();

                // 각 단계별 결과를 담을 객체들
                StepResult uploadStep = null;
                StepResult downloadStep = null;

                try {
                        // sourceType에 따른 분기 처리 (가져오기 방법)
                        String importType = actualSourceType; // 가져오기 방법 타입 (file 또는 s3)

                        log.info("처리 타입 (가져오기 방법): {}", importType);

                        DataCtlgCustomTrainingDataCreateRes result = null;

                        if ("file".equals(importType)) {
                                // 파일 업로드 타입: 기존 로직 사용
                                // 매퍼를 사용하여 파라미터를 DTO로 변환
                                DataCtlgDatasetUploadReq request = dataCtlgDataSetMapper.toDatasetUploadReq(
                                                file, projectId, name, actualDatasetType, createdBy, payload, status,
                                                tags, updatedBy,
                                                description);

                                // 기존 uploadDatasetFile 메서드를 단계별 결과 포함 버전으로 호출
                                result = uploadDatasetFileWithSteps(file, request, startTime);
                        } else {
                                // S3 타입: S3에서 파일 다운로드 후 처리
                                File tempFile = null;
                                String tempFilePath = null;

                                try {
                                        // 1. S3에서 파일 다운로드
                                        long stepStartTime = System.currentTimeMillis();

                                        // S3에서 임시 저장소로 파일 다운로드
                                        Map<String, Object> downloadResult = downloadFileFromS3ToTempStorage(fileName,
                                                        null);
                                        tempFile = (File) downloadResult.get("tempFile");
                                        tempFilePath = tempFile != null ? tempFile.getAbsolutePath() : null;

                                        downloadStep = StepResult.builder()
                                                        .success(true)
                                                        .message("S3에서 파일 다운로드가 성공적으로 완료되었습니다")
                                                        .result(downloadResult)
                                                        .processingTimeMs(System.currentTimeMillis() - stepStartTime)
                                                        .build();

                                        log.info(">>> S3 파일 다운로드 완료 - fileName: {}, tempFile: {}",
                                                        fileName, tempFilePath);

                                        // 2. 다운로드한 파일을 MultipartFile로 변환하여 uploadFile API 호출
                                        long uploadStepStartTime = System.currentTimeMillis();
                                        boolean apiCallSuccess = false;
                                        MultipartFile multipartFile = null;
                                        Closeable closeableMultipartFile = null;

                                        try {
                                                // File을 MultipartFile로 변환
                                                multipartFile = convertFileToMultipartFile(tempFile, fileName);
                                                if (multipartFile instanceof Closeable) {
                                                        closeableMultipartFile = (Closeable) multipartFile;
                                                }

                                                // 사용자 정보 조회 및 기본값 설정
                                                String userId = tokenInfo.getUserName() != null
                                                                ? tokenInfo.getUserName()
                                                                : "admin";

                                                // 기본값 설정
                                                String datasetName = name != null ? name : fileName;
                                                String datasetType = actualDatasetType;
                                                String datasetStatus = status != null && !status.isEmpty() ? status
                                                                : "processing";
                                                String datasetDescription = description != null ? description
                                                                : "S3에서 다운로드한 파일로 생성된 데이터셋";
                                                String datasetProjectId = projectId != null && !projectId.isEmpty()
                                                                ? projectId
                                                                : getCurrentProjectId();
                                                String datasetCreatedBy = createdBy != null && !createdBy.isEmpty()
                                                                ? createdBy
                                                                : userId;
                                                String datasetUpdatedBy = updatedBy != null && !updatedBy.isEmpty()
                                                                ? updatedBy
                                                                : userId;

                                                // tags는 프론트에서 전송한 JSON 배열 형태 그대로 사용
                                                String datasetTags = tags != null ? tags : "";

                                                // payload 형식 수정: 빈 객체 대신 null 또는 유효한 JSON
                                                String datasetPayload = payload != null && !payload.isEmpty()
                                                                && !payload.equals("{}") ? payload : null;

                                                log.info("데이터셋 업로드 파라미터 - name: {}, type: {}, status: {}, projectId: {}, createdBy: {}, tags: {}",
                                                                datasetName, datasetType, datasetStatus,
                                                                datasetProjectId, datasetCreatedBy, datasetTags);

                                                // SKT AI API 호출 (파일 업로드) - POST /api/v1/datasets/upload/files
                                                Object uploadResult = sktaiDataDatasetsService.uploadFile(
                                                                multipartFile, datasetName, datasetType, datasetStatus,
                                                                datasetDescription,
                                                                datasetTags, datasetProjectId, datasetCreatedBy,
                                                                datasetUpdatedBy, datasetPayload);

                                                log.info("SKT AI API 호출 완료 - result: {}", uploadResult);
                                                log.info("SKT AI API 응답 타입: {}",
                                                                uploadResult != null ? uploadResult.getClass().getName()
                                                                                : "null");

                                                Map<String, Object> uploadResultMap = new HashMap<>();
                                                uploadResultMap.put("apiResponse", uploadResult);
                                                uploadResultMap.put("responseType",
                                                                uploadResult != null ? uploadResult.getClass().getName()
                                                                                : "null");
                                                uploadResultMap.put("fileName", fileName);
                                                uploadResultMap.put("fileSize", tempFile.length());
                                                uploadResultMap.put("datasetName", datasetName);
                                                uploadResultMap.put("datasetType", datasetType);

                                                uploadStep = StepResult.builder()
                                                                .success(true)
                                                                .message("SKT AI API 호출이 성공적으로 완료되었습니다")
                                                                .result(uploadResultMap)
                                                                .processingTimeMs(System.currentTimeMillis()
                                                                                - uploadStepStartTime)
                                                                .build();

                                                // API 응답 처리
                                                if (uploadResult instanceof com.skax.aiplatform.client.sktai.data.dto.response.Dataset) {
                                                        com.skax.aiplatform.client.sktai.data.dto.response.Dataset dataset = (com.skax.aiplatform.client.sktai.data.dto.response.Dataset) uploadResult;

                                                        // Dataset을 DataCtlgCustomTrainingDataCreateRes로 변환
                                                        result = dataCtlgDataSetMapper
                                                                        .toCustomTrainingDataCreateRes(dataset);

                                                        // Dataset ADXP 권한부여
                                                        adminAuthService.setResourcePolicyByCurrentGroup(
                                                                        "/datasets/" + dataset.getId());
                                                        adminAuthService.setResourcePolicyByCurrentGroup(
                                                                        "/datasets/" + dataset.getId()
                                                                                        + "/tags");
                                                        log.info("@@@@ Dataset ADXP 권한부여 후 - datasetId: {}",
                                                                        dataset.getId());
                                                        // 전체 성공 여부 확인
                                                        boolean overallSuccess = downloadStep.getSuccess()
                                                                        && uploadStep.getSuccess();

                                                        result.setSuccess(overallSuccess);
                                                        result.setMessage(
                                                                        overallSuccess ? "커스텀 학습 데이터 생성이 성공적으로 완료되었습니다"
                                                                                        : "일부 단계에서 실패가 발생했습니다");
                                                        result.setCreationTimeMs(
                                                                        System.currentTimeMillis() - startTime);
                                                        result.setUploadStep(uploadStep);

                                                        log.info("데이터셋 파일 업로드 완료 (S3 타입) - datasetId: {}, name: {}, overallSuccess: {}",
                                                                        result.getId(), result.getName(),
                                                                        overallSuccess);

                                                        // API 호출 성공 여부 설정
                                                        apiCallSuccess = overallSuccess;

                                                } else {
                                                        // Dataset이 아닌 경우 기본 응답
                                                        log.warn("예상치 못한 응답 타입: {}",
                                                                        uploadResult != null
                                                                                        ? uploadResult.getClass()
                                                                                                        .getName()
                                                                                        : "null");

                                                        result = DataCtlgCustomTrainingDataCreateRes.builder()
                                                                        .id(UUID.randomUUID())
                                                                        .name("Unknown Dataset")
                                                                        .type("custom")
                                                                        .status("processing")
                                                                        .isDeleted(false)
                                                                        .success(true)
                                                                        .message("커스텀 학습 데이터 생성이 완료되었습니다 (기본 응답)")
                                                                        .creationTimeMs(System.currentTimeMillis()
                                                                                        - startTime)
                                                                        .uploadStep(uploadStep)
                                                                        .build();

                                                        // API 호출 성공 여부 설정
                                                        apiCallSuccess = result.getSuccess() && uploadStep != null
                                                                        && uploadStep.getSuccess();
                                                }

                                        } catch (BusinessException e) {
                                                log.error(">>> SKT AI API 호출 실패 (BusinessException) - fileName: {}, error: {}",
                                                                fileName,
                                                                e.getMessage(), e);
                                                throw e;
                                        } catch (FeignException e) {
                                                log.error(">>> SKT AI API 호출 실패 (FeignException) - fileName: {}, 상태코드: {}, error: {}",
                                                                fileName,
                                                                e.status(), e.getMessage(), e);
                                                throw e;
                                        } catch (RuntimeException e) {
                                                log.error(">>> SKT AI API 호출 실패 (RuntimeException) - fileName: {}, error: {}",
                                                                fileName,
                                                                e.getMessage(), e);
                                                throw e;
                                        } catch (Exception e) {
                                                log.error(">>> SKT AI API 호출 실패 (Exception) - fileName: {}, error: {}",
                                                                fileName,
                                                                e.getMessage(), e);
                                                throw e;
                                        } finally {
                                                if (closeableMultipartFile != null) {
                                                        try {
                                                                closeableMultipartFile.close();
                                                        } catch (IOException closeException) {
                                                                log.warn(
                                                                                ">>> 임시 파일 스트림 닫기 실패 - fileName: {}, error: {}",
                                                                                fileName,
                                                                                closeException.getMessage(),
                                                                                closeException);
                                                        }
                                                }

                                                // API 호출 성공 시 임시 파일 삭제 (경로 기반으로 안전하게 삭제)
                                                if (apiCallSuccess && tempFilePath != null) {
                                                        cleanupTempFileByPath(tempFilePath);
                                                } else if (tempFile != null) {
                                                        // 경로가 없는 경우 File 객체로 삭제 시도
                                                        cleanupTempFile(tempFile);
                                                }
                                        }

                                } catch (BusinessException e) {
                                        log.error(">>> S3 파일 다운로드 실패 (BusinessException) - fileName: {}, error: {}",
                                                        fileName,
                                                        e.getMessage(), e);
                                        throw e;
                                } catch (FeignException e) {
                                        log.error(">>> S3 파일 다운로드 실패 (FeignException) - fileName: {}, 상태코드: {}, error: {}",
                                                        fileName,
                                                        e.status(), e.getMessage(), e);
                                        throw e;
                                } catch (RuntimeException e) {
                                        log.error(">>> S3 파일 다운로드 실패 (RuntimeException) - fileName: {}, error: {}",
                                                        fileName,
                                                        e.getMessage(), e);
                                        throw e;
                                } catch (Exception e) {
                                        log.error(">>> S3 파일 다운로드 실패 (Exception) - fileName: {}, error: {}", fileName,
                                                        e.getMessage(), e);
                                        throw e;
                                } finally {
                                        // S3 다운로드 실패 시 임시 파일 정리
                                        // result가 null이거나 실패한 경우에만 삭제
                                        if (tempFile != null && (result == null || !result.getSuccess())) {
                                                cleanupTempFile(tempFile);
                                        }
                                }
                        }

                        // 응답에 따라 적절한 메시지 설정
                        if (result.getSuccess() != null && result.getSuccess()) {
                                result.setMessage("커스텀 학습 데이터 생성이 성공적으로 완료되었습니다");
                        } else {
                                if (result.getMessage() == null || result.getMessage().trim().isEmpty()) {
                                        result.setMessage("커스텀 학습 데이터 생성 중 실패가 발생했습니다");
                                }
                        }

                        return result;

                } catch (BusinessException e) {
                        log.error("커스텀 학습 데이터셋 생성 실패 (BusinessException) - fileName: {}, error: {}",
                                        file != null ? file.getOriginalFilename() : fileName, e.getMessage(), e);
                        throw e;
                } catch (FeignException e) {
                        log.error("커스텀 학습 데이터셋 생성 실패 (FeignException) - fileName: {}, 상태코드: {}, error: {}",
                                        file != null ? file.getOriginalFilename() : fileName, e.status(),
                                        e.getMessage(), e);
                        throw e;
                } catch (RuntimeException e) {
                        log.error("커스텀 학습 데이터셋 생성 실패 (RuntimeException) - fileName: {}, error: {}",
                                        file != null ? file.getOriginalFilename() : fileName, e.getMessage(), e);
                        throw e;
                } catch (Exception e) {
                        log.error("커스텀 학습 데이터셋 생성 실패 (Exception) - fileName: {}, error: {}",
                                        file != null ? file.getOriginalFilename() : fileName, e.getMessage(), e);
                        throw e;
                }
        }

        /**
         * 데이터셋 파일 업로드 (단일 파일) - 단계별 결과 포함
         *
         * @param file      업로드할 파일
         * @param request   데이터셋 업로드 요청 정보
         * @param startTime 시작 시간
         * @return 데이터셋 업로드 결과
         */
        public DataCtlgCustomTrainingDataCreateRes uploadDatasetFileWithSteps(MultipartFile file,
                        DataCtlgDatasetUploadReq request, long startTime) {
                log.info("데이터셋 파일 업로드 시작 (단계별 결과 포함) - fileName: {}, datasetName: {}, type: {}",
                                file.getOriginalFilename(), request.getName(), request.getType());

                StepResult uploadStep = null;

                try {
                        // 사용자 정보 조회 및 기본값 설정
                        String userId = tokenInfo.getUserName() != null ? tokenInfo.getUserName() : "system";

                        // 기본값 설정
                        String name = request.getName() != null ? request.getName() : file.getOriginalFilename();
                        String type = request.getType() != null ? request.getType() : "custom";
                        String status = request.getStatus() != null && !request.getStatus().isEmpty()
                                        ? request.getStatus()
                                        : "processing";
                        String description = request.getDescription() != null ? request.getDescription()
                                        : "파일 업로드를 통해 생성된 데이터셋";
                        String projectId = request.getProjectId() != null && !request.getProjectId().isEmpty()
                                        ? request.getProjectId()
                                        : getCurrentProjectId();
                        String createdBy = request.getCreatedBy() != null && !request.getCreatedBy().isEmpty()
                                        ? request.getCreatedBy()
                                        : userId;
                        String updatedBy = request.getUpdatedBy() != null && !request.getUpdatedBy().isEmpty()
                                        ? request.getUpdatedBy()
                                        : userId;

                        // tags는 프론트에서 전송한 JSON 배열 형태 그대로 사용
                        String tags = request.getTags() != null ? request.getTags() : "";

                        // payload 형식 수정: 빈 객체 대신 null 또는 유효한 JSON
                        String payload = request.getPayload() != null && !request.getPayload().isEmpty()
                                        && !request.getPayload().equals("{}")
                                                        ? request.getPayload()
                                                        : null;

                        log.info("데이터셋 업로드 파라미터 - name: {}, type: {}, status: {}, projectId: {}, createdBy: {}, tags: {}",
                                        name, type, status, projectId, createdBy, tags);

                        // SKT AI API 호출 (파일 업로드)
                        long stepStartTime = System.currentTimeMillis();
                        try {
                                // SKT AI API 호출
                                Object result = sktaiDataDatasetsService.uploadFile(
                                                file, name, type, status, description, tags, projectId,
                                                createdBy, updatedBy, payload);

                                log.info("SKT AI API 호출 완료 - result: {}", result);
                                log.info("SKT AI API 응답 타입: {}", result != null ? result.getClass().getName() : "null");

                                Map<String, Object> uploadResult = new HashMap<>();
                                uploadResult.put("apiResponse", result);
                                uploadResult.put("responseType", result != null ? result.getClass().getName() : "null");
                                uploadResult.put("fileName", file.getOriginalFilename());
                                uploadResult.put("fileSize", file.getSize());
                                uploadResult.put("datasetName", name);
                                uploadResult.put("datasetType", type);

                                uploadStep = StepResult.builder()
                                                .success(true)
                                                .message("SKT AI API 호출이 성공적으로 완료되었습니다")
                                                .result(uploadResult)
                                                .processingTimeMs(System.currentTimeMillis() - stepStartTime)
                                                .build();

                        } catch (BusinessException e) {
                                log.error("SKT AI API 호출 실패 (BusinessException)", e);
                                Map<String, Object> error = new HashMap<>();
                                error.put("errorCode", "API_CALL_FAILED");
                                error.put("errorMessage", e.getMessage());

                                uploadStep = StepResult.builder()
                                                .success(false)
                                                .message("SKT AI API 호출에 실패했습니다")
                                                .error(error)
                                                .processingTimeMs(System.currentTimeMillis() - stepStartTime)
                                                .build();
                                throw e;
                        } catch (FeignException e) {
                                log.error("SKT AI API 호출 실패 (FeignException) - 상태코드: {}", e.status(), e);
                                Map<String, Object> error = new HashMap<>();
                                error.put("errorCode", "API_CALL_FAILED");
                                error.put("errorMessage", e.getMessage());

                                uploadStep = StepResult.builder()
                                                .success(false)
                                                .message("SKT AI API 호출에 실패했습니다")
                                                .error(error)
                                                .processingTimeMs(System.currentTimeMillis() - stepStartTime)
                                                .build();
                                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                                                String.format("SKT AI API 호출에 실패했습니다: HTTP %d - %s", e.status(),
                                                                e.getMessage()));
                        } catch (RuntimeException e) {
                                log.error("SKT AI API 호출 실패 (RuntimeException)", e);
                                Map<String, Object> error = new HashMap<>();
                                error.put("errorCode", "API_CALL_FAILED");
                                error.put("errorMessage", e.getMessage());

                                uploadStep = StepResult.builder()
                                                .success(false)
                                                .message("SKT AI API 호출에 실패했습니다")
                                                .error(error)
                                                .processingTimeMs(System.currentTimeMillis() - stepStartTime)
                                                .build();
                                throw e;
                        } catch (Exception e) {
                                log.error("SKT AI API 호출 실패 (Exception)", e);
                                Map<String, Object> error = new HashMap<>();
                                error.put("errorCode", "API_CALL_FAILED");
                                error.put("errorMessage", e.getMessage());

                                uploadStep = StepResult.builder()
                                                .success(false)
                                                .message("SKT AI API 호출에 실패했습니다")
                                                .error(error)
                                                .processingTimeMs(System.currentTimeMillis() - stepStartTime)
                                                .build();
                                throw e;
                        }

                        // API 응답 처리
                        Object result = uploadStep.getResult().get("apiResponse");

                        // Dataset 응답인 경우 변환
                        if (result instanceof com.skax.aiplatform.client.sktai.data.dto.response.Dataset) {
                                com.skax.aiplatform.client.sktai.data.dto.response.Dataset dataset = (com.skax.aiplatform.client.sktai.data.dto.response.Dataset) result;

                                // Dataset을 DataCtlgCustomTrainingDataCreateRes로 변환
                                DataCtlgCustomTrainingDataCreateRes response = DataCtlgCustomTrainingDataCreateRes
                                                .builder()
                                                .id(dataset.getId())
                                                .name(dataset.getName())
                                                .type(dataset.getType())
                                                .description(dataset.getDescription())
                                                .tags(dataset.getTags() != null ? dataset.getTags().stream()
                                                                .map(tag -> DataCtlgCustomTrainingDataCreateRes.DatasetTag
                                                                                .builder()
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
                                // Dataset ADXP 권한부여
                                adminAuthService.setResourcePolicyByCurrentGroup(
                                                "/datasets/" + (dataset.getId()));
                                adminAuthService.setResourcePolicyByCurrentGroup(
                                                "/datasets/"
                                                                + (dataset.getId()) + "/tags");

                                log.info("@@@@ dataset create response: {}", dataset);
                                log.info("@@@@ Dataset ADXP 권한부여 후 - datasetId: {}", dataset.getId());

                                // 전체 성공 여부 확인
                                boolean overallSuccess = uploadStep.getSuccess();

                                response.setSuccess(overallSuccess);
                                response.setMessage(overallSuccess ? "커스텀 학습 데이터 생성이 성공적으로 완료되었습니다"
                                                : "SKT AI API 호출에 실패했습니다");
                                response.setCreationTimeMs(System.currentTimeMillis() - startTime);
                                response.setUploadStep(uploadStep);

                                log.info("데이터셋 파일 업로드 완료 (단계별 결과 포함) - datasetId: {}, name: {}, overallSuccess: {}",
                                                response.getId(), response.getName(), overallSuccess);

                                return response;
                        }

                        // Dataset이 아닌 경우 기본 응답
                        log.warn("예상치 못한 응답 타입: {}", result != null ? result.getClass().getName() : "null");

                        DataCtlgCustomTrainingDataCreateRes response = DataCtlgCustomTrainingDataCreateRes.builder()
                                        .id(UUID.randomUUID())
                                        .name("Unknown Dataset")
                                        .type("custom")
                                        .status("processing")
                                        .isDeleted(false)
                                        .success(true)
                                        .message("커스텀 학습 데이터 생성이 완료되었습니다 (기본 응답)")
                                        .creationTimeMs(System.currentTimeMillis() - startTime)
                                        .uploadStep(uploadStep)
                                        .build();

                        return response;

                } catch (BusinessException e) {
                        log.error("데이터셋 파일 업로드 실패 (단계별 결과 포함) (BusinessException) - fileName: {}, error: {}",
                                        file.getOriginalFilename(), e.getMessage(), e);
                        throw e;
                } catch (FeignException e) {
                        log.error("데이터셋 파일 업로드 실패 (단계별 결과 포함) (FeignException) - fileName: {}, 상태코드: {}, error: {}",
                                        file.getOriginalFilename(), e.status(), e.getMessage(), e);
                        throw e;
                } catch (RuntimeException e) {
                        log.error("데이터셋 파일 업로드 실패 (단계별 결과 포함) (RuntimeException) - fileName: {}, error: {}",
                                        file.getOriginalFilename(), e.getMessage(), e);
                        throw e;
                } catch (Exception e) {
                        log.error("데이터셋 파일 업로드 실패 (단계별 결과 포함) (Exception) - fileName: {}, error: {}",
                                        file.getOriginalFilename(), e.getMessage(), e);
                        throw e;
                }
        }

        /**
         * 데이터셋 파일 업로드 (단일 파일) - 기존 로직
         *
         * @param file    업로드할 파일
         * @param request 데이터셋 업로드 요청 정보
         * @return 데이터셋 업로드 결과
         */
        public DataCtlgCustomTrainingDataCreateRes uploadDatasetFile(MultipartFile file,
                        DataCtlgDatasetUploadReq request) {
                log.info("데이터셋 파일 업로드 시작 - fileName: {}, datasetName: {}, type: {}",
                                file.getOriginalFilename(), request.getName(), request.getType());

                try {
                        // 1. 사용자 정보 조회
                        String userId = tokenInfo.getUserName() != null ? tokenInfo.getUserName() : "system";

                        // 2. 기본값 설정
                        String name = request.getName() != null ? request.getName() : file.getOriginalFilename();
                        String type = request.getType() != null ? request.getType() : "custom";
                        String status = request.getStatus() != null && !request.getStatus().isEmpty()
                                        ? request.getStatus()
                                        : "processing";
                        String description = request.getDescription() != null ? request.getDescription()
                                        : "파일 업로드를 통해 생성된 데이터셋";
                        String projectId = request.getProjectId() != null && !request.getProjectId().isEmpty()
                                        ? request.getProjectId()
                                        : getCurrentProjectId();
                        String createdBy = request.getCreatedBy() != null && !request.getCreatedBy().isEmpty()
                                        ? request.getCreatedBy()
                                        : userId;
                        String updatedBy = request.getUpdatedBy() != null && !request.getUpdatedBy().isEmpty()
                                        ? request.getUpdatedBy()
                                        : userId;

                        // tags는 프론트에서 전송한 JSON 배열 형태 그대로 사용
                        String tags = request.getTags() != null ? request.getTags() : "";

                        // payload 형식 수정: 빈 객체 대신 null 또는 유효한 JSON
                        String payload = request.getPayload() != null && !request.getPayload().isEmpty()
                                        && !request.getPayload().equals("{}")
                                                        ? request.getPayload()
                                                        : null;

                        log.info("데이터셋 업로드 파라미터 - name: {}, type: {}, status: {}, projectId: {}, createdBy: {}, tags: {}",
                                        name, type, status, projectId, createdBy, tags);

                        // 3. SKT AI API 호출
                        Object result = sktaiDataDatasetsService.uploadFile(
                                        file, name, type, status, description, tags, projectId,
                                        createdBy, updatedBy, payload);

                        log.info("SKT AI API 호출 완료 - result: {}", result);
                        log.info("SKT AI API 응답 타입: {}", result != null ? result.getClass().getName() : "null");

                        // 테스트용: API 응답을 그대로 반환
                        if (result instanceof com.skax.aiplatform.client.sktai.data.dto.response.Dataset) {
                                com.skax.aiplatform.client.sktai.data.dto.response.Dataset dataset = (com.skax.aiplatform.client.sktai.data.dto.response.Dataset) result;

                                // Dataset을 DataCtlgCustomTrainingDataCreateRes로 변환
                                DataCtlgCustomTrainingDataCreateRes response = DataCtlgCustomTrainingDataCreateRes
                                                .builder()
                                                .id(dataset.getId())
                                                .name(dataset.getName())
                                                .type(dataset.getType())
                                                .description(dataset.getDescription())
                                                .tags(dataset.getTags() != null ? dataset.getTags().stream()
                                                                .map(tag -> DataCtlgCustomTrainingDataCreateRes.DatasetTag
                                                                                .builder()
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

                                // Dataset ADXP 권한부여
                                adminAuthService.setResourcePolicyByCurrentGroup(
                                                "/datasets/" + dataset.getId());
                                adminAuthService.setResourcePolicyByCurrentGroup(
                                                "/datasets/" + dataset.getId() + "/tags");

                                log.info("@@@@ dataset create response: {}", dataset);
                                log.info("@@@@ Dataset ADXP 권한부여 후 - datasetId: {}", dataset.getId());

                                log.info("데이터셋 파일 업로드 완료 - datasetId: {}, name: {}",
                                                response.getId(), response.getName());

                                return response;
                        }

                        // Dataset이 아닌 경우 기본 응답
                        log.warn("예상치 못한 응답 타입: {}", result != null ? result.getClass().getName() : "null");
                        return DataCtlgCustomTrainingDataCreateRes.builder()
                                        .id(UUID.randomUUID())
                                        .name("Unknown Dataset")
                                        .type("custom")
                                        .status("processing")
                                        .isDeleted(false)
                                        .build();

                } catch (BusinessException e) {
                        log.error("데이터셋 파일 업로드 실패 (BusinessException) - fileName: {}, error: {}",
                                        file.getOriginalFilename(), e.getMessage(), e);
                        throw e;
                } catch (FeignException e) {
                        log.error("데이터셋 파일 업로드 실패 (FeignException) - fileName: {}, 상태코드: {}, error: {}",
                                        file.getOriginalFilename(), e.status(), e.getMessage(), e);
                        throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                                        String.format("데이터셋 파일 업로드에 실패했습니다: HTTP %d - %s", e.status(), e.getMessage()));
                } catch (RuntimeException e) {
                        log.error("데이터셋 파일 업로드 실패 (RuntimeException) - fileName: {}, error: {}",
                                        file.getOriginalFilename(), e.getMessage(), e);
                        throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                                        "데이터셋 파일 업로드에 실패했습니다: " + e.getMessage());
                } catch (Exception e) {
                        log.error("데이터셋 파일 업로드 실패 (Exception) - fileName: {}, error: {}",
                                        file.getOriginalFilename(), e.getMessage(), e);
                        throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                                        "데이터셋 파일 업로드에 실패했습니다: " + e.getMessage());
                }
        }

        /**
         * 현재 사용자 정보 조회
         *
         * @return 현재 사용자명 또는 기본값
         */
        private String getCurrentUser() {
                try {
                        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                        if (authentication != null && authentication.isAuthenticated() &&
                                        !"anonymousUser".equals(authentication.getName())) {
                                return authentication.getName();
                        }
                } catch (SecurityException e) {
                        log.warn("현재 사용자 정보를 가져올 수 없습니다 (SecurityException): {}", e.getMessage());
                } catch (RuntimeException e) {
                        log.warn("현재 사용자 정보를 가져올 수 없습니다 (RuntimeException): {}", e.getMessage());
                } catch (Exception e) {
                        log.warn("현재 사용자 정보를 가져올 수 없습니다 (Exception): {}", e.getMessage());
                }
                return "admin"; // 기본값
        }

        /**
         * 현재 프로젝트 ID 조회
         *
         * @return 현재 프로젝트 ID
         */
        private String getCurrentProjectId() {
                try {
                        // 사용자가 접근 가능한 첫 번째 프로젝트 ID 조회
                        ClientsRead clientsRead = sktaiProjectService.getProjects(1, 1, null, null, null);

                        // 안전한 배열 접근을 위한 검증
                        if (clientsRead.getData() == null || clientsRead.getData().isEmpty()) {
                                log.warn("접근 가능한 프로젝트가 없습니다. 기본 프로젝트 ID 사용");
                                return "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5"; // 기본값
                        }

                        ClientRead firstClient = clientsRead.getData().get(0);
                        if (firstClient.getProject() == null || firstClient.getProject().getId() == null) {
                                log.warn("프로젝트 정보가 올바르지 않습니다. 기본 프로젝트 ID 사용");
                                return "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5"; // 기본값
                        }

                        String projectId = firstClient.getProject().getId();
                        log.debug("현재 프로젝트 ID 조회 성공: {}", projectId);
                        return projectId;

                } catch (BusinessException e) {
                        log.warn("프로젝트 ID 조회 실패 (BusinessException): {}. 기본 프로젝트 ID 사용", e.getMessage());
                        return "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5"; // 기본값
                } catch (FeignException e) {
                        log.warn("프로젝트 ID 조회 실패 (FeignException) - 상태코드: {}: {}. 기본 프로젝트 ID 사용", e.status(),
                                        e.getMessage());
                        return "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5"; // 기본값
                } catch (RuntimeException e) {
                        log.warn("프로젝트 ID 조회 실패 (RuntimeException): {}. 기본 프로젝트 ID 사용", e.getMessage());
                        return "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5"; // 기본값
                } catch (Exception e) {
                        log.warn("프로젝트 ID 조회 실패: {}. 기본 프로젝트 ID 사용", e.getMessage());
                        return "24ba585a-02fc-43d8-b9f1-f7ca9e020fe5"; // 기본값
                }
        }

        /**
         * 데이터셋 소스 아카이브 다운로드
         * 
         * <p>
         * 데이터셋의 원본 파일을 압축한 아카이브(ZIP/TAR)를 다운로드합니다.
         * 지원되는 데이터셋 타입: model_benchmark, rag_evaluation, custom
         * </p>
         * 
         * @param datasetId 데이터셋 고유 식별자 (UUID 형식)
         * @return 파일 스트림을 포함한 ResponseEntity (Content-Type: application/zip 또는
         *         application/x-tar)
         */
        @Override
        public ResponseEntity<Resource> getDatasetSourceArchive(UUID datasetId) {
                log.info("데이터셋 소스 아카이브 다운로드 요청 - datasetId: {}", datasetId);

                try {
                        ResponseEntity<Resource> response = sktaiDataDatasetsService.getDatasetSourceArchive(datasetId);

                        // 응답 헤더에서 파일명 추출 (로깅용)
                        String filename = "unknown";
                        if (response.getHeaders().getContentDisposition() != null &&
                                        response.getHeaders().getContentDisposition().getFilename() != null) {
                                filename = response.getHeaders().getContentDisposition().getFilename();
                        }

                        // Content-Type 확인 (로깅용)
                        String contentType = "unknown";
                        if (response.getHeaders() != null && response.getHeaders().getContentType() != null) {
                                contentType = response.getHeaders().getContentType().toString();
                        }

                        log.info("데이터셋 소스 아카이브 다운로드 완료 - datasetId: {}, filename: {}, contentType: {}",
                                        datasetId, filename, contentType);

                        return response;

                } catch (BusinessException e) {
                        throw handleException("데이터셋 소스 아카이브 다운로드", e);
                } catch (FeignException e) {
                        throw handleException("데이터셋 소스 아카이브 다운로드", e);
                } catch (RuntimeException e) {
                        throw handleException("데이터셋 소스 아카이브 다운로드", e);
                } catch (Exception e) {
                        throw handleException("데이터셋 소스 아카이브 다운로드", e);
                }
        }

        /**
         * File을 MultipartFile로 변환 (스트림 방식 - 대용량 파일 지원)
         *
         * 파일 전체를 메모리에 올리지 않고, 필요할 때만 스트림으로 읽어옵니다.
         * 대용량 파일 처리에 적합합니다.
         * 
         * @param file             변환할 File
         * @param originalFileName 원본 파일명
         * @return MultipartFile 객체
         */
        private MultipartFile convertFileToMultipartFile(File file, String originalFileName) {
                try {
                        String contentType = Files.probeContentType(file.toPath());
                        if (contentType == null) {
                                contentType = "application/octet-stream";
                        }

                        return new FileMultipartFile("file", originalFileName, contentType, file);
                } catch (IOException e) {
                        log.error(">>> File을 MultipartFile로 변환 실패 - file: {}, error: {}",
                                        file.getAbsolutePath(), e.getMessage(), e);
                        throw new RuntimeException("File을 MultipartFile로 변환 중 오류가 발생했습니다: " + e.getMessage(), e);
                }
        }

        /**
         * File 기반의 MultipartFile 구현 (스트림 방식)
         *
         * 파일 전체를 메모리에 올리지 않고, 스트림 방식으로 처리합니다.
         * 대용량 파일 처리에 적합합니다.
         */
        private static class FileMultipartFile implements MultipartFile, Closeable {
                private static final Logger LOGGER = LoggerFactory.getLogger(FileMultipartFile.class);

                private final String name;
                private final String originalFilename;
                private final String contentType;
                private final File file;
                private CloseTrackingInputStream currentInputStream;

                public FileMultipartFile(String name, String originalFilename, String contentType, File file) {
                        this.name = name != null ? name : "file";
                        this.originalFilename = originalFilename != null ? originalFilename : "file";
                        this.contentType = contentType != null ? contentType : "application/octet-stream";
                        this.file = file;
                }

                @Override
                public String getName() {
                        return this.name;
                }

                @Override
                public String getOriginalFilename() {
                        return this.originalFilename;
                }

                @Override
                public String getContentType() {
                        return this.contentType;
                }

                @Override
                public boolean isEmpty() {
                        return this.file == null || !this.file.exists() || this.file.length() == 0;
                }

                @Override
                public long getSize() {
                        return this.file != null && this.file.exists() ? this.file.length() : 0;
                }

                @Override
                public byte[] getBytes() throws IOException {
                        // 주의: 이 메서드는 전체 파일을 메모리에 올립니다.
                        // 대용량 파일의 경우 OutOfMemoryError가 발생할 수 있습니다.
                        // 가능하면 getInputStream()을 사용하는 것을 권장합니다.
                        if (this.file == null || !this.file.exists()) {
                                return new byte[0];
                        }
                        return Files.readAllBytes(this.file.toPath());
                }

                @Override
                public InputStream getInputStream() throws IOException {
                        // 스트림 방식으로 파일을 읽습니다 (메모리 효율적)
                        if (this.file == null || !this.file.exists()) {
                                return new java.io.ByteArrayInputStream(new byte[0]);
                        }

                        closeCurrentStream();

                        CloseTrackingInputStream stream = new CloseTrackingInputStream(
                                        new java.io.FileInputStream(this.file),
                                        this::clearCurrentStream);
                        this.currentInputStream = stream;
                        return stream;
                }

                @Override
                public void transferTo(File dest) throws IOException, IllegalStateException {
                        // 스트림 방식으로 파일 복사 (메모리 효율적)
                        if (this.file == null || !this.file.exists()) {
                                return;
                        }
                        try (InputStream inputStream = new java.io.FileInputStream(this.file);
                                        java.io.FileOutputStream outputStream = new java.io.FileOutputStream(dest)) {
                                byte[] buffer = new byte[8192]; // 8KB 버퍼
                                int bytesRead;
                                while ((bytesRead = inputStream.read(buffer)) != -1) {
                                        outputStream.write(buffer, 0, bytesRead);
                                }
                        }
                }

                @Override
                public void close() throws IOException {
                        closeCurrentStream();
                }

                private void closeCurrentStream() throws IOException {
                        if (this.currentInputStream != null) {
                                try {
                                        this.currentInputStream.close();
                                } finally {
                                        this.currentInputStream = null;
                                }
                        }
                }

                private void clearCurrentStream() {
                        this.currentInputStream = null;
                }

                private static class CloseTrackingInputStream extends FilterInputStream {
                        private final Runnable onClose;

                        protected CloseTrackingInputStream(InputStream in, Runnable onClose) {
                                super(in);
                                this.onClose = onClose;
                        }

                        @Override
                        public void close() throws IOException {
                                try {
                                        super.close();
                                } finally {
                                        if (this.onClose != null) {
                                                try {
                                                        this.onClose.run();
                                                } catch (RuntimeException runtimeException) {
                                                        LOGGER.warn(">>> CloseTrackingInputStream onClose 실행 중 오류 발생",
                                                                        runtimeException);
                                                }
                                        }
                                }
                        }
                }

        }

        /**
         * 임시 버킷 삭제를 위한 스케줄러 메서드
         *
         * @param datasourceId   데이터소스 ID
         * @param tempBucketName 임시 버킷명
         * @param username       현재 로그인된 사용자명
         * @return 삭제 여부 (true: 삭제 완료, false: 조건 미충족으로 스케줄 계속 진행)
         */
        public boolean checkAndDeleteTempBucket(String datasourceId, String tempBucketName, String username) {
                log.info(">>> 임시 버킷 삭제 스케줄러 시작 - datasourceId: {}, tempBucketName: {}, username: {}",
                                datasourceId, tempBucketName, username);

                // 현재 로그인된 사용자로 인증하기 위해 SecurityContextHolder에 사용자 정보 설정
                Authentication originalAuthentication = SecurityContextHolder.getContext().getAuthentication();
                try {
                        // SecurityContextHolder에 사용자 인증 정보 설정
                        UserDetails userDetails = User.builder()
                                        .username(username)
                                        .password("")
                                        .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                                        .build();
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.debug(">>> SecurityContextHolder에 사용자 인증 정보 설정 완료: {}", username);

                        // getDatasource/{datasourceId} 호출하여 status 확인
                        String datasourceStatus = sktaiDataDatasourcesService
                                        .getDatasource(UUID.fromString(datasourceId)).getStatus();

                        log.info(">>> GET datasource/{} 결과 status: {}", datasourceId, datasourceStatus);

                        // status 체크 - preparing이 아닌 경우 임시버킷 삭제 후 종료, preparing이면 스케줄 계속 진행
                        boolean shouldDelete = false;

                        // datasource status 체크: preparing이 아닌 경우 삭제
                        if (datasourceStatus != null && !datasourceStatus.equalsIgnoreCase("preparing")) {
                                shouldDelete = true;
                        }

                        if (shouldDelete) {
                                log.info(">>> 임시 버킷 삭제 조건 충족 - datasourceStatus: {} (preparing이 아님), tempBucketName: {}",
                                                datasourceStatus, tempBucketName);

                                // 조건 만족 시 버킷 삭제 메서드 호출
                                try {
                                        Map<String, Object> deleteResult = deleteTempBucket(tempBucketName);
                                        if (deleteResult != null && Boolean.TRUE.equals(deleteResult.get("success"))) {
                                                log.info(">>> 임시 버킷 삭제 성공 - tempBucketName: {}", tempBucketName);
                                        } else {
                                                log.warn(">>> 임시 버킷 삭제 실패 또는 버킷이 이미 없음 - tempBucketName: {}, result: {}",
                                                                tempBucketName, deleteResult);
                                        }
                                        return true;
                                } catch (BusinessException e) {
                                        log.error(">>> 임시 버킷 삭제 중 오류 발생 - tempBucketName: {}, error: {}",
                                                        tempBucketName, e.getMessage(), e);
                                        return true; // 오류 발생 시 스케줄 종료
                                } catch (RuntimeException e) {
                                        log.error(">>> 임시 버킷 삭제 중 오류 발생 - tempBucketName: {}, error: {}",
                                                        tempBucketName, e.getMessage(), e);
                                        return true; // 오류 발생 시 스케줄 종료
                                } catch (Exception e) {
                                        log.error(">>> 임시 버킷 삭제 중 오류 발생 - tempBucketName: {}, error: {}",
                                                        tempBucketName, e.getMessage(), e);
                                        return true; // 오류 발생 시 스케줄 종료
                                }
                        } else {
                                log.info(">>> 임시 버킷 삭제 조건 미충족 - datasourceStatus: {} (preparing 상태), 스케줄 계속 진행",
                                                datasourceStatus);
                                return false; // 조건 미충족, 스케줄 계속 진행
                        }

                } catch (BusinessException e) {
                        log.error(">>> 임시 버킷 삭제 스케줄러 실행 중 BusinessException 발생 -> 종료처리 - datasourceId: {}, tempBucketName: {}, error: {}",
                                        datasourceId, tempBucketName, e.getMessage(), e);
                        return true; // 오류 발생 시 스케줄 종료
                } catch (FeignException e) {
                        log.error(">>> 임시 버킷 삭제 스케줄러 실행 중 FeignException 발생 -> 종료처리 - datasourceId: {}, tempBucketName: {}, 상태코드: {}, error: {}",
                                        datasourceId, tempBucketName, e.status(), e.getMessage(), e);
                        return true; // 오류 발생 시 스케줄 종료
                } catch (RuntimeException e) {
                        log.error(">>> 임시 버킷 삭제 스케줄러 실행 중 RuntimeException 발생 - datasourceId: {}, tempBucketName: {}, error: {}",
                                        datasourceId, tempBucketName, e.getMessage(), e);
                        return true; // 오류 발생 시 스케줄 종료
                } catch (Exception e) {
                        log.error(">>> 임시 버킷 삭제 스케줄러 실행 중 예상치 못한 오류 발생 - datasourceId: {}, tempBucketName: {}, error: {}",
                                        datasourceId, tempBucketName, e.getMessage(), e);
                        return true; // 오류 발생 시 스케줄 종료
                } finally {
                        SecurityContextHolder.getContext().setAuthentication(originalAuthentication);
                }
        }

        /**
         * 데이터소스 Policy 설정
         * 
         * @param datasourceId 데이터소스 ID
         * @param memberId     사용자 ID
         * @param projectName  프로젝트명
         * @return 설정된 Policy 목록
         */

        @Override
        public List<PolicyRequest> setDataSourcePolicy(String datasourceId, String memberId, String projectName) {
                log.info("데이터소스 Policy 설정 요청 - datasourceId: {}, memberId: {}, projectName: {}", datasourceId, memberId,
                                projectName);

                // datasourceId 검증
                if (!StringUtils.hasText(datasourceId)) {
                        log.error("데이터소스 Policy 설정 실패 - datasourceId null이거나 비어있음");
                        throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "datasourceId는 필수입니다");
                }

                // memberId 검증
                if (!StringUtils.hasText(memberId)) {
                        log.error("데이터소스 Policy 설정 실패 - memberId가 null이거나 비어있음");
                        throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "사용자 ID는 필수입니다");
                }

                // projectName 검증
                if (!StringUtils.hasText(projectName)) {
                        log.error("데이터소스 Policy 설정 실패 - projectName이 null이거나 비어있음");
                        throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "프로젝트명은 필수입니다");
                }

                try {
                        // Policy 설정
                        adminAuthService.setResourcePolicyByMemberIdAndProjectName(
                                        "/api/v1/datasources/" + datasourceId, memberId, projectName);

                        adminAuthService.setResourcePolicyByMemberIdAndProjectName(
                                        "/api/v1/datasources/" + datasourceId + "/files", memberId, projectName);

                        log.info("데이터소스 Policy 설정 완료 - datasourceId: {}, memberId: {}, projectName: {}", datasourceId,
                                        memberId, projectName);

                        // 설정된 Policy 조회
                        List<PolicyRequest> policy = sktaiAuthService.getPolicy("/api/v1/datasources/" + datasourceId);

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

                        log.info("데이터소스 Policy 설정 완료 - datasourceId: {}, policy 개수: {} (필터링 전: {}, 필터링 후: {})",
                                        datasourceId,
                                        filteredPolicy.size(), policy.size(), filteredPolicy.size());
                        return filteredPolicy;

                } catch (BusinessException e) {
                        log.error("데이터소스 Policy 설정 실패 (BusinessException) - datasourceId: {}, errorCode: {}",
                                        datasourceId,
                                        e.getErrorCode(), e);
                        throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                                        "데이터소스 Policy 설정에 실패했습니다: " + e.getMessage());
                } catch (RuntimeException e) {
                        log.error("데이터소스 Policy 설정 실패 (RuntimeException) - datasourceId: {}, error: {}", datasourceId,
                                        e.getMessage(), e);
                        throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                                        "데이터소스 Policy 설정에 실패했습니다: " + e.getMessage());
                } catch (Exception e) {
                        log.error("데이터소스 Policy 설정 실패 (Exception) - datasourceId: {}", datasourceId, e);
                        throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                                        "데이터소스 Policy 설정에 실패했습니다: " + e.getMessage());
                }
        }

        /**
         * 학습 데이터셋 Policy 설정
         * 
         * @param datasetId   학습 데이터셋 ID
         * @param memberId    사용자 ID
         * @param projectName 프로젝트명
         * @return 설정된 Policy 목록
         */

        @Override
        public List<PolicyRequest> setDatasetPolicy(String datasetId, String memberId, String projectName) {
                log.info("학습 데이터셋 Policy 설정 요청 - datasetId: {}, memberId: {}, projectName: {}", datasetId, memberId,
                                projectName);

                // datasetId 검증
                if (!StringUtils.hasText(datasetId)) {
                        log.error("학습 데이터셋 Policy 설정 실패 - datasetId null이거나 비어있음");
                        throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "datasetId 필수입니다");
                }

                // memberId 검증
                if (!StringUtils.hasText(memberId)) {
                        log.error("학습 데이터셋 Policy 설정 실패 - memberId가 null이거나 비어있음");
                        throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "사용자 ID는 필수입니다");
                }

                // projectName 검증
                if (!StringUtils.hasText(projectName)) {
                        log.error("학습 데이터셋 Policy 설정 실패 - projectName이 null이거나 비어있음");
                        throw new BusinessException(ErrorCode.MISSING_REQUEST_PARAMETER, "프로젝트명은 필수입니다");
                }

                try {
                        // Policy 설정
                        adminAuthService.setResourcePolicyByMemberIdAndProjectName(
                                        "/api/v1/datasets/" + datasetId, memberId, projectName);

                        adminAuthService.setResourcePolicyByMemberIdAndProjectName(
                                        "/api/v1/datasets/" + datasetId + "/tags", memberId, projectName);

                        log.info("학습데이터셋 Policy 설정 완료 - datasetId: {}, memberId: {}, projectName: {}", datasetId,
                                        memberId, projectName);

                        // 설정된 Policy 조회
                        List<PolicyRequest> policy = sktaiAuthService.getPolicy("/api/v1/datasets/" + datasetId);

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

                        log.info("학습 데이터셋 Policy 설정 완료 - datasetId: {}, policy 개수: {} (필터링 전: {}, 필터링 후: {})",
                                        datasetId,
                                        filteredPolicy.size(), policy.size(), filteredPolicy.size());
                        return filteredPolicy;

                } catch (BusinessException e) {
                        log.error("학습 데이터셋 Policy 설정 실패 (BusinessException) - datasetId: {}, errorCode: {}",
                                        datasetId,
                                        e.getErrorCode(), e);
                        throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                                        "학습 데이터셋 Policy 설정에 실패했습니다: " + e.getMessage());
                } catch (RuntimeException e) {
                        log.error("학습 데이터셋 Policy 설정 실패 (RuntimeException) - datasetId: {}, error: {}", datasetId,
                                        e.getMessage(), e);
                        throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                                        "학습 데이터셋 Policy 설정에 실패했습니다: " + e.getMessage());
                } catch (Exception e) {
                        log.error("학습 데이터셋 Policy 설정 실패 (Exception) - datasetId: {}", datasetId, e);
                        throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR,
                                        "학습 데이터셋 Policy 설정에 실패했습니다: " + e.getMessage());
                }
        }
}