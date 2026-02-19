import { nodesAtom } from '@/components/builder/atoms/AgentAtom';
import { CustomAccordionItem } from '@/components/builder/common/button/CustomAccordionItem';
import { SelectLLMPop } from '@/components/builder/pages/modal/SelectLLMPop';
import type { CustomNode } from '@/components/builder/types/Agents';
import { UIIcon2 } from '@/components/UI/atoms/UIIcon2';
import { UIImage } from '@/components/UI/atoms/UIImage';
import { UIRadio2 } from '@/components/UI/atoms/UIRadio2';
import { UITypography } from '@/components/UI/atoms/UITypography';
import type { GetModelDeployResponse } from '@/services/deploy/model/types';
import { useModal } from '@/stores/common/modal';
import { useAtom } from 'jotai';
import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { useNavigate } from 'react-router';
import { InsertLLMParameterPop } from '../../modal/InsertLLMParameterPop';
import { SelectExtLLMParameterPop } from '../../modal/SelectExtLLMParameterPop';

interface LLMChangeData {
  selectedLLM?: GetModelDeployResponse;
  generatorType?: 'internal' | 'external';
  internalLLMParams?: {
    params?: Array<{ name: string; type: string; value: string | null }>;
    disabled_params?: string[];
  };
  externalLLMParams?: Array<{ key: string; keytable_id: string; nodeId: string }>;
  modelParameters?: any;
}

interface llmProps {
  selectedServingName: string;
  selectedServingModel: string;
  nodeId?: string;
  nodeData?: CustomNode['data'];
  isGeneratorNode?: boolean;
  // eslint-disable-next-line no-unused-vars
  onChange?: (data: LLMChangeData | GetModelDeployResponse) => void;
  isReRanker?: boolean;
  asAccordionItem?: boolean;
  title?: React.ReactNode;
}

