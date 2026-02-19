import { UITypography } from '@/components/UI/atoms';
import { UIBarChart, UICircleChart, UILineChart, UIHorizontalBarChart } from '@/components/UI/molecules/chart';
import type { ApexOptions } from 'apexcharts';
import { Fragment, useState } from 'react';

export const UITestChartPage = () => {
  // smooth 차트
  const [lineChartState] = useState({
    series: [
      // 개발자 영역 : y축
      {
        name: 'name1',
        data: [31.0, 40.0, 28.0, 51.0, 42.0, 0.0, 0.0],
      },
      {
        name: 'series2',
        data: [11.0, 32.0, 45.4, 32.0, 34.0, 52.0, 41.0],
      },
      {
        name: 'series2',
        data: [11.0, 32.0, 45.4, 32.0, 34.0, 52.0, 41.0],
      },
    ],
    options: {
      xaxis: {
        // 개발자 영역 : X축
        type: 'category',
        categories: ['step1', 'step2', 'step3', 'step4', 'step5', 'step6', 'step7', 'step8', 'step9', 'step10'],
      },
      // 퍼블리셔 영역
      colors: ['#4A86FF', '#71FFD0', '#4AEAFF', '#FF9161', '#FF6161', '#8742FF', '#F758FF', '#FC4661', '#005DF9', '#2B9E68'],
    } as ApexOptions,
  });

  // smooth 차트
  const [lineChartState2] = useState({
    series: [
      {
        name: 'linechart2',
        data: [0.0, 40.0, 28.0, 51.0, 55.0],
      },
    ],
    options: {
      xaxis: {
        // 개발자 영역 : X축
        type: 'category',
        categories: [1, 2, 3, 4, 5],
      },
      // 퍼블리셔 영역
      colors: ['#FF6161'],
    } as ApexOptions,
  });

  // straight 차트
  const [straightChartState2] = useState({
    series: [
      {
        name: 'straightChartState2',
        data: [200, 400, 600, 300, 800],
      },
    ],
    options: {
      xaxis: {
        // 개발자 영역 : X축
        name: '시간',
        type: 'category',
        categories: ['09:00', '10:00', '11:00', '12:00', '13:00'],
      },
      // 퍼블리셔 영역
      colors: ['#2B9E68'],
    } as ApexOptions,
  });

  const [barChartState] = useState({
    series: [
      // 개발자 영역
      {
        name: '호출건수',
        data: [21, 22, 10, 28, 16, 21, 13, 30, 10, 10, 100, 120, 30, 104, 234, 23, 23, 20],
      },
    ],
    options: {
      fill: {
        // 퍼블리셔 영역
        type: 'gradient',
        gradient: {
          type: 'vertical',
          gradientToColors: ['#2670FF', '#9DC1FF'],
          stops: [60, 100],
          opacityFrom: 0.6,
          opacityTo: 0.35,
        },
      },
      xaxis: {
        // 개발자 영역 : Y축
        categories: ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15', '16', '17', '18'],
      },
    } as ApexOptions,
  });

  const [horizontalBarChartState] = useState({
    series: [
      {
        name: '사용 횟수',
        data: [44, 55, 41, 64, 22],
      },
    ],
    options: {
      fill: {
        type: 'gradient',
        gradient: {
          type: 'horizontal',
          gradientToColors: ['rgba(157, 193, 255, 0.35)'],
          stops: [0, 100],
          colorStops: [
            {
              offset: 0,
              color: 'rgba(157, 193, 255, 0.35)',
              opacity: 1
            },
            {
              offset: 100,
              color: 'rgba(38, 112, 255, 0.6)',
              opacity: 1
            }
          ]
        },
      },
      plotOptions: {
        bar: {
          barHeight: '50%',
          borderRadius: 4,
        },
      },
      xaxis: {
        categories: ['데이터 저장소', '모텔 카탈로그', '플레이 그라운드', '로그', '파인튜닝'],
        labels: {
          show: false,
        },
      },
      tooltip: {
        enabled: false,
      },
      dataLabels: {
        enabled: false,
      },
    } as ApexOptions,
  });

  return (

    <Fragment>
      <div className='flex flex-col px-6 py-6 gap-10 overflow-y-auto h-screen'>

        <div className='text-title-1 bg-gray-300 p-[10px] mb-3'>Chart-원형</div>
        <div className='pb-15'>
          {/* 원형 차트 [S] */}
          <div className='chart-container'>
            <div className='w-[650px] '> {/* [참고] 원형차트 width:650px 일 경우 기준임 */}
              <div className='w-full'>
                <UICircleChart.Full
                  label='가장 많이 사용한 메뉴'
                  value={70.30}
                  total={100}
                  dateRange='2025.08.01 ~ 2025.08.31'
                />
              </div>
            </div>
          </div>
          {/* // 원형 차트 [E] */}
        </div>

        <div className='text-title-1 bg-gray-300 p-[10px] mb-3'>Chart-Horizontal</div>
        <div className='pb-15'>
          {/* 막대 Horizontal 차트 [S] */}
          <div className='chart-container'>
            <div className='w-[590px]'>
              <div className='w-full'>
                <UIHorizontalBarChart
                  label='메뉴별 사용 현황'
                  options={horizontalBarChartState.options}
                  series={horizontalBarChartState.series}
                  height={250}
                  dateRange='2025.08.01 ~ 2025.08.31'
                />
              </div>
            </div>
          </div>
          {/* // 막대 Horizontal 차트 [E] */}
        </div>

        <div className='text-title-1 bg-gray-300 p-[10px] mb-3'>Chart-막대</div>
        <div className='pb-15'>
          <div className='chart-container'>
            <div className='w-[590px] '>
              {/* 막대 차트 [S] */}
              <div className='w-full'>
                <UIBarChart label='가장많이 사용한 메뉴' options={barChartState.options} series={barChartState.series} />
              </div>
            </div>
          </div>
        </div>


        <div className='text-title-1 bg-gray-300 p-[10px] mb-3'>Chart-반원형</div>
          <div className='pb-15'>

            {/* [참고] gap:20 으로 세팅 : 그래프 svg 타입으로 width 크기를 맞추기위해서 20으로 작게 지정  */}
            {/* ################### 반원 그래프 사용시 : 기본 group 세트 영역 [S] ################### */}
            <div className='flex chart-graph gap-x-5 justify-center'>

              <div className='w-[240px] flex items-center justify-center'>
                <UICircleChart.Half type='CPU' value={50.30} total={100} showLabel={false} /> {/* showLabel : 라벨 리스트 숨김처리 */}
              </div>
              <div className='w-[240px] flex items-center justify-center'>
                <UICircleChart.Half type='Memory' value={35} total={100} />
              </div>
              <div className='w-[240px] flex items-center justify-center'>
                <UICircleChart.Half type='GPU' value={80.30} total={100} />
              </div>
            </div>
            {/* ################### // group 세트 영역 [E] ################### */}

          </div>
        <div>
          <div className='text-title-1 bg-gray-300 p-[10px] mb-3'>Chart-Type1</div>
          <UILineChart label='손실값' x='Step' y='손실값' options={lineChartState.options} series={lineChartState.series} />
          <div className='text-title-1 bg-gray-300 p-[10px] mb-3'>Chart-Type2</div>
          <UILineChart label='평균 응답시간' x='시간' y='평균 응답시간' options={lineChartState2.options} series={lineChartState2.series} />
          <div className='text-title-1 bg-gray-300 p-[10px] mb-3'>Chart-Type3</div>
          <UILineChart
            type='straight'
            label={
              <div>
                <UITypography variant='title-3'>접속자 수</UITypography>
                <UITypography variant='body-1' className='text-blue-800'>
                  1000
                </UITypography>
                <UITypography variant='body-1' className='text-blue-800'>
                  명
                </UITypography>
              </div>
            }
            x='시간'
            y='접속자 수'
            options={straightChartState2.options}
            series={straightChartState2.series}
          />
          <div className='text-title-1 bg-gray-300 p-[10px] mb-3'>Chart-Type4</div>
          <UIBarChart label='시간별 호출건수' x='시간' y='호출건수' options={barChartState.options} series={barChartState.series} />
        </div>
      </div>
    </Fragment>
  );
};
