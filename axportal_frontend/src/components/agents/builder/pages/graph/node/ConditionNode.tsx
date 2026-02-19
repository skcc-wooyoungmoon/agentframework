import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { useAtom } from 'jotai'; import { Handle, type NodeProps, Position, useReactFlow, useUpdateNodeInternals } from '@xyflow/react';

import { hasChatTestedAtom } from '@/components/agents/builder/atoms/logAtom.ts';
import { Card, CardBody, CardFooter } from '@/components/agents/builder/common/index.ts';
import { LogModal } from '@/components/agents/builder/common/modal/log/LogModal.tsx';
import { getNodeStatus } from '@/components/agents/builder/utils/GraphUtils.ts';
import { useAutoUpdateNodeInternals } from '@/components/agents/builder/hooks/useAutoUpdateNodeInternals';
import { useGraphActions } from '@/components/agents/builder/hooks/useGraphActions.ts';
import { useNodeTracing } from '@/components/agents/builder/hooks/useNodeTracing';
import { CustomScheme } from '@/components/agents/builder/pages/graph/contents/CustomScheme.tsx';
import { SelectConditionInputKeys } from '@/components/agents/builder/pages/graph/contents/SelectConditionInputKeys.tsx';
import { SelectConditionOperation } from '@/components/agents/builder/pages/graph/contents/SelectConditionOperation.tsx';
import { SelectConditionType } from '@/components/agents/builder/pages/graph/contents/SelectConditionType.tsx';
import {
  type ConditionItem,
  type CustomEdge,
  type CustomNode,
  type CustomNodeInnerData,
  type InputKeyItem,
  NodeType,
  type OutputKeyItem,
} from '@/components/agents/builder/types/Agents';
import { useModal } from '@/stores/common/modal/useModal';

import { NodeFooter, NodeHeader } from '.';
import { useNodeHandler } from '@/components/agents/builder/hooks/useNodeHandler';

interface NodeFormState {
  nodeName: string;
  description: string;
  conditions: ConditionItem[];
  inputKeys: InputKeyItem[];
  outputKeys: OutputKeyItem[];
}

