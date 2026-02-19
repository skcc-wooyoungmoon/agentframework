/**
 * 이미지 타입 (백엔드 값)
 */
export const ImageType = {
  VSCODE: 'VSCODE',
  JUPYTER: 'JUPYTER',
} as const;

export type ImageType = (typeof ImageType)[keyof typeof ImageType];

/**
 * 이미지 타입 UI 라벨 매핑
 * 백엔드 값 → UI 라벨
 */
export const IMAGE_TYPE_LABEL: Record<ImageType, string> = {
  VSCODE: 'VS Code',
  JUPYTER: 'Jupyter Notebook',
};

/**
 * UI 라벨을 백엔드 값으로 변환
 */
export const UI_LABEL_TO_IMAGE_TYPE: Record<string, ImageType> = {
  'VS Code': ImageType.VSCODE,
  'Jupyter Notebook': ImageType.JUPYTER,
};

/**
 * 이미지 목록 조회 요청 타입
 *
 * @param page - 페이지 번호
 * @param size - 페이지 크기 (기본값: 12)
 * @param keyword - 검색어 (이미지명, 설명)
 * @param imgG - 이미지 구분 (VSCODE, JUPYTER)
 */
export type GetImageListRequest = {
  page?: number;
  size?: number;
  keyword?: string;
  imgG?: ImageType;
};

/**
 * 이미지 정보 타입
 *
 * @param uuid - 이미지 UUID
 * @param imgG - 이미지 타입
 * @param imgNm - 이미지명
 * @param dtlCtnt - 이미지 상세 설명
 * @param fstCreatedAt - 생성 일시 (yyyy-MM-dd HH:mm:ss)
 * @param lstUpdatedAt - 수정 일시 (yyyy-MM-dd HH:mm:ss)
 */
export type ImageInfo = {
  uuid: string;
  imgG: ImageType;
  imgNm: string;
  dtlCtnt: string;
  fstCreatedAt: string;
  lstUpdatedAt: string;
};

/**
 * 이미지 목록 조회 응답 타입
 */
export type GetImageListResponse = ImageInfo;

/**
 * UI에서 사용하는 이미지 행 데이터
 *
 * @param id - 행 번호 (NO 컬럼용)
 * @param uuid - 이미지 UUID
 * @param toolName - 도구명 (UI 표시용, IMAGE_TYPE_LABEL로 변환된 값)
 * @param imageName - 이미지명
 * @param description - 설명
 * @param createdDate - 생성일시
 * @param modifiedDate - 최종 수정일시
 */
export type ImageRowData = {
  id: string;
  uuid: string;
  toolName: string;
  imageName: string;
  description: string;
  createdDate: string;
  modifiedDate: string;
};

/**
 * 이미지 상세 조회 응답 타입
 *
 * @param uuid - 이미지 UUID
 * @param imgNm - 이미지명
 * @param dtlCtnt - 이미지 설명
 * @param imgG - 이미지 구분 (VSCODE, JUPYTER)
 * @param imgUrl - 이미지 경로/URL
 * @param createdAt - 생성일시
 * @param createdBy - 생성자
 * @param updatedAt - 수정일시
 * @param updatedBy - 수정자
 */
export type GetImageDetailResponse = {
  uuid: string;
  imgNm: string;
  dtlCtnt: string;
  imgG: ImageType;
  imgUrl: string;
  createdAt: string;
  createdBy: string;
  updatedAt: string;
  updatedBy: string;
};

/**
 * UI에서 사용하는 이미지 상세 데이터
 *
 * @param uuid - 이미지 UUID
 * @param toolName - 도구명 (IMAGE_TYPE_LABEL로 변환된 값)
 * @param imageName - 이미지명
 * @param description - 설명
 * @param imageUrl - 이미지 URL
 * @param creator - 생성자
 * @param createdDate - 생성일시
 * @param modifier - 수정자
 * @param modifiedDate - 수정일시
 */
export type ImageDetailData = {
  uuid: string;
  toolName: string;
  imageName: string;
  description: string;
  imageUrl: string;
  creator: string;
  createdDate: string;
  modifier: string;
  modifiedDate: string;
};

/**
 * 이미지 생성 요청 타입
 *
 * @param imgG - 이미지 타입 (VSCODE, JUPYTER)
 * @param imgNm - 이미지명
 * @param imgUrl - 이미지 경로/URL
 * @param dtlCtnt - 설명
 */
export type CreateImageRequest = {
  imgG: ImageType;
  imgNm: string;
  imgUrl: string;
  dtlCtnt: string;
};

/**
 * 이미지 생성 응답 타입
 */
export type CreateImageResponse = {
  uuid: string; // 생성된 이미지 UUID
};

/**
 * 이미지 삭제 요청 타입
 *
 * @param uuids - 삭제할 이미지 UUID 목록
 */
export type DeleteImageRequest = {
  uuids: string[];
};

/**
 * 이미지 삭제 응답 타입
 */
export type DeleteImageResponse = void;

/**
 * 이미지 수정 요청 타입
 *
 * @param imgG - 이미지 타입 (VSCODE, JUPYTER)
 * @param imgNm - 이미지명 (max 150 chars)
 * @param imgUrl - 이미지 경로/URL (max 300 chars)
 * @param dtlCtnt - 설명 (max 4000 chars)
 */
export type UpdateImageRequest = {
  imgG: ImageType;
  imgNm: string;
  imgUrl: string;
  dtlCtnt: string;
};

/**
 * 이미지 수정 응답 타입
 */
export type UpdateImageResponse = void;

/**
 * 이미지 리소스 환경 설정 요청 타입
 *
 * @param imgG - 이미지 구분 (VSCODE, JUPYTER)
 * @param limitCnt - 이미지 생성 가능 개수 (최소 1개 이상)
 */
export type UpdateImageResourceRequest = {
  imgG: ImageType;
  limitCnt: number;
};

/**
 * 이미지 리소스 환경 설정 응답 타입
 */
export type UpdateImageResourceResponse = void;

/**
 * IDE 리소스 환경 설정 조회 응답 타입
 *
 * @param imgG - 이미지 타입 (VSCODE, JUPYTER)
 * @param limitCnt - 생성 가능 개수
 */
export type ImageResourceRes = {
  imgG: ImageType;
  limitCnt: number;
};

/**
 * IDE 리소스 환경 설정 조회 응답 (배열)
 */
export type GetImageResourceResponse = ImageResourceRes[];

/**
 * DW 계정 정보 (백엔드 응답)
 *
 * @param accountId - 계정 ID
 * @param role - 계정 역할
 */
export type DwAccountRes = {
  accountId: string;
  role: string;
};

/**
 * DW 계정 목록 조회 응답
 */
export type GetDwAccountListResponse = DwAccountRes[];

/**
 * UI에서 사용하는 DW 계정 행 데이터
 *
 * @param id - 행 번호 (NO 컬럼용)
 * @param accountId - 계정 ID
 * @param accountType - 계정 유형 (role을 UI 표시용으로 변환)
 */
export type DwAccountRowData = {
  id: string;
  accountId: string;
  accountType: string;
};
