import React, { useCallback, useEffect, useMemo, useState } from 'react';

import { useAtom } from 'jotai';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIButton2, UIIcon2, UITooltip, UITypography } from '@/components/UI/atoms';
import { UIRadio2 } from '@/components/UI/atoms/UIRadio2';
import type { UIStepperItem } from '@/components/UI/molecules';
import {
  UIArticle,
  UIDropdown,
  UIFormField,
  UIInput,
  UIPopupBody,
  UIPopupFooter,
  UIPopupHeader,
  UIStepper,
  UIUnitGroup
} from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { useGetDwAccount } from '@/services/home/webide/ide.services';
import { useModal } from '@/stores/common/modal';
import { useUser } from '@/stores/auth/useUser';
import { ideCreWizardAtom } from '@/stores/home/webide/ideCreWizard.atoms';

import { IdeDwAccountListPopup } from './IdeDwAccountListPopup';

/** IdeCreateStep3Popup Props 타입 */
interface IdeCreateStep3PopupProps {
  /** 팝업 표시 여부 */
  isOpen: boolean;
  /** 팝업 닫기 핸들러 */
  onClose: () => void;
  /** 이전 버튼 클릭 핸들러 */
  onPrev: () => void;
  /** 다음 버튼 클릭 핸들러 */
  onNext: () => void;
}

/** 스테퍼 아이템 */
const stepperItems: UIStepperItem[] = [
  { id: 'step1', label: '프로젝트 선택', step: 1 },
  { id: 'step2', label: '도구 및 이미지 선택', step: 2 },
  { id: 'step3', label: 'DW 계정 선택', step: 3 },
  { id: 'step4', label: '자원 선택', step: 4 },
];

/**
 * IDE 생성 Step3 - DW 계정 선택 팝업
 */
