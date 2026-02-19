import React, { useMemo, useState } from 'react';

import { UIButton2, UIFileBox, UIIcon2, UIRadio2, UITypography } from '@/components/UI/atoms';
import { UIImage } from '@/components/UI/atoms/UIImage';
import { UIDataCnt } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIArticle, UIFormField, UIGroup, UIList, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup } from '@/components/UI/molecules';
import { UIInput } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

// import { useModal } from '@/stores/common/modal';
import { DesignLayout } from '../../components/DesignLayout';

export const DT_020101_P03: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true);
  const [searchValue, setSearchValue] = useState('');

  // 스테퍼 데이터
  const stepperItems = [
    { step: 1, label: '데이터 정보입력' },
    { step: 2, label: '데이터 가져오기' },
    { step: 3, label: '선택 데이터 확인' },
    { step: 4, label: '프로세서 선택' },
  ];

  // 파일 목록 상태
  const [files, setFiles] = useState<string[]>(['Summary_train_2nd.csv', 'Summary_train_2nd.csv', 'Summary_train_2nd.csv', 'Summary_train_2nd.csv']);

  // 샘플 데이터
  const rowData = [
    {
      id: '1',
      agentName: '입출금이자유로운예금_상품설명서.pdf',
      description: '대출 약관 관련 데이터세트',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '2',
      agentName: '입출금이자유로운예금_상품설명서.pdf',
      description: '대출 약관 관련 데이터세트',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '3',
      agentName: '입출금이자유로운예금_상품설명서.pdf',
      description: '대출 약관 관련 데이터세트',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '4',
      agentName: '입출금이자유로운예금_상품설명서.pdf',
      description: '대출 약관 관련 데이터세트',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '5',
      agentName: '입출금이자유로운예금_상품설명서.pdf',
      description: '대출 약관 관련 데이터세트',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '6',
      agentName: '입출금이자유로운예금_상품설명서.pdf',
      description: '대출 약관 관련 데이터세트',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '7',
      agentName: '입출금이자유로운예금_상품설명서.pdf',
      description: '대출 약관 관련 데이터세트',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
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
      {
        headerName: '파일명',
        field: 'agentName' as any,
        width: 272,
        sortable: false,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      // [251113_퍼블수정] 그리드 컬럼 속성 수정
      {
        headerName: '설명',
        field: 'description' as any,
        minWidth: 392,
        flex: 1,
        sortable: false,
        showTooltip: true,
        cellStyle: {
          paddingLeft: '16px',
        },
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
        headerName: '생성일시',
        field: 'createdDate' as any,
        width: 180,
        sortable: false,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate' as any,
        width: 180,
        sortable: false,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    [rowData]
  );

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  // [251114_퍼블수정] 파일 업로드 영역 수정
  const handleFileRemove = (index?: number) => {
    if (index === undefined) return;
    setFiles((prev: any) => prev.filter((_: any, i: any) => i !== index));
    // setFileToggles(prev => prev.filter((_, i) => i !== index));
  };

  // 더보기 메뉴 설정

  /* [251120_퍼블수정] 컨텐츠 수정 */
  // 라디오 카드 옵션 데이터
  const radioOptions = [
    {
      value: 'quick_start',
      label: 'No',
      image: '/assets/images/data/ico-radio-data-number.svg',
      alt: 'No',
      description: ['각 데이터를 구분하기 위한 고유 번호입니다.'],
    },
    {
      value: 'System',
      label: 'System',
      image: '/assets/images/data/ico-radio-visual08.svg',
      alt: 'System',
      description: ['모델의 역할과 말투, 답변 규칙을 정하는 시스템 프롬프트입니다.', 'ex) 당신은 금융 고객센터 상담원입니다. 항상 존댓말로, 3문장 이내로 답변하세요.'],
    },
    {
      value: 'User',
      label: 'User',
      image: '/assets/images/data/ico-radio-visual06.svg',
      alt: 'User',
      description: ['실제 사용자가 입력한 질문·요청 내용을 적는 컬럼입니다.', 'ex) 퇴직연금 DC형과 IRP 차이 알려줘.'],
    },
    {
      value: 'Assistant',
      label: 'Assistant',
      image: '/assets/images/data/ico-radio-visual07.svg',
      alt: 'Assistant',
      description: [
        '해당 User 입력에 대해 모델이 출력해야 하는 기대 응답입니다.',
        'ex) DC형은 퇴직연금을 내가 운용하고, IRP는 퇴직금과 개인 납입금을 모아 운용하는 개인형 연금계좌입니다.',
      ],
    },
    {
      value: 'Chosen',
      label: 'Chosen',
      image: '/assets/images/data/ico-radio-visual09.svg',
      alt: 'Chosen',
      description: [
        'User에 대한 여러 답변 중 더 선호되는 답변입니다. 모델이 이런 스타일을 더 많이 따라 하도록 학습합니다.',
        'ex) DC형은 퇴직연금을 내가 운용하고, IRP는 퇴직금과 개인 납입금을 모아 운용하는 개인형 연금계좌입니다.',
      ],
    },
    {
      value: 'Rejected',
      label: 'Rejected',
      image: '/assets/images/data/ico-radio-visual10.svg',
      alt: 'Rejected',
      description: [
        'User에 대한 답변 중 지양해야 하는 답변입니다. 부정확한, 불친절한 답변을 넣어 모델이 피하도록 학습합니다.',
        'ex) 둘 다 노후 준비용 계좌로서 사실 큰 차이 없습니다.',
      ],
    },
    {
      value: 'System',
      label: 'System',
      image: '/assets/images/data/ico-radio-visual11.svg',
      alt: 'System',
      description: [
        '모델이 학습할 실제 텍스트 내용입니다. 문장, 문단, 문서 등 자유로운 형식의 텍스트를 입력할 수 있습니다.',
        'ex) 퇴직연금 DC형은 회사가 적립한 금액을 근로자가 직접 운용하는 제도입니다.',
      ],
    },
  ];

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
                <UIStepper currentStep={2} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: 80 }}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: 80 }} disabled={true}>
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
          <UIPopupHeader title='데이터 가져오기' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle>
              <UIFormField gap={12} direction='column'>
                <div className='inline-flex items-center'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                    가져오기 방법 선택
                  </UITypography>
                </div>
                <div>
                  <UIUnitGroup gap={12} direction='column' align='start'>
                    <UIRadio2 name='basic1' value='option1' label='데이터 탐색' />
                    {/* 여기 label='데이터 탐색' 수정  */}
                    {/* [251111_퍼블수정] 타이틀명칭 변경 : 데이터 저장소 > 데이터 탐색 */}
                    <UIRadio2 name='basic1' value='option2' label='파일 업로드' />
                  </UIUnitGroup>
                </div>
              </UIFormField>
            </UIArticle>
            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='flex-shrink-0'>
                    <UIGroup gap={8} direction='row' align='start'>
                      <div style={{ width: '168px', paddingRight: '8px' }}>
                        <UIDataCnt count={10} prefix='학습 데이터 총' unit='건' />
                      </div>
                    </UIGroup>
                  </div>
                  <div className='flex items-center gap-2'>
                    <div className='w-[360px]'>
                      <UIInput.Search
                        value={searchValue}
                        onChange={e => {
                          setSearchValue(e.target.value);
                        }}
                        placeholder='검색어 입력'
                      />
                    </div>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid type='multi-select' rowData={rowData} columnDefs={columnDefs} onClickRow={(_params: any) => {}} onCheck={(_selectedIds: any[]) => {}} />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination currentPage={1} totalPages={1} onPageChange={() => {}} className='flex justify-center' />
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>

            <UIArticle>
              <UIGroup gap={16} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  파일 업로드
                </UITypography>
                <div>
                  <UIButton2 className='btn-tertiary-outline download' onClick={() => {}}>
                    파일 업로드
                  </UIButton2>
                </div>
                <div>
                  {/* 파일 목록 */}
                  {/* [251114_퍼블수정] 파일 업로드 영역 수정 */}
                  {files.length > 0 && (
                    <UIFileBox
                      variant='default'
                      size='full'
                      items={files.map(fileName => ({ fileName, fileSize: 99, progress: 100 }))}
                      onFileRemove={handleFileRemove}
                      className='w-full'
                    />
                  )}
                </div>
                <div>
                  <UIList
                    gap={4}
                    direction='column'
                    className='ui-list_bullet'
                    data={[
                      {
                        dataItem: (
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            대용량 데이터는 데이터 저장소에서 가져와야합니다.
                          </UITypography>
                        ),
                      },
                      {
                        dataItem: (
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            업로드한 파일의 인코딩이 UTF-8로 작성이 되어야 합니다.
                          </UITypography>
                        ),
                      },
                      {
                        dataItem: (
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            암호화 설정된 파일은 업로드가 불가능하니 암호화 해제 후 파일 업로드를 해주세요.
                          </UITypography>
                        ),
                      },
                      {
                        dataItem: (
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            지원되는 파일 확장자 : .ZIP
                          </UITypography>
                        ),
                      },
                      {
                        dataItem: (
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            Custom 유형의 학습 데이터세트는 업로드 파일 크기 제한이 없습니다.
                          </UITypography>
                        ),
                      },
                    ]}
                  />
                </div>
              </UIGroup>
            </UIArticle>

            <UIArticle>
              <div className='box-fill'>
                <UIUnitGroup gap={8} direction='column' align='start'>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0 6px' }}>
                    <UIIcon2 className='ic-system-16-info-gray' />
                    <UITypography variant='body-2' className='secondary-neutral-600 text-sb'>
                      학습 데이터 유형에 알맞은 필수 칼럼값을 확인해주세요.
                    </UITypography>
                  </div>
                  <div style={{ paddingLeft: '22px' }}>
                    <UIUnitGroup gap={8} direction='column' align='start'>
                      <UIList
                        gap={4}
                        direction='column'
                        className='ui-list_dash'
                        data={[
                          {
                            dataItem: (
                              <UITypography variant='body-2' className='secondary-neutral-600'>
                                {'지도학습 데이터 필수 칼럼값 : no, system, user, assistant'}
                              </UITypography>
                            ),
                          },
                        ]}
                      />
                      <UIList
                        gap={4}
                        direction='column'
                        className='ui-list_dash'
                        data={[
                          {
                            dataItem: (
                              <UITypography variant='body-2' className='secondary-neutral-600'>
                                {'비지도학습 데이터 필수 칼럼값 : no, text'}
                              </UITypography>
                            ),
                          },
                        ]}
                      />
                      <UIList
                        gap={4}
                        direction='column'
                        className='ui-list_dash'
                        data={[
                          {
                            dataItem: (
                              <UITypography variant='body-2' className='secondary-neutral-600'>
                                {'DPO학습 데이터 필수 칼럼값 : user, chosen, rejected'}
                              </UITypography>
                            ),
                          },
                        ]}
                      />
                    </UIUnitGroup>
                  </div>
                </UIUnitGroup>
              </div>
            </UIArticle>
            {/* [251120_퍼블수정] 컨텐츠 수정 */}
            <UIArticle>
              <UIFormField gap={16} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                  지도 학습 필수 컬럼별 역할 설명
                </UITypography>
                <div className='flex gap-6'>
                  {radioOptions.slice(0, 4).map(option => (
                    <div key={option.value} className='flex flex-col space-y-3 w-[298px]'>
                      {/* 라디오 카드 */}
                      <div className='w-[298px] h-[200px] rounded-[20px] p-6 flex items-center justify-center bg-col-gray-100 border-2 border-transparent cursor-default pointer-events-none'>
                        <UIImage src={option.image} alt={option.alt} className='max-w-full max-h-full' />
                      </div>
                      {/* 텍스트 영역 - 카드 밑으로 분리 */}
                      <div className='flex flex-col gap-2'>
                        <UITypography variant='body-1' className='secondary-neutral-800'>
                          {option.label}
                        </UITypography>
                        {option.description && option.description.length > 0 && (
                          <div className='flex flex-col gap-2'>
                            {option.description.map((desc, idx) => (
                              <UITypography key={idx} variant='body-2' className='secondary-neutral-600'>
                                {desc}
                              </UITypography>
                            ))}
                          </div>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              </UIFormField>
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray'>이전</UIButton2>
                <UIButton2 className='btn-secondary-blue'>다음</UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
