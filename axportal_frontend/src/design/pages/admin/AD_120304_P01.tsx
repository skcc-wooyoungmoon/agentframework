import React, { useState } from 'react';

import { UIButton2, UITypography, UIIcon2 } from '@/components/UI/atoms';
import { UIDropdown, UIGroup } from '@/components/UI/molecules';
import { UIArticle, UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup, UIFormField } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { DesignLayout } from '../../components/DesignLayout';

export const AD_120304_P01: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음

  // 데이터 세트 유형 상태
  const [dataset, setdataset] = useState('1');
  const [isDatasetDropdownOpen, setIsDatasetDropdownOpen] = useState(false);

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'admin', label: '관리' }}
        initialSubMenu={{
          id: 'admin-roles',
          label: '역할 관리',
          icon: 'ico-lnb-menu-20-admin-role',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              기본 정보
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              기본 정보 수정 진행 중...
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
            <UIPopupHeader title='구성원 역할 변경' description='' position='left' />
            {/* <UIPopupBody></UIPopupBody> */}
            {/* 레이어 팝업 바디 : [참고] 이 페이지에는 왼쪽 body 영역 없음. */}
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
        {/* 우측 Contents 영역 콘텐츠 - 구성원 역할 변경 */}
        <section className='section-popup-content'>
          <UIPopupHeader title='구성원 역할 변경' description='역할을 변경 후, 저장 버튼을 눌러주세요.' position='right' />
          <UIPopupBody>
            {/* [251106_퍼블수정] 마크업 수정 */}
            <UIArticle>
              {/* 데이터 세트 유형 섹션 */}
              <UIFormField gap={8} direction='column'>
                <UIGroup gap={0} direction='row'>
                  {/* [251106_퍼블수정] 마크업 수정 */}
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                    장정현
                  </UITypography>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true} divider={true}>
                    Data기획Unit
                  </UITypography>
                </UIGroup>

                <UIDropdown
                  value={dataset}
                  options={[
                    { value: '1', label: '테스터' },
                    { value: '2', label: '관리자' },
                    { value: '3', label: '편집자' },
                    { value: '4', label: '엔지니어' },
                    { value: '5', label: '디자이너' },
                    { value: '6', label: '퍼블리셔' },
                  ]}
                  isOpen={isDatasetDropdownOpen}
                  onClick={() => setIsDatasetDropdownOpen(!isDatasetDropdownOpen)}
                  onSelect={(value: string) => {
                    setdataset(value);
                    setIsDatasetDropdownOpen(false);
                  }}
                  placeholder='데이터 세트 유형 선택'
                />
              </UIFormField>
            </UIArticle>
            <UIArticle>
              {/* 데이터 세트 유형 섹션 */}
              <UIFormField gap={8} direction='column'>
                <UIGroup gap={0} direction='row'>
                  {/* [251106_퍼블수정] 마크업 수정 */}
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                    장정현
                  </UITypography>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb' divider={true} required={true}>
                    Data기획Unit
                  </UITypography>
                </UIGroup>

                <UIDropdown
                  value={dataset}
                  options={[
                    { value: '1', label: '테스터' },
                    { value: '2', label: '관리자' },
                    { value: '3', label: '편집자' },
                    { value: '4', label: '엔지니어' },
                    { value: '5', label: '디자이너' },
                    { value: '6', label: '퍼블리셔' },
                  ]}
                  isOpen={isDatasetDropdownOpen}
                  onClick={() => setIsDatasetDropdownOpen(!isDatasetDropdownOpen)}
                  onSelect={(value: string) => {
                    setdataset(value);
                    setIsDatasetDropdownOpen(false);
                  }}
                  placeholder='데이터 세트 유형 선택'
                />
              </UIFormField>
            </UIArticle>
            <UIArticle>
              {/* 데이터 세트 유형 섹션 */}
              <UIFormField gap={8} direction='column'>
                {/* [251106_퍼블수정] 마크업 수정 */}
                <UIGroup gap={0} direction='row'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                    장정현
                  </UITypography>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb' divider={true} required={true}>
                    Data기획Unit
                  </UITypography>
                </UIGroup>

                <UIDropdown
                  value={dataset}
                  options={[
                    { value: '1', label: '테스터' },
                    { value: '2', label: '관리자' },
                    { value: '3', label: '편집자' },
                    { value: '4', label: '엔지니어' },
                    { value: '5', label: '디자이너' },
                    { value: '6', label: '퍼블리셔' },
                  ]}
                  isOpen={isDatasetDropdownOpen}
                  onClick={() => setIsDatasetDropdownOpen(!isDatasetDropdownOpen)}
                  onSelect={(value: string) => {
                    setdataset(value);
                    setIsDatasetDropdownOpen(false);
                  }}
                  placeholder='데이터 세트 유형 선택'
                />
              </UIFormField>
            </UIArticle>
            <UIArticle>
              {/* 데이터 세트 유형 섹션 */}
              <UIFormField gap={8} direction='column'>
                {/* [251106_퍼블수정] 마크업 수정 */}
                <UIGroup gap={0} direction='row'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                    장정현
                  </UITypography>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb' divider={true} required={true}>
                    Data기획Unit
                  </UITypography>
                </UIGroup>

                <UIDropdown
                  value={dataset}
                  options={[
                    { value: '1', label: '테스터' },
                    { value: '2', label: '관리자' },
                    { value: '3', label: '편집자' },
                    { value: '4', label: '엔지니어' },
                    { value: '5', label: '디자이너' },
                    { value: '6', label: '퍼블리셔' },
                  ]}
                  isOpen={isDatasetDropdownOpen}
                  onClick={() => setIsDatasetDropdownOpen(!isDatasetDropdownOpen)}
                  onSelect={(value: string) => {
                    setdataset(value);
                    setIsDatasetDropdownOpen(false);
                  }}
                  placeholder='데이터 세트 유형 선택'
                />
              </UIFormField>
            </UIArticle>
            <UIArticle>
              {/* 데이터 세트 유형 섹션 */}
              <UIFormField gap={8} direction='column'>
                {/* [251106_퍼블수정] 마크업 수정 */}
                <UIGroup gap={0} direction='row'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                    장정현
                  </UITypography>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb' divider={true} required={true}>
                    Data기획Unit
                  </UITypography>
                </UIGroup>

                <UIDropdown
                  value={dataset}
                  options={[
                    { value: '1', label: '테스터' },
                    { value: '2', label: '관리자' },
                    { value: '3', label: '편집자' },
                    { value: '4', label: '엔지니어' },
                    { value: '5', label: '디자이너' },
                    { value: '6', label: '퍼블리셔' },
                  ]}
                  isOpen={isDatasetDropdownOpen}
                  onClick={() => setIsDatasetDropdownOpen(!isDatasetDropdownOpen)}
                  onSelect={(value: string) => {
                    setdataset(value);
                    setIsDatasetDropdownOpen(false);
                  }}
                  placeholder='데이터 세트 유형 선택'
                />
              </UIFormField>
            </UIArticle>

            <UIArticle>
              <div className='box-fill'>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0 6px' }}>
                  <UIIcon2 className='ic-system-16-info-gray' />
                  <UITypography variant='body-2' className='secondary-neutral-600'>
                    Public 프로젝트에서 포탈관리자 역할인 경우, 프로젝트 관리자로 자동 참여되어 역할을 변경할 수 없습니다.
                  </UITypography>
                </div>
              </div>
            </UIArticle>
          </UIPopupBody>
          {/* <UIPopupFooter></UIPopupFooter> */}
        </section>
      </UILayerPopup>
    </>
  );
};
