package com.skax.aiplatform.client.ione.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 작업 요청 결과 조회 DTO
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublishWorkInfoResult {

    /**
     * 생성일시
     */
    @JsonProperty("createDate")
    private String createDate;
    
    /**
     * 생성자
     */
    @JsonProperty("creator")
    private String creator;
    
    /**
     * 수정일시
     */
    @JsonProperty("modifyDate")
    private String modifyDate;
    
    /**
     * 수정자
     */
    @JsonProperty("modifier")
    private String modifier;
    
    /**
     * 작업 순번
     */
    @JsonProperty("infWorkSeq")
    private String infWorkSeq;
    
    /**
     * 상위 작업 순번
     */
    @JsonProperty("infWorkParentsSeq")
    private String infWorkParentsSeq;
    
    /**
     * 작업 타입
     */
    @JsonProperty("infWorkType")
    private String infWorkType;
    
    /**
     * 작업 데이터 (JSON 문자열)
     */
    @JsonProperty("infWorkData")
    private String infWorkData;
    
    /**
     * 작업 스케줄
     */
    @JsonProperty("infWorkSchedule")
    private String infWorkSchedule;
    
    /**
     * 클라이언트 IP
     */
    @JsonProperty("infWorkClientIp")
    private String infWorkClientIp;
    
    /**
     * 작업 상태
     */
    @JsonProperty("infWorkStatus")
    private String infWorkStatus;
    
    /**
     * 작업 메시지
     */
    @JsonProperty("infWorkMsg")
    private String infWorkMsg;
    
    /**
     * 작업 로그 경로
     */
    @JsonProperty("infWorkLogPath")
    private String infWorkLogPath;
    
    /**
     * 재발행 가능 여부
     */
    @JsonProperty("infWorkRepubEnable")
    private String infWorkRepubEnable;
    
    /**
     * 취소 가능 여부
     */
    @JsonProperty("infWorkCancleEnable")
    private String infWorkCancleEnable;
    
    /**
     * 작업 시작일시
     */
    @JsonProperty("infWorkStartDate")
    private String infWorkStartDate;
    
    /**
     * 작업 종료일시
     */
    @JsonProperty("infWorkEndDate")
    private String infWorkEndDate;
}