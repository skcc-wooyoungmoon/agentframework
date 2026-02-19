import React, { useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIButton2, UITypography, UITooltip, UIIcon2 } from '@/components/UI/atoms';
import { UIRadio2 } from '@/components/UI/atoms/UIRadio2';
import { UIStepper, UIUnitGroup, UIPopupFooter, UIPopupHeader, UIPopupBody, UIFormField, UIDropdown, UIInput, type UIStepperItem } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { DesignLayout } from '../../components/DesignLayout';

export const HM_060101_P05: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음

  // 그리드 선택 상태
  const [selectedId, _] = useState<string>('');

  // DW 계정 사용 여부 선택 상태
  const [selectedUsage, setSelectedUsage] = useState('use');
  const handleUsageChange = (_value: string) => {
    setSelectedUsage(_value);
  };

  // 계정 유형 선택 상태
  const [dataset, setDataset] = useState('사용자 계정');
  const [isDatasetDropdownOpen, setIsDatasetDropdownOpen] = useState(false);

  // 서비스 계정 입력 상태
  const [serviceAccountValue, setServiceAccountValue] = useState('');

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  const handleCancel = () => {
    handleClose();
  };

  const handleNext = () => {
    // 다음 단계로 이동 로직
  };

  // 스테퍼 데이터
  const stepperItems: UIStepperItem[] = [
    {
      id: 'step1',
      label: '프로젝트 선택',
      step: 1,
    },
    {
      id: 'step2',
      label: '도구 및 이미지 선택',
      step: 2,
    },
    {
      id: 'step3',
      label: 'DW 계정 선택',
      step: 3,
    },
    {
      id: 'step4',
      label: '자원 선택',
      step: 4,
    },
  ];

  const modelData = [
    {
      id: '1',
      no: 1,
      accountId: 'DW_USER01',
    },
    {
      id: '2',
      no: 2,
      accountId: 'DW_USER02',
    },
    {
      id: '3',
      no: 3,
      accountId: 'DW_USER03',
    },
    {
      id: '4',
      no: 4,
      accountId: 'DW_USER04',
    },
    {
      id: '5',
      no: 5,
      accountId: 'DW_USER05',
    },
    {
      id: '6',
      no: 6,
      accountId: 'DW_USER06',
    },
  ];

  // 그리드 컬럼 정의
  const columnDefs: any = React.useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no' as const,
        width: 56,
        minWidth: 56,
        maxWidth: 56,
        cellClass: 'text-center',
        headerClass: 'text-center',
        cellStyle: {
          textAlign: 'center',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        },
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '계정 ID',
        field: 'accountId' as const,
        flex: 1,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
    ],
    [selectedId]
  );

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
            <UIPopupHeader title='IDE 생성' description='' position='left' />
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={3} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} onClick={handleNext}>
                    생성
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 */}
        <section className='section-popup-content'>
          <UIPopupHeader title='DW 계정 선택' description='DW 계정 사용 여부를 선택해주세요. 사용을 원할 경우, 원하는 계정을 선택해주세요.' position='right' />
          <UIPopupBody>
            {/* 모델 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  DW 계정 사용 여부
                </UITypography>
                <UIUnitGroup gap={12} direction='column' align='center'>
                  <UIRadio2
                    name='usage'
                    label='사용'
                    value='use'
                    checked={selectedUsage === 'use'}
                    onChange={(checked, value) => {
                      if (checked) handleUsageChange(value);
                    }}
                  />
                  <UIRadio2
                    name='usage'
                    label='미사용'
                    value='notUse'
                    checked={selectedUsage === 'notUse'}
                    onChange={(checked, value) => {
                      if (checked) handleUsageChange(value);
                    }}
                  />
                </UIUnitGroup>
              </UIFormField>
            </UIArticle>

            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  계정 유형
                </UITypography>
                <UIDropdown
                  value={dataset}
                  options={[
                    { value: '1', label: '사용자 계정' },
                    { value: '2', label: '서비스 계정' },
                  ]}
                  isOpen={isDatasetDropdownOpen}
                  onClick={() => setIsDatasetDropdownOpen(!isDatasetDropdownOpen)}
                  onSelect={(value: string) => {
                    setDataset(value);
                    setIsDatasetDropdownOpen(false);
                  }}
                  placeholder='자원 프리셋 선택'
                />
              </UIFormField>
            </UIArticle>

            {dataset === '2' && (
              <UIArticle>
                <UIFormField gap={8} direction='column'>
                  <div className='flex items-center'>
                    <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                      서비스 계정
                    </UITypography>
                    <UITooltip
                      trigger='click'
                      position='bottom-start'
                      type='notice'
                      title=''
                      items={['사전에 포탈 관리자와 협의하여 업무 목적에 맞는 계정을 사용해주세요.']}
                      bulletType='default'
                      showArrow={false}
                      showCloseButton={true}
                      className='ml-1 relative top-[2px]'
                    >
                      <UIButton2>
                        <UIIcon2 className='ic-system-20-info' />
                      </UIButton2>
                    </UITooltip>
                  </div>
                  <UIInput.Text value={serviceAccountValue} placeholder='직접 입력' onChange={e => setServiceAccountValue(e.target.value)} />
                </UIFormField>
              </UIArticle>
            )}

            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='flex justify-between w-full items-center'>
                    <div style={{ width: '168px', paddingRight: '8px' }}>
                      <UIDataCnt count={modelData.length} prefix='총' unit='건' />
                    </div>
                    <UIButton2 className='btn-option-outlined'>권한 계정 목록</UIButton2>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid type='single-select' rowData={modelData} columnDefs={columnDefs} onClickRow={(_params: any) => {}} />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination currentPage={1} totalPages={3} onPageChange={() => {}} className='flex justify-center' />
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>
          </UIPopupBody>
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                  이전
                </UIButton2>
                <UIButton2 className='btn-secondary-blue' style={{ width: '80px' }} onClick={handleNext}>
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
