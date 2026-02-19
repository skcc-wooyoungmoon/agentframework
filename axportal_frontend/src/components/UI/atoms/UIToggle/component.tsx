import { UIIcon2 } from '../UIIcon2';

import type { UIToggleProps } from './types';

export const UIToggle: React.FC<UIToggleProps> = ({
  checked = false,
  disabled = false,
  onChange,
  size = 'medium',
  className = 'h-[40px]',
  label,
  labelPosition = 'right',
  innerText,
  variant = 'basic',
  segmentOptions = [],
  segmentValue = '',
  onSegmentChange,
}) => {
  const handleToggle = (e?: React.MouseEvent | React.ChangeEvent) => {
    if (e) {
      e.preventDefault();
      e.stopPropagation();
    }
    if (!disabled && onChange) {
      onChange(!checked);
    }
  };

  // 텍스트 토글용 컨테이너 사이즈 (더 넓게)
  const textContainerSizes = {
    medium: 'w-[64px] h-[32px]',
    small: 'w-[56px] h-[28px]',
  };

  // 기본 토글 컨테이너 사이즈
  const basicContainerSizes = {
    medium: 'w-[46px] h-[24px]',
    small: 'w-[38px] h-[20px]',
  };

  // 텍스트 토글용 원형 버튼 사이즈
  const textCircleSizes = {
    medium: 'w-[28px] h-[28px]',
    small: 'w-[24px] h-[24px]',
  };

  // 기본 토글 원형 버튼 사이즈
  const basicCircleSizes = {
    medium: 'w-[20px] h-[20px]',
    small: 'w-[16px] h-[16px]',
  };

  // 텍스트 토글용 원형 버튼 위치
  const textCirclePositions = {
    medium: checked ? 'translate-x-[34px]' : 'translate-x-[2px]',
    small: checked ? 'translate-x-[30px]' : 'translate-x-[2px]',
  };

  // 기본 토글 원형 버튼 위치
  const basicCirclePositions = {
    medium: checked ? 'left-[24px]' : 'left-[2px]',
    small: checked ? 'left-[20px]' : 'left-[2px]',
  };

  // dataView 토글용 컨테이너 사이즈 (고정)
  const dataViewContainerSizes = {
    medium: 'w-[74px] h-[40px]',
    small: 'w-[74px] h-[40px]',
  };

  // dataView 토글용 사각형 버튼 사이즈 (고정)
  const dataViewSquareSizes = {
    medium: 'w-[32px] h-[32px]',
    small: 'w-[32px] h-[32px]',
  };

  // dataView 토글용 사각형 버튼 위치 (리스트뷰는 left-1, 카드뷰는 34px)
  const dataViewSquarePositions = {
    medium: checked ? 'left-[38px]' : 'left-[4px]',
    small: checked ? 'left-[38px]' : 'left-[4px]',
  };

  // 현재 variant에 따른 사이즈와 위치 결정
  const containerSizes = variant === 'dataView' ? dataViewContainerSizes : variant === 'text' ? textContainerSizes : basicContainerSizes;
  const circleSizes = variant === 'dataView' ? dataViewSquareSizes : variant === 'text' ? textCircleSizes : basicCircleSizes;
  const circlePositions = variant === 'dataView' ? dataViewSquarePositions : variant === 'text' ? textCirclePositions : basicCirclePositions;

  // 배경색 결정
  let bgColor = '';
  if (variant === 'dataView') {
    bgColor = 'bg-gray-200'; // dataView 토글은 고정 배경색 (#E7EDF6)
  } else if (disabled && checked) {
    bgColor = 'bg-blue-300'; // disabled_selected - var(--color-blue-300)
  } else if (disabled) {
    bgColor = 'bg-gray-300'; // disabled - var(--color-gray-300)
  } else if (checked) {
    bgColor = 'bg-blue-800'; // selected - var(--color-blue-800)
  } else {
    bgColor = 'bg-gray-500'; // unselected - var(--color-gray-500)
  }

  // 세그먼트 토글 렌더링
  if (variant === 'segment') {
    return (
      <div className={'inline-flex rounded-lg p-1 gap-1 ' + className} style={{ backgroundColor: '#f3f6fb' }}>
        {segmentOptions.map(option => (
          <button
            key={option.id}
            onClick={e => {
              e.preventDefault();
              !option.disabled && onSegmentChange?.(option.id);
            }}
            disabled={option.disabled}
            className={
              'px-4 py-2 text-base rounded-md transition-all duration-200 ' +
              (segmentValue === option.id ? 'bg-white font-normal shadow-sm' : 'bg-transparent font-normal hover:bg-white/50') +
              (option.disabled ? ' opacity-50 cursor-not-allowed' : ' cursor-pointer')
            }
            style={{
              letterSpacing: '-0.08px',
              color: segmentValue === option.id ? '#005df9' : '#576072',
              fontSize: '16px',
              lineHeight: '18px',
            }}
          >
            {option.label}
          </button>
        ))}
      </div>
    );
  }

  // 토글 스위치 요소
  // disabled 케이스: 불투명도 감소 및 커서 변경
  const disabledClass = disabled ? 'cursor-not-allowed tg-disabled' : '';

  const toggleElement =
    variant === 'dataView' ? (
      <div className={containerSizes[size] + ' ' + bgColor + ' rounded-md p-1 relative transition-colors ain-transition ' + disabledClass}>
        {/* 리스트 아이콘 */}
        <UIIcon2 className={`${!checked ? 'ic-system-24-toggle-area-list-on' : 'ic-system-24-toggle-area-list'} absolute left-[8px] top-[8px] z-10`} aria-label='list view' />
        {/* 카드 아이콘 */}
        <UIIcon2 className={`${checked ? 'ic-system-24-toggle-area-card-on' : 'ic-system-24-toggle-area-card'} absolute right-[8px] top-[8px] z-10`} aria-label='card view' />
        {/* 이동하는 사각형 배경 */}
        <div className={'w-[32px] h-[32px] bg-white rounded-sm absolute transition-transform ain-transition z-0 ' + circlePositions[size]} />
      </div>
    ) : (
      <div className={containerSizes[size] + ' ' + bgColor + ' rounded-full relative transition-colors ain-transition ' + disabledClass}>
        {/* 텍스트 표시 (variant가 text일 때만) */}
        {variant === 'text' && innerText && (
          <span
            className={
              'absolute inset-0 flex items-center text-white font-medium ' +
              (size === 'medium' ? 'text-[11px] leading-[14px] ' : 'text-[10px] leading-[12px] ') +
              (checked ? 'justify-start pl-2' : 'justify-end pr-2')
            }
            style={{ fontWeight: 500 }}
          >
            {innerText}
          </span>
        )}
        <div className={circleSizes[size] + ' bg-white rounded-full absolute top-[2px] transition-transform ain-transition ' + circlePositions[size]} />
      </div>
    );

  // 라벨 요소 - labelPosition이 top일 때는 form-label 클래스 사용
  const labelElement = label ? labelPosition === 'top' ? <span className='form-label block mb-2'>{label}</span> : <span className='text-sm'>{label}</span> : null;

  // labelPosition이 top일 때는 다른 레이아웃 사용
  if (labelPosition === 'top') {
    return (
      <div className={'w-full ' + className}>
        {labelElement}
        <label
          className={'inline-flex items-center ' + (disabled ? 'cursor-not-allowed' : 'cursor-pointer')}
          onClick={e => {
            e.preventDefault();
            e.stopPropagation();
            handleToggle(e);
          }}
        >
          <input
            type='checkbox'
            checked={checked}
            onChange={() => {}} // 빈 함수로 변경
            disabled={disabled}
            className='sr-only peer'
            tabIndex={-1}
            style={{ position: 'fixed', left: '-9999px' }}
          />
          {toggleElement}
        </label>
      </div>
    );
  }

  return (
    <label
      className={'inline-flex items-center ' + (disabled ? 'cursor-not-allowed' : 'cursor-pointer') + ' ' + className}
      onClick={e => {
        e.preventDefault();
        e.stopPropagation();
        handleToggle(e);
      }}
    >
      <input
        type='checkbox'
        checked={checked}
        onChange={() => {}} // 빈 함수로 변경
        disabled={disabled}
        className='sr-only peer'
        tabIndex={-1}
        style={{ position: 'fixed', left: '-9999px' }}
      />
      {labelPosition === 'left' ? (
        <>
          {labelElement && <span className='mr-3'>{labelElement}</span>}
          {toggleElement}
        </>
      ) : (
        <>
          {toggleElement}
          {labelElement && <span className='ml-3'>{labelElement}</span>}
        </>
      )}
    </label>
  );
};
