import React, { useState, useEffect, useRef } from 'react';
import type { createRequest } from './type.ts';
import { UIFileBox, UIButton2, UIToggle, UITypography } from '@/components/UI/atoms';
import { UIDropdown, UIUnitGroup, UIInput, UITextArea2, UIFormField, UIArticle, UIPopupHeader, UIPopupBody, UIPopupFooter, UIList, UIGroup } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { putNoticeWithFiles } from '@/services/admin/noticeMgmt/noticeMgmt.services';
import { useNoticeMgmt } from '@/stores/admin/noticeMgmt/noticeMgmt.atoms';
import { useModal } from '@/stores/common/modal';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup.tsx';

interface NoticeMgmtUpdatePageProps {
  onSubmit?: (payload: createRequest) => void;
  open: boolean;
  onClose: (updatedData?: any) => void;
  selectedRowData?: any;
}

export const NoticeMgmtUpdatePage: React.FC<NoticeMgmtUpdatePageProps> = ({ open, onClose, selectedRowData }) => {
  const [isPopupOpen, setIsPopupOpen] = useState(open);
  const [submitDisabled, setSubmitDisabled] = useState(true);

  const { noticeTypeOptions, selectedNoticeDetail } = useNoticeMgmt();

  const isDataLoadedRef = useRef(false);

  const { openAlert } = useModal();
  const showAlert = (message: string, title: string = '안내') => {
    openAlert({
      message: message,
      title: title,
      confirmText: '확인',
      onConfirm: () => {
        // Alert confirmed
      },
    });
  };

  const [title, setTitle] = useState('');
  const [msg, setMsg] = useState('');
  const [type, setType] = useState('시스템 점검');
  
  const { showCancelConfirm } = useCommonPopup();
  
  // 초기값 저장 (변경사항 비교용)
  const initialValuesRef = useRef({
    title: '',
    msg: '',
    type: '',
    startDate: '',
    startHour: '',
    startMinute: '',
    endDate: '',
    endHour: '',
    endMinute: '',
    isPeriodEnabled: true,
    existingFilesCount: 0,
  });

  useEffect(() => {
    setIsPopupOpen(open);
  }, [open]);

  useEffect(() => {
    const dataSource = selectedRowData || selectedNoticeDetail;

    if (!open) {
      isDataLoadedRef.current = false;
      return;
    }

    if (isDataLoadedRef.current) {
      return;
    }

    if (dataSource && typeof dataSource === 'object' && Object.keys(dataSource).length > 0) {
      isDataLoadedRef.current = true;

      const initialTitle = dataSource.title || '';
      const initialMsg = dataSource.msg || '';
      const initialType = dataSource.type || '시스템 점검';
      
      setTitle(initialTitle);
      setMsg(initialMsg);
      setType(initialType);
      
      let initialStartDate = formatDate(today);
      let initialStartHour = '00';
      let initialStartMinute = '00';
      let initialEndDate = formatDate(oneMonthLater);
      let initialEndHour = '00';
      let initialEndMinute = '00';
      let initialIsPeriodEnabled = true;

      if (dataSource.expFrom) {
        let expFromDate = '';
        let expFromHour = '00';
        let expFromMinute = '00';

        if (typeof dataSource.expFrom === 'string') {
          const parts = dataSource.expFrom.split(' ');
          if (parts.length >= 2) {
            const datePart = parts[0];
            const timePart = parts[1];
            const timeParts = timePart.split(':');

            expFromDate = datePart.includes('.') ? datePart.replace(/\./g, '-') : datePart;
            expFromHour = timeParts[0] || '00';
            expFromMinute = timeParts[1] || '00';
          } else if (parts.length === 1) {
            expFromDate = parts[0].includes('.') ? parts[0].replace(/\./g, '-') : parts[0];
          }
        }

        if (expFromDate && expFromDate.trim()) {
          const testDate = new Date(expFromDate);
          if (!isNaN(testDate.getTime())) {
            initialStartDate = expFromDate;
            initialStartHour = expFromHour;
            initialStartMinute = expFromMinute;
            setStartDate(expFromDate);
            setStartHour(expFromHour);
            setStartMinute(expFromMinute);
          } else {
            setStartDate(formatDate(today));
            setStartHour('00');
            setStartMinute('00');
          }
        } else {
          setStartDate(formatDate(today));
          setStartHour('00');
          setStartMinute('00');
        }
        
      } else {
        setStartDate(formatDate(today));
        setStartHour('00');
        setStartMinute('00');
      }

      if (dataSource.expTo) {
        let expToDate = '';
        let expToHour = '00';
        let expToMinute = '00';

        if (typeof dataSource.expTo === 'string') {
          const parts = dataSource.expTo.split(' ');
          if (parts.length >= 2) {
            const datePart = parts[0];
            const timePart = parts[1];
            const timeParts = timePart.split(':');

            expToDate = datePart.includes('.') ? datePart.replace(/\./g, '-') : datePart;
            expToHour = timeParts[0] || '00';
            expToMinute = timeParts[1] || '00';
          } else if (parts.length === 1) {
            expToDate = parts[0].includes('.') ? parts[0].replace(/\./g, '-') : parts[0];
          }
        }

        if (expToDate && expToDate.trim()) {
          const testDate = new Date(expToDate);
          if (!isNaN(testDate.getTime())) {
            initialEndDate = expToDate;
            initialEndHour = expToHour;
            initialEndMinute = expToMinute;
            setEndDate(expToDate);
            setEndHour(expToHour);
            setEndMinute(expToMinute);
        
          } else {
            setEndDate(formatDate(oneMonthLater));
            setEndHour('00');
            setEndMinute('00');
          }
        } else {
          setEndDate(formatDate(oneMonthLater));
          setEndHour('00');
          setEndMinute('00');
        }
        
      } else {
        setEndDate(formatDate(oneMonthLater));
        setEndHour('00');
        setEndMinute('00');
      }

      if (dataSource.useYn) {
        initialIsPeriodEnabled = dataSource.useYn === 'Y';
        setIsPeriodEnabled(dataSource.useYn === 'Y');
      }

      if (dataSource.files && Array.isArray(dataSource.files)) {
        setExistingFiles(dataSource.files);
      }
      
      // 초기값 저장
      initialValuesRef.current = {
        title: initialTitle,
        msg: initialMsg,
        type: initialType,
        startDate: initialStartDate,
        startHour: initialStartHour,
        startMinute: initialStartMinute,
        endDate: initialEndDate,
        endHour: initialEndHour,
        endMinute: initialEndMinute,
        isPeriodEnabled: initialIsPeriodEnabled,
        existingFilesCount: dataSource.files?.length || 0,
      };
    } else {
      setTitle('');
      setMsg('');
      setType('시스템 점검');
      setStartDate(formatDate(today));
      setEndDate(formatDate(oneMonthLater));
      setIsPeriodEnabled(true);
      setExistingFiles([]);
      setFiles([]);
      setDeletedFileIds([]);
    }
  }, [selectedNoticeDetail, selectedRowData, open]);

  useEffect(() => {
    setSubmitDisabled(!(title.trim() && msg.trim() && type));
  }, [title, msg, type]);

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
    if (key === 'type') {
      setType(value);
    }
    setDropdownStates(prev => ({ ...prev, [key]: false }));
  };

  const typeOptions = noticeTypeOptions;

  const [files, setFiles] = useState<File[]>([]);
  const [existingFiles, setExistingFiles] = useState<
    Array<{
      fileId: number;
      originalFilename: string;
      storedFilename: string;
      fileSize: number;
      contentType: string;
      uploadDate: string;
      useYn: string;
    }>
  >([]);
  const [deletedFileIds, setDeletedFileIds] = useState<number[]>([]);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleFileSelect = (event: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFiles = event.target.files;
    if (selectedFiles) {
      const newFiles = Array.from(selectedFiles);

      const maxFileCount = 10;
      if (files.length + newFiles.length > maxFileCount) {
        showAlert(
          `업로드 가능한 최대 개수(10개)를 초과했습니다.\n\n기존 파일을 삭제 후 다시 시도해주세요.`,
          '실패'
        );
        return;
      }

      const maxSize = 500 * 1024 * 1024;
      const validSizeFiles = newFiles.filter(file => {
        if (file.size > maxSize) {
          showAlert(
            `업로드 가능한 최대 용량(500MB)을 초과했습니다.\n\n파일을 압축하거나 나눠서 업로드해 주세요.`,
            '실패'
          );
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

  const [startHour, setStartHour] = useState('00');
  const [startMinute, setStartMinute] = useState('00');
  const [endHour, setEndHour] = useState('00');
  const [endMinute, setEndMinute] = useState('00');
  const [isStartHourOpen, setIsStartHourOpen] = useState(false);
  const [isStartMinuteOpen, setIsStartMinuteOpen] = useState(false);
  const [isEndHourOpen, setIsEndHourOpen] = useState(false);
  const [isEndMinuteOpen, setIsEndMinuteOpen] = useState(false);

  const today = new Date();
  const oneMonthLater = new Date();
  oneMonthLater.setMonth(today.getMonth() + 1);

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

  const [startDate, setStartDate] = useState(formatDate(today));
  const [endDate, setEndDate] = useState(formatDate(oneMonthLater));
  const [isPeriodEnabled, setIsPeriodEnabled] = useState(true);

  useEffect(() => {
    // Date/time state change handler
  }, [startDate, startHour, startMinute, endDate, endHour, endMinute]);

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

  const handleExistingFileRemove = (fileId: number) => {
    setDeletedFileIds(prev => [...prev, fileId]);
    setExistingFiles(prev => prev.filter(file => file.fileId !== fileId));
  };

  const handleUpdateClick = () => {
    // 변경사항 확인
    const hasChanges = 
      title !== initialValuesRef.current.title ||
      msg !== initialValuesRef.current.msg ||
      type !== initialValuesRef.current.type ||
      startDate !== initialValuesRef.current.startDate ||
      startHour !== initialValuesRef.current.startHour ||
      startMinute !== initialValuesRef.current.startMinute ||
      endDate !== initialValuesRef.current.endDate ||
      endHour !== initialValuesRef.current.endHour ||
      endMinute !== initialValuesRef.current.endMinute ||
      isPeriodEnabled !== initialValuesRef.current.isPeriodEnabled ||
      files.length > 0 ||
      deletedFileIds.length > 0;
    
    if (!hasChanges) {
      openAlert({
        message: '수정된 내용이 없습니다.',
        title: '안내',
        confirmText: '확인',
      });
      return;
    }
    
    // 컨펌 없이 바로 수정 실행
    executeUpdate();
  };

  const executeUpdate = async () => {
    const dataSource = selectedRowData || selectedNoticeDetail;
    if (!dataSource?.notiId) {
      showAlert('공지사항 ID가 없습니다.');
      return;
    }

    if (!title || !title.trim()) {
      showAlert('제목을 입력해주세요.');
      return;
    }

    if (!msg || !msg.trim()) {
      showAlert('내용을 입력해주세요.');
      return;
    }

    if (!type) {
      showAlert('유형을 선택해주세요.');
      return;
    }

    const currentExpFrom = isPeriodEnabled ? `${startDate} ${startHour}:${startMinute}:00` : '';
    const currentExpTo = isPeriodEnabled ? `${endDate} ${endHour}:${endMinute}:00` : '';

    if (dataSource?.notiId) {
      const noticeData = {
        title: title,
        msg: msg,
        type,
        useYn: isPeriodEnabled ? 'Y' : 'N',
        expFrom: currentExpFrom,
        expTo: currentExpTo,
      };

      try {
        await putNoticeWithFiles(dataSource.notiId, noticeData, files, deletedFileIds, {
          onSuccess: _response => {
            openAlert({
              message: '수정사항이 저장되었습니다.',
              title: '완료',
              confirmText: '확인',
              onConfirm: () => {
                // 성공 후 처리 계속
              },
            });

            const allFiles = [
              ...existingFiles,
              ...files.map(file => ({
                fileId: -1,
                originalFilename: file.name,
                storedFilename: file.name,
                fileSize: file.size,
                uploadDate: new Date().toISOString(),
                isNew: true,
              })),
            ];

            const updatedData = {
              notiId: dataSource?.notiId || '',
              id: dataSource?.notiId || '',
              title: title || '',
              msg: msg || '',
              content: msg || '',
              type: type || '시스템 점검',
              useYn: isPeriodEnabled ? 'Y' : 'N',
              expFrom: isPeriodEnabled ? `${startDate} ${startHour}:${startMinute}:00` : '',
              expTo: isPeriodEnabled ? `${endDate} ${endHour}:${endMinute}:00` : '',
              createBy: dataSource?.createBy || '',
              createAt: dataSource?.createAt || '',
              createdAt: dataSource?.createAt || '',
              updateAt: new Date().toISOString(),
              modifiedDate: new Date().toISOString(),
              files: allFiles,
              newFiles: files,
            };

            onClose(updatedData);
          },
          onError: () => {
            showAlert('수정에 실패했습니다.', '실패');
          },
        });
      } catch (error) {
        // Error already handled by onError callback
      }
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
          <UIPopupHeader title='공지사항 수정' description='' position='left' />
          <UIPopupBody>
            <UIArticle></UIArticle>
          </UIPopupBody>
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={submitDisabled} onClick={handleUpdateClick}>
                  저장
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      <section className='section-popup-content'>
        <UIPopupHeader title='공지사항 수정' description='등록한 공지사항의 제목, 유형, 내용 등을 수정할 수 있습니다.' position='right' />
        <UIPopupBody>
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                제목
              </UITypography>
              <UIInput.Text value={title} onChange={(e: React.ChangeEvent<HTMLInputElement>) => setTitle(e.target.value)} placeholder='입력' disabled={false} />
            </UIFormField>
          </UIArticle>
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
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
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                내용
              </UITypography>
              <UITextArea2
                value={msg}
                onChange={(e: React.ChangeEvent<HTMLTextAreaElement>) => {
                  // 4000자를 초과하지 않도록 제한
                  const value = e.target.value;
                  if (value.length <= 4000) {
                    setMsg(value);
                  }
                }}
                placeholder='입력'
                maxLength={4000}
                disabled={false}
              />
            </UIFormField>
          </UIArticle>

          <UIArticle>
            <UIGroup direction='column' gap={16}>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                파일 업로드
              </UITypography>
              <UIButton2 className='btn-tertiary-outline download' style={{ width: '112px' }} onClick={handleFileUploadClick}>
                파일 업로드
              </UIButton2>
              <input type='file' ref={fileInputRef} onChange={handleFileSelect} multiple className='hidden' />

              {(existingFiles.length > 0 || files.length > 0) && (
                <div className='space-y-3'>
                  {existingFiles.map(file => (
                    <UIFileBox
                      key={file.fileId}
                      variant='default'
                      size='full'
                      fileName={file.originalFilename}
                      fileSize={Math.round(file.fileSize / 1024)}
                      onFileRemove={() => handleExistingFileRemove(file.fileId)}
                      className='w-full'
                    />
                  ))}
                  {files.map((file, index) => (
                    <UIFileBox
                      key={`new-${index}`}
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
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                게시 기간 설정
              </UITypography>
              <div className='flex'>
                <UIToggle
                  size='medium'
                  checked={isPeriodEnabled}
                  onChange={() => {
                    setIsPeriodEnabled(!isPeriodEnabled);
                  }}
                />
              </div>
              <div>
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
                        placeholder='날짜 선택'
                        disabled={!isPeriodEnabled}
                        editable={true}
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
