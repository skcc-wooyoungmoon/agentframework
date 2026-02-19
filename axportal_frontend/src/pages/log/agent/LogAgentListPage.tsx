import React, { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { Button } from '@/components/common/auth';
import { UIBox, UIDataCnt, UILabel, UIPagination, UIToggle, UITypography, type UILabelIntent } from '@/components/UI/atoms';
import { UIArticle, UIDropdown, UIGroup, UIInput, UIPageBody, UIPageHeader } from '@/components/UI/molecules';
import { UICardList } from '@/components/UI/molecules/card/UICardList';
import { UIGridCard } from '@/components/UI/molecules/card/UIGridCard';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { env, RUN_MODE_TYPES } from '@/constants/common/env.constants';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { AGENT_DEPLOY_STATUS } from '@/constants/deploy/agentDeploy.constants';
import { useBackRestoredState } from '@/hooks/common/navigation';
import { useGetAgentAppList } from '@/services/deploy/agent/agentDeploy.services';
import type { GetAgentAppResponse } from '@/services/deploy/agent/types';
import { dateUtils } from '@/utils/common';
import type { ColDef } from 'ag-grid-community';

interface SearchValues {
  page: number;
  size: number;
  searchKeyword: string;
  view: string;
}

export const LogAgentListPage = () => {
  const navigate = useNavigate();

  const { filters: searchValues, updateFilters: setSearchValues } = useBackRestoredState<SearchValues>(STORAGE_KEYS.SEARCH_VALUES.AGENT_DEPLOY_LOG_LIST, {
    page: 1,
    size: 12, // 기본 12개씩 표시
    searchKeyword: '',
    view: 'grid',
  });

  const { data, isSuccess, refetch, isLoading } = useGetAgentAppList(
    {
      page: searchValues.page, // 백엔드 API는 1부터 시작
      size: searchValues.size,
      targetType: 'all',
      sort: '',
      filter: '',
      search: searchValues.searchKeyword,
    },
    {
      enabled: !env.VITE_NO_PRESSURE_MODE,// 조회 중에도 기존 데이터 유지
      placeholderData: previousData => previousData, // 조회 중에도 기존 데이터 유지
    }
  );

  const updatePageSizeAndRefetch = (patch: Partial<Pick<SearchValues, 'page' | 'size'>>) => {
    setSearchValues(prev => ({ ...prev, ...patch }));
    setTimeout(() => refetch(), 0);
  };

  const [dataList, setDataList] = useState<GetAgentAppResponse[]>([]);

  useEffect(() => {
    if (isSuccess && data) {
      setDataList((data.content as unknown as GetAgentAppResponse[]) || []);
    }
  }, [data, isSuccess]);

  // 조회 버튼
  const handleSearch = () => {
    setSearchValues(prev => ({ ...prev, page: 1 }));
    refetch();
  };

  const handleSearchKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  const handleDetailClick = (appId: string) => {
    navigate(`${appId}`);
  };

  // 그리드 컬럼 정의
  const columnDefs: ColDef[] = useMemo(
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
        valueGetter: (params: any) => (searchValues.page - 1) * searchValues.size + params.node.rowIndex + 1,
      },
      {
        headerName: '배포명',
        field: 'name',
        width: 272,
        minWidth: 272,
        maxWidth: 272,
        suppressSizeToFit: true,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '빌더명',
        field: 'builderName',
        width: 272,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '상태',
        field: 'deploymentStatus',
        width: 120,
        valueGetter: (params: any) => {
          // deploymentStatus가 null이면 deployments 배열에서 가장 최신 버전의 status 사용
          if (params.data?.deploymentStatus) {
            return params.data.deploymentStatus;
          }
          if (params.data?.deployments && Array.isArray(params.data.deployments) && params.data.deployments.length > 0) {
            // version이 높은 순으로 정렬하여 가장 최신 버전의 status 반환
            const sortedDeployments = [...params.data.deployments].sort((a, b) => (b.version || 0) - (a.version || 0));
            return sortedDeployments[0]?.status || '';
          }
          return '';
        },
        cellRenderer: (params: any) => {
          const statusValue = params.value || params.data?.deploymentStatus;

          // 상태 배지 로직 (인라인 처리)
          let label = '';
          let intent: UILabelIntent = 'gray';

          if (!statusValue) {
            const fallback = AGENT_DEPLOY_STATUS.Failed;
            label = fallback.label;
            intent = fallback.intent as UILabelIntent;
          } else {
            const normalized = Object.keys(AGENT_DEPLOY_STATUS).find(key => key.toLowerCase() === String(statusValue).toLowerCase());

            if (!normalized) {
              label = statusValue;
              intent = 'gray';
            } else {
              const config = AGENT_DEPLOY_STATUS[normalized as keyof typeof AGENT_DEPLOY_STATUS];
              label = config.label;
              intent = (config.intent as UILabelIntent) || 'gray';
            }
          }

          return (
            <UILabel variant='badge' intent={intent}>
              {label}
            </UILabel>
          );
        },
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '설명',
        field: 'description',
        minWidth: 392,
        flex: 1,
        showTooltip: true,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '배포 유형',
        field: '',
        width: 120,
        valueGetter: (params: any) => {
          return params.data.deployments[0]?.targetType === 'agent_graph' ? '기본' : '사용자 정의';
        },
      },
      {
        headerName: '버전',
        field: 'deploymentVersion',
        width: 120,
        valueGetter: (params: any) => {
          return params.data.deploymentVersion ? `ver.${params.data.deploymentVersion}` : '';
        },
      },
      {
        headerName: '운영 배포 여부', // TODO : 확인 필요
        field: 'isMigration',
        width: 120,
        valueGetter: (params: any) => {
          return env.VITE_RUN_MODE !== RUN_MODE_TYPES.PROD ? (params.data.isMigration ? '배포' : '미배포') : '배포';
        },
      },
      {
        headerName: '공개범위',
        field: 'publicStatus',
        width: 120,
      },
      {
        headerName: '생성일시',
        field: 'createdAt',
        width: 180,
        cellStyle: {
          paddingLeft: 16,
        },
        valueGetter: (params: any) => {
          return dateUtils.formatDate(params.data.createdAt, 'datetime');
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'updatedAt',
        width: 180,
        cellStyle: {
          paddingLeft: 16,
        },
        valueGetter: (params: any) => {
          return dateUtils.formatDate(params.data.updatedAt, 'datetime') || dateUtils.formatDate(params.data.createdAt, 'datetime');
        },
      },
      {
        headerName: '',
        field: 'more', // 더보기 컬럼 필드명 (고정)
        width: 56,
      },
    ],
    [data]
  );

  return (
    <section className='section-page'>
      <UIPageHeader title='에이전트 사용 로그' description='에이전트의 배포 로그와 사용 로그를 확인하고 관리할 수 있습니다.' />
      <UIPageBody>
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
                      <td colSpan={3}>
                        <div className='flex-1'>
                          <UIInput.Search
                            value={searchValues.searchKeyword}
                            placeholder='배포명 입력'
                            maxLength={50}
                            onChange={e => setSearchValues(prev => ({ ...prev, searchKeyword: e.target.value.slice(0, 50) }))}
                            onKeyDown={handleSearchKeyDown}
                          />
                        </div>
                      </td>
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

        <UIArticle className='article-grid'>
          <UIListContainer>
            <UIListContentBox.Header>
              <div className='article-header'>
                <div className='grid-header-left'>
                  <UIDataCnt count={data?.totalElements || dataList.length} prefix='총' />
                </div>
              </div>
              <div className='flex items-center gap-[8px]'>
                <div style={{ width: '180px', flexShrink: 0 }}>
                  <UIDropdown
                    value={String(searchValues.size)}
                    options={[
                      { value: '12', label: '12개씩 보기' },
                      { value: '36', label: '36개씩 보기' },
                      { value: '60', label: '60개씩 보기' },
                    ]}
                    onSelect={(value: string) => updatePageSizeAndRefetch({ size: Number(value), page: 1 })}
                    height={40}
                    variant='dataGroup'
                  />
                </div>
                <UIToggle
                  variant='dataView'
                  checked={searchValues.view === 'card'}
                  disabled={(data?.totalElements ?? dataList.length) === 0}
                  onChange={checked => setSearchValues(prev => ({ ...prev, view: checked ? 'card' : 'grid' }))}
                />
              </div>
            </UIListContentBox.Header>
            <UIListContentBox.Body>
              {searchValues.view === 'grid' ? (
                <UIGrid<GetAgentAppResponse>
                  loading={isLoading}
                  rowData={dataList}
                  columnDefs={columnDefs as any}
                  onClickRow={(params: any) => {
                    handleDetailClick(params.data.id);
                  }}
                />
              ) : (
                <UICardList
                  loading={isLoading}
                  rowData={dataList}
                  flexType='none'
                  card={(item: any) => (
                    <UIGridCard
                      key={item.id}
                      id={item.id}
                      data={item}
                      onClick={(e: any) => {
                        // 체크박스/라벨 클릭 시 카드 onClick 방지
                        const target = e?.target as HTMLElement | null;
                        if (target && (target.closest('input[type="checkbox"]') || target.closest('label'))) {
                          e.stopPropagation?.();
                          return;
                        }
                        handleDetailClick(item.id);
                      }}
                      title={item.name}
                      caption={item.description || ''}
                      rows={[
                        {
                          label: '버전',
                          value: item.deploymentVersion?.toString() ? `ver.${item.deploymentVersion?.toString()}` : '',
                        },
                        {
                          label: '운영배포 여부',
                          value: item.servingType === 'serving' ? '배포' : '미배포',
                        },
                        { label: '생성일시', value: item.createdAt ? dateUtils.formatDate(item.createdAt, 'datetime') : '' },
                        { label: '최종수정일시', value: item.updatedAt ? dateUtils.formatDate(item.updatedAt, 'datetime') : '' },
                      ]}
                      statusArea={(() => {
                        // deploymentStatus가 null이면 deployments 배열에서 가장 최신 버전의 status 사용
                        let statusValue = item.deploymentStatus;
                        if (!statusValue && item.deployments && Array.isArray(item.deployments) && item.deployments.length > 0) {
                          const sortedDeployments = [...item.deployments].sort((a: any, b: any) => (b.version || 0) - (a.version || 0));
                          statusValue = sortedDeployments[0]?.status;
                        }

                        // 상태 배지 로직 (인라인 처리)
                        let label = '';
                        let intent: UILabelIntent = 'gray';

                        if (!statusValue) {
                          const fallback = AGENT_DEPLOY_STATUS.Failed;
                          label = fallback.label;
                          intent = fallback.intent as UILabelIntent;
                        } else {
                          const normalized = Object.keys(AGENT_DEPLOY_STATUS).find(key => key.toLowerCase() === String(statusValue).toLowerCase());

                          if (!normalized) {
                            label = statusValue;
                            intent = 'gray';
                          } else {
                            const config = AGENT_DEPLOY_STATUS[normalized as keyof typeof AGENT_DEPLOY_STATUS];
                            label = config.label;
                            intent = (config.intent as UILabelIntent) || 'gray';
                          }
                        }

                        return (
                          <UILabel variant='badge' intent={intent}>
                            {label}
                          </UILabel>
                        );
                      })()}
                    />
                  )}
                />
              )}
            </UIListContentBox.Body>
            <UIListContentBox.Footer>
              <UIPagination
                currentPage={searchValues.page}
                totalPages={data?.totalPages || 1}
                onPageChange={(page: number) => updatePageSizeAndRefetch({ page })}
                hasNext={data?.hasNext}
                className='flex justify-center'
              />
            </UIListContentBox.Footer>
          </UIListContainer>
        </UIArticle>
      </UIPageBody>
    </section>
  );
};
