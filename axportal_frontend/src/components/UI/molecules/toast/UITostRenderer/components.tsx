import { Toaster } from 'react-hot-toast';
import type { UITostRendererProps } from './types';

export const UITostRenderer = ({ position = 'bottom-center', containerStyle, ...props }: UITostRendererProps) => {
  return (
    <Toaster
      position={position}
      containerStyle={{
        ...containerStyle,
        bottom: '64px',
        zIndex: 20000, // 모달 dim 높게 설정
      }}
      {...props}
    />
  );
};
