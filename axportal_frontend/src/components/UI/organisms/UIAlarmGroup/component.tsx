import { UIAlarmItem } from '@/components/UI/molecules/UIAlarmItem';
import { UITypography } from '@/components/UI';
import { UIUnitGroup } from '@/components/UI/molecules';
import { UIIcon2 } from '@/components/UI/atoms/UIIcon2';
import type { UIAlarmGroupProps } from './types';

export const UIAlarmGroup: React.FC<UIAlarmGroupProps> = ({ alarmData, children, onItemClick, className = '' }) => {
  const isEmpty = !alarmData || alarmData.length === 0;

  return (
    <div className={'alarm-content ' + className}>
      {children ? (
        children
      ) : isEmpty ? (
        // <div className='flex items-center justify-center h-full py-12'>
        //   <div className='text-center'>
        //     <p className='text-gray-500 text-sm'>알림이 없습니다.</p>
        //   </div>
        // </div>
        <UIUnitGroup gap={4} direction='column' vAlign='center'>
          <UIIcon2 className='ic-system-72-alarm mb-3 mt-3' />
          <UITypography variant='body-1' className='secondary-neutral-700 text-sb'>
            새로운 알림이 없습니다.
          </UITypography>
          <UITypography variant='body-1' className='secondary-neutral-700 text-[#737C85]'>
            알림을 받으면 여기에 표시됩니다.
          </UITypography>
        </UIUnitGroup>
      ) : (
        alarmData?.map((group, groupIndex) => (
          <div key={groupIndex} className='alarm-group'>
            <div className='date-divider'>{group.date}</div>
            {group.items.map(item => (
              <UIAlarmItem
                key={item.id}
                id={item.id}
                title={item.title}
                description={item.description}
                time={item.time}
                isRead={item.isRead}
                type={item.type}
                onClick={onItemClick}
                actionButton={item.actionButton}
              />
            ))}
          </div>
        ))
      )}
    </div>
  );
};
