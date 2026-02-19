import { forwardRef, useEffect, useImperativeHandle, useRef, useState } from 'react';

import { UIInput } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { MODAL_ID } from '@/constants/modal/modalId.constants.ts';
import { useModal } from '@/stores/common/modal';
import type { PostSwingSmsRequest } from '@/services/auth/types.ts';
import { usePostSwingSms } from '@/services/auth/auth.services.ts';

/**
 * LG_010101_P06 - SMS 인증
 */

export type LoginSwingSmsAuthPopupHandle = {
  getAuthCode: () => string;
  getEventId: () => string;
  getSmsTimer: () => string;
  setError: (message: string) => void;
};

type LoginSwingSmsAuthPopupProps = {
  employeeId: string;
  onEnter?: () => void;
};

export const LoginSwingSmsAuthPopup = forwardRef<LoginSwingSmsAuthPopupHandle, LoginSwingSmsAuthPopupProps>((props, ref) => {
  const [authCode, setAuthCode] = useState('');
  const [eventId, setEventId] = useState('');
  const [isTimerRunning, setIsTimerRunning] = useState(false);
  const [timer, setTimer] = useState<string>('00:00');
  const [errorMessage, setErrorMessage] = useState('');
  const intervalRef = useRef<number | null>(null);
  const { openAlert, updateModal } = useModal();
  const { mutate: postSwingSms } = usePostSwingSms();

  useImperativeHandle(ref, () => ({
    getAuthCode: () => authCode,
    getEventId: () => eventId,
    getSmsTimer: () => timer,
    setError: (message: string) => setErrorMessage(message),
  }));

  useEffect(() => {
    updateModal(MODAL_ID.SWING_SMS_MODAL, { confirmDisabled: authCode.length !== 6 });
  }, [authCode, updateModal]);

  useEffect(() => {
    startCountdown(120);
    return () => {
      if (intervalRef.current) {
        window.clearInterval(intervalRef.current);
      }
    };
  }, []);

  const formatTime = (seconds: number) => {
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`;
  };

  const startCountdown = (durationSec = 120) => {
    clearCountdown();
    setIsTimerRunning(true);
    let remaining = durationSec;
    setTimer(formatTime(remaining));
    intervalRef.current = window.setInterval(() => {
      remaining -= 1;
      if (remaining <= 0) {
        setTimer('00:00');
        clearCountdown();
      }
      setTimer(formatTime(remaining));
    }, 1000);
  };

  const clearCountdown = () => {
    if (intervalRef.current) {
      window.clearInterval(intervalRef.current);
      intervalRef.current = null;
    }
    setTimer('00:00');
    // setIsTimerRunning(false);
  };

  const swingSmsRequestData: PostSwingSmsRequest = {
    username: props.employeeId,
  };

  const swingSmsAuthRequest = () => {
    postSwingSms(swingSmsRequestData, {
      onSuccess: response => {
        // console.log('등록 성공:', response);

        // 이벤트ID 재설정
        if (response?.status === 200) {
          setEventId(response.data.authEventId);
          startCountdown(120);
        } else {
          // 그 외 상태코드: 에러 처리
          openAlert({
            title: '오류',
            message: '인증 처리 중 알 수 없는 응답이 발생했습니다. 다시 시도해주세요.',
            confirmText: '확인',
          });
        }
      },
      onError: () => {
        // onError: error => {
        // console.log(error);
      },
    });
  };

  return (
    <section className='section-modal'>
      <UIArticle>
        <div className='article-body'>
          <UIInput.Auth
            value={authCode}
            status={isTimerRunning ? 'processing' : 'error'}
            timer={timer}
            placeholder='인증번호 6자리 입력'
            maxLength={6}
            onChange={e => {
              setAuthCode(e.target.value.replace(/\D/g, '').slice(0, 6));
              setErrorMessage('');
            }}
            onKeyDown={e => {
              if (e.key === 'Enter' && authCode.length === 6) {
                props.onEnter?.();
              }
            }}
            error={errorMessage}
            authButtonDisabled={isTimerRunning && timer != '00:00'}
            onClickAuthRequest={swingSmsAuthRequest}
          />
        </div>
      </UIArticle>
    </section>
  );
});
