import { UIDropdownOption } from '../UIDropdownOption';

export interface UIDropdownListProps {
  /** 옵션 목록 */
  options: Array<{
    value: string;
    label: string;
  }>;
  /** 선택된 값 */
  selectedValue?: string;
  /** 드랍다운 상태 */
  state?: 'default' | 'error' | 'focused';
  /** 옵션 선택 이벤트 핸들러 */
  onSelect?: (value: string) => void;
  /** 드롭다운 표시 위치 */
  position?: 'top' | 'bottom';
  /** 추가 CSS 클래스 */
  className?: string;
  /** 폰트 크기 */
  fontSize?: 14 | 16;
}

/**
 * UIDropdownList 컴포넌트
 *
 * @description 드랍다운 옵션 리스트 컴포넌트
 * - 기본 6개 노출, 초과 시 스크롤
 * - 드랍다운과 4px 간격
 * - 테두리: 1px solid #DCE2ED, radius 8px
 * - 그림자: 0px 4px 20px 0px #00000014
 */
export function UIDropdownList({ options, selectedValue, state = 'default', onSelect, className = '', fontSize = 16 }: UIDropdownListProps) {
  const getBorderColor = () => {
    if (state === 'error') return 'border-negative-red';
    if (state === 'focused') return 'border-blue-800';
    return 'border-gray-300';
  };

  const maxVisibleItems = 6;
  const itemHeight = fontSize === 14 ? 36 : 40; // UIListContentBox.Header 내부: 36px, 일반: 40px
  const actualHeight = Math.min(options.length, maxVisibleItems) * itemHeight + 14; // padding 6px * 2
  const maxHeight = maxVisibleItems * itemHeight + 14; // padding 포함
  const needsScroll = options.length > maxVisibleItems;

  const getPositionClasses = () => {
    // 드롭다운 바로 아래에 고정 위치로 표시
    return 'absolute left-0 right-0 z-[900] overflow-hidden';
  };

  return (
    <div
      className={`
        ${getPositionClasses()}
        w-full bg-white rounded-lg border border-solid
        ${getBorderColor()}
        ${needsScroll ? 'overflow-y-auto dropdown-scrollbar' : ''}
        ${className}
      `}
      style={{
        boxShadow: '0px 4px 20px 0px rgba(0, 0, 0, 0.08)',
        height: `${actualHeight}px`,
        maxHeight: `${maxHeight}px`,
        top: 'calc(100% + 4px)',
        padding: '6px',
      }}
    >
      {options.map(option => (
        <UIDropdownOption
          key={option.value}
          value={option.value}
          label={option.label}
          isSelected={option.value === selectedValue}
          state='default'
          onClick={onSelect}
          fontSize={fontSize}
        />
      ))}
    </div>
  );
}
