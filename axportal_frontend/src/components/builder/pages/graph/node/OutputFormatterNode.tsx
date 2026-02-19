import { keyTableAtom } from '@/components/builder/atoms/AgentAtom';
import { type ColorType } from '@/components/builder/common/button/ColorType';
import { Card, CardBody, CardFooter, DefaultButton, LogModal } from '@/components/builder/common/index.ts';
import { ABClassNames } from '@/components/builder/components/ui';
import { useAutoUpdateNodeInternals } from '@/components/builder/hooks/useAutoUpdateNodeInternals';
import { useGraphActions } from '@/components/builder/hooks/useGraphActions.ts';
import { NodeType, type CustomNode, type CustomNodeInnerData, type OutputChatDataSchema } from '@/components/builder/types/Agents';
import keyTableData from '@/components/builder/types/keyTableData.json';
import { getNodeStatus } from '@/components/builder/utils/GraphUtils.ts';
import { useModal } from '@/stores/common/modal';
import { Handle, Position, type NodeProps } from '@xyflow/react';
import { useAtom } from 'jotai/index';
import React, { Fragment, useEffect, useMemo, useState } from 'react';
import { v4 as uuidv4 } from 'uuid';
import { NodeFooter, NodeHeader } from '.';
import { logState } from '../../..';
import KeyTables from '../controller/KeyTables';

interface Token {
  id: string;
  text: string;
}

export const OutputFormatterNode: React.FC<NodeProps<CustomNode>> = ({ data, id, type }) => {
  const { nodes, removeNode, syncNodeData, toggleNodeView } = useGraphActions();

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

  const [nodeName, setNodeName] = useState(data.name as string);
  const [description, setDescription] = useState((data.description as string) || (keyTableData['output__chat']['field_default']['description'] as string));

  const { syncAllNodeKeyTable } = useGraphActions();
  const [keyTableList] = useAtom(keyTableAtom);

  const [, setLogData] = useAtom(logState);
  const { openModal } = useModal();

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

  const [text, setText] = useState(initText || '');
  const [tokens, setTokens] = useState<Token[]>(initTokens || []);
  const [keyMap, setKeyMap] = useState<Record<string, string>>({ ...initKeyMap });

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
    syncOutputData();
  }, [text, nodeName, description]);

  //output chat - 훅은 조건문 전에 호출
  const containerRef = useAutoUpdateNodeInternals(id);

  const node = nodes.find(node => node.id === id);
  if (!node) return null;

  const syncOutputData = () => {
    // console.log('syncOutputData');
    const newInnerData = {
      ...nodeData,
    };

    const newData = {
      ...data,
      type: NodeType.OutputFormatter.name,
      id: id,
      name: nodeName,
      description: description,
      format_string: text,
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
          id='output_formatter_left'
          key={`output_formatter_left_${nodeData.isToggle}_${Date.now()}`}
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
                style={{
                  minHeight: '80px',
                  maxHeight: '100px',
                  height: 'auto',
                  overflow: 'hidden',
                }}
                placeholder={'설명 입력'}
                rows={3}
                value={description}
                onChange={e => setDescription(e.target.value)}
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
            <CardBody className='p-4'>
              <div className='mb-4 w-full pt-5 flex flex-col'>
                <label className='fw-bold form-label text-lg mb-2'>{'출력 형식'}</label>
                {/* formatString 입력창 - textarea로 변경 */}
                <textarea
                  className='nodrag w-full resize-none border rounded-lg p-3 focus:outline-none border-gray-300 focus:border-gray-400'
                  rows={5}
                  value={text}
                  onChange={handleChange}
                  placeholder='출력 형식 입력 (예: {{query}}에 대한 답변입니다)'
                />
                <div className='mt-2'>
                  <label className='form-label font-light'>출력 문장에 사용할 변수를 {'{{key}}'} 형식으로 입력해주세요.</label>
                </div>
                {/* 글자 보이는 영역 - 항상 표시 */}
                <div className='mt-4 w-full min-h-[80px] border border-gray-300 rounded-lg p-3 bg-gray-50 relative overflow-hidden'>
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
                          <DefaultButton
                            key={token.id}
                            color={color}
                            className='mr-1 mb-1'
                            style={{ maxWidth: 'calc(100% - 0.25rem)', display: 'inline-block', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}
                            onClick={() => handleOpenKeyTableModal(token.id, currentKey)}
                          >
                            {currentKey}
                          </DefaultButton>
                          <br />
                        </Fragment>
                      );
                    }
                  })}
                </div>
              </div>
            </CardBody>
          </div>
        )}

        <CardFooter>
          <NodeFooter onClick={handleFooterFold} isToggle={nodeData.isToggle as boolean} />
        </CardFooter>
      </Card>
    </div>
  );
};
