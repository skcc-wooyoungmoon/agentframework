import type { ApexOptions } from 'apexcharts';
import { useMemo } from 'react';
import ReactApexChart from 'react-apexcharts';
import { UITypography } from '../../../atoms/UITypography/component';
import { UIEmptyChart } from '../UIEmptyChart/component';
import type { UIHorizontalBarChartProps } from './types';

export const UIHorizontalBarChart = ({ options, series = [], height = 350, dateRange, ...containerProps }: UIHorizontalBarChartProps) => {
  // 데이터 확인: series가 존재하고 첫 번째 시리즈에 data가 있는지 체크
  const hasData = series.length > 0 && Array.isArray((series[0] as any)?.data) && (series[0] as any).data.length > 0;

  const state = useMemo(
    () => ({
      series: hasData ? [{ ...(series[0] as object), type: 'bar' }] : ([] as ApexOptions['series']),
      options: {
        ...options,
        chart: {
          ...options.chart,
          type: 'bar',
          toolbar: {
            show: false,
          },
          margins: {
            left: 100, // [251111_퍼블수정] Y축 텍스트 영역 너비 제한
          },
        },
        plotOptions: {
          ...options.plotOptions,
          bar: {
            ...options.plotOptions?.bar,
            horizontal: true,
            barHeight: options.plotOptions?.bar?.barHeight || '50%',
            borderRadius: options.plotOptions?.bar?.borderRadius || 4,
            borderRadiusApplication: 'end',
          },
        },
        tooltip: {
          ...options.tooltip,
        },
        dataLabels: {
          ...options.dataLabels,
          enabled: options.dataLabels?.enabled !== undefined ? options.dataLabels.enabled : false,
          textAnchor: options.dataLabels?.textAnchor || 'start',
          offsetX: options.dataLabels?.offsetX !== undefined ? options.dataLabels.offsetX : 0,
          style: {
            fontSize: options.dataLabels?.style?.fontSize || '14px',
            colors: options.dataLabels?.style?.colors || ['#576072'],
            fontWeight: options.dataLabels?.style?.fontWeight || 400,
          },
        },
        legend: {
          ...options.legend,
          show: false,
        },
        xaxis: {
          ...options.xaxis,
          labels: {
            ...options.xaxis?.labels,
            style: {
              colors: ['#8B95A9'],
              fontSize: '12px',
            },
          },
        },
        yaxis: {
          ...options.yaxis,
          labels: {
            maxWidth: 170, // [251111_퍼블수정] Y축 라벨 최대 너비 제한
            offsetX: -2, // [251111_퍼블수정] Y축 라벨 위치 조정
            style: {
              colors: ['#373E4D'],
              fontSize: '12px',
            },
          },
        },
      } as ApexOptions,
    }),
    [series, options, height, hasData]
  );

  // 데이터가 없으면 UIEmptyChart 표시
  if (!hasData) {
    return <UIEmptyChart title={typeof containerProps.label === 'string' ? containerProps.label : '차트'} dateRange={dateRange} minHeight={`${height}px`} />;
  }

  return (
    <div className='chart-item'>
      <div className='chart-header mb-4 flex justify-between'>
        <div className='flex items-center gap-4'>
          <div className='chart-title'>
            {typeof containerProps.label === 'string' ? (
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                {containerProps.label}
              </UITypography>
            ) : (
              containerProps.label
            )}
          </div>
          <div className='chart-legend flex-1'>
            <ul>
              <li className='legend-item'>
                <span className='legend-marker legend-marker-sky'></span>
                <span className='text-body-2 legend-label'>이용자 수</span>
              </li>
            </ul>
          </div>
        </div>
        {dateRange && <div className='text-body-2 ml-auto text-[#8B95A9]'>{dateRange}</div>}
      </div>
      <div id='chart'>
        <ReactApexChart options={state.options} series={state.series as ApexOptions['series']} type='bar' height={height} />
      </div>
      <div id='html-dist'></div>
    </div>
  );
};
