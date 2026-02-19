package com.skax.aiplatform.service.data;

import java.util.List;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.data.request.DataCtlgDataSetTag;
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

/**
 * 데이터 카탈로그 데이터셋 서비스 인터페이스
 * 
 * <p>
 * 데이터 카탈로그 데이터셋 관련 비즈니스 로직을 정의하는 서비스 인터페이스입니다.
 * </p>
 * 
 * @author HyeleeLee
 * @since 2025-08-19
 * @version 1.0
 */
public interface DataCtlgDataSetService {

        /**
         * 데이터셋 목록 조회
         * 
         * @param request 조회 요청 파라미터
         * @return 데이터셋 목록 응답 (Page 형식)
         */
        PageResponse<DataCtlgDataSetListRes> getDatasets(Integer page, Integer size, String sort, String filter,
                        String search);

        /**
         * 데이터셋 상세 조회
         * 
         * @param request 데이터셋 상세 조회 요청
         * @return 데이터셋 상세 정보
         */
        DataCtlgDataSetByIdRes getDatasetById(UUID datasetId);

        // /**
        // * 데이터셋 생성
        // *
        // * @param request 데이터셋 생성 요청
        // * @return 생성된 데이터셋 정보
        // */
        // DataCtlgDataSetCreateRes createDataset(DataCtlgDataSetCreateReq request);

        /**
         * 데이터셋 수정
         * 
         * @param request 데이터셋 수정 요청
         * @return 수정된 데이터셋 정보
         */
        DataCtlgDataSetUpdateRes updateDataset(UUID datasetId, DataCtlgDataSetUpdateReq request);

        /**
         * 데이터셋 삭제
         * 
         * @param request 데이터셋 삭제 요청
         * @return
         */
        void deleteDataSet(UUID datasetId, UUID datasourceId);

        /**
         * 커스텀 학습데이터셋 삭제
         * 
         * @param request 데이터셋 삭제 요청
         * @return
         */
        void deleteCustomDataSet(UUID datasetId);

        /**
         * 데이터소스 상세 조회
         * 
         * @param request 데이터셋 상세 조회 요청
         * @return 데이터셋 상세 정보
         */

        DataCtlgDataSourceByIdRes getDataSourceById(UUID datasourceId);

        // /**
        // * 데이터소스 생성
        // *
        // * @param request 데이터소스 생성 요청
        // * @return 생성된 데이터소스 정보
        // */
        // DataCtlgDataSourceCreateRes createDataSource(DataCtlgDataSourceCreateReq
        // request);

        /**
         * 데이터셋 태그 수정
         * 
         * @param request 데이터셋 태그 수정
         * @return 데이터셋 상세 정보
         */

        DataCtlgDataSetUpdateRes updateDatasetTags(UUID datasetId, List<DataCtlgDataSetTag> tags);

        /**
         * 데이터셋 태그 삭제
         * 
         * @param request 데이터셋 태그 삭제
         * @return 데이터셋 상세 정보
         */

        DataCtlgDataSetUpdateRes deleteDatasetTag(UUID datasetId, List<DataCtlgDataSetTag> tags);

        /**
         * 데이터셋 파일 업로드
         *
         * @param files 데이터셋 업로드 할 파일들
         * @return 데이터셋 파일 업로드 저장 정보
         */
        DataCtlgUploadFilesRes uploadDatasetFile(List<MultipartFile> files);

        /**
         * 데이터소스 파일 목록 조회
         *
         * @param datasourceId 데이터소스 ID
         * @param page         페이지 번호
         * @param size         페이지 크기
         * @return 데이터소스 파일 목록 응답 (PageResponse 형태)
         */
        PageResponse<DataCtlgDataSourceFileRes> getDataSourceFiles(String datasourceId, Integer page, Integer size);

        /**
         * UDP 등록을 위한 데이터소스 파일 처리
         *
         * @param datasourceFileId 데이터소스 파일 ID
         * @param request          파일 처리 요청
         * @return 파일 처리 결과
         */
        // DataCtlgDataSourceUdpRegisterRes registerDataSourceFileToUdp(String
        // datasourceFileId,
        // DataCtlgDataSourceFileDownloadReq request);

        /**
         * 데이터저장소 등록을 위한 데이터셋 파일 처리
         *
         * @param dataId       데이터셋 ID
         * @param isCustomType 커스텀 타입 여부
         * @param request      파일 처리 요청
         * @return 파일 처리 결과
         */
        DataCtlgDataSourceUdpRegisterRes registerDataSetFileToOzone(String dataId, boolean isCustomType,
                        DataCtlgDataSourceFileDownloadReq request);

        /**
         * 학습 데이터 생성 (커스텀이 아닌 경우)
         *
         * @param request 학습 데이터 생성 요청 (파일명 리스트 포함)
         * @return 학습 데이터 생성 결과
         */
        DataCtlgTrainingDataCreateRes createTrainingDataNotCustom(DataCtlgTrainingDatasetCreateFromFilesReq request);

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

        DataCtlgCustomTrainingDataCreateRes createCustomTrainingDataset(
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
                        String description);

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
        ResponseEntity<Resource> getDatasetSourceArchive(UUID datasetId);

        /**
         * 임시 버킷 삭제 스케줄러용 상태 점검 및 삭제 처리
         *
         * @param datasourceId   데이터소스 ID
         * @param tempBucketName 임시 버킷명
         * @param username       현재 작업 사용자명
         * @return true면 스케줄 종료, false면 계속 진행
         */
        boolean checkAndDeleteTempBucket(String datasourceId, String tempBucketName, String username);

        /**
         * 데이터소스 Policy 설정
         * 
         * @param datasourceId 데이터소스 ID
         * @param memberId     사용자 ID
         * @param projectName  프로젝트명
         * @return 설정된 Policy 목록
         */
        List<PolicyRequest> setDataSourcePolicy(String datasourceId, String memberId, String projectName);

        /**
         * 학습 데이터셋 Policy 설정
         * 
         * @param datasetId   학습 데이터셋 ID
         * @param memberId    사용자 ID
         * @param projectName 프로젝트명
         * @return 설정된 Policy 목록
         */
        List<PolicyRequest> setDatasetPolicy(String datasetId, String memberId, String projectName);
}
