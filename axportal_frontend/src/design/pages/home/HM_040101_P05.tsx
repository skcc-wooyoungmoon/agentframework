import React, { useState } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { DesignLayout } from '../../components/DesignLayout';

export const HM_040101_P05: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'model', label: '모델' }}
        initialSubMenu={{
          id: 'model-catalog',
          label: '모델 카탈로그',
          icon: 'ico-lnb-menu-20-model-catalog',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              모델 카탈로그
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              파인튜닝 등록 진행 중...
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
            <UIPopupHeader title='프로젝트 참여' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              {/* 스테퍼 영역 */}
              <UIStepper
                items={[
                  { id: 'step1', step: 1, label: '프로젝트 선택' },
                  { id: 'step2', step: 2, label: '프로젝트 정보 확인' },
                ]}
                currentStep={2}
                direction='vertical'
              />
            </UIPopupBody>
            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }}>
                    참여
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
          <UIPopupHeader title='프로젝트 정보 확인' description='선택하신 프로젝트 정보를 확인 후, 참여하고 싶으신 경우 참여 버튼을 눌러주세요.' position='right' />
          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  기본 정보
                </UITypography>
              </div>
              <div className='article-body'>
                <div className='border-t border-black'>
                  <table className='tbl-v'>
                    {/* [251106_퍼블수정] width값 수정 */}
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
                            프로젝트명
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            비정형 데이터 자산화 플랫폼 구축
                          </UITypography>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            설명
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            비정형 데이터 자산화 플랫폼 구축 프로젝트 입니다.
                          </UITypography>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            참여인원
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            9
                          </UITypography>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </UIArticle>
            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  담당자 정보
                </UITypography>
              </div>
              <div className='article-body'>
                <div className='border-t border-black'>
                  <table className='tbl-v'>
                    {/* [251106_퍼블수정] width값 수정 */}
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
                            생성자
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            김신한ㅣData기획Unit
                          </UITypography>
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            생성 일시
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            2025.03.24 18:23:43
                          </UITypography>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            프로젝트 관리자
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UITypography variant='body-2' className='secondary-neutral-600 py-3.5'>
                            김신한ㅣData기획Unit <br />
                            박상길ㅣData기획Unit <br />
                            홍길동ㅣData기획Unit
                          </UITypography>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }}>
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
