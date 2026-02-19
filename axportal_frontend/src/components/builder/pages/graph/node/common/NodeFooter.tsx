import { type ColorType } from '@/components/builder/common/button/ColorType';
import { ToggleButton } from '@/components/builder/common/button/ToggleButton';

interface FooterActionProps {
  // eslint-disable-next-line no-unused-vars
  onClick: (bool: boolean) => void;
  isToggle: boolean;
}

export const NodeFooter = ({ onClick, isToggle }: FooterActionProps) => {
  return (
    <div className='card-footer w-full p-0'>
      <ToggleButton
        className={'w-full justify-center'}
        onClick={() => onClick(!isToggle)}
        text={['확장', '접기']}
        icon={['arrow-down', 'arrow-up']}
        color={'secondary' as ColorType}
        isToggle={isToggle}
      />
    </div>
  );
};
