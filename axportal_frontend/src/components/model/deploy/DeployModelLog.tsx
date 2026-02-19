import { Button } from '@/components/common/auth';
import { useGetModelDeploySystemLog } from '@/services/deploy/model/modelDeploy.services';
import { useMemo } from 'react';
import { UICode, UITypography } from '../../UI';
import { UIArticle, UIUnitGroup } from '../../UI/molecules';

export const DeployModelLog = ({ servingId }: { servingId: string }) => {
  // const { data: endpointInfo } = useGetModelDeployEndpointInfo(servingId);
  const { data, refetch } = useGetModelDeploySystemLog(servingId);

  const logs = useMemo(() => {
    if (!data?.result?.logs) {
      return '';
    }

    return data?.result?.logs;
  }, [data]);

  const handleDownload = () => {
    if (!logs) {
      return;
    }

    const dataStr = JSON.stringify(logs, null, 2);
    const dataBlob = new Blob([dataStr], { type: 'application/json' });
    const url = URL.createObjectURL(dataBlob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `log_${Date.now()}.json`;
    link.click();
    URL.revokeObjectURL(url);
  };

  const handleRefresh = () => {
    refetch();
  };
  return (
    <UIArticle>
      <div className='article-header'>
        <UIUnitGroup direction='row' align='space-between' gap={0}>
          <UITypography variant='title-4' className='secondary-neutral-900'>
            시스템로그
          </UITypography>
          <Button onClick={handleRefresh} className='w-10 h-10'></Button>
          <Button className='btn-option-outlined' onClick={handleDownload}>
            다운로드
          </Button>
        </UIUnitGroup>
      </div>
      <div className='article-body'>
        <UICode value={logs} language='python' theme='dark' width='100%' minHeight='512px' height='512px' maxHeight='512px' readOnly={true} />
      </div>
    </UIArticle>
  );
};
