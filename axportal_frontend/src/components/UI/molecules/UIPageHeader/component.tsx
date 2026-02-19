import { useEffect, useState } from 'react';
import type { UIPageHeaderProps } from './types';
import { UITypography } from '@/components/UI/atoms';

export const UIPageHeader = ({ title, description, actions }: UIPageHeaderProps) => {
  const [isInPopupContent, setIsInPopupContent] = useState(false);

  useEffect(() => {
    // 부모 요소에서 section-popup-content 클래스 확인
    const checkParentClass = (element: HTMLElement | null): boolean => {
      if (!element) return false;
      if (element.classList.contains('section-popup-content')) return true;
      return checkParentClass(element.parentElement);
    };

    // 컴포넌트가 마운트된 후 부모 요소 확인
    const timer = setTimeout(() => {
      const currentElement = document.querySelector('.page-header');
      if (currentElement) {
        setIsInPopupContent(checkParentClass(currentElement.parentElement));
      }
    }, 0);

    return () => clearTimeout(timer);
  }, []);

  const gapClass = description ? (isInPopupContent ? 'gap-[12px]' : 'gap-[24px]') : '';
  const titleClass = isInPopupContent ? 'text-xl leading-[28px] font-semibold' : '';
  const descriptionClass = isInPopupContent ? 'text-base font-normal leading-6 tracking-[-0.005em] text-[#373E4D]' : '';

  return (
    <div className='page-header'>
      <div className={'flex flex-col ' + gapClass}>
        <div className='flex items-center justify-between'>
          <h2 className={titleClass}>
            {isInPopupContent ? (
              title
            ) : (
              <UITypography variant='headline-2-product' className='secondary-neutral-900'>
                {title}
              </UITypography>
            )}
          </h2>
          {actions && <div className='flex items-center gap-3'>{actions}</div>}
        </div>
        {description && (
          <div className={descriptionClass}>
            {isInPopupContent ? (
              Array.isArray(description) ? (
                description.map((line, idx) => <div key={idx}>{line}</div>)
              ) : (
                description
              )
            ) : (
              <UITypography variant='body-1' className='text-gray-700'>
                {Array.isArray(description) ? (
                  description.map((line, idx) => <div key={idx}>{line}</div>)
                ) : (
                  description
                )}
              </UITypography>
            )}
          </div>
        )}
      </div>
    </div>
  );
};
