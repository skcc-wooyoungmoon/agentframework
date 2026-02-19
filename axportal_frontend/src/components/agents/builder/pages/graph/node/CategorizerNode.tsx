import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { useAtom } from 'jotai';
import { Handle, type NodeProps, Position, useReactFlow, useUpdateNodeInternals } from '@xyflow/react';
import { hasChatTestedAtom } from '@/components/agents/builder/atoms/logAtom.ts';
import { Card, CardBody, CardFooter } from '@/components/agents/builder/common';
import { LogModal } from '@/components/agents/builder/common/modal/log/LogModal.tsx';
import { useGraphActions } from '@/components/agents/builder/hooks/useGraphActions.ts';
import { CustomScheme } from '@/components/agents/builder/pages/graph/contents/CustomScheme.tsx';
import { SelectLLM } from '@/components/agents/builder/pages/graph/contents/SelectLLM.tsx';
import { getNodeStatus } from '@/components/agents/builder/utils/GraphUtils.ts';
import { useNodeTracing } from '@/components/agents/builder/hooks/useNodeTracing';
import { type CustomEdge, type CustomNode, type CustomNodeInnerData, type InputKeyItem, NodeType, type OutputKeyItem } from '@/components/agents/builder/types/Agents';
import { useModal } from '@/stores/common/modal/useModal';
import { selectedLLMRepoAtom } from '@/components/agents/builder/atoms/llmAtom';

import { NodeFooter, NodeHeader } from '.';
import { useNodeHandler } from '@/components/agents/builder/hooks/useNodeHandler';

interface Category {
  id: string;
  category: string;
  description: string;
}

