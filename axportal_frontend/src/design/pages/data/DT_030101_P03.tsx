import React, { useState } from 'react';
import { UITypography, UIButton2, UIRadio2, UIFileBox } from '@/components/UI/atoms';
import { UIPopupHeader, UIPopupFooter, UIArticle, UIPopupBody, UIFormField, UIInput, UIGroup, UIList } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { DesignLayout } from '../../components/DesignLayout';
import { UIUnitGroup } from '@/components/UI/molecules';
import { UICode } from '@/components/UI/atoms/UICode';

export const DT_030101_P03: React.FC = () => {
  const [isPopupOpen] = useState(true); // 팝업이므로 항상 열려있음

  // 폼 데이터 상태
  const [formData, setFormData] = useState({
    typeValue: '',
    description: '',
  });

  // 파일 목록 상태
  const [files, setFiles] = useState<string[]>(['Summary_train_1st.xml']);

  // Script 유형 상태 (Code/File)
  const [scriptType, setScriptType] = useState('Code');

  const handleFileRemove = (index: number) => {
    setFiles((prev: any) => prev.filter((_: any, i: any) => i !== index));
    // setFileToggles(prev => prev.filter((_, i) => i !== index));
  };

  const handleInputChange = (field: string, value: string) => {
    setFormData(prev => ({
      ...prev,
      [field]: value,
    }));
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
              <UIPopupHeader
                title={
                  <>
                    Custom Script
                    <br />
                    만들기
                  </>
                }
                description=''
                position='left'
              />
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
                    <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={true}>
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
          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            {/* 유형 선택 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UIUnitGroup gap={0} direction='row' vAlign='center' align='space-between'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                    유형 선택
                  </UITypography>
                  <UIButton2 className='btn-tertiary-sky-blue w-[96px] btn-semibold'>미리보기</UIButton2>
                </UIUnitGroup>
                <UIUnitGroup gap={12} direction='column' align='start'>
                  <UIRadio2 name='basic1' value='option1' label='Loader' />
                  <UIRadio2 name='basic1' value='option2' label='Chunking' />
                </UIUnitGroup>
              </UIFormField>
            </UIArticle>

            {/* 이름 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  이름
                </UITypography>
                <div>
                  <UIInput.Text
                    value={formData.typeValue}
                    placeholder='이름 입력'
                    onChange={e => {
                      handleInputChange('typeValue', e.target.value);
                    }}
                  />
                </div>
              </UIFormField>
            </UIArticle>

            {/* 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  설명
                </UITypography>
                <div>
                  <UIInput.Text
                    value={formData.description}
                    placeholder='설명 입력'
                    onChange={e => {
                      handleInputChange('description', e.target.value);
                    }}
                  />
                </div>
              </UIFormField>
            </UIArticle>

            {/* 입력 필드 */}
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  Script
                </UITypography>
                <div>
                  <UIUnitGroup gap={12} direction='column' align='start'>
                    <UIRadio2 name='basic2' value='Code' label='Code' checked={scriptType === 'Code'} onChange={() => setScriptType('Code')} />
                    <UIRadio2 name='basic2' value='File' label='File' checked={scriptType === 'File'} onChange={() => setScriptType('File')} />
                  </UIUnitGroup>
                </div>
              </UIFormField>

              {/* Code 선택 시: 에디터 화면 */}
              {scriptType === 'Code' && (
                <div className='mt-[16px]'>
                  <UICode
                    value={'여기는 에디터 화면입니다. 테스트 testtesttesttest'}
                    language='python'
                    theme='dark'
                    width='100%'
                    minHeight='460px'
                    height='460px'
                    readOnly={false}
                  />
                </div>
              )}

              {/* File 선택 시: 파일 업로드 */}
              {scriptType === 'File' && (
                <div className='mt-[16px]'>
                  <UIGroup gap={16} direction='column'>
                    <div>
                      <UIButton2 className='btn-tertiary-outline download' onClick={() => {}}>
                        파일 업로드
                      </UIButton2>
                    </div>
                    <div>
                      {/* 파일 목록 */}
                      {files.length > 0 && (
                        <div className='space-y-3'>
                          {files.map((fileName, index) => (
                            <UIFileBox
                              key={index}
                              variant='default'
                              size='full'
                              fileName={fileName}
                              fileSize={99}
                              onFileRemove={() => handleFileRemove(index)}
                              className='w-full'
                            />
                          ))}
                        </div>
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
                                지원되는 파일 확장자 : .xls, .xlsx, .csv, .xlt, .xltx, .xlsm, .xlsb, .xltm
                              </UITypography>
                            ),
                          },
                        ]}
                      />
                    </div>
                  </UIGroup>
                </div>
              )}
            </UIArticle>
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
