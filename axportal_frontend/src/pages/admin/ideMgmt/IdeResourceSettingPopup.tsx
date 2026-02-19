import { useCallback, useEffect, useRef, useState } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { ImageType, useGetImageResource, useUpdateImageResource } from '@/services/admin/ideMgmt';
import {
  UIArticle,
  UIFormField,
  UIGroup,
  UIPopupBody,
  UIPopupFooter,
  UIPopupHeader,
  UIUnitGroup
} from '@/components/UI/molecules';
import { UIInput } from '@/components/UI/molecules/input/UIInput';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';

interface IdeResourceSettingPopupProps {
  isOpen: boolean;
  onClose: () => void;
  onSave?: (data: { jupyterCount: number; vscodeCount: number }) => void;
}

/**
 * 관리 > IDE 관리 > 환경설정 팝업
 */
export const IdeResourceSettingPopup: React.FC<IdeResourceSettingPopupProps> = ({ isOpen, onClose }) => {
  const { showCancelConfirm, showEditComplete } = useCommonPopup();
  const { mutate: updateImageResource, isPending: isUpdating } = useUpdateImageResource();
  const { data: resourceData } = useGetImageResource();

  const [jupyterLimitCount, setJupyterLimitCount] = useState('10');
  const [vscodeLimitCount, setVscodeLimitCount] = useState('10');

  // 초기화 여부를 추적 (같은 팝업 세션에서 한 번만 초기화)
  // - 팝업이 열릴 때: resourceData로 state 초기화
  // - 팝업이 닫힐 때: 플래그 리셋 (다음 오픈 시 재초기화 가능)
  const hasInitializedRef = useRef(false);

  // 특정 타입의 limitCnt를 가져오는 헬퍼 함수 (메모이제이션)
  const getLimitCount = useCallback(
    (imageType: ImageType): number => {
      const resource = resourceData?.find(item => item.imgG === imageType);
      return resource?.limitCnt ?? 0;
    },
    [resourceData]
  );

  // 1. isOpen 변경 관리: 팝업이 닫힐 때 초기화 플래그 리셋
  useEffect(() => {
    if (!isOpen) {
      hasInitializedRef.current = false;
    }
  }, [isOpen]);

  // 2. resourceData 변경 관리: 팝업이 열려있고 아직 초기화하지 않았을 때만 초기화
  useEffect(() => {
    if (isOpen && resourceData && !hasInitializedRef.current) {
      setJupyterLimitCount(getLimitCount(ImageType.JUPYTER).toString());
      setVscodeLimitCount(getLimitCount(ImageType.VSCODE).toString());
      hasInitializedRef.current = true;
    }
  }, [isOpen, resourceData, getLimitCount]);

  // 숫자만 입력 가능하도록 처리 (0 제외, 빈 값 허용)
  const handleNumberInput = (value: string, setter: React.Dispatch<React.SetStateAction<string>>) => {
    // 빈 값 허용
    if (value === '') {
      setter('');
      return;
    }

    // 숫자만 허용
    const numericValue = value.replace(/[^0-9]/g, '');

    // 앞자리 0 제거 (예: '01' -> '1')
    const trimmedValue = numericValue.replace(/^0+/, '') || '0';

    setter(trimmedValue);
  };

  // 유효성 검사: 빈 값이 아니고, 1 이상 99 이하의 숫자인지 확인
  const isValidCount = (value: string) => {
    if (value === '') return false;
    const num = parseInt(value, 10);
    return !isNaN(num) && num >= 1 && num <= 99;
  };

  // 99 초과 여부 확인
  const isJupyterOverMax = parseInt(jupyterLimitCount, 10) > 99;
  const isVscodeOverMax = parseInt(vscodeLimitCount, 10) > 99;

  // 폼 유효성 검사
  const isFormValid = isValidCount(jupyterLimitCount) && isValidCount(vscodeLimitCount);

  // 취소 핸들러
  const handleCancel = () => {
    showCancelConfirm({
      onConfirm: () => {
        onClose();
      },
    });
  };

  // 저장 핸들러
  const handleSave = () => {
    const jupyterCount = parseInt(jupyterLimitCount, 10);
    const vscodeCount = parseInt(vscodeLimitCount, 10);

    // 배열로 전송 (1번의 요청으로 Jupyter, VS Code 동시 업데이트)
    updateImageResource(
      [
        { imgG: ImageType.JUPYTER, limitCnt: jupyterCount },
        { imgG: ImageType.VSCODE, limitCnt: vscodeCount },
      ],
      {
        onSuccess: () => {
          showEditComplete({
            onConfirm: () => {
              onClose();
            },
          });
        },
      }
    );
  };

  return (
    <UILayerPopup
      isOpen={isOpen}
      onClose={onClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        <UIPopupAside>
          <UIPopupHeader title='환경설정' description='' position='left' />
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} onClick={handleSave} disabled={!isFormValid || isUpdating}>
                  저장
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      <section className='section-popup-content'>
        <UIPopupBody>
          {/* 환경 설정 설명 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UIGroup gap={4} direction='column'>
                <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb' required={false}>
                  환경 설정
                </UITypography>
                <UITypography variant='body-2' className='secondary-neutral-600'>
                  Jupyter, VS Code의 기본 생성 개수를 설정할 수 있습니다. 사용자 1명 기준으로 적용됩니다.
                </UITypography>
              </UIGroup>
            </UIFormField>
          </UIArticle>

          {/* 도구별 생성 개수 현황 */}
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                도구별 생성 개수
              </UITypography>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  <colgroup>
                    <col style={{ width: '152px' }} />
                    <col style={{ width: 'calc(50% - 152px)' }} />
                    <col style={{ width: '152px' }} />
                    <col style={{ width: 'calc(50% - 152px)' }} />
                  </colgroup>
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          Jupyter Notebook
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {getLimitCount(ImageType.JUPYTER)}개
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          VS Code
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {getLimitCount(ImageType.VSCODE)}개
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>

          {/* 입력 필드 */}
          <UIArticle>
            <UIUnitGroup gap={26} direction='row' align='start'>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  Jupyter Notebook
                </UITypography>
                <UIInput.Text
                  value={jupyterLimitCount}
                  placeholder=''
                  onChange={e => handleNumberInput(e.target.value, setJupyterLimitCount)}
                  error={isJupyterOverMax ? '최대 99개까지 입력 가능합니다.' : undefined}
                />
              </UIFormField>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  VS Code
                </UITypography>
                <UIInput.Text
                  value={vscodeLimitCount}
                  placeholder=''
                  onChange={e => handleNumberInput(e.target.value, setVscodeLimitCount)}
                  error={isVscodeOverMax ? '최대 99개까지 입력 가능합니다.' : undefined}
                />
              </UIFormField>
            </UIUnitGroup>
          </UIArticle>
        </UIPopupBody>
      </section>
    </UILayerPopup>
  );
};
