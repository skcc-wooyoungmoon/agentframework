import React, { useState, useMemo } from 'react';

import { UIDataCnt } from '@/components/UI';
import { UIDataList, UIUnitGroup } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPagination } from '@/components/UI/atoms';
import { UIInput } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import type { ColDef } from 'ag-grid-community';
import { useGetMDPackageDetail } from '@/services/data/storage/dataStorage.services';
import dateUtils from '@/utils/common/date.utils';

interface DocumentListModalProps {
  mdPackage?: {
    datasetCd: string;
    datasetCardName: string;
  };
}

export const DocumentListModal = ({ mdPackage }: DocumentListModalProps) => {
  // 검색어 입력값 (실시간 입력)
  const [searchValue, setSearchValue] = useState('');
  // 실제 검색에 사용할 값 (엔터키 또는 검색 버튼 클릭 시 업데이트)
  const [appliedSearchValue, setAppliedSearchValue] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const countPerPage = 5;
  // Dropdown 값 관리
  const [searchType, setSearchType] = useState<'fileNm' | 'uuid'>('fileNm');

  // 문서 목록 조회 - appliedSearchValue 사용
  const searchParams = useMemo(
    () => ({
      datasetCd: mdPackage?.datasetCd || '',
      page: currentPage,
      countPerPage: countPerPage,
      ...(searchType === 'fileNm' ? { searchWord: appliedSearchValue } : { uuid: appliedSearchValue }),
    }),
    [mdPackage?.datasetCd, currentPage, countPerPage, appliedSearchValue]
  );

  const { data: documentsData, isLoading } = useGetMDPackageDetail(searchParams, {
    // 이전 데이터를 유지하여 로딩 중에도 빈 화면이 보이지 않도록 함
    placeholderData: previousData => previousData,
  });

  // console.log('======= DocumentListModal =======', documentsData);
  // console.log('======= mdPackage =======', mdPackage);

  // useGetMDPackageDetail이 실행되기 전까지는 화면이 안 보이게 함
  // datasetCd가 없거나 로딩 중일 때는 화면을 숨김

  // 에러 발생하여 아래 코드 주석 처리
  // if (isLoading) {
  //   return null;
  // }

  // 그리드 데이터 변환
  const rowData = useMemo(() => {
    if (!documentsData?.content) return [];

    return documentsData.content.map((item, index) => ({
      no: (currentPage - 1) * countPerPage + index + 1,
      id: item.docUuid,
      name: item.docTitle,
      title: item.originMetadata?.origin_metadata?.title || '',
      attachName: item.originMetadata?.origin_metadata?.attach_nm || '',
      uuid: item.docUuid,
      createdDate: dateUtils.formatYyyyMmDdToDot(item.docCreateDay),
      modifiedDate: dateUtils.formatYyyyMmDdToDot(item.docMdfcnDay),
      ...item,
    }));
  }, [documentsData, currentPage, countPerPage]);

  // 검색어 엔터 시 조회 핸들러
  const handleSearch = () => {
    setAppliedSearchValue(searchValue); // 입력값을 실제 검색값으로 적용
    setCurrentPage(1); // 페이지를 1로 리셋
    // refetch는 appliedSearchValue가 변경되면 자동으로 호출됨
  };

  const columnDefs: ColDef[] = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no',
        width: 56,
        cellStyle: {
          textAlign: 'center' as const,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        } as any,
        sortable: false,
        suppressHeaderMenuButton: true,
      },
      {
        headerName: '이름',
        field: 'name',
        flex: 1,
        minWidth: 243,
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
        headerName: '타이틀',
        field: 'title',
        width: 253,
        sortable: false,
        cellStyle: { paddingLeft: 16 },
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
        headerName: '첨부파일 이름',
        field: 'attachName',
        width: 253,
        sortable: false,
        cellStyle: { paddingLeft: 16 },
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
        headerName: 'UUID',
        field: 'uuid',
        width: 320,
        sortable: false,
        cellStyle: { paddingLeft: 16 },
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
        sortable: false,
        cellStyle: { paddingLeft: 16 },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate',
        width: 180,
        sortable: false,
        cellStyle: { paddingLeft: 16 },
      },
    ],
    []
  );

  return (
    <section className='section-modal'>
      <UIArticle className='article-dataList'>
        <div className='article-body'>
          <UIDataList
            gap={12}
            direction='column'
            datalist={[
              { dataName: '이름', dataValue: mdPackage?.datasetCardName || '' },
              { dataName: '구성 MD파일', dataValue: `${documentsData?.totalElements?.toLocaleString() || 0}개` },
            ]}
          >
            {null}
          </UIDataList>
        </div>
      </UIArticle>

      <UIArticle className='article-grid'>
        <UIListContainer>
          <UIListContentBox.Header>
            <div className='w-full'>
              <UIUnitGroup gap={16} direction='column'>
                <div className='flex justify-between w-full items-center'>
                  <div className='flex-shrink-0'>
                    <div style={{ width: '168px', paddingRight: '8px' }}>
                      <UIDataCnt count={documentsData?.totalElements || 0} prefix='MD파일 총' />
                    </div>
                  </div>
                  <div className='flex gap-2 flex-shrink-0'>
                    <div style={{ width: '160px', flexShrink: 0 }}>
                      <UIDropdown
                        value={String(searchType)}
                        options={[
                          { value: 'fileNm', label: '파일명' },
                          { value: 'uuid', label: 'UUID' },
                        ]}
                        onSelect={(value: string) => {
                          setSearchType(value as 'fileNm' | 'uuid');
                          setSearchValue('');
                        }}
                        onClick={() => {}}
                        height={40}
                        variant='dataGroup'
                        disabled={false}
                      />
                    </div>
                    <div className='w-[360px]'>
                      <UIInput.Search
                        value={searchValue}
                        onChange={e => {
                          setSearchValue(e.target.value);
                        }}
                        placeholder='검색어 입력'
                        onKeyDown={e => {
                          if (e.key === 'Enter') {
                            handleSearch();
                          }
                        }}
                      />
                    </div>
                  </div>
                </div>
              </UIUnitGroup>
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            <UIGrid
              type='default'
              loading={isLoading}
              rowData={rowData}
              columnDefs={columnDefs}
              /* onClickRow={(params: any) => {
                console.log('다중 onClickRow', params);
              }}
              onCheck={(selectedIds: any[]) => {
                console.log('다중 onSelect', selectedIds);
              }} */
            />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination currentPage={currentPage} totalPages={documentsData?.totalPages || 1} onPageChange={page => setCurrentPage(page)} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </section>
  );
};
