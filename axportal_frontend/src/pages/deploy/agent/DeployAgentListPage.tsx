import React, { useCallback, useMemo, useState } from 'react';

import { UIDataCnt, UILabel, UIToggle } from '@/components/UI';
import { UIBox, UITypography, type UILabelIntent } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGroup } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UICardList } from '@/components/UI/molecules/card/UICardList';
import { UIGridCard } from '@/components/UI/molecules/card/UIGridCard';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIGrid } from '@/components/UI/molecules/grid/UIGrid/component';
import { UIInput } from '@/components/UI/molecules/input';
import { UIListContentBox } from '@/components/UI/molecules/list/UIListContentBox';
import { Button } from '@/components/common/auth';
import { api } from '@/configs/axios.config';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { env, RUN_MODE_TYPES } from '@/constants/common/env.constants';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { AGENT_DEPLOY_STATUS } from '@/constants/deploy/agentDeploy.constants';
import { useLayerPopup } from '@/hooks/common/layer';
import { useBackRestoredState } from '@/hooks/common/navigation';
import { useDeleteAgentApp, useDeleteAgentAppDeploy, useGetAgentAppList } from '@/services/deploy/agent/agentDeploy.services';
import type { GetAgentAppDeployListByIdResponse, GetAgentAppResponse } from '@/services/deploy/agent/types';
import { useModal } from '@/stores/common/modal';
import dateUtils from '@/utils/common/date.utils';
import type { ColDef } from 'ag-grid-community';
import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { DeployAgentEditPopupPage } from './DeployAgentEditPopupPage';
interface SearchValues {
  page: number;
  size: number;
  searchKeyword: string;
  view: string;
}

