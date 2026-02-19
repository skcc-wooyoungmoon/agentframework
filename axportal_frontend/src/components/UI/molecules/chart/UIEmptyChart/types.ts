import React from 'react';

export interface UIEmptyChartProps {
  /** 차트 제목 (문자열 또는 React 컴포넌트) */
  title: string | React.ReactNode;
  /** 날짜 범위 (선택사항) */
  dateRange?: string;
  /** 메시지 텍스트 (기본값: "조회된 데이터가 없습니다") */
  message?: string;
  /** 최소 높이 (기본값: "264px") */
  minHeight?: string;
  /** 추가 className */
  className?: string;
}
