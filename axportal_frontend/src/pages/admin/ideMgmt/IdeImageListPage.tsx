import React, { useCallback, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';

import { UIGroup, UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { UIBox, UIButton2, UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { useBackRestoredState } from '@/hooks/common/navigation';
import {
  type GetImageListRequest,
  IMAGE_TYPE_LABEL,
  type ImageRowData,
  UI_LABEL_TO_IMAGE_TYPE,
  useDeleteImage,
  useGetImageList
} from '@/services/admin/ideMgmt';
import { IdeImageCreatePopup } from './IdeImageCreatePopup';
import { IdeImageUpdatePopup } from './IdeImageUpdatePopup';

type SearchFormValues = {
  searchKeyword: string;
  category: string;
};

type SearchParams = Required<Pick<GetImageListRequest, 'page' | 'size'>> & Omit<GetImageListRequest, 'page' | 'size'>;

/**
 * 관리 > IDE 관리 > 이미지 관리 (TAB)
 */
export const IdeImageListPage = () => {
  const navigate = useNavigate();
  const { showDeleteItemNotSelected, showDeleteConfirm, showDeleteComplete } = useCommonPopup();

  // 검색 폼 상태 (UI 입력값)
  const { filters: searchForm, updateFilters: setSearchForm } = useBackRestoredState<SearchFormValues>(STORAGE_KEYS.SEARCH_VALUES.IDE_IMAGE_LIST_FORM, {
    searchKeyword: '',
    category: '전체',
  });

  // 검색 파라미터 (API 요청값)
  const { filters: searchParams, updateFilters: setSearchParams } = useBackRestoredState<SearchParams>(STORAGE_KEYS.SEARCH_VALUES.IDE_IMAGE_LIST_PARAMS, {
    page: 1,
    size: 12,
    keyword: '',
    imgG: undefined,
  });

  // 이미지 등록 팝업 상태
  const [isCreatePopupOpen, setIsCreatePopupOpen] = useState(false);

  // 이미지 수정 팝업 상태
  const [isUpdatePopupOpen, setIsUpdatePopupOpen] = useState(false);
  const [selectedImageForUpdate, setSelectedImageForUpdate] = useState<ImageRowData | null>(null);

  // 선택된 이미지 ID 목록
  const [selectedIds, setSelectedIds] = useState<string[]>([]);

  // API 호출
  const { data, refetch } = useGetImageList(searchParams);
  const { mutate: deleteImages, isPending: isDeletingImages } = useDeleteImage();

  /**
   * 선택된 이미지 삭제
   */
  const handleDeleteImages = useCallback(
    (imageIds: string[]) => {
      // 1. 선택된 항목 없음 검증
      if (imageIds.length === 0 || isDeletingImages) {
        showDeleteItemNotSelected();
        return;
      }

      // 2. 삭제 확인
      showDeleteConfirm({
        onConfirm: () => {
          // 3. 삭제 API 호출
          deleteImages(
            { uuids: imageIds },
            {
              onSuccess: () => {
                // 삭제 완료 메시지 표시
                const itemName = imageIds.length === 1 ? '이미지가' : `이미지 ${imageIds.length}개`;
                showDeleteComplete({
                  itemName,
                  onConfirm: () => {
                    // 선택 초기화 및 데이터 새로고침
                    setSelectedIds([]);
                    setSearchParams({ ...searchParams, page: 1 });
                    refetch();
                  },
                });
              },
            }
          );
        },
      });
    },
    [deleteImages, isDeletingImages, refetch, searchParams, setSearchParams, showDeleteConfirm, showDeleteItemNotSelected, showDeleteComplete]
  );

  /**
   * 이미지 수정 완료 핸들러
   */
  const handleUpdateComplete = useCallback(() => {
    setIsUpdatePopupOpen(false);
    setSelectedImageForUpdate(null);
    refetch();
  }, [refetch]);

  /**
   * 그리드 행 클릭 핸들러 (상세 페이지 이동)
   */
  const handleRowClick = (params: any) => {
    const imageId = params.data?.uuid;
    if (imageId) {
      navigate(`/admin/ide-mgmt/image/${imageId}`);
    }
  };

  /**
   * 검색 실행 핸들러
   */
  const handleSearch = useCallback(() => {
    const imgG = searchForm.category === '전체' ? undefined : UI_LABEL_TO_IMAGE_TYPE[searchForm.category];

    setSearchParams({
      page: 1,
      size: searchParams.size,
      keyword: searchForm.searchKeyword || undefined,
      imgG,
    });
  }, [searchForm, searchParams.size, setSearchParams]);

  /**
   * 페이지 변경 핸들러
   */
  const handlePageChange = useCallback(
    (page: number) => {
      setSearchParams(prev => ({ ...prev, page }));
    },
    [setSearchParams]
  );

  /**
   * 그리드 체크박스 선택 핸들러
   */
  const handleGridCheck = useCallback((selectedRows: any[]) => {
    const newSelectedIds = selectedRows.map(row => row.uuid);
    setSelectedIds(newSelectedIds);
  }, []);

  // 더보기 메뉴 설정
  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          label: '수정',
          action: 'modify',
          onClick: (row: any) => {
            // 선택된 이미지 데이터 저장 후 수정 팝업 열기
            setSelectedImageForUpdate(row);
            setIsUpdatePopupOpen(true);
          },
        },
        {
          label: '삭제',
          action: 'delete',
          onClick: (row: any) => {
            void handleDeleteImages([row.uuid]);
          },
        },
      ],
      isActive: () => true,
    }),
    [handleDeleteImages]
  );

  // API 응답 데이터를 그리드 행 데이터로 변환
  const rowData: ImageRowData[] = useMemo(() => {
    if (!data?.content) return [];

    return data.content.map((item, index) => ({
      id: String((searchParams.page - 1) * searchParams.size + index + 1),
      uuid: item.uuid,
      toolName: IMAGE_TYPE_LABEL[item.imgG],
      imageName: item.imgNm,
      description: item.dtlCtnt,
      createdDate: item.fstCreatedAt,
      modifiedDate: item.lstUpdatedAt,
    }));
  }, [data?.content, searchParams.page, searchParams.size]);

  // 그리드 컬럼 정의
  const columnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id',
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
        headerName: '도구명',
        field: 'toolName',
        width: 272,
        cellStyle: {
          paddingLeft: '16px',
        },
        sortable: false,
      },
      {
        headerName: '이미지명',
        field: 'imageName',
        width: 272,
        cellStyle: {
          paddingLeft: '16px',
        },
        sortable: false,
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
        headerName: '설명',
        field: 'description',
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
        sortable: false,
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
        headerName: '생성일시',
        field: 'createdDate',
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
        sortable: false,
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate',
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
        sortable: false,
      },
      {
        headerName: '',
        field: 'more',
        width: 56,
        sortable: false,
        suppressHeaderMenuButton: true,
      },
    ],
    []
  );

  return (
    <>
      {/* 검색 영역 */}
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
                          value={searchForm.searchKeyword}
                          placeholder='이미지명, 설명 입력'
                          onChange={e => {
                            setSearchForm(prev => ({ ...prev, searchKeyword: e.target.value }));
                          }}
                          onKeyDown={e => {
                            if (e.key === 'Enter') {
                              handleSearch();
                            }
                          }}
                        />
                      </div>
                    </td>
                    <th>
                      <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                        도구명
                      </UITypography>
                    </th>
                    <td>
                      <div>
                        <UIDropdown
                          value={searchForm.category}
                          placeholder='전체'
                          options={[
                            { value: '전체', label: '전체' },
                            { value: 'Jupyter Notebook', label: 'Jupyter Notebook' },
                            { value: 'VS Code', label: 'VS Code' },
                          ]}
                          onSelect={value => setSearchForm(prev => ({ ...prev, category: value }))}
                        />
                      </div>
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

      {/* 그리드 영역 */}
      <UIArticle className='article-grid'>
        <UIListContainer>
          <UIListContentBox.Header>
            <div className='w-full'>
              <UIUnitGroup gap={16} direction='column'>
                <div className='flex justify-between w-full items-center'>
                  <div className='flex-shrink-0'>
                    <div style={{ width: '168px', paddingRight: '8px' }}>
                      <UIDataCnt count={data?.totalElements || 0} prefix='총' unit='건' />
                    </div>
                  </div>
                  <div className='flex items-center gap-2'>
                    <UIButton2 className='btn-tertiary-outline' onClick={() => setIsCreatePopupOpen(true)}>
                      이미지 등록
                    </UIButton2>
                    <div style={{ width: '160px', flexShrink: 0 }}>
                      <UIDropdown
                        value={String(searchParams.size)}
                        disabled={rowData.length === 0}
                        options={[
                          { value: '12', label: '12개씩 보기' },
                          { value: '36', label: '36개씩 보기' },
                          { value: '60', label: '60개씩 보기' },
                        ]}
                        onSelect={(value: string) => {
                          setSearchParams(prev => ({ ...prev, page: 1, size: Number(value) }));
                        }}
                        onClick={() => {}}
                        height={40}
                        variant='dataGroup'
                      />
                    </div>
                  </div>
                </div>
              </UIUnitGroup>
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            <UIGrid type='multi-select' rowData={rowData} columnDefs={columnDefs} moreMenuConfig={moreMenuConfig} onClickRow={handleRowClick} onCheck={handleGridCheck} />
          </UIListContentBox.Body>
          <UIListContentBox.Footer className='ui-data-has-btn'>
            <UIButton2 className='btn-option-outlined' style={{ width: '40px' }} onClick={() => handleDeleteImages(selectedIds)} disabled={rowData.length === 0}>
              삭제
            </UIButton2>
            <UIPagination currentPage={searchParams.page} totalPages={data?.totalPages || 1} onPageChange={handlePageChange} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>

      {/* 이미지 등록 팝업 */}
      <IdeImageCreatePopup isOpen={isCreatePopupOpen} onClose={() => setIsCreatePopupOpen(false)} onSave={refetch} />

      {/* 이미지 수정 팝업 */}
      <IdeImageUpdatePopup
        isOpen={isUpdatePopupOpen}
        onClose={() => {
          setIsUpdatePopupOpen(false);
          setSelectedImageForUpdate(null);
        }}
        onSave={handleUpdateComplete}
        initialData={
          selectedImageForUpdate
            ? {
                toolName: selectedImageForUpdate.toolName,
                imageName: selectedImageForUpdate.imageName,
                description: selectedImageForUpdate.description,
              }
            : undefined
        }
        imageUuid={selectedImageForUpdate?.uuid}
      />
    </>
  );
};
