package com.skax.aiplatform.client.sktai.finetuning.dto.response;

/**
 * SKTAI Fine-tuning Training 메트릭 타입 열거형
 * 
 * <p>Training 메트릭의 타입을 정의합니다.</p>
 * 
 * <h3>타입 설명:</h3>
 * <ul>
 *   <li><strong>train</strong>: 학습 메트릭</li>
 *   <li><strong>evaluation</strong>: 평가 메트릭</li>
 *   <li><strong>dpo</strong>: DPO(Direct Preference Optimization) 메트릭</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
public enum TrainingMetricTypeEnum {
    /**
     * 학습 메트릭
     */
    train,
    
    /**
     * 평가 메트릭
     */
    evaluation,
    
    /**
     * DPO(Direct Preference Optimization) 메트릭
     */
    dpo
}
