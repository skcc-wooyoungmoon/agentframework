import { ModalProvider } from '@/providers/common/ModalProvider';
import { Provider } from 'jotai';
import React from 'react';

export const AgentBuilderProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  return (
    <Provider>
      <ModalProvider>{children}</ModalProvider>
    </Provider>
  );
};
