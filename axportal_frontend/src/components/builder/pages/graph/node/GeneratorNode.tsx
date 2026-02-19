import { Card, CardBody, CardFooter } from '@/components/builder/common/index.ts';
import { ABClassNames } from '@/components/builder/components/ui';
import { useAutoUpdateNodeInternals } from '@/components/builder/hooks/useAutoUpdateNodeInternals';
import { useGraphActions } from '@/components/builder/hooks/useGraphActions.ts';
import { CustomScheme } from '@/components/builder/pages/graph/contents/CustomScheme.tsx';
import { SelectFewShot } from '@/components/builder/pages/graph/contents/SelectFewShot.tsx';
import { SelectLLM } from '@/components/builder/pages/graph/contents/SelectLLM.tsx';
import { SelectMCPs } from '@/components/builder/pages/graph/contents/SelectMCPs.tsx';
import { SelectPrompt } from '@/components/builder/pages/graph/contents/SelectPrompt.tsx';
import { SelectTools } from '@/components/builder/pages/graph/contents/SelectTools.tsx';
import { type CustomNode, type CustomNodeInnerData, type GeneratorDataSchema, type InputKeyItem, NodeType, type OutputKeyItem } from '@/components/builder/types/Agents';
import keyTableData from '@/components/builder/types/keyTableData.json';

import { getNodeStatus } from '@/components/builder/utils/GraphUtils.ts';
import type { generateMCPCatalog } from '@/services/agent/mcp/types';
import { Handle, type NodeProps, Position, useUpdateNodeInternals } from '@xyflow/react';

import { LogModal } from '@/components/builder/common/modal/log/LogModal.tsx';
import type { GetAgentToolByIdResponse } from '@/services/agent/tool/types';
import { useModal } from '@/stores/common/modal/useModal';
import { useAtom } from 'jotai';
import React, { useEffect, useMemo, useState } from 'react';
import { NodeFooter, NodeHeader } from '.';
import { logState } from '../../..';
import { keyTableAtom } from '@/components/builder/atoms/AgentAtom';
import type { KeyTableData } from '@/components/builder/types/Agents';

