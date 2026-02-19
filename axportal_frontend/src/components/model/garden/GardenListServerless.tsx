import { forwardRef, memo, useEffect, useImperativeHandle, useMemo } from 'react';

import { useNavigate } from 'react-router-dom';

import { Button } from '@/components/common/auth';
import { UIDataCnt } from '@/components/UI';
import { UIBox, UIPagination, UITypography } from '@/components/UI/atoms';
import { UIGroup, UIInput } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { useGetModelTypes } from '@/services/model/ctlg/modelCtlg.services';
import { useGetModelGardenList } from '@/services/model/garden/modelGarden.services';
import type { GetModelGardenRequest } from '@/services/model/garden/types';
import { dateUtils } from '@/utils/common';

export const GardenListServerless = forwardRef<
  { refetch: () => void },
  { filters: Omit<GetModelGardenRequest, 'dplyTyp' | 'status'>; updateFilters: (newFilters: Partial<Omit<GetModelGardenRequest, 'dplyTyp' | 'status'>>) => void }
>(({ filters, updateFilters }, ref) => {
  const navigate = useNavigate();

  // serverless 모델 리스트 조회
  const {
    data: modelGardenList,
    refetch,
    isLoading,
  } = useGetModelGardenList({
    page: filters.page,
    size: filters.size,
    dplyTyp: 'serverless',
    search: filters.search,
    type: filters.type === '전체' ? '' : filters.type,
  });

  // ref를 통해 refetch 함수 노출
  useImperativeHandle(ref, () => ({
    refetch,
  }));

  const handleSearch = () => {
    updateFilters({ page: 1 });
    refetch();
  };

  useEffect(() => {
    refetch();
  }, [filters.page, filters.size]);

  const rowData = useMemo(
    () =>
      modelGardenList?.content.map((item, index) => ({
        ...item,
        no: index + 1 + (filters.page - 1) * filters.size,
      })) ?? [],
    [modelGardenList?.content]
  );

  const handleClickRow = (params: any) => {
    navigate(`${params.data.id}`);
  };

  // 그리드 컬럼 정의 (피그마 기반)
  const columnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no',
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
        headerName: '모델명',
        field: 'name',
        width: 272,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '설명',
        field: 'description',
        flex: 1,
        showTooltip: true,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: memo((params: any) => {
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
        headerName: '모델유형',
        field: 'type',
        width: 120,
      },
      {
        headerName: '생성일시',
        field: 'created_at',
        width: 180,
        valueFormatter: (params: any) => {
          return dateUtils.formatDate(params.data.created_at, 'datetime');
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'updated_at',
        width: 180,
        valueFormatter: (params: any) => {
          return dateUtils.formatDate(params.data.updated_at, 'datetime');
        },
      },
    ],
    []
  );

  const { data: modelTypes } = useGetModelTypes();
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
                        value={filters.search}
                        onChange={e => {
                          updateFilters({ ...filters, search: e.target.value });
                        }}
                        placeholder='모델명, 설명 입력'
                      />
                    </td>
                    <th>
                      <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                        모델유형
                      </UITypography>
                    </th>
                    <td>
                      <UIDropdown
                        value={filters.type || '전체'}
                        placeholder='조회 조건 선택'
                        options={[{ value: '전체', label: '전체' }, ...(modelTypes?.types.map(type => ({ value: type, label: type })) || [])]}
                        onSelect={value => updateFilters({ ...filters, type: value })}
                      />
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', width: '128px' }}>
              <Button className='btn-secondary-blue' style={{ width: '100%' }} onClick={handleSearch}>
                조회
              </Button>
            </div>
          </UIGroup>
        </UIBox>
      </UIArticle>

      <UIArticle className='article-grid'>
        {/* 전체 데이터 목록 */}
        <UIListContainer>
          <UIListContentBox.Header>
            <div className='flex-shrink-0'>
              <UIDataCnt count={modelGardenList?.totalElements || 0} prefix='총' />
            </div>
            <div className='flex items-center gap-2'>
              <div style={{ width: '180px', flexShrink: 0 }}>
                <UIDropdown
                  disabled={(modelGardenList?.totalElements || 0) === 0}
                  value={String(filters.size)}
                  options={[
                    { value: '12', label: '12개씩 보기' },
                    { value: '36', label: '36개씩 보기' },
                    { value: '60', label: '60개씩 보기' },
                  ]}
                  onSelect={(value: string) => {
                    updateFilters({ ...filters, size: Number(value) });
                  }}
                  height={40}
                  variant='dataGroup'
                />
              </div>
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            <UIGrid rowData={rowData} loading={isLoading} columnDefs={columnDefs} onClickRow={handleClickRow} />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination
              currentPage={filters.page}
              totalPages={modelGardenList?.totalPages || 1}
              onPageChange={page => updateFilters({ ...filters, page })}
              className='flex justify-center'
            />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </>
  );
});
