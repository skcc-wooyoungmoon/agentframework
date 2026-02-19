/**
 * 문자열 관련 유틸리티 함수들
 *
 * 문자열 조작, 포맷팅, 변환에 관련된 다양한 유틸리티 기능들:
 * - 문자열 패딩: padString(str, length, char)
 * - 문자열 마스킹: maskString(str, start, end, maskChar)
 * - 텍스트 변환: camelCase, snake_case, kebab-case
 * - 문자열 검증: isEmpty, isWhitespace
 */

// ========================================
// 📝 문자열 포맷팅 함수들
// ========================================

/**
 * 문자열을 특정 길이로 패딩
 * @param str 패딩할 문자열
 * @param length 목표 길이
 * @param char 패딩에 사용할 문자 (기본값: ' ')
 * @param position 패딩 위치 ('start' | 'end') (기본값: 'start')
 * @returns 패딩된 문자열
 * @example
 * ```tsx
 * padString('123', 5) // "  123"
 * padString('123', 5, '0') // "00123"
 * padString('123', 5, '0', 'end') // "12300"
 * ```
 */
const padString = (str: string, length: number, char: string = ' ', position: 'start' | 'end' = 'start'): string => {
  return position === 'start' ? str.padStart(length, char) : str.padEnd(length, char);
};

/**
 * 문자열을 특정 부분 마스킹
 * @param str 마스킹할 문자열
 * @param start 앞에서부터 보여줄 문자 수
 * @param end 뒤에서부터 보여줄 문자 수
 * @param maskChar 마스킹에 사용할 문자 (기본값: '*')
 * @returns 마스킹된 문자열
 * @example
 * ```tsx
 * maskString('1234567890', 3, 2) // "123*****90"
 * maskString('홍길동', 1, 1, '○') // "홍○동"
 * ```
 */
const maskString = (str: string, start: number, end: number, maskChar: string = '*'): string => {
  if (str.length <= start + end) return str;

  const prefix = str.substring(0, start);
  const suffix = str.substring(str.length - end);
  const maskLength = str.length - start - end;

  return prefix + maskChar.repeat(maskLength) + suffix;
};

// ========================================
// 🔄 문자열 변환 함수들
// ========================================

/**
 * 문자열을 camelCase로 변환 (내부 함수)
 */
const toCamelCase = (str: string): string => {
  return str.replace(/(?:^\w|[A-Z]|\b\w)/g, (word, index) => (index === 0 ? word.toLowerCase() : word.toUpperCase())).replace(/\s+/g, '');
};

/**
 * 문자열을 snake_case로 변환 (내부 함수)
 */
const toSnakeCase = (str: string): string => {
  return str
    .replace(/\W+/g, ' ')
    .split(/ |\B(?=[A-Z])/)
    .map(word => word.toLowerCase())
    .join('_');
};

/**
 * 문자열을 kebab-case로 변환 (내부 함수)
 */
const toKebabCase = (str: string): string => {
  return str
    .replace(/\W+/g, ' ')
    .split(/ |\B(?=[A-Z])/)
    .map(word => word.toLowerCase())
    .join('-');
};

/**
 * 문자열을 PascalCase로 변환 (내부 함수)
 */
const toPascalCase = (str: string): string => {
  return str.replace(/(?:^\w|[A-Z]|\b\w)/g, word => word.toUpperCase()).replace(/\s+/g, '');
};

// ========================================
// ✂️ 문자열 조작 함수들
// ========================================

/**
 * 문자열 자르기 (말줄임표 포함)
 * @param str 자를 문자열
 * @param maxLength 최대 길이
 * @param ellipsis 말줄임표 (기본값: '...')
 * @returns 잘린 문자열
 * @example
 * ```tsx
 * truncateString('안녕하세요 반갑습니다', 10) // "안녕하세요 반갑..."
 * truncateString('Hello World', 8, '…') // "Hello W…"
 * ```
 */
const truncateString = (str: string, maxLength: number, ellipsis: string = '...'): string => {
  if (str.length <= maxLength) return str;
  return str.slice(0, maxLength - ellipsis.length) + ellipsis;
};

/**
 * 문자열에서 특수문자 제거
 * @param str 처리할 문자열
 * @param keepSpaces 공백 유지 여부 (기본값: true)
 * @returns 특수문자가 제거된 문자열
 * @example
 * ```tsx
 * removeSpecialChars('Hello, World!') // "Hello World"
 * removeSpecialChars('Hello, World!', false) // "HelloWorld"
 * ```
 */
const removeSpecialChars = (str: string, keepSpaces: boolean = true): string => {
  const pattern = keepSpaces ? /[^\w\s가-힣]/g : /[^\w가-힣]/g;
  return str.replace(pattern, '');
};

