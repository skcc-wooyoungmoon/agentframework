import { useEffect, useMemo, useState } from 'react';

import { UIDataCnt, UIToggle } from '@/components/UI';
import { UIPagination } from '@/components/UI/atoms';
import { UICardList } from '@/components/UI/molecules/card/UICardList';
import { UIGridCard } from '@/components/UI/molecules/card/UIGridCard';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { useGetProcList } from '@/services/data/tool/dataToolProc.services';
import { useNavigate } from 'react-router-dom';

interface ProcListProps {
  isActiveTab?: boolean;
}

export function ProcList({ isActiveTab }: ProcListProps) {
  const [view, setView] = useState<'grid' | 'card'>('grid');
  const [page, setPage] = useState(1);
  const [size, setSize] = useState(12);
  const navigate = useNavigate();

  const { data: procData, isSuccess } = useGetProcList(
    {
      page,
      size,
      sort: 'created_at,desc',
      filter: '',
    },
    { enabled: isActiveTab }
  );

  // 총 페이지 (API 기준)
  const totalPages = isSuccess ? procData?.totalPages || 1 : 1;

  // 그리드 행 클릭 핸들러
  const handleRowClick = (item: any) => {
    navigate(`/data/dataTools/proc/${item.id}`);
  };

  // 그리드 컬럼 정의
  const columnDefs = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id' as any,
        width: 60,
        minWidth: 60,
        maxWidth: 60,
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
        valueGetter: (params: any) => (page - 1) * size + params.node.rowIndex + 1,
      },
      {
        headerName: '이름',
        field: 'name',
        width: 200,
      },
      {
        headerName: '설명',
        field: 'description',
        flex: 1,
      },
    ],
    [procData]
  );

  const [dataList, setDataList] = useState<any[]>([]);

  useEffect(() => {
    if (isSuccess && procData) {
      setDataList(procData.content || []);
    }
    // console.log('dataList', dataList);
  }, [procData, isSuccess]);

  // 페이지네이션 핸들러 수정
  const handlePageChange = (newPage: number) => {
    setPage(newPage);
  };

  return (
    <>
      {/* 데이터 그룹 컴포넌트 */}
      <UIArticle className='article-grid'>
        <UIListContainer>
          <UIListContentBox.Header>
            <div className='grid-header-left'>
              <UIDataCnt count={procData?.totalElements || 0} prefix='총' />
            </div>
            <div className='flex items-center gap-2'>
              <div style={{ width: '180px', flexShrink: 0 }}>
                <UIDropdown
                  value={`${size}개씩 보기`}
                  options={[
                    { value: '12', label: '12개씩 보기' },
                    { value: '36', label: '36개씩 보기' },
                    { value: '60', label: '60개씩 보기' },
                  ]}
                  onSelect={(value: string) => {
                    setSize(Number(value));
                    setPage(1);
                  }}
                  height={40}
                  variant='dataGroup'
                />
              </div>
              <UIToggle variant='dataView' checked={view === 'card'} onChange={checked => setView(checked ? 'card' : 'grid')} />
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            {view === 'grid' ? (
              <UIGrid<any>
                type='default'
                rowData={dataList}
                columnDefs={columnDefs as any}
                onClickRow={(params: any) => {
                  handleRowClick(params.data);
                  // console.log('params', params.data);
                }}
              />
            ) : (
              <UICardList
                rowData={dataList}
                flexType='none'
                card={(item: any) => (
                  <UIGridCard key={item.id} id={item.id} title={item.name} caption={item.description || ''} data={item} onClick={() => handleRowClick(item)} rows={[]} />
                )}
              />
            )}
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination currentPage={page} totalPages={totalPages} hasNext={procData?.hasNext} onPageChange={handlePageChange} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </>
  );
}
