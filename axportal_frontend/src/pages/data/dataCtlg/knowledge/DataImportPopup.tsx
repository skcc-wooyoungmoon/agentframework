import React, { useState, useEffect } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIButton2, UITypography, UIFileBox } from '@/components/UI/atoms';
import { UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup, UIInput, UIStepper, UIGroup, UIFormField } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';

import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown/component';
import { useModal } from '@/stores/common/modal';
import { DocumentListModal } from './DocumentListModal';
import { useExecuteDataiku } from '@/services/knowledge/knowledge.services';
import { useGetMDPackageList, useGetOriginSystems } from '@/services/data/storage/dataStorage.services';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';

interface DataImportPopupProps {
  isOpen: boolean;
  onClose: () => void;
  knowledgeId?: string; // 지식 ID 추가
  onComplete?: () => void; // 완료 시 콜백
}

export const DataImportPopup: React.FC<DataImportPopupProps> = ({ isOpen, onClose, knowledgeId, onComplete }) => {
  const [currentStep, setCurrentStep] = useState(1); // 현재 스텝 (1: 데이터 선택, 2: 선택 데이터 확인)
  const [searchValue, setSearchValue] = useState('');
  const [searchInputValue, setSearchInputValue] = useState(''); // 입력 중인 검색어
  const [selectedSourceSystem, setSelectedSourceSystem] = useState('전체');
  const [currentPage, setCurrentPage] = useState(1);
  const [selectedItems, setSelectedItems] = useState<any[]>([]); // 선택된 항목들 (id 기준으로 중복 제거)
  const [selectedItemsMap, setSelectedItemsMap] = useState<Map<string, any>>(new Map()); // id를 key로 하는 Map
  const countPerPage = 12; // 고정값

  // 공통 팝업 훅
  const { showCancelConfirm } = useCommonPopup();

  // 모달 훅
  const { openModal, openAlert } = useModal();

  // 팝업이 열릴 때 상태 초기화
  useEffect(() => {
    if (isOpen) {
      setSelectedItems([]);
      setSelectedItemsMap(new Map());
      setCurrentStep(1);
      setSearchValue('');
      setSearchInputValue('');
      setSelectedSourceSystem('전체');
      setCurrentPage(1);
    }
  }, [isOpen]);

  // Dataiku 실행 mutation
  const executeDataikuMutation = useExecuteDataiku({
    onSuccess: () => {
      onComplete?.();
    },
    onError: /* async (error: any) */ () => {
      // console.error(`Dataiku 실행 중 오류가 발생했습니다.\n${error?.response?.data?.message || error?.message || '알 수 없는 오류'}`);
      // await openAlert({
      //   title: '오류',
      //   message: `Dataiku 실행 중 오류가 발생했습니다.\n${error?.response?.data?.message || error?.message || '알 수 없는 오류'}`,
      // });
    },
  });

  // 원천 시스템 목록 조회 (팝업이 열릴 때만 실행)
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

  // 취소 버튼 클릭
  const handleCancel = () => {
    showCancelConfirm({
      onConfirm: () => {
        onClose();
      },
    });
  };

  // 다음 버튼 클릭
  const handleNext = () => {
    if (selectedItems.length > 0) {
      setCurrentStep(2);
    }
  };

  // 이전 버튼 클릭
  const handlePrevious = () => {
    setCurrentStep(1);
  };

  // 선택된 항목 삭제 핸들러
  const handleRemoveItem = (itemId: string) => {
    const newMap = new Map(selectedItemsMap);
    newMap.delete(itemId);
    setSelectedItemsMap(newMap);
    setSelectedItems(Array.from(newMap.values()));
  };

  // 저장 버튼 클릭 - Dataiku 실행
  const handleSave = async () => {
    if (selectedItems.length === 0) {
      await openAlert({
        title: '안내',
        message: '선택된 데이터가 없습니다.',
      });
      return;
    }

    // Dataiku 실행 요청 데이터 구성
    const requestData = {
      knowledgeId: knowledgeId,
      selectedDatasets: selectedItems.map(item => ({
        datasetCardId: item.datasetCardId,
        datasetCardName: item.datasetCardName || item.name,
        datasetCd: item.datasetCd,
        originSystemCd: item.originSystemCd,
        originSystemName: item.originSystemName || item.depth,
      })),
    };

    // console.log('🚀 Dataiku 실행 요청:', requestData);
    executeDataikuMutation.mutate(requestData);
  };

  // 스테퍼 데이터
  const stepperItems = [
    { step: 1, label: '데이터 선택' },
    { step: 2, label: '선택 데이터 확인' },
  ];

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
        prev.forEach(item => {
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

  // 원천시스템 옵션 (전체 옵션 포함)
  const sourceSystemOptions = React.useMemo(() => {
    return [{ value: '전체', label: '전체' }, ...sourceSystems];
  }, [sourceSystems]);

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
    <UILayerPopup
      isOpen={isOpen}
      onClose={onClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        /* 좌측 Step 영역 콘텐츠 */
        <UIPopupAside>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='지식 데이터 추가' position='left' />
          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle>
              <UIStepper currentStep={currentStep} items={stepperItems} direction='vertical' />
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} onClick={handleSave} disabled={currentStep === 1}>
                  저장
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      {/* 우측 Contents 영역 콘텐츠 */}
      <section className='section-popup-content'>
        {currentStep === 1 ? (
          <>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='데이터 선택' description='지식REPO에 추가할 데이터 저장소의 MD파일 패키지를 선택 해 주세요.' position='right' />

            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              <UIArticle className='article-grid'>
                <UIListContainer>
                  <UIListContentBox.Header>
                    <div className='flex items-center'>
                      <div style={{ width: '182px', paddingRight: '8px' }}>
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
                          }}
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
                    <UIPagination
                      currentPage={currentPage}
                      totalPages={mdPackageData?.totalPages || 1}
                      onPageChange={page => setCurrentPage(page)}
                      className='flex justify-center'
                    />
                  </UIListContentBox.Footer>
                </UIListContainer>
              </UIArticle>
            </UIPopupBody>
            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2
                    className='btn-secondary-blue'
                    style={{ width: '80px' }}
                    disabled={selectedItems.length === 0}
                    onClick={() => {
                      // console.log('🔵 다음 버튼 클릭 - 선택된 항목:', selectedItems.length, '개');
                      handleNext();
                    }}
                  >
                    다음
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </>
        ) : (
          <>
            {/* Step 2: 선택 데이터 확인 */}
            <UIPopupHeader title='선택 데이터 확인' position='right' />

            <UIPopupBody>
              <UIArticle>
                <UIFormField gap={8} direction='column'>
                  <UIDataCnt count={selectedItems.length} prefix='선택된 데이터 총' />
                  <UIGroup gap={16} direction='column'>
                    <div>
                      {selectedItems.length > 0 && (
                        <div className='space-y-3'>
                          {selectedItems.map((item: any) => (
                            <UIFileBox
                              key={item.id}
                              variant='default'
                              size='full'
                              fileName={item.datasetCardName || item.name}
                              onFileRemove={() => handleRemoveItem(item.id)}
                              className='w-full'
                            />
                          ))}
                        </div>
                      )}
                    </div>
                  </UIGroup>
                </UIFormField>
              </UIArticle>
            </UIPopupBody>

            {/* 레이어 팝업 footer */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-secondary-blue' style={{ width: '80px' }} onClick={handlePrevious}>
                    이전
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </>
        )}
      </section>
    </UILayerPopup>
  );
};
