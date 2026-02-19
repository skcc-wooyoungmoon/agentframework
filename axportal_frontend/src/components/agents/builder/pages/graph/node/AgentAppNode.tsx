import { isChangeAgentAppAtom, selectedAgentAppIdRepoAtom, selectedAgentAppVersionRepoAtom } from '@/components/agents/builder/atoms/AgentAtom.ts';
import { hasChatTestedAtom } from '@/components/agents/builder/atoms/logAtom.ts';
import { Accordion } from '@/components/agents/builder/common/accordion/Accordion.tsx';
import { Card, CardBody, CardFooter } from '@/components/agents/builder/common';
import { LogModal } from '@/components/agents/builder/common/modal/log/LogModal.tsx';
import { useAutoUpdateNodeInternals } from '@/components/agents/builder/hooks/useAutoUpdateNodeInternals';
import { useGraphActions } from '@/components/agents/builder/hooks/useGraphActions.ts';
import { useNodeTracing } from '@/components/agents/builder/hooks/useNodeTracing';
import { CustomScheme } from '@/components/agents/builder/pages/graph/contents/CustomScheme.tsx';
import { type AgentAppDataSchema, type CustomNode, type CustomNodeInnerData, type InputKeyItem, NodeType, type OutputKeyItem } from '@/components/agents/builder/types/Agents.ts';
import { getNodeStatus } from '@/components/agents/builder/utils/GraphUtils.ts';
import { env } from '@/constants/common/env.constants';
import { useGetAgentAppById } from '@/services/deploy/agent/agentDeploy.services';
import type { GetAgentAppByIdResponse, InputKey, OutputKey } from '@/services/deploy/agent/types';
import { useModal } from '@/stores/common/modal/useModal';
import { Handle, type NodeProps, Position, useUpdateNodeInternals } from '@xyflow/react';
import { useAtom } from 'jotai';
import React, { useEffect, useMemo, useRef, useState } from 'react';
import { SelectAgentApp } from '../contents/SelectAgentApp';
import { NodeFooter, NodeHeader } from './';
import { useNodeHandler } from '@/components/agents/builder/hooks/useNodeHandler';

