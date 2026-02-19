/**
 * 날짜 관련 유틸리티 함수들
 *
 * 두 가지 주요 기능을 제공합니다:
 *
 * 1. **Date-fns 기반 날짜 처리**:
 *    - 날짜 포맷팅, 상대 시간, 날짜 계산, 비교 등
 *
 * 2. **IMask 기반 패턴 포맷팅**:
 *    - 시간/날짜 입력값에 구분자 추가 (HH:mm:ss, YYYY-MM-DD)
 */

import {
  addDays,
  addMonths,
  addYears,
  differenceInDays,
  differenceInHours,
  differenceInMinutes,
  endOfDay,
  endOfMonth,
  endOfWeek,
  format,
  formatDistanceToNow,
  formatRelative,
  isAfter,
  isBefore,
  parseISO,
  startOfDay,
  startOfMonth,
  startOfWeek,
  subDays,
  subMonths,
  subYears,
} from 'date-fns';
import { ko } from 'date-fns/locale/ko';
import IMask from 'imask';

// ========================================
// 🕒 Time/Date Pattern Formatting Constants (시간/날짜 패턴 상수)
// ========================================
const TIME_DATE_PATTERNS = {
  time: '00:00:00',
  date: '0000-00-00',
} as const;

// ========================================
// ⏰ Pattern Formatting Functions (패턴 포맷팅 함수들)
// ========================================

/**
 * IMask 인스턴스를 생성하는 내부 헬퍼 함수
 */
const createTimeDateMask = (pattern: string) => IMask.createMask({ mask: pattern });

/**
 * 시간/날짜 패턴 포맷팅 적용 (구분자 추가)
 *
 * 가독성 향상을 위해 시간/날짜 문자열에 콜론, 하이픈 등의 구분자를 추가합니다.
 *
 * @param value 포맷팅을 적용할 문자열
 * @param type 포맷팅 타입
 * @returns 포맷팅이 적용된 문자열
 * @example
 * ```tsx
 * formatTimePattern('123456', 'time') // "12:34:56"
 * formatTimePattern('20240315', 'date') // "2024-03-15"
 * ```
 */
const formatTimePattern = (value: string, type: keyof typeof TIME_DATE_PATTERNS): string => {
  const mask = createTimeDateMask(TIME_DATE_PATTERNS[type]);
  mask.resolve(value);
  return mask.value;
};

/**
 * 시간/날짜 패턴 포맷팅 해제
 *
 * @param formatted 포맷팅이 적용된 문자열
 * @param type 포맷팅 타입
 * @returns 포맷팅이 제거된 원본 문자열
 * @example
 * ```tsx
 * unformatTimePattern('12:34:56', 'time') // "123456"
 * unformatTimePattern('2024-03-15', 'date') // "20240315"
 * ```
 */
const unformatTimePattern = (formatted: string, type: keyof typeof TIME_DATE_PATTERNS): string => {
  const mask = createTimeDateMask(TIME_DATE_PATTERNS[type]);
  mask.resolve(formatted);
  return mask.unmaskedValue;
};

/**
 * 시간/날짜 패턴이 완전한지 검증
 *
 * @param value 검증할 값
 * @param type 포맷팅 타입
 * @returns 완전한 형태이면 true
 * @example
 * ```tsx
 * validateTimePattern('12:34:56', 'time') // true
 * validateTimePattern('12:34:5', 'time') // false
 * validateTimePattern('2024-03-15', 'date') // true
 * validateTimePattern('2024-03-1', 'date') // false
 * ```
 */
const validateTimePattern = (value: string, type: keyof typeof TIME_DATE_PATTERNS): boolean => {
  const mask = createTimeDateMask(TIME_DATE_PATTERNS[type]);
  mask.resolve(value);
  return mask.isComplete;
};

/**
 * yyyymmdd 형태의 숫자를 yyyy.MM.dd 형식의 문자열로 변환
 * @param dateNum yyyymmdd 형태의 숫자 (예: 20191229)
 * @returns yyyy.MM.dd 형식의 문자열 (예: "2019.12.29")
 * @example
 * ```tsx
 * formatYyyyMmDdToDot(20191229) // "2019.12.29"
 * formatYyyyMmDdToDot(20240115) // "2024.01.15"
 * ```
 */
const formatYyyyMmDdToDot = (dateNum: number | null | undefined): string => {
  if (!dateNum) return '';
  const dateStr = String(dateNum);
  if (dateStr.length !== 8 || !/^\d{8}$/.test(dateStr)) return '';
  // formatTimePattern을 사용하여 yyyy-MM-dd 형식으로 변환 후 점으로 변경
  return formatTimePattern(dateStr, 'date').replace(/-/g, '.');
};

