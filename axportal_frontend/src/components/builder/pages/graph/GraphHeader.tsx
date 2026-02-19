import { useAtom } from 'jotai/index';

import { UIImage } from '@/components/UI/atoms/UIImage';
import { agentAtom } from '@/components/builder/atoms/AgentAtom';

interface GraphHeaderProps {
  name: string;
  onClickDesc?: () => void;
  readOnly?: boolean;
}

const GraphHeader = ({ name, onClickDesc, readOnly = false }: GraphHeaderProps) => {
  const [agent] = useAtom(agentAtom);

  const handleDescClick = () => {
    if (onClickDesc) onClickDesc();
  };

  return (
    <div className='flex max-w-[550px] items-center' style={{ zIndex: 50 }}>
      <div className='flex items-start flex-col pl-[50px] gap-[6px] mt-4'>
        <div className='flex items-center'>
          <h1
            className='mr-2 truncate text-lg font-bold text-gray-800'
            style={{
              fontFamily: 'Inter',
              fontWeight: 700,
              fontSize: '26px',
              lineHeight: '36px',
              letterSpacing: '-0.08px',
            }}
          >
            {agent?.name ?? name}
          </h1>
          {/* <span className='mr-2 truncate text-xs text-gray-700'>{formatDate(updatedDate)}</span> */}
        </div>
        <div style={{ opacity: readOnly ? 0.6 : 1 }}>
          <button
            type='button'
            onClick={readOnly ? undefined : handleDescClick}
            className='!text-base flex items-center gap-1 hover:bg-transparent'
            style={{
              zIndex: 55,
              pointerEvents: readOnly ? 'none' : 'auto',
              cursor: readOnly ? 'not-allowed' : 'pointer',
            }}
            disabled={readOnly}
          >
            <span className='text-[#005DF9] font-normal text-base leading-6 tracking-[-0.005em]'>빌더 정보</span>
            <UIImage src='/assets/images/system/ico-system-16-arrow-right.svg' alt='arrow right' className='w-4 h-4' />
          </button>
        </div>
      </div>
    </div>
  );
};

export default GraphHeader;
