import { useEffect, useMemo, useState } from 'react';

import { Button } from '@/components/common/auth';
import { UIDataCnt, UIToggle } from '@/components/UI';
import { UIBox, UIButton2, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UITextLabel } from '@/components/UI/atoms/UITextLabel';
import { UIGroup, UIInput } from '@/components/UI/molecules';
import { UICardList } from '@/components/UI/molecules/card/UICardList';
import { UIGridCard } from '@/components/UI/molecules/card/UIGridCard';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIGrid } from '@/components/UI/molecules/grid/UIGrid/component';
import { UIListContainer } from '@/components/UI/molecules/list/UIListContainer/component';
import { UIListContentBox } from '@/components/UI/molecules/list/UIListContentBox';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { env } from '@/constants/common/env.constants';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { useAuthCheck } from '@/hooks/common/auth';
import { useBackRestoredState } from '@/hooks/common/navigation';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { useDeleteVectorDB, useGetConnectionArgs, useGetVectorDBList } from '@/services/data/tool/dataToolVectorDB.services';
import { useModal } from '@/stores/common/modal';
import { dateUtils } from '@/utils/common';
import { useNavigate } from 'react-router-dom';
interface VectorDBListProps {
  isActiveTab?: boolean;
}

interface SearchValues {
  page: number;
  size: number;
  filter: string;
  searchKeyword: string;
  view: string;
}

