import React, { useState } from 'react';
import { DesignLayout } from '../../components/DesignLayout';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UIButton2, UICheckbox2, UIIcon2, UITooltip, UIToggle } from '@/components/UI/atoms';
import { UIPopupHeader, UIPopupFooter, UIArticle, UIPopupBody, UIFormField, UIGroup } from '@/components/UI/molecules';
import { UIUnitGroup } from '@/components/UI/molecules';
import { UITypography } from '@/components/UI/atoms';

import { UIStepper, UIDropdown } from '@/components/UI/molecules';

export const DT_020101_P07: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  // 스테퍼 데이터
  const stepperItems = [
    { step: 1, label: '지식 기본 설정' },
    { step: 2, label: '데이터 선택' },
    { step: 3, label: '선택 데이터 확인' },
    { step: 4, label: '청킹 설정' },
    { step: 5, label: '임베딩 설정' },
    { step: 6, label: '지식 등록' },
  ];

  const [checked1, setChecked1] = useState(false);

  const [selectedValues, setSelectedValues] = useState<string[]>([]);

  // const [isPopupOpen] = useState(true); // 팝업이므로 항상 열려있음
  const [isSupplierDropdownOpen, setIsSupplierDropdownOpen] = useState(false);

  // 드롭다운 핸들러
  const handleSupplierSelect = (_value: string) => {
    setIsSupplierDropdownOpen(false);
  };

  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'data', label: '데이터' }}
        initialSubMenu={{
          id: 'data-tools',
          label: '데이터도구',
          icon: 'ico-lnb-menu-20-data-storage',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              데이터 도구
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              Ingestion Tool 만들기 진행 중...
            </UITypography>
          </div>
        </div>
      </DesignLayout>

      {/* DesignLayout 위에 높은 z-index로 뜨는 UILayerPopup */}
      <UILayerPopup
        isOpen={isPopupOpen}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          // [251120_퍼블수정] 검수요청 현행화 수정
          <UIPopupAside>
            {/* 데이터 도구 만들기 제목 */}
            <UIPopupHeader title='지식 생성' description='' position='left' />

            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={5} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>

            {/* 레이어 팝업 바디 : [참고] 이 페이지에는 왼쪽 body 영역 없음. */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled>
                    만들기
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 */}
        {/* 콘텐츠 영역 */}
        <section className='section-popup-content'>
          <UIPopupHeader title='임베딩 설정' description='청킹 완료된 데이터를 임베딩할 모델과 벡터DB를 설정해주세요.' position='right' />
          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  임베딩 모델
                </UITypography>
                <UIDropdown
                  required={true}
                  value={'비정형 임베딩모델'}
                  isOpen={isSupplierDropdownOpen}
                  onClick={() => setIsSupplierDropdownOpen(!isSupplierDropdownOpen)}
                  onSelect={handleSupplierSelect}
                  options={[
                    { value: 'system', label: '[AzureAISearch][Shared] axplatform-ai-search-dev' },
                    { value: 'custom', label: '111' },
                  ]}
                />
              </UIFormField>
            </UIArticle>

            <UIArticle>
              {/* [251120_퍼블수정] 부연설명 추가 */}
              <UIFormField gap={8} direction='column'>
                <UIGroup direction='column' gap={4}>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                    벡터DB
                  </UITypography>
                  <UITypography variant='body-2' className='secondary-neutral-600'>
                    올바른 벡터DB를 선택해야 지식이 정상적으로 동작합니다. 기본 지식과 사용자 정의 지식은 사용하는 벡터DB가 다를 수 있으니, 선택한 벡터DB를 한 번 더 확인해주세요.
                  </UITypography>
                </UIGroup>

                <UIDropdown
                  required={true}
                  value={'[비정형데이터플랫폼] Elasticsearch'}
                  isOpen={isSupplierDropdownOpen}
                  onClick={() => setIsSupplierDropdownOpen(!isSupplierDropdownOpen)}
                  onSelect={handleSupplierSelect}
                  options={[
                    { value: 'system', label: '[비정형데이터플랫폼] Elasticsearch' },
                    { value: 'custom', label: '222' },
                  ]}
                />
              </UIFormField>
            </UIArticle>

            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <div>
                  <UIGroup gap={4} direction={'column'}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '2px' }}>
                      <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                        동기화 여부
                      </UITypography>
                      <UITooltip
                        trigger='click'
                        position='bottom-start'
                        type='notice'
                        title='학습데이터 파일 양식'
                        items={[
                          '비정형데이터플랫폼의 원천 MD데이터가 변경시 해당 지식 REPO내의 지식 데이터 동기화 여부를 선택해주세요.',
                          '한 번 설정한 동기화 여부는 수정할 수 없습니다.',
                        ]}
                        bulletType='dash' // [251217_퍼블수정] dash 대신 default 변경
                        showArrow={false}
                        showCloseButton={true}
                        className='tooltip-wrap ml-1'
                      >
                        <UIButton2 className='btn-ic'>
                          <UIIcon2 className='ic-system-20-info' />
                        </UIButton2>
                      </UITooltip>
                    </div>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      동기화 선택시 해당 지식 Repo는 실시간으로 데이터 동기화를 진행합니다.
                    </UITypography>
                  </UIGroup>
                </div>
                <div>
                  <UIUnitGroup gap={0}>
                    <UIToggle
                      size='medium'
                      checked={checked1}
                      onChange={() => {
                        setChecked1(!checked1);
                        // 개발자 로직 처리
                      }}
                    />
                  </UIUnitGroup>
                </div>
              </UIFormField>
            </UIArticle>

            {checked1 && (
              <UIArticle>
                <UIFormField gap={12} direction='column'>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '2px' }}>
                    <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                      동기화 대상
                    </UITypography>
                  </div>
                  <div>
                    <UIUnitGroup gap={12} direction='column' align='start' className='group-radio'>
                      <UICheckbox2
                        name='basic6'
                        value='option1'
                        label='개발계'
                        className='chk box'
                        checked={selectedValues.includes('option1')}
                        onChange={(checked, value) => {
                          if (checked) {
                            setSelectedValues([...selectedValues, value]);
                          } else {
                            setSelectedValues(selectedValues.filter(v => v !== value));
                          }
                        }}
                      />
                      <UICheckbox2
                        name='basic6'
                        value='option2'
                        label='운영계'
                        className='chk box'
                        checked={selectedValues.includes('option2')}
                        onChange={(checked, value) => {
                          if (checked) {
                            setSelectedValues([...selectedValues, value]);
                          } else {
                            setSelectedValues(selectedValues.filter(v => v !== value));
                          }
                        }}
                      />
                    </UIUnitGroup>
                  </div>
                </UIFormField>
              </UIArticle>
            )}
          </UIPopupBody>

          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray'>이전</UIButton2>
                {/* <UIButton2 className='btn-secondary-blue'>다음</UIButton2> */}
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
