import React, { useState } from 'react';

import { UITabs } from '../../../components/UI/organisms';
import { DesignLayout } from '../../components/DesignLayout';
import { UIDataCnt, UIToggle } from '@/components/UI';

import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UITypography } from '@/components/UI/atoms';
import { UICircleChart } from '@/components/UI/molecules/chart';
import { UIGroup, UIInput, UIDropdown } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIPagination } from '@/components/UI/atoms/UIPagination';

export const AD_040101 = () => {
  const [activeTab, setActiveTab] = useState('Tab1');

  // 검색 상태
  const [searchValue, setSearchValue] = useState('');
  const [searchValues, setSearchValues] = useState({
    searchType: '전체',
  });

  // 드롭다운 상태
  const [dropdownStates, setDropdownStates] = useState({
    searchType: false,
  });

  const handleDropdownToggle = (key: string) => {
    setDropdownStates(prev => ({
      ...prev,
      [key]: !prev[key as keyof typeof prev],
    }));
  };

  const handleDropdownSelect = (key: string, value: string) => {
    setSearchValues(prev => ({ ...prev, [key]: value }));
    setDropdownStates(prev => ({ ...prev, [key]: false }));
  };

  // 탭 옵션 정의
  const tabOptions = [
    { id: 'Tab1', label: '포탈 자원 현황' },
    { id: 'Tab2', label: 'GPU 노드별 자원 현황' },
    { id: 'Tab3', label: '솔루션 자원 현황' },
  ];

  // 샘플 데이터
  const projectData = [
    {
      no: '1',
      userName: '김신한',
      employeeNumber: '23012345',
      toolName: 'VSCodeLab',
      imageName: 'vscode-lab:latest',
      accountId: 'user001',
      cpuAllocation: '4',
      memoryAllocation: '12',
      ideExpireDate: '2025.08.24 18:23:43',
      status: '실행중',
    },
    {
      no: '2',
      userName: '오주용',
      employeeNumber: '23012346',
      toolName: 'JupyterLab',
      imageName: 'jupyter-lab:v3.5',
      accountId: 'user002',
      cpuAllocation: '8',
      memoryAllocation: '16',
      ideExpireDate: '2025.09.15 14:30:00',
      status: '실행중',
    },
    {
      no: '3',
      userName: '이신한',
      employeeNumber: '23012347',
      toolName: 'RStudio',
      imageName: 'rstudio:2024.1',
      accountId: 'user003',
      cpuAllocation: '2',
      memoryAllocation: '8',
      ideExpireDate: '2025.07.20 10:15:30',
      status: '중지',
    },
    {
      no: '4',
      userName: '박신한',
      employeeNumber: '23012348',
      toolName: 'VSCodeLab',
      imageName: 'vscode-lab:latest',
      accountId: 'user004',
      cpuAllocation: '4',
      memoryAllocation: '12',
      ideExpireDate: '2025.08.30 09:45:12',
      status: '실행중',
    },
    {
      no: '5',
      userName: '최신한',
      employeeNumber: '23012349',
      toolName: 'JupyterLab',
      imageName: 'jupyter-lab:v3.5',
      accountId: 'user005',
      cpuAllocation: '6',
      memoryAllocation: '24',
      ideExpireDate: '2025.10.01 16:20:45',
      status: '실행중',
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
        headerName: '사용자명',
        field: 'userName' as const,
        width: 160,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '행번',
        field: 'employeeNumber' as const,
        width: 160,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '도구명',
        field: 'toolName' as const,
        width: 200,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '이미지명',
        field: 'imageName' as const,
        minWidth: 200,
        flex: 1,
        showTooltip: true,
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
      {
        headerName: '계정 ID',
        field: 'accountId' as const,
        width: 170,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: 'CPU 할당량 (Core)',
        field: 'cpuAllocation' as const,
        width: 160,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: 'Memory 할당량 (GiB)',
        field: 'memoryAllocation' as const,
        width: 160,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: 'IDE 만료 일시',
        field: 'ideExpireDate' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '상태',
        field: 'status' as const,
        width: 80,
        cellStyle: {
          paddingLeft: '16px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        },
        cellRenderer: React.memo((params: any) => {
          const isRunning = params.value === '실행중';
          return (
            <UIToggle
              checked={isRunning}
              onChange={(checked: boolean) => {
                console.log('토글 상태 변경:', params.data.no, checked ? '실행중' : '중지');
              }}
              size='small'
            />
          );
        }),
      },
    ],
    [projectData]
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
        <UIPageHeader title='자원 관리' description='포탈, GPU 노드, 솔루션 네임스페이스별 자원 할당량과 사용률을 한눈에 확인할 수 있습니다.' />

        {/* 페이지 바디 */}
        <UIPageBody>
          {/* 탭 영역 */}
          <UIArticle className='article-tabs'>
            <div className='flex'>
              <UITabs items={tabOptions} activeId={activeTab} onChange={setActiveTab} size='large' />
            </div>
          </UIArticle>

          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                배포 자원 현황
              </UITypography>
            </div>
            <div className='article-body'>
              {/* Chart 영역 구분 */}
              <div className='chart-container mt-4'>
                <div className='chart-item flex-1 h-[326px]'>
                  <div className='chart-header mb-4'>
                    <UITypography variant='title-3' className='secondary-neutral-700 text-sb'>
                      에이전트 배포
                    </UITypography>
                  </div>
                  <div className='flex chart-graph h-[210px] gap-x-10 justify-center items-center'>
                    <div className='w-[240px] flex items-center justify-center'>
                      <UICircleChart.Half type='CPU' value={50} total={70.2} usedLabel='할당량' availableLabel='여유량' />
                    </div>
                    <div className='w-[240px] flex items-center justify-center'>
                      <UICircleChart.Half type='Memory' value={20} total={100} usedLabel='할당량' availableLabel='여유량' />
                    </div>
                  </div>
                </div>
                <div className='chart-item flex-1'>
                  <div className='chart-header mb-4'>
                    <UITypography variant='title-3' className='secondary-neutral-700 text-sb'>
                      모델 배포
                    </UITypography>
                  </div>
                  {/* [참고] gap:20 으로 세팅 : 그래프 svg 타입으로 width 크기를 맞추기위해서 20으로 작게 지정  */}
                  <div className='flex chart-graph h-[210px] gap-x-5 justify-center'>
                    <div className='w-[240px] flex items-center justify-center'>
                      <UICircleChart.Half type='CPU' value={50} total={50.25} usedLabel='할당량' availableLabel='여유량' />
                    </div>
                    <div className='w-[240px] flex items-center justify-center'>
                      <UICircleChart.Half type='Memory' value={20} total={100} usedLabel='할당량' availableLabel='여유량' />
                    </div>
                    <div className='w-[240px] flex items-center justify-center'>
                      <UICircleChart.Half type='GPU' value={128.72} total={20} usedLabel='할당량' availableLabel='여유량' />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </UIArticle>

          <UIArticle className='article-grid'>
            <UITypography variant='title-4' className='secondary-neutral-900'>
              IDE 자원 현황
            </UITypography>
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='flex justify-between items-center w-full'>
                  <div className='flex-shrink-0'>
                    <div style={{ width: '168px', paddingRight: '8px' }}>
                      <UIDataCnt count={projectData.length} prefix='총' unit='건' />
                    </div>
                  </div>
                  <div>
                    <UIGroup direction='row' gap={8}>
                      <div style={{ width: '180px', flexShrink: 0 }}>
                        <UIDropdown
                          value={searchValues.searchType}
                          placeholder='조회 조건 선택'
                          options={[
                            { value: 'val1', label: '전체' },
                            { value: 'val2', label: '사용자명1' },
                            { value: 'val3', label: '사용자명2' },
                          ]}
                          isOpen={dropdownStates.searchType}
                          height={40}
                          onClick={() => handleDropdownToggle('searchType')}
                          onSelect={value => handleDropdownSelect('searchType', value)}
                        />
                      </div>
                      <div className='w-[360px] h-[40px]'>
                        <UIInput.Search
                          value={searchValue}
                          placeholder='검색어 입력'
                          onChange={e => {
                            setSearchValue(e.target.value);
                          }}
                        />
                      </div>
                    </UIGroup>
                  </div>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid type='default' rowData={projectData} columnDefs={columnDefs} onClickRow={(_params: any) => {}} onCheck={(_selectedIds: any[]) => {}} />
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination currentPage={1} totalPages={3} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
