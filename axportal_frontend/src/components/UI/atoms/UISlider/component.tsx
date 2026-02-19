import React, { useRef, useState, useEffect, useCallback } from 'react';

import { UITypography } from '@/components/UI/atoms/UITypography';
import type { UISliderProps } from './types';

export const UISlider: React.FC<UISliderProps> = ({
  min = 0,
  max = 10,
  value = 0,
  onChange,
  disabled = false,
  className = '',
  unit = '',
  label,
  required = false,
  showTextField = false,
  textValue = '',
  onTextChange,
  color = '#2670FF',
  decimalPlaces,
  step,
}) => {
  const sliderRef = useRef<HTMLInputElement>(null);

  // step 값을 동적으로 계산
  const calculateStep = useCallback((): number => {
    if (step !== undefined) {
      return step;
    }

    // max 값이 정수가 아니면 소수점 단위로 계산
    if (max % 1 !== 0) {
      // max의 소수점 자릿수에 따라 step 계산
      const maxStr = max.toString();
      const decimalLength = (maxStr.split('.')[1] || '').length;
      return Math.pow(10, -decimalLength);
    }

    return 1;
  }, [step, max]);

  // 값을 포맷팅하는 헬퍼 함수
  const formatValue = useCallback(
    (val: number) => {
      // max 값에 도달했을 때는 max 값 그대로 표시
      if (Math.abs(val - max) < 0.0001) {
        return String(max);
      }

      if (decimalPlaces !== undefined) {
        return val.toFixed(decimalPlaces);
      }

      // decimalPlaces가 지정되지 않은 경우, 정수면 정수로, 소수면 소수점 첫째 자리까지
      if (val % 1 === 0) {
        return String(Math.round(val));
      } else {
        // 소수인 경우 최대 소수점 첫째 자리까지
        return val.toFixed(1);
      }
    },
    [decimalPlaces, max]
  );

  // 텍스트 필드 입력 상태를 내부에서 관리
  const [inputText, setInputText] = useState(textValue || formatValue(value));

  // textValue prop이나 value prop이 변경되면 inputText 업데이트
  useEffect(() => {
    if (textValue) {
      setInputText(textValue);
    } else {
      setInputText(formatValue(value));
    }
  }, [textValue, value, formatValue]);

  // 색상별 border color 매핑 (rgba 형식으로 투명도 30% 적용)
  const getBorderColor = (mainColor: string) => {
    switch (mainColor) {
      case '#2670FF':
        return 'rgba(104, 158, 255, 0.3)'; // #689EFF with 30% opacity
      case '#37D8D0':
        return 'rgba(55, 216, 208, 0.3)'; // #37D8D0 with 30% opacity
      case '#8166D2':
        return 'rgba(129, 102, 210, 0.3)'; // #8166D2 with 30% opacity
      default:
        return mainColor;
    }
  };

  // molecules 스타일 (슬라이더 + 텍스트 필드 조합)
  // value, min, max가 모두 0일 때 처리
  const isAllZero = value === 0 && min === 0 && max === 0;
  const percentage = isAllZero || max === min ? 0 : ((value - min) / (max - min)) * 100;
  // 값이 max에 도달했을 때 정확히 100%로 설정
  const displayPercentage = isAllZero ? 0 : Math.abs(value - max) < 0.0001 ? 100 : percentage;

  return (
    <div className={`w-full ${className}`}>
      {/* Label */}
      {label && (
        <div className='mb-4'>
          <UITypography variant='body-1' className='text-sb' required={required}>
            {label}
          </UITypography>
        </div>
      )}

      {/* Slider Input Area */}
      <div className='flex items-center gap-6'>
        {/* Slider */}
        <div className='flex-1 relative'>
          {/* Slider Background */}
          <div className='relative h-3 bg-[#e7edf6] rounded-full'>
            {/* Active Track */}
            <div
              className='absolute top-0 left-0 h-3 rounded-l-full'
              style={{
                width: `${displayPercentage}%`,
                backgroundColor: color,
              }}
            />

            {/* Slider Thumb - value, min, max가 모두 0일 때 비노출 */}
            {!isAllZero && (
              <div
                className='absolute transform -top-[8px] -translate-x-1/2 w-7 h-7 rounded-full shadow-sm cursor-pointer'
                style={{
                  left: displayPercentage >= 99 ? `calc(100% - 16px)` : `calc(${displayPercentage}% - 8px)`,
                  borderWidth: '6px',
                  borderStyle: 'solid',
                  borderColor: getBorderColor(color),
                }}
              >
                {/* Inner white circle */}
                <div className='absolute top-0 left-0 w-4 h-4 bg-white rounded-full' />
              </div>
            )}
          </div>

          {/* Hidden Range Input */}
          <input
            ref={sliderRef}
            type='range'
            min={min}
            max={max}
            step={calculateStep()}
            value={value}
            onChange={e => {
              let newValue = parseFloat(e.target.value);
              // step이 소수점인 경우 그대로 사용, 정수인 경우 반올림
              const currentStep = calculateStep();
              if (currentStep % 1 === 0) {
                newValue = Math.round(newValue);
              }
              onChange?.(newValue);
            }}
            className='absolute inset-0 w-full h-full opacity-0 cursor-pointer'
            style={{ pointerEvents: 'auto' }}
            disabled={disabled}
          />

          {/* Caption */}
          <div className='flex justify-between mt-2 text-sm text-[#576072]'>
            <span>{min}</span>
            <span>{max}</span>
          </div>
        </div>

        {/* Text Input & Unit Text Container */}
        {showTextField && (
          <div className='flex items-center gap-2'>
            {/* Text Input */}
            <div className='w-[81px]'>
              <input
                type='text'
                value={inputText}
                onChange={e => {
                  const newValue = e.target.value;
                  setInputText(newValue);

                  // 빈 문자열이 아니고 숫자로 변환 가능한 경우만 onChange 호출
                  if (newValue !== '') {
                    const numVal = parseFloat(newValue);
                    if (!isNaN(numVal) && numVal >= min && numVal <= max) {
                      onChange?.(numVal);
                    }
                  }
                  onTextChange?.(newValue);
                }}
                onBlur={() => {
                  // blur 시 유효하지 않은 값이면 현재 value로 복구
                  const numVal = parseFloat(inputText);
                  if (inputText === '' || isNaN(numVal) || numVal < min || numVal > max) {
                    setInputText(textValue || formatValue(value));
                  } else {
                    // 유효한 값이면 포맷 적용
                    setInputText(formatValue(numVal));
                  }
                }}
                className='w-full h-10 px-3 text-sm text-[#242a34] bg-white border border-[#dce2ed] rounded-lg focus:outline-none'
                disabled={disabled}
              />
            </div>

            {/* Unit Text - 30px 고정 너비, 우측 정렬 */}
            {unit && (
              <div className='flex items-center justify-end w-[30px]'>
                <span className='text-sm text-[#576072] font-medium'>{unit}</span>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};
