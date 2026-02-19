import { isChangeFewShotAtom, isChangePromptAtom, isChangeToolsAtom, selectedFewShotIdRepoAtom, selectedPromptIdRepoAtom } from '@/components/agents/builder/atoms/AgentAtom';
import { clearModelEventAtom, isClearModelAtom } from '@/components/agents/builder/atoms/clearModelAtom';
import { isChangeLLMAtom, selectedLLMRepoAtom } from '@/components/agents/builder/atoms/llmAtom';
import { useNodeTracing } from '@/components/agents/builder/hooks/useNodeTracing';

import { Card, CardBody, CardFooter } from '@/components/agents/builder/common/index.ts';
import { useGraphActions } from '@/components/agents/builder/hooks/useGraphActions.ts';
import { SelectFewShot } from '@/components/agents/builder/pages/graph/contents/SelectFewShot.tsx';
import { SelectLLM } from '@/components/agents/builder/pages/graph/contents/SelectLLM.tsx';
import { SelectPrompt } from '@/components/agents/builder/pages/graph/contents/SelectPrompt.tsx';
import { type CustomNode, type CustomNodeInnerData, type GeneratorDataSchema, type InputKeyItem, NodeType, type OutputKeyItem } from '@/components/agents/builder/types/Agents';
import type { GetModelDeployResponse } from '@/services/deploy/model/types';
import { Handle, type NodeProps, Position, useUpdateNodeInternals } from '@xyflow/react';
import { useAtom } from 'jotai';
import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { NodeFooter, NodeHeader } from '.';

import { hasChatTestedAtom } from '@/components/agents/builder/atoms/logAtom.ts';
import { selectedListAtom } from '@/components/agents/builder/atoms/toolsAtom.ts';
import { LogModal } from '@/components/agents/builder/common/modal/log/LogModal.tsx';
import { useNodeHandler } from '@/components/agents/builder/hooks/useNodeHandler';
import { CustomScheme } from '@/components/agents/builder/pages/graph/contents/CustomScheme.tsx';
import { SelectMCP } from '@/components/agents/builder/pages/graph/contents/SelectMCP.tsx';
import { SelectTools } from '@/components/agents/builder/pages/graph/contents/SelectTools.tsx';
import { type MCPSelection } from '@/components/agents/builder/types/mcpTypes';
import { type Tool } from '@/components/agents/builder/types/Tools.ts';
import { getNodeStatus } from '@/components/agents/builder/utils/GraphUtils.ts';
import { useModal } from '@/stores/common/modal/useModal';

