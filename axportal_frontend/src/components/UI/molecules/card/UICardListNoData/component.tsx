import { UIIcon2 } from '../../../atoms/UIIcon2';

/**
 * 카드리스트 데이터 없음 컴포넌트
 */
export function UICardListNoData() {
  return (
    <div className='w-full flex items-center justify-center h-[115px] py-[146px] text-gray-500'>
      <div className='text-center'>
        <UIIcon2 className='ic-system-80-default-nodata mb-3' />
        <div className='text-base font-normal leading-6 text-[#7E889B]'>조회된 결과가 없습니다.</div>
      </div>
    </div>
  );
}
