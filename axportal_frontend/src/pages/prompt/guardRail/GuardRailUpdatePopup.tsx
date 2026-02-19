import { createElement, useState } from 'react';

import { useQueryClient } from '@tanstack/react-query';

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
import { GuardRailPromptPickPopup } from '@/pages/prompt/guardRail/GuardRailPromptPickPopup';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { useUpdateGuardRail } from '@/services/prompt/guardRail/guardRail.services';
import { useModal } from '@/stores/common/modal';

interface GuardRailUpdatePopupProps {
  isOpen: boolean;
  onClose: () => void;
  initialData?: {
    id: string;
    projectId: string;
    name: string;
    description: string;
    promptName: string;
    promptId: string;
    llms: Array<{ servingName: string }>;
    tags?: Array<{ tag: string }>;
  };
}

/**
 * 프롬프트 > 가드레일 > (TAB) 가드레일 관리 > 가드레일 상세 > 수정 팝업
 */
export const GuardRailUpdatePopup: React.FC<GuardRailUpdatePopupProps> = ({ isOpen, onClose, initialData }) => {
  const { openModal, openAlert } = useModal();
  const { showCancelConfirm } = useCommonPopup();
  const queryClient = useQueryClient();

  const [guardrailName, setGuardrailName] = useState(initialData?.name || '');
  const [description, setDescription] = useState(initialData?.description || '');
  const [selectedPromptName, setSelectedPromptName] = useState(initialData?.promptName || '');
  const [selectedPromptId, setSelectedPromptId] = useState(initialData?.promptId || '');

  const projectId = initialData?.projectId || '';
  const llms = initialData?.llms || [];
  const tags = initialData?.tags || [];

  // 가드레일 수정 API
  const { mutate: updateGuardRail } = useUpdateGuardRail({
    onSuccess: response => {
      if (response.data.data.result) {
        openAlert({
          title: '완료',
          message: '수정사항이 저장되었습니다.',
          onConfirm: () => {
            // 가드레일 상세 캐시 무효화 - 수정된 데이터 즉시 반영
            queryClient.invalidateQueries({
              queryKey: ['GET', `/guardrails/${initialData?.id}`],
            });
            onClose();
          },
        });
      } else {
        openAlert({
          title: '실패',
          message: '가드레일 수정에 실패했습니다.',
        });
      }
    },
  });

  // 프롬프트 선택 팝업 열기
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
      cancelText: '취소',
      confirmText: '선택',
      onConfirm: () => {
        if (selectedPromptRef.current.name) {
          setSelectedPromptName(selectedPromptRef.current.name);
          setSelectedPromptId(selectedPromptRef.current.id);
        }
      },
    });
  };

  // 변경사항 감지
  const hasChanges = guardrailName !== initialData?.name || description !== initialData?.description || selectedPromptId !== initialData?.promptId;

  // 저장 버튼 클릭 핸들러
  const handleSave = () => {
    // 변경사항 없는 경우
    if (!hasChanges) {
      openAlert({
        title: '안내',
        message: '수정사항이 없습니다.',
      });
      return;
    }

    // 필수 필드 검증
    if (!guardrailName.trim()) {
      openAlert({
        title: '안내',
        message: '가드레일명을 입력해주세요.',
      });
      return;
    }

    if (!selectedPromptId) {
      openAlert({
        title: '안내',
        message: '프롬프트를 선택해주세요.',
      });
      return;
    }

    // API 호출
    updateGuardRail({
      id: initialData?.id!,
      name: guardrailName,
      description,
      projectId,
      promptId: selectedPromptId,
      llms: llms.map(llm => ({
        servingName: llm.servingName,
      })),
      tags: tags,
    });
  };

  // 취소 버튼 클릭 핸들러
  const handleClose = () => {
    showCancelConfirm({
      onConfirm: () => {
        onClose();
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
          <UIPopupHeader title='가드레일 수정' position='left' />
          {/* 레이어 팝업 바디 */}
          {/* <UIPopupBody>
            </UIPopupBody> */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: 80 }} onClick={handleClose}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue' style={{ width: 80 }} onClick={handleSave}>
                  저장
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      {/* 우측 Contents 영역 콘텐츠 - 기존 컴포넌트 사용 */}
      <section className='section-popup-content'>
        {/* 레이어 팝업 헤더 */}
        <UIPopupHeader title='가드레일 수정' description='현재 등록된 가드레일을 수정할 수 있습니다.' position='right' />

        {/* 레이어 팝업 바디 */}
        <UIPopupBody>
          <UIArticle>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-title-4-sb'>
                이름
              </UITypography>
              <UIInput.Text
                value={guardrailName}
                onChange={e => {
                  setGuardrailName(e.target.value);
                }}
                placeholder='이름 입력'
              />
            </UIUnitGroup>
          </UIArticle>

          {/* 직접 입력 필드 */}
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
          {/* 태그 입력 필드 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                가드레일 프롬프트
              </UITypography>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <div className='flex-1'>
                  <UIInput.Text value={selectedPromptName} readOnly placeholder='가드레일 프롬프트 선택' />
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
      </section>
    </UILayerPopup>
  );
};