export const GeneratorNode: React.FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  const { removeNode, toggleNodeView, syncNodeData, syncAllNodeKeyTable } = useGraphActions();
  const updateNodeInternals = useUpdateNodeInternals();

  const newInnerData: CustomNodeInnerData = {
    isRun: false,
    isToggle: false,
  };

  const initPromptId = useMemo(() => {
    const gData = data as GeneratorDataSchema;
    const currentPromptId = gData.prompt_id || '';

    return currentPromptId;
  }, [data, id]);

  const initFewShotId = useMemo(() => {
    const gData = data as GeneratorDataSchema;
    return gData.fewshot_id ?? '';
  }, [data]);

  const initServingModel = useMemo(() => {
    const gData = data as GeneratorDataSchema;
    return gData.serving_model || '';
  }, [data]);

  const initServingName = useMemo(() => {
    const gData = data as GeneratorDataSchema;
    return gData.serving_name || '';
  }, [data]);

  const initModelParameters = useMemo(() => {
    const gData = data as any;
    const modelParams = gData.model_parameters || { params: [], disabled_params: [] };
    return {
      params: ((modelParams.params == null || !Array.isArray(modelParams.params)) ? [] : modelParams.params).map((param: any) => ({
        ...param,
        type: param.type ? param.type.toLowerCase() : param.type,
      })),
      disabled_params: modelParams.disabled_params || [],
    };
  }, [data]);

  const innerData: CustomNodeInnerData = data.innerData ?? newInnerData;

  useNodeTracing(id, data.name as string, data, innerData);
  const isRun = useMemo(() => innerData?.isRun ?? false, [innerData?.isRun]);
  const isDone = useMemo(() => innerData?.isDone ?? false, [innerData?.isDone]);
  const isError = useMemo(() => innerData?.isError ?? false, [innerData?.isError]);

  const [nodeStatus, setNodeStatus] = useState<string | null>(null);
  useEffect(() => {
    setNodeStatus(getNodeStatus(isRun, isDone, isError));
  }, [isRun, isDone, isError]);

  const [nodeName, setNodeName] = useState(data.name as string);
  const [description, setDescription] = useState((data.description as string) || '');
  const [servingName, setServingName] = useState(initServingName);
  const [servingModel, setServingModel] = useState(initServingModel);
  const [promptId, setPromptId] = useState<string | null>(initPromptId);
  const [fewShotId, setFewShotId] = useState<string | null>(initFewShotId);
  const [toolIds] = useState((data.tool_ids as string[]) || []);
  const [modelParameters, setModelParameters] = useState(initModelParameters);

  const normalizeMcpSelection = (catalog: any): MCPSelection => {
    if (!catalog) {
      return {
        catalogId: '',
        catalogName: '',
        serverName: '',
        serverUrl: '',
        toolIds: [],
        tools: [],
      };
    }

    const tools = Array.isArray(catalog.tools) ? catalog.tools : [];
    const normalizedTools = tools.map((tool: any) => ({
      ...tool,
      id: tool?.id || tool?.name || tool?.code || '',
      name: tool?.name || tool?.title || tool?.display_name || '',
      description: tool?.description || '',
    }));

    const normalized: MCPSelection = {
      catalogId: catalog.catalogId || catalog.id || catalog.mcp_id || '',
      catalogName: catalog.catalogName || catalog.name || catalog.server_name || catalog.serverName || '',
      serverName: catalog.serverName || catalog.server_name || catalog.name || '',
      serverUrl: catalog.serverUrl || catalog.server_url || catalog.url || '',
      toolIds: catalog.toolIds || (normalizedTools.length > 0 ? normalizedTools.map((tool: any) => tool?.id || tool?.name || '') : []),
      tools: normalizedTools,
      mcp_id: catalog.mcp_id || catalog.catalogId || catalog.id || '',
    };

    return normalized;
  };

  const initialMcpCatalogs =
    ((data as any).mcp_catalogs as any[]) ||
    ((data as any)?.mcp_catalogs as any[]) ||
    ((data as any)?.mcpCatalogs as any[]) ||
    ((data as any)?.innerData?.mcp_catalogs as any[]) ||
    [];
  const initialMcpSelections =
    ((data as any).mcp_selections as any[]) ||
    ((data as any)?.mcp_selections as any[]) ||
    ((data as any)?.mcpSelections as any[]) ||
    ((data as any)?.innerData?.mcp_selections as any[]) ||
    [];

  const initialMcpStateSource = (initialMcpCatalogs.length > 0 ? initialMcpCatalogs : initialMcpSelections).map(normalizeMcpSelection);
  const [selectedMCPs, setSelectedMCPs] = useState<MCPSelection[]>(initialMcpStateSource);
  const initialInputKeys = useMemo(() => {
    const loadedKeys = (data.input_keys as InputKeyItem[]) || [];
    return loadedKeys;
  }, [data.input_keys]);
  const [inputKeys, setInputKeys] = useState<InputKeyItem[]>(initialInputKeys);
  const [inputValues, setInputValues] = useState<string[]>(inputKeys.map(item => item.name));

  const initialOutputKeys = useMemo(() => {
    const initOutputItems: OutputKeyItem[] = (data.output_keys as OutputKeyItem[]) || [];
    return (data.output_keys as OutputKeyItem[]) || initOutputItems;
  }, [data.output_keys]);
  const [outputKeys, _setOutputKeys] = useState<OutputKeyItem[]>(initialOutputKeys);
  const [_outputValues, setOutputValues] = useState<string[]>(outputKeys.map(item => (item == null ? '' : item.name)));
  const nodesUpdatedRef = useRef(false);

  const isInitialMountRef = useRef(true);
  const prevDataRef = useRef(data);

  useEffect(() => {
    if (isInitialMountRef.current) {
      if (!(data as any)?.release_version && (data as any)?.release_version !== 'latest') {
        syncGenData();
      }
    }
  }, []);

  useEffect(() => {
    if (isInitialMountRef.current) {
      isInitialMountRef.current = false;
      prevDataRef.current = data;
      return;
    }

    const dataChanged = prevDataRef.current !== data;
    if (!dataChanged) {
      return;
    }
    const rawInputKeys = (data.input_keys as InputKeyItem[]) || [];
    const newInputKeys = rawInputKeys;
    const newOutputKeys = (data.output_keys as OutputKeyItem[]) || [];

    const savedInputKeysCount = newInputKeys.length;
    const currentInputKeysCount = inputKeys.length;
    const inputKeysContentEqual =
      savedInputKeysCount === currentInputKeysCount &&
      newInputKeys.every((savedKey, idx) => {
        const currentKey = inputKeys[idx];
        return (
          currentKey &&
          currentKey.name === savedKey.name &&
          currentKey.required === savedKey.required &&
          (currentKey.keytable_id || '') === (savedKey.keytable_id || '') &&
          (currentKey.fixed_value || null) === (savedKey.fixed_value || null)
        );
      });

    if (savedInputKeysCount > 0 && !inputKeysContentEqual && savedInputKeysCount >= currentInputKeysCount) {
      const finalKeys: InputKeyItem[] = newInputKeys.map(newKey => {
        const existingKey = inputKeys.find(ek => ek.name === newKey.name);
        return {
          ...newKey,
          keytable_id: existingKey?.keytable_id || newKey.keytable_id || '',
          fixed_value: existingKey?.fixed_value !== undefined ? existingKey.fixed_value : newKey.fixed_value,
        };
      });

      const keysChanged =
        finalKeys.length !== inputKeys.length ||
        finalKeys.some((key, idx) => {
          const existingKey = inputKeys[idx];
          return (
            !existingKey ||
            key.name !== existingKey.name ||
            key.required !== existingKey.required ||
            key.keytable_id !== existingKey.keytable_id ||
            key.fixed_value !== existingKey.fixed_value
          );
        });

      if (keysChanged) {
        setInputKeys(finalKeys);
      }
    }

    const outputKeysContentEqual =
      newOutputKeys.length === outputKeys.length &&
      newOutputKeys.every((savedKey, idx) => {
        const currentKey = outputKeys[idx];
        return currentKey && currentKey.name === savedKey.name;
      });

    if (newOutputKeys.length > 0 && !outputKeysContentEqual) {
      _setOutputKeys(newOutputKeys);
    }

    prevDataRef.current = data;
  }, [data, data.input_keys, data.output_keys]);

  const syncGenData = () => {
    const currentSelectedTools = selectedToolsRepo[id];
    const newToolIds = currentSelectedTools === null ? [] : currentSelectedTools ? currentSelectedTools.map(tool => tool.id) : toolIds;
    const toolDetails =
      currentSelectedTools === null
        ? []
        : currentSelectedTools
          ? currentSelectedTools.map(tool => {
            const result: any = {
              id: tool.id,
              display_name: (tool as any).displayName || (tool as any).display_name || tool.name || '',
              code: tool.code || '',
              tool_type: (tool as any).toolType || tool.tool_type || 'custom_code',
              updated_at: (tool as any).updatedAt || (tool as any).updated_at || '',
              created_by: (tool as any).createdBy?.id || (tool as any).created_by || '',
              updated_by: (tool as any).updatedBy?.id || (tool as any).updated_by || '',
              name: tool.name,
              description: tool.description || '',
              created_at: (tool as any).createdAt || (tool as any).created_at || '',
            };

            if (tool.input_keys) result.input_keys = tool.input_keys;
            if (tool.server_url) result.server_url = tool.server_url;
            if (tool.method) result.method = tool.method;
            if (tool.project_id) result.project_id = tool.project_id;
            if ((tool as any).apiParam) result.apiParam = (tool as any).apiParam;

            return result;
          })
          : [];
    const currentSelectedMCPs = selectedMCPs === null ? [] : selectedMCPs || [];
    const mappedCatalogs = currentSelectedMCPs.map((m: any) => {
      const server = m.server || {};
      const serverName = server.name || m.serverName || m.server_name || m.catalogName || m.name || '';
      const serverUrl =
        server.url || server.serverUrl || server.server_url || m.serverUrl || m.server_url || m.url || (m.server && typeof m.server === 'object' && (m.server as any).url) || '';
      let activeTools = m.tools || m.selectedTools || undefined;
      if (activeTools !== undefined && !Array.isArray(activeTools)) {
        activeTools = undefined;
      }

      if (Array.isArray(activeTools) && activeTools.length === 0) {
        activeTools = undefined;
      }

      if (Array.isArray(activeTools) && activeTools.length > 0) {
        activeTools = activeTools.map((tool: any) => {
          if (!tool.inputSchema || !tool.outputSchema) {
          }
          return tool;
        });
      }

      return {
        id: m.catalogId || m.id || m.mcp_id || '',
        name: m.catalogName || m.name || serverName || '',
        server_name: serverName,
        server_url: serverUrl,
        tools: activeTools,
      };
    });

    const allTools = toolDetails;
    const currentLLM = selectedLLMRepo[id];
    const finalServingName = currentLLM?.servingName || servingName || '';
    const finalServingModel = currentLLM?.servingModel || servingModel || '';

    const newData: any = {
      ...data,
      type: NodeType.AgentGenerator.name,
      id: id,
      name: nodeName,
      description: description,
      serving_name: finalServingName,
      serving_model: finalServingModel,
      release_version: (data as any)?.release_version || (data as any)?.releaseVersion || 'latest',
      input_keys: inputKeys.map(key => {
        const preservedKeytableId = key.keytable_id !== null && key.keytable_id !== undefined ? String(key.keytable_id) : '';

        return {
          ...key,
          fixed_value: key.fixed_value,
          keytable_id: preservedKeytableId,
        };
      }),
      output_keys: outputKeys,
      innerData: {
        ...data.innerData,
      },
    };

    const currentPromptId = selectedPromptIdRepo[id];
    const currentFewShotId = selectedFewShotIdRepo[id];

    const finalPromptId =
      currentPromptId === null || currentPromptId === undefined || currentPromptId === ''
        ? ''
        : typeof currentPromptId === 'string' && currentPromptId.trim() !== ''
          ? currentPromptId
          : '';
    const finalFewShotId =
      currentFewShotId === null || currentFewShotId === undefined || currentFewShotId === ''
        ? ''
        : typeof currentFewShotId === 'string' && currentFewShotId.trim() !== ''
          ? currentFewShotId
          : '';

    newData.prompt_id = finalPromptId;
    newData.fewshot_id = finalFewShotId;
    newData.tool_ids = newToolIds.length > 0 ? newToolIds : [];
    newData.tools = allTools.length > 0 ? allTools : [];
    newData.mcp_catalogs = mappedCatalogs.length > 0 ? mappedCatalogs : [];

    if (modelParameters && (modelParameters.params.length > 0 || modelParameters.disabled_params.length > 0)) {
      newData.model_parameters = {
        params: (modelParameters.params || []).map((param: { name: string; type: string; value: string }) => ({
          ...param,
          type: param.type ? param.type.toLowerCase() : param.type,
        })),
        disabled_params: modelParameters.disabled_params || [],
      };
    } else {
      newData.model_parameters = {
        params: [],
        disabled_params: [],
      };
    }

    if (Array.isArray((data as any).variables) && (data as any).variables.length > 0) {
      newData.variables = (data as any).variables;
    } else if (!newData.variables) {
      newData.variables = [];
    }

    syncNodeData(id, newData);
  };

  const [selectedPromptIdRepo, setSelectedPromptIdRepo] = useAtom(selectedPromptIdRepoAtom);
  const [isChangePrompt, setChangePrompt] = useAtom(isChangePromptAtom);
  const [selectedFewShotIdRepo, setSelectedFewShotIdRepo] = useAtom(selectedFewShotIdRepoAtom);
  const [isChangeFewShot, setChangeFewShot] = useAtom(isChangeFewShotAtom);
  const [isChangeTools, setChangeTools] = useAtom(isChangeToolsAtom);
  const [selectedToolsRepo, setSelectedToolsRepo] = useAtom<Record<string, Tool[]>>(selectedListAtom);

  const [selectedLLMRepo, setSelectedLLMRepo] = useAtom(selectedLLMRepoAtom);
  const [isChangeLLM, setChangeLLM] = useAtom(isChangeLLMAtom);

  const [clearModelEvent] = useAtom(clearModelEventAtom);
  const [isClearModel, _setClearModel] = useAtom(isClearModelAtom);
  const toolInfoList = selectedToolsRepo[id] === null ? [] : selectedToolsRepo[id] || [];

  const { openModal } = useModal();
  const [hasChatTested] = useAtom(hasChatTestedAtom);

  useEffect(() => {
    const gData = data as GeneratorDataSchema;

    const atomPromptId = selectedPromptIdRepo[id];
    const finalPromptId =
      gData.prompt_id && gData.prompt_id.trim() !== '' ? gData.prompt_id : atomPromptId && atomPromptId !== null && atomPromptId.trim() !== '' ? atomPromptId : '';

    if (finalPromptId && finalPromptId !== promptId) {
      setPromptId(finalPromptId);
    } else if (!finalPromptId && promptId) {
      setPromptId('');
    } else if (atomPromptId === null && promptId) {
      setPromptId('');
    }

    const currentFewShotIdFromData = gData.fewshot_id || '';
    if (currentFewShotIdFromData !== fewShotId) {
      setFewShotId(currentFewShotIdFromData);
    }

    const currentLLM = selectedLLMRepo[id];
    const isLLMAtomEmpty = currentLLM === undefined || currentLLM === null;

    // data에서 serving_name/serving_model이 비어있으면 atom도 삭제 (모델 사용중지 후 반영)
    const hasDataModel = gData.serving_name && gData.serving_name.trim() !== '' && gData.serving_model && gData.serving_model.trim() !== '';

    if (isLLMAtomEmpty && hasDataModel) {
      setSelectedLLMRepo((prev: Record<string, any>) => ({
        ...prev,
        [id]: {
          servingName: gData.serving_name,
          servingModel: gData.serving_model,
        },
      }));
      if (gData.serving_name !== servingName) {
        setServingName(gData.serving_name);
      }
      if (gData.serving_model !== servingModel) {
        setServingModel(gData.serving_model);
      }
    } else if (!hasDataModel && currentLLM) {
      // data에 모델이 없고 atom에 있으면 삭제 (모델 사용중지 반영)
      setSelectedLLMRepo((prev: Record<string, any>) => {
        const { [id]: _removed, ...rest } = prev;
        return rest;
      });
      if (servingName) {
        setServingName('');
      }
      if (servingModel) {
        setServingModel('');
      }
    } else if (isLLMAtomEmpty) {
      if (servingName) {
        setServingName('');
      }
      if (servingModel) {
        setServingModel('');
      }
    } else {
      if (gData.serving_name && gData.serving_name !== servingName) {
        setServingName(gData.serving_name);
      }

      if (gData.serving_model && gData.serving_model !== servingModel) {
        setServingModel(gData.serving_model);
      }
    }
  }, [data, id, promptId, fewShotId, servingName, servingModel, selectedPromptIdRepo, selectedLLMRepo]);

  useEffect(() => {
    const gData = data as GeneratorDataSchema;

    const currentAtomValue = selectedPromptIdRepo[id];
    if (gData.prompt_id && gData.prompt_id.trim() !== '') {
      if (currentAtomValue !== gData.prompt_id) {
        setSelectedPromptIdRepo(prev => ({
          ...prev,
          [id]: gData.prompt_id,
        }));
      }
    } else {
      if (currentAtomValue !== null && currentAtomValue !== undefined) {
        setSelectedPromptIdRepo(prev => ({
          ...prev,
          [id]: null,
        }));
      }
    }

    const currentFewShotAtomValue = selectedFewShotIdRepo[id];
    const dataFewShotId = gData.fewshot_id || '';

    if (dataFewShotId.trim() !== '') {
      if (currentFewShotAtomValue !== dataFewShotId) {
        setSelectedFewShotIdRepo(prev => ({
          ...prev,
          [id]: dataFewShotId,
        }));
      }
    }
    if (gData.tools && Array.isArray(gData.tools) && gData.tools.length > 0) {
      const currentAtomValue = selectedToolsRepo[id];
      const toolsChanged = !currentAtomValue || currentAtomValue.length !== gData.tools.length || JSON.stringify(currentAtomValue) !== JSON.stringify(gData.tools);

      if (toolsChanged) {
        setSelectedToolsRepo(prev => ({
          ...prev,
          [id]: gData.tools,
        }));
      }
    }
  }, [data, id]);

  useEffect(() => {
    const nodeId = data.id as string;
    const currentRepoValue = selectedPromptIdRepo[nodeId];

    if (initPromptId && initPromptId.trim() !== '' && initPromptId !== currentRepoValue) {
      setSelectedPromptIdRepo(prev => ({
        ...prev,
        [nodeId]: initPromptId,
      }));

      if (initPromptId !== promptId) {
        setPromptId(initPromptId);
        nodesUpdatedRef.current = true;
      }
    } else if (!initPromptId || initPromptId.trim() === '') {
      if (currentRepoValue !== null && currentRepoValue !== undefined) {
        setSelectedPromptIdRepo(prev => ({
          ...prev,
          [nodeId]: null,
        }));
        if (promptId !== '') {
          setPromptId('');
          nodesUpdatedRef.current = true;
        }
      }
    }
  }, [initPromptId, data.id, selectedPromptIdRepo]);

  useEffect(() => {
    const nodeId = data.id as string;

    if (selectedFewShotIdRepo[nodeId] !== undefined) {
      return;
    }

    if (initFewShotId && initFewShotId !== '') {
      setSelectedFewShotIdRepo(prev => ({
        ...prev,
        [nodeId]: initFewShotId,
      }));
    }
  }, []);

  const currentToolIds = useMemo(() => {
    return ((data as any).tool_ids || []) as string[];
  }, [data]);

  useEffect(() => {
    const nodeId = data.id as string;
    const currentTools = (data as any).tools || [];

    if (selectedToolsRepo[nodeId] === null) {
      if (currentToolIds.length === 0) {
        return;
      }
    }

    const savedToolIds = selectedToolsRepo[nodeId]?.map(t => t.id) || [];
    const toolIdsChanged = JSON.stringify([...currentToolIds].sort()) !== JSON.stringify([...savedToolIds].sort());

    if (!toolIdsChanged) {
      if (selectedToolsRepo[nodeId] !== undefined && selectedToolsRepo[nodeId] !== null) {
        return;
      }
    }
    let initialTools: Tool[] = [];

    if (currentToolIds.length === 0) {
      if (selectedToolsRepo[nodeId] === null || (selectedToolsRepo[nodeId] && selectedToolsRepo[nodeId].length === 0)) {
        return;
      }
      setSelectedToolsRepo((prev: Record<string, Tool[]>) => ({
        ...prev,
        [nodeId]: null as any,
      }));
      return;
    }

    if (currentTools && Array.isArray(currentTools)) {
      initialTools = currentTools.filter((t: any) => {
        return t.id && currentToolIds.includes(t.id);
      });
    }

    if (initialTools.length === 0 && (data as any).tool_details && Array.isArray((data as any).tool_details)) {
      initialTools = (data as any).tool_details.filter((t: any) => {
        return t.id && currentToolIds.includes(t.id);
      });
    }

    if (initialTools.length === 0 && data.tools && (data.tools as Tool[]).length > 0) {
      initialTools = (data.tools as Tool[]).filter((t: any) => {
        return t.id && currentToolIds.includes(t.id);
      });
    }

    if (initialTools.length === 0 && currentToolIds.length > 0) {
      const toolIdsToFetch = currentToolIds as string[];

      Promise.all(
        toolIdsToFetch.map(async (toolId: string) => {
          try {
            const response = await fetch(`/api/agentTool/${toolId}`);
            if (response.ok) {
              const result = await response.json();
              return result.data; // Tool 객체
            }
            return null;
          } catch (err) {
            return null;
          }
        })
      ).then(fetchedTools => {
        const validTools = fetchedTools.filter((t): t is Tool => t !== null);
        if (validTools.length > 0) {
          setSelectedToolsRepo((prev: Record<string, Tool[]>) => ({
            ...prev,
            [nodeId]: validTools,
          }));
        } else {
          setSelectedToolsRepo((prev: Record<string, Tool[]>) => ({
            ...prev,
            [nodeId]: [],
          }));
        }
      });
      return;
    }

    if (initialTools.length > 0) {
      setSelectedToolsRepo((prev: Record<string, Tool[]>) => {
        const prevTools = prev[nodeId];

        if (prevTools === null) {
          const savedToolIds = prevTools ? [] : [];
          const actualToolIdsChanged = JSON.stringify([...currentToolIds].sort()) !== JSON.stringify([...savedToolIds].sort());
          if (!actualToolIdsChanged) {
            return prev;
          }
        }

        if (prevTools && prevTools.length === initialTools.length) {
          const prevIds = prevTools
            .map(t => t.id)
            .sort()
            .join(',');
          const newIds = initialTools
            .map(t => t.id)
            .sort()
            .join(',');
          if (prevIds === newIds) {
            return prev;
          }
        }
        if (prevTools === null || prevTools === undefined) {
          return {
            ...prev,
            [nodeId]: initialTools,
          };
        }
        return prev;
      });
    } else {
      setSelectedToolsRepo((prev: Record<string, Tool[]>) => {
        const prevTools = prev[nodeId];
        if (prevTools === null) {
          return prev;
        }
        if (prevTools === undefined || prevTools.length === 0) {
          return {
            ...prev,
            [nodeId]: [],
          };
        }
        return prev;
      });
    }
  }, [currentToolIds, id, data]);

  useEffect(() => {
    setInputValues(inputKeys.map(item => item.name));
    syncAllNodeKeyTable();
    syncGenData();
  }, [inputKeys]);
  useEffect(() => {
    setOutputValues(outputKeys.map(item => (item == null ? '' : item.name)));
  }, [outputKeys]);

  useEffect(() => {
    if (nodesUpdatedRef.current) {
      syncGenData();
      nodesUpdatedRef.current = false;
    }
  }, [nodeName, description, servingName, servingModel, promptId, fewShotId, selectedMCPs, inputKeys, outputKeys, modelParameters]);

  useEffect(() => {
    if (isChangePrompt) {
      const newPromptId = selectedPromptIdRepo[id];

      if (newPromptId === null || newPromptId === undefined) {
        setPromptId('');
        nodesUpdatedRef.current = true;
        syncGenData();
      } else if (newPromptId !== promptId) {
        setPromptId(newPromptId);
        nodesUpdatedRef.current = true;
        syncGenData();
      }
      setChangePrompt(false);
    }
  }, [isChangePrompt, id, selectedPromptIdRepo]);

  useEffect(() => {
    if (isChangeFewShot) {
      const newFewShotId = selectedFewShotIdRepo[id];

      if (newFewShotId === null || newFewShotId === undefined || newFewShotId === '') {
        setFewShotId('');
        nodesUpdatedRef.current = true;
        syncGenData();
        setChangeFewShot(false);
      } else if (newFewShotId && newFewShotId !== fewShotId) {
        setFewShotId(newFewShotId);
        nodesUpdatedRef.current = true;
        syncGenData();
        setChangeFewShot(false);
      }
    }
  }, [isChangeFewShot, selectedFewShotIdRepo, id, fewShotId, setChangeFewShot]);

  useEffect(() => {
    if (isChangeTools) {
      syncGenData();
      nodesUpdatedRef.current = true;
      setChangeTools(false);
    }
  }, [isChangeTools, syncGenData]);

  useEffect(() => {
    if (!isChangeLLM) return;

    const currentLLM = selectedLLMRepo[id];
    if (currentLLM) {
      const currentServingName = currentLLM.servingName || '';
      const currentServingModel = currentLLM.servingModel || '';

      if (currentServingName !== servingName || currentServingModel !== servingModel) {
        setServingName(currentServingName);
        setServingModel(currentServingModel);
        nodesUpdatedRef.current = true;
      }
      setChangeLLM(false);
    } else if (currentLLM === null) {
      if (servingName || servingModel) {
        setServingName('');
        setServingModel('');
        nodesUpdatedRef.current = true;
      }
      setChangeLLM(false);
    }
  }, [isChangeLLM]);

  const prevSelectedToolsRef = useRef<Tool[] | null | undefined>(undefined);

  useEffect(() => {
    const currentTools = selectedToolsRepo[id];
    const currentToolIds = currentTools === null ? [] : currentTools ? currentTools.map(t => t.id) : [];
    const existingToolIds = toolIds || [];

    const prevTools = prevSelectedToolsRef.current;
    const prevToolIds = prevTools === null ? [] : prevTools ? prevTools.map(t => t.id) : [];

    const isDifferent = JSON.stringify(currentToolIds.sort()) !== JSON.stringify(existingToolIds.sort());
    const isPrevDifferent = JSON.stringify(currentToolIds.sort()) !== JSON.stringify(prevToolIds.sort());

    if (isDifferent && isPrevDifferent && currentTools !== undefined) {
      prevSelectedToolsRef.current = currentTools;
      nodesUpdatedRef.current = true;
      syncGenData();
    } else if (currentTools !== undefined) {
      prevSelectedToolsRef.current = currentTools;
    }
  }, [selectedToolsRepo, id, toolIds]);

  useEffect(() => {
    const handleClearModelFromNode = (event: Event) => {
      const customEvent = event as CustomEvent;
      const { nodeId, nodeType /*, modelName*/ } = customEvent.detail;

      if (nodeId === id && (nodeType === 'Generator' || nodeType === 'agent__generator')) {
        setServingName('');
        setServingModel('');
        nodesUpdatedRef.current = true;
        syncGenData();
      }
    };

    window.addEventListener('clear-model-from-node', handleClearModelFromNode);

    return () => {
      window.removeEventListener('clear-model-from-node', handleClearModelFromNode);
    };
  }, [id, servingName, servingModel]);

  useEffect(() => {
    if (isClearModel && clearModelEvent) {
      const { nodeId, nodeType } = clearModelEvent;

      if (nodeId === id && (nodeType === 'Generator' || nodeType === 'agent__generator')) {
        setServingName('');
        setServingModel('');
        nodesUpdatedRef.current = true;
        syncGenData();
      }
    }
  }, [isClearModel, clearModelEvent, id]);

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

  const onClickDelete = () => {
    removeNode(id);
  };

  const handleFooterFold = (bool: boolean) => {
    toggleNodeView(id, bool);
  };

  const handleLLMChange = useCallback(
    (selectedLLM: GetModelDeployResponse) => {
      const llmName = selectedLLM.name || selectedLLM.modelName || '';
      const servingIdValue = selectedLLM.servingId || '';
      setServingName(llmName);
      setServingModel(servingIdValue);
      nodesUpdatedRef.current = true;
    },
    [servingName, servingModel]
  );

  const handleModelParametersChange = useCallback((newModelParameters: { params: Array<{ name: string; type: string; value: string }>; disabled_params: string[] }) => {
    setModelParameters(newModelParameters);
    nodesUpdatedRef.current = true;
  }, []);

  const handleNodeNameChange = (value: string) => {
    setNodeName(value);
    nodesUpdatedRef.current = true;
  };

  const handleDescriptionChange = (value: string) => {
    setDescription(value);
    nodesUpdatedRef.current = true;
  };

  const handleMCPChange = (mcpSelections: MCPSelection[]) => {
    const normalize = (arr: MCPSelection[]) =>
      [...arr]
        .sort((x, y) => String(x.catalogId).localeCompare(String(y.catalogId)))
        .map((m: any) => ({
          catalogId: m.catalogId || m.id || m.mcp_id || '',
          catalogName: m.catalogName || m.name || '',
          toolsIds: (Array.isArray(m.tools) ? m.tools.map((t: any) => t?.id || t?.name) : []).sort().join(','),
        }));
    const nextStr = JSON.stringify(normalize(mcpSelections || []));
    const currStr = JSON.stringify(normalize(selectedMCPs || []));
    if (nextStr === currStr) {
      return;
    }

    setSelectedMCPs(mcpSelections);
    nodesUpdatedRef.current = true;
  };

  useEffect(() => {
    updateNodeInternals(id);
  }, [id, updateNodeInternals, innerData.isToggle]);

  const containerRef = useRef<HTMLDivElement | null>(null);
  const resizeTimerRef = useRef<number | null>(null);
  const rafRef = useRef<number | null>(null);
  useEffect(() => {
    if (!containerRef.current) return;
    const el = containerRef.current;
    const Rz: any = (window as any).ResizeObserver;
    if (!Rz) return;
    const ro = new Rz(() => {
      updateNodeInternals(id);
      if (rafRef.current) cancelAnimationFrame(rafRef.current);
      rafRef.current = requestAnimationFrame(() => updateNodeInternals(id));
      if (resizeTimerRef.current) window.clearTimeout(resizeTimerRef.current as any);
      resizeTimerRef.current = window.setTimeout(() => updateNodeInternals(id), 40) as any;
    });
    ro.observe(el);
    return () => {
      try {
        ro.unobserve(el);
      } catch (_) { }
      if (resizeTimerRef.current) window.clearTimeout(resizeTimerRef.current as any);
      if (rafRef.current) cancelAnimationFrame(rafRef.current);
    };
  }, [id, updateNodeInternals]);
  const { autoResize, stopPropagation, preventAndStop } = useNodeHandler();
  return (
    <div ref={containerRef}>
      <Card className={['agent-card w-full min-w-[500px] max-w-[500px]', nodeStatus].filter(e => !!e).join(' ')}>
        <Handle
          type='target'
          id='gen_left'
          position={Position.Left}
          isConnectable={true}
          style={{
            width: 20,
            height: 20,
            background: '#6B7280',
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
          onClickDelete={onClickDelete}
          defaultValue={nodeName}
          onChange={handleNodeNameChange}
          onClickLog={handleHeaderClickLog}
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
                  value={description}
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
                  <span className='text-blue-500'>{description.length}</span>/100
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
                      value={description}
                      onChange={e => {
                        const value = e.target.value;
                        if (value.length <= 100) {
                          handleDescriptionChange(value);
                        }
                      }}
                      maxLength={100}
                      onMouseDown={stopPropagation}
                      onMouseUp={stopPropagation}
                      onSelect={stopPropagation}
                      onDragStart={preventAndStop}
                      onDrag={preventAndStop}
                    />
                    <div className='absolute bottom-2 right-2 text-xs text-gray-500'>
                      <span className='text-blue-500'>{description.length}</span>/100
                    </div>
                  </div>
                </div>
              </CardBody>

              <div className='border-t border-gray-200'>
                <CardBody className='p-4'>
                  <div className=''>
                    <SelectLLM
                      selectedServingName={servingName}
                      selectedServingModel={servingModel}
                      onChange={handleLLMChange}
                      nodeId={id}
                      nodeType={type}
                      asAccordionItem={true}
                      title={
                        <>
                          {'LLM'}
                          <span className='ag-color-red'>*</span>
                        </>
                      }
                      initialModelParameters={modelParameters}
                      onModelParametersChange={handleModelParametersChange}
                    />
                    <SelectPrompt
                      selectedPromptId={(() => {
                        const atomValue = selectedPromptIdRepo[id];
                        const localValue = promptId;
                        const dataValue = (data as GeneratorDataSchema).prompt_id;
                        return atomValue || dataValue || localValue || '';
                      })()}
                      nodeId={id}
                      nodeType={NodeType.AgentGenerator.name}
                      asAccordionItem={true}
                      title={'Prompt'}
                    />
                    <SelectFewShot selectedFewShotId={selectedFewShotIdRepo[id] || fewShotId} nodeId={id} asAccordionItem={true} title={'Few-shots'} />
                    <SelectTools nodeId={id} toolInfoList={toolInfoList} asAccordionItem={true} title={'Tools'} mode={'multiple'} />
                    <SelectMCP selectedMCPs={selectedMCPs} onChange={handleMCPChange} nodeId={id} asAccordionItem={true} title={'MCPs'} />
                  </div>
                </CardBody>
              </div>

              <div className='bg-gray-50 px-4 py-3 border-t border-gray-200'>
                <h3 className='text-lg font-semibold text-gray-700'>Schema</h3>
              </div>

              <CustomScheme
                id={id}
                inputKeys={inputKeys}
                setInputKeys={setInputKeys}
                inputValues={inputValues}
                setInputValues={setInputValues}
                innerData={data.innerData}
                outputKeys={outputKeys}
                type={NodeType.AgentGenerator.name}
              />
            </>
          )}
        </>

        <CardFooter className='p-0'>
          <NodeFooter onClick={handleFooterFold} isToggle={innerData.isToggle as boolean} />
        </CardFooter>
        <Handle
          type='source'
          id='gen_right'
          position={Position.Right}
          isConnectable={true}
          style={{
            width: 20,
            height: 20,
            background: '#6B7280',
            top: '50%',
            transform: 'translateY(-50%)',
            right: -10,
            border: '2px solid white',
            boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
            zIndex: 20,
          }}
        />
      </Card>
    </div>
  );
};
