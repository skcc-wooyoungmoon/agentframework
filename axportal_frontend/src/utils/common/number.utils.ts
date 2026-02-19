/**
 * 숫자 및 숫자 관련 패턴 포맷팅 유틸리티 함수들
 *
 * 두 가지 주요 포맷팅 기능을 제공합니다:
 *
 * 1. **Numeral.js 기반 숫자 포맷팅**:
 *    - 천단위 콤마, 통화, 축약형, 소수점, 퍼센트, 바이트 등
 *
 * 2. **IMask 기반 숫자 패턴 포맷팅**:
 *    - 전화번호, 주민번호, 사업자번호, 카드번호 등에 구분자 추가
 *
 * 📍 시간/날짜 패턴 포맷팅은 date.ts에 있습니다.
 * (formatTimePattern, unformatTimePattern, validateTimePattern)
 */

import IMask from 'imask';
import numeral from 'numeral';

// ========================================
// 📱 Number Pattern Formatting Constants (숫자 패턴 포맷팅 상수)
// ========================================
const PATTERN_FORMATS = {
  phone: '000-0000-0000',
  residentNumber: '000000-0000000',
  businessNumber: '000-00-00000',
  creditCard: '0000-0000-0000-0000',
} as const;

// ========================================
// 📐 Pattern Formatting Functions (패턴 포맷팅 함수들)
// ========================================

/**
 * IMask 인스턴스를 생성하는 내부 헬퍼 함수
 */
const createMask = (pattern: string) => IMask.createMask({ mask: pattern });

/**
 * 입력값에 패턴 포맷팅을 적용 (구분자 추가)
 *
 * 가독성 향상을 위해 숫자나 문자열에 하이픈, 콜론 등의 구분자를 추가합니다.
 *
 * @param value 포맷팅을 적용할 문자열
 * @param type 포맷팅 타입
 * @returns 포맷팅이 적용된 문자열
 * @example
 * ```tsx
 * formatPattern('01012345678', 'phone') // "010-1234-5678"
 * formatPattern('1234567890123', 'residentNumber') // "123456-7890123"
 * formatPattern('1234567890123456', 'creditCard') // "1234-5678-9012-3456"
 * ```
 */
const formatPattern = (
  value: string,
  type: keyof typeof PATTERN_FORMATS
): string => {
  const mask = createMask(PATTERN_FORMATS[type]);
  mask.resolve(value);
  return mask.value;
};

/**
 * 패턴 포맷팅된 값에서 원본 값 추출 (포맷 해제)
 *
 * @param formatted 포맷팅이 적용된 문자열
 * @param type 포맷팅 타입
 * @returns 포맷팅이 제거된 원본 문자열
 * @example
 * ```tsx
 * unformatPattern('010-1234-5678', 'phone') // "01012345678"
 * unformatPattern('1234-5678-9012-3456', 'creditCard') // "1234567890123456"
 * ```
 */
const unformatPattern = (
  formatted: string,
  type: keyof typeof PATTERN_FORMATS
): string => {
  const mask = createMask(PATTERN_FORMATS[type]);
  mask.resolve(formatted);
  return mask.unmaskedValue;
};

/**
 * 패턴 포맷팅된 값이 완전한지 검증
 *
 * @param value 검증할 값
 * @param type 포맷팅 타입
 * @returns 완전한 형태이면 true
 * @example
 * ```tsx
 * validatePattern('010-1234-5678', 'phone') // true
 * validatePattern('010-1234-567', 'phone') // false
 * ```
 */
const validatePattern = (
  value: string,
  type: keyof typeof PATTERN_FORMATS
): boolean => {
  const mask = createMask(PATTERN_FORMATS[type]);
  mask.resolve(value);
  return mask.isComplete;
};

// ========================================
// 🔢 Numeral.js 기반 숫자 포맷팅 함수들
// ========================================

/**
 * 천단위 콤마 포맷팅 (내부용)
 */
