import { inputRetrieverAtom } from '@/components/builder/atoms/inputNodeAtom.ts';
import { Card, CardBody, CardFooter, LogModal } from '@/components/builder/common/index.ts';
import { ABClassNames } from '@/components/builder/components/ui';
import { useAutoUpdateNodeInternals } from '@/components/builder/hooks/useAutoUpdateNodeInternals';
import { useGraphActions } from '@/components/builder/hooks/useGraphActions.ts';
import { useNodeValidation } from '@/components/builder/hooks/useNodeValidation.ts';
import { type CustomNode, type CustomNodeInnerData, type InputKeyItem, type InputNodeDataSchema, NodeType } from '@/components/builder/types/Agents';
import keyTableData from '@/components/builder/types/keyTableData.json';
import { getNodeStatus } from '@/components/builder/utils/GraphUtils.ts';
import { UIImage } from '@/components/UI/atoms/UIImage';
import { useModal } from '@/stores/common/modal/useModal';
import { Handle, type NodeProps, Position, useUpdateNodeInternals } from '@xyflow/react';
import { useAtom } from 'jotai/index';
import React, { useEffect, useMemo, useRef, useState } from 'react';
import { NodeFooter, NodeHeader } from '.';
import { logState } from '../../..';
import { InsertLLMParameterPop } from '../../modal/InsertLLMParameterPop';
import { CustomErrorMessage } from './common/CustomErrorMessage';

