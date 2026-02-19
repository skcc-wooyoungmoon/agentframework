import { UIButton2, UIPlaygroundCardBox, UITextLabel, UITypography } from '@/components/UI';
import { UIGroup } from '@/components/UI/molecules';
import { useSetModelParam } from '@/hooks/model/useSetModelParam';
import type { PlaygroundModel, ModelParameters, ModelPlaygroundChatResponse } from '@/services/model/playground/types';

interface ModelProps {
  model: PlaygroundModel;
  onDelete?: (modelId: string) => void;
  onUpdateParameters?: (modelId: string, parameters: ModelParameters) => void;
  onExecute?: (model: PlaygroundModel) => void;
  result?: ModelPlaygroundChatResponse | null;
  error?: string;
  isExecuting?: boolean;
}

export const Model = ({ model, onDelete, onUpdateParameters, onExecute, result, error, isExecuting }: ModelProps) => {
  const { openSetModelParamModal } = useSetModelParam({ onUpdateParameters });

  const handleOpenParamPopup = () => {
    openSetModelParamModal(model);
  };

  // 실행 결과에서 메시지 추출
  const getResultMessage = () => {
    if (!result) {
      return null;
    }

    if (!result.choices || result.choices.length === 0) {
      return null;
    }

    const firstChoice = result.choices[0];
    if (!firstChoice || !firstChoice.message) {
      return null;
    }

    return firstChoice.message.content;
  };

  const resultMessage = getResultMessage();

  // result.error 또는 error prop 중 하나라도 있으면 에러로 처리
  const displayError = result?.error || error;

  return (
    <>
      <UIPlaygroundCardBox className={displayError ? 'error' : ''} message={displayError}>
        {/* PlaygroundCardContent */}
        <div className='box-container'>
          <div className='w-full bg-white border-[#dce2ed] pt-5 pb-0 px-8 flex flex-col gap-5'>
            <div className='flex flex-col pb-5 gap-5 border-b border-[#DCE2ED]'>
              <div className='flex items-center justify-between'>
                <UITypography variant='body-1' className='secondary-neutral-900 text-sb'>
                  {model.name}{' '}
                </UITypography>
                <div className='flex items-center gap-3'>
                  <UIButton2 className='btn-text-14-semibold-point' leftIcon={{ className: 'ic-system-24-delete', children: '' }} onClick={() => onDelete?.(model.instanceId)}>
                    삭제
                  </UIButton2>
                  <UIButton2
                    className='btn-text-14-semibold-point'
                    leftIcon={{ className: 'ic-system-24-page', children: '' }}
                    onClick={() => onExecute?.(model)}
                    disabled={isExecuting}
                  >
                    {isExecuting ? '실행 중...' : '실행'}
                  </UIButton2>
                </div>
              </div>
              <div className='flex items-center gap-2'>
                <UIGroup gap={8} direction={'row'}>
                  <UITextLabel intent='gray'>{model.isPrivate ? '내부공유' : '전체공유'}</UITextLabel>
                </UIGroup>
                <UIButton2 className='btn-text-14-gray' rightIcon={{ className: 'ic-system-12-arrow-right-gray', children: '' }} onClick={handleOpenParamPopup}>
                  파라미터 설정
                </UIButton2>
              </div>
            </div>
          </div>
          {/* Content Box */}
          <div className='py-5 px-8'>
            <div className='box-article'>
              <div className='h-full rounded-xl overflow-y-auto custom-box-scroll text-[#242a34] text-base font-normal font-pretendard leading-6 tracking-[-0.031em] pr-3'>
                <UITypography variant='body-1' className='secondary-neutral-800'>
                  {/* 플레이그라운드 실행 결과 */}
                  {isExecuting && (
                    <div className='flex items-center justify-center py-8'>
                      <div className='text-gray-500'>실행 중...</div>
                    </div>
                  )}

                  {!isExecuting && !displayError && resultMessage && <div className='whitespace-pre-wrap'>{resultMessage}</div>}

                  {!isExecuting && !displayError && !resultMessage && <div className='text-gray-400 text-center py-8'>실행 결과가 여기에 표시됩니다.</div>}
                </UITypography>
              </div>
            </div>
          </div>
        </div>
      </UIPlaygroundCardBox>
    </>
  );
};
