// noinspection DuplicatedCode

import { isChangePromptAtom, selectedPromptIdRepoAtom } from '@/components/agents/builder/atoms/AgentAtom';
import { selectedLLMRepoAtom } from '@/components/agents/builder/atoms/llmAtom';
import { hasChatTestedAtom } from '@/components/agents/builder/atoms/logAtom.ts';
import { Accordion } from '@/components/agents/builder/common/accordion/Accordion.tsx';
import { Card, CardBody, CardFooter } from '@/components/agents/builder/common/index.ts';
import { LogModal } from '@/components/agents/builder/common/modal/log/LogModal.tsx';
import { useGraphActions } from '@/components/agents/builder/hooks/useGraphActions.ts';
import { CustomScheme } from '@/components/agents/builder/pages/graph/contents/CustomScheme.tsx';
import { SelectLLM } from '@/components/agents/builder/pages/graph/contents/SelectLLM.tsx';
import { SelectPrompt } from '@/components/agents/builder/pages/graph/contents/SelectPrompt.tsx';
import { type CompressorDataSchema, type CustomNode, type CustomNodeInnerData, type InputKeyItem, NodeType, type OutputKeyItem } from '@/components/agents/builder/types/Agents';
import { Handle, type NodeProps, Position, useUpdateNodeInternals } from '@xyflow/react';
import { useAutoUpdateNodeInternals } from '@/components/agents/builder/hooks/useAutoUpdateNodeInternals';
import { getNodeStatus } from '@/components/agents/builder/utils/GraphUtils.ts';
import { useNodeTracing } from '@/components/agents/builder/hooks/useNodeTracing';
import { useModal } from '@/stores/common/modal/useModal';
import { useAtom } from 'jotai';
import { type FC, useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { NodeFooter, NodeHeader } from '.';
import { useNodeHandler } from '@/components/agents/builder/hooks/useNodeHandler';

interface NodeFormState {
  nodeName: string;
  description: string;
  servingName: string;
  servingModel: string;
  prompt: string;
  inputKeys: InputKeyItem[];
  outputKeys: OutputKeyItem[];
}

export const CompressorNode: FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  const [selectedPromptIdRepo] = useAtom(selectedPromptIdRepoAtom);
  const [isChangePrompt, setChangePrompt] = useAtom(isChangePromptAtom);
  const [selectedLLMRepo] = useAtom(selectedLLMRepoAtom);
  const [hasChatTested] = useAtom(hasChatTestedAtom);
  const { removeNode, toggleNodeView, syncNodeData } = useGraphActions();
  const updateNodeInternals = useUpdateNodeInternals();
  const { openModal } = useModal();

  const newInnerData: CustomNodeInnerData = {
    isRun: false,
    isDone: false,
    isError: false,
    isToggle: false,
  };

  const nodeData: CustomNodeInnerData = data.innerData ?? newInnerData;
  useNodeTracing(id, data.name as string, data, nodeData);

  const isRun = useMemo(() => nodeData?.isRun ?? false, [nodeData?.isRun]);
  const isDone = useMemo(() => nodeData?.isDone ?? false, [nodeData?.isDone]);
  const isError = useMemo(() => nodeData?.isError ?? false, [nodeData?.isError]);

  const [nodeStatus, setNodeStatus] = useState<string | null>(null);
  useEffect(() => {
    setNodeStatus(getNodeStatus(isRun, isDone, isError));
  }, [isRun, isDone, isError]);
  const schemaData: CompressorDataSchema = data as CompressorDataSchema;

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

  const nodesUpdatedRef = useRef(false);
  const initialSyncDone = useRef(false);
  const [promptId, setPromptId] = useState<string | null>((schemaData?.context_refiner?.llm_chain?.prompt as string) || '');

  const servingNameFromData = (data.serving_name as string) || '';
  const servingNameFromSchema = (schemaData?.context_refiner?.llm_chain?.llm_config?.serving_name as string) || '';
  const servingModelFromData = (data.serving_model as string) || '';
  const servingModelFromSchema = ((schemaData?.context_refiner?.llm_chain?.llm_config as any)?.serving_model as string) || '';

  const finalServingName = servingNameFromData || servingNameFromSchema;
  const finalServingModel = servingModelFromData || servingModelFromSchema;

  const [servingName, setServingName] = useState(finalServingName);
  const [servingModel, setServingModel] = useState(finalServingModel);

  const [formState, setFormState] = useState<NodeFormState>({
    nodeName: data.name as string,
    description: (data.description as string) || '',
    servingName: finalServingName,
    servingModel: finalServingModel,
    prompt: promptId || '',
    inputKeys: (data.input_keys as InputKeyItem[]) || [],
    outputKeys: (data.output_keys as OutputKeyItem[]) || [],
  });

  const [inputValues, setInputValues] = useState<string[]>(formState.inputKeys.map(item => item.name));

  const syncCurrentData = useCallback(() => {
    const currentPromptId = selectedPromptIdRepo[id];
    const currentLLM = selectedLLMRepo[id];

    const finalPrompt = currentPromptId === null ? '' : (currentPromptId || formState.prompt || '');
    const finalServingName = currentLLM?.servingName || formState.servingName || '';
    const finalServingModel = currentLLM?.servingModel || formState.servingModel || '';

    const newData = {
      ...data,
      id: id,
      name: formState.nodeName,
      description: formState.description,
      input_keys: formState.inputKeys,
      output_keys: formState.outputKeys,
      serving_name: finalServingName,
      serving_model: finalServingModel,
      retriever_id: null,
      kind: 'contextual_compression',
      context_refiner: {
        llm_chain: {
          llm_config: {
            api_key: '',
            serving_name: finalServingName,
            serving_model: finalServingModel,
          },
          prompt: typeof finalPrompt === 'string' && finalPrompt.trim() !== '' ? finalPrompt : '',
        },
      },
      innerData: {
        ...nodeData,
      },
    };

    syncNodeData(id, newData);
  }, [data, id, nodeData, syncNodeData, formState, selectedPromptIdRepo, selectedLLMRepo]);

  useEffect(() => {
    if (nodesUpdatedRef.current) {
      syncCurrentData();
      nodesUpdatedRef.current = false;
    }
  }, [syncCurrentData]);

  useEffect(() => {
    if (!initialSyncDone.current) {
      syncCurrentData();
      initialSyncDone.current = true;
    }
  }, [syncCurrentData, formState.servingName, formState.servingModel]);

  useEffect(() => {
    const currentServingName = (data.serving_name as string) ||
      (schemaData.context_refiner?.llm_chain?.llm_config?.serving_name as string) || '';
    const currentServingModel = (data.serving_model as string) ||
      ((schemaData.context_refiner?.llm_chain?.llm_config as any)?.serving_model as string) || '';

    if (servingName !== currentServingName || servingModel !== currentServingModel) {
      setServingName(currentServingName);
      setServingModel(currentServingModel);
      setFormState(prev => ({
        ...prev,
        servingName: currentServingName,
        servingModel: currentServingModel,
      }));
    }
  }, [data.serving_name, data.serving_model, schemaData.context_refiner]);

  useEffect(() => {
    updateNodeInternals(id);
  }, [id, updateNodeInternals, nodeData.isToggle]);

  const handleFieldChange = (field: keyof NodeFormState, value: any) => {
    setFormState(prev => ({
      ...prev,
      [field]: value,
    }));
    nodesUpdatedRef.current = true;
  };

  useEffect(() => {
    if (isChangePrompt) {
      handleFieldChange('prompt', selectedPromptIdRepo[id]);
      setPromptId(selectedPromptIdRepo[id]);
      setChangePrompt(false);
    }
  }, [isChangePrompt, id]);

  const handleInputKeysChange = useCallback((newInputKeys: InputKeyItem[]) => {
    handleFieldChange('inputKeys', newInputKeys);
  }, []);

  const handleFooterFold = (isFold: boolean) => {
    toggleNodeView(id, isFold);
  };

  const handleDelete = () => {
    removeNode(id);
  };

  useEffect(() => {
    if (servingName !== formState.servingName || servingModel !== formState.servingModel) {
      setFormState(prev => ({
        ...prev,
        servingName: servingName,
        servingModel: servingModel,
      }));
      nodesUpdatedRef.current = true;
    }
  }, [servingName, servingModel]);

  const handleLLMChange = useCallback((selectedLLM: any) => {
    const llmName = selectedLLM.name || selectedLLM.modelName || '';
    const servingId = selectedLLM.servingId || '';

    handleFieldChange('servingName', llmName);
    handleFieldChange('servingModel', servingId);
    setServingName(llmName);
    setServingModel(servingId);
    nodesUpdatedRef.current = true;
  }, [handleFieldChange]);

  useEffect(() => {
    if (nodesUpdatedRef.current) {
      syncCurrentData();
      nodesUpdatedRef.current = false;
    }
  }, [formState]);

  const handleNodeNameChange = useCallback((value: string) => {
    handleFieldChange('nodeName', value);
    nodesUpdatedRef.current = true;
  }, []);

  const handleDescriptionChange = useCallback((value: string) => {
    handleFieldChange('description', value);
    nodesUpdatedRef.current = true;
  }, []);

  const containerRef = useAutoUpdateNodeInternals(id);
  const { autoResize, stopPropagation, preventAndStop } = useNodeHandler();

  return (
    <div ref={containerRef}>
      <Card className={['agent-card w-full min-w-[500px] max-w-[500px]', nodeStatus].filter(e => !!e).join(' ')}>
        <Handle
          type='target'
          id='compressor_left'
          key={`compressor_left_${nodeData.isToggle}`}
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
          data={nodeData}
          defaultValue={formState.nodeName}
          onClickDelete={handleDelete}
          onChange={handleNodeNameChange}
          onClickLog={handleHeaderClickLog}
        />
        <>
          {nodeData.isToggle && (
            <div className='bg-white px-4 py-4 border-b border-gray-200'>
              <label className='block font-semibold text-sm text-gray-700 mb-2'>{'설명'}</label>
              <div className='relative'>
                <textarea
                  className='nodrag w-full resize-none border border-gray-300 rounded-lg p-3 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500'
                  style={{
                    minHeight: '80px',
                    maxHeight: '100px',
                    height: 'auto',
                    overflow: 'hidden'
                  }}
                  placeholder={'설명 입력'}
                  value={formState.description}
                  onChange={e => {
                    const value = e.target.value;
                    if (value.length <= 100) {
                      handleDescriptionChange(value);
                    }
                    autoResize(e.target);
                  }}
                  onInput={(e: any) => {
                    e.target.style.height = 'auto';
                    e.target.style.height = e.target.scrollHeight + 'px';
                  }}
                  maxLength={100}
                  onMouseDown={stopPropagation}
                  onMouseUp={stopPropagation}
                  onSelect={stopPropagation}
                  onDragStart={preventAndStop}
                  onDrag={preventAndStop}
                />
                <div className='absolute bottom-2 right-2 text-xs text-gray-500'>
                  <span className='text-blue-500'>{formState.description.length}</span>/100
                </div>
              </div>
            </div>
          )}

          {!nodeData.isToggle && (
            <>
              <CardBody className='p-4'>
                <div className='mb-4'>
                  <label className='block font-semibold text-sm text-gray-700 mb-2'>{'설명'}</label>
                  <div className='relative'>
                    <textarea
                      className='nodrag w-full resize-none border border-gray-300 rounded-lg p-3 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500'
                      rows={3}
                      placeholder={'설명 입력'}
                      value={formState.description}
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
                      <span className='text-blue-500'>{formState.description.length}</span>/100
                    </div>
                  </div>
                </div>

                <hr className='border-gray-200' />

                <Accordion>
                  <SelectLLM
                    selectedServingName={servingName}
                    selectedServingModel={servingModel}
                    onChange={handleLLMChange}
                    asAccordionItem={true}
                    title={
                      <>
                        {'LLM'}
                        <span className='text-red-500'>*</span>
                      </>
                    }
                  />
                  <SelectPrompt selectedPromptId={promptId} nodeId={id} nodeType={NodeType.RetrieverCompressor.name} asAccordionItem={true} title='프롬프트' />
                </Accordion>
              </CardBody>

              <div className='bg-gray-50 px-4 py-3 border-t border-gray-200'>
                <h3 className='text-lg font-semibold text-gray-700'>Schema</h3>
              </div>

              <CustomScheme
                id={id}
                inputKeys={formState.inputKeys}
                setInputKeys={handleInputKeysChange}
                inputValues={inputValues}
                setInputValues={setInputValues}
                innerData={data.innerData}
                outputKeys={formState.outputKeys}
                type={NodeType.RetrieverCompressor.name}
              />
            </>
          )}
        </>

        <CardFooter>
          <NodeFooter onClick={handleFooterFold} isToggle={nodeData.isToggle} />
        </CardFooter>
        <Handle
          type='source'
          id='compressor_right'
          key={`compressor_right_${nodeData.isToggle}`}
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
