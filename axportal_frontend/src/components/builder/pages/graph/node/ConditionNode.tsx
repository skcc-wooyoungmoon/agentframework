// noinspection DuplicatedCode

import { Card, CardBody, CardFooter, LogModal } from '@/components/builder/common/index.ts';
import { ABClassNames } from '@/components/builder/components/ui';
import { useAutoUpdateNodeInternals } from '@/components/builder/hooks/useAutoUpdateNodeInternals';
import { useGraphActions } from '@/components/builder/hooks/useGraphActions.ts';
import { useNodeValidation } from '@/components/builder/hooks/useNodeValidation.ts';
import { CustomScheme } from '@/components/builder/pages/graph/contents/CustomScheme.tsx';
import { SelectConditionInputKeys } from '@/components/builder/pages/graph/contents/SelectConditionInputKeys.tsx';
import { SelectConditionOperation } from '@/components/builder/pages/graph/contents/SelectConditionOperation.tsx';
import { SelectConditionType } from '@/components/builder/pages/graph/contents/SelectConditionType.tsx';
import { type ConditionItem, type CustomEdge, type CustomNode, type CustomNodeInnerData, type InputKeyItem, NodeType, type OutputKeyItem } from '@/components/builder/types/Agents';
import keyTableData from '@/components/builder/types/keyTableData.json';
import { getNodeStatus } from '@/components/builder/utils/GraphUtils.ts';
import { UIImage } from '@/components/UI/atoms/UIImage';
import { useModal } from '@/stores/common/modal';
import { Handle, type NodeProps, Position, useReactFlow } from '@xyflow/react';
import { useAtom } from 'jotai';
import React, { useCallback, useEffect, useRef, useState } from 'react';
import { NodeFooter, NodeHeader } from '.';
import { logState } from '../../..';
import { CustomErrorMessage } from './common/CustomErrorMessage';
interface NodeFormState {
  nodeName: string;
  description: string;
  conditions: ConditionItem[];
  inputKeys: InputKeyItem[];
  outputKeys: OutputKeyItem[];
}