export const ConditionNode: React.FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  const { removeNode, toggleNodeView, syncNodeData } = useGraphActions();
  const { getEdges, setEdges } = useReactFlow();
  const updateNodeInternals = useUpdateNodeInternals();
  const { openModal } = useModal();
  const [hasChatTested] = useAtom(hasChatTestedAtom);

  const conditionRefs = useRef<(HTMLDivElement | null)[]>([]);
  const elseRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    syncCurrentData();
  }, []);

  const [formState, setFormState] = useState<NodeFormState>(() => {
    const processConditions = (conditions: any[]): ConditionItem[] => {
      if (!Array.isArray(conditions))
        return [
          {
            id: 'condition-1',
            type: 'string',
            operator: 'equal',
            input_key: {
              name: '',
              required: true,
              keytable_id: '',
            },
            value: {
              name: '',
              required: true,
              keytable_id: '',
            },
          },
          {
            id: 'condition-else',
            type: 'string',
            operator: 'equal',
            input_key: {
              name: '',
              required: true,
              keytable_id: '',
            },
            value: {
              name: '',
              required: true,
              keytable_id: '',
            },
          },
        ];

      return conditions.map(con => ({
        ...con,
        id: con.id || '',
      }));
    };

    const initialIfConditions = processConditions(
      (data.conditions as ConditionItem[]) || [
        {
          id: 'condition-1',
          type: 'string',
          operator: 'equal',
          input_key: {
            name: '',
            required: true,
            keytable_id: '',
          },
          value: {
            name: '',
            required: true,
            keytable_id: '',
          },
        },
      ]
    );

    const rawOutputKeys = (data.output_keys as OutputKeyItem[]) || [];
    const normalizedOutputKeys =
      rawOutputKeys.length > 0
        ? rawOutputKeys.map(output => {
          if (output.name === 'condition_label') {
            return {
              ...output,
              name: 'selected',
              keytable_id: output.keytable_id?.replace('condition_label', 'selected') || `selected_${id}`,
            };
          }
          return output;
        })
        : [{ name: 'selected', keytable_id: `selected_${id}` }];

    return {
      nodeName: data.name as string,
      description: (data.description as string) || '',
      conditions: initialIfConditions,
      inputKeys: (data.input_keys as InputKeyItem[]) || [],
      outputKeys: normalizedOutputKeys,
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

  const syncCurrentData = useCallback(() => {
    const normalizedOutputKeys = formState.outputKeys.map(output => {
      if (output.name === 'condition_label') {
        return {
          ...output,
          name: 'selected',
          keytable_id: output.keytable_id?.replace('condition_label', 'selected') || `selected_${id}`,
        };
      }
      return output;
    });

    const finalOutputKeys = normalizedOutputKeys.length > 0 ? normalizedOutputKeys : [{ name: 'selected', keytable_id: `selected_${id}` }];

    const sortedConditions = [...(formState.conditions ?? [])].sort((a, b) => {
      const aIsElse = a.id?.includes('condition-else') || a.id === 'condition-else';
      const bIsElse = b.id?.includes('condition-else') || b.id === 'condition-else';

      if (aIsElse && !bIsElse) return 1;
      if (!aIsElse && bIsElse) return -1;
      if (aIsElse && bIsElse) return 0;

      if (!aIsElse && !bIsElse) {
        const extractNumber = (id: string): number => {
          const match = id.match(/condition-(\d+)/);
          return match ? parseInt(match[1], 10) : Number.MAX_SAFE_INTEGER;
        };
        const numA = extractNumber(a.id || '');
        const numB = extractNumber(b.id || '');
        return numA - numB;
      }
      return 0;
    });

    const newData = {
      ...data,
      id: id,
      name: formState.nodeName,
      description: formState.description,
      input_keys: formState.inputKeys,
      output_keys: finalOutputKeys,
      conditions: sortedConditions,
      default_condition: `${id}-condition-else`,
      innerData: {
        ...nodeData,
      },
    };
    syncNodeData(id, newData);
  }, [data, id, nodeData, syncNodeData, formState]);

  useEffect(() => {
    if (nodesUpdatedRef.current) {
      syncCurrentData();
      nodesUpdatedRef.current = false;
    }
  }, [syncCurrentData]);

  useEffect(() => {
    updateNodeInternals(id);
  }, [id, updateNodeInternals, nodeData.isToggle]);

  const containerRef = useAutoUpdateNodeInternals(id);

  const handleFieldChange = useCallback((field: keyof NodeFormState, value: any) => {
    setFormState(prev => ({
      ...prev,
      [field]: value,
    }));
    nodesUpdatedRef.current = true;
  }, []);

  const handleInputKeysChange = useCallback(
    (newInputKeys: InputKeyItem[]) => {
      handleFieldChange('inputKeys', newInputKeys);
    },
    [handleFieldChange]
  );

  const handleNodeNameChange = useCallback(
    (value: string) => {
      handleFieldChange('nodeName', value);
    },
    [handleFieldChange]
  );

  const handleDescriptionChange = useCallback(
    (value: string) => {
      handleFieldChange('description', value);
    },
    [handleFieldChange]
  );

  const addCondition = useCallback(() => {
    setFormState(prev => {
      const newId = `condition-${prev.conditions.length + 1}`;
      const newCondition = {
        id: newId,
        type: 'string',
        operator: 'equal',
        input_key: {
          name: '',
          required: true,
          keytable_id: '',
        },
        value: {
          name: '',
          required: true,
          keytable_id: '',
        },
      };
      return {
        ...prev,
        conditions: [...prev.conditions, newCondition],
      };
    });
    nodesUpdatedRef.current = true;
  }, []);

  const deleteCondition = useCallback((index: number) => {
    setFormState(prev => ({
      ...prev,
      conditions: prev.conditions.filter((_, i) => i !== index),
    }));
    const edges = getEdges();
    setEdges(edges.filter(edge => edge.label !== `condition-${index + 1}`));
    nodesUpdatedRef.current = true;
  }, []);

  const updateCondition = useCallback(
    (index: number, field: string, value: string | any) => {
      setFormState(prev => {
        const updatedConditions = prev.conditions.map((condition, i) => {
          if (i !== index) {
            return condition;
          }

          if (field === 'value') {
            const prevValue = condition.value ?? { name: '', required: true, keytable_id: '', fixed_value: '' };
            return {
              ...condition,
              value: {
                ...prevValue,
                ...(value as Record<string, any>),
              },
            };
          }

          return { ...condition, [field]: value };
        });

        if (field === 'id') {
          const edges = getEdges();
          const conditionId = prev.conditions[index].id;

          setEdges(
            edges.map(edge => {
              const typedEdge = edge as CustomEdge;

              if (typedEdge.source === id && (typedEdge.sourceHandle === `handle-${conditionId}` || typedEdge.data?.condition?.id === conditionId)) {
                const updatedData = {
                  ...(typedEdge.data || {}),
                  category: {
                    ...(typedEdge.data?.condition || {}),
                    id: conditionId,
                    category: value,
                    description: typedEdge.data?.category?.description || '',
                  },
                  condition_label: value,
                };

                return {
                  ...typedEdge,
                  label: value,
                  condition_label: value,
                  data: updatedData,
                };
              }
              return typedEdge;
            })
          );
        }

        return {
          ...prev,
          conditions: updatedConditions,
        };
      });
      nodesUpdatedRef.current = true;
    },
    [id, getEdges, setEdges]
  );

  const onClickDelete = () => {
    removeNode(id);
  };

  const handleFooterFold = (bool: boolean) => {
    toggleNodeView(id, bool);
  };
  const { autoResize, stopPropagation, preventAndStop } = useNodeHandler();
  return (
    <div ref={containerRef}>
      <Card className={['agent-card w-full min-w-[500px] max-w-[500px]', nodeStatus].filter(e => !!e).join(' ')}>
        <Handle
          type='target'
          id='condition_left'
          key={`condition_left_${nodeData.isToggle}`}
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
          type={type}
          data={nodeData}
          onClickDelete={onClickDelete}
          defaultValue={formState.nodeName}
          onChange={handleNodeNameChange}
          nodeId={id}
          onClickLog={handleHeaderClickLog}
        />

        <>
          {nodeData.isToggle && (
            <>
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
              <CardBody>
                <div className='w-full'>
                  {formState.conditions
                    .filter(condition => condition.id !== 'condition-else')
                    .map((condition, index) => (
                      <div
                        key={condition.id}
                        className='border border-gray-200 rounded-lg mb-3'
                        ref={el => {
                          conditionRefs.current[index] = el;
                        }}
                      >
                        <div className='rounded-t-lg bg-gray-100 px-4 py-2 flex items-center justify-between relative'>
                          <h3 className='text-base font-bold text-gray-700'>{index === 0 ? 'IF' : 'ELSE IF'}</h3>
                          {index > 0 && (
                            <button type='button' className='text-red-500 text-sm font-medium' onClick={() => deleteCondition(index)} title='조건 삭제'>
                              <img alt='ico-system-24-outline-gray-trash' className='w-[24px] h-[24px]  ' src='/assets/images/system/ico-system-24-outline-gray-trash.svg' /> 삭제
                            </button>
                          )}
                          <Handle
                            type='source'
                            position={Position.Right}
                            id={`handle-${condition.id || `condition-${index}`}`}
                            style={{
                              position: 'absolute',
                              top: '50%',
                              transform: 'translateY(-50%)',
                              width: 20,
                              height: 20,
                              right: -10,
                              background: '#000000',
                              border: '2px solid white',
                              boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
                              zIndex: 10,
                            }}
                          />
                        </div>

                        <div className='mt-5 w-full relative px-4'>
                          <div className='relative mb-4 w-full'>
                            <div className='mb-1 flex flex-wrap items-baseline gap-2.5 lg:flex-nowrap'>
                              <label className='whitespace-nowrap'>{'Type'}</label>
                              <SelectConditionType
                                selectedCondition={condition}
                                onChange={(selectedType: string) => {
                                  updateCondition(index, 'type', selectedType);
                                }}
                              />
                              <SelectConditionInputKeys
                                selectedCondition={condition}
                                inputKeys={formState.inputKeys}
                                onChange={(selectedInputKeyName: string, selectedInputKeyTableId: string) => {
                                  updateCondition(index, 'input_key', {
                                    name: selectedInputKeyName,
                                    required: true,
                                    keytable_id: selectedInputKeyTableId,
                                  });
                                }}
                              />
                              {index > 0 && index + 1 === formState.conditions.length && (
                                <button
                                  onClick={() => deleteCondition(index)}
                                  className='btn-icon btn btn-xs btn-light text-red-500 btn-bg-del'
                                  disabled={formState.conditions.length === 1}
                                >
                                  <i className='ki-filled ki-trash text-xs' />
                                </button>
                              )}
                            </div>
                            <div className='mb-1 flex flex-wrap items-baseline gap-2.5 lg:flex-nowrap'>
                              <label className='form-label flex max-w-20 items-center gap-1 whitespace-nowrap'>{'Operation'}</label>
                              <SelectConditionOperation
                                selectedCondition={condition}
                                onChange={(selectedOperator: string) => {
                                  updateCondition(index, 'operator', selectedOperator);
                                }}
                              />
                              <input
                                type='text'
                                className='input w-full'
                                value={condition.value.fixed_value ?? ''}
                                onChange={e =>
                                  updateCondition(index, 'value', {
                                    name: `condition-${index + 1}`,
                                    required: true,
                                    fixed_value: e.target.value,
                                  })
                                }
                                placeholder='Value'
                              />
                            </div>
                          </div>
                        </div>
                      </div>
                    ))}
                  <div className='border border-gray-200 rounded-lg mb-3'>
                    <div className='rounded-t-lg bg-gray-100 px-4 py-2 relative' ref={elseRef}>
                      <h3 className='text-base font-bold text-gray-700'>ELSE</h3>
                      <Handle
                        type='source'
                        position={Position.Right}
                        id='handle-condition-else'
                        style={{
                          position: 'absolute',
                          top: '50%',
                          transform: 'translateY(-50%)',
                          width: 20,
                          height: 20,
                          right: -10,
                          background: '#000000',
                          border: '2px solid white',
                          boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
                          zIndex: 10,
                        }}
                      />
                    </div>

                    <div className='mt-5 w-full relative px-4'>
                      <div key={'condition-else'} className='relative mb-4 w-full'>
                        <div className='mb-2 flex items-center gap-2'>
                          <input type='text' className='input w-full text-xs' value='모든 조건이 만족하지 않을 때 실행됩니다.' readOnly />
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </CardBody>
            </>
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

                <div className='w-full'>
                  {formState.conditions
                    .filter(condition => condition.id !== 'condition-else')
                    .map((condition, index) => (
                      <div
                        key={condition.id}
                        className='border border-gray-200 rounded-lg mb-3'
                        ref={el => {
                          conditionRefs.current[index] = el;
                        }}
                      >
                        <div className='bg-gray-100 py-2 px-2 flex items-center justify-between relative'>
                          <h3 className='form-label flex max-w-20 items-center gap-1 whitespace-nowrap'>{index === 0 ? 'IF' : 'ELSE IF'}</h3>
                          {index > 0 && (
                            <button
                              type='button'
                              className='flex gap-1 items-center text-black-500 hover:text-black-500 text-sm font-medium text-[12px]'
                              onClick={() => deleteCondition(index)}
                              title='조건 삭제'
                            >
                              <span className='leading-[1]'>삭제</span>
                              <img alt='ico-system-24-outline-gray-trash' className='w-[24px] h-[24px]  ' src='/assets/images/system/ico-system-24-outline-gray-trash.svg' />
                            </button>
                          )}

                          <Handle
                            type='source'
                            position={Position.Right}
                            id={`handle-${condition.id || `condition-${index}`}`}
                            style={{
                              position: 'absolute',
                              top: '50%',
                              transform: 'translateY(-50%)',
                              width: 20,
                              height: 20,
                              right: -30,
                              background: '#000000',
                              border: '2px solid white',
                              boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
                              zIndex: 10,
                            }}
                          />
                        </div>

                        <div className='mt-5 w-full relative px-2'>
                          <div className='flex flex-col gap-3 relative mb-4 w-full'>
                            <div className='flex flex-col gap-1'>
                              <label className='form-label flex max-w-20 items-center gap-1 whitespace-nowrap'>{'타입'}</label>
                              <div className='grid grid-cols-2 gap-2'>
                                <SelectConditionType
                                  selectedCondition={condition}
                                  className='b-selectbox'
                                  onChange={(selectedType: string) => {
                                    updateCondition(index, 'type', selectedType);
                                  }}
                                />
                                <SelectConditionInputKeys
                                  selectedCondition={condition}
                                  inputKeys={formState.inputKeys}
                                  onChange={(selectedInputKeyName: string, selectedInputKeyTableId: string) => {
                                    updateCondition(index, 'input_key', {
                                      name: selectedInputKeyName,
                                      required: true,
                                      keytable_id: selectedInputKeyTableId,
                                    });
                                  }}
                                />
                                {index > 0 && index + 1 === formState.conditions.length && (
                                  <button
                                    onClick={() => deleteCondition(index)}
                                    className='btn-icon btn btn-xs btn-light text-red-500 btn-bg-del'
                                    disabled={formState.conditions.length === 1}
                                  >
                                    <i className='ki-filled ki-trash text-xs' />
                                  </button>
                                )}
                              </div>
                            </div>
                            <div className='flex flex-col gap-1'>
                              <label className='form-label flex max-w-20 items-center gap-1 whitespace-nowrap'>{'연산자'}</label>
                              <div className='grid grid-cols-2 gap-2'>
                                <SelectConditionOperation
                                  selectedCondition={condition}
                                  onChange={(selectedOperator: string) => {
                                    updateCondition(index, 'operator', selectedOperator);
                                  }}
                                />
                                <input
                                  type='text'
                                  className='input w-full rounded-lg border border-gray-300 bg-white p-2 py-1.5'
                                  value={condition.value.fixed_value ?? ''}
                                  onChange={e =>
                                    updateCondition(index, 'value', {
                                      name: `condition-${index + 1}`,
                                      required: true,
                                      fixed_value: e.target.value,
                                    })
                                  }
                                  placeholder='Value'
                                />
                              </div>
                            </div>
                          </div>
                        </div>
                      </div>
                    ))}
                  <div className='border border-gray-200 rounded-lg mb-3'>
                    <div className='bg-gray-100 py-2 px-2 relative'>
                      <h3 className='text-base font-bold text-gray-700'>ELSE</h3>

                      <Handle
                        type='source'
                        position={Position.Right}
                        id='handle-condition-else'
                        style={{
                          position: 'absolute',
                          top: '50%',
                          transform: 'translateY(-50%)',
                          width: 20,
                          height: 20,
                          right: -30,
                          background: '#000000',
                          border: '2px solid white',
                          boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
                          zIndex: 10,
                        }}
                      />
                    </div>

                    <div className='mt-5 w-full relative px-2'>
                      <div key={'condition-else'} className='relative mb-4 w-full'>
                        <div className='mb-2 flex items-center gap-2'>
                          <input type='text' className='input w-full text-xs' value='모든 조건이 만족하지 않을 때 실행됩니다.' readOnly />
                        </div>
                      </div>
                    </div>
                  </div>

                  <div className='flex justify-center'>
                    <button
                      onClick={addCondition}
                      className='bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded-lg border-0 transition-colors duration-200 mt-3'
                    >
                      Add Condition
                    </button>
                  </div>
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
                type={NodeType.AgentCondition.name}
                disabledKeyIn={true}
              />
            </>
          )}
        </>

        <CardFooter>
          <NodeFooter onClick={handleFooterFold} isToggle={nodeData.isToggle as boolean} />
        </CardFooter>
      </Card>
    </div>
  );
};
