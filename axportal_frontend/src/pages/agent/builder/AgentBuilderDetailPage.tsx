import { useEffect, useMemo, useRef, useState } from 'react';

import { Button } from '@/components/common/auth';
import { ManagerInfoBox } from '@/components/common/manager/ManagerInfoBox/component';
import { ProjectInfoBox } from '@/components/common/project/ProjectInfoBox/component';
import { UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIArticle, UIGroup, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageFooter } from '@/components/UI/molecules/UIPageFooter';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { useApiQueries, useApiQuery } from '@/hooks/common/api/useApi';
import { useLayerPopup } from '@/hooks/common/layer';
import { useDeleteAgentBuilder, useGetAgentLineages } from '@/services/agent/builder/agentBuilder.services';
import type { AgentBuilderDetailRes } from '@/services/agent/builder/types';
import { useGetAgentAppList } from '@/services/deploy/agent/agentDeploy.services';
import type { GetModelDeployResponse } from '@/services/deploy/model/types';
import { useModal } from '@/stores/common/modal';
import { useQueryClient } from '@tanstack/react-query';
import { useNavigate, useParams } from 'react-router-dom';

import { api } from '@/configs/axios.config';
import { env, RUN_MODE_TYPES } from '@/constants/common/env.constants';
import { getPhoenixProjectId } from '@/services/agent/builder/agentBuilder.services';
import { useUser } from '@/stores';
import { AgentBuilderEditPopupPage } from './AgentBuilderEditPopupPage';

type KnowledgeRepoMetadata = {
  id?: string;
  name?: string;
  embeddingModelServingName?: string;
  embeddingModelName?: string;
  embedding_model_serving_name?: string;
  embedding_model_name?: string;
  vectorDbName?: string;
  vectorDbType?: string;
  vector_db_name?: string;
  vector_db_type?: string;
  indexName?: string;
  index_name?: string;
  ragChunkIndexNm?: string;
  rag_chunk_index_nm?: string;
  [key: string]: any;
};

