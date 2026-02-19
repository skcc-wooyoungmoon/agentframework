import { useCallback, useEffect, useRef, useState } from 'react';

import { useQueryClient } from '@tanstack/react-query';

import { UIButton2, UIIcon2, UIToggle, UITooltip, UITypography } from '@/components/UI/atoms';
import {
  type UIAccordionItemProps,
  UIArticle,
  UIDropdown,
  UIFormField,
  UIInput,
  UIList,
  UIPopupBody,
  UIPopupFooter,
  UIPopupHeader,
  UITextArea2,
  UIUnitGroup,
} from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import type { LayerPopupProps } from '@/hooks/common/layer';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { InfPromptWorkFlowPopupPage } from '@/pages/prompt';
import { useGetInfPromptBuiltin, useUpdateInfPrompt } from '@/services/prompt/inference/inferencePrompts.services';
import { useModal } from '@/stores/common/modal/useModal';

type InfPromptVarUI = {
  id: string;
  label: string;
  variableEnabled: boolean;
  regexValue?: string;
  tokenLimitEnabled: boolean;
  tokenLimitValue?: string;
  showAddButton?: boolean;
};

interface InfPromptEditPopupPageProps extends LayerPopupProps {
  projectId?: string;
  promptUuid?: string; // 수정할 프롬프트 ID
  onEditSuccess?: () => void; // 수정 성공 시 콜백 추가
  // 기존 데이터
  initialName?: string;
  initialTemplate?: string;
  initialSystemPrompt?: string;
  initialUserPrompt?: string;
  initialTags?: string[];
  initialRelease?: boolean; // 현재 릴리즈 상태
  initialVariables?: InfPromptVarUI[];
}

/**
 * 프롬프트 > 추론 프롬프트 > 추론 프롬프트 수정 팝업
 */