export const GeneratorNode: React.FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  const { removeNode, toggleNodeView, syncNodeData } = useGraphActions();
  const updateNodeInternals = useUpdateNodeInternals();

  const newInnerData: CustomNodeInnerData = {
    isRun: false,
    isDone: false,
    isError: false,
    isToggle: false,
  };

  const innerData: CustomNodeInnerData = data.innerData ?? newInnerData;
  const schemaData: GeneratorDataSchema = data as GeneratorDataSchema;

  const [nodeStatus, setNodeStatus] = useState<string | null>(null);
  useEffect(() => {
    const status = getNodeStatus(innerData.isRun, innerData.isDone, innerData.isError);
    setNodeStatus(status);
  }, [innerData.isRun, innerData.isDone, innerData.isError]);

  const [nodeName, setNodeName] = useState(data.name as string);
  const [description, setDescription] = useState((data.description as string) || (keyTableData['agent__generator']['field_default']['description'] as string));
  const [servingName, setServingName] = useState((schemaData.serving_name as string) || '');
  const [servingModel, setServingModel] = useState((schemaData.serving_model as string) || '');
  const [promptId, setPromptId] = useState<string | null>(schemaData.prompt_id || '');
  const [fewShotId, setFewShotId] = useState<string | null>(schemaData.fewshot_id || '');
  const [toolIds, setToolIds] = useState((schemaData.tool_ids as string[]) || []);
  const [toolInfoList, setToolInfoList] = useState<GetAgentToolByIdResponse[]>(() => {
    if (!schemaData.tools || !Array.isArray(schemaData.tools)) {
      return [];
    }
    // schemaData.tools를 GetAgentToolByIdResponse[] 타입에 맞게 변환
    return schemaData.tools.map((tool: any): GetAgentToolByIdResponse => {
      return {
        id: tool.id || '',
        name: tool.name || '',
        displayName: tool.displayName,
        description: tool.description || '',
        toolType: tool.toolType || tool.tool_type || '',
        method: tool.method,
        serverUrl: tool.serverUrl || tool.server_url,
        apiParam: tool.apiParam || tool.api_param,
        inputKeys: tool.inputKeys || tool.input_keys,
        code: tool.code,
        createdAt: tool.createdAt || tool.created_at,
        updatedAt: tool.updatedAt || tool.updated_at,
        createdBy: tool.createdBy || tool.created_by || '',
        updatedBy: tool.updatedBy || tool.updated_by || '',
        publicStatus: tool.publicStatus || tool.public_status || '내부공유',
      };
    });
  });

  const [mcpDataList, setMcpDataList] = useState((schemaData.mcp_catalogs as generateMCPCatalog[]) || []);

  // Generator 관련 상태
  const [generatorType, setGeneratorType] = useState<'internal' | 'external'>(((schemaData as any).generator_type as 'internal' | 'external') || 'internal');
  const [internalLLMParams, setInternalLLMParams] = useState<{
    params?: Array<{ name: string; type: string; value: string | null }>;
    disabled_params?: string[];
  }>(((schemaData as any).internal_llm_params as any) || { params: [], disabled_params: [] });
  const [externalLLMParams, setExternalLLMParams] = useState<Array<{ key: string; keytable_id: string; nodeId: string }>>(
    ((schemaData as any).external_llm_params as Array<{ key: string; keytable_id: string; nodeId: string }>) || []
  );
  // modelParameters는 SelectLLM에서 계산
  const [modelParameters, setModelParameters] = useState<any>((schemaData as any).model_parameters || null);

  //[input keys handle]
  const initialInputKeys = useMemo(() => {
    const initInputItems: InputKeyItem[] = (schemaData.input_keys as InputKeyItem[]) || [];
    return (schemaData.input_keys as InputKeyItem[]) || initInputItems;
  }, [schemaData.input_keys]);
  const [inputKeys, setInputKeys] = useState<InputKeyItem[]>(initialInputKeys);
  const [inputValues, setInputValues] = useState<string[]>(inputKeys.map(item => item.name));

  //[output keys handle]
  const initialOutputKeys = useMemo(() => {
    const initOutputItems: OutputKeyItem[] = (schemaData.output_keys as OutputKeyItem[]) || [];
    return (schemaData.output_keys as OutputKeyItem[]) || initOutputItems;
  }, [schemaData.output_keys]);
  const [outputKeys] = useState<OutputKeyItem[]>(initialOutputKeys);
  const [, setOutputValues] = useState<string[]>(outputKeys.filter(item => item != null).map(item => item.name));
  const [, setLogData] = useAtom(logState);
  const { openModal } = useModal();
  const { syncAllNodeKeyTable } = useGraphActions();
  const [keyTableList] = useAtom(keyTableAtom);

  useEffect(() => {
    setInputValues(inputKeys.map(item => item.name));
    syncAllNodeKeyTable();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [inputKeys]);
  useEffect(() => {
    setOutputValues(outputKeys.map(item => (item == null ? '' : item.name)));
  }, [outputKeys]);

  // keyTableList 변경 시: externalLLMParams에서 키테이블에 없는 항목 제거
  useEffect(() => {
    if (keyTableList.length >= 0 && externalLLMParams.length > 0) {
      const updatedExternalLLMParams = externalLLMParams.filter(param => {
        if (param.keytable_id && param.keytable_id.trim() !== '') {
          // keyTableList에 존재하는지 확인
          return keyTableList.some((entry: KeyTableData) => entry.id === param.keytable_id);
        }
        return true; // keytable_id가 없으면 유지
      });

      if (updatedExternalLLMParams.length !== externalLLMParams.length) {
        setExternalLLMParams(updatedExternalLLMParams);
      }
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [keyTableList]);

  useEffect(() => {
    syncGenData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [
    nodeName,
    description,
    servingName,
    servingModel,
    promptId,
    fewShotId,
    toolIds,
    toolInfoList,
    inputKeys,
    outputKeys,
    mcpDataList,
    generatorType,
    internalLLMParams,
    externalLLMParams,
    modelParameters,
  ]);

  const syncGenData = () => {
    const newInnerData = {
      ...innerData,
    };

    const newData = {
      ...data,
      type: NodeType.AgentGenerator.name,
      id: id,
      name: nodeName,
      description: description,
      serving_name: servingName,
      serving_model: servingModel,
      prompt_id: promptId,
      fewshot_id: fewShotId,
      tool_ids: toolIds,
      tools: toolInfoList,
      input_keys: inputKeys.map(key => ({
        ...key,
        fixed_value: key.fixed_value,
        keytable_id: key.keytable_id || '',
      })),
      output_keys: outputKeys,
      innerData: newInnerData,
      mcp_catalogs: mcpDataList,
      generator_type: generatorType,
      internal_llm_params: internalLLMParams,
      external_llm_params: externalLLMParams,
      model_parameters: modelParameters,
    };

    syncNodeData(id, newData);
  };

  const onClickDelete = () => {
    removeNode(id);
  };

  const handleFooterFold = (bool: boolean) => {
    toggleNodeView(id, bool);
  };

  const handleLLMChange = (changeData: any) => {
    // 기존 방식 (GetModelDeployResponse) 또는 새로운 방식 (LLMChangeData) 모두 지원
    if (changeData && 'name' in changeData && 'id' in changeData) {
      // 기존 방식: GetModelDeployResponse 직접 전달
      setServingName(changeData.name || '');
      setServingModel(changeData.id || '');
    } else {
      // 새로운 방식: LLMChangeData 객체
      if (changeData.selectedLLM) {
        setServingName(changeData.selectedLLM?.name || '');
        setServingModel(changeData.selectedLLM?.id || ''); // lineage 정보에 저장할 serving_model 값
      }
      if (changeData.generatorType !== undefined) {
        setGeneratorType(changeData.generatorType);
      }
      if (changeData.internalLLMParams !== undefined) {
        setInternalLLMParams(changeData.internalLLMParams);
      }
      if (changeData.externalLLMParams !== undefined) {
        setExternalLLMParams(changeData.externalLLMParams);
      }
      // modelParameters는 SelectLLM에서 계산되므로 여기서는 업데이트하지 않음
      // 대신 syncGenData에서 직접 사용
      if (changeData.modelParameters !== undefined) {
        setModelParameters(changeData.modelParameters);
      }
    }
  };

  const handleNodeNameChange = (value: string) => {
    setNodeName(value);
  };

  const handleDescriptionChange = (value: string) => {
    setDescription(value);
  };

  // Handle 위치/개수가 바뀔 때 노드 내부 레이아웃 재계산 (접힘/펼침 등)
  useEffect(() => {
    updateNodeInternals(id);
  }, [id, updateNodeInternals, innerData.isToggle]);

  // 노드 내부 아코디언(SelectLLM/Prompt/FewShot/Tools/MCP) 열고 닫을 때 높이 변화 감지하여 연결선 재계산
  const containerRef = useAutoUpdateNodeInternals(id);

  const handleHeaderClickLog = () => {
    if (data.innerData.logData) {
      setLogData(
        data.innerData.logData.map(item => ({
          log: item,
        }))
      );
      // console.log('🔍 handleHeaderClickLog!!!!', nodeName);
      // console.log(
      //   '🔍 handleHeaderClickLog!!!! logData',
      //   data.innerData.logData.map(item => ({
      //     log: item,
      //   }))
      // );
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
          id='gen_left'
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
          onClickDelete={onClickDelete}
          onClickLog={handleHeaderClickLog}
          defaultValue={nodeName}
          onChange={handleNodeNameChange}
        />

        <>
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

          {!innerData.isToggle && (
            <>
              <div className='border-t border-gray-200'>
                <CardBody className='p-4'>
                  <SelectLLM
                    selectedServingName={servingName}
                    selectedServingModel={servingModel}
                    nodeId={id}
                    nodeData={data}
                    onChange={handleLLMChange}
                    asAccordionItem={true}
                    isGeneratorNode={true}
                    title={
                      <>
                        {'LLM'}
                        <span className='ag-color-red'>*</span>
                      </>
                    }
                  />
                  <SelectPrompt
                    selectedPromptId={promptId}
                    nodeId={id}
                    nodeType={NodeType.AgentGenerator.name}
                    asAccordionItem={true}
                    title={'Prompt'}
                    onPromptUpdate={selectedPrompt => {
                      setPromptId(selectedPrompt?.id || '');
                    }}
                  />
                  <SelectFewShot
                    selectedFewShotId={fewShotId}
                    nodeId={id}
                    asAccordionItem={true}
                    title={'Few-shot'}
                    onFewShotUpdate={selectedFewShot => {
                      setFewShotId(selectedFewShot?.id || '');
                    }}
                  />

                  <SelectTools
                    toolIds={toolIds}
                    nodeId={id}
                    toolInfoList={toolInfoList}
                    isSingle={false}
                    asAccordionItem={true}
                    title={'Tools'}
                    onToolsUpdate={selectedTool => {
                      setToolIds(selectedTool);
                    }}
                    onToolInfoListUpdate={selectedToolInfoList => {
                      setToolInfoList(selectedToolInfoList);
                    }}
                  />
                  <SelectMCPs
                    nodeId={id}
                    mcpInfoList={mcpDataList}
                    asAccordionItem={true}
                    title={'MCPs'}
                    onMCPUpdate={selectedMCP => {
                      setMcpDataList(selectedMCP);
                    }}
                  />
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
                innerData={data.innerData}
                outputKeys={outputKeys}
                type={NodeType.AgentGenerator.name}
              />
            </>
          )}
        </>

        <CardFooter>
          <NodeFooter onClick={handleFooterFold} isToggle={innerData.isToggle as boolean} />
        </CardFooter>
        <Handle
          type='source'
          id='gen_right'
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
