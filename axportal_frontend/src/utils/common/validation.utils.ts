/**
 * 유효성 검사 관련 유틸리티 함수들
 *
 * 일반적인 검증은 validator.js와 yup 라이브러리를 사용하세요:
 * - 이메일: validator.isEmail(email)
 * - URL: validator.isURL(url)
 * - IP 주소: validator.isIP(ip)
 * - 신용카드: validator.isCreditCard(card)
 * - 숫자: validator.isNumeric(str)
 * - 강력한 비밀번호: validator.isStrongPassword(password)
 * - 복합 스키마: yup.object().shape({...})
 *
 * 한국 특화 검증만 이 파일에서 제공합니다.
 */

import validator from 'validator';

/**
 * 지원되는 유효성 검사 타입들
 */
type ValidationType =
  | 'email'
  | 'phone'
  | 'url'
  | 'residentNumber'
  | 'businessNumber'
  | 'creditCard'
  | 'ipAddress'
  | 'strongPassword'
  | 'korean'
  | 'english'
  | 'numeric'
  | 'decimal';

/**
 * 데이터 타입별 유효성 검사 (통합 함수)
 *
 * @param type 검증할 데이터 타입
 * @param data 검증할 데이터
 * @returns 데이터가 유효한지 여부
 * @example
 * ```tsx
 * isValidData('email', 'test@example.com') // true
 * isValidData('phone', '010-1234-5678') // true
 * isValidData('residentNumber', '123456-1234567') // true
 * ```
 */
const isValidData = (type: ValidationType, data: string | number): boolean => {
  const stringData = String(data);

  switch (type) {
    case 'email':
      return validator.isEmail(stringData);
    case 'phone':
      return isKoreanPhoneNumber(stringData);
    case 'url':
      return validator.isURL(stringData);
    case 'residentNumber':
      return isKoreanResidentNumber(stringData);
    case 'businessNumber':
      return isKoreanBusinessNumber(stringData);
    case 'creditCard':
      return validator.isCreditCard(stringData);
    case 'ipAddress':
      return validator.isIP(stringData);
    case 'strongPassword':
      return validator.isStrongPassword(stringData);
    case 'korean':
      return isKorean(stringData);
    case 'english':
      return isEnglish(stringData);
    case 'numeric':
      return validator.isNumeric(stringData);
    case 'decimal':
      return isValidDecimal(stringData);
    default:
      return false;
  }
};

// ========================================
// 🇰🇷 한국 특화 검증 함수들
// ========================================

/**
 * 한국 전화번호 형식 검증 (내부용)
 */
const isKoreanPhoneNumber = (phone: string): boolean => {
  const phoneRegex = /^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$/;
  return phoneRegex.test(phone.replace(/\s/g, ''));
};

/**
 * 한국 주민등록번호 형식 검증 (내부용)
 */
const isKoreanResidentNumber = (residentNumber: string): boolean => {
  const cleaned = residentNumber.replace(/[^0-9]/g, '');
  if (cleaned.length !== 13) return false;

  const front = cleaned.substring(0, 6);
  const back = cleaned.substring(6);

  // 생년월일 검증
  // const year = parseInt(front.substring(0, 2));
  const month = parseInt(front.substring(2, 4));
  const day = parseInt(front.substring(4, 6));

  if (month < 1 || month > 12) return false;
  if (day < 1 || day > 31) return false;

  // 성별 코드 검증
  const genderCode = parseInt(back.charAt(0));
  if (![1, 2, 3, 4, 9, 0].includes(genderCode)) return false;

  return true;
};

/**
 * 한국 사업자등록번호 형식 검증 (내부용)
 */
const isKoreanBusinessNumber = (businessNumber: string): boolean => {
  const cleaned = businessNumber.replace(/[^0-9]/g, '');
  if (cleaned.length !== 10) return false;

  const weights = [1, 3, 7, 1, 3, 7, 1, 3, 5];
  let sum = 0;

  for (let i = 0; i < 9; i++) {
    sum += parseInt(cleaned.charAt(i)) * weights[i];
  }

  const remainder = sum % 10;
  const checkDigit = remainder === 0 ? 0 : 10 - remainder;

  return checkDigit === parseInt(cleaned.charAt(9));
};

/**
 * 한글만 포함하는지 검증 (내부용)
 */
const isKorean = (text: string): boolean => {
  return /^[가-힣\s]+$/.test(text);
};

/**
 * 영문만 포함하는지 검증 (내부용)
 */
const isEnglish = (text: string): boolean => {
  return /^[a-zA-Z\s]+$/.test(text);
};

/**
 * 소수점 형식 검증 (내부용)
 */
const isValidDecimal = (text: string): boolean => {
  // 빈 문자열 허용 (입력 중 삭제 가능)
  if (text === '') return true;
  // 소수점 입력 중 상태 허용 (예: "1.", ".5", "123.456")
  // 숫자와 소수점만 허용, 소수점은 하나만
  const decimalPattern = /^\d*\.?\d*$/;
  if (!decimalPattern.test(text)) return false;
  // 완전한 decimal 형식이거나 입력 중인 상태 허용
  // "1.", ".5", "123" 같은 입력 중 상태도 허용
  return validator.isDecimal(text) || /^\d+\.$/.test(text) || /^\.\d+$/.test(text);
};

// Default 로 모든 함수와 타입들을 묶어서 제공
export default {
  // 통합 검증 함수
  isValidData,
};