export const AgentBuilderDetailPage = () => {
  const { agentId } = useParams<{ agentId: string }>();
  const navigate = useNavigate();
  const layerPopupOne = useLayerPopup();

  const { openAlert, openConfirm } = useModal();
  const queryClient = useQueryClient();

  const prevAgentIdRef = useRef<string>('');
  useEffect(() => {
    if (prevAgentIdRef.current !== agentId && prevAgentIdRef.current !== '') {
      queryClient.removeQueries({ queryKey: ['agent-builder', prevAgentIdRef.current] });
      queryClient.removeQueries({ queryKey: ['agentLineages', prevAgentIdRef.current] });
    }

    if (prevAgentIdRef.current !== agentId) {
      prevAgentIdRef.current = agentId || '';
    }
  }, [agentId, queryClient]);

  const {
    data: agentBuilderResponse,
    error: _error,
    refetch: refetchAgentBuilder,
  } = useApiQuery<AgentBuilderDetailRes>({
    queryKey: ['agent-builder', agentId],
    url: `/agent/builder/${agentId}`,
    timeout: 60000,
    enabled: Boolean(agentId),
    staleTime: 0,
    gcTime: 0,
    refetchOnMount: 'always',
    refetchOnReconnect: true,
    refetchOnWindowFocus: false,
    select: (data: any) => data,
  } as any);

  const agentBuilder = (agentBuilderResponse as any)?.data || agentBuilderResponse;
  const { data: lineagesData, refetch: refetchLineages } = useGetAgentLineages(
    { agentId: agentId! },
    {
      enabled: Boolean(agentId),
      staleTime: 0,
      gcTime: 0,
      refetchOnMount: 'always',
      refetchOnReconnect: true,
      refetchOnWindowFocus: false,
    }
  );
  const deleteAgentBuilderMutation = useDeleteAgentBuilder();

  const normalizedBuilder = useMemo(() => {
    if (!agentBuilder) {
      return {
        name: '',
        description: '',
        publicStatus: '전체공유',
      };
    }

    const raw = agentBuilder as any;
    const name = raw.name ?? raw.graph?.name ?? raw.data?.name ?? '';
    const description = raw.description ?? raw.graph?.description ?? raw.data?.description ?? '';
    const normalizedDescription = (() => {
      const text = String(description ?? '').trim();
      if (!text || text === '설명 없음') {
        return '';
      }
      return text;
    })();
    const publicStatus = raw.publicStatus ?? raw.graph?.publicStatus ?? raw.data?.publicStatus ?? '전체공유';

    return {
      name: String(name ?? ''),
      description: normalizedDescription,
      publicStatus: String(publicStatus ?? '전체공유'),
    };
  }, [agentBuilder]);

  const { data: deployedAgentsData } = useGetAgentAppList({
    page: 1,
    size: 1000,
    targetType: 'all',
    sort: 'created_at,desc',
    filter: '',
    search: '',
  });

  const deployedAgentIds = useMemo(() => {
    if (!deployedAgentsData?.content) return new Set<string>();

    const ids = new Set<string>();
    deployedAgentsData.content.forEach((app: any) => {
      if (Array.isArray(app.deployments)) {
        app.deployments.forEach((deployment: any) => {
          if (deployment?.deploymentConfigPath) {
            const pathParts = deployment.deploymentConfigPath.split('/');
            const extractedId = pathParts[pathParts.length - 2];
            if (extractedId) {
              ids.add(extractedId);
            }
          }
          if (deployment?.targetId) {
            ids.add(deployment.targetId);
          }
        });
      }
      if (app?.targetId) {
        ids.add(app.targetId);
      }
    });

    return ids;
  }, [deployedAgentsData]);

  const isDeployed = useMemo(() => {
    if (!agentId) return false;
    return deployedAgentIds.has(agentId);
  }, [agentId, deployedAgentIds]);

  const deploymentStatus = useMemo(() => (isDeployed ? '개발배포' : '미배포'), [isDeployed]);

  const managerPeople = useMemo(() => {
    if (!agentBuilder) {
      return undefined;
    }

    return [
      {
        userId: agentBuilder.createdBy || '',
        datetime: agentBuilder.createdAt || '',
      },
      {
        userId: agentBuilder.updatedBy || agentBuilder.createdBy || '',
        datetime: agentBuilder.updatedAt || agentBuilder.createdAt || '',
      },
    ];
  }, [agentBuilder, agentBuilder?.createdBy, agentBuilder?.createdAt, agentBuilder?.updatedBy, agentBuilder?.updatedAt]);

  const modelColumnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id' as any,
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
        } as any,
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '노드명',
        field: 'nodeName' as any,
        width: 392,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '노드 종류',
        field: 'nodeType' as any,
        width: 392,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '모델명',
        field: 'modelName' as any,
        flex: 1,
        cellStyle: { paddingLeft: '16px' },
      },
    ],
    []
  );

  const knowledgeColumnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id' as any,
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
        } as any,
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '노드명',
        field: 'nodeName' as any,
        width: 392,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '노드 종류',
        field: 'nodeType' as any,
        width: 392,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '지식명',
        field: 'knowledgeName' as any,
        flex: 1,
        cellStyle: { paddingLeft: '16px', cursor: 'pointer', color: '#1B84FF', textDecoration: 'underline' },
      },
    ],
    []
  );

  const servingModelIds = useMemo(() => {
    if (!agentBuilder?.nodes) return [];

    const ids: string[] = [];
    agentBuilder.nodes.forEach((node: any) => {
      let servingId = node.data?.serving_model || '';

      if (!servingId) {
        if (node.data?.query_rewriter?.llm_chain?.llm_config) {
          servingId = node.data.query_rewriter.llm_chain.llm_config.serving_model || '';
        } else if (node.data?.context_refiner?.llm_chain?.llm_config) {
          servingId = node.data.context_refiner.llm_chain.llm_config.serving_model || '';
        }
      }

      const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
      if (servingId && uuidRegex.test(servingId) && !ids.includes(servingId)) {
        ids.push(servingId);
      }
    });

    return ids;
  }, [agentBuilder?.nodes]);

  const modelDeployQueries = useApiQueries<GetModelDeployResponse>(
    servingModelIds.map(servingId => ({
      url: `/modelDeploy/${servingId}`,
      queryKey: ['modelDeploy', servingId],
      options: {
        enabled: !!servingId,
        retry: false,
        retryOnMount: false,
        onError: (error: any) => {
          if (error?.response?.status === 403) {
            return;
          }
          // 다른 에러는 기본 처리 (필요시 로깅 가능)
        },
      },
    }))
  );

  const servingModelNameMap = useMemo(() => {
    const map: Record<string, string> = {};
    modelDeployQueries.forEach((query, index) => {
      if (query.data && servingModelIds[index]) {
        map[servingModelIds[index]] = query.data.name || query.data.displayName || '';
      }
    });
    return map;
  }, [modelDeployQueries, servingModelIds]);

  const modelRowData = useMemo(() => {
    if (!agentBuilder?.nodes) {
      return [];
    }

    const modelNodes = agentBuilder.nodes.filter((node: any) => {
      const hasTopLevel = !!(node.data?.serving_model || node.data?.serving_name);
      const hasInQueryRewriter = !!(node.data?.query_rewriter?.llm_chain?.llm_config?.serving_model || node.data?.query_rewriter?.llm_chain?.llm_config?.serving_name);
      const hasInContextRefiner = !!(node.data?.context_refiner?.llm_chain?.llm_config?.serving_model || node.data?.context_refiner?.llm_chain?.llm_config?.serving_name);
      const hasInRerankCnf = !!(node.data?.context_refiner?.rerank_cnf?.model_info?.serving_model || node.data?.context_refiner?.rerank_cnf?.model_info?.serving_name);

      const result = hasTopLevel || hasInQueryRewriter || hasInContextRefiner || hasInRerankCnf;

      return result;
    });

    const matchedModels = modelNodes.map((node: any, index: number) => {
      let servingId = node.data?.serving_model || '';

      if (!servingId || servingId === 'null') {
        if (node.data?.query_rewriter?.llm_chain?.llm_config) {
          servingId = node.data.query_rewriter.llm_chain.llm_config.serving_model || '';
        } else if (node.data?.context_refiner?.llm_chain?.llm_config) {
          servingId = node.data.context_refiner.llm_chain.llm_config.serving_model || '';
        } else if (node.data?.context_refiner?.rerank_cnf?.model_info) {
          servingId = node.data.context_refiner.rerank_cnf.model_info.serving_model || '';
        }
      }

      let servingName = node.data?.serving_name || '';
      if (!servingName) {
        if (node.data?.query_rewriter?.llm_chain?.llm_config) {
          servingName = node.data.query_rewriter.llm_chain.llm_config.serving_name || '';
        } else if (node.data?.context_refiner?.llm_chain?.llm_config) {
          servingName = node.data.context_refiner.llm_chain.llm_config.serving_name || '';
        } else if (node.data?.context_refiner?.rerank_cnf?.model_info) {
          servingName = node.data.context_refiner.rerank_cnf.model_info.serving_name || '';
        }
      }

      if ((!servingId || servingId === 'null') && lineagesData?.data) {
        const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

        const matchingLineage = lineagesData.data.find((lineage: any) => {
          const sourceKey = (lineage as any).sourceKey || (lineage as any).source_key;
          const targetType = (lineage as any).targetType || (lineage as any).target_type;
          const targetKey = (lineage as any).targetKey || (lineage as any).target_key;

          const matchesSource = sourceKey === node.id || sourceKey === agentId;

          return matchesSource && (targetType === 'SERVING_MODEL' || targetType === 'MODEL' || targetType === 'MODEL_VERSION') && targetKey && uuidRegex.test(targetKey);
        });

        if (matchingLineage) {
          servingId = (matchingLineage as any).targetKey || '';
        }
      }

      if ((!servingId || servingId === 'null') && servingName && servingName !== 'null' && servingName.trim() !== '') {
        const modelNameToUuidMap: Record<string, string> = {};
        Object.entries(servingModelNameMap).forEach(([uuid, modelName]) => {
          if (modelName && modelName.trim() !== '') {
            modelNameToUuidMap[modelName] = uuid;
          }
        });

        if (modelNameToUuidMap[servingName]) {
          servingId = modelNameToUuidMap[servingName];
        }
      }

      if ((!servingId || !servingName) && (node.type === 'retriever__doc_compressor' || node.type === 'retriever__doc_filter' || node.type === 'retriever__doc_reranker')) {
        if (node.type === 'retriever__doc_reranker' && node.data?.context_refiner?.rerank_cnf?.model_info) {
          const modelInfo = node.data.context_refiner.rerank_cnf.model_info;
          if (!servingId) {
            servingId = modelInfo.serving_model || modelInfo.servingModel || modelInfo.model || '';
          }
          if (!servingName) {
            servingName = modelInfo.serving_name || modelInfo.servingName || modelInfo.model_name || '';
          }
        } else if (node.data?.context_refiner?.llm_chain?.llm_config) {
          const llmConfig = node.data.context_refiner.llm_chain.llm_config;
          if (!servingId) {
            servingId = llmConfig.serving_model || llmConfig.servingModel || llmConfig.model || '';
          }
          if (!servingName) {
            servingName = llmConfig.serving_name || llmConfig.servingName || llmConfig.model_name || '';
          }
        }
      }

      let displayModelName = servingName || '-';

      const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
      if (!displayModelName || displayModelName === '-' || displayModelName === 'D' || uuidRegex.test(displayModelName)) {
        if (servingId && servingModelNameMap[servingId]) {
          displayModelName = servingModelNameMap[servingId];
        } else if (servingId && uuidRegex.test(servingId)) {
          displayModelName = `Model-${servingId.substring(0, 8)}`;
        } else {
          displayModelName = '-';
        }
      }

      const nodeName = node.data?.name || node.data?.label || node.id;

      let nodeType = 'Serving Model';
      if (node.type === 'agent__generator') {
        nodeType = 'Generator';
      } else if (node.type === 'retriever__rewriter_hyde') {
        nodeType = 'Rewriter HyDE';
      } else if (node.type === 'retriever__rewriter_multiquery') {
        nodeType = 'MultiQuery';
      } else if (node.type === 'retriever__doc_compressor') {
        nodeType = 'Compressor';
      } else if (node.type === 'retriever__doc_filter') {
        nodeType = 'Doc Filter';
      } else if (node.type === 'retriever__doc_reranker') {
        nodeType = 'Doc ReRanker';
      } else if (node.type === 'agent__reviewer') {
        nodeType = 'Reviewer';
      } else if (node.type === 'agent__categorizer') {
        nodeType = 'Categorizer';
      } else if (node.type) {
        nodeType = node.type;
      }

      return {
        id: index + 1,
        nodeName: nodeName,
        nodeType: nodeType,
        modelName: displayModelName,
        servingId: servingId || '',
        node: node,
      };
    });

    return matchedModels;
  }, [agentBuilder, servingModelNameMap]);

  const knowledgeRepoIds = useMemo<string[]>(() => {
    const ids = new Set<string>();

    agentBuilder?.nodes?.forEach((node: any) => {
      const repoId =
        node.data?.knowledge_retriever?.repo_id ??
        node.data?.knowledge_retriever?.repoId ??
        node.data?.knowledge_retriever?.knowledge_info?.repo_id ??
        node.data?.knowledge_retriever?.knowledge_info?.repoId ??
        node.data?.knowledge_id ??
        node.data?.knowledgeId ??
        node.data?.knowledge?.repo_id ??
        node.data?.knowledge?.repoId ??
        node.data?.schemaData?.repo_id ??
        node.data?.schemaData?.knowledge_retriever?.repo_id ??
        null;
      const validRepoId = repoId && String(repoId).trim() !== '' ? repoId : null;
      if (validRepoId) {
        ids.add(validRepoId);
      }
    });

    lineagesData?.data?.forEach((lineage: any) => {
      const targetType = (lineage as any).targetType ?? (lineage as any).target_type;
      if (targetType === 'KNOWLEDGE') {
        const targetKey = (lineage as any).targetKey ?? (lineage as any).target_key;
        if (targetKey) {
          ids.add(targetKey);
        }
      }
    });

    return Array.from(ids);
  }, [agentBuilder?.nodes, lineagesData?.data]);

  const [knowledgeRepoMap, setKnowledgeRepoMap] = useState<Record<string, KnowledgeRepoMetadata>>({});

  useEffect(() => {
    let isCancelled = false;

    const fetchKnowledgeRepos = async () => {
      if (knowledgeRepoIds.length === 0) {
        setKnowledgeRepoMap({});
        return;
      }

      const entries = await Promise.all(
        knowledgeRepoIds.map(async repoId => {
          if (!repoId) {
            return null;
          }

          const response = await api.get(`/dataCtlg/knowledge/repos/external/${repoId}`);
          const payload = response?.data?.data ?? response?.data ?? null;

          return payload ? ([repoId, payload] as const) : null;
        })
      );

      if (isCancelled) {
        return;
      }

      const map: Record<string, KnowledgeRepoMetadata> = {};
      entries.forEach(entry => {
        if (entry) {
          const [repoId, payload] = entry;
          map[repoId] = payload as KnowledgeRepoMetadata;
        }
      });

      setKnowledgeRepoMap(map);
    };

    fetchKnowledgeRepos();

    return () => {
      isCancelled = true;
    };
  }, [knowledgeRepoIds]);

  const knowledgeRowData = useMemo(() => {
    if (!agentBuilder?.nodes) {
      return [];
    }

    const knowledgeRowMap = new Map<
      string,
      {
        id: number;
        nodeName: string;
        nodeType: string;
        knowledgeName: string;
        knowledgeId: string;
        knwId?: string;
        expKnwId?: string;
      }
    >();

    const knowledgeNodes = agentBuilder.nodes.filter((node: any) => {
      return node.type === 'retriever__knowledge';
    });

    knowledgeNodes.forEach((node: any) => {
      let knowledgeId =
        node.data?.knowledge_retriever?.repo_id ??
        node.data?.knowledge_retriever?.repoId ??
        node.data?.knowledge_retriever?.knowledge_info?.repo_id ??
        node.data?.knowledge_retriever?.knowledge_info?.repoId ??
        node.data?.knowledge_id ??
        node.data?.knowledgeId ??
        node.data?.knowledge?.repo_id ??
        node.data?.knowledge?.repoId ??
        node.data?.schemaData?.repo_id ??
        node.data?.schemaData?.knowledge_retriever?.repo_id ??
        (node.data?.schemaData as any)?.knowledge_retriever?.repo_id ??
        null;

      let validKnowledgeId = knowledgeId && String(knowledgeId).trim() !== '' ? knowledgeId : null;

      if (!validKnowledgeId && lineagesData?.data) {
        const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

        const matchingLineage = lineagesData.data.find((lineage: any) => {
          const sourceKey = (lineage as any).sourceKey || (lineage as any).source_key;
          const targetType = (lineage as any).targetType || (lineage as any).target_type;
          const targetKey = (lineage as any).targetKey || (lineage as any).target_key;

          const matchesSource = sourceKey === node.id || sourceKey === agentId;

          return matchesSource && targetType === 'KNOWLEDGE' && targetKey && uuidRegex.test(targetKey);
        });

        if (matchingLineage) {
          validKnowledgeId = (matchingLineage as any).targetKey || '';
        }
      }

      if (!validKnowledgeId) {
        return;
      }

      if (knowledgeRowMap.has(validKnowledgeId)) {
        return;
      }

      const nodeKnowledgeName = node.data?._knowledgeName ?? node.data?.knowledge_name ?? node.data?.knowledgeName ?? `Knowledge-${validKnowledgeId.substring(0, 8)}`;

      const repoInfo = knowledgeRepoMap[validKnowledgeId];
      const repoKnowledgeName = repoInfo?.name as string | undefined;

      let actualKnwId = validKnowledgeId;

      if (repoInfo) {
        if ((repoInfo as any)?.knw_id) {
          actualKnwId = (repoInfo as any).knw_id;
        } else if ((repoInfo as any)?.index_name) {
          const indexName = (repoInfo as any).index_name;
          const uuidMatch = indexName.match(/([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})/i);
          if (uuidMatch) {
            actualKnwId = uuidMatch[1];
          }
        } else if ((repoInfo as any)?.rag_chunk_index_nm) {
          const ragChunkIndexNm = (repoInfo as any).rag_chunk_index_nm;
          const uuidMatch = ragChunkIndexNm.match(/([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})/i);
          if (uuidMatch) {
            actualKnwId = uuidMatch[1];
          }
        }
      }

      if (actualKnwId === validKnowledgeId && lineagesData?.data) {
        const knowledgeLineage = lineagesData.data.find((lineage: any) => {
          const targetType = (lineage as any).targetType ?? (lineage as any).target_type;
          const sourceKey = (lineage as any).sourceKey ?? (lineage as any).source_key;

          return targetType === 'KNOWLEDGE' && (sourceKey === node.id || sourceKey === agentId);
        });

        if (knowledgeLineage) {
          const targetKey = (knowledgeLineage as any).targetKey ?? (knowledgeLineage as any).target_key;
          if (targetKey && targetKey !== validKnowledgeId) {
            actualKnwId = targetKey;
          }
        }
      }

      const repoKnwId = actualKnwId;
      const repoExpKnwId = validKnowledgeId;

      knowledgeRowMap.set(validKnowledgeId, {
        id: knowledgeRowMap.size + 1,
        nodeName: node.data?.name || node.data?.label || node.id,
        nodeType: node.type || 'Knowledge',
        knowledgeName: repoKnowledgeName ?? nodeKnowledgeName,
        knowledgeId: validKnowledgeId,
        knwId: repoKnwId,
        expKnwId: repoExpKnwId,
      });
    });

    const rows = Array.from(knowledgeRowMap.values());
    return rows;
  }, [agentBuilder?.nodes, lineagesData?.data, knowledgeRepoMap, agentId]);

  useEffect(() => {
    if (agentId) {
      refetchLineages();
    }
  }, [agentId, refetchLineages]);

  const assets = useMemo(() => {
    const assetList: Array<{ type: string; id: string }> = [];

    if (agentBuilder?.id) {
      assetList.push({ type: 'graph', id: agentBuilder.id });
    }

    const lineagesArray = Array.isArray(lineagesData) ? lineagesData : lineagesData?.data;
    if (lineagesArray && Array.isArray(lineagesArray)) {
      lineagesArray.forEach((lineage: any) => {
        const targetType = (lineage as any).targetType ?? (lineage as any).target_type;
        const targetKey = (lineage as any).targetKey ?? (lineage as any).target_key;

        let assetType = '';

        if (targetType === 'AGENT_GRAPH') {
          assetType = 'graph';
        } else if (targetType === 'FEW_SHOT') {
          assetType = 'few-shot';
        } else if (targetType === 'TOOL') {
          assetType = 'tool';
        } else if (targetType === 'MCP') {
          assetType = 'mcp';
        } else if (targetType === 'PROMPT') {
          assetType = 'infer-prompts';
        }
        // else if (targetType === 'SERVING_AGENT') {
        //   assetType = 'agent-serving';
        // }

        if (assetType && targetKey) {
          const exists = assetList.some(a => a.type === assetType && a.id === targetKey);
          if (!exists) {
            assetList.push({ type: assetType, id: targetKey });
          }
        }
      });
    }

    return assetList;
  }, [agentBuilder, lineagesData]);

  const { user } = useUser();

  const handlePhoenix = async () => {
    if (!agentBuilder?.id) {
      openAlert({
        title: '안내',
        message: '에이전트 ID가 없습니다.',
        confirmText: '확인',
      });
      return;
    }

    try {
      const phoenixProjectInfo = await getPhoenixProjectId(agentBuilder.id);

      const phoenixProjectId = phoenixProjectInfo?.projectId || (agentBuilder as any)?.phoenixProjectId;

      let phoenixUrl: string = phoenixProjectInfo?.phoenixUrl || '';

      if (!phoenixUrl) {
        const phoenixBaseUrl = env.VITE_PHOENIX_BASE_URL;
        const baseUrlWithoutProjects = phoenixBaseUrl.replace(/\/projects\/?$/, '').replace(/\/$/, '');

        if (phoenixProjectId && typeof phoenixProjectId === 'string') {
          phoenixUrl = `${baseUrlWithoutProjects}/projects/${phoenixProjectId}/spans`;
        } else {
          phoenixUrl = `${baseUrlWithoutProjects}/projects`;
        }
      }

      const newWindow = window.open(phoenixUrl, '_blank', 'noopener,noreferrer');
      if (newWindow) {
        newWindow.focus();
      } else {
        openAlert({
          title: '안내',
          message: '팝업이 차단되었습니다. 브라우저 설정에서 팝업을 허용해주세요.',
          confirmText: '확인',
        });
      }
    } catch (error) {
      openAlert({
        title: '오류',
        message: 'Phoenix를 열 수 없습니다.',
        confirmText: '확인',
      });
    }
  };

  const handleBuilderCanvas = (isReadOnly: boolean) => {
    if (agentBuilder?.id) {
      const raw = agentBuilder as any;
      if (!isReadOnly && Number(raw?.lstPrjSeq) === -999 && Number(user.activeProject.prjSeq) !== -999 && Number(user.activeProject.prjSeq) !== Number(raw?.fstPrjSeq)) {
        openAlert({
          title: '안내',
          message: '빌더 편집에 대한 권한이 없습니다.',
          confirmText: '확인',
        });
        return;
      }

      const graphPath = isReadOnly ? `/test/secret/graph2` : `/test/secret/graph2`;
      navigate(graphPath, {
        state: {
          agentId: agentBuilder.id,
          isReadOnly: isReadOnly,
          data: {
            id: agentBuilder.id,
            name: agentBuilder.name,
            description: agentBuilder.description,
            project_id: agentBuilder.id,
            nodes: agentBuilder.nodes || [],
            edges: agentBuilder.edges || [],
            created_at: agentBuilder.createdAt,
            updated_at: agentBuilder.updatedAt,
            created_by: agentBuilder.createdBy,
            updated_by: agentBuilder.updatedBy,
          },
        },
      });
    }
  };

  const handleDelete = async () => {
    if (isDeployed) {
      await openAlert({
        title: '안내',
        message: '배포된 에이전트는 삭제할 수 없습니다.',
      });
      return;
    }

    const isConfirmed = await openConfirm({
      title: '안내',
      message: '삭제하시겠어요?\n삭제한 정보는 복구할 수 없습니다.',
      confirmText: '예',
      cancelText: '아니요',
    });

    if (!isConfirmed) {
      return;
    }

    const response = await deleteAgentBuilderMutation.mutateAsync({ graphUuid: agentId! });
    const isSuccess = response?.success ?? (response === undefined || response === null);
    if (!isSuccess) {
      throw new Error((response as any)?.message || '삭제에 실패했습니다.');
    }

    await openAlert({
      title: '완료',
      message: `빌더가 삭제되었습니다.`,
      confirmText: '확인',
    });

    navigate('/test/secret');
  };

  const handleAgentBuilderEditPopup = () => {
    layerPopupOne.onOpen();
  };

  return (
    <>
      <section className='section-page'>
        <UIPageHeader
          title='빌더 조회'
          description=''
          actions={
            <UIGroup gap={8} direction='row' align='start'>
              {env.VITE_RUN_MODE !== RUN_MODE_TYPES.PROD ? (
                <Button auth={AUTH_KEY.AGENT.BUILDER_UPDATE} className='btn-tertiary-outline line-only-blue' onClick={() => handleBuilderCanvas(false)}>
                  빌더캔버스 편집
                </Button>
              ) : (
                <Button className='btn-tertiary-outline line-only-blue' onClick={() => handlePhoenix()}>
                  <span className='text-gray-500'>Phoenix</span>
                </Button>
              )}
              <Button className='btn-tertiary-outline line-only-blue' onClick={() => handleBuilderCanvas(true)}>
                빌더캔버스 조회
              </Button>
            </UIGroup>
          }
        />
        <UIPageBody>
          <UIArticle>
            <div className='article-header'>
              <UIUnitGroup direction='row' align='space-between' gap={0}>
                <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                  에이전트 정보
                </UITypography>
              </UIUnitGroup>
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
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600 break-words'>
                          {normalizedBuilder.name}
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
                        <UITypography variant='body-2' className='secondary-neutral-600 whitespace-pre-wrap break-words'>
                          {normalizedBuilder.description || ''}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          배포여부
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {deploymentStatus}
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>

          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='w-full'>
                  <UIUnitGroup gap={16} direction='column'>
                    <div className='flex justify-between w-full items-center'>
                      <div className='w-full'>
                        <UIGroup gap={12} direction='row' align='start'>
                          <div style={{ width: '200px', display: 'flex', alignItems: 'center' }}>
                            <UIDataCnt count={modelRowData.length} prefix='사용 모델 총' />
                          </div>
                        </UIGroup>
                      </div>
                    </div>
                  </UIUnitGroup>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid
                  rowData={modelRowData}
                  columnDefs={modelColumnDefs}
                  onClickRow={(params: any) => {
                    let servingId = params.data?.servingId;
                    if ((!servingId || servingId.trim() === '' || servingId === 'null') && params.data?.node) {
                      const node = params.data.node;

                      servingId = node.data?.serving_model || '';

                      if (!servingId || servingId === 'null') {
                        if (node.data?.query_rewriter?.llm_chain?.llm_config) {
                          servingId = node.data.query_rewriter.llm_chain.llm_config.serving_model || '';
                        } else if (node.data?.context_refiner?.llm_chain?.llm_config) {
                          servingId = node.data.context_refiner.llm_chain.llm_config.serving_model || '';
                        }
                      }

                      if ((!servingId || servingId === 'null') && lineagesData?.data) {
                        const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

                        const matchingLineage = lineagesData.data.find((lineage: any) => {
                          const sourceKey = (lineage as any).sourceKey || (lineage as any).source_key;
                          const targetType = (lineage as any).targetType || (lineage as any).target_type;
                          const targetKey = (lineage as any).targetKey || (lineage as any).target_key;

                          const matchesSource = sourceKey === node.id || sourceKey === agentId;

                          return (
                            matchesSource && (targetType === 'SERVING_MODEL' || targetType === 'MODEL' || targetType === 'MODEL_VERSION') && targetKey && uuidRegex.test(targetKey)
                          );
                        });

                        if (matchingLineage) {
                          servingId = (matchingLineage as any).targetKey || '';
                        }
                      }

                      if (
                        (!servingId || servingId === 'null') &&
                        params.data?.modelName &&
                        params.data.modelName !== '-' &&
                        params.data.modelName !== 'null' &&
                        params.data.modelName.trim() !== ''
                      ) {
                        const modelNameToUuidMap: Record<string, string> = {};
                        Object.entries(servingModelNameMap).forEach(([uuid, modelName]) => {
                          if (modelName && modelName.trim() !== '') {
                            modelNameToUuidMap[modelName] = uuid;
                          }
                        });

                        if (modelNameToUuidMap[params.data.modelName]) {
                          servingId = modelNameToUuidMap[params.data.modelName];
                        }
                      }
                    }

                    const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
                    if (env.VITE_RUN_MODE !== RUN_MODE_TYPES.PROD) {
                      if (servingId && servingId.trim() !== '' && servingId !== 'null' && uuidRegex.test(servingId)) {
                        navigate(`/deploy/modelDeploy/${encodeURIComponent(servingId)}`);
                      } else {
                      }
                    } else {
                    }
                  }}
                />
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination currentPage={1} totalPages={1} onPageChange={() => { }} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>

          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='w-full'>
                  <UIUnitGroup gap={16} direction='column'>
                    <div className='flex justify-between w-full items-center'>
                      <div className='w-full'>
                        <UIGroup gap={12} direction='row' align='start'>
                          <div style={{ width: '200px', display: 'flex', alignItems: 'center' }}>
                            <UIDataCnt count={knowledgeRowData.length} prefix='사용 지식 총' />
                          </div>
                        </UIGroup>
                      </div>
                    </div>
                  </UIUnitGroup>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid
                  rowData={knowledgeRowData}
                  columnDefs={knowledgeColumnDefs}
                  onClickRow={(params: any) => {
                    const rowData = params.data;
                    const knowledgeId = rowData.knowledgeId;
                    const knwId = rowData.knwId;
                    const expKnwId = rowData.expKnwId;

                    if (!knowledgeId) {
                      return;
                    }

                    const repoInfo = knowledgeRepoMap[knowledgeId] as any;

                    let actualKnwId = knwId;

                    if (actualKnwId === knowledgeId && repoInfo?.index_name) {
                      const uuidMatch = repoInfo.index_name.match(/([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})/i);
                      if (uuidMatch) {
                        actualKnwId = uuidMatch[1];
                      }
                    }

                    if (actualKnwId === knowledgeId && repoInfo?.rag_chunk_index_nm) {
                      const uuidMatch = repoInfo.rag_chunk_index_nm.match(/([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})/i);
                      if (uuidMatch) {
                        actualKnwId = uuidMatch[1];
                      }
                    }

                    const expKnwIdValue = expKnwId || knowledgeId;

                    const knowledgeData = {
                      knwId: actualKnwId,
                      expKnwId: expKnwIdValue,
                      id: actualKnwId || expKnwIdValue,
                      name: rowData.knowledgeName || '',
                      description: '',
                      embedding: repoInfo?.embedding_model_serving_name || repoInfo?.embeddingModelServingName || repoInfo?.embedding_info?.serving_name || '',
                      vectorDB: repoInfo?.vectordb_conn_info?.vectorDbName || repoInfo?.vectordb_conn_info?.vectorDbType || '',
                      ragChunkIndexNm: repoInfo?.index_name || repoInfo?.indexName || '',
                      dataPipelineLoadStatus: 'completed',
                      isCustomKnowledge: false,
                    };

                    const navigationId = actualKnwId || expKnwIdValue || knowledgeId;

                    if (env.VITE_RUN_MODE !== RUN_MODE_TYPES.PROD) {
                      navigate(`/data/dataCtlg/knowledge/detail/${encodeURIComponent(navigationId)}`, {
                        state: { knowledgeData },
                      });
                    } else {
                    }
                  }}
                />
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination currentPage={1} totalPages={1} onPageChange={() => { }} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>

          {managerPeople && <ManagerInfoBox type='uuid' people={managerPeople} />}

          <ProjectInfoBox assets={assets} auth={AUTH_KEY.AGENT.BUILDER_CHANGE_PUBLIC} />
        </UIPageBody>

        <UIPageFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='center'>
              <Button auth={AUTH_KEY.AGENT.BUILDER_DELETE} className='btn-primary-gray' onClick={handleDelete} disabled={isDeployed}>
                삭제
              </Button>
              <Button auth={AUTH_KEY.AGENT.BUILDER_UPDATE} className='btn-primary-blue' onClick={handleAgentBuilderEditPopup}>
                수정
              </Button>
            </UIUnitGroup>
          </UIArticle>
        </UIPageFooter>
      </section>
      <AgentBuilderEditPopupPage
        agentId={agentBuilder?.id || ''}
        agentName={normalizedBuilder.name}
        agentDescription={normalizedBuilder.description}
        isOpen={layerPopupOne.currentStep > 0}
        onClose={layerPopupOne.onClose}
        onUpdateSuccess={() => {
          refetchAgentBuilder();
        }}
      />
    </>
  );
};
