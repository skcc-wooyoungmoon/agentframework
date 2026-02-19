import React, { useMemo, useState } from 'react';
import { useLocation, useParams } from 'react-router-dom';

import { UIButton2, UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { useGetMDPackageDetail } from '@/services/data/storage/dataStorage.services';
import type { GetMDPackageDetailRequest } from '@/services/data/storage/types';
import { useModal } from '@/stores/common/modal';
import dateUtils from '@/utils/common/date.utils';
import { MDPackageMetadataPopupPage } from './MDPackageMetadataPopupPage';

export function MDPackageDetailPage() {
  const { id } = useParams<{ id: string }>();
  const location = useLocation();
  const { openModal } = useModal();

  // search 타입
  const [searchValue, setSearchValue] = useState('');
  const [page, setPage] = useState(1);
  const [size] = useState(12); // 페이지당 표시 수를 12개로 고정

  // dropdown 값 관리
  const [searchType, setSearchType] = useState<'fileNm' | 'uuid'>('fileNm');
  // 검색어 상태 관리 (실제 검색에 사용될 검색어)
  const [searchKeyword, setSearchKeyword] = useState('');

  // API 호출을 위한 파라미터
  const searchParams: GetMDPackageDetailRequest = useMemo(
    () => ({
      page,
      countPerPage: size,
      datasetCd: id || '',
      //searchWord: searchKeyword,
      ...(searchType === 'fileNm' ? { searchWord: searchKeyword } : { uuid: searchKeyword }),
    }),
    [page, size, id, searchKeyword]
  );

  // API 호출
  const { data: detailData, isLoading } = useGetMDPackageDetail(searchParams, {
    // 이전 데이터를 유지하여 로딩 중에도 빈 화면이 보이지 않도록 함
    placeholderData: previousData => previousData,
  });

  // 패키지 기본 정보 (전달받은 state 정보만 사용)
  const packageData = useMemo(() => {
    // location.state에서 전달받은 패키지 정보가 있으면 사용
    const statePackageInfo = location.state?.packageInfo;
    if (statePackageInfo) {
      return {
        name: statePackageInfo.name || '',
        summary: statePackageInfo.summary || '',
        sourceSystem: statePackageInfo.sourceSystem || '',
      };
    }

    // state가 없으면 빈 값으로 설정
    return {
      name: '',
      summary: '',
      sourceSystem: '',
    };
  }, [location.state]);

  // API에서 받은 데이터를 그리드용으로 변환
  const rowData = useMemo(() => {
    if (!detailData?.content) return [];

    return detailData.content.map((item, idx) => ({
      id: String((page - 1) * size + idx + 1), // 페이지네이션을 고려한 NO 계산
      docUuid: item.docUuid || '',
      docTitle: item.docTitle,
      title: item.originMetadata?.origin_metadata?.title || '',
      attachName: item.originMetadata?.origin_metadata?.attach_nm || '',
      createdDate: item.docCreateDay ? dateUtils.formatDate(item.docCreateDay, 'custom', { pattern: 'yyyy.MM.dd' }) : '',
      modifiedDate: item.docMdfcnDay ? dateUtils.formatDate(item.docMdfcnDay, 'custom', { pattern: 'yyyy.MM.dd' }) : '',
      originMetadata: item.originMetadata || null,
      more: 'more',
    }));
  }, [detailData, page, size]);

  // 그리드 컬럼 정의
  const columnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id' as any,
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
        valueGetter: (params: { node: { rowIndex: number } }) => {
          return (page - 1) * size + params.node.rowIndex + 1;
        },
      },
      {
        headerName: '파일 이름',
        field: 'docTitle' as any,
        width: 312,
        sortable: false,
        cellStyle: {
          paddingLeft: '16px',
        },
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
        field: 'title' as any,
        minWidth: 312,
        flex: 1,
        sortable: false,
        cellStyle: {
          paddingLeft: '16px',
        },
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
        field: 'attachName' as any,
        width: 312,
        sortable: false,
        cellStyle: {
          paddingLeft: '16px',
        },
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
        field: 'docUuid' as any,
        width: 200,
        sortable: false,
        cellStyle: {
          paddingLeft: '16px',
        },
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
        headerName: '생성일',
        field: 'createdDate' as any,
        width: 120,
        sortable: false,
        cellStyle: {
          paddingLeft: '16px',
        },
        valueGetter: (params: any) => {
          const v = params.data?.createdDate;
          if (v === null || v === undefined) return '';
          const s = String(v);
          if (s.trim() === '' || s.toLowerCase() === 'null' || s.toLowerCase() === 'undefined') return '';
          return v;
        },
      },
      {
        headerName: '최종 수정일',
        field: 'modifiedDate' as any,
        width: 120,
        sortable: false,
        valueGetter: (params: any) => {
          const v = params.data?.modifiedDate;
          if (v === null || v === undefined) return '';
          const s = String(v);
          if (s.trim() === '' || s.toLowerCase() === 'null' || s.toLowerCase() === 'undefined') return '';
          return v;
        },
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '메타데이터',
        field: 'modifiedDate' as any,
        width: 120,
        sortable: false,
        cellStyle: {
          paddingLeft: '16px',
        },
        cellRenderer: (params: any) => {
          const originMetadata = params.data?.originMetadata;
          return (
            <UIButton2
              className='btn-text-14-underline-point'
              onClick={() =>
                openModal({
                  title: '메타데이터',
                  type: 'large',
                  body: <MDPackageMetadataPopupPage metadata={originMetadata} />,
                  showFooter: false,
                  backdropClosable: true,
                  trapFocus: true,
                })
              }
            >
              메타데이터
            </UIButton2>
          );
        },
      },
    ],
    [page, size]
  );

  const totalPages = detailData?.totalPages || 1;
  const totalElements = detailData?.totalElements || 0;

  return (
    <>
      <section className='section-page'>
        <UIPageHeader title='데이터 상세 조회' />
        <UIPageBody>
          <UIArticle className='article-filter'>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                데이터 상세
              </UITypography>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  <colgroup>
                    <col style={{ width: '128px' }} />
                    <col style={{ width: '656px' }} />
                    <col style={{ width: '128px' }} />
                    <col style={{ width: '624px' }} />
                  </colgroup>
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          이름
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {packageData.name}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          요약
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {packageData.summary}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          원천 시스템
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {packageData.sourceSystem}
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
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
                          <UIDataCnt count={totalElements} prefix='구성 파일 총' />
                        </div>
                      </div>
                      <div className='flex gap-2flex-shrink-0'>
                        <div style={{ width: '160px', flexShrink: 0 }}>
                          <UIDropdown
                            value={searchType}
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
                            onKeyDown={e => {
                              if (e.key === 'Enter') {
                                setSearchKeyword(searchValue); // 엔터 시 검색어 적용
                                setPage(1); // 검색 시 첫 페이지로 이동
                              }
                            }}
                            placeholder='검색어 입력'
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
                <UIPagination currentPage={page} totalPages={totalPages} onPageChange={setPage} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>
    </>
  );
}
