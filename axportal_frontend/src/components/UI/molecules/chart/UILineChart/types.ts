import type { ApexOptions } from 'apexcharts';
import type React from 'react';
import type { UIChartContainerProps } from '../UIChartContainer';

export type UILineChartProps = {
  type?: 'smooth' | 'straight';
  options: ApexOptions;
  series: ApexOptions['series'];
  /** 차트 너비 */
  width?: string | number;
  /** 차트 높이 */
  height?: string | number;
  /** 데이터 없음 상태 강제 설정 */
  noData?: boolean;
  /** 데이터 없음 상태에서 헤더에 표시할 커스텀 컨텐츠 */
  customTitle?: React.ReactNode;
} & Omit<UIChartContainerProps, 'children'>;
