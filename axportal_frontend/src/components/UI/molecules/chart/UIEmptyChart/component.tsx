import { UITypography } from '../../../atoms/UITypography/component';
import type { UIEmptyChartProps } from './types';
import { UIIcon2 } from '@/components/UI/atoms/UIIcon2';

/**
 * 데이터가 없을 때 표시하는 빈 차트 컴포넌트
 * - 제목, 날짜 범위, 메시지를 표시
 * - 다양한 차트 컴포넌트에서 재사용 가능
 */
export const UIEmptyChart = ({ title, dateRange, message = '조회된 데이터가 없습니다.', className = '' }: UIEmptyChartProps) => {
  return (
    <div className={`chart-item h-full flex flex-col ${className}`}>
      {/* 차트 헤더 */}
      <div className='chart-header mb-4 flex justify-between items-center'>
        <div className='flex items-center gap-4'>
          <h3 className='chart-title'>
            {typeof title === 'string' ? (
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                {title}
              </UITypography>
            ) : (
              title
            )}
          </h3>
        </div>
        {dateRange && <div className='text-body-2 ml-auto text-[#8B95A9]'>{dateRange}</div>}
      </div>

      {/* 데이터 없음 메시지 영역 */}
      <div className='flex-1 flex items-center justify-center' style={{ minHeight: '264px' }}>
        {/* <div className='text-center text-[#9ca3af]'>{message}</div> */}
        <div className='flex flex-col justify-center items-center gap-3'>
          <span className='ico-nodata'>
            <UIIcon2 className='ic-system-80-default-nodata' />
          </span>
          <span className='text-body-1 secondary-neutral-500'>{message}</span>
        </div>
      </div>
    </div>
  );
};
