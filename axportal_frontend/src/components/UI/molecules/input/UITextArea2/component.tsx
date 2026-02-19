import { forwardRef, useMemo, useState, useEffect, useRef } from 'react';
import type { UITextArea2Props } from './types';

export const UITextArea2 = forwardRef<HTMLTextAreaElement, UITextArea2Props>(
  (
    {
      value,
      onChange,
      onBlur,
      onFocus,
      hint,
      placeholder,
      maxLength,
      disabled,
      readOnly,
      error,
      className = '',
      lineType = 'multi-line',
      rows = 4,
      resizable = true,
      noBorder = false,
      enableScrollFade = false,
      ...props
    },
    ref
  ) => {
    // 숫자 천 단위 콤마 포맷팅
    const formatNumber = (num: number) => {
      return num.toLocaleString('ko-KR');
    };

    // 입력 값 삭제
    const handleClear = (e: React.MouseEvent<HTMLButtonElement>) => {
      e.stopPropagation();
      e.preventDefault();
      onChange?.({ target: { value: '' } } as React.ChangeEvent<HTMLTextAreaElement>);
    };

    const isOver = useMemo(() => maxLength && value && value.length > maxLength, [maxLength, value]);

    const handleChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
      if (disabled || readOnly) return;
      onChange?.({ ...e, target: { ...e.target, value: e.target.value.slice(0, maxLength || e.target.value.length) } } as React.ChangeEvent<HTMLTextAreaElement>);
    };

    const handleFocus = (e: React.FocusEvent<HTMLTextAreaElement>) => {
      if (disabled || readOnly) return;
      onFocus?.(e);
      setIsFocused(true);
    };

    const handleBlur = (e: React.FocusEvent<HTMLTextAreaElement>) => {
      if (disabled || readOnly) return;
      setIsFocused(false);
      onBlur?.(e);
    };

    // focus 상태 관리
    const [isFocused, setIsFocused] = useState(false);

    // 스크롤 fade 효과를 위한 wrapper ref
    const wrapperRef = useRef<HTMLDivElement>(null);
    const textareaRef = useRef<HTMLTextAreaElement>(null);

    // 스크롤 fade 효과 처리
    useEffect(() => {
      if (!enableScrollFade) return;

      const wrapperElement = wrapperRef.current;
      const textareaElement = textareaRef.current;

      if (!wrapperElement || !textareaElement) return;

      const handleScrollFade = () => {
        // 포커스 상태일 때는 fade 효과 제거
        if (isFocused) {
          wrapperElement.classList.remove('fade-bottom', 'fade-top');
          return;
        }

        const hasScroll = textareaElement.scrollHeight > textareaElement.clientHeight;
        const isAtTop = textareaElement.scrollTop <= 1;
        const isAtBottom = textareaElement.scrollTop + textareaElement.clientHeight >= textareaElement.scrollHeight - 1;

        if (hasScroll) {
          if (isAtBottom) {
            wrapperElement.classList.remove('fade-bottom');
            wrapperElement.classList.add('fade-top');
          } else if (isAtTop) {
            wrapperElement.classList.remove('fade-top');
            wrapperElement.classList.add('fade-bottom');
          }
        } else {
          wrapperElement.classList.remove('fade-bottom', 'fade-top');
        }
      };

      textareaElement.addEventListener('scroll', handleScrollFade);

      // 초기 상태 설정
      handleScrollFade();

      return () => {
        textareaElement.removeEventListener('scroll', handleScrollFade);
      };
    }, [enableScrollFade, isFocused]);

    const cssStatus = useMemo(() => {
      if (disabled) {
        return 'disabled';
      }
      if (readOnly) {
        return 'readonly';
      }
      if (error || isOver) {
        return 'error';
      }
      return '';
    }, [disabled, readOnly, error, isOver]);

    // ref 병합 처리
    const mergedRef = (node: HTMLTextAreaElement) => {
      textareaRef.current = node;
      if (typeof ref === 'function') {
        ref(node);
      } else if (ref) {
        ref.current = node;
      }
    };

    return (
      <div className={'form-group ' + cssStatus + (className ? ' ' + className : '')}>
        <div
          ref={enableScrollFade ? wrapperRef : null}
          className={'textfield ' + lineType + (enableScrollFade ? ' box-article' : '')}
          style={noBorder ? { border: 'none', borderTop: '1px solid var(--color-gray-200)', borderRadius: 0, padding: '12px 0px' } : undefined}
        >
          <textarea
            ref={mergedRef}
            {...props}
            placeholder={placeholder}
            maxLength={maxLength}
            rows={rows}
            value={value}
            onChange={handleChange}
            onFocus={handleFocus}
            onBlur={handleBlur}
            disabled={disabled}
            readOnly={readOnly}
            style={{ resize: resizable ? 'vertical' : 'none', whiteSpace: 'pre-wrap', ...props.style }}
          />

          {/* clear */}
          {value && isFocused && (
            <button type='button' className='textfield-clear' onMouseDown={handleClear}>
              <i className='ic-system-24-clear' aria-hidden='true' />
            </button>
          )}
        </div>
        <div className='form-message'>
          <div className='desc-message'>
            {/* hint */}
            {hint && <span className='bullet-text'>{hint}</span>}
            {/* error */}
            {error && <span className='error-text'>{error}</span>}
          </div>
          {/* maxlength */}
          {maxLength && (
            <div className='count-text'>
              <span className='current'>{formatNumber(value?.length || 0)}자</span>/<span className='total'>{formatNumber(maxLength)}자</span>
            </div>
          )}
        </div>
      </div>
    );
  }
);

UITextArea2.displayName = 'UITextArea2';
