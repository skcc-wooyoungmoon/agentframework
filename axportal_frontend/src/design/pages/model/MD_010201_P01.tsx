import { useState } from 'react';

import { UITypography, UIButton2 } from '@/components/UI/atoms';
import { UIArticle, UIFormField, UIUnitGroup, UIPopupHeader, UIPopupBody, UIPopupFooter } from '@/components/UI/molecules';
import { UIInput } from '@/components/UI/molecules/input/UIInput';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { DesignLayout } from '../../components/DesignLayout';

export const MD_010201_P01: React.FC = () => {
  const [isPopupOpen] = useState(true); // 팝업이므로 항상 열려있음
  const [modelName, setModelName] = useState('문서요약기_계약서형');
  const [selectedApiKey, setSelectedApiKey] = useState('test-gapk-Bi2UcfTSyZRvARYdbsWaEch7-458sEEWde');
  const [tags, setTags] = useState<string[]>([]);

  const handleClose = () => {
    // 팝업 닫기 동작 제거 (디자인 페이지이므로 항상 열려있음)
    // 
  };

  const handleCancel = () => {
    // 
    // 취소 동작 처리
  };

  const handleCreate = () => {
    // 
    // 만들기 동작 처리
  };

  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'model', label: '모델' }}
        initialSubMenu={{
          id: 'model-catalog',
          label: '모델카탈로그 조회',
          icon: 'ico-lnb-menu-20-model-catalog',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              모델카탈로그 조회
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              모델 수정 진행 중...
            </UITypography>
          </div>
        </div>
      </DesignLayout>

      {/* DesignLayout 위에 높은 z-index로 뜨는 UILayerPopup */}
      <UILayerPopup
        isOpen={isPopupOpen}
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
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} onClick={handleCreate}>
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
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                  표시 이름
                </UITypography>
                <UIInput.Text value={modelName} placeholder='' onChange={e => setModelName(e.target.value)} />
              </UIFormField>
            </UIArticle>

            {/* API Key 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  API Key
                </UITypography>
                <UIInput.Text value={selectedApiKey} placeholder='API Key 입력' onChange={e => setSelectedApiKey(e.target.value)} disabled={false} />
              </UIFormField>
            </UIArticle>

            {/* 태그 섹션 */}
            <UIArticle>
              <UIInput.Tags tags={tags} onChange={setTags} placeholder='태그 입력' label='태그' />
            </UIArticle>

            {/* URL 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  URL
                </UITypography>
                <UIInput.Text value={'https://api.platform.a49.com/v1.1'} placeholder='URL 입력' onChange={e => setSelectedApiKey(e.target.value)} disabled={false} />
              </UIFormField>
            </UIArticle>

            {/* Identifier 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  Identifier
                </UITypography>
                <UIInput.Text value={'azure/openai/gpt-4o-mini-2024-07-18'} placeholder='Identifier 입력' onChange={e => setSelectedApiKey(e.target.value)} disabled={false} />
              </UIFormField>
            </UIArticle>
          </UIPopupBody>
          {/* <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }}>
                  이전
                </UIButton2>
                <UIButton2 className='btn-secondary-blue' style={{ width: '80px' }}>
                  다음
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter> */}
        </section>
      </UILayerPopup>
    </>
  );
};
