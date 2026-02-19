import { useCallback, useEffect, useRef } from 'react';

import { useLocation, useNavigate } from 'react-router';

import { LogoutAutoNoti } from '@/pages/auth/login/LogoutAutoNoti';
import { TransactionTimerModal } from '@/pages/auth/login/LogoutNoti.tsx';
import { authServices } from '@/services/auth/auth.non.services.ts';
import { useModal } from '@/stores/common/modal';
import { authUtils } from '@/utils/common';

/**
 * TransactionInterceptor
 * - axios 요청/응답에서 발생시키는 트랜잭션 이벤트를 수신하여 얼럿을 띄웁니다.
 * - 성공 시에만 알림을 노출합니다. (에러는 ErrorHandler에서 처리)
 */
export const TransactionInterceptor = () => {
  const { openAlert, openModal, closeAllModals } = useModal();
  const timerStartTimeRef = useRef<number>(0); // 타이머 시작 시간
  const timerIntervalRef = useRef<NodeJS.Timeout | null>(null); // 인터벌 타이머
  const firstModalOpenedRef = useRef<boolean>(false); // 1차 모달이 열렸는지 여부
  const navigate = useNavigate();
  const location = useLocation();

  // Ref to hold the current openFirstWarningModal function to avoid circular dependency
  const openFirstWarningModalRef = useRef<() => void>(() => {});

  // 타이머 초기화 함수
  const clearIdleTimer = useCallback(() => {
    if (timerIntervalRef.current !== null) {
      clearInterval(timerIntervalRef.current);
      timerIntervalRef.current = null;
    }
    firstModalOpenedRef.current = false; // 모달 상태 초기화
  }, []);

  // 유휴 타이머 시작 함수 (50분 후 1차 경고 팝업)
  const startIdleTimer = useCallback(() => {
    // 기존 타이머가 있다면 취소
    clearIdleTimer();

    // 로그인 페이지에서는 타이머 동작 안함
    if (window.location.pathname === '/' || window.location.pathname.startsWith('/login')) {
      return;
    }

    firstModalOpenedRef.current = false; // 모달 상태 초기화
    const firstWarningTimeMs = 50 * 60 * 1000; // 1차 경고 (50분)
    // const firstWarningTimeMs = 10 * 1000; // 테스트목적 (10초)

    // 타이머 시작 시간 저장
    timerStartTimeRef.current = Date.now();

    // 정확한 타이머를 위해 현재 시간과 계산하는 방식으로 처리
    timerIntervalRef.current = setInterval(() => {
      // 시작 시간부터 경과한 시간 (밀리초)
      const elapsedMs = Date.now() - timerStartTimeRef.current;

      // 50분 지났을 때 1차 경고 모달 표시 (한 번만)
      if (elapsedMs >= firstWarningTimeMs && !firstModalOpenedRef.current) {
        // console.log('50분이 경과했습니다. 1차 경고 모달을 표시합니다.');
        if (openFirstWarningModalRef.current) {
          openFirstWarningModalRef.current();
        }
        // 1차 모달의 TransactionTimerModal이 10분 카운트다운 후 자동으로 handleLogout() 호출
      }
    }, 1000);
  }, [clearIdleTimer]);

  const handleLogout = useCallback(() => {
    try {
      // timer 정지
      clearIdleTimer();
      closeAllModals();

      authServices.logout();

      // closeAllModals 후 약간의 딜레이를 주고 로그아웃 완료 팝업 표시
      setTimeout(() => {
        openModal({
          type: 'small',
          title: '로그아웃 안내',
          showFooter: true,
          confirmText: '확인',
          backdropClosable: false,
          body: <LogoutAutoNoti />,
          onConfirm: () => {
            // window.location.href = '/login';
            navigate('/', { replace: true });
          },
          onClose: () => {
            // window.location.href = '/login';
            navigate('/', { replace: true });
          },
        });
      }, 100);

      // window.location.href = '/login';
    } catch {
      // console.error('로그아웃 실패:', error);
      // 오류가 발생해도 토큰은 삭제하고 로그인 페이지로 리다이렉트
      authUtils.clearTokens();
      openAlert({
        title: '오류',
        message: '로그아웃 처리 중 오류가 발생했습니다. 다시 로그인해주세요.',
        confirmText: '확인',
        onConfirm: () => {
          // window.location.href = '/login';
          navigate('/', { replace: true });
        },
      });
    }
  }, [clearIdleTimer, closeAllModals, openModal, navigate, openAlert]);

  /**
   * 1차 경고 모달 (50분 시점)
   */
  const openFirstWarningModal = useCallback(() => {
    firstModalOpenedRef.current = true;
    openModal({
      type: 'small',
      title: '로그아웃 안내',
      showFooter: true,
      confirmText: '확인',
      backdropClosable: false,
      body: (
        <TransactionTimerModal
          initialTime={60 * 10} // 10분
          // initialTime={10} // 10초 (테스트)
          onTimeEnd={() => {
            handleLogout();
          }}
        />
      ),
      onConfirm: async () => {
        // 확인 버튼 클릭 시 타이머 리셋
        await authServices.refresh(); // 토큰 리프래시
        firstModalOpenedRef.current = false;
        clearIdleTimer();
        startIdleTimer();
      },
    });
  }, [openModal, handleLogout, clearIdleTimer, startIdleTimer]);

  // Update ref
  useEffect(() => {
    openFirstWarningModalRef.current = openFirstWarningModal;
  }, [openFirstWarningModal]);

  useEffect(() => {
    if (location.pathname === '/' || location.pathname.startsWith('/login')) {
      clearIdleTimer();
    }
  }, [location.pathname, clearIdleTimer]);

  useEffect(() => {
    // 트랜잭션 시작 핸들러
    const handleTransactionStart = (event: CustomEvent) => {
      // console.log('트랜잭션 시작:', event.detail);
      // 로그인 API가 아닌 경우에만 타이머 시작
      // event.detail은 이제 config 객체입니다
      if (event.detail) {
        const url = event.detail.url || '';
        if (!url.includes('/auth/logout') && !url.includes('/auth/register')) {
          startIdleTimer();
        } else {
          clearIdleTimer();
        }
      }
    };

    // 트랜잭션 성공 핸들러
    const handleTransactionSuccess = (event: CustomEvent) => {
      //  console.log('트랜잭션 성공:', event.detail);
      // 로그인 성공 시에만 타이머 시작, 실패 시에는 시작하지 않음
      if (event.detail && event.detail.config) {
        const url = event.detail.config.url || '';
        if (!url.includes('/auth/logout') && !url.includes('/auth/register')) {
          startIdleTimer();
        } else {
          clearIdleTimer();
        }
      }
    };

    // 이벤트 리스너 등록
    window.addEventListener('api-transaction-start', handleTransactionStart as EventListener);
    window.addEventListener('api-transaction-success', handleTransactionSuccess as EventListener);

    /*클린업 함수*/
    return () => {
      window.removeEventListener('api-transaction-start', handleTransactionStart as EventListener);
      window.removeEventListener('api-transaction-success', handleTransactionSuccess as EventListener);
    };
  }, [startIdleTimer, clearIdleTimer]);

  return null;
};
