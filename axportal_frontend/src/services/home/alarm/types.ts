import type { PaymentApprovalRequest } from '@/services/common/types.ts';

export interface AlarmInfo {
  id: string;
  username: string;
  type: string;
  msg: string;
  isRead: boolean;

  createdAt: string;
  createdBy: string;
  updateAt: string;
  updateBy: string;
  readAt: string;
}

export interface ReadAlarmsResponse {
  count: number;
  alarms: AlarmInfo[];
}
/**
 * @description 알림 읽음 처리 요청
 */
export interface MarkAlarmReadRequest {
  alarmId: string;
}

export interface ReadAlarmsRequest {
  username: string;
}

export interface CreateAlarmRequest {
  username?: string; // 사용자 이름 (선택적)
  msg: string; // 알림 내용
  type: string; // 알림 유형 (예: 'NOTIFICATION', 'WARNING', 'INFO')
}

export interface MarkAlarmReadRequest {
  alarmId: string; // URL 경로 파라미터
  username: string; // 쿼리 또는 본문 파라미터
}

export interface CancelAlarmRequest {
  alarmId: string; // URL 경로 파라미터
  username: string; // 쿼리 또는 본문 파라미터
}

export interface ReadBulkAlarmRequest {
  username: string; // URL 경로 파라미터
}

export interface ApprovalUserRequest {
  alarmId: string; // URL 경로 파라미터
}

export interface ApprovalUserReponse {
  apiSpclV: string;
  memberId: string;
  gyljjaMemberId: string;
  deptNm: string;
  jkwNm: string;
  fstCreatedAt: string;
  payApprovalInfo: PaymentApprovalRequest;
}
