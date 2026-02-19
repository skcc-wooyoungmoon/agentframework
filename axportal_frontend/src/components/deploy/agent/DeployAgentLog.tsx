import { Button } from '@/components/common/auth';
import { UITypography } from '@/components/UI/atoms';
import { UICode } from '@/components/UI/atoms/UICode';
import { UIUnitGroup } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { useGetAgentAppById, useGetAgentAppDeployListById, useGetAgentDeployInfo, useGetAgentSysLog } from '@/services/deploy/agent/agentDeploy.services';
import { dateUtils } from '@/utils/common';
import { useCallback, useEffect, useMemo, useState } from 'react';

export function DeployAgentLog({ appId }: { appId: string }) {
  // 에이전트 앱 데이터 조회
  const { data: agentAppData } = useGetAgentAppById({ appId: appId });

  /**
   * 에이전트 배포 버전 리스트 데이터 조회
   */
  const { data: agentAppDeployListData } = useGetAgentAppDeployListById({
    appId: appId,
    page: 1,
    size: 6,
    sort: 'deployed_dt,desc',
  });

  // agentAppDeployListData 중에 Available 상태인 항목이 있는지 확인
  const hasAvailableDeployment = useMemo(() => {
    if (!agentAppDeployListData?.content || agentAppDeployListData.content.length === 0) return false;
    return agentAppDeployListData.content.some((deploy: any) => deploy.status === 'Available');
  }, [agentAppDeployListData]);

  const { data: agentDeployInfo } = useGetAgentDeployInfo(appId || '', {
    enabled: !!appId && hasAvailableDeployment,
  });

  // 오늘 날짜를 한국 시간 기준으로 계산하여 초기값 설정
  const getTodayKstDate = () => {
    const now = new Date();
    // 한국 시간 기준 날짜 사용
    return dateUtils.formatDate(now, 'custom').replaceAll('-', '.');
  };

  const [searchDate, setSearchDate] = useState<string>(getTodayKstDate());
  const [logData, setLogData] = useState<string>('');
  const [isLoading, setIsLoading] = useState<boolean>(false);

  const onSuccessCallback = useCallback(
    (response: any) => {
      // 백엔드 응답 구조: { success: true, message: "...", data: "로그내용" }
      if (response.data && typeof response.data === 'string' && response.data.trim() !== '') {
        // 로그 데이터가 있으면 사용
        let formattedData = response.data;

        // JSON 문자열인 경우 파싱 후 포맷팅
        try {
          const parsed = JSON.parse(response.data);
          formattedData = JSON.stringify(parsed, null, 2);
        } catch (e) {
          // JSON이 아니면 줄바꿈 문자 처리
          // \n을 실제 줄바꿈으로 변환
          formattedData = response.data.replace(/\\n/g, '\n');
        }

        setLogData(formattedData);
        setIsLoading(false);
      } else {
        // 로그가 없으면 한국 시간 기준 어제 날짜로 재조회 (최대 1번만)
        const now = new Date();
        // const todayDate = new Date(now);
        const todayDate = new Date(now);
        const todayDateStr = dateUtils.formatDate(todayDate, 'custom').replaceAll('-', '.');

        // 어제 날짜 계산
        const yesterdayDate = new Date(now);
        yesterdayDate.setDate(yesterdayDate.getDate() - 1);
        const yesterdayDateStr = dateUtils.formatDate(yesterdayDate, 'custom').replaceAll('-', '.');

        // 현재 searchDate가 한국 시간 기준 오늘 날짜인 경우에만 어제 날짜로 재조회
        if (searchDate === todayDateStr) {
          setSearchDate(yesterdayDateStr);
        } else {
          // 어제 날짜로도 조회했는데 로그가 없으면
          setIsLoading(false);
        }
      }
    },
    [agentAppData?.createdAt, agentAppData?.targetId, searchDate]
  );

  const onErrorCallback = useCallback(() => {
    setLogData('로그를 불러오는데 실패했습니다.');
    setIsLoading(false);
  }, []);

  const { mutate: getSysLog } = useGetAgentSysLog({
    onSuccess: onSuccessCallback,
    onError: onErrorCallback,
  });

  const handleLoadLog = useCallback(() => {
    // targetId는 선택적이지만, isvcName은 필수
    if (!searchDate || !agentDeployInfo?.isvcName) {
      return;
    }

    setIsLoading(true);
    setLogData(''); // 기존 로그 데이터 초기화

    const queryBody = {
      _source: ['message'],
      size: 1000,
      query: {
        bool: {
          must: [
            { match: { 'kubernetes.namespace_name': 'ns-24ba585a-02fc-43d8-b9f1-f7ca9e020fe5' } },
            { match_phrase_prefix: { 'kubernetes.labels.app': `${agentDeployInfo.isvcName}` } },
          ],
        },
      },
    };

    getSysLog({
      index: `gaf_syslog_project-log-${searchDate}`,
      body: queryBody,
    });
  }, [searchDate, agentAppData?.targetId, agentDeployInfo?.isvcName, getSysLog]);

  // searchDate는 이미 오늘 날짜로 초기화되어 있음
  // deployDt로 덮어쓰지 않고 오늘 날짜를 유지

  // searchDate가 변경되면 로그 조회
  useEffect(() => {
    // targetId는 선택적이지만, isvcName은 필수
    if (searchDate && agentDeployInfo?.isvcName) {
      handleLoadLog();
    }
  }, [handleLoadLog, searchDate, agentDeployInfo?.isvcName]);

  const handleDownload = () => {
    const dataStr = JSON.stringify(logData, null, 2);
    const dataBlob = new Blob([dataStr], { type: 'application/json' });
    const url = URL.createObjectURL(dataBlob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `log_${agentAppData?.createdAt}_${agentAppData?.targetId}.json`;
    link.click();
    URL.revokeObjectURL(url);
  };

  return (
    <UIArticle>
      <div className='article-header'>
        <UIUnitGroup direction='row' align='space-between' gap={0}>
          <UITypography variant='title-4' className='secondary-neutral-900'>
            시스템로그
          </UITypography>
          <Button className='btn-option-outlined' onClick={handleDownload}>
            다운로드
          </Button>
        </UIUnitGroup>
      </div>
      <div className='article-body'>
        <UICode
          value={isLoading ? '로그를 조회중입니다...' : logData || `Elasticsearch 인덱스/문서를 찾을 수 없음: no such index [gaf_syslog_project-log-${searchDate}]`}
          language='json'
          theme='dark'
          width='100%'
          minHeight='512px'
          height='512px'
          maxHeight='512px'
          readOnly={true}
        />
      </div>
    </UIArticle>
  );
}
