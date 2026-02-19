import { edgesAtom, nodesAtom } from '@/components/agents/builder/atoms/AgentAtom';
import { hasChatTestedAtom } from '@/components/agents/builder/atoms/logAtom.ts';
import { Card, CardBody, CardFooter } from '@/components/agents/builder/common/index.ts';
import { LogModal } from '@/components/agents/builder/common/modal/log/LogModal.tsx';
import { useGraphActions } from '@/components/agents/builder/hooks/useGraphActions.ts';
import { CustomScheme } from '@/components/agents/builder/pages/graph/contents/CustomScheme.tsx';
import { UICode } from '@/components/UI/atoms/UICode';
import { type CoderDataSchema, type CustomNode, type CustomNodeInnerData, type InputKeyItem, NodeType, type OutputKeyItem } from '@/components/agents/builder/types/Agents';
import { Handle, type NodeProps, Position, useUpdateNodeInternals } from '@xyflow/react';
import { getNodeStatus } from '@/components/agents/builder/utils/GraphUtils.ts';
import { useNodeTracing } from '@/components/agents/builder/hooks/useNodeTracing';
import { useModal } from '@/stores/common/modal/useModal';
import { useAtom } from 'jotai';
import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { createPortal } from 'react-dom';
import { NodeFooter, NodeHeader } from '.';
import { useNodeHandler } from '@/components/agents/builder/hooks/useNodeHandler';

interface NodeFormState {
  nodeName: string;
  description: string;
  code: string;
  inputKeys: InputKeyItem[];
  outputKeys: OutputKeyItem[];
}

