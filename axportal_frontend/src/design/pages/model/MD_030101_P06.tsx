import React, { useState, useMemo } from 'react';

import { UIButton2, UITypography, UIDataCnt, UITextLabel, UILabel } from '@/components/UI/atoms';
import { UIStepper, UIUnitGroup, UIPopupFooter, UIPopupHeader, UIPopupBody, UIFormField, UIInput, type UIStepperItem } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';

import { DesignLayout } from '../../components/DesignLayout';

export const MD_030101_P06: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음

  // 폼 상태
  const [batchSize, setBatchSize] = useState('1');

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
      label: '모델 선택',
      step: 1,
    },
    {
      id: 'step2',
      label: '기본 정보 입력',
      step: 2,
    },
    {
      id: 'step3',
      label: '자원 할당',
      step: 3,
    },
    {
      id: 'step4',
      label: '학습 데이터세트 선택',
      step: 4,
    },
    {
      id: 'step5',
      label: '파라미터 설정',
      step: 5,
    },
    {
      id: 'step6',
      label: '입력정보 확인',
      step: 6,
    },
  ];

  // 샘플 데이터 (rowData로 변수명 변경)
  const rowData = [
    {
      id: '1',
      name: '부적절한 언어 필터',
      accountStatus: '이용 가능',
      prompt: '욕설, 비속어 등 부적절한 언어를 감지하고 차단합니다',
      tags: ['가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자'],
      publicStatus: '전체공유',
      version: '비지도학습',
      createdDate: '2025.03.25 10:15:20',
      modifiedDate: '2024-01-20 14:25:00',
    },
  ];

  // 그리드 컬럼 정의
  const columnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id' as any,
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
        } as any,
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      // 251113_퍼블수정 그리드 컬럼 속성 '이름' 영역 수정 S
      {
        headerName: '이름',
        field: 'name',
        width: 272,
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
      // 251113_퍼블수정 그리드 컬럼 속성 '이름' 영역 수정 E
      {
        headerName: '상태',
        field: 'accountStatus' as any,
        width: 120,
        cellRenderer: React.memo((params: any) => {
          const statusColors = {
            이용가능: 'complete',
            실패: 'error',
            진행중: 'progress',
            취소: 'stop',
          } as const;
          return (
            <UILabel variant='badge' intent={statusColors[params.value as keyof typeof statusColors]}>
              {params.value}
            </UILabel>
          );
        }),
      },
      {
        headerName: '설명',
        field: 'prompt',
        minWidth: 392,
        flex: 1,
        showTooltip: true,
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
      {
        headerName: '공개범위',
        field: 'publicStatus',
        width: 120,
      },
      // 251107_퍼블수정 그리드 컬럼 속성 '태그' 영역 수정 S
      {
        headerName: '태그',
        field: 'tags' as const,
        width: 230,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: (params: any) => {
          if (!params.value || !Array.isArray(params.value) || params.value.length === 0) {
            return null;
          }
          const tagText = params.value.join(', ');
          return (
            <div title={tagText}>
              <div className='flex gap-1'>
                {params.value.slice(0, 2).map((tag: string, index: number) => (
                  <UITextLabel key={index} intent='tag' className='nowrap'>
                    {tag}
                  </UITextLabel>
                ))}
              </div>
            </div>
          );
        },
      },
      // 251107_퍼블수정 그리드 컬럼 속성 '태그' 영역 수정 E
      {
        headerName: '유형',
        field: 'version',
        width: 120,
      },

      {
        headerName: '생성일시',
        field: 'createdDate',
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    [rowData]
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
            <UIPopupHeader title='파인튜닝 만들기' description='' position='left' />
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={6} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} onClick={handleNext}>
                    튜닝시작
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 */}
        <section className='section-popup-content'>
          <UIPopupHeader title='입력정보 확인' description='' position='right' />
          <UIPopupBody>
            {/* 모델 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  모델
                </UITypography>
                <UIInput.Text value={'stable-diffusion-xl-base-1.0'} placeholder='1' onChange={_e => {}} readOnly={true} />
              </UIFormField>
            </UIArticle>

            {/* 파인튜닝 이름 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  파인튜닝 이름
                </UITypography>
                <UIInput.Text value={'FT_Test_ver.1_250612'} placeholder='0.0001' onChange={_e => {}} readOnly={true} />
              </UIFormField>
            </UIArticle>

            {/* 학습 유형 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  데이터세트 유형
                </UITypography>
                <UIInput.Text value={'지도 학습'} placeholder='1' onChange={e => setBatchSize(e.target.value)} readOnly={true} />
              </UIFormField>
            </UIArticle>

            {/* 효율성 구성(RFT) */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  Efficiency Configuration (PEFT)
                </UITypography>
                <UIInput.Text value={'Full Fine-tuning'} placeholder='1' onChange={e => setBatchSize(e.target.value)} readOnly={true} />
              </UIFormField>
            </UIArticle>
            {/* 미세 조정 기기술 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  Fine Tuning Techniques
                </UITypography>
                <UIInput.Text value={'BASIC'} placeholder='1' onChange={e => setBatchSize(e.target.value)} readOnly={true} />
              </UIFormField>
            </UIArticle>
            {/* CPU */}
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIFormField gap={8} direction='column'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                    CPU
                  </UITypography>
                  <UIInput.Text value={batchSize} placeholder='1' onChange={e => setBatchSize(e.target.value)} readOnly={true} />
                </UIFormField>
                <UIFormField gap={8} direction='column'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                    Memory
                  </UITypography>
                  <UIInput.Text value={batchSize} placeholder='1' onChange={e => setBatchSize(e.target.value)} readOnly={true} />
                </UIFormField>
                <UIFormField gap={8} direction='column'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                    GPU
                  </UITypography>
                  <UIInput.Text value={batchSize} placeholder='1' onChange={e => setBatchSize(e.target.value)} readOnly={true} />
                </UIFormField>
              </UIUnitGroup>
            </UIArticle>

            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <UIUnitGroup gap={16} direction='column'>
                    <div className='flex justify-between w-full items-center'>
                      <div className='flex-shrink-0'>
                        <div style={{ width: '168px', paddingRight: '8px' }}>
                          <UIDataCnt count={rowData.length} prefix='학습 데이터세트 총' unit='건' />
                        </div>
                      </div>
                    </div>
                  </UIUnitGroup>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid
                    type='default'
                    rowData={rowData}
                    columnDefs={columnDefs}
                    onClickRow={(_params: any) => {}}
                    onCheck={(_selectedIds: any[]) => {
                    }}
                  />
                </UIListContentBox.Body>
                {/* [참고] classname 관련
                  - 그리드 하단 (삭제) 버튼이 있는 경우 classname 지정 (예시) <UIListContentBox.Footer classname="ui-data-has-btn">
                  - 그리드 하단 (버튼) 없는 경우 classname 없이 (예시) <UIListContentBox.Footer>
                */}
              </UIListContainer>
            </UIArticle>

            {/* 학습 횟수 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  Epochs
                </UITypography>
                <UIInput.Text value={batchSize} placeholder='1' onChange={e => setBatchSize(e.target.value)} readOnly={true} />
              </UIFormField>
            </UIArticle>
            {/* 검증 비율 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  Validation Split 
                </UITypography>
                <UIInput.Text value={'0.2'} placeholder='1' onChange={e => setBatchSize(e.target.value)} readOnly={true} />
              </UIFormField>
            </UIArticle>

            {/* 학습률 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  Learning Rate
                </UITypography>
                <UIInput.Text value={'00001'} placeholder='1' onChange={e => setBatchSize(e.target.value)} readOnly={true} />
              </UIFormField>
            </UIArticle>

            {/* 배치 사이즈 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  Batch Size
                </UITypography>
                <UIInput.Text value={batchSize} placeholder='1' onChange={e => setBatchSize(e.target.value)} readOnly={true} />
              </UIFormField>
            </UIArticle>
            {/* 조기 종료 인내도 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  Early Stopping
                </UITypography>
                <UIInput.Text value={'3'} placeholder='3' onChange={e => setBatchSize(e.target.value)} readOnly={true} />
              </UIFormField>
            </UIArticle>
          </UIPopupBody>
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }} onClick={handleCancel}>
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
