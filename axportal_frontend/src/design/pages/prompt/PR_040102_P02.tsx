import React, { useState } from 'react';

import { UIButton2, UIFileBox, UIRadio2, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIFormField, UIGroup, UIInput, UITextArea2, UIList, UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { DesignLayout } from '../../components/DesignLayout';

export const PR_040102_P02: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true);
  const [, setTextValue] = useState('');
  // 파일 목록 상태
  const [files, setFiles] = useState<string[]>(['Summary_train_1st.xml']);

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  // [251114_퍼블수정] 파일 업로드 영역 수정
  const handleFileRemove = (index?: number) => {
    if (index === undefined) return;
    setFiles((prev: any) => prev.filter((_: any, i: any) => i !== index));
    // setFileToggles(prev => prev.filter((_, i) => i !== index));
  };

  const [tags, setTags] = useState<string[]>([]);

  // textarea 타입
  const [textareaValue, setTextareaValue] = useState(`<workflow name="loan_approval_flow">
      <step id="1" action="validate_identity"/>
      <step id="2" action="check_credit_score"/>
      <step id="3" action="approve_or_reject"/>
  </workflow>
  
  <workflow name="loan_approval_flow">
      <step id="1" action="validate_identity"/>
      <step id="2" action="check_credit_score"/>
      <step id="3" action="approve_or_reject"/>
  </workflow>
  
  <workflow name="loan_approval_flow">
      <step id="1" action="validate_identity"/>
      <step id="2" action="check_credit_score"/>
      <step id="3" action="approve_or_reject"/>
  </workflow>`);

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
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='워크플로우 수정' position='left' />
            {/* 레이어 팝업 바디 */}
            {/* <UIPopupBody></UIPopupBody> */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: 80 }}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: 80 }} disabled={false}>
                    저장
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 - 기존 컴포넌트 사용 */}
        <section className='section-popup-content'>
          {/* 레이어 팝업 헤더 */}
          {/* <UIPopupHeader title='' position='right' /> */}

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle>
              <UIUnitGroup gap={8} direction='column'>
                <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-sb'>
                  이름
                </UITypography>
                <UIInput.Text
                  value={'Test Few Shots'}
                  onChange={e => {
                    setTextValue(e.target.value);
                  }}
                  placeholder='라이센스 입력'
                />
              </UIUnitGroup>
            </UIArticle>

            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <div className='inline-flex items-center'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                    등록 방법 선택
                  </UITypography>
                </div>
                <div>
                  <UIUnitGroup gap={12} direction='column' align='start'>
                    <UIRadio2 name='basic1' value='option1' label='파일 업로드' />
                    <UIRadio2 name='basic1' value='option2' label='직접 입력' />
                  </UIUnitGroup>
                </div>
              </UIFormField>
            </UIArticle>

            <UIArticle>
              <UIGroup gap={16} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  파일 업로드
                </UITypography>
                <div>
                  <UIButton2 className='btn-tertiary-outline download' onClick={() => {}}>
                    파일 업로드
                  </UIButton2>
                </div>
                <div>
                  {/* 파일 목록 */}
                  {/* [251114_퍼블수정] 파일 업로드 영역 수정 */}
                  {files.length > 0 && (
                    <UIFileBox variant='default' size='full' items={files.map(fileName => ({ fileName, fileSize: 99 }))} onFileRemove={handleFileRemove} className='w-full' />
                  )}
                </div>
                <div>
                  <UIList
                    gap={4}
                    direction='column'
                    className='ui-list_bullet'
                    data={[
                      {
                        dataItem: (
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            암호화 설정된 파일은 업로드가 불가능하니 암호화 해제 후 파일 업로드를 해주세요.
                          </UITypography>
                        ),
                      },
                      {
                        dataItem: (
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            지원되는 파일 확장자 : .xml
                          </UITypography>
                        ),
                      },
                    ]}
                  />
                </div>
              </UIGroup>
            </UIArticle>
            {/* 직접 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  직접 입력
                </UITypography>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <div className='flex-1'>
                    <UITextArea2 value={textareaValue} placeholder='' style={{ height: '394px' }} onChange={e => setTextareaValue(e.target.value)} />
                  </div>
                </UIUnitGroup>
              </UIFormField>
            </UIArticle>
            {/* 태그 입력 필드 */}
            <UIArticle>
              <UIInput.Tags tags={tags} onChange={setTags} placeholder='태그 입력' label='태그' />
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          {/* <UIPopupFooter></UIPopupFooter> */}
        </section>
      </UILayerPopup>
    </>
  );
};
