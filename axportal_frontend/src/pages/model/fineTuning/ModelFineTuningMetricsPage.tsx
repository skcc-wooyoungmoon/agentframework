import { UIButton2, UIDataCnt, UILabel, UIPagination, type UILabelIntent } from '@/components/UI';
import { UIProgress } from '@/components/UI/atoms/UIProgress/component';
import { UIArticle, UIPageBody, UIPageHeader } from '@/components/UI/molecules';
import { UILineChart } from '@/components/UI/molecules/chart';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { useFineTuningSelectModal } from '@/hooks/model/useFineTuningSelectModal.ts';
import { useGetFineTuningTrainingsByIds } from '@/services/model/fineTuning/modelFineTuning.service.ts';
import type { FineTuningTraining } from '@/services/model/fineTuning/types';
import { fineTuningSelectedIdsAtom, fineTuningSelectPopupAtom } from '@/stores/model/fineTuning/fineTuning.atoms.ts';
import type { ApexOptions } from 'apexcharts';
import { useAtom } from 'jotai';
import React, { useMemo, useState } from 'react';
import { FINE_TUNING_STATUS_MAP } from '@/constants/model/fineTuningStatus.constants';

export const ModelFineTuningMetricsPage = () => {
  const [selectedIds] = useAtom(fineTuningSelectedIdsAtom);
  const [selectedFineTuningsIds, setSelectedFineTuningsIds] = useAtom(fineTuningSelectPopupAtom);
  const [localSelectedIds, setLocalSelectedIds] = useState<string[]>(selectedIds);

  // localSelectedIds를 사용하여 개별 조회
  const fineTuningQueries = useGetFineTuningTrainingsByIds(
    localSelectedIds.map(id => ({
      id,
      isMetric: true,
    }))
  );

  const { openFineTuningSelectModal } = useFineTuningSelectModal({
    onConfirm: () => {
      setLocalSelectedIds(selectedFineTuningsIds);
    },
    onCancel: () => {
      setSelectedFineTuningsIds([]);
    },
  });

  const handleTuningRegister = async () => {
    await openFineTuningSelectModal();
  };

  // 개별 조회 결과 처리
  const fineTuningData = useMemo(() => {
    return fineTuningQueries.map(query => query.data).filter((item): item is FineTuningTraining => item !== undefined);
  }, [fineTuningQueries]);

  // 차트 데이터 준비
  const chartData = useMemo(() => {
    if (fineTuningData.length === 0) {
      return [];
    }

    // 모델별로 데이터 그룹화
    return fineTuningData.map(item => {
      // Step 1-10까지 배열 생성
      const fullData = Array.from({ length: 10 }, (_, index) => {
        const step = index + 1;
        const metric = item.metricDetails?.find(m => m.step === step);
        // return {
        //   x: step,
        //   y: metric ? metric.loss || 0 : 0,
        // };
        return metric?.loss || 0;
      });

      return {
        name: item.name,
        data: fullData,
      };
    });
  }, [fineTuningData]);

  const lineColors = ['#4A86FF', '#71FFD0', '#4AEAFF', '#FF9161', '#FFAB08', '#8742FF', '#F758FF', '#FC4661', '#005DF9', '#2B9E68'];

  // 그리드 컬럼 정의
  const columnDefs: any = useMemo(
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
          textAlign: 'center' as const,
          display: 'flex' as const,
          alignItems: 'center' as const,
          justifyContent: 'center' as const,
        },
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
        valueGetter: (params: any) => {
          return params.node.rowIndex + 1;
        },
      },
      {
        headerName: '모델명',
        field: 'name',
        width: 264,
        cellRenderer: React.memo((params: any) => {
          // colorIndex에 따라 색상 가져오기

          const colorIndex = params.node.rowIndex % lineColors.length;
          const color = lineColors[colorIndex];

          return (
            <div className='flex items-center gap-2'>
              <svg width='16' height='16' viewBox='0 0 16 16' fill='none' xmlns='http://www.w3.org/2000/svg'>
                <circle cx='8' cy='8' r='4' fill={color} />
              </svg>
              <span>{params.value}</span>
            </div>
          );
        }),
      },
      {
        headerName: '설명',
        field: 'description',
        flex: 1,
      },
      {
        headerName: '상태',
        field: 'status',
        width: 120,
        cellRenderer: React.memo((params: { value: string }) => {
          return (
            <UILabel variant='badge' intent={FINE_TUNING_STATUS_MAP[params.value as keyof typeof FINE_TUNING_STATUS_MAP]?.intent as UILabelIntent}>
              {FINE_TUNING_STATUS_MAP[params.value as keyof typeof FINE_TUNING_STATUS_MAP]?.label}
            </UILabel>
          );
        }),
      },
      {
        headerName: '진행률',
        field: 'progress',
        width: 487,
        cellRenderer: React.memo((params: any) => {
          const percentage = params.data.progress?.percentage || 0;

          return <UIProgress value={percentage} status='normal' showPercent={true} className='w-[100%]' />;
        }),
      },
    ],
    []
  );

  const lineChartState = useMemo(() => {
    return {
      series: chartData,
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
              {
                offset: 16.3,
                color: 'rgba(255, 171, 8, 0.1)',
                opacity: 1,
              },
              {
                offset: 110.4,
                color: 'rgba(255, 171, 8, 0)',
                opacity: 1,
              },
            ],
          },
        },
        // colors: ['#FC4661', '#FFAB08', '#56F2B9'],
        colors: lineColors,
      } as ApexOptions,
    };
  }, [chartData, lineColors]);

  return (
    <>
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
                    <UIDataCnt count={fineTuningData.length || 0} />
                  </div>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid type='default' rowData={fineTuningData} columnDefs={columnDefs} />
              </UIListContentBox.Body>
              <UIListContentBox.Footer className='ui-data-has-btn'>
                <UIButton2 className='btn-option-outlined' style={{ width: '40px' }} onClick={handleTuningRegister}>
                  추가
                </UIButton2>
                <UIPagination currentPage={1} totalPages={1} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>

          {/* 그래프 영역 */}
          <UIArticle>
            <div className='article-body'>
              {/* Chart 영역 구분 */}
              <div className='chart-container flex mt-4 w-full'>
                <UILineChart label='손실값' x='x:Step' y='y:Value' options={lineChartState.options} series={lineChartState.series} width='100%' />
              </div>
            </div>
          </UIArticle>
        </UIPageBody>
      </section>
    </>
  );
};
