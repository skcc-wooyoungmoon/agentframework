package com.skax.aiplatform.client.udp.document.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 문서 정보 DTO
 * 
 * <p>
 * 검색된 개별 문서의 상세 정보를 담는 데이터 구조입니다.
 * </p>
 * 
 * <h3>포함 정보:</h3>
 * <ul>
 * <li><strong>기본 정보</strong>: 문서 UUID, 요약, 키워드</li>
 * <li><strong>데이터셋 정보</strong>: 소속 데이터셋 코드 및 이름</li>
 * <li><strong>날짜 정보</strong>: 생성일, 최종수정일</li>
 * <li><strong>다운로드 경로</strong>: MD 형식 문서 다운로드 경로</li>
 * <li><strong>첨부파일</strong>: 부모문서 UUID</li>
 * <li><strong>메타데이터</strong>: 원천시스템 메타데이터 (title 포함)</li>
 * </ul>
 * 
 * @author ByounggwanLee
 * @since 2025-10-16
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "문서 상세 정보")
public class DocumentInfo {

  /**
   * 데이터셋코드
   */
  @JsonProperty("doc_dataset_cd")
  @Schema(description = "데이터셋 코드", example = "rgl")
  private String datasetCd;

  /**
   * 데이터셋이름
   */
  @JsonProperty("doc_dataset_nm")
  @Schema(description = "데이터셋 이름", example = "규정")
  private String datasetName;

  /**
   * 문서UUID
   */
  @JsonProperty("uuid")
  @Schema(description = "문서 UUID", example = "doc00_sbrgl20160411680300000002")
  private String docUuid;

  /**
   * 문서제목 (doc_nm 우선, 없으면 origin_metadata.title 사용 가능)
   */
  @JsonProperty("doc_nm")
  @Schema(description = "문서 제목", example = "자산구성형 개인종합자산관리계약 업무규정")
  private String docTitle;

  /**
   * 문서요약
   */
  @JsonProperty("doc_summary")
  @Schema(description = "문서 요약")
  private String docSummary;

  /**
   * 생성일
   */
  // @JsonProperty("create_date")
  // @Schema(description = "문서 생성일", example = "20191228")
  // private Long createDate;

  // /**
  // * 최종수정일
  // */
  // @JsonProperty("last_mod_date")
  // @Schema(description = "문서 최종 수정일", example = "20191228")
  // private Long lastModDate;

  /**
   * 생성일
   */
  @JsonProperty("doc_create_day")
  @Schema(description = "문서 생성일", example = "20191228")
  private String docCreateDay;

  /**
   * 최종수정일
   */
  @JsonProperty("doc_mdfcn_day")
  @Schema(description = "문서 최종 수정일", example = "20191228")
  private String docMdfcnDay;

  /**
   * 문서 경로 (익명화)
   */
  @JsonProperty("doc_path_anony")
  @Schema(description = "익명화된 문서 경로")
  private String docPathAnonyMd;

  /**
   * 청크 경로
   */
  @JsonProperty("doc_path_chunk")
  @Schema(description = "문서 청크 경로")
  private String docPathChunk;

  /**
   * 첨부 부모문서UUID
   */
  @JsonProperty("parent_doc_uuid")
  @Schema(description = "첨부 부모문서 UUID")
  private String attachParentDocUuid;

  /**
   * 문서 배열 키워드
   */
  @JsonProperty("doc_keyword_list")
  @Schema(description = "문서 키워드 목록")
  private List<String> docArrayKeywords;

  /**
   * 파일 타입
   */
  @JsonProperty("doc_file_type")
  @Schema(description = "문서 파일 타입", example = "hwp")
  private String docFileType;

  /**
   * 파일 크기 (문자열)
   */
  @JsonProperty("doc_file_size")
  @Schema(description = "문서 파일 크기", example = "83000")
  private String docFileSize;

  /**
   * 첨부 여부
   */
  @JsonProperty("attach_yn")
  @Schema(description = "첨부 여부", example = "N")
  private String attachYn;

  /**
   * 첨부 리스트 UUID
   */
  @JsonProperty("attach_list_uuid")
  @Schema(description = "첨부 리스트 UUID")
  private List<String> attachListUuid;

  /**
   * 문서 상태
   */
  @JsonProperty("doc_stts")
  @Schema(description = "문서 상태", example = "U")
  private String docStts;

  /**
   * 마이그레이션 여부
   */
  @JsonProperty("mig_yn")
  @Schema(description = "마이그레이션 여부", example = "N")
  private String migYn;

  /**
   * 마이그레이션 문서 만료일
   */
  @JsonProperty("mig_doc_expire_date")
  @Schema(description = "마이그레이션 문서 만료일", example = "20251217")
  private String migDocExpireDate;

  /**
   * 문서 인덱싱 일시
   */
  @JsonProperty("doc_indx_date")
  @Schema(description = "문서 인덱싱 일시", example = "2025-10-22 02:32:52")
  private String docIndxDate;

  /**
   * 외부 표기 호환: doc_nm 접근자
   */
  public String getDocNm() {
    return this.docTitle;
  }

  /**
   * 원천메타데이터
   */
  @JsonProperty("origin_metadata")
  @Schema(description = "수집 원천에서 추출된 메타데이터 (title 필드 포함)", example = """
      {
        "doc_exec_dt": "20160621",
        "parsed_token_cnt": 0,
        "title": "자산구성형 개인종합자산관리계약 업무규정"
      }
      """)
  private Map<String, Object> originMetadata;
}