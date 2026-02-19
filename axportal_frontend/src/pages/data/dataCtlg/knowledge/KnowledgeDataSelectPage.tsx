import React, { useState, useEffect } from 'react';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown/component';
import { UIInput } from '@/components/UI/molecules/input';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIPagination } from '@/components/UI';
import { useModal } from '@/stores/common/modal';
import { useGetMDPackageList, useGetOriginSystems } from '@/services/data/storage/dataStorage.services';
import { DocumentListModal } from './DocumentListModal';

type KnowledgeDataSelectPageProps = {
  // selectedSourceSystem: string;
  // currentPage: number;
  selectedItems: any[];
  selectedItemsMap: Map<string, any>;
  isOpen: boolean;

  // setSelectedSourceSystem: (value: string) => void;
  // setCurrentPage: (value: number) => void;
  // // setSelectedItems: (value: any[]) => void;
  // setSelectedItemsMap: (value: Map<string, any>) => void;
  setSelectedItems: React.Dispatch<React.SetStateAction<any[]>>;
  setSelectedItemsMap: React.Dispatch<React.SetStateAction<Map<string, any>>>;

  // searchValue: string;
  // setSearchValue: (value: string) => void;
  // searchInputValue: string;
  // setSearchInputValue: (value: string) => void;
  // isOpen: boolean;
  // setIsOpen: (value: boolean) => void;
};

