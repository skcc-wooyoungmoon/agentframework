import React, { useState } from 'react';
import { UITypography, UIButton2 } from '@/components/UI/atoms';
import { UIPopupHeader, UIPopupFooter, UIArticle, UIPopupBody, UIFormField, UIInput, UIList } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { DesignLayout } from '../../components/DesignLayout';
import { UIUnitGroup } from '@/components/UI/molecules';
import { UIDropdown } from '../../../components/UI/molecules/dropdown/UIDropdown';

export const AD_130202_P01: React.FC = () => {
  const [isPopupOpen] = useState(true); // 팝업이므로 항상 열려있음

  // text 타입
  // const [textValue, setTextValue] = useState('');

  // 입력예시 섹션 상태
  // const [exampleInputValue, setExampleInputValue] = useState('300');
  // const [exampleDropdownValue, setExampleDropdownValue] = useState('');
  const [isExampleDropdownOpen, setIsExampleDropdownOpen] = useState(false);

  // Quota 섹션 상태
  // const [quotaInputValue, setQuotaInputValue] = useState('300');
  const [, setQuotaDropdownValue] = useState('');
  const [isQuotaDropdownOpen, setIsQuotaDropdownOpen] = useState(false);

  // 입력예시 드롭다운 핸들러
  const handleExampleDropdownToggle = () => {
    setIsExampleDropdownOpen(!isExampleDropdownOpen);
  };

  const handleExampleDropdownSelect = (_value: string) => {
    // setExampleDropdownValue(value);
    setIsExampleDropdownOpen(false);
  };

  // Quota 드롭다운 핸들러
  const handleQuotaDropdownToggle = () => {
    setIsQuotaDropdownOpen(!isQuotaDropdownOpen);
  };

  const handleQuotaDropdownSelect = (_value: string) => {
    setQuotaDropdownValue(_value);
    setIsQuotaDropdownOpen(false);
  };

  const handleClose = () => {
    // 팝업 닫기 동작 제거 (디자인 페이지이므로 항상 열려있음)
    // 
  };

  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'admin', label: '관리' }}
        initialSubMenu={{
          id: 'admin-roles',
          label: '역할 관리',
          icon: 'ico-lnb-menu-20-admin-role',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              기본 정보
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              지식 만들기 진행 중...
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
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='Quota 수정' description='' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              <UIArticle>{/* 바디 영역 */}</UIArticle>
            </UIPopupBody>
            {/* 레이어 팝업 바디 : [참고] 이 페이지에는 왼쪽 body 영역 없음. */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={false}>
                    저장
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
          {/* 레이어 팝업 헤더 */}
          {/*  [251106_퍼블수정] 텍스트 수정 */}
          <UIPopupHeader title='Quota 수정' position='right' description='단위시간별 Quota를 수정할 수 있습니다.' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            {/* 입력폼 */}
            <UIArticle>
              <div className='card-form-box'>
                <UIFormField gap={16} direction='column'>
                  <UITypography variant='title-4' className='secondary-neutral-800 !text-lg text-sb' required={false}>
                    입력예시
                  </UITypography>
                  <UIUnitGroup gap={16} direction='row'>
                    <UIFormField gap={8} direction='column'>
                      <UITypography variant='body-1' className='secondary-neutral-800 text-sb'>
                        김신한
                      </UITypography>
                      <UIUnitGroup gap={16} direction='row' vAlign='center'>
                        <div className='flex-1'>
                          <UIInput.Text
                            value={'300'}
                            disabled={true}
                            onChange={_e => {
                              // setExampleInputValue(_e.target.value);
                            }}
                          />
                        </div>
                        <UITypography variant='body-1' className='secondary-neutral-800 text-sb'>
                          회
                        </UITypography>
                        <div>/</div>
                        <div className='flex-1'>
                          <UIDropdown
                            value={'일'}
                            placeholder='일'
                            options={[
                              { value: '일', label: '일' },
                              { value: '월', label: '월' },
                              { value: '년', label: '년' },
                            ]}
                            disabled={true}
                            isOpen={isExampleDropdownOpen}
                            onClick={handleExampleDropdownToggle}
                            onSelect={handleExampleDropdownSelect}
                          />
                        </div>
                      </UIUnitGroup>

                      <div>
                        <UIList
                          gap={4}
                          direction='column'
                          className='ui-list_bullet'
                          data={[
                            {
                              dataItem: (
                                <UITypography variant='body-2' className='secondary-neutral-600'>
                                  호출 가능 건수를 1일 기준 300회로 설정하고 싶은 경우, 다음과 같이 설정할 수 있습니다. (300회/일)
                                </UITypography>
                              ),
                            },
                          ]}
                        />
                      </div>
                    </UIFormField>
                  </UIUnitGroup>
                </UIFormField>
              </div>
            </UIArticle>

            {/* 입력폼 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  Quota
                </UITypography>
                <UIFormField gap={8} direction='column'>
                  <UITypography variant='body-1' className='secondary-neutral-800 text-sb'>
                    김수민 (23023463)
                  </UITypography>
                  <UIUnitGroup gap={16} direction='row' vAlign='center'>
                    <div className='flex-1'>
                      <UIInput.Text
                        value={'20'}
                        onChange={_e => {
                          // setQuotaInputValue(_e.target.value);
                        }}
                      />
                    </div>
                    <UITypography variant='body-1' className='secondary-neutral-800 text-sb'>
                      회
                    </UITypography>
                    <div>/</div>
                    <div className='flex-1'>
                      {/* 25110_퍼블 속성값 수정 */}
                      <UIDropdown
                        value={'시'}
                        placeholder='일'
                        options={[
                          { value: '일', label: '일' },
                          { value: '월', label: '월' },
                          { value: '년', label: '년' },
                        ]}
                        isOpen={isQuotaDropdownOpen}
                        onClick={handleQuotaDropdownToggle}
                        onSelect={handleQuotaDropdownSelect}
                      />
                    </div>
                  </UIUnitGroup>
                </UIFormField>
              </UIFormField>
            </UIArticle>
          </UIPopupBody>
        </section>
      </UILayerPopup>
    </>
  );
};
