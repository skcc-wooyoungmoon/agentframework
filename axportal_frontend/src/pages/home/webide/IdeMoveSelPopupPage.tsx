import { useEffect, useState } from 'react';

import { useAtomValue, useSetAtom } from 'jotai';

import { IdeStep2VerPickPopupPage, IdeStep3DwPickPopupPage } from '@/pages/home';
import { useCreateIde, useDeleteIde, useGetIde } from '@/services/home/webide/ide.services';
// ✅ CreateIdeRequest import 제거
import type { IdeDeleteReq } from '@/services/home/webide/types';
import { useUser } from '@/stores/auth/useUser';
import { ideActionAtom, ideTypeAtom, ideTerminateSuccessAtom } from '@/stores/home/webideStore';

interface IDEStatus {
    type: 'jupyter' | 'vscode' | null;
    name: string;
    status: 'active' | 'inactive' | 'starting' | 'creating';
    url?: string;
    ideId?: string;
}

export const IdeMoveSelPopupPage = () => {
    const { user } = useUser();
    const setTerminateSuccess = useSetAtom(ideTerminateSuccessAtom);
    const setIdeAction = useSetAtom(ideActionAtom);

    const [selectedIDE, setSelectedIDE] = useState<'jupyter' | 'vscode' | null>('jupyter');
    const [isStep2PopupOpen, setIsStep2PopupOpen] = useState(false);
    const [isStep3PopupOpen, setIsStep3PopupOpen] = useState(false);
    const [selectedVersion, setSelectedVersion] = useState<string>('');
    const [, setSelectedDwAccount] = useState<string>('');
    const [, setDwAccountUsed] = useState<boolean>(false);
    const [, setError] = useState<string | null>(null);

    const [ideStatuses, setIdeStatuses] = useState<IDEStatus[]>([
        {
            type: 'jupyter',
            name: 'Jupyter Notebook',
            status: 'inactive',
            url: undefined,
        },
        {
            type: 'vscode',
            name: 'VS Code',
            status: 'inactive',
            url: undefined,
        },
    ]);

    const { data: jupyterData, refetch: refetchJupyter } = useGetIde(
        {
            userName: user?.userInfo?.memberId && user.userInfo.memberId !== '사용자 이름 없음' && user.userInfo.memberId.trim() !== '' ? user.userInfo.memberId : 'testuser',
            ideType: 'jupyter',
        },
        { enabled: false }
    );

    const { data: vscodeData, refetch: refetchVscode } = useGetIde(
        {
            userName: user?.userInfo?.memberId && user.userInfo.memberId !== '사용자 이름 없음' && user.userInfo.memberId.trim() !== '' ? user.userInfo.memberId : 'testuser',
            ideType: 'vscode',
        },
        { enabled: false }
    );

    const createIdeMutation = useCreateIde({
        onSuccess: () => {
            refetchJupyter();
            refetchVscode();
            setSelectedVersion('');
            setSelectedDwAccount('');
            setDwAccountUsed(false);
            setError(null);
            setIsStep2PopupOpen(false);
            setIsStep3PopupOpen(false);
        },
        onError: () => {
            setError('IDE 생성에 실패했습니다.');
            setSelectedVersion('');
            setSelectedDwAccount('');
            setDwAccountUsed(false);
            setIsStep2PopupOpen(false);
            setIsStep3PopupOpen(false);
        },
    });

    const deleteIdeMutation = useDeleteIde({
        onSuccess: () => {
            if (selectedIDE === 'jupyter') {
                refetchJupyter();
            } else if (selectedIDE === 'vscode') {
                refetchVscode();
            }
            setError(null);
            // 종료 완료 알림
            setTerminateSuccess(prev => ({ success: true, seq: prev.seq + 1 }));
        },
        onError: () => {
            setError('IDE 종료에 실패했습니다.');
        },
    });

    useEffect(() => {
        if (jupyterData) {
            if (jupyterData.inUse && jupyterData.items.length > 0) {
                const firstItem = jupyterData.items[0];
                let status: 'active' | 'starting' | 'inactive' | 'creating' = 'inactive';

                if (firstItem.status === 'RUNNING') {
                    status = 'active';
                } else if (firstItem.status === 'STARTING') {
                    status = 'starting';
                }  else if (firstItem.status === 'CREATING') {
                    status = 'creating';
                } else {
                    status = 'inactive';
                }

                setIdeStatuses(prev =>
                    prev.map(ide =>
                        ide.type === 'jupyter'
                            ? {
                                ...ide,
                                status: status,
                                url: status === 'active' ? firstItem.ingressUrl : undefined,
                                ideId: firstItem.ideId,
                            }
                            : ide
                    )
                );
            } else {
                setIdeStatuses(prev =>
                    prev.map(ide =>
                        ide.type === 'jupyter'
                            ? {
                                ...ide,
                                status: 'inactive',
                                url: undefined,
                                ideId: undefined,
                            }
                            : ide
                    )
                );
            }
        }
    }, [jupyterData]);

    useEffect(() => {
        if (vscodeData) {
            if (vscodeData.inUse && vscodeData.items.length > 0) {
                const firstItem = vscodeData.items[0];
                let status: 'active' | 'starting' | 'inactive' = 'inactive';

                if (firstItem.status === 'RUNNING') {
                    status = 'active';
                } else if (firstItem.status === 'STARTING') {
                    status = 'starting';
                } else {
                    status = 'inactive';
                }

                setIdeStatuses(prev =>
                    prev.map(ide =>
                        ide.type === 'vscode'
                            ? {
                                ...ide,
                                status: status,
                                url: status === 'active' ? firstItem.ingressUrl : undefined,
                                ideId: firstItem.ideId,
                            }
                            : ide
                    )
                );
            } else {
                setIdeStatuses(prev =>
                    prev.map(ide =>
                        ide.type === 'vscode'
                            ? {
                                ...ide,
                                status: 'inactive',
                                url: undefined,
                                ideId: undefined,
                            }
                            : ide
                    )
                );
            }
        }
    }, [vscodeData]);

    const handleCreateIDE = () => {
        if (!selectedIDE) return;
        setIsStep2PopupOpen(true);
    };

    const handleStep2Close = () => {
        setIsStep2PopupOpen(false);
        // 팝업 취소 시 액션 상태 초기화
        setIdeAction({ action: null, seq: 0 });
    };

    const handleStep2Next = (versionData: string) => {
        setSelectedVersion(versionData);
        setIsStep2PopupOpen(false);
        setIsStep3PopupOpen(true);
    };

    const handleStep3Close = () => {
        setIsStep3PopupOpen(false);
        // IDE 생성 완료 후 액션 상태 초기화하여 로그인 시 재실행 방지
        setIdeAction({ action: null, seq: 0 });
    };

    // ✅ handleCreateIdeRequest 함수 완전히 제거

    const handleStep3Prev = () => {
        setIsStep3PopupOpen(false);
        setIsStep2PopupOpen(true);
    };

    const handleStep3DwSelect = (dwAccount: string, isUsed: boolean) => {
        setSelectedDwAccount(dwAccount);
        setDwAccountUsed(isUsed);
    };

    const handleTerminateIDE = async (targetIdeType?: 'jupyter' | 'vscode' | null) => {
        const ideTypeToTerminate = targetIdeType ?? selectedIDE;

        if (!ideTypeToTerminate) return;

        const selectedIDEStatus = ideStatuses.find(ide => ide.type === ideTypeToTerminate);
        if (!selectedIDEStatus?.ideId) {
            setError('IDE ID를 찾을 수 없습니다.');
            return;
        }

        let ideItemData = null;
        if (ideTypeToTerminate === 'jupyter' && jupyterData?.items && jupyterData.items.length > 0) {
            ideItemData = jupyterData.items[0];
        } else if (ideTypeToTerminate === 'vscode' && vscodeData?.items && vscodeData.items.length > 0) {
            ideItemData = vscodeData.items[0];
        }

        if (!ideItemData) {
            setError('IDE 정보를 찾을 수 없습니다.');
            return;
        }

        const deleteRequest: IdeDeleteReq = {
            ideId: ideItemData.ideId,
            userId: ideItemData.userId,
            username: ideItemData.username,
            ide: ideItemData.ide,
            status: ideItemData.status,
            prjSeq: ideItemData.prjSeq,
            ingressUrl: ideItemData.ingressUrl,
            pythonVer: ideItemData.pythonVer,
        };

        try {
            deleteIdeMutation.mutate(deleteRequest);
        } catch {
            setError('IDE 종료에 실패했습니다.');
        }
    };

    const actionTrigger = useAtomValue(ideActionAtom);
    const ideType = useAtomValue(ideTypeAtom);

    useEffect(() => {
        if (!actionTrigger || !actionTrigger.action) return;

        setSelectedIDE(ideType);

        if (actionTrigger.action === 'create') {
            handleCreateIDE();
        } else if (actionTrigger.action === 'terminate') {
            handleTerminateIDE(ideType);
        }
    }, [actionTrigger.seq]);

    return (
        <>
            <IdeStep2VerPickPopupPage
                isOpen={isStep2PopupOpen}
                onClose={handleStep2Close}
                selectedTool={selectedIDE}
                onNext={handleStep2Next}
            />

            <IdeStep3DwPickPopupPage
                isOpen={isStep3PopupOpen}
                onClose={handleStep3Close}
                selectedTool={selectedIDE}
                selectedVersion={selectedVersion}
                onPrev={handleStep3Prev}
                onDwSelect={handleStep3DwSelect}
                onSuccess={() => {
                    refetchJupyter();
                    refetchVscode();
                    setError(null);
                }}
                isCreating={createIdeMutation.isPending}
            />
        </>
    );
};