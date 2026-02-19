import { Button } from '@/components/common/auth';
import { UITypography } from '@/components/UI/atoms';
import { UIArticle, UIFormField, UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UITextArea2, UIUnitGroup, type UIStepperItem } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { api } from '@/configs/axios.config';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { useCreateAgentFromTemplate, useGetTemplateDetail } from '@/services/agent/builder2/agentBuilder.services';
import { useAgentBuilder } from '@/stores/agent/useAgentBuilder';
import { useModal } from '@/stores/common/modal';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

interface AgentInfo {
  name: string;
  description: string;
  fileAttachment?: File;
  selectedLLM: string;
  selectedPrompt: string;
}

interface AgentStep2BaseInfoInputPopupPageProps {
  isOpen: boolean;
  stepperItems: UIStepperItem[];
  onClose: () => void;
  onPreviousStep: () => void;
}

export function AgentStep2BaseInfoInputPopupPage({ isOpen, stepperItems = [], onClose, onPreviousStep }: AgentStep2BaseInfoInputPopupPageProps) {
  const navigate = useNavigate();
  const { selectedType, selectedTemplate, resetAgentBuilder } = useAgentBuilder();
  const { openConfirm, openAlert } = useModal();

  // 템플릿 상세 정보 가져오기
  const { data: templateDetail } = useGetTemplateDetail(selectedType || '', {
    enabled: !!selectedType && selectedType !== 'new_template',
  });

  // 에이전트 생성 API 훅
  const createAgentMutation = useCreateAgentFromTemplate({
    onSuccess: async response => {
      // console.log('📡 백엔드 응답 데이터:', response);

      let templateNodes: any[] = [];
      let templateEdges: any[] = [];

      // 템플릿 상세 정보에서 가져오기
      if (templateDetail?.data) {
        if (Array.isArray(templateDetail.data?.nodes)) {
          templateNodes = templateDetail.data.nodes;
        }
        if (Array.isArray(templateDetail.data?.edges)) {
          templateEdges = templateDetail.data.edges;
        }
      }

      // 에이전트 데이터 생성 (캔버스용)
      const graphUuid = response.data?.graphUuid || response.data?.id || `temp-${Date.now()}`;
      const agentData = {
        id: graphUuid,
        name: agentInfo.name,
        description: agentInfo.description,
        created_at: new Date().toISOString(),
        updated_at: new Date().toISOString(),
        project_id: 'default-project',
        edges: templateEdges,
        nodes: templateNodes,
        // 추가 정보
        fileAttachment: agentInfo.fileAttachment,
        selectedLLM: agentInfo.selectedLLM,
        selectedPrompt: agentInfo.selectedPrompt,
        templateType: selectedType,
        template: selectedTemplate,
        // SKT AI Platform에서 생성된 정보
        sktaiResponse: response.data,
        // SKT AI Platform에서 실제 생성된 그래프 ID
        graphUuid: graphUuid,
      };

      onClose();
      navigate('/agent/builder/graph', {
        state: {
          data: agentData,
        },
      });
    },
  });

  const [agentInfo, setAgentInfo] = useState<AgentInfo>({
    name: '',
    description: '',
    selectedLLM: '',
    selectedPrompt: '',
  });

  // 이름이 유효한지 확인 (공백만 있는 경우 무효, 50자 초과 시 무효)
  const isNameValid = agentInfo.name.trim().length > 0 && agentInfo.name.length <= 50;

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

  const handlePreviousStep = () => {
    onPreviousStep();
  };

  const handleInputChange = (field: keyof AgentInfo, value: string) => {
    setAgentInfo(prev => ({ ...prev, [field]: value }));
  };

  const checkDuplicateName = async (name: string) => {
    const trimmedName = name.trim();
    if (trimmedName === '') {
      return { isDuplicate: false, checkFailed: false };
    }

    try {
      const response = await api.get('/agent/builder', {
        params: {
          page: 1,
          size: 50,
          search: trimmedName,
        },
      });
      const list = response?.data?.data?.content ?? [];
      const isDuplicate = list.some((item: any) => String(item?.name ?? '').trim() === trimmedName);
      return { isDuplicate, checkFailed: false };
    } catch (error) {
      console.warn('에이전트 이름 중복 체크 실패:', error);
      return { isDuplicate: false, checkFailed: true };
    }
  };

  const handleConfirm = async () => {
    const duplicateCheck = await checkDuplicateName(agentInfo.name);

    if (duplicateCheck.isDuplicate) {
      openAlert({
        title: '안내',
        message: '이미 존재하는 빌더명입니다. 다른 이름을 입력해주세요.',
      });
      return;
    }

    let templateId: string | null = null;

    if (selectedTemplate?.template_id) {
      // selectedTemplate에 template_id가 있으면 사용
      templateId = selectedTemplate.template_id;
    } else if (selectedType && selectedType !== 'new_template') {
      // selectedType이 있고 'new_template'이 아니면 사용
      templateId = selectedType;
    }

    // new_template이거나 template_id가 없으면 null로 전달 (백엔드에서 빈 그래프 생성)
    if (!templateId || selectedType === 'new_template') {
      templateId = null;
    }

    // SKT AI Platform API 요청 데이터 생성
    const requestData: any = {
      name: agentInfo.name,
      description: agentInfo.description,
    };

    // template_id가 있을 때만 추가 (new_template일 때는 전달하지 않음)
    if (templateId) {
      requestData.template_id = templateId;
    }

    // console.log('📤 최종 요청 데이터:', requestData);

    // 에이전트 생성 API 호출
    createAgentMutation.mutate(requestData);
  };

  return (
    <UILayerPopup
      isOpen={isOpen}
      onClose={handleClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        /* 좌측 Step 영역 콘텐츠 */
        <UIPopupAside>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='에이전트 등록하기' description='' position='left' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle>
              <UIStepper currentStep={2} items={stepperItems} direction='vertical' />
            </UIArticle>
          </UIPopupBody>

          {/* 레이어 팝업 푸터 */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <Button className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                  취소
                </Button>
                <Button auth={AUTH_KEY.AGENT.AGENT_CREATE} className='btn-tertiary-blue' style={{ width: '80px' }} disabled={!isNameValid} onClick={handleConfirm}>
                  저장
                </Button>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      {/* 우측 Contents 영역 콘텐츠 */}
      {/* 콘텐츠 영역 */}
      <section className='section-popup-content'>
        {/* 레이어 팝업 헤더 */}
        <UIPopupHeader title='기본 정보 입력' description='' position='right' />

        {/* 레이어 팝업 바디 */}
        <UIPopupBody>
          {/* 이름 입력 필드 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                이름
              </UITypography>
              <UIInput.Text value={agentInfo.name} onChange={e => handleInputChange('name', e.target.value)} placeholder='에이전트 이름 입력' maxLength={50} />
            </UIFormField>
          </UIArticle>

          {/* 설명 입력 필드 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                설명
              </UITypography>

              <UITextArea2 value={agentInfo.description} onChange={e => handleInputChange('description', e.target.value)} placeholder='에이전트 설명 입력' maxLength={100} />
            </UIFormField>
          </UIArticle>
        </UIPopupBody>
        {/* 레이어 팝업 footer */}
        <UIPopupFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='start'>
              <Button className='btn-secondary-gray' onClick={handlePreviousStep}>
                이전
              </Button>
            </UIUnitGroup>
          </UIArticle>
        </UIPopupFooter>
      </section>
    </UILayerPopup>
  );
}
