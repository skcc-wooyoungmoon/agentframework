import { EdgeLabelRenderer } from '@xyflow/react';
import type { ReactNode } from 'react';

type Props = {
  labelX: number;
  labelY: number;
  label: ReactNode;
  onEdgeClick: () => void;
};

const CloseEdgeButton = ({ labelX, labelY, label, onEdgeClick }: Props) => {
  return (
    <EdgeLabelRenderer>
      <div
        className={'pointer-events-auto absolute origin-center'}
        style={{
          transform: `translate(-50%, -50%) translate(${labelX}px,${labelY}px)`,
        }}
      >
        <div className={'flex flex-col items-center gap-2 align-middle'}>
          {label && <div className={'rounded-lg border border-gray-300 bg-white px-4 py-1.5 text-lg font-semibold text-gray-900 shadow-md'}>{label}</div>}
          <button
            className={
              'text-edge-label h-[35px] w-[35px] border-[5px] border-[#f7f9fb] bg-[#2563eb] ' + 'hover:bg-theme-hover cursor-pointer rounded-full text-[19px] hover:text-white'
            }
            onClick={onEdgeClick}
          >
            <i className='ki-filled ki-cross text-white'></i>
          </button>
        </div>
      </div>
    </EdgeLabelRenderer>
  );
};

export { CloseEdgeButton };
