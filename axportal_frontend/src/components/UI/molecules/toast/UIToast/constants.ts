import { UIToastTypeEnum, type UIToastType } from './type';

export const UIToastIconMap: Record<UIToastType, string | null> = {
  [UIToastTypeEnum.SUCCESS]: 'ic-system-24-complete',
  [UIToastTypeEnum.ERROR]: 'ic-system-24-error',
  [UIToastTypeEnum.WARNING]: 'ic-system-24-warning',
  [UIToastTypeEnum.INFO]: 'ic-system-24-info',
  [UIToastTypeEnum.DEFAULT]: null,
};
