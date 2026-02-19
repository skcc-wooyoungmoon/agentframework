import { atom, useAtom } from 'jotai';

// 에이전트 빌더 상태 타입 정의
export interface AgentBuilderData {
  // 선택된 템플릿 정보
  selectedType: string;
  selectedTemplate: any;

  // 에이전트 정보
  agentName: string;
  agentDescription: string;
}

// 초기 데이터
const initialAgentBuilderData: AgentBuilderData = {
  selectedType: 'new_template',
  selectedTemplate: null,
  agentName: '',
  agentDescription: '',
};

// 에이전트 빌더 데이터 atom
export const agentBuilderDataAtom = atom<AgentBuilderData>(initialAgentBuilderData);

// 에이전트 빌더 데이터 업데이트 atom
export const updateAgentBuilderDataAtom = atom(null, (get, set, update: Partial<AgentBuilderData>) => {
  const currentData = get(agentBuilderDataAtom);
  set(agentBuilderDataAtom, { ...currentData, ...update });
});

// 에이전트 빌더 데이터 초기화 atom
export const resetAgentBuilderDataAtom = atom(null, (_, set) => {
  set(agentBuilderDataAtom, initialAgentBuilderData);
});

// 커스텀 훅
export const useAgentBuilder = () => {
  const [agentBuilderData, setAgentBuilderData] = useAtom(agentBuilderDataAtom);
  const [, updateAgentBuilderData] = useAtom(updateAgentBuilderDataAtom);
  const [, resetAgentBuilderData] = useAtom(resetAgentBuilderDataAtom);

  // 데이터 업데이트 함수들
  const updateSelectedTemplate = (templateType: string, template: any) => {
    updateAgentBuilderData({
      selectedType: templateType,
      selectedTemplate: template,
    });
  };

  const resetAgentBuilder = () => {
    updateAgentBuilderData({
      selectedTemplate: null,
      selectedType: 'new_template',
      agentName: '',
      agentDescription: '',
    });
  };

  const setSelectedType = (type: string) => {
    updateAgentBuilderData({ selectedType: type });
  };

  const setSelectedTemplate = (template: any) => {
    updateAgentBuilderData({ selectedTemplate: template });
  };

  const setAgentName = (name: string) => {
    updateAgentBuilderData({ agentName: name });
  };

  const setAgentDescription = (description: string) => {
    updateAgentBuilderData({ agentDescription: description });
  };

  return {
    // 상태
    selectedType: agentBuilderData.selectedType,
    selectedTemplate: agentBuilderData.selectedTemplate,
    agentName: agentBuilderData.agentName,
    agentDescription: agentBuilderData.agentDescription,

    // 기본 함수들
    agentBuilderData,
    setAgentBuilderData,
    updateAgentBuilderData,
    resetAgentBuilderData,

    // 데이터 업데이트 함수들
    updateSelectedTemplate,
    resetAgentBuilder,
    setSelectedType,
    setSelectedTemplate,
    setAgentName,
    setAgentDescription,

    // 유틸리티 함수
    getFinalAgentBuilderData: () => agentBuilderData,
  };
};
