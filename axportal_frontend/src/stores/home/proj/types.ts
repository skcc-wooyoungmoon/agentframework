// src/stores/home/proj/types.ts

// 프로젝트 참여 위자드에서 선택된 프로젝트 정보 타입
export type ProjJoinSelected = {
  id: string;
  no: number;
  projectName: string;
  description: string;
  participantCount: string;
  manager: string;
  fstCreatedAt: string;
  createrInfo: string;
};
