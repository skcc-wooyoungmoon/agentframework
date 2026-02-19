import type { UIDataCntProps } from './types';

export const UIDataCnt: React.FC<UIDataCntProps> = ({ count = 0, unit = '건', prefix = '총', format = true, className = '' }) => {
  const formatNumber = (num: number): string => {
    if (!format) return String(num);
    return num.toLocaleString('ko-KR');
  };

  // count가 0보다 작을 때 처리
  if (count < 0) {
    // prefix가 "총"이면 prefix와 count 모두 숨김
    if (prefix === '총') {
      return null;
    }
    // prefix가 "총"이 아니면 prefix만 노출
    return (
      <span className={'ui-data-cnt text-base font-semibold text-gray-900 ' + className}>
        {prefix}
      </span>
    );
  }

  return (
    <span className={'ui-data-cnt text-base font-semibold text-gray-900 ' + className}>
      {prefix} <span className='primary-800'>{formatNumber(count)}</span>
      {unit}
    </span>
  );
};

UIDataCnt.displayName = 'UIDataCnt';
