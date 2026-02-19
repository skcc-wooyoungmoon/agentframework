/**
 * Dataset Card 타입 정의
 */

/**
 * Dataset Card 정보 (백엔드 응답)
 */
export type DatasetCardInfo = {
  dataset_card_id: string;
  dataset_card_name: string;
  dataset_cd: string;
  dataset_name: string;
  origin_system_cd: string;
  origin_system_name: string;
  dataset_card_type: string;
  dataset_summary: string;
  preview: string;
  metadata: string;
  download_path: string;
};

/**
 * Dataset Card 검색 응답 (백엔드 응답)
 */
export type DatasetCardSearchResponse = {
  total_count: number;
  page: number;
  result_lists: DatasetCardInfo[];
};

/**
 * Dataset Card 검색 요청
 */
export type DatasetCardSearchRequest = {
  datasetCardType?: string;
  page?: number;
  size?: number;
  search?: string;
};

