import { hasChatTestedAtom } from '@/components/agents/builder/atoms/logAtom.ts';
import { Card, CardBody, CardFooter } from '@/components/agents/builder/common/index.ts';
import { LogModal } from '@/components/agents/builder/common/modal/log/LogModal.tsx';
import { useAutoUpdateNodeInternals } from '@/components/agents/builder/hooks/useAutoUpdateNodeInternals';
import { useGraphActions } from '@/components/agents/builder/hooks/useGraphActions.ts';
import { useModal } from '@/stores/common/modal/useModal';
import { useNodeValidation } from '@/components/agents/builder/hooks/useNodeValidation.ts';
import { useNodeTracing } from '@/components/agents/builder/hooks/useNodeTracing';
import { type CustomNode, type CustomNodeInnerData, type InputKeyItem, type InputNodeDataSchema, NodeType } from '@/components/agents/builder/types/Agents';
import { Handle, type NodeProps, Position, useUpdateNodeInternals } from '@xyflow/react';
import { getNodeStatus } from '@/components/agents/builder/utils/GraphUtils.ts';
import { useAtom } from 'jotai/index';
import React, { useEffect, useMemo, useRef, useState } from 'react';
import { NodeFooter, NodeHeader } from '.';
import { useNodeHandler } from '@/components/agents/builder/hooks/useNodeHandler';

