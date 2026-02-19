import React, { useState } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup, UIInput, UIGroup, UIFormField } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { DesignLayout } from '../../components/DesignLayout';

export const DP_040102_P01: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true);

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  // text 타입
  const [textValue, setTextValue] = useState('');

  // 금지어 리스트
  const [bannedWords, setBannedWords] = useState(['', '', '', '', '외국인비하']);

  const handleBannedWordChange = (index: number, value: string) => {
    const newBannedWords = [...bannedWords];
    newBannedWords[index] = value;
    setBannedWords(newBannedWords);
  };

  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'data', label: '데이터' }}
        initialSubMenu={{
          id: 'data-tools',
          label: '데이터도구',
          icon: 'ico-lnb-menu-20-data-storage',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              데이터 도구
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              Ingestion Tool 만들기 진행 중...
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
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='세이프티 필터 수정' position='left' />
            {/* 레이어 팝업 바디 */}
            {/* <UIPopupBody></UIPopupBody> */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: 80 }}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: 80 }} disabled={false}>
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
          <UIPopupHeader title='세이프티 필터 수정' description='현재 등록된 금지어를 수정하거나 새로운 단어를 추가할 수 있습니다.' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-sb'>
                  분류
                </UITypography>

                <UIInput.Text
                  value={textValue}
                  placeholder='선택'
                  onChange={e => {
                    setTextValue(e.target.value);
                  }}
                />
              </UIFormField>
            </UIArticle>

            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-sb'>
                  금지어
                </UITypography>
                <UITypography variant='body-2' className='secondary-neutral-600'>
                  차단할 키워드를 입력해주세요. 띄어쓰기나 동사의 기봄형도 포함할 수 있습니다.
                </UITypography>
                <UIGroup gap={8} direction='column' align='start'>
                  {bannedWords.map((word, index) => (
                    <div key={index}>
                      <UIUnitGroup gap={8} direction='row' align='start'>
                        <div className='flex-1'>
                          <UIInput.Text value={word} onChange={e => handleBannedWordChange(index, e.target.value)} placeholder='입력' />
                        </div>
                        <UIButton2 className='ic-system-48-delete cursor-pointer'>{''}</UIButton2>
                      </UIUnitGroup>
                    </div>
                  ))}
                  <div>
                    <UIButton2 className='btn-secondary-outline-blue'>금지어 추가</UIButton2>
                  </div>
                  {/* 기획 현행화 : 삭제
                  <div>
                    <UIList
                      gap={4}
                      direction='column'
                      className='ui-list_bullet'
                      data={[
                        {
                          dataItem: (
                            <UITypography variant='body-2' className='secondary-neutral-600'>
                              차단할 키워드를 입력해주세요.
                              <br />
                              띄어쓰기나 동사의 기본형도 포함할 수 있습니다.
                            </UITypography>
                          ),
                        },
                      ]}
                    />
                  </div> */}
                </UIGroup>
              </UIFormField>
            </UIArticle>
          </UIPopupBody>
        </section>
      </UILayerPopup>
    </>
  );
};
