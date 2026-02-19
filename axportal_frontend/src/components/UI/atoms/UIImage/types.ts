export type UIImageProps = React.ImgHTMLAttributes<HTMLImageElement> & {
  src: string;
  alt: string;
  className?: string;
  /**
   * 이미지 로딩 방식
   * - 'lazy': 지연 로딩 (기본값, 스크롤 후 보이는 이미지)
   * - 'eager': 즉시 로딩 (LCP 이미지, 첫 화면에 보이는 중요 이미지)
   */
  loading?: 'lazy' | 'eager';
};