export const InputNode: React.FC<NodeProps<CustomNode>> = ({ data, id, type }: NodeProps<CustomNode>) => {
  const { nodes, toggleNodeView } = useGraphActions();
  const { removeNode, syncNodeData } = useGraphActions();
  const updateNodeInternals = useUpdateNodeInternals();
  const { validateNode, getValidation } = useNodeValidation();
  const { openModal } = useModal();
  const [hasChatTested] = useAtom(hasChatTestedAtom);
  const validation = getValidation(id, 'input');

  const newInnerData: CustomNodeInnerData = {
    isRun: false,
    isToggle: false,
    logData: [],
  };

  const innerData: CustomNodeInnerData = data.innerData ?? newInnerData;

  useNodeTracing(id, data.name as string, data, innerData);

  const [nodeStatus, setNodeStatus] = useState<string | null>(null);
  useEffect(() => {
    const currentInnerData = data.innerData ?? newInnerData;
    setNodeStatus(getNodeStatus(currentInnerData.isRun, currentInnerData.isDone, currentInnerData.isError));
  }, [data.innerData?.isRun, data.innerData?.isDone, data.innerData?.isError, data.innerData]);

  const [nodeName, setNodeName] = useState(data.name as string);
  const [description, setDescription] = useState((data.description as string) || '');

  const initItem: InputKeyItem = {
    name: 'query',
    required: true,
    keytable_id: '',
    fixed_value: null,
    description: '사용자가 입력한 질문',
  };

  const dummyItem: InputKeyItem = {
    name: '',
    required: false,
    keytable_id: '',
    fixed_value: null,
    description: '',
  };
  const initialInputKeys = useMemo(() => {
    const initInputItems: InputKeyItem[] = [initItem];
    return ((data as InputNodeDataSchema).input_keys as InputKeyItem[]) || initInputItems;
  }, [(data as InputNodeDataSchema).input_keys]);

  const [inputKeys, setInputKeys] = useState<InputKeyItem[]>(initialInputKeys);
  const [inputValues, setInputValues] = useState<string[]>(inputKeys.map(item => item.name));
  const nodesUpdatedRef = useRef(false);

  const isInitialMountRef = useRef(true);
  const prevDataRef = useRef(data);

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
    const newInputKeys = ((data as InputNodeDataSchema).input_keys as InputKeyItem[]) || [];
    const hasOnlyDefaultInputKeys = inputKeys.length <= 1 && inputKeys.every(k => k.name === 'query') && inputKeys.every(k => !k.keytable_id && !k.fixed_value);
    const hasEmptyInputKeys = inputKeys.length === 0;
    if (newInputKeys.length > 0 && (hasOnlyDefaultInputKeys || hasEmptyInputKeys)) {
      setInputKeys(newInputKeys);
    }

    prevDataRef.current = data;
  }, [data, (data as InputNodeDataSchema).input_keys]);

  const syncInputData = () => {
    const newInnerData = {
      ...innerData,
    };

    const newData = {
      ...data,
      type: NodeType.Input.name,
      id: id,
      name: nodeName,
      description: description,
      input_keys: inputKeys,
      innerData: newInnerData,
    };

    syncNodeData(id, newData);
  };

  const node = nodes.find(node => node.id === id);

  useEffect(() => {
    syncInputData();
  }, []);

  useEffect(() => {
    validateNode(id, type, inputKeys);
  }, [id, type, inputKeys, validateNode]);

  useEffect(() => {
    setInputValues(inputKeys.map(item => item.name));
  }, [inputKeys]);

  useEffect(() => {
    if (nodesUpdatedRef.current) {
      nodesUpdatedRef.current = false;
      syncInputData();
    }
  }, [nodeName, description, inputKeys]);

  useEffect(() => {
    updateNodeInternals(id);
  }, [id, updateNodeInternals, innerData.isToggle]);

  const containerRef = useAutoUpdateNodeInternals(id);

  if (!node) return null;

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

  const handleAddInput = () => {
    setInputKeys([...inputKeys, dummyItem]);
    nodesUpdatedRef.current = true;
  };

  const handleRemoveInput = (index: number) => {
    setInputKeys(inputKeys.filter((_, i) => i !== index));
    nodesUpdatedRef.current = true;
  };

  const handleInputKeyChange = (index: number, keyType: 'name' | 'fixed_value' | 'description', value: string) => {
    if (keyType !== 'name') {
      const updatedInputKeys = [...inputKeys];
      updatedInputKeys[index] = {
        ...updatedInputKeys[index],
        [keyType]: value,
      };
      setInputKeys(updatedInputKeys);
      nodesUpdatedRef.current = true;
      return;
    }
    const newInputValues = [...inputValues];
    newInputValues[index] = value;
    setInputValues(newInputValues);
    if (value !== '') {
      const updatedInputKeys = [...inputKeys];
      updatedInputKeys[index] = {
        ...updatedInputKeys[index],
        name: value,
        keytable_id: `${value}__${node.id}`,
      };
      setInputKeys(updatedInputKeys);
    }
    nodesUpdatedRef.current = true;
  };

  const handleNodeNameChange = (value: string) => {
    setNodeName(value);
    nodesUpdatedRef.current = true;
  };

  const handleDescriptionChange = (value: string) => {
    setDescription(value);
    nodesUpdatedRef.current = true;
  };
  const { autoResize, preventAndStop } = useNodeHandler();
  return (
    <div ref={containerRef}>
      <Card className={['agent-card w-full min-w-[500px] max-w-[500px]', nodeStatus].filter(e => !!e).join(' ')}>
        <NodeHeader
          nodeId={id}
          type={type}
          data={innerData}
          onClickDelete={onClickDelete}
          defaultValue={nodeName}
          onChange={handleNodeNameChange}
          onClickLog={handleHeaderClickLog}
        />
        <React.Fragment>
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
                  onMouseDown={e => preventAndStop(e)}
                  onMouseUp={e => preventAndStop(e)}
                  onSelect={e => preventAndStop(e)}
                  onDragStart={e => preventAndStop(e)}
                  onDrag={e => preventAndStop(e)}
                />
                <div className='absolute bottom-2 right-2 text-xs text-gray-500'>
                  <span className='text-blue-500'>{description.length}</span>/100
                </div>
              </div>
            </div>
          )}

          {!innerData.isToggle && (
            <React.Fragment>
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
                      onMouseDown={e => preventAndStop(e)}
                      onMouseUp={e => preventAndStop(e)}
                      onSelect={e => preventAndStop(e)}
                      onDragStart={e => preventAndStop(e)}
                      onDrag={e => preventAndStop(e)}
                    />
                    <div className='absolute bottom-2 right-2 text-xs text-gray-500'>
                      <span className='text-blue-500'>{description.length}</span>/100
                    </div>
                  </div>
                </div>
              </CardBody>

              <div className='border-t border-gray-200'>
                <div className='bg-gray-50 px-4 py-3'>
                  <h3 className='text-lg font-semibold text-gray-700'>Schema</h3>
                </div>
                <div className='mt-3 px-4'>
                  <div className='mb-3 flex gap-2 items-center'>
                    <input
                      type='text'
                      value={inputValues[0] || ''}
                      onChange={e => handleInputKeyChange(0, 'name', e.target.value)}
                      className='flex-1 border border-gray-300 rounded-lg px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500'
                      placeholder='Key 입력'
                      readOnly
                    />
                  </div>
                  {validation && !validation.isValid && (
                    <div className='mb-3 p-2 bg-red-50 border border-red-200 rounded-lg'>
                      <div className='text-red-600 text-sm font-medium mb-1'>⚠️ 설정 오류</div>
                      {validation.errors.map((error, index) => (
                        <div key={index} className='ag-color-red text-xs'>
                          • {error.message}
                        </div>
                      ))}
                    </div>
                  )}
                  {inputKeys.slice(1).map((item, index) => (
                    <div key={index + 1} className='mb-3 flex gap-2 items-center'>
                      <input
                        type='text'
                        value={inputValues[index + 1] || ''}
                        onChange={e => handleInputKeyChange(index + 1, 'name', e.target.value)}
                        className='flex-1 border border-gray-300 rounded-lg px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500'
                        placeholder='Key 입력'
                      />
                      <input
                        type='text'
                        value={item?.fixed_value || ''}
                        onChange={e => handleInputKeyChange(index + 1, 'fixed_value', e.target.value)}
                        className='flex-1 border border-gray-300 rounded-lg px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500'
                        placeholder='Value 입력'
                      />
                      <button
                        onClick={() => handleRemoveInput(index + 1)}
                        className='btn-icon btn btn-sm btn-light text-primary btn-bg-del'
                        title='삭제'
                        style={{
                          border: '1px solid #d1d5db',
                          borderRadius: '6px',
                          padding: '6px',
                          color: '#6b7280',
                          cursor: 'pointer',
                          fontSize: '14px',
                          transition: 'all 0.2s ease',
                        }}
                      >
                        🗑️
                      </button>
                    </div>
                  ))}

                  {/* Add Input Button */}
                  <div className='flex justify-center mb-3'>
                    <button onClick={handleAddInput} className='bg-blue-500 hover:bg-blue-600 text-white rounded-md px-4 py-2 text-sm font-medium transition-colors'>
                      input 추가
                    </button>
                  </div>
                </div>
              </div>
            </React.Fragment>
          )}
        </React.Fragment>

        <CardFooter>
          <NodeFooter onClick={handleFooterFold} isToggle={innerData.isToggle as boolean} />
        </CardFooter>
        <Handle
          type='source'
          id='input_right'
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
