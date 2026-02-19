package com.skax.aiplatform.client.ione.statistics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API KEY 호출 통계 응답 DTO
 * 
 * <p>API KEY 사용에 대한 호출 통계 정보를 담는 DTO입니다.</p>
 * 
 * @author ByounggwanLee
 * @since 2025-10-13
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiStatistics {
    
   private Integer totalCount;
   private Integer succCount;
   private Integer failCount;
   private Integer resMiliSec;
   private String year;
   private String month;
   private String day;
   private String hour;
   private String miniute;
}