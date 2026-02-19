/**
 * 보안 마스킹 관련 유틸리티 함수들
 *
 * 개인정보 보호를 위한 정보 숨김 기능을 제공합니다:
 * - 전화번호, 주민번호, 사업자번호 등의 중간 부분 마스킹
 * - 이메일 주소의 로컬 부분 마스킹
 * - 카드번호의 중간 자리 마스킹
 *
 * 📍 패턴 포맷팅 기능은 number.ts에 있습니다.
 * (formatPattern, unformatPattern, validatePattern)
 */

// ========================================
// 🔒 Security Masking Functions (보안 마스킹)
// ========================================

/**
 * 전화번호 보안 마스킹
 *
 * 전화번호의 중간 4자리를 *로 마스킹합니다.
 * 포맷팅된 형태와 원본 형태 모두 지원합니다.
 *
 * @param phone 마스킹할 전화번호
 * @returns 마스킹된 전화번호
 * @example
 * ```tsx
 * maskPhone('010-1234-5678') // "010-****-5678"
 * maskPhone('01012345678') // "010****5678"
 * maskPhone('02-123-4567') // "02-***-4567"
 * maskPhone('0212344567') // "02***4567"
 * ```
 */
const maskPhone = (phone: string): string => {
  const cleaned = phone.replace(/\D/g, '');

  if (cleaned.length === 11) {
    // 휴대폰: 010-****-5678
    return phone.includes('-')
      ? `${cleaned.slice(0, 3)}-****-${cleaned.slice(-4)}`
      : `${cleaned.slice(0, 3)}****${cleaned.slice(-4)}`;
  } else if (cleaned.length === 10) {
    // 지역번호: 02-***-4567
    return phone.includes('-')
      ? `${cleaned.slice(0, 2)}-***-${cleaned.slice(-4)}`
      : `${cleaned.slice(0, 2)}***${cleaned.slice(-4)}`;
  } else if (cleaned.length === 9) {
    // 지역번호: 031-**-5678
    return phone.includes('-')
      ? `${cleaned.slice(0, 3)}-**-${cleaned.slice(-4)}`
      : `${cleaned.slice(0, 3)}**${cleaned.slice(-4)}`;
  }

  return phone; // 형식이 맞지 않으면 원본 반환
};

/**
 * 주민등록번호 보안 마스킹
 *
 * 주민등록번호의 뒷자리 전체를 *로 마스킹합니다.
 *
 * @param residentNumber 마스킹할 주민등록번호
 * @returns 마스킹된 주민등록번호
 * @example
 * ```tsx
 * maskResidentNumber('123456-1234567') // "123456-*******"
 * maskResidentNumber('1234561234567') // "123456*******"
 * ```
 */
const maskResidentNumber = (residentNumber: string): string => {
  const cleaned = residentNumber.replace(/\D/g, '');

  if (cleaned.length === 13) {
    return residentNumber.includes('-')
      ? `${cleaned.slice(0, 6)}-*******`
      : `${cleaned.slice(0, 6)}*******`;
  }

  return residentNumber; // 길이가 맞지 않으면 원본 반환
};

/**
 * 사업자등록번호 보안 마스킹
 *
 * 사업자등록번호의 중간 2자리와 마지막 5자리 중 일부를 마스킹합니다.
 *
 * @param businessNumber 마스킹할 사업자등록번호
 * @returns 마스킹된 사업자등록번호
 * @example
 * ```tsx
 * maskBusinessNumber('123-45-67890') // "123-**-***90"
 * maskBusinessNumber('1234567890') // "123****90"
 * ```
 */
const maskBusinessNumber = (businessNumber: string): string => {
  const cleaned = businessNumber.replace(/\D/g, '');

  if (cleaned.length === 10) {
    return businessNumber.includes('-')
      ? `${cleaned.slice(0, 3)}-**-***${cleaned.slice(-2)}`
      : `${cleaned.slice(0, 3)}****${cleaned.slice(-2)}`;
  }

  return businessNumber; // 길이가 맞지 않으면 원본 반환
};

