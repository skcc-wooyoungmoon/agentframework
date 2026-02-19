import React, { useEffect, useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UITextLabel } from '@/components/UI/atoms/UITextLabel';
import { UITypography } from '@/components/UI/atoms/UITypography';
import { UIInput } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import type { GetWorkFlowResponse } from '@/services/prompt/workFlow/types';
import { useGetWorkFlowList } from '@/services/prompt/workFlow/workFlow.services';
import { useUser } from '@/stores';
import { dateUtils } from '@/utils/common';

interface InfPromptWorkFlowPopupPageProps {
  onSelectionChange?: (selectedWorkFlows: Array<{ workflowId: string; xmlText?: string; workflowName?: string; versionNo?: number }>) => void;
}

export const InfPromptWorkFlowPopupPage = ({ onSelectionChange }: InfPromptWorkFlowPopupPageProps) => {
  const [searchValue, setSearchValue] = useState('');
  const [page, setPage] = useState(1);
  const [size] = useState(6);
  const [sort] = useState<string | undefined>('created_at,desc');
  const [selectedWorkFlowIds, setSelectedWorkFlowIds] = useState<string[]>([]);

  // API 요청 파라미터
  const { user } = useUser();
  const [requestParams, setRequestParams] = useState({
    project_id: user.adxpProject.prjUuid,
    page,
    size,
    sort,
    search: '',
    tag: '', // Tag filter
  });

  const { data, isLoading } = useGetWorkFlowList(requestParams);

  // 초기 마운트 시 상태 초기화
  useEffect(() => {
    setSelectedWorkFlowIds([]);
    setSearchValue('');
    setPage(1);
  }, []);

  // 검색 실행 함수
  const handleSearch = () => {
    setRequestParams(prev => ({
      ...prev,
      search: searchValue.trim(),
      page: 1, // 검색 시 첫 페이지로 이동
    }));
    setPage(1);
  };

  // 페이지/사이즈 변경 시 API 파라미터 업데이트
  useEffect(() => {
    setRequestParams(prev => ({
      ...prev,
      page,
      size,
    }));
  }, [page, size]);

  // 선택 변경 시 상위로 전달
  useEffect(() => {
    if (!onSelectionChange) return;
    if (!data?.content) {
      onSelectionChange([]);
      return;
    }

    const selectedWorkFlows = selectedWorkFlowIds.map(workflowId => {
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      const workflow = (data.content as any).find((item: any) => item.workflowId === workflowId);
      return {
        workflowId: workflow?.workflowId || workflowId,
        xmlText: workflow?.xmlText || '',
        workflowName: workflow?.workflowName || '',
        versionNo: workflow?.versionNo || 0,
      };
    });
    onSelectionChange(selectedWorkFlows);
  }, [selectedWorkFlowIds, data, onSelectionChange]);

  const rowData = React.useMemo(() => {
    if (!data?.content) return [];
    return data.content.map((item: GetWorkFlowResponse, index: number) => ({
      ...item,
      no: (requestParams.page - 1) * requestParams.size + index + 1,
    }));
  }, [data, requestParams]);

  // 그리드 컬럼 정의
  const columnDefs = React.useMemo(
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
        // valueGetter: (params: { node?: { rowIndex?: number } }) => {
        //   const rowIndex = params.node?.rowIndex ?? 0;
        //   const no = (page - 1) * size + rowIndex + 1;
        //   return String(no);
        // },
      },
      {
        headerName: '이름',
        field: 'workflowName' as const,
        flex: 1,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '버전',
        field: 'versionNo' as const,
        width: 120,
        cellStyle: { paddingLeft: '13px' },
        cellRenderer: React.memo((params: { value?: string | number }) => {
          const versionNo = params.value;
          if (versionNo === null || versionNo === undefined || versionNo === '') return null;
          return (
            <div className='flex items-center gap-1'>
              <UITextLabel intent='gray'>Lastest.{versionNo}</UITextLabel>
            </div>
          );
        }),
      },
      {
        headerName: '태그',
        field: 'tags' as const,
        width: 230,
        cellStyle: { paddingLeft: '13px' },
        cellRenderer: React.memo((params: { value?: unknown; data?: any }) => {
          const item = params.data as { tags?: string[]; tagsRaw?: string };
          let tags: string[] = [];
          if (Array.isArray(item?.tags) && item.tags.length > 0) {
            tags = item.tags.filter(t => t && t.trim()).map(t => t.trim());
          } else if (typeof item?.tagsRaw === 'string') {
            tags = item.tagsRaw
              .split(',')
              .map((t: string) => t.trim())
              .filter(Boolean);
          }
          if (!tags || tags.length === 0) {
            return null;
          }
          const title = tags.join(', ');
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
              title={title}
            >
              <div className='flex items-center gap-1'>
                {tags.slice(0, 2).map((tag: string, index: number) => (
                  <UITextLabel key={index} intent='tag'>
                    {tag}
                  </UITextLabel>
                ))}
                {tags.length > 2 && (
                  <UITypography variant='caption-2' className='secondary-neutral-550'>
                    {'...'}
                  </UITypography>
                )}
              </div>
            </div>
          );
        }),
      },
      {
        headerName: '생성일시',
        field: 'createdAt' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
        valueGetter: (params: any) => {
          return dateUtils.formatDate(params.data.createdAt, 'datetime');
        },
      },
    ],
    [page, size]
  );

  return (
    <section className='section-modal'>
      <UIArticle className='article-grid'>
        <UIListContainer>
          <UIListContentBox.Header>
            <div className='flex justify-between items-center w-full'>
              <div className='flex-shrink-0'>
                <div style={{ width: '168px', paddingRight: '8px' }}>
                  <UIDataCnt count={data?.totalElements || 0} prefix='총' />
                </div>
              </div>
              <div>
                <div className='w-[360px]'>
                  <UIInput.Search
                    value={searchValue}
                    placeholder='검색어 입력'
                    onChange={e => {
                      setSearchValue(e.target.value);
                    }}
                    onKeyDown={e => {
                      if (e.key === 'Enter') {
                        handleSearch();
                      }
                    }}
                  />
                </div>
              </div>
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            <UIGrid
              type='multi-select'
              rowData={rowData}
              checkKeyName='workflowId'
              selectedDataList={selectedWorkFlowIds.map(id => ({ workflowId: id }))}
              loading={isLoading}
              // eslint-disable-next-line @typescript-eslint/no-explicit-any
              columnDefs={columnDefs as any}
              onClickRow={() => {
                // 워크플로우 클릭 처리
              }}
              onCheck={(selectedRows: Array<{ workflowId?: string }>) => {
                const ids = (selectedRows || []).map(row => row?.workflowId).filter((id): id is string => typeof id === 'string' && id.length > 0);
                setSelectedWorkFlowIds(ids);
              }}
            />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination currentPage={page} totalPages={data?.totalPages || 0} onPageChange={(newPage: number) => setPage(newPage)} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </section>
  );
};
