import React, { forwardRef, useEffect, useImperativeHandle, useMemo } from 'react';

import { useNavigate } from 'react-router-dom';

import { Button } from '@/components/common/auth';
import { UIDataCnt } from '@/components/UI';
import { UIBox, UILabel, UIPagination, UITypography, type UILabelIntent } from '@/components/UI/atoms';
import { UIGroup } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIInput } from '@/components/UI/molecules/input';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { MODEL_GARDEN_STATUS, MODEL_GARDEN_STATUS_OPTIONS, MODEL_GARDEN_STATUS_TYPE } from '@/constants/model/garden.constants';
import { usePermissionCheck } from '@/hooks/common/auth';
import { useGetModelGardenList } from '@/services/model/garden/modelGarden.services';
import type { GetModelGardenRequest, ModelGardenInfo } from '@/services/model/garden/types';
import { useModal } from '@/stores/common/modal';

export const GardenListSelfHosting = forwardRef<
  { refetch: () => void },
  {
    onOpenInPopup: (data: ModelGardenInfo) => void;
    filters: Omit<GetModelGardenRequest, 'dplyTyp' | 'type'>;
    updateFilters: (newFilters: Partial<Omit<GetModelGardenRequest, 'dplyTyp' | 'type'>>) => void;
  }
>(({ onOpenInPopup, filters, updateFilters }, ref) => {
  const { openAlert } = useModal();
  const navigate = useNavigate();
  const { checkPermissionAndShowAlert } = usePermissionCheck();

  // self-hosting 모델 리스트 조회
  const {
    data: modelGardenList,
    refetch,
    isLoading,
  } = useGetModelGardenList({
    page: filters.page,
    size: filters.size,
    dplyTyp: 'self-hosting',
    search: filters.search,
    status: filters.status === '전체' ? '' : filters.status,
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

  // 그리드 컬럼 정의
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
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '모델명',
        field: 'name',
        width: 280,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '상태',
        field: 'statusNm',
        width: 120,
        cellRenderer: React.memo((params: any) => {
          const statusInfo = MODEL_GARDEN_STATUS[params.value as keyof typeof MODEL_GARDEN_STATUS] || {
            value: 'stop',
            label: '알 수 없음',
          };

          return (
            <UILabel variant='badge' intent={statusInfo.value as UILabelIntent}>
              {statusInfo.label}
            </UILabel>
          );
        }),
      },
      {
        headerName: '설명',
        field: 'description',
        flex: 1,
        minWidth: 380,
      },
      {
        headerName: '크기',
        field: 'size',
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
        valueFormatter: (params: any) => {
          // return `${stringUtils.formatBytesToGB(params.data.size)}GB`;
          return (params.value?.toString() ?? '0') + 'GB';
        },
      },
      {
        headerName: '모델 반입',
        field: 'button',
        width: 130,
        cellStyle: {
          paddingLeft: '16px',
        },
        cellRenderer: (params: { data: ModelGardenInfo; node: { rowIndex: number } }) => {
          return (
            <Button
              auth={AUTH_KEY.MODEL.SELF_HOSTING_MODEL_IMPORT}
              onClick={() => {
                checkPermissionAndShowAlert(() => {
                  // 선택하여 반입
                  if (params.data.statusNm === MODEL_GARDEN_STATUS_TYPE.PENDING) {
                    onOpenInPopup(params.data);
                  } else {
                    openAlert({
                      title: '안내',
                      message: '이미 반입완료되었거나 반입중인 모델입니다.',
                    });
                  }
                });
              }}
              className='btn-text-14-underline-point'
            >
              모델 반입
            </Button>
          );
        },
      },
    ],
    []
  );

  const rowData = useMemo(() => {
    return (
      modelGardenList?.content.map((item, index) => ({
        ...item,
        no: index + 1 + (filters.page - 1) * filters.size,
      })) || []
    );
  }, [modelGardenList]);

  const handleClickRow = (params: any) => {
    navigate(`${params.data.id}`, {
      state: filters,
    });
  };

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
                          value={filters.search}
                          placeholder='모델명, 설명 입력'
                          onChange={e => {
                            updateFilters({ ...filters, search: e.target.value });
                          }}
                        />
                      </div>
                    </td>
                    <th>
                      <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                        상태
                      </UITypography>
                    </th>
                    <td>
                      <UIDropdown value={filters.status || ''} options={MODEL_GARDEN_STATUS_OPTIONS} onSelect={value => updateFilters({ ...filters, status: value })} />
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            <div style={{ width: '128px' }}>
              <Button onClick={handleSearch} className='btn-secondary-blue' style={{ width: '100%' }}>
                조회
              </Button>
            </div>
          </UIGroup>
        </UIBox>
      </UIArticle>

      {/* 데이터 그룹 컴포넌트 */}
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
            <UIGrid<ModelGardenInfo> rowData={rowData} loading={isLoading} columnDefs={columnDefs} onClickRow={handleClickRow} />
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
