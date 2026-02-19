import { useRef } from 'react';

import { UIButton2 } from '../UIButton2';
import { UIIcon2 } from '../UIIcon2';
import { UIImage } from '../UIImage';
import { UIToggle } from '../UIToggle';
import { UITypography } from '../UITypography';

import type { UIFileBoxProps } from './types';

export function UIFileBox({
  variant = 'default',
  size = 'default',
  items,
  data,
  file,
  fileName,
  fileSize,
  progress,
  status,
  toggleEnabled = false,
  disabled = false,
  onFileSelect,
  onFileRemove,
  onToggleChange,
  onMetadataClick,
  onClickFileName,
  accept = '*/*',
  className = '',
  guideMessage,
  metadataText,
}: UIFileBoxProps) {
  const fileInputRef = useRef<HTMLInputElement>(null);

  // items prop이 있을 때는 파일 목록 렌더링
  if (items && items.length > 0) {
    return (
      <div className={'space-y-3 max-h-[372px] overflow-y-auto custom-box-scroll pr-3 ' + className}>
        {items.map((item, index) => (
          <UIFileBox
            key={item.fileId || index}
            variant={variant}
            size={size}
            file={item.file}
            fileName={item.fileName}
            fileSize={item.fileSize}
            progress={item.progress}
            status={item.status}
            toggleEnabled={item.toggleEnabled}
            disabled={disabled}
            onFileRemove={() => onFileRemove?.(index)}
            onToggleChange={enabled => onToggleChange?.(enabled, index)}
            accept={accept}
            guideMessage={item.guideMessage || guideMessage}
            metadataText={item.metadataText || metadataText}
            onMetadataClick={onMetadataClick}
            onClickFileName={onClickFileName}
          />
        ))}
      </div>
    );
  }

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFile = event.target.files?.[0] || null;
    onFileSelect?.(selectedFile);
  };

  const handleRemoveFile = () => {
    if (disabled) return;
    onFileRemove?.();
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  const formatFileSize = (size: number) => {
    if (size === undefined || size === null || Number.isNaN(size)) return '';
    return `${Intl.NumberFormat().format(size)}KB`;
  };

  const getDisplayFileName = () => {
    return data?.fileName || fileName || file?.name || '';
  };

  const getDisplayFileSize = () => {
    if (data?.fileSize !== undefined && data?.fileSize !== null) return formatFileSize(data.fileSize);
    if (fileSize !== undefined && fileSize !== null) return formatFileSize(fileSize);
    if (file) return formatFileSize(file.size);
    return '';
  };

  const getProgress = () => {
    return data?.progress !== undefined ? data.progress : progress;
  };

  const getStatus = () => {
    return data?.status || status;
  };

  const getToggleEnabled = () => {
    return data?.toggleEnabled !== undefined ? data.toggleEnabled : toggleEnabled;
  };

  const getSizeStyles = () => {
    switch (size) {
      case 'wide':
        return 'max-w-2xl';
      case 'full':
        return 'w-full';
      default:
        return 'max-w-md';
    }
  };

  if (variant === 'default') {
    const displayFileName = getDisplayFileName();
    const displayFileSize = getDisplayFileSize();
    const currentProgress = getProgress();
    const currentStatus = getStatus();

    return (
      <div className={'filebox-wrap ' + className + ' ' + getSizeStyles()}>
        <input ref={fileInputRef} type='file' accept={accept} onChange={handleFileChange} className='hidden' disabled={disabled} />

        <div className='filebox-item bg-gray-100 rounded-xl px-4 py-4 h-[52px] flex items-center justify-between'>
          <div className='filebox-item_left flex items-center gap-3'>

            {currentProgress !== undefined ? (
              <div className={'file-progress ' + currentStatus}>
                <div className='file-progress_gauge' style={{ width: currentProgress + '%' }}></div>
              </div>
            ) : <>
              <UIIcon2 className='ic-system-16-file-save' />
              <span className='text-sm font-normal leading-5 text-gray-600' style={{ letterSpacing: '-0.08px' }}>
                {displayFileName || '파일을 선택하세요'}
              </span></>}
          </div>
          <div className='filebox-item_right flex items-center gap-2'>
            {displayFileSize && <span className='text-[13px] font-normal leading-5 text-gray-600 text-center'>{displayFileSize}</span>}
            {currentProgress === undefined && <button onClick={handleRemoveFile} disabled={disabled} className='text-gray-600 hover:text-gray-700 cursor-pointer disabled:cursor-not-allowed flex items-center'>
              <UIIcon2 className='ic-system-16-file-Delete' />
            </button>}
          </div>
        </div>
        {guideMessage && (
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
              {guideMessage}
            </span>
          </div>
        )}
      </div>
    );
  }

  if (variant === 'txtBtn') {
    const displayFileName = getDisplayFileName();
    const displayFileSize = getDisplayFileSize();

    return (
      <div className={className + ' ' + getSizeStyles()}>
        <input ref={fileInputRef} type='file' accept={accept} onChange={handleFileChange} className='hidden' disabled={disabled} />

        <div className='bg-gray-100 rounded-xl px-4 py-4 h-[56px] flex items-center justify-between'>
          <div className='flex items-center gap-3'>
            <UIIcon2 className='ic-system-16-file-save' />
            <span className='text-sm font-normal leading-5 text-gray-600' style={{ letterSpacing: '-0.08px' }}>
              {displayFileName || '파일을 선택하세요'}
            </span>
          </div>

          <div className='flex items-center gap-4'>
            <UIButton2
              onClick={onMetadataClick}
              className='text-sm font-normal leading-5 text-gray-800 underline underline-offset-4 cursor-pointer'
              style={{ letterSpacing: '-0.08px' }}
            >
              {metadataText || '메타데이터'}
            </UIButton2>
            <div className='flex items-center gap-2'>
              {displayFileSize && <span className='text-[13px] font-normal leading-5 text-gray-600 text-center'>{displayFileSize}</span>}
              <button onClick={handleRemoveFile} disabled={disabled} className='text-gray-600 hover:text-gray-700 cursor-pointer disabled:cursor-not-allowed'>
                <UIIcon2 className='ic-system-16-file-Delete' />
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (variant === 'toggle') {
    const displayFileName = getDisplayFileName();
    const displayFileSize = getDisplayFileSize();
    const currentToggleEnabled = getToggleEnabled();

    return (
      <div className={className + ' ' + getSizeStyles()}>
        <input ref={fileInputRef} type='file' accept={accept} onChange={handleFileChange} className='hidden' disabled={disabled} />

        <div className='bg-gray-100 rounded-xl px-4 py-4 h-[52px] flex items-center justify-between'>
          <div className='flex items-center gap-3 cursor-pointer'>
            <UIIcon2 className='ic-system-16-file-save' />
            <span className='text-sm font-normal leading-5 text-gray-600' style={{ letterSpacing: '-0.08px' }}>
              {displayFileName || '파일을 선택하세요'}
            </span>
          </div>

          <div className='flex items-center gap-4'>
            <div className='flex items-center gap-2'>
              <span className='text-[13px] font-semibold leading-5 text-gray-600' style={{ letterSpacing: '-0.08px' }}>
                민감정보 포함
              </span>
              <UIToggle checked={currentToggleEnabled} onChange={onToggleChange} disabled={disabled} />
            </div>
            <div className='flex items-center gap-2'>
              {displayFileSize && <span className='text-[13px] font-normal leading-5 text-gray-600 text-center'>{displayFileSize}</span>}
              <button onClick={handleRemoveFile} disabled={disabled} className='text-gray-600 hover:text-gray-700 cursor-pointer disabled:cursor-not-allowed'>
                <UIIcon2 className='ic-system-16-file-Delete' />
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (variant === 'processing') {
    const displayFileName = getDisplayFileName();
    const displayFileSize = getDisplayFileSize();

    return (
      <div className={className + ' ' + getSizeStyles()}>
        <div className='bg-gray-100 rounded-xl px-4 py-4 h-[52px] flex items-center justify-between'>
          <div className='flex items-center gap-3'>
            <UIIcon2 className='ic-system-16-file-save' />
            <span className='text-sm font-normal leading-5 text-gray-600' style={{ letterSpacing: '-0.08px' }}>
              {displayFileName}
            </span>
          </div>

          <div className='flex items-center gap-4'>
            <div className='flex items-center gap-2'>
              <UIImage src='/assets/images/common/ico-circle-making-20.svg' alt='processing' width={20} height={20} />
              <span className='text-[13px] font-semibold leading-5 text-[#576072]'>지식 생성 중</span>
            </div>
            <div className='flex items-center gap-2'>
              {displayFileSize && <span className='text-[13px] font-normal leading-5 text-gray-600 text-center'>{displayFileSize}</span>}
              <button onClick={handleRemoveFile} disabled={disabled} className='text-gray-600 hover:text-gray-700 cursor-pointer disabled:cursor-not-allowed'>
                <UIIcon2 className='ic-system-16-file-Delete' />
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (variant === 'completed') {
    const displayFileName = getDisplayFileName();
    const displayFileSize = getDisplayFileSize();

    return (
      <div className={className + ' ' + getSizeStyles()}>
        <div className='bg-gray-100 rounded-xl px-4 py-4 h-[52px] flex items-center justify-between'>
          <div className='flex items-center gap-3'>
            <UIIcon2 className='ic-system-16-file-save' />
            <span className='text-sm font-normal leading-5 text-gray-600' style={{ letterSpacing: '-0.08px' }}>
              {displayFileName}
            </span>
          </div>

          <div className='flex items-center gap-4'>
            <div className='flex items-center gap-2'>
              <UIImage src='/assets/images/common/ico-circle-check-20.svg' alt='completed' width={20} height={20} />
              <span className='text-[13px] font-semibold leading-5 text-[#005DF9]'>지식 생성 완료</span>
            </div>
            <div className='flex items-center gap-2'>
              {displayFileSize && <span className='text-[13px] font-normal leading-5 text-gray-600 text-center'>{displayFileSize}</span>}
              <button onClick={handleRemoveFile} disabled={disabled} className='text-gray-600 hover:text-gray-700 cursor-pointer disabled:cursor-not-allowed'>
                <UIIcon2 className='ic-system-16-file-Delete' />
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (variant === 'link') {
    const displayFileName = getDisplayFileName();

    return (
      <a
        href='#'
        className='flex items-center gap-2 text-[#576072] hover:text-[#005DF9] underline group'
        onClick={e => {
          e.preventDefault();
          onClickFileName?.();
        }}
      >
        <UIIcon2 className='ic-system-16-file-save' />
        <UITypography
          variant='body-2'
          className='secondary-neutral-600'
          style={{
            color: 'inherit',
            transition: 'color 0.2s ease',
          }}
        >
          {displayFileName}
        </UITypography>
      </a>
    );
  }

  return null;
}