// ========================================
// 📅 Date-fns 기반 날짜 포맷팅 함수들
// ========================================

/**
 * 기본 날짜 포맷팅 (date-fns 기반)
 * @param date 포맷팅할 날짜
 * @param pattern 날짜 포맷 패턴 (기본값: 'yyyy-MM-dd')
 * @param useKoreanLocale 한국어 로케일 사용 여부 (기본값: true)
 * @returns 포맷팅된 날짜 문자열
 * @example
 * ```tsx
 * formatDateWithPattern(new Date()) // "2024-01-15"
 * formatDateWithPattern(new Date(), 'yyyy년 MM월 dd일') // "2024년 01월 15일"
 * formatDateWithPattern(new Date(), 'MMM dd, yyyy', false) // "Jan 15, 2024"
 * ```
 */
const formatDateWithPattern = (date: Date | string | number, pattern: string = 'yyyy-MM-dd', useKoreanLocale: boolean = true): string => {
  const dateObj = typeof date === 'string' ? parseISO(date) : new Date(date);
  return format(dateObj, pattern, useKoreanLocale ? { locale: ko } : undefined);
};

/**
 * 상대 시간 포맷팅 (date-fns 기반)
 * @param date 비교할 날짜
 * @param useKoreanLocale 한국어 로케일 사용 여부 (기본값: true)
 * @returns 상대 시간 문자열
 * @example
 * ```tsx
 * formatRelativeTime(new Date()) // "방금 전"
 * formatRelativeTime(subDays(new Date(), 1)) // "1일 전"
 * formatRelativeTime(addDays(new Date(), 1)) // "1일 후"
 * ```
 */
const formatRelativeTime = (date: Date | string | number, useKoreanLocale: boolean = true): string => {
  const dateObj = typeof date === 'string' ? parseISO(date) : new Date(date);
  return formatDistanceToNow(dateObj, {
    addSuffix: true,
    locale: useKoreanLocale ? ko : undefined,
  });
};

/**
 * 상대적 날짜 표현 (date-fns 기반)
 * @param date 비교할 날짜
 * @param baseDate 기준 날짜 (기본값: 현재 시간)
 * @param useKoreanLocale 한국어 로케일 사용 여부 (기본값: true)
 * @returns 상대적 날짜 문자열
 * @example
 * ```tsx
 * formatRelativeDate(new Date()) // "오늘 오후 3:45"
 * formatRelativeDate(subDays(new Date(), 1)) // "어제 오후 3:45"
 * formatRelativeDate(addDays(new Date(), 1)) // "내일 오후 3:45"
 * ```
 */
const formatRelativeDate = (date: Date | string | number, baseDate: Date = new Date(), useKoreanLocale: boolean = true): string => {
  const dateObj = typeof date === 'string' ? parseISO(date) : new Date(date);
  return formatRelative(dateObj, baseDate, {
    locale: useKoreanLocale ? ko : undefined,
  });
};

/**
 * 날짜 더하기 (date-fns 기반)
 * @param date 기준 날짜
 * @param amount 더할 수량
 * @param unit 단위 ('days' | 'months' | 'years')
 * @returns 계산된 날짜
 * @example
 * ```tsx
 * addToDate(new Date(), 7, 'days') // 7일 후
 * addToDate(new Date(), 2, 'months') // 2개월 후
 * addToDate(new Date(), 1, 'years') // 1년 후
 * ```
 */
const addToDate = (date: Date | string | number, amount: number, unit: 'days' | 'months' | 'years'): Date => {
  const dateObj = typeof date === 'string' ? parseISO(date) : new Date(date);

  switch (unit) {
    case 'days':
      return addDays(dateObj, amount);
    case 'months':
      return addMonths(dateObj, amount);
    case 'years':
      return addYears(dateObj, amount);
    default:
      return dateObj;
  }
};

/**
 * 날짜 빼기 (date-fns 기반)
 * @param date 기준 날짜
 * @param amount 뺄 수량
 * @param unit 단위 ('days' | 'months' | 'years')
 * @returns 계산된 날짜
 * @example
 * ```tsx
 * subtractFromDate(new Date(), 7, 'days') // 7일 전
 * subtractFromDate(new Date(), 2, 'months') // 2개월 전
 * subtractFromDate(new Date(), 1, 'years') // 1년 전
 * ```
 */
