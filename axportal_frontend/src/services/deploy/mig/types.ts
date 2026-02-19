// export type GetMigLineageRequest = {
//   uuid: string;
// };

// export type GetMigLineageResponse = {
//   data: MigLineageItem[];
// };

// export type MigLineageItem = {
//   source_key: string;
//   target_key: string;
//   action: string;
//   depth: string;
//   source_type: string;
//   target_type: string;
// };

export type AssetValidationRequest = {
  project_id: string;
  type: string;
};

// Map<String, List<Map<String, Object>>> 형식의 응답 타입
export type GetMigResourceEndpointsRequest = {
  project_id: string;
  type: string;
  uuid: string;
};

export type GetMigResourceEndpointsResponse = Record<string, Array<Record<string, any>>>;

// CopyFolder Request 타입
export type CopyFolderRequest = {
  project_id: string;
  type: string;
  id: string;
  projectName: string;
  assetName: string;
  resourceEndpoints?: Record<string, Array<Record<string, any>>>; // request body에 포함될 데이터
};

// 운영 이행 관리 조회 요청 타입
export type GetMigMasRequest = {
  /** 페이지 번호 (1부터 시작) */
  page?: number;
  /** 페이지 크기 (12, 36, 60) */
  size?: number;
  /** 조회 시작일시 (fst_created_at >= startDate) */
  startDate?: string; // ISO 8601 형식: "2025-06-29T00:00:00"
  /** 조회 종료일시 (fst_created_at <= endDate) */
  endDate?: string; // ISO 8601 형식: "2025-06-30T23:59:59"
  /** 이행 대상 검색어 (asst_nm LIKE) */
  asstNm?: string;
  /** 이행 분류 필터 (asst_g) */
  asstG?: string;
  /** 프로젝트 시퀀스 */
  prjSeq?: number;
};

export type GetMigMasWithMapRequest = {
  /** 마이그레이션 UUID */
  uuid?: string;
  /** 시퀀스 번호 */
  sequence?: number;
  /** 어시스트 그룹 (이행 분류) */
  asstG?: string;
};

// 운영 이행 관리 조회 응답 아이템 타입
export type GetMigMasResponseItem = {
  /** 시퀀스 번호 */
  seqNo: number;
  /** UUID */
  uuid: string;
  /** 어시스트 그룹 (이행 분류) */
  asstG: string;
  /** 어시스트 명 (이행 대상) */
  asstNm: string;
  /** 프로젝트 시퀀스 */
  prjSeq: number;
  /** GPO 프로젝트 명 */
  gpoPrjNm: string;
  /** 파일 경로 */
  filePath: string;
  /** 파일 명들 */
  fileNms: string;
  /** 프로그램 설명 내용 */
  pgmDescCtnt?: string;
  /** 삭제 여부 */
  delYn: number;
  /** 최초 생성일시 (이행 요청일시) */
  fstCreatedAt: string; // ISO 8601 형식: "2025-06-29T18:32:43"
  /** 생성자 */
  createdBy: string;
};

// 운영 이행 관리 조회 응답 타입 (페이지네이션)
export type GetMigMasResponse = {
  /** 데이터 목록 */
  content: GetMigMasResponseItem[];
  /** 페이지 정보 */
  pageable: {
    page: number;
    size: number;
    sort: string;
  };
  /** 총 개수 */
  totalElements: number;
  /** 총 페이지 수 */
  totalPages: number;
  /** 첫 페이지 여부 */
  first: boolean;
  /** 마지막 페이지 여부 */
  last: boolean;
  /** 다음 페이지 존재 여부 */
  hasNext: boolean;
  /** 이전 페이지 존재 여부 */
  hasPrevious: boolean;
};

// 운영 이행 관리 조회 응답 아이템 타입 (Map 포함)
export type GetMigMasWithMapResponseItem = {
  // ========== GPO_MIG_MAS 컬럼 ==========
  /** 시퀀스 번호 (MAS) */
  masSeqNo: number;
  /** UUID (MAS) */
  masUuid: string;
  /** 어시스트 그룹 (MAS) - 이행 분류 */
  masAsstG: string;
  /** 어시스트 명 (MAS) - 이행 대상 */
  masAsstNm: string;
  /** 프로젝트 시퀀스 */
  masPrjSeq: number;
  /** GPO 프로젝트 명 */
  masGpoPrjNm: string;
  /** 마이그레이션 파일 경로 */
  masMigFilePath: string;
  /** 마이그레이션 파일 명 */
  masMigFileNm: string;
  /** 프로그램 설명 내용 */
  masPgmDescCtnt?: string;
  /** 삭제 여부 */
  masDelYn: number;
  /** 최초 생성일시 (이행 요청일시) */
  masFstCreatedAt: string; // ISO 8601 형식: "2025-06-29T18:32:43"
  /** 생성자 */
  masCreatedBy: string;

  // ========== GPO_MIG_ASST_MAP_MAS 컬럼 ==========
  /** 시퀀스 번호 (MAP) */
  mapSeqNo: number;
  /** 마이그레이션 시퀀스 번호 */
  mapMigSeqNo: string;
  /** 마이그레이션 UUID (MAP) */
  mapMigUuid: string;
  /** 어시스트 UUID */
  mapAsstUuid: string;
  /** 어시스트 그룹 (MAP) - 매핑 어시스트 그룹 */
  mapAsstG: string;
  /** 어시스트 명 (MAP) - 매핑 어시스트 명 */
  mapAssetNm: string;
  /** 마이그레이션 매핑 명 */
  mapMigMapNm: string;
  /** 개발 상세 내용 */
  mapDvlpDtlCtnt?: string;
  /** 운영 상세 내용 */
  mapUnyungDtlCtnt?: string;
};

// 운영 이행 관리 조회 응답 타입 (Map 포함, 리스트 형태)
export type GetMigMasWithMapResponse = GetMigMasWithMapResponseItem[];
