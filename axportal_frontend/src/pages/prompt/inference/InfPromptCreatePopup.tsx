import { useCallback, useEffect, useMemo, useRef, useState } from 'react';

import { UIButton2, UIIcon2, UIToggle, UITooltip, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIDropdown, UIFormField, UIInput, UIList, UIPopupBody, UIPopupFooter, UIPopupHeader, UITextArea2, UIUnitGroup } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import type { LayerPopupProps } from '@/hooks/common/layer';
import { InfPromptWorkFlowPopupPage } from '@/pages/prompt';
import { useCreateInfPrompt, useGetInfPromptBuiltin } from '@/services/prompt/inference/inferencePrompts.services';
import { useUser } from '@/stores';
import { useModal } from '@/stores/common/modal/useModal';
import { useNavigate } from 'react-router';

// ============================================
// 타입 정의
// ============================================

/**
 * UI 상태 관리용 변수 설정 아이템
 * VariableSettingItem과는 독립적으로 관리되어 UI 결합도 최소화
 */
type InfPromptVarUI = {
  id: string;
  label: string;
  variableEnabled: boolean;
  regexValue?: string;
  tokenLimitEnabled: boolean;
  tokenLimitValue?: string;
  showAddButton?: boolean;
};

/**
 * 프롬프트 아코디언 아이템 (시스템 프롬프트, 유저 프롬프트)
 */
type PromptAccordionItem = {
  title: string;
  defaultOpen: boolean;
  showNoticeIcon: boolean;
};

interface InfPromptCreatePopupProps extends LayerPopupProps {
  projectId?: string;
  onCreateSuccess?: () => void; // 생성 성공 시 콜백
}

// ============================================
// 컴포넌트
// ============================================

/**
 * 프롬프트 > 추론 프롬프트 목록 페이지 > 추론 프롬프트 생성 팝업
 *
 * 주요 기능:
 * - 프롬프트 생성 (이름, 템플릿, 프롬프트 내용)
 * - 시스템/유저 프롬프트 관리
 * - 워크플로우 추가 및 통합
 * - 변수 자동 감지 및 유효성 검사
 * - 태그 관리
 */
