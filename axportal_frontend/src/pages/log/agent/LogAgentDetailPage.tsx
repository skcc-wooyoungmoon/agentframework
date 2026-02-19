import { Button } from '@/components/common/auth';
import { UIBox, UIDataCnt, UILabel, UIPagination, UITypography } from '@/components/UI';
import { UIButton2 } from '@/components/UI/atoms';
import { UIArticle, UIDropdown, UIGroup, UIPageBody, UIPageHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIInput } from '@/components/UI/molecules/input/UIInput';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { useGetAgentBuilderById } from '@/services/agent/builder/agentBuilder.services';
import { useGetAgentAppApiKeyListById, useGetAgentAppById } from '@/services/deploy/agent/agentDeploy.services';
import { useGetAgentLogList } from '@/services/log/agent/agentLog.services';
import type { GetAgentLogListRequest, GetAgentLogListResponse, LogDetailData, LogPopupData } from '@/services/log/agent/types';
import { useUser } from '@/stores';
import { useModal } from '@/stores/common/modal';
import dateUtils from '@/utils/common/date.utils';
import { downloadCsv } from '@/utils/common/file.utils';
import { useCallback, useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { LogAgentAllPopupPage } from './';


export function LogAgentDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { openAlert } = useModal();
  const [selectedLog, setSelectedLog] = useState<LogPopupData | null>(null);
  const [selectedLogs, setSelectedLogs] = useState<LogDetailData[]>([]);
  const { data, isSuccess } = useGetAgentAppApiKeyListById({ appId: id || '' }, { enabled: !!id });

  // 에이전트 배포 정보 조회
  const { data: agentAppData } = useGetAgentAppById(
    { appId: id || '' },
    {
      enabled: !!id,
    }
  );

  // 빌더 상세 정보 조회 (권한 체크용)
  const { data: agentBuilder } = useGetAgentBuilderById(agentAppData?.targetId || '', {
    enabled: Boolean(agentAppData?.targetId),
  });

  const { user } = useUser();

  // 날짜 상수 정의
  const today = new Date();
  const oneMonthAgo = (() => {
    const date = new Date(today);
    date.setMonth(today.getMonth() - 1);
    return date;
  })();

  // 검색 조건
  const [searchValues, setSearchValues] = useState<GetAgentLogListRequest>({
    page: 1,
    size: 6,
    fromDate: dateUtils.formatDateWithPattern(oneMonthAgo, 'yyyy-MM-dd'),
    toDate: dateUtils.formatDateWithPattern(today, 'yyyy-MM-dd'),
    fields: '',
    errorLogs: false,
    additionalHistoryOption: 'tracing,model,retrieval',
    filter: '',
    search: '',
    sort: 'request_time,desc',
  });

  const camelToSnake = (value: string) => value.replace(/([a-z0-9])([A-Z])/g, '$1_$2').toLowerCase();

  const buildSearchQuery = useCallback((input: string): string => {
    if (!input || input.trim() === '') return '';

    const pairs = input
      .split(',')
      .map(pair => pair.trim())
      .filter(Boolean);
    const queryParts: string[] = [];

    pairs.forEach(pair => {
      const colonIndex = pair.indexOf(':');
      if (colonIndex > 0) {
        const key = camelToSnake(pair.substring(0, colonIndex).trim());
        let value = pair.substring(colonIndex + 1).trim();
        if (!key || !value) {
          return;
        }
        if (!value.includes('*')) {
          value = `*${value}*`;
        }
        queryParts.push(`${key}:${value}`);
      } else {
        let normalized = pair;
        if (!normalized.includes('*')) {
          normalized = `*${normalized}*`;
        }
        queryParts.push(`input_json:${normalized}`);
      }
    });

    return queryParts.join(',');
  }, []);

  // API 호출 (filter는 동적으로 추가)
  const {
    data: logData,
    isLoading,
    refetch,
  } = useGetAgentLogList(
    {
      ...searchValues,
      filter: isSuccess && data?.apiKeys?.[0] ? `api_key:${data.apiKeys[0]}` : 'api_key:',
      search: buildSearchQuery(searchValues.search || ''),
    },
    {
    enabled: !!id,
    refetchOnMount: true,
    refetchOnWindowFocus: false,
  });

  // API 응답을 UI 데이터로 변환
  const rowData = useMemo(() => {
    if (!logData?.content || !Array.isArray(logData.content)) {
      return [];
    }

    const result = logData.content.map((item: GetAgentLogListResponse, index: number) => {
      // 에러 판단 로직
      let status: 'normal' | 'error' = item.errorCode ? 'error' : 'normal';
      // API 응답에서 직접 errorMessage 사용
      let errorMessage: string | undefined = item.errorMessage;

      return {
        no: (searchValues.page - 1) * searchValues.size + index + 1,
        requestTime: dateUtils.formatDate(item.requestTime, 'datetime'),
        responseTime: item.responseTime ? dateUtils.formatDate(item.responseTime, 'datetime') : '-',
        elapsedTime: item.elapsedTime || 0,
        funcType: item.funcType || '-',
        servingType: item.servingType || '-',
        endpoint: item.endpoint || '-',
        apiKey: item.apiKey || '-',
        agentAppServingName: item.agentAppServingName || '-',
        company: item.company || '-',
        department: item.department || '-',
        user: item.user || '-',
        chatId: item.chatId || '-',
        content: { sessionKey: item.chatId || item.transactionId, items: [item] },
        transactionId: item.transactionId || '-',
        callKeyName: item.user || '-', // 거래 식별자에 user 값 사용
        completionTokens: item.completionTokens || 0,
        promptTokens: item.promptTokens || 0,
        totalTokens: item.totalTokens || 0,
        inputJson: item.inputJson || '',
        outputJson: item.outputJson || '',
        status,
        errorCode: item.errorCode,
        errorMessage,
        tracing: item.tracing,
        model: item.model,
        retrieval: item.retrieval,
      };
    });

    return result;
  }, [logData, searchValues.page, searchValues.size]);

  const filteredRowData = useMemo(() => {
    const targetStatus = searchValues.errorLogs ? 'error' : 'normal';
    return rowData.filter(item => item.status === targetStatus);
  }, [rowData, searchValues.errorLogs]);

  // 조회 버튼 핸들러
  const handleSearch = () => {
    setSearchValues(prev => ({ ...prev, page: 1 })); // 페이지를 1로 리셋
    refetch(); // 조회 버튼 클릭 시 명시적으로 조회
  };

  // 행 클릭 핸들러 (세션 정보 포함)
  const handleRowClick = (logData: LogDetailData) => {
    // content가 세션 정보인지 확인
    const sessionInfo = typeof logData.content === 'object' && logData.content.sessionKey ? logData.content : null;

    const popupData: LogPopupData = {
      id: logData.no,
      timestamp: logData.requestTime,
      requestTime: logData.requestTime,
      responseTime: logData.responseTime,
      callType: logData.funcType,
      status: logData.status,
      sessionId: logData.chatId,
      traceId: logData.transactionId,
      content: logData.inputJson || logData.outputJson || '',
      callKeyName: logData.user,
      latency: logData.elapsedTime?.toString() || '0',
      // API 응답 필드들 추가
      inputJson: logData.inputJson || '',
      outputJson: logData.outputJson || '',
      // 세션 정보 추가 (request-response 묶음)
      sessionKey: sessionInfo?.sessionKey,
      sessionItems: sessionInfo?.items?.map((item: any) => ({
        ...item,
        errorCode: item.errorCode || item.error_code,
        errorMessage: item.errorMessage || item.error_message,
      })),
      funcType: logData.funcType,
      // 에러 정보 추가
      errorCode: logData.errorCode,
      errorMessage: logData.errorMessage,
      // RAG 추적 정보 추가
      tracing: logData.tracing,
      model: logData.model,
      retrieval: logData.retrieval,
    };

    setSelectedLog(popupData);
  };

  // 사이드패널 닫기
  const handleCloseSidePanel = () => {
    setSelectedLog(null);
  };

  const handleDropdownSelect = (key: string, value: string) => {
    if (key === 'status') {
      setSearchValues(prev => ({ ...prev, errorLogs: value === '실패' }));
    }
  };

  // 선택된 로그 CSV 다운로드
  const handleDownloadSelectedLogs = useCallback(async () => {
    if (selectedLogs.length === 0) {
      await openAlert({
        title: '안내',
        message: '다운로드할 로그를 선택해주세요.',
      });
      return;
    }

    const csvData = selectedLogs.map(log => ({
      NO: log.no,
      요청일시: log.requestTime,
      응답일시: log.responseTime,
      '총 소요시간': `${Math.round(log.elapsedTime * 1000)}ms`,
      '호출 종류': log.funcType,
      상태: log.status === 'normal' ? '정상' : '실패',
      '세션 ID': log.chatId,
      'Trace ID': log.transactionId,
      내용: log.inputJson || log.outputJson || '',
      회사: log.company,
      부서: log.department,
      사용자: log.user,
      '완료 토큰': log.completionTokens,
      '프롬프트 토큰': log.promptTokens,
      '총 토큰': log.totalTokens,
    }));

    const filename = `에이전트배포_로그_선택_${dateUtils.formatDate(new Date(), 'datetime').replace(/[:\s]/g, '')}.csv`;
    downloadCsv(csvData, filename);
  }, [selectedLogs, openAlert]);

  // 그리드 컬럼 정의
  const columnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no',
        width: 56,
        minWidth: 56,
        maxWidth: 56,
        cellClass: 'text-center',
        headerClass: 'text-center',
        cellStyle: {
          textAlign: 'center',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        },
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '요청 일시',
        field: 'requestTime',
        width: 180,
      },
      {
        headerName: '상태',
        field: 'status',
        width: 120,
        cellRenderer: (params: any) => {
          const colorMap: { [key: string]: string } = {
            normal: 'complete',
            error: 'error',
          };
          const statusLabel = params.value === 'normal' ? '정상' : '실패';
          return (
            <div className='h-full flex items-center'>
              <UILabel variant='badge' intent={colorMap[params.value] as any}>
                {statusLabel}
              </UILabel>
            </div>
          );
        },
      },
      {
        headerName: '총 소요시간',
        field: 'elapsedTime',
        width: 120,
        cellClass: 'text-center',
        headerClass: 'text-center',
        cellStyle: {
          textAlign: 'center',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        },
        cellRenderer: (params: any) => {
          return <div className='h-full flex items-center justify-center'>{`${Math.round(params.value * 1000)}ms`}</div>;
        },
      },
      {
        headerName: '내용',
        field: 'inputJson',
        minWidth: 200,
        flex: 1,
        cellClass: 'auto-height-cell',
        cellRenderer: (params: any) => {
          // 세션 정보가 있는 경우 표시
          const sessionInfo = typeof params.data?.content === 'object' && params.data.content.sessionKey ? params.data.content : null;
          const content = params.data?.inputJson || params.data?.outputJson || '-';

          return (
            <div
              style={{
                display: '-webkit-box',
                WebkitLineClamp: 4,
                WebkitBoxOrient: 'vertical',
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'pre-wrap',
                wordBreak: 'break-word',
                lineHeight: '1.4',
                margin: '14px 16px',
              }}
            >
              {sessionInfo && sessionInfo.items.length > 1 ? `[세션 ${sessionInfo.items.length}개 요청] ${content}` : content}
            </div>
          );
        },
        autoHeight: true,
        cellStyle: {
          padding: '0',
        },
      },
      {
        headerName: '거래 식별자',
        field: 'callKeyName',
        width: 120,
      },
    ],
    []
  );

  // totalPages 계산 
  const totalPages = logData?.totalPages || 0;

  // 빌더 바로가기 핸들러
  const handleBuilderClick = useCallback(() => {
    if (!agentBuilder?.id) {
      return;
    }

    // 권한 체크
    if (agentBuilder) {
      const raw = agentBuilder as any;
      if (Number(raw?.lstPrjSeq) === -999 && Number(user.activeProject.prjSeq) !== -999 && Number(user.activeProject.prjSeq) !== Number(raw?.fstPrjSeq)) {
        openAlert({
          title: '안내',
          message: '빌더 편집에 대한 권한이 없습니다.',
          confirmText: '확인',
        });
        return;
      }
    }

    navigate(`/agent/builder/graph`, {
      state: {
        agentId: agentBuilder.id,
        isReadOnly: false,
        data: {
          id: agentBuilder.id,
          name: agentBuilder.name,
          description: agentBuilder.description,
          project_id: agentBuilder.project_id,
          nodes: agentBuilder.nodes || [],
          edges: agentBuilder.edges || [],
        },
      },
    });
  }, [agentBuilder, user, navigate, openAlert]);

  return (
    <section className='section-page'>
      {/* 페이지 헤더 */}
      <UIPageHeader title='에이전트 사용 로그 조회' description='' />

      <UIPageBody>
        {/* 에이전트 배포 정보 섹션 */}
        <UIArticle className='pb-4'>
          <div className='article-header'>
            <UITypography variant='title-4' className='secondary-neutral-900'>
              에이전트 배포 정보
            </UITypography>
          </div>
          <div className='article-body'>
            <div className='border-t border-black'>
              <table className='tbl-v'>
                <colgroup>
                  <col style={{ width: '10%' }} />
                  <col style={{ width: '40%' }} />
                  <col style={{ width: '10%' }} />
                  <col style={{ width: '40%' }} />
                </colgroup>
                <tbody>
                  <tr>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        배포명
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {agentAppData?.name || '-'}
                      </UITypography>
                    </td>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        배포유형
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {agentAppData?.servingType === 'stream' ? '스트리밍' : agentAppData?.servingType === 'custom' ? '커스텀' : '기본'}
                      </UITypography>
                    </td>
                  </tr>
                  <tr>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        빌더
                      </UITypography>
                    </th>
                    <td>
                      <UIUnitGroup gap={16} direction='row' vAlign='center'>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {agentAppData?.builderName || '-'}
                        </UITypography>
                        {agentAppData?.targetId && (
                          <UIButton2 className='btn-text-14-point' rightIcon={{ className: 'ic-system-12-arrow-right-blue', children: '' }} onClick={handleBuilderClick}>
                            빌더 바로가기
                          </UIButton2>
                        )}
                      </UIUnitGroup>
                    </td>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        설명
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {agentAppData?.description || ''}
                      </UITypography>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </UIArticle>
        {/* 검색 영역 */}
        <UIArticle className='article-filter'>
          <UIBox className='box-filter'>
            <UIGroup gap={40} direction='row'>
              <div style={{ width: 'calc(100% - 168px)' }}>
                <table className='tbl_type_b'>
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                          검색
                        </UITypography>
                      </th>
                      <td>
                        <UIUnitGroup gap={32} direction='row'>
                          <div style={{ width: '100%' }}>
                            <UIInput.Search
                              value={searchValues.search || ''}
                              onChange={e => setSearchValues(prev => ({ ...prev, search: e.target.value }))}
                              onKeyDown={e => {
                                if (e.key === 'Enter') {
                                  handleSearch();
                                }
                              }}
                              placeholder='내용 입력'
                            />
                          </div>
                        </UIUnitGroup>
                      </td>
                      <th>
                        <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                          조회 기간
                        </UITypography>
                      </th>
                      <td>
                        <UIUnitGroup gap={32} direction='row'>
                          <div className='flex-1' style={{ zIndex: '10' }}>
                            <UIUnitGroup gap={8} direction='row' vAlign='center'>
                              <div className='flex-1'>
                                <UIInput.Date
                                  value={dateUtils.formatDate(new Date(searchValues.fromDate), 'custom', { pattern: 'yyyy.MM.dd' })}
                                  onChange={e => {
                                    const newStartDate = new Date(e.target.value);
                                    const currentEndDate = new Date(searchValues.toDate);

                                    // 시작일 변경 시 종료일과의 차이가 30일을 초과하면 종료일을 조정
                                    const daysDiff = dateUtils.getDateDifference(newStartDate, currentEndDate, 'days');
                                    if (daysDiff > 30) {
                                      setSearchValues(prev => ({
                                        ...prev,
                                        fromDate: dateUtils.formatDateWithPattern(newStartDate, 'yyyy-MM-dd'),
                                        toDate: dateUtils.formatDateWithPattern(dateUtils.addToDate(newStartDate, 30, 'days'), 'yyyy-MM-dd'),
                                      }));
                                    } else if (daysDiff < 0) {
                                      setSearchValues(prev => ({
                                        ...prev,
                                        fromDate: dateUtils.formatDateWithPattern(newStartDate, 'yyyy-MM-dd'),
                                        toDate: dateUtils.formatDateWithPattern(newStartDate, 'yyyy-MM-dd'),
                                      }));
                                    } else {
                                      setSearchValues(prev => ({
                                        ...prev,
                                        fromDate: dateUtils.formatDateWithPattern(newStartDate, 'yyyy-MM-dd'),
                                      }));
                                    }
                                  }}
                                />
                              </div>

                              <UITypography variant='body-1' className='secondary-neutral-p w-[11px] justify-center'>
                                ~
                              </UITypography>

                              <div className='flex-1'>
                                <UIInput.Date
                                  value={dateUtils.formatDate(new Date(searchValues.toDate), 'custom', { pattern: 'yyyy.MM.dd' })}
                                  onChange={e => {
                                    const newEndDate = new Date(e.target.value);
                                    const currentStartDate = new Date(searchValues.fromDate);

                                    // 종료일 변경 시 시작일과의 차이가 30일을 초과하면 시작일을 조정
                                    const daysDiff = dateUtils.getDateDifference(currentStartDate, newEndDate, 'days');
                                    if (daysDiff > 30) {
                                      setSearchValues(prev => ({
                                        ...prev,
                                        fromDate: dateUtils.formatDateWithPattern(dateUtils.addToDate(newEndDate, -30, 'days'), 'yyyy-MM-dd'),
                                        toDate: dateUtils.formatDateWithPattern(newEndDate, 'yyyy-MM-dd'),
                                      }));
                                    } else if (daysDiff < 0) {
                                      setSearchValues(prev => ({
                                        ...prev,
                                        fromDate: dateUtils.formatDateWithPattern(newEndDate, 'yyyy-MM-dd'),
                                        toDate: dateUtils.formatDateWithPattern(newEndDate, 'yyyy-MM-dd'),
                                      }));
                                    } else {
                                      setSearchValues(prev => ({
                                        ...prev,
                                        toDate: dateUtils.formatDateWithPattern(newEndDate, 'yyyy-MM-dd'),
                                      }));
                                    }
                                  }}
                                />
                              </div>
                            </UIUnitGroup>
                          </div>
                        </UIUnitGroup>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                          상태
                        </UITypography>
                      </th>
                      <td>
                        <UIDropdown
                          value={searchValues.errorLogs ? '실패' : '정상'}
                          placeholder='로그 종류 선택'
                          options={[
                            { value: '정상', label: '정상' },
                            { value: '실패', label: '실패' },
                          ]}
                          onSelect={value => handleDropdownSelect('status', value)}
                        />
                      </td>
                      {/* 검색영역 리사이징 간격유지위해 th,td 삭제 X */}
                      <th></th>
                      <td></td>
                    </tr>
                  </tbody>
                </table>
              </div>
              <div style={{ width: '128px' }}>
                <Button className='btn-secondary-blue' style={{ width: '100%' }} onClick={handleSearch}>
                  조회
                </Button>
              </div>
            </UIGroup>
          </UIBox>
        </UIArticle>

        {/* 그리드 영역 */}
        <UIArticle className='article-grid'>
          <UIListContainer>
            <UIListContentBox.Header>
              <div className='w-full'>
                <UIUnitGroup gap={16} direction='column'>
                  <div className='flex justify-between w-full items-center'>
                    <div className='flex-shrink-0'>
                      <div style={{ width: '168px', paddingRight: '8px' }}>
                        <UIDataCnt count={logData?.totalElements || 0} prefix='사용 로그 총' unit='건' />
                      </div>
                    </div>
                  </div>
                </UIUnitGroup>
              </div>
            </UIListContentBox.Header>
            <UIListContentBox.Body>
              <UIGrid<LogDetailData>
                type='multi-select'
                rowData={filteredRowData}
                loading={isLoading}
                columnDefs={columnDefs}
                selectedDataList={selectedLogs}
                onCheck={(selectedDatas: LogDetailData[]) => {
                  setSelectedLogs(selectedDatas);
                }}
                onClickRow={(params: any) => {
                  handleRowClick(params.data);
                }}
              />
            </UIListContentBox.Body>
            <UIListContentBox.Footer className='ui-data-has-btn'>
              <Button className='btn-option-outlined' style={{ width: '65px' }} onClick={handleDownloadSelectedLogs}>
                다운로드
              </Button>
              <UIPagination
                currentPage={searchValues.page}
                totalPages={totalPages || 1}
                onPageChange={(newPage: number) => {
                  setSearchValues(prev => ({ ...prev, page: newPage }));
                }}
                className='flex justify-center'
              />
            </UIListContentBox.Footer>
          </UIListContainer>
        </UIArticle>
      </UIPageBody>

      {/* 사이드패널 */}
      {selectedLog && <LogAgentAllPopupPage selectedLog={selectedLog} onClose={handleCloseSidePanel} />}
    </section>
  );
}

export default LogAgentDetailPage;
