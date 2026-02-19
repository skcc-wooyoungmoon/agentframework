import { UIButton2, UIIcon2 } from '@/components/UI/atoms';
import { useEffect } from 'react';
import { createPortal } from 'react-dom';

import { Fragment } from 'react/jsx-runtime';
import type { UIAlarmProps } from './types';

export function UIAlarm({ position = 'right', size = 'small', width, children, className = '', isVisible = true, onClose, title = '알림함' }: UIAlarmProps) {
  // size에 따라 width 결정
  const getWidth = () => {
    if (width) return width; // width가 명시적으로 제공된 경우 우선 사용
    return size === 'large' ? 'w-[850px]' : 'w-[448px]';
  };

  const computedWidth = getWidth();
  // 레이아웃 시프트 방지 - 최강 방법
  useEffect(() => {
    if (isVisible) {
      // 스크롤바 너비 계산
      const scrollbarWidth = window.innerWidth - document.documentElement.clientWidth;

      // 기존 스타일 저장
      const originalStyles = {
        html: {
          overflow: document.documentElement.style.overflow,
          paddingRight: document.documentElement.style.paddingRight,
        },
        body: {
          overflow: document.body.style.overflow,
          paddingRight: document.body.style.paddingRight,
          position: document.body.style.position,
        },
      };

      // 모든 가능한 메인 컨테이너들 찾기
      const allContainers = [
        document.documentElement,
        document.body,
        document.querySelector('#root'),
        document.querySelector('#root > div'),
        document.querySelector('.main-content'),
        document.querySelector('.page-body'),
        document.querySelector('[class*="main"]'),
        document.querySelector('.content'),
        document.querySelector('.layout'),
        document.querySelector('.article-grid'),
        document.querySelector('.article-body'),
        document.querySelector('.ui-list-container'),
        document.querySelector('.ui-list-content-box'),
        document.querySelector('.ag-root-wrapper'),
        document.querySelector('.ag-root'),
      ].filter(Boolean);

      // 컨테이너별 원본 스타일 저장
      const containerStyles = allContainers.map(container => ({
        element: container as HTMLElement,
        originalStyle: {
          overflow: (container as HTMLElement).style.overflow,
          paddingRight: (container as HTMLElement).style.paddingRight,
          marginRight: (container as HTMLElement).style.marginRight,
          transform: (container as HTMLElement).style.transform,
          position: (container as HTMLElement).style.position,
        },
      }));

      // 1. HTML과 Body에 스크롤 방지 및 패딩 추가
      document.documentElement.style.overflow = 'hidden';
      document.documentElement.style.paddingRight = `${scrollbarWidth}px`;
      document.body.style.overflow = 'hidden';
      document.body.style.paddingRight = `${scrollbarWidth}px`;

      // 2. 모든 컨테이너에 패딩 추가
      containerStyles.forEach(({ element }) => {
        element.style.paddingRight = `${scrollbarWidth}px`;
      });

      // 3. 고정/절대 위치 요소들도 처리
      const fixedElements = document.querySelectorAll('.fixed, .absolute, [style*="position: fixed"], [style*="position: absolute"]');
      const fixedElementStyles = Array.from(fixedElements).map(element => ({
        element: element as HTMLElement,
        originalStyle: {
          paddingRight: (element as HTMLElement).style.paddingRight,
          marginRight: (element as HTMLElement).style.marginRight,
        },
      }));

      fixedElementStyles.forEach(({ element }) => {
        element.style.paddingRight = `${scrollbarWidth}px`;
      });

      return () => {
        // HTML과 Body 스타일 복원
        document.documentElement.style.overflow = originalStyles.html.overflow;
        document.documentElement.style.paddingRight = originalStyles.html.paddingRight;
        document.body.style.overflow = originalStyles.body.overflow;
        document.body.style.paddingRight = originalStyles.body.paddingRight;
        document.body.style.position = originalStyles.body.position;

        // 모든 컨테이너 스타일 복원
        containerStyles.forEach(({ element, originalStyle }) => {
          element.style.overflow = originalStyle.overflow;
          element.style.paddingRight = originalStyle.paddingRight;
          element.style.marginRight = originalStyle.marginRight;
          element.style.transform = originalStyle.transform;
          element.style.position = originalStyle.position;
        });

        // 고정 요소들 스타일 복원
        fixedElementStyles.forEach(({ element, originalStyle }) => {
          element.style.paddingRight = originalStyle.paddingRight;
          element.style.marginRight = originalStyle.marginRight;
        });
      };
    }
  }, [isVisible]);

  if (!isVisible) {
    return null;
  }

  return createPortal(
    <Fragment>
      {/* Dimmed 오버레이 (MainLayout 전체를 덮음) */}
      <div className='fixed inset-0 bg-dimmed min-w-[1920px] min-h-screen z-[9998]'></div>

      <div className='fixed top-0 right-0 bottom-0 z-[9999]'>
        <aside
          className={`${computedWidth} bg-white shadow-sm border-gray-200 flex-shrink-0 overflow-y-auto h-full ${position === 'left' ? 'border-r order-first' : 'order-last'} ${className}`}
        >
          <div className='h-full flex flex-col'>
            {/* 필터 헤더 */}
            <div className='filter-header p-6 bg-white' style={{ height: '76px' }}>
              <div className='flex items-center justify-between h-full'>
                <h3 className='text-lg font-semibold text-gray-900'>{title}</h3>
                <div className='flex flex-end items-center gap-4'>
                  {onClose && (
                    <UIButton2 onClick={onClose} className='!w-6 !h-6 !min-w-0 !p-0 !bg-transparent transition-colors flex-shrink-0 cursor-pointer' title='닫기'>
                      <UIIcon2 className='ic-system-24-outline-large-close' aria-label='닫기' />
                    </UIButton2>
                  )}
                </div>
              </div>
            </div>

            {/* 필터 콘텐츠 */}
            <div className='flex-1 overflow-y-auto'>{children}</div>
          </div>
        </aside>
      </div>
    </Fragment>,
    document.body
  );
}
