import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIButton2, UIIcon2, UILabel, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { useModal } from '@/stores/common/modal';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { IdeUsageExtensionPopup } from '@/pages/home/webide';
import { useGetImageResource } from '@/services/admin/ideMgmt/ideMgmt.services';
import { ImageType } from '@/services/admin/ideMgmt/ideMgmt.types';
import { useDeleteIde, useExtendIdeExpiration, useGetIdeStatus } from '@/services/home/webide/ide.services';
import type { IdeStatusRes } from '@/services/home/webide/types';
import { useUser } from '@/stores/auth';
import dateUtils from '@/utils/common/date.utils';

/** 페이지당 표시할 항목 수 */
const PAGE_SIZE = 12;

/**
 * 만료 시간까지 남은 시간을 포맷팅
 * @param expireDate ISO 8601 형식의 만료 일시 (예: "2025-03-24T14:23:43")
 * @returns "X시간 Y분 (YYYY.MM.DD HH:mm:ss)" 형식의 문자열
 */
const formatRemainingTime = (expireDate: string): string => {
  const expireDateTime = new Date(expireDate);
  const now = new Date();
  const diffMs = expireDateTime.getTime() - now.getTime();

  // 이미 만료된 경우
  if (diffMs <= 0) {
    return '만료됨';
  }

  // 남은 시간 계산 (시간, 분)
  const totalMinutes = Math.floor(diffMs / (1000 * 60));
  const hours = Math.floor(totalMinutes / 60);
  const minutes = totalMinutes % 60;

  // 만료 일시 포맷팅
  const formattedExpireDate = dateUtils.formatDateWithPattern(expireDateTime, 'yyyy.MM.dd HH:mm:ss');

  return `${hours}시간 ${minutes}분 (${formattedExpireDate})`;
};

/** IDE 아이템 타입 정의 (그리드 표시용) */
interface IdeItem {
  id: string;
  no: number;
  toolName: string;
  imageName: string;
  status: '사용 가능';
  dwAccount: string;
  remainingTime: string;
  ideExpireDate: string;
  svrUrlNm: string;
}

/** IdeMovePopup Props 타입 */
interface IdeMovePopupProps {
  /** 팝업 표시 여부 */
  isOpen: boolean;
  /** 팝업 닫기 핸들러 */
  onClose: () => void;
  /** IDE 추가 버튼 클릭 핸들러 */
  onAddIde?: () => void;
}

/**
 * IdeStatusRes를 IdeItem으로 변환
 * @param statusList API 응답 데이터
 * @returns 그리드 표시용 데이터
 */
const transformToIdeItems = (statusList: IdeStatusRes[]): IdeItem[] => {
  return statusList.map((item, index) => ({
    id: item.ideUuid,
    no: index + 1,
    toolName: item.imgG === 'JUPYTER' ? 'Jupyter Notebook' : 'VS Code',
    imageName: item.imgNm,
    status: '사용 가능' as const,
    dwAccount: item.dwAccountId || '',
    remainingTime: formatRemainingTime(item.expAt),
    ideExpireDate: item.expAt,
    svrUrlNm: item.svrUrlNm,
  }));
};

/**
 * IDE 이동 팝업 컴포넌트
 * 기존에 생성된 IDE를 선택하여 이동하거나, 새로운 IDE를 생성할 수 있는 팝업
 */
