import type { UIProgressProps } from './types';

/**
 * Progress 컴포넌트 (Atomic Design: atom)
 * - 진행률을 시각적으로 표시하는 프로그레스 바
 * - 배경: #CDE0FF, 높이: 8px, border-radius: 8px
 * - 기본 상태: #005DF9, 에러 상태: #D61111
 * - 퍼센트 텍스트 표시 (Pretendard, 13px, 오른쪽 정렬)
 * - 진행바와 텍스트 간격: 8px
 */
export function UIProgress({ value = 0, status = 'normal', showPercent = true, className = '' }: UIProgressProps) {
  const clampedValue = Math.max(0, Math.min(100, value));

  return (
    <div className={`progress-wrap ${className} ${status}`}>
      <div className='progress-gauge'>
        <div className={`progress-bg`} />
        <div className={`progress-bar`} style={{ width: clampedValue + '%' }} />
      </div>
      {showPercent && (
        <span
          className='progress-percent'
          style={{
            fontFamily: 'Pretendard',
            letterSpacing: '-0.08px',
          }}
        >
          {clampedValue}%
        </span>
      )}
    </div>
  );
}
