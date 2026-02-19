import React, { useEffect, useMemo, useState } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup, type UIStepperItem, UIFormField, UIDropdown, UIInput } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { useGetPythonVer } from '@/services/home/webide/ide.services';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';

// 스테퍼 데이터
const stepperItems: UIStepperItem[] = [
  {
    id: 'step1',
    label: '버전 선택',
    step: 1,
  },
  {
    id: 'step2',
    label: 'DW 계정 선택',
    step: 2,
  },
];

interface IdeStep2VerPickPopupPageProps {
  isOpen: boolean;
  onClose: () => void;
  selectedTool?: 'jupyter' | 'vscode' | null;
  // eslint-disable-next-line no-unused-vars
  onNext?: (version: string) => void;
}

export const IdeStep2VerPickPopupPage: React.FC<IdeStep2VerPickPopupPageProps> = ({ isOpen, onClose, selectedTool = 'jupyter', onNext }) => {
  // 상태 관리
  const [selectedVersion, setSelectedVersion] = useState<string>('');
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  
  // 공통 팝업 훅
  const { showCancelConfirm } = useCommonPopup();

  // Python 버전 목록 조회
  const { data: pythonVerData, isLoading: loading, error } = useGetPythonVer({ ideType: selectedTool }, { enabled: isOpen });

  // 버전 옵션 데이터 생성
  const versionOptions = useMemo(() => {
    const versions = pythonVerData?.versions || [];
    return versions.map(version => ({
      value: version,
      label: `V${version}`,

    }));
  }, [pythonVerData?.versions]);

  // 첫 번째 버전을 기본값으로 설정
  useEffect(() => {
    if (versionOptions.length > 0 && !selectedVersion) {
      setSelectedVersion(versionOptions[0].value);
    }
  }, [versionOptions, selectedVersion]);

  const handleCancelClick = () => {
    showCancelConfirm({
      onConfirm: () => {
        setSelectedVersion('');
        setIsDropdownOpen(false);
        onClose();
      },
    });
  };

  const handleClose = () => {
    setSelectedVersion('');
    setIsDropdownOpen(false);
    onClose();
  };

  const handleNext = () => {
    if (!selectedVersion) {
      return;
    }

    if (onNext) {
      onNext(selectedVersion);
    }
  };

  return (
    <>
      <UILayerPopup
        isOpen={isOpen}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='IDE 생성' description='' position='left' />

            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              <UIArticle>
                <UIStepper currentStep={1} items={stepperItems} direction='vertical' />
              </UIArticle>
            </UIPopupBody>

            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-aside-gray' onClick={handleCancelClick}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-aside-blue opacity-50 cursor-not-allowed' disabled>
                    생성
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        <section className='section-popup-content'>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='버전 선택' description='선택한 도구의 버전을 선택해주세요.' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                  도구명
                </UITypography>
                <UIInput.Text
                  id='textfield1-1'
                  title='텍스트 필드 타이틀'
                  placeholder='Jupyter Notebook'
                  value={selectedTool === 'jupyter' ? 'Jupyter Notebook' : 'VS Code'}
                  readOnly
                />
              </UIFormField>
            </UIArticle>

            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  Python
                </UITypography>
                <UIDropdown
                  value={selectedVersion}
                  options={versionOptions}
                  isOpen={isDropdownOpen}
                  onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                  onSelect={(value: string) => {
                    setSelectedVersion(value);
                    setIsDropdownOpen(false);
                  }}
                  placeholder='Python 버전 선택'
                  disabled={loading || !!error}
                  className={selectedVersion ? 'text-blue-600' : ''}
                  data-state={selectedVersion ? 'selected' : 'placeholder'}
                />
                {loading && <div className='mt-2 text-sm text-gray-500'>버전 목록을 불러오는 중...</div>}
                {error && <div className='mt-2 text-sm text-red-500'>Python 버전 목록을 불러오는데 실패했습니다.</div>}
              </UIFormField>
            </UIArticle>
          </UIPopupBody>

          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} align='start'>
                <UIButton2 className={`btn-secondary-blue ${!selectedVersion ? 'opacity-50 cursor-not-allowed' : ''}`} onClick={handleNext} disabled={!selectedVersion}>
                  다음
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
