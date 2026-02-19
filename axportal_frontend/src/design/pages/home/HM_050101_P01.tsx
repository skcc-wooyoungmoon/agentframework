import { UIAlarm, UIAlarmGroup } from '@/components/UI/organisms';
import type { AlarmGroup } from '@/components/UI/organisms';
import { UIButton2 } from '@/components/UI/atoms/UIButton2';

import { DesignLayout } from '../../components/DesignLayout';
import { UITypography } from '@/components/UI';

export const HM_050101_P01: React.FC = () => {
  const handleClose = () => {};

  // 알람 데이터
  const alarmData: AlarmGroup[] = [
    {
      date: '2025.08.21',
      items: [
        {
          id: '1',
          title: '[모델명] 프로젝트 참여 요청 승인',
          description: '요청 승인되었습니다. 아래 프로젝트를 이용할 수 있어요.',
          time: '08:15:29',
          // isRead: false,
          type: 'dot',
          // actionButton prop을 사용하여 버튼 추가 (필요시 활성화)
          actionButton: <UIButton2 className='btn-option-outlined'>결재 취소</UIButton2>,
        },
        {
          id: '2',
          title: '[모델명] 모델 변경 요청 완료',
          description: '요청 변경되었습니다. 지금부터 새로운 답변방식에 맞춰해주세요.',
          time: '08:15:29',
          // isRead: false,
          type: 'dot',
          // 버튼이 필요없는 경우 actionButton prop을 생략하면 됨
        },
        {
          id: '3',
          title: '[모델명] 프로젝트 참여 요청 승인',
          description: '요청 승인되었습니다. 아래 프로젝트를 이용할 수 있어요.',
          time: '08:15:29',
          // isRead: true,
          type: 'normal',
          // 다른 버튼 예시
          actionButton: (
            <UIButton2 className='btn-option-outlined' disabled>
              결재 취소
            </UIButton2>
          ),
        },
      ],
    },
    {
      date: '2025.08.20',
      items: [
        {
          id: '4',
          title: '프로젝트 참여 요청 승인',
          description: '요청 승인되었습니다. 아래 프로젝트를 이용할 수 있어요.',
          time: '08:15:29',
          // isRead: true,
          type: 'normal',
        },
        {
          id: '5',
          title: '프로젝트 참여 요청 승인',
          description: '요청 승인되었습니다. 아래 프로젝트를 이용할 수 있어요.',
          time: '08:15:29',
          // isRead: true,
          type: 'normal',
        },
        {
          id: '6',
          title: '모델 변경 요청 완료',
          description: '요청 변경되었습니다. 지금부터 새로운 답변방식에 맞춰해주세요.',
          time: '08:15:29',
          // isRead: true,
          type: 'normal',
        },
      ],
    },
    {
      date: '2025.08.19',
      items: [
        {
          id: '7',
          title: '프로젝트 참여 요청 승인',
          description: '요청 승인되었습니다. 아래 프로젝트를 이용할 수 있어요.',
          time: '08:15:29',
          // isRead: true,
          type: 'normal',
        },
        {
          id: '8',
          title: '마지막 라인',
          description: '마지막 라인 테스트입니다.',
          time: '08:15:29',
          // isRead: true,
          type: 'normal',
        },
      ],
    },
  ];

  // 알람 클릭 Event
  const handleAlarmItemClick = (_item: { id: string; title: string; description: string; time: string; isRead?: boolean }, event: React.MouseEvent<HTMLDivElement>) => {
    // dot class
    const titleElement = event.currentTarget.querySelector('.title.dot');
    if (titleElement) {
      titleElement.classList.remove('dot');
    }
  };

  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'model', label: '모델' }}
        initialSubMenu={{
          id: 'model-catalog',
          label: '모델 카탈로그',
          icon: 'ico-lnb-menu-20-model-catalog',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              모델 카탈로그
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              파인튜닝 등록 진행 중...
            </UITypography>
          </div>
        </div>
      </DesignLayout>

      {/* 알람 */}
      <UIAlarm onClose={handleClose}>
        <div className='h-full bg-white'>
          <div className='alarm'>
            {/* [251102-퍼블수정] : 알림함 데이터 없을시 */}
            <UIAlarmGroup alarmData={alarmData} onItemClick={handleAlarmItemClick} />
          </div>
        </div>
      </UIAlarm>
    </>
  );
};
