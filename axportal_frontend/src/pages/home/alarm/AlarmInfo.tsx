import React, { useEffect, useState } from 'react';

import { UIButton2 } from '@/components/UI/atoms/UIButton2';
import type { AlarmGroup } from '@/components/UI/organisms';
import { UIAlarm, UIAlarmGroup } from '@/components/UI/organisms';
import { AlarmProjPopup } from '@/pages/home/alarm/AlarmProjPopup.tsx';
import { authServices } from '@/services/auth/auth.non.services.ts';
import { usePutCancelAlarm, usePutMarkAlarm, usePutReadBulkAlarm, useReadAlarms } from '@/services/home/alarm/alarmInfo.service';
import { useUser } from '@/stores';
import { useModal } from '@/stores/common/modal';

interface AlarmInfoProps {
  onClose?: () => void;
}

export const AlarmInfo: React.FC<AlarmInfoProps> = ({ onClose }) => {
  const [isAlarmInfoOpen, setIsAlarmInfoOpen] = useState(true);
  const { openAlert, openModal } = useModal();
  const [alarmData, setAlarmData] = useState<AlarmGroup[]>([]);
  const [disabledAlarmIds] = useState<Set<string>>(new Set());
  const { user, updateUser } = useUser();
  const { openConfirm } = useModal();

  const username = user.userInfo.memberId;

  const handleClose = () => {
    setIsAlarmInfoOpen(false);
    if (onClose) {
      onClose();
    }
  };

  const { data, refetch } = useReadAlarms({ username: username });

  /**
   * 일괄 읽음 처리
   */
  const { mutate: putReadBulkAlarm } = usePutReadBulkAlarm({
    onSuccess: async () => /* data */ {
      // console.log('알림 일괄 읽음 처리 성공:', data);
      // 사용자 데이터를 갱신
      try {
        const updatedUser = await authServices.getMe();
        if (updatedUser) {
          updateUser(updatedUser);
        }
      } catch {
        // 핵심 비즈니스로직과 상관없는 정보 업데이트 코드
        // 사용자 정보를 갱신시켜 화면에 보이는 프로젝트 정보를 갱신해주기 위함이며 오류가 발생하더라도 건너뛰기 위해 try/catch 처리함
      }
    },
    onError: /* error */ () => {
      // console.error('알림 일괄 읽음 처리 실패:', error);
    },
  });

  const processAlarmData = async () => {
    if (data) {
      // console.log('++++++++++++++++++++++++++++++!');
      // console.log('useReadAlarms 응답 데이터 (useEffect):', JSON.stringify(data, null, 2));
      if (data && data.alarms && data.alarms.length > 0) {
        const groupedAlarms = convertToAlarmGroups(data.alarms);
        setAlarmData(groupedAlarms);

        // 데이터 읽은 후 일괄 읽음 처리 API 호출
        putReadBulkAlarm({ username: username });
      }

      // 사용자 데이터를 갱신
      try {
        const updatedUser = await authServices.getMe();
        if (updatedUser) {
          updateUser(updatedUser);
        }
      } catch {
        // 핵심 비즈니스로직과 상관없는 정보 업데이트 코드
        // 사용자 정보를 갱신시켜 화면에 보이는 프로젝트 정보를 갱신해주기 위함이며 오류가 발생하더라도 건너뛰기 위해 try/catch 처리함
      }
    }
  };

  // 컴포넌트 마운트 시 useEffect를 사용하여 데이터를 콘솔에 출력
  useEffect(() => {
    processAlarmData();
  }, [data]);

  const handleCancelPayment = (alarmId: string, event: React.MouseEvent) => {
    // 이벤트 버블링 방지 (알람 아이템 클릭 이벤트와 분리)
    event.stopPropagation();

    openConfirm({
      title: '안내',
      message: '결재 요청을 취소하시겠어요?\n' + '취소 시, 처음부터 다시 결재를 요청해야합니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        putCancelAlarm({ alarmId, username: username });
      },
      onCancel: () => {
        // console.log('취소됨');
      },
    });
  };

  // AlarmInfo[] 배열을 날짜별로 그룹화하여 AlarmGroup[] 형태로 변환하는 함수
  const convertToAlarmGroups = (alarms: any[]): AlarmGroup[] => {
    // 날짜별로 알람을 그룹화
    const groupedByDate: { [date: string]: any[] } = {};
    // console.log('alarms:', alarms);

    alarms.forEach(alarm => {
      try {
        // createdAt이 유효한 날짜인지 확인
        const dateObj = new Date(alarm.createdAt);

        // status_nm에 따른 버튼 생성 함수
        const createActionButton = (statusNm: string, alarmId: string) => {
          if (!statusNm) return undefined;
          // enable: 활성화된 버튼
          if (statusNm === 'REQUEST') {
            return (
              <UIButton2 className='btn-option-outlined' onClick={e => handleCancelPayment(alarmId, e)} disabled={disabledAlarmIds.has(alarmId)}>
                결재 취소
              </UIButton2>
            );
          }
          // disable: 비활성화된 버튼
          if (statusNm === 'REJECT' || statusNm === 'COMPLETED') {
            return (
              <UIButton2 className='btn-option-outlined' disabled>
                결재 취소
              </UIButton2>
            );
          }
          if (statusNm === 'CANCELED') {
            return (
              <UIButton2 className='btn-option-outlined' disabled>
                취소 완료
              </UIButton2>
            );
          }
          return undefined;
        };

        // 유효하지 않은 날짜인 경우 처리
        if (isNaN(dateObj.getTime())) {
          // console.warn('유효하지 않은 날짜 형식:', alarm.createdAt);
          // 기본 날짜 사용 (오늘 날짜)
          const today = new Date();
          const formattedDate = `${today.getFullYear()}.${String(today.getMonth() + 1).padStart(2, '0')}.${String(today.getDate()).padStart(2, '0')}`;
          const formattedTime = '00:00:00'; // 기본 시간

          // 해당 날짜의 그룹이 없으면 생성
          if (!groupedByDate[formattedDate]) {
            groupedByDate[formattedDate] = [];
          }

          // AlarmItem 형식에 맞게 변환하여 추가
          groupedByDate[formattedDate].push({
            id: alarm.alarmId || '',
            memberId: alarm.memberId || '',
            title: alarm.ttlNm || '알림',
            description: alarm.alarmCtnt || '',
            time: formattedTime,
            type: alarm.readYn === '0' ? 'dot' : 'normal',
            statusNm: alarm.statusNm || '',
            apiRstMsg: alarm.apiRstMsg || '',
            createdAt: alarm.createdAt, // 원본 날짜 정보 보존
            actionButton: createActionButton(alarm.statusNm, alarm.alarmId),
          });
        } else {
          // 정상적인 날짜 처리
          // YYYY.MM.DD 형식으로 날짜 포맷팅
          // console.warn('유효하지 않은 날짜 형식:', alarm.createdAt);

          const formattedDate = `${dateObj.getFullYear()}.${String(dateObj.getMonth() + 1).padStart(2, '0')}.${String(dateObj.getDate()).padStart(2, '0')}`;

          // HH:MM:SS 형식으로 시간 포맷팅
          const formattedTime = `${String(dateObj.getHours()).padStart(2, '0')}:${String(dateObj.getMinutes()).padStart(2, '0')}:${String(dateObj.getSeconds()).padStart(2, '0')}`;

          // 해당 날짜의 그룹이 없으면 생성
          if (!groupedByDate[formattedDate]) {
            groupedByDate[formattedDate] = [];
          }

          // AlarmItem 형식에 맞게 변환하여 추가
          groupedByDate[formattedDate].push({
            id: alarm.alarmId || '',
            memberId: alarm.memberId || '',
            title: alarm.ttlNm || '알림',
            description: alarm.alarmCtnt || '',
            time: formattedTime,
            type: alarm.readYn === '0' ? 'dot' : 'normal',
            statusNm: alarm.statusNm || '',
            apiRstMsg: alarm.apiRstMsg || '',
            createdAt: alarm.createdAt, // 원본 날짜 정보 보존
            actionButton: createActionButton(alarm.statusNm, alarm.alarmId),
          });
        }
      } catch {
        // 알람항목 N개 처리 중 데이터 규격 오류로 인해 정상적이지 않은 알람 데이터는 스킵하기 위해 try/catch 처리함
      }
    });

    // 날짜별 그룹을 AlarmGroup[] 형태로 변환하고 날짜순으로 정렬 (최신 날짜가 먼저 오도록)
    const result: AlarmGroup[] = Object.keys(groupedByDate)
      .sort((a, b) => new Date(b.replace(/\./g, '-')).getTime() - new Date(a.replace(/\./g, '-')).getTime())
      .map(date => ({
        date,
        // 각 날짜 그룹 내에서 시간순으로 정렬 (최신 시간이 먼저 오도록)
        items: groupedByDate[date].sort((a, b) => {
          // createdAt 값이 있는 경우 이를 기준으로 정렬
          if (a.createdAt && b.createdAt) {
            return new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
          }
          // time 값으로 정렬 (HH:MM:SS 형식)
          return b.time.localeCompare(a.time);
        }),
      }));

    return result;
  };

  // 알람 프로젝트 팝업을 여는 함수
  const handleOpenAlarmProjPopup = (partialItem: { id: string; title: string; description: string; time: string; isRead?: boolean | undefined }) => {
    const fullItem = getFullAlarmItem(partialItem);
    const tmpProjectId = fullItem.apiRstMsg;
    // AlarmInfo 닫기
    setIsAlarmInfoOpen(false);
    // AlarmProjPopup 모달 열기 (바탕 화면은 모달 시스템의 반투명/투명 배경 적용)
    openModal({
      type: 'large',
      title: '간편결재 결과',
      showFooter: true,
      cancelText: '',
      confirmText: '확인',
      onClose: () => {
        // 모달이 X 버튼이나 배경 클릭으로 닫힐 때도 알람 패널 다시 열기
        setIsAlarmInfoOpen(true);
      },
      onCancel: () => {
        // 모달 푸터의 취소로 닫힐 때도 알람 패널 다시 열기
        setIsAlarmInfoOpen(true);
      },
      onConfirm: () => {
        // 모달 푸터의 확인으로 닫힐 때도 알람 패널 다시 열기
        setIsAlarmInfoOpen(true);
      },
      body: <AlarmProjPopup alarmItem={partialItem} projectId={tmpProjectId} />,
    });
  };

  // 알람 클릭 Event
  const handleAlarmItemClick = (partialItem: { id: string; title: string; description: string; time: string; isRead?: boolean | undefined }) => {
    const item = getFullAlarmItem(partialItem);

    // 알람 읽음 처리
    const alarmData = {
      alarmId: item.id,
      username: username || '',
    };
    putMarkAlarm(alarmData);

    // 실패 케이스는 결재상태를 포함하고 있으므로 텍스트 예외처리
    if (['APPROVAL', 'REJECT'].includes(item.statusNm) && !item.title.endsWith('실패')) {
      handleOpenAlarmProjPopup(item);
    }
  };

  const getFullAlarmItem = (partialItem: { id: string; title: string; description: string; time: string; isRead?: boolean }) => {
    let fullItem: any = null;

    for (const group of alarmData) {
      const foundItem = group.items.find(i => i.id === partialItem.id);
      if (foundItem) {
        fullItem = foundItem;
        break;
      }
    }

    if (fullItem) {
      return {
        id: fullItem.id,
        title: fullItem.title,
        description: fullItem.description,
        time: fullItem.time,
        isRead: fullItem.isRead,
        memberId: fullItem.memberId,
        statusNm: fullItem.statusNm,
        apiRstMsg: fullItem.apiRstMsg,
      };
    } else {
      // fallback: 원본 item으로 반환
      // console.warn('알람 전체 데이터를 찾을 수 없습니다:', partialItem.id);
      return {
        ...partialItem,
        memberId: '',
        statusNm: '',
        apiRstMsg: '',
      };
    }
  };
  /**
   * dot 로 메시지 확인 여부 정보 전달
   */
  const { mutate: putMarkAlarm } = usePutMarkAlarm({
    onSuccess: /* data */ () => {
      // console.log('알림 읽음 처리 성공:', data);
      // 알림 목록을 다시 조회하여 화면을 갱신
      if (typeof refetch === 'function') {
        refetch();
      }
    },
    onError: /* error */ () => {
      // console.error('알림 읽음 처리 실패:', error);
      // 실패 알림 표시 (필요 시)
      openAlert({
        title: '오류',
        message: '알림 읽음 처리 중 오류가 발생했습니다. 다시 시도해주세요.',
      });
    },
  });
  /**
   * 결재취소 버튼 클릭
   */
  const { mutate: putCancelAlarm } = usePutCancelAlarm({
    onSuccess: /* data */ () => {
      // console.log('알림 읽음 처리 성공:', data);

      openAlert({
        title: '완료',
        message: '결재 요청 취소가 완료되었습니다.',
      });

      // 알림 목록을 다시 조회하여 화면을 갱신
      if (typeof refetch === 'function') {
        refetch();
      }
    },
    onError: /* error */ () => {
      // console.error('결재 취소 처리 실패:', error);

      openAlert({
        title: '실패',
        message: '결재 요청 취소에 실패했습니다.',
      });
    },
  });

  return (
    <>
      {/* 알람 */}
      {isAlarmInfoOpen && (
        <UIAlarm onClose={handleClose}>
          <div className='h-full bg-white'>
            <div className='alarm'>
              <UIAlarmGroup alarmData={alarmData} onItemClick={handleAlarmItemClick} />
            </div>
          </div>
        </UIAlarm>
      )}
    </>
  );
};
