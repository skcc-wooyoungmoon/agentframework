import { useState } from 'react';

import { UIFileBox, UITypography, UIButton2 } from '@/components/UI/atoms';
import { UIArticle, UIDropdown, UIGroup, UIList } from '@/components/UI/molecules';

export const DT_030101_P04 = () => {
  /** 파일 업로드  ---------*/
  const [files, setFiles] = useState<object[]>([
    { fileName: 'Summary_train_1st', progress: '40' },
    { fileName: 'Summary_train_1st', progress: '0', status: 'error' },
  ]);
  const handleFileRemove = (index: number) => {
    setFiles(prev => prev.filter((_, i) => i !== index));
  };
  /** 파일 업로드  ---------*/

  const [selectedLoader, setSelectedLoader] = useState('Custom Loader');
  const [selectedCustom, setSelectedCustom] = useState('[M] report loader(json)');

  const [isSupplierDropdownOpen, setIsSupplierDropdownOpen] = useState(false);

  // 드롭다운 핸들러
  const handleSupplierSelect = (_value: string) => {
    setSelectedLoader(_value);
    setSelectedCustom(_value);
    setIsSupplierDropdownOpen(false);
  };

  return (
    <section className='section-modal'>
      <UIArticle>
        <div className='flex flex-col gap-6'>
          {/* colum */}
          <div className='flex gap-4'>
            <div className='w-[182px] mt-[8px]'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                파일 선택
              </UITypography>
            </div>
            <div className='flex-1'>
              <div className='mb-4'>
                <UIButton2 className='btn-tertiary-outline download' disabled>
                  파일 업로드
                </UIButton2>
              </div>
              <div className=''>
                <UIGroup gap={16} direction='column'>
                  <div>
                    {/* 파일 목록 */}
                    {files.length > 0 && (
                      <div className='space-y-3'>
                        {files.map((item, index) => (
                          <UIFileBox
                            key={index}
                            variant='txtBtn'
                            size='full'
                            fileName={(item as any).fileName}
                            fileSize={99}
                            onFileRemove={() => handleFileRemove(index)}
                            className='w-full'
                            metadataText='메타데이터'
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
                              업로드 가능한 파일 포맷 : PY, TXT, XLSX, PDF, XLS, DOCX, HTML, CSV, PPTX, ZIP, JSON, XML
                            </UITypography>
                          ),
                        },
                      ]}
                    />
                  </div>
                </UIGroup>
              </div>
            </div>
          </div>
          {/* colum */}
          <div className='flex gap-4'>
            <div className='w-[182px] flex items-center'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Loader 유형 선택
              </UITypography>
            </div>
            <div className='flex-1'>
              <UIDropdown
                required={true}
                value={selectedLoader}
                isOpen={isSupplierDropdownOpen}
                onClick={() => setIsSupplierDropdownOpen(!isSupplierDropdownOpen)}
                onSelect={handleSupplierSelect}
                options={[
                  { value: 'select1', label: 'Custom Loader' },
                  { value: 'select2', label: 'Custom Loader 222' },
                ]}
              />
            </div>
          </div>
          {/* colum */}
          <div className='flex gap-4'>
            <div className='w-[182px] flex items-center'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Custom Loader
              </UITypography>
            </div>
            <div className='flex-1'>
              <UIDropdown
                required={true}
                value={selectedCustom}
                isOpen={isSupplierDropdownOpen}
                onClick={() => setIsSupplierDropdownOpen(!isSupplierDropdownOpen)}
                onSelect={handleSupplierSelect}
                options={[
                  { value: 'select1', label: '[M] report loader(json)' },
                  { value: 'select2', label: '[M] report loader(json)' },
                ]}
              />
            </div>
          </div>
        </div>
      </UIArticle>

      <UIArticle>
        <div className='filebox-item bg-gray-100 rounded-xl px-6 py-5 flex items-center justify-between'>
          <UIList
            gap={4}
            direction='column'
            className='ui-list_important'
            data={[
              {
                dataItem: (
                  <UITypography variant='body-2' className='secondary-neutral-600 text-sb'>
                    등록한 커스텀 스크립트를 사용하여 미리보기를 하려면, 해당 문서 파일을 업로드해 주세요.
                  </UITypography>
                ),
              },
            ]}
          />
        </div>
      </UIArticle>
    </section>
  );
};
