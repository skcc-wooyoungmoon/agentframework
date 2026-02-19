import { UITypography } from '@/components/UI/atoms';

import type { UIPopupHeaderProps } from './types';

export const UIPopupHeader = ({ title, position, description, actions }: UIPopupHeaderProps) => {
  return (
    <div className='popup-header'>
      <div className={'flex flex-col ' + (description ? 'gap-[12px]' : '')}>
        <div className='flex items-center justify-between'>
          {position === 'left' && (
            <h2>
              <UITypography variant='title-1' className='secondary-neutral-900 text-title-1-sb'>
                {title}
              </UITypography>
            </h2>
          )}

          {position === 'right' && (
            <h2>
              <UITypography variant='title-2' className='secondary-neutral-900 text-title-2-sb'>
                {title}
              </UITypography>
            </h2>
          )}

          {actions && <div className='flex items-center gap-2'>{actions}</div>}
        </div>

        {description && (
          <div>
            <UITypography variant='body-1' className='primary-600'>
              {description}
            </UITypography>
          </div>
        )}
      </div>
    </div>
  );
};
