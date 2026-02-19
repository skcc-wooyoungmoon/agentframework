import { UITypography } from '../../../atoms/UITypography/component';
import type { UIChartContainerProps } from './types';

export const UIChartContainer = ({ children, label, x, y }: UIChartContainerProps) => {
  return (
    <div className='chart-item'>
      <div className='chart-header mb-4'>
        <div className='chart-title'>
          {typeof label === 'string' ? (
            <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
              {label}
            </UITypography>
          ) : (
            label
          )}
        </div>
        {x && y && (
          <div className='flex gap-2'>
            <div
              className='bg-[#E7EDF6] text-[#8B95A9] rounded-[50px] w-fit'
              style={{ height: '26px', paddingRight: '12px', paddingLeft: '12px', gap: '4px', display: 'flex', alignItems: 'center' }}
            >
              <div style={{ fontFamily: 'Pretendard', fontWeight: 600, fontSize: '12px', lineHeight: '140%', letterSpacing: '2%' }}>x: {x}</div>
            </div>
            <div
              className='bg-[#E7EDF6] text-[#8B95A9] rounded-[50px] w-fit'
              style={{ height: '26px', paddingRight: '12px', paddingLeft: '12px', gap: '4px', display: 'flex', alignItems: 'center' }}
            >
              <div style={{ fontFamily: 'Pretendard', fontWeight: 600, fontSize: '12px', lineHeight: '140%', letterSpacing: '2%' }}>y: {y}</div>
            </div>
          </div>
        )}
      </div>
      {children}
    </div>
    // <div className='border border-width-1 border-[#E2E5EA] rounded-2xl p-4'>
    //   <div className='flex justify-between items-center'>
    //     <div>{typeof label === 'string' ? <UITypography variant='title-3'>{label}</UITypography> : label}</div>
    //     {x && y && (
    //       <div className='flex gap-2'>
    //         <div className=' bg-[#E7EDF6] text-[#8B95A9] rounded-[50px] w-fit'>
    //           <div className='px-2 py-1'>x: {x}</div>
    //         </div>
    //         <div className=' bg-[#E7EDF6] text-[#8B95A9] rounded-[50px] w-fit'>
    //           <div className='px-2 py-1'>y: {y}</div>
    //         </div>
    //       </div>
    //     )}
    //   </div>
    //   {children}
    // </div>
  );
};
