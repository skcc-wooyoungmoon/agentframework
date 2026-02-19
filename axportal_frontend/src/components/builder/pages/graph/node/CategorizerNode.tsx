import { Card, CardBody, CardFooter, LogModal } from '@/components/builder/common/index.ts';
import { ABClassNames } from '@/components/builder/components/ui';
import { useGraphActions } from '@/components/builder/hooks/useGraphActions.ts';
import { CustomScheme } from '@/components/builder/pages/graph/contents/CustomScheme.tsx';
import { SelectLLM } from '@/components/builder/pages/graph/contents/SelectLLM.tsx';
import { type CustomEdge, type CustomNode, type CustomNodeInnerData, type InputKeyItem, NodeType, type OutputKeyItem } from '@/components/builder/types/Agents';
import keyTableData from '@/components/builder/types/keyTableData.json';
import { getNodeStatus } from '@/components/builder/utils/GraphUtils.ts';
import { UIImage } from '@/components/UI/atoms/UIImage';
import { useModal } from '@/stores/common/modal/useModal';
import { Handle, type NodeProps, Position, useReactFlow, useUpdateNodeInternals } from '@xyflow/react';
import { useAtom } from 'jotai';
import React, { useCallback, useEffect, useState } from 'react';
import { NodeFooter, NodeHeader } from '.';
import { logState } from '../../..';
import { useAutoUpdateNodeInternals } from '../../../hooks/useAutoUpdateNodeInternals';

interface Category {
  id: string;
  category: string;
  description: string;
}

interface NodeFormState {
  nodeName: string;
  description: string;
  servingName: string;
  servingModel: string;
  categories: Category[];
  inputKeys: InputKeyItem[];
  outputKeys: OutputKeyItem[];
}

