import { useMemo } from 'react';
import { UICode } from '@/components/UI';
import { useGetFineTuningTrainingEvents } from '@/services/model/fineTuning/modelFineTuning.service.ts';

export const ModelFineTuningLogPopupPage = ({ trainingId }: { trainingId?: string }) => {
  const { data: fineTuningEvents } = useGetFineTuningTrainingEvents({ trainingId: trainingId || '' }, { enabled: !!trainingId, staleTime: 0, refetchOnMount: true });

  const log = useMemo(() => {
    if (!fineTuningEvents?.data?.length) return '';

    return fineTuningEvents.data.map(event => event.log).join('\n');
  }, [fineTuningEvents]);

  return (
    <div className='flex h-full'>
      {/* 소스코드 영역 */}
      <UICode value={log} language='python' theme='dark' width='100%' minHeight='472px' maxHeight='472px' readOnly={true} />
    </div>
  );
};
