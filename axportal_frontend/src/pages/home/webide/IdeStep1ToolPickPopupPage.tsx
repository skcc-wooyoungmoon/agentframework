import React, {useEffect,useState} from 'react';
import {useAtom, useAtomValue, useSetAtom} from 'jotai';

import {UIButton2, UIDataCnt, UILabel, UIPagination} from '@/components/UI/atoms';
import {UIArticle, UIUnitGroup} from '@/components/UI/molecules';
import { UIModalContent } from '@/components/UI/molecules/modal';
import { useGetIde} from '@/services/home/webide/ide.services';
import { useGetAccessToken} from '@/services/home/webide/ide.services';
import { useUser } from '@/stores/auth/useUser';
import { useModal } from '@/stores/common/modal';
import { ideActionAtom, ideTypeAtom, ideTerminateSuccessAtom } from '@/stores/home/webideStore';
import {UIListContainer, UIListContentBox} from "@/components/UI/molecules/list";
import {UIGrid} from "@/components/UI/molecules/grid";
import {useCopyHandler} from "@/hooks/common/util";

export const IdeStep1ToolPickPopupPage = () => {
  // 공유 상태(Jotai)로 선택 값을 관리 (jupyter | vscode)
  const [ideType, setIdeType] = useAtom(ideTypeAtom);
  const setIdeAction = useSetAtom(ideActionAtom);
  const terminateSuccess = useAtomValue(ideTerminateSuccessAtom);
  const setTerminateSuccess = useSetAtom(ideTerminateSuccessAtom);
  const { closeAllModals, openConfirm, openAlert } = useModal();
  const { user } = useUser();
  // IDE 상태 조회 - 팝업이 열릴 때 자동으로 조회
  const { data: jupyterData } = useGetIde({
    userName: user?.userInfo?.memberId && user.userInfo.memberId !== '사용자 이름 없음' && user.userInfo.memberId.trim() !== '' ? user.userInfo.memberId : 'testuser',
    ideType: 'jupyter',
  });

  const { data: vscodeData } = useGetIde({
    userName: user?.userInfo?.memberId && user.userInfo.memberId !== '사용자 이름 없음' && user.userInfo.memberId.trim() !== '' ? user.userInfo.memberId : 'testuser',
    ideType: 'vscode',
  });

  // IDE 사용 여부 판단 - 서버 응답의 inUse 필드 사용
  const jupyterInUse = jupyterData?.inUse ?? false;
  const vscodeInUse = vscodeData?.inUse ?? false;

  const [selectedDataList, setSelectedDataList] = useState<any[]>([]); // 타입 추가!


    // 그리드 데이터 생성 - API 데이터 기반
  const projectData = [
    {
      no: 1,
      name: 'Jupyter Notebook',
      status: jupyterInUse ? '사용중' : '미사용',
      value: 'jupyter',
    },
    {
      no: 2,
      name: 'VS Code',
      status: vscodeInUse ? '사용중' : '미사용',
      value: 'vscode',
    },
  ];

  // 그리드 컬럼 정의
  const columnDefs: any = React.useMemo(
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
        headerName: '도구명',
        field: 'name' as const,
        width: 340,
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
      {
        headerName: '상태',
        field: 'status',
        flex: 1,
        cellRenderer: React.memo((params: any) => {
          const getStatusIntent = (status: string) => {
            switch (status) {
              case '사용중':
                return 'complete';
              case '미사용':
                return 'progress';
              case '실패':
                return 'error';
              case '취소':
                return 'stop';
              default:
                return 'complete';
            }
          };
          return (
            <UILabel variant='badge' intent={getStatusIntent(params.value)}>
              {params.value}
            </UILabel>
          );
        }),
      },
    ],
    []
  );

  // Footer UI states - using useState to trigger re-renders
  const [showTerminate, setShowTerminate] = useState<boolean>(false);
  const [primaryText, setPrimaryText] = useState<'생성하기' | '바로가기'>('생성하기');

    useEffect(() => {
        const selected = projectData.find(item => item.value === ideType);
        if (selected) {
            const inUseByStatus = selected.status === '사용중';
            setShowTerminate(inUseByStatus);
            setPrimaryText(inUseByStatus ? '바로가기' : '생성하기');
        }
    }, [ideType, projectData]);

  useEffect(() => {
    if (projectData && projectData.length > 0 && selectedDataList.length === 0) {
      setSelectedDataList([projectData[0]]);
      handleRowClick(projectData[0])
    }
  }, [projectData,selectedDataList.length]); // selectedDataList.length === 0 조건으로 한 번만 실행

  // 종료 완료 알림 표시
  useEffect(() => {
    if (terminateSuccess.success) {
      openAlert({
        title: '안내',
        message: '사용 종료 처리가 완료되었습니다.',
        confirmText: '확인',
        onConfirm: () => {
          setTerminateSuccess({ success: false, seq: 0 });
          // 종료 후 상태 변경
          setShowTerminate(false);
          setPrimaryText('생성하기');
        },
      });
    }
  }, [terminateSuccess.seq, openAlert, setTerminateSuccess]);

  const handleToolTypeChange = (value: 'jupyter' | 'vscode' | null) => {
    if (!value) return;
    setIdeType(value);
  };

  // 그리드 행 클릭 핸들러
    const handleRowClick = (row: any) => {
        //const clickedRow = row?.value;
        const selectedValue = row?.value;
        // value 검증
        if (selectedValue === 'jupyter' || selectedValue === 'vscode') {
            setSelectedDataList([row]);
            handleToolTypeChange(selectedValue);
        }
    };

  const handleGo = () => {
    // 선택된 IDE 타입에 따라 URL 가져오기
    const ideData = ideType === 'jupyter' ? jupyterData : vscodeData;

    // inUse가 true이고 items가 있을 때 첫 번째 아이템의 ingressUrl 사용
    if (ideData?.inUse && ideData.items && ideData.items.length > 0) {
      const url = ideData.items[0].ingressUrl;
      if (url) {
        window.open(url, '_blank', 'noopener,noreferrer');
      }
    }

    closeAllModals();
  };
  const handleCreate = async () => {
    setIdeAction(prev => ({ action: 'create', seq: prev.seq + 1 }));
    closeAllModals();
  };
  const handleTerminateClick = () => {
    openConfirm({
      title: '안내',
      message: '해당 도구를 사용 종료하시겠습니까?',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        setIdeAction(prev => ({ action: 'terminate', seq: prev.seq + 1 }));
      },
    });
  };

  // 토큰 조회
  const { refetch: fetchToken } = useGetAccessToken({
        enabled: false
    });
  const { handleCopy } = useCopyHandler();

  const handleCopyToken = (tokenType: 'Access' | 'Refresh') => async () => {
      const result = await fetchToken();
      const key = tokenType === 'Access' ? 'access_token' : 'refresh_token';

      await handleCopy(result.data?.[key] ?? '');
  };

        return (
    <div className='flex h-full flex-col'>
      <UIArticle>
        <div className='flex h-full'>
          <UIArticle className='article-grid w-full'>
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='flex justify-between items-center w-full'>
                  <div className='flex-shrink-0'>
                    <UIDataCnt count={projectData.length} prefix='총' unit='건' />
                  </div>
                </div>
                  <div className='flex justify-end w-full'>
                      <UIUnitGroup gap={8} direction='row' align='end'>
                          <UIButton2 className='btn-tertiary-outline' onClick={handleCopyToken('Access')}>Access Token 복사</UIButton2>
                          <UIButton2 className='btn-tertiary-outline' onClick={handleCopyToken('Refresh')}>Refresh Token 복사</UIButton2>
                      </UIUnitGroup>
                  </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid
                  type='single-select'
                  rowData={projectData} 
                  columnDefs={columnDefs}
                  selectedDataList={selectedDataList}
                  checkKeyName="no"
                  onClickRow={(params) => handleRowClick(params.data)}
                  onCheck={(params) => {
                      if (!params || params.length === 0) {
                          if (selectedDataList?.length === 1) {
                              setSelectedDataList([...selectedDataList]);
                          }
                          return;
                      }
                      if (!params || params.length === 0) {
                      return;
                  }
                      handleRowClick(params?.[0])}
                  }
                />
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination 
                  currentPage={1} 
                  totalPages={1} 
                  onPageChange={() => {}} 
                  className='flex justify-center' 
                />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </div>
      </UIArticle>

      {/* 커스텀 푸터: 상태에 따라 버튼/텍스트 변경 */}
      <UIModalContent.Footer
        type={'modal-medium'}
        negativeButton={showTerminate ? { text: '사용종료', onClick: handleTerminateClick } : undefined}
        positiveButton={{ text: primaryText, onClick: primaryText === '바로가기' ? handleGo : handleCreate }}
      />
    </div>
  );
};
