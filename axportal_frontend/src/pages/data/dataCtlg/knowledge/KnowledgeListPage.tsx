import React, { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router';

import { UIArticle, UIDropdown, UIGroup, UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { useDeleteExternalKnowledge, useGetExternalRepos } from '@/services/knowledge/knowledge.services';
import { useModal } from '@/stores/common/modal';
import dateUtils from '@/utils/common/date.utils';

import { Button } from '@/components/common/auth';
import { UIButton2, UILabel, UITypography } from '@/components/UI/atoms';
import { UIBox } from '@/components/UI/atoms/UIBox';
import { UIDataCnt } from '@/components/UI/atoms/UIDataCnt';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIToggle } from '@/components/UI/atoms/UIToggle';
import { UICardList } from '@/components/UI/molecules/card/UICardList';
import { UIGridCard } from '@/components/UI/molecules/card/UIGridCard';
import { AUTH_KEY } from '@/constants/auth';
import { env } from '@/constants/common/env.constants';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { useBackRestoredState } from '@/hooks/common/navigation';
import { useUser } from '@/stores/auth/useUser';

// 검색 조건
interface SearchValues {
  page: number;
  size: number;
  searchKeyword: string;
  status: string;
  view: string;
}

/**
 * 데이터 카탈로그 - 지식 페이지
 */
export const KnowledgeListPage = () => {
  const navigate = useNavigate();
  const { openAlert, openConfirm } = useModal();

  // 체크된 항목 저장 (그리드용)
  const [selectedRows, setSelectedRows] = useState<any[]>([]);
  const { user } = useUser();
  // 검색 조건
  const { filters: searchValues, updateFilters: setSearchValues } = useBackRestoredState<SearchValues>(STORAGE_KEYS.SEARCH_VALUES.KNOWLEDGE_LIST, {
    page: 1,
    size: 12,
    searchKeyword: '',
    status: '전체',
    view: 'grid',
  });

  // External Knowledge 목록 조회 - Backend API 연동
  const {
    data: externalReposData,
    refetch,
    isFetching,
  } = useGetExternalRepos(
    {
      page: searchValues.page,
      size: searchValues.size,
      search: searchValues.searchKeyword,
      sort: 'updated_at,desc',
      filter: searchValues.status === '전체' ? undefined : `is_active:${searchValues.status === '활성화'}`, // 상태값 조건
    },
    {
      enabled: !env.VITE_NO_PRESSURE_MODE,
    }
  );

  const updatePageSizeAndRefetch = (patch: Partial<Pick<SearchValues, 'page' | 'size'>>) => {
    setSearchValues(prev => ({ ...prev, ...patch }));
    setTimeout(() => refetch(), 0);
  };

  // 지식 생성 완료 이벤트 수신
  useEffect(() => {
    const handleKnowledgeCreated = () => {
      // console.log('지식 생성 완료 이벤트 수신 - 목록 새로고침');
      refetch();
    };

    window.addEventListener('knowledge-created', handleKnowledgeCreated);

    return () => {
      window.removeEventListener('knowledge-created', handleKnowledgeCreated);
    };
  }, [refetch]);

  // API 응답 데이터를 UI에 맞게 변환 - NO 컬럼 순차 번호 추가
  const rowData = useMemo(() => {
    // 페이지 이동 시 데이터 clear
    if (isFetching && !externalReposData?.data) {
      return [];
    }
    if (!externalReposData?.data) {
      return [];
    }
    return externalReposData.data.map((item: any, index: number) => {
      return {
        // 그리드 표시용 필드
        no: (searchValues.page - 1) * searchValues.size + index + 1,
        id: item.id, // exp_knw_id (ADXP Repository ID)
        name: item.name,
        status: item.is_active ? '활성화' : '비활성화',
        description: item.description || '',
        publicRange: item.public_status || '',
        vectorDB: item.vector_db_name || '',
        embedding: item.embedding_model_name || '',
        splitMethod: item.index_name || '',
        createdDate: item.created_at ? dateUtils.formatDate(item.created_at, 'datetime') : '',
        modifiedDate: item.updated_at ? dateUtils.formatDate(item.updated_at, 'datetime') : '',
        more: 'more',
        lstPrjSeq: item.lst_prj_seq,
        fstPrjSeq: item.fst_prj_seq,

        // 상세 페이지용 필드 (camelCase 통일)
        knwId: item.knw_id,
        expKnwId: item.id,
        ragChunkIndexNm: item.rag_chunk_index_nm || item.index_name,
      };
    });
  }, [externalReposData, searchValues.page, searchValues.size, isFetching]);

  const totalPages = externalReposData?.payload?.pagination?.last_page || 1;
  const totalCount = externalReposData?.payload?.pagination?.total || 0;

  const { mutate: deleteExternalKnowledge } = useDeleteExternalKnowledge();

  // 선택된 항목 삭제 핸들러
  const handleDelete = async () => {
    if (selectedRows.length === 0) {
      await openAlert({
        title: '안내',
        message: '삭제할 항목을 선택해주세요.',
      });
      return;
    }

    // selectedRows에서 삭제에 필요한 정보 추출
    const deleteItems = selectedRows.map((row: any) => ({
      knwId: row.knwId,
      expKnwId: row.expKnwId,
      ragChunkIndexNm: row.ragChunkIndexNm,
    }));

    await openConfirm({
      title: '안내',
      message: '삭제하시겠어요? \n삭제한 정보는 복구할 수 없습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        deleteExternalKnowledge(
          {
            items: deleteItems,
          },
          {
            onSuccess: async () => {
              await openAlert({
                title: '완료',
                message: '지식이 삭제되었습니다.',
                onConfirm: async () => {
                  await refetch();
                },
              });
            },
          }
        );
      },
    });

    // 선택 초기화 및 목록 새로고침
    setSelectedRows([]);
  };

  // 단일 항목 삭제 핸들러 (더보기 메뉴용)
  const handleDeleteSingle = async (rowData: any) => {
    if (!rowData.knwId && !rowData.expKnwId) {
      await openAlert({
        title: '오류',
        message: 'Backend에서 DB 정보를 받지 못했습니다. Backend를 재시작해주세요.',
      });
      return;
    }

    await openConfirm({
      title: '안내',
      message: '삭제하시겠어요? \n삭제한 정보는 복구할 수 없습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        deleteExternalKnowledge(
          {
            items: [
              {
                knwId: rowData.knwId,
                expKnwId: rowData.expKnwId,
                ragChunkIndexNm: rowData.ragChunkIndexNm,
              },
            ],
          },
          {
            onSuccess: async () => {
              await openAlert({
                title: '완료',
                message: '지식이 삭제되었습니다.',
                onConfirm: async () => {
                  await refetch();
                },
              });
            },
          }
        );
      },
    });
  };

  // 조회 버튼 클릭 핸들러
  const handleSearch = () => {
    updatePageSizeAndRefetch({ page: 1 });
  };

  const handlePageChange = (page: number) => {
    updatePageSizeAndRefetch({ page });
  };

  const handlePageSizeChange = (value: string) => {
    const size = parseInt(value.replace('개씩 보기', ''), 10);
    updatePageSizeAndRefetch({ size, page: 1 });
  };

  // 공개에셋은 고향프로젝트가 아닌 프로젝트에서는 수정 불가
  const checkPublicAssetPermission = (rowData: any, alertMessage: string = '지식/학습 데이터 편집에 대한 권한이 없습니다.') => {
    if (Number(rowData?.lstPrjSeq) === -999 && Number(user.activeProject.prjSeq) !== -999 && Number(user.activeProject.prjSeq) !== Number(rowData?.fstPrjSeq)) {
      openAlert({
        title: '안내',
        message: alertMessage,
        confirmText: '확인',
      });
      return false;
    }
    return true;
  };

  // 더보기 메뉴 설정 - DT_020301 스타일
  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          label: '삭제',
          action: 'delete',
          auth: AUTH_KEY.DATA.KNOWLEDGE_DELETE,
          onClick: (rowData: any) => {
            if (!checkPublicAssetPermission(rowData, '지식/학습 데이터 삭제에 대한 권한이 없습니다.')) {
              return;
            }
            handleDeleteSingle(rowData);
          },
        },
      ],
      isActive: () => true,
    }),
    [openAlert, refetch]
  );

  // 상태 드롭다운 선택
  const handleDropdownSelect = (key: keyof SearchValues, value: string) => {
    setSearchValues(prev => ({ ...prev, [key]: value }));
  };

  // 그리드 컬럼 정의 - DT_020301 스타일, NO 컬럼 수정
  const columnDefs: any = useMemo(
    () => [
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
        width: 272,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '상태',
        field: 'status',
        width: 120,
        cellRenderer: (params: any) => {
          const colorMap: { [key: string]: string } = {
            활성화: 'complete',
            비활성화: 'error',
          };
          return (
            <UILabel variant='badge' intent={colorMap[params.value] as any}>
              {params.value}
            </UILabel>
          );
        },
      },
      {
        headerName: '설명',
        field: 'description',
        minWidth: 370,
        flex: 1,
        showTooltip: true,
        suppressSizeToFit: true,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                paddingLeft: '0',
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
                width: '100%',
              }}
            // 호버 시 전체 텍스트 표시
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
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '벡터DB',
        field: 'vectorDB',
        width: 120,
        minWidth: 120,
        maxWidth: 120,
        suppressSizeToFit: true,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                paddingLeft: '0',
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
                width: '100%',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '임베딩 모델',
        field: 'embedding',
        width: 260,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '인덱스명',
        field: 'splitMethod',
        width: 120,
        minWidth: 120,
        maxWidth: 120,
        suppressSizeToFit: true,
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                paddingLeft: '0',
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
                width: '100%',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '생성일시',
        field: 'createdDate',
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate',
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
    []
  );

  // 상세 이동
  const handleRowClick = (params: any) => {
    // console.log('행 클릭:', params);
    // 지식 상세 페이지로 이동 (전체 데이터를 state로 전달)
    const id = params.data.knwId || params.data.expKnwId || params.data.id;
    if (id) {
      navigate(`/data/dataCtlg/knowledge/detail/${id}`);
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
                      <div>
                        <UIInput.Search
                          value={searchValues.searchKeyword}
                          placeholder='이름, 설명 입력'
                          onChange={e => setSearchValues(prev => ({ ...prev, searchKeyword: e.target.value }))}
                        />
                      </div>
                    </td>
                    <th style={{ width: '107px' }}>
                      <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                        상태
                      </UITypography>
                    </th>
                    <td>
                      <UIDropdown
                        value={searchValues.status}
                        placeholder='조회 조건 선택'
                        options={[
                          { value: '전체', label: '전체' },
                          { value: '활성화', label: '활성화' },
                          { value: '비활성화', label: '비활성화' },
                        ]}
                        onSelect={value => handleDropdownSelect('status', value)}
                      />
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

      {/* 데이터 그리드 컴포넌트 - DT_020301 스타일 */}
      <UIArticle className='article-grid'>
        <div className='article-body' style={{ position: 'relative' }}>
          <UIListContainer>
            <UIListContentBox.Header>
              <div className='w-full'>
                <UIUnitGroup gap={16} direction='column'>
                  <div className='flex justify-between w-full items-center'>
                    <div className='flex-shrink-0'>
                      <div style={{ width: '168px', paddingRight: '8px' }}>
                        <UIDataCnt count={totalCount} prefix='총' />
                      </div>
                    </div>
                    <div className='flex items-center gap-[8px]'>
                      <div style={{ width: '180px', flexShrink: 0 }}>
                        <UIDropdown
                          value={`${searchValues.size}개씩 보기`}
                          options={[
                            { value: '12개씩 보기', label: '12개씩 보기' },
                            { value: '36개씩 보기', label: '36개씩 보기' },
                            { value: '60개씩 보기', label: '60개씩 보기' },
                          ]}
                          onSelect={handlePageSizeChange}
                          height={40}
                          variant='dataGroup'
                          disabled={totalCount === 0}
                        />
                      </div>
                      {/* 뷰 토글 컴포넌트 - DT_020301 스타일 */}
                      <UIToggle
                        variant='dataView'
                        checked={searchValues.view === 'card'}
                        onChange={checked => setSearchValues(prev => ({ ...prev, view: checked ? 'card' : 'grid' }))}
                        disabled={totalCount === 0}
                      />
                    </div>
                  </div>
                </UIUnitGroup>
              </div>
            </UIListContentBox.Header>
            <UIListContentBox.Body>
              {searchValues.view === 'grid' ? (
                <UIGrid
                  type='multi-select'
                  loading={isFetching}
                  rowData={rowData}
                  columnDefs={columnDefs}
                  moreMenuConfig={moreMenuConfig}
                  selectedDataList={selectedRows}
                  checkKeyName={'id'}
                  onClickRow={(params: any) => {
                    handleRowClick(params);
                  }}
                  onCheck={(checkedRows: any[]) => {
                    // console.log('🔍 체크된 항목:', checkedRows);
                    setSelectedRows(checkedRows);
                  }}
                />
              ) : (
                <UICardList
                  rowData={rowData}
                  flexType='none'
                  loading={isFetching}
                  card={(item: any) => {
                    const getStatusIntent = (status: string) => {
                      switch (status) {
                        case '활성화':
                          return 'complete';
                        case '비활성화':
                          return 'error';
                        default:
                          return 'complete';
                      }
                    };
                    return (
                      <UIGridCard
                        id={item.id}
                        title={item.name}
                        caption={item.description}
                        data={item}
                        moreMenuConfig={moreMenuConfig}
                        statusArea={
                          <UILabel variant='badge' intent={getStatusIntent(item.status)}>
                            {item.status}
                          </UILabel>
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
                        rows={[
                          { label: '백터DB', value: item.vectorDB },
                          { label: '임베딩 모델', value: item.embedding },
                          { label: '인덱스명', value: item.splitMethod },
                        ]}
                        onClick={() => handleRowClick({ data: item })}
                      />
                    );
                  }}
                />
              )}
            </UIListContentBox.Body>
            <UIListContentBox.Footer className='ui-data-has-btn'>
              <Button auth={AUTH_KEY.DATA.KNOWLEDGE_DELETE} className='btn-option-outlined' style={{ width: '40px' }} onClick={handleDelete} disabled={totalCount === 0}>
                삭제
              </Button>
              <UIPagination currentPage={searchValues.page} hasNext={externalReposData?.hasNext} totalPages={totalPages} onPageChange={handlePageChange} className='flex justify-center' />
            </UIListContentBox.Footer>
          </UIListContainer>
        </div>
      </UIArticle>
    </>
  );
};