export function DeployAgentListPage() {
  const { openAlert, openConfirm } = useModal();
  const layerPopupEdit = useLayerPopup(); // 수정 팝업용

  const navigate = useNavigate();

  // const [currentStep, setCurrentStep] = useState<number>(0);

  // 수정 팝업 상태 관리
  const [selectedEditData, setSelectedEditData] = useState<{ appId: string; name: string; description: string } | null>(null);

  // 검색 조건 (입력용)
  const { filters: searchValues, updateFilters: setSearchValues } = useBackRestoredState<SearchValues>(STORAGE_KEYS.SEARCH_VALUES.DEPLOY_AGENT_LIST, {
    page: 1,
    size: 12,
    searchKeyword: '',
    view: 'grid',
  });

  const { data, isSuccess, refetch, isLoading } = useGetAgentAppList(
    {
      page: searchValues.page,
      size: searchValues.size,
      targetType: 'all',
      sort: 'created_at,desc',
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

  const [dataList, setDataList] = useState<any[]>([]);

  // 총 페이지 (API 기준)
  const totalPages = isSuccess ? data?.totalPages || 1 : 1;

  useEffect(() => {
    if (isSuccess && data) {
      setDataList(data.content || []);
    }
  }, [data, isSuccess]);

  // 체크박스 상태 관리
  const [selectedDataList, setSelectedDataList] = useState<GetAgentAppResponse[]>([]);
  const handleSelect = useCallback((datas: GetAgentAppResponse[]) => {
    setSelectedDataList(datas);
  }, []);

  const handlePageChange = (newPage: number) => {
    updatePageSizeAndRefetch({ page: newPage });
  };

  // 조회 버튼
  const handleSearch = () => {
    setSearchValues(prev => ({ ...prev, page: 1 }));
    refetch();
  };

  /**
   * 데이터 삭제
   */
  const handleDeleteConfirm = async (ids: string[]) => {
    if (ids.length === 0) {
      openAlert({
        title: '안내',
        message: '삭제할 항목을 선택해주세요.',
      });
      return;
    }

    // isMigration이 true인 항목이 있는지 확인
    const migrationItems = ids.filter(id => {
      const item = dataList.find(d => d.id === id);
      return item?.isMigration === true;
    });

    if (migrationItems.length > 0) {
      await openAlert({
        title: '안내',
        message: `선택한 항목 중 ${migrationItems.length}개의 운영 배포된 에이전트가 포함되어 있습니다.\n운영 배포된 에이전트는 삭제할 수 없습니다.`,
      });
      return;
    }

    openConfirm({
      title: '안내',
      message: '삭제하시겠어요? \n삭제한 정보는 복구할 수 없습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: async () => {
        // 선택된 앱들의 모든 배포 버전 삭제
        try {
          const deployDeletePromises: Promise<PromiseSettledResult<any>>[] = [];

          for (const appId of ids) {
            try {
              // 각 앱의 배포 리스트 조회 (API 직접 호출)
              const response = await api.get<{ content: GetAgentAppDeployListByIdResponse }>(`agentDeploy/app/${appId}/deploy`, {
                params: {
                  page: 1,
                  size: 1000, // 충분히 큰 값으로 설정하여 전체 데이터 조회
                  sort: 'deployed_dt,desc',
                },
              });

              // 각 배포 버전 삭제
              const deployList = response.data?.content || [];
              if (Array.isArray(deployList) && deployList.length > 0) {
                for (const deployItem of deployList) {
                  if (deployItem.id) {
                    // 타임아웃 추가 (각 삭제 요청당 2초)
                    const deleteWithTimeout = Promise.race([
                      deleteAgentAppDeploy({ deployId: deployItem.id }),
                      new Promise((_, reject) => setTimeout(() => reject(new Error('타임아웃')), 2000)),
                    ]);

                    deployDeletePromises.push(
                      deleteWithTimeout
                        .then(result => ({ status: 'fulfilled' as const, value: result }))
                        .catch(error => {
                          console.error(`배포 버전 삭제 실패 (deployId: ${deployItem.id}):`, error);
                          return { status: 'rejected' as const, reason: error };
                        })
                    );
                  }
                }
              }
            } catch (error) {
              console.error(`앱 배포 리스트 조회 실패 (appId: ${appId}):`, error);
            }
          }

          // 모든 배포 버전 삭제 완료 대기 (성공/실패 관계없이 모두 완료될 때까지)
          const results = await Promise.allSettled(deployDeletePromises);

          // 실패한 항목이 있는지 확인
          const failedCount = results.filter(r => r.status === 'rejected').length;
          if (failedCount > 0) {
            console.warn(`${failedCount}개의 배포 버전 삭제가 실패했지만 앱 삭제를 계속 진행합니다.`);
          }
        } catch (error) {
          console.error('배포 버전 삭제 중 오류:', error);
          // 에러가 발생해도 앱 삭제는 계속 진행
        }

        // 배포 버전 삭제 완료 후 앱 삭제
        handleDelete(ids);
      },
      onCancel: () => { },
    });
  };

  /**
   * 배포된 에이전트 삭제
   */
  const { mutate: deleteAgentAppTool } = useDeleteAgentApp({
    onSuccess: () => { },
    onError: () => { },
  });

  /**
   * 배포 버전 삭제
   */
  const { mutateAsync: deleteAgentAppDeploy } = useDeleteAgentAppDeploy();

  /**
   * 데이터 삭제
   */
  const handleDelete = async (ids: string[]) => {
    let successCount = 0;
    let failCount = 0;

    // 순차적으로 삭제 처리
    for (const id of ids) {
      await new Promise<void>(resolve => {
        deleteAgentAppTool(
          { appId: id },
          {
            onSuccess: () => {
              successCount++;
              resolve();
            },
            onError: () => {
              failCount++;
              resolve();
            },
          }
        );
      });
    }

    if (ids.length === 1) {
      // 단건 삭제
      if (successCount > 0) {
        openAlert({
          title: '완료',
          message: '에이전트 배포가 삭제되었습니다.',
        });
      }
    } else {
      // 다건 삭제
      if (failCount == 0) {
        openAlert({
          title: '완료',
          message: '에이전트 배포 삭제가 완료되었습니다.',
        });
      } else {
        openAlert({
          title: '안내',
          message: `에이전트 배포 삭제가 완료되었습니다.\n${successCount}건 성공, ${failCount}건 실패\n\n실패한 항목은 확인 후 다시 시도해주세요.`,
        });
      }
    }

    // 성공적으로 삭제된 경우에만 목록 새로고침
    if (successCount > 0) {
      // 체크박스 상태 초기화
      setSelectedDataList([]);
      refetch();
    }
  };

  /**
   * 스테퍼 데이터
   */
  // const stepperItems: UIStepperItem[] = [
  //   {
  //     id: 'step1',
  //     label: '에이전트 선택',
  //     step: 1,
  //   },
  //   {
  //     id: 'step2',
  //     label: '배포 정보 입력',
  //     step: 2,
  //   },
  //   {
  //     id: 'step3',
  //     label: '자원 할당',
  //     step: 3,
  //   },
  // ];

  /**
   * 상세 페이지 이동
   * @param appId 에이전트 배포 고유 아이디
   */
  const handleRowClick = async (item: any) => {
    // targetId가 null이면 external_graph, 있으면 agent_graph
    const targetType = item.targetId ? 'agent_graph' : 'external_graph';
    navigate(`${item.id}`, {
      state: {
        targetType: targetType,
      },
    });
  };

  // const handleAgentAppDeployPopup = () => {
  //   setCurrentStep(1);
  // };

  // const handleNextStep = () => {
  //   setCurrentStep(prev => prev + 1);
  // };

  // const handlePreviousStep = () => {
  //   setCurrentStep(prev => prev - 1);
  // };

  // const handlePopupClose = () => {
  //   setCurrentStep(0);
  // };

  /**
   * 수정 팝업 열기
   */
  const handleOpenEditPopup = (rowData: any) => {
    layerPopupEdit.onOpen();
    setSelectedEditData({
      appId: rowData.id,
      name: rowData.name || '',
      description: rowData.description || '',
    });
  };

  // 더보기 메뉴 설정
  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          label: '수정',
          action: 'modify',
          auth: AUTH_KEY.DEPLOY.AGENT_DEPLOY_UPDATE,
          onClick: (rowData: any) => {
            handleOpenEditPopup(rowData);
          },
        },
        {
          label: '삭제',
          action: 'delete',
          auth: AUTH_KEY.DEPLOY.AGENT_DEPLOY_DELETE,
          onClick: (rowData: any) => {
            handleDeleteConfirm([rowData.id]);
          },
        },
      ],
      isActive: () => true, // 모든 배포에 대해 활성화
    }),
    []
  );

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
    <>
      {/* 섹션 페이지 */}
      <section className='section-page'>
        <UIPageHeader
          title='에이전트 배포'
          description={['배포한 에이전트의 정보를 확인하고 관리할 수 있습니다.', '배포 에이전트를 선택하여 간단한 사용방법과 시스템 로그를 확인해보세요.']}
        // actions={
        //   <>
        //     <Button
        //       auth={AUTH_KEY.DEPLOY.AGENT_DEPLOY_CREATE}
        //       className='btn-text-14-semibold-point'
        //       leftIcon={{ className: 'ic-system-24-add', children: '' }}
        //       onClick={handleAgentAppDeployPopup}
        //     >
        //       에이전트 배포
        //     </Button>
        //   </>
        // }
        />
        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle className='article-filter'>
            <UIBox className='box-filter'>
              <UIGroup gap={40} direction='row'>
                {/* 테이블 th = 80px 일 경우  */}
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
            <UIListContentBox.Header>
              <div className='flex-shrink-0'>
                <UIGroup gap={8} direction='row' align='start'>
                  <div style={{ width: '168px', paddingRight: '8px' }}>
                    <UIDataCnt count={data?.totalElements ?? 0} prefix='총' unit='건' />
                  </div>
                </UIGroup>
              </div>
              <div className='flex items-center gap-2'>
                <div style={{ width: '180px', flexShrink: 0 }}>
                  <UIDropdown
                    value={String(searchValues.size)}
                    disabled={(data?.totalElements ?? 0) === 0}
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
                  disabled={(data?.totalElements ?? 0) === 0}
                  onChange={checked => setSearchValues(prev => ({ ...prev, view: checked ? 'card' : 'grid' }))}
                />
              </div>
            </UIListContentBox.Header>
            <UIListContentBox.Body>
              {searchValues.view === 'grid' ? (
                <UIGrid<GetAgentAppResponse>
                  type='multi-select'
                  loading={isLoading}
                  selectedDataList={selectedDataList}
                  rowData={dataList}
                  columnDefs={columnDefs}
                  moreMenuConfig={moreMenuConfig}
                  onClickRow={(params: any) => {
                    handleRowClick(params.data);
                  }}
                  onCheck={handleSelect}
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
                      moreMenuConfig={moreMenuConfig}
                      data={item}
                      checkbox={{
                        checked: selectedDataList.some(data => data.id === item.id),
                        onChange: (checked: boolean, value: string) => {
                          if (checked) {
                            const newItem = dataList.find(data => data.id === value);
                            if (newItem) {
                              setSelectedDataList([...selectedDataList, newItem]);
                            }
                          } else {
                            setSelectedDataList(selectedDataList.filter(data => data.id !== value));
                          }
                        },
                      }}
                      onClick={(e: any) => {
                        // 체크박스/라벨 클릭 시 카드 onClick 방지
                        const target = e?.target as HTMLElement | null;
                        if (target && (target.closest('input[type="checkbox"]') || target.closest('label'))) {
                          e.stopPropagation?.();
                          return;
                        }
                        handleRowClick(item);
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
            <UIListContentBox.Footer className='ui-data-has-btn'>
              <Button
                auth={AUTH_KEY.DEPLOY.AGENT_DEPLOY_DELETE}
                className='btn-option-outlined'
                style={{ width: '40px' }}
                disabled={(data?.totalElements ?? 0) === 0}
                onClick={() => {
                  handleDeleteConfirm(selectedDataList.map(item => item.id));
                }}
              >
                삭제
              </Button>
              <UIPagination currentPage={searchValues.page} totalPages={totalPages} onPageChange={handlePageChange} hasNext={data?.hasNext} className='flex justify-center' />
            </UIListContentBox.Footer>
          </UIArticle>
        </UIPageBody>
      </section>

      {/* 팝업들을 DeployAgentProvider로 감싸서 독립적인 상태 관리 */}
      {/* <DeployAgentProvider> */}
      {/* Step 1. 에이전트 배포 만들기 */}
      {/* <DeployAgentStep1AgentSelectPopupPage isOpen={currentStep === 1} stepperItems={stepperItems} onClose={handlePopupClose} onNextStep={handleNextStep} /> */}
      {/* Step 2. 배포 정보 입력 */}
      {/* <DeployAgentStep2InfoInputPopupPage
          isOpen={currentStep === 2}
          stepperItems={stepperItems}
          onClose={handlePopupClose}
          onNextStep={handleNextStep}
          onPreviousStep={handlePreviousStep}
        /> */}
      {/* Step 3. 자원 할당 */}
      {/* <DeployAgentStep3ResAllocPopupPage isOpen={currentStep === 3} stepperItems={stepperItems} onClose={handlePopupClose} onPreviousStep={handlePreviousStep} />
      </DeployAgentProvider> */}

      {/* 수정 팝업 */}
      <DeployAgentEditPopupPage
        appId={selectedEditData?.appId || ''}
        name={selectedEditData?.name || ''}
        description={selectedEditData?.description || ''}
        isOpen={layerPopupEdit.currentStep > 0}
        onClose={layerPopupEdit.onClose}
        onUpdateSuccess={() => {
          layerPopupEdit.onClose();
          refetch();
          setSelectedEditData(null);
        }}
      />
    </>
  );
}
