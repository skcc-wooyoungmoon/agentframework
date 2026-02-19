import React, { useState } from 'react';

import { UIFileBox, UIToggle, UIButton2, UITypography } from '@/components/UI/atoms';
import { UIDropdown, UIInput, UITextArea2, UIFormField, UIArticle, UIPopupFooter, UIPopupHeader, UIPopupBody, UIUnitGroup, UIList, UIGroup } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { DesignLayout } from '../../components/DesignLayout';

export const AD_090102_P01: React.FC = () => {
  const [isPopupOpen, setIsPopupOpen] = useState(true); // 팝업이므로 기본적으로 열려있음

  // 검색 상태
  const [searchText, setSearchText] = useState('8월 5일(월) 정기 시스템 점검 예정 안내');
  const [contentText, setContentText] = useState(`안정적인 서비스 운영을 위한 정기 점검이 다음과 같이 진행될 예정입니다.
점검 일시: 2025년 8월 5일(월) 오전 2시 ~ 오전 5시
점검 대상: 전체 포탈 서비스 (웹 UI, API 등)
영향 범위: 점검 시간 동안 서비스 이용이 불가능합니다.`);

  // 파일 상태
  const [files, setFiles] = useState<string[]>(['test-file.pdf']);

  // 날짜/시간 상태
  const [startHour] = useState('00');
  const [startMinute] = useState('00');
  const [endHour] = useState('00');
  const [endMinute] = useState('00');

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
            <UIPopupHeader title='공지사항 수정' description='' position='left' />
            <UIPopupBody>
              <UIArticle>{/* 좌측 스테퍼 영역 콘텐츠 */}</UIArticle>
            </UIPopupBody>
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={false}>
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
          <UIPopupHeader title='공지사항 수정' description='등록한 공지사항의 제목, 유형, 내용 등을 수정할 수 있습니다.' position='right' />
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
                  value='시스템 점검'
                  placeholder='유형 선택'
                  isOpen={false}
                  disabled={false}
                  onClick={() => {}}
                  onSelect={() => {}}
                  options={[
                    { value: '공지사항', label: '공지사항' },
                    { value: '이벤트', label: '이벤트' },
                    { value: '안내', label: '안내' },
                    { value: '시스템 점검', label: '시스템 점검' },
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
                <UITextArea2 value={contentText} placeholder='내용 입력' onChange={e => setContentText(e.target.value)} maxLength={4000} disabled={false} />
              </UIFormField>
            </UIArticle>
            <UIArticle>
              {/* 파일 업로드 섹션 */}
              <UIGroup direction='column' gap={16}>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                  파일 업로드
                </UITypography>
                <UIButton2 className='btn-tertiary-outline download' style={{ width: '112px' }} onClick={() => {}}>
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
                  사용 여부
                </UITypography>
                <div className='flex'>
                  {/* 게시 기간 토글 */}
                  <UIToggle checked={true} onChange={_checked => {}} />
                </div>
                <div>
                  {/* 날짜 및 시간 선택 */}
                  <div className='flex items-center gap-4'>
                    {/* 시작 날짜/시간 */}
                    <div className='flex items-center gap-4'>
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
                          value={startHour}
                          isOpen={false}
                          disabled={false}
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
                          value={startMinute}
                          isOpen={false}
                          disabled={false}
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
                          value={endHour}
                          isOpen={false}
                          disabled={false}
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
                          value={endMinute}
                          isOpen={false}
                          disabled={false}
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
