import type { UIImageProps } from './types';

/**
 * UIImage 컴포넌트
 *
 * 이미지 지연 로딩을 기본으로 적용한 공통 이미지 컴포넌트입니다.
 *
 * @example
 * // 지연 로딩 (기본값)
 * <UIImage src="/assets/images/icon.svg" alt="아이콘" />
 *
 * // 즉시 로딩 (LCP 이미지)
 * <UIImage src="/assets/images/logo.svg" alt="로고" loading="eager" />
 */
export function UIImage({
  src,
  alt,
  className = '',
  loading = 'lazy', // 기본값: 지연 로딩
  ...imgProps
}: UIImageProps) {
  return <img src={src} alt={alt} className={className} loading={loading} {...imgProps} />;
}
