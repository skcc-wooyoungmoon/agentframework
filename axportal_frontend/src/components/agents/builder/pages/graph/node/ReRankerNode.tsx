import { Accordion } from '@/components/agents/builder/common/accordion/Accordion.tsx';
import { Card, CardBody, CardFooter } from '@/components/agents/builder/common/index.ts';
import { useGraphActions } from '@/components/agents/builder/hooks/useGraphActions.ts';
import { CustomScheme } from '@/components/agents/builder/pages/graph/contents/CustomScheme.tsx';
import { SelectLLM } from '@/components/agents/builder/pages/graph/contents/SelectLLM.tsx';
import { type CustomNode, type CustomNodeInnerData, type InputKeyItem, NodeType, type OutputKeyItem, type ReRankerDataSchema } from '@/components/agents/builder/types/Agents';
import { Handle, type NodeProps, Position, useUpdateNodeInternals } from '@xyflow/react';
import { useAutoUpdateNodeInternals } from '@/components/agents/builder/hooks/useAutoUpdateNodeInternals';
import { getNodeStatus } from '@/components/agents/builder/utils/GraphUtils.ts';
import { useNodeTracing } from '@/components/agents/builder/hooks/useNodeTracing';
import { type FC, useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { NodeFooter, NodeHeader } from '.';
import { useAtom } from 'jotai';
import { hasChatTestedAtom } from '@/components/agents/builder/atoms/logAtom.ts';
import { selectedLLMRepoAtom, isChangeLLMAtom } from '@/components/agents/builder/atoms/llmAtom';
import { LogModal } from '@/components/agents/builder/common/modal/log/LogModal.tsx';
import { useModal } from '@/stores/common/modal/useModal';
import { useNodeHandler } from '@/components/agents/builder/hooks/useNodeHandler';

interface NodeFormState {
  nodeName: string;
  description: string;
  servingName: string;
  servingModel: string;
  inputKeys: InputKeyItem[];
  outputKeys: OutputKeyItem[];
}

export const ReRankerNode: FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  const { removeNode, toggleNodeView, syncNodeData } = useGraphActions();
  const updateNodeInternals = useUpdateNodeInternals();
  const [selectedLLMRepo] = useAtom(selectedLLMRepoAtom);
  const [isChangeLLM, setChangeLLM] = useAtom(isChangeLLMAtom);
  const [hasChatTested] = useAtom(hasChatTestedAtom);
  const { openModal } = useModal();

  const [formState, setFormState] = useState<NodeFormState>(() => {
    const contextRefiner = (data as ReRankerDataSchema).context_refiner;
    return {
      nodeName: data.name as string,
      description: (data.description as string) || '',
      servingName: contextRefiner?.rerank_cnf?.model_info?.serving_name ?? '',
      servingModel: contextRefiner?.rerank_cnf?.model_info?.serving_model ?? '',
      inputKeys: (data.input_keys as InputKeyItem[]) || [],
      outputKeys: (data.output_keys as OutputKeyItem[]) || [],
    };
  });

  const [inputValues, setInputValues] = useState<string[]>(formState.inputKeys.map(item => item.name));

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
  const nodesUpdatedRef = useRef(false);

  const syncCurrentData = useCallback(() => {
    const currentLLM = selectedLLMRepo[id];
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
      kind: 'rerank',
      context_refiner: {
        rerank_cnf: {
          model_info: {
            serving_name: finalServingName,
            serving_model: finalServingModel,
          },
        },
      },
      innerData: {
        ...nodeData,
      },
    };
    syncNodeData(id, newData);
  }, [data, id, nodeData, syncNodeData, formState, selectedLLMRepo]);

  useEffect(() => {
    if (nodesUpdatedRef.current) {
      syncCurrentData();
      nodesUpdatedRef.current = false;
    }
  }, [syncCurrentData]);

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
    if (!isChangeLLM) return;

    const currentLLM = selectedLLMRepo[id];
    if (currentLLM) {
      const currentServingName = currentLLM.servingName || '';
      const currentServingModel = currentLLM.servingModel || '';

      if (currentServingName !== formState.servingName || currentServingModel !== formState.servingModel) {
        handleFieldChange('servingName', currentServingName);
        handleFieldChange('servingModel', currentServingModel);
        nodesUpdatedRef.current = true;
      }
      setChangeLLM(false);
    } else if (currentLLM === null) {
      if (formState.servingName || formState.servingModel) {
        handleFieldChange('servingName', '');
        handleFieldChange('servingModel', '');
        nodesUpdatedRef.current = true;
      }
      setChangeLLM(false);
    }
  }, [isChangeLLM]);

  const handleInputKeysChange = useCallback((newInputKeys: InputKeyItem[]) => {
    handleFieldChange('inputKeys', newInputKeys);
  }, []);

  const handleNodeNameChange = useCallback((value: string) => {
    handleFieldChange('nodeName', value);
  }, []);

  const handleDescriptionChange = useCallback((value: string) => {
    handleFieldChange('description', value);
  }, []);

  const handleFooterFold = (isFold: boolean) => {
    toggleNodeView(id, isFold);
  };

  const handleDelete = () => {
    removeNode(id);
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

  const handleLLMChange = useCallback((selectedLLM: any) => {
    handleFieldChange('servingName', selectedLLM.model_name as string);
    handleFieldChange('servingModel', selectedLLM.serving_id as string);
    nodesUpdatedRef.current = true;
  }, []);

  const containerRef = useAutoUpdateNodeInternals(id);
  const { autoResize, stopPropagation, preventAndStop } = useNodeHandler();
  return (
    <div ref={containerRef}>
      <Card className={['agent-card w-full min-w-[500px] max-w-[500px]', nodeStatus].filter(e => !!e).join(' ')}>
        <Handle
          type='target'
          id={`rerank_left_${id}`}
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
                    overflow: 'hidden',
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

                <div className='mb-0 w-auto'>
                  <Accordion>
                    <SelectLLM
                      selectedServingName={formState.servingName}
                      selectedServingModel={formState.servingModel}
                      onChange={handleLLMChange}
                      isReRanker={true}
                      nodeId={id}
                      asAccordionItem={true}
                      title={
                        <>
                          <label className={`form-label items-center gap-1 max-w-56`}>
                            Re-Rank 모델
                            <span className='ag-color-red ml-0.5'>*</span>
                          </label>
                        </>
                      }
                    />
                  </Accordion>
                </div>
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
                type={NodeType.RetrieverReRanker.name}
              />
            </>
          )}
        </>

        <CardFooter>
          <NodeFooter onClick={handleFooterFold} isToggle={nodeData.isToggle} />
        </CardFooter>
        <Handle
          type='source'
          id={`rerank_right_${id}`}
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