/**
 * 문자열에서 HTML 태그 제거
 * @param str HTML이 포함된 문자열
 * @returns HTML 태그가 제거된 문자열
 * @example
 * ```tsx
 * stripHtmlTags('<p>Hello <strong>World</strong>!</p>') // "Hello World!"
 * ```
 */
const stripHtmlTags = (str: string): string => {
  return str.replace(/<[^>]*>/g, '');
};

/**
 * 여러 공백을 하나로 변환
 * @param str 처리할 문자열
 * @returns 공백이 정리된 문자열
 * @example
 * ```tsx
 * normalizeSpaces('Hello    World   !') // "Hello World !"
 * ```
 */
const normalizeSpaces = (str: string): string => {
  return str.replace(/\s+/g, ' ').trim();
};

// ========================================
// ✅ 문자열 검증 함수들
// ========================================

/**
 * 문자열이 비어있는지 확인
 * @param str 확인할 문자열
 * @returns 비어있으면 true
 * @example
 * ```tsx
 * isEmpty('') // true
 * isEmpty('  ') // true
 * isEmpty('hello') // false
 * ```
 */
const isEmpty = (str: string | null | undefined): boolean => {
  return !str || str.trim().length === 0;
};

/**
 * 문자열이 공백만 포함하는지 확인
 * @param str 확인할 문자열
 * @returns 공백만 포함하면 true
 * @example
 * ```tsx
 * isWhitespace('   ') // true
 * isWhitespace('\t\n') // true
 * isWhitespace('hello') // false
 * ```
 */
const isWhitespace = (str: string): boolean => {
  return /^\s*$/.test(str);
};

/**
 * 문자열이 숫자로만 구성되어 있는지 확인
 * @param str 확인할 문자열
 * @returns 숫자로만 구성되어 있으면 true
 * @example
 * ```tsx
 * isNumericString('12345') // true
 * isNumericString('123.45') // false
 * isNumericString('abc123') // false
 * ```
 */
const isNumericString = (str: string): boolean => {
  return /^\d+$/.test(str);
};

// ========================================
// 🎨 동적 문자열 변환 함수
// ========================================

/**
 * 문자열 케이스 변환 통합 함수
 *
 * 다양한 케이스 형식 간의 변환을 처리하는 통합 API입니다.
 *
 * @param str 변환할 문자열
 * @param caseType 변환할 케이스 타입
 *   - 'camel': camelCase (첫 글자 소문자, 이후 단어 첫 글자 대문자)
 *   - 'snake': snake_case (소문자, 단어 간 밑줄)
 *   - 'kebab': kebab-case (소문자, 단어 간 하이픈)
 *   - 'pascal': PascalCase (모든 단어 첫 글자 대문자)
 * @returns 변환된 문자열
 * @example
 * ```tsx
 * convertStringCase('hello_world', 'camel') // "helloWorld"
 * convertStringCase('HelloWorld', 'snake') // "hello_world"
 * convertStringCase('hello world', 'kebab') // "hello-world"
 * convertStringCase('hello-world', 'pascal') // "HelloWorld"
 * ```
 */
const convertStringCase = (str: string, caseType: 'camel' | 'snake' | 'kebab' | 'pascal'): string => {
  switch (caseType) {
    case 'camel':
      return toCamelCase(str);
    case 'snake':
      return toSnakeCase(str);
    case 'kebab':
      return toKebabCase(str);
    case 'pascal':
      return toPascalCase(str);
    default:
      return str;
  }
};

// UUID 생성
/**
 * UUID 생성
 * @returns UUID
 */
function generateUuid(): string {
  try {
    // crypto.randomUUID()가 지원되는 경우 사용
    if (typeof crypto !== 'undefined' && crypto.randomUUID) {
      return crypto.randomUUID();
    }

    // 지원되지 않는 경우 대체 방법 사용
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
      const r = (secureRandomNumber() * 16) | 0;
      const v = c === 'x' ? r : (r & 0x3) | 0x8;
      return v.toString(16);
    });
  } catch (error) {
    // console.error('🎄 generateUuid 오류 : https 설정 X', error);
    // 최후의 수단으로 타임스탬프 기반 ID 생성
    return new Date().getTime().toString() + secureRandomString(9);
  }
}

/**
 * 숫자/문자로 구성된 보안 랜덤 문자열 생성 (base36)
 *
 * Web Crypto API(getRandomValues)를 사용하여 균등 분포의 바이트를 생성하고,
 * 각 바이트를 36진수(0-9a-z)에 매핑하여 문자열을 만듭니다.
 *
 * @param length 생성할 문자열 길이 (양의 정수)
 * @returns 생성된 랜덤 base36 문자열
 * @throws Error crypto.getRandomValues 미지원 환경
 * @example
 * // 길이 12의 랜덤 문자열 생성 (예: 'f2k9x0q8ab1c')
 * const id = secureRandomString(12);
 */
