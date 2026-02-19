import {
  isChangeKnowledgeAtom,
  selectedKnowledgeDetailAtom,
  selectedKnowledgeIdRepoAtom,
  selectedKnowledgeNameRepoAtom,
  selectedKnowledgeRepoKindAtom,
  selectedKnowledgeRetrieverIdAtom,
} from '@/components/agents/builder/atoms/AgentAtom';
import { hasChatTestedAtom } from '@/components/agents/builder/atoms/logAtom.ts';
import { Accordion } from '@/components/agents/builder/common/accordion/Accordion.tsx';
import { Card, CardBody, CardFooter } from '@/components/agents/builder/common/index.ts';
import { LogModal } from '@/components/agents/builder/common/modal/log/LogModal.tsx';
import { useGraphActions } from '@/components/agents/builder/hooks/useGraphActions.ts';
import { useModal } from '@/stores/common/modal/useModal';
import { CustomScheme } from '@/components/agents/builder/pages/graph/contents/CustomScheme.tsx';
import { SelectKnowledge } from '@/components/agents/builder/pages/graph/contents/SelectKnowledge.tsx';
import { UISlider } from '@/components/UI/atoms';
import {
  type CustomNode,
  type CustomNodeInnerData,
  type InputKeyItem,
  NodeType,
  type OutputKeyItem,
  type RetrievalOptions,
  type RetrieverDataSchema,
  type KnowledgeRetriever,
} from '@/components/agents/builder/types/Agents';
import keyTableData from '@/components/agents/builder/types/keyTableData.json';
import { Handle, type NodeProps, Position, useUpdateNodeInternals } from '@xyflow/react';
import { useAutoUpdateNodeInternals } from '@/components/agents/builder/hooks/useAutoUpdateNodeInternals';
import { getNodeStatus } from '@/components/agents/builder/utils/GraphUtils.ts';
import { useNodeTracing } from '@/components/agents/builder/hooks/useNodeTracing';
import { useAtom } from 'jotai';
import { type FC, useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { NodeFooter, NodeHeader } from '.';
import { useNodeHandler } from '@/components/agents/builder/hooks/useNodeHandler';

type ExtendedRetrievalOptions = RetrievalOptions & { filter?: any | null };

const normalizeRetrievalOptions = (options?: Partial<ExtendedRetrievalOptions> | null): ExtendedRetrievalOptions => {
  const docFields = Array.isArray(options?.doc_format_metafields) ? [...options.doc_format_metafields] : [];

  return {
    doc_format_metafields: docFields,
    top_k: options?.top_k ?? null,
    threshold: options?.threshold ?? null,
    vector_field: options?.vector_field ?? null,
    file_ids: options?.file_ids ?? null,
    keywords: options?.keywords ?? null,
    order_by: options?.order_by ?? 'doc_rank',
    retrieval_mode: options?.retrieval_mode ?? 'dense',
    hybrid_dense_ratio: options?.hybrid_dense_ratio ?? 0.5,
    filter: options?.filter ?? null,
  };
};

const mergeRetrievalOptions = (prevOptions?: Partial<ExtendedRetrievalOptions> | null, detailOptions?: Partial<ExtendedRetrievalOptions> | null): ExtendedRetrievalOptions => {
  const prevNormalized = normalizeRetrievalOptions(prevOptions ?? null);

  if (!detailOptions) {
    return prevNormalized;
  }

  const detailNormalized = normalizeRetrievalOptions(detailOptions);
  const prevDocFields = Array.isArray(prevNormalized.doc_format_metafields) ? prevNormalized.doc_format_metafields : [];
  const detailDocFields = Array.isArray(detailNormalized.doc_format_metafields) ? detailNormalized.doc_format_metafields : [];

  return {
    doc_format_metafields: detailDocFields.length ? detailDocFields : prevDocFields,
    top_k: detailOptions.top_k ?? prevNormalized.top_k,
    threshold: detailOptions.threshold ?? prevNormalized.threshold,
    vector_field: detailOptions.vector_field ?? prevNormalized.vector_field,
    file_ids: detailOptions.file_ids ?? prevNormalized.file_ids,
    keywords: detailOptions.keywords ?? prevNormalized.keywords,
    order_by: detailOptions.order_by ?? prevNormalized.order_by,
    retrieval_mode: detailOptions.retrieval_mode ?? prevNormalized.retrieval_mode,
    hybrid_dense_ratio: detailOptions.hybrid_dense_ratio ?? prevNormalized.hybrid_dense_ratio,
    filter: detailOptions.filter ?? prevNormalized.filter ?? null,
  };
};

const retrievalOptionsEqual = (a?: Partial<ExtendedRetrievalOptions> | null, b?: Partial<ExtendedRetrievalOptions> | null) => {
  const normalizedA = normalizeRetrievalOptions(a ?? null);
  const normalizedB = normalizeRetrievalOptions(b ?? null);

  return JSON.stringify(normalizedA) === JSON.stringify(normalizedB);
};

export const RetrieverNode: FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  const { removeNode, toggleNodeView, syncNodeData } = useGraphActions();
  const updateNodeInternals = useUpdateNodeInternals();
  const newInnerData: CustomNodeInnerData = {
    isRun: false,
    isDone: false,
    isError: false,
    isToggle: false,
  };
  const { openModal } = useModal();
  const [hasChatTested] = useAtom(hasChatTestedAtom);
  const [selectedKnowledgeIdRepo, setSelectedKnowledgeIdRepo] = useAtom(selectedKnowledgeIdRepoAtom);
  const [selectedKnowledgeNameRepo, setSelectedKnowledgeNameRepo] = useAtom(selectedKnowledgeNameRepoAtom);
  const [selectedKnowledgeRepoKind, setSelectedKnowledgeRepoKind] = useAtom(selectedKnowledgeRepoKindAtom);
  const [selectedKnowledgeRetrieverId, setSelectedKnowledgeRetrieverId] = useAtom(selectedKnowledgeRetrieverIdAtom);
  const [selectedKnowledgeDetail] = useAtom(selectedKnowledgeDetailAtom);
  const [isChangeKnowledge, setIsChangeKnowledge] = useAtom(isChangeKnowledgeAtom);

  const nodesUpdatedRef = useRef(false);
  const knowledgeNameRef = useRef<string>(
    (data as any)._knowledgeName || (data as any).innerData?.knowledgeName || (data as any).knowledge_name || (data as any).knowledge_retriever?.name || ''
  );
  const innerData: CustomNodeInnerData = (data.innerData as CustomNodeInnerData) ?? newInnerData;

  useNodeTracing(id, data.name as string, data, innerData);

  const isRun = useMemo(() => innerData?.isRun ?? false, [innerData?.isRun]);
  const isDone = useMemo(() => innerData?.isDone ?? false, [innerData?.isDone]);
  const isError = useMemo(() => innerData?.isError ?? false, [innerData?.isError]);

  const [nodeStatus, setNodeStatus] = useState<string | null>(null);
  useEffect(() => {
    setNodeStatus(getNodeStatus(isRun, isDone, isError));
  }, [isRun, isDone, isError]);

  const initializedRef = useRef(false);
  const [inputKeys, setInputKeys] = useState<InputKeyItem[]>(() => (Array.isArray(data.input_keys) ? (data.input_keys as InputKeyItem[]).map(key => ({ ...key })) : []));
  const [inputValues, setInputValues] = useState<string[]>(() => inputKeys.map(item => item.name));

  const outputKeys = useMemo(() => (Array.isArray(data.output_keys) ? (data.output_keys as OutputKeyItem[]) : []), [data.output_keys]);

  const defaultRetrieverOptions = keyTableData['retriever__knowledge']['field_default']['retrieval_options'] as Record<string, any>;
  const schemaData = useMemo(() => {
    const retrieverData = data as Partial<RetrieverDataSchema>;
    const knowledgeRetriever = (retrieverData?.knowledge_retriever ?? {}) as Partial<KnowledgeRetriever>;
    const options = (knowledgeRetriever.retrieval_options ?? {}) as Partial<RetrievalOptions>;
    
    // 🔥 노드 데이터에서 지식 정보 확인 (새 그래프일 때 atom 값 무시)
    const nodeDataRepoId = knowledgeRetriever.repo_id ?? (data as any)?.knowledge_retriever?.repo_id ?? '';
    const nodeDataRepoKind = knowledgeRetriever.repo_kind ?? (data as any)?.knowledge_retriever?.repo_kind ?? '';
    const nodeDataRetrieverId = retrieverData?.retriever_id ?? (knowledgeRetriever as any)?.retriever_id ?? (data as any)?.retriever_id ?? '';
    
    // 🔥 노드 데이터에 지식 정보가 있을 때만 atom 값 사용, 없으면 atom 값 완전히 무시 (빈 문자열 반환)
    const hasNodeDataKnowledge = !!nodeDataRepoId;
    
    // 🔥 노드 데이터에 지식 정보가 없으면 atom 값 무시하고 빈 문자열 반환
    if (!hasNodeDataKnowledge) {
      return {
        retriever_id: '',
        knowledge_retriever: {
          repo_id: '',
          repo_kind: 'repo_ext',
          project_id: '',
          active_collection_id: '',
          index_name: '',
          script: '',
          serving_name: null,
          connect_type: 'api',
          connect_info: null,
          embedding_info: null,
          knowledge_info: null,
          vectordb_conn_info: null,
          retrieval_options: {
            doc_format_metafields: defaultRetrieverOptions?.doc_format_metafields ?? [],
            top_k: defaultRetrieverOptions?.top_k ?? 5,
            threshold: defaultRetrieverOptions?.threshold ?? 0.7,
            vector_field: null,
            file_ids: null,
            keywords: null,
            order_by: 'doc_rank',
            retrieval_mode: 'dense',
            hybrid_dense_ratio: 0.5,
            filter: null,
          },
        },
      };
    }
    
    // 🔥 노드 데이터에 지식 정보가 있으면 노드 데이터만 사용 (atom 값은 사용하지 않음)
    return {
      retriever_id: nodeDataRetrieverId || nodeDataRepoId || '',
      knowledge_retriever: {
        repo_id: nodeDataRepoId,
        repo_kind: nodeDataRepoKind || 'repo_ext',
        project_id: knowledgeRetriever.project_id ?? (data as any)?.knowledge_retriever?.project_id ?? '',
        active_collection_id: knowledgeRetriever.active_collection_id ?? (data as any)?.knowledge_retriever?.active_collection_id ?? '',
        index_name: knowledgeRetriever.index_name ?? (data as any)?.knowledge_retriever?.index_name ?? '',
        script: knowledgeRetriever.script ?? (data as any)?.knowledge_retriever?.script ?? '',
        serving_name: knowledgeRetriever.serving_name ?? null,
        connect_type: knowledgeRetriever.connect_type ?? 'api',
        connect_info: knowledgeRetriever.connect_info ?? null,
        embedding_info: knowledgeRetriever.embedding_info ?? (data as any)?.knowledge_retriever?.embedding_info ?? null,
        knowledge_info: knowledgeRetriever.knowledge_info ?? (data as any)?.knowledge_retriever?.knowledge_info ?? null,
        vectordb_conn_info: knowledgeRetriever.vectordb_conn_info ?? (data as any)?.knowledge_retriever?.vectordb_conn_info ?? null,
        retrieval_options: {
          doc_format_metafields: options.doc_format_metafields ?? (options as any)?.doc_format_metafieds ?? defaultRetrieverOptions?.doc_format_metafields ?? [],
          top_k: options.top_k ?? defaultRetrieverOptions?.top_k ?? 5,
          threshold: options.threshold ?? defaultRetrieverOptions?.threshold ?? 0.7,
          vector_field: options.vector_field ?? null,
          file_ids: options.file_ids ?? null,
          keywords: options.keywords ?? null,
          order_by: options.order_by ?? 'doc_rank',
          retrieval_mode: options.retrieval_mode ?? 'dense',
          hybrid_dense_ratio: options.hybrid_dense_ratio ?? 0.5,
          filter: options.filter ?? null,
        },
      },
    };
  }, [data, defaultRetrieverOptions]);

  const [formState, setFormState] = useState<RetrieverDataSchema>(() => {
    const knowledgeDefaults = schemaData.knowledge_retriever;
    return {
      type: NodeType.RetrieverRetriever.name,
      id,
      retriever_id: schemaData.retriever_id ?? '',
      name: data.name as string,
      description: (data.description as string) || keyTableData['retriever__knowledge']['field_default']['description'],
      kind: 'knowledge',
      knowledge_retriever: {
        repo_id: knowledgeDefaults.repo_id ?? '',
        repo_kind: knowledgeDefaults.repo_kind ?? 'repo_ext',
        project_id: knowledgeDefaults.project_id ?? '',
        active_collection_id: knowledgeDefaults.active_collection_id ?? '',
        index_name: knowledgeDefaults.index_name ?? '',
        script: knowledgeDefaults.script ?? '',
        serving_name: null,
        connect_type: knowledgeDefaults.connect_type ?? 'api',
        connect_info: knowledgeDefaults.connect_info ?? null,
        embedding_info: knowledgeDefaults.embedding_info ?? null,
        knowledge_info: knowledgeDefaults.knowledge_info ?? null,
        vectordb_conn_info: knowledgeDefaults.vectordb_conn_info ?? null,
        retrieval_options: {
          doc_format_metafields: knowledgeDefaults.retrieval_options?.doc_format_metafields ?? [],
          top_k:
            knowledgeDefaults.retrieval_options?.top_k ?? defaultRetrieverOptions?.top_k ?? keyTableData['retriever__knowledge']['field_default']['retrieval_options']['top_k'],
          threshold:
            knowledgeDefaults.retrieval_options?.threshold ??
            defaultRetrieverOptions?.threshold ??
            keyTableData['retriever__knowledge']['field_default']['retrieval_options']['threshold'],
          vector_field: knowledgeDefaults.retrieval_options?.vector_field ?? null,
          file_ids: knowledgeDefaults.retrieval_options?.file_ids ?? null,
          keywords: knowledgeDefaults.retrieval_options?.keywords ?? null,
          order_by: knowledgeDefaults.retrieval_options?.order_by ?? 'doc_rank',
          retrieval_mode: knowledgeDefaults.retrieval_options?.retrieval_mode ?? 'dense',
          hybrid_dense_ratio: knowledgeDefaults.retrieval_options?.hybrid_dense_ratio ?? 0.5,
          filter: knowledgeDefaults.retrieval_options?.filter ?? null,
        },
      },
      input_keys: inputKeys,
      output_keys: outputKeys,
    };
  });

  const applyKnowledgeSelection = useCallback(
    (params: { repoId?: string; repoKind?: string; retrieverId?: string; detail?: Partial<KnowledgeRetriever> }) => {
      if (!params.repoId && !params.detail) {
        return false;
      }

      let updated = false;
      setFormState(prev => {
        const prevRetriever = prev.knowledge_retriever;
        const detail = params.detail ?? {};
        const repoIdValue = params.repoId ?? prevRetriever.repo_id ?? '';
        const repoKindValue = params.repoKind ?? detail.repo_kind ?? prevRetriever.repo_kind ?? 'repo_ext';
        const retrieverIdValue = params.retrieverId ?? prev.retriever_id ?? '';
        const projectIdValue = detail.project_id ?? prevRetriever.project_id ?? '';
        const activeCollectionIdValue = detail.active_collection_id ?? prevRetriever.active_collection_id ?? '';
        const indexNameValue = detail.index_name ?? prevRetriever.index_name ?? '';
        const scriptValue = detail.script ?? prevRetriever.script ?? '';
        const embeddingInfoValue = detail.embedding_info ?? prevRetriever.embedding_info ?? null;
        const knowledgeInfoValue = detail.knowledge_info ?? prevRetriever.knowledge_info ?? null;
        const vectordbConnInfoValue = detail.vectordb_conn_info ?? prevRetriever.vectordb_conn_info ?? null;
        const detailRetrievalOptions = (detail as any)?.retrieval_options ?? null;
        const nextRetrievalOptions = mergeRetrievalOptions(prevRetriever.retrieval_options, detailRetrievalOptions);

        const hasChanged =
          prev.retriever_id !== retrieverIdValue ||
          prevRetriever.repo_id !== repoIdValue ||
          prevRetriever.repo_kind !== repoKindValue ||
          prevRetriever.project_id !== projectIdValue ||
          prevRetriever.active_collection_id !== activeCollectionIdValue ||
          prevRetriever.index_name !== indexNameValue ||
          prevRetriever.script !== scriptValue ||
          JSON.stringify(prevRetriever.embedding_info ?? null) !== JSON.stringify(embeddingInfoValue ?? null) ||
          JSON.stringify(prevRetriever.knowledge_info ?? null) !== JSON.stringify(knowledgeInfoValue ?? null) ||
          JSON.stringify(prevRetriever.vectordb_conn_info ?? null) !== JSON.stringify(vectordbConnInfoValue ?? null) ||
          !retrievalOptionsEqual(prevRetriever.retrieval_options, nextRetrievalOptions);

        if (!hasChanged) {
          return prev;
        }

        updated = true;

        return {
          ...prev,
          retriever_id: retrieverIdValue,
          knowledge_retriever: {
            ...prevRetriever,
            repo_id: repoIdValue,
            repo_kind: repoKindValue,
            project_id: projectIdValue,
            active_collection_id: activeCollectionIdValue,
            index_name: indexNameValue,
            script: scriptValue,
            embedding_info: embeddingInfoValue,
            knowledge_info: knowledgeInfoValue,
            vectordb_conn_info: vectordbConnInfoValue,
            retrieval_options: nextRetrievalOptions,
          },
        };
      });

      if (updated) {
        nodesUpdatedRef.current = true;
      }

      return updated;
    },
    [setFormState]
  );

  useEffect(() => {
    if (!initializedRef.current && Array.isArray(data.input_keys)) {
      setInputKeys((data.input_keys as InputKeyItem[]).map(key => ({ ...key })));
      initializedRef.current = true;
    }
  }, [data.input_keys]);

  useEffect(() => {
    const currentRepoValue = selectedKnowledgeIdRepo[id];
    const currentRepoKind = selectedKnowledgeRepoKind[id];
    const currentRetrieverId = selectedKnowledgeRetrieverId[id];

    if (currentRepoValue !== undefined && currentRepoValue !== formState.knowledge_retriever.repo_id) {
      const DELETED_STATE = '__DELETED__';
      const finalRepoValue = currentRepoValue === DELETED_STATE ? '' : currentRepoValue;
      const finalRepoKind = currentRepoKind === DELETED_STATE ? 'repo_ext' : currentRepoKind;
      const finalRetrieverId = currentRetrieverId === DELETED_STATE ? '' : currentRetrieverId;

      setFormState(prev => ({
        ...prev,
        retriever_id: finalRetrieverId || prev.retriever_id,
        knowledge_retriever: {
          ...prev.knowledge_retriever,
          repo_id: finalRepoValue,
          repo_kind: finalRepoKind || prev.knowledge_retriever.repo_kind,
        }
      }));
      nodesUpdatedRef.current = true;
    }
  }, [selectedKnowledgeIdRepo, selectedKnowledgeRepoKind, selectedKnowledgeRetrieverId, id, formState.knowledge_retriever.repo_id]);

  useEffect(() => {
    setInputValues(inputKeys.map(item => item.name));
  }, [inputKeys]);

  useEffect(() => {
    if (!isChangeKnowledge) {
      return;
    }

    const repoIdValue = selectedKnowledgeIdRepo[id];
    const detail = selectedKnowledgeDetail[id];
    const repoKindValue = selectedKnowledgeRepoKind[id];
    const retrieverIdValue = selectedKnowledgeRetrieverId[id];

    applyKnowledgeSelection({
      repoId: repoIdValue,
      repoKind: repoKindValue,
      retrieverId: retrieverIdValue,
      detail,
    });

    setSelectedKnowledgeNameRepo(prev => ({
      ...prev,
      [id]: selectedKnowledgeNameRepo[id] || prev[id] || '',
    }));
    knowledgeNameRef.current = selectedKnowledgeNameRepo[id] || knowledgeNameRef.current || (data as any)._knowledgeName || '';

    setIsChangeKnowledge(false);
  }, [
    applyKnowledgeSelection,
    data,
    id,
    isChangeKnowledge,
    selectedKnowledgeDetail,
    selectedKnowledgeIdRepo,
    selectedKnowledgeNameRepo,
    selectedKnowledgeRepoKind,
    selectedKnowledgeRetrieverId,
    setIsChangeKnowledge,
    setSelectedKnowledgeNameRepo,
  ]);

  const syncCurrentData = () => {
    try {
      const currentKnowledgeId = selectedKnowledgeIdRepo[id];
      const currentRepoKind = selectedKnowledgeRepoKind[id];
      const currentRetrieverId = selectedKnowledgeRetrieverId[id];

      const DELETED_STATE = '__DELETED__';
      const finalKnowledgeId = currentKnowledgeId === DELETED_STATE ? '' : currentKnowledgeId || formState.knowledge_retriever?.repo_id || (data as any).knowledge_id || '';
      const finalRepoKind = currentRepoKind === DELETED_STATE ? 'repo_ext' : currentRepoKind || (formState.knowledge_retriever?.repo_kind ?? 'repo_ext');
      const finalRetrieverId = currentRetrieverId === DELETED_STATE ? '' : currentRetrieverId || formState.retriever_id || finalKnowledgeId || '';

      const knowledgeName = selectedKnowledgeNameRepo[id] === DELETED_STATE ? '' : (selectedKnowledgeNameRepo[id] || knowledgeNameRef.current || (innerData as any)?.knowledgeName || (data as any)._knowledgeName || '');

      const newData = {
        type: formState.type,
        id: formState.id,
        retriever_id: finalRetrieverId,
        name: formState.name,
        description: formState.description,
        kind: formState.kind,
        knowledge_id: finalKnowledgeId,
        knowledge_name: knowledgeName,
        _knowledgeName: knowledgeName,
        knowledge_retriever: {
          repo_id: finalKnowledgeId,
          repo_kind: finalRepoKind,
          project_id: formState.knowledge_retriever?.project_id ?? '',
          active_collection_id: formState.knowledge_retriever?.active_collection_id ?? '',
          index_name: formState.knowledge_retriever?.index_name ?? '',
          script: formState.knowledge_retriever?.script ?? '',
          name: knowledgeName,
          serving_name: formState.knowledge_retriever?.serving_name ?? null,
          connect_type: formState.knowledge_retriever?.connect_type ?? 'direct',
          connect_info: formState.knowledge_retriever?.connect_info ?? null,
          embedding_info: formState.knowledge_retriever?.embedding_info ?? null,
          knowledge_info: formState.knowledge_retriever?.knowledge_info ?? null,
          vectordb_conn_info: formState.knowledge_retriever?.vectordb_conn_info ?? null,
          retrieval_options: {
            doc_format_metafields: formState.knowledge_retriever?.retrieval_options?.doc_format_metafields ?? [],
            top_k: formState.knowledge_retriever?.retrieval_options?.top_k ?? 5,
            threshold: formState.knowledge_retriever?.retrieval_options?.threshold ?? 0.0,
            vector_field: formState.knowledge_retriever?.retrieval_options?.vector_field ?? 'vector',
            file_ids: formState.knowledge_retriever?.retrieval_options?.file_ids ?? null,
            keywords: formState.knowledge_retriever?.retrieval_options?.keywords ?? null,
            order_by: formState.knowledge_retriever?.retrieval_options?.order_by ?? 'doc_rank',
            retrieval_mode: formState.knowledge_retriever?.retrieval_options?.retrieval_mode ?? 'dense',
            hybrid_dense_ratio: formState.knowledge_retriever?.retrieval_options?.hybrid_dense_ratio ?? 0.5,
            filter: formState.knowledge_retriever?.retrieval_options?.filter ?? null,
          },
        },
        input_keys: Array.isArray(inputKeys) ? [...inputKeys] : [],
        output_keys: Array.isArray(outputKeys) ? [...outputKeys] : [],
        innerData: {
          isToggle: innerData?.isToggle ?? false,
          knowledgeName,
        },
        position: data?.position,
        measured: data?.measured,
        selected: data?.selected,
        dragging: data?.dragging,
      };

      syncNodeData(id, newData);
    } catch {
      const fallbackData = {
        type: formState.type,
        id: formState.id,
        name: formState.name || 'Retriever',
        description: formState.description || '',
        kind: formState.kind || 'retriever',
        input_keys: [],
        output_keys: [],
        innerData: {
          isToggle: false,
          knowledgeName: '',
        },
      };
      syncNodeData(id, fallbackData);
    }
  };

  const initDataRef = useRef<boolean>(false);
  const prevInitDataRef = useRef<{
    repoId?: string;
    repoKind?: string;
    retrieverId?: string;
    knowledgeName?: string;
  }>({});

  useEffect(() => {
    const retrieverData = data as RetrieverDataSchema;
    const currentRepoId = retrieverData.knowledge_retriever?.repo_id ?? '';
    const currentRepoKind = retrieverData.knowledge_retriever?.repo_kind ?? '';
    const currentRetrieverId = retrieverData.retriever_id ?? '';
    const savedKnowledgeName =
      (data as any)._knowledgeName || (data as any).innerData?.knowledgeName || (data as any).knowledge_name || (data as any).knowledge_retriever?.name || '';

    const prevData = prevInitDataRef.current;
    const hasChanged =
      prevData.repoId !== currentRepoId ||
      prevData.repoKind !== currentRepoKind ||
      prevData.retrieverId !== currentRetrieverId ||
      prevData.knowledgeName !== savedKnowledgeName;

    if (initDataRef.current && !hasChanged) {
      const atomRepoId = selectedKnowledgeIdRepo[id];
      const atomRepoKind = selectedKnowledgeRepoKind[id];
      const atomRetrieverId = selectedKnowledgeRetrieverId[id];

      if (
        currentRepoId === atomRepoId &&
        String(currentRepoKind) === String(atomRepoKind) &&
        String(currentRetrieverId) === String(atomRetrieverId)
      ) {
        return;
      }
    }

    const initKnowledgeData = async () => {
      // 🔥 노드 데이터에 지식 정보가 없거나 repo_id가 없으면 atom에서 모두 제거 (새 그래프일 때 이전 값 제거)
      if (!retrieverData.knowledge_retriever || !currentRepoId) {
        setSelectedKnowledgeIdRepo(prev => {
          if (prev[id]) {
            const newRepo = { ...prev };
            delete newRepo[id];
            return newRepo;
          }
          return prev;
        });
        setSelectedKnowledgeRepoKind(prev => {
          if (prev[id]) {
            const newRepo = { ...prev };
            delete newRepo[id];
            return newRepo;
          }
          return prev;
        });
        setSelectedKnowledgeRetrieverId(prev => {
          if (prev[id]) {
            const newRepo = { ...prev };
            delete newRepo[id];
            return newRepo;
          }
          return prev;
        });
        setSelectedKnowledgeNameRepo(prev => {
          if (prev[id]) {
            const newRepo = { ...prev };
            delete newRepo[id];
            return newRepo;
          }
          return prev;
        });
        knowledgeNameRef.current = '';
        prevInitDataRef.current = {
          repoId: '',
          repoKind: '',
          retrieverId: '',
          knowledgeName: '',
        };
        initDataRef.current = true;
        return;
      }

      // 🔥 노드 데이터에 지식 정보가 있을 때만 atom 업데이트
      if (retrieverData.knowledge_retriever && currentRepoId) {
        setSelectedKnowledgeIdRepo(prev => {
          if (prev[id] === currentRepoId) {
            return prev;
          }
          // 현재 값이 노드 데이터와 다른 경우 덮어쓰지 않음 (사용자가 선택한 값 유지)
          if (prev[id] && prev[id] !== currentRepoId) {
            return prev;
          }
          return {
            ...prev,
            [id]: currentRepoId,
          };
        });

        const existingRepoKind = currentRepoKind || ((data as any)?.knowledge_retriever?.repo_kind ?? '');
        const repoKindStr = existingRepoKind ? String(existingRepoKind) : 'repo_ext';
        if (currentRepoId) {
          setSelectedKnowledgeRepoKind(prev => {
            if (prev[id] === repoKindStr) {
              return prev;
            }
            return {
              ...prev,
              [id]: repoKindStr,
            };
          });
        } else {
          setSelectedKnowledgeRepoKind(prev => {
            if (prev[id]) {
              const newRepo = { ...prev };
              delete newRepo[id];
              return newRepo;
            }
            return prev;
          });
        }

        if (currentRetrieverId && currentRepoId) {
          const retrieverIdStr = String(currentRetrieverId);
          setSelectedKnowledgeRetrieverId(prev => {
            if (prev[id] === retrieverIdStr) {
              return prev;
            }
            return {
              ...prev,
              [id]: retrieverIdStr,
            };
          });
        } else {
          setSelectedKnowledgeRetrieverId(prev => {
            if (prev[id]) {
              const newRepo = { ...prev };
              delete newRepo[id];
              return newRepo;
            }
            return prev;
          });
        }

        if (savedKnowledgeName && currentRepoId) {
          setSelectedKnowledgeNameRepo(prev => {
            if (prev[id] === savedKnowledgeName) {
              return prev;
            }
            return {
              ...prev,
              [id]: savedKnowledgeName,
            };
          });
          knowledgeNameRef.current = savedKnowledgeName;
        } else {
          setSelectedKnowledgeNameRepo(prev => {
            if (prev[id]) {
              const newRepo = { ...prev };
              delete newRepo[id];
              return newRepo;
            }
            return prev;
          });
          knowledgeNameRef.current = '';
        }
      }

      prevInitDataRef.current = {
        repoId: currentRepoId,
        repoKind: currentRepoKind,
        retrieverId: currentRetrieverId,
        knowledgeName: savedKnowledgeName,
      };

      initDataRef.current = true;
    };

    initKnowledgeData().then();
  }, [data, id, type, isChangeKnowledge]);

  const prevSyncDataRef = useRef<{
    formState?: string;
    inputKeys?: string;
    outputKeys?: string;
  }>({});

  useEffect(() => {
    const currentFormState = JSON.stringify(formState);
    const currentInputKeys = JSON.stringify(inputKeys);
    const currentOutputKeys = JSON.stringify(outputKeys);

    const prev = prevSyncDataRef.current;
    const hasChanged = prev.formState !== currentFormState || prev.inputKeys !== currentInputKeys || prev.outputKeys !== currentOutputKeys;

    if (!hasChanged) {
      return;
    }

    if (!formState || !formState.id || !formState.type) {
      return;
    }

    prevSyncDataRef.current = {
      formState: currentFormState,
      inputKeys: currentInputKeys,
      outputKeys: currentOutputKeys,
    };

    syncCurrentData();
  }, [formState, inputKeys, outputKeys]);

  useEffect(() => {
    updateNodeInternals(id);
  }, [id, updateNodeInternals, innerData.isToggle]);

  const handleFieldChange = (field: keyof RetrieverDataSchema, value: any) => {
    setFormState(prev => ({
      ...prev,
      [field]: value,
    }));
    nodesUpdatedRef.current = true;
  };

  const handleOptionFieldChange = (field: keyof RetrievalOptions, value: any) => {
    setFormState(prev => {
      const newState = {
        ...prev,
        knowledge_retriever: {
          ...prev.knowledge_retriever,
          retrieval_options: prev.knowledge_retriever.retrieval_options
            ? {
              ...prev.knowledge_retriever.retrieval_options,
              [field]: value,
            }
            : {
              doc_format_metafields: field === 'doc_format_metafields' ? value : null,
              top_k: field === 'top_k' ? value : null,
              threshold: field === 'threshold' ? value : null,
              vector_field: field === 'vector_field' ? value : null,
              file_ids: field === 'file_ids' ? value : null,
              keywords: field === 'keywords' ? value : null,
              order_by: field === 'order_by' ? value : 'doc_rank',
              retrieval_mode: field === 'retrieval_mode' ? value : 'dense',
              hybrid_dense_ratio: field === 'hybrid_dense_ratio' ? value : 0.5,
            },
        },
      };

      return newState;
    });
    nodesUpdatedRef.current = true;
  };

  const handleHeaderClickLog = () => {
    const nodeName = (data as any).name || data.innerData?.name || id;

    if (hasChatTested) {
      openModal({
        type: 'large',
        title: '로그',
        body: <LogModal id={'builder_log'} nodeId={String(nodeName)} />,
        showFooter: false,
      });
    }
  };

  const handleFooterFold = (isFold: boolean) => {
    toggleNodeView(id, isFold);
  };

  const handleDelete = () => {
    removeNode(id);
  };

  const handleNodeNameChange = useCallback((value: string) => {
    handleFieldChange('name', value);
  }, []);

  const handleDescriptionChange = useCallback((value: string) => {
    handleFieldChange('description', value);
  }, []);

  const handleTopKChange = useCallback((value: string) => {
    handleOptionFieldChange('top_k', value);
  }, []);

  const handleThresholdChange = useCallback((value: string) => {
    handleOptionFieldChange('threshold', value);
  }, []);

  const getInitialDenseRatio = (): number => {
    const ratio = formState.knowledge_retriever?.retrieval_options?.hybrid_dense_ratio;
    if (ratio === null || ratio === undefined) return 0.5;
    if (typeof ratio === 'string') {
      const parsed = parseFloat(ratio);
      return isNaN(parsed) ? 0.5 : Math.min(Math.max(parsed, 0.1), 0.9);
    }
    return Math.min(Math.max(ratio, 0.1), 0.9);
  };
  const initialDenseRatio = getInitialDenseRatio();
  const [denseRatio, setDenseRatio] = useState(initialDenseRatio);
  const [denseRatioText, setDenseRatioText] = useState(initialDenseRatio.toFixed(1));
  const initialSparseRatio = 1 - initialDenseRatio;
  const [sparseRatio, setSparseRatio] = useState(initialSparseRatio);
  const [sparseRatioText, setSparseRatioText] = useState(initialSparseRatio.toFixed(1));

  const normalizeValue = (value: number): number => {
    const rounded = Math.round(value * 10) / 10;
    return Math.min(Math.max(rounded, 0.1), 0.9);
  };
  const normalizeTextValue = (text: string): number => {
    const value = parseFloat(text) || 0;
    if (value <= 0) return 0.1;
    if (value >= 1) return 0.9;
    return normalizeValue(value);
  };
  const handleTextInput = (text: string, setText: (text: string) => void) => {
    if (text.includes('.')) {
      const parts = text.split('.');
      if (parts[1] && parts[1].length > 1) {
        return;
      }
    }
    setText(text);
  };

  const getInitialSearchMode = useMemo(() => {
    const mode = formState.knowledge_retriever?.retrieval_options?.retrieval_mode;
    if (mode === 'dense') return 'Dense';
    if (mode === 'sparse') return 'Sparse';
    if (mode === 'hybrid') return 'Hybrid';
    return 'Dense';
  }, [formState.knowledge_retriever?.retrieval_options?.retrieval_mode]);
  const [searchMode, setSearchMode] = useState<'Dense' | 'Sparse' | 'Hybrid'>(getInitialSearchMode);

  useEffect(() => {
    const currentMode = formState.knowledge_retriever?.retrieval_options?.retrieval_mode;
    if (currentMode === 'dense' && searchMode !== 'Dense') {
      setSearchMode('Dense');
    } else if (currentMode === 'sparse' && searchMode !== 'Sparse') {
      setSearchMode('Sparse');
    } else if (currentMode === 'hybrid' && searchMode !== 'Hybrid') {
      setSearchMode('Hybrid');
    }
  }, [formState.knowledge_retriever?.retrieval_options?.retrieval_mode]);

  useEffect(() => {
    const currentRatio = formState.knowledge_retriever?.retrieval_options?.hybrid_dense_ratio;
    if (currentRatio !== undefined && currentRatio !== null) {
      const numericRatio = typeof currentRatio === 'string' ? (isNaN(parseFloat(currentRatio)) ? 0.5 : parseFloat(currentRatio)) : currentRatio;
      const normalizedRatio = normalizeValue(numericRatio);
      if (Math.abs(denseRatio - normalizedRatio) > 0.01) {
        setDenseRatio(normalizedRatio);
        setDenseRatioText(normalizedRatio.toFixed(1));
        const newSparseValue = normalizeValue(1 - normalizedRatio);
        setSparseRatio(newSparseValue);
        setSparseRatioText(newSparseValue.toFixed(1));
      }
    }
  }, [formState.knowledge_retriever?.retrieval_options?.hybrid_dense_ratio, denseRatio]);

  const containerRef = useAutoUpdateNodeInternals(id);
  const { autoResize, stopPropagation, preventAndStop } = useNodeHandler();

  return (
    <div ref={containerRef}>
      <Card className={['agent-card w-full min-w-[500px] max-w-[500px]', nodeStatus].filter(e => !!e).join(' ')}>
        <Handle
          type='target'
          id={`retriever_left_${id}`}
          position={Position.Left}
          isConnectable={true}
          style={{
            width: 20,
            height: 20,
            background: '#000000',
            top: '50%',
            transform: 'translateY(-50%)',
            left: -10,
            border: '2px solid white',
            zIndex: 20,
          }}
        />
        <NodeHeader
          nodeId={id}
          type={type}
          data={innerData}
          defaultValue={formState.name ?? ''}
          onChange={handleNodeNameChange}
          onClickLog={handleHeaderClickLog}
          onClickDelete={handleDelete}
        />
        <>
          {innerData.isToggle && (
            <div className='bg-white px-4 py-4 border-b border-gray-200'>
              <label className='block font-semibold text-sm text-gray-700 mb-2'>{'설명'}</label>
              <div className='relative'>
                <textarea
                  className='nodrag w-full resize-none border border-gray-300 rounded-lg p-3 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500'
                  style={{
                    minHeight: '80px',
                    maxHeight: '100px',
                    height: 'auto',
                    overflow: 'hidden',
                  }}
                  placeholder={'설명 입력'}
                  value={formState.description ?? ''}
                  onChange={e => {
                    const value = e.target.value;
                    if (value.length <= 100) {
                      handleDescriptionChange(value);
                    }
                    autoResize(e.target);
                  }}
                  onInput={(e: any) => {
                    autoResize(e.target);
                  }}
                  maxLength={100}
                  onMouseDown={stopPropagation}
                  onMouseUp={stopPropagation}
                  onSelect={stopPropagation}
                  onDragStart={preventAndStop}
                  onDrag={preventAndStop}
                />
                <div className='absolute bottom-2 right-2 text-xs text-gray-500'>
                  <span className='text-blue-500'>{(formState.description ?? '').length}</span>/100
                </div>
              </div>
            </div>
          )}

          {!innerData.isToggle && (
            <>
              <CardBody className='p-4'>
                <div className='mb-4'>
                  <label className='block font-semibold text-sm text-gray-700 mb-2'>{'설명'}</label>
                  <div className='relative'>
                    <textarea
                      className='nodrag w-full resize-none border border-gray-300 rounded-lg p-3 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500'
                      rows={3}
                      placeholder={'설명 입력'}
                      value={formState.description ?? ''}
                      onChange={e => {
                        const value = e.target.value;
                        if (value.length <= 100) {
                          handleDescriptionChange(value);
                        }
                      }}
                      maxLength={100}
                      onKeyDown={e => {
                        if (
                          (formState.description ?? '').length >= 100 &&
                          e.key !== 'Backspace' &&
                          e.key !== 'Delete' &&
                          e.key !== 'ArrowLeft' &&
                          e.key !== 'ArrowRight' &&
                          e.key !== 'Home' &&
                          e.key !== 'End'
                        ) {
                          e.preventDefault();
                        }
                      }}
                      onInput={e => {
                        if ((e.target as HTMLTextAreaElement).value.length > 100) {
                          (e.target as HTMLTextAreaElement).value = (formState.description ?? '').slice(0, 100);
                        }
                      }}
                    />
                    <div className='absolute bottom-2 right-2 text-xs text-gray-500'>
                      <span className='text-blue-500'>{(formState.description ?? '').length}</span>/100
                    </div>
                  </div>
                </div>

                <hr className='border-gray-200' />

                <div className='mb-2 w-auto'>
                  <Accordion>
                    <SelectKnowledge
                      selectedRepoId={(data as any)?.knowledge_retriever?.repo_id ?? ''}
                      nodeId={id}
                      asAccordionItem={true}
                      title={
                        <>
                          {'지식'}
                          <span className='ag-color-red'>*</span>
                        </>
                      }
                      nodeData={data}
                    />
                  </Accordion>
                </div>

                <div className='w-auto'>
                  <div className='fw-bold form-label mb-2'>K</div>
                  <input
                    type='number'
                    min='1'
                    className='nodrag w-full h-9 rounded-lg border border-gray-300 bg-white p-2 outline-none'
                    value={formState.knowledge_retriever.retrieval_options?.top_k ?? 0}
                    onChange={e => handleTopKChange(e.target.value)}
                    placeholder='1 이상의 정수 값'
                    onKeyDown={e => {
                      const allowedKeys = ['Backspace', 'Delete', 'ArrowLeft', 'ArrowRight', 'ArrowUp', 'ArrowDown', 'Home', 'End', 'Tab'];
                      const isNumber = e.key >= '0' && e.key <= '9';
                      const isAllowedKey = allowedKeys.includes(e.key);

                      if (!isNumber && !isAllowedKey) {
                        e.preventDefault();
                      }
                    }}
                  />
                  <span className='mb-2 text-sm text-gray-500'>{` knowledge 검색 결과에서 반환할 최상위 문서의 개수`}</span>
                </div>

                <div className='mb-2 w-auto'>
                  <div className='fw-bold form-label mb-2'>Threshold</div>
                  <input
                    type='number'
                    min='0'
                    max='1'
                    step='0.01'
                    className='nodrag w-full h-9 rounded-lg border border-gray-300 bg-white p-2 outline-none'
                    value={formState.knowledge_retriever.retrieval_options?.threshold ?? 0}
                    onChange={e => handleThresholdChange(e.target.value)}
                    placeholder='0~1 사이의 값'
                    onKeyDown={e => {
                      const allowedKeys = ['Backspace', 'Delete', 'ArrowLeft', 'ArrowRight', 'ArrowUp', 'ArrowDown', 'Home', 'End', 'Tab'];
                      const isNumber = e.key >= '0' && e.key <= '9';
                      const isDecimal = e.key === '.';
                      const isAllowedKey = allowedKeys.includes(e.key);

                      if (!isNumber && !isDecimal && !isAllowedKey) {
                        e.preventDefault();
                      }
                    }}
                  />
                  <span className='mb-2 text-sm text-gray-500'>{` 검색 결과의 최소 유사도 점수`}</span>
                </div>

                <div className='mb-2 w-auto'>
                  <div className='fw-bold form-label mb-2'>검색모드</div>
                  <select
                    id=''
                    name=''
                    className='nodrag b-selectbox'
                    value={searchMode}
                    onChange={e => {
                      const newMode = e.target.value as 'Dense' | 'Sparse' | 'Hybrid';
                      setSearchMode(newMode);
                      const modeValue = newMode.toLowerCase() as 'dense' | 'sparse' | 'hybrid';
                      handleOptionFieldChange('retrieval_mode', modeValue);
                    }}
                  >
                    <option value='Dense'>Dense</option>
                    <option value='Sparse'>Sparse</option>
                    <option value='Hybrid'>Hybrid</option>
                  </select>
                </div>
                {searchMode === 'Hybrid' && (
                  <>
                    <div className='mb-2 w-auto nodrag'>
                      <UISlider
                        label='Dense'
                        required={true}
                        value={denseRatio}
                        min={0.1}
                        max={0.9}
                        step={0.1}
                        onChange={value => {
                          const normalizedValue = normalizeValue(value);
                          setDenseRatio(normalizedValue);
                          setDenseRatioText(normalizedValue.toFixed(1));
                          const newSparseValue = normalizeValue(1 - normalizedValue);
                          setSparseRatio(newSparseValue);
                          setSparseRatioText(newSparseValue.toFixed(1));
                          handleOptionFieldChange('hybrid_dense_ratio', normalizedValue);
                        }}
                        startLabel='0.1'
                        endLabel='0.9'
                        width='100%'
                        showTextField={true}
                        textValue={denseRatioText}
                        onTextChange={text => {
                          handleTextInput(text, setDenseRatioText);
                          const parsedValue = parseFloat(text);
                          if (!isNaN(parsedValue) && text.length > 0) {
                            const normalizedValue = normalizeTextValue(text);
                            setDenseRatio(normalizedValue);
                            const newSparseValue = normalizeValue(1 - normalizedValue);
                            setSparseRatio(newSparseValue);
                            setSparseRatioText(newSparseValue.toFixed(1));
                            handleOptionFieldChange('hybrid_dense_ratio', normalizedValue);
                          }
                        }}
                        textFieldWidth='w-32'
                      />
                    </div>
                    <div className='mb-2 w-auto nodrag'>
                      <UISlider
                        label='Sparse'
                        required={true}
                        value={sparseRatio}
                        min={0.1}
                        max={0.9}
                        step={0.1}
                        onChange={value => {
                          const normalizedValue = normalizeValue(value);
                          setSparseRatio(normalizedValue);
                          setSparseRatioText(normalizedValue.toFixed(1));
                          const newDenseValue = normalizeValue(1 - normalizedValue);
                          setDenseRatio(newDenseValue);
                          setDenseRatioText(newDenseValue.toFixed(1));
                          handleOptionFieldChange('hybrid_dense_ratio', newDenseValue);
                        }}
                        startLabel='0.1'
                        endLabel='0.9'
                        width='100%'
                        showTextField={true}
                        textValue={sparseRatioText}
                        onTextChange={text => {
                          handleTextInput(text, setSparseRatioText);
                          const parsedValue = parseFloat(text);
                          if (!isNaN(parsedValue) && text.length > 0) {
                            const normalizedValue = normalizeTextValue(text);
                            setSparseRatio(normalizedValue);
                            const newDenseValue = normalizeValue(1 - normalizedValue);
                            setDenseRatio(newDenseValue);
                            setDenseRatioText(newDenseValue.toFixed(1));
                            handleOptionFieldChange('hybrid_dense_ratio', newDenseValue);
                          }
                        }}
                        textFieldWidth='w-32'
                      />
                    </div>
                  </>
                )}
              </CardBody>

              <div className='bg-gray-50 px-4 py-3 border-t border-gray-200'>
                <h3 className='text-lg font-semibold text-gray-700'>Schema</h3>
              </div>

              <CustomScheme
                id={id}
                inputKeys={inputKeys}
                setInputKeys={setInputKeys}
                inputValues={inputValues}
                setInputValues={setInputValues}
                innerData={innerData}
                outputKeys={outputKeys}
              />
            </>
          )}
        </>

        <CardFooter>
          <NodeFooter onClick={handleFooterFold} isToggle={innerData.isToggle} />
        </CardFooter>
        <Handle
          type='source'
          id={`retriever_right_${id}`}
          position={Position.Right}
          isConnectable={true}
          style={{
            width: 20,
            height: 20,
            background: '#000000',
            top: '50%',
            transform: 'translateY(-50%)',
            right: -10,
            border: '2px solid white',
            zIndex: 20,
          }}
        />
      </Card>
    </div>
  );
};
