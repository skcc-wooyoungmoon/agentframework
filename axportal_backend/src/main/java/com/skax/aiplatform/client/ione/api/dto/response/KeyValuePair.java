package com.skax.aiplatform.client.ione.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Key-Value 페어
 * 
 * @author system
 * @since 2025-09-16
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
