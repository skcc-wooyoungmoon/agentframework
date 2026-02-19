import React, { useState } from 'react';

import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UITypography, UIIcon2 } from '@/components/UI/atoms';
import { UIFormField, UIUnitGroup } from '@/components/UI/molecules';
import { UIRadio2 } from '@/components/UI/atoms/UIRadio2';

export const HM_060101_P02: React.FC = () => {
  const [selectedPeriod, setSelectedPeriod] = useState('3days');
  const handlePeriodChange = (_value: string) => {
    setSelectedPeriod(_value);
  };

  return (
    <section className='section-modal'>
      <UIArticle>
        <div className='box-fill'>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0 6px' }}>
            <UIIcon2 className='ic-system-16-info-gray' />
            <UITypography variant='body-2' className='secondary-neutral-600'>
              원하는 기간을 선택 후 설정 버튼을 클릭해주세요. 해당 기간 만큼 만료 일시가 자동 연장 처리됩니다.
            </UITypography>
          </div>
        </div>
      </UIArticle>
      <UIArticle>
        <div className='article-header'>
          <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
            IDE 정보
          </UITypography>
        </div>
        <div className='article-body'>
          <div className='border-t border-black'>
            <table className='tbl-v'>
              <colgroup>
                <col style={{ width: '152px' }} />
                <col style={{ width: '350px' }} />
                <col style={{ width: '152px' }} />
                <col style={{ width: '350px' }} />
              </colgroup>
              <tbody>
                <tr>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      이미지명
                    </UITypography>
                  </th>
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      Python_v3.12_RAG
                    </UITypography>
                  </td>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      IDE 만료 일시
                    </UITypography>
                  </th>
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      2025.03.24 18:23:43
                    </UITypography>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </UIArticle>

      <UIArticle>
        <UIFormField gap={8} direction='column'>
          <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
            기간 선택
          </UITypography>
          <UIUnitGroup gap={12} direction='column' align='center'>
            <UIRadio2
              name='period'
              label='3일'
              value='3days'
              checked={selectedPeriod === '3days'}
              onChange={(checked, value) => {
                if (checked) handlePeriodChange(value);
              }}
            />
            <UIRadio2
              name='period'
              label='7일'
              value='7days'
              checked={selectedPeriod === '7days'}
              onChange={(checked, value) => {
                if (checked) handlePeriodChange(value);
              }}
            />
            <UIRadio2
              name='period'
              label='14일'
              value='14days'
              checked={selectedPeriod === '14days'}
              onChange={(checked, value) => {
                if (checked) handlePeriodChange(value);
              }}
            />
          </UIUnitGroup>
        </UIFormField>
      </UIArticle>
    </section>
  );
};
