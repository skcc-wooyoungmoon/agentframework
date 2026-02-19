import React, { useState } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIFormField, UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, type UIStepperItem, UIUnitGroup } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import type { ModelGardenInfo } from '@/services/model/garden/types.ts';

interface ModelCtlgStep3ExtrainProps {
  isOpen: boolean;
  onPreviousStep: () => void;
  onClose: () => void;
  stepperItems: UIStepperItem[];
  selectedModelGarden: ModelGardenInfo | undefined;
  onSave: (displayName: string, apiKey: string, tags: string[]) => void;
}

export const ModelCtlgStep3Extrain: React.FC<ModelCtlgStep3ExtrainProps> = ({
  isOpen,
  onPreviousStep,
  onClose,
  stepperItems,
  selectedModelGarden,
  onSave,
}: ModelCtlgStep3ExtrainProps) => {
  const [displayName, setDisplayName] = useState('');
  const [apiKey, setApiKey] = useState('');

  const [tags, setTags] = useState<string[]>([]);

  const handleClose = () => {
    onClose();
  };

  const validateCheck = () => {
    return !(selectedModelGarden && apiKey);
  };

  const handleOnSave = () => {
    onSave(displayName, apiKey, tags);
  };
  return (
    <>
      {/* DesignLayout 위에 높은 z-index로 뜨는 UILayerPopup */}
      <UILayerPopup
        isOpen={isOpen}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='모델 등록' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              {/* 스테퍼 영역 */}
              <UIStepper items={stepperItems} currentStep={3} direction='vertical' />
            </UIPopupBody>
            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleClose}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={validateCheck()} onClick={handleOnSave}>
                    등록
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 */}
        {/* 콘텐츠 영역 */}
        <section className='section-popup-content'>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader
            title='추가 정보 입력'
            description='Serverless 모델의 API Key 정보와 프로젝트 내에서 모델의 용도 및 식별을 위한 추가 정보를 입력해주세요.'
            position='right'
          />
          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            {/* 모델 이름 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb'>
                  표시 이름
                </UITypography>
                <UIInput.Text
                  value={displayName}
                  onChange={e => {
                    setDisplayName(e.target.value);
                  }}
                  placeholder='표시 이름 입력'
                  maxLength={50}
                />
              </UIFormField>
            </UIArticle>

            {/* 라이센스 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb' required={true}>
                  API Key
                </UITypography>
                <UIInput.Text
                  value={apiKey}
                  onChange={e => {
                    setApiKey(e.target.value);
                  }}
                  placeholder='API Key 입력'
                />
              </UIFormField>
            </UIArticle>

            {/* 태그 입력 필드 */}
            <UIArticle>
              <UIInput.Tags tags={tags} onChange={setTags} label='태그' />
              {/* <UIFormField gap={8} direction='column'>
                    <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                      태그
                    </UITypography>
                  <div>
                    <UIUnitGroup gap={8} direction='row' align='start'>
                      <div className='form-group' style={{ flex: 1 }}>
                        <UITagsField
                          tags={tags}
                          newTag={newTag}
                          onNewTagChange={value => setNewTag(value)}
                          onTagAdd={handleAddTag}
                          onTagRemove={index => {
                            setTags(tags.filter((_, i) => i !== index));
                          }}
                          placeholder='태그 입력'
                          tagMaxLength={8} // 태그 하나당 최대 글자 수
                          showClearButton={true}
                        />
                      </div>
                      <div>
                        <UIButton2 className='btn-secondary-outline !min-w-[64px] !font-semibold' onClick={handleAddTag}>
                          추가
                        </UIButton2>
                      </div>
                    </UIUnitGroup>
                  </div>
                </UIFormField> */}
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }} onClick={onPreviousStep}>
                  이전
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
