package com.skax.aiplatform.client.udp.document.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * UDP 문서 검색 응답 DTO
 * 
 * <p>UDP 시스템의 문서 검색 결과를 담는 응답 데이터 구조입니다.
 * 총 개수, 페이지 정보와 함께 검색된 문서 목록을 제공합니다.</p>
 * 
 * <h3>응답 구조:</h3>
 * <ul>
 *   <li><strong>대상수</strong>: 검색 조건에 맞는 총 문서 수</li>
 *   <li><strong>페이지</strong>: 현재 조회 페이지</li>
 *   <li><strong>결과목록</strong>: 검색된 문서들의 상세 정보</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 * @see DocumentInfo 개별 문서 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
    description = "UDP 문서 검색 응답 정보",
    example = """
        {
          "total_count": 1,
          "page": 1,
          "result_lists": [
            {
              "doc_summary": null,
              "origin_metadata": {
                "doc_exec_dt": "20160621",
                "parsed_token_cnt": 0,
                "title": "자산구성형 개인종합자산관리계약 업무규정"
              },
              "last_mod_date": 20191228,
              "doc_uuid": "doc00_sbrgl20160411680300000002",
              "dataset_name": "규정",
              "create_date": 20191228,
              "doc_path_anony_md": null,
              "dataset_cd": "rgl",
              "attach_parent_doc_uuid": null,
              "doc_array_keywords": null
            }
          ]
        }
        """
)
public class DocumentSearchResponse {

    /**
     * 대상수
     * 
     * <p>검색 조건에 맞는 대상의 총 수입니다.</p>
     */
    @JsonProperty("total_count")
    @Schema(description = "대상의 총 수", example = "1")
    private Long totalCount;

    /**
     * 페이지
     * 
     * <p>현재 조회 페이지입니다.</p>
     */
    @JsonProperty("page")
    @Schema(description = "현재 조회 페이지", example = "1")
    private Long page;

    /**
     * 결과목록
     * 
     * <p>데이터셋 검색 결과만큼 반복되는 문서 목록입니다.</p>
     * 
     * @implNote 검색 결과가 없는 경우 빈 리스트가 반환됩니다.
     */
    @JsonProperty("result_lists")
    @Schema(
        description = "데이터셋 검색 결과 목록",
        example = """
            [
              {
                "dataset_cd": "rgl",
                "dataset_name": "규정",
                "doc_uuid": "doc00_sbrgl20160411680300000002",
                "doc_title": "자산구성형 개인종합자산관리계약 업무규정",
                "doc_array_keywords": null,
                "doc_summary": null,
                "create_date": 20191228,
                "last_mod_date": 20191228,
                "doc_path_anony_md": null,
                "attach_parent_doc_uuid": null,
                "origin_metadata": {
                  "doc_exec_dt": "20160621",
                  "parsed_token_cnt": 0,
                  "title": "자산구성형 개인종합자산관리계약 업무규정"
                }
              }
            ]
            """
    )
    private List<DocumentInfo> resultLists;
}