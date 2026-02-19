import { forwardRef, useEffect, useMemo, useRef, useState } from 'react';

import { UIButton2, UIIcon2, UITypography } from '@/components/UI/atoms';
import { UIFormField } from '../../UIFormField';
import { UIUnitGroup } from '../../UIUnitGroup';
import { Day, Month } from './datepicker';
import type { UIInputDatePickerProps, UIInputFieldProps, UIInputTagsProps } from './types';

const Tags = forwardRef<HTMLInputElement, UIInputTagsProps>(
  (
    {
      tags,
      onChange,
      placeholder = '태그 입력',
      disabled,
      readOnly,
      error,
      helperText,
      helperTextType = 'default',
      helperTextSpacing = 'default',
      label,
      required = false,
      onFocus,
      onBlur,
      buttonText = '추가',
      onButtonClick,
      maxLength = 8,
      ...props
    },
    ref
  ) => {
    // const TAG_MAX_LENGTH = 8;
    // const innerMaxLength = maxLength;
    // console.log('innerMaxLength', innerMaxLength);
    const [newTag, setNewTag] = useState('');

    const [isTagLengthError, setIsTagLengthError] = useState(false);

    // 태그 글자 수 초과 에러 검증
    const effectiveError = error || (isTagLengthError ? `태그 하나 당 최대 ${maxLength}자까지 입력 가능합니다.` : '');

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
      if (disabled || readOnly || tags.length === 7) return;
      setIsTagLengthError(e.target.value.length > maxLength);
      setNewTag(e.target.value);
    };

    const handleFocus = (e: React.FocusEvent<HTMLInputElement>) => {
      if (disabled || readOnly) return;
      onFocus?.(e);
    };

    const handleBlur = (e: React.FocusEvent<HTMLInputElement>) => {
      if (disabled || readOnly) return;
      onBlur?.(e);
    };

    const handleTagAdd = () => {
      if (isTagLengthError) return;
      if (newTag.trim()) {
        onChange?.([...tags, newTag.trim()]);
        setNewTag('');
      }
    };

    const handleTagRemove = (index: number) => {
      onChange?.(tags.filter((_, i) => i !== index));
    };

    return (
      <UIFormField gap={8} direction='column'>
        {/* 라벨 */}
        {label && (
          <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={required}>
            {label}
          </UITypography>
        )}
        <div>
          <UIUnitGroup gap={8} direction='row'>
            <div className='form-group' style={{ flex: 1 }}>
              <div className={`textfield `}>
                {/* 기존 태그들 */}
                {tags.map((tag, index) => (
                  <div key={index} className='flex items-center gap-1 bg-gray-50 border border-gray-200 text-[#576072] rounded-md px-3 py-2 flex-shrink-0 h-8'>
                    <span className='text-sm font-normal leading-5 tracking-[-0.005em]'>{tag}</span>
                    <button onClick={() => handleTagRemove?.(index)} className='flex items-center justify-center w-4 h-4 rounded ml-1' disabled={disabled || readOnly}>
                      <UIIcon2 className='ic-system-12-close' />
                    </button>
                  </div>
                ))}

                {/* 태그 입력 */}
                <input
                  ref={ref}
                  type='text'
                  {...props}
                  value={newTag}
                  onChange={handleChange}
                  onFocus={handleFocus}
                  onBlur={handleBlur}
                  placeholder={tags.length === 0 ? placeholder : ''}
                  disabled={disabled}
                  readOnly={readOnly}
                  className='flex-1 min-w-0 outline-none text-base leading-5 placeholder:text-gray-500'
                  onKeyDown={e => {
                    // 최대 글자 수에 도달했을 때 추가 문자 입력 시도 감지
                    if (isTagLengthError && e.key.length === 1 && !e.ctrlKey && !e.metaKey && !e.altKey) {
                      return;
                    }

                    if (e.key === 'Enter') {
                      e.preventDefault();
                      // 한글 조합형 이벤트 분류 (엔터키 이벤트 중복발생 방지)
                      if (e.nativeEvent.isComposing) {
                        return;
                      }
                      if (!isTagLengthError && newTag.trim()) {
                        handleTagAdd?.();
                      }
                    }
                    if (e.key === 'Backspace') {
                      if (!newTag && tags.length > 0) {
                        handleTagRemove?.(tags.length - 1);
                      } else {
                        // 백스페이스로 글자를 지우면 에러 상태 해제
                        setIsTagLengthError(false);
                      }
                    }
                  }}
                />
              </div>

              {(effectiveError || helperText) && (
                <div className={`${helperTextSpacing === 'none' ? 'mt-0' : 'mt-1'} flex justify-between items-center text-caption-1`}>
                  <div className={`pl-3 ${helperTextSpacing === 'none' ? 'pt-0' : 'pt-1'}`}>
                    {effectiveError ? (
                      <span className='text-negative-red text-[14px] font-normal'>{effectiveError}</span>
                    ) : helperText ? (
                      helperTextType === 'bullet' ? (
                        <div className='flex items-center gap-2'>
                          <div className='w-1 h-1 bg-gray-900 rounded-full flex-shrink-0' />
                          <span className='text-gray-900 text-[14px] font-normal leading-5 tracking-[-0.14px]'>{helperText}</span>
                        </div>
                      ) : (
                        <span className='text-gray-500'>{helperText}</span>
                      )
                    ) : null}
                  </div>
                </div>
              )}
            </div>

            {/* 추가 버튼 */}
            <div>
              <UIButton2 className='btn-secondary-outline !min-w-[64px]' onClick={onButtonClick ? onButtonClick : handleTagAdd} disabled={disabled || readOnly}>
                {buttonText}
              </UIButton2>
            </div>
          </UIUnitGroup>
        </div>
      </UIFormField>
    );
  }
);