export const IdeCreateStep3Popup: React.FC<IdeCreateStep3PopupProps> = ({ isOpen, onClose, onPrev, onNext }) => {
  // 모달 훅
  const { openModal } = useModal();
  const { showCancelConfirm } = useCommonPopup();

  // 사용자 정보
  const { user } = useUser();

  // 위자드 데이터 상태
  const [wizardData, setWizardData] = useAtom(ideCreWizardAtom);
  const selectedUsage = wizardData.dwAccountUsage;
  const accountType = wizardData.dwAccountType;
  const serviceAccountValue = wizardData.selectedDwAccountId;
  const selectedId = wizardData.selectedDwAccountId;

  const setSelectedUsage = (value: string) => {
    setWizardData(prev => ({ ...prev, dwAccountUsage: value }));
  };
  const setAccountType = (value: string) => {
    setWizardData(prev => ({ ...prev, dwAccountType: value }));
  };
  const setServiceAccountValue = (value: string) => {
    setWizardData(prev => ({ ...prev, selectedDwAccountId: value }));
  };
  const setSelectedId = (id: string) => {
    setWizardData(prev => ({ ...prev, selectedDwAccountId: id }));
  };

  // 계정 유형 선택 상태
  const [isAccountTypeDropdownOpen, setIsAccountTypeDropdownOpen] = useState(false);

  // 현재 페이지
  const [currentPage, setCurrentPage] = useState(1);

  // 페이지당 아이템 수
  const pageSize = 12;

  /**
   * 다음 버튼 비활성화 여부
   */
  const isNextDisabled = useMemo(() => {
    if (selectedUsage === 'notUse') return false;
    if (selectedUsage === 'use') {
      if (accountType === '1') return !selectedId;
      if (accountType === '2') return serviceAccountValue.trim().length === 0;
    }
    return true;
  }, [selectedUsage, accountType, selectedId, serviceAccountValue]);

  /**
   * 권한 계정 목록 팝업 오픈 핸들러
   */
  const handleOpenDwAccountList = useCallback(() => {
    openModal({
      title: '권한 계정 목록',
      body: <IdeDwAccountListPopup />,
      type: 'large',
      showFooter: false,
    });
  }, [openModal]);

  // DW 계정 목록 조회
  const { data: dwAccountRes, isSuccess } = useGetDwAccount({ userId: user.userInfo.adxpUserId }, { enabled: isOpen && !!user.userInfo.adxpUserId && selectedUsage === 'use' });

  useEffect(() => {
    if (isSuccess && selectedUsage === 'use' && accountType === '1' && dwAccountRes?.length === 0) {
      handleOpenDwAccountList();
    }
  }, [isSuccess, selectedUsage, accountType, dwAccountRes, handleOpenDwAccountList]);

  // 그리드에 표시할 데이터
  const dwAccountList = useMemo(() => {
    return (dwAccountRes || []).map((item, index) => ({
      ...item,
      id: item.dbAccountId,
      no: index + 1,
      accountId: item.dbAccountId,
    }));
  }, [dwAccountRes]);

  // 선택된 아이템 목록 (UIGrid 전달용)
  const selectedDataList = useMemo(() => {
    const selected = dwAccountList.find(item => item.id === selectedId);
    return selected ? [selected] : [];
  }, [dwAccountList, selectedId]);

  // 현재 페이지의 아이템 목록
  const pagedItems = useMemo(() => {
    const startIndex = (currentPage - 1) * pageSize;
    return dwAccountList.slice(startIndex, startIndex + pageSize);
  }, [dwAccountList, currentPage, pageSize]);

  // 전체 페이지 수
  const totalPages = Math.ceil(dwAccountList.length / pageSize) || 1;

  /**
   * DW 계정 사용 여부 변경 핸들러
   */
  const handleUsageChange = (value: string) => {
    setSelectedUsage(value);
  };

  // 그리드 컬럼 정의
  const columnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no' as const,
        width: 56,
        minWidth: 56,
        maxWidth: 56,
        cellClass: 'text-center',
        headerClass: 'text-center',
        cellStyle: {
          textAlign: 'center',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        },
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '계정 ID',
        field: 'accountId' as const,
        flex: 1,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
    ],
    []
  );

  /**
   * 체크박스 선택 핸들러
   */
  const handleCheck = (selectedRows: any[]) => {
    setSelectedId(selectedRows[0]?.id || '');
  };

  /**
   * 행 클릭 핸들러
   */
  const handleRowClick = (params: any) => {
    setSelectedId(params.data.id);
  };

  /**
   * 취소 버튼 클릭 핸들러
   */
  const handleCancel = () => {
    showCancelConfirm({
      onConfirm: onClose,
    });
  };

  /**
   * 이전 버튼 클릭 핸들러
   */
  const handlePrev = () => {
    onPrev();
  };

  /**
   * 다음 버튼 클릭 핸들러
   */
  const handleNext = () => {
    onNext();
  };

  return (
    <UILayerPopup
      isOpen={isOpen}
      onClose={onClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        <UIPopupAside>
          {/* 좌측 헤더 */}
          <UIPopupHeader title='IDE 생성' position='left' />
          {/* 좌측 바디 - 스테퍼 */}
          <UIPopupBody>
            <UIArticle>
              <UIStepper currentStep={3} items={stepperItems} direction='vertical' />
            </UIArticle>
          </UIPopupBody>
          {/* 좌측 푸터 */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: 80 }} onClick={handleCancel}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue' style={{ width: 80 }} disabled>
                  생성
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      {/* 우측 Contents 영역 */}
      <section className='section-popup-content'>
        {/* 우측 헤더 */}
        <UIPopupHeader title='DW 계정 선택' description='DW 계정 사용 여부를 선택해주세요. 사용을 원할 경우, 원하는 계정을 선택해주세요.' position='right' />

        {/* 우측 바디 */}
        <UIPopupBody>
          {/* DW 계정 사용 여부 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                DW 계정 사용 여부
              </UITypography>
              <UIUnitGroup gap={12} direction='column' align='center'>
                <UIRadio2
                  name='usage'
                  label='미사용'
                  value='notUse'
                  checked={selectedUsage === 'notUse'}
                  onChange={(checked, value) => {
                    if (checked) handleUsageChange(value);
                  }}
                />
                <UIRadio2
                  name='usage'
                  label='사용'
                  value='use'
                  checked={selectedUsage === 'use'}
                  onChange={(checked, value) => {
                    if (checked) handleUsageChange(value);
                  }}
                />
              </UIUnitGroup>
            </UIFormField>
          </UIArticle>

          {/* 계정 유형 (사용 선택 시에만 표시) */}
          {selectedUsage === 'use' && (
            <>
              <UIArticle>
                <UIFormField gap={8} direction='column'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                    계정 유형
                  </UITypography>
                  <UIDropdown
                    value={accountType}
                    options={[
                      { value: '1', label: '사용자 계정' },
                      { value: '2', label: '서비스 계정' },
                    ]}
                    isOpen={isAccountTypeDropdownOpen}
                    onClick={() => setIsAccountTypeDropdownOpen(!isAccountTypeDropdownOpen)}
                    onSelect={(value: string) => {
                      setAccountType(value);
                      setIsAccountTypeDropdownOpen(false);
                    }}
                    placeholder='계정 유형 선택'
                  />
                </UIFormField>
              </UIArticle>

              {/* 서비스 계정 입력 (서비스 계정 선택 시에만 표시) */}
              {accountType === '2' && (
                <UIArticle>
                  <UIFormField gap={8} direction='column'>
                    <div className='flex items-center'>
                      <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                        서비스 계정
                      </UITypography>
                      <UITooltip
                        trigger='click'
                        position='bottom-start'
                        type='notice'
                        title=''
                        items={['사전에 포탈 관리자와 협의하여 업무 목적에 맞는 계정을 사용해주세요.']}
                        bulletType='default'
                        showArrow={false}
                        showCloseButton={true}
                        className='ml-1 relative top-[2px]'
                      >
                        <UIButton2>
                          <UIIcon2 className='ic-system-20-info' />
                        </UIButton2>
                      </UITooltip>
                    </div>
                    <UIInput.Text value={serviceAccountValue} placeholder='직접 입력' onChange={e => setServiceAccountValue(e.target.value)} />
                  </UIFormField>
                </UIArticle>
              )}

              {/* 계정 목록 그리드 (사용자 계정 선택 시에만 표시) */}
              {accountType === '1' && (
                <UIArticle className='article-grid'>
                  <UIListContainer>
                    <UIListContentBox.Header>
                      <div className='flex justify-between w-full items-center'>
                          <UIDataCnt count={dwAccountList.length} prefix='총' unit='건' />
                          {isSuccess && selectedUsage === 'use' && accountType === '1' && dwAccountRes?.length === 0
                            && <UIButton2 className='btn-option-outlined' onClick={handleOpenDwAccountList}>권한 계정 목록</UIButton2>
                          }
                      </div>
                    </UIListContentBox.Header>
                    <UIListContentBox.Body>
                      <UIGrid type='single-select' rowData={pagedItems} columnDefs={columnDefs} onCheck={handleCheck} onClickRow={handleRowClick} selectedDataList={selectedDataList} checkKeyName='id' />
                    </UIListContentBox.Body>
                    <UIListContentBox.Footer>
                      <UIPagination
                        currentPage={currentPage}
                        totalPages={totalPages}
                        onPageChange={setCurrentPage}
                        className='flex justify-center'
                      />
                    </UIListContentBox.Footer>
                  </UIListContainer>
                </UIArticle>
              )}
            </>
          )}
        </UIPopupBody>

        {/* 우측 푸터 */}
        <UIPopupFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='start'>
              <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }} onClick={handlePrev}>
                이전
              </UIButton2>
              <UIButton2 className='btn-secondary-blue' style={{ width: '80px' }} onClick={handleNext} disabled={isNextDisabled}>
                다음
              </UIButton2>
            </UIUnitGroup>
          </UIArticle>
        </UIPopupFooter>
      </section>
    </UILayerPopup>
  );
};
