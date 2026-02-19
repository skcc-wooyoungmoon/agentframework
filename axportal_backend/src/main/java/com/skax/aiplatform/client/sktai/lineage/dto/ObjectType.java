package com.skax.aiplatform.client.sktai.lineage.dto;

import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Lineage 객체 타입 열거형
 *
 * <p>SKTAI Lineage 시스템에서 추적 가능한 모든 객체 타입을 정의합니다.
 * AI/ML 워크플로우의 다양한 컴포넌트들을 포괄합니다.</p>
 *
 * <h3>주요 카테고리:</h3>
 * <ul>
 *   <li><strong>Agent 관련</strong>: SERVING_AGENT, AGENT_GRAPH, AGENT_APP 등</li>
 *   <li><strong>Model 관련</strong>: MODEL, MODEL_VERSION, SERVING_MODEL</li>
 *   <li><strong>Data 관련</strong>: DATASET, DATASOURCE, DATASOURCE_FILE</li>
 *   <li><strong>Knowledge 관련</strong>: KNOWLEDGE, VECTOR_DB, PROMPT</li>
 *   <li><strong>Tool 관련</strong>: TOOL, MCP, CUSTOM_SCRIPT</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @version 1.0
 * @since 2025-10-17
 */
@Schema(
        description = "Lineage 추적 가능 객체 타입",
        example = "MODEL"
)
public enum ObjectType {

    /**
     * 서빙 에이전트
     */
    SERVING_AGENT,

    /**
     * 에이전트 그래프
     */
    AGENT_GRAPH,

    /**
     * 에이전트 애플리케이션
     */
    AGENT_APP,

    /**
     * 에이전트 배포
     */
    AGENT_DEPLOYMENT,

    /**
     * 서브 에이전트
     */
    SUB_AGENT,

    /**
     * 서빙 모델
     */
    SERVING_MODEL,

    /**
     * 모델
     */
    MODEL,

    /**
     * 모델 버전
     */
    MODEL_VERSION,

    /**
     * 지식베이스
     */
    KNOWLEDGE,

    /**
     * 프롬프트
     */
    PROMPT,

    /**
     * 가드레일
     */
    GUARDRAILS,

    /**
     * 도구
     */
    TOOL,

    /**
     * MCP (Model Context Protocol)
     */
    MCP,

    /**
     * Few-Shot 학습
     */
    FEW_SHOT,

    /**
     * 인제스트 도구
     */
    INGESTION_TOOL,

    /**
     * 데이터 소스
     */
    DATASOURCE,

    /**
     * 데이터 소스 파일
     */
    DATASOURCE_FILE,

    /**
     * 벡터 데이터베이스
     */
    VECTOR_DB,

    /**
     * 커스텀 스크립트
     */
    CUSTOM_SCRIPT,

    /**
     * 훈련
     */
    TRAINING,

    /**
     * 데이터셋
     */
    DATASET,

    /**
     * 세이프티 필터
     */
    SAFETY_FILTER,

    /**
     * 프로젝트
     */
    PROJECT,

    /**
     * APIKEY
     */
    APIKEY;
    
    /**
     * JSON 직렬화용 값 반환
     *
     * @return JSON으로 직렬화될 문자열 값 (enum 이름)
     */
    @JsonValue
    public String getValue() {
        return this.name();
    }

    /**
     * 문자열로부터 ObjectType 변환
     *
     * @param value 변환할 문자열 값
     * @return 해당하는 ObjectType 또는 null
     */
    public static ObjectType fromValue(String value) {
        try {
            return ObjectType.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return this.name();
    }
}
