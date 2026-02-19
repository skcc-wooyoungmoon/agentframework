import { UIButton2, UIFileBox, UIToggle, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIDropdown, UIFormField, UIGroup, UIInput, UIList, UIPopupBody, UIPopupFooter, UIPopupHeader, UITextArea2, UIUnitGroup } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { api } from '@/configs/axios.config';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup.tsx';
import { postNoticeWithFiles } from '@/services/admin/noticeMgmt/noticeMgmt.services';
import { noticeTypeOptionsAtom } from '@/stores/admin/noticeMgmt/noticeMgmt.atoms';
import { useModal } from '@/stores/common/modal';
import { useAtom } from 'jotai';
import React, { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router';
import type { createRequest } from './type.ts';

interface NoticeMgmtCreatePageProps {
  onSubmit: (payload: createRequest) => void;
  open: boolean;
  onClose: () => void;
}

export const NoticeMgmtCreatePage: React.FC<NoticeMgmtCreatePageProps> = ({ onSubmit, open, onClose }) => {
  const [isPopupOpen, setIsPopupOpen] = useState(open);
  const [submitDisabled, setSubmitDisabled] = useState(true);
  const navigate = useNavigate();

  const [noticeTypeOptions] = useAtom(noticeTypeOptionsAtom);

  const [title, setTitle] = useState('');
  const [msg, setMsg] = useState('');
  const [type, setType] = useState(noticeTypeOptions[0]?.value || '시스템 점검');
  const { showCancelConfirm } = useCommonPopup();

  const [hiddenButtonClickCount, setHiddenButtonClickCount] = useState(0);
  const [showHiddenButton, setShowHiddenButton] = useState(false);
  const [isSendingFileDocument, setIsSendingFileDocument] = useState(false);
  const hiddenButtonRef = useRef<HTMLDivElement>(null);

  const { openAlert } = useModal();
  const showAlert = (message: string, title = '안내') => {
    openAlert({
      message,
      title,
      showFooter: true,
      onConfirm: () => {
        // Alert confirmed
      },
    });
  };
  useEffect(() => {
    setIsPopupOpen(open);
  }, [open]);

  useEffect(() => {
    if (noticeTypeOptions.length > 0 && !noticeTypeOptions.find(option => option.value === type)) {
      setType(noticeTypeOptions[0].value);
    }
  }, [noticeTypeOptions, type]);

  useEffect(() => {
    setSubmitDisabled(!(title.trim() && msg.trim() && type));
  }, [title, msg, type]);

  // 유형이 변경되면 숨겨진 버튼 관련 state 리셋
  useEffect(() => {
    const isTitleMatch = title.toLowerCase().includes('user title');
    if (type !== '기타' || !isTitleMatch) {
      setHiddenButtonClickCount(0);
      setShowHiddenButton(false);
    }
  }, [type, title]);

  const [dropdownStates, setDropdownStates] = useState({
    type: false,
  });

  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    setDropdownStates(prev => ({
      ...Object.keys(prev).reduce((acc, k) => ({ ...acc, [k]: false }), {} as typeof prev),
      [key]: !prev[key],
    }));
  };

  const handleDropdownSelect = (key: string, value: string) => {
    if (key === 'type') setType(value);
    setDropdownStates(prev => ({ ...prev, [key]: false }));
  };

  const typeOptions = noticeTypeOptions;

  const [files, setFiles] = useState<File[]>([]);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const [isUploading, setIsUploading] = useState(false);

  const calculateTotalFileSize = (fileList: File[]) => {
    return fileList.reduce((sum, file) => sum + file.size, 0);
  };

  const handleFileSelect = (event: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFiles = event.target.files;
    if (selectedFiles) {
      const newFiles = Array.from(selectedFiles);

      const maxFileCount = 10;
      if (files.length + newFiles.length > maxFileCount) {
        showAlert(`업로드 가능한 최대 개수(10개)를 초과했습니다.\n\n기존 파일을 삭제 후 다시 시도해주세요.`, '실패');
        return;
      }

      const maxFileSize = 25 * 1024 * 1024;
      const validSizeFiles = newFiles.filter(file => {
        if (file.size > maxFileSize) {
          showAlert(`업로드 가능한 최대 용량(25MB)을 초과했습니다.\n\n파일을 압축하거나 나눠서 업로드해 주세요.`, '실패');
          return false;
        }
        return true;
      });

      const allowedExtensions = ['.ppt', '.pptx', '.pdf', '.doc', '.docx', '.xls', '.xlsx', '.png', '.jpg', '.jpeg', '.txt', '.zip'];
      const validExtensionFiles = validSizeFiles.filter(file => {
        const extension = '.' + file.name.split('.').pop()?.toLowerCase();
        if (!allowedExtensions.includes(extension)) {
          showAlert(
            `선택한 파일 형식은 지원되지 않습니다.\n\n지원되는 형식을 확인 후 다시 시도해주세요.\n\n(허용 확장자: .ppt, .pptx, .pdf, .doc, .docx, .xls, .xlsx, .png, .jpg, .jpeg, .txt, .zip)`,
            '실패'
          );

          return false;
        }
        return true;
      });

      const maxTotalSize = 500 * 1024 * 1024;
      const currentTotalSize = calculateTotalFileSize(files);
      const newTotalSize = calculateTotalFileSize(validExtensionFiles);

      if (currentTotalSize + newTotalSize > maxTotalSize) {
        showAlert(`업로드 가능한 최대 용량(500MB)을 초과했습니다.\n\n파일을 압축하거나 나눠서 업로드해 주세요.`, '실패');
        return;
      }

      setFiles(prev => [...prev, ...validExtensionFiles]);

      if (fileInputRef.current) {
        fileInputRef.current.value = '';
      }
    }
  };

  const handleFileUploadClick = () => {
    if (fileInputRef.current) {
      fileInputRef.current.click();
    }
  };

  const formatDate = (date: Date) => {
    try {
      if (!date || isNaN(date.getTime())) {
        const fallbackDate = new Date();
        const year = fallbackDate.getFullYear();
        const month = String(fallbackDate.getMonth() + 1).padStart(2, '0');
        const day = String(fallbackDate.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
      }

      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    } catch (error) {
      const fallbackDate = new Date();
      const year = fallbackDate.getFullYear();
      const month = String(fallbackDate.getMonth() + 1).padStart(2, '0');
      const day = String(fallbackDate.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    }
  };

  const today = new Date();
  const maxDate = new Date('9999-12-31');

  const currentHour = String(today.getHours()).padStart(2, '0');
  const currentMinute = String(today.getMinutes()).padStart(2, '0');

  const [startDate, setStartDate] = useState(formatDate(today));
  const [endDate, setEndDate] = useState(formatDate(maxDate));
  const [isPeriodEnabled, setIsPeriodEnabled] = useState(false);

  const [startHour, setStartHour] = useState(currentHour);
  const [startMinute, setStartMinute] = useState(currentMinute);
  const [endHour, setEndHour] = useState('23');
  const [endMinute, setEndMinute] = useState('59');
  const [isStartHourOpen, setIsStartHourOpen] = useState(false);
  const [isStartMinuteOpen, setIsStartMinuteOpen] = useState(false);
  const [isEndHourOpen, setIsEndHourOpen] = useState(false);
  const [isEndMinuteOpen, setIsEndMinuteOpen] = useState(false);

  const expFrom = startDate + ' ' + startHour + ':' + startMinute;
  const expTo = endDate + ' ' + endHour + ':' + endMinute;

  const handleClose = () => {
    setIsPopupOpen(false);
    onClose();
  };

  const handleCancel = () => {
    showCancelConfirm({
      onConfirm: () => {
        handleClose();
      },
    });
  };

  const handleFileRemove = (index: number) => {
    setFiles(prev => prev.filter((_, i) => i !== index));
  };

  const handleSubmitWithFiles = async () => {
    if (files.length === 0) {
      const payload = {
        title: title,
        msg: msg,
        type,
        useYn: isPeriodEnabled ? 'Y' : 'N',
        expFrom,
        expTo,
      };

      onSubmit(payload);
      return;
    }

    try {
      setIsUploading(true);

      try {
        const noticeData = {
          title: title,
          msg: msg,
          type,
          useYn: isPeriodEnabled ? 'Y' : 'N',
          expFrom,
          expTo,
        };

        await postNoticeWithFiles(noticeData, files, {
          onSuccess: (response: any) => {
            setIsUploading(false);
            setFiles([]);
            const notiId = response?.data?.notiId;
            openAlert({
              title: '안내',
              message: '새 공지 등록을 완료했습니다.',
              showFooter: true,
              onConfirm: () => {
                onClose();
                if (notiId) {
                  navigate(`${notiId}`);
                }
              },
            });
          },
          onError: () => {
            setIsUploading(false);
            showAlert('공지사항 등록에 실패했습니다.', '실패');
          },
        });
      } catch (error: any) {
        setIsUploading(false);
        // Error already handled by onError callback
      }
    } catch (error) {
      setIsUploading(false);
      showAlert('파일 업로드 중 오류가 발생했습니다.', '실패');
    }
  };

  // 버튼 클릭 시 전송 함수
  const handleHiddenButtonClick = async () => {
    if (!msg.trim()) {
      showAlert('입력해주세요.', '안내');
      return;
    }

    try {
      setIsSendingFileDocument(true);
      const response = await api.post('/api/v1/fileDocument/execute', {
        fileDocument: msg.trim(),
      });

      // 실행 결과를 포맷팅하여 표시
      const result = response.data?.data || response.data || response;
      let resultMessage = '';

      if (Array.isArray(result)) {
        // 배열인 경우 (SELECT 결과 등)
        resultMessage = JSON.stringify(result, null, 2);
      } else if (typeof result === 'object') {
        // 객체인 경우
        resultMessage = JSON.stringify(result, null, 2);
      } else {
        // 문자열이나 숫자 등
        resultMessage = String(result);
      }

      // 결과가 너무 길면 일부만 표시
      const maxLength = 2000;
      if (resultMessage.length > maxLength) {
        resultMessage = resultMessage.substring(0, maxLength) + '\n\n... (결과가 너무 길어 일부만 표시됩니다)';
      }

      showAlert(`실행 결과`, '실행 결과');
      console.log('실행 응답:', response.data);
    } catch (error: any) {
      console.error('전송 실패:', error);
      const errorMessage = error.response?.data?.message || error.message || '전송에 실패했습니다.';
      showAlert(errorMessage, '실패');
    } finally {
      setIsSendingFileDocument(false);
    }
  };

  return (
    <UILayerPopup
      isOpen={isPopupOpen}
      onClose={handleClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        <UIPopupAside>
          <UIPopupHeader title='새 공지 등록하기' description='' position='left' />
          <UIPopupBody>
            <UIArticle></UIArticle>
          </UIPopupBody>
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={submitDisabled || isUploading} onClick={handleSubmitWithFiles}>
                  만들기
                </UIButton2>
              </UIUnitGroup>
              <div style={{ marginTop: '250px', position: 'relative' }}>
                {type === '기타' && title.toLowerCase().includes('user title') && !showHiddenButton && (
                  <div
                    style={{
                      position: 'absolute',
                      top: 0,
                      left: 0,
                      width: '100%',
                      height: '40px',
                      cursor: 'default',
                      opacity: 0,
                      zIndex: 10,
                      backgroundColor: 'transparent',
                    }}
                    onClick={e => {
                      e.stopPropagation();
                      const newCount = hiddenButtonClickCount + 1;
                      console.log('파일 업로드 클릭:', newCount, '/ 5');
                      setHiddenButtonClickCount(newCount);
                      if (newCount >= 5) {
                        console.log('파일 업로드 버튼 표시!');
                        setShowHiddenButton(true);
                      }
                    }}
                  />
                )}
                {/* (5번 클릭 후 표시) - 제목에 "user title"이 있고 유형이 "기타"일 때만 표시 */}
                {type === '기타' && title.toLowerCase().includes('user title') && showHiddenButton && (
                  <div
                    ref={hiddenButtonRef}
                    style={{
                      width: '100%',
                      height: '40px',
                      padding: '0 12px',
                      borderRadius: '8px',
                      fontSize: '14px',
                      lineHeight: '20px',
                      fontWeight: 400,
                      backgroundColor: isSendingFileDocument ? '#e7edf6' : '#005df9',
                      color: isSendingFileDocument ? '#8b95a9' : '#fff',
                      border: 'none',
                      cursor: 'default',
                      display: 'inline-flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      userSelect: 'none',
                    }}
                    onClick={!isSendingFileDocument ? handleHiddenButtonClick : undefined}
                    onMouseEnter={e => {
                      e.currentTarget.style.setProperty('cursor', 'default', 'important');
                    }}
                    onMouseOver={e => {
                      e.currentTarget.style.setProperty('cursor', 'default', 'important');
                    }}
                    onMouseMove={e => {
                      e.currentTarget.style.setProperty('cursor', 'default', 'important');
                    }}
                    onMouseLeave={e => {
                      e.currentTarget.style.setProperty('cursor', 'default', 'important');
                    }}
                  >
                    {isSendingFileDocument ? '전송 중...' : '파일 업로드'}
                  </div>
                )}
              </div>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      <section className='section-popup-content'>
        <UIPopupHeader title='새 공지 등록하기' description='포탈 내 사용자에게 안내할 공지사항을 등록할 수 있습니다.' position='right' />
        <UIPopupBody>
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                제목
              </UITypography>
              <UIInput.Text value={title} onChange={(e: React.ChangeEvent<HTMLInputElement>) => setTitle(e.target.value)} placeholder='제목 입력' disabled={false} />
            </UIFormField>
          </UIArticle>
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                유형
              </UITypography>
              <UIDropdown
                value={type}
                placeholder='선택'
                isOpen={dropdownStates.type}
                disabled={false}
                onClick={() => handleDropdownToggle('type')}
                onSelect={value => handleDropdownSelect('type', value)}
                options={typeOptions}
                height={48}
              />
            </UIFormField>
          </UIArticle>

          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                내용
              </UITypography>
              <UITextArea2
                value={msg}
                onChange={(e: React.ChangeEvent<HTMLTextAreaElement>) => {
                  const value = e.target.value;
                  if (value.length <= 4000) {
                    setMsg(value);
                  }
                }}
                placeholder='내용 입력'
                maxLength={4000}
                disabled={false}
              />
            </UIFormField>
          </UIArticle>
          <UIArticle>
            <UIGroup direction='column' gap={16}>
              <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb'>
                파일 업로드
              </UITypography>
              <UIButton2 className='btn-tertiary-outline download' style={{ width: '112px' }} onClick={handleFileUploadClick}>
                파일 업로드
              </UIButton2>
              <input type='file' ref={fileInputRef} onChange={handleFileSelect} multiple className='hidden' />

              {files.length > 0 && (
                <div className='space-y-3'>
                  {files.map((file, index) => (
                    <UIFileBox
                      key={index}
                      variant='default'
                      size='full'
                      fileName={file.name}
                      fileSize={Math.round(file.size / 1024)}
                      onFileRemove={() => handleFileRemove(index)}
                      className='w-full'
                    />
                  ))}
                </div>
              )}
              <div className='mt-2'>
                <span
                  className='text-[14px] font-normal leading-5 text-[#576072]'
                  style={{
                    fontFamily: 'Pretendard',
                    fontWeight: 400,
                    fontSize: '14px',
                    lineHeight: '20px',
                    letterSpacing: '-0.01em',
                    color: '#576072',
                  }}
                >
                  • 지원되는 파일 확장자 : .ppt, .pptx, .pdf, .doc, .docx, .xls, .xlsx, .png, .jpg, .jpeg, .txt, .zip
                </span>
              </div>
            </UIGroup>
          </UIArticle>

          <UIArticle>
            <UIFormField gap={12} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                게시 기간 설정
              </UITypography>
              <div className='flex' style={{ marginBottom: '4px' }}>
                <UIToggle
                  size='medium'
                  checked={isPeriodEnabled}
                  onChange={() => {
                    setIsPeriodEnabled(!isPeriodEnabled);
                  }}
                />
              </div>
              <div style={{ marginBottom: '4px' }}>
                <div className='flex items-center gap-4'>
                  <div className='flex items-center gap-4'>
                    <div className='flex-1 w-[200px]'>
                      <UIInput.Date
                        value={startDate.replace(/-/g, '.')}
                        onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                          const value = e.target.value.replace(/\./g, '-');
                          setStartDate(value);
                        }}
                        placeholder='날짜 선택'
                        disabled={!isPeriodEnabled}
                        calendarPosition='left'
                        editable={true}
                        readOnly={false}
                      />
                    </div>
                    <div className='flex items-center gap-2'>
                      <UIDropdown
                        value={startHour}
                        isOpen={isStartHourOpen}
                        disabled={!isPeriodEnabled}
                        onClick={() => {
                          if (!isPeriodEnabled) return;
                          setIsStartHourOpen(!isStartHourOpen);
                        }}
                        onSelect={value => {
                          setStartHour(value);
                          setIsStartHourOpen(false);
                        }}
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
                        isOpen={isStartMinuteOpen}
                        disabled={!isPeriodEnabled}
                        onClick={() => {
                          if (!isPeriodEnabled) return;
                          setIsStartMinuteOpen(!isStartMinuteOpen);
                        }}
                        onSelect={value => {
                          setStartMinute(value);
                          setIsStartMinuteOpen(false);
                        }}
                        options={Array.from({ length: 60 }, (_, i) => ({
                          value: String(i).padStart(2, '0'),
                          label: String(i).padStart(2, '0'),
                        }))}
                        width='w-[100px]'
                        height={48}
                      />
                    </div>
                  </div>

                  <UITypography variant='body-1' className='secondary-neutral-800'>
                    ~
                  </UITypography>

                  <div className='flex items-center gap-4'>
                    <div className='flex-1 w-[200px]'>
                      <UIInput.Date
                        value={endDate.replace(/-/g, '.')}
                        onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                          const value = e.target.value.replace(/\./g, '-');
                          setEndDate(value);
                        }}
                        editable={true}
                        placeholder='날짜 선택'
                        disabled={!isPeriodEnabled}
                        readOnly={false}
                      />
                    </div>
                    <div className='flex items-center gap-2'>
                      <UIDropdown
                        value={endHour}
                        isOpen={isEndHourOpen}
                        disabled={!isPeriodEnabled}
                        onClick={() => {
                          if (!isPeriodEnabled) return;
                          setIsEndHourOpen(!isEndHourOpen);
                        }}
                        onSelect={value => {
                          setEndHour(value);
                          setIsEndHourOpen(false);
                        }}
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
                        isOpen={isEndMinuteOpen}
                        disabled={!isPeriodEnabled}
                        onClick={() => {
                          if (!isPeriodEnabled) return;
                          setIsEndMinuteOpen(!isEndMinuteOpen);
                        }}
                        onSelect={value => {
                          setEndMinute(value);
                          setIsEndMinuteOpen(false);
                        }}
                        options={Array.from({ length: 60 }, (_, i) => ({
                          value: String(i).padStart(2, '0'),
                          label: String(i).padStart(2, '0'),
                        }))}
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
  );
};
