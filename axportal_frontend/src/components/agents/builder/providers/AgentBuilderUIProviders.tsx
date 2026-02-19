import React from 'react';
import { ABIntlProvider } from '../components/ui/ABIntl';
import { ABToastProvider } from '../components/ui/ABToast';

interface AgentBuilderUIProvidersProps {
  children: React.ReactNode;
  locale?: string;
}

export const AgentBuilderUIProviders: React.FC<AgentBuilderUIProvidersProps> = ({ children, locale = 'ko' }) => {
  return (
    <ABIntlProvider defaultLocale={locale}>
      <ABToastProvider>{children}</ABToastProvider>
    </ABIntlProvider>
  );
};