const formatNumberWithComma = (num: number | string): string => {
  return numeral(num).format('0,0');
};

/**
 * 통화 형식 포맷팅 (내부용)
 */
const formatCurrency = (
  num: number | string,
  currency: string = '원'
): string => {
  return numeral(num).format('0,0') + currency;
};

/**
 * 축약 형식 포맷팅 (내부용)
 */
const formatNumberAbbreviated = (
  num: number | string,
  decimals: number = 1
): string => {
  const formatString = decimals > 0 ? `0.${'0'.repeat(decimals)}a` : '0a';
  return numeral(num).format(formatString);
};

/**
 * 소수점 포맷팅 (내부용)
 */
const formatDecimal = (num: number | string, decimals: number = 2): string => {
  const formatString = `0.${'0'.repeat(decimals)}`;
  return numeral(num).format(formatString);
};

/**
 * 퍼센트 포맷팅 (내부용)
 */
const formatPercent = (
  value: number | string,
  total: number = 100,
  decimals: number = 1
): string => {
  const ratio = total === 100 ? Number(value) : Number(value) / total;
  const formatString = decimals > 0 ? `0.${'0'.repeat(decimals)}%` : '0%';
  return numeral(ratio).format(formatString);
};

/**
 * 바이트 크기 포맷팅 (내부용)
 */
const formatBytes = (bytes: number | string, decimals: number = 1): string => {
  const formatString = decimals > 0 ? `0.${'0'.repeat(decimals)}b` : '0b';
  return numeral(bytes).format(formatString);
};

/**
 * 통합 포맷팅 함수 - 숫자 포맷팅과 패턴 포맷팅을 한 번에 처리
 *
 * 두 가지 카테고리의 포맷팅을 지원합니다:
 * 1. **숫자 포맷팅**: comma, currency, abbreviated, decimal, percent, bytes
 * 2. **패턴 포맷팅**: phone, residentNumber, businessNumber, creditCard
 *
 * @param value 포맷팅할 값
 * @param type 포맷 타입 (숫자 또는 패턴)
 * @param options 추가 옵션 (숫자 포맷팅에만 적용)
 * @returns 포맷팅된 값
 * @example
 * ```tsx
 * // 숫자 포맷팅
 * formatNumber(1234567, 'comma') // "1,234,567"
 * formatNumber(1234567, 'currency', { currency: '$' }) // "1,234,567$"
 *
 * // 패턴 포맷팅
 * formatNumber('01012345678', 'phone') // "010-1234-5678"
 * formatNumber('1234567890123456', 'creditCard') // "1234-5678-9012-3456"
 * ```
 */
const formatNumber = (
  value: number | string,
  type:
    | 'comma'
    | 'currency'
    | 'abbreviated'
    | 'decimal'
    | 'percent'
    | 'bytes'
    | 'phone'
    | 'residentNumber'
    | 'businessNumber'
    | 'creditCard',
  options?: {
    currency?: string;
    decimals?: number;
    total?: number;
  }
): string => {
  // 패턴 포맷팅 타입인지 확인
  if (type in PATTERN_FORMATS) {
    return formatPattern(String(value), type as keyof typeof PATTERN_FORMATS);
  }

  // 숫자 포맷팅 처리
  const { currency = '원', decimals = 1, total = 100 } = options || {};

  switch (type) {
    case 'comma':
      return formatNumberWithComma(value);
    case 'currency':
      return formatCurrency(value, currency);
    case 'abbreviated':
      return formatNumberAbbreviated(value, decimals);
    case 'decimal':
      return formatDecimal(value, decimals);
    case 'percent':
      return formatPercent(value, total, decimals);
    case 'bytes':
      return formatBytes(value, decimals);
    default:
      return String(value);
  }
};

// Default
export default {
  // 통합 포맷팅 함수
  formatNumber,

  // 패턴 포맷팅 함수들
  formatPattern,
  unformatPattern,
  validatePattern,
};
