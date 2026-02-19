import { useEffect, useMemo, useState } from 'react';

import { useNavigate, useParams } from 'react-router-dom';

import { Button } from '@/components/common/auth';
import { ManagerInfoBox } from '@/components/common/manager';
import { ProjectInfoBox } from '@/components/common/project/ProjectInfoBox/component';
import { UIDataCnt, UILabel, UITextLabel, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid/UIGrid/component';
import { UIListContainer } from '@/components/UI/molecules/list/UIListContainer/component';
import { UIListContentBox } from '@/components/UI/molecules/list/UIListContentBox';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageFooter } from '@/components/UI/molecules/UIPageFooter';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { useLayerPopup } from '@/hooks/common/layer';
import { useDeleteAgentMcpCtlgById, useGetAgentMcpById, useGetAgentMcpByIdSycnTools } from '@/services/agent/mcp/agentMcp.services';
import { useModal } from '@/stores/common/modal';

import { AgentMcpCtlgEditPopupPage } from './AgentMcpCtlgEditPopupPage';
import { AgentMcpCtlgToolDetailPopupPage } from './AgentMcpCtlgToolDetailPopupPage';

import type { ColDef } from 'node_modules/ag-grid-community/dist/types/src/entities/colDef';

export function AgentMcpCtlgDetailPage() {
  const { openModal, openAlert, openConfirm } = useModal();

  const navigate = useNavigate();
  const layerPopupOne = useLayerPopup();
  const { mcpId } = useParams<{ mcpId: string }>();
  const { data: agentMcpCtlgData, refetch } = useGetAgentMcpById({ mcpId: mcpId || '' }, { enabled: !!mcpId });
  const { data: agentMcpByIdSycnToolsData, refetch: refetchAgentMcpByIdSycnTools } = useGetAgentMcpByIdSycnTools({ mcpId: mcpId || '' }, { enabled: false }); // 초기에는 비활성화

  const [toolListData, setToolListData] = useState<any[]>([]);
  const [toolListPage, setToolListPage] = useState<number>(1);
  const [isUsingSyncData, setIsUsingSyncData] = useState<boolean>(false);
  const pageSize = 6;

  const paginatedToolListData = useMemo(() => {
    if (!Array.isArray(toolListData)) {
      return [];
    }
    const startIndex = (toolListPage - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    return toolListData.slice(startIndex, endIndex);
  }, [toolListData, toolListPage, pageSize]);

  useEffect(() => {
    if (agentMcpCtlgData && !isUsingSyncData) {
      const initialTools = (agentMcpCtlgData as any)?.tools || [];
      setToolListData(initialTools);
      setToolListPage(1);
    }
  }, [agentMcpCtlgData, isUsingSyncData]);

  useEffect(() => {
    if (agentMcpByIdSycnToolsData && isUsingSyncData) {
      let toolsArray: any[] = [];
      const data = agentMcpByIdSycnToolsData as any;
      if (Array.isArray(data)) {
        toolsArray = data;
      } else if (data?.data && Array.isArray(data.data)) {
        toolsArray = data.data;
      } else if (data?.data?.data && Array.isArray(data.data.data)) {
        toolsArray = data.data.data;
      }
      setToolListData(toolsArray);
      setToolListPage(1);
    }
  }, [agentMcpByIdSycnToolsData, isUsingSyncData]);

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
        },
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
        valueGetter: (params: any) => (toolListPage - 1) * pageSize + params.node.rowIndex + 1,
      },
      {
        headerName: '툴 이름',
        field: 'name',
        width: 272,
      },
      {
        headerName: '설명',
        field: 'description',
        flex: 1,
        showTooltip: false,
      },
    ],
    [agentMcpByIdSycnToolsData, toolListPage, pageSize]
  );

  const handleUpdateMcpCtlg = () => {
    layerPopupOne.onOpen();
  };

  const handleRefreshTools = () => {
    const isEnabled = !!(agentMcpCtlgData as any)?.enabled;

    if (!isEnabled) {
      openAlert({
        title: '알림',
        message: 'MCP 서버가 이용 불가능한 상태입니다. \n서버 상태를 확인해주세요.',
      });
      return;
    }

    setIsUsingSyncData(true);
    refetchAgentMcpByIdSycnTools();
  };

  const handleToolRowClick = (params: any) => {
    const toolJson = JSON.stringify(params.data, null, 2);

    openModal({
      title: '툴 상세',
      type: 'medium',
      body: <AgentMcpCtlgToolDetailPopupPage tool={toolJson} />,
      showFooter: false,
    });
  };

  const handleDeleteConfirm = async (toolId: string) => {
    openConfirm({
      title: '안내',
      message: '삭제하시겠어요? \n삭제한 정보는 복구할 수 없습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        deleteAgentMcpCtlg({ mcpId: toolId });
      },
      onCancel: () => { },
    });
  };

  const { mutate: deleteAgentMcpCtlg } = useDeleteAgentMcpCtlgById({
    onSuccess: () => {
      openAlert({
        title: '완료',
        message: '에이전트 MCP 서버가 삭제되었습니다.',
        onConfirm: () => {
          navigate('/agent/mcp', { replace: true });
        },
      });
    },
    onError: () => {
      openAlert({
        title: '실패',
        message: '에이전트 MCP 서버 삭제에 실패하였습니다.',
      });
    },
  });

  return (
    <>
      <section className='section-page'>
        <UIPageHeader title='MCP 서버 조회' description='' />

        <UIPageBody>
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                MCP 서버 정보
              </UITypography>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  <colgroup>
                    <col style={{ width: '128px' }} />
                    <col style={{ width: 'auto' }} />
                    <col style={{ width: '128px' }} />
                    <col style={{ width: 'auto' }} />
                  </colgroup>
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          이름
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {agentMcpCtlgData?.name}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          상태
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {(() => {
                            const status = !!(agentMcpCtlgData as any)?.enabled;
                            const intent = status ? 'complete' : 'error';
                            return (
                              <UILabel variant='badge' intent={intent as any}>
                                {status ? '이용 가능' : '이용 불가능'}
                              </UILabel>
                            );
                          })()}
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
                          {agentMcpCtlgData?.description}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          유형
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {(agentMcpCtlgData as any)?.type}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          태그
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {(agentMcpCtlgData as any)?.tags && Array.isArray((agentMcpCtlgData as any).tags) ? (
                            <div className='flex items-center gap-1'>
                              {(agentMcpCtlgData as any).tags.map((tag: any, index: number) => {
                                const tagName = typeof tag === 'string' ? tag : tag?.name || '';
                                return (
                                  <UITextLabel key={index} intent='tag'>
                                    {tagName}
                                  </UITextLabel>
                                );
                              })}
                            </div>
                          ) : (
                            <UITextLabel intent='tag'>{(agentMcpCtlgData as any)?.tags || ''}</UITextLabel>
                          )}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          전송 유형
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {(agentMcpCtlgData as any)?.transportType === 'streamable-http'
                            ? 'Streamable HTTP'
                            : (agentMcpCtlgData as any)?.transportType === 'sse'
                              ? 'SSE'
                              : (agentMcpCtlgData as any)?.transportType || ''}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          서버 URL
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {(agentMcpCtlgData as any)?.serverUrl || ''}
                        </UITypography>
                      </td>
                    </tr>
                    {(() => {
                      const authType = (agentMcpCtlgData as any)?.authType as string | undefined;
                      const authConfig = (agentMcpCtlgData as any)?.authConfig;

                      if (!authType || authType === 'none') {
                        return (
                          <tr>
                            <th>
                              <UITypography variant='body-2' className='secondary-neutral-900'>
                                인증 유형
                              </UITypography>
                            </th>
                            <td colSpan={3}>
                              <UITypography variant='body-2' className='secondary-neutral-600'>
                                None
                              </UITypography>
                            </td>
                          </tr>
                        );
                      }

                      if (authType === 'basic') {
                        return (
                          <>
                            <tr>
                              <th>
                                <UITypography variant='body-2' className='secondary-neutral-900'>
                                  인증 유형
                                </UITypography>
                              </th>
                              <td>
                                <UITypography variant='body-2' className='secondary-neutral-600'>
                                  Basic
                                </UITypography>
                              </td>
                              <th>
                                <UITypography variant='body-2' className='secondary-neutral-900'>
                                  User name
                                </UITypography>
                              </th>
                              <td>
                                <UITypography variant='body-2' className='secondary-neutral-600'>
                                  {authConfig?.username || ''}
                                </UITypography>
                              </td>
                            </tr>
                            <tr>
                              <th>
                                <UITypography variant='body-2' className='secondary-neutral-900'>
                                  Password
                                </UITypography>
                              </th>
                              <td colSpan={3}>
                                <UITypography variant='body-2' className='secondary-neutral-600'>
                                  ● ● ● ● ● ● ●
                                </UITypography>
                              </td>
                            </tr>
                          </>
                        );
                      }

                      if (authType === 'bearer') {
                        return (
                          <tr>
                            <th>
                              <UITypography variant='body-2' className='secondary-neutral-900'>
                                인증 유형
                              </UITypography>
                            </th>
                            <td>
                              <UITypography variant='body-2' className='secondary-neutral-600'>
                                Bearer
                              </UITypography>
                            </td>
                            <th>
                              <UITypography variant='body-2' className='secondary-neutral-900'>
                                Token
                              </UITypography>
                            </th>
                            <td>
                              <UITypography variant='body-2' className='secondary-neutral-600'>
                                ● ● ● ● ● ● ●
                              </UITypography>
                            </td>
                          </tr>
                        );
                      }

                      if (authType === 'custom-header') {
                        return (
                          <>
                            <tr>
                              <th>
                                <UITypography variant='body-2' className='secondary-neutral-900'>
                                  인증 유형
                                </UITypography>
                              </th>
                              <td>
                                <UITypography variant='body-2' className='secondary-neutral-600'>
                                  Custom Header
                                </UITypography>
                              </td>
                              <th>
                                <UITypography variant='body-2' className='secondary-neutral-900'>
                                  Key
                                </UITypography>
                              </th>
                              <td>
                                <UITypography variant='body-2' className='secondary-neutral-600'>
                                  {authConfig?.key || ''}
                                </UITypography>
                              </td>
                            </tr>
                            <tr>
                              <th>
                                <UITypography variant='body-2' className='secondary-neutral-900'>
                                  Value
                                </UITypography>
                              </th>
                              <td colSpan={3}>
                                <UITypography variant='body-2' className='secondary-neutral-600'>
                                  ● ● ● ● ● ● ●
                                </UITypography>
                              </td>
                            </tr>
                          </>
                        );
                      }

                      return null;
                    })()}
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>

          <UIArticle className='article-grid'>
            <div className='article-body'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='w-full'>
                    <UIUnitGroup gap={0} direction='row'>
                      <div className='flex justify-between w-full items-center'>
                        <div className='flex-shrink-0'>
                          <div style={{ width: '168px', paddingRight: '8px' }}>
                            <UIDataCnt count={toolListData?.length || 0} prefix='툴 목록 총' unit='건' />
                          </div>
                        </div>
                      </div>
                      <div>
                        <Button auth={AUTH_KEY.AGENT.MCP_SERVER_REFRESH} className='btn-option-outlined' onClick={() => handleRefreshTools()}>
                          새로고침
                        </Button>
                      </div>
                    </UIUnitGroup>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid type='default' rowData={paginatedToolListData} columnDefs={columnDefs} onClickRow={handleToolRowClick} />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination
                    currentPage={toolListPage}
                    totalPages={Math.ceil((Array.isArray(toolListData) ? toolListData.length : 0) / pageSize) || 1}
                    onPageChange={(page: number) => {
                      setToolListPage(page);
                    }}
                    className='flex justify-center'
                  />
                </UIListContentBox.Footer>
              </UIListContainer>
            </div>
          </UIArticle>

          <ManagerInfoBox
            type='uuid'
            people={[
              { userId: agentMcpCtlgData?.createdBy || '', datetime: agentMcpCtlgData?.createdAt || '' },
              { userId: agentMcpCtlgData?.updatedBy || '', datetime: agentMcpCtlgData?.updatedAt || '' },
            ]}
          />

          <ProjectInfoBox assets={[{ type: 'mcp', id: mcpId || '' }]} auth={AUTH_KEY.AGENT.MCP_SERVER_CHANGE_PUBLIC} />
        </UIPageBody>
        <UIPageFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='center'>
              <Button auth={AUTH_KEY.AGENT.MCP_SERVER_DELETE} className='btn-primary-gray' onClick={() => handleDeleteConfirm((agentMcpCtlgData as any)?.id)}>
                삭제
              </Button>
              <Button auth={AUTH_KEY.AGENT.MCP_SERVER_UPDATE} className='btn-primary-blue' disabled={false} onClick={() => handleUpdateMcpCtlg()}>
                수정
              </Button>
            </UIUnitGroup>
          </UIArticle>
        </UIPageFooter>
      </section>
      <AgentMcpCtlgEditPopupPage
        isOpen={layerPopupOne.currentStep > 0}
        onClose={layerPopupOne.onClose}
        mcpId={(agentMcpCtlgData as any)?.id || (mcpId as string) || ''}
        name={agentMcpCtlgData ? agentMcpCtlgData?.name || '' : undefined}
        description={agentMcpCtlgData ? agentMcpCtlgData?.description || '' : undefined}
        serverUrl={agentMcpCtlgData ? (agentMcpCtlgData as any)?.serverUrl || '' : undefined}
        transportType={agentMcpCtlgData ? ((agentMcpCtlgData as any)?.transportType as 'streamable-http' | 'sse' | undefined) : undefined}
        authType={agentMcpCtlgData ? ((agentMcpCtlgData as any)?.authType as 'none' | 'basic' | 'bearer' | 'custom-header' | undefined) : undefined}
        authConfig={agentMcpCtlgData ? (agentMcpCtlgData as any)?.authConfig : undefined}
        tags={
          agentMcpCtlgData && Array.isArray((agentMcpCtlgData as any)?.tags)
            ? (agentMcpCtlgData as any).tags.map((tag: any) => (typeof tag === 'string' ? tag : tag?.name || ''))
            : undefined
        }
        onUpdateSuccess={() => refetch()}
      />
    </>
  );
}
