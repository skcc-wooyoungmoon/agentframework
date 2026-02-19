// import { ROUTES } from "@/components/builder/constants/routes.ts";
import React, { useRef } from 'react';

import { NodeButton } from '@/components/builder/common/button/NodeButton';
import { NodeType } from '@/components/builder/types/Agents';
import { useDnD } from '@/components/builder/utils/DnDContext.tsx';
// import { useNavigate } from "react-router-dom";

interface GraphSidebarProps {
  readOnly?: boolean;
}

const GraphSidebar: React.FC<GraphSidebarProps> = ({ readOnly = false }) => {
  const [, setType] = useDnD();
  const sidebarRef = useRef<HTMLDivElement>(null);
  // const navigate = useNavigate();

  const onDragStart = (event: React.DragEvent<HTMLButtonElement>, nodeType: string) => {
    if (readOnly) return; // readOnly 모드일 때 드래그 비활성화

    if (setType) {
      setType(nodeType);
    }
    event.dataTransfer.setData('application/reactflow', nodeType);
    event.dataTransfer.effectAllowed = 'move';
  };

  const graphSidebarItems = [
    {
      title: 'Note',
      type: NodeType.Note.name,
      icon: 'ki-bookmark',
      description: 'Agent 설명, 코드 주석, 디버깅 메모, 협업 정보 등을 메모합니다.',
    },
    {
      title: 'Input',
      type: NodeType.Input.name,
      icon: 'ki-questionnaire-tablet',
      description: '사용자 질의 및 Agent 동작에 필요한 초기 변수를 설정합니다.',
    },
    {
      title: 'Output Keys',
      type: NodeType.OutputSelector.name,
      icon: 'ki-message-programming',
      description: '답변을 JSON Object로 반환합니다.',
    },
    {
      title: 'Output Chat',
      type: NodeType.OutputFormatter.name,
      icon: 'ki-message-text-2',
      description: '채팅에 표시될 최종 답변을 포맷팅후 스트림으로 반환합니다.',
    },
    {
      title: 'Generator',
      type: NodeType.AgentGenerator.name,
      icon: 'ki-technology-1',
      description: 'Prompt를 기반으로 사용자 질의에 대한 LLM 답변을 생성합니다. Tool, Few-shot을 사용할 수 있습니다.',
    },
    {
      title: 'AgentApp',
      type: NodeType.AgentApp.name,
      icon: 'ki-technology-1',
      description: '프로젝트내 Agent를 호출합니다.',
    },
    {
      title: 'Code',
      type: NodeType.AgentCoder.name,
      icon: 'ki-square-brackets',
      description: 'Code 기반으로 동작하는 Node를 제공합니다.',
    },
    {
      title: 'Categorizer',
      type: NodeType.AgentCategorizer.name,
      icon: 'ki-category',
      description: '사용자 질의를 보고, 주어진 Description에 맞는 Category를 반환합니다.',
    },
    {
      title: 'Condition',
      type: NodeType.AgentCondition.name,
      icon: 'ki-category',
      description: '조건에 따라 워크플로우를 두 개의 분기로 나누는 조건부 분기합니다.',
    },
    {
      title: 'Union',
      type: NodeType.AgentUnion.name,
      icon: 'ki-category',
      description: '지정한 포맷으로 출력을 포맷팅합니다.',
    },
    {
      title: 'Reviewer',
      type: NodeType.AgentReviewer.name,
      icon: 'ki-category',
      description: 'N개의 Input 변수를 받아, Pass/Fail 판단하는 노드입니다.',
    },
    {
      title: 'Rewriter HyDE',
      type: NodeType.RewriterHyDE.name,
      icon: 'ki-notepad-edit',
      description: 'LLM을 활용해 사용자 질의와 관련된 가상의 문서를 생성하여, 이를 유사도 기반 검색 질의로 활용합니다.',
    },
    {
      title: 'Rewriter MultiQuery',
      type: NodeType.RewriterMultiQuery.name,
      icon: 'ki-notepad-edit',
      description: 'LLM을 활용해 사용자 질문에 대한 다양한 시각에서 다중 질의를 생성하여, 이를 유사도 기반 검색 질의로 활용합니다.',
    },
    {
      title: 'Retriever',
      type: NodeType.RetrieverRetriever.name,
      icon: 'ki-search-list',
      description: 'knowledge에 질의에 대한 유사도 검색을 수행하여, 검색된 document 목록을 전달합니다.',
    },
    {
      title: 'DOC ReRanker',
      type: NodeType.RetrieverReRanker.name,
      icon: 'ki-ranking',
      description: '검색된 document를 ReRank 모델을 이용하여 다시 정렬합니다.',
    },
    {
      title: 'DOC Compressor',
      type: NodeType.RetrieverCompressor.name,
      icon: 'ki-abstract-26',
      description: '문맥 압축을 수행하여 검색 결과를 간소화합니다.',
    },
    {
      title: 'DOC Filter',
      type: NodeType.RetrieverFilter.name,
      icon: 'ki-filter-tablet',
      description: '문맥 필터링을 통해 검색 결과를 정제합니다.',
    },
    {
      title: 'Tool',
      type: NodeType.Tool.name,
      icon: 'ki-wrench',
      description: '등록한 Tool을 독립적으로 실행합니다.',
    },
  ];

  return (
    <div
      ref={sidebarRef}
      className='flex flex-col'
      style={{
        width: '100%',
        height: '100%', // 부모 컨테이너의 높이에 맞춤
        backgroundColor: 'transparent', // 배경을 투명하게
        borderRadius: '12px',
        padding: '8px', // 16px에서 12px로 줄임
        overflowY: 'auto', // 내용이 많을 때만 스크롤
        zIndex: 1, // 명시적으로 낮은 z-index 설정
        position: 'relative',
        display: 'flex',
        flexDirection: 'column',
      }}
    >
      {/* <h3 className='text-sm font-semibold text-gray-800 mb-3'>노드 추가</h3> text-lg에서 text-sm으로, mb-4에서 mb-3으로 */}
      {/* Node Items */}
      <div className='space-y-2 w-full'>
        {' '}
        {/* space-y-3에서 space-y-2로 줄임 */}
        {graphSidebarItems.length > 0 &&
          graphSidebarItems.map(item => {
            return (
              <NodeButton
                key={item.type}
                title={item.title}
                type={item.type}
                icon={item.icon}
                description={item.description}
                onDragStart={onDragStart}
                disabled={readOnly}
                sidebarRef={sidebarRef}
              />
            );
          })}
      </div>
    </div>
  );
};

export default GraphSidebar;
