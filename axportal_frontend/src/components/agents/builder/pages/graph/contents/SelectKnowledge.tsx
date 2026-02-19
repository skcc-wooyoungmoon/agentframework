import {
  isChangeKnowledgeAtom,
  selectedKnowledgeDetailAtom,
  selectedKnowledgeIdRepoAtom,
  selectedKnowledgeServingNameAtom,
  selectedKnowledgeNameRepoAtom,
  selectedKnowledgeRepoKindAtom,
  selectedKnowledgeRetrieverIdAtom,
} from '@/components/agents/builder/atoms/AgentAtom';
import { api } from '@/configs/axios.config';
import { CustomAccordionItem } from '@/components/agents/builder/common/Button/CustomAccordionItem';
import { useModal } from '@/stores/common/modal';
import { SelectKnowledgePop } from '@/components/agents/builder/pages/modal/SelectKnowledgePop.tsx';
import { useAtom } from 'jotai/index';
import React, { useEffect, useMemo, useRef } from 'react';
import { useNavigate } from 'react-router-dom';

interface KnowledgeProps {
  selectedRepoId: string;
  nodeId: string;
  asAccordionItem?: boolean;
  title?: React.ReactNode;
  readOnly?: boolean;
  nodeData?: any;
}

export const SelectKnowledge = ({ selectedRepoId, nodeId, asAccordionItem = false, title, readOnly = false, nodeData }: KnowledgeProps) => {
  const { openModal } = useModal();
  const navigate = useNavigate();
  const [selectedKnowledgeIdRepo, setSelectedKnowledgeIdRepo] = useAtom(selectedKnowledgeIdRepoAtom);
  const [selectedKnowledgeServingName, setSelectedKnowledgeServingName] = useAtom(selectedKnowledgeServingNameAtom);
  const [selectedKnowledgeNameRepo, setSelectedKnowledgeNameRepo] = useAtom(selectedKnowledgeNameRepoAtom);
  const [, setSelectedKnowledgeRepoKind] = useAtom(selectedKnowledgeRepoKindAtom);
  const [, setSelectedKnowledgeRetrieverId] = useAtom(selectedKnowledgeRetrieverIdAtom);
  const [selectedKnowledgeDetail, setSelectedKnowledgeDetail] = useAtom(selectedKnowledgeDetailAtom);
  const [, setChangeKnowledge] = useAtom(isChangeKnowledgeAtom);

  const DELETED_STATE = '__DELETED__';
  const actualSelectedRepoId = selectedKnowledgeIdRepo[nodeId] === DELETED_STATE ? '' : selectedKnowledgeIdRepo[nodeId] || selectedRepoId;

  const knowledgeDisplayNameRef = useRef<string>((nodeData as any)?.knowledge_name || (nodeData as any)?._knowledgeName || (nodeData as any)?.knowledge_retriever?.name || '');

  const fetchedRepoIdRef = useRef<string | null>(null);
  const isFetchingRef = useRef<boolean>(false);

  const selectedKnowledgeInfo = useMemo(() => {
    if (selectedKnowledgeIdRepo[nodeId] === DELETED_STATE || !actualSelectedRepoId) {
      return null;
    }

    const targetName =
      selectedKnowledgeNameRepo[nodeId] === DELETED_STATE
        ? ''
        : selectedKnowledgeNameRepo[nodeId] || knowledgeDisplayNameRef.current || (nodeData as any)?.knowledge_retriever?.name || '';

    return {
      id: actualSelectedRepoId,
      name: targetName || `Knowledge (ID: ${actualSelectedRepoId})`,
      is_active: true,
      embedding_model_serving_name: selectedKnowledgeServingName[nodeId] === DELETED_STATE ? '' : selectedKnowledgeServingName[nodeId] || '',
    };
  }, [actualSelectedRepoId, nodeId, selectedKnowledgeNameRepo, selectedKnowledgeServingName, selectedKnowledgeIdRepo]);

  useEffect(() => {
    const repoId = actualSelectedRepoId;
    if (selectedKnowledgeIdRepo[nodeId] === DELETED_STATE || !repoId) {
      fetchedRepoIdRef.current = null;
      isFetchingRef.current = false;
      return;
    }

    if (fetchedRepoIdRef.current && fetchedRepoIdRef.current !== repoId) {
      fetchedRepoIdRef.current = null;
      isFetchingRef.current = false;
    }
    if (isFetchingRef.current) {
      return;
    }

    if (fetchedRepoIdRef.current === repoId) {
      const currentDetail = selectedKnowledgeDetail[nodeId];
      const hasDetailForRepo = !!currentDetail && currentDetail.repo_id === repoId && currentDetail.__fetched;
      const existingName = selectedKnowledgeNameRepo[nodeId] || knowledgeDisplayNameRef.current;

      if (hasDetailForRepo && existingName && existingName.trim().length > 0) {
        return;
      } else {
        fetchedRepoIdRef.current = null;
      }
    }

    const ensureString = (value: any) => {
      if (value === null || value === undefined) {
        return '';
      }
      return typeof value === 'string' ? value : String(value);
    };
    const ensureObjectOrNull = (value: any) => {
      return value && typeof value === 'object' ? value : null;
    };

    const existingName =
      selectedKnowledgeNameRepo[nodeId] || knowledgeDisplayNameRef.current || (nodeData as any)?.knowledge_retriever?.name || (nodeData as any)?.knowledge_name || '';

    const currentDetail = selectedKnowledgeDetail[nodeId];
    const hasDetailForRepo = !!currentDetail && currentDetail.repo_id === repoId && currentDetail.__fetched;

    if (existingName && existingName.trim().length > 0 && hasDetailForRepo) {
      fetchedRepoIdRef.current = repoId;
      return;
    }

    if (!existingName || existingName.trim().length === 0 || !hasDetailForRepo) {
      isFetchingRef.current = true;
      fetchedRepoIdRef.current = repoId;

      const fetchKnowledgeDetail = async () => {
        try {
          const response = await api.get(`/dataCtlg/knowledge/repos/external/${repoId}`);
          const payload = response?.data?.data ?? response?.data ?? null;

          if (!payload || typeof payload !== 'object') {
            isFetchingRef.current = false;
            return;
          }

          const fetchedName = ensureString(payload.name ?? payload.repoName ?? payload.knwNm ?? payload.displayName ?? payload.title ?? '');
          const repoKindValue = ensureString(payload.repo_kind ?? payload.repoKind ?? payload.kind ?? '');
          const retrieverIdValue = ensureString(payload.retriever_id ?? payload.retrieverId ?? '');
          const indexNameValue = ensureString(payload.index_name ?? payload.indexName ?? payload.index ?? '');
          const scriptValue = ensureString(payload.script ?? payload.crawler_script ?? payload.crawlerScript ?? payload.etlScript ?? '');
          const projectIdValue = ensureString(payload.project_id ?? payload.projectId ?? '');
          const activeCollectionIdValue = ensureString(payload.active_collection_id ?? payload.activeCollectionId ?? '');
          const embeddingServingName = ensureString(payload.embedding_model_serving_name ?? payload.embeddingModelServingName ?? '');
          const embeddingInfoValue = ensureObjectOrNull(payload.embedding_info ?? payload.embeddingInfo);
          const knowledgeInfoValue = ensureObjectOrNull(payload.knowledge_info ?? payload.knowledgeInfo);
          const vectordbConnInfoValue = ensureObjectOrNull(payload.vectordb_conn_info ?? payload.vectordbConnInfo ?? payload.vectorDbConnInfo ?? payload.vector_db_conn_info);
          const retrievalOptionsValue = ensureObjectOrNull(payload.retrieval_options ?? payload.retrievalOptions);

          let knwIdValue = ensureString(payload.knw_id ?? payload.knwId ?? '');

          if (!knwIdValue && payload.index_name) {
            const uuidMatch = payload.index_name.match(/([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})/i);
            if (uuidMatch) {
              knwIdValue = uuidMatch[1];
            }
          }

          if (!knwIdValue && payload.rag_chunk_index_nm) {
            const uuidMatch = payload.rag_chunk_index_nm.match(/([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})/i);
            if (uuidMatch) {
              knwIdValue = uuidMatch[1];
            }
          }

          const expKnwIdValue = ensureString(payload.id ?? payload.expKnwId ?? repoId);

          if (fetchedRepoIdRef.current !== repoId) {
            isFetchingRef.current = false;
            return;
          }

          if (fetchedName.trim().length > 0) {
            const trimmedName = fetchedName.trim();
            knowledgeDisplayNameRef.current = trimmedName;
            setSelectedKnowledgeNameRepo(prev => {
              if (prev[nodeId] === trimmedName) {
                return prev;
              }
              return {
                ...prev,
                [nodeId]: trimmedName,
              };
            });
          }

          setSelectedKnowledgeIdRepo(prev => {
            if (prev[nodeId] === repoId) {
              return prev;
            }
            return {
              ...prev,
              [nodeId]: repoId,
            };
          });
          setSelectedKnowledgeRepoKind(prev => {
            const newValue = repoKindValue || 'repo_ext';
            if (prev[nodeId] === newValue) {
              return prev;
            }
            return {
              ...prev,
              [nodeId]: newValue,
            };
          });
          setSelectedKnowledgeRetrieverId(prev => {
            if (prev[nodeId] === retrieverIdValue) {
              return prev;
            }
            return {
              ...prev,
              [nodeId]: retrieverIdValue,
            };
          });
          if (embeddingServingName) {
            setSelectedKnowledgeServingName(prev => {
              if (prev[nodeId] === embeddingServingName) {
                return prev;
              }
              return {
                ...prev,
                [nodeId]: embeddingServingName,
              };
            });
          }

          setSelectedKnowledgeDetail(prev => {
            const existing = prev[nodeId];
            if (existing && existing.repo_id === repoId && existing.__fetched) {
              return prev;
            }
            return {
              ...prev,
              [nodeId]: {
                __fetched: true,
                repo_id: repoId,
                knw_id: knwIdValue || expKnwIdValue || repoId,
                exp_knw_id: expKnwIdValue || repoId,
                repo_kind: repoKindValue || 'repo_ext',
                project_id: projectIdValue,
                active_collection_id: activeCollectionIdValue,
                index_name: indexNameValue,
                script: scriptValue,
                embedding_info: embeddingInfoValue,
                knowledge_info: knowledgeInfoValue,
                vectordb_conn_info: vectordbConnInfoValue,
                retrieval_options: retrievalOptionsValue ?? undefined,
              },
            };
          });
          setChangeKnowledge(true);
          setChangeKnowledge(false);
          isFetchingRef.current = false;
        } catch (error) {
          isFetchingRef.current = false;
        }
      };

      fetchKnowledgeDetail().catch(() => {
        isFetchingRef.current = false;
      });
    }
  }, [
    actualSelectedRepoId,
    nodeId,
    nodeData,
    selectedKnowledgeIdRepo,
    setChangeKnowledge,
    setSelectedKnowledgeDetail,
    setSelectedKnowledgeIdRepo,
    setSelectedKnowledgeNameRepo,
    setSelectedKnowledgeRepoKind,
    setSelectedKnowledgeRetrieverId,
    setSelectedKnowledgeServingName,
  ]);

  const handleClickSearch = () => {
    if (readOnly) return;

    openModal({
      title: '지식 선택',
      type: 'large',
      body: <SelectKnowledgePop modalId={`select-knowledge-pop_${nodeId}`} nodeId={nodeId} />,
      showFooter: true,
      confirmText: '확인',
      confirmDisabled: false,
      onConfirm: () => {
        if ((window as any).knowledgeApplyHandler) {
          (window as any).knowledgeApplyHandler();
        }
      },
    });
  };

  const handleRemoveKnowledge = () => {
    const DELETED_STATE = '__DELETED__';

    setSelectedKnowledgeIdRepo(prev => ({
      ...prev,
      [nodeId]: DELETED_STATE,
    }));
    setSelectedKnowledgeNameRepo(prev => ({
      ...prev,
      [nodeId]: DELETED_STATE,
    }));
    setSelectedKnowledgeServingName(prev => ({
      ...prev,
      [nodeId]: DELETED_STATE,
    }));
    setSelectedKnowledgeRepoKind(prev => ({
      ...prev,
      [nodeId]: DELETED_STATE,
    }));
    setSelectedKnowledgeRetrieverId(prev => ({
      ...prev,
      [nodeId]: DELETED_STATE,
    }));
    setSelectedKnowledgeDetail(prev => {
      const next = { ...prev };
      delete next[nodeId];
      return next;
    });
    knowledgeDisplayNameRef.current = '';

    fetchedRepoIdRef.current = null;
    isFetchingRef.current = false;

    setChangeKnowledge(true);
    setChangeKnowledge(false);
  };

  const content = (
    <>
      <div className='w-full rounded-lg'>
        <div className='flex w-full items-center gap-2 rounded-lg bg-white p-2 rounded-lg border border-gray-300'>
          {selectedKnowledgeInfo ? (
            <div className='flex items-center gap-2 flex-1'>
              <div className='flex items-center gap-2'>
                <button
                  className='rounded-lg bg-gray-100 px-3 py-1 text-gray-700 max-w-[200px] truncate hover:bg-gray-200 transition-colors cursor-pointer'
                  title={selectedKnowledgeInfo.name}
                  onClick={e => {
                    e.stopPropagation();
                    if (actualSelectedRepoId) {
                      const detail = selectedKnowledgeDetail[nodeId] as any;
                      let knwId = detail?.knw_id;

                      if (knwId === actualSelectedRepoId && detail?.index_name) {
                        const uuidMatch = detail.index_name.match(/([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})/i);
                        if (uuidMatch) {
                          knwId = uuidMatch[1];
                        }
                      }

                      if (knwId === actualSelectedRepoId && detail?.rag_chunk_index_nm) {
                        const uuidMatch = detail.rag_chunk_index_nm.match(/([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})/i);
                        if (uuidMatch) {
                          knwId = uuidMatch[1];
                        }
                      }

                      const expKnwId = detail?.exp_knw_id || detail?.id || actualSelectedRepoId;

                      const knowledgeData = {
                        knwId: knwId,
                        expKnwId: expKnwId,
                        id: knwId || expKnwId,
                        name: selectedKnowledgeInfo.name || '',
                        description: '',
                        embedding: selectedKnowledgeInfo.embedding_model_serving_name || detail?.embedding_info?.serving_name || '',
                        vectorDB: detail?.vectordb_conn_info?.vectorDbName || detail?.vectordb_conn_info?.vectorDbType || '',
                        ragChunkIndexNm: detail?.index_name || '',
                        dataPipelineLoadStatus: 'completed',
                        isCustomKnowledge: false,
                      };
                      const navigationId = knwId || expKnwId || actualSelectedRepoId;

                      navigate(`/data/dataCtlg/knowledge/detail/${encodeURIComponent(navigationId)}`, {
                        state: { knowledgeData },
                      });
                    }
                  }}
                >
                  {selectedKnowledgeInfo.name}
                </button>
                {selectedKnowledgeInfo.embedding_model_serving_name && (
                  <span className='rounded-lg bg-blue-100 px-3 py-1 text-blue-700 text-sm'>{selectedKnowledgeInfo.embedding_model_serving_name}</span>
                )}
              </div>
              {selectedKnowledgeInfo.is_active ? (
                <i className='ki-filled ki-check-circle text-green-500' title='Knowledge is active'></i>
              ) : (
                <i className='ki-filled ki-cross-circle text-red-500' title='Knowledge is not active'></i>
              )}
              <button onClick={handleRemoveKnowledge} className='btn-icon btn btn-sm btn-light text-primary btn-node-action ml-auto' title='삭제'>
                <img alt='ico-system-24-outline-gray-trash' className='w-[20px] h-[20px]' src='/assets/images/system/ico-system-24-outline-gray-trash.svg' />
              </button>
            </div>
          ) : (
            <div className='flex w-full'>
              <span className='h-[36px] leading-[36px] text-sm text-gray-500'>Knowledge를 선택해주세요</span>
            </div>
          )}
        </div>
      </div>
      {!readOnly && (
        <div className='flex justify-end'>
          <button onClick={handleClickSearch} className='bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded-lg border-0 transition-colors duration-200 mt-3'>
            검색
          </button>
        </div>
      )}
    </>
  );

  if (asAccordionItem) {
    const accordionTitle = (
      <>
        {title}
        {selectedKnowledgeInfo && (
          <span className='ml-2 text-gray-500'>
            {selectedKnowledgeInfo.name}
            {selectedKnowledgeInfo.is_active ? (
              <i className='ki-filled ki-check-circle ml-2 text-green-500' title='Knowledge is active'></i>
            ) : (
              <i className='ki-filled ki-cross-circle ml-2 text-red-500' title='Knowledge is not active'></i>
            )}
          </span>
        )}
      </>
    );
    return (
      <CustomAccordionItem title={accordionTitle} defaultOpen={false}>
        {content}
      </CustomAccordionItem>
    );
  }

  return content;
};
