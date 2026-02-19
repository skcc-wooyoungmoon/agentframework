import React, { useState } from 'react';

import { UIFileBox, UITypography, UIButton2 } from '@/components/UI/atoms';
import { UIDataCnt } from '@/components/UI/';
import { UIStepper, UIPopupHeader, UIPopupBody, UIPopupFooter, UIArticle, UIUnitGroup, UIGroup, UIList } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { DesignLayout } from '../../components/DesignLayout';

export const DT_020101_P09: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음

  /** 파일 업로드  ---------*/
  const [files, setFiles] = useState<object[]>([
    { fileName: 'Summary_train_1st', progress: '40' },
    { fileName: 'Summary_train_1st', progress: '0', status: 'error' },
    { fileName: 'Summary_train_1st', progress: '0', status: 'error' },
  ]);
  // [251114_퍼블수정] 파일 업로드 영역 수정
  const handleFileRemove = (index?: number) => {
    if (index === undefined) return;
    setFiles(prev => prev.filter((_, i) => i !== index));
  };
  /** 파일 업로드  ---------*/

  // 스테퍼 데이터
  const stepperItems = [
    { step: 1, label: '데이터 정보입력' },
    { step: 2, label: '데이터 가져오기' },
    { step: 3, label: '선택 데이터 확인' },
    { step: 4, label: '프로세서' },
  ];

  const handleClose = () => {
    setIsPopupOpen(false);
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
            <UIPopupHeader title='학습 데이터세트 생성' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={3} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: 80 }}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: 80 }}>
                    만들기
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
          <UIPopupHeader title='선택 데이터 확인' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle>
              <div className='filebox-item bg-gray-100 rounded-xl px-6 py-5 flex items-center justify-between'>
                <UIList
                  gap={4}
                  direction='column'
                  className='ui-list_important'
                  data={[
                    {
                      dataItem: (
                        <UITypography variant='body-2' className='secondary-neutral-600 text-sb'>
                          한 번 생성한 데이터세트의 파일 구성은 변경할 수 없습니다. 추가한 데이터를 다시 한 번 확인 해주세요.
                        </UITypography>
                      ),
                    },
                  ]}
                />
              </div>
            </UIArticle>
            <UIArticle>
              <UIUnitGroup gap={16} direction='column'>
                <UIDataCnt count={files.length} prefix='선택된 데이터 총' unit='건' />
                <UIGroup gap={12} direction='column'>
                  {/* 파일 목록 */}
                  {/* [251114_퍼블수정] 파일 업로드 영역 수정 */}
                  {files.length > 0 && <UIFileBox variant='default' size='full' items={files as any} onFileRemove={handleFileRemove} className='w-full' />}
                  <div>
                    <UIList
                      gap={4}
                      direction='column'
                      className='ui-list_bullet'
                      data={[
                        {
                          dataItem: (
                            <UITypography variant='body-2' className='secondary-neutral-600'>
                              단일 파일 최대 업로드 크기 : 100MB, 전체 파일 최대 업로드 크기 : 200MB
                            </UITypography>
                          ),
                        },
                      ]}
                    />
                  </div>
                </UIGroup>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray' style={{ width: 80 }}>
                  이전
                </UIButton2>
                <UIButton2 className='btn-secondary-blue' style={{ width: 80 }}>
                  다음
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
