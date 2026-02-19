export type GetSamplesRequest = {
  page: number;
  size: number;
  sort: string[];
};

export type GetSampleByIdRequest = {
  id: number;
};

export type SampleType = {
  id: number;
  username: string;
  fullName: string;
  email: string;
  phoneNumber: string;
  department: string;
  position: string;
  isActive: boolean;
  createdAt: string | null;
  updatedAt: string | null;
  createdBy: string | null;
  updatedBy: string | null;
};

export type GetSamplesResponse = SampleType;
export type GetSampleByIdResponse = SampleType;

export type PostSampleRequest = {
  username: string;
  fullName: string;
  email: string;
  phoneNumber: string;
  department: string;
  position: string;
  isActive: boolean;
};

export type PutSampleRequest = {
  id: number;
} & PostSampleRequest;
