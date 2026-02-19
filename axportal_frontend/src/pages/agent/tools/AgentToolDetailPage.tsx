import { useMemo, useState } from 'react';

import { useNavigate, useParams } from 'react-router-dom';

import { Button } from '@/components/common/auth';
import { ManagerInfoBox } from '@/components/common/manager';
import { ProjectInfoBox } from '@/components/common/project/ProjectInfoBox/component';
import { UICode, UIDataCnt, UIPagination } from '@/components/UI';
import { UILabel } from '@/components/UI/atoms/UILabel';
import { UITypography } from '@/components/UI/atoms/UITypography';
import { UIGrid } from '@/components/UI/molecules/grid/UIGrid/component';
import { UIListContainer } from '@/components/UI/molecules/list/UIListContainer';
import { UIListContentBox } from '@/components/UI/molecules/list/UIListContentBox';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageFooter } from '@/components/UI/molecules/UIPageFooter';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIUnitGroup } from '@/components/UI/molecules/UIUnitGroup';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { useLayerPopup } from '@/hooks/common/layer';
import { useDeleteAgentToolById, useGetAgentToolById } from '@/services/agent/tool/agentTool.services';
import { useModal } from '@/stores/common/modal';

import { AgentToolEditPopupPage } from './AgentToolEditPopupPage';

