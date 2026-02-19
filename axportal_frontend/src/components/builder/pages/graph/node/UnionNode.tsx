import { keyTableAtom } from '@/components/builder/atoms/AgentAtom.ts';
import { type ColorType } from '@/components/builder/common/button/ColorType';
import { Card, CardBody, CardFooter, DefaultButton, LogModal } from '@/components/builder/common/index.ts';
import { useAutoUpdateNodeInternals } from '@/components/builder/hooks/useAutoUpdateNodeInternals';
import { useGraphActions } from '@/components/builder/hooks/useGraphActions.ts';
import { type CustomNode, type CustomNodeInnerData, NodeType, type OutputChatDataSchema } from '@/components/builder/types/Agents';
import { type InputKeyItem, type OutputKeyItem, type UnionDataSchema } from '@/components/builder/types/Agents.ts';
import keyTableData from '@/components/builder/types/keyTableData.json';
import { getNodeStatus } from '@/components/builder/utils/GraphUtils.ts';
import { useModal } from '@/stores/common/modal/useModal';
import { Handle, type NodeProps, Position } from '@xyflow/react';
import { useAtom } from 'jotai/index';
import React, { Fragment, useEffect, useMemo, useState } from 'react';
import { v4 as uuidv4 } from 'uuid';
import { NodeFooter, NodeHeader } from '.';
import { logState } from '../../../atoms/logAtom';
import { ABClassNames } from '../../../components/ui/ABClassNames';
import KeyTables from '../controller/KeyTables';

interface Token {
  id: string;
  text: string;
}

