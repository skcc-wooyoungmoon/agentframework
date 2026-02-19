package com.skax.aiplatform.client.ione.system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Key-Value 페어
 * 
 * <p>iONE 시스템에서 사용되는 키-값 쌍을 나타내는 공통 DTO입니다.
 * 헤더, 쿠키, 쿼리 파라미터 등에서 사용됩니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-08-14
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KeyValuePair {
    
    /**
     * Key-Value 페어의 Key 값
     */
    private String key;
    
    /**
     * Key-Value 페어의 Value 값
     */
    private String value;
}