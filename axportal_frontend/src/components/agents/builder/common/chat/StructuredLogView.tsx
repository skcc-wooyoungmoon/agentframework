import { type FC, useState, useEffect, useRef } from 'react';
import { useCopyHandler } from '@/hooks/common/util/useCopyHandler';

interface LogEntry {
  time: string;
  log: string;
  type?: string;
}

interface StructuredLogViewProps {
  logs: LogEntry[];
  totalTime?: string;
}

const StructuredLogView: FC<StructuredLogViewProps> = ({ logs, totalTime = '0.00s' }) => {
  const [expandedIndices, setExpandedIndices] = useState<Set<number>>(new Set());
  const scrollContainerRef = useRef<HTMLDivElement>(null);
  const prevLogsLengthRef = useRef<number>(0);
  const { handleCopy } = useCopyHandler();

  useEffect(() => {
    if (logs.length > prevLogsLengthRef.current) {
      prevLogsLengthRef.current = logs.length;

      if (scrollContainerRef.current) {
        scrollContainerRef.current.scrollTo({
          top: scrollContainerRef.current.scrollHeight,
          behavior: 'smooth',
        });
      }
    }
  }, [logs.length]);

  const toggleExpand = (index: number) => {
    const newExpanded = new Set(expandedIndices);
    if (newExpanded.has(index)) {
      newExpanded.delete(index);
    } else {
      newExpanded.add(index);
    }
    setExpandedIndices(newExpanded);
  };

  const renderLogContent = (log: LogEntry, index: number) => {
    const isExpanded = expandedIndices.has(index);

    if (log.type === 'user' || log.log.includes('👤 사용자 입력') || log.log.includes('[사용자 입력]')) {
      const match = log.log.match(/(?:👤 사용자 입력|사용자 입력): (.+)/) || log.log.match(/\[사용자 입력\] (.+)/);
      const content = match ? match[1] : log.log;

      return (
        <div className='border-l-4 border-purple-400 pl-3 py-2 bg-purple-50'>
          <div className='text-xs text-gray-500 mb-1'>{log.time}</div>
          <div className='flex items-center gap-2 mb-2'>
            <span className='text-purple-700 font-semibold text-sm'>👤 사용자</span>
          </div>
          <div className='text-purple-800 text-sm whitespace-pre-wrap'>{content}</div>
        </div>
      );
    }

    if (log.type === 'progress' || log.log.includes('🔄 진행')) {
      const nodeMatch = log.log.match(/\[([^\]]+)\]/);
      const nodeId = nodeMatch ? nodeMatch[1] : '';
      const content = log.log.replace(/\[.+?\]\s*🔄 진행(:| 상태\n)?/, '').trim();

      return (
        <div className='border-l-4 border-blue-400 pl-3 py-2 bg-blue-50'>
          <div className='text-xs text-gray-500 mb-1'>{log.time}</div>
          <div className='flex items-center gap-2 mb-1'>
            <div className='w-2 h-2 bg-blue-500 rounded-full animate-pulse'></div>
            <span className='text-blue-700 font-semibold text-sm'>🔄 진행 상태</span>
            {nodeId && <span className='text-xs text-blue-600 font-mono'>[{nodeId}]</span>}
          </div>
          <div className='bg-blue-100 p-2 rounded text-blue-800 text-sm whitespace-pre-wrap'>{content}</div>
        </div>
      );
    }

    if (log.type === 'llm_streaming' || log.log.includes('🤖 LLM')) {
      const nodeMatch = log.log.match(/\[([^\]]+)\]/);
      const nodeId = nodeMatch ? nodeMatch[1] : '';
      const content = log.log.replace(/\[.+?\]\s*🤖 LLM.*?:\s*/, '').trim();

      return (
        <div className='border-l-4 border-cyan-400 pl-3 py-2 bg-cyan-50'>
          <div className='text-xs text-gray-500 mb-1'>{log.time}</div>
          <div className='flex items-center gap-2 mb-1'>
            <span className='text-cyan-700 font-semibold text-sm'>🤖 LLM 스트리밍</span>
            {nodeId && <span className='text-xs text-cyan-600 font-mono'>[{nodeId}]</span>}
          </div>
          <div className='bg-cyan-100 p-2 rounded text-cyan-800 text-sm whitespace-pre-wrap'>{content}</div>
        </div>
      );
    }

    if (log.type === 'llm_content' || log.log.includes('💬 AI 응답')) {
      const nodeMatch = log.log.match(/\[([^\]]+)\]/);
      const nodeId = nodeMatch ? nodeMatch[1] : '';
      const fullContent = log.log;

      const lines = fullContent.split('\n');
      const needsExpand = lines.length > 10;
      const preview = isExpanded ? fullContent : lines.slice(0, 10).join('\n') + (needsExpand ? '\n...' : '');

      return (
        <div className='border-l-4 border-green-400 pl-3 py-2 bg-green-50'>
          <div className='text-xs text-gray-500 mb-1'>{log.time}</div>
          <div className='flex items-center gap-2 mb-2'>
            <span className='text-green-700 font-semibold text-sm'>💬 AI 응답 생성 완료</span>
            {nodeId && <span className='text-xs text-green-600 font-mono'>[{nodeId}]</span>}
          </div>
          <div className='bg-green-100 p-3 rounded text-green-800 text-sm whitespace-pre-wrap overflow-x-auto max-h-96 overflow-y-auto'>{preview}</div>
          <div className='flex gap-2 mt-2'>
            {needsExpand && (
              <button onClick={() => toggleExpand(index)} className='text-xs px-2 py-1 bg-green-200 hover:bg-green-300 text-green-800 rounded'>
                {isExpanded ? '접기' : '펼치기'}
              </button>
            )}
            <button onClick={() => handleCopy(fullContent)} className='text-xs px-2 py-1 bg-green-200 hover:bg-green-300 text-green-800 rounded'>
              복사
            </button>
          </div>
        </div>
      );
    }

    const nodeTypeMap: Record<string, { icon: string; label: string; color: string; bgColor: string; borderColor: string }> = {
      user: { icon: '👤', label: '사용자 입력', color: 'gray', bgColor: 'gray-50', borderColor: 'gray-400' },
      input: { icon: '📥', label: 'Input 처리', color: 'cyan', bgColor: 'cyan-50', borderColor: 'cyan-400' },
      structured: { icon: '📝', label: '업데이트', color: 'orange', bgColor: 'orange-50', borderColor: 'orange-400' },
      generator: { icon: '🤖', label: 'Generator 처리', color: 'blue', bgColor: 'blue-50', borderColor: 'blue-400' },
      output: { icon: '📤', label: '출력 처리', color: 'purple', bgColor: 'purple-50', borderColor: 'purple-400' },
      retriever: { icon: '🔍', label: '지식 검색 처리', color: 'teal', bgColor: 'teal-50', borderColor: 'teal-400' },
      rewriter: { icon: '✍️', label: 'Rewriter HyDE 처리', color: 'green', bgColor: 'green-50', borderColor: 'green-400' },
      condition: { icon: '🔀', label: '조건 분기 처리', color: 'amber', bgColor: 'amber-50', borderColor: 'amber-400' },
      reviewer: { icon: '✅', label: '검토 처리', color: 'emerald', bgColor: 'emerald-50', borderColor: 'emerald-400' },
      categorizer: { icon: '📂', label: '카테고리 분류 처리', color: 'violet', bgColor: 'violet-50', borderColor: 'violet-400' },
      union: { icon: '🔗', label: 'Union 처리', color: 'sky', bgColor: 'sky-50', borderColor: 'sky-400' },
      merger: { icon: '🔀', label: 'Merger 처리', color: 'rose', bgColor: 'rose-50', borderColor: 'rose-400' },
      reranker: { icon: '📊', label: 'ReRanker 처리', color: 'pink', bgColor: 'pink-50', borderColor: 'pink-400' },
      compressor: { icon: '📦', label: '문서 압축 처리', color: 'slate', bgColor: 'slate-50', borderColor: 'slate-400' },
      filter: { icon: '🔍', label: '문서 필터링 처리', color: 'lime', bgColor: 'lime-50', borderColor: 'lime-400' },
      agent_app: { icon: '🤖', label: 'AgentApp 처리', color: 'indigo', bgColor: 'indigo-50', borderColor: 'indigo-400' },
      tool: { icon: '🔧', label: 'Tool 처리', color: 'yellow', bgColor: 'yellow-50', borderColor: 'yellow-400' },
      coder: { icon: '💻', label: 'Coder 처리', color: 'fuchsia', bgColor: 'fuchsia-50', borderColor: 'fuchsia-400' },
      final_result: { icon: '💬', label: '최종 답변', color: 'purple', bgColor: 'purple-50', borderColor: 'purple-400' },
    };

    const NODE_LOG_PATTERNS = [
      '📝 입력 데이터 처리',
      '📝 업데이트',
      '🤖 Generator 처리',
      '📤 출력 처리',
      '🔍 지식 검색 처리',
      '🔀 조건 분기 처리',
      '✅ 검토 처리',
      '📂 카테고리 분류 처리',
      '🔗 Union 처리',
      '🔀 Merger 처리',
      '📊 ReRanker 처리',
      '📦 문서 압축 처리',
      '🔍 문서 필터링 처리',
      '🤖 AgentApp 처리',
      '🔧 Tool 처리',
      '💻 Coder 처리',
      '✍️ Rewriter HyDE',
      '💬 최종 답변',
    ] as const;

    const isNodeTypeLog = (log: LogEntry): boolean => {
      if (log.type && nodeTypeMap[log.type]) return true;
      return NODE_LOG_PATTERNS.some((pattern) => log.log.includes(pattern));
    };
    const extractNodeId = (logText: string): string => {
      const match = logText.match(/\[([^\]]+)\]/);
      return match ? match[1] : '';
    }
    const createPreview = (content: string, isExpanded: boolean, maxLines = 10): { preview: string, needsExpand: boolean } => {
      const lines = content.split('\n');
      const needsExpand = lines.length > maxLines;
      if (isExpanded) {
        return { preview: content, needsExpand };
      }
      const truncatedLines = lines.slice(0, maxLines).join('\n');
      const preview = needsExpand ? `${truncatedLines}\n...` : truncatedLines;
      return { preview, needsExpand };
    }
    const NodeLogEntry: React.FC<{
      log: LogEntry;
      index: number;
      isExpanded: boolean;
      toggleExpand: (index: number) => void;
      handleCopy: (text: string) => void;
    }> = ({ log, index, isExpanded, toggleExpand, handleCopy }) => {
      const nodeId = extractNodeId(log.log);
      const nodeInfo = (log.type && nodeTypeMap[log.type]) || nodeTypeMap['structured'];
      const { color, bgColor, borderColor } = nodeInfo;
      const { preview, needsExpand } = createPreview(log.log, isExpanded);

      return (
        <div className={`border-l-4 border-${borderColor} pl-3 py-2 bg-${bgColor}`}>
          <div className='text-xs text-gray-500 mb-1'>{log.time}</div>
          <div className={`text-${color}-700 font-semibold mb-2 flex items-center gap-2 text-sm`}>
            <span>{nodeInfo.icon} {nodeInfo.label}</span>
            {nodeId && <span className={`text-xs text-${color}-600 font-mono`}>[{nodeId}]</span>}
          </div>
          <div className={`bg-${color}-100 p-3 rounded text-${color}-800 text-sm whitespace-pre-wrap overflow-x-auto ${isExpanded ? 'max-h-[600px]' : 'max-h-96'} overflow-y-auto`}>
            {isExpanded ? log.log : preview}
          </div>
          <div className='flex gap-2 mt-2'>
            {needsExpand && (
              <button onClick={() => toggleExpand(index)} className={`text-xs px-2 py-1 bg-${color}-200 hover:bg-${color}-300 text-${color}-800 rounded`}>
                {isExpanded ? '접기' : '펼치기'}
              </button>
            )}
            <button onClick={() => handleCopy(log.log)} className={`text-xs px-2 py-1 bg-${color}-200 hover:bg-${color}-300 text-${color}-800 rounded`}>
              복사
            </button>
          </div>
        </div>
      )
    }
    if (isNodeTypeLog(log)) {
      return (<NodeLogEntry
        log={log}
        index={index}
        isExpanded={isExpanded}
        toggleExpand={toggleExpand}
        handleCopy={handleCopy}
      />)
    }

    if (log.type === 'final_result' || log.log.includes('💬 최종 답변') || log.log.includes('🎯 최종 출력') || log.log.includes('🎯 최종 결과')) {
      const nodeMatch = log.log.match(/\[([^\]]+)\]/);
      const nodeId = nodeMatch ? nodeMatch[1] : '';
      const contentMatch = log.log.match(/💬 최종 답변\n━+\n(.+)/s) || log.log.match(/최종 답변\n━+\n(.+)/s);
      const content = contentMatch ? contentMatch[1] : log.log.replace(/.*?최종 답변\n━+\n?/, '').trim();

      const lines = content.split('\n');
      const needsExpand = lines.length > 10;
      const preview = isExpanded ? content : lines.slice(0, 10).join('\n') + (needsExpand ? '\n...' : '');

      return (
        <div className='border-l-4 border-purple-400 pl-3 py-2 bg-purple-50'>
          <div className='text-xs text-gray-500 mb-1'>{log.time}</div>
          <div className='flex items-center gap-2 mb-2'>
            <span className='text-purple-700 font-semibold text-sm'>💬 최종 답변</span>
            {nodeId && <span className='text-xs text-purple-600 font-mono'>[{nodeId}]</span>}
          </div>
          <div className='bg-purple-100 p-3 rounded text-purple-800 text-sm whitespace-pre-wrap max-h-96 overflow-y-auto'>{preview}</div>
          <div className='flex gap-2 mt-2'>
            {needsExpand && (
              <button onClick={() => toggleExpand(index)} className='text-xs px-2 py-1 bg-purple-200 hover:bg-purple-300 text-purple-800 rounded'>
                {isExpanded ? '접기' : '펼치기'}
              </button>
            )}
            <button onClick={() => handleCopy(content)} className='text-xs px-2 py-1 bg-purple-200 hover:bg-purple-300 text-purple-800 rounded'>
              복사
            </button>
          </div>
        </div>
      );
    }

    if (log.type === 'tool_calls' || log.log.includes('🔧 Tool 호출')) {
      const nodeMatch = log.log.match(/\[([^\]]+)\]/);
      const nodeId = nodeMatch ? nodeMatch[1] : '';
      const content = log.log.replace(/\[.+?\]\s*🔧 Tool 호출\n/, '').trim();

      let formattedContent = content;
      try {
        if (content.startsWith('{') || content.startsWith('[')) {
          const parsed = JSON.parse(content);
          formattedContent = JSON.stringify(parsed, null, 2);
        }
      } catch (e) {
        formattedContent = content;
      }

      const lines = formattedContent.split('\n');
      const needsExpand = lines.length > 3;
      const preview = isExpanded ? formattedContent : lines.slice(0, 3).join('\n') + (needsExpand ? '\n...' : '');

      return (
        <div className='border-l-4 border-yellow-400 pl-3 py-2 bg-yellow-50'>
          <div className='text-xs text-gray-500 mb-1'>{log.time}</div>
          <div className='flex items-center gap-2 mb-2'>
            <span className='text-yellow-700 font-semibold text-sm'>🔧 Tool Calls</span>
            {nodeId && <span className='text-xs text-yellow-600'>[{nodeId}]</span>}
          </div>
          <div className='bg-yellow-100 p-2 rounded text-yellow-800 font-mono text-xs whitespace-pre-wrap overflow-x-auto'>{preview}</div>
          <div className='flex gap-2 mt-2'>
            {needsExpand && (
              <button onClick={() => toggleExpand(index)} className='text-xs px-2 py-1 bg-yellow-200 hover:bg-yellow-300 text-yellow-800 rounded'>
                {isExpanded ? '접기' : '펼치기'}
              </button>
            )}
            <button onClick={() => handleCopy(formattedContent)} className='text-xs px-2 py-1 bg-yellow-200 hover:bg-yellow-300 text-yellow-800 rounded'>
              복사
            </button>
          </div>
        </div>
      );
    }

    if (log.type === 'tool_result' || log.log.includes('✅ Tool 결과')) {
      const nodeMatch = log.log.match(/\[([^\]]+)\]/);
      const nodeId = nodeMatch ? nodeMatch[1] : '';
      const content = log.log.replace(/\[.+?\]\s*✅ Tool 결과\n/, '').trim();

      let formattedContent = content;
      try {
        if (content.startsWith('{') || content.startsWith('[')) {
          const parsed = JSON.parse(content);
          formattedContent = JSON.stringify(parsed, null, 2);
        }
      } catch (e) {
        formattedContent = content;
      }

      const lines = formattedContent.split('\n');
      const needsExpand = lines.length > 3;
      const preview = isExpanded ? formattedContent : lines.slice(0, 3).join('\n') + (needsExpand ? '\n...' : '');

      return (
        <div className='border-l-4 border-cyan-400 pl-3 py-2 bg-cyan-50'>
          <div className='text-xs text-gray-500 mb-1'>{log.time}</div>
          <div className='flex items-center gap-2 mb-2'>
            <span className='text-cyan-700 font-semibold text-sm'>✅ Tool Result</span>
            {nodeId && <span className='text-xs text-cyan-600'>[{nodeId}]</span>}
          </div>
          <div className='bg-cyan-100 p-2 rounded text-cyan-800 font-mono text-xs whitespace-pre-wrap overflow-x-auto'>{preview}</div>
          <div className='flex gap-2 mt-2'>
            {needsExpand && (
              <button onClick={() => toggleExpand(index)} className='text-xs px-2 py-1 bg-cyan-200 hover:bg-cyan-300 text-cyan-800 rounded'>
                {isExpanded ? '접기' : '펼치기'}
              </button>
            )}
            <button onClick={() => handleCopy(formattedContent)} className='text-xs px-2 py-1 bg-cyan-200 hover:bg-cyan-300 text-cyan-800 rounded'>
              복사
            </button>
          </div>
        </div>
      );
    }

    const nodeMatch = log.log.match(/\[([^\]]+)\]/);
    const nodeId = nodeMatch ? nodeMatch[1] : '';

    return (
      <div className='border-l-4 border-gray-400 pl-3 py-2 bg-gray-50'>
        <div className='text-xs text-gray-500 mb-1'>{log.time}</div>
        <div className='flex items-center gap-2 mb-1'>{nodeId && <span className='text-xs text-gray-600'>[{nodeId}]</span>}</div>
        <div className='text-gray-700 text-sm whitespace-pre-wrap'>{log.log}</div>
      </div>
    );
  };

  return (
    <div ref={scrollContainerRef} className='bg-white h-full overflow-y-auto'>
      <div className='sticky top-0 bg-gray-100 border-b border-gray-300 px-4 py-2 z-10'>
        <span className='text-sm font-semibold text-gray-700'>
          {logs.length > 0 ? `실행 중... [Total time: ${totalTime}]` : `Call completed [Total time: ${totalTime}]`}
        </span>
      </div>
      <div className='p-3 space-y-3'>
        {logs.map((log, index) => (
          <div key={index}>{renderLogContent(log, index)}</div>
        ))}
      </div>
    </div>
  );
};

export { StructuredLogView };
