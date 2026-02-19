import React, { useEffect, useMemo, useState } from 'react';

import { UIButton2, UIIcon2, UISlider, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIDropdown, UIFormField, UIInput, UIList, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup } from '@/components/UI/molecules';
import { UICircleChart } from '@/components/UI/molecules/chart';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { env, RUN_MODE_TYPES } from '@/constants/common/env.constants.ts';
import type { LayerPopupProps } from '@/hooks/common/layer';
import { useGetScalingGroups } from '@/services/home/dashboard/dashboard.services.ts';

interface DeployModelStep3ResAllocProps extends LayerPopupProps {
  resourceAllocData: {
    selectedResource: string;
    cpuValue: number;
    memoryValue: number;
    gpuValue: number;
    instanceCount: number;
  };
  setResourceAllocData: React.Dispatch<
    React.SetStateAction<{
      selectedResource: string;
      cpuValue: number;
      memoryValue: number;
      gpuValue: number;
      instanceCount: number;
    }>
  >;
  onClickCreateDeployModel: (isDirectDeploy?: boolean) => void;
}

const chartValuesInit = {
  cpu: { value: 0, maxValue: 0, usableValue: 0 },
  memory: { value: 0, maxValue: 0, usableValue: 0 },
  gpu: { value: 0, maxValue: 0, usableValue: 0 },
};

// 바이트를 기가바이트로 변환하고 소수점 1자리로 제한 (버림 처리)
const bytesToGB = (bytes: number): number => {
  return Math.floor((bytes / 1024 ** 3) * 10) / 10;
};

// 소수점 1자리로 버림 처리 (부동소수점 오차 보정)
const normalizeFloat = (num: number): number => {
  return Math.floor((num + 1e-9) * 10) / 10;
};

