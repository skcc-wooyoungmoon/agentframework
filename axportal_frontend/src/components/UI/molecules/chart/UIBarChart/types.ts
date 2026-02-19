import type { ApexOptions } from 'apexcharts';
import type { UIChartContainerProps } from '../UIChartContainer';

export type UIBarChartProps = {
  options: ApexOptions;
  series: ApexOptions['series'];
  /** 차트 높이 */
  height?: number | string;
} & Omit<UIChartContainerProps, 'children'>;