interface NodeFormState {
  nodeName: string;
  description: string;
  prompt: string;
  servingName: string;
  servingModel: string;
  categories: Category[];
  inputKeys: InputKeyItem[];
  outputKeys: OutputKeyItem[];
}
// setInputKeys('inputKeys', initializedInputKeys);
export const CategorizerNode: React.FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  const { removeNode, toggleNodeView, syncNodeData } = useGraphActions();
  const { getEdges, setEdges } = useReactFlow();
  const updateNodeInternals = useUpdateNodeInternals();
  const { openModal } = useModal();
  const [hasChatTested] = useAtom(hasChatTestedAtom);

  const initialSyncDone = useRef(false);
  const nodesUpdatedRef = useRef(false);

  useEffect(() => {
    if (!initialSyncDone.current) {
      const edges = getEdges();
      const savedCategories = (data.categories as Category[]) || [];

      const validHandleIds = savedCategories.map((_, index) => `handle-category-${index}`);

      const updatedEdges = edges
        .map(edge => {
          const typedEdge = edge as CustomEdge;

          if (typedEdge.source === id) {
            const invalidHandles = ['handle-any_to_kor', 'handle-kor_to_any', 'handle-any', 'handle-kor'];
            if (typedEdge.sourceHandle && invalidHandles.includes(typedEdge.sourceHandle)) {
              return null;
            }
            if (typedEdge.sourceHandle?.startsWith('handle-category-')) {
              const handlePart = typedEdge.sourceHandle.replace('handle-', '');

              if (handlePart.match(/category-\d{13}/)) {
                const categoryIndex = savedCategories.findIndex(cat => cat.id === handlePart);

                if (categoryIndex !== -1 && validHandleIds.includes(`handle-category-${categoryIndex}`)) {
                  return {
                    ...typedEdge,
                    sourceHandle: `handle-category-${categoryIndex}`,
                  };
                } else {
                  return null;
                }
              }
              if (!validHandleIds.includes(typedEdge.sourceHandle)) {
                return null;
              }
            }
          }

          return edge;
        })
        .filter((edge): edge is CustomEdge => edge !== null);

      setEdges(updatedEdges);
      syncCurrentData();
      initialSyncDone.current = true;
    }
  }, []);

  const [formState, setFormState] = useState<NodeFormState>(() => {
    const processCategories = (categories: any[]): Category[] => {
      if (!Array.isArray(categories))
        return [
          {
            id: 'category-0',
            category: 'Category 1',
            description: '',
          },
        ];
      return categories.map((cat, index) => ({
        ...cat,
        id: `category-${index}`,
        category: cat.category || `Category ${index + 1}`,
        description: cat.description || '',
      }));
    };
    const initialCategories = processCategories((data.categories as Category[]) || []);
    const defaultOutputKeys: OutputKeyItem[] = [
      {
        name: 'selected',
        keytable_id: `selected_${id}`,
      },
    ];

    const dataOutputKeys = (data.output_keys as OutputKeyItem[]) || [];
    const outputKeys =
      dataOutputKeys.length > 0
        ? dataOutputKeys.map(output => {
          if (output.name === 'content') {
            return {
              ...output,
              name: 'selected',
              keytable_id: output.keytable_id || `selected_${id}`,
            };
          }
          return output;
        })
        : defaultOutputKeys;

    return {
      nodeName: data.name as string,
      description: (data.description as string) || '',
      prompt: '',
      servingName: (data.serving_name as string) || '',
      servingModel: (data.serving_model as string) || '',
      categories: initialCategories,
      inputKeys: (data.input_keys as InputKeyItem[]) || [],
      outputKeys: outputKeys,
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

  const [selectedLLMRepo] = useAtom(selectedLLMRepoAtom);

  const syncCurrentData = useCallback(() => {
    const normalizedOutputKeys = formState.outputKeys.map(output => {
      if (output.name === 'content') {
        return {
          ...output,
          name: 'selected',
          keytable_id: output.keytable_id || `selected_${id}`,
        };
      }
      return output;
    });

    const finalOutputKeys = normalizedOutputKeys.length > 0 ? normalizedOutputKeys : [{ name: 'selected', keytable_id: `selected_${id}` }];

    const currentLLM = selectedLLMRepo[id];
    const finalServingName = currentLLM?.servingName || formState.servingName || '';
    const finalServingModel = currentLLM?.servingModel || formState.servingModel || '';

    const newData = {
      ...data,
      id: id,
      name: formState.nodeName,
      description: formState.description,
      input_keys: formState.inputKeys,
      output_keys: finalOutputKeys,
      serving_name: finalServingName,
      serving_model: finalServingModel,
      prompt_id: '',
      categories: formState.categories ?? [],
      innerData: {
        ...nodeData,
      },
    };

    syncNodeData(id, newData);
  }, [data, id, nodeData, syncNodeData, formState, selectedLLMRepo]);

  useEffect(() => {
    const currentServingName = (data.serving_name as string) || '';
    const currentServingModel = (data.serving_model as string) || '';

    const dataOutputKeys = (data.output_keys as OutputKeyItem[]) || [];

    const normalizedOutputKeys =
      dataOutputKeys.length > 0
        ? dataOutputKeys.map(output => {
          if (output.name === 'content') {
            return {
              ...output,
              name: 'selected',
              keytable_id: output.keytable_id || `selected_${id}`,
            };
          }
          return output;
        })
        : [{ name: 'selected', keytable_id: `selected_${id}` }];

    const currentOutputName = formState.outputKeys[0]?.name || '';
    const newOutputName = normalizedOutputKeys[0]?.name || '';
    const shouldUpdateOutputKeys = currentOutputName !== newOutputName;

    if (formState.servingName !== currentServingName || formState.servingModel !== currentServingModel || shouldUpdateOutputKeys) {
      setFormState(prev => ({
        ...prev,
        servingName: currentServingName,
        servingModel: currentServingModel,
        outputKeys: normalizedOutputKeys,
      }));

      nodesUpdatedRef.current = true;
    }
  }, [data.serving_name, data.serving_model, data.output_keys, id]);

  useEffect(() => {
    if (nodesUpdatedRef.current) {
      syncCurrentData();
      nodesUpdatedRef.current = false;
    }
  }, [syncCurrentData]);

  const handleFieldChange = useCallback((field: string, value: any) => {
    setFormState(prev => ({
      ...prev,
      [field]: value,
    }));
  }, []);

  const handleFieldWithUpdate = useCallback(
    (newInputKeys: InputKeyItem[] | string, field: string | undefined) => {
      if (!field) return;
      handleFieldChange(field, newInputKeys);
      if (field !== 'inputKeys') {
        nodesUpdatedRef.current = true;
      }
    },
    [handleFieldChange]
  );

  const addCategory = useCallback(() => {
    setFormState(prev => {
      const newIndex = prev.categories.length;
      const newId = `category-${newIndex}`;
      const newCategory = {
        id: newId,
        category: `Category ${newIndex + 1}`,
        description: '',
      };

      const newCategories = [...prev.categories, newCategory];

      return {
        ...prev,
        categories: newCategories,
      };
    });

    nodesUpdatedRef.current = true;

    setEdges(edges => [...edges]);
  }, [setEdges, id]);

  const deleteCategory = useCallback(
    (index: number) => {
      const edges = getEdges();
      let categoryToDelete: Category | null = null;

      setFormState(prev => {
        categoryToDelete = prev.categories[index];

        const remainingCategories = prev.categories.filter((_, i) => i !== index);
        const reindexedCategories = remainingCategories.map((cat, idx) => ({
          ...cat,
          id: `category-${idx}`,
        }));

        const updatedEdges = edges
          .filter(edge => {
            const typedEdge = edge as CustomEdge;
            return !(typedEdge.source === id && typedEdge.sourceHandle === `handle-${categoryToDelete!.id}`);
          })
          .map(edge => {
            const typedEdge = edge as CustomEdge;
            if (typedEdge.source === id && typedEdge.sourceHandle) {
              const oldCategoryIndex = prev.categories.findIndex(cat => `handle-${cat.id}` === typedEdge.sourceHandle);
              if (oldCategoryIndex !== -1 && oldCategoryIndex > index) {
                return {
                  ...typedEdge,
                  sourceHandle: `handle-category-${oldCategoryIndex - 1}`,
                } as CustomEdge;
              }
            }
            return edge as CustomEdge;
          });

        setEdges(updatedEdges as any);
        nodesUpdatedRef.current = true;

        return {
          ...prev,
          categories: reindexedCategories,
        };
      });
    },
    [id, getEdges, setEdges]
  );

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

  const handleLLMChange = useCallback(
    (selectedLLM: any) => {
      const llmName = selectedLLM.name || selectedLLM.modelName || '';
      const servingId = selectedLLM.servingId || '';

      handleFieldChange('servingName', llmName);
      handleFieldChange('servingModel', servingId);
      nodesUpdatedRef.current = true;
    },
    [handleFieldChange]
  );

  useEffect(() => {
    updateNodeInternals(id);
  }, [id, updateNodeInternals, nodeData.isToggle, formState.categories.length]);

  const containerRef = useRef<HTMLDivElement | null>(null);
  const rafRef = useRef<number | null>(null);
  const resizeTimerRef = useRef<number | null>(null);

  useEffect(() => {
    const el = containerRef.current;
    if (!el) return;
    const Rz = typeof window !== 'undefined' ? window.ResizeObserver : undefined;
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
      ro.disconnect();
      if (rafRef.current) cancelAnimationFrame(rafRef.current);
      if (resizeTimerRef.current) window.clearTimeout(resizeTimerRef.current as any);
    };
  }, [id, updateNodeInternals]);

  const { autoResize, stopPropagation, preventAndStop } = useNodeHandler();

  return (
    <div ref={containerRef}>
      <Card className={['agent-card w-full min-w-[500px] max-w-[500px]', nodeStatus].filter(e => !!e).join(' ')} style={{ overflow: 'visible' }}>
        <Handle
          type='target'
          id='categorizer_left'
          key={`categorizer_left_${nodeData.isToggle}`}
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
          onChange={handleFieldWithUpdate}
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
                        handleFieldWithUpdate(value, 'description');
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
                  {formState.categories.map((category, index) => {
                    return (
                      <div key={category.id} className='mt-3 w-full relative px-2'>
                        <div className='nodrag w-full border border-gray-300 rounded-lg p-3 focus:border-gray-400 focus:outline-none mb-3'>
                          <h3 className='text-base font-bold text-gray-700'>{category.category || `Category ${index + 1}`}</h3>
                        </div>

                        <div className='relative w-full'>
                          <textarea
                            className='nodrag w-full resize-none border border-gray-300 rounded-lg p-3 focus:border-gray-400 focus:outline-none'
                            value={category.description}
                            onChange={e => updateCategory(index, 'description', e.target.value)}
                            placeholder='Description'
                            maxLength={100}
                            onMouseDown={stopPropagation}
                            onMouseUp={stopPropagation}
                            onSelect={stopPropagation}
                            onDragStart={preventAndStop}
                            onDrag={preventAndStop}
                          />
                          <div className='absolute bottom-4 right-4 text-xs text-gray-500'>
                            <span className='text-blue-500'>{category.description.length}</span>/100
                          </div>
                        </div>
                        <Handle
                          key={`handle-${index}`}
                          type='source'
                          position={Position.Right}
                          id={`handle-category-${index}`}
                          isConnectable={true}
                          style={{
                            position: 'absolute',
                            top: index === 0 ? '20%' : index === formState.categories.length - 1 ? '80%' : `${20 + (index * 60) / (formState.categories.length - 1)}%`,
                            transform: 'translateY(-50%)',
                            width: 20,
                            height: 20,
                            right: -30,
                            background: '#6B7280',
                            border: '2px solid white',
                            borderRadius: '50%',
                            zIndex: 20,
                          }}
                        />
                      </div>
                    );
                  })}
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
                          handleFieldWithUpdate(value, 'description');
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

                <div className='bg-white'>
                  <div className='p-0'>
                    <div className='p-0 rounded-lg'>
                      <SelectLLM
                        selectedServingName={formState.servingName}
                        selectedServingModel={formState.servingModel}
                        onChange={handleLLMChange}
                        asAccordionItem={true}
                        title={
                          <>
                            {'LLM'}
                            <span className='text-red-500'>*</span>
                          </>
                        }
                      />
                    </div>
                  </div>
                </div>

                <div className='w-full'>
                  {formState.categories.map((category, index) => {
                    return (
                      <div key={category.id} className='relative'>
                        <div className='mt-3 w-full relative px-2'>
                          <div className='mb-2 flex items-center gap-2'>
                            <input
                              type='text'
                              className='nodrag w-full border border-gray-300 rounded-lg p-3 focus:border-gray-400 focus:outline-none'
                              value={category.category}
                              onChange={e => updateCategory(index, 'category', e.target.value)}
                              placeholder='Category Name'
                              onMouseDown={stopPropagation}
                              onMouseUp={stopPropagation}
                            />
                            <button onClick={() => deleteCategory(index)} className='btn-icon btn btn-sm btn-light text-primary' title='삭제'>
                              <img alt='ico-system-24-outline-gray-trash' className='w-[24px] h-[24px]' src='/assets/images/system/ico-system-24-outline-gray-trash.svg' />
                            </button>
                          </div>

                          <div className='relative mb-4'>
                            <textarea
                              className='nodrag w-full border border-gray-300 rounded-lg p-3 focus:border-gray-400 focus:outline-none resize-none'
                              rows={3}
                              value={category.description}
                              onChange={e => updateCategory(index, 'description', e.target.value)}
                              placeholder='Description'
                              maxLength={100}
                              onMouseDown={stopPropagation}
                              onMouseUp={stopPropagation}
                              onSelect={stopPropagation}
                              onDragStart={preventAndStop}
                              onDrag={preventAndStop}
                            />
                            <div className='absolute bottom-2 right-2 text-xs text-gray-500'>
                              <span className='text-blue-500'>{category.description.length}</span>/100
                            </div>
                          </div>
                        </div>

                        <Handle
                          key={`handle-${index}`}
                          type='source'
                          position={Position.Right}
                          id={`handle-category-${index}`}
                          isConnectable={true}
                          style={{
                            position: 'absolute',
                            top: index === 0 ? '20%' : index === formState.categories.length - 1 ? '20%' : `${20 + (index * 0) / (formState.categories.length - 1)}%`,
                            transform: 'translateY(-50%)',
                            width: 20,
                            height: 20,
                            right: -30,
                            background: '#6B7280',
                            border: '2px solid white',
                            borderRadius: '50%',
                            zIndex: 20,
                          }}
                        />
                      </div>
                    );
                  })}

                  <div className='flex justify-center'>
                    <button
                      onClick={addCategory}
                      className='bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded-lg border-0 transition-colors duration-200  mt-3'
                    >
                      + 카테고리 추가
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
                setInputKeys={handleFieldWithUpdate}
                inputValues={inputValues}
                setInputValues={setInputValues}
                innerData={data.innerData}
                outputKeys={formState.outputKeys}
                type={NodeType.AgentCategorizer.name}
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