export const InfPromptEditPopupPage = ({
  currentStep,
  onClose,
  promptUuid,
  onEditSuccess,
  initialName = '',
  initialTemplate = 'none',
  initialSystemPrompt = '',
  initialUserPrompt = '',
  initialTags = [],
  initialRelease = false,
  initialVariables = [],
}: InfPromptEditPopupPageProps) => {
  const [datasetName, setDatasetName] = useState(initialName);
  const [datasetDescription] = useState('');

  const [tags, setTags] = useState<string[]>(initialTags);
  const [selectedTemplate, setSelectedTemplate] = useState(initialTemplate);
  const [selectedTemplateName, setSelectedTemplateName] = useState('None');
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [hasAttemptedSave, setHasAttemptedSave] = useState<boolean>(false);
  const [hasTemplateOverride, setHasTemplateOverride] = useState<boolean>(false);
  const { openAlert, openConfirm } = useModal();
  const { showNoEditContent } = useCommonPopup();
  const queryClient = useQueryClient();

  // 에러 상태
  const [, setErrorName] = useState<boolean>(false);

  // 프롬프트 아코디언 데이터
  const [promptAccordionItems, setPromptAccordionItems] = useState<Omit<UIAccordionItemProps, 'content'>[]>([]);

  // 프롬프트 텍스트 값들을 관리하는 상태
  const [promptContents, setPromptContents] = useState<string[]>([initialSystemPrompt, initialUserPrompt]);

  // 빌트인 프롬프트 데이터 가져오기
  const { data: builtinData } = useGetInfPromptBuiltin();
  const { openModal } = useModal();

  // 초기화 상태 추적을 위한 Ref
  const lastInitState = useRef<{ step: number; promptUuid?: string }>({ step: -1, promptUuid: undefined });

  // 템플릿별 설명 데이터
  const templateDescriptions: Record<string, { title: string; description: string; variables: string }> = {
    AGENT__GENERATOR: {
      title: 'AGENT__GENERATOR 란?',
      description:
        '사용자의 질문에 직접 답변할 때 사용되는 “기본 응답 템플릿”을 만드는 기능입니다. 사용자가 입력한 문장 + 관련 컨텍스트(검색 결과 등)를 활용해 답변을 생성할 때 사용됩니다.',
      variables: '기본 변수: query, context',
    },
    AGENT__CATEGORIZER: {
      title: 'AGENT__CATEGORIZER 란?',
      description:
        '입력된 내용을 사전에 정의된 카테고리로 분류하는 템플릿을 만드는 기능입니다. 사용자의 질문이 어떤 타입인지(예: 계좌 문의/대출 문의/오류 문의 등) 분류할 때 사용됩니다.',
      variables: '기본 변수: topic_classes, topic_descripstions, query, context',
    },
    RETRIEVER__REWRITER_HYDE: {
      title: 'RETRIEVER__REWRITER_HYDE 란?',
      description: '검색 성능을 높이기 위해 “가상 문서(HYDE 문서)” 를 생성하는 템플릿입니다. 검색 엔진이 더 관련성 높은 결과를 찾을 수 있도록 돕습니다.',
      variables: '기본 변수: query',
    },
    RETRIEVER__REWRITER_MULTYQUERY: {
      title: 'RETRIEVER_REWRITER_MULTYQUERY 란?',
      description: '하나의 질문을 여러 개의 다양한 검색 쿼리로 재작성하는 기능입니다. 다양한 관점에서 질문을 표현해 검색 범위를 확장할 때 사용됩니다.',
      variables: '기본 변수: query, num_queries',
    },
    RETRIEVER__DOC_COMPRESSOR: {
      title: 'RETRIEVER__DOC_COMPRESSOR 란?',
      description: '검색된 문서들을 불필요한 내용은 제거하고 필요한 핵심 정보만 압축하는 템플릿입니다. 긴 문서를 “핵심 요약본”으로 만들때 사용됩니다.',
      variables: '기본 변수: query, context',
    },
    RETRIEVER__DOC_FILTER: {
      title: 'RETRIEVER__DOC_FILTER란?',
      description: '검색된 문서 중에서 품질이 낮거나 관련성이 낮은 문서를 제외하는 템플릿입니다. 관련 문서만 남겨 LLM이 더 정확한 답변을 하도록 돕습니다.',
      variables: '기본 변수: query, context',
    },
    TEMPLATE__TRANSLATOR_ANY_TO_KOR: {
      title: 'TEMPLATE__TRANSLATOR_ANY_TO_KOR 란?',
      description: '여러 언어로 된 입력을 한국어로 번역하는 템플릿입니다. 다국어 입력을 한글화하여 처리하고 싶을 때 사용합니다.',
      variables: '기본 변수: query, context',
    },
    TEMPLATE__TRANSLATOR_KOR_TO_ANY: {
      title: 'TEMPLATE__TRANSLATOR_KOR_TO_ANY 란?',
      description: '한국어를 원하는 언어로 번역하는 템플릿입니다. 번역 품질, 톤, 대상 언어 스타일 등을 정의할 수 있습니다.',
      variables: '기본 변수: query, context',
    },
    AGENT__REVIEWER: {
      title: 'AGENT__REVIEWER 란?',
      description: '생성된 콘텐츠(또는 다른 에이전트의 출력물)의 품질을 평가하고 수정하는 템플릿입니다. 오타, 문맥 오류, 논리적 문제, 정책 위반 여부 등을 확인합니다.',
      variables: '기본 변수: query, context',
    },
    PLAN_AND_EXECUTE_PLANNER: {
      title: 'PLAN_AND_EXECUTE_PLANNER 란?',
      description:
        '사용자의 요청을 여러 단계의 실행 계획(Plan) 으로 구체화하는 템플릿입니다. 사용자의 질문이 복잡하거나 여러 단계를 거쳐야 해결되는 경우에 구조화하는 역할을 합니다.',
      variables: '기본 변수: query, context',
    },
    PLAN_AND_EXECUTE_EXECUTOR: {
      title: 'PLAN_AND_EXECUTE_EXECUTOR 란?',
      description: 'Planner가 만든 계획대로 실제로 단계별 작업을 실행하는 템플릿입니다. Planner가 만든 Plan을 받아서 각 단계에서 어떤 행동을 해야 하는지 실제 실행합니다.',
      variables: '기본 변수: query, context',
    },
    PLAN_AND_EXECUTE_REVIEWER: {
      title: 'PLAN_AND_EXECUTE_REVIEWER 란?',
      description: 'Executor가 수행한 결과가 계획대로 잘 실행됐는지, 오류는 없는지, 품질 문제가 없는지 검토하는 템플릿입니다.',
      variables: '기본 변수: query, context',
    },
  };

  // 선택된 템플릿의 설명 가져오기
  const currentTemplateInfo = selectedTemplate !== 'None' ? templateDescriptions[selectedTemplateName] : null;

  // 팝업이 열릴 때 기존 데이터로 초기화 (한 번만)
  useEffect(() => {
    const isOpening = currentStep === 1 && lastInitState.current.step !== 1;
    const isPromptChanged = currentStep === 1 && promptUuid !== lastInitState.current.promptUuid;

    if (isOpening || isPromptChanged) {
      // Edit 페이지 초기화
      // 모든 상태를 전달받은 초기값으로 재설정 (처음 열릴 때만)
      setDatasetName(initialName);
      setTags(initialTags);
      setSelectedTemplate(initialTemplate);
      setPromptContents([initialSystemPrompt, initialUserPrompt]);
      setVariableSettingsItems((initialVariables || []).map(item => ({ ...item })));
      setHasTemplateOverride(false);

      // 기본 아코디언 아이템 설정 (기존 데이터 포함)
      setPromptAccordionItems([
        {
          title: '시스템 프롬프트',
          defaultOpen: true,
          showNoticeIcon: true,
        },
        {
          title: '유저 프롬프트',
          defaultOpen: true,
          showNoticeIcon: false,
        },
      ]);
    }

    // 현재 상태 저장
    lastInitState.current = { step: currentStep, promptUuid };
  }, [currentStep, promptUuid, initialName, initialTags, initialTemplate, initialSystemPrompt, initialUserPrompt, initialVariables]);

  const updateInfPromptMutation = useUpdateInfPrompt({
    onSuccess: () => {
      // 1. 모든 추론 프롬프트 관련 쿼리 무효화 (통일된 키로)
      queryClient.invalidateQueries({ queryKey: ['inf-prompts'] });

      // 2. 부모 페이지의 콜백 호출
      onEditSuccess?.();

      openAlert({
        title: '완료',
        bodyType: 'text',
        message: '수정 사항이 저장되었습니다.',
        confirmText: '확인',
      });
    },
    onError: () => {
      // 수정 실패 처리
    },
    onSettled: () => {
      handleClose();
    },
  });

  // 템플릿 선택 시 프롬프트 아코디언 아이템 업데이트
  useEffect(() => {
    if (selectedTemplate && selectedTemplate !== 'none' && builtinData?.data) {
      const selectedBuiltin = builtinData.data.find(item => item.uuid === selectedTemplate);
      if (selectedBuiltin) {
        const messages = Array.isArray(selectedBuiltin.messages) ? selectedBuiltin.messages : [];
        const newAccordionItems = messages.map((_message, index) => ({
          title: index === 0 ? '시스템 프롬프트' : '유저 프롬프트',
          defaultOpen: index === 0, // 첫 번째 항목만 열기
          showNoticeIcon: false,
        }));
        setPromptAccordionItems(newAccordionItems);

        // 프롬프트 내용도 별도로 저장
        setPromptContents(messages.map(message => message.message));

        // variableSettingsItems도 업데이트
        const variables = Array.isArray(selectedBuiltin.variables) ? selectedBuiltin.variables : [];
        const newVariableSettingsItems = variables.map((variable, index) => ({
          id: `${variable.variable}-${index}`,
          label: variable.variable,
          variableEnabled: false, // 사용자가 명시적으로 활성화하도록 기본값 false
          regexValue: variable.validation || '',
          tokenLimitEnabled: false, // 사용자가 명시적으로 활성화하도록 기본값 false
          tokenLimitValue: variable.tokenLimit ? variable.tokenLimit.toString() : '',
          showAddButton: false,
        }));
        setVariableSettingsItems(newVariableSettingsItems);
        setHasTemplateOverride(true);
      }
    } else if (selectedTemplate === 'none' && hasTemplateOverride) {
      // 템플릿 선택 후 None으로 되돌리는 경우에만 초기화
      setPromptAccordionItems([
        {
          title: '시스템 프롬프트',
          defaultOpen: true,
          showNoticeIcon: true,
        },
        {
          title: '유저 프롬프트',
          defaultOpen: true,
          showNoticeIcon: false,
        },
      ]);

      setPromptContents(['', '']);
      setVariableSettingsItems([]);
      setHasTemplateOverride(false);
    }
  }, [selectedTemplate, builtinData, hasTemplateOverride]);

  // save 버튼을 한 번 누른 후에는 실시간으로 유효성 검사
  useEffect(() => {
    if (hasAttemptedSave) {
      const isNameEmpty = datasetName.trim() === '';

      setErrorName(isNameEmpty);
    }
  }, [hasAttemptedSave, datasetName, promptContents]);

  // 프롬프트 내용 변경 핸들러
  const handlePromptContentChange = (index: number, newContent: string) => {
    // 프롬프트 텍스트 값 업데이트
    setPromptContents(prev => {
      const newContents = [...prev];
      newContents[index] = newContent;
      return newContents;
    });
  };

  const detectAndSyncVariables = useCallback((contents: string[]) => {
    const allContent = contents.join(' ');
    const variableRegex = /\{\{([a-zA-Z가-힣0-9_]+)\}\}/g;
    const detected = new Set<string>();
    let match;

    while ((match = variableRegex.exec(allContent)) !== null) {
      const variableName = match[1].trim();
      if (variableName) {
        detected.add(variableName);
      }
    }

    const uniqueVariables = Array.from(detected);

    setVariableSettingsItems(prev => {
      if (uniqueVariables.length === 0) {
        return prev.length ? [] : prev;
      }

      const prevMap = new Map(prev.map(item => [item.label, item]));
      const nextItems = uniqueVariables.map((label, index) => {
        const existing = prevMap.get(label);
        if (existing) {
          return existing;
        }
        return {
          id: `${label}-${Date.now()}-${index}`,
          label,
          variableEnabled: true,
          regexValue: '',
          tokenLimitEnabled: true,
          tokenLimitValue: '',
          showAddButton: false,
        };
      });

      return nextItems;
    });
  }, []);

  useEffect(() => {
    detectAndSyncVariables(promptContents);
  }, [promptContents, detectAndSyncVariables]);

  // 변수 속성 설정 데이터
  const [variableSettingsItems, setVariableSettingsItems] = useState<InfPromptVarUI[]>(() => (initialVariables || []).map(item => ({ ...item })));

  // 워크플로우 팝업 상태
  const selectedPromptIndexRef = useRef<number | null>(null);

  const handleClose = () => {
    // 모든 상태 초기화
    setDatasetName('');
    setTags([]);
    setSelectedTemplate('none');
    setIsDropdownOpen(false);
    setHasAttemptedSave(false);
    setErrorName(false);

    // 프롬프트 아코디언 아이템 초기화 (시스템 프롬프트, 유저 프롬프트)
    setPromptAccordionItems([
      {
        title: '시스템 프롬프트',
        defaultOpen: true,
        showNoticeIcon: true,
      },
      {
        title: '유저 프롬프트',
        defaultOpen: true,
        showNoticeIcon: false,
      },
    ]);

    // 프롬프트 내용 초기화 (시스템 프롬프트, 유저 프롬프트)
    setPromptContents(['', '']);

    // 변수 설정 초기화
    setVariableSettingsItems([]);
    setHasTemplateOverride(false);

    // 워크플로우 팝업 상태 초기화
    selectedPromptIndexRef.current = null;

    onClose();
  };

  const handleCancel = () => {
    openConfirm({
      title: '안내',
      message: '화면을 나가시겠어요?\n입력한 정보가 저장되지 않을 수 있습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        handleClose();
      },
      onCancel: () => {},
    });
  };

  /**
   * 태그 변경 핸들러
   * 최대 7개까지만 추가 가능
   *
   * @param newTags - 새로운 태그 배열
   */
  const handleTagsChange = (newTags: string[]) => {
    // 최대 7개까지만 허용
    if (newTags.length <= 7) {
      setTags(newTags);
    }
  };

  /**
   * 추론 프롬프트 저장
   */
  const handleSave = () => {
    const isSameStringArray = (a: string[], b: string[]) => a.length === b.length && a.every((v, i) => v === b[i]);
    const normalizeVarList = (list: InfPromptVarUI[]) =>
      list
        .map(v => ({
          label: v.label,
          variableEnabled: v.variableEnabled,
          regexValue: v.regexValue || '',
          tokenLimitEnabled: v.tokenLimitEnabled,
          tokenLimitValue: v.tokenLimitValue || '',
        }))
        .sort((x, y) => x.label.localeCompare(y.label));

    const isSameVarList = (a: InfPromptVarUI[], b: InfPromptVarUI[]) => {
      const na = normalizeVarList(a);
      const nb = normalizeVarList(b);
      if (na.length !== nb.length) return false;
      return na.every((v, i) => {
        const t = nb[i];
        return (
          v.label === t.label &&
          v.variableEnabled === t.variableEnabled &&
          v.regexValue === t.regexValue &&
          v.tokenLimitEnabled === t.tokenLimitEnabled &&
          v.tokenLimitValue === t.tokenLimitValue
        );
      });
    };

    const isNoEdit =
      datasetName === initialName &&
      isSameStringArray(tags, initialTags) &&
      selectedTemplate === initialTemplate &&
      (promptContents[0] || '') === initialSystemPrompt &&
      (promptContents[1] || '') === initialUserPrompt &&
      isSameVarList(variableSettingsItems, initialVariables);

    if (isNoEdit) {
      showNoEditContent();
      return;
    }

    // 저장 시도 플래그 설정
    setHasAttemptedSave(true);

    // 변수 설정 (state 직접 변형 금지)
    const sanitizedVariableSettingsItems = variableSettingsItems.map(item => {
      const next = { ...item };

      if (next.variableEnabled && (!next.regexValue || next.regexValue.trim() === '')) {
        next.variableEnabled = false;
      }

      const parsedTokenLimitValue = parseInt(next.tokenLimitValue || '0');
      if (next.tokenLimitEnabled && (!next.tokenLimitValue || next.tokenLimitValue.trim() === '' || Number.isNaN(parsedTokenLimitValue) || parsedTokenLimitValue <= 0)) {
        next.tokenLimitEnabled = false;
      }

      return next;
    });

    setVariableSettingsItems(sanitizedVariableSettingsItems);

    // string[]를 infPromptTags[]로 변환
    const infPromptTags = tags.map(tag => ({ tag }));

    // promptContents를 messages로 변환 (시스템 프롬프트: mtype 1, 유저 프롬프트: mtype 2)
    const messages = promptContents.map((content, index) => {
      const promptTitle = promptAccordionItems[index]?.title;
      const mtype = promptTitle === '시스템 프롬프트' ? 1 : 2;

      return {
        mtype,
        message: content,
        order: index + 1,
      };
    });

    // variableSettingsItems를 variables 형식으로 변환
    const finalVariables = sanitizedVariableSettingsItems.map(item => ({
      variable: item.label,
      validation: item.regexValue || '',
      validationFlag: item.variableEnabled || false,
      tokenLimitFlag: item.tokenLimitEnabled || false,
      tokenLimit: parseInt(item.tokenLimitValue || '0') || 0,
    }));

    const requestData = {
      promptUuid: promptUuid || '', // URL 바인딩용 promptUuid
      newName: datasetName, // 백엔드 스펙에 맞는 필드명
      desc: datasetDescription,
      messages: messages,
      release: initialRelease, // 현재 릴리즈 상태 유지
      tags: infPromptTags,
      variables: finalVariables,
    };
    updateInfPromptMutation.mutate(requestData);
  };

  // 워크플로우 추가 핸들러
  const handleAddWorkFlow = (promptIndex: number) => {
    selectedPromptIndexRef.current = promptIndex;
    let selectedWorkFlowsCapture: Array<{ workflowId: string; xmlText?: string; workflowName?: string; versionNo?: number }> = [];

    openModal({
      type: 'large',
      title: '워크플로우 선택',
      body: <InfPromptWorkFlowPopupPage onSelectionChange={list => (selectedWorkFlowsCapture = list)} />,
      showFooter: true,
      confirmText: '확인',
      onConfirm: () => {
        if (selectedWorkFlowsCapture.length > 0) {
          handleWorkFlowSelected(selectedWorkFlowsCapture);
        }
      },
    });
  };

  // 워크플로우 선택 완료 핸들러 (다중 선택 지원)
  const handleWorkFlowSelected = (selectedWorkFlows: Array<{ workflowId: string | { xmlText?: string }; xmlText?: string; workflowName?: string; versionNo?: number }>) => {
    if (selectedPromptIndexRef.current !== null && selectedWorkFlows.length > 0) {
      // 각 워크플로우의 xmlText를 추출
      const xmlTexts: string[] = [];
      selectedWorkFlows.forEach(workflow => {
        // workflowId가 객체인 경우 처리 (잘못된 데이터 구조 대응)
        let actualXmlText = workflow.xmlText;
        if (!actualXmlText && typeof workflow.workflowId === 'object' && workflow.workflowId?.xmlText) {
          actualXmlText = workflow.workflowId.xmlText;
        }

        if (actualXmlText && actualXmlText.trim() !== '') {
          xmlTexts.push(actualXmlText);
        }
      });

      // XML 텍스트들을 하나의 문자열로 합치기
      const combinedXmlText = xmlTexts.join('\n\n');

      // 현재 프롬프트 내용 가져오기
      const currentContent = promptContents[selectedPromptIndexRef.current as number] || '';

      // 워크플로우 XML 텍스트를 현재 내용에 추가
      const separator = currentContent ? '\n\n' : '';
      const updatedContent = currentContent + separator + combinedXmlText;

      // 프롬프트 내용 업데이트
      setPromptContents(prev => {
        const newContents = [...prev];
        newContents[selectedPromptIndexRef.current as number] = updatedContent;
        return newContents;
      });
    }
  };

  // const handleAddPrompt = () => {
  //   // 이미 2개(시스템 프롬프트, 유저 프롬프트)가 있으면 추가하지 않음
  //   if (promptAccordionItems.length >= 2) {
  //     return;
  //   }

  //   const promptTypes = ['시스템 프롬프트', '유저 프롬프트'];
  //   const usedTypes = promptAccordionItems.map(item => item.title);
  //   const availableType =
  //     promptTypes.find(type => !usedTypes.includes(type)) || '시스템 프롬프트';

  //   const newIndex = promptAccordionItems.length;
  //   setPromptAccordionItems([
  //     ...promptAccordionItems,
  //     {
  //       title: availableType,
  //       content: (
  //         <UITextField
  //           value=''
  //           onChange={value => handlePromptContentChange(newIndex, value)}
  //           placeholder='프롬프트를 입력하세요'
  //           className='w-full'
  //         />
  //       ),
  //       defaultOpen: false,
  //       showNoticeIcon: false,
  //     },
  //   ]);

  //   // 프롬프트 내용 배열에도 빈 문자열 추가
  //   setPromptContents(prev => [...prev, '']);
  // };

  return (
    <UILayerPopup
      isOpen={currentStep === 1}
      onClose={handleClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        /* 좌측 Step 영역 콘텐츠 */
        <UIPopupAside>
          <UIPopupHeader title='추론 프롬프트 수정' description='' position='left' />
          <UIPopupBody>
            <UIArticle>{/* 추가 콘텐츠 필요 시 여기에 작성 */}</UIArticle>
          </UIPopupBody>
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} onClick={handleSave} disabled={updateInfPromptMutation.isPending}>
                  저장
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      {/* 우측 Contents 영역 콘텐츠 */}
      <section className='section-popup-content'>
        <UIPopupBody>
          {/* 이름 입력 필드 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                이름
              </UITypography>
              <UIInput.Text value={datasetName} onChange={e => setDatasetName(e.target.value)} placeholder='이름 입력' />
            </UIFormField>
          </UIArticle>

          {/* 템플릿 드롭다운 */}
          <UIArticle>
            <UIUnitGroup gap={8} direction='column'>
              <UIFormField gap={4} direction='row'>
                <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-sb'>
                  템플릿
                </UITypography>
                <UITooltip
                  trigger='click'
                  position='bottom-start'
                  type='notice'
                  title=''
                  items={['템플릿 선택 시 템플릿 사용 가이드가 노출됩니다.']}
                  bulletType='default'
                  showArrow={false}
                  showCloseButton={true}
                  className='tooltip-wrap ml-1'
                >
                  <UIButton2 className='btn-text-only-16 p-0'>
                    <UIIcon2 className='ic-system-20-info' />
                  </UIButton2>
                </UITooltip>
              </UIFormField>
              <UIDropdown
                value={selectedTemplate}
                onSelect={select => {
                  const label = builtinData?.data.find(item => item.uuid === select)?.name;
                  setSelectedTemplate(select);
                  setSelectedTemplateName(label || 'None');
                  setIsDropdownOpen(false);
                }}
                onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                isOpen={isDropdownOpen}
                placeholder='템플릿 선택'
                options={[
                  { value: 'none', label: 'None' },
                  ...(builtinData?.data.map(item => ({
                    value: item.uuid,
                    label: item.name,
                  })) ?? []),
                ]}
                required={false}
                className='w-[50%]'
              />
              {currentTemplateInfo && (
                <div className='box-fill'>
                  <UIUnitGroup gap={8} direction='column' align='start'>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0 6px' }}>
                      <UIIcon2 className='ic-system-16-info-gray' />
                      <UITypography variant='body-2' className='secondary-neutral-600 text-sb'>
                        {currentTemplateInfo.title}
                      </UITypography>
                    </div>
                    <div style={{ paddingLeft: '22px' }}>
                      <UIUnitGroup gap={8} direction='column' align='start'>
                        <UIList
                          gap={4}
                          direction='column'
                          className='ui-list_dash'
                          data={[
                            {
                              dataItem: (
                                <UITypography variant='body-2' className='secondary-neutral-600'>
                                  {currentTemplateInfo.description}
                                </UITypography>
                              ),
                            },
                          ]}
                        />
                        <UIList
                          gap={4}
                          direction='column'
                          className='ui-list_dash'
                          data={[
                            {
                              dataItem: (
                                <UITypography variant='body-2' className='secondary-neutral-600'>
                                  {currentTemplateInfo.variables}
                                </UITypography>
                              ),
                            },
                          ]}
                        />
                      </UIUnitGroup>
                    </div>
                  </UIUnitGroup>
                </div>
              )}
            </UIUnitGroup>
          </UIArticle>

          {/* 프롬프트 섹션 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UIFormField gap={4} direction='row'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                  프롬프트
                </UITypography>
                <UITooltip
                  trigger='click'
                  position='bottom-start'
                  type='notice'
                  title='프롬프트 안내'
                  items={[
                    '{{변수명}}을 입력하면 해당 변수의 속성 설정 영역이 자동으로 표시됩니다.',
                    '워크플로우 추가 버튼으로 프롬프트에 사용할 워크플로우(XML)를 불러올 수 있습니다. 워크플로우는 프롬프트 > 워크플로우 메뉴에서 관리할 수 있습니다.',
                  ]}
                  bulletType='default'
                  showArrow={false}
                  showCloseButton={true}
                  className='tooltip-wrap ml-1'
                >
                  <UIButton2 className='btn-text-only-16 p-0'>
                    <UIIcon2 className='ic-system-20-info' />
                  </UIButton2>
                </UITooltip>
              </UIFormField>

              <UIUnitGroup gap={16} direction='column'>
                {[0, 1].map(index => (
                  <div key={index} className='border border-gray-200 bg-white rounded-[16px]'>
                    <div className='flex items-center justify-between px-[32px] py-[20px]'>
                      <div className='flex items-center gap-3'>
                        <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                          {index === 0 ? '시스템 프롬프트' : '유저 프롬프트'}
                        </UITypography>
                      </div>
                      <UIButton2 className='btn-text-14-semibold-point' style={{ display: 'flex', alignItems: 'center', gap: '4px' }} onClick={() => handleAddWorkFlow(index)}>
                        <UIIcon2 className='ic-system-24-add' />
                        워크플로우 추가
                      </UIButton2>
                    </div>
                    <div className='pl-[32px] pr-[32px]'>
                      <UITextArea2
                        className='w-full px-0 bg-white resize-none focus:outline-none'
                        value={promptContents[index] || ''}
                        placeholder=''
                        onChange={e => handlePromptContentChange(index, e.target.value)}
                        noBorder={true}
                      />
                    </div>
                  </div>
                ))}
              </UIUnitGroup>
            </UIFormField>
          </UIArticle>

          {/* 변수 속성 설정 섹션 */}
          {variableSettingsItems.length > 0 && (
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <div className='inline-flex items-center'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb'>
                    변수 속성 설정
                  </UITypography>
                  <UITooltip
                    trigger='click'
                    position='bottom-start'
                    type='notice'
                    title='변수 속성 설정 안내'
                    items={[
                      '프롬프트에 변수가 감지되면 해당 변수의 형식과 입력 범위를 정의할 수 있는 설정 영역이 노출됩니다.',
                      '변수에 허용할 입력 형식을 정규표현식으로 지정해 유효한 값만 입력되도록 제어합니다.',
                      '변수에 입력될 최대 토큰 수를 지정해 과도한 길이의 입력을 방지합니다.',
                    ]}
                    bulletType='default'
                    showArrow={false}
                    showCloseButton={true}
                    className='tooltip-wrap ml-1'
                  >
                    <UIButton2 className='btn-text-only-16 p-0'>
                      <UIIcon2 className='ic-system-20-info' />
                    </UIButton2>
                  </UITooltip>
                </div>
                <div className='variable-settings-card flex flex-col gap-4'>
                  {variableSettingsItems.map(item => (
                    <div key={item.id} className='variable-setting-item'>
                      {/* 라벨 */}
                      <div className='flex items-center gap-2 mb-2 h-6'>
                        <div className='w-1 h-6 flex items-center'>
                          <div className='w-1 h-1 bg-gray-500 rounded-full'></div>
                        </div>
                        <UITypography variant='body-1' className='secondary-neutral-600 text-sb'>
                          {item.label}
                        </UITypography>
                      </div>

                      {/* 컨텐츠 영역 - 2열 그리드 */}
                      <div className='grid grid-cols-2 gap-3'>
                        {/* 변수 설정 박스 */}
                        <div className='bg-white border border-gray-200 rounded-[18px] overflow-hidden'>
                          <div className='px-8 pt-8 pb-6'>
                            <div className='flex items-center gap-3'>
                              <UIToggle
                                checked={item.variableEnabled}
                                onChange={checked => setVariableSettingsItems(prev => prev.map(v => (v.id === item.id ? { ...v, variableEnabled: checked } : v)))}
                                size='small'
                              />
                              <UITypography variant='title-4' className='secondary-neutral-800'>
                                변수 설정
                              </UITypography>
                            </div>
                          </div>
                          <div className='w-full px-8 pb-6'>
                            <UITextArea2
                              className='w-full px-0 bg-white resize-none focus:outline-none'
                              value={item.regexValue || ''}
                              placeholder='정규표현식 입력'
                              onChange={e => {
                                if (!item.variableEnabled) return;
                                setVariableSettingsItems(prev => prev.map(v => (v.id === item.id ? { ...v, regexValue: e.target.value } : v)));
                              }}
                              noBorder={true}
                              rows={3}
                            />
                          </div>
                        </div>

                        {/* 토큰 제한 박스 */}
                        <div className='bg-white border border-gray-200 rounded-[18px] overflow-hidden'>
                          <div className='px-8 pt-8 pb-6'>
                            <div className='flex items-center gap-3'>
                              <UIToggle
                                checked={item.tokenLimitEnabled}
                                onChange={checked => setVariableSettingsItems(prev => prev.map(v => (v.id === item.id ? { ...v, tokenLimitEnabled: checked } : v)))}
                                size='small'
                              />
                              <UITypography variant='title-4' className='secondary-neutral-800'>
                                토큰 제한
                              </UITypography>
                            </div>
                          </div>
                          <div className='w-full px-8 pb-6'>
                            <UITextArea2
                              className='w-full px-0 bg-white resize-none focus:outline-none'
                              rows={3}
                              placeholder='토큰 제한 수 입력'
                              value={item.tokenLimitValue || ''}
                              onChange={e => {
                                if (!item.tokenLimitEnabled) return;
                                setVariableSettingsItems(prev => prev.map(v => (v.id === item.id ? { ...v, tokenLimitValue: e.target.value } : v)));
                              }}
                              noBorder={true}
                            />
                          </div>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </UIFormField>
            </UIArticle>
          )}

          {/* 태그 섹션 */}
          <UIArticle>
            <UIInput.Tags tags={tags} onChange={handleTagsChange} label='태그' required={true} placeholder='태그 입력' maxLength={14} />
          </UIArticle>
        </UIPopupBody>
      </section>
    </UILayerPopup>
  );
};