export const CategorizerNode: React.FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  // console.log('🔍 CategorizerNode data:', data);

  const { removeNode, toggleNodeView, syncNodeData } = useGraphActions();
  const { getEdges, setEdges } = useReactFlow();
  const updateNodeInternals = useUpdateNodeInternals();

  const [, setLogData] = useAtom(logState);
  const { openModal } = useModal();

  const [formState, setFormState] = useState<NodeFormState>(() => {
    const processCategories = (categories: any[]): Category[] => {
      if (!Array.isArray(categories))
        return [
          // {
          //   id: 'category-1',
          //   category: 'Category 1',
          //   description: '',
          // },
        ];

      // 항상 인덱스 기반 ID 사용 (일관성 유지)
      return categories.map(cat => ({
        ...cat,
        id: cat.id || '',
      }));
    };

    const initialCategories = processCategories((data.categories as Category[]) || []);

    return {
      nodeName: data.name as string,
      description: (data.description as string) || (keyTableData['agent__categorizer']['field_default']['description'] as string),
      servingName: (data.serving_name as string) || '',
      servingModel: (data.serving_model as string) || '',
      categories: initialCategories,
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
      //node state
      serving_name: formState.servingName,
      serving_model: formState.servingModel,
      categories: formState.categories ?? [],
      // public node state
      innerData: {
        ...nodeData,
      },
    };

    syncNodeData(id, newData);
  }, [data, id, nodeData, syncNodeData, formState]);

  // formState 변경 시 자동 동기화
  useEffect(() => {
    syncCurrentData();
  }, [formState]);

  const handleFieldChange = useCallback((field: keyof NodeFormState, value: any) => {
    setFormState(prev => ({
      ...prev,
      [field]: value,
    }));
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

  const addCategory = useCallback(() => {
    setFormState(prev => {
      // 인덱스 기반 ID 생성 (일관성 유지)
      const newId = `${id}-Category-${prev.categories.length + 1}`;
      const newCategory = {
        id: newId,
        category: newId,
        description: '',
      };

      return {
        ...prev,
        categories: [...prev.categories, newCategory],
      };
    });
  }, []);

  const deleteCategory = useCallback((index: number) => {
    setFormState(prev => ({
      ...prev,
      categories: prev.categories.filter((_, i) => i !== index),
    }));

    const edges = getEdges();
    setEdges(
      edges.filter(edge => {
        const typedEdge = edge as CustomEdge;
        return typedEdge.sourceHandle !== `handle-${id}-Category-${index + 1}`;
      })
    );
  }, []);

  const updateCategory = useCallback(
    (index: number, field: 'category' | 'description', value: string) => {
      setFormState(prev => {
        const updatedCategories = prev.categories.map((category, i) => (i === index ? { ...category, [field]: value } : category));

        if (field === 'category') {
          const edges = getEdges();
          const categoryId = prev.categories[index].id;

          setEdges(
            edges.map(edge => {
              const typedEdge = edge as CustomEdge;

              if (typedEdge.source === id && (typedEdge.sourceHandle === `handle-${categoryId}` || typedEdge.data?.category?.id === categoryId)) {
                const updatedData = {
                  ...(typedEdge.data || {}),
                  category: {
                    ...(typedEdge.data?.category || {}),
                    id: categoryId,
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
          categories: updatedCategories,
        };
      });
    },
    [id, getEdges, setEdges]
  );

  const onClickDelete = () => {
    removeNode(id);
  };

  const handleFooterFold = (bool: boolean) => {
    toggleNodeView(id, bool);
  };

  const handleLLMChange = (selectedLLM: any) => {
    handleFieldChange('servingName', selectedLLM?.name || '');
    handleFieldChange('servingModel', selectedLLM?.id || ''); // lineage 정보에 저장할 serving_model 값
  };

  // Handle 위치/개수가 바뀔 때 노드 내부 레이아웃 재계산 (접힘/펼침, 카테고리 수 변화 등)
  useEffect(() => {
    updateNodeInternals(id);
  }, [id, updateNodeInternals, nodeData.isToggle, formState.categories.length]);

  // 노드 내부 아코디언 및 콘텐츠 높이 변화 감지하여 연결선 재계산 (Generator와 동일한 방식)
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
      <Card className={ABClassNames('agent-card w-full min-w-[500px] max-w-[500px]', nodeStatus)} style={{ overflow: 'visible' }}>
        <Handle
          type='target'
          id='categorizer_left'
          key={`categorizer_left_${nodeData.isToggle}`}
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
        </CardBody>
        {!nodeData.isToggle && (
          <>
            <div className='border-t border-gray-200'>
              <CardBody className='p-4'>
                <SelectLLM
                  selectedServingName={formState.servingName}
                  selectedServingModel={formState.servingModel}
                  onChange={handleLLMChange}
                  asAccordionItem={true}
                  title={
                    <>
                      {'LLM'}
                      <span className='ag-color-red'>*</span>
                    </>
                  }
                />
              </CardBody>

              <CardBody className='p-4'>
                <div className='w-full'>
                  {formState.categories.map((category, index) => {
                    return (
                      <div key={category.id} className='border border-gray-200 rounded-lg mb-3 relative'>
                        <div className='mt-3 w-full relative px-2'>
                          <div className='mb-2 flex items-center gap-2'>
                            <input
                              type='text'
                              className='nodrag w-full border border-gray-300 rounded-lg p-3 focus:border-gray-400 focus:outline-none'
                              value={category.category}
                              onChange={e => updateCategory(index, 'category', e.target.value)}
                              placeholder='Category Name'
                            />
                            {index === formState?.categories?.length - 1 && (
                              <button
                                onClick={() => deleteCategory(index)}
                                className='flex h-[20px] w-[20px] items-center justify-center rounded-md hover:bg-gray-100 cursor-pointer ml-auto'
                                title='삭제'
                                style={{
                                  backgroundColor: '#ffffff',
                                  border: '1px solid #d1d5db',
                                  borderRadius: '6px',
                                  padding: '6px',
                                  color: '#6b7280',
                                  cursor: 'pointer',
                                  fontSize: '16px',
                                  transition: 'all 0.2s ease',
                                  minWidth: '32px',
                                  width: '32px',
                                  height: '32px',
                                }}
                              >
                                <UIImage src='/assets/images/system/ico-system-24-outline-gray-trash.svg' alt='No data' className='w-20 h-20' />
                              </button>
                            )}
                          </div>

                          <div className='relative mb-4'>
                            <textarea
                              className='nodrag w-full border border-gray-300 rounded-lg p-3 focus:border-gray-400 focus:outline-none resize-none'
                              rows={3}
                              value={category.description}
                              onChange={e => updateCategory(index, 'description', e.target.value)}
                              placeholder='Description'
                              maxLength={100}
                            />
                            <div className='absolute bottom-2 right-2 text-xs text-gray-500'>
                              <span className='text-blue-500'>{category.description.length}</span>/100
                            </div>
                          </div>
                        </div>

                        {/* Handle - 카테고리 위치에 따라 배치 (편집 모드) */}
                        <Handle
                          key={`handle-${index}`}
                          type='source'
                          position={Position.Right}
                          id={`handle-${category.id}`}
                          style={{
                            position: 'absolute',
                            // 첫 번째 카테고리는 위쪽, 두 번째 카테고리는 아래쪽에 배치
                            top: index === 0 ? '20%' : index === formState.categories.length - 1 ? '20%' : `${20 + (index * 0) / (formState.categories.length - 1)}%`,
                            transform: 'translateY(-50%)',
                            width: 20,
                            height: 20,
                            right: -30,
                            background: '#000000',
                            border: '2px solid white',
                            borderRadius: '50%',
                            zIndex: 20,
                          }}
                        />
                      </div>
                    );
                  })}

                  <div className='flex justify-center'>
                    <button onClick={addCategory} className='btn btn-light rounded-md border border-gray-300 px-4 py-2 text-dark'>
                      카테고리 추가
                    </button>
                  </div>
                </div>
              </CardBody>
            </div>

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
              type={NodeType.AgentCategorizer.name}
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
