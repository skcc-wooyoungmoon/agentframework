import { useEffect, useRef } from 'react';
import { useAtom } from 'jotai';
import { tracingMessagesAtom } from '@/components/agents/builder/atoms/messagesAtom';
import { type CustomNodeInnerData } from '@/components/agents/builder/types/Agents';
import { useGraphActions } from './useGraphActions';

/**
 * 각 노드 컴포넌트에서 tracingMessages를 구독하여 자신의 상태를 업데이트하는 훅
 * 🔥 기본 세팅:
 * - isRun = true, isDone = false → 연두색 (실행 중)
 * - isRun = true, isDone = true → 파란색 (완료)
 * - isError = true → 빨간색 (에러, 최우선)
 * 
 * 🔥 실시간 업데이트: tracingMessages가 변경될 때마다 즉시 상태 확인 및 업데이트
 */
export const useNodeTracing = (
  nodeId: string,
  nodeName: string | undefined,
  data: any,
  innerData: CustomNodeInnerData
) => {
  const [tracingMessages] = useAtom(tracingMessagesAtom);
  const { syncNodeData } = useGraphActions();
  const prevInnerDataRef = useRef<CustomNodeInnerData | null>(null);
  const prevTracingMessagesLengthRef = useRef<number>(0);
  const dataRef = useRef(data);
  const innerDataRef = useRef(innerData);
  const isMountedRef = useRef(true);
  useEffect(() => {
    isMountedRef.current = true;
    return () => {
      isMountedRef.current = false;
    };
  }, []);

  useEffect(() => {
    if (!isMountedRef.current) return;
    dataRef.current = data;
    innerDataRef.current = innerData;
  });

  // 새 채팅 시작 감지 - 모든 노드 초기화
  useEffect(() => {
    if (!isMountedRef.current || !syncNodeData) return;
    const currentLength = tracingMessages?.length || 0;
    const prevLength = prevTracingMessagesLengthRef.current;
    
    // 새 채팅 시작: 이전에 비어있었고 현재 메시지가 있으면
    if (prevLength === 0 && currentLength > 0) {
      const currentData = dataRef.current;
      const currentInnerData = innerDataRef.current;
      
      // 🔥 새 채팅 시작 시 모든 노드 초기화 (isRun: false, isDone: false)
      if (currentInnerData.isRun || currentInnerData.isDone) {
        const resetInnerData: CustomNodeInnerData = {
          ...currentInnerData,
          isRun: false,
          isDone: false,
          isError: false,
        };
        try {
          syncNodeData(nodeId, { ...currentData, innerData: resetInnerData });
          prevInnerDataRef.current = resetInnerData;
        } catch (error) {
          // 에러 무시
        }
      }
      prevInnerDataRef.current = null;
    }
    
    prevTracingMessagesLengthRef.current = currentLength;
  }, [tracingMessages, nodeId, syncNodeData]);

  // 🔥 실시간 노드 상태 업데이트: tracingMessages가 변경될 때마다 즉시 확인
  useEffect(() => {
    if (!isMountedRef.current || !syncNodeData) return;
    
    const currentData = dataRef.current;
    const currentInnerData = innerDataRef.current;
    const currentNodeType = dataRef.current?.type || '';
    
    // 채팅이 끝났을 때: 실행 중이었던 노드를 완료 상태로
    if (!tracingMessages || tracingMessages.length === 0) {
      if (currentInnerData.isRun && !currentInnerData.isDone && !currentInnerData.isError) {
        const updatedInnerData: CustomNodeInnerData = {
          ...currentInnerData,
          isRun: true,
          isDone: true,
          isError: false,
        };
        const prevInnerData = prevInnerDataRef.current;
        const hasChanges = !prevInnerData || 
          updatedInnerData.isRun !== prevInnerData.isRun ||
          updatedInnerData.isDone !== prevInnerData.isDone;
        
        if (hasChanges) {
          syncNodeData(nodeId, { ...currentData, innerData: updatedInnerData });
          prevInnerDataRef.current = updatedInnerData;
        }
      }
      return;
    }

    if (currentNodeType === 'note') return;
    
    // 🔥 노드 매칭 확인 함수
    // - msg에 nodeId(또는 node_name)가 명확히 들어오면 "ID 매칭"만 사용 (동일 타입 노드 동시 실행 방지)
    // - msg에 nodeId가 없거나 unknown이면 "타입 기반 휴리스틱"을 fallback으로 사용
    const isNodeMessage = (msg: any) => {
      const msgNodeId = msg.nodeId || msg.node_name || msg.nodeName || msg.node_id || '';
      const msgNodeType = msg.nodeType || msg.node_type || '';
      
      const hasConcreteNodeId = Boolean(msgNodeId) && msgNodeId !== 'unknown';
      const isNodeMatch = hasConcreteNodeId && (
        msgNodeId === nodeId ||
        (nodeName && String(nodeName).toLowerCase() === String(msgNodeId).toLowerCase()) ||
        (nodeName && String(nodeName).toLowerCase().includes(String(msgNodeId).toLowerCase())) ||
        (nodeName && String(msgNodeId).toLowerCase().includes(String(nodeName).toLowerCase()))
      );
      
      // ⚠️ 타입 기반 매칭은 msgNodeId가 없는 경우에만 fallback으로 사용
      const isTypeMatch =
        msgNodeType === currentNodeType || 
        (currentNodeType === 'agent__generator' && msgNodeType === 'generator') ||
        (currentNodeType === 'agent__categorizer' && (msgNodeType === 'categorizer' || msgNodeType === 'agent__categorizer')) ||
        (currentNodeType === 'agent__reviewer' && (msgNodeType === 'reviewer' || msgNodeType === 'agent__reviewer')) ||
        (currentNodeType === 'condition' && msgNodeType === 'condition') ||
        (currentNodeType === 'union' && msgNodeType === 'union') ||
        (currentNodeType === 'agent__coder' && (msgNodeType === 'code' || msgNodeType === 'agent__coder')) ||
        (currentNodeType === 'agent__app' && msgNodeType === 'agent__app') ||
        (currentNodeType === 'tool' && msgNodeType === 'tool') ||
        (currentNodeType === 'merger' && msgNodeType === 'merger') ||
        (currentNodeType === 'retriever__knowledge' && (msgNodeType === 'retriever__knowledge' || msgNodeType === 'retriever__main')) ||
        (currentNodeType === 'retriever__rewriter_hyde' && msgNodeType === 'retriever__rewriter_hyde') ||
        (currentNodeType === 'retriever__rewriter_multiquery' && msgNodeType === 'retriever__rewriter_multiquery') ||
        (currentNodeType === 'retriever__doc_reranker' && msgNodeType === 'retriever__doc_reranker') ||
        (currentNodeType === 'retriever__doc_compressor' && msgNodeType === 'retriever__doc_compressor') ||
        (currentNodeType === 'retriever__doc_filter' && msgNodeType === 'retriever__doc_filter') ||
        (currentNodeType === 'output__chat' && (msgNodeType === 'output__chat' || msgNodeType === 'output__keys' || msgNodeType === 'output__formatter' || msgNodeType === 'output__selector')) ||
        (currentNodeType === 'input__basic' && (msg.callback === 'user_input' || msg.updates?.user_input || msg.status === 'input'));
      
      if (hasConcreteNodeId) {
        return isNodeMatch;
      }
      return isTypeMatch;
    };
    
    // 🔥 "현재 실행중 노드"를 전역적으로 1개만 선택 (분기 시 동시 실행처럼 보이는 현상 방지)
    // - 가장 최근(running 성격) 메시지 1개를 선택하고, 해당 노드만 isRun=true (연두색)로 표시
    // - 나머지는 updates/chain_end가 있으면 완료(파란색), 아니면 idle로 유지
    // 🔥 완료된 노드는 전역 active에서 제외 (연두색 → 파란색 전환 보장)
    let globalActiveIndex = -1;
    let globalActiveMsg: any | null = null;
    
    // 먼저 각 노드의 완료 상태를 확인하기 위한 맵 생성
    const nodeCompletionMap = new Map<string, boolean>();
    for (let i = tracingMessages.length - 1; i >= 0; i--) {
      const msg = tracingMessages[i];
      const msgNodeId = msg?.nodeId || msg?.node_name || msg?.nodeName || msg?.node_id || '';
      if (!msgNodeId || msgNodeId === 'unknown') continue;
      
      if (!nodeCompletionMap.has(msgNodeId)) {
        const msgCallback = msg.callback || msg.event || '';
        const hasCompletion = 
          (msg.updates && Object.keys(msg.updates).length > 0) ||
          msgCallback === 'on_chain_end' ||
          msgCallback === 'chain_end';
        nodeCompletionMap.set(msgNodeId, hasCompletion);
      }
    }
    
    for (let i = (tracingMessages?.length || 0) - 1; i >= 0; i--) {
      const msg = tracingMessages[i];
      const msgNodeId = msg?.nodeId || msg?.node_name || msg?.nodeName || msg?.node_id || '';
      const hasConcreteNodeId = Boolean(msgNodeId) && msgNodeId !== 'unknown';
      if (!hasConcreteNodeId) continue;

      // 🔥 완료된 노드는 전역 active에서 제외
      if (nodeCompletionMap.get(msgNodeId)) {
        continue;
      }

      const msgCallback = msg.callback || msg.event || '';
      const hasRunningSignal =
        Boolean(msg.progress) ||
        Boolean(msg.llm?.content || msg.log?.llm?.content) ||
        msgCallback === 'on_chain_start' ||
        msgCallback === 'chain_start';

      if (hasRunningSignal) {
        globalActiveIndex = i;
        globalActiveMsg = msg;
        break;
      }
    }
    const isGlobalActiveNode = globalActiveIndex !== -1 && globalActiveMsg ? isNodeMessage(globalActiveMsg) : false;

    // 🔥 모든 메시지 타입을 찾기 (역순으로 확인하여 가장 최근 메시지 우선)
    let latestUpdatesIndex = -1;
    let latestProgressIndex = -1; // 🔥 progress 메시지도 찾기
    let latestStartIndex = -1;
    let latestEndIndex = -1;
    let latestLlmContentIndex = -1; // 🔥 llm.content가 있는 메시지도 실행 중으로 감지
    
    for (let i = tracingMessages.length - 1; i >= 0; i--) {
      const msg = tracingMessages[i];
      if (!isNodeMessage(msg)) continue;
      
      const msgCallback = msg.callback || msg.event || '';
      
      // 🔥 updates가 있는 메시지 찾기
      const hasUpdates = msg.updates && Object.keys(msg.updates).length > 0;
      if (hasUpdates && latestUpdatesIndex === -1) {
        latestUpdatesIndex = i;
      }
      
      // 🔥 progress 메시지 찾기 (실행 중 상태로 되돌릴 수 있음)
      if (msg.progress && latestProgressIndex === -1) {
        latestProgressIndex = i;
      }
      
      // 가장 최근 on_chain_end 찾기
      if ((msgCallback === 'on_chain_end' || msgCallback === 'chain_end') && latestEndIndex === -1) {
        latestEndIndex = i;
      }
      
      // 가장 최근 on_chain_start 찾기
      if ((msgCallback === 'on_chain_start' || msgCallback === 'chain_start') && latestStartIndex === -1) {
        latestStartIndex = i;
      }
      
      // 🔥 llm.content가 있는 메시지도 실행 중으로 감지 (실시간 색상 변경용)
      // 🔥 가장 최근 llm.content 메시지 찾기 (스트리밍 중 여러 개가 있을 수 있음)
      if ((msg.llm?.content || msg.log?.llm?.content) && latestLlmContentIndex === -1) {
        latestLlmContentIndex = i;
      }
    }
    
    // 🔥 상태 판단: 가장 최근 이벤트 기준으로 즉시 판단
    // 🔥 progress나 llm.content가 updates보다 최근이면 실행 중 상태로 되돌림
    let isRun = false;
    let isDone = false;
    
    // ✅ 순차 표시 규칙
    // - 전역에서 "현재 실행중 노드" 1개만 isRun=true, isDone=false (연두색)
    // - 자신에게 updates/chain_end가 있으면 완료(isDone=true, 파란색)
    // - 분기 노드들이 동시에 running으로 보이지 않도록 running은 전역 active에만 부여
    // 🔥 완료 신호(updates/chain_end)가 있으면 무조건 완료 상태로 전환 (연두색 → 파란색)
    // 🔥 완료 신호가 있으면 전역 active 여부와 관계없이 완료 상태로 설정
    const hasCompletionSignal = latestUpdatesIndex !== -1 || latestEndIndex !== -1;
    
    if (hasCompletionSignal) {
      isRun = true;
      isDone = true; // 완료 (파란색) - 연두색에서 파란색으로 전환
    } else if (isGlobalActiveNode) {
      isRun = true;
      isDone = false; // 실행 중 (연두색) - 전역 active 노드만 실행 중으로 표시
    } else {
      isRun = false;
      isDone = false; // 대기 중
    }
    
    // 🔥 에러 상태 확인
    const isError = tracingMessages.some(msg => {
      if (!isNodeMessage(msg)) return false;
      const msgCallback = msg.callback || msg.event || '';
      return msgCallback === 'on_chain_error' || msgCallback === 'chain_error';
    });
    
    const updatedInnerData: CustomNodeInnerData = {
      ...currentInnerData,
      isRun,
      isDone,
      isError,
      logData: currentInnerData.logData ?? [],
    };

    // 🔥 변경이 있으면 즉시 업데이트 (실시간 반응)
    const prevInnerData = prevInnerDataRef.current;
    const hasChanges = !prevInnerData || 
      updatedInnerData.isRun !== prevInnerData.isRun ||
      updatedInnerData.isDone !== prevInnerData.isDone ||
      updatedInnerData.isError !== prevInnerData.isError;

    // 🔥 updates가 발견되면 무조건 업데이트 (완료 상태 강제 적용)
    const shouldForceUpdate = latestUpdatesIndex !== -1;

    // 🔥 updates가 있으면 무조건 업데이트, 아니면 변경이 있을 때만 업데이트
    if ((hasChanges || shouldForceUpdate) && isMountedRef.current && syncNodeData) {
      try {
        const nodeDataToSync = { ...currentData, innerData: updatedInnerData };
        syncNodeData(nodeId, nodeDataToSync);
        prevInnerDataRef.current = updatedInnerData;
      } catch (error) {
        // 에러 무시
      }
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [tracingMessages, nodeId, nodeName]);
};