export const InfPromptCreatePopup = ({ currentStep, onClose, onCreateSuccess: _ }: InfPromptCreatePopupProps) => {
  // ============================================
  // State: 프롬프트 기본 정보
  // ============================================
  const [datasetName, setDatasetName] = useState('');
  const [datasetDescription] = useState('');
  const [tags, setTags] = useState<string[]>([]);

  // ============================================
  // State: 템플릿 및 드롭다운
  // ============================================
  const [selectedTemplate, setSelectedTemplate] = useState('None');
  const [selectedTemplateName, setSelectedTemplateName] = useState('None');
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);

  // ============================================
  // State: 프롬프트 콘텐츠
  // ============================================
  /**
   * 프롬프트 아코디언 항목들
   * [시스템 프롬프트, 유저 프롬프트]
   */
  const [promptAccordionItems, setPromptAccordionItems] = useState<PromptAccordionItem[]>([]);

  /**
   * 프롬프트 텍스트 값들
   * 인덱스: 0 = 시스템 프롬프트, 1 = 유저 프롬프트
   */
  const [promptContents, setPromptContents] = useState<string[]>([]);

  // ============================================
  // State: 변수 설정
  // ============================================
  const [variableSettingsItems, setVariableSettingsItems] = useState<InfPromptVarUI[]>([]);

  // ============================================
  // State: 워크플로우
  // ============================================
  /**
   * 선택된 프롬프트 인덱스 (워크플로우 추가 시 어느 프롬프트에 추가할지 결정)
   * useRef 사용: 렌더링 최적화를 위해 상태 변경 없이 값만 저장
   */
  const selectedPromptIndexRef = useRef<number | null>(null);

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

  // ============================================
  // API: 데이터 조회
  // ============================================

  /**
   * 빌트인 프롬프트 템플릿 데이터
   */
  const { data: builtinData } = useGetInfPromptBuiltin();

  // ============================================
  // API: 데이터 변경 및 모달
  // ============================================

  const { openModal, openAlert, openConfirm } = useModal();
  const navigate = useNavigate();
  // 생성 API 호출
  const createInfPromptMutation = useCreateInfPrompt({
    onSuccess: ({ data: { promptUuid } }) => {
      openAlert({
        title: '완료',
        message: '추론 프롬프트 등록이 완료되었습니다.',
        onConfirm: () => {
          // onCreateSuccess?.();
          handleClose();
          navigate(`${promptUuid}`);
        },
      });
    },
  });

  // ============================================
  // Helper 함수: 변수 감지
  // ============================================

  /**
   * 프롬프트 내용 전체를 다시 파싱해 변수 목록을 동기화
   * - {{variable}} 패턴을 모두 추출한 뒤 중복을 제거
   * - 기존 변수 설정이 있으면 유지하고, 새 변수만 기본 값으로 추가
   */
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

  // ============================================
  // Event Handler: 초기화 및 취소
  // ============================================

  /**
   * 모든 상태를 초기화하고 팝업 닫기
   */
  const handleClose = () => {
    setDatasetName('');
    setTags([]);
    setSelectedTemplate('None');
    setIsDropdownOpen(false);

    // 프롬프트 아코디언 초기화
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

    // 프롬프트 내용 초기화
    setPromptContents(['', '']);

    // 변수 설정 초기화
    setVariableSettingsItems([]);

    onClose();
  };

  /**
   * 취소 버튼 클릭 핸들러
   * 사용자 확인 후 팝업 닫기
   */
  const handleCancel = () => {
    openConfirm({
      title: '안내',
      message: '화면을 나가시겠어요?\n입력한 정보가 저장되지 않을 수 있습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        handleClose();
      },
      onCancel: () => { },
    });
  };

  // ============================================
  // Event Handler: 태그
  // ============================================

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

  // ============================================
  // Computed: 저장 버튼 활성화 조건
  // ============================================

  /**
   * 저장 버튼 활성화 조건 체크
   * - 이름이 입력되어 있어야 함
   * - 시스템 프롬프트가 입력되어 있어야 함
   * - 유저 프롬프트가 입력되어 있어야 함
   * - 태그가 추가되어 있어야 함
   */
  const isSaveDisabled = useMemo(() => {
    const isNameEmpty = datasetName.trim() === '';
    const isSystemPromptEmpty = !promptContents[0] || promptContents[0].trim() === '';
    const isUserPromptEmpty = !promptContents[1] || promptContents[1].trim() === '';
    const isTagsEmpty = tags.length === 0;

    return isNameEmpty || isSystemPromptEmpty || isUserPromptEmpty || isTagsEmpty || createInfPromptMutation.isPending;
  }, [datasetName, promptContents, tags, createInfPromptMutation.isPending]);

  // ============================================
  // Event Handler: 저장
  // ============================================

  /**
   * 프롬프트 저장 핸들러
   * - 필수 항목 유효성 검사
   * - 변수 설정 유효성 검사
   * - API 요청
   */
  const { user } = useUser();

  // 저장
  const handleSave = () => {
    // 변수 설정
    for (const item of variableSettingsItems) {
      // 변수 설정이 켜져있는데 정규표현식이 비어있으면
      if (item.variableEnabled && (!item.regexValue || item.regexValue.trim() === '')) {
        item.variableEnabled = false;
      }

      // 토큰 제한이 켜져있는데 토큰값이 비어있거나 0이면
      if (item.tokenLimitEnabled && (!item.tokenLimitValue || item.tokenLimitValue.trim() === '' || parseInt(item.tokenLimitValue) <= 0)) {
        item.tokenLimitEnabled = false;
      }
    }

    // string[]를 infPromptTags[]로 변환
    const infPromptTags = tags.map(tag => ({ tag }));

    // promptContents를 messages로 변환
    // mtype 1: 시스템 프롬프트, mtype 2: 유저 프롬프트
    const messages = promptContents.map((content, index) => {
      const promptTitle = promptAccordionItems[index]?.title;
      const mtype = promptTitle === '시스템 프롬프트' ? 1 : 2;

      return {
        mtype,
        message: content,
        order: index + 1,
      };
    });

    // variableSettingsItems를 API 형식으로 변환
    const finalVariables = variableSettingsItems.map(item => ({
      variable: item.label,
      validation: item.regexValue || '',
      validationFlag: item.variableEnabled || false,
      tokenLimitFlag: item.tokenLimitEnabled || false,
      tokenLimit: parseInt(item.tokenLimitValue || '0') || 0,
    }));

    const requestData = {
      desc: datasetDescription,
      messages: messages,
      name: datasetName,
      release: false,
      tags: infPromptTags,
      projectId: user.adxpProject.prjUuid,
      variables: finalVariables,
      templateUuid: selectedTemplate !== 'None' ? selectedTemplate : null,
    };

    createInfPromptMutation.mutate(requestData);
  };

  // ============================================
  // Event Handler: 프롬프트 콘텐츠
  // ============================================

  /**
   * 프롬프트 내용 변경 핸들러
   * 변수 자동 감지도 함께 수행
   *
   * @param index - 프롬프트 인덱스 (0: 시스템, 1: 유저)
   * @param newContent - 새로운 프롬프트 내용
   */
  const handlePromptContentChange = (index: number, newContent: string) => {
    setPromptContents(prev => {
      const newContents = [...prev];
      newContents[index] = newContent;
      return newContents;
    });
  };

  // ============================================
  // Event Handler: 워크플로우
  // ============================================

  /**
   * 워크플로우 추가 버튼 클릭 핸들러
   * 모달을 열어 워크플로우 선택 UI 제공
   *
   * @param promptIndex - 워크플로우를 추가할 프롬프트 인덱스
   */
  const handleAddWorkFlow = (promptIndex: number) => {
    selectedPromptIndexRef.current = promptIndex;
    let selectedWorkFlowsCapture: Array<{
      workflowId: string;
      xmlText?: string;
      workflowName?: string;
      versionNo?: number;
    }> = [];

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

  /**
   * 워크플로우 선택 완료 핸들러 (다중 선택 지원)
   * 선택된 워크플로우의 XML을 프롬프트 내용에 통합
   *
   * @param selectedWorkFlows - 선택된 워크플로우 배열
   */
  const handleWorkFlowSelected = (
    selectedWorkFlows: Array<{
      workflowId: string | { xmlText?: string };
      xmlText?: string;
      workflowName?: string;
      versionNo?: number;
    }>
  ) => {
    // console.log('=== 워크플로우 선택 완료 ===');
    // console.log('받은 데이터:', selectedWorkFlows);
    // console.log('선택된 프롬프트 인덱스:', selectedPromptIndexRef.current);

    if (selectedPromptIndexRef.current !== null && selectedWorkFlows.length > 0) {
      // 각 워크플로우의 xmlText를 추출
      const xmlTexts: string[] = [];

      selectedWorkFlows.forEach((workflow /* , index */) => {
        // console.log(`워크플로우 ${index + 1}:`, workflow);
        // console.log(`- workflowId: ${workflow.workflowId}`);
        // console.log(`- xmlText: ${workflow.xmlText ? '있음' : '없음'}`);
        // console.log(`- xmlText 길이: ${workflow.xmlText?.length || 0}`);

        // workflowId가 객체인 경우 처리 (잘못된 데이터 구조 대응)
        let actualXmlText = workflow.xmlText;
        if (!actualXmlText && typeof workflow.workflowId === 'object' && workflow.workflowId?.xmlText) {
          // console.log('workflowId가 객체입니다. 내부 xmlText 사용:', workflow.workflowId.xmlText);
          actualXmlText = workflow.workflowId.xmlText;
        }

        if (actualXmlText && actualXmlText.trim() !== '') {
          xmlTexts.push(actualXmlText);
        }
      });

      // console.log('추출된 XML 텍스트들:', xmlTexts);

      // XML 텍스트들을 하나의 문자열로 합치기
      const combinedXmlText = xmlTexts.join('\n\n');
      // console.log('합쳐진 XML 텍스트:', combinedXmlText);
      // console.log('합쳐진 XML 텍스트 길이:', combinedXmlText.length);

      // 현재 프롬프트 내용 가져오기
      const currentContent = promptContents[selectedPromptIndexRef.current] || '';
      // console.log('현재 프롬프트 내용:', currentContent);

      // 워크플로우 XML 텍스트를 현재 내용에 추가
      const separator = currentContent ? '\n\n' : '';
      const updatedContent = currentContent + separator + combinedXmlText;
      // console.log('최종 업데이트된 프롬프트 내용:', updatedContent);

      // 프롬프트 내용 업데이트
      setPromptContents(prev => {
        const newContents = [...prev];
        newContents[selectedPromptIndexRef.current as number] = updatedContent;
        // console.log('새로운 promptContents:', newContents);
        return newContents;
      });

      // 변수 감지 실행
      // console.log('워크플로우 추가 완료!');
    } else {
      // console.log('워크플로우 추가 실패 - selectedPromptIndex:', selectedPromptIndexRef.current, 'selectedWorkFlows.length:', selectedWorkFlows.length);
    }
  };

  // ============================================
  // Side Effect: 템플릿 선택 시 처리
  // ============================================
  /**
   * 템플릿이 선택되었을 때 프롬프트 아코디언과 프롬프트 내용 업데이트
   * 빌트인 템플릿 데이터를 기반으로 초기값 설정
   */
  useEffect(() => {
    if (selectedTemplate && selectedTemplate !== 'None' && builtinData?.data) {
      const selectedBuiltin = builtinData.data.find(item => item.uuid === selectedTemplate);
      if (selectedBuiltin) {
        const newAccordionItems = (selectedBuiltin.messages ?? []).map((_message, index) => ({
          title: index === 0 ? '시스템 프롬프트' : '유저 프롬프트',
          defaultOpen: index === 0, // 첫 번째 항목만 열기
          showNoticeIcon: false,
        }));
        setPromptAccordionItems(newAccordionItems);

        // 프롬프트 내용도 별도로 저장
        setPromptContents((selectedBuiltin.messages ?? []).map(message => message.message));

        // variableSettingsItems도 업데이트
        const newVariableSettingsItems = (selectedBuiltin.variables ?? []).map((variable, index) => ({
          id: `${variable.variable}-${index}`,
          label: variable.variable,
          variableEnabled: variable.validationFlag,
          regexValue: variable.validation,
          tokenLimitEnabled: variable.tokenLimitFlag,
          tokenLimitValue: variable.tokenLimit.toString(),
          showAddButton: false,
        }));
        setVariableSettingsItems(newVariableSettingsItems);
      }
    } else if (selectedTemplate === 'None') {
      // None 선택 시 기본값으로 초기화 (시스템 프롬프트, 유저 프롬프트)
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

      // 프롬프트 내용도 초기화 (시스템 프롬프트, 유저 프롬프트)
      setPromptContents(['', '']);

      // 변수 설정도 초기화
      setVariableSettingsItems([]);
    }
  }, [selectedTemplate, builtinData]);

  // ============================================
  // Side Effect: 프롬프트 변경 시 변수 감지
  // ============================================
  /**
   * 프롬프트 아코디언 항목이 변경될 때마다 변수 자동 감지 실행
   */
  useEffect(() => {
    detectAndSyncVariables(promptContents);
  }, [promptContents, detectAndSyncVariables]);

  // ============================================
  // Render
  // ============================================
  return (
    <>
      <UILayerPopup
        isOpen={currentStep === 1}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            <UIPopupHeader title='추론 프롬프트 등록' description='' position='left' />

            {/* 버튼 그룹 */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} onClick={handleSave} disabled={isSaveDisabled}>
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
            {/* ──────────────────────── 이름 입력 필드 ──────────────────────── */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                  이름
                </UITypography>
                <UIInput.Text value={datasetName} placeholder='이름 입력' onChange={e => setDatasetName(e.target.value)} disabled={false} />
              </UIFormField>
            </UIArticle>

            {/* ──────────────────────── 템플릿 드롭다운 ──────────────────────── */}
            <UIArticle>
              <UIUnitGroup gap={8} direction='column'>
                <UIFormField gap={4} direction='row'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
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
                  }}
                  onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                  isOpen={isDropdownOpen}
                  placeholder='템플릿 선택'
                  options={[{ value: 'None', label: 'None' }, ...(builtinData?.data.map(item => ({ value: item.uuid, label: item.name })) ?? [])]}
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

            {/* ──────────────────────── 프롬프트 섹션 ──────────────────────── */}
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
                  {/* 시스템 프롬프트 */}
                  <div className='border border-gray-200 bg-white rounded-[16px]'>
                    <div className='flex items-center justify-between px-[32px] py-[20px]'>
                      <div className='flex items-center gap-3'>
                        <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                          시스템 프롬프트
                        </UITypography>
                      </div>
                      <UIButton2 className='btn-text-14-semibold-point' style={{ display: 'flex', alignItems: 'center', gap: '4px' }} onClick={() => handleAddWorkFlow(0)}>
                        <UIIcon2 className='ic-system-24-add' />
                        워크플로우 추가
                      </UIButton2>
                    </div>
                    <div className='pl-[32px] pr-[32px]'>
                      <UITextArea2
                        className='w-full px-0 bg-white resize-None focus:outline-None'
                        value={promptContents[0] || ''}
                        placeholder=' '
                        onChange={e => handlePromptContentChange(0, e.target.value)}
                        noBorder={true}
                      />
                    </div>
                  </div>

                  {/* 유저 프롬프트 */}
                  <div className='border border-gray-200 bg-white rounded-[16px]'>
                    <div className='flex items-center justify-between px-[32px] py-[20px]'>
                      <div className='flex items-center gap-3'>
                        <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                          유저 프롬프트
                        </UITypography>
                      </div>
                      <UIButton2 className='btn-text-14-semibold-point' style={{ display: 'flex', alignItems: 'center', gap: '4px' }} onClick={() => handleAddWorkFlow(1)}>
                        <UIIcon2 className='ic-system-24-add' />
                        워크플로우 추가
                      </UIButton2>
                    </div>
                    <div className='pl-[32px] pr-[32px]'>
                      <UITextArea2
                        className='w-full px-0 bg-white resize-None focus:outline-None'
                        value={promptContents[1] || ''}
                        placeholder=' '
                        onChange={e => handlePromptContentChange(1, e.target.value)}
                        noBorder={true}
                      />
                    </div>
                  </div>
                </UIUnitGroup>
              </UIFormField>
            </UIArticle>

            {/* ──────────────────────── 변수 속성 설정 섹션 ──────────────────────── */}
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
                                className='w-full px-0 bg-white resize-None focus:outline-None'
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
                                className='w-full px-0 bg-white resize-None focus:outline-None'
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

            {/* ──────────────────────── 태그 섹션 ──────────────────────── */}
            <UIArticle>
              <UIInput.Tags tags={tags} onChange={handleTagsChange} label='태그' required={true} placeholder='태그 입력' maxLength={14} />
            </UIArticle>
          </UIPopupBody>
        </section>
      </UILayerPopup>
    </>
  );
};
