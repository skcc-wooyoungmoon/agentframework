export interface GetNoticesRequest {
  page?: number;
  size?: number; // Spring Boot Pageable 표준 파라미터
  searchType?: 'title' | 'content' | 'titleContent';
  searchValue?: string;
  dateFrom?: string; // YYYY.MM.DD format
  dateTo?: string; // YYYY.MM.DD format
  condition?: '전체' | '제목' | '내용'; // 검색어 유형
  noticeType?: '전체' | '시스템 점검' | '서비스 출시 및 오픈' | '보안 안내' | '이용 가이드' | '버전/기능 업데이트' | '기타';
  sort?: string; // 기본값: "modifiedDate,desc"
}

export interface NoticeItem {
  id: string;
  title: string;
  type: string;
  content: string;
  modifiedDate: string;
  createdDate: string;
}

export interface GetNoticesResponse {
  items: NoticeItem[];
  totalCount: number;
  page: number;
  pageSize: number;
}

export interface GetNoticeByIdRequest {
  noticeId: string;
}

export interface GetNoticeByIdResponse {
  notiId: number;
  title: string;
  msg: string;
  type: string;
  useYn: string;
  expFrom: string;
  expTo: string;
  createAt: string;
  createBy: string;
  createByName: string;
  updateAt: string;
  updateBy: string;
  updateByName: string;
  files?: {
    fileId: number;
    originalFilename: string;
    storedFilename: string;
    contentType: string;
    filePath: string;
    fileSize: number;
  }[];
}
