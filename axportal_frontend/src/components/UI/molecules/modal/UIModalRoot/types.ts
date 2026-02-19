export type UIModalRootProps = {
  zIndex: number;
  /** @deprecated */
  onClose?: () => void;
  /** @deprecated */
  backdropClosable?: boolean;

  trapFocus?: boolean;
  children: React.ReactNode;
};
