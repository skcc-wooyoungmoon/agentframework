import { Button } from '@/components/common/auth';
import { UITypography } from '@/components/UI/atoms';
import { UIImage } from '@/components/UI/atoms/UIImage';
import { UIArticle, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup, type UIStepperItem } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { useAgentTemplates } from '@/hooks/agent/useAgentTemplates';
import { useAgentBuilder } from '@/stores/agent/useAgentBuilder';
import { useModal } from '@/stores/common/modal';

type SelectionType = 'quick_start' | 'new_template' | string;

interface AgentStep1TmplSelectPopupPageProps {
  isOpen: boolean;
  stepperItems: UIStepperItem[];
  onClose: () => void;
  onNextStep: () => void;
}

export function AgentStep1TmplSelectPopupPage({ isOpen, stepperItems = [], onClose, onNextStep }: AgentStep1TmplSelectPopupPageProps) {
  const { templates } = useAgentTemplates();
  const { selectedType, setSelectedType, updateSelectedTemplate, resetAgentBuilder } = useAgentBuilder();
  const { openConfirm } = useModal();

  const handleClose = () => {
    onClose();
  };

  const handleCancel = () => {
    openConfirm({
      title: '안내',
      message: '화면을 나가시겠어요?\n입력한 정보가 저장되지 않을 수 있습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        resetAgentBuilder();
        handleClose();
      },
    });
  };

  const handleNext = async () => {
    if (!selectedType) {
      return;
    }

    let selectedTemplate = null;

    if (selectedType === 'new_template') {
      selectedTemplate = null;
    } else {
      selectedTemplate = templates.find(t => t.template_id === selectedType);

      if (!selectedTemplate) {
        return;
      }
    }

    updateSelectedTemplate(selectedType, selectedTemplate);

    onNextStep();
  };

  const handleToolTypeChange = (value: SelectionType) => {
    setSelectedType(value);
  };

  const newTemplateOption = {
    value: 'new_template' as SelectionType,
    label: '빈 템플릿',
    description: '비어있는 템플릿에서 에이전트를 생성할 수 있습니다.',
    image: '/assets/images/agent/ico-radio-ag-visual02.svg',
    alt: '빈 템플릿',
  };

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

  const exampleTemplateOptions = templates.map(template => ({
    value: template.template_id as SelectionType,
    label: template.template_name,
    description: template.template_description,
    image: getTemplateIcon(template.icon || '', template.template_name || ''),
    alt: template.template_name,
    template: template,
  }));

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
    <UILayerPopup
      isOpen={isOpen}
      onClose={handleClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        <UIPopupAside>
          <UIPopupHeader title='에이전트 등록하기' description='' position='left' />
          <UIPopupBody>
            <UIArticle>
              <UIStepper currentStep={1} items={stepperItems} direction='vertical' />
            </UIArticle>
          </UIPopupBody>
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
          <UIArticle>
            <div className='grid grid-cols-3 gap-6'>
              {renderTemplateCard(newTemplateOption, true)}
              {exampleTemplateOptions.slice(0, 2).map(option => renderTemplateCard(option))}
            </div>
          </UIArticle>

          {exampleTemplateOptions.length > 2 && (
            <UIArticle>
              <div className='grid grid-cols-3 gap-6 mb-8'>{exampleTemplateOptions.slice(2).map(option => renderTemplateCard(option))}</div>
            </UIArticle>
          )}
          {exampleTemplateOptions.length === 0 && (
            <UIArticle>
              <div className='text-center py-8'>
                <p className='text-gray-500'>사용 가능한 예제 템플릿이 없습니다.</p>
              </div>
            </UIArticle>
          )}
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
  );
}
