import type { ReactNode } from 'react';

export type UIChartContainerProps = {
  children: ReactNode;
  label: string | ReactNode;
  x?: string;
  y?: string;
};
