/**
 * Common Utils - 통합 Export
 *
 * 모든 유틸리티 함수들을 한 곳에서 import할 수 있도록 re-export하는 파일
 *
 * 사용법:
 * 1. Named imports: import { formatNumber, maskPhone } from '@/utils/common'
 * 2. Category imports: import { dateUtils, maskUtils } from '@/utils/common'
 * 3. Individual file imports: import { mask } from '@/utils/common/mask'
 * 4. Default import: import utils from '@/utils/common'
 */

// ========================================
// 📦 Category exports (Utils 객체들)
// ========================================

import authUtilsDefault from './auth.utils';
import dateUtilsDefault from './date.utils';
import maskUtilsDefault from './mask.utils';
import numberUtilsDefault from './number.utils';
import storageUtilsDefault from './storage.utils';
import stringUtilsDefault from './string.utils';
import validationUtilsDefault from './validation.utils';

// 카테고리별 유틸리티 객체들을 export
export const dateUtils = dateUtilsDefault;
export const maskUtils = maskUtilsDefault;
export const numberUtils = numberUtilsDefault;
export const stringUtils = stringUtilsDefault;
export const validationUtils = validationUtilsDefault;
export const authUtils = authUtilsDefault;

// ========================================
// 📦 통합 Default Export (하위 호환성)
// ========================================

/**
 * 모든 유틸리티를 포함하는 통합 객체
 * 기존 format.ts의 default export와 동일한 구조로 하위 호환성 제공
 */
export default {
  dateUtils: dateUtilsDefault,
  maskUtils: maskUtilsDefault,
  numberUtils: numberUtilsDefault,
  stringUtils: stringUtilsDefault,
  validationUtils: validationUtilsDefault,
  authUtils: authUtilsDefault,
  storageUtils: storageUtilsDefault,
};