function secureRandomString(length: number): string {
  const alphabetSize = 36; // 0-9a-z
  if (typeof globalThis.crypto?.getRandomValues !== 'function') {
    throw new Error('Secure random generator is unavailable: crypto.getRandomValues not supported.');
  }
  const buffer = new Uint8Array(length);
  globalThis.crypto.getRandomValues(buffer);
  return Array.from(buffer, v => (v % alphabetSize).toString(36)).join('');
}

/**
 * Math.random() 대체: Crypto 기반 0 <= x < 1 난수 생성
 *
 * Uint32Array 2개(총 64비트)에서 상위 21비트 + 하위 32비트를 결합해
 * 53비트 정밀도의 균등 분포 난수를 생성합니다.
 *
 * @returns 0 이상 1 미만의 부동소수 난수 (53비트 정밀도)
 * @throws Error crypto.getRandomValues 미지원 환경
 * @example
 * const r = secureRandomNumber(); // 0 <= r < 1
 */
function secureRandomNumber(): number {
  if (typeof globalThis.crypto?.getRandomValues !== 'function') {
    throw new Error('Secure random generator is unavailable: crypto.getRandomValues not supported.');
  }
  const u32 = new Uint32Array(2);
  globalThis.crypto.getRandomValues(u32);
  // 상위 32비트에서 하위 21비트만 사용하여 53비트 구성
  const high = u32[0] >>> 0; // 32비트
  const low = u32[1] >>> 0;  // 32비트
  const random53 =
    ((high & 0x001FFFFF) * 0x100000000) + low; // 21비트 * 2^32 + 32비트 = 53비트
  return random53 / 0x20000000000000; // 2^53
}

/**
 * 프로필 아이콘용 문자열 생성
 * - 이메일: 로컬파트를 기준으로 분절(_.-) 후 앞 2개 이니셜
 * - 한글/CJK: 공백 제거 후 마지막 2글자(2글자 미만이면 있는 만큼)
 * - 라틴(영문): 공백 기준 단어 앞 2개 이니셜, 단어 1개면 앞 2글자
 * - 숫자 위주(전화번호 등): 마지막 2자리
 * - 그 외: 앞 2글자
 */
function getProfileIconString(name: string): string {
  const original = (name ?? '').trim();
  if (!original) return '사용자 이름 없음';

  const hasHangul = /[가-힣]/.test(original);
  const hasCJK = /[\u4E00-\u9FFF\u3040-\u30FF]/.test(original); // 한중일 한자/가나
  const alphaChars = original.match(/[A-Za-z]/g) ?? [];

  // 1) 한글 또는 CJK(가나/한자)
  if (hasHangul || hasCJK) {
    const onlyCJK = original.replace(/\s+/g, '');
    const codepoints = Array.from(onlyCJK);
    const take = codepoints.slice(-2).join('');
    return take || codepoints.slice(0, 2).join('');
  }

  // 2) 라틴(영문) 이름 처리
  if (alphaChars.length > 0) {
    const words = original
      .split(/\s+/)
      .map(w => w.replace(/[^A-Za-z]/g, ''))
      .filter(Boolean);
    if (words.length >= 2) {
      const initials = (words[0][0] ?? '') + (words[1][0] ?? '');
      return initials.toUpperCase();
    }
    const single = words[0] ?? '';
    const headTwo = (single.slice(0, 2) || original.slice(0, 2)).toUpperCase();
    return headTwo;
  }

  // 3) 그 외 문자: 앞 2글자
  return Array.from(original).slice(0, 2).join('');
}

/**
 * 크기 단위 변환
 * @param size 크기
 * @returns 크기 단위 변환된 문자열
 */
function formatBytesToGB(bytes: number | undefined): string {
  if (!bytes) return '0';
  const gb = bytes / (1024 * 1024 * 1024);
  return `${gb.toFixed(2)}`;
}

// Default
export default {
  // 포맷팅 함수들
  padString,
  maskString,

  // 변환 함수들
  convertStringCase, // 통합 문자열 변환 API

  // 조작 함수들
  truncateString,
  removeSpecialChars,
  stripHtmlTags,
  normalizeSpaces,

  // 검증 함수들
  isEmpty,
  isWhitespace,
  isNumericString,

  // UUID
  generateUuid,
  secureRandomString,
  secureRandomNumber,

  // 사용자 관련
  getProfileIconString,

  // 크기 단위 변환
  formatBytesToGB,
};
