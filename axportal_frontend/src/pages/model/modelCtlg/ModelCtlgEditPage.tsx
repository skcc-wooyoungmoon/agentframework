import { useEffect, useState } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIFormField, UIPopupBody, UIPopupFooter, UIPopupHeader, UITextArea2, UIUnitGroup } from '@/components/UI/molecules';
import { UIInput } from '@/components/UI/molecules/input/UIInput';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import type { LayerPopupProps } from '@/hooks/common/layer';
import { useGetModelCtlgById, useUpdateModelCtlg } from '@/services/model/ctlg/modelCtlg.services';
import type { ModelCtlgType } from '@/services/model/ctlg/types';
import { useModal } from '@/stores/common/modal';

export function ModelCtlgEditPage({ currentStep, onClose, id, onSuccess }: LayerPopupProps & { id: string; onSuccess: () => void }) {
  const { openAlert, openConfirm } = useModal();

  const { data: modelCtlg } = useGetModelCtlgById(id ?? '');

  useEffect(() => {
    setNewModelCtlg(modelCtlg);
  }, [modelCtlg]);

  // 모델 수정하기
  const { mutate: updateModelCtlg } = useUpdateModelCtlg();

  const [newModelCtlg, setNewModelCtlg] = useState<ModelCtlgType | undefined>(undefined);
  const handleChangeModelCtlg = (value: Partial<ModelCtlgType>) => {
    if (newModelCtlg) {
      setNewModelCtlg({ ...newModelCtlg, ...value });
    }
  };

  const handleClose = () => {
    onClose();
  };

  const handleCancel = () => {
    openConfirm({
      title: '안내',
      message: '화면을 나가시겠어요?\n입력한 정보가 저장되지 않을 수 있습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        onClose();
      },
      onCancel: () => {},
    });
  };

  const changeCheck = () => {
    if (!modelCtlg || !newModelCtlg) {
      return false;
    }

    if (modelCtlg.displayName !== newModelCtlg.displayName) {
      return true;
    }

    if (newModelCtlg.servingType === 'serverless') {
      if (modelCtlg.key !== newModelCtlg.key || JSON.stringify(modelCtlg.tags.map(tag => tag.name)) !== JSON.stringify(newModelCtlg.tags.map(tag => tag.name))) {
        return true;
      }
    } else if (newModelCtlg.servingType === 'self-hosting') {
      if (modelCtlg.description !== newModelCtlg.description) {
        return true;
      }
    }

    return false;
  };

  const handleSubmit = () => {
    if (!modelCtlg || !newModelCtlg) {
      return;
    }

    const isChange = changeCheck();
    if (!isChange) {
      openAlert({
        title: '안내',
        message: '수정된 내용이 없습니다.',
        confirmText: '확인',
      });
      return;
    }

    const { path, ...tempNewModelCtlg } = { ...newModelCtlg, originTags: modelCtlg.tags };

    updateModelCtlg(tempNewModelCtlg as ModelCtlgType, {
      onSuccess: () => {
        openAlert({
          title: '완료',
          message: '수정사항이 저장되었습니다.',
          confirmText: '확인',
          onConfirm: () => {
            onSuccess();
          },
        });
      },
    });
  };

  const handleTagAdd = (newTags: string[]) => {
    handleChangeModelCtlg({ tags: newTags.map(tag => ({ id: '', created_at: '', updated_at: '', name: tag })) });
  };

  return (
    <UILayerPopup
      isOpen={currentStep === 1}
      onClose={handleClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        /* 좌측 Step 영역 콘텐츠 */
        <UIPopupAside>
          <UIPopupHeader title='모델 수정' description='' position='left' />
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} onClick={handleSubmit} disabled={!newModelCtlg || newModelCtlg.displayName.length > 50}>
                  저장
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      {/* 우측 Contents 영역 콘텐츠 */}
      <section className='section-popup-content'>
        <UIPopupBody>
          {/* 표시 이름 입력 필드 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb'>
                표시 이름
              </UITypography>
              <UIInput.Text value={newModelCtlg?.displayName} placeholder='' onChange={e => handleChangeModelCtlg({ displayName: e.target.value })} maxLength={50} />
            </UIFormField>
          </UIArticle>

          {/* API Key 입력 필드 */}
          {newModelCtlg?.servingType === 'serverless' && (
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                  API Key
                </UITypography>
                <UIInput.Text value={newModelCtlg?.key} placeholder='API Key 입력' onChange={e => handleChangeModelCtlg({ key: e.target.value })} disabled={false} />
              </UIFormField>
            </UIArticle>
          )}

          {/* 태그 섹션 */}
          {newModelCtlg?.servingType === 'serverless' && (
            <UIArticle>
              <UIInput.Tags tags={newModelCtlg.tags.map(tag => tag.name)} onChange={tags => handleTagAdd(tags)} label='태그' />
            </UIArticle>
          )}

          {/* 설명 입력 필드 */}
          {newModelCtlg?.servingType === 'self-hosting' && (
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb'>
                  설명
                </UITypography>
                <UITextArea2
                  value={newModelCtlg?.description}
                  onChange={e => {
                    handleChangeModelCtlg({ description: e.target.value });
                  }}
                  maxLength={100}
                  placeholder='설명 입력'
                  required={true}
                  rows={3}
                />
              </UIFormField>
            </UIArticle>
          )}
        </UIPopupBody>
      </section>
    </UILayerPopup>
  );
}
