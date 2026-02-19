import { forwardRef, useImperativeHandle, useState } from 'react';

import { UISlider, UITypography } from '@/components/UI/atoms';
import { UIToggle } from '@/components/UI';
import type { ModelParameters } from '@/services/model/playground/types';

interface SetModelParamProps {
  isOpen?: boolean;
  onClose?: () => void;
  onConfirm?: (parameters: ModelParameters) => void;
  initialParameters?: ModelParameters;
  modelName?: string;
}

export interface SetModelParamRef {
  handleConfirm: () => void;
}

export const SetModelParam = forwardRef<SetModelParamRef, SetModelParamProps>(({ onConfirm, initialParameters }, ref) => {
  // Temperature 상태
  const [temperatureRatio, setTemperatureRatio] = useState<number>(initialParameters?.temperature ?? 1);
  const [temperatureChecked, setTemperatureChecked] = useState<boolean>(initialParameters?.temperatureChecked === true);

  // Top P 상태
  const [topPRatio, setTopPRatio] = useState<number>(initialParameters?.topP ?? 1);
  const [topPChecked, setTopPChecked] = useState<boolean>(initialParameters?.topPChecked === true);

  // Presence Penalty 상태
  const [presenceRatio, setPresenceRatio] = useState<number>(initialParameters?.presencePenalty || 0);
  const [presenceChecked, setPresenceChecked] = useState<boolean>(initialParameters?.presencePenaltyChecked === true);

  // Frequency Penalty 상태
  const [frequencyRatio, setFrequencyRatio] = useState<number>(initialParameters?.frequencyPenalty || 0);
  const [frequencyChecked, setFrequencyChecked] = useState<boolean>(initialParameters?.frequencyPenaltyChecked === true);

  // Max Token 상태
  const [maxTokenRatio, setMaxTokenRatio] = useState<number>(initialParameters?.maxTokens || 4096);
  const [maxTokenChecked, setMaxTokenChecked] = useState<boolean>(initialParameters?.maxTokensChecked === true);

  // 확인 버튼 클릭 시 파라미터 전달
  const handleConfirm = () => {
    const parameters: ModelParameters = {};

    // 값과 체크 상태를 함께 저장
    parameters.temperature = temperatureRatio;
    parameters.temperatureChecked = temperatureChecked;

    parameters.topP = topPRatio;
    parameters.topPChecked = topPChecked;

    parameters.presencePenalty = presenceRatio;
    parameters.presencePenaltyChecked = presenceChecked;

    parameters.frequencyPenalty = frequencyRatio;
    parameters.frequencyPenaltyChecked = frequencyChecked;

    parameters.maxTokens = maxTokenRatio;
    parameters.maxTokensChecked = maxTokenChecked;

    onConfirm?.(parameters);
  };

  // ref를 통해 handleConfirm 함수를 노출
  useImperativeHandle(
    ref,
    () => ({
      handleConfirm,
    }),
    [temperatureChecked, topPChecked, presenceChecked, frequencyChecked, maxTokenChecked, temperatureRatio, topPRatio, presenceRatio, frequencyRatio, maxTokenRatio]
  );

  return (
    <div className='flex h-full'>
      <div className='w-full flex flex-col gap-5'>
        {/* Column */}
        <div className='flex flex-col gap-4'>
          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
            Temperature
          </UITypography>
          <div className='flex w-full'>
            <div className=''>
              <UIToggle
                size='medium'
                checked={temperatureChecked}
                onChange={() => {
                  setTemperatureChecked(!temperatureChecked);
                }}
              />
            </div>
            <div className='pl-8 pr-6 flex-1'>
              <UISlider
                required={true}
                value={temperatureRatio}
                min={0}
                max={2}
                onChange={value => {
                  setTemperatureRatio(value);
                }}
                startLabel='0'
                endLabel='2'
                width='100%'
                showTextField={true}
                disabled={!temperatureChecked}
                decimalPlaces={1}
                step={0.1}
              />
            </div>
          </div>
        </div>

        {/* Column */}
        <div className='flex flex-col gap-4'>
          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
            Top P
          </UITypography>
          <div className='flex w-full'>
            <div className=''>
              <UIToggle
                size='medium'
                checked={topPChecked}
                onChange={() => {
                  setTopPChecked(!topPChecked);
                }}
              />
            </div>
            <div className='pl-8 pr-6 flex-1'>
              <UISlider
                required={true}
                value={topPRatio}
                min={0}
                max={1}
                onChange={value => {
                  setTopPRatio(value);
                }}
                startLabel='0'
                endLabel='1'
                width='100%'
                showTextField={true}
                disabled={!topPChecked}
                decimalPlaces={1}
                step={0.1}
              />
            </div>
          </div>
        </div>

        {/* Column */}
        <div className='flex flex-col gap-4'>
          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
            Presence Penalty
          </UITypography>
          <div className='flex w-full'>
            <div className=''>
              <UIToggle
                size='medium'
                checked={presenceChecked}
                onChange={() => {
                  setPresenceChecked(!presenceChecked);
                }}
              />
            </div>
            <div className='pl-8 pr-6 flex-1'>
              <UISlider
                required={true}
                value={presenceRatio}
                min={-2}
                max={2}
                onChange={value => {
                  setPresenceRatio(value);
                }}
                startLabel='-2'
                endLabel='2'
                width='100%'
                showTextField={true}
                disabled={!presenceChecked}
                decimalPlaces={1}
                step={0.1}
              />
            </div>
          </div>
        </div>

        {/* Column */}
        <div className='flex flex-col gap-4'>
          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
            Frequency Penalty
          </UITypography>
          <div className='flex w-full'>
            <div className=''>
              <UIToggle
                size='medium'
                checked={frequencyChecked}
                onChange={() => {
                  setFrequencyChecked(!frequencyChecked);
                }}
              />
            </div>
            <div className='pl-8 pr-6 flex-1'>
              <UISlider
                required={true}
                value={frequencyRatio}
                min={-2}
                max={2}
                onChange={value => {
                  setFrequencyRatio(value);
                }}
                startLabel='-2'
                endLabel='2'
                width='100%'
                showTextField={true}
                disabled={!frequencyChecked}
                decimalPlaces={1}
                step={0.1}
              />
            </div>
          </div>
        </div>

        {/* Column */}
        <div className='flex flex-col gap-4'>
          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
            Max Token
          </UITypography>
          <div className='flex w-full'>
            <div className=''>
              <UIToggle
                size='medium'
                checked={maxTokenChecked}
                onChange={() => {
                  setMaxTokenChecked(!maxTokenChecked);
                }}
              />
            </div>
            <div className='pl-8 pr-6 flex-1'>
              <UISlider
                required={true}
                value={maxTokenRatio}
                min={1}
                max={4096}
                onChange={value => {
                  setMaxTokenRatio(value);
                }}
                startLabel='1'
                endLabel='4096'
                width='100%'
                showTextField={true}
                disabled={!maxTokenChecked}
                decimalPlaces={0}
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
});
