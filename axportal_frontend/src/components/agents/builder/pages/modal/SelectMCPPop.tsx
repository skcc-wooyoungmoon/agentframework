import { UIDataCnt, UIPagination, UITextLabel } from '@/components/UI';
import { UILabel } from '@/components/UI/atoms/UILabel';
import { UIInput } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { useGetAgentMcpList } from '@/services/agent/mcp/agentMcp.services';
import { useModal } from '@/stores/common/modal/useModal';
import { dateUtils } from '@/utils/common';
import React, { memo, useEffect, useMemo, useState } from 'react';
import type { MCPSelection } from '../../types/mcpTypes';

interface SelectMCPPopProps {
  modalId: string;
  catalogNumber: number;
  selectedMCPs: MCPSelection[];
  onSelectMCP: (mcpSelection: MCPSelection) => void;
  onSelectMCPBatch?: (mcpSelections: MCPSelection[]) => void;
}

const MCPList: React.FC<SelectMCPPopProps> = ({ modalId, catalogNumber: _catalogNumber, onSelectMCP, onSelectMCPBatch, selectedMCPs }) => {
  const { closeModal } = useModal();
  const [tempSelectList, setTempSelectList] = useState<MCPSelection[]>(selectedMCPs || []);

  const [page, setPage] = useState(1);
  const [size] = useState(6);
  const [searchTerm, setSearchTerm] = useState('');

  const {
    data: mcpList,
    isSuccess,
    isLoading: isLoadingMCP,
    refetch,
  } = useGetAgentMcpList({
    page: page,
    size: size,
    sort: '',
    search: searchTerm,
  });

  const safeString = (value: any): string => {
    if (value === null || value === undefined) return '';
    if (typeof value === 'string') return value;
    if (typeof value === 'number' || typeof value === 'boolean') return String(value);
    if (typeof value === 'object') {
      if (value.name && typeof value.name === 'string') return value.name;
      if (value.name && typeof value.name === 'object') return safeString(value.name);
      return JSON.stringify(value);
    }
    return String(value);
  };

  const projectData = React.useMemo(() => {
    if (!isSuccess || !mcpList?.content) return [];

    return mcpList.content.map((item: any, index: number) => {
      const nameValue = safeString(item.name);
      const descriptionValue = safeString(item.description);
      const tagValue = Array.isArray(item.tags) ? item.tags : (item.tags ? safeString(item.tags) : 'no Tag');

      return {
        id: item.id,
        no: (page - 1) * size + index + 1,
        name: nameValue,
        status: item.enabled ? '이용가능' : '이용불가',
        description: descriptionValue,
        tag: tagValue,
        createdDate: item.createdAt ? dateUtils.formatDate(item.createdAt, 'datetime') : '',
        modifiedDate: item.updatedAt ? dateUtils.formatDate(item.updatedAt, 'datetime') : '',
        originalData: {
          ...item,
          name: nameValue,
        },
      };
    });
  }, [mcpList, isSuccess, page, size]);

  useEffect(() => {
    const timeoutId = setTimeout(() => {
      refetch();
    }, 300);

    return () => clearTimeout(timeoutId);
  }, [searchTerm, refetch]);

  useEffect(() => {
    const handleApply = () => {
      if (tempSelectList.length > 0) {
        if (onSelectMCPBatch) {
          onSelectMCPBatch(tempSelectList);
        } else {
          tempSelectList.forEach((mcpSelection/* , index */) => {

            onSelectMCP(mcpSelection);
          });
        }
        closeModal(modalId);
      }
    };

    (window as any).mcpApplyHandler = handleApply;

    return () => {
      delete (window as any).mcpApplyHandler;
    };
  }, [tempSelectList, onSelectMCP, onSelectMCPBatch, closeModal, modalId]);

  const handlePageChange = (newPage: number) => {
    setPage(newPage);
  };

  const updateSearchTerm = (term: string) => {
    setSearchTerm(term);
    setPage(1);
  };

  const gridData = useMemo(
    () =>
      projectData.map(item => ({
        id: item.id,
        no: item.no,
        name: item.name,
        status: item.status,
        description: item.description,
        tag: item.tag,
        createdDate: item.createdDate,
        modifiedDate: item.modifiedDate,
        originalData: item,
      })),
    [projectData]
  );

  const handleGridSelection = (datas: any[]) => {
    const selectedMcps = datas
      .map(d => {
        const originalData = d.originalData || d;
        const server = originalData.server || {};
        const nestedOriginalData = originalData.originalData || originalData;
        const nameValue = safeString(originalData.name);

        const serverName =
          safeString(server.name) ||
          server.serverName ||
          server.server_name ||
          nestedOriginalData.serverName ||
          nestedOriginalData.server_name ||
          safeString(nestedOriginalData.name) ||
          originalData.serverName ||
          originalData.server_name ||
          nameValue ||
          '';

        const serverUrl =
          server.url ||
          server.serverUrl ||
          server.server_url ||
          originalData.server?.url ||
          originalData.server?.serverUrl ||
          originalData.server?.server_url ||
          nestedOriginalData.serverUrl ||
          nestedOriginalData.server_url ||
          nestedOriginalData.url ||
          originalData.serverUrl ||
          originalData.server_url ||
          originalData.url ||
          '';

        const toolsList = originalData.tools || originalData.server?.tools || nestedOriginalData.tools || [];

        return {
          catalogId: originalData.id,
          catalogName: nameValue,
          serverName: serverName,
          serverUrl: serverUrl,
          toolIds: [originalData.id],
          tools: Array.isArray(toolsList) ? toolsList : [],
          mcp_id: originalData.id,
        };
      })
      .filter(Boolean)
      .filter((mcp, index, self) =>
        index === self.findIndex(m => m.catalogId === mcp.catalogId)
      );

    const currentPageMcpIds = new Set(gridData.map(g => g.originalData?.id || g.id));
    const otherPageSelections = tempSelectList.filter(mcp => !currentPageMcpIds.has(mcp.catalogId));
    const uniqueSelections = [...otherPageSelections, ...selectedMcps];

    setTempSelectList(uniqueSelections);
  };

  const columnDefs: any = React.useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no' as const,
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
        valueGetter: (params: any): string | number => {
          // 🔥 값이 객체인 경우 문자열로 변환
          const value = params.data?.no;
          return typeof value === 'object' && value !== null ? safeString(value) : value;
        },
      },
      {
        headerName: '서버명',
        field: 'name' as const,
        width: 272,
        cellStyle: { paddingLeft: '16px' },
        valueGetter: (params: any): string => {
          // 🔥 값이 객체인 경우 문자열로 변환
          const value = params.data?.name;
          return safeString(value);
        },
        cellRenderer: React.memo((params: any) => {
          const displayName = safeString(params.value) || '-';
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {displayName}
            </div>
          );
        }),
      },
      {
        headerName: '상태',
        field: 'status' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
        valueGetter: (params: any): string => {
          // 🔥 값이 객체인 경우 문자열로 변환
          const value = params.data?.status;
          return safeString(value);
        },
        cellRenderer: React.memo((params: any) => {
          const statusValue = safeString(params.value);
          const getStatusIntent = (status: string) => {
            switch (status) {
              case '이용불가':
                return 'error';
              case '이용가능':
                return 'complete';
              default:
                return 'complete';
            }
          };
          return (
            <UILabel variant='badge' intent={getStatusIntent(statusValue)}>
              {statusValue}
            </UILabel>
          );
        }),
      },
      {
        headerName: '설명',
        field: 'description',
        minWidth: 392,
        showTooltip: true,
        valueGetter: (params: any): string => {
          // 🔥 값이 객체인 경우 문자열로 변환
          const value = params.data?.description;
          return safeString(value);
        },
        cellRenderer: React.memo((params: any) => {
          const descriptionValue = safeString(params.value);
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {descriptionValue}
            </div>
          );
        }),
      },
      {
        headerName: '태그',
        field: 'tag' as const,
        width: 230,
        cellStyle: { paddingLeft: '16px' },
        valueGetter: (_params: any): string => {
          // 🔥 태그 필드는 valueGetter에서 처리하지 않고 cellRenderer에서 처리
          // valueGetter는 빈 문자열 반환 (실제 렌더링은 cellRenderer에서)
          return '';
        },
        cellRenderer: memo((params: any) => {
          // 🔥 params.data에서 직접 tag 값을 가져와서 처리
          const tagValue = params.data?.tag;

          if (!tagValue || tagValue === '' || (Array.isArray(tagValue) && tagValue.length === 0)) {
            return null;
          }

          if (Array.isArray(tagValue)) {
            const tagNames = tagValue.map((tag: any) => safeString(tag));
            const tagText = tagNames.join(', ');

            return (
              <div title={tagText}>
                <div className='flex gap-1'>
                  {tagNames.slice(0, 2).map((tagName: string, index: number) => (
                    <UITextLabel key={index} intent='tag' className='nowrap'>
                      {tagName}
                    </UITextLabel>
                  ))}
                </div>
              </div>
            );
          }

          const singleTag = safeString(tagValue);
          if (!singleTag || singleTag === 'no Tag') {
            return null;
          }

          return (
            <div title={singleTag}>
              <div className='flex gap-1'>
                <UITextLabel intent='tag' className='nowrap'>
                  {singleTag}
                </UITextLabel>
              </div>
            </div>
          );
        }),
      },
      {
        headerName: '생성일시',
        field: 'createdDate' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
        valueGetter: (params: any): string => {
          // 🔥 값이 객체인 경우 문자열로 변환
          const value = params.data?.createdDate;
          return typeof value === 'object' && value !== null ? safeString(value) : (value || '');
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
        valueGetter: (params: any): string => {
          // 🔥 값이 객체인 경우 문자열로 변환
          const value = params.data?.modifiedDate;
          return typeof value === 'object' && value !== null ? safeString(value) : (value || '');
        },
      },
    ],
    []
  );

  const selectedGridData = useMemo(
    () => {
      if (tempSelectList.length === 0) return [];
      const selectedCatalogIds = new Set(tempSelectList.map(selected => selected.catalogId));
      return gridData.filter(item => {
        const itemId = item.originalData?.id || item.id;
        return selectedCatalogIds.has(itemId);
      });
    },
    [tempSelectList, gridData]
  );

  return (
    <section className='section-modal'>
      <UIArticle className='article-grid'>
        <UIListContainer>
          <UIListContentBox.Header>
            <div className='flex justify-between items-center w-full'>
              <div className='flex-shrink-0'>
                <div style={{ width: '168px', paddingRight: '8px' }}>
                  <UIDataCnt count={mcpList?.totalElements || 0} prefix='총' unit='건' />
                </div>
              </div>
              <div>
                <div className='w-[360px]'>
                  <UIInput.Search
                    value={searchTerm}
                    placeholder='서버명 입력'
                    onChange={e => {
                      updateSearchTerm(e.target.value);
                    }}
                  />
                </div>
              </div>
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            <UIGrid
              type='multi-select'
              rowData={gridData}
              columnDefs={columnDefs}
              selectedDataList={selectedGridData}
              checkKeyName='id'
              onCheck={handleGridSelection}
              loading={isLoadingMCP}
            />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination currentPage={page} totalPages={mcpList?.totalPages || 1} onPageChange={handlePageChange} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </section>
  );
};

export const SelectMCPPop: React.FC<SelectMCPPopProps> = props => {
  return <MCPList {...props} />;
};
