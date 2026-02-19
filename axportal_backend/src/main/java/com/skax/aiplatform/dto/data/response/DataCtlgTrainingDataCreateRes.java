package com.skax.aiplatform.dto.data.response;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 학습 데이터 생성 응답 DTO (커스텀이 아닌 경우)
 * 
 * @author 장지원
 * @since 2025-10-28
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataCtlgTrainingDataCreateRes {

    /**
     * 생성 성공 여부
     */
    private Boolean success;

    /**
     * 생성된 학습 데이터 ID
     */
    private String trainingDataId;

    /**
     * 학습 데이터 이름
     */
    private String name;

    /**
     * 학습 데이터 타입
     */
    private String type;

    /**
     * 처리된 파일 수
     */
    private Integer processedFileCount;

    /**
     * 처리된 파일 목록
     */
    private List<String> processedFiles;

    /**
     * 실패한 파일 목록
     */
    private List<String> failedFiles;

    /**
     * 생성 결과 메시지
     */
    private String message;

    /**
     * 생성 시간 (밀리초)
     */
    private Long creationTimeMs;

    /**
     * 에러 코드 (실패 시)
     */
    private String errorCode;

    /**
     * 에러 상세 정보 (실패 시)
     */
    private String errorDetails;

    /**
     * 임시 버킷명 (S3 타입인 경우)
     */
    private String tempBucketName;

    /**
     * 입력 파일 리스트 (요청에 전달된 파일명들)
     */
    private List<String> inputFileNames;

    /**
     * 필터링 결과로 매칭된 파일 개수
     */
    private Integer matchedCount;

    /**
     * 필터링 결과로 매칭된 S3 객체 키 목록
     */
    private List<String> matchedKeys;

    /**
     * 1단계: 파일 복사 및 임시 버킷 생성 결과 (S3 타입) 또는 사용자 정보 설정 결과 (File 타입)
     */
    private StepResult preparationStep;

    /**
     * 2단계: 데이터소스 생성 결과
     */
    private StepResult datasourceCreationStep;

    /**
     * 3단계: 데이터셋 생성 결과
     */
    private StepResult datasetCreationStep;

    /**
     * S3 복사/임시 버킷 생성 전체 결과 (S3 타입)
     */
    private Map<String, Object> copyResult;

    /**
     * 생성된 데이터소스 ID
     */
    private String datasourceId;

    /**
     * 생성된 데이터셋 ID
     */
    private String datasetId;

    /**
     * 데이터셋 status
     */
    private String status;

    /**
     * 생성된 데이터셋 errorMessage
     */
    private String errorMessage;
}
