import { useMemo } from 'react';

import { UIIcon2 } from '../../atoms/UIIcon2';

import type { UIStepperProps } from './types';

/**
 * UIStepper 컴포넌트 (Atomic Design: molecule)
 * - 여러 단계의 프로세스 진행 상태를 표시하는 스텝퍼
 * - 완료, 진행중, 미완료 상태를 시각적으로 구분
 */
export function UIStepper({ items, currentStep, direction = 'vertical', className = '' }: UIStepperProps) {
  const isVertical = useMemo(() => direction === 'vertical', [direction]);

  const getStepCircleStyles = (status: string) => {
    switch (status) {
      case 'completed':
        return '';
      case 'ongoing':
        return 'bg-blue-800';
      case 'incompleted':
      default:
        return 'bg-gray-300';
    }
  };

  const getStepTextStyles = (status: string) => {
    switch (status) {
      case 'ongoing':
        return 'text-white font-semibold';
      case 'incompleted':
      default:
        return 'text-gray-500 font-semibold';
    }
  };

  const getStepLabelStyles = (status: string) => {
    switch (status) {
      case 'completed':
        return 'text-lg font-semibold leading-[26px]';
      case 'ongoing':
        return 'text-lg font-semibold leading-[26px]';
      case 'incompleted':
        return 'text-lg font-semibold leading-[26px]';
      default:
        return 'text-lg font-semibold leading-[26px]';
    }
  };

  const getStepStatus = (step: number, currentStep: number) => {
    if (step < currentStep) return 'completed';
    else if (step === currentStep) return 'ongoing';
    else return 'incompleted';
  };

  const containerClasses = isVertical ? 'flex flex-col' : 'flex flex-row items-start';

  return (
    <div className={containerClasses + ' ' + className}>
      {items.map((item, index) => (
        <div key={item.id || `step-${item.step}`} className={isVertical ? 'relative mb-[23px] last:mb-0' : 'flex flex-row items-center'}>
          <div className={isVertical ? 'flex items-start' : 'flex flex-col items-center'}>
            <div className='flex items-start h-[26px]'>
              <div className='relative'>
                {/* {item.status === 'completed' ? ( */}
                {getStepStatus(item.step, currentStep) === 'completed' ? (
                  <UIIcon2 className='ic-system-24-outline-blue-complete' />
                ) : (
                  <div className={'w-6 h-6 rounded-full flex items-center justify-center ' + getStepCircleStyles(getStepStatus(item.step, currentStep))}>
                    <span className={'text-sm leading-5 ' + getStepTextStyles(getStepStatus(item.step, currentStep))}>{item.step}</span>
                  </div>
                )}
              </div>

              {isVertical && (
                <span
                  className={'ml-2 break-words whitespace-normal flex-1 ' + getStepLabelStyles(getStepStatus(item.step, currentStep))}
                  style={{
                    maxWidth: 'calc(100% - 22px)',
                    color: getStepStatus(item.step, currentStep) === 'ongoing' ? '#005DF9' : '#8B95A9',
                  }}
                >
                  {item.label}
                </span>
              )}
            </div>
          </div>

          {/* 세로 방향 바 - 아이콘 중앙에서 시작 */}
          {isVertical && index < items.length - 1 && (
            <div
              className='absolute left-[11px] top-[24px] w-0.5 z-0'
              style={{
                height: 'calc(100% - 1px)',
                backgroundColor: getStepStatus(item.step, currentStep) === 'completed' ? '#005DF9' : '#DCE2ED',
              }}
            />
          )}

          {/* 가로 방향 */}
          {!isVertical && (
            <>
              <span
                className={'mt-2 ' + getStepLabelStyles(getStepStatus(item.step, currentStep))}
                style={{
                  color: getStepStatus(item.step, currentStep) === 'ongoing' ? '#005DF9' : '#8B95A9',
                }}
              >
                {item.label}
              </span>
              {index < items.length - 1 && (
                <div
                  className='h-0.5 w-[30px] mx-2'
                  style={{
                    backgroundColor: getStepStatus(item.step, currentStep) === 'completed' ? '#005DF9' : '#DCE2ED',
                  }}
                />
              )}
            </>
          )}
        </div>
      ))}
    </div>
  );
}
