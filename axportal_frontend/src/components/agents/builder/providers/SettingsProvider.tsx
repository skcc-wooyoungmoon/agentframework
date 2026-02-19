/* eslint-disable no-unused-vars */
import { createContext, type PropsWithChildren, useContext, useState } from 'react';

import { defaultSettings, type ISettings, type TSettingsThemeMode } from '@/components/agents/builder/common/config/settings.config';

import { getData, setData } from '@/components/agents/builder/utils/LocalStorage';

export interface ISettingsProps {
  settings: ISettings;
  storeSettings: (settings: Partial<ISettings>) => void;
  updateSettings: (settings: Partial<ISettings>) => void;
  getThemeMode: () => TSettingsThemeMode;
}

const SETTINGS_CONFIGS_KEY = 'settings-configs';

const getStoredSettings = (): Partial<ISettings> => {
  return (getData(SETTINGS_CONFIGS_KEY) as Partial<ISettings>) || {};
};

const initialProps: ISettingsProps = {
  settings: { ...defaultSettings, ...getStoredSettings() },
  updateSettings: (_settings: Partial<ISettings>) => {},
  storeSettings: (_settings: Partial<ISettings>) => {},
  getThemeMode: () => 'light',
};

const LayoutsContext = createContext<ISettingsProps>(initialProps);
const useSettings = () => useContext(LayoutsContext);

const SettingsProvider = ({ children }: PropsWithChildren) => {
  const [settings, setSettings] = useState(initialProps.settings);

  const updateSettings = (newSettings: Partial<ISettings>) => {
    setSettings({ ...settings, ...newSettings });
  };

  const storeSettings = (newSettings: Partial<ISettings>) => {
    setData(SETTINGS_CONFIGS_KEY, { ...getStoredSettings(), ...newSettings });
    updateSettings(newSettings);
  };

  const getThemeMode = (): TSettingsThemeMode => {
    const { themeMode } = settings;

    if (themeMode === 'system') {
      return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
    } else if (themeMode === 'dark') {
      return 'dark';
    } else {
      return 'light';
    }
  };

  return <LayoutsContext.Provider value={{ settings, updateSettings, storeSettings, getThemeMode }}>{children}</LayoutsContext.Provider>;
};

// eslint-disable-next-line react-refresh/only-export-components
export { SettingsProvider, useSettings };
