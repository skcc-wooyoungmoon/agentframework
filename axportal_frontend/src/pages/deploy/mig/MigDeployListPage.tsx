import { useMemo, useState } from 'react';

import { Button } from '@/components/common/auth';
import { UIBox, UIDataCnt, UIIcon2, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGroup, UIInput, UIUnitGroup, type UIStepperItem } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { MigDeployProvider } from '@/providers/deploy/MigDeployProvider';
import { useGetMigMas } from '@/services/deploy/mig/mig.services';
import type { GetMigMasRequest } from '@/services/deploy/mig/types';
import { useUser } from '@/stores/auth/useUser';
import { useModal } from '@/stores/common/modal/useModal';
import { MIG_DEPLOY_CATEGORY_MAP } from '@/stores/deploy/types';
import { dateUtils } from '@/utils/common';
import { useNavigate } from 'react-router-dom';
import { MigDeployStep1TypeSelectPopupPage, MigDeployStep2TargetSelectPopupPage, MigDeployStep3AddInfoPopupPage, MigDeployStep4FinalCheckPopupPage } from './index';

interface MigDeployConfirmBodyProps {
  iconClassName?: string;
  message: string;
}

function MigDeployConfirmBody({ message }: MigDeployConfirmBodyProps) {
  return (
    <section className='section-modal'>
      <UIArticle>
        <UIGroup gap={16} direction='column' vAlign='center'>
          <UIIcon2 className='ic-system-56-feedback' />
          <UITypography variant='body-1' className='secondary-neutral-600 text-center'>
            {message}
          </UITypography>
        </UIGroup>
      </UIArticle>
    </section>
  );
}

