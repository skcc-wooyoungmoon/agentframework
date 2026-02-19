import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIFormField, UIInput, UIList, UIPopupBody, UIPopupFooter, UIPopupHeader } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import React, { useEffect, useRef, useState } from 'react';

import { UIUnitGroup } from '@/components/UI/molecules';
import { useUpdateQuota } from '@/services/deploy/apikey/apikey.services';
import { useModal } from '@/stores/common/modal';
import { UIDropdown } from '../../../components/UI/molecules/dropdown/UIDropdown';

interface ApiKeyMgmtUpdatePageProps {
  isOpen: boolean;
  onClose: () => void;
  apiKeyData?: any;
}

export const ApiKeyMgmtUpdatePage: React.FC<ApiKeyMgmtUpdatePageProps> = ({ isOpen, onClose, apiKeyData }) => {
  const { openConfirm, openAlert } = useModal();

  const { mutate: updateQuota } = useUpdateQuota(apiKeyData?.id || '');

  const [isExampleDropdownOpen, setIsExampleDropdownOpen] = useState(false);

  const [quotaInputValue, setQuotaInputValue] = useState('');
  const [quotaDropdownValue, setQuotaDropdownValue] = useState('D');
  const [isQuotaDropdownOpen, setIsQuotaDropdownOpen] = useState(false);

  // 초기값 저장 (변경사항 비교용)
  const initialValuesRef = useRef({
    quotaInputValue: '',
    quotaDropdownValue: 'D',
  });

  useEffect(() => {
    if (apiKeyData?.quota) {
      const initialQuotaInputValue = apiKeyData.quota.value?.toString() || '';
      const initialQuotaDropdownValue = apiKeyData.quota.type || 'D';
      
      setQuotaInputValue(initialQuotaInputValue);
      setQuotaDropdownValue(initialQuotaDropdownValue);
      
      // 초기값 저장
      initialValuesRef.current = {
        quotaInputValue: initialQuotaInputValue,
        quotaDropdownValue: initialQuotaDropdownValue,
      };
    }
  }, [apiKeyData]);

  const handleExampleDropdownToggle = () => {
    setIsExampleDropdownOpen(!isExampleDropdownOpen);
  };

  const handleExampleDropdownSelect = (_value: string) => {
    setIsExampleDropdownOpen(false);
  };

  const handleQuotaDropdownToggle = () => {
    setIsQuotaDropdownOpen(!isQuotaDropdownOpen);
  };

  const handleQuotaDropdownSelect = (value: string) => {
    setQuotaDropdownValue(value);
    setIsQuotaDropdownOpen(false);
  };

  const handleSave = () => {
    if (!apiKeyData?.id) {
      openAlert({
        title: '알림',
        message: 'API Key 정보를 찾을 수 없습니다.',
      });
      return;
    }

    // 변경사항 확인
    const hasChanges = 
      quotaInputValue !== initialValuesRef.current.quotaInputValue ||
      quotaDropdownValue !== initialValuesRef.current.quotaDropdownValue;
    
    if (!hasChanges) {
      openAlert({
        title: '안내',
        message: '수정된 내용이 없습니다.',
        confirmText: '확인',
      });
      return;
    }

    if (!quotaInputValue || Number(quotaInputValue) <= 0) {
      openAlert({
        title: '알림',
        message: 'Quota 값을 올바르게 입력해주세요.',
      });
      return;
    }

    const params = {
      quota: {
        type: quotaDropdownValue,
        value: Number(quotaInputValue),
      },
    };

    updateQuota(params, {
      onSuccess: () => {
        openAlert({
          title: '완료',
          message: '수정사항이 저장되었습니다.',
          confirmText: '확인',
          onConfirm: () => {
            onClose();
            window.dispatchEvent(new CustomEvent('quotaUpdated'));
          },
        });
      },
      onError: () => {
        openAlert({
          title: '실패',
          message: 'Quota 수정에 실패했습니다.',
        });
      },
    });
  };

  const handleCancel = () => {
    openConfirm({
      message: '화면을 나가시겠어요?\n입력한 정보가 저장되지 않을 수 있습니다.',
      title: '안내',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        onClose();
      },
      onCancel: () => {
        // Cancel handling
      },
    });
  };

  return (
    <>
      <UILayerPopup
        isOpen={isOpen}
        onClose={onClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          <UIPopupAside>
            <UIPopupHeader title='Quota 수정' description='' position='left' />
            <UIPopupBody>
              <UIArticle></UIArticle>
            </UIPopupBody>
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={false} onClick={handleSave}>
                    저장
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        <section className='section-popup-content'>
          <UIPopupHeader title='Quota 수정' position='right' description='단위시간별 Quota를 수정할 수 있습니다.' />

          <UIPopupBody>
            <UIArticle>
              <div className='card-form-box'>
                <UIFormField gap={16} direction='column'>
                  <UITypography variant='title-4' className='secondary-neutral-800 !text-lg text-title-4-sb' required={false}>
                    입력예시
                  </UITypography>
                  <UIUnitGroup gap={16} direction='row'>
                    <UIFormField gap={8} direction='column'>
                      <UITypography variant='body-1' className='secondary-neutral-800 text-sb'>
                        김신한
                      </UITypography>
                      <UIUnitGroup gap={16} direction='row' vAlign='center'>
                        <div className='flex-1'>
                          <UIInput.Text
                            value={'300'}
                            disabled={true}
                            onChange={_e => {
                              // Disabled input
                            }}
                          />
                        </div>
                        <UITypography variant='body-1' className='secondary-neutral-800 text-sb'>
                          회
                        </UITypography>
                        <div>/</div>
                        <div className='flex-1'>
                          <UIDropdown
                            value={'D'}
                            placeholder='일'
                            options={[
                              { value: 'D', label: '일' },
                              { value: 'M', label: '월' },
                              { value: 'Y', label: '년' },
                            ]}
                            disabled={true}
                            isOpen={isExampleDropdownOpen}
                            onClick={handleExampleDropdownToggle}
                            onSelect={handleExampleDropdownSelect}
                          />
                        </div>
                      </UIUnitGroup>
                      <div>
                        <UIList
                          gap={4}
                          direction='column'
                          className='ui-list_bullet'
                          data={[
                            {
                              dataItem: (
                                <UITypography variant='body-2' className='secondary-neutral-600'>
                                  호출 가능 건수를 1일 기준 300회로 설정하고 싶은 경우, 다음과 같이 설정할 수 있습니다. (300회/일)
                                </UITypography>
                              ),
                            },
                          ]}
                        />
                      </div>
                    </UIFormField>
                  </UIUnitGroup>
                </UIFormField>
              </div>
            </UIArticle>

            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                  Quota
                </UITypography>

                <UIFormField gap={8} direction='column'>
                  <UITypography variant='body-1' className='secondary-neutral-800 text-sb'>
                    {apiKeyData?.name || '-'}
                  </UITypography>
                  <UIUnitGroup gap={16} direction='row' vAlign='center'>
                    <div className='flex-1'>
                      <UIInput.Text
                        value={quotaInputValue}
                        type='number'
                        placeholder='0'
                        onChange={e => {
                          const value = e.target.value;
                          
                          // 빈 문자열 허용
                          if (value === '') {
                            setQuotaInputValue('');
                            return;
                          }
                          
                          // 숫자가 아닌 문자 제거
                          const numbersOnly = value.replace(/\D/g, '');
                          
                          // 숫자가 없으면 빈 문자열로 설정
                          if (numbersOnly === '') {
                            setQuotaInputValue('');
                            return;
                          }
                          
                          // 0으로 시작하는 숫자는 허용하지 않음 (이전 값 유지)
                          if (numbersOnly.startsWith('0')) {
                            return;
                          }
                          
                          // 숫자만 있고 0으로 시작하지 않으면 설정
                          setQuotaInputValue(numbersOnly);
                        }}
                        onKeyDown={e => {
                          // 숫자 키는 항상 허용 (Edge 100 호환성을 위해 조기 return)
                          const isNumberKey = /^\d$/.test(e.key);
                          if (isNumberKey) {
                            return; // preventDefault 호출하지 않음
                          }

                          // 허용된 키들
                          const allowedKeys = [
                            'Backspace',
                            'Delete',
                            'ArrowLeft',
                            'ArrowRight',
                            'ArrowUp',
                            'ArrowDown',
                            'Tab',
                            'Home',
                            'End',
                          ];
                          const isAllowedKey = allowedKeys.includes(e.key);
                          
                          // Ctrl 조합 키 허용 (대소문자 모두)
                          const isCtrlA = e.ctrlKey && (e.key === 'a' || e.key === 'A');
                          const isCtrlC = e.ctrlKey && (e.key === 'c' || e.key === 'C');
                          const isCtrlV = e.ctrlKey && (e.key === 'v' || e.key === 'V');
                          const isCtrlX = e.ctrlKey && (e.key === 'x' || e.key === 'X');
                          
                          // 허용되지 않은 키(문자 키 등)는 차단
                          if (!isAllowedKey && !isCtrlA && !isCtrlC && !isCtrlV && !isCtrlX) {
                            e.preventDefault();
                          }
                        }}
                        onPaste={e => {
                          e.preventDefault();
                          const pastedText = e.clipboardData.getData('text');
                          
                          // 문자가 포함되어 있으면 입력 방지
                          if (!/^\d+$/.test(pastedText)) {
                            return;
                          }
                          
                          // 숫자만 있고 0으로 시작하지 않으면 설정
                          if (pastedText && !pastedText.startsWith('0')) {
                            setQuotaInputValue(pastedText);
                          }
                        }}
                      />
                    </div>
                    <UITypography variant='body-1' className='secondary-neutral-800 text-sb'>
                      회
                    </UITypography>
                    <div>/</div>
                    <div className='flex-1'>
                      <UIDropdown
                        value={quotaDropdownValue}
                        placeholder='일'
                        options={[
                          { value: 'MIN', label: '분' },
                          { value: 'HR', label: '시' },
                          { value: 'D', label: '일' },
                          { value: 'W', label: '주' },
                          { value: 'M', label: '월' },
                        ]}
                        isOpen={isQuotaDropdownOpen}
                        onClick={handleQuotaDropdownToggle}
                        onSelect={handleQuotaDropdownSelect}
                      />
                    </div>
                  </UIUnitGroup>
                </UIFormField>
              </UIFormField>
            </UIArticle>
          </UIPopupBody>
        </section>
      </UILayerPopup>
    </>
  );
};