export const AgentAppNode: React.FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  const { removeNode, toggleNodeView, syncNodeData, syncAllNodeKeyTable } = useGraphActions();
  const updateNodeInternals = useUpdateNodeInternals();

  const schemaData: AgentAppDataSchema = data as AgentAppDataSchema;
  const innerData: CustomNodeInnerData = data.innerData ?? {
    isRun: false,
    isDone: false,
    isError: false,
    isToggle: false,
  };

  useNodeTracing(id, data.name as string, data, innerData);

  const isRun = useMemo(() => innerData?.isRun ?? false, [innerData?.isRun]);
  const isDone = useMemo(() => innerData?.isDone ?? false, [innerData?.isDone]);
  const isError = useMemo(() => innerData?.isError ?? false, [innerData?.isError]);

  const [nodeStatus, setNodeStatus] = useState<string | null>(null);
  useEffect(() => {
    setNodeStatus(getNodeStatus(isRun, isDone, isError));
  }, [isRun, isDone, isError]);

  const [isChangeAgentApp, setChangeAgentApp] = useAtom(isChangeAgentAppAtom);
  const [selectedAgentAppIdRepo] = useAtom(selectedAgentAppIdRepoAtom);
  const [selectedAgentAppVersionRepo] = useAtom(selectedAgentAppVersionRepoAtom);
  const { openModal } = useModal();
  const [hasChatTested] = useAtom(hasChatTestedAtom);

  const nodesUpdatedRef = useRef(false);

  const isInitialMountRef = useRef(true);
  const prevDataRef = useRef(data);

  const [agentAppId, setAgentAppId] = useState<string | null>(selectedAgentAppIdRepo[id] !== undefined ? selectedAgentAppIdRepo[id] : schemaData?.agent_app_id);
  const [nodeName, setNodeName] = useState(schemaData.name as string);
  const [description, setDescription] = useState((schemaData.description as string) || '');
  const [apiKey] = useState<string>(env.VITE_API_KEY || '');

  const [deploymentVersion, setDeploymentVersion] = useState<number | undefined>((schemaData as any)?.deployment_version);

  const hasInitialAgent = Boolean(schemaData?.agent_app_id);

  const defaultInputKeys = useMemo<InputKeyItem[]>(
    () => [
      {
        name: 'query',
        required: true,
        keytable_id: null,
        fixed_value: null,
      },
    ],
    []
  );

  const defaultOutputKeys = useMemo<OutputKeyItem[]>(
    () => [
      {
        name: 'content',
        keytable_id: `content__${id}`,
      },
    ],
    [id]
  );

  const initialInputKeys = useMemo(() => {
    if (hasInitialAgent && Array.isArray(schemaData.input_keys)) {
      return schemaData.input_keys as InputKeyItem[];
    }
    return [];
  }, [hasInitialAgent, schemaData.input_keys]);

  const [inputKeys, setInputKeys] = useState<InputKeyItem[]>(initialInputKeys);

  const prevSchemaInputKeysRef = useRef<string>('');
  useEffect(() => {
    const schemaInputKeysStr = JSON.stringify(schemaData.input_keys);
    if (hasInitialAgent && Array.isArray(schemaData.input_keys) && schemaData.input_keys.length > 0 && prevSchemaInputKeysRef.current !== schemaInputKeysStr) {
      prevSchemaInputKeysRef.current = schemaInputKeysStr;

      const savedInputKeys = schemaData.input_keys as InputKeyItem[];

      const mergedInputKeys = savedInputKeys.map((savedKey: InputKeyItem) => {
        const currentKey = inputKeys.find(k => k.name === savedKey.name);
        if (savedKey.keytable_id && savedKey.keytable_id.trim() !== '') {
          return savedKey;
        }
        if (currentKey && currentKey.keytable_id && currentKey.keytable_id.trim() !== '') {
          return {
            ...savedKey,
            keytable_id: currentKey.keytable_id,
            fixed_value: currentKey.fixed_value ?? savedKey.fixed_value,
          };
        }
        return savedKey;
      });

      if (JSON.stringify(mergedInputKeys) !== JSON.stringify(inputKeys)) {
        setInputKeys(mergedInputKeys);
      }
    }
  }, [schemaData.input_keys, hasInitialAgent]);

  const initialOutputKeys = useMemo(() => {
    if (hasInitialAgent && Array.isArray(schemaData.output_keys)) {
      return schemaData.output_keys as OutputKeyItem[];
    }
    return [];
  }, [hasInitialAgent, schemaData.output_keys]);

  const [outputKeys, setOutputKeys] = useState<OutputKeyItem[]>(initialOutputKeys);

  const { data: agentAppData } = useGetAgentAppById(
    { appId: agentAppId || '' },
    {
      enabled: Boolean(agentAppId),
      staleTime: 0,
      gcTime: 0,
    }
  );

  useEffect(() => {
    const currentRepoValue = selectedAgentAppIdRepo[id];
    if (currentRepoValue !== undefined && currentRepoValue !== agentAppId) {
      setAgentAppId(currentRepoValue);
      nodesUpdatedRef.current = true;
    }
  }, [selectedAgentAppIdRepo, id, agentAppId]);

  useEffect(() => {
    const currentVersionValue = selectedAgentAppVersionRepo[id];
    if (currentVersionValue !== undefined && currentVersionValue !== deploymentVersion) {
      setDeploymentVersion(currentVersionValue);
      nodesUpdatedRef.current = true;
    }
  }, [selectedAgentAppVersionRepo, id, deploymentVersion]);

  useEffect(() => {
    syncAllNodeKeyTable();
    syncGenData();
  }, [inputKeys]);

  useEffect(() => {
    if (nodesUpdatedRef.current) {
      syncGenData();
      nodesUpdatedRef.current = false;
    }
  }, [nodeName, description, outputKeys]);

  useEffect(() => {
    if (isChangeAgentApp) {
      setChangeAgentApp(false);
      syncGenData();
      nodesUpdatedRef.current = true;
    }
  }, [isChangeAgentApp, setChangeAgentApp]);
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

    const newInputKeys = Array.isArray(data.input_keys) ? (data.input_keys as InputKeyItem[]) : [];
    const newOutputKeys = Array.isArray(data.output_keys) ? (data.output_keys as OutputKeyItem[]) : [];

    const savedInputKeysCount = newInputKeys.length;
    const currentInputKeysCount = inputKeys.length;

    const inputKeysContentEqual =
      savedInputKeysCount === currentInputKeysCount &&
      newInputKeys.every((savedKey, idx) => {
        if (!savedKey || typeof savedKey !== 'object' || !savedKey.name) {
          return false;
        }
        const currentKey = inputKeys[idx];
        return (
          currentKey &&
          typeof currentKey === 'object' &&
          currentKey.name === savedKey.name &&
          currentKey.required === savedKey.required &&
          (currentKey.keytable_id || '') === (savedKey.keytable_id || '') &&
          (currentKey.fixed_value || null) === (savedKey.fixed_value || null)
        );
      });
    if (savedInputKeysCount > 0 && !inputKeysContentEqual && savedInputKeysCount >= currentInputKeysCount) {
      const finalKeys = [...newInputKeys];

      finalKeys.forEach((newKey, index) => {
        if (!newKey || typeof newKey !== 'object' || !newKey.name) {
          return;
        }
        const existingKey = inputKeys.find(ek => ek && typeof ek === 'object' && ek.name === newKey.name);
        if (existingKey && (existingKey.keytable_id || existingKey.fixed_value)) {
          finalKeys[index] = {
            ...newKey,
            keytable_id: existingKey.keytable_id || newKey.keytable_id || '',
            fixed_value: existingKey.fixed_value || newKey.fixed_value || null,
          };
        }
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
        if (!savedKey || typeof savedKey !== 'object' || !savedKey.name) {
          return false;
        }
        const currentKey = outputKeys[idx];
        return currentKey && typeof currentKey === 'object' && currentKey.name === savedKey.name;
      });

    if (newOutputKeys.length > 0 && !outputKeysContentEqual) {
      setOutputKeys(newOutputKeys);
    }

    prevDataRef.current = data;
  }, [data, data.input_keys, data.output_keys]);

  const prevAgentAppIdRef = useRef<string | null>(null);
  const prevAgentAppDataRef = useRef<any>(null);

  useEffect(() => {
    if (agentAppId !== prevAgentAppIdRef.current || agentAppData !== prevAgentAppDataRef.current) {
      prevAgentAppIdRef.current = agentAppId;
      prevAgentAppDataRef.current = agentAppData;

      if (agentAppData && agentAppId && agentAppId !== '') {
        const appResponse = agentAppData as GetAgentAppByIdResponse;
        const deployments = appResponse.deployments ?? [];
        const preferredVersion = deploymentVersion ?? appResponse.deploymentVersion;

        const selectedDeployment =
          deployments.find(deploy => deploy.version === preferredVersion) ||
          deployments.find(deploy => (deploy.inputKeys?.length ?? 0) > 0 || (deploy.outputKeys?.length ?? 0) > 0) ||
          deployments[0];

        const inputKeysSource: InputKey[] =
          (Array.isArray(agentAppData.inputKeys) && agentAppData.inputKeys.length > 0 ? agentAppData.inputKeys : selectedDeployment?.inputKeys) || [];
        const existingInputKeysMap = new Map<string, InputKeyItem>();

        if (Array.isArray(schemaData.input_keys)) {
          schemaData.input_keys.forEach((key: InputKeyItem) => {
            if (key.name && key.keytable_id && key.keytable_id.trim() !== '') {
              existingInputKeysMap.set(key.name, key);
            }
          });
        }

        if (Array.isArray(inputKeys)) {
          inputKeys.forEach((key: InputKeyItem) => {
            if (key.name && key.keytable_id && key.keytable_id.trim() !== '') {
              existingInputKeysMap.set(key.name, key);
            }
          });
        }

        const resolvedInputKeys: InputKeyItem[] =
          inputKeysSource.length > 0
            ? inputKeysSource.map((key: InputKey) => {
              const existingKey = existingInputKeysMap.get(key.name);
              const preservedKeytableId = existingKey?.keytable_id || null;
              const preservedFixedValue = existingKey?.fixed_value || null;

              return {
                name: key.name,
                required: key.required || false,
                keytable_id: preservedKeytableId,
                fixed_value: preservedFixedValue || (key.fixedValue ?? null),
              };
            }) : defaultInputKeys;
        const hasChanges =
          resolvedInputKeys.length !== inputKeys.length ||
          resolvedInputKeys.some((newKey, index) => {
            const currentKey = inputKeys[index];
            if (!currentKey) return true;
            if (currentKey.name !== newKey.name || currentKey.required !== newKey.required) {
              return true;
            }
            if (currentKey.keytable_id && !newKey.keytable_id) {
              return false;
            }
            return currentKey.keytable_id !== newKey.keytable_id;
          });

        if (hasChanges) {
          setInputKeys(resolvedInputKeys);
        }

        const outputKeysSource: OutputKey[] =
          (Array.isArray(agentAppData.outputKeys) && agentAppData.outputKeys.length > 0 ? agentAppData.outputKeys : selectedDeployment?.outputKeys) || [];

        const resolvedOutputKeys: OutputKeyItem[] =
          outputKeysSource.length > 0
            ? outputKeysSource.map((key: OutputKey, index: number) => ({
              name: key.name || key.keytableId || `output_${index + 1}`,
              keytable_id: key.keytableId || `${key.name || `output_${index + 1}`}__${id}`, // output은 자동 생성 허용
            }))
            : defaultOutputKeys;
        setOutputKeys(resolvedOutputKeys);

        nodesUpdatedRef.current = true;
      } else {
        setInputKeys([]);
        setOutputKeys([]);
      }
    }
  }, [agentAppData, agentAppId, id, defaultInputKeys, defaultOutputKeys, deploymentVersion]);

  const syncGenData = () => {
    const currentAgentAppId = selectedAgentAppIdRepo[id];
    const currentDeploymentVersion = selectedAgentAppVersionRepo[id];
    const finalAgentAppId = currentAgentAppId === null ? '' : currentAgentAppId || agentAppId || '';
    const finalDeploymentVersion = currentDeploymentVersion === null ? undefined : currentDeploymentVersion !== undefined ? currentDeploymentVersion : deploymentVersion;

    const newData = {
      ...data,
      type: NodeType.AgentApp.name,
      id: id,
      name: nodeName,
      description: description,
      agent_app_id: finalAgentAppId,
      deployment_version: finalDeploymentVersion,
      input_keys:
        inputKeys?.map(key => ({
          ...key,
          fixed_value: key.fixed_value || '',
          keytable_id: key.keytable_id ?? '',
        })) || [],
      api_key: apiKey || '',
      output_keys: outputKeys || [],
      innerData: innerData,
    };

    syncNodeData(id, newData);
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

  const onClickDelete = () => {
    removeNode(id);
  };

  const handleFooterFold = (bool: boolean) => {
    toggleNodeView(id, bool);
  };

  const handleNodeNameChange = (value: string) => {
    setNodeName(value);
    nodesUpdatedRef.current = true;
  };

  const handleDescriptionChange = (value: string) => {
    setDescription(value);
    nodesUpdatedRef.current = true;
  };

  useEffect(() => {
    updateNodeInternals(id);
  }, [id, updateNodeInternals, innerData.isToggle]);

  const containerRef = useAutoUpdateNodeInternals(id);
  const { autoResize, stopPropagation, preventAndStop } = useNodeHandler();

  return (
    <div ref={containerRef}>
      <Card className={['agent-card w-full min-w-[500px] max-w-[500px]', nodeStatus].filter(e => !!e).join(' ')}>
        <Handle
          type='target'
          id='gen_left'
          key={`gen_left_${innerData.isToggle}_${Date.now()}`}
          position={Position.Left}
          isConnectable={true}
          style={{
            width: 20,
            height: 20,
            top: '50%',
            transform: 'translateY(-50%)',
            left: -10,
            border: '2px solid white',
            background: '#000000',
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

              <hr className='border-gray-200' />

              <CardBody>
                <Accordion>
                  <SelectAgentApp
                    selectedAgentAppId={agentAppId}
                    nodeId={id}
                    nodeType={NodeType.AgentApp.name}
                    asAccordionItem={true}
                    title={
                      <>
                        {'Agent'}
                        <span className='ag-color-red'>*</span>
                      </>
                    }
                  />
                </Accordion>
              </CardBody>

              <div className='bg-gray-50 px-4 py-3 border-t border-gray-200'>
                <h3 className='text-lg font-semibold text-gray-700'>Schema</h3>
              </div>

              <CustomScheme
                id={id}
                inputKeys={inputKeys}
                setInputKeys={setInputKeys}
                inputValues={inputKeys.map(item => item.name)}
                setInputValues={(_values: string[]) => { }}
                innerData={data.innerData}
                outputKeys={outputKeys}
                type={NodeType.AgentApp.name}
                readOnly={false}
                lockedInputKeys={['query']}
              />
            </>
          )}
        </>

        <CardFooter>
          <NodeFooter onClick={handleFooterFold} isToggle={innerData.isToggle as boolean} />
        </CardFooter>
        <Handle
          type='source'
          id='gen_right'
          key={`gen_right_${innerData.isToggle}_${Date.now()}`}
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