export function MigDeployListPage() {
  const [currentStep, setCurrentStep] = useState<number>(0);
  const navigate = useNavigate();
  const { openModal } = useModal();
  const { user } = useUser();


  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    condition: false,
    menu: false,
    menu2: false,
    pageSize: false,
  });

  // 각 드롭다운 값 상태
  const [searchTypeValue, setSearchTypeValue] = useState('배포 요청일시');
  const [menuValue2, setMenuValue2] = useState('전체');

  // 검색 조건 (입력용)
  const [dateValueStart, setDateValueStart] = useState(
    dateUtils.formatDate(dateUtils.addToDate(Date.now(), -30, 'days'), 'custom', {
      pattern: 'yyyy.MM.dd',
    })
  );
  const [dateValueEnd, setDateValueEnd] = useState(
    dateUtils.formatDate(Date.now(), 'custom', {
      pattern: 'yyyy.MM.dd',
    })
  );
  const [searchKeyword, setSearchKeyword] = useState('');
  const [pageSize, setPageSize] = useState(12);

  // 실제 검색에 사용할 값 (조회 버튼 클릭 시 업데이트)
  const [appliedSearchValues, setAppliedSearchValues] = useState<GetMigMasRequest>({
    page: 1,
    size: 12,
    startDate:
      dateUtils.formatDate(dateUtils.addToDate(Date.now(), -30, 'days'), 'custom', {
        pattern: 'yyyy-MM-dd',
      }) + 'T00:00:00',
    endDate:
      dateUtils.formatDate(Date.now(), 'custom', {
        pattern: 'yyyy-MM-dd',
      }) + 'T23:59:59',
    prjSeq: user.activeProject?.prjSeq ? Number(user.activeProject.prjSeq) : undefined,
  });

  // 날짜 형식 변환 함수 (yyyy.MM.dd -> ISO 8601)
  const convertDateToISO = (dateStr: string, isEndDate: boolean = false): string => {
    if (!dateStr) return '';
    const date = dateStr.replace(/\./g, '-');
    return isEndDate ? `${date}T23:59:59` : `${date}T00:00:00`;
  };

  // API 호출
  const { data: migMasData, refetch: refetchMigMas } = useGetMigMas(appliedSearchValues, {
    enabled: !!appliedSearchValues.page && !!appliedSearchValues.size,
  });

  // 응답 데이터를 그리드 형식으로 변환
  const rowData = useMemo(() => {
    if (!migMasData || !migMasData.content) return [];

    return migMasData.content.map(item => ({
      id: item.uuid || String(item.seqNo),
      seqNo: item.seqNo,
      asstG: item.asstG,
      type: MIG_DEPLOY_CATEGORY_MAP[item.asstG as keyof typeof MIG_DEPLOY_CATEGORY_MAP] || item.asstG,
      target: item.asstNm,
      modifiedDate: item.fstCreatedAt ? dateUtils.formatDate(item.fstCreatedAt, 'datetime') : '',
      uuid: item.uuid,
      prjSeq: item.prjSeq,
      gpoPrjNm: item.gpoPrjNm,
      filePath: item.filePath,
      fileNms: item.fileNms,
    }));
  }, [migMasData]);

  // 드롭다운 핸들러
  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    setDropdownStates(prev => ({
      ...Object.keys(prev).reduce((acc, k) => ({ ...acc, [k]: false }), {} as typeof prev),
      [key]: !prev[key],
    }));
  };

  // 조회 버튼 핸들러
  const handleSearch = () => {
    const searchParams: GetMigMasRequest = {
      page: 1,
      size: pageSize,
      startDate: convertDateToISO(dateValueStart, false),
      endDate: convertDateToISO(dateValueEnd, true),
      prjSeq: user.activeProject?.prjSeq ? Number(user.activeProject.prjSeq) : undefined,
    };

    // undefined가 아닌 값만 추가
    if (searchKeyword.trim()) {
      searchParams.asstNm = searchKeyword.trim();
    }
    if (menuValue2 !== '전체') {
      searchParams.asstG = menuValue2;
    }

    setAppliedSearchValues(searchParams);
  };

  // 페이지 변경 핸들러
  const handlePageChange = (newPage: number) => {
    setAppliedSearchValues(prev => ({
      ...prev,
      page: newPage,
    }));
  };

  // 페이지 크기 변경 핸들러
  const handlePageSizeChange = (value: string) => {
    const sizeMap: Record<string, number> = {
      '1': 12,
      '2': 36,
      '3': 60,
    };
    const newSize = sizeMap[value] || 12;
    setPageSize(newSize);
    setAppliedSearchValues(prev => ({
      ...prev,
      page: 1,
      size: newSize,
    }));
  };

  // 그리드 컬럼 정의
  const columnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'seqNo' as any,
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
        valueGetter: (params: any) => {
          const currentPage = appliedSearchValues.page || 1;
          const pageSize = appliedSearchValues.size || 12;
          return (currentPage - 1) * pageSize + (params.node?.rowIndex || 0) + 1;
        },
      },
      {
        headerName: '배포 분류',
        field: 'type',
        minWidth: 622,
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '배포 대상',
        field: 'target',
        width: 622,
      },
      {
        headerName: '배포 요청일시',
        field: 'modifiedDate',
        minWidth: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    [appliedSearchValues.page, appliedSearchValues.size]
  );

  /**
   * 스테퍼 데이터
   */
  const stepperItems: UIStepperItem[] = [
    {
      id: 'step1',
      label: '분류 선택',
      step: 1,
    },
    {
      id: 'step2',
      label: '배포 대상 선택',
      step: 2,
    },
    {
      id: 'step3',
      label: '운영용 정보 입력',
      step: 3,
    },
    {
      id: 'step4',
      label: '최종 정보 확인',
      step: 4,
    },
  ];

  const handleMigDeployPopup = () => {
    openModal({
      type: '2xsmall',
      title: '안내',
      body: <MigDeployConfirmBody message=' 운영 환경 내 일관된 프로젝트 및 역할 구조 유지를 위해, 선택한 프로젝트 정보와 해당 프로젝트 내 역할 정보가 함께 배포됩니다.' />,
      confirmText: '예',
      onConfirm: () => {
        setCurrentStep(1);
      },
    });
  };

  const handleNextStep = () => {
    setCurrentStep(prev => prev + 1);
  };

  const handlePreviousStep = () => {
    setCurrentStep(prev => prev - 1);
  };

  const handlePopupClose = () => {
    setCurrentStep(0);
  };

  const handleRowClick = (item: any) => {
    // state로 asstG, seqNo, uuid 전달
    navigate(`/deploy/migDeploy/${item.uuid}`, {
      state: {
        asstG: item.asstG || '',
        seqNo: item.seqNo || '',
        uuid: item.uuid || '',
      },
    });
  };

  return (
    <>
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='운영 배포'
          description='포탈 개발 환경 내 에셋들을 운영으로 이행할 수 있으며, 배포한 이력을 확인할 수 있습니다.'
          actions={
            <Button
              className='btn-text-18-semibold-point'
              leftIcon={{ className: 'ic-system-24-add', children: '' }}
              onClick={handleMigDeployPopup}
            >
              운영 배포
            </Button>
          }
        />

        {/* 페이지 바디 */}
        <UIPageBody>
          {/* [251105_퍼블수정] 검색영역 수정 */}
          <UIArticle className='article-filter'>
            <UIBox className='box-filter'>
              <UIGroup gap={40} direction='row'>
                <div style={{ width: 'calc(100% - 168px)' }}>
                  <table className='tbl_type_b'>
                    <tbody>
                      <tr>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            조회 기간
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UIUnitGroup gap={32} direction='row'>
                            <div className='flex-1'>
                              <UIDropdown
                                value={searchTypeValue}
                                placeholder='조회 기간 선택'
                                options={[{ value: 'fstCreatedAt', label: '배포 요청일시' }]}
                                isOpen={dropdownStates.searchType}
                                onClick={() => handleDropdownToggle('searchType')}
                                onSelect={(value: string) => {
                                  setSearchTypeValue(value);
                                  setDropdownStates(prev => ({ ...prev, searchType: false }));
                                }}
                              />
                            </div>
                            <div className='flex-1' style={{ zIndex: '10' }}>
                              <UIUnitGroup gap={8} direction='row' vAlign='center'>
                                <div className='flex-1'>
                                  <UIInput.Date
                                    value={dateValueStart}
                                    onChange={e => {
                                      setDateValueStart(e.target.value);
                                    }}
                                  />
                                </div>

                                <UITypography variant='body-1' className='secondary-neutral-p w-[11px]'>
                                  ~
                                </UITypography>

                                <div className='flex-1'>
                                  <UIInput.Date
                                    value={dateValueEnd}
                                    onChange={e => {
                                      setDateValueEnd(e.target.value);
                                    }}
                                  />
                                </div>
                              </UIUnitGroup>
                            </div>
                          </UIUnitGroup>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            검색
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UIUnitGroup gap={32} direction='row' className='items-center'>
                            <div className='flex-1'>
                              <UIInput.Search value={searchKeyword} placeholder='배포 대상 입력' onChange={e => setSearchKeyword(e.target.value)} />
                            </div>
                            <div className='flex flex-1 items-center'>
                              <UITypography variant='body-1' className='!w-[80px] secondary-neutral-800 text-body-1-sb'>
                                배포 분류
                              </UITypography>
                              <UIDropdown
                                value={menuValue2}
                                placeholder='배포 분류 선택'
                                options={[
                                  { value: '전체', label: '전체' },
                                  { value: 'SAFETY_FILTER', label: '세이프티 필터' },
                                  { value: 'GUARDRAILS', label: '가드레일' },
                                  { value: 'KNOWLEDGE', label: '지식' },
                                  { value: 'VECTOR_DB', label: '벡터 DB' },
                                  { value: 'SERVING_MODEL', label: '모델' },
                                  { value: 'AGENT_APP', label: '에이전트' },
                                  { value: 'PROJECT', label: '프로젝트' },
                                ]}
                                isOpen={dropdownStates.menu2}
                                onClick={() => handleDropdownToggle('menu2')}
                                onSelect={(value: string) => {
                                  setMenuValue2(value);
                                  setDropdownStates(prev => ({ ...prev, menu2: false }));
                                }}
                              />
                            </div>
                          </UIUnitGroup>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
                <div style={{ width: '128px' }}>
                  <Button className='btn-secondary-blue' style={{ width: '100%' }} onClick={handleSearch}>
                    조회
                  </Button>
                </div>
              </UIGroup>
            </UIBox>
          </UIArticle>

          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <UIUnitGroup gap={16} direction='column'>
                  <div className='flex justify-between w-full items-center'>
                    <div className='flex-shrink-0'>
                      <div style={{ width: '168px', paddingRight: '8px' }}>
                        <UIDataCnt count={migMasData?.totalElements || 0} prefix='총' unit='건' />
                      </div>
                    </div>
                    <div style={{ width: '160px', flexShrink: 0 }}>
                      <UIDropdown
                        value={String(pageSize === 12 ? '1' : pageSize === 36 ? '2' : '3')}
                        options={[
                          { value: '1', label: '12개씩 보기' },
                          { value: '2', label: '36개씩 보기' },
                          { value: '3', label: '60개씩 보기' },
                        ]}
                        isOpen={dropdownStates.pageSize}
                        onClick={() => handleDropdownToggle('pageSize')}
                        onSelect={(value: string) => {
                          handlePageSizeChange(value);
                          setDropdownStates(prev => ({ ...prev, pageSize: false }));
                        }}
                        height={40}
                        variant='dataGroup'
                        width='w-40'
                      />
                    </div>
                  </div>
                </UIUnitGroup>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid
                  type='default'
                  rowData={rowData}
                  columnDefs={columnDefs}
                  onClickRow={(params: any) => {
                    handleRowClick(params.data);
                  }}
                />
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination
                  currentPage={appliedSearchValues.page || 1}
                  totalPages={migMasData?.totalPages || 1}
                  onPageChange={handlePageChange}
                  className='flex justify-center'
                />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>
      <MigDeployProvider>
        {currentStep === 1 && <MigDeployStep1TypeSelectPopupPage isOpen={currentStep === 1} stepperItems={stepperItems} onClose={handlePopupClose} onNextStep={handleNextStep} />}
        {currentStep === 2 && (
          <MigDeployStep2TargetSelectPopupPage
            isOpen={currentStep === 2}
            stepperItems={stepperItems}
            onClose={handlePopupClose}
            onPreviousStep={handlePreviousStep}
            onNextStep={handleNextStep}
          />
        )}
        {currentStep === 3 && (
          <MigDeployStep3AddInfoPopupPage
            isOpen={currentStep === 3}
            stepperItems={stepperItems}
            onClose={handlePopupClose}
            onPreviousStep={handlePreviousStep}
            onNextStep={handleNextStep}
          />
        )}
        {currentStep === 4 && (
          <MigDeployStep4FinalCheckPopupPage
            isOpen={currentStep === 4}
            stepperItems={stepperItems}
            onClose={handlePopupClose}
            onPreviousStep={handlePreviousStep}
            onSuccess={() => {
              refetchMigMas();
            }}
          />
        )}
      </MigDeployProvider>
    </>
  );
}
