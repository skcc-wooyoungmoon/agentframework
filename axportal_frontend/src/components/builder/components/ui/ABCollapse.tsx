/**
 * react-collapse 완전 대체 - 순수 React + CSS 기반 접을 수 있는 컴포넌트
 * react-collapse 의존성 완전 제거
 */
import React, { useState, useEffect, useRef } from 'react';
import { ABClassNames } from './ABClassNames';

interface ABCollapseProps {
  isOpened: boolean;
  children: React.ReactNode;
  className?: string;
  theme?: {
    collapse?: string;
    content?: string;
  };
}

export const ABCollapse: React.FC<ABCollapseProps> = ({ isOpened, children, className, theme: _theme }) => {
  const [height, setHeight] = useState<number | string>(isOpened ? 'auto' : 0);
  const [isAnimating, setIsAnimating] = useState(false);
  const contentRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!contentRef.current) return;

    if (isOpened) {
      // 열기 애니메이션
      setIsAnimating(true);
      const contentHeight = contentRef.current.scrollHeight;
      setHeight(0);

      // 다음 프레임에서 높이 설정
      requestAnimationFrame(() => {
        setHeight(contentHeight);
      });

      // 애니메이션 완료 후
      const timer = setTimeout(() => {
        setHeight('auto');
        setIsAnimating(false);
      }, 300);

      return () => clearTimeout(timer);
    } else {
      // 닫기 애니메이션
      setIsAnimating(true);
      const contentHeight = contentRef.current.scrollHeight;
      setHeight(contentHeight);

      // 다음 프레임에서 높이를 0으로
      requestAnimationFrame(() => {
        setHeight(0);
      });

      // 애니메이션 완료 후
      const timer = setTimeout(() => {
        setIsAnimating(false);
      }, 300);

      return () => clearTimeout(timer);
    }
  }, [isOpened]);

  return (
    <div className={ABClassNames('overflow-hidden transition-all duration-300 ease-in-out', isAnimating ? '' : 'transition-none', className)} style={{ height }}>
      <div ref={contentRef}>{children}</div>
    </div>
  );
};