/**
 * 계좌번호 보안 마스킹
 *
 * 계좌번호의 중간 부분을 *로 마스킹합니다.
 *
 * @param accountNumber 마스킹할 계좌번호
 * @returns 마스킹된 계좌번호
 * @example
 * ```tsx
 * maskAccountNumber('1234567890123456') // "123456********3456"
 * maskAccountNumber('123-456-789012') // "123-***-***012"
 * ```
 */
const maskAccountNumber = (accountNumber: string): string => {
  const cleaned = accountNumber.replace(/\D/g, '');

  if (cleaned.length >= 8) {
    const start = Math.min(6, Math.floor(cleaned.length / 3));
    const end = Math.min(4, Math.floor(cleaned.length / 4));
    const maskLength = cleaned.length - start - end;

    if (accountNumber.includes('-')) {
      // 하이픈이 있는 경우 원본 형태 유지하면서 마스킹
      return accountNumber.replace(/\d/g, (digit, index) => {
        const digitIndex = accountNumber
          .slice(0, index)
          .replace(/\D/g, '').length;
        return digitIndex >= start && digitIndex < cleaned.length - end
          ? '*'
          : digit;
      });
    } else {
      return `${cleaned.slice(0, start)}${'*'.repeat(
        maskLength
      )}${cleaned.slice(-end)}`;
    }
  }

  return accountNumber; // 너무 짧으면 원본 반환
};

/**
 * 이메일 주소 보안 마스킹
 *
 * 이메일의 로컬 부분(@ 앞)을 첫 글자와 마지막 글자만 남기고 나머지를 *로 마스킹합니다.
 * 도메인 부분은 그대로 유지됩니다. 개인정보 보호를 위한 표시용 마스킹입니다.
 *
 * @param email 마스킹할 이메일 주소
 * @returns 마스킹된 이메일 주소 (유효하지 않은 이메일은 원본 반환)
 * @example
 * ```tsx
 * maskEmail('john.doe@example.com') // "j*****e@example.com"
 * maskEmail('a@test.com') // "a@test.com" (로컬 부분이 1글자인 경우)
 * maskEmail('ab@test.com') // "ab@test.com" (로컬 부분이 2글자인 경우)
 * maskEmail('abc@test.com') // "a*c@test.com"
 * maskEmail('invalid-email') // "invalid-email" (유효하지 않은 이메일은 원본 반환)
 * ```
 */
const maskEmail = (email: string): string => {
  const [local, domain] = email.split('@');
  if (!local || !domain) return email;
  const maskedLocal =
    local.length > 2
      ? local[0] + '*'.repeat(local.length - 2) + local.at(-1)
      : local;
  return `${maskedLocal}@${domain}`;
};

/**
 * 카드번호 보안 마스킹
 *
 * 카드번호의 처음 4자리와 마지막 4자리만 표시하고 나머지는 *로 마스킹합니다.
 * 보안상 중요한 카드번호 정보를 안전하게 표시할 때 사용합니다.
 *
 * @param num 마스킹할 카드번호 (하이픈이나 공백 포함 가능, 자동으로 제거됨)
 * @returns 마스킹된 카드번호 (8자리 미만인 경우 원본 반환)
 * @example
 * ```tsx
 * maskCardNumber('1234567890123456') // "1234-********-3456"
 * maskCardNumber('1234-5678-9012-3456') // "1234-********-3456"
 * maskCardNumber('1234 5678 9012 3456') // "1234-********-3456"
 * maskCardNumber('12345678') // "1234-****-5678" (정확히 8자리인 경우)
 * maskCardNumber('1234567') // "1234567" (8자리 미만인 경우 원본 반환)
 * ```
 */
const maskCardNumber = (num: string): string => {
  const cleaned = num.replace(/\D/g, '');
  if (cleaned.length < 8) return num;
  return `${cleaned.slice(0, 4)}-${'*'.repeat(
    cleaned.length - 8
  )}-${cleaned.slice(-4)}`;
};

// ========================================
// 🎨 s
// ========================================
export default {
  maskPhone,
  maskResidentNumber,
  maskBusinessNumber,
  maskAccountNumber,
  maskEmail,
  maskCardNumber,
};
