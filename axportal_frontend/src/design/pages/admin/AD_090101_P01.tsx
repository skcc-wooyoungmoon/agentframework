import React, { useState } from 'react';

import { UIButton2, UIFileBox, UIToggle, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIDropdown, UIFormField, UIGroup, UIInput, UIList, UIPopupBody, UIPopupFooter, UIPopupHeader, UITextArea2, UIUnitGroup } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { useModal } from '@/stores/common/modal';
import { DesignLayout } from '../../components/DesignLayout';

export const AD_090101_P01: React.FC = () => {
  const {} = useModal();

  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음

  // 검색 상태
  const [searchText, setSearchText] = useState('');

  // 파일 상태
  const [files, setFiles] = useState<string[]>(['test-file.pdf']);

  // 토글
  const [checked1, setChecked1] = useState(false);

  // date 타입
  const [startDateValue, setStartDateValue] = useState('2025.06.29');
  const [endDateValue, setEndDateValue] = useState('2025.06.29');

  const handleClose = () => {
    setIsPopupOpen(false);
  };

  // [251114_퍼블수정] 파일 업로드 영역 수정
  const handleFileRemove = (index?: number) => {
    if (index === undefined) return;
    setFiles(prev => prev.filter((_, i) => i !== index));
  };

  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'admin', label: '관리' }}
        initialSubMenu={{
          id: 'admin-tools',
          label: '관리도구',
          icon: 'ico-lnb-menu-20-admin',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              관리 도구
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              관리 Tool 만들기 진행 중...
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
            <UIPopupHeader title='새 공지 등록하기' description='' position='left' />
            <UIPopupBody>
              <UIArticle>{/* 좌측 스테퍼 영역 콘텐츠 */}</UIArticle>
            </UIPopupBody>
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
        <section className='section-popup-content'>
          <UIPopupHeader title='새 공지 등록하기' description='포탈 내 사용자에게 안내할 공지사항을 등록할 수 있습니다.' position='right' />
          <UIPopupBody>
            <UIArticle>
              {/*  [251105_퍼블수정] 속성값 수정 */}
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  제목
                </UITypography>
                <UIInput.Text value={searchText} placeholder='제목 입력' onChange={e => setSearchText(e.target.value)} disabled={false} />
              </UIFormField>
            </UIArticle>
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  유형
                </UITypography>
                <UIDropdown
                  value=''
                  placeholder='유형 선택'
                  isOpen={false}
                  disabled={false}
                  onClick={() => {}}
                  onSelect={() => {}}
                  options={[
                    { value: '공지사항', label: '공지사항' },
                    { value: '이벤트', label: '이벤트' },
                    { value: '안내', label: '안내' },
                  ]}
                  height={48}
                />
              </UIFormField>
            </UIArticle>

            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  내용
                </UITypography>
                {/*  [251105_퍼블수정] 속성값 수정 */}
                <UITextArea2 value={searchText} placeholder='내용 입력' onChange={e => setSearchText(e.target.value)} maxLength={4000} disabled={false} />
              </UIFormField>
            </UIArticle>
            <UIArticle>
              {/* 파일 업로드 섹션 */}
              <UIGroup direction='column' gap={16}>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                  파일 업로드
                </UITypography>
                {/* [251105_퍼블수정] : 버튼 className = flex-none w-[112px] 클래스 추가 */}
                <UIButton2 className='btn-tertiary-outline download flex-none w-[112px]' onClick={() => {}}>
                  파일 업로드
                </UIButton2>

                {/* 파일 목록 */}
                {/* [251114_퍼블수정] 파일 업로드 영역 수정 */}
                {files.length > 0 && (
                  <UIFileBox
                    variant='default'
                    size='full'
                    items={files.map(fileName => ({
                      fileName,
                      fileSize: 99,
                      // [251119_퍼블수정] : guideMessage - 문구는 퍼블화면에 강제노출하여 확인하는 용도로 사용됩니다.
                      guideMessage: '• 지원되는 파일 확장자 : .ppt, .pptx, .pdf, .doc, .docx, .xls, .xlsx, .png, .jpg, .jpeg, .txt, .zip',
                    }))}
                    onFileRemove={handleFileRemove}
                    className='w-full'
                  />
                )}
              </UIGroup>
            </UIArticle>
            <UIArticle>
              <UIFormField gap={12} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  게시 기간 설정
                </UITypography>
                <div className='flex' style={{ marginBottom: '4px' }}>
                  {/* 게시 기간 토글 */}
                  <UIToggle
                    size='medium'
                    checked={checked1}
                    onChange={() => {
                      setChecked1(!checked1);
                      // 개발자 로직 처리
                    }}
                  />
                </div>
                <div style={{ marginBottom: '4px' }}>
                  {/* 날짜 및 시간 선택 */}
                  <div className='flex items-center gap-4'>
                    {/* 시작 날짜/시간 */}
                    <div className='flex items-center gap-4'>
                      {/* 캘린더 노출위치 수정 */}
                      <div className='flex-1 w-[200px]'>
                        <UIInput.Date
                          value={startDateValue}
                          onChange={e => {
                            setStartDateValue(e.target.value);
                          }}
                          calendarPosition='left'
                          readOnly={false}
                        />
                        {/* [251105_퍼블수정] : UIInput.Date = readOnly={false} 추가 공지사항 관련부분만 */}
                      </div>

                      <div className='flex items-center gap-2'>
                        <UIDropdown
                          value='00'
                          isOpen={false}
                          disabled={true}
                          onClick={() => {}}
                          onSelect={() => {}}
                          options={Array.from({ length: 24 }, (_, i) => ({
                            value: String(i).padStart(2, '0'),
                            label: String(i).padStart(2, '0'),
                          }))}
                          width='w-[100px]'
                          height={48}
                        />
                        <UITypography variant='body-1' className='secondary-neutral-800'>
                          :
                        </UITypography>
                        <UIDropdown
                          value='00'
                          isOpen={false}
                          disabled={true}
                          onClick={() => {}}
                          onSelect={() => {}}
                          options={[
                            { value: '00', label: '00' },
                            { value: '10', label: '10' },
                            { value: '20', label: '20' },
                            { value: '30', label: '30' },
                            { value: '40', label: '40' },
                            { value: '50', label: '50' },
                          ]}
                          width='w-[100px]'
                          height={48}
                        />
                      </div>
                    </div>

                    {/* 구분자 */}
                    <UITypography variant='body-1' className='secondary-neutral-800'>
                      ~
                    </UITypography>

                    {/* 종료 날짜/시간 */}
                    <div className='flex items-center gap-4'>
                      <div className='flex-1 w-[200px]'>
                        <UIInput.Date
                          value={endDateValue}
                          onChange={e => {
                            setEndDateValue(e.target.value);
                          }}
                          readOnly={false}
                        />
                        {/* [251105_퍼블수정] : UIInput.Date = readOnly={false} 추가 공지사항 관련부분만 */}
                      </div>

                      <div className='flex items-center gap-2'>
                        <UIDropdown
                          value='00'
                          isOpen={false}
                          disabled={true}
                          onClick={() => {}}
                          onSelect={() => {}}
                          options={Array.from({ length: 24 }, (_, i) => ({
                            value: String(i).padStart(2, '0'),
                            label: String(i).padStart(2, '0'),
                          }))}
                          width='w-[100px]'
                          height={48}
                        />
                        <UITypography variant='body-1' className='secondary-neutral-800'>
                          :
                        </UITypography>
                        <UIDropdown
                          value='00'
                          isOpen={false}
                          disabled={true}
                          onClick={() => {}}
                          onSelect={() => {}}
                          options={[
                            { value: '00', label: '00' },
                            { value: '10', label: '10' },
                            { value: '20', label: '20' },
                            { value: '30', label: '30' },
                            { value: '40', label: '40' },
                            { value: '50', label: '50' },
                          ]}
                          width='w-[100px]'
                          height={48}
                        />
                      </div>
                    </div>
                  </div>
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
                            게시 여부를 활성화하지 않은 경우, 해당 공지사항은 임시 저장처리 됩니다.
                          </UITypography>
                        ),
                      },
                    ]}
                  />
                </div>
              </UIFormField>
            </UIArticle>
          </UIPopupBody>
        </section>
      </UILayerPopup>
    </>
  );
};
