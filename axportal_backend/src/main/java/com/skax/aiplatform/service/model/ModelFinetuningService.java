package com.skax.aiplatform.service.model;

import com.skax.aiplatform.client.sktai.auth.dto.request.PolicyRequest;
import com.skax.aiplatform.client.sktai.finetuning.dto.response.TrainingMetricsRead;
import com.skax.aiplatform.common.response.PageResponse;
import com.skax.aiplatform.dto.model.request.ModelFineTuningCreateReq;
import com.skax.aiplatform.dto.model.request.ModelFineTuningUpdateReq;
import com.skax.aiplatform.dto.model.response.ModelFineTuningCreateRes;
import com.skax.aiplatform.dto.model.response.ModelFineTuningStatusRes;
import com.skax.aiplatform.dto.model.response.ModelFineTuningTrainingRes;
import com.skax.aiplatform.dto.model.response.ModelFineTuningTrainingsRes;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ModelFinetuningService {

    /**
     * 파인튜닝 모델 생성
     * 
     * @param registReq 파인튜닝 모델 생성 요청 DTO
     * @return 생성된 파인튜닝 모델 정보
     */
    ModelFineTuningCreateRes registFineTuningTraining(ModelFineTuningCreateReq registReq);

    /**
     * 파인튜닝 모델 목록 조회
     * 
     * @param pageable 페이지 정보
     * @param sort     정렬 기준
     * @param filter   필터 조건
     * @param search   검색어
     * @return 파인튜닝 모델 목록 페이지
     * @throws Exception 조회 실패 시
     */
    PageResponse<ModelFineTuningTrainingsRes> getFineTuningTrainings(Pageable pageable, String sort, String filter,
            String search);

    /**
     * 파인튜닝 모델 상세 조회
     * 
     * @param trainingId 파인튜닝 모델 ID
     * @return 파인튜닝 모델 상세 정보
     */
    ModelFineTuningTrainingRes getFineTuningTraining(String trainingId);

    /**
     * 파인튜닝 모델 수정
     * 
     * @param trainingId 파인튜닝 모델 ID
     * @param updateReq  파인튜닝 모델 수정 요청 DTO
     * @return 수정된 파인튜닝 모델 정보
     */
    ModelFineTuningTrainingRes updateFineTuningTraining(String trainingId, ModelFineTuningUpdateReq updateReq, String scalingGroup);

    /**
     * 파인튜닝 모델 삭제
     * 
     * @param trainingId 파인튜닝 모델 ID
     */
    void deleteFineTuningTraining(String trainingId);

    /**
     * 파인튜닝 모델 상태 조회
     * 
     * @param trainingId 파인튜닝 모델 ID
     * @return 파인튜닝 모델 상태 정보
     */
    ModelFineTuningStatusRes getFineTuningTrainingStatus(String trainingId);

    /**
     * 트레이너 상세 조회
     * 
     * @param trainerId 트레이너 ID
     * @return 트레이너 상세 정보
     */
    com.skax.aiplatform.client.sktai.finetuning.dto.response.TrainerRead getTrainerById(String trainerId);

    /**
     * 파인튜닝 상세정보 조회
     *
     * @param trainingId 파인튜닝 ID 배열
     * @return TrainingMetricsRead 파인튜닝 모델 매트릭 정보 목록
     */
    TrainingMetricsRead getFineTuningMetricsById(String trainingId);

    /**
     * 파인튜닝 모델 이벤트 조회
     *
     * @param trainingId 파인튜닝 모델 ID
     * @param last       마지막 이벤트 식별자 (증분 조회용)
     * @return TrainingEventsRead 파인튜닝 모델 이벤트 목록
     */
    com.skax.aiplatform.client.sktai.finetuning.dto.response.TrainingEventsRead getFineTuningTrainingEvents(
            String trainingId, String last);

    /**
     * 파인튜닝 모델 Policy 설정
     *
     * @param trainingId  파인튜닝 모델 ID
     * @param memberId    사용자 ID
     * @param projectName 프로젝트명
     * @return List<PolicyRequest> 설정된 Policy 목록
     */
    List<PolicyRequest> setFineTuningTrainingPolicy(String trainingId, String memberId, String projectName);
}
