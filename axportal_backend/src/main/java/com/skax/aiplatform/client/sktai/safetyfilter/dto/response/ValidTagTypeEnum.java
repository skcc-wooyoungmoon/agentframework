package com.skax.aiplatform.client.sktai.safetyfilter.dto.response;

/**
 * SKTAI Safety Filter 유효 태그 타입 열거형
 * 
 * <p>Safety Filter에서 사용할 수 있는 태그 매칭 타입을 정의합니다.</p>
 * 
 * <h3>타입 설명:</h3>
 * <ul>
 *   <li><strong>ALL</strong>: 모든 형태소 매칭</li>
 *   <li><strong>NN</strong>: 명사만 매칭</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-08-15
 * @version 1.0
 */
public enum ValidTagTypeEnum {
    /**
     * 모든 형태소 매칭
     */
    ALL,
    
    /**
     * 명사만 매칭
     */
    NN
}
