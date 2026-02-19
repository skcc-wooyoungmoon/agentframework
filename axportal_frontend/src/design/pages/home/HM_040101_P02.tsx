import React, { useMemo, useState } from 'react';

import { UIButton2, UIDataCnt, UITypography, UILabel, UITextLabel } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIArticle, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup, type UIStepperItem } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIInput } from '@/components/UI/molecules/input';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { DesignLayout } from '../../components/DesignLayout';

// import { useModal } from '@/stores/common/modal';

// 스테퍼 데이터
const stepperItems: UIStepperItem[] = [
  {
    id: 'step1',
    label: '기본 정보 입력',
    step: 1,
  },
  {
    id: 'step2',
    label: '구성원 선택', // [251104_퍼블수정] : 구성원 추가 > 구성원 선택
    step: 2,
  },
];

export const HM_040101_P02: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true);
  const [value, setValue] = useState('전체');
  // const { openAlert } = useModal();

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  // search 타입
  const [searchValue1, setSearchValue1] = useState('');

  // UIGrid 컬럼 설정
  const columnDefs: any[] = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no',
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
        } as const,
        sortable: false,
        suppressHeaderMenuButton: true,
      },
      {
        headerName: '계정 상태', // [251104_퍼블수정] : 계정상태 > 계정 상태  (띄어쓰기)
        field: 'userRole',
        width: 120,
        cellStyle: {
          paddingLeft: '16px',
        } as const,
        cellRenderer: React.memo((params: any) => {
          return <UITextLabel intent='blue'>{params.value}</UITextLabel>;
        }),
      },
      // 251113_퍼블수정 그리드 컬럼 속성 '이름' 영역 수정 S
      {
        headerName: '이름',
        field: 'name',
        width: 272,
        cellStyle: {
          paddingLeft: '16px',
        } as const,
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
        headerName: '부서',
        field: 'department',
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        } as const,
      },
      {
        headerName: '인사 상태',
        field: 'employmentStatus',
        width: 120,
        cellStyle: {
          paddingLeft: '16px',
        } as const,
        cellRenderer: React.memo((params: any) => {
          const getStatusIntent = (status: string) => {
            switch (status) {
              case '재직':
                return 'complete';
              case '퇴사':
                return 'error';
              default:
                return 'complete';
            }
          };
          return (
            <UILabel variant='badge' intent={getStatusIntent(params.value)}>
              {params.value}
            </UILabel>
          );
        }),
      },
      {
        headerName: '마지막 접속일시',
        field: 'lastLoginAt',
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        } as const,
      },
    ],
    []
  );

  // UIGrid 로우 데이터
  const rowData = [
    {
      no: 1,
      id: '1',
      userRole: '활성화',
      name: '김신한',
      department: 'AI UNIT',
      employmentStatus: '재직',
      lastLoginAt: '2025.03.24 18:23:43',
    },
    {
      no: 2,
      id: '2',
      userRole: '활성화',
      name: '홍길동',
      department: '슈퍼SOL플랫폼부',
      employmentStatus: '재직',
      lastLoginAt: '2025.03.24 18:23:43',
    },
    {
      no: 3,
      id: '3',
      userRole: '활성화',
      name: '이영희',
      department: 'AI UNIT',
      employmentStatus: '퇴사',
      lastLoginAt: '2025.03.24 18:23:43',
    },
    {
      no: 4,
      id: '4',
      userRole: '활성화',
      name: '박철수',
      department: '디지털혁신부',
      employmentStatus: '재직',
      lastLoginAt: '2025.03.24 18:23:43',
    },
    {
      no: 5,
      id: '5',
      userRole: '활성화',
      name: '최미나',
      department: '슈퍼SOL플랫폼부',
      employmentStatus: '재직',
      lastLoginAt: '2025.03.24 18:23:43',
    },
    {
      no: 6,
      id: '6',
      userRole: '활성화',
      name: '정승호',
      department: 'AI UNIT',
      employmentStatus: '재직',
      lastLoginAt: '2025.03.24 18:23:43',
    },
    {
      no: 7,
      id: '7',
      userRole: '활성화',
      name: '강지은',
      department: '디지털혁신부',
      employmentStatus: '재직',
      lastLoginAt: '2025.03.24 18:23:43',
    },
    {
      no: 8,
      id: '8',
      userRole: '활성화',
      name: '윤태현',
      department: '슈퍼SOL플랫폼부',
      employmentStatus: '재직',
      lastLoginAt: '2025.03.24 18:23:43',
    },
    {
      no: 9,
      id: '9',
      userRole: '활성화',
      name: '조현미',
      department: 'AI UNIT',
      employmentStatus: '재직',
      lastLoginAt: '2025.03.24 18:23:43',
    },
    {
      no: 10,
      id: '10',
      userRole: '활성화',
      name: '서동진',
      department: '디지털혁신부',
      employmentStatus: '재직',
      lastLoginAt: '2025.03.24 18:23:43',
    },
  ];

  return (
    <>
      <DesignLayout
        initialMenu={{ id: 'home', label: '홈' }}
        initialSubMenu={{
          id: 'home-ide',
          label: 'IDE',
          icon: 'ico-lnb-menu-20-home',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              홈
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              IDE 생성...
            </UITypography>
          </div>
        </div>
      </DesignLayout>

      <UILayerPopup
        isOpen={isPopupOpen}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='프로젝트 생성' description='' position='left' />

            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={2} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>

            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-aside-gray'>취소</UIButton2>
                  <UIButton2 className='btn-aside-blue'>생성</UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        <section className='section-popup-content'>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='구성원 선택' description='함께할 구성원을 선택할 수 있습니다. 선택하지 않는 경우 프로젝트는 본인 단독으로 생성됩니다.' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                  요청자 정보
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
                            이름
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            김신한
                          </UITypography>
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            부서
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            슈퍼SOL플랫폼부
                          </UITypography>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            프로젝트명
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            pubilc
                          </UITypography>
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            역할
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            일반 사용자
                          </UITypography>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </UIArticle>

            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='w-full'>
                    <UIUnitGroup gap={16} direction='column'>
                      <div className='flex justify-between w-full items-center'>
                        <div style={{ width: '168px', paddingRight: '8px', display: 'flex', alignItems: 'center' }}>
                          <UIDataCnt count={rowData.length} prefix='총' unit='건' />
                        </div>
                        <div className='flex items-center gap-2'>
                          <div style={{ width: '160px', flexShrink: 0 }}>
                            <UIDropdown
                              value={String(value)}
                              options={[
                                { value: '이름', label: '이름' },
                                { value: '이름2', label: '이름2' },
                              ]}
                              onSelect={(value: string) => {setValue(value);
                              }}
                              onClick={() => {}}
                              height={40}
                              variant='dataGroup'
                            />
                          </div>
                          <div style={{ width: '360px' }}>
                            <UIInput.Search
                              value={searchValue1}
                              placeholder='검색어 입력'
                              onChange={e => {
                                setSearchValue1(e.target.value);
                              }}
                            />
                          </div>
                        </div>
                      </div>
                    </UIUnitGroup>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid
                    type='multi-select'
                    rowData={rowData}
                    columnDefs={columnDefs}
                    onClickRow={(_params: unknown) => {
                    }}
                    onCheck={(_selectedIds: any[]) => {
                    }}
                  />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination currentPage={1} totalPages={5} onPageChange={() => {}} className='flex justify-center' />
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>
          </UIPopupBody>

          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} align='start'>
                <UIButton2 className='btn-secondary-gray'>이전</UIButton2>
                {/* <UIButton2 className='btn-secondary-blue'>다음</UIButton2> */}
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
