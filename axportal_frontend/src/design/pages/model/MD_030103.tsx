import React, { useMemo, useState } from 'react';

import { UIButton2 } from '@/components/UI/atoms/UIButton2';
import { UIDataCnt } from '@/components/UI/atoms/UIDataCnt';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIProgress } from '@/components/UI/atoms/UIProgress/component';
import { UIArticle, UIPageBody } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer } from '@/components/UI/molecules/list/UIListContainer';
import { UIListContentBox } from '@/components/UI/molecules/list/UIListContentBox';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UILineChart } from '@/components/UI/molecules/chart';
import { DesignLayout } from '../../components/DesignLayout';
import type { ApexOptions } from 'apexcharts';
import { UILabel, UITypography } from '@/components/UI';

export const MD_030103 = () => {
  // 10가지 컬러 정의
  const lineColors = ['#4A86FF', '#71FFD0', '#4AEAFF', '#FF9161', '#FFAB08', '#8742FF', '#F758FF', '#FC4661', '#005DF9', '#2B9E68'];

  // 그리드 샘플 데이터
  const sampleData = [
    {
      id: '1',
      modelName: 'FT_TEST_20250605',
      description: '파인튜닝 모델 성능 개선을 위한 테스트 모델',
      status: '이용가능',
      progress: 100,
      colorIndex: 0,
    },
    {
      id: '2',
      modelName: 'FT_PRODUCTION_20250520',
      description: '상용 서비스에 적용할 프로덕션 모델',
      status: '이용가능',
      progress: 75,
      colorIndex: 1,
    },
    {
      id: '3',
      modelName: 'FT_EXPERIMENTAL_20250515',
      description: '실험적 기능 테스트를 위한 모델',
      status: '학습중',
      progress: 10,
      colorIndex: 2,
    },
    {
      id: '4',
      modelName: 'FT_OPTIMIZE_20250510',
      description: '성능 최적화를 위한 모델 튜닝',
      status: '이용가능',
      progress: 45,
      colorIndex: 3,
    },
    {
      id: '5',
      modelName: 'FT_BASELINE_20250501',
      description: '베이스라인 성능 비교를 위한 모델',
      status: '이용가능',
      progress: 100,
      colorIndex: 4,
    },
    {
      id: '6',
      modelName: 'FT_ADVANCED_20250425',
      description: '고급 기능 추가를 위한 파인튜닝 모델',
      status: '이용가능',
      progress: 60,
      colorIndex: 5,
    },
    {
      id: '7',
      modelName: 'FT_CUSTOM_20250420',
      description: '커스텀 요구사항 반영 모델',
      status: '이용가능',
      progress: 85,
      colorIndex: 6,
    },
    {
      id: '8',
      modelName: 'FT_RESEARCH_20250415',
      description: '연구 개발용 실험 모델',
      status: '학습중',
      progress: 30,
      colorIndex: 7,
    },
    {
      id: '9',
      modelName: 'FT_STABLE_20250410',
      description: '안정화 버전 배포용 모델',
      status: '이용가능',
      progress: 95,
      colorIndex: 8,
    },
    {
      id: '10',
      modelName: 'FT_BETA_20250405',
      description: '베타 테스트용 모델',
      status: '이용가능',
      progress: 55,
      colorIndex: 9,
    },
  ];

  // 그리드 컬럼 정의
  const columnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id',
        width: 56,
        minWidth: 56,
        maxWidth: 56,
        cellClass: 'text-center',
        headerClass: 'text-center',
        cellStyle: {
          textAlign: 'center' as const,
          display: 'flex' as const,
          alignItems: 'center' as const,
          justifyContent: 'center' as const,
        },
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '모델명',
        field: 'modelName',
        width: 264,
        cellRenderer: React.memo((params: any) => {
          // colorIndex에 따라 색상 가져오기
          const color = lineColors[params.data.colorIndex];

          return (
            <div className='flex items-center gap-2'>
              <svg width='16' height='16' viewBox='0 0 16 16' fill='none' xmlns='http://www.w3.org/2000/svg'>
                <circle cx='8' cy='8' r='4' fill={color} />
              </svg>
              <UITypography variant='body-2' className='secondary-neutral-600'>
                {params.value}
              </UITypography>
            </div>
          );
        }),
      },
      // [251113_퍼블수정] 그리드 컬럼 속성 수정
      {
        headerName: '설명',
        field: 'description',
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
        headerName: '상태',
        field: 'status',
        width: 120,
        cellRenderer: React.memo((params: any) => {
          const getStatusIntent = (status: string) => {
            switch (status) {
              case '이용가능':
                return 'complete';
              case '학습중':
                return 'progress';
              case '이용중지':
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
        headerName: '진행률',
        field: 'progress',
        width: 487,
        cellRenderer: React.memo((params: { value: number }) => {
          return <UIProgress value={params.value} status='normal' showPercent={true} className='w-[100%]' />;
        }),
      },
    ],
    []
  );

  const [lineChartState3] = useState({
    series: [
      // 개발자 영역 : y축
      {
        name: 'name1',
        data: [31.0, 40.0, 28.0, 51.0, 42.0, 35.0, 48.0, 55.0, 38.0, 45.0, 32.0, 28.0],
      },
      {
        name: 'name2',
        data: [20.0, 35.0, 45.0, 38.0, 50.0, 42.0, 38.0, 44.0, 52.0, 36.0, 40.0, 33.0],
      },
      {
        name: 'name3',
        data: [15.0, 25.0, 35.0, 42.0, 30.0, 38.0, 45.0, 40.0, 35.0, 42.0, 38.0, 30.0],
      },
      {
        name: 'name4',
        data: [25.0, 30.0, 40.0, 35.0, 45.0, 38.0, 42.0, 48.0, 40.0, 38.0, 35.0, 32.0],
      },
      {
        name: 'name5',
        data: [18.0, 28.0, 38.0, 45.0, 35.0, 40.0, 50.0, 45.0, 38.0, 40.0, 35.0, 28.0],
      },
      {
        name: 'name6',
        data: [22.0, 32.0, 42.0, 38.0, 48.0, 42.0, 45.0, 52.0, 42.0, 45.0, 40.0, 35.0],
      },
      {
        name: 'name7',
        data: [28.0, 38.0, 32.0, 42.0, 38.0, 45.0, 40.0, 48.0, 45.0, 42.0, 38.0, 33.0],
      },
      {
        name: 'name8',
        data: [32.0, 42.0, 38.0, 48.0, 42.0, 38.0, 45.0, 50.0, 42.0, 38.0, 42.0, 38.0],
      },
      {
        name: 'name9',
        data: [26.0, 36.0, 42.0, 40.0, 45.0, 40.0, 42.0, 46.0, 40.0, 42.0, 38.0, 35.0],
      },
      {
        name: 'name10',
        data: [30.0, 40.0, 35.0, 45.0, 40.0, 42.0, 48.0, 52.0, 45.0, 42.0, 40.0, 38.0],
      },
    ],
    options: {
      chart: {
        width: '100%',
      },
      xaxis: {
        // 개발자 영역 : X축
        type: 'category',
        categories: ['Step 1', 'Step 2', 'Step 3', 'Step 4', 'Step 5', 'Step 6', 'Step 7', 'Step 8', 'Step 9', 'Step 10', 'Step 11', 'Step 12'],
      },
      stroke: {
        width: 2,
        curve: 'smooth',
      },
      legend: {
        show: false,
      },
      // 퍼블리셔 영역
      fill: {
        type: 'gradient',
        gradient: {
          type: 'vertical',
          shadeIntensity: 0,
          colorStops: [
            [
              // 라인 1: #4A86FF
              { offset: 16.3, color: 'rgba(74, 134, 255, 0.1)', opacity: 1 },
              { offset: 110.4, color: 'rgba(74, 134, 255, 0)', opacity: 1 },
            ],
            [
              // 라인 2: #71FFD0
              { offset: 16.3, color: 'rgba(113, 255, 208, 0.1)', opacity: 1 },
              { offset: 110.4, color: 'rgba(113, 255, 208, 0)', opacity: 1 },
            ],
            [
              // 라인 3: #4AEAFF
              { offset: 16.3, color: 'rgba(74, 234, 255, 0.1)', opacity: 1 },
              { offset: 110.4, color: 'rgba(74, 234, 255, 0)', opacity: 1 },
            ],
            [
              // 라인 4: #FF9161
              { offset: 16.3, color: 'rgba(255, 145, 97, 0.1)', opacity: 1 },
              { offset: 110.4, color: 'rgba(255, 145, 97, 0)', opacity: 1 },
            ],
            [
              // 라인 5: #FFAB08
              { offset: 16.3, color: 'rgba(255, 171, 8, 0.1)', opacity: 1 },
              { offset: 110.4, color: 'rgba(255, 171, 8, 0)', opacity: 1 },
            ],
            [
              // 라인 6: #8742FF
              { offset: 16.3, color: 'rgba(135, 66, 255, 0.1)', opacity: 1 },
              { offset: 110.4, color: 'rgba(135, 66, 255, 0)', opacity: 1 },
            ],
            [
              // 라인 7: #F758FF
              { offset: 16.3, color: 'rgba(247, 88, 255, 0.1)', opacity: 1 },
              { offset: 110.4, color: 'rgba(247, 88, 255, 0)', opacity: 1 },
            ],
            [
              // 라인 8: #FC4661
              { offset: 16.3, color: 'rgba(252, 70, 97, 0.1)', opacity: 1 },
              { offset: 110.4, color: 'rgba(252, 70, 97, 0)', opacity: 1 },
            ],
            [
              // 라인 9: #005DF9
              { offset: 16.3, color: 'rgba(0, 93, 249, 0.1)', opacity: 1 },
              { offset: 110.4, color: 'rgba(0, 93, 249, 0)', opacity: 1 },
            ],
            [
              // 라인 10: #2B9E68
              { offset: 16.3, color: 'rgba(43, 158, 104, 0.1)', opacity: 1 },
              { offset: 110.4, color: 'rgba(43, 158, 104, 0)', opacity: 1 },
            ],
          ],
        },
      },
      colors: ['#4A86FF', '#71FFD0', '#4AEAFF', '#FF9161', '#FFAB08', '#8742FF', '#F758FF', '#FC4661', '#005DF9', '#2B9E68'],
    } as ApexOptions,
  });

  return (
    <DesignLayout
      initialMenu={{ id: 'model', label: '모델' }}
      initialSubMenu={{
        id: 'fine-tuning',
        label: '파인튜닝',
        icon: 'ico-lnb-menu-20-fine-tuning',
      }}
    >
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader title='매트릭뷰' description='' />

        {/* 페이지 바디 */}
        <UIPageBody>
          {/* 그리드 영역 */}
          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='flex-shrink-0'>
                  <div style={{ width: '168px', paddingRight: '8px' }}>
                    <UIDataCnt count={sampleData.length} prefix='총' unit='건' />
                  </div>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid type='default' rowData={sampleData} columnDefs={columnDefs} />
              </UIListContentBox.Body>
              <UIListContentBox.Footer className='ui-data-has-btn'>
                <UIButton2 className='btn-option-outlined' style={{ width: '40px' }}>
                  추가
                </UIButton2>
                <UIPagination currentPage={1} totalPages={5} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>

          {/* 그래프 영역 */}
          <UIArticle>
            <div className='article-body'>
              {/* Chart 영역 구분 */}
              <div className='chart-container flex mt-4 w-full'>
                <UILineChart label='손실값' x='x:Step' y='y:Value' options={lineChartState3.options} series={lineChartState3.series} width='100%' />
              </div>
            </div>
          </UIArticle>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
