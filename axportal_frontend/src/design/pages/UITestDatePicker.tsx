import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIInput } from '@/components/UI/molecules/input/UIInput';
import { useEffect, useRef, useState } from 'react';
import { DesignLayout } from '../components/DesignLayout';

export const UITestDatePicker = () => {
  // 월별 피커 - default 상태
  const [monthYear, setMonthYear] = useState('2025.11');

  // 월별 피커 - disabled 상태
  const [disabledMonthYear] = useState(2025);
  const [disabledMonth] = useState(11);

  // 일별 피커 - default 상태
  const [selectedDay, setSelectedDay] = useState('2025.11.27');

  // 일별 피커 - maxDate 상태 (독립적 제어)
  const [maxDateSelectedDay, setMaxDateSelectedDay] = useState('2025.11.27');

  // 일별 피커 - disabled 상태
  const [disabledDayYear] = useState(2025);
  const [disabledDayMonth] = useState(10);
  const [disabledDay] = useState(27);

  // 일별 범위 피커 (7일 범위) - 시작일
  const [rangeStartYear, setRangeStartYear] = useState(2025);
  const [rangeStartMonth, setRangeStartMonth] = useState(10);
  const [rangeStartDay, setRangeStartDay] = useState(20);

  // 일별 범위 피커 (7일 범위) - 종료일
  const [rangeEndYear, setRangeEndYear] = useState(2025);
  const [rangeEndMonth, setRangeEndMonth] = useState(10);
  const [rangeEndDay, setRangeEndDay] = useState(27);

  // 어느 쪽 날짜가 변경되었는지 추적
  const lastChangedRef = useRef<'start' | 'end'>('start');

  // 날짜에 일수를 더하거나 빼는 함수
  const addDaysToDate = (year: number, month: number, day: number, days: number) => {
    const date = new Date(year, month, day);
    date.setDate(date.getDate() + days);
    return {
      year: date.getFullYear(),
      month: date.getMonth(),
      day: date.getDate(),
    };
  };

  // 시작일이 변경되면 종료일을 7일 뒤로 자동 설정
  useEffect(() => {
    if (lastChangedRef.current === 'start') {
      const newEndDate = addDaysToDate(rangeStartYear, rangeStartMonth, rangeStartDay, 7);
      setRangeEndYear(newEndDate.year);
      setRangeEndMonth(newEndDate.month);
      setRangeEndDay(newEndDate.day);
    }
  }, [rangeStartYear, rangeStartMonth, rangeStartDay]);

  // 종료일이 변경되면 시작일을 7일 이전으로 자동 설정
  useEffect(() => {
    if (lastChangedRef.current === 'end') {
      const newStartDate = addDaysToDate(rangeEndYear, rangeEndMonth, rangeEndDay, -7);
      setRangeStartYear(newStartDate.year);
      setRangeStartMonth(newStartDate.month);
      setRangeStartDay(newStartDate.day);
    }
  }, [rangeEndYear, rangeEndMonth, rangeEndDay]);

  // 날짜 문자열을 파싱하는 헬퍼 함수
  const parseDateString = (dateStr: string) => {
    const match = dateStr.match(/^(\d{4})\.(\d{2})\.(\d{2})$/);
    if (match) {
      return {
        year: parseInt(match[1], 10),
        month: parseInt(match[2], 10) - 1, // 0-based month
        day: parseInt(match[3], 10),
      };
    }
    return null;
  };

  // 범위 피커 시작일 변경 핸들러
  const handleRangeStartDayChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const dateObj = parseDateString(e.target.value);
    if (dateObj) {
      lastChangedRef.current = 'start';
      setRangeStartYear(dateObj.year);
      setRangeStartMonth(dateObj.month);
      setRangeStartDay(dateObj.day);
    }
  };

  // 범위 피커 종료일 변경 핸들러
  const handleRangeEndDayChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const dateObj = parseDateString(e.target.value);
    if (dateObj) {
      lastChangedRef.current = 'end';
      setRangeEndYear(dateObj.year);
      setRangeEndMonth(dateObj.month);
      setRangeEndDay(dateObj.day);
    }
  };

  return (
    <DesignLayout>
      <section className='section-page pub-guide'>
        <UIPageHeader title='DatePicker 컴포넌트 예시' description='커스텀 날짜 피커 가이드입니다.' />

        <UIPageBody>
          {/* 월별 */}
          <UIArticle>
            <div className='guide-section'>
              <h2 className='guide-title'>월별</h2>
              <div className='guide-content'>
                <div className='flex gap-12'>
                  {/* Default */}
                  <div className='flex-1'>
                    <label className='block text-sm font-semibold mb-2'>Default</label>
                    <UIInput.Date type='MONTH' value={monthYear} onChange={e => setMonthYear(e.target.value)} />
                  </div>

                  {/* Disabled */}
                  <div className='flex-1'>
                    <label className='block text-sm font-semibold mb-2'>Disabled</label>
                    <UIInput.Date type='MONTH' value={`${disabledMonthYear}.${String(disabledMonth + 1).padStart(2, '0')}`} onChange={() => {}} disabled />
                  </div>
                </div>
              </div>
            </div>
          </UIArticle>

          <hr style={{ margin: '40px 0', borderColor: '#e5e7eb' }} />

          {/* 일별 */}
          <UIArticle>
            <div className='guide-section'>
              <h2 className='guide-title'>일별 (직접 수정 X / maxDate)</h2>
              <div className='guide-content'>
                <div className='flex gap-12'>
                  {/* Default */}
                  <div className='flex-1'>
                    <label className='block text-sm font-semibold mb-2'>Default</label>
                    <UIInput.Date value={selectedDay} onChange={e => setSelectedDay(e.target.value)} />
                  </div>

                  {/* maxDate */}
                  <div className='flex-1'>
                    <label className='block text-sm font-semibold mb-2'>maxDate (2026.02.01)</label>
                    <UIInput.Date value={maxDateSelectedDay} onChange={e => setMaxDateSelectedDay(e.target.value)} maxDate='2026.02.01' />
                  </div>

                  {/* Disabled */}
                  <div className='flex-1'>
                    <label className='block text-sm font-semibold mb-2'>Disabled</label>
                    <UIInput.Date
                      value={`${disabledDayYear}.${String(disabledDayMonth + 1).padStart(2, '0')}.${String(disabledDay).padStart(2, '0')}`}
                      onChange={() => {}}
                      disabled
                    />
                  </div>
                </div>
              </div>
            </div>
          </UIArticle>

          {/* 일별 : 날짜 직접 입력 */}
          <UIArticle>
            <div className='guide-section'>
              <h2 className='guide-title'>일별 : 날짜 직접 입력</h2>
              <div className='guide-content'>
                <div className='flex gap-12'>
                  {/* Default */}
                  <div className='flex-1'>
                    <label className='block text-sm font-semibold mb-2'>date start</label>
                    <UIInput.Date value={selectedDay} onChange={e => setSelectedDay(e.target.value)} editable />
                  </div>
                  <div className='flex-1'>
                    <label className='block text-sm font-semibold mb-2'>date end</label>
                    <UIInput.Date value={selectedDay} onChange={e => setSelectedDay(e.target.value)} editable />
                  </div>

                  {/* Disabled */}
                  <div className='flex-1'>
                    <label className='block text-sm font-semibold mb-2'>Disabled</label>
                    <UIInput.Date
                      value={`${disabledDayYear}.${String(disabledDayMonth + 1).padStart(2, '0')}.${String(disabledDay).padStart(2, '0')}`}
                      onChange={() => {}}
                      disabled
                    />
                  </div>
                </div>
              </div>
            </div>
          </UIArticle>

          {/* 일별 : 7일 이후 / 7일 이전 */}
          <UIArticle>
            <div className='guide-section'>
              <h2 className='guide-title'>일별 범위 : 7일 이후 / 7일 이전</h2>
              <div className='guide-content'>
                <div className='flex gap-12'>
                  {/* 시작일 (7일 이후 강조) */}
                  <div className='flex-1'>
                    <label className='block text-sm font-semibold mb-2'>date start (7일 이후)</label>
                    <UIInput.Date
                      value={`${rangeStartYear}.${String(rangeStartMonth + 1).padStart(2, '0')}.${String(rangeStartDay).padStart(2, '0')}`}
                      onChange={handleRangeStartDayChange}
                      editable
                      highlightDays={{ after: 7 }}
                    />
                  </div>
                  {/* 종료일 (7일 이전 강조) */}
                  <div className='flex-1'>
                    <label className='block text-sm font-semibold mb-2'>date end (7일 이전)</label>
                    <UIInput.Date
                      value={`${rangeEndYear}.${String(rangeEndMonth + 1).padStart(2, '0')}.${String(rangeEndDay).padStart(2, '0')}`}
                      onChange={handleRangeEndDayChange}
                      editable
                      highlightDays={{ before: 7 }}
                    />
                  </div>
                </div>
              </div>
            </div>
          </UIArticle>

          <UIArticle>
            <div style={{ padding: '300px 0' }}></div>
          </UIArticle>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
