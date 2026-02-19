import React, { useState, useEffect, useRef } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIUnitGroup, UIPopupFooter, UIPopupHeader, UIPopupBody, UIFormField, UIInput, UITextArea2, UIArticle } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { useModal } from '@/stores/common/modal';
import { useUpdateGuardRailPrompt } from '@/services/prompt/guardRail/guardRail.services';
import type { GetGuardRailPromptByIdResponse } from '@/services/prompt/guardRail/types';

interface GuardRailPromptUpdatePageProps {
  isOpen: boolean;
  onClose: () => void;
  guardRailData: GetGuardRailPromptByIdResponse | null;
  onUpdateSuccess?: (updatedData: { name: string; message: string; tags: string[] }) => void;
}

export const GuardRailPromptUpdatePage: React.FC<GuardRailPromptUpdatePageProps> = ({ isOpen, onClose, guardRailData, onUpdateSuccess }) => {
  const { openAlert, openConfirm } = useModal();

  // 폼 상태
  const [name, setName] = useState('');
  const [nameError, setNameError] = useState('');
  const [prompt, setPrompt] = useState('');
  const [tags, setTags] = useState<string[]>([]);
  
  // 초기값 저장 (변경사항 비교용)
  const initialValuesRef = useRef({
    name: '',
    prompt: '',
    tags: [] as string[],
  });

  // guardRailData가 변경될 때 폼 상태 업데이트
  useEffect(() => {
    if (guardRailData) {
      const initialName = guardRailData.name || '';
      const initialPrompt = guardRailData.message || '';
      const tagStrings = (guardRailData.tags || []).map(tag => (typeof tag === 'string' ? tag : tag.tag));
      
      setName(initialName);
      setNameError('');
      setPrompt(initialPrompt);
      setTags(tagStrings);
      
      // 초기값 저장
      initialValuesRef.current = {
        name: initialName,
        prompt: initialPrompt,
        tags: [...tagStrings],
      };
    }
  }, [guardRailData]);

  // 수정 mutation
  const { mutate: updateGuardRailPrompt, isPending: isUpdating } = useUpdateGuardRailPrompt({
    onSuccess: () => {
      // 수정 성공 시 수정된 데이터를 전달
      const updatedData = {
        name: name.trim(),
        message: prompt.trim(),
        tags: tags,
      };

      openAlert({
        title: '안내',
        message: '수정사항이 저장되었습니다.',
        onConfirm: () => {
          onClose();
          if (onUpdateSuccess) {
            onUpdateSuccess(updatedData);
          }
        },
      });
    },
    onError: () => {
      openAlert({
        title: '실패',
        message: '가드레일 프롬프트 수정에 실패하였습니다.',
      });
    },
  });

  const handleClose = () => {
    openConfirm({
      title: '안내',
      message: '화면을 나가시겠어요?\n입력한 정보가 저장되지 않을 수 있습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        // guardRailData의 원본 값으로 리셋
        if (guardRailData) {
          setName(guardRailData.name || '');
          setNameError('');
          setPrompt(guardRailData.message || '');
          const tagStrings = (guardRailData.tags || []).map(tag => (typeof tag === 'string' ? tag : tag.tag));
          setTags(tagStrings);
        }
        onClose();
      },
    });
  };

  const handleCancel = () => {
    handleClose();
  };

  const handleSave = () => {
    if (!guardRailData?.uuid) return;

    if (!name.trim()) {
      openAlert({
        title: '안내',
        message: '이름을 입력해주세요.',
      });
      return;
    }

    if (!prompt.trim()) {
      openAlert({
        title: '안내',
        message: '프롬프트를 입력해주세요.',
      });                  
      return;
    }

    // 변경사항 확인
    const tagsChanged = 
      tags.length !== initialValuesRef.current.tags.length ||
      tags.some((tag, index) => tag !== initialValuesRef.current.tags[index]);
    
    const hasChanges = 
      name !== initialValuesRef.current.name ||
      prompt !== initialValuesRef.current.prompt ||
      tagsChanged;
    
    if (!hasChanges) {
      openAlert({
        message: '수정된 내용이 없습니다.',
        title: '안내',
        confirmText: '확인',
      });
      return;
    }

    // 백엔드 API 형식에 맞게 구성
    const updateData = {
      id: guardRailData.uuid,
      new_name: name.trim(),
      desc: '', // 설명은 선택사항
      release: false,
      messages: [
        {
          message: prompt.trim(), // 첫 번째 메시지
          mtype: 1,
        }
      ],
      tags: tags.map(tag => ({
        tag: tag,
        version_id: null,
      })),
      variables: [
        {
          variable: '{{user_input}}',
          validation: '',
          validation_flag: false,
          token_limit_flag: false,
          token_limit: 0,
        },
      ],
    };

    updateGuardRailPrompt(updateData as any);
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
          <UIPopupHeader
            title={
              <>
                가드레일 프롬프트
                <br />
                수정
              </>
            }
            description=''
            position='left'
          />
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel} disabled={isUpdating}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} onClick={handleSave} disabled={!name.trim() || !prompt.trim() || isUpdating}>
                  {isUpdating ? '수정중' : '저장'}
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      {/* 우측 Contents 영역 콘텐츠 */}
      <section className='section-popup-content'>
        <UIPopupHeader title='가드레일 프롬프트 수정' description='등록된 가드레일 프롬프트를 수정할 수 있습니다.' position='right' />
        <UIPopupBody>
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                이름
              </UITypography>
              <UIInput.Text
                value={name}
                placeholder='이름 입력'
                maxLength={50}
            error={nameError}
                onChange={e => {
                  const value = e.target.value;
                  if (value.length <= 50) {
                    setName(value);
                setNameError('');
              } else {
                setNameError('50자까지만 입력해주세요');
                  }
                }}
              />
            </UIFormField>
          </UIArticle>

          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                가드레일 프롬프트
              </UITypography>
              <UITextArea2 value={prompt} placeholder='가드레일 프롬프트 입력' onChange={e => setPrompt(e.target.value)} maxLength={4000} />
            </UIFormField>
          </UIArticle>

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
