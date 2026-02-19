import type { ApexOptions } from 'apexcharts';
import { useMemo } from 'react';
import ReactApexChart from 'react-apexcharts';
import { UITypography } from '../../../atoms/UITypography/component';
import { UIProgressBar } from '@/components/UI/molecules';
import { UIEmptyChart } from '../UIEmptyChart/component';

const Full = ({
  label,
  value,
  total,
  ratio,
  dateRange,
  successValue,
  failValue,
}: {
  label: string;
  value: number;
  total: number;
  ratio?: number;
  dateRange?: string;
  successValue?: number;
  failValue?: number;
}) => {
  // 데이터 확인: total이 0이면 데이터가 없는 상태
  const hasData = total !== 0 && total !== null && total !== undefined;

  const innerRatio = useMemo(() => {
    if (ratio) return ratio;
    if (!hasData) return 0;
    return Number(((value / total) * 100).toFixed(2));
  }, [value, total, hasData]);

  const state = useMemo(() => {
    return {
      series: [innerRatio],
      options: {
        chart: {
          type: 'radialBar',
          height: 380,
          offsetY: -40,
          offsetX: -60,
        },
        plotOptions: {
          radialBar: {
            size: '95%',
            hollow: {
              size: '25%',
            },
            track: {
              strokeWidth: '100%',
              background: '#DCE2ED',
            },
            dataLabels: {
              show: false,
            },
          },
        },
        fill: {
          type: 'gradient',
          gradient: {
            shade: 'dark',
            type: 'horizontal',
            shadeIntensity: 0,
            gradientToColors: ['rgba(157, 193, 255, 1)'],
            inverseColors: false,
            opacityFrom: 0,
            opacityTo: 0.3,
            stops: [0, 100],
            colorStops: [
              {
                offset: 0,
                color: 'rgba(38, 112, 255, 1)',
                opacity: 0.9,
              },
              {
                offset: 100,
                color: 'rgba(157, 193, 255, 1)',
                opacity: 1,
              },
            ],
          },
        },
        labels: [label],
      } as ApexOptions,
    };
  }, [innerRatio, label]);

  // 데이터가 없으면 UIEmptyChart 표시
  if (!hasData) {
    return <UIEmptyChart title={label} dateRange={dateRange} minHeight='258px' />;
  }

  return (
    <div className='chart-item'>
      <div className='chart-header mb-4 flex'>
        <div className='flex items-center gap-4'>
          <div className='chart-title'>
            <UITypography variant='title-3' className='secondary-neutral-700 text-sb'>
              {label}
            </UITypography>
          </div>
          <div className='chart-legend flex-1'>
            <ul>
              <li className='legend-item'>
                <span className='legend-marker legend-marker-blue'></span>
                <UITypography variant='body-1' className='secondary-neutral-600'>
                  성공
                </UITypography>
              </li>
              <li className='legend-item'>
                <span className='legend-marker legend-marker-gray'></span>
                <UITypography variant='body-1' className='secondary-neutral-600'>
                  실패
                </UITypography>
              </li>
            </ul>
          </div>
        </div>
        {dateRange && <div className='text-body-2 ml-auto text-[#8B95A9]'>{dateRange}</div>}
      </div>
      <div className='w-full relative flex items-center h-[220px]'>
        <div className='relative h-full' style={{ minHeight: 'auto' }}>
          <ReactApexChart options={state.options} series={state.series} type='radialBar' height={380} />
          {/* 원형 그래프 중앙 텍스트 */}
          <div className='circle-graph'>
            <div className='text-center'>
              <UITypography variant='body-2' className='text-center-tit text-sb'>
                성공
              </UITypography>
              <UITypography variant='body-2' className='text-center-percent text-sb'>
                {((value / total) * 100).toFixed(2)}%
              </UITypography>
            </div>
          </div>
        </div>
        <div className='w-[360px] absolute right-0 top-9'>
          <div className='flex flex-col flex-1 gap-4'>
            <UIProgressBar label='성공' value={successValue ?? 75} total={100} color='#2670FF' textColor='#005DF9' />
            <UIProgressBar label='실패' value={failValue ?? 25} total={100} color='#DCE2ED' textColor='#8B95A9' />
          </div>
        </div>
      </div>
    </div>
  );
};

const Half = ({
  type,
  value,
  total,
  ratio,
  showLabel = true,
  usedLabel = '사용된 자원',
  availableLabel = '사용가능한 자원',
}: {
  type: 'CPU' | 'Memory' |  'MemoryMB' | 'GPU';
  value: number;
  total: number;
  ratio?: number;
  showLabel?: boolean;
  usedLabel?: string;
  availableLabel?: string;
}) => {
  // ratio 내부 처리
  // 소수점 두자리
  const innerRatio = useMemo(() => {
    if (ratio) return ratio;
    if (value === 0) return 0;
    return Number(((value / total) * 100).toFixed(2));
  }, [value, total]);

  // color
  const color = {
    CPU: '#368DED',
    Memory: '#37D8D0',
    MemoryMB: '#37D8D0',
    GPU: '#8166D2',
  };

  // type에 따른 표시 라벨
  const displayLabel = {
    CPU: 'CPU(Core)',
    Memory: 'Memory(GiB)',
    MemoryMB: 'Memory(MiB)',
    GPU: 'GPU(fGPU)',
  };

  const state = useMemo(() => {
    return {
      series: [innerRatio],
      options: {
        chart: {
          type: 'radialBar',
          width: 260,
          height: 338,
          offsetY: -40,
          offsetX: -20,
        },
        plotOptions: {
          radialBar: {
            startAngle: -90,
            endAngle: 90,
            track: {
              strokeWidth: '100%',
            },
            dataLabels: {
              name: {
                show: false,
              },
              value: {
                show: false,
              },
            },
          },
        },
        fill: {
          colors: [color[type]],
        },
      } as ApexOptions,
    };
  }, [innerRatio, color]);

  return (
    <div className='relative w-[260px] h-[150px]'>
      <div className='graph-cont w-full h-full'>
        <ReactApexChart options={state.options} series={state.series} type='radialBar' width={298} height={320} />
        <div className='graph-cont-data'>
          <div className='flex flex-col items-center'>
            <UITypography variant='title-3' className='text-title-3' style={{ color: color[type] }}>
              {value}
            </UITypography>
            <UITypography variant='title-4' className='text-title-4 text-neutral-400'>
              / {total}
            </UITypography>
          </div>
        </div>
      </div>
      <div className='graph-legend px-6.5 pr-6'>
        <div className='flex items-center gap-2 '>
          <div className={`w-1 h-6 bg-[${color[type]}]`} style={{ backgroundColor: color[type] }}></div>
          <span className='text-title-4 text-[#576072]'>{displayLabel[type]}</span>
        </div>
        <span className='text-title-4 text-[#576072]'>{innerRatio}%</span>
      </div>
      {showLabel && (
        <div className='graph-label mt-2 px-6.5 pr-6'>
          <ul>
            <li>
              <span style={{ backgroundColor: color[type] }}></span>{usedLabel}
            </li>
            <li>
              <span className='dot-resources'></span>{availableLabel}
            </li>
            {/* 사용가능한 자원은 dot color 고정 */}
          </ul>
        </div>
      )}
    </div>
  );
};

export const UICircleChart = {
  Full,
  Half,
};