export const KnowledgeDataSelectPage: React.FC<KnowledgeDataSelectPageProps> = ({
  //   selectedSourceSystem,
  //   setSelectedSourceSystem,
  //   currentPage,
  //   setCurrentPage,
  // selectedItems,
  setSelectedItems,
  selectedItemsMap,
  setSelectedItemsMap,
  isOpen,
  // searchValue, 
  // setSearchValue, 
  // searchInputValue, 
  // setSearchInputValue, 
  // isOpen, 
  // setIsOpen 
}) => {
  const [searchValue, setSearchValue] = useState('');
  const [searchInputValue, setSearchInputValue] = useState(''); // 입력 중인 검색어
  const [selectedSourceSystem, setSelectedSourceSystem] = useState('전체');
  const [currentPage, setCurrentPage] = useState(1);
  // const [selectedItems, setSelectedItems] = useState<any[]>([]); // 선택된 항목들 (id 기준으로 중복 제거)
  // const [selectedItemsMap, setSelectedItemsMap] = useState<Map<string, any>>(new Map()); // id를 key로 하는 Map
  const countPerPage = 12; // 고정값

  // 모달 훅
  const { openModal } = useModal();


  useEffect(() => {
    if (isOpen) {
      setSearchValue('');
      setSearchInputValue('');
      setSelectedSourceSystem('전체');
      setCurrentPage(1);
    }
  }, [isOpen]);

  // 원천 시스템 목록 조회 (팝업이 열릴 때만 실행)
  //  const { data: originSystemsData } = useGetOriginSystems({ enabled: isOpen });
  const { data: originSystemsData } = useGetOriginSystems({ enabled: isOpen });

  const sourceSystems = React.useMemo(() => {
    if (!originSystemsData?.datasetReferList) return [];
    return originSystemsData.datasetReferList.map(system => ({
      value: system.datasetcardReferCd,
      label: system.datasetcardReferNm,
    }));
  }, [originSystemsData]);

  // MD 패키지 목록 조회
  const { data: mdPackageData, isLoading } = useGetMDPackageList(
    {
      page: currentPage,
      countPerPage: countPerPage,
      originSystemCd: selectedSourceSystem === '전체' ? undefined : selectedSourceSystem,
      searchWord: searchValue || undefined,
    },
    {
      enabled: isOpen, // 팝업이 열릴 때만 실행
      // 이전 데이터를 유지하여 로딩 중에도 빈 화면이 보이지 않도록 함
      placeholderData: previousData => previousData,
    }
  );

  // 검색 실행 함수
  const handleSearch = () => {
    setSearchValue(searchInputValue);
    setCurrentPage(1);
  };

  // 원천 시스템 변경 시
  const handleSourceSystemChange = (value: string) => {
    setSelectedSourceSystem(value);
    setCurrentPage(1);
  };


  // 원천시스템 옵션 (전체 옵션 포함)
  const sourceSystemOptions = React.useMemo(() => {
    return [{ value: '전체', label: '전체' }, ...sourceSystems];
  }, [sourceSystems]);


  // MD 패키지 데이터를 그리드용으로 변환
  const datasetData = React.useMemo(() => {
    if (!mdPackageData?.content) return [];

    return mdPackageData.content.map((item: any, index: number) => ({
      id: item.datasetCardId,
      no: (currentPage - 1) * countPerPage + index + 1,
      name: item.datasetCardName,
      description: item.datasetCardSummary,
      depth: item.originSystemName,
      datasetCd: item.datasetCd,
      datasetCardId: item.datasetCardId,
      datasetCardName: item.datasetCardName,
      originSystemName: item.originSystemName,
      originSystemCd: item.originSystemCd,
      // 원본 데이터 보관
      ...item,
    }));
  }, [mdPackageData, currentPage, countPerPage]);

  // 현재 페이지에서 선택되어야 할 항목들 (selectedItemsMap에 있는 항목들)
  const currentPageSelectedItems = React.useMemo(() => {
    return datasetData.filter((item: any) => selectedItemsMap.has(item.id));
  }, [datasetData, selectedItemsMap]);

  // 체크박스 선택 핸들러 - 현재 페이지의 선택 상태를 전체 선택 상태에 반영
  const handleSelectionChange = React.useCallback(
    (selectedRows: any[]) => {
      setSelectedItemsMap(prevMap => {
        const newMap = new Map(prevMap);

        // 현재 페이지의 모든 항목 ID 추출
        const currentPageIds = datasetData.map((item: any) => item.id);

        // 현재 페이지의 기존 선택 항목들을 제거
        currentPageIds.forEach((id: string) => {
          newMap.delete(id);
        });

        // 현재 페이지에서 새로 선택된 항목들을 추가
        selectedRows.forEach(row => {
          newMap.set(row.id, row);
        });

        return newMap;
      });

      setSelectedItems(prev => {
        // prevMap을 사용하지 않고 selectedRows 기반으로 새로 계산
        const tempMap = new Map();
        selectedRows.forEach(row => {
          tempMap.set(row.id, row);
        });

        // 다른 페이지의 선택 항목들 추가
        const currentPageIds = datasetData.map((item: any) => item.id);
        prev.forEach((item: any) => {
          if (!currentPageIds.includes(item.id)) {
            tempMap.set(item.id, item);
          }
        });

        const newSelectedItems = Array.from(tempMap.values());
        return newSelectedItems;
      });
    },
    [datasetData]
  );


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
        headerName: 'Dataset명',
        field: 'name' as const,
        width: 272,
        sortable: false,
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
        headerName: '설명',
        field: 'description' as const,
        flex: 1,
        showTooltip: true,
        sortable: false,
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
        headerName: '원천시스템',
        field: 'depth' as const,
        width: 120,
        sortable: false,
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
    ],
    []
  );

  return (
    <>
      <UIArticle className='article-grid'>
        <UIListContainer>
          <UIListContentBox.Header>
            <div className='flex items-center'>
              <div style={{ width: '182px', paddingRight: '8px' }}>
                {/* [251120_퍼블수정] 검수요청 현행화 수정 */}
                <UIDataCnt count={mdPackageData?.totalElements || 0} prefix='Dataset 총' />
              </div>
              <div className='flex items-center gap-2'>
                <UITypography variant='body-1' className='secondary-neutral-900'>
                  원천시스템
                </UITypography>
                <div style={{ width: '270px', flexShrink: 0 }}>
                  <UIDropdown value={String(selectedSourceSystem)} options={sourceSystemOptions} onSelect={handleSourceSystemChange} height={40} variant='dataGroup' />
                </div>
              </div>
            </div>

            <div style={{ display: 'flex', marginLeft: 'auto', gap: '0 12px' }}>
              <div style={{ width: '360px', flexShrink: 0 }}>
                {/* 251128_퍼블수정 속성값 수정 */}
                <UIInput.Search
                  value={searchInputValue}
                  placeholder='검색어 입력'
                  style={{
                    width: '100%',
                    boxSizing: 'border-box',
                  }}
                  onChange={e => {
                    setSearchInputValue(e.target.value);
                  }}
                  onKeyDown={e => {
                    if (e.key === 'Enter') {
                      handleSearch();
                    }
                  }
                  }
                />
              </div>
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            <UIGrid
              type='multi-select'
              loading={isLoading}
              rowData={datasetData}
              columnDefs={columnDefs}
              selectedDataList={currentPageSelectedItems}
              onClickRow={(params: any) => {
                // console.log('MD 패키지 클릭:', params);
                openModal({
                  title: 'MD파일 구성 조회',
                  type: 'large',
                  body: (
                    <DocumentListModal
                      mdPackage={{
                        datasetCd: params.data.datasetCd,
                        datasetCardName: params.data.name,
                      }}
                    />
                  ),
                  showFooter: false,
                });
              }}
              onCheck={(selectedRows: any[]) => {
                // console.log('🔴 onCheck 호출됨:', selectedRows.length, '개');
                handleSelectionChange(selectedRows);
              }}
            />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination currentPage={currentPage} totalPages={mdPackageData?.totalPages || 1} onPageChange={page => setCurrentPage(page)} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </>
  );
};
