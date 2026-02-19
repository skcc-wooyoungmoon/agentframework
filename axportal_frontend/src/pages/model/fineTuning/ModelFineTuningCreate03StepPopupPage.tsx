import { useAtom } from 'jotai';
import React, { useEffect, useMemo, useState } from 'react';

import { UIButton2, UIIcon2, UISlider, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIDropdown, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, type UIStepperItem, UIUnitGroup } from '@/components/UI/molecules';
import { UICircleChart } from '@/components/UI/molecules/chart';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { env, RUN_MODE_TYPES } from '@/constants/common/env.constants.ts';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { useGetScalingGroups } from '@/services/home/dashboard/dashboard.services.ts';
import {
  fineTuningCpuValueAtom,
  fineTuningGpuValueAtom,
  fineTuningMemoryValueAtom,
  fineTuningScalingGroupAtom,
  resetAllFineTuningDataAtom,
} from '@/stores/model/fineTuning/fineTuning.atoms';

interface LayerPopupProps {
  currentStep: number;
  stepperItems: UIStepperItem[];
  onNextStep: () => void;
  onPreviousStep: () => void;
  onClose: () => void;
}

// 바이트를 기가바이트로 변환하고 소수점 1자리로 제한 (버림 처리)
const bytesToGB = (bytes: number): number => {
  return Math.floor((bytes / 1024 ** 3) * 10) / 10;
};

const chartValuesInit = {
  cpu: { value: 0, maxValue: 0, usableValue: 0 },
  memory: { value: 0, maxValue: 0, usableValue: 0 },
  gpu: { value: 0, maxValue: 0, usableValue: 0 },
};

// 소수점 1자리로 버림 처리 (부동소수점 오차 보정)
const normalizeFloat = (num: number): number => {
  return Math.floor((num + 1e-9) * 10) / 10;
};

