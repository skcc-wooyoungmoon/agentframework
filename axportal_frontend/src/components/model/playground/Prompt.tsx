import { UIPlaygroundCardBox, UITypography } from '@/components/UI';
import { UITextArea2 } from '@/components/UI/molecules';

interface PromptProps {
  type: 'system' | 'user';
  value?: string;
  onChange?: (value: string) => void;
}

export const Prompt = ({ type, value = '', onChange }: PromptProps) => {
  return (
    <UIPlaygroundCardBox>
      <div className='box-container'>
        <div className='w-full bg-white border-[#dce2ed] pt-5 pb-0 px-8 flex flex-col gap-5'>
          <div className='flex flex-col pb-5 gap-5 border-b border-[#DCE2ED]'>
            {/* 타이틀 & 버튼 */}
            <div className='flex items-center justify-between'>
              <UITypography variant='body-1' className='secondary-neutral-900 text-sb'>
                {type === 'system' ? '시스템' : '유저'} 프롬프트
              </UITypography>
            </div>
          </div>
        </div>
        <div className='py-5 px-8'>
          <UITextArea2 value={value} onChange={e => onChange?.(e.target.value)} className='area-none-line' resizable={false} enableScrollFade={true} />
        </div>
      </div>
    </UIPlaygroundCardBox>
  );
};
