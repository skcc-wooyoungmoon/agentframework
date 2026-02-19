import { env } from '@/constants/common/env.constants';
import { UIIcon2 } from '../../../atoms/UIIcon2';

/**
 * 데이터 없음 오버레이 컴포넌트
 */
export function UIListNoData({ noDataMessage }: { noDataMessage?: string }) {
  const internalNoDataMessage = noDataMessage ? noDataMessage :
    env.VITE_NO_PRESSURE_MODE ? '조회 버튼을 통해 목록을 확인해주세요.' : '조회된 결과가 없습니다.';
  return (
    <div className='flex items-center justify-center h-[115px] -mt-[15px] text-gray-500'>
      <div className='text-center'>
        <UIIcon2 className='ic-system-80-default-nodata mb-3' />
        <div className='text-base font-normal leading-6 text-[#7E889B]'>{internalNoDataMessage}</div>
      </div>
    </div>
  );
}
