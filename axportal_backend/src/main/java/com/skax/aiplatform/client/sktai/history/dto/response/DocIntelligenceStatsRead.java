package com.skax.aiplatform.client.sktai.history.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 문서 지능형 분석 통계 정보 응답 DTO
 * 
 * <p>SKTAI History API에서 문서 지능형 분석(Document Intelligence) 통계 조회 시 반환되는 응답 데이터 구조입니다.
 * 문서 처리 성능, 인식 정확도, 처리량 등을 포함한 집계 데이터를 제공합니다.</p>
 * 
 * <h3>통계 종류:</h3>
 * <ul>
 *   <li><strong>처리량 통계</strong>: 총 문서 수, 처리 완료/실패 비율</li>
 *   <li><strong>성능 통계</strong>: 평균 처리 시간, 처리 속도</li>
 *   <li><strong>정확도 통계</strong>: OCR 정확도, 구조 분석 정확도</li>
 *   <li><strong>문서 유형별 통계</strong>: PDF, 이미지, 텍스트 등 유형별 분석</li>
 *   <li><strong>언어별 통계</strong>: 지원 언어별 처리 성능</li>
 * </ul>
 * 
 * <h3>사용 사례:</h3>
 * <ul>
 *   <li>문서 처리 시스템 성능 모니터링</li>
 *   <li>OCR 및 구조 분석 정확도 추적</li>
 *   <li>문서 유형별 최적화 방안 도출</li>
 *   <li>처리 용량 계획 및 리소스 관리</li>
 *   <li>서비스 품질 관리 및 개선</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-09-24
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "문서 지능형 분석 통계 정보 응답",
    example = """
        {
          "data": {
            "total_documents": 2500,
            "processed_documents": 2450,
            "failed_documents": 50,
            "success_rate": 98.0,
            "average_processing_time": 3.5,
            "ocr_accuracy": 96.8,
            "structure_accuracy": 94.2,
            "document_types": {
              "pdf": {
                "count": 1800,
                "success_rate": 98.5,
                "avg_processing_time": 4.2
              },
              "image": {
                "count": 700,
                "success_rate": 97.1,
                "avg_processing_time": 2.1
              }
            }
          }
        }
        """
)
public class DocIntelligenceStatsRead {
    
    /**
     * 문서 지능형 분석 통계 데이터
     * 
     * <p>문서 처리에 대한 종합적인 통계 정보를 포함하는 동적 객체입니다.
     * 다양한 문서 유형과 처리 메트릭을 수용할 수 있도록 유연한 구조로 설계되었습니다.</p>
     * 
     * <h4>일반적으로 포함되는 통계:</h4>
     * <ul>
     *   <li><strong>총 문서 수</strong>: total_documents</li>
     *   <li><strong>처리 완료/실패 건수</strong>: processed_documents, failed_documents</li>
     *   <li><strong>성공률</strong>: success_rate (백분율)</li>
     *   <li><strong>평균 처리 시간</strong>: average_processing_time (초)</li>
     *   <li><strong>OCR 정확도</strong>: ocr_accuracy (백분율)</li>
     *   <li><strong>구조 분석 정확도</strong>: structure_accuracy (백분율)</li>
     *   <li><strong>언어 인식 정확도</strong>: language_detection_accuracy</li>
     *   <li><strong>문서 유형별 통계</strong>: document_types 객체</li>
     *   <li><strong>언어별 통계</strong>: languages 객체</li>
     *   <li><strong>페이지별 통계</strong>: page_statistics</li>
     *   <li><strong>품질 분포</strong>: quality_distribution</li>
     * </ul>
     * 
     * @implNote 타입 안전성을 위해 구체적인 StatsDataItem DTO 배열을 사용합니다.
     */
    @JsonProperty("data")
    @Schema(
        description = "문서 지능형 분석 통계 데이터 (동적 구조)",
        example = """
            {
              "summary": {
                "total_documents": 2500,
                "processed_documents": 2450,
                "failed_documents": 50,
                "success_rate": 98.0,
                "average_processing_time_seconds": 3.5,
                "median_processing_time_seconds": 2.8,
                "p95_processing_time_seconds": 8.2,
                "total_pages_processed": 12500,
                "total_processing_time_hours": 2.4
              },
              "accuracy_metrics": {
                "overall_ocr_accuracy": 96.8,
                "text_extraction_accuracy": 97.2,
                "structure_analysis_accuracy": 94.2,
                "language_detection_accuracy": 99.1,
                "table_extraction_accuracy": 92.5,
                "form_field_accuracy": 95.8
              },
              "document_types": {
                "pdf": {
                  "count": 1800,
                  "success_rate": 98.5,
                  "avg_processing_time": 4.2,
                  "ocr_accuracy": 97.1,
                  "avg_pages": 5.2
                },
                "jpeg": {
                  "count": 500,
                  "success_rate": 97.8,
                  "avg_processing_time": 2.1,
                  "ocr_accuracy": 96.3,
                  "avg_pages": 1.0
                },
                "png": {
                  "count": 200,
                  "success_rate": 96.5,
                  "avg_processing_time": 1.8,
                  "ocr_accuracy": 95.8,
                  "avg_pages": 1.0
                }
              },
              "languages": {
                "korean": {
                  "documents": 1500,
                  "ocr_accuracy": 97.8,
                  "success_rate": 98.5
                },
                "english": {
                  "documents": 800,
                  "ocr_accuracy": 98.2,
                  "success_rate": 98.9
                },
                "mixed": {
                  "documents": 200,
                  "ocr_accuracy": 94.5,
                  "success_rate": 96.0
                }
              },
              "quality_distribution": {
                "high_quality": 1800,
                "medium_quality": 550,
                "low_quality": 150
              },
              "error_analysis": {
                "image_quality_issues": 25,
                "unsupported_format": 12,
                "processing_timeout": 8,
                "memory_errors": 5
              },
              "time_series": {
                "daily": [
                  {"date": "2025-09-23", "documents": 120, "avg_processing_time": 3.2},
                  {"date": "2025-09-24", "documents": 135, "avg_processing_time": 3.8}
                ]
              }
            }
            """
    )
    private List<StatsDataItem> data;
}