export const ModelFineTuningCreate03StepPopupPage: React.FC<LayerPopupProps> = ({ currentStep, stepperItems, onClose, onNextStep, onPreviousStep }) => {
  // useModal 훅
  const { showCancelConfirm } = useCommonPopup();

  // Jotai 상태 관리
  const [, resetAllData] = useAtom(resetAllFineTuningDataAtom);
  const [cpuValue, setCpuValue] = useAtom(fineTuningCpuValueAtom);
  const [memoryValue, setMemoryValue] = useAtom(fineTuningMemoryValueAtom);
  const [gpuValue, setGpuValue] = useAtom(fineTuningGpuValueAtom);
  const [resourceGroup, setResourceGroup] = useAtom(fineTuningScalingGroupAtom);

  // Custom 리소스 설정 상태
  // const [customCpu, setCustomCpu] = useState(0);
  // const [customMemory, setCustomMemory] = useState(0);
  // const [customGpu, setCustomGpu] = useState(0);
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  // 리소스 데이터 기반으로 차트 값 계산
  const [chartValues, setChartValues] = useState<typeof chartValuesInit>(chartValuesInit);

  const handleNext = () => {
    onNextStep();
  };

  // 취소 핸들러 - 확인 알러트 후 상태 초기화
  const handleCancel = () => {

    showCancelConfirm({
      onConfirm: () => {
        // 모든 상태값 초기화 (Jotai 공통 함수 사용)
        resetAllData();
      },
    });
  };

  // React Query 훅 사용
  const { data: scalingGroupData } = useGetScalingGroups(
    { isActive: true },
    {
      staleTime: 0, // 데이터를 항상 stale로 취급
      refetchOnMount: 'always', // 마운트 시 항상 refetch
    }
  );

  const resourceGroupList = useMemo(() => {
    const groupList = [{ value: 'none', label: '리소스 그룹 선택' }];

    scalingGroupData?.scalingGroups?.forEach(group => {
      if ([RUN_MODE_TYPES.E_DEV, RUN_MODE_TYPES.E_LOCAL].includes(env.VITE_RUN_MODE) || (group?.name?.startsWith('FT') && group?.agentList && group?.agentList?.length > 0)) {
        groupList.push({ ...group, value: group.name, label: group.name });
      }
    });

    return groupList;
  }, [scalingGroupData]);

  useEffect(() => {
    if (resourceGroup === 'none' || !scalingGroupData) {
      setChartValues(chartValuesInit);
      setCpuValue(0);
      setMemoryValue(0);
      setGpuValue(0);
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

      const cpuUseable = normalizeFloat(cpuQuota - cpuUsed);
      const memoryUseable = normalizeFloat(memQuota - memUsed);
      const gpuUseable = normalizeFloat(gpuQuota - gpuUsed);

      const newCpu = Math.min(1, Math.max(1, Math.floor(cpuUseable * 0.25)));
      const newMemory = Math.min(1, Math.max(1, Math.floor(memoryUseable * 0.25)));
      const newGpu = Math.min(0, Math.max(0, Math.floor(gpuUseable)));

      if (!cpuValue) {
        setCpuValue(newCpu);
      }

      if (!cpuValue) {
        setMemoryValue(newMemory);
      }

      if (!cpuValue) {
        setGpuValue(newGpu);
      }

      // 차트 값 설정
      setChartValues({
        cpu: {
          value: cpuUsed,
          maxValue: cpuQuota,
          usableValue: cpuUseable,
        },
        memory: {
          value: memUsed,
          maxValue: memQuota,
          usableValue: memoryUseable,
        },
        gpu: {
          value: gpuUsed,
          maxValue: gpuQuota,
          usableValue: gpuUseable,
        },
      });
    }
  }, [scalingGroupData, resourceGroup]);

  // 초기 리소스 값들을 Jotai atoms에 설정
  // useEffect(() => {
  //   setCpuValue(customCpu);
  //   setMemoryValue(customMemory);
  //   setGpuValue(customGpu);
  // }, [customCpu, customMemory, customGpu, setCpuValue, setMemoryValue, setGpuValue]);

  return (
    <>
      {/* DesignLayout 위에 높은 z-index로 뜨는 UILayerPopup */}
      <UILayerPopup
        isOpen={currentStep === 3}
        onClose={onClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            <UIPopupHeader title='파인튜닝 등록' description='' position='left' />
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={currentStep} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={true}>
                    튜닝시작
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 */}
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
                  isOpen={isDropdownOpen}
                  onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                  onSelect={(value: string) => {
                    if (value === 'none') {
                      // setCustomCpu(0);
                      // setCustomMemory(0);
                      // setCustomGpu(0);

                      setCpuValue(0);
                      setMemoryValue(0);
                      setGpuValue(0);
                    }
                    setResourceGroup(value);
                    setIsDropdownOpen(false);
                  }}
                  placeholder=' 리소스 그룹 선택'
                />
              </UIUnitGroup>
            </UIArticle>
            <UIArticle>
              {/* 새로운 디자인: 리소스 정보 + 리소스 차트 */}
              <div className='flex items-center gap-[80px]'>
                {/* 왼쪽: 리소스 차트 */}
                <div className='flex-1 flex ml-[60px] justify-center'>
                  <div className='flex chart-graph h-[300px] gap-x-10 justify-center'>
                    <div className='w-[240px] flex items-center justify-center'>
                      <UICircleChart.Half type='CPU' value={chartValues?.cpu?.usableValue} total={chartValues?.cpu?.maxValue} showLabel={false} />
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
                  <div className='resource-info-container w-36 h-[218px] opacity-100 gap-4 p-4 rounded-xl border border-gray-300 flex flex-col justify-between'>
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

            {/* 리소스 슬라이더 (Custom 모드 고정) */}
            <UIArticle>
              <div className='w-full flex flex-col space-y-[8px]'>
                <UISlider
                  label='CPU'
                  value={cpuValue}
                  textValue={`${cpuValue}`}
                  min={resourceGroup === 'none' ? 0 : 1}
                  max={chartValues?.cpu?.usableValue}
                  required={true}
                  showTextField={true}
                  onChange={(value: number) => {
                    const flooredValue = Math.floor(value * 10) / 10;
                    setCpuValue(flooredValue);
                  }}
                  unit=''
                  color='#2670FF'
                  decimalPlaces={1}
                  step={0.1}
                />

                <UISlider
                  label='Memory'
                  value={memoryValue}
                  textValue={`${memoryValue}`}
                  min={resourceGroup === 'none' ? 0 : 1}
                  max={chartValues?.memory?.usableValue}
                  required={true}
                  showTextField={true}
                  onChange={(value: number) => {
                    const flooredValue = Math.floor(value * 10) / 10;
                    setMemoryValue(flooredValue);
                  }}
                  unit=''
                  color='#37D8D0'
                  decimalPlaces={1}
                  step={0.1}
                />

                <UISlider
                  label='GPU'
                  value={gpuValue}
                  textValue={`${gpuValue}`}
                  min={0}
                  max={chartValues?.gpu?.usableValue}
                  required={true}
                  showTextField={true}
                  onChange={(value: number) => {
                    const flooredValue = Math.floor(value * 10) / 10;
                    setGpuValue(flooredValue);
                  }}
                  unit=''
                  color='#8166D2'
                  decimalPlaces={1}
                  step={0.1}
                />
              </div>
            </UIArticle>
          </UIPopupBody>

          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }} onClick={onPreviousStep}>
                  이전
                </UIButton2>
                <UIButton2 className='btn-secondary-blue' style={{ width: '80px' }} onClick={handleNext} disabled={resourceGroup === 'none'}>
                  다음
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
