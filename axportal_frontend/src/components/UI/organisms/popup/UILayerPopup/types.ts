export interface UILayerPopupProps {
  isOpen?: boolean;
  onClose?: () => void;
  title?: string;
  children?: React.ReactNode;
  size?: 'sm' | 'md' | 'lg' | 'xl' | 'full' | 'fullscreen';
  position?: 'center' | 'left' | 'right';
  showOverlay?: boolean;
  className?: string;
  headerActions?: React.ReactNode;
  /** fullscreen 사이즈일 때 좌측 Step 영역(270px)에 들어갈 콘텐츠 */
  leftContent?: React.ReactNode;
}
