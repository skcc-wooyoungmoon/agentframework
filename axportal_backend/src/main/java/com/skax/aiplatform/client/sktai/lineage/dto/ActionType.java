package com.skax.aiplatform.client.sktai.lineage.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Lineage 액션 타입 열거형
 * 
 * <p>Lineage 관계에서 소스 객체가 타겟 객체에 대해 수행하는 액션의 타입을 정의합니다.
 * 데이터 흐름과 의존성 관계를 명확히 구분하기 위한 열거형입니다.</p>
 * 
 * <h3>액션 타입:</h3>
 * <ul>
 *   <li><strong>USE</strong>: 소스가 타겟을 사용/참조하는 관계</li>
 *   <li><strong>CREATE</strong>: 소스가 타겟을 생성/산출하는 관계</li>
 * </ul>
 * 
 * <h3>사용 예시:</h3>
 * <pre>
 * // 데이터셋을 사용하여 모델 학습
 * ActionType.USE
 * 
 * // 파이프라인이 새로운 아티팩트 생성
 * ActionType.CREATE
 * </pre>
 *
 * @author ByounggwanLee
 * @since 2025-10-17
 * @version 1.0
 */
@Schema(
    description = "Lineage 액션 타입",
    allowableValues = {"USE", "CREATE"},
    example = "USE"
)
public enum ActionType {
    
    /**
     * 사용 액션
     * 
     * <p>소스 객체가 타겟 객체를 사용하거나 참조하는 관계를 나타냅니다.
     * 예: 모델이 데이터셋을 사용, 파이프라인이 모델을 참조</p>
     */
    @Schema(description = "소스가 타겟을 사용/참조")
    USE("USE"),
    
    /**
     * 생성 액션
     * 
     * <p>소스 객체가 타겟 객체를 생성하거나 산출하는 관계를 나타냅니다.
     * 예: 학습 작업이 모델을 생성, 전처리가 정제된 데이터를 생성</p>
     */
    @Schema(description = "소스가 타겟을 생성/산출")
    CREATE("CREATE");
    
    private final String value;
    
    /**
     * ActionType 생성자
     * 
     * @param value JSON 직렬화에 사용될 문자열 값
     */
    ActionType(String value) {
        this.value = value;
    }
    
    /**
     * JSON 직렬화용 값 반환
     * 
     * @return JSON으로 직렬화될 문자열 값
     */
    @JsonValue
    public String getValue() {
        return value;
    }
    
    /**
     * 문자열로부터 ActionType 변환
     * 
     * @param value 변환할 문자열 값
     * @return 해당하는 ActionType 또는 null
     */
    public static ActionType fromValue(String value) {
        for (ActionType actionType : ActionType.values()) {
            if (actionType.value.equals(value)) {
                return actionType;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return value;
    }
}