export const CodeNode: React.FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  const { removeNode, toggleNodeView, syncNodeData } = useGraphActions();
  const updateNodeInternals = useUpdateNodeInternals();
  const { openModal } = useModal();
  const [hasChatTested] = useAtom(hasChatTestedAtom);
  const [edges] = useAtom(edgesAtom);
  const [allNodes] = useAtom(nodesAtom);

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

  // 사용자가 저장한 값 그대로 사용 (필터링이나 자동 추가 없음)
  const initialInputKeys = useMemo(() => {
    const loadedKeys = (data.input_keys as InputKeyItem[]) || [];
    // 저장된 값이 있으면 그대로 사용
    if (loadedKeys.length > 0) {
      return loadedKeys;
    }

    // input_keys가 비어있고 Generator 노드에 연결되어 있으면 자동 생성 (초기 생성 시에만)
    const incomingEdges = edges.filter(edge => edge.target === id);
    for (const edge of incomingEdges) {
      const sourceNode = allNodes.find(node => node.id === edge.source);
      if (sourceNode && sourceNode.type === 'agent__generator') {
        const sourceOutputKeys = (sourceNode.data as any)?.output_keys || [];
        if (Array.isArray(sourceOutputKeys) && sourceOutputKeys.length > 0) {
          // Generator의 output_keys를 Code의 input_keys로 생성
          return sourceOutputKeys.map((outputKey: any) => ({
            name: outputKey.name,
            required: true,
            keytable_id: outputKey.keytable_id || '',
            fixed_value: null,
          }));
        }
      }
    }

    return loadedKeys;
  }, [data.input_keys, edges, allNodes, id]);

  const [formState, setFormState] = useState<NodeFormState>({
    nodeName: data.name as string,
    description: (data.description as string) || '',
    code: (data as CoderDataSchema).code || '',
    inputKeys: initialInputKeys,
    outputKeys: (data.output_keys as OutputKeyItem[]) || [],
  });

  const [inputValues, setInputValues] = useState<string[]>(formState.inputKeys.map(item => item.name));
  const [isCodeModalOpen, setIsCodeModalOpen] = useState(false);
  const nodesUpdatedRef = useRef(false);
  const isInitialMountRef = useRef(true);
  const prevDataRef = useRef(data);
  const prevCodeRef = useRef<string>(formState.code);
  const prevSetInputKeysRef = useRef<string>('');

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

    const currentInputKeys = (data.input_keys as InputKeyItem[]) || [];
    const prevInputKeys = (prevDataRef.current.input_keys as InputKeyItem[]) || [];

    const inputKeysChanged = JSON.stringify(currentInputKeys) !== JSON.stringify(prevInputKeys);

    if (inputKeysChanged) {
      const currentKeysStr = JSON.stringify(currentInputKeys.map(k => k.name));
      const prevKeysStr = JSON.stringify((prevDataRef.current?.input_keys as InputKeyItem[] || []).map(k => k.name));

      if (currentKeysStr !== prevKeysStr) {
        const prevInputKeys = (prevDataRef.current?.input_keys as InputKeyItem[]) || [];
        const preservedKeys = currentInputKeys.map(newKey => {
          const existingKey = prevInputKeys.find(ek => ek.name === newKey.name);
          if (existingKey && (existingKey.keytable_id || existingKey.fixed_value !== null && existingKey.fixed_value !== undefined)) {
            return {
              ...newKey,
              keytable_id: existingKey.keytable_id || newKey.keytable_id || '',
              fixed_value: existingKey.fixed_value !== null && existingKey.fixed_value !== undefined ? existingKey.fixed_value : (newKey.fixed_value !== undefined ? newKey.fixed_value : null),
            };
          }
          return newKey;
        });
        const preservedKeysStr = JSON.stringify(preservedKeys);
        if (preservedKeysStr !== prevSetInputKeysRef.current && !nodesUpdatedRef.current) {
          prevSetInputKeysRef.current = preservedKeysStr;

          setFormState(prev => ({
            ...prev,
            inputKeys: preservedKeys,
          }));
        }
      }
    }

    const currentOutputKeys = (data.output_keys as OutputKeyItem[]) || [];
    const prevOutputKeys = (prevDataRef.current.output_keys as OutputKeyItem[]) || [];
    const outputKeysChanged = JSON.stringify(currentOutputKeys) !== JSON.stringify(prevOutputKeys);

    if (outputKeysChanged) {
      setFormState(prev => ({
        ...prev,
        outputKeys: currentOutputKeys,
      }));
    }

    const currentCode = (data as CoderDataSchema).code || '';
    const prevCode = ((prevDataRef.current as CoderDataSchema)?.code) || '';
    const codeChanged = currentCode !== prevCode;

    if (codeChanged && !isCodeModalOpen) {
      if (formState.code !== currentCode) {
        setFormState(prev => ({
          ...prev,
          code: currentCode,
        }));
        prevCodeRef.current = currentCode;
      } else {
        prevCodeRef.current = currentCode;
      }
    } else if (!codeChanged) {
      prevCodeRef.current = currentCode;
    }

    prevDataRef.current = data;
  }, [data, data.input_keys, data.output_keys, isCodeModalOpen, id]);

  const prevInputKeysRef = useRef<string>(JSON.stringify(formState.inputKeys));

  const syncCurrentData = useCallback(() => {
    const newInnerData = {
      ...nodeData,
    };
    const inputKeysToSave = Array.isArray(formState.inputKeys)
      ? formState.inputKeys.filter((key: InputKeyItem) => key !== null && key !== undefined)
      : [];

    const newData = {
      ...data,
      type: NodeType.AgentCoder.name,
      id: id,
      name: formState.nodeName,
      description: formState.description,
      code: formState.code,
      input_keys: inputKeysToSave,
      output_keys: formState.outputKeys,
      innerData: newInnerData,
    };

    syncNodeData(id, newData);
  }, [id, data, formState.nodeName, formState.description, formState.code, formState.inputKeys, formState.outputKeys, nodeData, syncNodeData]);

  useEffect(() => {
    const currentInputKeysStr = JSON.stringify(formState.inputKeys);
    if (currentInputKeysStr !== prevInputKeysRef.current) {
      prevInputKeysRef.current = currentInputKeysStr;
      nodesUpdatedRef.current = true;
    }
  }, [formState.inputKeys]);

  useEffect(() => {
    if (prevCodeRef.current !== formState.code) {
      prevCodeRef.current = formState.code;
      if (!isCodeModalOpen) {
        nodesUpdatedRef.current = true;
      }
    }
  }, [formState.code, isCodeModalOpen]);

  useEffect(() => {
    if (nodesUpdatedRef.current && !isCodeModalOpen) {
      nodesUpdatedRef.current = false;
      syncCurrentData();
    }
  }, [formState.nodeName, formState.description, formState.inputKeys, formState.code, syncCurrentData, id, isCodeModalOpen]);

  useEffect(() => {
    updateNodeInternals(id);
  }, [id, updateNodeInternals, nodeData.isToggle]);

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

  const onClickDelete = () => {
    removeNode(id);
  };

  const handleFooterFold = (bool: boolean) => {
    toggleNodeView(id, bool);
  };

  const handleCodeChange = useCallback(
    (newCode: string) => {
      setFormState(prev => ({
        ...prev,
        code: newCode,
      }));
      if (!isCodeModalOpen) {
        nodesUpdatedRef.current = true;
      }
    },
    [isCodeModalOpen]
  );
  const { autoResize, stopPropagation, preventAndStop } = useNodeHandler()
  return (
    <>
      <div ref={containerRef}>
        <Card className={['agent-card w-full min-w-[500px] max-w-[500px]', nodeStatus].filter(e => !!e).join(' ')}>
          <Handle
            type='target'
            id='code_left'
            key={`code_left_${nodeData.isToggle}_${Date.now()}`}
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
            onClickDelete={onClickDelete}
            defaultValue={formState.nodeName}
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
                </CardBody>
                <CardBody>
                  <div className='mb-4'>
                    <div className='flex items-center justify-between mb-3'>
                      <div className='flex items-center gap-2'>
                        <span className='text-lg font-bold text-gray-700'>코드</span>
                        <span className='ag-color-red'>*</span>
                      </div>
                      <button
                        className='px-4 py-2 text-sm font-medium bg-blue-500 text-white rounded hover:bg-blue-600 transition-colors'
                        onClick={() => setIsCodeModalOpen(true)}
                        style={{
                          backgroundColor: '#3b82f6',
                          color: 'white',
                          padding: '8px 16px',
                          borderRadius: '4px',
                          border: 'none',
                          cursor: 'pointer',
                        }}
                      >
                        코드 수정
                      </button>
                    </div>
                    <UICode value={formState.code} onChange={handleCodeChange} language='python' theme='dark' width='100%' minHeight='300px' maxHeight='484px' readOnly={false} />
                  </div>
                </CardBody>

                <div className='bg-gray-50 px-4 py-3 border-t border-gray-200'>
                  <h3 className='tetext-lg font-semibold text-gray-700'>Schema</h3>
                </div>

                <CustomScheme
                  id={id}
                  inputKeys={formState.inputKeys}
                  setInputKeys={handleInputKeysChange}
                  inputValues={inputValues}
                  setInputValues={setInputValues}
                  innerData={data.innerData}
                  outputKeys={formState.outputKeys}
                  type={NodeType.AgentCoder.name}
                />
              </>
            )}
          </>
          <CardFooter>
            <NodeFooter onClick={handleFooterFold} isToggle={nodeData.isToggle as boolean} />
          </CardFooter>
          <Handle
            type='source'
            id='code_right'
            key={`code_right_${nodeData.isToggle}_${Date.now()}`}
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
      {isCodeModalOpen &&
        createPortal(
          <div
            className='fixed inset-0 bg-black/60 flex items-center justify-center'
            style={{
              zIndex: 99999,
              position: 'fixed',
              top: 0,
              left: 0,
              right: 0,
              bottom: 0,
              width: '100vw',
              height: '100vh',
            }}
          >
            <div
              className='bg-white rounded-lg shadow-xl'
              style={{
                zIndex: 100000,
                width: '70vw',
                height: '670px',
                maxWidth: '1200px',
                maxHeight: '800px',
              }}
            >
              <div className='flex items-center justify-between p-4 border-b border-gray-200'>
                <div className='flex items-center gap-2'>
                  <span className='text-xl font-bold text-gray-700'>코드 수정</span>
                  <span className='ag-color-red'>*</span>
                </div>
                <button className='text-gray-500 hover:text-gray-700 text-2xl font-bold' onClick={() => setIsCodeModalOpen(false)}>
                  ×
                </button>
              </div>
              <div className='p-4' style={{ width: '100%', overflow: 'hidden' }}>
                <UICode value={formState.code} onChange={handleCodeChange} language='python' theme='dark' width='100%' minHeight='510px' maxHeight='510px' readOnly={false} />
              </div>
              <div className='flex justify-center p-2 border-t border-gray-200'>
                <button
                  className='px-6 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition-colors'
                  onClick={() => {
                    syncCurrentData();
                    setIsCodeModalOpen(false);
                  }}
                >
                  저장
                </button>
              </div>
            </div>
          </div>,
          document.body
        )}
    </>
  );
};
