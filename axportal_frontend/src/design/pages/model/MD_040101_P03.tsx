import { useState } from 'react';

import { UISlider, UITypography } from '@/components/UI/atoms';
import { UIToggle } from '@/components/UI';

export const MD_040101_P03 = () => {
  // Temperature 상태
  /* [251110_퍼블수정] 슬라이더 속성값 재정의 */
  const [temperatureRatio, setTemperatureRatio] = useState(0.2);
  const [temperatureChecked, setTemperatureChecked] = useState(false);

  // Top P 상태
  /* [251110_퍼블수정] 슬라이더 속성값 재정의 */
  const [topPRatio, setTopPRatio] = useState(0.5);
  const [topPChecked, setTopPChecked] = useState(false);

  // Presence Penalty 상태
  /* [251110_퍼블수정] 슬라이더 속성값 재정의 */
  const [presenceRatio, setPresenceRatio] = useState(0.3);
  const [presenceChecked, setPresenceChecked] = useState(false);

  // Frequency Penalty 상태
  /* [251110_퍼블수정] 슬라이더 속성값 재정의 */
  const [frequencyRatio, setFrequencyRatio] = useState(0.4);
  const [frequencyChecked, setFrequencyChecked] = useState(false);

  // Max Token 상태
  /* [251110_퍼블수정] 슬라이더 속성값 재정의 */
  const [maxTokenRatio, setMaxTokenRatio] = useState(1000);
  const [maxTokenChecked, setMaxTokenChecked] = useState(false);

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
            {/* [251110_퍼블수정] 슬라이더 속성값 재정의 */}
            <div className='pl-8 flex-1'>
              <UISlider required={true} value={temperatureRatio} min={0} max={10} onChange={setTemperatureRatio} startLabel='0' endLabel='1' width='100%' showTextField={true} />
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
            {/* [251110_퍼블수정] 슬라이더 속성값 재정의 */}
            <div className='pl-8 flex-1'>
              <UISlider required={true} value={topPRatio} min={0} max={10} onChange={setTopPRatio} startLabel='0' endLabel='1' width='100%' showTextField={true} />
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
            {/* [251110_퍼블수정] 슬라이더 속성값 재정의 */}
            <div className='pl-8 flex-1'>
              <UISlider required={true} value={presenceRatio} min={0} max={10} onChange={setPresenceRatio} startLabel='0' endLabel='1' width='100%' showTextField={true} />
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
            {/* [251110_퍼블수정] 슬라이더 속성값 재정의 */}
            <div className='pl-8 flex-1'>
              <UISlider required={true} value={frequencyRatio} min={0} max={10} onChange={setFrequencyRatio} startLabel='0' endLabel='1' width='100%' showTextField={true} />
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
            {/* [251110_퍼블수정] 슬라이더 속성값 재정의 */}
            <div className='pl-8 flex-1'>
              <UISlider required={true} value={maxTokenRatio} min={0} max={2000} onChange={setMaxTokenRatio} startLabel='0' endLabel='2000' width='100%' showTextField={true} />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
