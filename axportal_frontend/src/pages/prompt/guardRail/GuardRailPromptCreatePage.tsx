import React, { useEffect, useState } from 'react';

import { useNavigate } from 'react-router-dom';

import { useSetAtom } from 'jotai';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import {
  UIArticle,
  UIFormField,
  UIInput,
  UIPopupBody,
  UIPopupFooter,
  UIPopupHeader,
  UITextArea2,
  UIUnitGroup
} from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { useCreateGuardRailPrompt } from '@/services/prompt/guardRail';
import { useModal } from '@/stores/common/modal';
import { useUser } from '@/stores';
import { selectedGuardRailPromptAtom } from '@/stores/prompt';

interface GuardRailCreatePageProps {
  open: boolean;
  onClose: () => void;
  onSubmit?: (data: any) => void;
  projectId?: string;
}

export const GuardRailPromptCreatePage: React.FC<GuardRailCreatePageProps> = ({ open, onClose }): React.ReactElement => {
  const navigate = useNavigate();
  const { user } = useUser();
  const setSelectedGuardRailPrompt = useSetAtom(selectedGuardRailPromptAtom);

  const [isPopupOpen, setIsPopupOpen] = useState(open);
  const { openAlert, openConfirm } = useModal();

  // open prop이 변경될 때 팝업 상태 업데이트
  useEffect(() => {
    setIsPopupOpen(open);
  }, [open]);

  // 폼 상태 - 프롬프트 가드레일 관련으로 변경
  const [guardRailName, setGuardRailName] = useState('');
  const [description, setDescription] = useState('');
  const [tags, setTags] = useState<string[]>([]);
  const [errorTextValue, setErrorTextValue] = useState('');

  // 가드레일 프롬프트 생성 mutation
  const createGuardRailPromptMutation = useCreateGuardRailPrompt({
    onSuccess: ({ data: { promptUuid } }) => {
      openAlert({
        message: '가드레일 프롬프트가 생성되었습니다.',
        title: '알림',
        confirmText: '확인',
        onConfirm: () => {
          handleClose();
          setSelectedGuardRailPrompt({ uuid: promptUuid } as any);
          navigate('/prompt/guardrail/guardrail-prompt-detail');
        },
      });
    },
    onError: () => {
      openAlert({
        message: '가드레일 프롬프트 생성에 실패했습니다.',
        title: '알림',
        confirmText: '확인',
      });
    },
  });

  // 버튼 비활성화 조건: 이름, 프롬프트, 태그는 모두 입력되어야 활성화
  const isButtonDisabled = !guardRailName.trim() || !description.trim() || tags.length === 0 || createGuardRailPromptMutation.isPending;

  const handleClose = () => {
    // 상태 초기화
    setGuardRailName('');
    setDescription('');
    setTags([]);
    setIsPopupOpen(false);
    onClose();
  };

  const handleCancel = () => {
    openConfirm({
      message: '화면을 나가시겠어요?\n입력한 정보가 저장되지 않을 수 있습니다.',
      title: '안내',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        handleClose();
      },
    });
  };

  const handleNext = () => {
    // 유효성 검증
    if (!guardRailName.trim() || !description.trim()) {
      openAlert({
        message: '필수 항목을 모두 입력해주세요.',
        title: '알림',
        confirmText: '확인',
      });
      return;
    }

    // 백엔드 API 형식에 맞게 구성
    const formData = {
      projectId: user.adxpProject.prjUuid,
      name: guardRailName.trim(),
      desc: '', // 설명은 선택사항
      release: true,
      messages: [
        {
          mtype: 1,
          message: description.trim(), // 첫 번째 메시지
        },
      ],
      tags: tags.map(tag => ({ tag: tag })),
      variables: [],
    };

    // API 호출
    createGuardRailPromptMutation.mutate(formData as any);
  };

  return (
    <UILayerPopup
      isOpen={isPopupOpen}
      onClose={handleClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        <UIPopupAside>
          <UIPopupHeader
            title={
              <>
                가드레일 프롬프트
                <br />
                생성
              </>
            }
            description=''
            position='left'
          />
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel} disabled={createGuardRailPromptMutation.isPending}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} onClick={handleNext} disabled={isButtonDisabled}>
                  만들기
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      {/* 우측 Contents 영역 콘텐츠 */}
      <section className='section-popup-content'>
        <UIPopupHeader title='가드레일 프롬프트 생성' description='가드레일에 적용할 시스템 프롬프트를 만들 수 있습니다.' position='right' />
        <UIPopupBody>
          {/* 이름 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                이름
              </UITypography>
              <UIInput.Text
                value={guardRailName}
                placeholder='이름 입력'
                maxLength={50}
                error={errorTextValue}
                onChange={e => {
                  const value = e.target.value;
                  if (value.length <= 50) {
                    setGuardRailName(value);
                    setErrorTextValue('');
                  } else {
                    setErrorTextValue('50자까지만 입력해주세요');
                  }
                }}
              />
            </UIFormField>
          </UIArticle>

          {/* 프롬프트 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                가드레일 프롬프트
              </UITypography>
              <UITextArea2 value={description} placeholder='가드레일 프롬프트 입력' onChange={e => setDescription(e.target.value)} maxLength={4000} />
            </UIFormField>
          </UIArticle>

          {/* 태그 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                태그
              </UITypography>
              <UIInput.Tags tags={tags} onChange={setTags} placeholder='태그 입력' />
            </UIFormField>
          </UIArticle>
        </UIPopupBody>
      </section>
    </UILayerPopup>
  );
};
