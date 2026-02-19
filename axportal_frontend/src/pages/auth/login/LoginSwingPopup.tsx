import { forwardRef, useImperativeHandle, useRef, useState } from 'react';

import { useNavigate } from 'react-router';

import { UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { MODAL_ID } from '@/constants/modal/modalId.constants';
import { usePostSwingLogin } from '@/services/auth/auth.services.ts';
import type { PostSwingRegisterRequest } from '@/services/auth/types.ts';
import { useModal } from '@/stores/common/modal';

/**
 * LG_010101_P05 - swinge 인증
 */
export type LoginSwingPopupHandle = {
  onConfirm: () => void;
};

const LoginSwingPopup = forwardRef<LoginSwingPopupHandle, object>(function LoginSwingPopup(_props, ref) {
  const [textValue, setLoginValue] = useState('');
  const [passwordValue, setPasswordValue] = useState('');
  const [errorMessage, setErrorMessage] = useState<string>('');
  const passwordInputRef = useRef<HTMLInputElement>(null);
  const { mutate: postSwingLogin } = usePostSwingLogin();
  const { closeModal, openConfirm } = useModal();
  const navigate = useNavigate();

  const handleConfirm = (authCode?: string) => {
    let id = textValue;
    let pw = passwordValue;

    if (authCode) {
      id = 'sso';
      pw = 'sso';
    }

    // console.log('인증요청 클릭: ' + id);

    setErrorMessage('');

    const swingLoginData: PostSwingRegisterRequest = {
      username: id,
      password: pw,
      ssoAuthCode: authCode,
      newJoinYn: 'N',
    };

    // API 호출
    postSwingLogin(swingLoginData, {
      onSuccess: response => {
        // console.log('등록 성공:', response);

        if (response?.status === 200) {
          // 200: LoginComp 노출 없이 메인으로 이동
          // window.location.href = '/';
          handleCloseModal();
          navigate('/', { replace: true });
        } else if (response?.status === 201) {
          // 201: LoginComp 노출
          // window.location.href = '/login-complete';
          handleCloseModal();
          navigate('/login-complete', { replace: true });
        } else if (response?.status === 204) {
          openConfirm({
            title: '회원가입 안내',
            message: '가입된 정보가 없습니다. \n회원가입 하시겠어요?',
            confirmText: '예',
            cancelText: '아니요',
            onConfirm: () => {
              // 재귀 호출하여 newJoinYn = 'Y'로 다시 인증 요청
              const retrySwingLoginData: PostSwingRegisterRequest = {
                username: id,
                password: pw,
                newJoinYn: 'Y',
              };
              postSwingLogin(retrySwingLoginData, {
                onSuccess: retryResponse => {
                  if (retryResponse?.status === 200) {
                    handleCloseModal();
                    navigate('/', { replace: true });
                  } else if (retryResponse?.status === 201) {
                    handleCloseModal();
                    navigate('/login-complete', { replace: true });
                  } else {
                    setErrorMessage('인증 처리 중 알 수 없는 응답이 발생했습니다. 다시 시도해주세요.');
                  }
                },
                onError: retryError => {
                  setErrorMessage(retryError?.message);
                },
              });
            },
            onCancel: () => {
              // console.log('취소됨');
            },
          });
        } else {
          // 그 외 상태코드: 에러 처리
          setErrorMessage('인증 처리 중 알 수 없는 응답이 발생했습니다. 다시 시도해주세요.');
          navigate('/login');
        }
      },
      onError: error => {
        // console.error('등록 실패:', error);
        // 타이머 중지 및 에러 메시지 표시
        setErrorMessage(error?.message);
        navigate('/login');
      },
    });
  };

  useImperativeHandle(
    ref,
    () => ({
      onConfirm: handleConfirm,
    }),
    [textValue, passwordValue, postSwingLogin, navigate, closeModal, openConfirm]
  );

  const handleCloseModal = () => {
    closeModal(MODAL_ID.SWING_LOGIN_MODAL);
  };

  const handleTextKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Tab') {
      e.preventDefault();
      passwordInputRef.current?.focus();
    }
  };

  const handlePasswordKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      handleConfirm();
    }
  };

  return (
    <section className='section-modal'>
      <UIArticle>
        <UIUnitGroup gap={16} direction='column'>
          <div>
            <UIInput.Text
              value={textValue}
              placeholder='아이디 입력'
              onChange={e => {
                const id = e.target.value;
                setLoginValue(id);
              }}
              onKeyDown={handleTextKeyDown}
            />
          </div>
          <div>
            <UIInput.Password
              ref={passwordInputRef}
              value={passwordValue}
              placeholder='비밀번호 입력'
              onChange={e => {
                const pw = e.target.value;
                setPasswordValue(pw);
              }}
              onKeyDown={handlePasswordKeyDown}
              error={errorMessage}
            />
          </div>
        </UIUnitGroup>
      </UIArticle>
    </section>
  );
});

export default LoginSwingPopup;