export const DeployModelStep3ResAlloc = ({
  currentStep,
  stepperItems = [],
  onClose,
  onPreviousStep,
  resourceAllocData,
  setResourceAllocData,
  onClickCreateDeployModel,
}: DeployModelStep3ResAllocProps) => {
  // TODO 우회
  const [isDirectDeploy, setIsDirectDeploy] = useState(false);
  // 자원 선택
  const [cpuValue, setCpuValue] = useState(0);
  const [memoryValue, setMemoryValue] = useState(0);
  const [gpuValue, setGpuValue] = useState(0);
  const [instanceCount, setInstanceCount] = useState(resourceAllocData.instanceCount);

  const [resourceGroup, setResourceGroup] = useState(resourceAllocData.selectedResource || 'none');

  // 리소스 데이터 기반으로 차트 값 계산
  const [chartValues, setChartValues] = useState<typeof chartValuesInit>(chartValuesInit);

  const { data: scalingGroupData } = useGetScalingGroups(
    { isActive: true },
    {
      staleTime: 0, // 데이터를 항상 stale로 취급
      refetchOnMount: 'always', // 마운트 시 항상 refetch
    }
  );

  useEffect(() => {
    if (resourceGroup === 'none' || !scalingGroupData) {
      setChartValues(chartValuesInit);
      return;
    }

    const targetGroup = scalingGroupData?.scalingGroups?.find(group => group.name === resourceGroup);

    if (targetGroup) {
      // schedulable이 true인 에이전트만 필터링
      const schedulableAgents = targetGroup.agentList?.filter(agent => agent.schedulable) || [];

      // 각 에이전트의 availableSlots와 occupiedSlots를 합산
      const availableSlots: Record<string, number> = {};
      const occupiedSlots: Record<string, number> = {};

      schedulableAgents.forEach(agent => {
        // availableSlots 합산
        if (agent.availableSlots) {
          Object.entries(agent.availableSlots).forEach(([key, value]) => {
            const numValue = typeof value === 'string' ? parseFloat(value) : (value as number);
            if (!isNaN(numValue)) {
              availableSlots[key] = (availableSlots[key] || 0) + numValue;
            }
          });
        }

        // occupiedSlots 합산
        if (agent.occupiedSlots) {
          Object.entries(agent.occupiedSlots).forEach(([key, value]) => {
            const numValue = typeof value === 'string' ? parseFloat(value) : (value as number);
            if (!isNaN(numValue)) {
              occupiedSlots[key] = (occupiedSlots[key] || 0) + numValue;
            }
          });
        }
      });

      // CPU 설정
      const cpuQuota = availableSlots.cpu || 0;
      const cpuUsed = occupiedSlots.cpu || 0;

      // Memory 설정 (바이트를 GB로 변환)
      const memQuota = availableSlots.mem ? bytesToGB(availableSlots.mem) : 0;
      const memUsed = occupiedSlots.mem ? bytesToGB(occupiedSlots.mem) : 0;

      // GPU 설정 (cuda.shares 또는 cuda_shares)
      const gpuQuota = availableSlots['cuda.shares'] || availableSlots.cuda_shares || 0;
      const gpuUsed = occupiedSlots['cuda.shares'] || occupiedSlots.cuda_shares || 0;

      // 차트 값 설정
      setChartValues({
        cpu: {
          value: cpuUsed,
          maxValue: cpuQuota,
          usableValue: normalizeFloat(cpuQuota - cpuUsed),
        },
        memory: {
          value: memUsed,
          maxValue: memQuota,
          usableValue: normalizeFloat(memQuota - memUsed),
        },
        gpu: {
          value: gpuUsed,
          maxValue: gpuQuota,
          usableValue: normalizeFloat(gpuQuota - gpuUsed),
        },
      });
    }
  }, [scalingGroupData, resourceGroup]);

  const resourceGroupList = useMemo(() => {
    const groupList = [{ value: 'none', label: '리소스 그룹 선택' }];

    scalingGroupData?.scalingGroups?.forEach(group => {
      if ([RUN_MODE_TYPES.E_DEV, RUN_MODE_TYPES.E_LOCAL].includes(env.VITE_RUN_MODE) || (group?.name?.startsWith('INF') && group?.agentList && group?.agentList?.length > 0)) {
        groupList.push({ ...group, value: group.name, label: group.name });
      }
    });

    return groupList;
  }, [scalingGroupData]);

  const handleClose = () => {
    onClose();
  };

  // 자원량에 따른 최대 복제본 수 계산
  const maxReplicaCount = useMemo(() => {
    // 리소스 그룹이 선택되지 않았거나 자원량이 설정되지 않은 경우 기본값 반환
    if (!resourceGroup || resourceGroup === 'none' || !cpuValue || !memoryValue || !gpuValue) {
      return 104; // 기본값
    }

    // 사용 가능한 자원량
    const cpuUsable = chartValues.cpu.usableValue;
    const memUsable = chartValues.memory.usableValue;
    const gpuUsable = chartValues.gpu.usableValue;

    // 각 자원별로 가능한 최대 복제본 수 계산
    // Step 1: CPU 기반 최대 복제본 수 계산
    const cpuQuotient = cpuValue > 0 ? Math.floor((cpuUsable / cpuValue) * 10) / 10 : Infinity;

    // Step 2: Memory 기반 최대 복제본 수 계산
    const memQuotient = memoryValue > 0 ? Math.floor((memUsable / memoryValue) * 10) / 10 : Infinity;

    // Step 3: GPU 기반 최대 복제본 수 계산
    const gpuQuotient = gpuValue > 0 ? Math.floor((gpuUsable / gpuValue) * 10) / 10 : Infinity;

    // Step 4: 최종 최대 복제본 수 결정 (가장 제한적인 자원 기준)
    const calculatedMaxReplica = Math.floor(Math.min(cpuQuotient, memQuotient, gpuQuotient, 104));

    return Math.max(1, calculatedMaxReplica); // 최소 1개
  }, [resourceGroup, cpuValue, memoryValue, gpuValue, chartValues]);

  // 자원량 변경 시 인스턴스 수가 최대값을 초과하지 않도록 조정
  useEffect(() => {
    setInstanceCount(prev => {
      if (prev > maxReplicaCount) {
        const adjustedCount = Math.max(1, Math.min(prev, maxReplicaCount));
        setResourceAllocData(prevData => ({
          ...prevData,
          instanceCount: adjustedCount,
        }));
        return adjustedCount;
      }
      return prev;
    });
  }, [maxReplicaCount]);

  // 슬라이더 핸들러
  const handleCpuChange = (value: number) => {
    const flooredValue = Math.floor(value * 10) / 10;
    setCpuValue(flooredValue);

    setResourceAllocData(prev => ({
      ...prev,
      cpuValue: flooredValue,
    }));
  };

  const handleMemoryChange = (value: number) => {
    const flooredValue = Math.floor(value * 10) / 10;
    setMemoryValue(flooredValue);
    setResourceAllocData(prev => ({
      ...prev,
      memoryValue: flooredValue,
    }));
  };

  const handleGpuChange = (value: number) => {
    const flooredValue = Math.floor(value * 10) / 10;
    setGpuValue(flooredValue);
    setResourceAllocData(prev => ({
      ...prev,
      gpuValue: flooredValue,
    }));
  };

  // 인스턴스 수 변경 핸들러
  const handleInstanceCountChange = (value: string) => {
    const numValue = parseInt(value) || 1;
    const clampedValue = Math.max(1, Math.min(numValue, maxReplicaCount));
    setInstanceCount(clampedValue);
    setResourceAllocData(prev => ({
      ...prev,
      instanceCount: clampedValue,
    }));
  };

  const handleInstanceCountIncrement = () => {
    const newValue = Math.min(instanceCount + 1, maxReplicaCount);
    setInstanceCount(newValue);
    setResourceAllocData(prev => ({
      ...prev,
      instanceCount: newValue,
    }));
  };

  const handleInstanceCountDecrement = () => {
    const newValue = Math.max(instanceCount - 1, 1);
    setInstanceCount(newValue);
    setResourceAllocData(prev => ({
      ...prev,
      instanceCount: newValue,
    }));
  };

  // 필수 필드 검증
  const validateRequiredFields = () => {
    return !resourceGroup || resourceGroup === 'none' || cpuValue === undefined || memoryValue === undefined || gpuValue === undefined || !instanceCount;
  };

  return (
    <>
      {/* DesignLayout 위에 높은 z-index로 뜨는 UILayerPopup */}
      <UILayerPopup
        isOpen={currentStep === 3}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            <UIPopupHeader title='모델 배포하기' description='' position='left' />
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={currentStep} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleClose}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={validateRequiredFields()} onClick={() => onClickCreateDeployModel(true)}>
                    배포
                  </UIButton2>
                </UIUnitGroup>
                <div className='mt-20'>
                  {/* // TODO YERI 우회 */}
                  {/* {env.VITE_RUN_MODE !== 'PROD' && */}
                  {!isDirectDeploy ? (
                    <UIButton2 className='w-10 h-10' onClick={() => setIsDirectDeploy(true)}></UIButton2>
                  ) : (
                    <UIButton2 className='btn-secondary-blue' disabled={validateRequiredFields()} onClick={() => onClickCreateDeployModel(true)}>
                      바로 배포
                    </UIButton2>
                  )}
                </div>
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
              <UIUnitGroup gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-900' required={true}>
                  리소스 그룹 선택
                </UITypography>
                <UIDropdown
                  value={resourceGroup}
                  options={resourceGroupList.map(item => ({ value: item.value, label: item.label }))}
                  onSelect={(value: string) => {
                    setResourceGroup(value);
                    setResourceAllocData(prev => ({ ...prev, selectedResource: value }));
                  }}
                  placeholder='리소스 그룹 선택'
                />
              </UIUnitGroup>
            </UIArticle>

            <UIArticle>
              {/* 새로운 디자인: 리소스 정보 + 리소스 차트 */}
              <div className='flex items-center gap-[128px]'>
                {/* 왼쪽: 리소스 차트 */}
                <div className='flex-1 flex ml-[60px] justify-center'>
                  <div className='flex chart-graph h-[300px] gap-x-20 justify-center'>
                    <div className='w-[240px] flex items-center justify-center'>
                      <UICircleChart.Half type='CPU' value={chartValues?.cpu?.usableValue} total={chartValues?.cpu?.maxValue} showLabel={false} />{' '}
                      {/* [참고] showLabel : 그래프 하단의 라벨(사용중인 자원) 숨김 처리 */}
                    </div>
                    <div className='w-[240px] flex items-center justify-center'>
                      <UICircleChart.Half type='Memory' value={chartValues?.memory?.usableValue} total={chartValues?.memory?.maxValue} showLabel={false} />
                    </div>
                    <div className='w-[240px] flex items-center justify-center'>
                      <UICircleChart.Half type='GPU' value={chartValues?.gpu?.usableValue} total={chartValues?.gpu?.maxValue} showLabel={false} />
                    </div>
                  </div>
                </div>

                {/* 오른쪽: 리소스 정보 */}
                <div className='flex-shrink-0'>
                  <div className='resource-info-container w-36 opacity-100 gap-4 p-4 rounded-xl border border-gray-300 flex flex-col justify-between'>
                    {/* 할당 가능한 자원 섹션 */}
                    <div className='resource-section'>
                      <UITypography variant='title-4' className='secondary-neutral-900'>
                        할당 가능한 자원
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
                        <div className='flex items-center gap-2'>
                          <UIIcon2 className='ic-system-8-dot-3' />
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            GPU
                          </UITypography>
                        </div>
                      </div>
                    </div>

                    {/* 할당된 자원 섹션 */}
                    <div className='resource-section'>
                      <UITypography variant='title-4' className='secondary-neutral-900'>
                        할당된 자원
                      </UITypography>
                      <div className='flex items-center gap-2 pt-3'>
                        <UIIcon2 className='ic-system-8-dot-4' />
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          사용중인 자원
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
                  textValue={cpuValue.toString()}
                  min={0}
                  max={chartValues.cpu.usableValue}
                  required={true}
                  onChange={handleCpuChange}
                  unit=''
                  color='#2670FF'
                  showTextField={true}
                  decimalPlaces={1}
                />
                <UISlider
                  label='Memory'
                  value={memoryValue}
                  textValue={memoryValue.toString()}
                  min={0}
                  max={chartValues.memory.usableValue}
                  required={true}
                  onChange={handleMemoryChange}
                  unit=''
                  color='#37D8D0'
                  showTextField={true}
                  decimalPlaces={1}
                />
                <UISlider
                  label='GPU'
                  value={gpuValue}
                  textValue={gpuValue.toString()}
                  step={0.1}
                  min={0}
                  max={chartValues.gpu.usableValue}
                  required={true}
                  onChange={handleGpuChange}
                  unit=''
                  color='#8166D2'
                  showTextField={true}
                  decimalPlaces={1}
                />
              </div>
            </UIArticle>

            {/* 입력 필터 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                  모델 복제 인스턴스 수
                </UITypography>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <div className='flex-1'>
                    <UIInput.Text
                      readOnly
                      value={instanceCount.toString()}
                      onChange={e => {
                        handleInstanceCountChange(e.target.value);
                      }}
                      placeholder='인스턴스 수 입력'
                    />
                  </div>
                  <div>
                    <UIButton2 className='btn-secondary-minus' onClick={handleInstanceCountDecrement} />
                  </div>
                  <div>
                    <UIButton2 className='btn-secondary-plus' onClick={handleInstanceCountIncrement} />
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
                        {resourceGroup && resourceGroup !== 'none' && cpuValue > 0 && memoryValue > 0 && gpuValue > 0
                          ? `선택한 리소스의 크기를 고려하여 설정할 수 있는 최대 복제본 수는 ${maxReplicaCount}개입니다.`
                          : '복제될 인스턴스 수를 입력해주세요. 최대 복제본 수는 상단에서 선택한 자원량에 따라 결정됩니다.'}
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
                <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }} onClick={onPreviousStep}>
                  이전
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
