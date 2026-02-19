import React, { memo, useEffect, useMemo, useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIButton2, UILabel, UIRadio2, UITypography } from '@/components/UI/atoms';
import { UIFormField, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { useGetDwAccount } from '@/services/home/webide/ide.services';
import { useUser } from '@/stores/auth/useUser';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { useModal } from '@/stores/common/modal/useModal';

interface IdeStep3DwPickPopupPageProps {
    isOpen: boolean;
    onClose: () => void;
    selectedTool?: 'jupyter' | 'vscode' | null;
    selectedVersion?: string;
    onPrev?: () => void;
    onDwSelect?: (account: string, used: boolean) => void;
    // ✅ onCreate 제거 - 내부에서 직접 처리
    // ✅ onSuccess 추가 - 생성 성공 시 상위 컴포넌트 알림
    onSuccess?: () => void;
    isCreating?: boolean;
}

export const IdeStep3DwPickPopupPage: React.FC<IdeStep3DwPickPopupPageProps> = ({
                                                                                    isOpen,
                                                                                    onClose,
                                                                                    onPrev,
                                                                                    onDwSelect,
                                                                                    onSuccess,
                                                                                    isCreating = false
                                                                                }) => {
    const [accountUsage, setAccountUsage] = useState<'unused' | 'used'>('unused');
    const [selectedDwAccount, setSelectedDwAccount] = useState<string>('');
    const [page, setPage] = useState(1);
    const size = 12;
    const [isSubmitting, setIsSubmitting] = useState(false);

    // const [ideType] = useAtom(ideTypeAtom);
    const { user } = useUser();

    // 공통 팝업 훅
    const { showCancelConfirm } = useCommonPopup();

    // 모달 훅
    const { openAlert } = useModal();

    // ✅ useCreateIde hook 사용
    // const { mutateAsync: createIde } = useCreateIde();

    // ✅ DW 계정 목록 조회
    const userId = user?.userInfo?.memberId || '';
    const isEnabled = !!userId && isOpen;

    const { data: dwAccountData, isLoading: isDwAccountLoading, error: dwAccountError } = useGetDwAccount(
        { userId },
        { enabled: isEnabled }
    );

    // ✅ API 호출 상태 모니터링
    useEffect(() => {
    }, [userId, isOpen, isEnabled, isDwAccountLoading, dwAccountData, dwAccountError]);

    // ✅ API 응답 데이터를 그리드 형식으로 변환
    const modelData = useMemo(() => {
        const accounts = dwAccountData || [];
        // 백엔드 데이터를 그리드 형식으로 변환
        const gridData = accounts.map((account, index) => ({
            id: account.dbAccountId, // 고유한 accountId를 id로 사용
            no: index + 1,
            accountId: account.dbAccountId,
            accountStatus: account.accountStatus === 'Y' ? '이용가능' : '이용불가',
            //disabled: account.accountStatus !== 'Y',
        }));

        return gridData;
    }, [dwAccountData]);

    const handleNext = async () => {
        const dwAccount = accountUsage === 'used' ? selectedDwAccount : '';
        const dwAccountUsed = accountUsage === 'used';

        if (accountUsage === 'used' && !selectedDwAccount) {
            return;
        }

        if (onDwSelect) {
            onDwSelect(dwAccount, dwAccountUsed);
        }

        try {
            setIsSubmitting(true);

            // const selectedProject = user?.projectList?.find(project => project.active);
            // const prjSeq = Number(selectedProject?.prjSeq) || 0;

            // ✅ API 호출 (await로 응답 대기)
            // await createIde({
            //     prjSeq: [prjSeq as number],
            //     userId,
            //     ideType: ideType as string,
            //     dwAccount,
            //     dwAccountUsed,
            // });

            // ✅ 서버 응답 후에만 완료 모달 표시
            openAlert({
                title: '완료',
                message: 'IDE가 생성이 완료되었습니다.\n지금부터 7일간 선택하신 버전과 설정으로 개발환경을\n사용하실 수 있습니다.',
                confirmText: '확인',
                onConfirm: () => {
                    // 성공 시 상위 컴포넌트에 알림
                    if (onSuccess) {
                        onSuccess();
                    }
                    // 팝업 닫기
                    onClose();
                },
            });

        } catch (error) {
            alert('IDE 생성 중 오류가 발생했습니다.');
        } finally {
            setIsSubmitting(false);
        }
    };

    const isCreateButtonEnabled = !isSubmitting && !isCreating && (
        accountUsage === 'unused' ||
        (accountUsage === 'used' && !!selectedDwAccount && !!modelData.find(item => item.accountId === selectedDwAccount && item.accountStatus === "이용가능"))
    );

    const handlePrev = () => {
        if (isCreating || isSubmitting) {
            return;
        }
        if (onPrev) {
            onPrev();
        }
    };

    const handleAccountUsageChange = (checked: boolean, value: string) => {
        if (checked) {
            const newUsage = value === 'option1' ? 'unused' : 'used';
            setAccountUsage(newUsage);

            // 'used'로 변경될 때 첫 번째 이용가능한 항목 자동 선택
            if (newUsage === 'used') {
                const firstAvailable = modelData.find(item => item.accountStatus === "이용가능");
                if (firstAvailable) {
                    setSelectedDwAccount(firstAvailable.accountId);
                }
            } else {
                // 'unused'로 변경될 때 선택 초기화
                setSelectedDwAccount('');
            }
        }
    };

    const handleCancelClick = () => {
        showCancelConfirm({
            onConfirm: () => {
                onClose();
            },
        });
    };

    const totalCount = modelData.length;
    const paginatedData = React.useMemo(() => {
        const start = (page - 1) * size;
        const paginated = modelData.slice(start, start + size);
        return paginated;
    }, [modelData, page, size]);
    const totalPages = Math.max(1, Math.ceil(totalCount / size));

    const columnDefs: any[] = React.useMemo(
        () => [
            {
                headerName: '',
                field: 'selection',
                width: 50,
                minWidth: 50,
                maxWidth: 50,
                cellClass: 'text-center',
                headerClass: 'text-center',
                cellRenderer: (params: any) => {
                    const isDisabled = params.data.accountStatus === '이용불가';
                    return (
                        <UIRadio2
                            name='dwAccountSelection'
                            checked={selectedDwAccount === params.data.accountId}
                            onChange={() => {
                                if (!isDisabled) {
                                    setSelectedDwAccount(params.data.accountId);
                                }
                            }}
                            disabled={isDisabled}
                        />
                    );
                },
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
                headerName: 'NO',
                field: 'no',
                width: 56,
                minWidth: 56,
                maxWidth: 56,
                cellClass: 'text-center',
                headerClass: 'text-center',
                cellStyle: (params: any) => ({
                    textAlign: 'center',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    opacity: params.data?.disabled ? 0.5 : 1,
                    cursor: params.data?.disabled ? 'not-allowed' : 'pointer',
                    color: params.data?.disabled ? '#999' : 'inherit',
                }),
                cellRenderer: (params: any) => {
                    return params.data?.no || '';
                },
                sortable: false,
                suppressHeaderMenuButton: true,
                suppressSizeToFit: true,
            },
            {
                headerName: '계정ID',
                field: 'accountId',
                width: 576,
                cellStyle: (params: any) => ({
                    paddingLeft: '16px',
                    opacity: params.data?.disabled ? 0.5 : 1,
                    cursor: params.data?.disabled ? 'not-allowed' : 'pointer',
                    color: params.data?.disabled ? '#999' : 'inherit',
                }),
            },
            {
                headerName: '계정 상태',
                field: 'accountStatus',
                flex: 1,
                cellStyle: (params: any) => ({
                    opacity: params.data?.disabled ? 0.5 : 1,
                    cursor: params.data?.disabled ? 'not-allowed' : 'pointer',
                }),
                cellRenderer: memo((params: { value: string }) => {
                    const colorMap: { [key: string]: string } = {
                        이용가능: 'complete',
                        이용불가: 'error',
                    };
                    return (
                        <UILabel variant='badge' intent={colorMap[params.value] as 'complete' | 'error'}>
                            {params.value}
                        </UILabel>
                    );
                }),
            },
        ],
        [selectedDwAccount]
    );

    // 데이터 로드 시 첫 번째 이용가능 항목 자동 선택
    useEffect(() => {
        if (accountUsage === 'used' && modelData.length > 0) {
            const firstAvailable = modelData.find(item => item.accountStatus === "이용가능");
            if (firstAvailable) {
                setSelectedDwAccount(firstAvailable.accountId);
            }
        }
    }, [accountUsage, modelData]);

    // DW 계정 데이터 유효성 검사 및 안내 메시지 표시
    useEffect(() => {
        if (accountUsage === 'used' && !isDwAccountLoading) {
            // 조건 1: 데이터가 0건인 경우
            if (modelData.length === 0) {
                openAlert({
                    title: '안내',
                    message: '사용자 정보에 DW 계정이 존재하지 않습니다.\n계정 사용 여부를 \'미사용\'으로 선택하거나, 포탈 관리자에게 계정 권한을 신청 후 다시 시도해주세요.',
                    confirmText: '확인',
                });
            }
            // 조건 2: 모든 데이터가 '이용불가'인 경우
            else if (modelData.every(item => item.accountStatus === '이용불가')) {
                openAlert({
                    title: '안내',
                    message: '이용 가능한 DW 계정이 없습니다.\n계정 사용 여부를 \'미사용\'으로 선택하거나, 포탈 관리자에게 계정 권한을 신청 후 다시 시도해주세요.',
                    confirmText: '확인',
                });
            }
        }
    }, [accountUsage, modelData, isDwAccountLoading, openAlert]);

    return (
        <>
            <UILayerPopup
                isOpen={isOpen}
                onClose={onClose}
                size='fullscreen'
                showOverlay={true}
                leftContent={
                    <UIPopupAside>
                        <UIPopupHeader title='IDE 생성' position='left' />
                        <UIPopupBody>
                            <UIStepper
                                items={[
                                    { id: 'step1', step: 1, label: '버전 선택' },
                                    { id: 'step2', step: 2, label: 'DW 계정 선택' },
                                ]}
                                currentStep={2}
                                direction='vertical'
                            />
                        </UIPopupBody>
                        <UIPopupFooter>
                            <UIArticle>
                                <UIUnitGroup gap={8} direction='row' align='start'>
                                    <UIButton2
                                        className='btn-tertiary-gray'
                                        style={{ width: '80px' }}
                                        disabled={isCreating || isSubmitting}
                                        onClick={handleCancelClick}
                                    >
                                        취소
                                    </UIButton2>
                                    <UIButton2
                                        className='btn-tertiary-blue'
                                        style={{ width: '80px' }}
                                        disabled={!isCreateButtonEnabled}
                                        onClick={handleNext}
                                    >
                                        {isCreating || isSubmitting ? '생성중...' : '생성'}
                                    </UIButton2>
                                </UIUnitGroup>
                            </UIArticle>
                        </UIPopupFooter>
                    </UIPopupAside>
                }
            >
                <section className='section-popup-content'>
                    <UIPopupHeader title='DW 계정 선택' description='DW 계정 사용을 원하는 경우, 계정을 선택해주세요.' position='right' />
                    <UIPopupBody>
                        <UIArticle>
                            <UIFormField gap={8} direction='column'>
                                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                                    계정 사용 여부
                                </UITypography>
                                <UIUnitGroup gap={12} direction='column' align='start'>
                                    <UIRadio2
                                        name='basic1'
                                        value='option1'
                                        label='미사용'
                                        checked={accountUsage === 'unused'}
                                        onChange={handleAccountUsageChange}
                                        disabled={isCreating || isSubmitting}
                                    />
                                    <UIRadio2
                                        name='basic1'
                                        value='option2'
                                        label='사용'
                                        checked={accountUsage === 'used'}
                                        onChange={handleAccountUsageChange}
                                        disabled={isCreating || isSubmitting}
                                    />
                                </UIUnitGroup>
                            </UIFormField>
                        </UIArticle>
                        {accountUsage === 'used' && (
                            <UIArticle className='article-grid'>
                                <UIListContainer>
                                    <UIListContentBox.Header>
                                        <div className='flex-shrink-0'>
                                            <div style={{ width: '168px', paddingRight: '8px' }}>
                                                <UIDataCnt count={totalCount} prefix='총' unit='건' />
                                            </div>
                                        </div>
                                        <div className='flex-shrink-0'></div>
                                    </UIListContentBox.Header>
                                    <UIListContentBox.Body>
                                        {isDwAccountLoading ? (
                                            <div className="flex items-center justify-center h-64">
                                                <UITypography variant="body-2" className="secondary-neutral-500">
                                                    DW 계정 목록을 불러오는 중입니다...
                                                </UITypography>
                                            </div>
                                        ) : (
                                            <UIGrid
                                                rowData={paginatedData}
                                                columnDefs={columnDefs}
                                                onClickRow={(params: any) => {
                                                    // roleId가 '-299'인 경우 선택 불가 (ProjQuitStep.tsx 패턴)
                                                    if (params.data.accountStatus === '이용불가') {
                                                        return;
                                                    }
                                                    // 선택된 계정 ID 저장
                                                    setSelectedDwAccount(params.data.accountId);
                                                }}
                                            />
                                        )}
                                    </UIListContentBox.Body>
                                    <UIListContentBox.Footer>
                                        <UIPagination
                                            currentPage={page}
                                            totalPages={totalPages}
                                            onPageChange={(newPage: number) => {
                                                setPage(newPage);
                                            }}
                                            className='flex justify-center'
                                        />
                                    </UIListContentBox.Footer>
                                </UIListContainer>
                            </UIArticle>
                        )}
                    </UIPopupBody>
                    <UIPopupFooter>
                        <UIArticle>
                            <UIUnitGroup gap={8} direction='row' align='start'>
                                <UIButton2
                                    className='btn-secondary-gray'
                                    style={{ width: '80px' }}
                                    onClick={handlePrev}
                                    disabled={isCreating || isSubmitting}
                                >
                                    이전
                                </UIButton2>
                            </UIUnitGroup>
                        </UIArticle>
                    </UIPopupFooter>
                </section>
            </UILayerPopup>
        </>
    );
};