import React, { type FC, useEffect, useState, useMemo } from 'react';
import { useAtom } from 'jotai/index';
import { selectedKnowledgeIdRepoAtom, selectedKnowledgeNameRepoAtom, selectedKnowledgeRepoKindAtom } from '@/components/agents/builder/atoms/AgentAtom';
import { useModal } from '@/stores/common/modal/useModal';
import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIInput } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { useApiQuery } from '@/hooks/common/api/useApi';

interface Knowledge {
  id: string;
  knw_id: string;
  name: string;
  description: string;
  embedding_model_serving_name: string;
  repo_kind?: string;
  retriever_id?: string;
}

interface SelectKnowledgePopProps {
  modalId: string;
  nodeId: string;
}

const KnowledgeList: FC<SelectKnowledgePopProps> = ({ modalId, nodeId }) => {
  const { closeModal } = useModal();
  const [, setSelectedKnowledgeIdRepo] = useAtom(selectedKnowledgeIdRepoAtom);
  const [, setSelectedKnowledgeNameRepo] = useAtom(selectedKnowledgeNameRepoAtom);
  const [, setSelectedKnowledgeRepoKind] = useAtom(selectedKnowledgeRepoKindAtom);
  const [page, setPage] = useState(1);
  const [size] = useState(6);
  const [searchTerm, setSearchTerm] = useState('');
  const [tempSelectedKnowledge, setTempSelectedKnowledge] = useState<Knowledge | null>(null);

  const { data, isLoading: isLoadingKnowledge } = useApiQuery<any>({
    queryKey: ['external-repos', JSON.stringify({ page, size, sort: 'updated_at,desc', search: searchTerm || undefined })],
    url: 'dataCtlg/knowledge/repos/external',
    params: { page, size, sort: 'updated_at,desc', search: searchTerm || undefined },
    staleTime: 30_000,
    retry: 1,
  });

  const [dataList, setDataList] = useState<Knowledge[]>([]);
  const [totalPagesState, setTotalPagesState] = useState(1);
  const [totalCount, setTotalCount] = useState(0);

  useEffect(() => {
    if (!data) return;
    const content: any[] = (data as any)?.data ?? [];
    const pagination = (data as any)?.payload?.pagination;
    const totalPages: number = pagination?.last_page ?? 1;
    const total: number = pagination?.total ?? 0;
    setTotalPagesState(Math.max(1, Number(totalPages) || 1));
    setTotalCount(Number(total) || 0);

    const mapped: Knowledge[] = content.map((item: any) => ({
      id: String(item.id ?? ''),
      knw_id: String(item.knw_id ?? item.id ?? ''), // knw_id가 없으면 id 사용
      name: String(item.name ?? 'Untitled'),
      description: String(item.description ?? ''),
      embedding_model_serving_name: String(item.embedding_model_name ?? ''),
      repo_kind: typeof item.repo_kind === 'string' ? item.repo_kind : typeof item.repoKind === 'string' ? item.repoKind : undefined,
      retriever_id: typeof item.retriever_id === 'string' ? item.retriever_id : typeof item.retrieverId === 'string' ? item.retrieverId : undefined,
    }));
    setDataList(mapped);

  }, [data]);

  useEffect(() => {
    const handleApply = () => {
      if (tempSelectedKnowledge) {
        const repoId = tempSelectedKnowledge.knw_id ?? tempSelectedKnowledge.id ?? '';
        const knowledgeName = tempSelectedKnowledge.name ?? '';
        const repoKind = tempSelectedKnowledge.repo_kind && tempSelectedKnowledge.repo_kind.trim() ? tempSelectedKnowledge.repo_kind : 'repo_ext';

        setSelectedKnowledgeIdRepo(prev => ({
          ...prev,
          [nodeId]: repoId,
        }));
        setSelectedKnowledgeNameRepo(prev => ({
          ...prev,
          [nodeId]: knowledgeName,
        }));
        setSelectedKnowledgeRepoKind(prev => ({
          ...prev,
          [nodeId]: repoKind,
        }));

        closeModal(modalId);
      }
    };

    (window as any).knowledgeApplyHandler = handleApply;

    return () => {
      delete (window as any).knowledgeApplyHandler;
    };
  }, [tempSelectedKnowledge, modalId, nodeId]);

  const handlePageChange = (newPage: number) => {
    setPage(newPage);
  };

  const updateSearchTerm = (term: string) => {
    setSearchTerm(term);
    setPage(1);
  };

  const handleGridSelection = (datas: any[]) => {
    if (datas.length > 0) {
      const selectedKnowledge = datas[0];
      setTempSelectedKnowledge(selectedKnowledge);
    } else {
      setTempSelectedKnowledge(null);
    }
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
      },
      {
        headerName: '이름',
        field: 'name' as const,
        width: 272,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value || '-'}
            </div>
          );
        }),
      },
      {
        headerName: '설명',
        field: 'description' as const,
        minWidth: 398,
        flex: 1,
        showTooltip: true,
        cellStyle: { paddingLeft: '16px' },
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
        headerName: '임베딩 모델',
        field: 'embedding_model_serving_name' as const,
        width: 230,
        cellStyle: { paddingLeft: '16px' },
      },
    ],
    []
  );
  const gridData = useMemo(
    () =>
      (Array.isArray(dataList) ? dataList : []).map((item, index) => ({
        id: item.id,
        no: (page - 1) * size + index + 1,
        name: item.name,
        description: item.description || '',
        embedding_model_serving_name: item.embedding_model_serving_name || '',
        originalData: item,
      })),
    [dataList, page, size]
  );
  const selectedGridData = useMemo(
    () => (tempSelectedKnowledge ? gridData.filter(item => item.originalData?.id === tempSelectedKnowledge.id) : []),
    [tempSelectedKnowledge, gridData]
  );

  return (
    <section className='section-modal'>
      <UIArticle className='article-grid'>
        <UIListContainer>
          <UIListContentBox.Header>
            <div className='flex justify-between items-center w-full'>
              <div className='flex-shrink-0'>
                <div style={{ width: '168px', paddingRight: '8px' }}>
                  <UIDataCnt count={totalCount} prefix='총' unit='건' />
                </div>
              </div>
              <div>
                <div className='w-[360px]'>
                  <UIInput.Search
                    value={searchTerm}
                    placeholder='이름, 설명 입력'
                    onChange={e => {
                      updateSearchTerm(e.target.value);
                    }}
                  />
                </div>
              </div>
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            <UIGrid type='single-select' rowData={gridData} columnDefs={columnDefs} selectedDataList={selectedGridData} onCheck={handleGridSelection} loading={isLoadingKnowledge} />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination currentPage={page} totalPages={totalPagesState} onPageChange={handlePageChange} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </section>
  );
};

export const SelectKnowledgePop: FC<SelectKnowledgePopProps> = props => {
  return <KnowledgeList {...props} />;
};
