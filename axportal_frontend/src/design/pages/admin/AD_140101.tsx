import React, { useMemo, useState } from 'react';

import { UIInput, UIGroup } from '@/components/UI/molecules';

import { UIBox, UIButton2, UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { useModal } from '@/stores/common/modal';
import { DesignLayout } from '../../components/DesignLayout';
import { UIDropdown } from '../../../components/UI/molecules/dropdown/UIDropdown';
import { UITabs } from '../../../components/UI/organisms';

interface SearchValues {
  dateType: string;
  dateRange: { startDate?: string; endDate?: string };
  searchType: string;
  searchKeyword: string;
  status: string;
  publicRange: string;
  category: string;
}

export const AD_140101 = () => {
  const [searchValues, setSearchValues] = useState<SearchValues>({
    dateType: '생성일시',
    dateRange: { startDate: '2025.06.30', endDate: '2025.07.30' },
    searchType: '이름',
    searchKeyword: '',
    status: '전체',
    publicRange: '전체',
    category: '전체',
  });

  const { openAlert } = useModal();
  const [value, setValue] = useState('12개씩 보기');

  // search 타입
  const [searchValue, setSearchValue] = useState('');

  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    status: false,
    publicRange: false,
    category: false,
  });

  // 드롭다운 핸들러
  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    setDropdownStates(prev => ({
      ...Object.keys(prev).reduce((acc, k) => ({ ...acc, [k]: false }), {} as typeof prev),
      [key]: !prev[key],
    }));
  };

  const handleDropdownSelect = (key: keyof SearchValues, value: string) => {
    setSearchValues(prev => ({ ...prev, [key]: value }));
    setDropdownStates(prev => ({ ...prev, [key]: false }));
  };

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
              message: `이미지 "${rowData.imageName}" 실행을 시작합니다.`,
            });
          },
        },
        {
          label: '수정',
          action: 'modify',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `이미지 "${rowData.imageName}" 수정 팝업을 엽니다.`,
            });
          },
        },
        {
          label: '복사',
          action: 'copy',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `이미지 "${rowData.imageName}" 복사가 완료되었습니다.`,
            });
          },
        },
        {
          label: '삭제',
          action: 'delete',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `이미지 "${rowData.imageName}" 삭제 화면으로 이동합니다.`,
            });
          },
        },
      ],
      isActive: () => true, // 모든 이미지에 대해 활성화
    }),
    []
  );

  const [activeTab, setActiveTab] = useState('ideTab1');

  // 탭 옵션 정의
  const tabOptions = [
    { id: 'ideTab1', label: '이미지 관리' },
    { id: 'ideTab2', label: 'DW 계정 관리' },
  ];

  // 샘플 데이터
  const rowData = [
    {
      id: '1',
      toolName: 'Jupyter Notebook',
      imageName: 'jupyter-notebook-v1.0',
      description: 'Python 기반 데이터 분석 및 시각화를 위한 IDE 이미지',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
    },
    {
      id: '2',
      toolName: 'VS Code',
      imageName: 'vscode-dev-v2.1',
      description: '범용 개발 환경을 위한 VS Code IDE 이미지',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
    },
    {
      id: '3',
      toolName: 'Jupyter Notebook',
      imageName: 'jupyter-ml-v3.2',
      description: '머신러닝 라이브러리가 포함된 Jupyter 이미지',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
    },
    {
      id: '4',
      toolName: 'VS Code',
      imageName: 'vscode-web-v1.5',
      description: '웹 개발 환경을 위한 VS Code IDE 이미지',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
    },
    {
      id: '5',
      toolName: 'Jupyter Notebook',
      imageName: 'jupyter-basic-v1.0',
      description: '기본 Python 환경이 포함된 Jupyter 이미지',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
    },
    {
      id: '6',
      toolName: 'VS Code',
      imageName: 'vscode-full-v4.0',
      description: '전체 개발 도구가 포함된 VS Code IDE 이미지',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
    },
    {
      id: '7',
      toolName: 'Jupyter Notebook',
      imageName: 'jupyter-advanced-v2.3',
      description: '고급 데이터 분석 도구가 포함된 Jupyter 이미지',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
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
        headerName: '도구명',
        field: 'toolName' as any,
        width: 272,
        cellStyle: {
          paddingLeft: '16px',
        },
        sortable: false,
      },
      {
        headerName: '이미지명',
        field: 'imageName' as any,
        width: 272,
        cellStyle: {
          paddingLeft: '16px',
        },
        sortable: false,
      },
      {
        headerName: '설명',
        field: 'description' as any,
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
        sortable: false,
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
        field: 'createdDate',
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate' as any,
        width: 180,
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
      initialMenu={{ id: 'agent', label: '에이전트' }}
      initialSubMenu={{
        id: 'agent-tools',
        label: '에이전트의 도구',
        icon: 'ico-lnb-menu-20-agent-tools',
      }}
    >
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='IDE 관리'
          description={['IDE 환경에서 사용할 개발 환경 이미지를 등록 및 관리하며, 포탈에 등록된 DW 계정을 확인할 수 있습니다.']}
          actions={
            <>
              {/* [251222_퍼블수정]: btn-text-14-semibold-point > btn-text-18-semibold-point  (클래스명 변경 : 폰트 크기 수정함) */}
              <UIButton2 className='btn-text-18-semibold-point' leftIcon={{ className: 'ic-system-24-outline-blue-setting', children: '' }}>
                환경 설정
              </UIButton2>
            </>
          }
        />

        {/* 페이지 바디 */}
        <UIPageBody>
          {/* 탭 영역 */}
          <UIArticle className='article-tabs'>
            <div className='flex'>
              <UITabs items={tabOptions} activeId={activeTab} onChange={setActiveTab} size='large' />
            </div>
          </UIArticle>

          {/* 검색 영역 */}
          <UIArticle className='article-filter'>
            <UIBox className='box-filter'>
              <UIGroup gap={40} direction='row'>
                <div style={{ width: 'calc(100% - 168px)' }}>
                  <table className='tbl_type_b'>
                    <tbody>
                      <tr>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            검색
                          </UITypography>
                        </th>
                        <td>
                          <div>
                            <UIInput.Search
                              value={searchValue}
                              placeholder='이미지명, 설명 입력'
                              onChange={e => {
                                setSearchValue(e.target.value);
                              }}
                            />
                          </div>
                        </td>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            도구명
                          </UITypography>
                        </th>
                        <td>
                          <div>
                            <UIDropdown
                              value={searchValues.category}
                              placeholder='전체'
                              options={[
                                { value: 'val01', label: '전체' },
                                { value: 'val02', label: 'Jupyter Notebook' },
                                { value: 'val03', label: 'VS Code' },
                              ]}
                              isOpen={dropdownStates.category}
                              onClick={() => handleDropdownToggle('category')}
                              onSelect={value => handleDropdownSelect('category', value)}
                            />
                          </div>
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
                <div className='w-full'>
                  <UIUnitGroup gap={16} direction='column'>
                    <div className='flex justify-between w-full items-center'>
                      <div className='flex-shrink-0'>
                        <div style={{ width: '168px', paddingRight: '8px' }}>
                          <UIDataCnt count={rowData.length} prefix='총' unit='건' />
                        </div>
                      </div>
                      <div className='flex items-center gap-2'>
                        <UIButton2 className='btn-tertiary-outline'>이미지 등록</UIButton2>
                        <div style={{ width: '160px', flexShrink: 0 }}>
                          <UIDropdown
                            value={String(value)}
                            disabled={false}
                            options={[
                              { value: '1', label: '12개씩 보기' },
                              { value: '2', label: '36개씩 보기' },
                              { value: '3', label: '60개씩 보기' },
                            ]}
                            onSelect={(value: string) => {
                              setValue(value);
                            }}
                            onClick={() => {}}
                            height={40}
                            variant='dataGroup'
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
                  moreMenuConfig={moreMenuConfig}
                  onClickRow={(_params: any) => {}}
                  onCheck={(_selectedIds: any[]) => {}}
                />
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination currentPage={1} totalPages={10} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