export const ConditionNode: React.FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  // console.log('🔍 ConditionNode!!!!!!!!!!!!!:', data, id, type);
  const { removeNode, toggleNodeView, syncNodeData } = useGraphActions();
  const { getEdges, setEdges } = useReactFlow();
  const { validateCondition, getValidation } = useNodeValidation();
  const validation = getValidation(id, 'node_value');

  const [, setLogData] = useAtom(logState);
  const { openModal } = useModal();

  useEffect(() => {
    syncCurrentData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const [formState, setFormState] = useState<NodeFormState>(() => {
    const processConditions = (conditions: any[]): ConditionItem[] => {
      if (!Array.isArray(conditions))
        return [
          {
            id: `${id}-condition-1`,
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
          id: `${id}-condition-1`,
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

    return {
      nodeName: data.name as string,
      description: (data.description as string) || (keyTableData['condition']['field_default']['description'] as string),
      conditions: initialIfConditions,
      inputKeys: (data.input_keys as InputKeyItem[]) || [],
      outputKeys: (data.output_keys as OutputKeyItem[]) || [],
    };
  });

  const [inputValues, setInputValues] = useState<string[]>(formState.inputKeys.map(item => item.name));

  const newInnerData: CustomNodeInnerData = {
    isRun: false,
    isToggle: false,
  };
  const nodeData: CustomNodeInnerData = data.innerData ?? newInnerData;
  const nodesUpdatedRef = useRef(false);

  const [nodeStatus, setNodeStatus] = useState<string | null>(null);
  useEffect(() => {
    const status = getNodeStatus(nodeData.isRun, nodeData.isDone, nodeData.isError);
    setNodeStatus(status);
  }, [nodeData.isRun, nodeData.isDone, nodeData.isError]);

  const syncCurrentData = useCallback(() => {
    const newData = {
      ...data,
      // basic node info
      id: id,
      name: formState.nodeName,
      description: formState.description,
      // node schema
      input_keys: formState.inputKeys,
      output_keys: formState.outputKeys,
      conditions: formState.conditions ?? [],
      default_condition: `${id}-condition-else`,
      // public node state
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
      setFormState(prev => {
        const newConditions = prev.conditions.map(condition => ({
          ...condition,
          input_key: {
            ...condition.input_key,
            keytable_id: '', // keytable_id를 빈 문자열로 초기화
          },
        }));

        return {
          ...prev,
          conditions: newConditions,
        };
      });
    },
    [handleFieldChange]
  );

  useEffect(() => {
    validateCondition(id, formState.conditions);
  }, [id, formState.conditions, validateCondition]);

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
      const newId = `${id}-condition-${prev.conditions.length + 1}`;
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
    setEdges(edges.filter(edge => edge.sourceHandle !== `handle-${id}-condition-${index + 1}` || edge.sourceHandle !== 'handle-'));
    nodesUpdatedRef.current = true;
  }, []);

  const updateCondition = useCallback(
    (index: number, field: string, value: string | any) => {
      setFormState(prev => {
        const updatedConditions = prev.conditions.map((condition, i) => (i === index ? { ...condition, [field]: value } : condition));

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

  // 노드 내부 콘텐츠 높이 변화 감지하여 연결선 재계산
  const containerRef = useAutoUpdateNodeInternals(id);

  const handleHeaderClickLog = () => {
    if (data.innerData.logData) {
      setLogData(
        data.innerData.logData.map(item => ({
          log: item,
        }))
      );
      openModal({
        type: 'large',
        title: '로그',
        body: <LogModal id={'builder_log'} />,
        showFooter: false,
      });
    }
  };

  return (
    <div ref={containerRef}>
      <Card className={ABClassNames('agent-card w-full min-w-[500px] max-w-[500px]', nodeStatus)}>
        <Handle
          type='target'
          position={Position.Left}
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
          onClickLog={handleHeaderClickLog}
          defaultValue={formState.nodeName}
          onChange={handleNodeNameChange}
          nodeId={id}
        />

        <CardBody className='p-4'>
          <div className='mb-4'>
            <label className='block font-semibold text-sm text-gray-700 mb-2'>{'설명'}</label>
            <div className='relative'>
              <textarea
                className='nodrag w-full resize-none border border-gray-300 rounded-lg p-3 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500'
                rows={3}
                placeholder={'설명 입력'}
                value={formState.description}
                onChange={e => handleDescriptionChange(e.target.value)}
                maxLength={100}
              />
              <div className='absolute bottom-2 right-2 text-xs text-gray-500'>
                <span className='text-blue-500'>{formState.description.length}</span>/100
              </div>
            </div>
          </div>

          <div className='w-full'>
            {formState.conditions.map((condition, index) => (
              <div key={condition.id} className='border border-gray-200 rounded-lg mb-3'>
                <div className='bg-gray-100 py-2 px-2 flex items-center justify-between relative'>
                  <h3 className='form-label flex max-w-20 items-center gap-1 whitespace-nowrap'>{index === 0 ? 'IF' : 'ELSE IF'}</h3>
                  {/* 첫 번째 조건(IF)은 삭제할 수 없음 */}
                  {index > 0 && (
                    <button
                      type='button'
                      className='flex h-[20px] w-[20px] items-center justify-center rounded-md hover:bg-gray-100 cursor-pointer ml-auto'
                      onClick={() => deleteCondition(index)}
                      title='조건 삭제'
                    >
                      <UIImage src='/assets/images/system/ico-system-24-outline-gray-trash.svg' alt='No data' className='w-20 h-20' />
                    </button>
                  )}

                  {/* Handle을 헤더 영역에 배치 */}
                  <Handle
                    type='source'
                    position={Position.Right}
                    id={`handle-${condition.id}`}
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
                          className={ABClassNames(
                            'w-full rounded-lg border px-3 py-2 text-sm nodrag ',
                            validation?.errors.filter(error => error.details?.inputIndex === index) &&
                              validation?.errors.filter(error => error.details?.inputIndex === index).length > 0
                              ? 'border-red-500 ring-red-500 focus:border-red-500 focus:ring-red-500'
                              : 'border-gray-300 focus:border-gray-300 focus:ring-gray-300'
                          )}
                          value={condition.value.fixed_value ?? ''}
                          onChange={e =>
                            updateCondition(index, 'value', {
                              name: `${id}-condition-${index + 1}`,
                              required: true,
                              fixed_value: e.target.value,
                            })
                          }
                          placeholder='Value'
                        />
                      </div>
                      {validation?.errors.filter(error => error.details?.inputIndex === index) &&
                        validation?.errors.filter(error => error.details?.inputIndex === index).length > 0 && (
                          <div className='mt-1'>
                            <CustomErrorMessage message={validation?.errors.filter(error => error.details?.inputIndex === index)[0].message} />
                          </div>
                        )}
                    </div>
                  </div>
                </div>
              </div>
            ))}

            {/* ELSE 조건은 항상 표시 */}
            <div className='border border-gray-200 rounded-lg mb-3'>
              <div className='bg-gray-100 py-2 px-2 relative' key={`${id}-condition-else`}>
                <h3 className='text-base font-bold text-gray-700'>ELSE</h3>

                {/* Handle을 헤더 영역에 배치 */}
                <Handle
                  type='source'
                  position={Position.Right}
                  id={`handle-${id}-condition-else`}
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
                <div className='relative mb-4 w-full'>
                  <div className='mb-2 flex items-center gap-2'>
                    <div className='text-xs'>모든 조건이 만족하지 않을 때 실행됩니다.</div>
                  </div>
                </div>
              </div>
            </div>

            {!nodeData.isToggle && (
              <div className='flex justify-center'>
                <button onClick={addCondition} className='btn btn-light rounded-md border border-gray-300 px-4 py-2 text-dark'>
                  조건 추가
                </button>
              </div>
            )}
          </div>
        </CardBody>

        {!nodeData.isToggle && (
          <>
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

        <CardFooter>
          <NodeFooter onClick={handleFooterFold} isToggle={nodeData.isToggle as boolean} />
        </CardFooter>
      </Card>
    </div>
  );
};
