export type createRequest = {
  notiId?: number;
  title: string;
  msg: string;
  type: string;
  useYn: string;
  expTo: string;
  expFrom: string;
  existingFileIds?: number[];
  newFiles?: File[];
};

export type noticeRespose = {
  notiId: number;
  title: string;
  msg: string;
  type: string;
  useYn: string;
  expAt?: string;
  expFrom?: string;
  expTo?: string;
  createAt: string;
  updateAt: string;
  createBy: string;
  updateBy: string;
  createdByName?: string;
  createdByDepts?: string;
  updatedByName?: string;
  updatedByDepts?: string;
  files?: Array<{
    fileId: number;
    originalFilename: string;
    storedFilename: string;
    fileSize: number;
    contentType: string;
    uploadDate: string;
    useYn: string;
  }>;
};