export const SelectLLM = ({ selectedServingName, selectedServingModel, nodeId, nodeData, isGeneratorNode, onChange, isReRanker, asAccordionItem = false, title }: llmProps) => {
  const { openModal } = useModal();
  const navigate = useNavigate();
  const tempLLMRef = useRef<GetModelDeployResponse | null>(null);
  const selectedExtLLMParamsRef = useRef<Array<{ key: string; keytable_id: string; nodeId: string }> | null>(null); // 팝업에서 선택한 External LLM Parameter들
  const [nodes] = useAtom(nodesAtom);

  // model_parameters로부터 초기값 추출하는 함수 (useCallback으로 메모이제이션)
  const getInitialStateFromModelParams = useCallback((modelParams: any) => {
    if (!modelParams) return null;

    // External LLM parameter 구조 확인 (keytable_id가 있으면 external)
    if (modelParams.keytable_id && modelParams.object_type === 'LLM parameters') {
      // keytable_id 형식: "name__nodeId"
      const keytableIdParts = modelParams.keytable_id.split('__');
      const nodeId = keytableIdParts.length > 1 ? keytableIdParts[keytableIdParts.length - 1] : '';
      const key = modelParams.name || keytableIdParts[0] || '';

      return {
        generatorType: 'external' as const,
        externalLLMParams: [
          {
            key: key,
            keytable_id: modelParams.keytable_id,
            nodeId: nodeId,
          },
        ],
        internalLLMParams: { params: [], disabled_params: [] },
      };
    }
    // Internal LLM parameter 구조 확인 (params 또는 disabled_params가 있으면 internal)
    else if (modelParams.params || modelParams.disabled_params) {
      return {
        generatorType: 'internal' as const,
        internalLLMParams: {
          params: modelParams.params || [],
          disabled_params: modelParams.disabled_params || [],
        },
        externalLLMParams: [],
      };
    }

    return null;
  }, []);

  // 초기값 계산: model_parameters가 있으면 우선 사용, 없으면 기존 필드 사용
  const initialModelParams = (nodeData as any)?.model_parameters;
  const initialStateFromModelParams = getInitialStateFromModelParams(initialModelParams);

  const [generatorType, setGeneratorType] = useState<'internal' | 'external'>(
    initialStateFromModelParams?.generatorType || ((nodeData as any)?.generator_type as 'internal' | 'external') || 'internal'
  );
  // generatorType의 최신 값을 참조하기 위한 ref
  const generatorTypeRef = useRef<'internal' | 'external'>(generatorType);

  const [internalLLMParams, setInternalLLMParams] = useState<{
    params?: Array<{ name: string; type: string; value: string | null }>;
    disabled_params?: string[];
  }>(initialStateFromModelParams?.internalLLMParams || ((nodeData as any)?.internal_llm_params as any) || { params: [], disabled_params: [] });
  const [externalLLMParams, setExternalLLMParams] = useState<Array<{ key: string; keytable_id: string; nodeId: string }>>(
    initialStateFromModelParams?.externalLLMParams || ((nodeData as any)?.external_llm_params as Array<{ key: string; keytable_id: string; nodeId: string }>) || []
  );

  // generatorType이 변경될 때마다 ref 업데이트
  useEffect(() => {
    generatorTypeRef.current = generatorType;
  }, [generatorType]);

  // nodeData가 변경될 때 state 업데이트
  // 무한 루프 방지를 위해 nodeData의 특정 필드만 비교
  const nodeDataRef = useRef<{ generator_type?: string; internal_llm_params?: any; external_llm_params?: any; model_parameters?: any; nodeId?: string } | null>(null);
  useEffect(() => {
    if (!nodeData || !nodeId) return;

    // nodeData의 id가 현재 nodeId와 일치하는지 확인 (다른 노드의 데이터 변경 방지)
    const nodeDataId = (nodeData as any)?.id;
    if (nodeDataId && nodeDataId !== nodeId) {
      // 다른 노드의 데이터이면 무시
      return;
    }

    // model_parameters가 있으면 우선 사용
    const currentModelParams = (nodeData as any)?.model_parameters;
    const stateFromModelParams = getInitialStateFromModelParams(currentModelParams);

    let currentGeneratorType: 'internal' | 'external';
    let currentInternalLLMParams: any;
    let currentExternalLLMParams: Array<{ key: string; keytable_id: string; nodeId: string }>;

    if (stateFromModelParams) {
      // model_parameters에서 추출한 값 사용
      currentGeneratorType = stateFromModelParams.generatorType;
      currentInternalLLMParams = stateFromModelParams.internalLLMParams;
      currentExternalLLMParams = stateFromModelParams.externalLLMParams;
    } else {
      // 기존 필드 사용
      currentGeneratorType = ((nodeData as any)?.generator_type as 'internal' | 'external') || 'internal';
      currentInternalLLMParams = ((nodeData as any)?.internal_llm_params as any) || { params: [], disabled_params: [] };
      currentExternalLLMParams = ((nodeData as any)?.external_llm_params as Array<{ key: string; keytable_id: string; nodeId: string }>) || [];
    }

    const prevData = nodeDataRef.current;
    const prevGeneratorType = prevData?.generator_type || 'internal';
    const prevInternalLLMParams = prevData?.internal_llm_params || { params: [], disabled_params: [] };
    const prevExternalLLMParams = prevData?.external_llm_params || [];
    const prevModelParams = prevData?.model_parameters;

    // model_parameters가 변경되었거나, 다른 필드들이 변경되었을 때만 업데이트
    const modelParamsChanged = JSON.stringify(prevModelParams) !== JSON.stringify(currentModelParams);
    const generatorTypeChanged = prevGeneratorType !== currentGeneratorType;
    const internalLLMParamsChanged = JSON.stringify(prevInternalLLMParams) !== JSON.stringify(currentInternalLLMParams);
    const externalLLMParamsChanged = JSON.stringify(prevExternalLLMParams) !== JSON.stringify(currentExternalLLMParams);

    // nodeData에서 값 가져와서 업데이트
    if (modelParamsChanged || generatorTypeChanged) {
      // 현재 상태와 다를 때만 업데이트
      if (currentGeneratorType !== generatorTypeRef.current) {
        setGeneratorType(currentGeneratorType);
      }
    }
    if (modelParamsChanged || internalLLMParamsChanged) {
      setInternalLLMParams(currentInternalLLMParams);
    }
    if (modelParamsChanged || externalLLMParamsChanged) {
      setExternalLLMParams(currentExternalLLMParams);
    }

    // ref 업데이트
    nodeDataRef.current = {
      generator_type: currentGeneratorType,
      internal_llm_params: currentInternalLLMParams,
      external_llm_params: currentExternalLLMParams,
      model_parameters: currentModelParams,
      nodeId: nodeId,
    };
  }, [nodeData, getInitialStateFromModelParams]);

  // modelParameters 계산
  const modelParameters = useMemo(() => {
    if (generatorType === 'external' && externalLLMParams.length > 0) {
      // External: 첫 번째 항목의 keytable_id로 InputNode의 fixed_value 찾기
      const firstExternalParam = externalLLMParams[0];

      // nodesAtom에서 최신 노드 데이터를 가져와서 사용 (keyTableList의 node는 오래된 데이터일 수 있음)
      // externalLLMParams의 nodeId로 InputNode 찾기
      const inputNode = nodes.find(node => node.id === firstExternalParam.nodeId && node.type === 'input__basic');

      if (inputNode?.data?.input_keys) {
        const inputKeys = inputNode.data.input_keys as any[];
        const llmKey = inputKeys.find((key: any) => key.keytable_id === firstExternalParam.keytable_id && key.object_type === 'LLM parameters');
        if (llmKey) {
          const result = {
            name: llmKey.name || firstExternalParam.key,
            required: llmKey.required || false,
            description: llmKey.description || '',
            fixed_value: llmKey.fixed_value || null,
            keytable_id: firstExternalParam.keytable_id,
            object_type: 'LLM parameters',
          };
          return result;
        }
      }
      return null;
    } else if (
      generatorType === 'internal' &&
      internalLLMParams &&
      ((internalLLMParams.params && internalLLMParams.params.length > 0) || (internalLLMParams.disabled_params && internalLLMParams.disabled_params.length > 0))
    ) {
      // Internal: params와 disabled_params 구조
      return {
        params: internalLLMParams.params || [],
        disabled_params: internalLLMParams.disabled_params || [],
      };
    }
    return null;
  }, [generatorType, externalLLMParams, internalLLMParams, nodes]);

  // Internal LLM Parameter 모달 열기
  const handleOpenInternalLLMParameterModal = useCallback(() => {
    const currentParams = internalLLMParams?.params || [];
    const currentDisabledParams = internalLLMParams?.disabled_params || [];

    let initialModelParams: any[] = [];
    let initialDisabledParams: any[] = [];

    if (currentParams && Array.isArray(currentParams)) {
      initialModelParams = currentParams.map((p: any) => ({
        keyName: p.name || '',
        type: p.type || 'string',
        value: p.value !== undefined ? p.value : null,
      }));
    }
    if (currentDisabledParams && Array.isArray(currentDisabledParams)) {
      initialDisabledParams = currentDisabledParams.map((key: string) => ({
        keyName: key,
      }));
    }

    openModal({
      type: 'large',
      title: 'Default Value',
      body: (
        <InsertLLMParameterPop
          initialModelParameters={initialModelParams}
          initialDisabledParameters={initialDisabledParams}
          onSave={(modelParams: any[], disabledParams: any[]) => {
            setInternalLLMParams({
              params: modelParams.map(p => {
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
            });
            setGeneratorType('internal');
          }}
        />
      ),
      showFooter: true,
      confirmText: '저장',
      onConfirm: () => {
        if ((window as any).defaultValueApplyHandler) {
          (window as any).defaultValueApplyHandler();
        }
      },
    });
  }, [nodeId, internalLLMParams, openModal]);

  // External LLM Parameter 모달 열기
  const handleOpenExternalLLMParameterModal = useCallback(() => {
    selectedExtLLMParamsRef.current = null; // 기존 선택값을 초기 상태로 반영

    openModal({
      type: 'large',
      title: '외부 LLM Parameter 선택',
      body: (
        <SelectExtLLMParameterPop
          nodeId={nodeId || ''}
          selectedParams={externalLLMParams}
          onSelect={selectedParams => {
            // 팝업에서 선택한 External LLM Parameter들을 ref에 저장
            const selectedParamsData = selectedParams.map(p => ({
              key: p.key,
              keytable_id: p.keytable_id || '',
              nodeId: p.nodeId || '',
            }));
            selectedExtLLMParamsRef.current = selectedParamsData;
          }}
        />
      ),
      showFooter: true,
      confirmText: '적용',
      onConfirm: () => {
        if (!selectedExtLLMParamsRef.current || selectedExtLLMParamsRef.current.length === 0) return;

        // 팝업에서 선택한 External LLM Parameter들을 상태에 반영
        setExternalLLMParams(selectedExtLLMParamsRef.current);
        // External LLM Parameter를 선택했으면 generatorType도 'external'로 설정
        if (generatorType !== 'external') {
          setGeneratorType('external');
        }
      },
    });
  }, [nodeId, externalLLMParams, openModal]);

  // 이전 값들을 저장해서 실제로 변경되었을 때만 onChange 호출
  const prevValuesRef = useRef({
    generatorType,
    internalLLMParams,
    externalLLMParams,
    modelParameters: null as any,
  });

  // generator 관련 값이 변경될 때마다 onChange 호출 (nodeId가 있을 때만, 즉 GeneratorNode에서만)
  useEffect(() => {
    if (!onChange || !nodeId) return;

    // 실제로 값이 변경되었는지 확인
    const hasChanged =
      prevValuesRef.current.generatorType !== generatorType ||
      JSON.stringify(prevValuesRef.current.internalLLMParams) !== JSON.stringify(internalLLMParams) ||
      JSON.stringify(prevValuesRef.current.externalLLMParams) !== JSON.stringify(externalLLMParams) ||
      JSON.stringify(prevValuesRef.current.modelParameters) !== JSON.stringify(modelParameters);

    if (hasChanged) {
      prevValuesRef.current = {
        generatorType,
        internalLLMParams,
        externalLLMParams,
        modelParameters,
      };

      // modelParameters는 useMemo로 계산되므로 항상 최신 값 사용
      onChange({
        generatorType,
        internalLLMParams,
        externalLLMParams,
        modelParameters,
      });
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [generatorType, internalLLMParams, externalLLMParams, modelParameters, nodeId]);

  const handleRemoveLLM = () => {
    // 빈 객체를 전달하여 선택 해제 (null 대신)
    if (nodeId) {
      // GeneratorNode인 경우
      onChange?.({
        selectedLLM: {} as GetModelDeployResponse,
        generatorType,
        internalLLMParams,
        externalLLMParams,
        modelParameters,
      });
    } else {
      // 다른 노드인 경우 (하위 호환성)
      onChange?.({} as GetModelDeployResponse);
    }
  };

  const handleClickSearch = () => {
    tempLLMRef.current = null; // 모달 열기 전에 ref 초기화

    openModal({
      title: isReRanker ? 'Re-Ranker 모델 선택' : 'LLM 모델 선택',
      type: 'large',
      body: (
        <SelectLLMPop
          selectedServingModel={selectedServingModel}
          isReRanker={isReRanker}
          onSelectLLM={(llm: GetModelDeployResponse) => {
            tempLLMRef.current = llm; // ref에 저장
          }}
        />
      ),
      showFooter: true,
      confirmText: '적용',
      confirmDisabled: false,
      onConfirm: () => {
        if (!tempLLMRef.current) return;

        if (nodeId) {
          // GeneratorNode인 경우
          onChange?.({
            selectedLLM: tempLLMRef.current,
            generatorType,
            internalLLMParams,
            externalLLMParams,
            modelParameters,
          });
        } else {
          // 다른 노드인 경우 (하위 호환성)
          onChange?.(tempLLMRef.current);
        }
      },
    });
  };

  const content = (
    <>
      {/* w-full rounded-lg p-2 shadow-sm (Asis class) */}
      <div className='flex w-full items-center gap-2 rounded-lg bg-white p-2 rounded-lg border border-gray-300'>
        <div className='flex-1 items-center'>
          {selectedServingName ? (
            <div className='h-[36px] flex items-center gap-2 flex-1'>
              <button
                className='rounded-lg bg-gray-100 px-3 py-1 text-gray-700 max-w-[200px] text-sm truncate hover:bg-gray-200 transition-colors cursor-pointer h-[28px]'
                title={selectedServingName}
                onClick={() => selectedServingModel && navigate(`/deploy/modelDeploy/${selectedServingModel}`)}
              >
                {selectedServingName}
              </button>
              <button
                onClick={handleRemoveLLM}
                className='flex h-[20px] w-[20px] items-center justify-center rounded-md hover:bg-gray-100 cursor-pointer ml-auto'
                title='삭제'
                style={{
                  backgroundColor: '#ffffff',
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
                <UIImage src='/assets/images/system/ico-system-24-outline-gray-trash.svg' alt='No data' className='w-20 h-20' />
              </button>
            </div>
          ) : (
              <div className='h-[36px] leading-[36px] text-sm text-gray-500'>{ isReRanker ? 'Re-Ranker 모델을 선택해주세요' : 'LLM 모델을 선택해주세요' }</div>
          )}
        </div>
      </div>
      <div className='flex justify-end mt-2'>
        <button
          onClick={handleClickSearch}
          className='bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded-lg border-0 transition-colors duration-200 cursor-pointer flex-shrink-0'
        >
          검색
        </button>
      </div>
      {isGeneratorNode && (
        <div className='flex flex-col'>
          {selectedServingModel.length > 0 && (
            <div className='my-4'>
              {/* Radio 버튼들 */}
              <div className='flex mb-3 justify-between items-center'>
                <div className='flex gap-4'>
                  <UIRadio2
                    name={`generatorType-${nodeId}`}
                    value='internal'
                    label='내부 설정'
                    checked={generatorType === 'internal'}
                    onChange={(checked, value) => {
                      if (checked) {
                        setGeneratorType(value as 'internal' | 'external');
                      }
                    }}
                  />
                  <UIRadio2
                    name={`generatorType-${nodeId}`}
                    value='external'
                    label='외부 설정'
                    checked={generatorType === 'external'}
                    onChange={(checked, value) => {
                      if (checked) {
                        setGeneratorType(value as 'internal' | 'external');
                      }
                    }}
                  />
                </div>
              </div>

              {!(generatorType === 'external' && externalLLMParams && externalLLMParams.length > 0) && (
                <div className='box-fill mt-2 mb-2'>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0 6px' }}>
                    <UIIcon2 className='ic-system-16-info-gray' />
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      {generatorType === 'internal' ? 'Generator 노드에서 사용할 LLM parameter를 설정해주세요.' : 'InputNode에서 등록한 외부 LLM parameter를 선택하세요.'}
                    </UITypography>
                  </div>
                </div>
              )}
              {/* Internal 설정 영역 */}
              {generatorType === 'internal' && (
                <div className='w-full'>
                  <div className='flex justify-end'>
                    <button
                      onClick={handleOpenInternalLLMParameterModal}
                      className='bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded-lg border-0 transition-colors duration-200 cursor-pointer'
                    >
                      설정
                    </button>
                  </div>
                </div>
              )}

              {/* External 설정 영역 */}
              {generatorType === 'external' && (
                <div className='w-full'>
                  {/* 선택된 항목 표시 */}
                  {externalLLMParams && externalLLMParams.length > 0 ? (
                    <div className='space-y-2'>
                      {externalLLMParams.map((param, idx) => {
                        const displayName = param.key || `Parameter ${idx + 1}`;
                        return (
                          <div
                            key={`${param.keytable_id}_${idx}`}
                            className='h-[55px] flex items-center gap-2 p-3 bg-white rounded-lg border border-gray-300 hover:border-blue-400 transition-colors'
                          >
                            <span className='rounded-lg bg-gray-100 px-3 py-1 text-gray-700 max-w-[200px] text-sm truncate transition-colors h-[28px]'>
                              {displayName}
                            </span>
                            <span className='rounded-lg bg-blue-100 px-3 py-1 text-blue-700 text-sm max-w-[200px] truncate h-[28px]'>
                              외부 LLM Parameters
                            </span>
                            <button
                              onClick={() => {
                                const updated = externalLLMParams.filter((_, i) => i !== idx);
                                setExternalLLMParams(updated);
                              }}
                              className='flex h-[20px] w-[20px] items-center justify-center rounded-md hover:bg-gray-100 cursor-pointer ml-auto'
                              title='삭제'
                              style={{
                                backgroundColor: '#ffffff',
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
                              <UIImage src='/assets/images/system/ico-system-24-outline-gray-trash.svg' alt='삭제' className='w-5 h-5' />
                            </button>
                          </div>
                        );
                      })}
                    </div>
                  ) : null}
                  <div className='flex justify-end mt-2'>
                    <button
                      onClick={handleOpenExternalLLMParameterModal}
                      className='bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded-lg border-0 transition-colors duration-200'
                    >
                      검색
                    </button>
                  </div>
                </div>
              )}
            </div>
          )}
        </div>
      )}
    </>
  );

  if (asAccordionItem) {
    const accordionTitle = (
      <>
        {title}
        {selectedServingName && <span className='ml-2 text-gray-500 font-medium'>{selectedServingName}</span>}
      </>
    );
    return (
      <CustomAccordionItem title={accordionTitle} defaultOpen={false}>
        {content}
      </CustomAccordionItem>
    );
  }

  return content;
};
