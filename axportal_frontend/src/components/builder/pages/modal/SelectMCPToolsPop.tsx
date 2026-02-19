import { UIButton2, UIDataCnt, UIToggle, UITypography } from '@/components/UI';
import { UIUnitGroup } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIGrid } from '@/components/UI/organisms';
import { useGetAgentMcpByIdTools } from '@/services/agent/mcp/agentMcp.services';
import type { MCPCatalog } from '@/services/agent/mcp/types';
import { useModal } from '@/stores/common/modal';
import { memo, useCallback, useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';

interface SelectMCPToolsPopProps {
  mcp: MCPCatalog;
  onApply?: (filteredTools: any[]) => void;
}

export const SelectMCPToolsPop = ({ mcp, onApply }: SelectMCPToolsPopProps) => {
  const { closeAllModals } = useModal();
  const navigate = useNavigate();
  const mcpId = mcp.id;

  // Mcp ToolsData 조회
  const { data: toolsData, isSuccess: isToolsDataSuccess, isLoading } = useGetAgentMcpByIdTools({ mcpId: mcpId }, { enabled: !!mcpId });

  // toolsData에서 모든 도구를 가져와서 gridData 생성
  const gridData = useMemo(() => {
    if (!isToolsDataSuccess || !toolsData) return [];

    // API 응답이 여러 형태일 수 있으므로 처리
    const toolsArray = (toolsData as any).data || [];

    return toolsArray.map((tool: any, idx: number) => ({
      ...tool,
      id: tool.id || tool.name,
      no: idx + 1,
    }));
  }, [toolsData, isToolsDataSuccess]);

  // 항목 토글, 전체 토글 초기값 세팅
  const [toolStates, setToolStates] = useState<Record<number, boolean>>({});
  const [allEnabled, setAllEnabled] = useState(true);

  // gridData와 mcp.tools를 비교하여 toolStates 초기화
  useEffect(() => {
    if (!gridData || gridData.length === 0) {
      setToolStates({});
      setAllEnabled(true);
      return;
    }

    // mcp.tools에 있는 도구의 id/name을 Set으로 만들어서 빠른 조회
    const activeToolIds = new Set((mcp?.tools || []).map((tool: any) => String(tool?.id || tool?.name)));

    // gridData의 각 도구에 대해 mcp.tools에 있으면 true, 없으면 false
    const states: Record<number, boolean> = {};
    gridData.forEach((tool: any, idx: number) => {
      const toolId = String(tool.id || tool.name);
      states[idx] = activeToolIds.has(toolId);
    });

    setToolStates(states);
    // 모든 도구가 활성화되어 있는지 확인
    setAllEnabled(gridData.length > 0 && Object.values(states).every(state => state === true));
  }, [gridData, mcp?.tools]);

  // 모든 항목 활성화 상태 갱신
  useEffect(() => {
    if (!toolStates || Object.keys(toolStates).length === 0) {
      setAllEnabled(true);
      return;
    }
    setAllEnabled(Object.values(toolStates).every(state => state));
  }, [toolStates]);

  // 항목 활성화/비활성화
  const handleToolSelection = useCallback((index: number, checked: boolean) => {
    setToolStates(prev => ({ ...prev, [index]: checked }));
  }, []);

  // 모두 활성화/비활성화
  const handleToggleAll = () => {
    const next = !allEnabled;
    const newStates: Record<number, boolean> = {};
    gridData.forEach((_: any, idx: number) => {
      newStates[idx] = next;
    });
    setToolStates(newStates);
    setAllEnabled(next);
  };

  const handleMCPServerDetail = () => {
    // 모든 모달 닫기
    closeAllModals();
    navigate(`/agent/mcp/${mcpId}`);
  };

  // 활성화 컬럼의 cellRenderer (toolStates와 handleToolSelection을 참조)
  const toggleCellRenderer = useMemo(
    () => (params: any) => {
      // rowIndex를 안전하게 가져오기
      const idx = params?.node?.rowIndex ?? params?.rowIndex ?? (params?.data?.no ? params.data.no - 1 : 0);
      return (
        <div className='flex h-full items-center justify-center'>
          <UIToggle checked={toolStates[idx] ?? false} onChange={checked => handleToolSelection(idx, checked)} />
        </div>
      );
    },
    [toolStates, handleToolSelection]
  );

  useEffect(() => {
    // gridData에서 toolState가 true인 항목만 필터하여 새로운 배열을 만들어서 업데이트
    const filteredTools = gridData.filter((_: any, idx: number) => toolStates[idx] === true);

    onApply?.(filteredTools);
  }, [toolStates, gridData]);

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
        headerName: '서버명',
        field: 'name',
        width: 250,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: memo((params: any) => {
          const nameValue = params.value;
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {nameValue}
            </div>
          );
        }),
      },
      {
        headerName: '설명',
        field: 'description',
        minWidth: 609,
        flex: 1,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: (params: any) => {
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
        },
      },
      {
        headerName: '활성화',
        field: 'toggle',
        width: 85,
        cellRenderer: toggleCellRenderer,
      },
    ],
    [toggleCellRenderer] // toggleCellRenderer만 dependency에 포함
  );

  return (
    <section className='section-modal'>
      <UIArticle className='article-grid'>
        <UIListContainer>
          <UIListContentBox.Header>
            <div className='flex justify-between items-center w-full'>
              <div className='flex-shrink-0'>
                <div style={{ width: '168px', paddingRight: '8px' }}>
                  <UIDataCnt count={gridData?.length || 0} prefix='총' unit='건' />
                </div>
              </div>
              <div>
                {gridData?.length > 0 && (
                  <UIUnitGroup gap={8} direction='row' align='center' vAlign='center'>
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      모두 활성화
                    </UITypography>
                    <UIToggle checked={allEnabled} onChange={handleToggleAll} />
                  </UIUnitGroup>
                )}
              </div>
            </div>
          </UIListContentBox.Header>

          {/* 그리드 영역 */}
          <UIListContentBox.Body>
            <UIGrid type='default' rowData={gridData} loading={isLoading} columnDefs={columnDefs} />
          </UIListContentBox.Body>

          <UIListContentBox.Footer className='ui-data-has-btn'>
            <UIButton2 className='btn-option-outlined' style={{ width: '122px' }} onClick={handleMCPServerDetail}>
              MCP서버 상세가기
            </UIButton2>
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </section>
  );
};
