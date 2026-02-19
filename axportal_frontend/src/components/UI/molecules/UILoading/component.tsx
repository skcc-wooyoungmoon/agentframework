import type { UILoadingProps } from './types';
import { UITypography } from '@/components/UI/atoms';
import { UIUnitGroup } from '@/components/UI/molecules';

export const UILoading = ({ title = '처리중입니다.', label = '잠시만 기다려 주세요.', className = '' }: UILoadingProps) => {
  return (
    <div className={'loading ' + className}>
      <div className='loading-container'>
        <div className='spinner'></div>
        <UIUnitGroup gap={4} direction='column'>
          <UITypography variant='title-2' className='text-white text-sb justify-center'>
            {title}
          </UITypography>
          <UITypography variant='body-1' className='text-white justify-center'>
            {label}
          </UITypography>
        </UIUnitGroup>
      </div>
    </div>
  );
};
