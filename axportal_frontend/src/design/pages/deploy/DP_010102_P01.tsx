import React, { useState } from 'react';

import { UITypography, UIButton2, UIIcon2 } from '@/components/UI/atoms';
import { UIInput, UITextArea2 } from '@/components/UI/molecules/input';

import { UIArticle, UIFormField, UIGroup, UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { DesignLayout } from '../../components/DesignLayout';

export const DP_010102_P01: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음

  // text 타입
  const [textValue, setTextValue] = useState('콜센터 응대 특화 모델');

  // textarea 타입
  const [textareaValue, setTextareaValue] = useState('고객 상담 응대 문장 생성에 최적화된 대형 언어모델');

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
            <UIPopupHeader title='모델 배포 수정' description='' position='left' />
            {/* <UIPopupBody></UIPopupBody> */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={false}>
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
          {/* <UIPopupHeader title='' description='' position='right' /> */}
          <UIPopupBody>
            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                  모델 정보
                </UITypography>
              </div>
              <div className='article-body'>
                <div className='border-t border-black'>
                  <table className='tbl-v'>
                    {/* [251105_퍼블수정] width값 수정 */}
                    <colgroup>
                      <col style={{ width: '10%' }} />
                      <col style={{ width: '40%' }} />
                      <col style={{ width: '10%' }} />
                      <col style={{ width: '40%' }} />
                    </colgroup>
                    <tbody>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            모델명
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            GPT-4 Callcenter-Tuned
                          </UITypography>
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            설명
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            콜센터 모델
                          </UITypography>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            표시이름
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            GPT-4-Callcenter-Tuned
                          </UITypography>
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            모델유형
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            language
                          </UITypography>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            배포유형
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            self_hosting
                          </UITypography>
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            공급사
                          </UITypography>
                        </th>
                        <td>
                          <UIUnitGroup gap={4} direction='row' vAlign='center'>
                            <UIIcon2 className='ic-system-24-google' />
                            <UITypography variant='body-2' className='secondary-neutral-600'>
                              Google
                            </UITypography>
                          </UIUnitGroup>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </UIArticle>
            <UIArticle>
              <UITypography variant='title-3' className='secondary-neutral-900'>
                배포 정보
              </UITypography>
            </UIArticle>
            {/* 배포명 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  배포명
                </UITypography>
                <UIInput.Text
                  value={textValue}
                  onChange={e => {
                    setTextValue(e.target.value);
                  }}
                  placeholder='배포명 입력'
                  readOnly={true}
                />
              </UIFormField>
            </UIArticle>
            {/* 설명 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                  설명
                </UITypography>
                <UITextArea2 value={textareaValue} placeholder='설명 입력' onChange={e => setTextareaValue(e.target.value)} maxLength={100} />
              </UIFormField>
            </UIArticle>
            <UIArticle>
              <UITypography variant='title-3' className='secondary-neutral-900'>
                세이프티 필터
              </UITypography>
            </UIArticle>
            {/* 입력 필터 섹션 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  입력 필터
                </UITypography>
                <UIGroup
                  gap={8}
                  direction='row'
                  align='start'
                  style={{ height: '48px', background: '#F3F6FB', border: '1px solid #DCE2ED', borderRadius: '8px', padding: '12px 16px' }}
                >
                  {/* <div style={{ border: '1px solid #DCE2ED', borderRadius: '4px', padding: '0 6px', color: '#576072' }}>
                    <UITypography variant='body-1' className='secondary-neutral-600'>
                      욕설/비속어
                    </UITypography>
                  </div>
                  <div style={{ border: '1px solid #DCE2ED', borderRadius: '4px', padding: '0 6px', color: '#576072' }}>
                    <UITypography variant='body-1' className='secondary-neutral-600'>
                      정치적 비속어
                    </UITypography>
                  </div> */}
                </UIGroup>
              </UIFormField>
            </UIArticle>
            {/* 출력 필터 섹션 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  출력 필터
                </UITypography>
                <UIGroup
                  gap={8}
                  direction='row'
                  align='start'
                  style={{ height: '48px', background: '#F3F6FB', border: '1px solid #DCE2ED', borderRadius: '8px', padding: '12px 16px' }} // [251106_퍼블수정] : 스타일 재정의 height 값 추가
                >
                  {/* <div style={{ border: '1px solid #DCE2ED', borderRadius: '4px', padding: '0 6px', color: '#576072' }}>
                    <UITypography variant='body-1' className='secondary-neutral-600'>
                      욕설/비속어
                    </UITypography>
                  </div>
                  <div style={{ border: '1px solid #DCE2ED', borderRadius: '4px', padding: '0 6px', color: '#576072' }}>
                    <UITypography variant='body-1' className='secondary-neutral-600'>
                      정치적 비속어
                    </UITypography>
                  </div> */}
                </UIGroup>
                {/* <UIInput.Text
                  value={''}
                  placeholder=''
                  onChange={e => {
                    setTextValue(e.target.value);
                  }}
                  disabled={true}
                /> */}
              </UIFormField>
            </UIArticle>
          </UIPopupBody>
          <UIPopupFooter></UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