const Password = forwardRef<HTMLInputElement, Omit<UIInputFieldProps, 'type'>>((props, ref) => {
  return <UIInputField type='password' {...props} ref={ref} />;
});

const DatePicker = forwardRef<HTMLInputElement, Omit<UIInputDatePickerProps, 'customButton'>>(
  // ({ value, onChange, disabled = false, readOnly = true, size = 'default', calendarLeftPosition = 'initial', className, dateType, maxDate, ...props }, ref) => {
  ({ type = 'DAY', ...props }, ref) => {
    return type === 'DAY' ? <Day {...props} ref={ref} /> : <Month {...props} ref={ref} />;
  }
);

const Search = forwardRef<HTMLInputElement, Omit<UIInputFieldProps, 'type'>>(({ maxLength = 50, ...props }, ref) => {
  const MAX_LENGTH = 50;
  return (
    <UIInputField
      type='search'
      {...props}
      ref={ref}
      maxLength={MAX_LENGTH}
      onChange={e => {
        props.onChange?.({ ...e, target: { ...e.target, value: e.target.value.slice(0, MAX_LENGTH) } } as React.ChangeEvent<HTMLInputElement>);
      }}
    />
  );
});

const Text = forwardRef<HTMLInputElement, UIInputFieldProps>(({ type = 'text', ...props }, ref) => {
  return <UIInputField type={type} {...props} ref={ref} />;
});

const Auth = forwardRef<
  HTMLInputElement,
  { status?: 'init' | 'processing' | 'done' | 'error'; timer?: string; onClickAuthRequest?: () => void; authButtonDisabled?: boolean; authButtonText?: string } & UIInputFieldProps
>(
  (
    {
      status = 'init',
      timer = '00:00',
      disabled = false,
      readOnly = false,
      onClickAuthRequest,
      size = 'default',
      value,
      authButtonDisabled = false,
      authButtonText = '인증 요청',
      ...props
    },
    ref
  ) => {
    const handleClickAuthRequest = () => {
      if (disabled || readOnly) return;
      onClickAuthRequest?.();
    };

    // status가 'processing' 또는 'done'일 때만 타이머와 버튼 표시
    const shouldShowAuthElements = status === 'processing' || status === 'done' || status === 'error';

    return (
      <UIInput.Text
        type='custom'
        ref={ref}
        disabled={disabled}
        readOnly={readOnly}
        size={size}
        value={value}
        {...props}
        customButton={
          shouldShowAuthElements ? (
            <>
              {status === 'processing' && <span className='textfield-timer text-body-2'>{timer}</span>}
              {status === 'done' && <i className='ic-system-24-outline-blue-check' aria-hidden='true' />}
              <button type='button' className='btn-option-outlined' disabled={readOnly || disabled || authButtonDisabled} onClick={handleClickAuthRequest}>
                {authButtonText}
              </button>
            </>
          ) : null
        }
      />
    );
  }
);

