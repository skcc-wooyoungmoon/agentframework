import React, { useEffect, useMemo, useState } from 'react';

import { UIBox, UIButton2, UIDataCnt, UILabel, UIPagination, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIDropdown, UIGroup, UIInput, UIPageBody, UIPageHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { LogModelAllPopupPage } from '@/pages/log/model/LogModelAllPopupPage.tsx';
import { useGetModelDeployDetail } from '@/services/deploy/model/modelDeploy.services';
import { type SafetyFilter, useGetSafetyFilterList } from '@/services/deploy/safetyFilter';
import { useGetModelHistoryList } from '@/services/log/model/model.service';
import type { GetModelHistoryListRequest, LogGridItem } from '@/services/log/model/types.ts';
import { useModal } from '@/stores/common/modal';
import { dateUtils } from '@/utils/common';
import { useParams } from 'react-router-dom';

const dropdownOptions = [
  { value: 'false', label: '정상' },
  { value: 'true', label: '실패' },
];

export const LogModelDetailPage = () => {
  const { id } = useParams<{ id: string }>();
  const { openAlert } = useModal();

  // CSV 다운로드 API 훅
  // const downloadCsvMutation = useDownloadModelHistoryCsv();

  // 검색 조건
  const [searchValues, setSearchValues] = useState<GetModelHistoryListRequest>({
    page: 1,
    size: 6,
    search: undefined,
    from_date: dateUtils.formatDate(dateUtils.subtractFromDate(new Date(), 30, 'days'), 'short'),
    to_date: dateUtils.formatDate(dateUtils.addToDate(new Date(), 1, 'days'), 'short'),
    filter: `model_serving_id:${id}`,
    sort: 'request_time,desc',
  });

  // API 호출 - id가 있을 때만 호출
  const {
    data: modelHistoryData,
    refetch,
    isFetching,
  } = useGetModelHistoryList(searchValues, {
    enabled: !!id, // id가 있을 때만 API 호출
  });

  const { data: modelDeployDetail } = useGetModelDeployDetail(id ?? '');

  // 모델 배포 내 세이프티 필터 관련 조회
  const { data: safetyFilterInputList } = useGetSafetyFilterList(
    {
      size: modelDeployDetail?.safetyFilterInputGroups?.length ?? 0,
      filter: `group_id:${modelDeployDetail?.safetyFilterInputGroups?.join('|')}`,
    },
    {
      enabled: !!modelDeployDetail?.safetyFilterInputGroups && modelDeployDetail?.safetyFilterInputGroups?.length > 0,
    }
  );
  const { data: safetyFilterOutputList } = useGetSafetyFilterList(
    {
      size: modelDeployDetail?.safetyFilterOutputGroups?.length ?? 0,
      filter: `group_id:${modelDeployDetail?.safetyFilterOutputGroups?.join('|')}`,
    },
    {
      enabled: !!modelDeployDetail?.safetyFilterOutputGroups && modelDeployDetail?.safetyFilterOutputGroups?.length > 0,
    }
  );

  useEffect(() => {
    refetch();
  }, [searchValues]);

  // API 데이터를 그리드 형식으로 변환
  const processedData: LogGridItem[] = useMemo(() => {
    if (!modelHistoryData?.content) return [];

    return modelHistoryData.content.map((item: any, index: number) => ({
      ...item,
      outputJsonGrid: item.outputJson && item.outputJson.length > 1000 ? item.outputJson.substring(0, 1000) : item.outputJson,
      no: (searchValues.page - 1) * searchValues.size + index + 1,
    }));
  }, [modelHistoryData, searchValues.page, searchValues.size]);

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
        } as any,
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '요청일시',
        field: 'requestTime',
        width: 180,
        valueFormatter: (params: any) => {
          return params.value ? dateUtils.formatDateWithPattern(params.value, 'yyyy.MM.dd HH:mm:ss', false) : '';
        },
      },
      {
        headerName: '상태',
        field: 'errorCode',
        width: 120,
        cellRenderer: (params: any) => {
          return (
            <div className={'h-full flex items-center'}>
              <UILabel variant='badge' intent={params.value ? 'error' : ('complete' as any)}>
                {params.value ? '실패' : '정상'}
              </UILabel>
            </div>
          );
        },
      },
      {
        headerName: '총 소요시간',
        field: 'elapsedTime',
        width: 120,
        valueFormatter: (params: any) => {
          return params.value ? `${Math.floor(params.value * 1000)}ms` : '';
        },
      },
      {
        headerName: '내용',
        field: 'description',
        flex: 1,
        cellClass: 'auto-height-cell',
        cellRenderer: (params: any) => {
          return (
            <div
              style={
                {
                  display: '-webkit-box',
                  WebkitLineClamp: 4,
                  WebkitBoxOrient: 'vertical',
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  whiteSpace: 'pre-wrap',
                  wordBreak: 'break-word',
                  lineHeight: '1.4',
                  margin: '14px 16px',
                } as React.CSSProperties
              }
            >
              request: {params.data.inputJson}
              <br />
              response: {params.data.outputJsonGrid}
            </div>
          );
        },
        autoHeight: true,
        cellStyle: {
          padding: 0,
        } as any,
      },
      {
        headerName: '거래 식별자',
        field: 'user',
        width: 120,
      },
    ],
    []
  );

  const [searchText, setSearchText] = useState('');

  // date 타입
  const [dateValueStart, setDateValueStart] = useState(dateUtils.subtractFromDate(new Date(), 30, 'days'));
  // const [dateValueEnd, setDateValueEnd] = useState(new Date());
  const [dateValueEnd, setDateValueEnd] = useState(dateUtils.addToDate(new Date(), 1, 'days'));

  // 드롭다운 상태
  const [dropdownStates, setDropdownStates] = useState({
    status: false,
    callType: false,
  });
  const [searchErrorLog, setSearchErrorLog] = useState<string>('false');

  const [selectedLog, setSelectedLog] = useState<LogGridItem | undefined>(undefined);

  const [checkedLogList, setCheckedLogList] = useState<LogGridItem[]>([]);

  const handleDropdownToggle = (key: string) => {
    setDropdownStates(prev => ({
      ...prev,
      [key]: !prev[key as keyof typeof prev],
    }));
  };

  const handleDropdownSelect = (key: string, value: string) => {
    setDropdownStates(prev => ({ ...prev, [key]: false }));
    setSearchErrorLog(value);
  };

  const handleSearch = () => {
    // 검색 시 페이지를 1로 리셋하고 검색 조건 업데이트
    setSearchValues(prev => ({
      ...prev,
      page: 1,
      search: searchText ? `input_json:${searchText},output_json:${searchText}` : undefined,
      from_date: dateUtils.formatDate(dateValueStart, 'short'),
      to_date: dateUtils.formatDate(dateValueEnd, 'short'),
      error_logs: searchErrorLog === 'true',
    }));
  };

  const handleDownload = () => {
    if (checkedLogList.length === 0) {
      openAlert({
        message: '다운로드할 로그를 선택해주세요.',
      });
      return;
    }

    // CSV 헤더 정의
    const headers = ['NO', '요청일시', '상태', '총 소요시간(ms)', '내용', '모델명', '사용자', '완성 토큰', '프롬프트 토큰', '총 토큰'];

    // CSV 데이터 변환
    const csvData = checkedLogList.map((log, index) => {
      const formattedDate = dateUtils.formatDateWithPattern(log.requestTime, 'yyyy.MM.dd HH:mm:ss', false);
      const elapsedTime = log.elapsedTime ? `${Math.floor(log.elapsedTime * 1000)}` : '';
      const status = log.errorMessage ? '실패' : '정상';
      const content = `request: ${log.inputJson || ''}\nresponse: ${log.outputJson || ''}`;

      return [
        index + 1,
        formattedDate,
        status,
        elapsedTime,
        `"${content.replace(/"/g, '""')}"`, // CSV에서 따옴표 이스케이프
        log.modelName || '',
        log.user || '',
        log.completionTokens || '',
        log.promptTokens || '',
        log.totalTokens || '',
      ];
    });

    // CSV 문자열 생성
    const csvContent = [headers.join(','), ...csvData.map(row => row.join(','))].join('\n');

    // BOM 추가 (한글 깨짐 방지)
    const BOM = '\uFEFF';
    const csvBlob = new Blob([BOM + csvContent], { type: 'text/csv;charset=utf-8;' });

    // 파일 다운로드
    const url = URL.createObjectURL(csvBlob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `model_log_${id}_${dateUtils.formatDate(new Date(), 'datetime').replace(/[:\s]/g, '_')}.csv`;
    link.click();
    URL.revokeObjectURL(url);
  };

  // const handleAllDownload = async () => {
  //   if (!id) {
  //     openAlert({
  //       message: '모델 ID가 없습니다.',
  //     });
  //     return;
  //   }
  //
  //   try {
  //     // 현재 검색 조건을 기반으로 다운로드 파라미터 생성
  //     const downloadParams = {
  //       from_date: dateUtils.formatDate(dateValueStart, 'short'),
  //       to_date: dateUtils.formatDate(dateValueEnd, 'short'),
  //       filter: `model_serving_id:${id}`,
  //       error_logs: searchErrorLog === 'true',
  //       search: searchText ? `input_json:${searchText},output_json:${searchText}` : undefined,
  //       sort: 'request_time,desc',
  //     };
  //
  //     // CSV 다운로드 API 호출
  //     const response = await downloadCsvMutation.mutateAsync(downloadParams);
  //
  //     // Blob을 파일로 다운로드
  //     const blob = new Blob([response], { type: 'text/csv;charset=utf-8;' });
  //     const url = URL.createObjectURL(blob);
  //     const link = document.createElement('a');
  //     link.href = url;
  //     link.download = `model_log_all_${id}_${dateUtils.formatDate(new Date(), 'short').replace(/[:\s]/g, '_')}.csv`;
  //     link.click();
  //     URL.revokeObjectURL(url);
  //
  //     openAlert({
  //       message: '로그 전체 다운로드가 완료되었습니다.',
  //     });
  //   } catch (error) {
  //     // console.error('CSV 다운로드 실패:', error);
  //     openAlert({
  //       message: '다운로드 중 오류가 발생했습니다.',
  //     });
  //   }
  // };

  return (
    <>
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader title='모델 사용 로그 조회' description='' />

        <UIPageBody>
          <UIArticle className='pb-4'>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                모델 정보
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
                          모델명
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {modelDeployDetail?.modelName}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          표시이름
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {modelDeployDetail?.displayName}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          배포명
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {modelDeployDetail?.name}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          배포 유형
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {modelDeployDetail?.servingType}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          설명
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {modelDeployDetail?.description}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          입력 세이프티 필터
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {safetyFilterInputList?.content?.map((filter: SafetyFilter) => filter.filterGroupName).join(', ') || ''}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          출력 세이프티 필터
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {safetyFilterOutputList?.content?.map((filter: SafetyFilter) => filter.filterGroupName).join(', ') || ''}
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
                            <div className='flex-1'>
                              <UIInput.Search value={searchText} placeholder='내용 입력' onChange={e => setSearchText(e.target.value)} />
                            </div>
                          </UIUnitGroup>
                        </td>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            조회 기간
                          </UITypography>
                        </th>
                        <td>
                          <div className='flex-1' style={{ zIndex: '10' }}>
                            <UIUnitGroup gap={4} direction='row' vAlign='center'>
                              <div className='flex-1'>
                                <UIInput.Date
                                  value={dateUtils.formatDate(dateValueStart, 'custom', { pattern: 'yyyy.MM.dd' })}
                                  onChange={e => {
                                    const newStartDate = new Date(e.target.value);
                                    setDateValueStart(newStartDate);

                                    // 시작일 변경 시 종료일과의 차이가 30일을 초과하면 종료일을 조정
                                    const daysDiff = dateUtils.getDateDifference(newStartDate, dateValueEnd, 'days');
                                    if (daysDiff > 30) {
                                      setDateValueEnd(dateUtils.addToDate(newStartDate, 30, 'days'));
                                    } else if (daysDiff < 0) {
                                      setDateValueEnd(newStartDate);
                                    }
                                  }}
                                />
                              </div>
                              <UITypography variant='body-1' className='secondary-neutral-p w-[11px]'>
                                ~
                              </UITypography>
                              <div className='flex-1'>
                                <UIInput.Date
                                  value={dateUtils.formatDate(dateValueEnd, 'custom', { pattern: 'yyyy.MM.dd' })}
                                  onChange={e => {
                                    const newEndDate = new Date(e.target.value);
                                    setDateValueEnd(newEndDate);

                                    // 종료일 변경 시 시작일과의 차이가 30일을 초과하면 시작일을 조정
                                    const daysDiff = dateUtils.getDateDifference(dateValueStart, newEndDate, 'days');
                                    if (daysDiff > 30) {
                                      setDateValueStart(dateUtils.addToDate(newEndDate, -30, 'days'));
                                    } else if (daysDiff < 0) {
                                      setDateValueStart(newEndDate);
                                    }
                                  }}
                                />
                              </div>
                            </UIUnitGroup>
                          </div>
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
                            value={searchErrorLog}
                            placeholder='로그 종류 선택'
                            options={dropdownOptions}
                            isOpen={dropdownStates.status}
                            onClick={() => handleDropdownToggle('status')}
                            onSelect={value => handleDropdownSelect('status', value)}
                          />
                        </td>
                        {/* [참고] 검색 폼 영역 : 리사이징 가변 가로 넓이를 맞추기위해 아래 th, td 빈 값으로 노출 (태그 삭제 X) */}
                        <th></th>
                        <td></td>
                      </tr>
                    </tbody>
                  </table>
                </div>
                <div style={{ width: '128px' }}>
                  <UIButton2 className='btn-secondary-blue' style={{ width: '100%' }} onClick={handleSearch}>
                    조회
                  </UIButton2>
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
                          <UIDataCnt count={modelHistoryData?.totalElements} />
                        </div>
                      </div>
                    </div>
                  </UIUnitGroup>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid
                  type='multi-select'
                  loading={isFetching}
                  rowData={processedData || []}
                  columnDefs={columnDefs}
                  domLayout={'autoHeight'}
                  onClickRow={(params: any) => {
                    // console.log('단일 onClickRow', params);
                    setSelectedLog(params.data);
                  }}
                  onCheck={(checkLogList: LogGridItem[]) => {
                    setCheckedLogList(checkLogList);
                  }}
                />
              </UIListContentBox.Body>
              <UIListContentBox.Footer className='ui-data-has-btn'>
                <UIButton2 className='btn-option-outlined' style={{ width: '65px' }} disabled={!(checkedLogList.length > 0)} onClick={handleDownload}>
                  다운로드
                </UIButton2>
                <UIPagination
                  currentPage={(modelHistoryData?.pageable?.page ?? 0) + 1}
                  totalPages={modelHistoryData?.totalPages || 1}
                  onPageChange={page => {
                    setSearchValues(prev => ({ ...prev, page }));
                  }}
                  className='flex justify-center'
                  hasNext={modelHistoryData?.hasNext}
                />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>

      {selectedLog && (
        <LogModelAllPopupPage
          logItem={selectedLog}
          onClose={() => {
            setSelectedLog(undefined);
          }}
        />
      )}
    </>
  );
};
