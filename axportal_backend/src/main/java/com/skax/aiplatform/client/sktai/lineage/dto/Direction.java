package com.skax.aiplatform.client.sktai.lineage.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Lineage 탐색 방향 열거형
 * 
 * <p>Lineage 관계 탐색 시 사용할 방향을 정의합니다.
 * BFS 알고리즘을 통해 상위 또는 하위 의존성을 탐색할 수 있습니다.</p>
 * 
 * <h3>방향 타입:</h3>
 * <ul>
 *   <li><strong>UPSTREAM</strong>: 상위 의존성 (부모/소스 방향)</li>
 *   <li><strong>DOWNSTREAM</strong>: 하위 의존성 (자식/타겟 방향)</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-10-17
 * @version 1.0
 */
@Schema(
    description = "Lineage 탐색 방향",
    allowableValues = {"upstream", "downstream"},
    example = "upstream"
)
public enum Direction {
    
    /**
     * 상위 의존성 방향
     * 
     * <p>현재 객체를 생성하는데 사용된 상위 객체들(부모, 소스)을 조회하는 방향입니다.
     * 예: 모델이 어떤 데이터셋을 사용했는지, 파이프라인이 어떤 소스를 참조했는지</p>
     */
    @Schema(description = "상위 의존성 (부모/소스 방향)")
    UPSTREAM("upstream"),
    
    /**
     * 하위 의존성 방향
     * 
     * <p>현재 객체에서 파생되어 생성된 하위 객체들(자식, 타겟)을 조회하는 방향입니다.
     * 예: 데이터셋이 어떤 모델 학습에 사용되었는지, 모델이 어떤 서비스에서 활용되는지</p>
     */
    @Schema(description = "하위 의존성 (자식/타겟 방향)")
    DOWNSTREAM("downstream");
    
    private final String value;
    
    /**
     * Direction 생성자
     * 
     * @param value JSON 직렬화에 사용될 문자열 값
     */
    Direction(String value) {
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
     * 문자열로부터 Direction 변환
     * 
     * @param value 변환할 문자열 값
     * @return 해당하는 Direction 또는 null
     */
    public static Direction fromValue(String value) {
        for (Direction direction : Direction.values()) {
            if (direction.value.equals(value)) {
                return direction;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return value;
    }
}