const UIInputField = forwardRef<HTMLInputElement, UIInputFieldProps>(
  (
    { type = 'text', placeholder = '날짜 입력', value, onChange, onBlur, onFocus, maxLength, disabled, readOnly, error, customButton, size = 'default', className, ...props },
    ref
  ) => {
    // 입력 값 삭제
    const handleClear = (e: React.MouseEvent<HTMLButtonElement>) => {
      e.stopPropagation();
      onChange?.({ target: { value: '' } } as React.ChangeEvent<HTMLInputElement>);
    };

    const isOver = useMemo(() => maxLength && value && value.length > maxLength, [maxLength, value]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
      if (disabled || readOnly) return;

      if (maxLength) {
        onChange?.({ ...e, target: { ...e.target, value: e.target.value.slice(0, maxLength + 1) } } as React.ChangeEvent<HTMLInputElement>);
      } else {
        onChange?.(e);
      }
    };

    const handleFocus = (e: React.FocusEvent<HTMLInputElement>) => {
      if (disabled || readOnly) return;
      onFocus?.(e);
      setIsFocused(true);
    };

    const handleBlur = (e: React.FocusEvent<HTMLInputElement>) => {
      if (disabled || readOnly) return;
      setIsFocused(false);

      onBlur?.({ ...e, target: { ...e.target, value: e.target.value.slice(0, maxLength || e.target.value.length) } } as React.FocusEvent<HTMLInputElement>);
    };

    // input ref를 위한 내부 ref
    const inputRef = useRef<HTMLInputElement>(null);

    // forwardRef와 내부 ref 동기화
    useEffect(() => {
      if (typeof ref === 'function') {
        ref(inputRef.current);
      } else if (ref) {
        ref.current = inputRef.current;
      }
    }, [ref]);

    // Tab 키로 이동 시 blur 처리
    const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
      if (e.key === 'Tab') {
        setIsFocused(false);
        // blur 이벤트를 명시적으로 트리거하기 위해 다음 이벤트 루프에서 처리
        setTimeout(() => {
          if (inputRef.current && document.activeElement !== inputRef.current) {
            // 실제 blur 이벤트가 발생하도록 input 요소의 blur() 호출
            // 이렇게 하면 handleBlur가 자동으로 호출됨
            inputRef.current.blur();
          }
        }, 0);
      }
      // 기존 onKeyDown 핸들러가 있다면 호출
      if (props.onKeyDown) {
        props.onKeyDown(e);
      }
    };

    // 내부 타입 관리
    const [internalType, setInternalType] = useState(type === 'password' ? 'password' : type === 'number' ? 'number' : 'text');

    // focus 상태 관리
    const [isFocused, setIsFocused] = useState(false);

    // 최대값 지정 되어있을 경우 입력 값 제한
    useEffect(() => {
      if (maxLength && !isFocused) {
        onChange?.({ target: { value: value?.slice(0, maxLength) } } as React.ChangeEvent<HTMLInputElement>);
      }
    }, [maxLength, isFocused, value]);

    // 비밀번호 보기 토글
    const handleVisible = () => {
      if (disabled || readOnly) return;
      setIsFocused(false);
      setInternalType(prev => (prev === 'password' ? 'text' : 'password'));
    };

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

    return (
      <div className={`form-group ${cssStatus}`}>
        <div className={`textfield ${size === 'small' ? 'textfield_small' : ''}`}>
          {/* search */}
          {type === 'search' && <i className={disabled || readOnly ? 'ic-system-24-outline-gray-search' : 'ic-system-24-outline-search'} aria-hidden='true' />}

          {/* Input 영역 */}
          <input
            ref={inputRef}
            type={internalType}
            title='텍스트 필드 타이틀'
            placeholder={placeholder}
            value={value || ''}
            onChange={handleChange}
            onFocus={handleFocus}
            onBlur={handleBlur}
            onKeyDown={handleKeyDown}
            disabled={disabled}
            readOnly={readOnly}
            className={className}
            {...props}
          />

          {/* 버튼 영역 */}
          {value && isFocused && (
            <button type='button' className='textfield-clear' onMouseDown={handleClear} disabled={disabled || readOnly}>
              <i className='ic-system-24-clear' aria-hidden='true' />
              <span className='visually-hidden'>입력 값 삭제</span>
            </button>
          )}
          {type === 'password' && value && (
            <button type='button' className='textfield-visibility' onClick={handleVisible} disabled={disabled || readOnly}>
              {internalType === 'password' ? (
                <>
                  <i className={disabled || readOnly ? 'ic-system-24-outline-gray-view' : 'ic-system-24-outline-view'} aria-hidden='true' />
                  <span className='visually-hidden'>비밀번호 보기</span>
                </>
              ) : (
                <>
                  <i className={disabled || readOnly ? 'ic-system-24-outline-gray-view-off' : 'ic-system-24-outline-view-off'} aria-hidden='true' />
                  <span className='visually-hidden'>비밀번호 숨기기</span>
                </>
              )}
            </button>
          )}
          {customButton}
        </div>

        <div className={`form-message ${error || isOver ? 'error' : ''}`}>
          {/* 오류 메시지 영역 */}
          {(error || isOver) && (
            <div className='desc-message'>
              <span className='error-text'>{error || `최대 ${maxLength}자까지 입력 가능합니다.`}</span>
            </div>
          )}

          {/* 카운트 영역 */}
          {/* {!(disabled || readOnly) && (
            <div className='count-text'>
              <span className={`current ${error || isOver ? 'error' : ''}`}>{value?.length}자</span>/<span className='total'>{maxLength}자</span>
            </div>
          )} */}
        </div>
      </div>
    );
  }
);

Tags.displayName = 'UIInput.Tags';
Password.displayName = 'UIInput.Password';
DatePicker.displayName = 'UIInput.Date';
// Month.displayName = 'UIInput.Month';
Search.displayName = 'UIInput.Search';
Text.displayName = 'UIInput.Text';
Auth.displayName = 'UIInput.Auth';

const UIInput = {
  Text,
  Tags,
  Password,
  Date: DatePicker,
  // Month,
  Search,
  Auth,
};

export { UIInput };
