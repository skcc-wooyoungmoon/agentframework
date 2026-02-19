import { agentAtom, isChangeFewShotAtom, selectedFewShotIdRepoAtom, selectedFewShotNameRepoAtom } from '@/components/agents/builder/atoms/AgentAtom';
import { useAuth } from '@/components/agents/builder/providers/Auth';

import { UIDataCnt, UIPagination, UITextLabel } from '@/components/UI';
import { UIInput } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { type FewShots } from '@/components/agents/builder/types/FewShots';
import { useGetFewShotList } from '@/services/prompt/fewshot/fewShotPrompts.services';
import type { GetFewShotListResponse } from '@/services/prompt/fewshot/types';
import { useAtom } from 'jotai/index';
import React, { type FC, memo, useEffect, useMemo, useState } from 'react';
import { api } from '@/configs/axios.config';

type Props = {
  readOnly?: boolean;
  onRowClick?: (id: string, name: string) => void;
  selectedRowId?: string;
  projectId?: string;
  nodeId: string;
  onSelectFewShot?: (fewShot: any) => void;
};

const InferenceFewShotsList: FC<Props> = ({ nodeId, readOnly = false, onSelectFewShot }) => {
  const [selectedFewShotInfo, setSelectedFewShotInfo] = useState<FewShots | null>(null);
  const [tempSelectedFewShot, setTempSelectedFewShot] = useState<FewShots | null>(null);
  const [, setChangeFewShot] = useAtom(isChangeFewShotAtom);

  useEffect(() => {
    return () => {
      if (selectedFewShotInfo) {
        setChangeFewShot(true);
      }
    };
  }, [selectedFewShotInfo, setChangeFewShot]);

  const [page, setPage] = useState(1);
  const [displaySize] = useState(6);
  const [searchTerm, setSearchTerm] = useState('');
  const [sortField] = useState('');

  const [allData, setAllData] = useState<GetFewShotListResponse[]>([]);
  const [isLoadingAll, setIsLoadingAll] = useState(false);

  const initialParams = {
    page: 1,
    size: 100,
    sort: sortField,
    projectId: '',
    filter: '',
    search: searchTerm || '',
  };

  const { data: initialData, isLoading: isLoadingFewShot } = useGetFewShotList(initialParams, {
    enabled: true,
  });

  useEffect(() => {
    setAllData([]);
    setIsLoadingAll(false);
  }, [searchTerm]);

  useEffect(() => {
    if (!initialData) return;
    if (isLoadingAll) return;

    const totalElements = initialData.totalElements || 0;
    const currentDataLength = initialData.content?.length || 0;
    const totalPages = initialData.totalPages || Math.ceil(totalElements / (currentDataLength || 100));

    const actualPageSize = currentDataLength > 0 ? currentDataLength : 100;
    const pageSize = actualPageSize;

    if (initialData.last || currentDataLength >= totalElements) {
      setAllData(initialData.content || []);
      return;
    }

    if (totalPages > 1 && totalElements > currentDataLength) {
      setIsLoadingAll(true);

      const fetchAllPages = async () => {
        try {
          const allPagesData: GetFewShotListResponse[] = [...(initialData.content || [])];

          for (let p = 2; p <= totalPages; p++) {
            const response = await api.get('/fewShot', {
              params: {
                page: p,
                size: pageSize,
                sort: sortField,
                projectId: '',
                filter: '',
                search: searchTerm || '',
              },
            });

            const content = response.data?.data?.content || [];
            if (content.length > 0) {
              allPagesData.push(...content);
            }
          }

          setAllData(allPagesData);
        } catch (error) {
          setAllData(initialData.content || []);
        } finally {
          setIsLoadingAll(false);
        }
      };

      fetchAllPages();
    } else {
      setAllData(initialData.content || []);
    }
  }, [initialData, sortField, searchTerm]);

  const safeFewShotsData = allData.length > 0 ? allData : (initialData?.content || []);

  const filteredFewShotsData = useMemo(
    () => safeFewShotsData.filter(item => item?.releaseVersion != null),
    [safeFewShotsData]
  );

  const filteredFewShotCount = filteredFewShotsData.length;

  const paginatedFewShotsData = useMemo(() => {
    const startIndex = (page - 1) * displaySize;
    const endIndex = startIndex + displaySize;
    return filteredFewShotsData.slice(startIndex, endIndex);
  }, [filteredFewShotsData, page, displaySize]);

  const shouldShowPagination = true;

  const handlePageChange = (newPage: number) => {
    setPage(newPage);
  };

  const updateSearchTerm = (term: string) => {
    setSearchTerm(term);
    setPage(1);
  };

  const [selectedFewShotIdRepo] = useAtom(selectedFewShotIdRepoAtom);
  const [_selectedFewShotNameRepo] = useAtom(selectedFewShotNameRepoAtom);

  useEffect(() => {
    const getSelectedFewShotInfo = async () => {
      if (selectedFewShotIdRepo[nodeId] && !selectedFewShotInfo) {
        const selectedFewShot = filteredFewShotsData.find(fewShot => fewShot.uuid === selectedFewShotIdRepo[nodeId]);
        if (selectedFewShot) {
          setSelectedFewShotInfo({
            ...selectedFewShot,
            created_at: (selectedFewShot as any).createdAt || new Date().toISOString(),
          } as FewShots);
        }
      }
    };

    getSelectedFewShotInfo();
  }, [selectedFewShotIdRepo[nodeId], selectedFewShotInfo, filteredFewShotsData]);

  const handleGridSelection = (datas: any[]) => {
    if (datas.length > 0 && datas[0]?.originalData) {
      const selectedData = datas[0].originalData;
      const fewShotData = {
        ...selectedData,
        messages: selectedData.messages || [],
        variables: selectedData.variables || [],
      };

      setTempSelectedFewShot(fewShotData);
      if (onSelectFewShot) {
        onSelectFewShot(fewShotData);
      }
    } else {
      setTempSelectedFewShot(null);
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
        minWidth: 442,
        flex: 1,
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
        headerName: '버전',
        field: 'version' as const,
        width: 238,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: React.memo((params: any) => {
          const releaseVerRaw = params.data?.originalData?.releaseVersion;
          const latestVerRaw = params.data?.originalData?.latestVersion;

          // Extract version number from object if needed
          const releaseVer = typeof releaseVerRaw === 'object' && releaseVerRaw !== null
            ? (releaseVerRaw.versionId || releaseVerRaw.version || releaseVerRaw.tag || '')
            : releaseVerRaw;
          
          const latestVer = typeof latestVerRaw === 'object' && latestVerRaw !== null
            ? (latestVerRaw.versionId || latestVerRaw.version || latestVerRaw.tag || '')
            : latestVerRaw;

          return (
            <div className='flex items-center gap-[8px]'>
              {releaseVer && <UITextLabel intent='blue'>Release Ver.{releaseVer}</UITextLabel>}
              {latestVer && <UITextLabel intent='gray'>Latest Ver.{latestVer}</UITextLabel>}
            </div>
          );
        }),
      },
      {
        headerName: '태그',
        field: 'tag' as const,
        width: 230,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: memo((params: any) => {
          if (!params.value || !Array.isArray(params.value) || params.value.length === 0) {
            return null;
          }
          const tagText = params.value.join(', ');
          return (
            <div title={tagText}>
              <div className='flex gap-1'>
                {params.value.slice(0, 2).map((tag: string, index: number) => (
                  <UITextLabel key={index} intent='tag' className='nowrap'>
                    {tag}
                  </UITextLabel>
                ))}
              </div>
            </div>
          );
        }),
      },
    ],
    []
  );

  const gridData = useMemo(
    () =>
      paginatedFewShotsData.map((item, index) => {
        let tagArray: string[] = [];
        if (Array.isArray((item as any).tags)) {
          tagArray = (item as any).tags.map((t: any) => (typeof t === 'string' ? t : t?.tag || ''));
        }

        return {
          id: item.uuid,
          no: (page - 1) * displaySize + index + 1,
          name: item.name,
          version: '',
          tag: tagArray.length > 0 ? tagArray : ['태그없음'],
          originalData: item,
        };
      }),
    [paginatedFewShotsData, page, displaySize]
  );

  const selectedGridData = useMemo(
    () => (tempSelectedFewShot ? gridData.filter(item => item.originalData?.uuid === tempSelectedFewShot.uuid) : []),
    [tempSelectedFewShot, gridData]
  );

  return (
    <section className='section-modal'>
      <UIArticle className='article-grid'>
        <UIListContainer>
          <UIListContentBox.Header>
            <div className='flex justify-between items-center w-full'>
              <div className='flex-shrink-0'>
                <div style={{ width: '168px', paddingRight: '8px' }}>
                  <UIDataCnt count={filteredFewShotCount} prefix='총' unit='건' />
                </div>
              </div>
              {!readOnly && (
                <div>
                  <div className='w-[360px]'>
                    <UIInput.Search
                      value={searchTerm}
                      placeholder='이름 입력'
                      onChange={e => {
                        updateSearchTerm(e.target.value);
                      }}
                    />
                  </div>
                </div>
              )}
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            <UIGrid type='single-select' rowData={gridData} columnDefs={columnDefs} selectedDataList={selectedGridData} onCheck={handleGridSelection} loading={isLoadingFewShot || isLoadingAll} />
          </UIListContentBox.Body>
          {shouldShowPagination && (
            <UIListContentBox.Footer>
              <UIPagination currentPage={page} totalPages={Math.ceil(filteredFewShotCount / displaySize)} onPageChange={handlePageChange} className='flex justify-center' />
            </UIListContentBox.Footer>
          )}
        </UIListContainer>
      </UIArticle>
    </section>
  );
};

type FewShotModalParamProps = {
  nodeId: string;
};

export const SelectFewShotPop: FC<FewShotModalParamProps & { readOnly?: boolean; onSelectFewShot?: (fewShot: any) => void }> = ({ nodeId, readOnly = false, onSelectFewShot }) => {
  const { currentUser } = useAuth();
  const [_agent] = useAtom(agentAtom);
  const [projectId, setProjectId] = useState<string>(currentUser?.project?.id || '');

  useEffect(() => {
    const newProjectId = currentUser?.project?.id || '';
    setProjectId(newProjectId);
  }, [currentUser?.project?.id]);

  const modalContent = <InferenceFewShotsList projectId={projectId} nodeId={nodeId} readOnly={readOnly} onSelectFewShot={onSelectFewShot} />;

  return modalContent;
};
