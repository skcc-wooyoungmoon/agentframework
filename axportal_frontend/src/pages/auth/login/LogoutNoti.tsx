import { useState, useEffect, useRef } from 'react';

import { UIIcon2, UITypography } from '@/components/UI/atoms';
import { UIArticle } from '@/components/UI/molecules';
import { useModal } from '@/stores/common/modal';

interface LogoutNoti {
  initialTime: number; // 초 단위
  onTimeEnd?: () => void;
  onConfirm?: () => void;
  onClose?: () => void;
  showDebugInfo?: boolean; // 디버그 정보 표시 여부 (기본값: false)
}

export const TransactionTimerModal = ({
  initialTime = 60 * 10, // 기본값 10분(600초)
  onTimeEnd,
}: LogoutNoti) => {
  const [timeLeft, setTimeLeft] = useState<number>(initialTime);
  const { closeAllModals } = useModal();
  const startTimeRef = useRef<number>(Date.now());
  const timerIdRef = useRef<NodeJS.Timeout | null>(null);

  // 시간을 분:초 형식으로 변환
  const formatTime = (seconds: number): string => {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes.toString().padStart(2, '0')}분 ${remainingSeconds.toString().padStart(2, '0')}초`;
  };

  const clearTimer = () => {
    if (timerIdRef.current) {
      clearInterval(timerIdRef.current);
      timerIdRef.current = null;
    }
  };

  useEffect(() => {
    // 컴포넌트 마운트 시 시작 시간 초기화
    startTimeRef.current = Date.now();

    // 기존 타이머 제거
    clearTimer();

    // 타이머가 이미 0이면 바로 종료
    if (initialTime <= 0) {
      onTimeEnd?.();
      closeAllModals();
      return;
    }

    // 정확한 타이머를 위해 현재 시간과 비교해 남은 시간 계산
    timerIdRef.current = setInterval(() => {
      const elapsedTime = Math.floor((Date.now() - startTimeRef.current) / 1000);
      const remaining = Math.max(0, initialTime - elapsedTime);

      setTimeLeft(remaining);

      if (remaining <= 0) {
        clearTimer();
        // console.log('================================== time end');
        onTimeEnd?.();
      }
    }, 500); // 0.5초마다 업데이트하여 더 정확한 표시 제공

    // 컴포넌트 언마운트 시 인터벌 정리
    return () => clearTimer();
  }, [closeAllModals, initialTime, onTimeEnd]);

  return (
    <section className='section-modal'>
      <UIArticle className='flex flex-col justify-center items-center'>
        {/* 피드백 아이콘 - 72px */}
        <UIIcon2 className='ic-system-72-feedback' />
        <div className='article-header' style={{ marginTop: '24px', marginBottom: '4px' }}>
          <UITypography variant='title-2' className='secondary-neutral-900 text-sb'>
            <UITypography variant='title-2' className='primary-800 text-sb'>
              {formatTime(timeLeft)}
            </UITypography>{' '}
            후 자동 로그아웃 예정입니다.
          </UITypography>
        </div>
        <div className='article-body' style={{ textAlign: 'center' }}>
          <UITypography variant='body-1' className='secondary-neutral-600'>
            [확인] 버튼을 클릭하면 로그인 시간을 <br /> 초기화할 수 있어요.
          </UITypography>
        </div>
      </UIArticle>
    </section>
  );
};
