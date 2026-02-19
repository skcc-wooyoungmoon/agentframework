import type { ApexOptions } from 'apexcharts';
import { useMemo } from 'react';
import ReactApexChart from 'react-apexcharts';
import { UIChartContainer } from '../UIChartContainer';
import { UIEmptyChart } from '../UIEmptyChart';
import type { UIBarChartProps } from './types';

export const UIBarChart = ({ options, series = [], height = 350, ...containerProps }: UIBarChartProps) => {
  // 데이터 확인: series가 존재하고 데이터가 있는지 체크
  const hasData = series.length > 0 && series.some((s: any) => Array.isArray(s?.data) && s.data.length > 0);

  const state = useMemo(
    () => ({
      series: series.length !== 0 ? [{ ...(series[0] as object), type: 'column' }] : ([] as ApexOptions['series']),
      options: {
        ...options,
        chart: {
          ...options.chart,
          width: 'calc(100% + 10px)',
          height: height,
          offsetX: -18,
          toolbar: {
            show: false,
          },
        },
        tooltip: {
          // TODO : tooltip 스타일 정의 필요
          ...options.tooltip,
        },
        plotOptions: {
          ...options.plotOptions,
          bar: {
            ...options.plotOptions?.bar,
            columnWidth: options.plotOptions?.bar?.columnWidth || '96%',
          },
        },
        dataLabels: {
          ...options.dataLabels,
          enabled: false,
        },
        legend: {
          ...options.legend,
          show: false,
        },
        xaxis: {
          ...options.xaxis,
          labels: {
            style: {
              colors: ['#37D8D0'],
              fontSize: '12px',
            },
          },
        },
        states: {
          active: {
            filter: {
              type: 'none',
            },
          },
          hover: {
            filter: {
              type: 'none',
            },
          },
        },
      } as ApexOptions,
    }),
    [series, options, height, hasData]
  );

  // 데이터가 없으면 UIEmptyChart 표시
  if (!hasData) {
    return <UIEmptyChart title={typeof containerProps.label === 'string' ? containerProps.label : '차트'} minHeight={`${height}px`} />;
  }

  return (
    <UIChartContainer {...containerProps}>
      <div id='chart'>
        <ReactApexChart options={state.options} series={state.series as ApexOptions['series']} type='bar' height={height} />
      </div>
      <div id='html-dist'></div>
    </UIChartContainer>
  );
};
