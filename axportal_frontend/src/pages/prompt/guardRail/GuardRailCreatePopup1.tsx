import { createElement, useState } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import {
  UIArticle,
  UIFormField,
  UIInput,
  UIPopupBody,
  UIPopupFooter,
  UIPopupHeader,
  UIStepper,
  UITextArea2,
  UIUnitGroup
} from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { GuardRailPromptPickPopup } from '@/pages/prompt/guardRail/GuardRailPromptPickPopup';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { useModal } from '@/stores/common/modal';
import { Button } from '@/components/common/auth';
import { AUTH_KEY } from '@/constants/auth';

interface GuardRailCreatePopupProps {
  isOpen?: boolean;
  onClose?: () => void;
  onNext?: (name: string, description: string, promptId: string, prompt: string) => void;
  // 이전 단계에서 돌아올 때 초기값
  initialName?: string;
  initialDescription?: string;
  initialPrompt?: string;
  initialPromptId?: string;
}

/**
 * 프롬프트 > 가드레일 > (TAB) 가드레일 관리 > 가드레일 생성 팝업1 (기본 정보 입력)
 */
export const GuardRailCreatePopup1 = ({
  isOpen = true,
  onClose,
  onNext,
  initialName = '',
  initialDescription = '',
  initialPrompt = '',
  initialPromptId = '',
}: GuardRailCreatePopupProps) => {
  // 이름
  const [name, setName] = useState(initialName);

  // 설명
  const [description, setDescription] = useState(initialDescription);

  // 가드레일 프롬프트 선택
  const [prompt, setPrompt] = useState(initialPrompt);
  const [promptId, setPromptId] = useState(initialPromptId);

  // 모달
  const { openModal } = useModal();
  const { showCancelConfirm } = useCommonPopup();

  // ================================
  // 이벤트 핸들러
  // ================================

  // 취소
  const handleClose = () => {
    showCancelConfirm({
      onConfirm: () => {
        onClose?.();
      },
    });
  };

  // 다음 (프롬프트 이름도 함께 전달)
  const handleNext = () => {
    onNext?.(name, description, promptId, prompt);
  };

  // 다음 버튼 활성화 여부 (이름, 가드레일 프롬프트 필수)
  const isNextEnabled = name.trim() !== '' && prompt.trim() !== '';

  // 프롬프트 선택
  const handleOpenPromptPickPopup = () => {
    const selectedPromptRef = { current: { id: '', name: '' } };

    openModal({
      type: 'large',
      title: '가드레일 프롬프트 선택',
      body: createElement(GuardRailPromptPickPopup, {
        onConfirm: (selectedPrompt: { id: string; name: string }) => {
          selectedPromptRef.current = selectedPrompt;
        },
      }),
      confirmText: '확인',
      onConfirm: () => {
        if (selectedPromptRef.current.name) {
          setPrompt(selectedPromptRef.current.name);
          setPromptId(selectedPromptRef.current.id);
        }
      },
    });
  };

  return (
    <UILayerPopup
      isOpen={isOpen}
      onClose={handleClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        /* 좌측 Step 영역 콘텐츠 */
        <UIPopupAside>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='가드레일 생성' position='left' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            {/* 스테퍼 영역 */}
            <UIStepper
              items={[
                { id: 'step1', step: 1, label: '기본 정보 입력' },
                { id: 'step2', step: 2, label: '배포 모델 선택' },
              ]}
              currentStep={1}
              direction='vertical'
            />
          </UIPopupBody>

          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: 80 }} onClick={handleClose}>
                  취소
                </UIButton2>
                <Button auth={AUTH_KEY.AGENT.GUARDRAIL_CREATE} className='btn-tertiary-blue' style={{ width: 80 }} disabled={true}>
                  만들기
                </Button>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      {/* 우측 Contents 영역 콘텐츠 - 기존 컴포넌트 사용 */}
      <section className='section-popup-content'>
        {/* 레이어 팝업 헤더 */}
        <UIPopupHeader title='가드레일 생성' description='분류와 가드레일을 입력해 새로운 필터링을 만들어보세요.' position='right' />

        {/* 레이어 팝업 바디 */}
        <UIPopupBody>
          <UIArticle>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-title-4-sb'>
                이름
              </UITypography>
              <UIInput.Text
                value={name}
                onChange={e => {
                  setName(e.target.value);
                }}
                maxLength={100}
                placeholder='이름 입력'
              />
            </UIUnitGroup>
          </UIArticle>

          {/* 설명 필드 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={false}>
                설명
              </UITypography>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <div className='flex-1'>
                  <UITextArea2 value={description} maxLength={100} onChange={e => setDescription(e.target.value)} placeholder='설명 입력' />
                </div>
              </UIUnitGroup>
            </UIFormField>
          </UIArticle>

          {/* 가드레일 프롬프트 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                가드레일 프롬프트
              </UITypography>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <div className='flex-1'>
                  <UIInput.Text value={prompt} placeholder='가드레일 프롬프트 선택' />
                </div>
                <div>
                  <UIButton2 className='btn-secondary-outline' onClick={handleOpenPromptPickPopup}>
                    선택
                  </UIButton2>
                </div>
              </UIUnitGroup>
            </UIFormField>
          </UIArticle>
        </UIPopupBody>

        {/* 레이어 팝업 footer */}
        <UIPopupFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='start'>
              {/* <UIButton2 className='btn-secondary-gray'>이전</UIButton2> */}
              <UIButton2 className='btn-secondary-blue' disabled={!isNextEnabled} onClick={handleNext}>
                다음
              </UIButton2>
            </UIUnitGroup>
          </UIArticle>
        </UIPopupFooter>
      </section>
    </UILayerPopup>
  );
};
