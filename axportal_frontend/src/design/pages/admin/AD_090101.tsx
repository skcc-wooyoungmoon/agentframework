import React, { useMemo, useState } from 'react';

import { UIBox, UIButton2, UIDataCnt, UILabel, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGroup, UIUnitGroup, UIInput } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { useModal } from '@/stores/common/modal';

import { UIDropdown } from '../../../components/UI/molecules/dropdown/UIDropdown';
import { DesignLayout } from '../../components/DesignLayout';

export const AD_090101 = () => {
  const { openAlert } = useModal();

  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    condition: false,
    menu: false,
    menu2: false,
    pageSize: false,
  });

  // search 타입
  const [searchValue, setSearchValue] = useState('');

  // 각 드롭다운 값 상태
  const [searchTypeValue, setSearchTypeValue] = useState('최종 수정일시');
  const [conditionValue, setConditionValue] = useState('제목');
  const [menuValue, setMenuValue] = useState('전체');
  const [menuValue2, setMenuValue2] = useState('전체');

  // 샘플 데이터
  const rowData = [
    {
      id: '1',
      title: '포털 서비스 점검 안내',
      status: '게시',
      type: '시스템',
      period: '2025.03.24 09:00 ~ 2025.03.30 18:00',
      modifiedDate: '2025.03.24 18:32:43',
      more: 'more',
    },
    {
      id: '2',
      title: '신규 AI 모델 출시 공지',
      status: '게시',
      type: '서비스',
      period: '2025.03.24 00:00 ~ 2025.04.30 23:59',
      modifiedDate: '2025.03.24 15:20:15',
      more: 'more',
    },
    {
      id: '3',
      title: '보안 정책 업데이트',
      status: '게시',
      type: '보안',
      period: '2025.03.20 00:00 ~ 2025.03.25 23:59',
      modifiedDate: '2025.03.23 14:45:22',
      more: 'more',
    },
    {
      id: '4',
      title: '데이터 백업 완료 안내',
      status: '임시저장',
      type: '시스템',
      period: '2025.03.22 00:00 ~ 2025.04.22 23:59',
      modifiedDate: '2025.03.22 09:30:00',
      more: 'more',
    },
    {
      id: '5',
      title: '사용자 교육 프로그램 안내',
      status: '만료',
      type: '교육',
      period: '2025.03.25 00:00 ~ 2025.04.25 23:59',
      modifiedDate: '2025.03.21 16:15:30',
      more: 'more',
    },
  ];

  // 더보기 메뉴 설정
  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          label: '수정',
          action: 'modify',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `"${rowData.title}" 수정 팝업을 엽니다.`,
            });
          },
        },
        {
          label: '삭제',
          action: 'delete',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `"${rowData.title}" 삭제 화면으로 이동합니다.`,
            });
          },
        },
      ],
    }),
    [openAlert]
  );

  // 드롭다운 핸들러
  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    setDropdownStates(prev => ({
      ...Object.keys(prev).reduce((acc, k) => ({ ...acc, [k]: false }), {} as typeof prev),
      [key]: !prev[key],
    }));
  };

  // date 타입
  const [dateValueStart, setDateValueStart] = useState('2025.06.29');
  const [dateValueEnd, setDateValueEnd] = useState('2025.06.30');
  const [value, setValue] = useState('12개씩 보기');

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
        headerName: '제목',
        field: 'title',
        minWidth: 480,
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '상태',
        field: 'status',
        width: 120,
        cellRenderer: React.memo((params: any) => {
          const getStatusIntent = (status: string) => {
            switch (status) {
              case '게시':
                return 'complete';
              case '임시저장':
                return 'progress';
              case '만료':
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
        headerName: '유형',
        field: 'type',
        width: 180,
      },
      {
        headerName: '게시 기간',
        field: 'period',
        width: 320,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate',
        minWidth: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '',
        field: 'more',
        width: 56,
        sortable: false,
        suppressHeaderMenuButton: true,
      },
    ],
    [rowData]
  );

  return (
    <DesignLayout
      initialMenu={{ id: 'admin', label: '관리' }}
      initialSubMenu={{
        id: 'admin-user',
        label: '사용자 관리',
        icon: 'ico-lnb-menu-20-admin-user',
      }}
    >
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='공지사항 관리'
          description='포탈 내 게시할 공지사항을 등록하고 관리할 수 있습니다.'
          actions={
            <>
              {/* [251222_퍼블수정]: btn-text-14-semibold-point > btn-text-18-semibold-point  (클래스명 변경 : 폰트 크기 수정함) */}
              <UIButton2 className='btn-text-18-semibold-point' leftIcon={{ className: 'ic-system-24-add', children: '' }}>
                새 공지 등록하기
              </UIButton2>
            </>
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
                                options={[
                                  { value: 'val1', label: '최종 수정일시1' },
                                  { value: 'val2', label: '최종 수정일시2' },
                                  { value: 'val3', label: '최종 수정일시3' },
                                  { value: 'val4', label: '최종 수정일시4' },
                                ]}
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
                            조회 조건
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UIUnitGroup gap={32} direction='row'>
                            <div className='flex-1'>
                              <UIDropdown
                                value={conditionValue}
                                placeholder='조회 조건 선택'
                                options={[
                                  { value: '전체', label: '전체' },
                                  { value: '시스템', label: '시스템' },
                                  { value: '서비스', label: '서비스' },
                                  { value: '보안', label: '보안' },
                                  { value: '교육', label: '교육' },
                                  { value: '정책', label: '정책' },
                                ]}
                                isOpen={dropdownStates.condition}
                                onClick={() => handleDropdownToggle('condition')}
                                onSelect={(value: string) => {
                                  setConditionValue(value);
                                  setDropdownStates(prev => ({ ...prev, condition: false }));
                                }}
                              />
                            </div>
                            <div className='flex-1'>
                              <UIInput.Search
                                value={searchValue}
                                placeholder='검색어 입력'
                                onChange={e => {
                                  setSearchValue(e.target.value);
                                }}
                              />
                            </div>
                          </UIUnitGroup>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            유형
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UIUnitGroup gap={32} direction='row' className='items-center'>
                            <div className='flex-1'>
                              <UIDropdown
                                value={menuValue}
                                placeholder='유형 선택'
                                options={[
                                  { value: '전체', label: '전체' },
                                  { value: '공지사항', label: '공지사항' },
                                  { value: 'FAQ', label: 'FAQ' },
                                  { value: '업데이트', label: '업데이트' },
                                ]}
                                isOpen={dropdownStates.menu}
                                onClick={() => handleDropdownToggle('menu')}
                                onSelect={(value: string) => {
                                  setMenuValue(value);
                                  setDropdownStates(prev => ({ ...prev, menu: false }));
                                }}
                              />
                            </div>
                            <div className='flex flex-1 items-center'>
                              <UITypography variant='body-1' className='!w-[80px] secondary-neutral-800 text-body-1-sb'>
                                상태
                              </UITypography>
                              <UIDropdown
                                value={menuValue2}
                                placeholder='상태 선택'
                                options={[
                                  { value: '전체', label: '전체' },
                                  { value: '공지사항', label: '공지사항' },
                                  { value: 'FAQ', label: 'FAQ' },
                                  { value: '업데이트', label: '업데이트' },
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
                  <UIButton2 className='btn-secondary-blue' style={{ width: '100%' }}>
                    조회
                  </UIButton2>
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
                        <UIDataCnt count={rowData.length} prefix='총' unit='건' />
                      </div>
                    </div>
                    <div style={{ width: '160px', flexShrink: 0 }}>
                      <UIDropdown
                        value={String(value)}
                        options={[
                          { value: '1', label: '12개씩 보기' },
                          { value: '2', label: '36개씩 보기' },
                          { value: '3', label: '60개씩 보기' },
                        ]}
                        isOpen={dropdownStates.pageSize}
                        onClick={() => handleDropdownToggle('pageSize')}
                        onSelect={(value: string) => {
                          setValue(value);
                          setDropdownStates(prev => ({ ...prev, pageSize: false }));
                        }}
                        height={40}
                        variant='dataGroup'
                        width='w-40'
                        disabled={true}
                      />
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
                  onCheck={(_selectedIds: any[]) => {}}
                />
              </UIListContentBox.Body>
              <UIListContentBox.Footer className='ui-data-has-btn'>
                <UIButton2 className='btn-option-outlined' style={{ width: '40px' }}>
                  삭제
                </UIButton2>
                <UIPagination currentPage={1} totalPages={10} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
