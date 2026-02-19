import { isChangeToolAtom } from '@/components/agents/builder/atoms/AgentAtom';
import { hasChatTestedAtom } from '@/components/agents/builder/atoms/logAtom.ts';
import { selectedAtom } from '@/components/agents/builder/atoms/toolsAtom.ts';
import { Accordion } from '@/components/agents/builder/common/accordion/Accordion.tsx';
import { Card, CardBody, CardFooter } from '@/components/agents/builder/common/index.ts';
import { LogModal } from '@/components/agents/builder/common/modal/log/LogModal.tsx';
import { useGraphActions } from '@/components/agents/builder/hooks/useGraphActions.ts';
import { useModal } from '@/stores/common/modal/useModal';
import { CustomScheme } from '@/components/agents/builder/pages/graph/contents/CustomScheme.tsx';
import { SelectSingleTool } from '@/components/agents/builder/pages/graph/contents/SelectTools.tsx';
import { type CustomNode, type CustomNodeInnerData, type InputKeyItem, NodeType, type OutputKeyItem } from '@/components/agents/builder/types/Agents';
import { type Tool, type ToolInputKey } from '@/components/agents/builder/types/Tools.ts';
import { Handle, type NodeProps, Position, useUpdateNodeInternals } from '@xyflow/react';
import { useAutoUpdateNodeInternals } from '@/components/agents/builder/hooks/useAutoUpdateNodeInternals';
import { getNodeStatus } from '@/components/agents/builder/utils/GraphUtils.ts';
import { useNodeTracing } from '@/components/agents/builder/hooks/useNodeTracing';
import { useAtom } from 'jotai/index';
import { type FC, useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { NodeFooter, NodeHeader } from '.';
import { useNodeHandler } from '@/components/agents/builder/hooks/useNodeHandler';

interface NodeFormState {
  type: string;
  id: string;
  name: string;
  description: string;
  tool_id: string;
  input_keys: InputKeyItem[];
  output_keys: OutputKeyItem[];
}

export const ToolNode: FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  const { removeNode, toggleNodeView, syncNodeData } = useGraphActions();
  const updateNodeInternals = useUpdateNodeInternals();

  const [formState, setFormState] = useState<NodeFormState>({
    type: NodeType.Tool.name,
    id: id,
    name: data.name as string,
    description: (data.description as string) || '',
    tool_id: (data.tool_id as string) || '',
    input_keys: (data.input_keys as InputKeyItem[]) || [],
    output_keys: (data.output_keys as OutputKeyItem[]) || [],
  });

  const [inputValues, setInputValues] = useState<string[]>(formState.input_keys.map(item => item.name));

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

  const [isChangeTool, setChangeTool] = useAtom(isChangeToolAtom);
  const [selectedToolRepo] = useAtom<Record<string, Tool>>(selectedAtom);
  const [hasChatTested] = useAtom(hasChatTestedAtom);

  const { openModal } = useModal();

  const syncCurrentData = () => {
    const currentTool = selectedToolRepo[id];
    const finalToolId = currentTool === null ? '' : currentTool?.id || formState.tool_id || '';

    const newData = {
      ...data,
      type: NodeType.Tool.name,
      id: id,
      name: formState.name,
      description: formState.description,
      input_keys: formState.input_keys,
      output_keys: formState.output_keys,
      tool_id: finalToolId,
      innerData: {
        ...nodeData,
      },
    };
    syncNodeData(id, newData);
  };

  useEffect(() => {
    syncCurrentData();
  }, [formState, formState.input_keys, formState.output_keys]);

  useEffect(() => {
    updateNodeInternals(id);
  }, [id, updateNodeInternals, nodeData.isToggle]);

  useEffect(() => {
    if (isChangeTool && selectedToolRepo[id] !== undefined) {
      const currentSelectedTool = selectedToolRepo[id];
      if (currentSelectedTool === null) {
        setFormState(prev => ({
          ...prev,
          tool_id: '',
          input_keys: [],
        }));
        setInputValues([]);
        syncCurrentData();
        setChangeTool(false);
        return;
      }

      const convertInputKeys = convertToolInputKeys(currentSelectedTool.input_keys ?? []);
      setFormState(prev => ({
        ...prev,
        tool_id: currentSelectedTool.id,
        input_keys: convertInputKeys,
      }));
      setInputValues(convertInputKeys.map(item => item.name));
      syncCurrentData();
      setChangeTool(false);
    }
  }, [isChangeTool]);

  const convertToolInputKeys = (toolInputKeys: ToolInputKey[]): InputKeyItem[] => {
    const result = [] as InputKeyItem[];

    toolInputKeys.forEach(key => {
      result.push({
        name: key.key,
        required: key.required,
        keytable_id: null,
        fixed_value: key.default_value,
      });
    });

    return result;
  };

  const handleFieldChange = (field: keyof NodeFormState, value: any) => {
    setFormState(prev => ({
      ...prev,
      [field]: value,
    }));
    nodesUpdatedRef.current = true;
  };

  const handleInputKeysChange = useCallback((newInputKeys: InputKeyItem[]) => {
    handleFieldChange('input_keys', newInputKeys);
  }, []);

  const handleNodeNameChange = useCallback((value: string) => {
    handleFieldChange('name', value);
  }, []);

  const handleDescriptionChange = useCallback((value: string) => {
    handleFieldChange('description', value);
  }, []);

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

  const containerRef = useAutoUpdateNodeInternals(id);
  const { autoResize, stopPropagation, preventAndStop } = useNodeHandler();

  return (
    <div ref={containerRef}>
      <Card className={['agent-card w-full min-w-[500px] max-w-[500px]', nodeStatus].filter(e => !!e).join(' ')}>
        <Handle
          type='target'
          id='tool_left'
          key={`tool_left_${nodeData.isToggle}`}
          position={Position.Left}
          isConnectable={true}
          style={{
            width: 20,
            height: 20,
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
          defaultValue={formState.name}
          onClickDelete={handleDelete}
          onClickLog={handleHeaderClickLog}
          onChange={handleNodeNameChange}
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

                <hr className='border-gray-200' />

                <Accordion>
                  <SelectSingleTool
                    toolId={formState.tool_id}
                    nodeId={id}
                    asAccordionItem={true}
                    title={
                      <>
                        {'도구'}
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
                inputKeys={formState.input_keys}
                setInputKeys={handleInputKeysChange}
                inputValues={inputValues}
                setInputValues={setInputValues}
                innerData={data.innerData}
                outputKeys={formState.output_keys}
                type={formState.type}
              />
            </>
          )}
        </>

        <CardFooter>
          <NodeFooter onClick={handleFooterFold} isToggle={nodeData.isToggle} />
        </CardFooter>
        <Handle
          type='source'
          id='tool_right'
          key={`tool_right_${nodeData.isToggle}`}
          position={Position.Right}
          isConnectable={true}
          style={{
            width: 20,
            height: 20,
            top: '50%',
            transform: 'translateY(-50%)',
            right: -10,
            zIndex: 20,
          }}
        />
      </Card>
    </div>
  );
};
