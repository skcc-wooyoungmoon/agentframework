import type { ApexOptions } from 'apexcharts';
import ReactApexChart from 'react-apexcharts';
import { useEffect, useRef, useState } from 'react';
import { UIChartContainer } from '../UIChartContainer';
import { UIEmptyChart } from '../UIEmptyChart/component';
import type { UILineChartProps } from './types';

export const UILineChart = ({ type = 'smooth', options, series = [], width, height = 350, noData = false, customTitle, ...containerProps }: UILineChartProps) => {
  // 데이터 확인: series가 존재하고 데이터가 있는지 체크
  const hasData = !noData && series.length > 0 && series.some((s: any) => Array.isArray(s?.data) && s.data.length > 0);

  const [chartWidth, setChartWidth] = useState<string | number>('100%');
  const chartContainerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    // width prop이 명시적으로 전달되면 자동 width 계산을 건너뜀
    if (width !== undefined) {
      setChartWidth('100%');
      return;
    }

    const updateChartWidth = () => {
      // LNB 토글 버튼의 상태 확인
      const lnbButton = document.querySelector('.ic-lnb-24-toggle-close, .ic-lnb-24-toggle-open');

      if (lnbButton?.classList.contains('ic-lnb-24-toggle-close')) {
        // LNB 닫혀있음
        //setChartWidth(1480); 다른 페이지 공통으로 컬럼 2개일경우 가로사이즈 이슈 주석처리.. 251209
      } else {
        // LNB 열려있음
        setChartWidth('100%');
      }
    };

    updateChartWidth();

    // 버튼 상태 변화 감지를 위해 주기적으로 확인
    const interval = setInterval(updateChartWidth, 100);

    // 윈도우 리사이즈 이벤트도 함께 처리
    window.addEventListener('resize', updateChartWidth);

    return () => {
      clearInterval(interval);
      window.removeEventListener('resize', updateChartWidth);
    };
  }, [width]);

  const innerOptions = {
    ...options,
    chart: {
      ...options.chart,
      toolbar: {
        show: false,
      },
    },
    tooltip: {
      // TODO : tooltip 스타일 정의 필요
      ...options.tooltip,
    },
    dataLabels: {
      ...options.dataLabels,
      enabled: false,
    },
    stroke: {
      ...options.stroke,
      curve: type,
    },
  } as ApexOptions;

  // 데이터가 없으면 UIEmptyChart 표시
  if (!hasData) {
    return <UIEmptyChart title={customTitle || containerProps.label} minHeight={`${height}px`} message='조회된 데이터가 없습니다.' />;
  }

  // width prop이 전달되면 부모 div도 100%로 설정하여 반응형으로 동작
  const containerWidth = width !== undefined ? '100%' : chartWidth;

  return (
    <UIChartContainer {...containerProps}>
      <div id='chart' ref={chartContainerRef} style={{ width: containerWidth, maxWidth: '100%' }}>
        <ReactApexChart options={innerOptions} series={series} type='area' width={width} height={height} />
      </div>
      <div id='html-dist'></div>
    </UIChartContainer>
  );
};
