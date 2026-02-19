import { agentAtom, selectedPromptDataRepoAtom, selectedPromptIdRepoAtom, selectedPromptNameRepoAtom } from '@/components/agents/builder/atoms/AgentAtom';
import { UIDataCnt, UIPagination, UITextLabel } from '@/components/UI';
import { UIInput } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';

import { useAuth } from '@/components/agents/builder/providers/Auth';
import { type Data } from '@/components/agents/builder/types/InferencePrompts';
import { useGetInfPromptList } from '@/services/prompt/inference/inferencePrompts.services';
import { useAtom } from 'jotai/index';
import React, { type FC, memo, useEffect, useMemo, useState } from 'react';
import { api } from '@/configs/axios.config';

type Props = {
  readOnly?: boolean;
  onRowClick?: (id: string, name: string) => void;
  selectedRowId?: string;
  projectId?: string;
  nodeId: string;
  nodeType?: string;
};

const InferencePromptsList: FC<Props> = ({ nodeId, readOnly = false, projectId }) => {
  const [page, setPage] = useState(1);
  const [displaySize] = useState(6);
  const [searchTerm, setSearchTerm] = useState('');
  const [sortField] = useState('created_at,desc');
  const [allData, setAllData] = useState<any[]>([]);
  const [isLoadingAll, setIsLoadingAll] = useState(false);

  const initialParams = {
    project_id: projectId,
    page: 1,
    size: 100,
    sort: sortField,
    filter: undefined,
    search: searchTerm || undefined,
  };

  const { data: initialData, isLoading: isLoadingPrompt } = useGetInfPromptList(initialParams, {
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
          const allPagesData: any[] = [...(initialData.content || [])];

          for (let p = 2; p <= totalPages; p++) {
            const response = await api.get('/inference-prompts', {
              params: {
                project_id: projectId,
                page: p,
                size: pageSize,
                sort: sortField,
                filter: undefined,
                search: searchTerm || undefined,
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
  }, [initialData, projectId, sortField, searchTerm]);

  const [selectedPromptIdRepo, setSelectedPromptIdRepo] = useAtom(selectedPromptIdRepoAtom);
  const [, setSelectedPromptNameRepo] = useAtom(selectedPromptNameRepoAtom);
  const [, setSelectedPromptDataRepo] = useAtom(selectedPromptDataRepoAtom);
  const [tempSelectedPrompt, setTempSelectedPrompt] = useState<any>(null);
  const safeData = allData.length > 0 ? allData : (initialData?.content || []);

  const filteredPrompts = useMemo(
    () =>
      safeData.filter(item => {
        const releaseVer =
          item?.release_version ??
          item?.releaseVersion ??
          item?.data?.release_version ??
          item?.data?.releaseVersion ??
          item?.release_version;
        return releaseVer != null;
      }),
    [safeData]
  );

  const filteredTotalCount = filteredPrompts.length;

  const paginatedPrompts = useMemo(() => {
    const startIndex = (page - 1) * displaySize;
    const endIndex = startIndex + displaySize;
    return filteredPrompts.slice(startIndex, endIndex);
  }, [filteredPrompts, page, displaySize]);
  const shouldShowPagination = true;

  useEffect(() => {
    if (selectedPromptIdRepo[nodeId] && filteredPrompts.length > 0 && !tempSelectedPrompt) {
      const currentPrompt = filteredPrompts.find((prompt: any) => {
        const promptId = prompt.uuid || prompt.id;
        return promptId === selectedPromptIdRepo[nodeId];
      });
      if (currentPrompt) {
        setTempSelectedPrompt(currentPrompt);
      }
    }
  }, [selectedPromptIdRepo, nodeId, filteredPrompts, tempSelectedPrompt]);

  const handlePageChange = (newPage: number) => {
    setPage(newPage);
  };

  const updateSearchTerm = (term: string) => {
    setSearchTerm(term);
    setPage(1);
  };


  const handleSelect = async (data: Data) => {
    if (!data) {
      return;
    }
    setTempSelectedPrompt(data);
  };

  useEffect(() => {
    (window as any).promptApplyHandler = () => {
      if (tempSelectedPrompt) {
        const promptId = tempSelectedPrompt.id ||
          tempSelectedPrompt.uuid ||
          tempSelectedPrompt.data?.id ||
          tempSelectedPrompt.data?.uuid ||
          '';

        // 🔥 promptName이 객체인 경우 문자열로 변환 (tag, versionId만 있는 객체는 제외)
        let promptNameRaw = tempSelectedPrompt.name ||
          tempSelectedPrompt.data?.name ||
          '';
        
        let promptName = '';
        if (typeof promptNameRaw === 'string') {
          promptName = promptNameRaw;
        } else if (typeof promptNameRaw === 'object' && promptNameRaw !== null) {
          // 객체인 경우 name, label, title 등의 속성을 찾거나, tag/versionId만 있는 객체는 무시
          const keys = Object.keys(promptNameRaw);
          const hasOnlyVersionFields = keys.length === 2 && 'tag' in promptNameRaw && 'versionId' in promptNameRaw;
          
          if (!hasOnlyVersionFields) {
            promptName = (promptNameRaw as any).name || 
                        (promptNameRaw as any).label || 
                        (promptNameRaw as any).title || 
                        (promptNameRaw as any).displayName || 
                        '';
          }
        }

        if (!promptId || promptId.trim() === '') {
          return;
        }

        setSelectedPromptIdRepo(prev => ({
          ...prev,
          [nodeId]: promptId,
        }));
        setSelectedPromptNameRepo(prev => ({
          ...prev,
          [nodeId]: promptName,
        }));
        setSelectedPromptDataRepo(prev => ({
          ...prev,
          [nodeId]: {
            id: promptId,
            name: promptName,
            messages: [],
            variables: [],
          },
        }));
      } else {
      }
    };

    return () => {
      delete (window as any).promptApplyHandler;
    };
  }, [tempSelectedPrompt, nodeId, setSelectedPromptIdRepo, setSelectedPromptNameRepo, setSelectedPromptDataRepo]);

  // 🔥 객체를 안전하게 문자열로 변환하는 헬퍼 함수
  const safeStringValue = (value: any): string => {
    if (value === null || value === undefined) {
      return '';
    }
    if (typeof value === 'string') {
      return value;
    }
    if (typeof value === 'object') {
      // tag, versionId만 있는 객체는 무시
      const keys = Object.keys(value);
      const hasOnlyVersionFields = keys.length === 2 && 'tag' in value && 'versionId' in value;
      if (hasOnlyVersionFields) {
        return '';
      }
      // 다른 객체는 name, label, title 등을 찾거나 빈 문자열
      return value.name || value.label || value.title || value.displayName || '';
    }
    return String(value);
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
        valueGetter: (params: any) => {
          // 🔥 값이 객체인 경우 문자열로 변환
          const value = params.data?.no;
          return typeof value === 'object' && value !== null ? safeStringValue(value) : value;
        },
      },
      {
        headerName: '이름',
        field: 'name' as const,
        minWidth: 442,
        flex: 1,
        cellStyle: { paddingLeft: '16px' },
        valueGetter: (params: any) => {
          // 🔥 값이 객체인 경우 문자열로 변환
          const value = params.data?.name;
          return safeStringValue(value) || '-';
        },
        cellRenderer: React.memo((params: any) => {
          const displayName = params.value || '-';
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
        headerName: '버전',
        field: 'release_version' as const,
        width: 238,
        cellStyle: { paddingLeft: '16px' },
        valueGetter: (_params: any): string => {
          // 🔥 버전 필드는 valueGetter에서 처리하지 않고 cellRenderer에서 처리
          // valueGetter는 빈 문자열 반환 (실제 렌더링은 cellRenderer에서)
          return '';
        },
        cellRenderer: memo((params: any) => {
          const releaseVerRaw = params.data?.release_version || params.data?.data?.release_version || params.data?.releaseVersion;
          const latestVerRaw = params.data?.latest_version || params.data?.data?.latest_version || params.data?.latestVersion;

          // 🔥 Extract version number from object if needed, ensure it's always a string
          let releaseVer = '';
          if (releaseVerRaw) {
            if (typeof releaseVerRaw === 'string') {
              releaseVer = releaseVerRaw;
            } else if (typeof releaseVerRaw === 'object' && releaseVerRaw !== null) {
              releaseVer = releaseVerRaw.versionId || releaseVerRaw.version || releaseVerRaw.tag || '';
            } else {
              releaseVer = String(releaseVerRaw);
            }
          }
          
          let latestVer = '';
          if (latestVerRaw) {
            if (typeof latestVerRaw === 'string') {
              latestVer = latestVerRaw;
            } else if (typeof latestVerRaw === 'object' && latestVerRaw !== null) {
              latestVer = latestVerRaw.versionId || latestVerRaw.version || latestVerRaw.tag || '';
            } else {
              latestVer = String(latestVerRaw);
            }
          }

          return (
            <div className='flex items-center gap-[8px]'>
              {releaseVer ? <UITextLabel intent='blue'>Release Ver.{releaseVer}</UITextLabel> : null}
              {latestVer ? <UITextLabel intent='gray'>Latest Ver.{latestVer}</UITextLabel> : null}
              {!releaseVer && !latestVer && <UITextLabel intent='gray'>버전 정보 없음</UITextLabel>}
            </div>
          );
        }),
      },
      {
        headerName: '태그',
        field: 'tags' as const,
        width: 230,
        cellStyle: { paddingLeft: '16px' },
        valueGetter: (_params: any): string => {
          // 🔥 태그 필드는 valueGetter에서 처리하지 않고 cellRenderer에서 처리
          // valueGetter는 빈 문자열 반환 (실제 렌더링은 cellRenderer에서)
          return '';
        },
        cellRenderer: (params: any) => {
          const tags = params.data?.tags;
          if (!tags || !Array.isArray(tags) || tags.length === 0) {
            return null;
          }
          const tagStrings = tags.map((tag: any) => (typeof tag === 'string' ? tag : tag?.tag || tag?.name || '')).filter(Boolean);
          if (tagStrings.length === 0) {
            return null;
          }
          const tagText = tagStrings.join(', ');
          return (
            <div title={tagText}>
              <div className='flex gap-1'>
                {tagStrings.slice(0, 2).map((tag: string, index: number) => (
                  <UITextLabel key={index} intent='tag' className='nowrap'>
                    {tag}
                  </UITextLabel>
                ))}
              </div>
            </div>
          );
        },
      },
    ],
    [page, displaySize]
  );
  const gridData = useMemo(
    () =>
      paginatedPrompts.map((item, index) => {
        const no = (page - 1) * displaySize + index + 1;
        
        // 🔥 객체 필드들을 안전하게 문자열로 변환
        const safeName = (() => {
          const nameRaw = item?.name || item?.data?.name;
          if (typeof nameRaw === 'string') return nameRaw;
          if (typeof nameRaw === 'object' && nameRaw !== null) {
            const keys = Object.keys(nameRaw);
            const hasOnlyVersionFields = keys.length === 2 && 'tag' in nameRaw && 'versionId' in nameRaw;
            if (!hasOnlyVersionFields) {
              return nameRaw.name || nameRaw.label || nameRaw.title || nameRaw.displayName || '';
            }
          }
          return '';
        })();
        
        const safeReleaseVersion = (() => {
          const verRaw = item?.release_version || item?.data?.release_version || item?.releaseVersion;
          if (typeof verRaw === 'string') return verRaw;
          if (typeof verRaw === 'object' && verRaw !== null) {
            return verRaw.versionId || verRaw.version || verRaw.tag || '';
          }
          return '';
        })();
        
        const safeLatestVersion = (() => {
          const verRaw = item?.latest_version || item?.data?.latest_version || item?.latestVersion;
          if (typeof verRaw === 'string') return verRaw;
          if (typeof verRaw === 'object' && verRaw !== null) {
            return verRaw.versionId || verRaw.version || verRaw.tag || '';
          }
          return '';
        })();
        
        return {
          ...item,
          name: safeName,
          release_version: safeReleaseVersion,
          latest_version: safeLatestVersion,
          no,
          originalData: item,
        };
      }),
    [paginatedPrompts, page, displaySize]
  );
  const selectedGridData = useMemo(
    () =>
      tempSelectedPrompt
        ? gridData.filter((item: any) => {
          const itemId = item.originalData?.uuid || item.originalData?.id;
          const selectedId = tempSelectedPrompt.uuid || tempSelectedPrompt.id;
          return itemId === selectedId;
        })
        : [],
    [tempSelectedPrompt, gridData]
  );

  return (
    <section className='section-modal'>
      <UIArticle className='article-grid'>
        <UIListContainer>
          <UIListContentBox.Header>
            <div className='flex justify-between items-center w-full'>
              <div className='flex-shrink-0'>
                <div style={{ width: '168px', paddingRight: '8px' }}>
                  <UIDataCnt count={filteredTotalCount} prefix='총' unit='건' />
                </div>
              </div>
              {!readOnly && (
                <div>
                  <div className='w-[360px]'>
                    <UIInput.Search
                      value={searchTerm}
                      placeholder='이름 입력'
                      onChange={e => {
                        setSearchTerm(e.target.value);
                      }}
                      onKeyDown={e => {
                        if (e.key === 'Enter') {
                          updateSearchTerm(searchTerm);
                        }
                      }}
                    />
                  </div>
                </div>
              )}
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            <UIGrid
              type='single-select'
              rowData={gridData}
              columnDefs={columnDefs}
              selectedDataList={selectedGridData}
              loading={isLoadingPrompt || isLoadingAll}
              onCheck={(selectedItems: any[]) => {
                if (selectedItems && selectedItems.length > 0 && selectedItems[0]?.originalData) {
                  handleSelect(selectedItems[0].originalData);
                } else if (selectedItems && selectedItems.length === 0) {
                  setTempSelectedPrompt(null);
                }
              }}
              onClickRow={(params: any) => {
                if (params.data?.originalData) {
                  handleSelect(params.data.originalData);
                } else {
                  handleSelect(params.data);
                }
              }}
            />
          </UIListContentBox.Body>
          {shouldShowPagination && (
            <UIListContentBox.Footer>
              <UIPagination
                currentPage={page}
                totalPages={Math.ceil(filteredTotalCount / displaySize)}
                onPageChange={handlePageChange}
                className='flex justify-center'
              />
            </UIListContentBox.Footer>
          )}
        </UIListContainer>
      </UIArticle>
    </section>
  );
};

type PromptModalParamProps = {
  nodeId: string;
  nodeType?: string;
};

export const SelectPromptPop: FC<PromptModalParamProps & { readOnly?: boolean }> = ({ nodeId, nodeType }) => {
  const { currentUser } = useAuth();
  const [_agent] = useAtom(agentAtom);
  const [projectId, setProjectId] = useState<string>(currentUser?.project?.id || '');

  useEffect(() => {
    const newProjectId = currentUser?.project?.id || '';
    setProjectId(newProjectId);
  }, [currentUser?.project?.id]);

  return <InferencePromptsList projectId={projectId} nodeId={nodeId} nodeType={nodeType} />;
};
