package com.skax.aiplatform.client.sktai.data.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skax.aiplatform.client.sktai.common.dto.Payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SKTAI 데이터소스 파일 목록 응답 DTO
 * 
 * <p>
 * 데이터소스에 속한 파일들의 목록과 페이징 정보를 포함하는 응답 DTO입니다.
 * 대량의 파일을 효율적으로 조회할 수 있도록 페이징 기능을 제공합니다.
 * </p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 * <li><strong>파일 목록</strong>: 현재 페이지의 파일 정보 리스트</li>
 * <li><strong>페이징 정보</strong>: 전체 개수, 페이지 정보, 네비게이션 링크</li>
 * <li><strong>메타데이터</strong>: 조회 결과 요약 정보</li>
 * </ul>
 * 
 * <h3>사용 시나리오:</h3>
 * <ul>
 * <li>데이터소스 파일 목록 페이징 조회</li>
 * <li>파일 관리 화면 데이터 표시</li>
 * <li>파일 검색 결과 표시</li>
 * </ul>
 *
 * @author ByounggwanLee
 * @since 2025-10-23
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "SKTAI 데이터소스 파일 목록 응답", example = """
    {
      "data": [
        {
          "id": "ac338846-e725-4930-a722-c4c36ad24dc4",
          "datasource_id": "454c67ff-99f6-4295-8cfb-810be4345467",
          "file_name": "테스트_unsupervised.xlsx",
          "file_path": "private/default/data/datasource/repo/datasource-454c67ff-99f6-4295-8cfb-810be4345467/9afa8bda-b60_20251019171533591813_86605e78.xlsx",
          "file_size": 10891,
          "is_deleted": false,
          "created_at": "2025-10-19T17:15:42.072812",
          "updated_at": "2025-10-19T17:15:42.073069",
          "created_by": "admin",
          "updated_by": "admin",
          "s3_etag": null,
          "file_metadata": null,
          "knowledge_config": null
        }
      ],
      "payload": {
        "pagination": {
          "page": 1,
          "first_page_url": "//datasources/454c67ff-99f6-4295-8cfb-810be4345467/files?page=1&size=10",
          "from_": 1,
          "last_page": 1,
          "links": [
            {
              "url": null,
              "label": "&laquo; Previous",
              "active": false,
              "page": null
            },
            {
              "url": "//datasources/454c67ff-99f6-4295-8cfb-810be4345467/files?page=1&size=10",
              "label": "1",
              "active": true,
              "page": 1
            },
            {
              "url": null,
              "label": "Next &raquo;",
              "active": false,
              "page": null
            }
          ],
          "next_page_url": null,
          "items_per_page": 10,
          "prev_page_url": null,
          "to": 1,
          "total": 1
        }
      }
    }
    """)
public class DatasourceFileList {

  /**
   * 파일 목록
   * 
   * <p>
   * 현재 페이지에 포함된 데이터소스 파일들의 상세 정보 목록입니다.
   * 각 파일은 ID, 이름, 크기, 상태 등의 정보를 포함합니다.
   * </p>
   * 
   * @implNote 빈 배열인 경우 해당 페이지에 파일이 없음을 의미합니다.
   */
  @JsonProperty("data")
  @Schema(description = "현재 페이지의 파일 정보 목록")
  private List<DatasourceFile> data;

  /**
   * 페이로드 정보
   * 
   * <p>
   * 페이징 정보와 추가 메타데이터를 포함하는 페이로드 객체입니다.
   * </p>
   */
  @JsonProperty("payload")
  @Schema(description = "페이로드 정보 (페이징 정보 포함)")
  private Payload payload;

  /**
   * 페이로드 정보 DTO
   * 
   * <p>
   * 페이징 정보를 포함하는 페이로드 객체입니다.
   * </p>
   */
  // @Data
  // @NoArgsConstructor
  // @AllArgsConstructor
  // @Builder
  // @Schema(description = "페이로드 정보")
  // public static class Payload {

  // /**
  // * 페이징 정보
  // *
  // * <p>페이지 네비게이션과 관련된 모든 정보를 포함합니다.</p>
  // */
  // @JsonProperty("pagination")
  // @Schema(description = "페이징 정보")
  // private Pagination pagination;
  // }
}