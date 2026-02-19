import { Button } from '@/components/common/auth';
import { UITypography } from '@/components/UI/atoms';
import { UIArticle, UIFormField, UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UITextArea2, UIUnitGroup, type UIStepperItem } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { api } from '@/configs/axios.config';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { TemplateService, useCreateAgentFromTemplate, useUpdateAgentBuilder } from '@/services/agent/builder/agentBuilder.services';
import { useAgentBuilder } from '@/stores/agent/useAgentBuilder';
import { useModal } from '@/stores/common/modal';
import { useUser } from '@/stores/auth/useUser';
import { useQueryClient } from '@tanstack/react-query';
import { useEffect, useState } from 'react';
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
  const queryClient = useQueryClient();
  const { selectedType, selectedTemplate, resetAgentBuilder } = useAgentBuilder();
  const { openConfirm, openAlert } = useModal();
  const { user } = useUser();

  const [templateDetail, setTemplateDetail] = useState<any>(null);

  useEffect(() => {
    const fetchTemplateDetail = async () => {
      if (selectedType && selectedType !== 'new_template') {
        const detail = await TemplateService.getTemplateDetail(selectedType);
        if (detail) {
          setTemplateDetail(detail);
        }
      }
    };

    fetchTemplateDetail();
  }, [selectedType]);

  const { mutateAsync: updateAgentBuilder } = useUpdateAgentBuilder();

  const createAgentMutation = useCreateAgentFromTemplate({
    onSuccess: async response => {
      if (!response) {
        await openAlert({
          title: '에이전트 생성 실패',
          message: '서버에서 응답을 받지 못했습니다. 네트워크 연결을 확인하고 다시 시도해주세요.',
        });
        return;
      }

      if (!response.data?.graphUuid) {
        if (!response.data) {
          response.data = {};
        }
        response.data.graphUuid = response.data.id || `temp-${Date.now()}`;
      }

      queryClient.invalidateQueries({ queryKey: ['agent-builder-list'] });

      let templateNodes: any[] = [];
      let templateEdges: any[] = [];

      try {
        if (Array.isArray(response.data?.nodes)) {
          templateNodes = response.data.nodes;
        }
        if (Array.isArray(response.data?.edges)) {
          templateEdges = response.data.edges;
        }

        if ((!templateNodes || templateNodes.length === 0) && response.data?.graph) {
          const graphData = response.data.graph;
          if (Array.isArray(graphData.nodes)) {
            templateNodes = graphData.nodes;
          }
          if (Array.isArray(graphData.edges)) {
            templateEdges = graphData.edges;
          }
        }

        if ((!templateNodes || templateNodes.length === 0) && templateDetail) {
          if (Array.isArray(templateDetail.nodes)) {
            templateNodes = templateDetail.nodes;
          } else if (Array.isArray(templateDetail.data?.nodes)) {
            templateNodes = templateDetail.data.nodes;
          }
          if (Array.isArray(templateDetail.edges)) {
            templateEdges = templateDetail.edges;
          } else if (Array.isArray(templateDetail.data?.edges)) {
            templateEdges = templateDetail.data.edges;
          }
        }
      } catch (extractError) {
        templateNodes = [];
        templateEdges = [];
      }

      if (templateNodes.length > 0 && templateEdges.length > 0) {
        const nodeIds = new Set(templateNodes.map((node: any) => node?.id).filter(Boolean));
        const invalidEdges = templateEdges.filter((edge: any) => {
          return !nodeIds.has(edge?.source) || !nodeIds.has(edge?.target);
        });

        if (invalidEdges.length > 0) {
        } else {
        }
      }
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
        fileAttachment: agentInfo.fileAttachment,
        selectedLLM: agentInfo.selectedLLM,
        selectedPrompt: agentInfo.selectedPrompt,
        templateType: selectedType,
        template: selectedTemplate,
        sktaiResponse: response.data,
        graphUuid: graphUuid,
      };

      if (templateNodes.length > 0 || templateEdges.length > 0) {
        const saveRequest = {
          agentId: graphUuid,
          id: graphUuid,
          name: agentInfo.name,
          description: agentInfo.description,
          graph: {
            nodes: templateNodes,
            edges: templateEdges,
          },
        };

        updateAgentBuilder(saveRequest as any);
      }

      onClose();
      navigate('/test/secret/graph2', {
        state: {
          data: agentData,
          templateDetail: templateDetail,
        },
      });
    },
    onError: error => {
      const errorAny = error as any;
      const errorResponse = errorAny?.response;

      if (errorResponse?.data?.data?.graphUuid || errorResponse?.data?.graphUuid) {
        const successData = errorResponse.data.data || errorResponse.data;
        queryClient.invalidateQueries({ queryKey: ['agent-builder-list'] });
        onClose();
        navigate('/test/secret/graph2', {
          state: {
            data: {
              id: successData.graphUuid,
              name: agentInfo.name,
              description: agentInfo.description,
              created_at: new Date().toISOString(),
              updated_at: new Date().toISOString(),
              project_id: 'default-project',
              edges: successData.edges || templateDetail?.edges || templateDetail?.data?.edges || [],
              nodes: successData.nodes || templateDetail?.nodes || templateDetail?.data?.nodes || [],
              fileAttachment: agentInfo.fileAttachment,
              selectedLLM: agentInfo.selectedLLM,
              selectedPrompt: agentInfo.selectedPrompt,
              templateType: selectedType,
              template: selectedTemplate,
              sktaiResponse: successData,
              graphUuid: successData.graphUuid,
            },
            templateDetail: templateDetail,
          },
        });

        return;
      }
    },
    onSettled: () => {
      setIsSubmitting(false);
    },
  });

  const [agentInfo, setAgentInfo] = useState<AgentInfo>({
    name: '',
    description: '',
    selectedLLM: '',
    selectedPrompt: '',
  });

  const [errors, setErrors] = useState<Partial<AgentInfo>>({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    return () => {
      setIsSubmitting(false);
    };
  }, []);

  const isNameValid = agentInfo.name.trim().length > 0 && agentInfo.name.length <= 50;
  const isSaveDisabled = !isNameValid || isSubmitting;

  const handleClose = () => {
    setIsSubmitting(false);
    onClose();
  };

  const handleCancel = () => {
    setIsSubmitting(false);
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

  const handleSave = () => {
    handleConfirm();
  };

  const handleInputChange = (field: keyof AgentInfo, value: string) => {
    setAgentInfo(prev => ({ ...prev, [field]: value }));

    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: undefined }));
    }
  };

  const validateForm = (): boolean => {
    const newErrors: Partial<AgentInfo> = {};

    if (!agentInfo.name.trim()) {
      newErrors.name = '이름을 입력해주세요.';
    }

    if (agentInfo.name.length > 50) {
      newErrors.name = '최대 50자까지 입력 가능합니다.';
    }

    if (agentInfo.description.length > 100) {
      newErrors.description = '최대 100자까지 입력 가능합니다.';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
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
      return { isDuplicate: false, checkFailed: true };
    }
  };

  const handleConfirm = async () => {
    if (!validateForm()) {
      return;
    }

    if (isSubmitting) {
      return;
    }

    const roleSeq = user?.activeProject?.prjRoleSeq;
    const roleNm = user?.activeProject?.prjRoleNm;
    const roleSeqStr = roleSeq ? String(roleSeq) : '';
    const roleSeqNum = roleSeq ? Number(roleSeq) : null;
    const isTester = roleSeqStr === '-297' || roleSeqNum === -297 || roleNm === '테스터';

    if (isTester) {
      await openAlert({
        title: '안내',
        message: '권한이 없습니다. 테스터는 조회 권한만 있습니다.',
        confirmText: '확인',
      });
      return;
    }

    const duplicateCheck = await checkDuplicateName(agentInfo.name);

    if (duplicateCheck.isDuplicate) {
      const duplicateMessage = '이미 존재하는 빌더명입니다. 다른 이름을 입력해주세요.';
      setErrors(prev => ({
        ...prev,
        name: duplicateMessage,
      }));
      openAlert({
        title: '안내',
        message: duplicateMessage,
      });
      return;
    }

    setIsSubmitting(true);

    let templateId: string | null = null;

    if (selectedTemplate?.template_id) {
      templateId = selectedTemplate.template_id;
    } else if (selectedType && selectedType !== 'new_template') {
      templateId = selectedType;
    }

    if (!templateId || selectedType === 'new_template') {
      templateId = null;
    }

    let templateNodes: any[] = [];
    let templateEdges: any[] = [];

    if (templateDetail) {
      const getTemplateData = (key: string) => {
        const result = templateDetail.data?.data?.[key] ?? templateDetail.data?.[key] ?? templateDetail[key];
        return Array.isArray(result) ? result : [];
      };

      templateNodes = getTemplateData('nodes');
      templateEdges = getTemplateData('edges');

      templateNodes = templateNodes.map((node: any) => {
        const nodeData = node.data || node;
        if ((node.type === 'retriever__knowledge' || node.type === 'retriever_knowledge') && nodeData.knowledge_retriever) {
          if (nodeData.knowledge_retriever.repo_id === null || nodeData.knowledge_retriever.repo_id === 'None') {
            nodeData.knowledge_retriever.repo_id = '';
          }
          if (Array.isArray(nodeData.input_keys)) {
            nodeData.input_keys = nodeData.input_keys.map((key: any, i: number) => ({
              ...key,
              name: key.name || `input_${i}`,
            }));
          }
        }
        return node;
      });
    }

    const requestData: any = {
      name: agentInfo.name,
      description: agentInfo.description,
    };

    if (templateId) {
      requestData.template_id = templateId;

      if (templateNodes.length > 0 || templateEdges.length > 0) {
        requestData.graph = {
          nodes: templateNodes,
          edges: templateEdges,
        };
      }
    }

    createAgentMutation.mutate(requestData);
  };

  return (
    <>
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
                <UIStepper currentStep={2} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>

            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <Button className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                    취소
                  </Button>
                  <Button auth={AUTH_KEY.AGENT.AGENT_CREATE} className='btn-tertiary-blue' style={{ width: '80px' }} disabled={isSaveDisabled} onClick={handleSave}>
                    저장
                  </Button>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        <section className='section-popup-content'>
          <UIPopupHeader title='기본 정보 입력' description='' position='right' />
          <UIPopupBody>
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  이름
                </UITypography>
                <div>
                  <UIInput.Text value={agentInfo.name} onChange={e => handleInputChange('name', e.target.value)} placeholder='에이전트 이름 입력' maxLength={50} />
                </div>
              </UIFormField>
            </UIArticle>

            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  설명
                </UITypography>
                <div>
                  <UITextArea2 value={agentInfo.description} onChange={e => handleInputChange('description', e.target.value)} placeholder='에이전트 설명 입력' maxLength={100} />
                </div>
              </UIFormField>
            </UIArticle>
          </UIPopupBody>
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
    </>
  );
}
