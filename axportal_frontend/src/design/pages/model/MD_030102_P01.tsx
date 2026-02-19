import React, { useState, useMemo } from 'react';

import { UIIcon2, UIRadio2, UIButton2, UITypography, UITooltip, UIToggle, UIDataCnt, UITextLabel, UISlider } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIDropdown, UIUnitGroup, UIFormField, UIPopupHeader, UIPopupBody, UIPopupFooter, UIArticle, UIGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIInput } from '@/components/UI/molecules/input';
import { UITextArea2 } from '@/components/UI/molecules/input/UITextArea2';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { useModal } from '@/stores/common/modal';
import { UINotice } from '../../../components/UI/atoms/UINotice';
import { DesignLayout } from '../../components/DesignLayout';

export const MD_030102_P01: React.FC = () => {
  const [isPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음
  const { openAlert } = useModal();

  // 슬라이더 상태 관리

  const [verificationValue, setVerificationValue] = useState(45);

  const [earlyStop] = useState(true);
  const [patience, setPatience] = useState('3');
  const [batchSize, setBatchSize] = useState('1');

  // 라디오 버튼 상태
  const [learningType, setLearningType] = useState('supervised'); // 지도학습/비지도학습
  const [adjustmentTech, setAdjustmentTech] = useState('basic'); // BASIC

  // 드롭다운 상태
  const [isLearningTypeDropdownOpen, setIsLearningTypeDropdownOpen] = useState(false);

  // 학습 유형 옵션
  const learningTypeOptions = [
    {
      value: 'supervised',
      label: '지도 학습',
      description: '입력과 출력을 하나의 세트 데이터로 모델을 학습시킵니다.',
    },
    {
      value: 'unsupervised',
      label: '비지도 학습',
      description: '입력 데이터로만 모델을 학습시킵니다.',
    },
  ];

  // 드롭다운용 학습 유형 옵션
  const learningTypeDropdownOptions = learningTypeOptions.map(option => ({
    value: option.value,
    label: option.label,
  }));

  // 현재 선택된 학습 유형의 라벨
  const selectedLearningTypeLabel = learningTypeOptions.find(option => option.value === learningType)?.label || '지도 학습';

  // 효율성 구성(PFT) 옵션 주석 처리

  // 미세 조정 기술 옵션 주석 처리

  // 샘플 데이터 (rowData로 변수명 변경)
  const rowData = [
    {
      id: '1',
      name: '예적금 상품 Q&A 세트',
      status: '진행중',
      description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담',
      type: '지도학습',
      tags: ['가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자'],
      openRange: '전체공유',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
    },
    {
      id: '2',
      name: '금융 대출 승인 데이터',
      status: '진행중',
      description: '고객 신용평가 및 대출 승인 관련 데이터',
      type: '지도학습',
      tags: ['가나다라마바사아', '가나다라마바사아'],
      openRange: '전체공유',
      createdDate: '2025.03.23 14:15:32',
      modifiedDate: '2025.03.23 14:15:32',
    },
    {
      id: '3',
      name: '민원 분류 데이터',
      status: '진행중',
      description: '고객 민원 분류 및 처리 데이터',
      type: '비지도학습',
      tags: ['test'],
      openRange: '전체공유',
      createdDate: '2025.03.22 09:45:21',
      modifiedDate: '2025.03.22 09:45:21',
    },
    {
      id: '4',
      name: '금융서류 분류 데이터',
      status: '종료',
      description: '각종 금융서류 분류 및 정리',
      type: '지도학습',
      tags: ['Tag1', 'Tag2'],
      openRange: '전체공유',
      createdDate: '2025.03.24 16:30:15',
      modifiedDate: '2025.03.24 16:30:15',
    },
    {
      id: '5',
      name: 'Transformer 번역',
      status: '진행중',
      description: '다국어 번역을 위한 Transformer 기반 신경망 모델',
      type: '지도학습',
      tags: ['NLP', '번역'],
      openRange: '전체공유',
      createdDate: '2025.03.21 12:00:00',
      modifiedDate: '2025.03.21 12:00:00',
    },
    {
      id: '6',
      name: 'AutoEncoder 이상탐지',
      status: '진행중',
      description: '데이터 이상 패턴 탐지를 위한 오토인코더 모델',
      type: '비지도학습',
      tags: ['이상탐지'],
      openRange: '전체공유',
      createdDate: '2025.03.20 15:30:00',
      modifiedDate: '2025.03.20 15:30:00',
    },
    {
      id: '7',
      name: 'RNN 시계열 예측',
      status: '실패',
      description: '시계열 데이터 분석 및 미래값 예측을 위한 RNN 모델',
      type: '지도학습',
      tags: ['시계열', '예측', 'ddd', '시계열', '예측', 'ddd'],
      openRange: '전체공유',
      createdDate: '2025.03.19 10:00:00',
      modifiedDate: '2025.03.19 10:00:00',
    },
  ];

  // 더보기 메뉴 설정
  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          label: '실행',
          action: 'run',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `테스트 "${rowData.name}" 실행을 시작합니다.`,
            });
          },
        },
        {
          label: '수정',
          action: 'modify',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `테스트 "${rowData.name}" 수정 팝업을 엽니다.`,
            });
          },
        },
        {
          label: '복사',
          action: 'copy',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `테스트 "${rowData.name}" 복사가 완료되었습니다.`,
            });
          },
        },
        {
          label: '삭제',
          action: 'delete',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `테스트 "${rowData.name}" 삭제 화면으로 이동합니다.`,
            });
          },
        },
      ],
      isActive: () => true, // 모든 테스트에 대해 활성화
    }),
    []
  );

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
        width: 262,
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
        headerName: '설명',
        field: 'description',
        minWidth: 230,
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
        headerName: '공개범위',
        field: 'openRange',
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '유형',
        field: 'type',
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate',
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },
    ],
    [rowData]
  );

  const verificationChange = (value: number) => {
    setVerificationValue(value);
  };

  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'model', label: '모델' }}
        initialSubMenu={{
          id: 'model-finetuning',
          label: '파인튜닝',
          icon: 'ico-lnb-menu-20-model',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              모델 파인튜닝
            </UITypography>
          </div>
        </div>
      </DesignLayout>

      {/* DesignLayout 위에 높은 z-index로 뜨는 UILayerPopup */}
      <UILayerPopup
        isOpen={isPopupOpen}
        // onClose={}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            <UIPopupHeader title='파인튜닝 수정' description='' position='left' />
            <UIPopupBody>
              <UIArticle>{/* 좌측 콘텐츠 영역 */}</UIArticle>
            </UIPopupBody>
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={true}>
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
            {/* 파인튜닝 이름 입력 섹션 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb' required={true}>
                  파인튜닝 이름
                </UITypography>
                <UIInput.Text value={'AI Chatbot'} placeholder='파인튜닝 이름을 입력해주세요' onChange={_e => {}} disabled={false} />
              </UIFormField>
            </UIArticle>

            {/* 파인튜닝 설명 입력 섹션 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                  설명
                </UITypography>
                <UITextArea2 placeholder='파인튜닝 설명을 입력해주세요' maxLength={100} value={'챗봇 테스트'} onChange={_e => {}} />
              </UIFormField>
            </UIArticle>

            {/* 학습 유형 섹션 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb' required={true}>
                  데이터세트 유형
                </UITypography>
                <UIDropdown
                  value={selectedLearningTypeLabel}
                  options={learningTypeDropdownOptions}
                  isOpen={isLearningTypeDropdownOpen}
                  onClick={() => setIsLearningTypeDropdownOpen(!isLearningTypeDropdownOpen)}
                  disabled={true}
                  onSelect={(_value: string) => {
                    setLearningType(_value);
                    setIsLearningTypeDropdownOpen(false);
                  }}
                />
              </UIFormField>
            </UIArticle>

            {/* 미세 조정 기술 섹션 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb' required={true}>
                  Fine Tuning Techniques
                </UITypography>
                <div className='space-y-4'>
                  <div className='flex items-start gap-2'>
                    <UIRadio2 name='adjustmentTech' value='basic' checked={adjustmentTech === 'basic'} onChange={() => setAdjustmentTech('basic')} />
                    <UIGroup direction='column' gap={4}>
                      <UITypography variant='body-1' className='secondary-neutral-800 text-sb'>
                        BASIC
                      </UITypography>
                      <UITypography variant='body-2' className='secondary-neutral-600 '>
                        초기 실험이나 작은 데이터에셋에 적합하며, 복잡한 튜닝 없이 바로 확인할 수 있습니다.
                      </UITypography>
                    </UIGroup>
                  </div>
                </div>
              </UIFormField>
            </UIArticle>

            {/* 자원 할당 섹션 */}
            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-3' className='secondary-neutral-900'>
                  자원 할당
                </UITypography>
              </div>
              <div className='article-body'>
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
              </div>
            </UIArticle>

            {/* 학습 횟수 입력 섹션 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb' required={true}>
                  Epochs
                </UITypography>
                <UIInput.Text value={'0.0001'} placeholder='학습 횟수 입력' onChange={_e => {}} disabled={false} />
              </UIFormField>
              <div className='mt-2'>
                <UINotice
                  variant='info'
                  message={
                    <>
                      전체 데이터셋을 모델이 한 번 학습하는 주기를 지정합니다. 값이 높을수록 학습이 오래 진행되지만, 과적합 위험이 증가하므로,
                      <br />
                      초기에 3~5회로 설정한 뒤 검증 성능 변화를 보고 조정하세요.
                    </>
                  }
                  bulletType='circle'
                  gapSize='large'
                />
              </div>
            </UIArticle>

            {/* 데이터 그리드 섹션 */}
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
                    type='multi-select'
                    rowData={rowData}
                    columnDefs={columnDefs}
                    moreMenuConfig={moreMenuConfig}
                    onClickRow={(_params: any) => {}}
                    onCheck={(_selectedIds: any[]) => {
                    }}
                  />
                </UIListContentBox.Body>
                <UIListContentBox.Footer className='ui-data-has-btn'>
                  <UIButton2 className='btn-option-outlined' style={{ width: '40px' }}>
                    추가
                  </UIButton2>
                  <UIPagination currentPage={1} totalPages={10} onPageChange={() => {}} className='flex justify-center' />
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>

            {/* 슬라이더 섹션 */}
            {/* [251110_퍼블수정] 슬라이더 속성값 재정의 */}
            <UIArticle>
              <div className='w-full'>
                <UISlider label='Validation Split' value={verificationValue} min={0} max={100} required={true} showTextField={true} onChange={verificationChange} color='#2670FF' />
              </div>
            </UIArticle>

            {/* 학습률 섹션 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb' required={true}>
                  Learning Rate
                </UITypography>
                <UIInput.Text value={'1'} placeholder='학습률 입력' onChange={_e => {}} disabled={false} />
              </UIFormField>
            </UIArticle>

            {/* 배치 사이즈 섹션 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb' required={true}>
                  Batch Size
                </UITypography>
                <UIInput.Text value={'1'} placeholder='학습 횟수 입력' onChange={_e => {}} disabled={false} />
              </UIFormField>
            </UIArticle>

            {/* 조기 종료 */}
            <UIArticle>
              <UIToggle label='Early Stopping' labelPosition='top' checked={earlyStop} variant='basic' size='medium' />
            </UIArticle>

            {/* 조기 종료 인내도 (조건부 표시) */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <div className='flex items-start'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                    Early Stopping Patience
                  </UITypography>
                  <UITooltip
                    trigger='click'
                    position='bottom-start'
                    type='notice'
                    title=''
                    items={['조기 종료 인내도는 입력한 학습횟수까지만 적용됩니다.']}
                    bulletType='default'
                    showArrow={false}
                    showCloseButton={true}
                    className='ml-1'
                  >
                    <UIButton2>
                      <UIIcon2 className='ic-system-20-info' />
                    </UIButton2>
                  </UITooltip>
                </div>
                <UIInput.Text value={patience} placeholder='3' onChange={e => setPatience(e.target.value)} />
              </UIFormField>
            </UIArticle>
          </UIPopupBody>
        </section>
      </UILayerPopup>
    </>
  );
};