export const InputNode: React.FC<NodeProps<CustomNode>> = ({ data, id, type }: NodeProps<CustomNode>) => {
  const { nodes, toggleNodeView } = useGraphActions();
  const { removeNode, syncNodeData, setKeyTable } = useGraphActions();
  const updateNodeInternals = useUpdateNodeInternals();
  const { validateNode, getValidation } = useNodeValidation();

  const validation = getValidation(id, 'input');

  const newInnerData: CustomNodeInnerData = {
    isRun: false,
    isDone: false,
    isError: false,
    isToggle: false,
    logData: [],
  };

  const nodeData: CustomNodeInnerData = data.innerData ?? newInnerData;
  // console.log('InputNode > nodeData::::::::::: ', nodeData);
  const [nodeName, setNodeName] = useState(data.name as string);
  const [description, setDescription] = useState((data.description as string) || (keyTableData['input__basic']['field_default']['description'] as string));

  const initItem: InputKeyItem = {
    name: 'query',
    required: true,
    keytable_id: '',
    fixed_value: null,
    description: '사용자가 입력한 질문',
    object_type: 'string',
  };

  const createDummyItem = (index: number): InputKeyItem => ({
    name: `new_string${index}`,
    required: false,
    keytable_id: `new_string${index}__${data.id}`,
    fixed_value: null,
    description: '',
    object_type: 'string',
  });

  const initialInputKeys = useMemo(() => {
    const initInputItems: InputKeyItem[] = [initItem];
    // const initInputItems: InputKeyItem[] = [dummyItem];
    return ((data as InputNodeDataSchema).input_keys as InputKeyItem[]) || initInputItems;
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [(data as InputNodeDataSchema).input_keys]);

  const [inputKeys, setInputKeys] = useState<InputKeyItem[]>(initialInputKeys);
  const [inputValues, setInputValues] = useState<string[]>(inputKeys.map(item => item.name));
  const nodesUpdatedRef = useRef(false);
  const [inputRetrieverNode] = useAtom(inputRetrieverAtom);

  const node = nodes.find(node => node.id === id);

  const [nodeStatus, setNodeStatus] = useState<string | null>(null);
  useEffect(() => {
    const ddd = getNodeStatus(nodeData.isRun, nodeData.isDone, nodeData.isError);
    setNodeStatus(ddd);
  }, [nodeData.isRun, nodeData.isDone, nodeData.isError]);

  const [, setLogData] = useAtom(logState);
  const { openModal } = useModal();

  // syncInputData 함수를 useEffect보다 위로 이동
  const syncInputData = () => {
    const newInnerData = {
      ...nodeData,
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

  useEffect(() => {
    syncInputData();
    // eslint-disable-next-line
  }, []);

  useEffect(() => {
    validateNode(id, type, inputKeys);
  }, [id, type, inputKeys, validateNode]);

  useEffect(() => {
    setInputValues(inputKeys.map(item => item.name));
  }, [inputKeys]);

  useEffect(() => {
    syncInputData();
    // eslint-disable-next-line
  }, [nodeName, description, inputKeys]);

  // inputKeys 변경 시 키테이블 업데이트
  useEffect(() => {
    const currentNode = nodes.find(n => n.id === id);
    if (currentNode) {
      const updatedNode = {
        ...currentNode,
        data: {
          ...currentNode.data,
          input_keys: inputKeys,
        },
      };
      setKeyTable(updatedNode);
    }
  }, [inputKeys, id, nodes]);

  // inputRetrieverNode 변경 감지하여 fixed_value 업데이트
  useEffect(() => {
    Object.values(inputRetrieverNode).forEach(retrieverData => {
      if (retrieverData.nodeId === id && inputKeys[retrieverData.inputIndex]) {
        const updatedInputKeys = [...inputKeys];
        updatedInputKeys[retrieverData.inputIndex] = {
          ...updatedInputKeys[retrieverData.inputIndex],
          fixed_value: {
            repo_id: retrieverData.inputData.repoId,
            retrieval_options: {
              // doc_format_metafields: retrieverData.inputData.retrievalOptions.doc_format_metafields || [],
              retrieval_mode: retrieverData.inputData.retrievalOptions.retrieval_mode,
              top_k: retrieverData.inputData.retrievalOptions.top_k ? Number(retrieverData.inputData.retrievalOptions.top_k) : null,
              threshold: retrieverData.inputData.retrievalOptions.threshold ? Number(retrieverData.inputData.retrievalOptions.threshold) : null,
              filter: retrieverData.inputData.retrievalOptions.filter || null,
              keywords: retrieverData.inputData.retrievalOptions.keywords || [],
              order_by: null,
              hybrid_dense_ratio: retrieverData.inputData.retrievalOptions.hybrid_dense_ratio ? Number(retrieverData.inputData.retrievalOptions.hybrid_dense_ratio) : null,
              file_ids: null,
            },
          },
        };
        setInputKeys(updatedInputKeys);
        nodesUpdatedRef.current = true;
      }
    });
    // eslint-disable-next-line
  }, [inputRetrieverNode]);

  // Handle 위치/개수가 바뀔 때 노드 내부 레이아웃 재계산 (접힘/펼침 등)
  useEffect(() => {
    updateNodeInternals(id);
  }, [id, updateNodeInternals, nodeData.isToggle]);

  // 노드 내부 콘텐츠 높이 변화 감지하여 연결선 재계산
  const containerRef = useAutoUpdateNodeInternals(id);

  // 모든 hooks 호출 후 early return 체크
  if (!node) return;

  const onClickDelete = () => {
    removeNode(id);
  };

  const handleFooterFold = (bool: boolean) => {
    toggleNodeView(id, bool);
  };

  const handleAddInput = () => {
    setInputKeys([...inputKeys, createDummyItem(inputKeys.length)]);
    nodesUpdatedRef.current = true;
  };

  const handleRemoveInput = (index: number) => {
    setInputKeys(inputKeys.filter((_, i) => i !== index));
    nodesUpdatedRef.current = true;
  };

  const handleInputKeyChange = (index: number, keyType: 'name' | 'fixed_value' | 'description' | 'object_type', value: string) => {
    if (keyType === 'object_type') {
      const updatedInputKeys = [...inputKeys];
      const currentName = updatedInputKeys[index].name;
      updatedInputKeys[index] = {
        ...updatedInputKeys[index],
        object_type: value as 'string' | 'LLM parameters',
        fixed_value: null,
      };
      // object_type이 변경되면 name과 keytable_id도 새로운 패턴으로 업데이트
      if (currentName.startsWith('new_')) {
        // LLM parameters인 경우 ext_llm_args 형식 사용
        let newName: string;
        if (value === 'LLM parameters') {
          newName = `ext_llm_args${index}`;
        } else {
          newName = `new_${value}${index}`;
        }
        const newKeytableId = `${newName}__${data.id}`;
        updatedInputKeys[index].name = newName;
        updatedInputKeys[index].keytable_id = newKeytableId;
      }
      setInputKeys(updatedInputKeys);
      nodesUpdatedRef.current = true;
      return;
    }
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

  const renderInputField = (item: InputKeyItem, index: number) => {
    const inputErrors = validation?.errors.filter(error => error.details?.inputIndex === index) || [];
    const inputTypeLists = ['string', 'LLM parameters'];
    const hasError = inputErrors.length > 0;

    if (index === 0) {
      return (
        <div key={index} className='mb-4 flex flex-col rounded-lg border border-gray-300 bg-white p-4'>
          {/* Query Input Field (첫 번째 항목) */}
          <div className='flex items-center gap-2 min-w-0'>
            <input
              type='text'
              value={inputValues[0] || ''}
              onChange={e => handleInputKeyChange(0, 'name', e.target.value)}
              className='nodrag flex-1 border border-gray-300 rounded-lg px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500'
              placeholder='Key 입력'
              readOnly
            />
          </div>
        </div>
      );
    }

    return (
      <div
        key={index}
        className={`relative mb-4 ${
          // 필수 입력값이 비어있으면 하단 여백 추가 (에러 메시지 공간)
          hasError ? 'mb-5' : ''
        }`}
      >
        <div className={`flex flex-col rounded-lg bg-white p-4 border ${hasError ? 'ag-border-red' : 'border-gray-300'}`}>
          <div className='flex items-center gap-2 min-w-0'>
            <input
              type='text'
              value={inputValues[index] || ''}
              onChange={e => handleInputKeyChange(index, 'name', e.target.value)}
              className='nodrag flex-1 min-w-0 border border-gray-300 rounded-lg px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500'
              style={{
                border: '1px solid #d1d5db',
                borderRadius: '6px',
                padding: '6px',
                fontSize: '14px',
              }}
              placeholder='Key 입력'
              readOnly={index === 0}
            />
            <select
              id='object_type'
              name='object_type'
              onChange={e => handleInputKeyChange(index, 'object_type', e.target.value as 'string' | 'LLM parameters')}
              className='select flex-1 min-w-0 max-w-[150px]'
              value={item?.object_type || 'string'}
              style={{
                border: '1px solid #d1d5db',
                borderRadius: '6px',
                padding: '6px',
                fontSize: '14px',
              }}
            >
              {inputTypeLists.map((typeItem, typeIndex) => (
                <option key={typeIndex} value={typeItem}>
                  {typeItem}
                </option>
              ))}
            </select>
            {(item?.object_type || 'string') === 'string' ? (
              <input
                type='text'
                value={
                  item?.fixed_value === null || item?.fixed_value === undefined
                    ? ''
                    : typeof item?.fixed_value === 'object'
                      ? ''
                      : String(item?.fixed_value)
                }
                onChange={e => handleInputKeyChange(index, 'fixed_value', e.target.value)}
                className='nodrag input flex-1 min-w-0'
                style={{
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  padding: '6px',
                  fontSize: '14px',
                }}
                placeholder='Input Value'
                disabled={index === 0}
              />
            ) : (
              <button
                onClick={() => {
                  // 현재 inputKey의 fixed_value에서 모델 파라미터 추출
                  const currentFixedValue = item?.fixed_value;
                  let initialModelParams: any[] = [];
                  let initialDisabledParams: any[] = [];

                  // fixed_value가 객체인 경우 파라미터 추출
                  if (currentFixedValue && typeof currentFixedValue === 'object') {
                    // params와 disabled_params 형태로 저장된 데이터 읽기
                    if (currentFixedValue.params && Array.isArray(currentFixedValue.params)) {
                      initialModelParams = currentFixedValue.params.map((p: any) => {
                        return {
                          keyName: p.name || '',
                          type: p.type || 'string',
                          value: p.value !== undefined ? p.value : null, // null 값도 그대로 유지
                        };
                      });
                    }
                    if (currentFixedValue.disabled_params && Array.isArray(currentFixedValue.disabled_params)) {
                      initialDisabledParams = currentFixedValue.disabled_params.map((key: string) => ({
                        keyName: key,
                      }));
                    }
                  }

                  openModal({
                    type: 'large',
                    title: 'Default Value',
                    body: (
                      <InsertLLMParameterPop
                        initialModelParameters={initialModelParams}
                        initialDisabledParameters={initialDisabledParams}
                        onSave={(modelParams: any[], disabledParams: any[]) => {
                          // 저장된 데이터를 fixed_value에 params와 disabled_params 형태로 저장
                          const updatedInputKeys = [...inputKeys];
                          updatedInputKeys[index] = {
                            ...updatedInputKeys[index],
                            fixed_value: {
                              params: modelParams.map(p => {
                                // value가 빈 문자열이면 null로 변환 (null 타입인 경우)
                                let value: string | null = p.value;
                                if (p.type === 'null') {
                                  value = null;
                                } else if (value === '') {
                                  value = null;
                                }
                                return {
                                  name: p.keyName,
                                  type: p.type || 'string',
                                  value: value,
                                };
                              }),
                              disabled_params: disabledParams.map(p => p.keyName),
                            },
                          };
                          setInputKeys(updatedInputKeys);
                          nodesUpdatedRef.current = true;
                        }}
                      />
                    ),
                    showFooter: true,
                    cancelText: 'Cancel',
                    confirmText: 'Save',
                    onConfirm: () => {
                      // 전역 핸들러를 통해 저장 처리
                      if ((window as any).defaultValueApplyHandler) {
                        (window as any).defaultValueApplyHandler();
                      }
                    },
                  });
                }}
                className='btn-icon btn btn-light flex-shrink-0 whitespace-nowrap px-3'
                style={{
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  padding: '6px',
                  color: '#6b7280',
                  cursor: 'pointer',
                  fontSize: '14px',
                  transition: 'all 0.2s ease',
                  minWidth: '32px',
                  width: '32px',
                  height: '32px',
                }}
              >
                설정
              </button>
            )}
            {index !== 0 && (
              <button
                onClick={() => handleRemoveInput(index)}
                className='btn-icon btn btn-sm btn-light text-primary p-2 btn-bg-del flex-shrink-0 cursor-pointer'
                style={{
                  border: '1px solid #d1d5db',
                  borderRadius: '6px',
                  padding: '6px',
                  color: '#6b7280',
                  cursor: 'pointer',
                  fontSize: '14px',
                  transition: 'all 0.2s ease',
                  minWidth: '32px',
                  width: '32px',
                  height: '32px',
                }}
                title='삭제'
              >
                <UIImage src='/assets/images/system/ico-system-24-outline-gray-trash.svg' alt='No data' className='w-20 h-20' />
              </button>
            )}
          </div>
        </div>

        {/* Error Messages */}
        {inputErrors.map((error, errorIndex) => (
          <div key={errorIndex} className='mt-1'>
            <CustomErrorMessage message={error.message} />
          </div>
        ))}
      </div>
    );
  };

  return (
    <div ref={containerRef}>
      <Card className={ABClassNames('agent-card w-full min-w-[500px] max-w-[500px]', nodeStatus)}>
        <NodeHeader
          nodeId={id}
          type={type}
          data={nodeData}
          onClickLog={handleHeaderClickLog}
          onClickDelete={onClickDelete}
          defaultValue={nodeName}
          onChange={handleNodeNameChange}
        />

        <CardBody className='p-4'>
          <div className='mb-4'>
            <label className='block font-semibold text-sm text-gray-700 mb-2'>{'설명'}</label>
            <div className='relative'>
              <textarea
                className='nodrag w-full resize-none border border-gray-300 rounded-lg p-3 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500'
                rows={3}
                placeholder={'설명 입력'}
                value={description}
                onChange={e => handleDescriptionChange(e.target.value)}
                maxLength={100}
              />
              <div className='absolute bottom-2 right-2 text-xs text-gray-500'>
                <span className='text-blue-500'>{description.length}</span>/100
              </div>
            </div>
          </div>
        </CardBody>

        {!nodeData.isToggle && (
          <div className='border-t border-gray-200'>
            <div className='bg-gray-50 px-4 py-3'>
              <h3 className='text-lg font-semibold text-gray-700'>Schema</h3>
            </div>
            <div className='mt-3 px-4'>
              {inputKeys.map((item, index) => renderInputField(item, index))}

              {/* Add Input Button */}
              <div className='flex justify-center mb-3'>
                <button onClick={handleAddInput} className='btn btn-light rounded-md border border-gray-300 px-4 py-2 text-dark'>
                  입력 추가
                </button>
              </div>
            </div>
          </div>
        )}

        <CardFooter>
          <NodeFooter onClick={handleFooterFold} isToggle={nodeData.isToggle as boolean} />
        </CardFooter>
        <Handle
          type='source'
          id='input_right'
          position={Position.Right}
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
