import { UIButton2 } from '@/components/UI/atoms/UIButton2';
import { forwardRef, useEffect, useMemo, useRef, useState } from 'react';
import type { UIInputDatePickerProps } from './types';

export const Day = forwardRef<HTMLDivElement, Omit<UIInputDatePickerProps, 'type' | 'customButton'>>(
  ({ value, onChange, className = '', disabled = false, readOnly = false, maxDate, editable = false, highlightDays, calendarPosition = 'right' }, ref) => {
    const [isOpen, setIsOpen] = useState(false);
    const [inputValue, setInputValue] = useState('');
    const containerRef = useRef<HTMLDivElement>(null);
    const inputRef = useRef<HTMLInputElement>(null);
    const today = new Date();

    const parseValue = (value: string | undefined) => {
      const date = value ? new Date(value) : new Date();
      const year = date.getFullYear();
      const month = date.getMonth();
      const day = date.getDate();
      return { year, month, day };
    };

    // 외부 ref와 내부 ref 병합
    useEffect(() => {
      if (ref) {
        if (typeof ref === 'function') {
          ref(containerRef.current);
        } else {
          ref.current = containerRef.current;
        }
      }
    }, [ref]);

    const weekDays = useMemo(() => ['일', '월', '화', '수', '목', '금', '토'], []);

    // 실제 선택된 날짜 값 (value prop과 동기화)
    const [selectedValue, setSelectedValue] = useState(() => {
      return parseValue(value);
    });

    // 캘린더 뷰 상태 (연도/월 네비게이션용)
    const [viewState, setViewState] = useState(() => {
      const parsed = parseValue(value);
      return { year: parsed.year, month: parsed.month };
    });

    // maxDate 파싱 (YYYY.MM.DD 형식)
    const parseMaxDate = (maxDateStr?: string) => {
      if (!maxDateStr) return null;
      const m = maxDateStr.match(/^(\d{4})\.(\d{2})\.(\d{2})$/);
      if (!m) return null;
      const y = parseInt(m[1], 10);
      const mo = parseInt(m[2], 10) - 1;
      const d = parseInt(m[3], 10);
      const dt = new Date(y, mo, d);
      dt.setHours(0, 0, 0, 0);
      return dt;
    };

    const maxDateObj = parseMaxDate(maxDate);

    // value가 변경되면 selectedValue와 viewState 동기화
    useEffect(() => {
      if (value) {
        const parsed = parseValue(value);
        setSelectedValue(parsed);
        setViewState({ year: parsed.year, month: parsed.month });
      }
    }, [value]);

    // 선택된 날짜가 변경되면 inputValue 동기화
    useEffect(() => {
      if (editable) {
        setInputValue(formatDayDate());
      }
      // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [value, editable, selectedValue.year, selectedValue.month, selectedValue.day]);

    useEffect(() => {
      const handleClickOutside = (e: MouseEvent) => {
        if (containerRef.current && !containerRef.current.contains(e.target as Node)) {
          setIsOpen(false);
          // viewstate 초기화
          setViewState(() => {
            const parsed = parseValue(value);
            return { year: parsed.year, month: parsed.month };
          });
        }
      };
      document.addEventListener('mousedown', handleClickOutside);
      return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    // 해당 월의 일수 계산
    const getDaysInMonth = (y: number, m: number) => {
      return new Date(y, m + 1, 0).getDate();
    };

    // 해당 월의 첫째 날 요일
    const getFirstDayOfMonth = (y: number, m: number) => {
      return new Date(y, m, 1).getDay();
    };

    // 달력 날짜 배열 생성 (viewState 기준)
    const generateCalendarDays = () => {
      const daysInMonth = getDaysInMonth(viewState.year, viewState.month);
      const firstDay = getFirstDayOfMonth(viewState.year, viewState.month);
      const days: { day: number; isCurrentMonth: boolean; month: number; year: number }[] = [];

      // 이전 달의 날짜 추가
      if (firstDay > 0) {
        const prevMonth = viewState.month === 0 ? 11 : viewState.month - 1;
        const prevYear = viewState.month === 0 ? viewState.year - 1 : viewState.year;
        const prevMonthDays = getDaysInMonth(prevYear, prevMonth);
        for (let i = prevMonthDays - firstDay + 1; i <= prevMonthDays; i++) {
          days.push({ day: i, isCurrentMonth: false, month: prevMonth, year: prevYear });
        }
      }

      // 현재 달의 날짜 추가
      for (let i = 1; i <= daysInMonth; i++) {
        days.push({ day: i, isCurrentMonth: true, month: viewState.month, year: viewState.year });
      }

      // 다음 달의 날짜 추가 (7의 배수가 되도록)
      let nextMonthDay = 1;
      const nextMonth = viewState.month === 11 ? 0 : viewState.month + 1;
      const nextYear = viewState.month === 11 ? viewState.year + 1 : viewState.year;
      while (days.length % 7 !== 0) {
        days.push({ day: nextMonthDay, isCurrentMonth: false, month: nextMonth, year: nextYear });
        nextMonthDay++;
      }

      return days;
    };

    // 선택된 날짜를 포맷팅
    const formatDayDate = () => {
      return `${selectedValue.year}.${String(selectedValue.month + 1).padStart(2, '0')}.${String(selectedValue.day).padStart(2, '0')}`;
    };

    const handlePrevYear = (e: any) => {
      e.stopPropagation();
      if (!disabled && !readOnly) {
        setViewState(prev => ({ ...prev, year: prev.year - 1 }));
      }
    };

    const handleNextYear = (e: any) => {
      e.stopPropagation();
      if (!disabled && !readOnly) {
        setViewState(prev => ({ ...prev, year: prev.year + 1 }));
      }
    };

    const handlePrevMonth = (e: any) => {
      e.stopPropagation();
      if (!disabled && !readOnly) {
        if (viewState.month === 0) {
          setViewState(prev => ({ ...prev, month: 11, year: prev.year - 1 }));
        } else {
          setViewState(prev => ({ ...prev, month: prev.month - 1 }));
        }
      }
    };

    const handleNextMonth = (e: any) => {
      e.stopPropagation();
      if (!disabled && !readOnly) {
        if (viewState.month === 11) {
          setViewState(prev => ({ ...prev, month: 0, year: prev.year + 1 }));
        } else {
          setViewState(prev => ({ ...prev, month: prev.month + 1 }));
        }
      }
    };

    const handleSelectDay = (dateObj: { day: number; month: number; year: number } | null, e: any) => {
      if (e) e.stopPropagation();
      if (dateObj && !disabled && !readOnly) {
        setSelectedValue({ year: dateObj.year, month: dateObj.month, day: dateObj.day });
        setViewState({ year: dateObj.year, month: dateObj.month });
        onChange?.({
          target: {
            value: `${dateObj.year}.${String(dateObj.month + 1).padStart(2, '0')}.${String(dateObj.day).padStart(2, '0')}`,
          },
        } as React.ChangeEvent<HTMLInputElement>);
        setIsOpen(false);
      }
    };

    const handleClickCalendar = () => {
      if (!disabled && !readOnly) {
        setIsOpen(!isOpen);
      }
    };

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
      if (!editable || disabled || readOnly) return;

      let value = e.target.value;

      // 숫자와 점(.)만 허용
      value = value.replace(/[^\d.]/g, '');

      // 점의 개수 제한 (최대 2개)
      const dots = value.split('.').length - 1;
      if (dots > 2) return;

      // 최대 길이 제한 (YYYY.MM.DD = 10자리)
      if (value.length > 10) return;

      // 형식 검증 및 자릿수 제한
      const parts = value.split('.');
      if (parts.length > 0 && parts[0].length > 4) return; // 년도 4자리
      if (parts.length > 1 && parts[1].length > 2) return; // 월 2자리
      if (parts.length > 2 && parts[2].length > 2) return; // 일 2자리

      // 모든 입력을 inputValue에 반영 (사용자가 타이핑하는 중간 과정 표시)
      setInputValue(value);

      // YYYY.MM.DD 형식 검증
      const datePattern = /^(\d{4})\.(\d{2})\.(\d{2})$/;
      const match = value.match(datePattern);

      if (match) {
        const inputYear = parseInt(match[1], 10);
        const inputMonth = parseInt(match[2], 10) - 1; // 0-based month
        const inputDay = parseInt(match[3], 10);

        // 유효한 날짜인지 확인
        const testDate = new Date(inputYear, inputMonth, inputDay);
        const isValidDate = testDate.getFullYear() === inputYear && testDate.getMonth() === inputMonth && testDate.getDate() === inputDay;

        if (isValidDate) {
          // maxDate 체크
          if (maxDateObj) {
            const inputDateObj = new Date(inputYear, inputMonth, inputDay);
            inputDateObj.setHours(0, 0, 0, 0);
            if (inputDateObj > maxDateObj) {
              return; // maxDate 이후 날짜는 무시
            }
          }

          // 유효한 날짜면 상태 업데이트
          setSelectedValue({ year: inputYear, month: inputMonth, day: inputDay });
          setViewState({ year: inputYear, month: inputMonth });
          onChange?.({
            target: {
              value: `${inputYear}.${String(inputMonth + 1).padStart(2, '0')}.${String(inputDay).padStart(2, '0')}`,
            },
          } as React.ChangeEvent<HTMLInputElement>);
        }
      }
    };

    const handleInputBlur = () => {
      if (!editable || disabled || readOnly) return;

      // blur 시 유효하지 않은 입력이면 원래 값으로 복원
      const datePattern = /^(\d{4})\.(\d{2})\.(\d{2})$/;
      if (!datePattern.test(inputValue)) {
        setInputValue(formatDayDate());
      }
    };

    const calendarDays = generateCalendarDays();

    return (
      <div className='datepicker-wrap' ref={containerRef} style={{ position: 'relative' }}>
        <div className={`form-group readonly ${disabled ? 'disabled' : ''}`}>
          <div className='textfield'>
            <input
              ref={inputRef}
              type='text'
              title='텍스트 필드 타이틀'
              placeholder='날짜 입력'
              value={editable ? inputValue : formatDayDate()}
              readOnly={!editable}
              onClick={handleClickCalendar}
              onChange={handleInputChange}
              onBlur={handleInputBlur}
              className={className}
              style={{ cursor: disabled || readOnly ? 'not-allowed' : editable ? 'text' : 'pointer' }}
              disabled={disabled}
            />
            <button type='button' className='textfield-date' onClick={handleClickCalendar} disabled={disabled || readOnly}>
              <i className='ic-system-24-calender' aria-hidden='true' />
              <span className='visually-hidden'>날짜 선택</span>
            </button>
          </div>

          <div className='form-message' />
        </div>

        {/* 날짜 캘린더 팝업 */}
        {isOpen && (
          <div
            className='datepicker-month'
            style={{
              width: '324px',
              ...(calendarPosition === 'left' ? { left: 0, right: 'initial' } : { left: 'initial', right: 0 }),
            }}
          >
            {/* 연/월 네비게이션 */}
            <div className='datepicker-month-header'>
              {/* 이전년도 */}
              <UIButton2 className='ui-datepicker-prev' onClick={handlePrevYear}></UIButton2>

              {/* 이전달 */}
              <UIButton2 className='ui-datepicker-prev-month' onClick={handlePrevMonth}></UIButton2>

              {/* 가운데 날짜 */}
              <div className='year'>
                {viewState.year}년 {viewState.month + 1}월
              </div>

              {/* 다음달 */}
              <UIButton2 className='ui-datepicker-next-month' onClick={handleNextMonth}></UIButton2>

              {/* 다음년도 */}
              <UIButton2 className='ui-datepicker-next' onClick={handleNextYear}></UIButton2>
            </div>

            {/* 요일 헤더 */}
            <div className='datepicker-week'>
              {weekDays.map((weekDay, idx) => (
                <div key={idx} className={`day ${idx === 0 ? 'sun' : idx === 6 ? 'sat' : ''}`}>
                  {weekDay}
                </div>
              ))}
            </div>

            {/* 날짜 그리드 */}
            <div className='datepicker-date'>
              {calendarDays.map((dateObj, idx) => {
                const { day: d, isCurrentMonth, month: dateMonth, year: dateYear } = dateObj;
                const isSelected = dateYear === selectedValue.year && dateMonth === selectedValue.month && d === selectedValue.day;
                const isToday = dateYear === today.getFullYear() && dateMonth === today.getMonth() && d === today.getDate();
                const dayOfWeek = idx % 7;
                const isSunday = dayOfWeek === 0;
                const isSaturday = dayOfWeek === 6;

                // maxDate 이후 날짜 체크
                const isFutureMaxDate: boolean =
                  maxDateObj !== null &&
                  (dateYear > maxDateObj!.getFullYear() ||
                    (dateYear === maxDateObj!.getFullYear() && dateMonth > maxDateObj!.getMonth()) ||
                    (dateYear === maxDateObj!.getFullYear() && dateMonth === maxDateObj!.getMonth() && d > maxDateObj!.getDate()));

                const isDisabled: boolean = isFutureMaxDate;

                // highlightAfterDays: 선택된 날짜로부터 정확히 N일 이후 체크
                let isAfterRange = false;
                if (highlightDays?.after !== undefined) {
                  const selectedDate = new Date(selectedValue.year, selectedValue.month, selectedValue.day);
                  const currentDate = new Date(dateYear, dateMonth, d);
                  const diffTime = currentDate.getTime() - selectedDate.getTime();
                  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
                  isAfterRange = diffDays === highlightDays?.after;
                }

                // highlightBeforeDays: 선택된 날짜로부터 정확히 N일 이전 체크
                let isBeforeRange = false;
                if (highlightDays?.before !== undefined) {
                  const selectedDate = new Date(selectedValue.year, selectedValue.month, selectedValue.day);
                  const currentDate = new Date(dateYear, dateMonth, d);
                  const diffTime = selectedDate.getTime() - currentDate.getTime();
                  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
                  isBeforeRange = diffDays === highlightDays?.before;
                }

                return (
                  <button
                    key={idx}
                    className={`
                    datepicker-day
                    ${isDisabled ? 'disabled' : ''}
                    ${isSelected ? 'selected' : ''}
                    ${isToday ? 'today' : ''}
                    ${!isCurrentMonth ? 'other-month' : ''}
                    ${isSunday ? 'sun' : isSaturday ? 'sat' : ''}
                    ${isAfterRange ? 'date-on-after' : ''}
                    ${isBeforeRange ? 'date-on-before' : ''}
                  `}
                    onClick={e => handleSelectDay({ day: d, month: dateMonth, year: dateYear }, e)}
                    disabled={isDisabled}
                  >
                    {d}
                  </button>
                );
              })}
            </div>
          </div>
        )}
      </div>
    );
  }
);

Day.displayName = 'Day';

export const Month = forwardRef<HTMLDivElement, UIInputDatePickerProps>(
  ({ value, onChange, className = '', disabled = false, readOnly = false, maxDate, editable = false, calendarPosition = 'right' }, ref) => {
    const [isOpen, setIsOpen] = useState(false);
    const [inputValue, setInputValue] = useState('');
    const containerRef = useRef<HTMLDivElement>(null);
    const inputRef = useRef<HTMLInputElement>(null);
    const monthLabels = useMemo(() => ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'], []);

    const parseValue = (value: string | undefined) => {
      const date = value ? new Date(value) : new Date();
      const year = date.getFullYear();
      const month = date.getMonth();
      return { year, month };
    };

    // 외부 ref와 내부 ref 병합
    useEffect(() => {
      if (ref) {
        if (typeof ref === 'function') {
          ref(containerRef.current);
        } else {
          ref.current = containerRef.current;
        }
      }
    }, [ref]);

    // 실제 선택된 월 값 (value prop과 동기화)
    const [selectedValue, setSelectedValue] = useState(() => {
      return parseValue(value);
    });

    // 캘린더 뷰 상태 (연도 네비게이션용)
    const [viewYear, setViewYear] = useState(() => {
      return parseValue(value).year;
    });

    // maxDate 파싱 (YYYY.MM 형식)
    const parseMaxDate = (maxDateStr?: string) => {
      if (!maxDateStr) return null;
      const m = maxDateStr.match(/^(\d{4})\.(\d{2})$/);
      if (!m) return null;
      const y = parseInt(m[1], 10);
      const mo = parseInt(m[2], 10) - 1;
      return { year: y, month: mo };
    };

    const maxDateObj = parseMaxDate(maxDate);

    // value가 변경되면 selectedValue와 viewYear 동기화
    useEffect(() => {
      if (value) {
        const m = value.match(/^(\d{4})\.(\d{2})$/);
        if (m) {
          const year = parseInt(m[1], 10);
          const month = parseInt(m[2], 10) - 1;
          setSelectedValue({ year, month });
          setViewYear(year);
        }
      }
    }, [value]);

    // 선택된 값이 변경되면 inputValue 동기화
    useEffect(() => {
      if (editable) {
        setInputValue(formatMonthDate());
      }
      // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [value, editable, selectedValue.year, selectedValue.month]);

    useEffect(() => {
      const handleClickOutside = (e: MouseEvent) => {
        if (containerRef.current && !containerRef.current.contains(e.target as Node)) {
          setIsOpen(false);
          // viewstate 초기화
          setViewYear(() => {
            return parseValue(value).year;
          });
        }
      };
      document.addEventListener('mousedown', handleClickOutside);
      return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    // 선택된 월을 포맷팅
    const formatMonthDate = () => {
      return `${selectedValue.year}.${String(selectedValue.month + 1).padStart(2, '0')}`;
    };

    const handlePrevYear = (e: any) => {
      e.stopPropagation();
      if (!disabled && !readOnly) {
        setViewYear(prev => prev - 1);
      }
    };

    const handleNextYear = (e: any) => {
      e.stopPropagation();
      if (!disabled && !readOnly) {
        setViewYear(prev => prev + 1);
      }
    };

    const handleSelectMonth = (monthNum: number, e: any) => {
      e.stopPropagation();
      if (!disabled && !readOnly) {
        const selectedMonth = monthNum - 1;
        const newValue = `${viewYear}.${String(monthNum).padStart(2, '0')}`;
        setSelectedValue({ year: viewYear, month: selectedMonth });
        onChange?.({
          target: {
            value: newValue,
          },
        } as React.ChangeEvent<HTMLInputElement>);
        setIsOpen(false);
      }
    };

    const handleClickCalendar = () => {
      if (!disabled && !readOnly) {
        setIsOpen(!isOpen);
      }
    };

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
      if (!editable || disabled || readOnly) return;

      let value = e.target.value;

      // 숫자와 점(.)만 허용
      value = value.replace(/[^\d.]/g, '');

      // 점의 개수 제한 (최대 1개)
      const dots = value.split('.').length - 1;
      if (dots > 1) return;

      // 최대 길이 제한 (YYYY.MM = 7자리)
      if (value.length > 7) return;

      // 형식 검증 및 자릿수 제한
      const parts = value.split('.');
      if (parts.length > 0 && parts[0].length > 4) return; // 년도 4자리
      if (parts.length > 1 && parts[1].length > 2) return; // 월 2자리

      // 모든 입력을 inputValue에 반영 (사용자가 타이핑하는 중간 과정 표시)
      setInputValue(value);

      // YYYY.MM 형식 검증
      const datePattern = /^(\d{4})\.(\d{2})$/;
      const match = value.match(datePattern);

      if (match) {
        const inputYear = parseInt(match[1], 10);
        const inputMonth = parseInt(match[2], 10) - 1; // 0-based month

        // 유효한 월인지 확인 (1-12)
        if (inputMonth < 0 || inputMonth > 11) return;

        // maxDate 체크
        if (maxDateObj) {
          if (inputYear > maxDateObj.year || (inputYear === maxDateObj.year && inputMonth > maxDateObj.month)) {
            return; // maxDate 이후 월은 무시
          }
        }

        // 유효한 월이면 상태 업데이트
        setSelectedValue({ year: inputYear, month: inputMonth });
        setViewYear(inputYear);
        onChange?.({
          target: {
            value: `${inputYear}.${String(inputMonth + 1).padStart(2, '0')}`,
          },
        } as React.ChangeEvent<HTMLInputElement>);
      }
    };

    const handleInputBlur = () => {
      if (!editable || disabled || readOnly) return;

      // blur 시 유효하지 않은 입력이면 원래 값으로 복원
      const datePattern = /^(\d{4})\.(\d{2})$/;
      if (!datePattern.test(inputValue)) {
        setInputValue(formatMonthDate());
      }
    };

    return (
      <div className='datepicker-wrap' ref={containerRef} style={{ position: 'relative' }}>
        <div className={`form-group readonly ${disabled ? 'disabled' : ''}`}>
          <div className='textfield'>
            <input
              ref={inputRef}
              type='text'
              title='텍스트 필드 타이틀'
              placeholder='월 입력'
              value={editable ? inputValue : formatMonthDate()}
              readOnly={!editable}
              onClick={handleClickCalendar}
              onChange={handleInputChange}
              onBlur={handleInputBlur}
              className={className}
              style={{ cursor: disabled || readOnly ? 'not-allowed' : editable ? 'text' : 'pointer' }}
              disabled={disabled}
            />
            <button type='button' className='textfield-date' onClick={handleClickCalendar} disabled={disabled || readOnly}>
              <i className='ic-system-24-calender' aria-hidden='true' />
              <span className='visually-hidden'>월 선택</span>
            </button>
          </div>

          <div className='form-message' />
        </div>

        {/* 월별 캘린더 팝업 */}
        {isOpen && (
          <div
            className='datepicker-month'
            style={{
              ...(calendarPosition === 'left' ? { left: 0, right: 'initial' } : { left: 'initial', right: 0 }),
            }}
          >
            {/* 연도 선택 영역 */}
            <div className='datepicker-month-header'>
              <UIButton2 className='ui-datepicker-prev' onClick={handlePrevYear}></UIButton2>
              <div className='year'>{viewYear}년</div>
              <UIButton2 className='ui-datepicker-next' onClick={handleNextYear}></UIButton2>
            </div>

            {/* 월별 그리드 */}
            <div className='datepicker-month-content'>
              {monthLabels.map((label, index) => {
                const monthNum = index + 1;
                const isSelected = selectedValue.year === viewYear && selectedValue.month === index;

                // maxDate 이후 월 체크
                const isFutureMaxDate: boolean = maxDateObj !== null && (viewYear > maxDateObj.year || (viewYear === maxDateObj.year && index > maxDateObj.month));

                const isDisabled: boolean = isFutureMaxDate;

                return (
                  <UIButton2
                    key={monthNum}
                    onClick={(e: any) => handleSelectMonth(monthNum, e)}
                    className='text-caption-2'
                    disabled={isDisabled}
                    style={{
                      backgroundColor: isSelected ? '#005DF9' : '#ffffff',
                      color: isSelected ? '#ffffff' : isDisabled ? '#d1d5db' : '#242A34',
                      cursor: isDisabled ? 'not-allowed' : 'pointer',
                    }}
                    onMouseEnter={e => {
                      if (!isSelected && !isDisabled) {
                        (e.target as HTMLButtonElement).style.backgroundColor = '#f3f4f6';
                      }
                    }}
                    onMouseLeave={e => {
                      if (!isSelected && !isDisabled) {
                        (e.target as HTMLButtonElement).style.backgroundColor = '#ffffff';
                      }
                    }}
                  >
                    {label}
                  </UIButton2>
                );
              })}
            </div>
          </div>
        )}
      </div>
    );
  }
);

Month.displayName = 'Month';
