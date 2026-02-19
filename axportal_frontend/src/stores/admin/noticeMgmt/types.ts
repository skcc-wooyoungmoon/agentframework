/**
 * 공지사항 상세 데이터 타입
 */
export interface NoticeDetailData {
  notiId?: string;
  id?: string;
  title?: string;
  msg?: string;
  content?: string;
  useYn?: string;
  type?: string;
  expFrom?: string;
  expTo?: string;
  createBy?: string;
  createAt?: string;
  modifiedDate?: string;
  updateAt?: string;
  createdAt?: string;
  files?: Array<{
    fileId: number;
    originalFilename: string;
    storedFilename: string;
    fileSize: number;
    contentType: string;
    uploadDate: string;
    useYn: string;
  }>;
}
