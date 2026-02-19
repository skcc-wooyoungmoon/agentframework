import type { ToasterProps, ToastPosition } from 'react-hot-toast';

export type UITostRendererProps = {
  position?: ToastPosition;
} & ToasterProps;
