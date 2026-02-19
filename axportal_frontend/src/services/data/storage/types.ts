export type GetMDPackageListRequest = {
  page?: number;
  countPerPage?: number;
  searchWord?: string;
  originSystemCd?: string;
};

export type MDPackageListItem = {
  datasetCardId: string | null;
  datasetCardName: string | null;

  datasetCd: string | null;
  datasetName: string | null;
  originSystemCd: string | null;
  originSystemName: string | null;
  datasetCardType: string | null;
  datasetCardSummary: string | null;
  preview: string | null;
  metadata: string | null;
  downloadPath: string | null;
};

export type GetTrainingDataListRequest = {
  page?: number;
  countPerPage?: number;
  cat01?: string; // 카테고리1 (학습/평가)
  cat02?: string; // 카테고리2 - 학습: SUPERVISED,UNSUPERVISED,DPO,CUSTOM / 평가: QUERY_SET,RESPONSE_SET,HUMAN_EVALUATION_RESULT_MANUAL,HUMAN_EVALUATION_RESULT_INTERACTIVE
  title?: string; // 제목 검색어
};

export type TrainingDataListItem = {
  createdBy: string | null;
  datasetCat01: string | null;
  datasetCat02: string | null;
  datasetCat03: string | null;
  datasetCat04: string | null;
  datasetCat05: string | null;
  descCtnt: string | null;
  fstCreatedAt: string | null;
  lstUpdatedAt: string | null;
  ozonePath: string | null;
  tags: string | null;
  title: string | null;
  updatedBy: string | null;
};

export type GetEvaluationDataListRequest = {
  page?: number;
  countPerPage?: number;
  cat01?: string; // 카테고리1 (학습/평가)
  cat02?: string; // 카테고리2 - 학습: SUPERVISED,UNSUPERVISED,DPO,CUSTOM / 평가: QUERY_SET,RESPONSE_SET,HUMAN_EVALUATION_RESULT_MANUAL,HUMAN_EVALUATION_RESULT_INTERACTIVE
  title?: string; // 제목 검색어
};

export type EvaluationDataListItem = {
  createdBy: string | null;
  datasetCat01: string | null;
  datasetCat02: string | null;
  datasetCat03: string | null;
  datasetCat04: string | null;
  datasetCat05: string | null;
  descCtnt: string | null;
  fstCreatedAt: string | null;
  lstUpdatedAt: string | null;
  ozonePath: string | null;
  tags: string | null;
  title: string | null;
  updatedBy: string | null;
};

// MD 패키지 상세 조회 관련 타입
export type GetMDPackageDetailRequest = {
  page?: number;
  countPerPage?: number;
  datasetCd: string;
  searchWord?: string;
  uuid?: string;
};

export type MDPackageDetailItem = {
  datasetCd: string | null;
  datasetName: string | null;
  docUuid: string | null;
  docTitle: string | null;
  docSummary: string | null;
  docCreateDay: number | null; // yyyymmdd 형태 정수
  docMdfcnDay: number | null; // yyyymmdd 형태 정수
  docPathAnonyMd: string | null;
  attachParentDocUuid: string | null;
  docArrayKeywords: string[] | null;
  originMetadata: Record<string, any> | null;
};

// 원천 시스템 관련 타입
export type OriginSystemItem = {
  datasetcardReferNm: string;
  datasetcardReferCd: string;
};

export type OriginSystemsResponse = {
  datasetReferList: OriginSystemItem[];
};
