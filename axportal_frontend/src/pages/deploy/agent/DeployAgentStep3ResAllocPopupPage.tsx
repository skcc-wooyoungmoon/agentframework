import { useEffect, useMemo, useState } from 'react';

import { Button } from '@/components/common/auth';
import { UIIcon2, UISlider, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIFormField, UIInput, UIList, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup, type UIStepperItem } from '@/components/UI/molecules';
import { UICircleChart } from '@/components/UI/molecules/chart';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { PayReqWizard } from '@/pages/common/PayReqWizrad.tsx';
import { useCreateAgentApp, useGetClusterResources } from '@/services/deploy/agent/agentDeploy.services';
import { useUser } from '@/stores/auth';
import { useModal } from '@/stores/common/modal';
import { useDeployAgent } from '@/stores/deploy/useDeployAgent';

interface DeployAgentStep3ResAllocPopupPageProps {
  isOpen: boolean;
  stepperItems: UIStepperItem[];
  builderName?: string;
  onClose: () => void;
  onPreviousStep: () => void;
  onDeploySuccess?: () => void;
  currentStep?: number;
}

export function DeployAgentStep3ResAllocPopupPage({
  isOpen,
  stepperItems = [],
  builderName = '',
  onClose,
  onPreviousStep,
  onDeploySuccess,
  currentStep = 3,
}: DeployAgentStep3ResAllocPopupPageProps) {
  const { deployData, updateDeployData, getFinalDeployData, resetDeployData } = useDeployAgent();
  const { openConfirm, openAlert } = useModal();
  const { showComplete } = useCommonPopup();
  const { user } = useUser();
  const [isPayReqWizardOpen, setIsPayReqWizardOpen] = useState(false);

  const createAgentAppMutation = useCreateAgentApp();

  const [approvalInfo, setApprovalInfo] = useState<{
    memberId: string;
    approvalType: string; // 업무코드
    approvalUniqueKey?: string; // 요청식별자 (중복방지 등 목적으로 각 업무에서 활용)
    approvalParamKey?: number; // 비정형 결재자 처리를 위한 키값 (ex. 프로젝트 참여)
    approvalParamValue?: string; // 비정형 결재자 처리를 위한 키값 (ex. 프로젝트 참여)
    approvalItemString: string; // 요청하는 대상/작업 이름 (알람 표시 목적)
    afterProcessParamString: string; // 후처리 변수
    approvalSummary?: string; // 결재사유 메세지
  }>({
    memberId: '',
    approvalType: '',
    afterProcessParamString: '',
    approvalItemString: '',
  });

  // 슬라이더 상태 관리
  const [cpuValue, setCpuValue] = useState(deployData.cpuLimit || 1);
  const [memoryValue, setMemoryValue] = useState(deployData.memLimit || 1);
  const [replicaValue, setReplicaValue] = useState('1');

  const { data: resourceData } = useGetClusterResources(
    { nodeType: 'agent' },
    {
      enabled: isOpen && !!deployData.targetId && deployData.targetId.length > 0,
    }
  );

  const { cpuTotal, memTotal, cpuUsed, memUsed, maxReplica } = useMemo(() => {
    const namespaceResource = resourceData?.namespace_resource;

    // 기본값 설정 (API 데이터가 없을 때 사용)
    const defaultValues = {
      cpu_quota: 0,
      mem_quota: 0,
      cpu_used: 0,
      mem_used: 0,
      cpu_usable: 0,
      mem_usable: 0,
    };

    // 실제 데이터 또는 기본값 사용
    const cpuUsable = namespaceResource?.cpu_usable ?? defaultValues.cpu_usable; // 사용 가능한 CPU 자원
    const memUsable = namespaceResource?.mem_usable ?? defaultValues.mem_usable; // 사용 가능한 메모리 자원

    // 최대 복제본 수 계산 (제공된 로직에 맞게)
    // Step 1: CPU 기반 최대 복제본 수 계산 (사용 가능한 CPU ÷ 사용자가 설정한 CPU 값)
    // 예: cpuUsable=8, cpuValue=2 → 8÷2 = 4.0개 복제본 가능
    const cpuQuotient = Math.floor((cpuUsable / cpuValue) * 10) / 10;

    // Step 2: 메모리 기반 최대 복제본 수 계산 (사용 가능한 메모리 ÷ 사용자가 설정한 메모리 값)
    // 예: memUsable=16, memoryValue=4 → 16÷4 = 4.0개 복제본 가능
    const memQuotient = Math.floor((memUsable / memoryValue) * 10) / 10;

    // Step 3: 최종 최대 복제본 수 결정 (CPU와 메모리 중 더 제한적인 자원 기준으로 결정)
    // 병목 자원을 기준으로 최대 복제본 수를 계산 (예: CPU로는 4개, 메모리로는 3개 가능 → 최대 3개)
    const calculatedMaxReplica = Math.floor(Math.min(cpuQuotient, memQuotient)) == Infinity ? 0 : Math.floor(Math.min(cpuQuotient, memQuotient));

    // 최대 복제본이 0이면 복제본 입력값을 '0'으로 설정
    if (calculatedMaxReplica === 0) {
      setReplicaValue('0');
    }

    return {
      cpuTotal: namespaceResource?.cpu_quota ?? defaultValues.cpu_quota,
      memTotal: namespaceResource?.mem_quota ?? defaultValues.mem_quota,
      cpuUsed: namespaceResource?.cpu_used ?? defaultValues.cpu_used,
      memUsed: namespaceResource?.mem_used ?? defaultValues.mem_used,
      maxReplica: calculatedMaxReplica,
    };
  }, [resourceData, cpuValue, memoryValue]);

  const handlePreviousStep = () => {
    // 이전 단계로 이동하기 전에 현재 값들을 deployData에 저장
    const numReplica = parseInt(replicaValue) || 1;
    updateDeployData({
      cpuLimit: cpuValue,
      cpuRequest: cpuValue,
      memLimit: memoryValue,
      memRequest: memoryValue,
      maxReplicas: numReplica,
      minReplicas: numReplica,
    });
    onPreviousStep();
  };

  /**
   * 닫기 버튼 클릭
   */
  const handleClose = () => {
    // 로컬 state 초기화
    setCpuValue(1);
    setMemoryValue(1);
    setReplicaValue('1');
    setIsPayReqWizardOpen(false);
    setApprovalInfo({
      memberId: '',
      approvalType: '',
      afterProcessParamString: '',
      approvalItemString: '',
    });
    // deployData 초기화 (팝업을 완전히 닫을 때만)
    resetDeployData();
    onClose();
  };

  // PayReqWizard 닫기 핸들러
  const handlePayReqWizardClose = () => {
    setIsPayReqWizardOpen(false);
    // 부모 컴포넌트에 알림
    onDeploySuccess?.();
    resetDeployData();
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
      onCancel: () => {
        // console.log('취소됨');
      },
    });
  };

  // 슬라이더 핸들러 - deployData 업데이트
  const handleCpuChange = (value: number) => {
    const v = Math.round(value);
    setCpuValue(v);
    // cpuRequest는 cpuLimit의 절반으로 설정 (반올림된 값 사용)
    const cpuRequest = v;
    updateDeployData({ cpuLimit: v, cpuRequest });
  };

  const handleMemoryChange = (value: number) => {
    const v = Math.round(value);
    setMemoryValue(v);
    // memRequest는 memLimit의 절반으로 설정 (반올림된 값 사용)
    const memRequest = v;
    updateDeployData({ memLimit: v, memRequest });
  };

  // 배포 성공 핸들러
  const handleDeploySuccess = (appId: string) => {
    showComplete({
      itemName: '에이전트 배포를',
      onConfirm: () => {
        onDeploySuccess?.();
      },
    });
  };

  // 배포 버튼 클릭 핸들러
  const handleDeploy = async (isDirectDeploy: boolean = false) => {
    const finalData = getFinalDeployData();

    // 유효성 검사
    if (!finalData.name) {
      await openAlert({
        title: '오류',
        message: '배포명을 입력해주세요.',
      });
      return;
    }

    if (!finalData.targetId) {
      await openAlert({
        title: '오류',
        message: '에이전트를 선택해주세요.',
      });
      return;
    }

    if (isDirectDeploy) {
      // TODO 우회 (삭제 예정) - 직접 배포
      createAgentAppMutation.mutate(finalData, {
        onSuccess: ({ data: appId }) => {
          handleDeploySuccess(appId);
        },
      });
    } else {
      // 일반 배포: 결재 프로세스 실행
      const newApprovalInfo = {
        memberId: user.userInfo.memberId,
        approvalType: '05', // 에이전트 배포
        approvalUniqueKey: user.userInfo.memberId + '-' + finalData.targetId,
        approvalItemString: `${finalData.name}`,
        afterProcessParamString: JSON.stringify({
          name: finalData.name,
          description: finalData.description,
          targetId: finalData.targetId,
          targetType: finalData.targetType,
          servingType: finalData.servingType,
          cpuLimit: finalData.cpuLimit,
          cpuRequest: finalData.cpuRequest,
          gpuLimit: finalData.gpuLimit,
          gpuRequest: finalData.gpuRequest,
          memLimit: finalData.memLimit,
          memRequest: finalData.memRequest,
          maxReplicas: finalData.maxReplicas,
          minReplicas: finalData.minReplicas,
          workersPerCore: finalData.workersPerCore,
          versionDescription: finalData.versionDescription,
          safetyFilterOptions: finalData.safetyFilterOptions,
        }),
        apprivalTableInfo: [
          [
            { key: '에이전트명', value: builderName },
            { key: '배포명', value: finalData.name },
          ],
          [{ key: '할당 자원', value: `CPU : ${finalData.cpuRequest}Core, Memory : ${finalData.memRequest}GiB` }],
        ],
      };
      setIsPayReqWizardOpen(true);
      setApprovalInfo(newApprovalInfo);
    }
  };

  // replica 값 감소
  const handleReplicaDecrement = () => {
    const numValue = parseInt(replicaValue) || 1;
    if (numValue > 1) {
      const next = numValue - 1;
      setReplicaValue(next.toString());
      updateDeployData({ maxReplicas: next, minReplicas: next });
    }
  };

  // replica 값 증가
  const handleReplicaIncrement = () => {
    const numValue = parseInt(replicaValue) || 1;
    if (numValue < maxReplica) {
      setReplicaValue((numValue + 1).toString());
      updateDeployData({ maxReplicas: numValue + 1, minReplicas: numValue + 1 });
    }
  };

  // 팝업 열릴 때 deployData에서 최신 값으로 동기화
  useEffect(() => {
    if (isOpen) {
      // deployData가 초기화된 상태(빈 값)이면 기본값으로 초기화
      if (!deployData.targetId) {
        setCpuValue(1);
        setMemoryValue(1);
        setReplicaValue('1');
        updateDeployData({ maxReplicas: 1, minReplicas: 1 });
      } else {
        // deployData에서 값 복원
        setCpuValue(deployData.cpuLimit || 1);
        setMemoryValue(deployData.memLimit || 1);
        setReplicaValue(String(deployData.maxReplicas || 1));
      }
      setIsPayReqWizardOpen(false);
    }
    // isOpen이 false가 될 때는 handleClose에서 처리하므로 여기서는 제거
  }, [isOpen, deployData.targetId, deployData.cpuLimit, deployData.memLimit, deployData.maxReplicas, updateDeployData]);

  // 필수 필드 검증
  const isDeployDisabled = !deployData.name || !deployData.targetId;

  return (
    <>
      <UILayerPopup
        isOpen={isOpen}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            <UIPopupHeader title='에이전트 배포하기' description='' position='left' />
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={currentStep} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <Button className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                    취소
                  </Button>
                  <Button
                    auth={AUTH_KEY.DEPLOY.AGENT_DEPLOY_CREATE}
                    className='btn-tertiary-blue'
                    style={{ width: '80px' }}
                    disabled={isDeployDisabled}
                    onClick={() => handleDeploy(true)}
                  >
                    배포
                  </Button>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 - 기존 컴포넌트 사용 */}
        <section className='section-popup-content'>
          <UIPopupHeader title='자원 할당' description='' position='right' />

          <UIPopupBody>
            <UIArticle>
              <UITypography variant='title-3' className='secondary-neutral-800 text-sb' required={false}>
                자원 할당
              </UITypography>
            </UIArticle>

            <UIArticle>
              {/* 새로운 디자인: 리소스 정보 + 리소스 차트 */}
              <div className='flex items-center'>
                {/* 왼쪽: 리소스 차트 */}
                <div className='flex-1 flex ml-[60px] justify-center'>
                  <div className='flex chart-graph h-[300px] gap-x-20 justify-center'>
                    <div className='w-[240px] flex items-center justify-center'>
                      {/* CPU Gauge */}
                      <UICircleChart.Half type='CPU' value={parseFloat(cpuUsed.toFixed(1))} total={parseFloat(cpuTotal.toFixed(1))} showLabel={false} />
                      {/* Memory Gauge */}
                      <UICircleChart.Half type='Memory' value={parseFloat(memUsed.toFixed(1))} total={parseFloat(memTotal.toFixed(1))} showLabel={false} />
                    </div>
                  </div>
                </div>

                {/* 오른쪽: 리소스 정보 */}
                <div className='flex-shrink-0'>
                  <div className='resource-info-container w-36 opacity-100 gap-4 p-4 rounded-xl border border-gray-300 flex flex-col justify-between'>
                    {/* 할당된 자원 섹션 */}
                    <div className='resource-section'>
                      <UITypography variant='title-4' className='secondary-neutral-900'>
                        할당된 자원
                      </UITypography>
                      <div className='flex flex-col gap-2 pt-3'>
                        <div className='flex items-center gap-2'>
                          <UIIcon2 className='ic-system-8-dot-1' />
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            CPU
                          </UITypography>
                        </div>
                        <div className='flex items-center gap-2'>
                          <UIIcon2 className='ic-system-8-dot-2' />
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            Memory
                          </UITypography>
                        </div>
                      </div>
                    </div>

                    {/* 할당 가능한  자원 섹션 */}
                    <div className='resource-section'>
                      <UITypography variant='title-4' className='secondary-neutral-900'>
                        할당 가능한 자원
                      </UITypography>

                      <div className='flex items-center gap-2 pt-3'>
                        <UIIcon2 className='ic-system-8-dot-4' />
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          사용 가능 자원
                        </UITypography>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </UIArticle>
            <UIArticle>
              <div className='w-full flex flex-col space-y-[8px]'>
                <UISlider
                  label='CPU'
                  value={cpuValue}
                  textValue={Math.round(cpuValue).toString()}
                  min={0}
                  max={Math.floor(resourceData?.namespace_resource?.cpu_usable || 0) >= 8 ? 8 : Math.floor(resourceData?.namespace_resource?.cpu_usable || 0)}
                  required={true}
                  onChange={handleCpuChange}
                  unit=''
                  color='#2670FF'
                  showTextField={true}
                />
                <UISlider
                  label='Memory'
                  value={memoryValue}
                  textValue={Math.round(memoryValue).toString()}
                  min={0}
                  max={Math.floor(resourceData?.namespace_resource?.mem_usable || 0) >= 16 ? 16 : Math.floor(resourceData?.namespace_resource?.mem_usable || 0)}
                  required={true}
                  onChange={handleMemoryChange}
                  unit=''
                  color='#37D8D0'
                  showTextField={true}
                />
              </div>
            </UIArticle>

            {/* 입력 필터 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                  에이전트 복제 인스턴스 수
                </UITypography>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <div className='flex-1'>
                    <UIInput.Text value={replicaValue} readOnly={true} onChange={() => {}} placeholder='복제본 수 입력' max={maxReplica} />
                  </div>
                  <div>
                    <Button className='btn-secondary-minus' onClick={handleReplicaDecrement}>
                      {''}
                    </Button>
                  </div>
                  <div>
                    <Button className='btn-secondary-plus' onClick={handleReplicaIncrement}>
                      {''}
                    </Button>
                  </div>
                </UIUnitGroup>
              </UIFormField>
              <UIList
                gap={4}
                direction='column'
                className='ui-list_bullet'
                data={[
                  {
                    dataItem: (
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        선택한 리소스의 크기를 고려하여 설정할 수 있는 최대 복제본 수는 {maxReplica}개입니다.
                      </UITypography>
                    ),
                  },
                ]}
              />
            </UIArticle>
          </UIPopupBody>

          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <Button className='btn-secondary-gray' style={{ width: '80px' }} onClick={handlePreviousStep}>
                  이전
                </Button>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
      {isPayReqWizardOpen && <PayReqWizard isOpen={isPayReqWizardOpen} onClose={handlePayReqWizardClose} approvalInfo={approvalInfo} />}
    </>
  );
}