const subtractFromDate = (date: Date | string | number, amount: number, unit: 'days' | 'months' | 'years'): Date => {
  const dateObj = typeof date === 'string' ? parseISO(date) : new Date(date);

  switch (unit) {
    case 'days':
      return subDays(dateObj, amount);
    case 'months':
      return subMonths(dateObj, amount);
    case 'years':
      return subYears(dateObj, amount);
    default:
      return dateObj;
  }
};

/**
 * 날짜 비교 (date-fns 기반)
 * @param date1 첫 번째 날짜
 * @param date2 두 번째 날짜
 * @param comparison 비교 타입 ('after' | 'before' | 'equal')
 * @returns 비교 결과
 * @example
 * ```tsx
 * compareDates(new Date(), addDays(new Date(), 1), 'before') // true
 * compareDates(new Date(), subDays(new Date(), 1), 'after') // true
 * ```
 */
const compareDates = (date1: Date | string | number, date2: Date | string | number, comparison: 'after' | 'before' | 'equal'): boolean => {
  const dateObj1 = typeof date1 === 'string' ? parseISO(date1) : new Date(date1);
  const dateObj2 = typeof date2 === 'string' ? parseISO(date2) : new Date(date2);

  switch (comparison) {
    case 'after':
      return isAfter(dateObj1, dateObj2);
    case 'before':
      return isBefore(dateObj1, dateObj2);
    case 'equal':
      return dateObj1.getTime() === dateObj2.getTime();
    default:
      return false;
  }
};

/**
 * 날짜 간 차이 계산 (date-fns 기반)
 * @param date1 시작 날짜
 * @param date2 끝 날짜
 * @param unit 단위 ('days' | 'hours' | 'minutes')
 * @returns 차이값
 * @example
 * ```tsx
 * getDateDifference(new Date(), addDays(new Date(), 7), 'days') // 7
 * getDateDifference(new Date(), addHours(new Date(), 3), 'hours') // 3
 * ```
 */
const getDateDifference = (date1: Date | string | number, date2: Date | string | number, unit: 'days' | 'hours' | 'minutes'): number => {
  const dateObj1 = typeof date1 === 'string' ? parseISO(date1) : new Date(date1);
  const dateObj2 = typeof date2 === 'string' ? parseISO(date2) : new Date(date2);

  switch (unit) {
    case 'days':
      return differenceInDays(dateObj2, dateObj1);
    case 'hours':
      return differenceInHours(dateObj2, dateObj1);
    case 'minutes':
      return differenceInMinutes(dateObj2, dateObj1);
    default:
      return 0;
  }
};

/**
 * 날짜 범위 구하기 (date-fns 기반)
 * @param date 기준 날짜
 * @param range 범위 타입 ('day' | 'week' | 'month')
 * @param position 위치 ('start' | 'end')
 * @returns 범위의 시작 또는 끝 날짜
 * @example
 * ```tsx
 * getDateRange(new Date(), 'day', 'start') // 오늘 00:00:00
 * getDateRange(new Date(), 'week', 'end') // 이번 주 일요일 23:59:59
 * getDateRange(new Date(), 'month', 'start') // 이번 달 1일 00:00:00
 * ```
 */
const getDateRange = (date: Date | string | number, range: 'day' | 'week' | 'month', position: 'start' | 'end'): Date => {
  const dateObj = typeof date === 'string' ? parseISO(date) : new Date(date);

  switch (range) {
    case 'day':
      return position === 'start' ? startOfDay(dateObj) : endOfDay(dateObj);
    case 'week':
      return position === 'start' ? startOfWeek(dateObj) : endOfWeek(dateObj);
    case 'month':
      return position === 'start' ? startOfMonth(dateObj) : endOfMonth(dateObj);
    default:
      return dateObj;
  }
};

/**
 * 통합 날짜/시간 포맷팅 함수 - 여러 타입을 한 번에 처리
 *
 * 두 가지 카테고리의 포맷팅을 지원합니다:
 * 1. **Date-fns 포맷팅**: short, long, relative, time, datetime, custom
 * 2. **패턴 포맷팅**: time-pattern, date-pattern
 *
 * @param date 포맷팅할 날짜 (패턴 포맷팅의 경우 문자열)
 * @param type 포맷 타입
 * @param options 추가 옵션 (Date-fns 포맷팅에만 적용)
 * @returns 포맷팅된 날짜
 * @example
 * ```tsx
 * // Date-fns 포맷팅
 * formatDate(new Date(), 'short') // "2024-01-15"
 * formatDate(new Date(), 'long') // "2024년 1월 15일"
 * formatDate(new Date(), 'relative') // "방금 전"
 *
 * // 패턴 포맷팅
 * formatDate('123456', 'time-pattern') // "12:34:56"
 * formatDate('20240315', 'date-pattern') // "2024-03-15"
 * ```
 */
