import React, { useState } from 'react';

import { UIButton2, UIRadio2 } from '@/components/UI/atoms';
import { UIPopupHeader, UIPopupFooter, UIArticle, UIPopupBody, UIGroup, UIList, UIDropdown } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
// import { UIToggle } from '@/components/UI';
import { UIStepper } from '@/components/UI/molecules';
import { UIInput, UITextArea2 } from '@/components/UI/molecules';
import { DesignLayout } from '../../components/DesignLayout';
import { UIUnitGroup, UIFormField } from '@/components/UI/molecules';
import { UITypography } from '@/components/UI/atoms';
import { UICode } from '@/components/UI/atoms/UICode';

export const DP_010101_P03: React.FC = () => {
  const [isPopupOpen] = useState(true); // 팝업이므로 항상 열려있음
  // const [advancedChecked, setTemperatureChecked] = useState(false);

  // 스테퍼 데이터
  const stepperItems = [
    { step: 1, label: '모델 선택' },
    { step: 2, label: '배포 정보 입력' },
    { step: 3, label: '자원 할당' },
  ];

  const handleClose = () => {
    // 팝업 닫기 동작 제거 (디자인 페이지이므로 항상 열려있음)
    //
  };

  // text 타입
  const [textValue, setTextValue] = useState('');

  // textarea 타입
  const [textareaValue, setTextareaValue] = useState('');

  // title 타입
  const [titleValue, setTitleValue] = useState('');

  // dropdown 타입
  const [dataset, setDataset] = useState('bai-repo:7080/bai/vllm:0.10.1-cuda12.8-ubuntu24.04');

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
              모델 배포하기 진행 중...
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
            <UIPopupHeader title='모델 배포하기' description='' position='left' />
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={2} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>
            {/* 레이어 팝업 바디 : [참고] 이 페이지에는 왼쪽 body 영역 없음. */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={true}>
                    배포
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
          {/* [251120_퍼블수정] 검수요청 현행화 수정 */}
          <UIPopupHeader
            title='배포 정보 입력'
            description='배포 정보를 입력 해주세요. 이미 등록된 배포명이나, 삭제 처리 중인 배포명과 동일한 이름은 중복으로 인식되어 등록할 수 없습니다.'
            position='right'
          />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            {/* 배포명 입력 필드 */}
            <UIArticle>
              {/* 251106 텍스트 및 마크업 수정 */}
              <UIFormField gap={8} direction='column'>
                <UIGroup gap={8} direction='column'>
                  <UITypography variant='title-3' className='secondary-neutral-900'>
                    배포명
                  </UITypography>
                  <UITypography variant='body-2' className='secondary-neutral-600'>
                    영문 및 숫자와 '-', '_', '/' 또는 '.'만 사용할 수 있으며 영문 또는 숫자로 시작하고 끝나도록 입력되어야합니다.
                  </UITypography>
                </UIGroup>
                <div>
                  <UIInput.Text
                    value={textValue}
                    onChange={e => {
                      setTextValue(e.target.value);
                    }}
                    placeholder='배포명 입력'
                  />
                </div>
              </UIFormField>
            </UIArticle>

            {/* 설명 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                  설명
                </UITypography>
                <UITextArea2 value={textareaValue} onChange={e => setTextareaValue(e.target.value)} placeholder='설명을 입력해주세요.' maxLength={100} />
              </UIFormField>
            </UIArticle>

            {/* 타이틀 노출 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  타이틀 노출
                </UITypography>
                <UITypography variant='body-2' className='secondary-neutral-600'>
                  설명글 노출
                </UITypography>
                <UIInput.Text value={titleValue} onChange={e => setTitleValue(e.target.value)} placeholder='설명을 입력해주세요.' />
              </UIFormField>
            </UIArticle>

            <UIArticle>
              {/* [251120_퍼블수정] 검수요청 현행화 수정 */}
              <div className='article-body'>
                <UIFormField gap={12} direction='column'>
                  <UIGroup gap={8} direction='column'>
                    <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                      프레임워크
                    </UITypography>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      SGlang 선택시 모델 추론 성능 모니터링이 제공되지 않습니다.
                    </UITypography>
                  </UIGroup>

                  <UIUnitGroup gap={12} direction='column' align='start'>
                    <UIRadio2 name='basic1' value='option1' label='vLLM' />
                    <UIRadio2 name='basic1' value='option2' label='SGLang' />
                  </UIUnitGroup>
                </UIFormField>
              </div>
            </UIArticle>

            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UIGroup gap={0} direction='row'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                    vLLM 버전
                  </UITypography>
                </UIGroup>
                <UIDropdown
                  value={dataset}
                  options={[
                    { value: '1', label: 'bai-repo:7080/bai/vllm:0.10.1-cuda12.8-ubuntu24.04' },
                    { value: '2', label: '$관리자가 table에 적재해 둔 이미지 명으로 노출$' },
                    { value: '3', label: '$관리자가 table에 적재해 둔 이미지 명으로 노출$' },
                  ]}
                  onClick={() => {}}
                  onSelect={(value: string) => setDataset(value)}
                  placeholder='vLLM 버전 버전 선택'
                />
              </UIFormField>
            </UIArticle>

            <UIArticle>
              <UIGroup gap={8} direction='column'>
                <UITypography variant='title-3' className='secondary-neutral-900'>
                  세이프티 필터
                </UITypography>
                <UITypography variant='body-2' className='secondary-neutral-600'>
                  아래에서 별도로 필터를 선택하지 않은 경우, 기본 필터링 기능만 자동으로 적용됩니다.
                </UITypography>
              </UIGroup>
            </UIArticle>

            {/* 입력 필터 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  입력 필터
                </UITypography>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <div className='flex-1'>
                    <UIInput.Text value={'세이프티 필터 선택'} onChange={() => {}} placeholder='세이프티 필터 선택' />
                  </div>
                  <div>
                    <UIButton2 className='btn-secondary-outline !min-w-[64px]'>선택</UIButton2>
                  </div>
                </UIUnitGroup>
              </UIFormField>
            </UIArticle>

            {/* 출력 필터 입력 필드 */}
            {/* [251120_퍼블수정] 검수요청 현행화 수정 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  출력 필터
                </UITypography>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <div className='flex-1'>
                    <UIInput.Text value={'출력 필터 선택'} onChange={() => {}} placeholder='출력 필터 선택' />
                  </div>
                  <div>
                    <UIButton2 className='btn-secondary-outline !min-w-[64px]'>선택</UIButton2>
                  </div>
                </UIUnitGroup>
                <UIList
                  gap={4}
                  direction='column'
                  className='ui-list_bullet'
                  data={[
                    {
                      dataItem: (
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          스트리밍 출력에 대해서는 출력 필터 지원이 불가능합니다.
                        </UITypography>
                      ),
                    },
                  ]}
                />
              </UIFormField>
            </UIArticle>

            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                  Advanced Setting
                </UITypography>
                {/* [251127_퍼블수정] 토글 삭제 및 현행화 수정 */}
                <UIUnitGroup direction='row' align='space-between' gap={0}>
                  <UITypography variant='body-2' className='secondary-neutral-600'>
                    아래에서 별도로 Advanced setting을 수정하지 않을 경우, 기본 Advanced setting값으로 적용됩니다.
                  </UITypography>
                  <UIButton2 className='btn-option-outlined'>초기화</UIButton2>
                </UIUnitGroup>
                {/* 소스코드 영역 */}
                <UICode value={'여기는 에디터 화면입니다. 테스트 테스트'} language='python' theme='dark' width='100%' minHeight='272px' maxHeight='272px' readOnly={true} />
              </UIFormField>
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray'>이전</UIButton2>
                <UIButton2 className='btn-secondary-blue'>다음</UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