export function VectorDBList({ }: VectorDBListProps) {
  const navigate = useNavigate();
  const { showTaskPartialComplete, showDeleteComplete } = useCommonPopup();
  const { openAlert, openConfirm } = useModal();
  const { checkAuth } = useAuthCheck();

  // 체크된 항목 저장 (그리드용)
  const [selectedRows, setSelectedRows] = useState<any[]>([]);

  // 드롭다운 옵션: 연결 가능한 벡터 DB 타입 목록
  const { data: toolConnectArgs } = useGetConnectionArgs();

  const toolTypeOptions = useMemo(() => {
    const enabledLoaders = Array.isArray(toolConnectArgs) ? toolConnectArgs.filter(item => item.enable === true) : [];

    return [
      { value: 'all', label: '전체' },
      ...enabledLoaders.map(item => ({
        value: item.type,
        label: item.displayName,
      })),
    ];
  }, [toolConnectArgs]);

  // 검색 조건 (뒤로가기 시 상태 복원)
  const { filters: searchValues, updateFilters: setSearchValues } = useBackRestoredState<SearchValues>(STORAGE_KEYS.SEARCH_VALUES.VECTOR_DB_LIST, {
    page: 1,
    size: 12,
    filter: 'all',
    searchKeyword: '',
    view: 'grid',
  });

  const {
    data: vectorData,
    refetch: refetchVector,
    isLoading,
  } = useGetVectorDBList(
    {
      page: searchValues.page,
      size: searchValues.size,
      sort: 'created_at,desc',
      filter: searchValues.filter !== 'all' && searchValues.filter !== 'all' ? `type:${searchValues.filter}` : undefined,
      search: searchValues.searchKeyword,
    },
    {
      enabled: !env.VITE_NO_PRESSURE_MODE,
      placeholderData: previousData => previousData, // 조회 중에도 기존 데이터 유지
    }
  );

  // API 응답 데이터를 useMemo로 저장
  const dataList = useMemo(() => {
    if (!vectorData?.content) {
      return [];
    }

    // map()을 사용하여 새로운 배열 생성
    return vectorData.content.map((item: any, index: number) => {
      return {
        no: (searchValues.page - 1) * searchValues.size + index + 1,
        id: item.id,
        name: item.name,
        type: item.type,
        isDefault: item.isDefault,
        createdAt: item.createdAt ? dateUtils.formatDate(item.createdAt, 'datetime') : '',
        updatedAt: item.updatedAt ? dateUtils.formatDate(item.updatedAt, 'datetime') : '',
      };
    });
  }, [vectorData]);

  const updatePageSizeAndRefetch = (patch: Partial<Pick<SearchValues, 'page' | 'size'>>) => {
    setSearchValues(prev => ({ ...prev, ...patch }));
    setTimeout(() => refetchVector(), 0);
  };

  // 벡터 DB 생성 완료 이벤트 수신
  useEffect(() => {
    const handleVectorDBCreated = () => {
      refetchVector();
    };

    window.addEventListener('vector-db-created', handleVectorDBCreated);

    return () => {
      window.removeEventListener('vector-db-created', handleVectorDBCreated);
    };
  }, [refetchVector]);

  // 총 페이지 (API 기준)
  const totalPages = vectorData?.totalPages || 1;

  const handlePageChange = (newPage: number) => {
    updatePageSizeAndRefetch({ page: newPage });
  };

  const handleSearch = () => {
    setSearchValues(prev => ({ ...prev, page: 1 }));
    refetchVector();
  };
  /**
   * 데이터 도구 - VectorDB 삭제
   */
  const { mutateAsync: deleteVectorDB } = useDeleteVectorDB();

  /**
   * 데이터 삭제
   */
  const handleDelete = async (ids: string[], isMultiple: boolean) => {
    // console.log('삭제할 항목들:', ids);
    if (ids.length === 0) {
      openAlert({
        title: '안내',
        message: '삭제할 항목을 선택해주세요.',
        confirmText: '확인',
      });
      return;
    } else {
      openConfirm({
        title: '안내',
        message: '삭제하시겠어요?\n삭제한 내용은 복구할 수 없습니다.',
        confirmText: '예',
        cancelText: '아니오',
        onConfirm: async () => {
          // 삭제 로직 실행
          let successCount = 0;
          let failCount = 0;

          // 순차적으로 삭제 처리
          for (const id of ids) {
            try {
              await deleteVectorDB({ vectorDbId: id });
              successCount++;
            } catch {
              // 개별 삭제 실패는 계속 진행
              failCount++;
            }
          }

          // 삭제 결과 알림
          if (failCount > 0) {
            await showTaskPartialComplete({
              taskName: '벡터 DB',
              successCount: successCount,
              failureCount: failCount,
              onConfirm: () => {
                refetchVector();
              },
            });
          } else {
            await showDeleteComplete({
              itemName: '벡터 DB',
              onConfirm: () => {
                refetchVector();
              },
            });
          }

          if (isMultiple) {
            setSelectedRows([]);
          }
        },
      });
    }
  };

  // 드롭다운 핸들러
  const handleDropdownSelect = (key: keyof SearchValues, value: string) => {
    setSearchValues(prev => ({ ...prev, [key]: value }));
  };

  // 그리드 컬럼 정의
  const columnDefs = useMemo(
    () =>
      [
        {
          headerName: 'NO',
          field: 'no' as any,
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
          } as any,
          sortable: false,
          suppressHeaderMenuButton: true,
          suppressSizeToFit: true,
        },
        {
          headerName: '이름',
          field: 'name',
          minWidth: 624,
          flex: 1,
        },
        {
          headerName: '유형',
          field: 'type',
          width: 187,
        },
        {
          headerName: '기본설정',
          field: 'defaultConfig',
          minWidth: 120,
          cellRenderer: (params: any) => {
            return (
              <div className='flex gap-1 flex-wrap'>
                <UITextLabel intent={params.data.isDefault ? 'blue' : 'gray'}>{params.data.isDefault?.toString() || 'false'}</UITextLabel>
              </div>
            );
          },
        },
        {
          headerName: '생성일시',
          field: 'createdAt',
          width: 180,
        },
        {
          headerName: '최종 수정일시',
          field: 'updatedAt',
          width: 180,
        },
        {
          headerName: '',
          field: 'more',
          width: 56,
        },
      ] as any, // 전체 배열에 타입 단언 추가
    []
  );

  const moreMenuConfig = useMemo(
    () => ({
      items: [
        // 더보기 클릭 시, 삭제만 보이도록 수정(2025.11.25)
        // {
        //   label: '수정',
        //   action: 'modify',
        //   auth: AUTH_KEY.DATA.VECTOR_DB_UPDATE,
        //   onClick: (rowData: any) => {
        //     handleRowClick(rowData);
        //   },
        // },
        {
          label: '삭제',
          action: 'delete',
          auth: AUTH_KEY.DATA.VECTOR_DB_DELETE,
          onClick: (rowData: any) => {
            handleDelete([rowData.id], false);
          },
        },
      ],
      isActive: () => true, // 모든 퓨샷에 대해 활성화
    }),
    []
  );

  const handleRowClick = (item: any) => {
    // 데이터 도구 편집 권한 체크
    const isAuthorized = checkAuth(AUTH_KEY.DATA.VECTOR_DB_DETAIL_VIEW);

    if (isAuthorized) {
      navigate(`/data/dataTools/vectorDB/${item.id}`);
    } else {
      openAlert({
        title: '안내',
        message: '권한이 없습니다.\n벡터DB 상세는 Public 프로젝트에서 포탈관리자만 조회할 수 있습니다.',
        confirmText: '확인',
      });
    }
  };

  return (
    <>
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
                    <td>
                      <UIInput.Search
                        value={searchValues.searchKeyword}
                        placeholder='검색어 입력'
                        onChange={e => setSearchValues(prev => ({ ...prev, searchKeyword: e.target.value }))}
                      />
                    </td>
                    <th>
                      <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                        유형
                      </UITypography>
                    </th>
                    <td>
                      <UIDropdown value={searchValues.filter} placeholder='조회 조건 선택' options={toolTypeOptions} onSelect={value => handleDropdownSelect('filter', value)} />
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            <div style={{ width: '128px' }}>
              <UIButton2 className='btn-secondary-blue' style={{ width: '100%' }} onClick={handleSearch}>
                조회
              </UIButton2>
            </div>
          </UIGroup>
        </UIBox>
      </UIArticle>
      <UIArticle className='article-grid'>
        {/* 다중 선택 그리드 */}
        <UIListContainer>
          <UIListContentBox.Header>
            <div className='flex-shrink-0'>
              <UIGroup gap={8} direction='row' align='start'>
                <div style={{ width: '168px', paddingRight: '8px' }}>
                  <UIDataCnt count={vectorData?.totalElements ?? 0} prefix='총' />
                </div>
              </UIGroup>
            </div>
            <div className='flex items-center gap-2'>
              <div style={{ width: '180px', flexShrink: 0 }}>
                <UIDropdown
                  value={String(searchValues.size)}
                  disabled={(vectorData?.totalElements ?? 0) === 0}
                  options={[
                    { value: '12', label: '12개씩 보기' },
                    { value: '36', label: '36개씩 보기' },
                    { value: '60', label: '60개씩 보기' },
                  ]}
                  onSelect={(value: string) => updatePageSizeAndRefetch({ size: Number(value), page: 1 })}
                  height={40}
                  variant='dataGroup'
                />
              </div>
              <UIToggle
                variant='dataView'
                checked={searchValues.view === 'card'}
                disabled={(vectorData?.totalElements ?? 0) === 0}
                onChange={checked => {
                  setSearchValues(prev => ({ ...prev, view: checked ? 'card' : 'grid' }));
                  //console.log('selectedRows............', selectedRows);
                }}
              />
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            {searchValues.view === 'grid' ? (
              <UIGrid
                type='multi-select'
                loading={isLoading}
                rowData={dataList}
                columnDefs={columnDefs}
                moreMenuConfig={moreMenuConfig}
                selectedDataList={selectedRows} // 추가: 체크박스 상태 유지용
                checkKeyName={'id'}
                onClickRow={(params: any) => {
                  handleRowClick(params.data);
                }}
                onCheck={(checkedRows: any[]) => {
                  // console.log('🔍 체크된 항목:', checkedRows);
                  setSelectedRows(checkedRows);
                }}
              />
            ) : (
              <UICardList
                loading={isLoading}
                rowData={dataList}
                flexType='none'
                card={(item: any) => {
                  return (
                    <UIGridCard
                      id={item.id}
                      title={item.name}
                      data={item}
                      moreMenuConfig={moreMenuConfig}
                      statusArea={
                        <UIGroup gap={8} direction='row'>
                          {item.isDefault === true ? <UITextLabel intent='blue'>true</UITextLabel> : <UITextLabel intent='gray'>false</UITextLabel>}
                        </UIGroup>
                      }
                      checkbox={{
                        checked: selectedRows.some(selectedItem => selectedItem.id === item.id),
                        onChange: (checked: boolean /* , value: string */) => {
                          // 카드뷰에서 체크박스를 변경할 때도 setSelectedRows를 사용하여 통일
                          if (checked) {
                            setSelectedRows([...selectedRows, item]);
                          } else {
                            setSelectedRows(selectedRows.filter((row: any) => row.id !== item.id));
                          }
                        },
                      }}
                      onClick={() => handleRowClick(item)}
                      rows={[
                        { label: '유형', value: item.type },
                        { label: '생성일시', value: item.createdAt },
                        { label: '최종수정일시', value: item.updatedAt },
                      ]}
                    />
                  );
                }}
              />
            )}
          </UIListContentBox.Body>
          <UIListContentBox.Footer className='ui-data-has-btn'>
            <Button
              className='btn-option-outlined'
              auth={AUTH_KEY.DATA.VECTOR_DB_DELETE}
              style={{ width: '40px' }}
              disabled={(vectorData?.totalElements ?? 0) === 0}
              onClick={() => {
                handleDelete(
                  selectedRows.map((row: any) => row.id),
                  true
                );
              }}
            >
              삭제
            </Button>
            <UIPagination currentPage={searchValues.page} hasNext={vectorData?.hasNext} totalPages={totalPages} onPageChange={handlePageChange} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </>
  );
}