export const UnionNode: React.FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  // console.log('🔍 UnionNode 호출:', data, id, type);

  const { nodes, removeNode, syncNodeData, toggleNodeView } = useGraphActions();
  const schemaData: UnionDataSchema = data as UnionDataSchema;

  const { syncAllNodeKeyTable } = useGraphActions();
  const [keyTableList] = useAtom(keyTableAtom);

  const initialOutputKeys = useMemo(() => {
    const initOutputItems: OutputKeyItem[] = (schemaData.output_keys as OutputKeyItem[]) || [];
    return (schemaData.output_keys as OutputKeyItem[]) || initOutputItems;
  }, [schemaData.output_keys]);
  const [outputKeys] = useState<OutputKeyItem[]>(initialOutputKeys);
  const [inputKeys, setInputKeys] = useState<string[]>([]);

  const changeOutPutName = (index: number, value: string) => {
    const newOutputKeys = [...outputKeys];
    newOutputKeys[index].name = value;
    syncNodeData(id, {
      ...data,
      output_keys: newOutputKeys,
    });
  };

  const newInnerData: CustomNodeInnerData = {
    isRun: false,
    isDone: false,
    isError: false,
    isToggle: false,
  };
  const nodeData: CustomNodeInnerData = data.innerData ?? newInnerData;

  const [nodeStatus, setNodeStatus] = useState<string | null>(null);
  useEffect(() => {
    const status = getNodeStatus(nodeData.isRun, nodeData.isDone, nodeData.isError);
    setNodeStatus(status);
  }, [nodeData.isRun, nodeData.isDone, nodeData.isError]);

  const [nodeName, setNodeName] = useState(data.name as string);
  const [description, setDescription] = useState((data.description as string) || (keyTableData['union']['field_default']['description'] as string));

  const { openModal } = useModal();

  // 출력 형식
  const initText = useMemo(() => {
    return ((data as OutputChatDataSchema).format_string as string) || '{{key}}';
  }, [data]);
  const { tokens: initTokens, keyMap: initKeyMap } = useMemo(() => {
    const newTokens: Token[] = [];
    const newKeyMap: Record<string, string> = {};

    const parts = initText.split(/(\{\{.*?\}\})/g);

    parts.forEach(part => {
      const match = part.match(/^\{\{(\w+)\}\}$/);
      if (match) {
        const key = match[1];
        newTokens.push({ id: key, text: `{{${key}}}` });
        newKeyMap[key] = key;
      } else {
        newTokens.push({ id: uuidv4(), text: part });
      }
    });

    return { tokens: newTokens, keyMap: newKeyMap };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [initText, keyTableList]);

  const [text, setText] = useState(initText || ''); // format_string (출력 형식식)
  const [tokens, setTokens] = useState<Token[]>(initTokens || []);
  const [keyMap, setKeyMap] = useState<Record<string, string>>({ ...initKeyMap });

  const [, setLogData] = useAtom(logState);

  const handleChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    const input = e.target.value;
    const newTokens: Token[] = [];
    const newKeyMap: Record<string, string> = {};

    // 플레이스홀더를 기준으로 텍스트를 분할
    const parts = input.split(/(\{\{.*?\}\})/g);

    parts.forEach(part => {
      const match = part.match(/^\{\{(\w+)\}\}$/);
      if (match) {
        const key = match[1];
        const id = uuidv4(); // 각 플레이스홀더에 대해 고유한 ID를 생성
        newTokens.push({ id, text: `{{${key}}}` });
        newKeyMap[id] = key; // ID와 키를 매핑
      } else {
        newTokens.push({ id: uuidv4(), text: part });
      }
    });

    setKeyMap(newKeyMap);
    setTokens(newTokens);
    setText(input);
  };

  const handleOpenKeyTableModal = (tokenId: string, _currentKey: string) => {
    syncAllNodeKeyTable();

    const tempKeyTableId = keyMap[tokenId] || null; // 키테이블 ID
    const modalState = {
      selectedId: tempKeyTableId,
      tempValue: '',
    };

    // 🔥 상태 변경 핸들러 (객체 속성 직접 수정)
    const handleStateChange = (state: { isKeyTable: boolean; selectedId: string | null; tempValue: string }) => {
      modalState.selectedId = state.selectedId;
      modalState.tempValue = state.tempValue;
    };

    openModal({
      title: '키테이블',
      type: 'medium',
      body: <KeyTables initTempValue={''} initSelectedId={tempKeyTableId} initVisibleKeyTables={true} disabledKeyIn={true} onStateChange={handleStateChange} />,
      showFooter: true,
      confirmText: '저장',
      onConfirm: () => {
        // console.log('🎯 키테이블 모달 저장:', index, modalState);

        if (tokenId && modalState.selectedId) {
          const selectedId = modalState.selectedId || '';

          // keyMap 업데이트 - keytable_id를 저장
          setKeyMap((prev: Record<string, string>) => ({
            ...prev,
            [tokenId]: selectedId,
          }));

          // 해당 토큰의 텍스트 업데이트 및 텍스트
          setTokens(prevTokens => prevTokens.map(token => (token.id === tokenId ? { ...token, text: `{{${selectedId}}}` } : token)));

          // 텍스트 재구성
          const newText = tokens.map(token => (token.id === tokenId ? `{{${selectedId}}}` : token.text)).join('');
          setText(newText);
        }
      },
    });
  };

  useEffect(() => {
    const newInputKeys: string[] = [];
    Object.values(keyMap).map(key => newInputKeys.push(key));
    setInputKeys(newInputKeys);
  }, [keyMap]);

  useEffect(() => {
    syncInputData();
    // eslint-disable-next-line
  }, [nodeName, description, text, keyMap, outputKeys]);

  // @ts-ignore - 훅은 조건문 전에 호출
  const containerRef = useAutoUpdateNodeInternals(id);

  const node = nodes.find(node => node.id === id);
  if (!node) return;

  const syncInputData = () => {
    const newInnerData = {
      ...nodeData,
    };

    const newInputData = inputKeys.map(key => ({
      name: key,
      required: true,
      description: '',
      keytable_id: key,
      fixed_value: null,
    })) as InputKeyItem[];

    const newData = {
      ...data,
      type: NodeType.AgentUnion.name,
      id: id,
      name: nodeName,
      description: description,
      format_string: text,
      input_keys: newInputData,
      output_keys: outputKeys,
      innerData: newInnerData,
    };

    syncNodeData(id, newData);
  };

  const onClickDelete = () => {
    removeNode(id);
  };

  const handleNodeNameChange = (value: string) => {
    setNodeName(value);
  };

  const handleFooterFold = (bool: boolean) => {
    toggleNodeView(id, bool);
  };

  const handleDescriptionChange = (val: string) => {
    setDescription(val);
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
          data={nodeData}
          onClickDelete={onClickDelete}
          onClickLog={handleHeaderClickLog}
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

        <hr className='border-gray-200' />
        <CardBody className='p-4'>
          <div className='mb-4 w-full pt-5 flex flex-col'>
            <label className='fw-bold form-label text-lg mb-2'>{'출력 형식'}</label>
            <textarea
              className='nodrag w-full resize-none border rounded-lg p-3 focus:outline-none border-gray-300 focus:border-gray-400'
              rows={5}
              value={text}
              onChange={handleChange}
            />
            <div className='mt-2'>
              <label className='form-label font-light'>출력 문장에 사용할 변수를 {'{{key}}'} 형식으로 입력해주세요.</label>
            </div>
          </div>
          {!nodeData.isToggle && (
            <>
              <div className='mt-4 bg-gray-50 px-4 py-3 border-t border-gray-200'>
                <h3 className='text-lg font-semibold text-gray-700'>Schema</h3>
              </div>

              <div className='bg-white p-3 shadow-sm'>
                <div className='flex w-full'>
                  {/* Inputs Section */}
                  <div className='w-[350px] pr-2'>
                    <h4 className='mb-4 w-full text-center text-lg font-semibold'>{'입력'}</h4>
                    <div className='mt-4 min-h-[80px] p-1'>
                      {tokens.map((token, index) => {
                        const match = token.text.match(/^\{\{(\w+)\}\}$/);
                        if (match) {
                          let currentKey = 'select key';
                          let color: ColorType = 'success';

                          const keyMapValue = keyMap[token.id];
                          if (keyMapValue) {
                            const findKeyTable = keyTableList.find(key => {
                              return key.id === keyMapValue;
                            });
                            if (findKeyTable) {
                              currentKey = `${findKeyTable.nodeName}_${findKeyTable.key}`;
                              color = 'primary';
                            }
                          }
                          return (
                            <Fragment key={index}>
                              <DefaultButton key={token.id} color={color} className='mr-1 mb-1' onClick={() => handleOpenKeyTableModal(token.id, currentKey)}>
                                {currentKey}
                              </DefaultButton>
                              <br />
                            </Fragment>
                          );
                        }
                      })}
                    </div>
                  </div>
                  {/* Separator Line */}
                  <div className='mx-4 w-px bg-gray-300' />
                  {/* Outputs Section */}
                  <div className='w-[180px] pl-2'>
                    <h4 className='mb-4 text-center text-lg font-semibold'>{'출력'}</h4>
                    <div className={`min-h-[100px] ${data.innerData?.isToggle ? 'hidden' : ''}`}>
                      <div className='flex w-full flex-col'>
                        {outputKeys.map((item, index) => (
                          <div key={index} className='w-full rounded-lg'>
                            <input
                              type='text'
                              className='w-full h-[38px] leading-[38px] text-sm text-gray-500 p-3 bg-gray-50 rounded-lg border border-gray-300 nodrag'
                              placeholder={item == null ? '' : item.name}
                              value={item.name}
                              onChange={e => {
                                changeOutPutName(index, e.target.value);
                              }}
                            />
                          </div>
                        ))}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </>
          )}
        </CardBody>

        <CardFooter>
          <NodeFooter onClick={handleFooterFold} isToggle={nodeData.isToggle as boolean} />
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
