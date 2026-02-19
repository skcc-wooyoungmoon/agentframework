// ProjCreStep1BaseInfo.tsx
import React, { useState, useEffect } from 'react';

import { useAtom } from 'jotai';

import { UITypography, UIButton2, UIRadio2 } from '@/components/UI/atoms';
import { UIUnitGroup, UIPopupHeader, UIPopupBody, UIPopupFooter, UIInput, UITextArea2 } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { useModal } from '@/stores/common/modal';
import { projCreBaseInfoAtom } from '@/stores/home/proj/projCreWizard.atoms';

interface ProjCreStep1BaseInfoProps {
  onNextStep: () => void;
}

export const ProjCreStep1BaseInfo: React.FC<ProjCreStep1BaseInfoProps> = ({ onNextStep }) => {
  // Jotai atom 사용
  const [baseInfo, setBaseInfo] = useAtom(projCreBaseInfoAtom);

  // 로컬 상태
  const [projectName, setProjectName] = useState(baseInfo.name);
  const [projectDesc, setProjectDesc] = useState(baseInfo.description);
  // 퍼블리싱 구조에 맞춰 라디오 값은 option1(미포함), option2(포함) 사용
  const [selectedValue1, setSelectedValue1] = useState<string>(baseInfo.is_sensitive === 'Y' ? 'option2' : 'option1');
  const [sensitiveReason, setSensitiveReason] = useState(baseInfo.sensitive_reason);
  const { openAlert } = useModal();

  // 로컬 상태 변경 시 Jotai atom도 업데이트
  useEffect(() => {
    setBaseInfo({
      name: projectName,
      description: projectDesc,
      is_sensitive: selectedValue1 === 'option2' ? 'Y' : 'N',
      sensitive_reason: selectedValue1 === 'option2' ? sensitiveReason : '',
    });
  }, [projectName, projectDesc, selectedValue1, sensitiveReason, setBaseInfo]);

  const handleRadioChange = (checked: boolean, value: string) => {
    if (checked) {
      setSelectedValue1(value);
      // '미포함(option1)'이 선택되면 sensitiveReason 초기화
      if (value === 'option1') {
        setSensitiveReason('');
      }
    }
  };

  const isFormValid = () => {
    // 필수 입력 값 검증
    if (!projectName) return false;
    if (selectedValue1 === 'option2' && !sensitiveReason) return false;
    return true;
  };

  const handleSubmit = () => {
    if (!isFormValid()) {
      openAlert({
        title: '입력 오류',
        message: '필수 항목을 모두 입력해주세요.',
      });
      return;
    }
    onNextStep();
  };

  return (
    <>
      {/* 우측 Contents 영역 콘텐츠 */}
      {/* 우측 Contents 영역 퍼블리싱 구조 */}
      <section className='section-popup-content'>
        {/* 레이어 팝업 헤더 */}
        <UIPopupHeader title='기본 정보 입력' description='생성할 프로젝트의 이름, 설명 등 기본 정보를 입력해주세요.' position='right' />
        {/* 레이어 팝업 바디 */}
        <UIPopupBody>
          {/* 역할명 입력 필드 */}
          <UIArticle>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800' required={true}>
                프로젝트명
              </UITypography>
              <UIInput.Text value={projectName} onChange={e => setProjectName(e.target.value)} placeholder='프로젝트명 입력' maxLength={50} />
            </UIUnitGroup>
          </UIArticle>

          {/* 설명 입력 필드 */}
          <UIArticle>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800'>
                설명
              </UITypography>
              <UITextArea2 value={projectDesc} maxLength={100} onChange={e => setProjectDesc(e.target.value)} placeholder='설명 입력' />
            </UIUnitGroup>
          </UIArticle>

          {/* 개인정보 포함 여부 필드 */}
          <UIArticle>
            <UIUnitGroup gap={12} direction='column' style={{ marginBottom: '8px' }}>
              <UITypography variant='title-4' className='secondary-neutral-800' required={true}>
                개인정보 포함 여부
              </UITypography>
              <div>
                <UIUnitGroup gap={16} direction='column'>
                  <UIRadio2 name='basic1' value='option1' label='미포함' checked={selectedValue1 === 'option1'} onChange={handleRadioChange} />
                  <UIRadio2 name='basic1' value='option2' label='포함' checked={selectedValue1 === 'option2'} onChange={handleRadioChange} />
                </UIUnitGroup>
              </div>
            </UIUnitGroup>

            {/* 개인정보 포함 사유 입력 (포함일 때만 표시) */}
            {selectedValue1 === 'option2' && (
              <UITextArea2 value={sensitiveReason} maxLength={100} onChange={e => setSensitiveReason(e.target.value)} placeholder='개인정보 포함 사유 입력' />
            )}
          </UIArticle>
        </UIPopupBody>
        {/* 레이어 팝업 footer */}
        <UIPopupFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='start'>
              <UIButton2 className='btn-secondary-blue' style={{ width: '80px' }} disabled={!isFormValid() || projectName.length > 50} onClick={handleSubmit}>
                다음
              </UIButton2>
            </UIUnitGroup>
          </UIArticle>
        </UIPopupFooter>
      </section>
    </>
  );
};
