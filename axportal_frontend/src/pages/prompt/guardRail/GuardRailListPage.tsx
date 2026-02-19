import React, { useMemo, useState } from 'react';

import { useNavigate } from 'react-router-dom';

import { UIBox, UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGroup, UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import {
  useCreateGuardRail,
  useDeleteGuardRail,
  useGetGuardRailList
} from '@/services/prompt/guardRail/guardRail.services';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { useModal } from '@/stores/common/modal';
import { useUser } from '@/stores/auth/useUser';
import { env } from '@/constants/common/env.constants';

import { GuardRailCreatePopup1 } from './GuardRailCreatePopup1';
import { GuardRailCreatePopup2 } from './GuardRailCreatePopup2';
import { Button } from '@/components/common/auth';
import { AUTH_KEY } from '@/constants/auth';

const DEFAULT_PAGE_SIZE = 12;

interface SearchValues {
  page: number;
  size: number;
  searchKeyword: string;
}

/**
 * 프롬프트 > 가드레일 > (TAB) 가드레일 관리
 */
export const GuardRailListPage = () => {
  const navigate = useNavigate();
  const { openAlert } = useModal();
  const { showDeleteConfirm } = useCommonPopup();
  const { user } = useUser();

  // 검색 조건
  const [searchValues, setSearchValues] = useState<SearchValues>({
    page: 1,
    size: DEFAULT_PAGE_SIZE,
    searchKeyword: '',
  });

  // 검색 입력값 (임시)
  const [searchInput, setSearchInput] = useState('');

  // 선택된 가드레일 ID 목록
  const [selectedIds, setSelectedIds] = useState<string[]>([]);

  // 가드레일 목록 조회
  const {
    data: guardRailList,
    refetch,
    isLoading,
  } = useGetGuardRailList(
    {
      page: searchValues.page,
      size: searchValues.size,
      search: searchValues.searchKeyword,
      project_id: user?.activeProject?.prjUuid,
    },
    {
      enabled: !env.VITE_NO_PRESSURE_MODE,
    }
  );

  // 가드레일 생성 API
  const { mutate: createGuardRail } = useCreateGuardRail({
    onSuccess: ({ data: { guardrailsId } }) => {
      openAlert({
        title: '완료',
        message: '가드레일 생성을 완료하였습니다.',
        onConfirm: () => {
          handleClosePopup();
          navigate(`/prompt/guardrail/detail/${guardrailsId}`);
        },
      });
    },
  });

  // 가드레일 삭제 API (단일/복수 통합)
  const { mutate: deleteGuardRail } = useDeleteGuardRail({
    onSuccess: async response => {
      const requestedCount = response.data?.totalCount || 0;
      const successCount = response.data?.successCount || 0;
      const failCount = requestedCount - successCount;

      // 선택 초기화
      setSelectedIds([]);

      // 삭제 후 남은 데이터 계산
      const currentTotal = guardRailList?.totalElements || 0;
      const remainingItems = Math.max(0, currentTotal - successCount);

      // 남은 데이터로 최대 페이지 계산 (1-based)
      const newTotalPages = remainingItems > 0 ? Math.ceil(remainingItems / searchValues.size) : 1;
      const maxPage = newTotalPages;

      // 현재 페이지가 최대 페이지를 초과하면 이동
      if (searchValues.page > maxPage) {
        setSearchValues(prev => ({ ...prev, page: maxPage }));
      }

      // 삭제 결과에 따른 메시지 구성
      const getDeleteAlertMessage = () => {
        if (failCount === requestedCount) {
          // 모두 실패
          return {
            title: '실패',
            message: '가드레일 삭제에 실패했습니다.',
          };
        }

        if (failCount === 0) {
          // 모두 성공
          return {
            title: '완료',
            message: '가드레일이 삭제되었습니다.',
          };
        }

        // 부분 성공
        return {
          title: '부분 삭제 완료',
          message: `${successCount}건 성공, ${failCount}건 실패\n\n삭제 실패한 항목은 확인 후 다시 시도해주세요.`,
        };
      };

      openAlert(getDeleteAlertMessage());
      if (!env.VITE_NO_PRESSURE_MODE) {
        refetch();
      }
    },

    onError: error => {
      openAlert({
        title: '오류',
        message: (error as any)?.error?.message || '가드레일 삭제에 실패했습니다.',
      });
    },
  });

  // 팝업 상태 관리
  const [isCreatePopupOpen, setIsCreatePopupOpen] = useState(false);
  const [currentStep, setCurrentStep] = useState(1); // 1: 기본 정보 입력, 2: 배포 모델 선택

  // 프로젝트 ID 가져오기
  const adxpProjectId = user?.adxpProject?.prjUuid || '';

  // 가드레일 생성 폼 상태
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    prompt: '',
    promptId: '',
    projectId: adxpProjectId,
  });

  // 생성 버튼 클릭 핸들러
  const handleCreateClick = () => {
    setIsCreatePopupOpen(true);
    setCurrentStep(1); // 팝업 열 때 Step 1로 초기화
  };

  // 팝업 닫기 핸들러
  const handleClosePopup = () => {
    setIsCreatePopupOpen(false);
    setCurrentStep(1); // 팝업 닫을 때 Step 초기화

    // 폼 데이터 초기화
    setFormData({
      name: '',
      description: '',
      prompt: '',
      promptId: '',
      projectId: adxpProjectId,
    });
    setSearchInput(''); // 검색 입력값 초기화
  };

  // 다음 단계로 이동 (Step 1 완료)
  const handleNextStep = (name: string, description: string, promptId: string, prompt: string) => {
    setFormData(prev => ({
      ...prev,
      name,
      description,
      prompt,
      promptId,
    }));
    setCurrentStep(2);
  };

  // 이전 단계로 이동
  const handlePreviousStep = () => {
    setCurrentStep(1);
  };

  // 가드레일 생성 완료 (배포 모델 선택 후 만들기 버튼 클릭)
  const handleConfirmGuardRail = (selectedModels: any[]) => {
    // 요청 데이터 구성 (CreateGuardRailRequest 타입에 맞게)
    const requestData = {
      projectId: formData.projectId,
      name: formData.name,
      description: formData.description,
      promptId: formData.promptId,
      llms: selectedModels.map(model => ({
        servingId: model.servingId,
        servingName: model.name,
      })),
    };

    // API 호출
    createGuardRail(requestData);
  };

  // 삭제 버튼 클릭 핸들러
  const handleDelete = () => {
    if (selectedIds.length === 0) {
      openAlert({
        title: '안내',
        message: '삭제할 항목을 선택해주세요.',
      });
      return;
    }

    showDeleteConfirm({
      onConfirm: () => {
        deleteGuardRail({ guardrailIds: selectedIds });
      },
    });
  };

  const updatePageSizeAndRefetch = (patch: Partial<Pick<SearchValues, 'page' | 'size'>>) => {
    setSearchValues(prev => ({ ...prev, ...patch }));
    setTimeout(() => refetch(), 0);
  };

  const handlePageSizeChange = (value: string) => {
    updatePageSizeAndRefetch({ size: Number(value), page: 1 });
  };

  const handlePageChange = (page: number) => {
    updatePageSizeAndRefetch({ page });
  };

  // 더보기 메뉴 설정
  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          key: 'delete',
          label: '삭제',
          action: 'delete',
          auth: AUTH_KEY.PROMPT.GUARDRAIL_DELETE,
          onClick: (rowData: any) => {
            showDeleteConfirm({
              onConfirm: () => {
                deleteGuardRail({ guardrailIds: [rowData.uuid] });
              },
            });
          },
        },
      ],
      isActive: () => true,
    }),
    [deleteGuardRail, showDeleteConfirm]
  );

  // API 응답 데이터를 가공한 행 데이터
  const rowData = useMemo(() => {
    return (
      guardRailList?.content.map(item => ({
        id: item.uuid,
        uuid: item.uuid,
        name: item.name,
        description: item.description,
        isPublicAsset: item.isPublicAsset,
        createdAt: item.createdAt,
        updatedAt: item.updatedAt,
        createdBy: item.createdBy,
        updatedBy: item.updatedBy,
        more: 'more',
      })) ?? []
    );
  }, [guardRailList]);

  // 그리드 컬럼 정의
  const columnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'uuid' as any,
        width: 56,
        minWidth: 56,
        maxWidth: 56,
        cellClass: 'text-center',
        headerClass: 'text-center',
        cellStyle: {
          textAlign: 'center' as const,
          display: 'flex' as const,
          alignItems: 'center' as const,
          justifyContent: 'center' as const,
        },
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
        valueGetter: (params: any) => {
          return (searchValues.page - 1) * searchValues.size + params.node.rowIndex + 1;
        },
      },
      {
        headerName: '이름',
        field: 'name',
        width: 272,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
              title={params.value}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '설명',
        field: 'description',
        minWidth: 632,
        flex: 1,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
              title={params.value}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '공개범위',
        field: 'publicRange',
        width: 120,
        valueFormatter: (params: any) => {
          return params.data.isPublicAsset ? '전체공유' : '내부공유';
        },
      },
      {
        headerName: '생성일시',
        field: 'createdAt',
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '최종 수정일시',
        field: 'updatedAt',
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '',
        field: 'more',
        width: 56,
      },
    ],
    [searchValues.page, searchValues.size]
  );

  return (
    <>
      {/* 가드레일 생성 팝업 - Step 1 */}
      {isCreatePopupOpen && currentStep === 1 && (
        <GuardRailCreatePopup1
          isOpen={isCreatePopupOpen}
          onClose={handleClosePopup}
          onNext={handleNextStep}
          initialName={formData.name}
          initialDescription={formData.description}
          initialPrompt={formData.prompt}
          initialPromptId={formData.promptId}
        />
      )}

      {/* 가드레일 생성 팝업 - Step 2 */}
      {isCreatePopupOpen && currentStep === 2 && (
        <GuardRailCreatePopup2
          isOpen={isCreatePopupOpen}
          onClose={handleClosePopup}
          onPrevious={handlePreviousStep}
          onConfirm={handleConfirmGuardRail}
          name={formData.name}
          description={formData.description}
          promptId={formData.promptId}
          projectId={formData.projectId}
        />
      )}

      {/* 가드레일 관리 콘텐츠 */}
      <UIArticle className='article-filter'>
        <UIBox className='box-filter'>
          <UIGroup gap={40} direction='row'>
            <div style={{ width: 'calc(100% - 168px)' }}>
              <table className='tbl_type_b'>
                <tbody>
                  <tr>
                    <th>
                      <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                        검색
                      </UITypography>
                    </th>
                    <td colSpan={3}>
                      <div className='flex-1'>
                        <UIInput.Search
                          value={searchInput}
                          onChange={e => {
                            setSearchInput(e.target.value);
                          }}
                          onKeyDown={(e: React.KeyboardEvent<HTMLInputElement>) => {
                            // 엔터 키 입력 시 검색 실행
                            if (e.key === 'Enter') {
                              setSearchValues(prev => ({
                                ...prev,
                                searchKeyword: searchInput,
                                page: 1,
                              }));
                            }
                          }}
                          placeholder='이름, 설명 입력'
                        />
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            <div style={{ width: '128px' }}>
              <Button
                auth={AUTH_KEY.PROMPT.GUARDRAIL_READ}
                className='btn-secondary-blue'
                style={{ width: '100%' }}
                onClick={() => {
                  // 조회 버튼 클릭 시 검색 실행
                  setSearchValues(prev => ({
                    ...prev,
                    searchKeyword: searchInput,
                    page: 1,
                  }));

                  if (env.VITE_NO_PRESSURE_MODE) {
                    // NO_PRESSURE_MODE에서는 조회 버튼에서만 조회되도록 refetch를 여기서 수행
                    setTimeout(() => refetch(), 0);
                  }
                }}
              >
                조회
              </Button>
            </div>
          </UIGroup>
        </UIBox>
      </UIArticle>

      <UIArticle className='article-grid'>
        <UIListContainer>
          <UIListContentBox.Header>
            <UIUnitGroup gap={16} direction='column'>
              <div className='flex justify-between w-full items-center'>
                <div className='flex-shrink-0'>
                  <div style={{ width: '168px', paddingRight: '8px' }}>
                    <UIDataCnt count={guardRailList?.totalElements ?? 0} prefix='총' unit='건' />
                  </div>
                </div>
                <div className='flex items-center gap-2'>
                  <Button auth={AUTH_KEY.PROMPT.GUARDRAIL_CREATE} className='btn-tertiary-outline' onClick={handleCreateClick}>
                    가드레일 생성
                  </Button>
                  <div style={{ width: '180px', flexShrink: 0 }}>
                    <UIDropdown
                      value={String(searchValues.size)}
                      options={[
                        { value: '12', label: '12개씩 보기' },
                        { value: '36', label: '36개씩 보기' },
                        { value: '60', label: '60개씩 보기' },
                      ]}
                      onSelect={handlePageSizeChange}
                      height={40}
                      variant='dataGroup'
                      disabled={guardRailList?.content.length === 0}
                    />
                  </div>
                </div>
              </div>
            </UIUnitGroup>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            <UIGrid
              type='multi-select'
              loading={isLoading}
              rowData={rowData}
              columnDefs={columnDefs}
              moreMenuConfig={moreMenuConfig}
              onClickRow={(params: any) => {
                // 행 클릭 시 상세 페이지로 이동
                navigate(`/prompt/guardrail/detail/${params.data.uuid}`);
              }}
              onCheck={(checkedRows: any[]) => {
                // 체크박스 선택 시 selectedIds 업데이트 (uuid만 추출)
                const ids = checkedRows.map((row: any) => row.uuid);
                setSelectedIds(ids);
              }}
            />
          </UIListContentBox.Body>
          {/* [참고] classname 관련
                  - 그리드 하단 (삭제) 버튼이 있는 경우 classname 지정 (예시) <UIListContentBox.Footer classname="ui-data-has-btn">
                  - 그리드 하단 (버튼) 없는 경우 classname 없이 (예시) <UIListContentBox.Footer>
                */}
          <UIListContentBox.Footer className='ui-data-has-btn'>
            <Button
              auth={AUTH_KEY.PROMPT.GUARDRAIL_DELETE}
              className='btn-option-outlined'
              style={{ width: '40px' }}
              onClick={handleDelete}
              disabled={guardRailList?.content.length === 0}
            >
              삭제
            </Button>
            <UIPagination
              currentPage={searchValues.page}
              hasNext={guardRailList?.hasNext}
              totalPages={guardRailList?.totalPages || 1}
              onPageChange={handlePageChange}
              className='flex justify-center'
            />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </>
  );
};
