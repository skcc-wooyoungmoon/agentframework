import { Button } from '@/components/common/auth';
import { UITypography } from '@/components/UI/atoms';
import { UIImage } from '@/components/UI/atoms/UIImage';
import { UIArticle, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup, type UIStepperItem } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { useGetBuilderTemplates } from '@/services/agent/builder2/agentBuilder.services';
import { useAgentBuilder } from '@/stores/agent/useAgentBuilder';
import { useModal } from '@/stores/common/modal';
import { useMemo } from 'react';

type SelectionType = 'quick_start' | 'new_template' | string;

interface AgentStep1TmplSelectPopupPageProps {
  isOpen: boolean;
  stepperItems: UIStepperItem[];
  onClose: () => void;
  onNextStep: () => void;
}

export function AgentStep1TmplSelectPopupPage({ isOpen, stepperItems = [], onClose, onNextStep }: AgentStep1TmplSelectPopupPageProps) {
  const { data } = useGetBuilderTemplates();
  const { selectedType, setSelectedType, updateSelectedTemplate, resetAgentBuilder } = useAgentBuilder();
  const { openConfirm } = useModal();

  const handleCancel = () => {
    openConfirm({
      title: '안내',
      message: '화면을 나가시겠어요?\n입력한 정보가 저장되지 않을 수 있습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        resetAgentBuilder();
        onClose();
      },
    });
  };

  const handleNext = async () => {
    if (!selectedType) {
      return;
    }

    const selectedTemplate = data?.templates?.find((t: any) => t.template_id === selectedType);
    // Provider에 데이터 저장
    updateSelectedTemplate(selectedType, selectedTemplate);

    // 부모 컴포넌트의 onNextStep 호출
    onNextStep();
  };

  const handleToolTypeChange = (value: SelectionType) => {
    setSelectedType(value);
  };

  // 새로운 템플릿 옵션
  const newTemplateOption = {
    value: 'new_template' as SelectionType,
    label: '빈 템플릿',
    description: '비어있는 템플릿에서 에이전트를 생성할 수 있습니다.',
    image: '/assets/images/agent/ico-radio-ag-visual02.svg',
    alt: '빈 템플릿',
  };

  // 템플릿 아이콘 매핑 함수
  function getTemplateIcon(iconName: string, templateName: string): string {
    const nameMap: { [key: string]: string } = {
      RAG: '/assets/images/agent/ico-radio-ag-visual03.svg',
      챗봇: '/assets/images/agent/ico-radio-ag-visual04.svg',
      Chatbot: '/assets/images/agent/ico-radio-ag-visual04.svg',
      번역기: '/assets/images/agent/ico-radio-ag-visual05.svg',
      Translator: '/assets/images/agent/ico-radio-ag-visual05.svg',
      'Simple RAG': '/assets/images/agent/ico-radio-ag-visual08.svg',
      'Plan And Execute': '/assets/images/agent/ico-radio-ag-visual09.svg',
    };

    const iconMap: { [key: string]: string } = {
      messages: '/assets/images/agent/ico-radio-ag-visual04.svg',
      'book-square': '/assets/images/agent/ico-radio-ag-visual03.svg',
      router: '/assets/images/agent/ico-radio-ag-visual05.svg',
      data: '/assets/images/agent/ico-radio-ag-visual06.svg',
    };

    if (nameMap[templateName]) {
      return nameMap[templateName];
    }

    if (iconMap[iconName]) {
      return iconMap[iconName];
    }

    return '/assets/images/agent/ico-radio-ag-visual02.svg';
  }

  // API에서 가져온 예제 템플릿들
  const exampleTemplateOptions = useMemo(() => {
    return data?.templates?.map((template: any) => ({
      value: template.template_id as SelectionType,
      label: template.name,
      description: template.description,
      image: getTemplateIcon(template.icon || '', template.name || ''),
      alt: template.name,
      template: template,
    }));
  }, [data]);

  // 템플릿 카드 렌더링 함수
  const renderTemplateCard = (option: any, isNewTemplate = false) => (
    <div key={option.value} className='flex flex-col space-y-5'>
      <div
        className={`relative w-[405px] h-[240px] rounded-[20px] p-6 cursor-pointer transition-all duration-200 flex items-center justify-center ${
          selectedType === option.value ? 'bg-[#f3f6fb] border-2 border-[#005df9]' : 'bg-col-gray-100 border-2 border-transparent hover:bg-gray-100'
        }`}
        onClick={() => {
          handleToolTypeChange(option.value);
        }}
      >
        {!isNewTemplate && (
          <div className='absolute top-4 left-4 px-1.5 py-0.5 text-xs leading-5 bg-[#C7CEDC] rounded-full'>
            <UITypography variant='caption-2' className='secondary-neutral-f text-sb'>
              기존 예제
            </UITypography>
          </div>
        )}
        <UIImage src={option.image} alt={option.alt} className='max-w-full max-h-full' />
      </div>
      <UIUnitGroup gap={8} direction='column' align='start'>
        <UITypography variant='body-1' className='secondary-neutral-800 text-sb'>
          {option.label}
        </UITypography>
        <UITypography variant='body-2' className='secondary-neutral-600'>
          {option.description}
        </UITypography>
      </UIUnitGroup>
    </div>
  );

  return (
    <>
      <UILayerPopup
        isOpen={isOpen}
        onClose={onClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='에이전트 등록하기' description='' position='left' />

            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={1} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>

            {/* 레이어 팝업 푸터 */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <Button className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                    취소
                  </Button>
                  <Button auth={AUTH_KEY.AGENT.AGENT_CREATE} className='btn-tertiary-blue' style={{ width: '80px' }} disabled={true}>
                    저장
                  </Button>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        <section className='section-popup-content'>
          <UIPopupHeader
            title='템플릿 선택'
            description='에이전트를 만들기 위한 빌더 템플릿을 선택해 주세요. 일부 템플릿에 포함된 기본 프롬프트는 배포가 제한될 수 있습니다.'
            position='right'
          />
          <UIPopupBody>
            <>
              <UIArticle>
                <div className='grid grid-cols-3 gap-6'>
                  {renderTemplateCard(newTemplateOption, true)}
                  {exampleTemplateOptions?.slice(0, 2)?.map((option: any) => renderTemplateCard(option))}
                </div>
              </UIArticle>

              {exampleTemplateOptions?.length > 2 && (
                <UIArticle>
                  <div className='grid grid-cols-3 gap-6'>{exampleTemplateOptions?.slice(2)?.map((option: any) => renderTemplateCard(option))}</div>
                </UIArticle>
              )}
            </>
          </UIPopupBody>

          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <Button className='btn-secondary-blue' style={{ width: '80px' }} onClick={handleNext} disabled={!selectedType}>
                  다음
                </Button>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
}
