import { UIIcon2 } from '@/components/UI/atoms';
import { UITypography } from '@/components/UI/atoms/UITypography';
import { useMemo } from 'react';
import { UIUnitGroup } from '../../UIUnitGroup';
import { UIToastIconMap } from './constants';
import { UIToastTypeEnum, type UIToastProps } from './type';

export const UIToast = ({ id, message, type = UIToastTypeEnum.DEFAULT, visible }: UIToastProps) => {
  const iconName = useMemo(() => UIToastIconMap[type], [type]);
  return (
    <div id={id} className={`${visible ? 'animate-custom-enter' : 'animate-custom-leave'} toast`} style={{ zIndex: 99999 }}>
      <div className='toast-content'>
        <UIUnitGroup gap={8} direction='row' vAlign='center' align='center'>
          {iconName && <UIIcon2 className={iconName} aria-hidden='true' />}
          <UITypography variant='body-1' className='text-white'>
            {message}
          </UITypography>
        </UIUnitGroup>
      </div>
    </div>
  );
};