export function AgentToolDetailPage() {
  const { toolId } = useParams<{ toolId: string }>();
  const { openAlert, openConfirm } = useModal();

  const navigate = useNavigate();
  const layerPopupOne = useLayerPopup();

  const { data: agentToolData, refetch } = useGetAgentToolById({ toolId: toolId || '' }, { enabled: !!toolId });

  const pageSize = 6;
  const [headerCurrentPage, setHeaderCurrentPage] = useState(1);
  const [queryCurrentPage, setQueryCurrentPage] = useState(1);
  const [bodyCurrentPage, setBodyCurrentPage] = useState(1);

  const headerRowData = useMemo(() => {
    if (!agentToolData?.apiParam || agentToolData?.toolType !== 'custom_api') return [];
    const apiParam = agentToolData.apiParam as any;
    const headers = apiParam.headers || apiParam.header || {};
    return Object.entries(headers).map(([name, value], index) => ({
      id: (index + 1).toString(),
      name,
      value: String(value),
    }));
  }, [agentToolData?.apiParam, agentToolData?.toolType]);

  const queryRowData = useMemo(() => {
    if (!agentToolData?.apiParam || agentToolData?.toolType !== 'custom_api') return [];
    const apiParam = agentToolData.apiParam as any;
    const params = apiParam.params || {
      ...(apiParam.static_params || {}),
      ...(apiParam.dynamic_params || {}),
    };
    return Object.entries(params).map(([name, value], index) => {
      const isDynamic = apiParam.dynamic_params?.[name] !== undefined || String(value) === 'str';
      return {
        id: (index + 1).toString(),
        name,
        value: String(value) === 'str' ? 'Tool 사용 시점에 값이 결정됩니다.' : String(value),
        isDynamic,
      };
    });
  }, [agentToolData?.apiParam, agentToolData?.toolType]);

  const bodyRowData = useMemo(() => {
    if (!agentToolData?.apiParam || agentToolData?.toolType !== 'custom_api') return [];
    const apiParam = agentToolData.apiParam as any;
    const body = apiParam.body || {
      ...(apiParam.static_body || {}),
      ...(apiParam.dynamic_body || {}),
    };
    return Object.entries(body).map(([name, value], index) => {
      const isDynamic = apiParam.dynamic_body?.[name] !== undefined || String(value) === 'str';
      return {
        id: (index + 1).toString(),
        name,
        value: String(value) === 'str' ? 'Tool 사용 시점에 값이 결정됩니다.' : String(value),
        isDynamic,
      };
    });
  }, [agentToolData?.apiParam, agentToolData?.toolType]);

  const paginatedHeaderData = useMemo(() => {
    const startIndex = (headerCurrentPage - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    return headerRowData.slice(startIndex, endIndex);
  }, [headerRowData, headerCurrentPage, pageSize]);

  const paginatedQueryData = useMemo(() => {
    const startIndex = (queryCurrentPage - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    return queryRowData.slice(startIndex, endIndex);
  }, [queryRowData, queryCurrentPage, pageSize]);

  const paginatedBodyData = useMemo(() => {
    const startIndex = (bodyCurrentPage - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    return bodyRowData.slice(startIndex, endIndex);
  }, [bodyRowData, bodyCurrentPage, pageSize]);

  const headerColumnDefs = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id',
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
        valueGetter: (params: any) => (headerCurrentPage - 1) * pageSize + params.node.rowIndex + 1,
      },
      {
        headerName: '파라미터 이름',
        field: 'name',
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
        showTooltip: true,
      },
      {
        headerName: '파라미터 값',
        field: 'value',
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
        showTooltip: true,
      },
    ],
    [headerCurrentPage, pageSize]
  );

  const queryColumnDefs = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id',
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
        valueGetter: (params: any) => (queryCurrentPage - 1) * pageSize + params.node.rowIndex + 1,
      },
      {
        headerName: '파라미터 이름',
        field: 'name',
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
        showTooltip: true,
      },
      {
        headerName: '파라미터 값',
        field: 'value',
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
        showTooltip: true,
      },
      {
        headerName: '다이나믹 여부',
        field: 'isDynamic',
        width: 150,
        cellStyle: {
          paddingLeft: '16px',
        },
        cellRenderer: (params: any) => {
          return (
            <UILabel variant='badge' intent={params.value ? 'blue' : 'gray'}>
              {params.value ? 'Y' : 'N'}
            </UILabel>
          );
        },
      },
    ],
    [queryCurrentPage, pageSize]
  );

  const bodyColumnDefs = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id',
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
        valueGetter: (params: any) => (bodyCurrentPage - 1) * pageSize + params.node.rowIndex + 1,
      },
      {
        headerName: '파라미터 이름',
        field: 'name',
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
        showTooltip: true,
      },
      {
        headerName: '파라미터 값',
        field: 'value',
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
        showTooltip: true,
      },
      {
        headerName: '다이나믹 여부',
        field: 'isDynamic',
        width: 150,
        cellStyle: {
          paddingLeft: '16px',
        },
        cellRenderer: (params: any) => {
          return (
            <UILabel variant='badge' intent={params.value ? 'blue' : 'gray'}>
              {params.value ? 'Y' : 'N'}
            </UILabel>
          );
        },
      },
    ],
    [bodyCurrentPage, pageSize]
  );

  const headerTotalPages = Math.ceil(headerRowData.length / pageSize);
  const queryTotalPages = Math.ceil(queryRowData.length / pageSize);
  const bodyTotalPages = Math.ceil(bodyRowData.length / pageSize);

  const handleDeleteConfirm = async (toolId: string) => {
    openConfirm({
      title: '안내',
      message: '삭제하시겠어요? \n삭제한 정보는 복구할 수 없습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        deleteAgentTool({ toolId: toolId });
      },
      onCancel: () => { },
    });
  };

  const { mutate: deleteAgentTool } = useDeleteAgentToolById({
    onSuccess: () => {
      openAlert({
        title: '완료',
        message: '에이전트 도구가 삭제되었습니다.',
        onConfirm: () => {
          navigate('/agent/tools', { replace: true });
        },
      });
    },
    onError: () => { },
  });

  const handleAgentToolEditPopup = () => {
    layerPopupOne.onOpen();
  };

  return (
    <>
      <section className='section-page'>
        <UIPageHeader title='Tool 조회' description='' />
        <UIPageBody>
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                기본 정보
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
                          이름
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {agentToolData?.name || ''}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          표시 이름
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {agentToolData?.displayName || ''}
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
                          {agentToolData?.description || ''}
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>

          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                Tool 정보
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
                  {agentToolData?.toolType === 'custom_api' ? (
                    <tbody>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            Tool 유형
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {agentToolData?.toolType || ''}
                          </UITypography>
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            메소드
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {agentToolData?.method || ''}
                          </UITypography>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            API URL
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {agentToolData?.serverUrl || ''}
                          </UITypography>
                        </td>
                      </tr>
                    </tbody>
                  ) : (
                    <tbody>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            Tool 유형
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {agentToolData?.toolType || ''}
                          </UITypography>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            코드
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UICode value={agentToolData?.code || ''} language='python' theme='dark' width='100%' minHeight='472px' maxHeight='472px' readOnly={true} />
                        </td>
                      </tr>
                    </tbody>
                  )}
                </table>
              </div>
            </div>
          </UIArticle>

          {agentToolData?.toolType === 'custom_api' && (
            <>
              <UIArticle className='article-grid'>
                <UIListContainer>
                  <UIListContentBox.Header>
                    <div className='flex justify-between items-center w-full'>
                      <div className='flex-shrink-0'>
                        <div style={{ width: '168px', paddingRight: '8px' }}>
                          <UIDataCnt count={headerRowData.length} prefix='헤더 파라미터 총' unit='건' />
                        </div>
                      </div>
                    </div>
                  </UIListContentBox.Header>
                  <UIListContentBox.Body>
                    <UIGrid type='default' rowData={paginatedHeaderData} columnDefs={headerColumnDefs as any} />
                  </UIListContentBox.Body>
                  <UIListContentBox.Footer>
                    <UIPagination currentPage={headerCurrentPage} totalPages={headerTotalPages || 1} onPageChange={setHeaderCurrentPage} className='flex justify-center' />
                  </UIListContentBox.Footer>
                </UIListContainer>
              </UIArticle>

              <UIArticle className='article-grid'>
                <UIListContainer>
                  <UIListContentBox.Header>
                    <div className='flex justify-between items-center w-full'>
                      <div className='flex-shrink-0'>
                        <div style={{ width: '168px', paddingRight: '8px' }}>
                          <UIDataCnt count={queryRowData.length} prefix='Query 파라미터 총' unit='건' />
                        </div>
                      </div>
                    </div>
                  </UIListContentBox.Header>
                  <UIListContentBox.Body>
                    <UIGrid type='default' rowData={paginatedQueryData} columnDefs={queryColumnDefs as any} />
                  </UIListContentBox.Body>
                  <UIListContentBox.Footer>
                    <UIPagination currentPage={queryCurrentPage} totalPages={queryTotalPages || 1} onPageChange={setQueryCurrentPage} className='flex justify-center' />
                  </UIListContentBox.Footer>
                </UIListContainer>
              </UIArticle>

              <UIArticle className='article-grid'>
                <UIListContainer>
                  <UIListContentBox.Header>
                    <div className='flex justify-between items-center w-full'>
                      <div className='flex-shrink-0'>
                        <div style={{ width: '168px', paddingRight: '8px' }}>
                          <UIDataCnt count={bodyRowData.length} prefix='Body 파라미터 총' unit='건' />
                        </div>
                      </div>
                    </div>
                  </UIListContentBox.Header>
                  <UIListContentBox.Body>
                    <UIGrid type='default' rowData={paginatedBodyData} columnDefs={bodyColumnDefs as any} />
                  </UIListContentBox.Body>
                  <UIListContentBox.Footer>
                    <UIPagination currentPage={bodyCurrentPage} totalPages={bodyTotalPages || 1} onPageChange={setBodyCurrentPage} className='flex justify-center' />
                  </UIListContentBox.Footer>
                </UIListContainer>
              </UIArticle>
            </>
          )}

          <ManagerInfoBox
            type='uuid'
            people={[
              { userId: agentToolData?.createdBy || '', datetime: agentToolData?.createdAt || '' },
              { userId: agentToolData?.updatedBy || '', datetime: agentToolData?.updatedAt || '' },
            ]}
          />

          <ProjectInfoBox assets={[{ type: 'tool', id: toolId || '' }]} auth={AUTH_KEY.AGENT.TOOL_CHANGE_PUBLIC} />
        </UIPageBody>
        <UIPageFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='center'>
              <Button
                auth={AUTH_KEY.AGENT.TOOL_DELETE}
                className='btn-primary-gray'
                onClick={() => {
                  if (toolId) {
                    handleDeleteConfirm(toolId);
                  }
                }}
              >
                삭제
              </Button>
              <Button auth={AUTH_KEY.AGENT.TOOL_UPDATE} className='btn-primary-blue' onClick={handleAgentToolEditPopup}>
                수정
              </Button>
            </UIUnitGroup>
          </UIArticle>
        </UIPageFooter>
      </section>

      <AgentToolEditPopupPage
        currentStep={layerPopupOne.currentStep}
        onNextStep={layerPopupOne.onNextStep}
        onPreviousStep={layerPopupOne.onPreviousStep}
        onClose={layerPopupOne.onClose}
        toolId={toolId || ''}
        toolName={agentToolData?.name || ''}
        toolDisplayName={agentToolData?.displayName || ''}
        toolDescription={agentToolData?.description || ''}
        toolType={agentToolData?.toolType || ''}
        method={agentToolData?.method || ''}
        serverUrl={agentToolData?.serverUrl || ''}
        headerParams={(() => {
          if (agentToolData?.apiParam && agentToolData?.toolType === 'custom_api') {
            const parsedApiParam = agentToolData?.apiParam;
            if (parsedApiParam?.header && typeof parsedApiParam.header === 'object' && !Array.isArray(parsedApiParam.header)) {
              return Object.entries(parsedApiParam.header).map(([name, value]) => ({
                name,
                value: String(value),
              }));
            }
          }
          return [];
        })()}
        apiParams={JSON.stringify(agentToolData?.apiParam || { header: {}, static_params: {}, dynamic_params: {}, static_body: {}, dynamic_body: {} })}
        code={agentToolData?.code || ''}
        onUpdateSuccess={() => {
          refetch();
        }}
      />
    </>
  );
}
