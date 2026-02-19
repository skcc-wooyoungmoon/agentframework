// clsx 라이브러리 없이 순수 JavaScript/TypeScript로 구현
// 조건부 클래스명 결합 유틸리티

type ClassValue = string | number | boolean | null | undefined | Record<string, any> | ClassValue[];

/**
 * 조건부 클래스명을 결합하는 함수
 * clsx 라이브러리와 동일한 기능을 제공하지만 외부 의존성 없음
 */
export const ABClassNames = (...args: ClassValue[]): string => {
  const classes: string[] = [];

  for (const arg of args) {
    if (!arg) continue;

    if (typeof arg === 'string') {
      classes.push(arg);
    } else if (typeof arg === 'number') {
      classes.push(String(arg));
    } else if (Array.isArray(arg)) {
      classes.push(ABClassNames(...arg));
    } else if (typeof arg === 'object') {
      for (const key in arg) {
        if (arg[key]) {
          classes.push(key);
        }
      }
    }
  }

  return classes.join(' ');
};