export const IdeMovePopup: React.FC<IdeMovePopupProps> = ({ isOpen, onClose, onAddIde }) => {
  // 검색어 상태 (UI 입력용)
  const [searchValue, setSearchValue] = useState('');

  // 검색어 상태 (API 요청용)
  const [appliedKeyword, setAppliedKeyword] = useState('');

  // 선택된 IDE ID
  const [selectedId, setSelectedId] = useState<string>('');

  // 현재 페이지
  const [currentPage, setCurrentPage] = useState(1);

  // 선택된 연장 기간 (모달 내부에서 선택된 값 추적용)
  const selectedPeriodRef = useRef('7days');

  // 모달 훅
  const { openModal, openConfirm, openAlert } = useModal();
  const { showCancelConfirm } = useCommonPopup();

  // 사용자 정보 조회
  const { user } = useUser();
  const memberId = user?.userInfo?.memberId || '';

  // IDE 상태 목록 조회 (서버 사이드 페이징/검색)
  const { data: ideStatusData, isLoading: isIdeStatusLoading, refetch: refetchIdeStatus } = useGetIdeStatus(
    {
      memberId,
      keyword: appliedKeyword || undefined,
      page: currentPage,
      size: PAGE_SIZE,
    },
    { enabled: isOpen && !!memberId }
  );

  // IDE 삭제 mutation
  const { mutate: deleteIde } = useDeleteIde({
    onSuccess: () => {
      openAlert({
        title: '완료',
        message: 'IDE 사용 종료 처리가 완료되었습니다.',
        confirmText: '확인',
      });
      setSelectedId('');
      refetchIdeStatus();
    },
    onError: () => {
      openAlert({
        title: '오류',
        message: 'IDE 사용 종료 처리에 실패하였습니다.',
        confirmText: '확인',
      });
    },
  });

  // IDE 사용 기간 연장 mutation
  const { mutate: extendIdeExpiration } = useExtendIdeExpiration({
    onSuccess: () => {
      openAlert({
        title: '완료',
        message: 'IDE 사용 기간이 연장되었습니다.',
        confirmText: '확인',
      });
      refetchIdeStatus();
    },
    onError: () => {
      openAlert({
        title: '오류',
        message: 'IDE 사용 기간 연장에 실패하였습니다.',
        confirmText: '확인',
      });
    },
  });

  // API 데이터를 그리드 표시용으로 변환 (서버에서 페이징/검색 처리)
  const ideItems = useMemo(() => {
    if (!ideStatusData?.content) return [];
    return transformToIdeItems(ideStatusData.content);
  }, [ideStatusData?.content]);

  // 서버 응답에서 페이징 정보 추출
  const totalPages = ideStatusData?.totalPages || 1;
  const totalElements = ideStatusData?.totalElements || 0;

  // 페이지 범위 조정 (삭제 등으로 데이터가 줄어들었을 때)
  useEffect(() => {
    // 로딩 중이거나 데이터가 없으면 페이지 조정하지 않음
    if (isIdeStatusLoading || !ideStatusData) return;

    if (currentPage > totalPages && totalPages > 0) {
      setCurrentPage(totalPages);
    }
  }, [totalPages, currentPage, isIdeStatusLoading, ideStatusData]);

  // IDE 리소스 환경 설정 조회
  const { data: resourceData } = useGetImageResource();

  // Jupyter Notebook, VS Code 최대 생성 개수
  const jupyterLimit = resourceData?.find(item => item.imgG === ImageType.JUPYTER)?.limitCnt ?? 0;
  const vscodeLimit = resourceData?.find(item => item.imgG === ImageType.VSCODE)?.limitCnt ?? 0;

  // Jupyter Notebook, VS Code 현재 보유 개수
  const jupyterCount = useMemo(() => {
    return ideItems.filter(item => item.toolName === 'Jupyter Notebook').length;
  }, [ideItems]);

  const vscodeCount = useMemo(() => {
    return ideItems.filter(item => item.toolName === 'VS Code').length;
  }, [ideItems]);

  /**
   * 사용 기간 연장 버튼 클릭 핸들러
   */
  const handleExtensionClick = useCallback(
    (ideItem: IdeItem) => {
      // ref 초기화 (기본값: 3일)
      selectedPeriodRef.current = '3days';

      openModal({
        type: 'large',
        title: '사용 기간 연장',
        body: (
          <IdeUsageExtensionPopup
            ideInfo={{
              imageName: ideItem.imageName,
              expireDate: ideItem.ideExpireDate,
            }}
            onPeriodSelect={period => {
              selectedPeriodRef.current = period;
            }}
          />
        ),
        confirmText: '설정',
        onConfirm: () => {
          // 기간 문자열을 숫자로 변환 ('3days' → 3, '7days' → 7, '14days' → 14)
          const extendDays = parseInt(selectedPeriodRef.current.replace('days', ''), 10);

          extendIdeExpiration({
            statusUuid: ideItem.id,
            extendDays,
          });
        },
      });
    },
    [openModal, extendIdeExpiration]
  );

  /**
   * 사용 종료 버튼 클릭 핸들러
   */
  const handleTerminateClick = useCallback(
    (ideItem: IdeItem) => {
      openConfirm({
        title: '안내',
        message: '해당 IDE를 사용 종료하시겠어요?\n사용 종료 처리한 IDE는 목록에서 삭제 처리되며, 더 이상 사용하실 수 없습니다.',
        confirmText: '예',
        cancelText: '아니요',
        onConfirm: () => {
          deleteIde({ ideId: ideItem.id });
        },
      });
    },
    [openConfirm, deleteIde]
  );

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
        valueGetter: (params: any) => {
          return (currentPage - 1) * PAGE_SIZE + params.node.rowIndex + 1;
        },
      },
      {
        headerName: '도구명',
        field: 'toolName' as const,
        width: 160,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: React.memo((params: { value: string }) => {
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
        headerName: '이미지명',
        field: 'imageName' as const,
        width: 200,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: React.memo((params: { value: string }) => {
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
        field: 'status' as const,
        width: 120,
        cellRenderer: React.memo(() => {
          return (
            <UILabel variant='badge' intent='complete'>
              사용 가능
            </UILabel>
          );
        }),
      },
      {
        headerName: 'DW 계정',
        field: 'dwAccount' as const,
        minWidth: 120,
        flex: 1,
        showTooltip: true,
        cellRenderer: React.memo((params: { value: string }) => {
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
        headerName: '남은 시간',
        field: 'remainingTime' as const,
        width: 260,
      },
      {
        headerName: '사용 기간 연장',
        field: 'id' as const,
        width: 140,
        cellRenderer: (params: { data: IdeItem }) => {
          return (
            <UIButton2 className='btn-text-14-underline-point' onClick={() => handleExtensionClick(params.data)}>
              사용 기간 연장
            </UIButton2>
          );
        },
      },
      {
        headerName: '사용 종료',
        field: 'id' as const,
        width: 140,
        cellRenderer: (params: { data: IdeItem }) => {
          return (
            <UIButton2 className='btn-text-14-underline-point' onClick={() => handleTerminateClick(params.data)}>
              사용 종료
            </UIButton2>
          );
        },
      },
    ],
    [handleExtensionClick, handleTerminateClick, currentPage]
  );

  /**
   * 취소 버튼 클릭 핸들러
   */
  const handleCancel = () => {
    showCancelConfirm({
      onConfirm: onClose,
    });
  };

  /**
   * 이동 버튼 클릭 핸들러
   */
  const handleMove = () => {
    if (selectedId) {
      const selectedIde = ideItems.find(item => item.id === selectedId);
      if (selectedIde?.svrUrlNm) {
        window.open(selectedIde.svrUrlNm, '_blank');
      }
      onClose();
    }
  };

  /**
   * 검색 실행 핸들러
   */
  const handleSearch = () => {
    setAppliedKeyword(searchValue);
    setCurrentPage(1);
  };

  /**
   * 검색어 입력 시 Enter 키 핸들러
   */
  const handleSearchKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  /**
   * 행 선택 핸들러
   */
  const handleRowClick = (datas: IdeItem[]) => {
    if (datas.length > 0) {
      setSelectedId(datas[0].id);
    } else {
      setSelectedId('');
    }
  };

  /**
   * IDE 추가 버튼 클릭 핸들러
   * 두 도구 모두 최대 개수에 도달한 경우 경고 모달 표시
   */
  const handleAddIde = () => {
    const isJupyterFull = jupyterCount >= jupyterLimit;
    const isVscodeFull = vscodeCount >= vscodeLimit;

    if (isJupyterFull && isVscodeFull) {
      openAlert({
        title: '안내',
        message: '해당 도구로 생성 가능 개수를 초과하여 더이상 생성이 불가합니다. 기존 IDE를 이용하거나, 종료 후 다시 시도해주세요.',
        confirmText: '확인',
      });
      return;
    }

    onClose();
    onAddIde?.();
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
            {/* 좌측 헤더 */}
            <UIPopupHeader title='IDE 이동' position='left' />
            {/* 좌측 바디 (빈 영역) */}
            <UIPopupBody></UIPopupBody>
            {/* 좌측 푸터 (버튼 영역) */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: 80 }} onClick={handleCancel}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: 80 }} onClick={handleMove} disabled={!selectedId}>
                    이동
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
          <UIPopupHeader title='IDE 이동' description='기존에 생성된 IDE를 선택하여 이동하거나, [IDE 추가] 버튼을 통해 새로운 IDE를 생성해보세요.' position='right' />

          {/* 우측 바디 */}
          <UIPopupBody>
            {/* IDE 목록 그리드 영역 */}
            <UIArticle className='article-grid'>
              <UIListContainer>
                <UIListContentBox.Header>
                  <div className='flex flex-shrink-0 gap-1 items-center'>
                    <div style={{ width: 'auto', paddingRight: '8px' }}>
                      <UIDataCnt count={totalElements} prefix='보유한 IDE 총' unit='건' />
                    </div>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      Jupyter Notebook 최대 {jupyterLimit}개, VS Code 최대 {vscodeLimit}개까지 생성할 수 있습니다.
                    </UITypography>
                  </div>
                  <div className='flex gap-3 flex-shrink-0'>
                    <div>
                      <UIButton2 className='btn-tertiary-outline line-only-blue' onClick={handleAddIde}>
                        IDE 추가
                      </UIButton2>
                    </div>
                    <div className='w-[360px]'>
                      <UIInput.Search
                        value={searchValue}
                        onChange={e => setSearchValue(e.target.value)}
                        onKeyDown={handleSearchKeyDown}
                        placeholder='도구명, 이미지명, DW계정 입력'
                      />
                    </div>
                  </div>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid type='single-select' loading={isIdeStatusLoading} rowData={ideItems} columnDefs={columnDefs} onCheck={handleRowClick} />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination currentPage={currentPage} totalPages={totalPages} onPageChange={setCurrentPage} className='flex justify-center' />
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>

            {/* 안내 문구 영역 */}
            <UIArticle>
              <div className='box-fill'>
                <div style={{ display: 'flex', alignItems: 'center', gap: '0 6px' }}>
                  <UIIcon2 className='ic-system-16-info-gray' />
                  <UITypography variant='body-2' className='secondary-neutral-600'>
                    생성에 실패한 IDE의 경우, 사용 종료 버튼을 클릭하여 삭제하실 수 있습니다.
                  </UITypography>
                </div>
              </div>
            </UIArticle>
          </UIPopupBody>
        </section>
      </UILayerPopup>
    </>
  );
};
