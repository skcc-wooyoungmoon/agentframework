import type { ApexOptions } from 'apexcharts';
import type { UIChartContainerProps } from '../UIChartContainer';

export type UIHorizontalBarChartProps = {
  options: ApexOptions;
  series: ApexOptions['series'];
  height?: number | string;
  dateRange?: string;
} & Omit<UIChartContainerProps, 'children'>;
