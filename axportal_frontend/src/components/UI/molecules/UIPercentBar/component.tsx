import type { UIPercentBarProps } from './types';

export const UIPercentBar: React.FC<UIPercentBarProps> = ({ value = 0, color = '#2670FF', height = 12, className = '', status = 'success' }) => {
  const percentage = Math.min(Math.max(value, 0), 100);

  // 상태에 따른 색상 결정
  const getBarColor = () => {
    if (status === 'error') {
      return '#D61111';
    }
    return color;
  };

  const barColor = getBarColor();

  return (
    <div className={'flex items-center gap-3 ' + className}>
      <div className='relative flex-1 rounded-full bg-[#CDE0FF]' style={{ height: `${height}px` }}>
        <div
          className='absolute top-0 left-0 h-full rounded-full transition-all duration-300'
          style={{
            width: `${percentage}%`,
            backgroundColor: barColor,
          }}
        />
      </div>
      {/* 퍼센트 영역은 외부 페이지내에서 따로 노출 후 제어됨 */}
      {/* <div className='text-title-4 inline-block font-semibold' style={{ color: status === 'fail' ? '#D61111' : '#005DF9' }}>
        {percentage}%
      </div> */}
    </div>
  );
};
