import { Card, CardBody, CardFooter, LogModal } from '@/components/builder/common/index.ts';
import { ABClassNames } from '@/components/builder/components/ui';
import { useAutoUpdateNodeInternals } from '@/components/builder/hooks/useAutoUpdateNodeInternals';
import { useGraphActions } from '@/components/builder/hooks/useGraphActions.ts';
import { CustomScheme } from '@/components/builder/pages/graph/contents/CustomScheme.tsx';
import { SelectKnowledge } from '@/components/builder/pages/graph/contents/SelectKnowledge.tsx';
import {
  type CustomNode,
  type CustomNodeInnerData,
  type InputKeyItem,
  type KnowledgeRetriever,
  NodeType,
  type OutputKeyItem,
  type RetrievalOptions,
  type RetrieverDataSchema,
} from '@/components/builder/types/Agents';
import keyTableData from '@/components/builder/types/keyTableData.json';
import { getNodeStatus } from '@/components/builder/utils/GraphUtils.ts';
import { UISlider } from '@/components/UI/atoms';
import { useModal } from '@/stores/common/modal/useModal';
import { Handle, type NodeProps, Position } from '@xyflow/react';
import { useAtom } from 'jotai';
import { type FC, useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { NodeFooter, NodeHeader } from '.';
import { logState } from '../../..';

export const RetrieverNode: FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  // console.log('🔍 RetrieverNode!!!!!!!!!!!!!:', data, id, type);
  const [, setLogData] = useAtom(logState);
  const { openModal } = useModal();

  const { removeNode, toggleNodeView, syncNodeData } = useGraphActions();

  const newInnerData: CustomNodeInnerData = {
    isRun: false,
    isDone: false,
    isError: false,
    isToggle: false,
  };
  const nodesUpdatedRef = useRef(false);

  // 🔥 샘플 프로젝트 방식: useMemo 제거, 직접 접근
  const innerData: CustomNodeInnerData = (data.innerData as CustomNodeInnerData) ?? newInnerData;

  const [nodeStatus, setNodeStatus] = useState<string | null>(null);
  useEffect(() => {
    const status = getNodeStatus(innerData.isRun, innerData.isDone, innerData.isError);
    setNodeStatus(status);
  }, [innerData.isRun, innerData.isDone, innerData.isError]);

  const initializedRef = useRef(false);
  const [inputKeys, setInputKeys] = useState<InputKeyItem[]>(() => (Array.isArray(data.input_keys) ? (data.input_keys as InputKeyItem[]).map(key => ({ ...key })) : []));
  const [inputValues, setInputValues] = useState<string[]>(() => inputKeys.map(item => item.name));

  const outputKeys = useMemo(() => (Array.isArray(data.output_keys) ? (data.output_keys as OutputKeyItem[]) : []), [data.output_keys]);

  const defaultRetrieverOptions = keyTableData['retriever__knowledge']['field_default']['retrieval_options'] as Record<string, any>;

  // 값 초기화
  const [formState, setFormState] = useState<RetrieverDataSchema>(() => {
    const retrieverData = data as Partial<RetrieverDataSchema>;
    const knowledgeRetriever = (retrieverData?.knowledge_retriever ?? {}) as Partial<KnowledgeRetriever>;
    const options = (knowledgeRetriever.retrieval_options ?? {}) as Partial<RetrievalOptions>;

    return {
      type: NodeType.RetrieverRetriever.name,
      id,
      name: data.name as string,
      description: (data.description as string) || keyTableData['retriever__knowledge']['field_default']['description'],
      knowledge_retriever: {
        script: knowledgeRetriever.script ?? '',
        repo_id: knowledgeRetriever.repo_id ?? '',
        repo_kind: knowledgeRetriever.repo_kind ?? 'repo_ext',
        index_name: knowledgeRetriever.index_name ?? '',
        project_id: knowledgeRetriever.project_id ?? '',
        embedding_info: knowledgeRetriever.embedding_info ?? null,
        knowledge_info: knowledgeRetriever.knowledge_info ?? null,
        vectordb_conn_info: knowledgeRetriever.vectordb_conn_info ?? null,
        active_collection_id: knowledgeRetriever.active_collection_id ?? '',
        retrieval_options: {
          top_k: options.top_k ?? defaultRetrieverOptions?.top_k ?? 5,
          filter: options.filter ?? null,
          file_ids: options.file_ids ?? null,
          keywords: options.keywords ?? null,
          order_by: options.order_by ?? 'doc_rank',
          threshold: options.threshold ?? defaultRetrieverOptions?.threshold ?? 0.7,
          vector_field: options.vector_field ?? null,
          retrieval_mode: options.retrieval_mode ?? 'dense',
          hybrid_dense_ratio: options.hybrid_dense_ratio ?? 0.5,
        },
      },
      input_keys: inputKeys,
      output_keys: outputKeys,
    };
  });

  const [sparseRatio, setSparseRatio] = useState<number>(1 - Number(formState.knowledge_retriever.retrieval_options?.hybrid_dense_ratio));

  useEffect(() => {
    setInputValues(inputKeys.map(item => item.name));
  }, [inputKeys]);

  // inputKeys가 변경될 때만 별도로 동기화 (keywords는 제외)
  useEffect(() => {
    const newData = {
      ...data,
      input_keys: inputKeys,
    };
    syncNodeData(id, newData);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [inputKeys]);

  useEffect(() => {
    if (!initializedRef.current && Array.isArray(data.input_keys)) {
      setInputKeys((data.input_keys as InputKeyItem[]).map(key => ({ ...key })));
      initializedRef.current = true;
    }
  }, [data.input_keys]);

  // formState 동기화
  const syncCurrentData = () => {
    const newData = {
      ...data,
      type: formState.type,
      id: formState.id,
      name: formState.name,
      description: formState.description,
      knowledge_retriever: {
        script: formState.knowledge_retriever?.script ?? '',
        repo_id: formState.knowledge_retriever?.repo_id,
        repo_kind: formState.knowledge_retriever?.repo_kind ?? 'repo_ext',
        index_name: formState.knowledge_retriever?.index_name ?? '',
        project_id: formState.knowledge_retriever?.project_id ?? '',
        embedding_info: formState.knowledge_retriever?.embedding_info ?? null,
        knowledge_info: formState.knowledge_retriever?.knowledge_info ?? null,
        vectordb_conn_info: formState.knowledge_retriever?.vectordb_conn_info ?? null,
        active_collection_id: formState.knowledge_retriever?.active_collection_id ?? '',
        retrieval_options: {
          top_k: formState.knowledge_retriever?.retrieval_options?.top_k ?? 5,
          filter: formState.knowledge_retriever?.retrieval_options?.filter ?? null,
          file_ids: formState.knowledge_retriever?.retrieval_options?.file_ids ?? null,
          keywords: formState.knowledge_retriever?.retrieval_options?.keywords ?? null,
          order_by: formState.knowledge_retriever?.retrieval_options?.order_by ?? 'doc_rank',
          threshold: formState.knowledge_retriever?.retrieval_options?.threshold ?? 0.0,
          vector_field: formState.knowledge_retriever?.retrieval_options?.vector_field ?? 'vector',
          retrieval_mode: formState.knowledge_retriever?.retrieval_options?.retrieval_mode ?? 'dense',
          hybrid_dense_ratio: formState.knowledge_retriever?.retrieval_options?.hybrid_dense_ratio ?? 0.5,
        },
      },
      input_keys: Array.isArray(inputKeys) ? [...inputKeys] : [],
      output_keys: Array.isArray(outputKeys) ? [...outputKeys] : [],
      innerData: {
        isToggle: innerData?.isToggle ?? false,
      },
      // 기존 data의 필수 필드들만 선별적으로 복사
      position: data?.position,
      measured: data?.measured,
      selected: data?.selected,
      dragging: data?.dragging,
    };

    syncNodeData(id, newData);
  };

  const prevFormStateRef = useRef<string | undefined>(undefined);
  const prevOutputKeysRef = useRef<string | undefined>(undefined);

  useEffect(() => {
    const currentFormState = JSON.stringify(formState);
    const currentOutputKeys = JSON.stringify(outputKeys);

    // 이전 값과 비교하여 변경되지 않았으면 스킵
    if (prevFormStateRef.current === currentFormState && prevOutputKeysRef.current === currentOutputKeys) {
      return;
    }

    prevFormStateRef.current = currentFormState;
    prevOutputKeysRef.current = currentOutputKeys;

    // console.log('🔍 useEffect - 값 변경 감지:', formState, outputKeys);
    syncCurrentData();
  }, [formState, outputKeys]);

  const handleFieldChange = (field: keyof RetrieverDataSchema, value: any) => {
    setFormState(prev => ({
      ...prev,
      [field]: value,
    }));
    nodesUpdatedRef.current = true;
  };

  const handleOptionFieldChange = (field: keyof RetrievalOptions, value: any) => {
    setFormState(prev => {
      const newState = {
        ...prev,
        knowledge_retriever: {
          ...prev.knowledge_retriever,
          retrieval_options: prev.knowledge_retriever.retrieval_options
            ? {
                ...prev.knowledge_retriever.retrieval_options,
                [field]: value,
              }
            : {
                top_k: field === 'top_k' ? value : null,
                threshold: field === 'threshold' ? value : null,
                vector_field: field === 'vector_field' ? value : null,
                file_ids: field === 'file_ids' ? value : null,
                keywords: field === 'keywords' ? value : null,
                order_by: field === 'order_by' ? value : 'doc_rank',
                retrieval_mode: field === 'retrieval_mode' ? value : 'dense',
                hybrid_dense_ratio: field === 'hybrid_dense_ratio' ? value : 0.5,
              },
        },
      };

      return newState;
    });
    nodesUpdatedRef.current = true;
  };

  const handleFooterFold = (isFold: boolean) => {
    toggleNodeView(id, isFold);
  };

  const handleDelete = () => {
    removeNode(id);
  };

  const handleNodeNameChange = useCallback((value: string) => {
    handleFieldChange('name', value);
  }, []);

  const handleDescriptionChange = useCallback((value: string) => {
    handleFieldChange('description', value);
  }, []);

  const handleTopKChange = useCallback((value: string) => {
    handleOptionFieldChange('top_k', value);
  }, []);

  const handleThresholdChange = useCallback((value: string) => {
    const _value = getThresholdValue(value);

    handleOptionFieldChange('threshold', _value);
  }, []);

  const handleRetrievalModeChange = useCallback(
    (value: string) => {
      handleOptionFieldChange('retrieval_mode', value);

      // dense 모드일 때는 keywords를 null로 설정
      if (value === 'dense') {
        handleOptionFieldChange('keywords', null);
      }

      // hybrid 모드가 아니면 hybrid_dense_ratio를 null로 설정
      if (value !== 'hybrid') {
        handleOptionFieldChange('hybrid_dense_ratio', null);
      } else {
        // hybrid 모드로 바뀔 때 hybrid_dense_ratio가 null이면 기본값 0.5로 설정
        const currentRatio = formState.knowledge_retriever?.retrieval_options?.hybrid_dense_ratio;
        if (currentRatio === null || currentRatio === undefined) {
          handleOptionFieldChange('hybrid_dense_ratio', 0.5);
        }
      }
    },
    [formState.knowledge_retriever?.retrieval_options?.hybrid_dense_ratio]
  );

  const handleHybridDenseRatioChange = useCallback((value: string) => {
    const _value = getRatioValue(value);

    handleOptionFieldChange('hybrid_dense_ratio', _value ?? null);
  }, []);

  const handleSparseRatioChange = useCallback((value: string) => {
    // sparseRatio 값 세팅
    const _value = getRatioValue(value);
    setSparseRatio(_value ?? 0);

    // denseRatio 값 세팅
    const denseRatio = getRatioValue((1 - _value).toString());
    handleOptionFieldChange('hybrid_dense_ratio', denseRatio ?? null);
  }, []);

  // 지식 선택 시 formState 업데이트 함수
  // 그래프 업데이트 시 knowledge_retriever하위 repo_id, repo_kind, retrieval_options 필드만 업데이트
  const handleKnowledgeUpdate = useCallback((selectedKnowledge: any) => {
    // console.log('🔍 handleKnowledgeUpdate 호출:', selectedKnowledge);

    setFormState(prev => ({
      ...prev,
      knowledge_retriever: {
        ...prev.knowledge_retriever,
        repo_id: selectedKnowledge.id, // ADXP ID
        repo_kind: selectedKnowledge.repo_kind,
      },
    }));
  }, []);

  // 텍스트 입력값 정규화
  const getRatioValue = (value: string) => {
    let floatValue = parseFloat(value) || 0;
    if (floatValue <= 0) return 0.1; // 0 이하 입력 시 최소값
    if (floatValue >= 1) return 0.9; // 1 이상 입력 시 최대값
    floatValue = parseFloat(floatValue.toFixed(1));

    // 값 정규화 (0.1~0.9 범위, 0.1 단위)
    const rounded = Math.round(floatValue * 10) / 10;
    return Math.min(Math.max(rounded, 0.1), 0.9);
  };

  // 텍스트 입력값 정규화
  const getThresholdValue = (value: string) => {
    let floatValue = parseFloat(value);

    // 1 이상 값은 1로 제한
    if (floatValue >= 1) {
      return 1;
    }

    // 0 이하 값은 0으로 제한
    if (floatValue <= 0) {
      return 0;
    }

    // 0~1 사이의 값은 소수점 2자리로 버림
    return Math.floor(floatValue * 100) / 100;
  };

  // formState의 hybrid_dense_ratio 변경 시 sparseRatio 동기화 (1 - hybrid_dense_ratio)
  useEffect(() => {
    const currentRatio = formState.knowledge_retriever?.retrieval_options?.hybrid_dense_ratio;

    if (currentRatio !== undefined && currentRatio !== null) {
      const sparseValue = 1 - Number(currentRatio);
      const normalizedSparseValue = getRatioValue(sparseValue.toString());
      setSparseRatio(normalizedSparseValue ?? 0);
    }
  }, [formState.knowledge_retriever?.retrieval_options?.hybrid_dense_ratio]);

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
          id={`retriever_left_${id}`}
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
          nodeId={id}
          type={type}
          data={innerData}
          defaultValue={formState.name ?? ''}
          onClickLog={handleHeaderClickLog}
          onChange={handleNodeNameChange}
          onClickDelete={handleDelete}
        />

        <CardBody className='p-4'>
          <div className='mb-4'>
            <label className='block font-semibold text-sm text-gray-700 mb-2'>{'설명'}</label>
            <div className='relative'>
              <textarea
                className='nodrag w-full resize-none border border-gray-300 rounded-lg p-3 text-sm focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500'
                rows={3}
                placeholder={'설명 입력'}
                value={formState.description ?? ''}
                onChange={e => handleDescriptionChange(e.target.value)}
                maxLength={100}
              />
              <div className='absolute bottom-2 right-2 text-xs text-gray-500'>
                <span className='text-blue-500'>{formState.description?.length}</span>/100
              </div>
            </div>
          </div>
        </CardBody>

        {!innerData.isToggle && (
          <>
            <div className='border-t border-gray-200'>
              <CardBody className='p-4'>
                <div className='mb-2 w-auto'>                  
                  <SelectKnowledge
                    selectedRepoId={formState.knowledge_retriever?.repo_id}
                    asAccordionItem={true}
                    title={
                      <>
                        {'Knowledge'}
                        <span className='ag-color-red'>*</span>
                      </>
                    }
                    onKnowledgeUpdate={handleKnowledgeUpdate}
                  />                  
                </div>

                <div className='w-auto'>
                  <div className='fw-bold form-label mb-2'>K</div>
                  <input
                    type='number'
                    min='1'
                    className='nodrag w-full h-9 rounded-lg border border-gray-300 bg-white p-2 outline-none'
                    value={formState.knowledge_retriever.retrieval_options?.top_k ?? 0}
                    onChange={e => handleTopKChange(e.target.value)}
                    placeholder='1 이상의 정수 값'
                    onKeyDown={e => {
                      // 숫자(0-9), 백스페이스, 삭제, 화살표, Home, End, Tab만 허용
                      const allowedKeys = ['Backspace', 'Delete', 'ArrowLeft', 'ArrowRight', 'ArrowUp', 'ArrowDown', 'Home', 'End', 'Tab'];
                      const isNumber = e.key >= '0' && e.key <= '9';
                      const isAllowedKey = allowedKeys.includes(e.key);

                      if (!isNumber && !isAllowedKey) {
                        e.preventDefault();
                      }
                    }}
                  />
                  <span className='mb-2 text-sm text-gray-500'>{` knowledge 검색 결과에서 반환할 최상위 문서의 개수`}</span>
                </div>

                <div className='mb-2 w-auto'>
                  <div className='fw-bold form-label mb-2'>Threshold</div>
                  <input
                    type='number'
                    min='0'
                    max='1'
                    step='0.01'
                    className='nodrag w-full h-9 rounded-lg border border-gray-300 bg-white p-2 outline-none'
                    value={formState.knowledge_retriever.retrieval_options?.threshold ?? 0}
                    onChange={e => handleThresholdChange(e.target.value)}
                    placeholder='0~1 사이의 값'
                    onKeyDown={e => {
                      // 숫자(0-9), 소수점(.), 백스페이스, 삭제, 화살표, Home, End, Tab만 허용
                      const allowedKeys = ['Backspace', 'Delete', 'ArrowLeft', 'ArrowRight', 'ArrowUp', 'ArrowDown', 'Home', 'End', 'Tab'];
                      const isNumber = e.key >= '0' && e.key <= '9';
                      const isDecimal = e.key === '.';
                      const isMinus = e.key === '-';
                      const isAllowedKey = allowedKeys.includes(e.key);

                      // 현재 입력값 확인
                      const currentValue = e.currentTarget.value;
                      const hasDecimal = currentValue.includes('.');
                      const decimalPart = hasDecimal ? currentValue.split('.')[1] : '';

                      // 소수점이 이미 있으면 또 입력 불가
                      if (isDecimal && hasDecimal) {
                        e.preventDefault();
                        return;
                      }

                      // 소수점 2자리 초과 입력 방지
                      if (hasDecimal && decimalPart.length >= 2 && isNumber) {
                        e.preventDefault();
                        return;
                      }

                      // 음수 입력 방지
                      if (isMinus) {
                        e.preventDefault();
                        return;
                      }

                      if (!isNumber && !isDecimal && !isAllowedKey) {
                        e.preventDefault();
                      }
                    }}
                  />
                  <span className='mb-2 text-sm text-gray-500'>{` 검색 결과의 최소 유사도 점수`}</span>
                </div>

                <div className='mb-2 w-auto'>
                  <div className='fw-bold form-label mb-2'>검색모드</div>
                  <select
                    className='nodrag b-selectbox'
                    value={formState.knowledge_retriever.retrieval_options?.retrieval_mode ?? 'dense'}
                    onChange={e => handleRetrievalModeChange(e.target.value)}
                  >
                    <option value='dense'>Dense</option>
                    <option value='sparse'>Sparse</option>
                    <option value='hybrid'>Hybrid</option>
                  </select>
                </div>

                {/* Hybrid 모드일 때만 Dense와 Sparse 슬라이더 표시 */}
                {formState.knowledge_retriever.retrieval_options?.retrieval_mode === 'hybrid' && (
                  <>
                    <div className='mb-2 w-auto nodrag'>
                      <UISlider
                        label='Dense'
                        required={true}
                        value={Number(formState.knowledge_retriever.retrieval_options?.hybrid_dense_ratio ?? 0.5)}
                        min={0.1}
                        max={0.9}
                        step={0.1}
                        onChange={value => handleHybridDenseRatioChange(value.toString())}
                        startLabel='0.1'
                        endLabel='0.9'
                        width='100%'
                        showTextField={true}
                        textValue={formState.knowledge_retriever.retrieval_options?.hybrid_dense_ratio?.toString() ?? '0.5'}
                        onTextChange={value => handleHybridDenseRatioChange(value)}
                        textFieldWidth='w-32'
                      />
                    </div>
                    <div className='mb-2 w-auto nodrag'>
                      <UISlider
                        label='Sparse'
                        required={true}
                        value={sparseRatio}
                        min={0.1}
                        max={0.9}
                        step={0.1}
                        onChange={value => handleSparseRatioChange(value.toString())}
                        startLabel='0.1'
                        endLabel='0.9'
                        width='100%'
                        showTextField={true}
                        textValue={`${sparseRatio}`}
                        onTextChange={text => handleSparseRatioChange(text)}
                        textFieldWidth='w-32'
                      />
                    </div>
                  </>
                )}
              </CardBody>
            </div>

            <div className='bg-gray-50 px-4 py-3 border-t border-gray-200'>
              <h3 className='text-lg font-semibold text-gray-700'>Schema</h3>
            </div>

            <CustomScheme
              id={id}
              inputKeys={inputKeys}
              setInputKeys={setInputKeys}
              inputValues={inputValues}
              setInputValues={setInputValues}
              innerData={innerData}
              outputKeys={outputKeys}
            />
          </>
        )}

        <CardFooter>
          <NodeFooter onClick={handleFooterFold} isToggle={innerData.isToggle} />
        </CardFooter>
        <Handle
          type='source'
          id={`retriever_right_${id}`}
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