const formatDate = (
  date: Date | string | number | undefined,
  type: 'short' | 'long' | 'relative' | 'time' | 'datetime' | 'custom' | 'time-pattern' | 'date-pattern',
  options?: {
    pattern?: string;
    useKoreanLocale?: boolean;
  }
): string => {
  if (!date) return '';
  // 패턴 포맷팅 타입인지 확인
  if (type === 'time-pattern') {
    return formatTimePattern(String(date), 'time');
  }
  if (type === 'date-pattern') {
    return formatTimePattern(String(date), 'date');
  }

  // Date-fns 포맷팅 처리
  const { pattern, useKoreanLocale = true } = options || {};

  switch (type) {
    case 'short':
      return formatDateWithPattern(date, 'yyyy-MM-dd', useKoreanLocale);
    case 'long':
      return formatDateWithPattern(date, 'yyyy년 M월 d일', useKoreanLocale);
    case 'relative':
      return formatRelativeTime(date, useKoreanLocale);
    case 'time':
      return formatDateWithPattern(date, 'HH:mm:ss', useKoreanLocale);
    case 'datetime':
      return formatDateWithPattern(date, 'yyyy.MM.dd HH:mm:ss', useKoreanLocale);
    case 'custom':
      return formatDateWithPattern(date, pattern || 'yyyy-MM-dd', useKoreanLocale);
    default:
      return formatDateWithPattern(date, 'yyyy-MM-dd', useKoreanLocale);
  }
};

// 하위 호환성을 위한 alias
const formatDateDynamic = formatDate;

/**
 * 타임스탬프를 날짜 문자열로 변환
 * @param timestamp 밀리초 타임스탬프
 * @returns "YYYY-MM-DD HH:mm" 형식의 문자열
 * @example
 * ```tsx
 * parseTimestamp(1705123456789) // "2024-01-13 15:30"
 * ```
 */
export const parseTimestamp = (timestamp: number): string => {
  const date = new Date(timestamp);

  // Extract date and time components
  const year = date.getUTCFullYear();
  const month = String(date.getUTCMonth() + 1).padStart(2, '0');
  const day = String(date.getUTCDate()).padStart(2, '0');
  const hours = String(date.getUTCHours()).padStart(2, '0');
  const minutes = String(date.getUTCMinutes()).padStart(2, '0');

  // Return formatted string as "YYYY-MM-DD HH:mm"
  return `${year}-${month}-${day} ${hours}:${minutes}`;
};

/**
 * 한국 시간대로 날짜를 변환하는 함수
 * @param dateString ISO 날짜 문자열
 * @returns 한국 시간대로 변환된 Date 객체
 */
const toKoreanTime = (dateString: string): Date => {
  const date = new Date(dateString);
  return new Date(date.toLocaleString('en-US', { timeZone: 'Asia/Seoul' }));
};

/**
 * 한국 시간대로 포맷팅된 날짜 문자열 반환
 * @param dateString ISO 날짜 문자열
 * @param format 'datetime' | 'date' | 'time'
 * @returns 포맷팅된 날짜 문자열
 */
const formatKoreanDateTime = (dateString: string, format: 'datetime' | 'date' | 'time' = 'datetime'): string => {
  const koreanDate = toKoreanTime(dateString);

  switch (format) {
    case 'date':
      return koreanDate.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        timeZone: 'Asia/Seoul',
      });
    case 'time':
      return koreanDate.toLocaleTimeString('ko-KR', {
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: true,
        timeZone: 'Asia/Seoul',
      });
    case 'datetime':
    default:
      return koreanDate.toLocaleString('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: true,
        timeZone: 'Asia/Seoul',
      });
  }
};

// Default export
export default {
  // 통합 포맷팅 함수
  formatDate,
  formatDateDynamic, // 하위 호환성

  // 한국 시간대 함수들
  toKoreanTime,
  formatKoreanDateTime,

  // 기본 포맷팅 함수들
  formatDateWithPattern,
  formatRelativeTime,
  formatRelativeDate,

  // 패턴 포맷팅 함수들
  formatTimePattern,
  unformatTimePattern,
  validateTimePattern,
  formatYyyyMmDdToDot,

  // 날짜 계산 함수들
  addToDate,
  subtractFromDate,
  getDateDifference,
  getDateRange,

  // 날짜 비교 함수들
  compareDates,
};
