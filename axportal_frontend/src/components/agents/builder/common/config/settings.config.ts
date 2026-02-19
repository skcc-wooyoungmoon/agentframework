export type TSettingsThemeMode = 'light' | 'dark' | 'system';

export type TSettingsContainer = 'default' | 'fluid' | 'fixed';

export interface ISettings {
  themeMode: TSettingsThemeMode;
  container: TSettingsContainer;
}

const defaultSettings: ISettings = {
  themeMode: 'light',
  container: 'fixed',
};

export { defaultSettings };
