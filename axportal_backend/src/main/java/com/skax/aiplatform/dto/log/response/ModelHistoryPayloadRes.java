package com.skax.aiplatform.dto.log.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 모델 사용 이력 페이로드 응답 DTO (카멜케이스)
 * 
 * <p>
 * 모델 사용 이력 조회 응답에서 사용되는 페이로드 구조입니다.
 * 페이징 정보와 추가 메타데이터를 포함하여 클라이언트에서 효율적인 데이터 처리를 지원합니다.
 * </p>
 * 
 * <h3>구조 특징:</h3>
 * <ul>
 * <li><strong>카멜케이스</strong>: Java 표준 네이밍 컨벤션 사용</li>
 * <li><strong>Laravel 스타일</strong>: Laravel의 pagination 구조를 따름</li>
 * <li><strong>페이징 지원</strong>: 대용량 데이터의 효율적인 조회</li>
 * </ul>
 * 
 * <h3>사용 사례:</h3>
 * <ul>
 * <li>모델 사용 이력 조회 시 페이징 정보</li>
 * <li>에이전트 통계 조회 시 집계 정보</li>
 * <li>문서 지능형 분석 결과의 메타데이터</li>
 * </ul>
 *
 * @author System
 * @since 2025-01-27
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "모델 사용 이력 페이로드 정보", example = """
        {
          "pagination": {
            "page": 1,
            "firstPageUrl": "/history/model/list?page=1&size=1",
            "from": 1,
            "lastPage": 152,
            "links": [
              {
                "url": null,
                "label": "&laquo; Previous",
                "active": false,
                "page": null
              },
              {
                "url": "/history/model/list?page=1&size=1",
                "label": "1",
                "active": true,
                "page": 1
              }
            ],
            "nextPageUrl": "/history/model/list?page=2&size=1",
            "itemsPerPage": 1,
            "prevPageUrl": null,
            "to": 1,
            "total": 152
          }
        }
        """)
public class ModelHistoryPayloadRes {

    /**
     * 페이징 정보
     * 
     * <p>
     * Laravel 스타일의 페이징 정보를 포함합니다.
     * 페이지 네비게이션을 위한 링크 정보와 메타데이터를 제공합니다.
     * </p>
     * 
     * <h4>포함되는 정보:</h4>
     * <ul>
     * <li><strong>page</strong>: 현재 페이지 번호</li>
     * <li><strong>itemsPerPage</strong>: 페이지당 항목 수</li>
     * <li><strong>total</strong>: 전체 항목 수</li>
     * <li><strong>lastPage</strong>: 마지막 페이지 번호</li>
     * <li><strong>links</strong>: 페이지 네비게이션 링크</li>
     * </ul>
     */
    @Schema(description = "페이징 정보", implementation = ModelHistoryPaginationRes.class)
    private ModelHistoryPaginationRes pagination;
}
