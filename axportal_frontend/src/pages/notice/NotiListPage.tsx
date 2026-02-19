import React, { useMemo, useState } from 'react';

import { useNavigate } from 'react-router-dom';

import { Button } from '@/components/common/auth';
import { UIBox, UIButton2, UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGroup, UIUnitGroup, UIInput } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { AUTH_KEY } from '@/constants/auth';
import { useGetNotices } from '@/services/notice';
import type { GetNoticesRequest } from '@/services/notice/types';
import { useModal } from '@/stores/common/modal';

export const NotiListPage = () => {
  const { openAlert } = useModal();
  const navigate = useNavigate();

  // 오늘 날짜 계산
  const today = new Date();
  const fromDate = new Date('2025-12-01');

  // 날짜 포맷팅 함수
  const formatDate = (date: Date) => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}.${month}.${day}`;
  };

  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    condition: false,
    type: false,
    pageSize: false,
  });

  // search 타입
  const [searchValue, setSearchValue] = useState('');

  // 각 드롭다운 값 상태
  const [dateTypeValue, setDateTypeValue] = useState('최종 수정일시');
  const [conditionValue, setConditionValue] = useState('제목');
  const [typeValue, setTypeValue] = useState('전체');

  // date 값들
  const [dateValueStart, setDateValueStart] = useState(formatDate(fromDate));
  const [dateValueEnd, setDateValueEnd] = useState(formatDate(today));
  const [pageSize, setPageSize] = useState(12);

  // 페이지네이션 상태
  const [currentPage, setCurrentPage] = useState(1);

  // 실제 검색에 사용될 파라미터 (조회 버튼 클릭 시에만 업데이트)
  const [actualSearchParams, setActualSearchParams] = useState<GetNoticesRequest>({
    page: 1,
    size: 12, // Spring Boot Pageable은 size 파라미터를 사용
    dateFrom: formatDate(fromDate),
    dateTo: formatDate(today),
    condition: '제목' as '전체' | '제목' | '내용',
    noticeType: undefined,
    sort: 'modifiedDate,desc',
  });

  // 현재 UI 조건으로 파라미터 생성 (실제 API 호출에는 사용하지 않음)
  const currentParams: GetNoticesRequest = useMemo(() => {
    const params: GetNoticesRequest = {
      page: currentPage, // 백엔드도 1-based page
      size: pageSize, // Spring Boot Pageable은 size 파라미터를 사용
      dateFrom: dateValueStart,
      dateTo: dateValueEnd,
      condition: conditionValue as '전체' | '제목' | '내용',
      noticeType: typeValue === '전체' ? undefined : (typeValue as '시스템 점검' | '서비스 출시 및 오픈' | '보안 안내' | '이용 가이드' | '버전/기능 업데이트' | '기타'),
      sort: 'modifiedDate,desc',
    };

    // 검색어가 있을 때만 추가
    if (searchValue.trim()) {
      params.searchValue = searchValue.trim();
      params.searchType = conditionValue === '전체' ? 'titleContent' : conditionValue === '제목' ? 'title' : 'content';
    }

    return params;
  }, [currentPage, pageSize, dateValueStart, dateValueEnd, conditionValue, typeValue, searchValue]);

  // API 호출 (실제 검색 파라미터 사용)
  // actualSearchParams가 변경되면 queryKey도 변경되어 React Query가 자동으로 새 쿼리 실행
  const { data: noticeData } = useGetNotices(actualSearchParams);

  // 더보기 메뉴 설정
  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          label: '수정',
          action: 'modify',
          onClick: (rowData: any) => {
            // console.log('수정 클릭:', rowData);
            openAlert({
              title: '안내',
              message: `"${rowData.title}" 수정 팝업을 엽니다.`,
            });
          },
        },
        {
          label: '삭제',
          action: 'delete',
          onClick: (rowData: any) => {
            // console.log('삭제 클릭:', rowData);
            openAlert({
              title: '안내',
              message: `"${rowData.title}" 삭제 화면으로 이동합니다.`,
            });
          },
        },
      ],
    }),
    [openAlert]
  );

  // 표시할 데이터 계산 (실제 API 데이터만 사용)
  const displayData = useMemo(() => {
    return noticeData?.content || [];
  }, [noticeData]);

  // 총 개수 계산
  const totalCount = noticeData?.totalElements || 0;

  // 검색 핸들러
  const handleSearch = () => {
    // 현재 UI 조건을 실제 검색 파라미터로 업데이트
    setActualSearchParams({ ...currentParams, page: 1 }); // 검색 시 첫 페이지로
    setCurrentPage(1); // UI 페이지도 1로 리셋
  };

  // 드롭다운 핸들러
  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    setDropdownStates(prev => ({
      ...Object.keys(prev).reduce((acc, k) => ({ ...acc, [k]: false }), {} as typeof prev),
      [key]: !prev[key],
    }));
  };

  // 그리드 컬럼 정의
  const columnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'rowNumber',
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
        cellRenderer: (params: any) => {
          const rowIndex = params.node.rowIndex;
          const currentPageNum = currentPage;
          return pageSize * (currentPageNum - 1) + rowIndex + 1;
        },
      },
      {
        headerName: '제목',
        field: 'title',
        width: 480,
        cellStyle: {
          paddingLeft: '16px',
        },
        showTooltip: true,
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
        headerName: '유형',
        field: 'type',
        width: 190,
      },
      {
        headerName: '내용',
        field: 'content',
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
        showTooltip: true,
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
        headerName: '최종 수정일시',
        field: 'modifiedDate',
        width: 220,
        cellStyle: {
          paddingLeft: '16px',
        },
        valueGetter: (params: any) => {
          const dateStr = params.data?.modifiedDate;
          if (!dateStr) return '';

          // ISO 8601 형식 (2025-01-15T10:30:45) 또는 기타 형식을 초 단위까지 표시
          try {
            const date = new Date(dateStr);
            const year = date.getFullYear();
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const day = String(date.getDate()).padStart(2, '0');
            const hours = String(date.getHours()).padStart(2, '0');
            const minutes = String(date.getMinutes()).padStart(2, '0');
            const seconds = String(date.getSeconds()).padStart(2, '0');
            return `${year}.${month}.${day} ${hours}:${minutes}:${seconds}`;
          } catch {
            return dateStr;
          }
        },
      },
    ],
    [displayData, totalCount, pageSize, currentPage]
  );

  const handleNoticeClick = () => {
    navigate('/admin/notice-mgmt');
  };

  return (
    <section className='section-page'>
      {/* 페이지 헤더 */}
      <UIPageHeader
        title='공지사항'
        description='포탈 내 게시된 공지사항을 확인할 수 있습니다.'
        actions={
          <>
            <Button
              auth={AUTH_KEY.ADMIN.NOTICE_CREATE}
              className='btn-text-18-semibold-point'
              leftIcon={{ className: 'ic-system-24-outline-blue-setting', children: '' }}
              onClick={handleNoticeClick}
            >
              공지사항 관리
            </Button>
          </>
        }
      />

      {/* 페이지 바디 */}
      <UIPageBody>
        <UIArticle className='article-filter'>
          <UIBox className='box-filter'>
            <UIGroup gap={40} direction='row'>
              <div style={{ width: 'calc(100% - 168px)' }}>
                <table className='tbl_type_b'>
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                          조회 기간
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UIUnitGroup gap={32} direction='row'>
                          <div className='flex-1'>
                            <UIDropdown
                              value={dateTypeValue}
                              placeholder='조회 조건 선택'
                              options={[{ value: '최종 수정일시', label: '최종 수정일시' }]}
                              isOpen={dropdownStates.dateType}
                              onClick={() => handleDropdownToggle('dateType')}
                              onSelect={(value: string) => {
                                setDateTypeValue(value);
                                setDropdownStates(prev => ({ ...prev, dateType: false }));
                              }}
                            />
                          </div>
                          <div className='flex-1'>
                            <UIUnitGroup gap={4} direction='row' vAlign='center'>
                              <div className='flex-1'>
                                <UIInput.Date
                                  value={dateValueStart}
                                  onChange={e => {
                                    const newStartDate = e.target.value;
                                    setDateValueStart(newStartDate);
                                    // 시작일이 종료일보다 큰 경우 종료일을 시작일과 같게 변경
                                    if (newStartDate > dateValueEnd) {
                                      setDateValueEnd(newStartDate);
                                    }
                                  }}
                                />
                              </div>
                              <UITypography variant='body-1' className='secondary-neutral-p w-[28px] justify-center text-center'>
                                ~
                              </UITypography>
                              <div className='flex-1'>
                                <UIInput.Date
                                  value={dateValueEnd}
                                  onChange={e => {
                                    const newEndDate = e.target.value;
                                    setDateValueEnd(newEndDate);
                                    // 종료일이 시작일보다 작은 경우 시작일을 종료일과 같게 변경
                                    if (newEndDate < dateValueStart) {
                                      setDateValueStart(newEndDate);
                                    }
                                  }}
                                />
                              </div>
                            </UIUnitGroup>
                          </div>
                        </UIUnitGroup>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                          조회 조건
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UIUnitGroup gap={32} direction='row'>
                          <div className='flex-1'>
                            <UIDropdown
                              value={conditionValue}
                              placeholder='조회 조건 선택'
                              options={[
                                { value: '전체', label: '전체' },
                                { value: '제목', label: '제목' },
                                { value: '내용', label: '내용' },
                              ]}
                              isOpen={dropdownStates.condition}
                              onClick={() => handleDropdownToggle('condition')}
                              onSelect={(value: string) => {
                                setConditionValue(value);
                                setDropdownStates(prev => ({ ...prev, condition: false }));
                              }}
                            />
                          </div>
                          <div className='flex-1'>
                            <UIInput.Search
                              value={searchValue}
                              placeholder='검색어 입력'
                              onChange={e => {
                                setSearchValue(e.target.value);
                              }}
                            />
                          </div>
                        </UIUnitGroup>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                          유형
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UIUnitGroup gap={32} direction='row'>
                          <div className='flex-1'>
                            <UIDropdown
                              value={typeValue}
                              placeholder='조회 조건 선택'
                              options={[
                                { value: '전체', label: '전체' },
                                { value: '시스템 점검', label: '시스템 점검' },
                                { value: '서비스 출시 및 오픈', label: '서비스 출시 및 오픈' },
                                { value: '보안 안내', label: '보안 안내' },
                                { value: '이용 가이드', label: '이용 가이드' },
                                { value: '버전/기능 업데이트', label: '버전/기능 업데이트' },
                                { value: '기타', label: '기타' },
                              ]}
                              isOpen={dropdownStates.type}
                              onClick={() => handleDropdownToggle('type')}
                              onSelect={(value: string) => {
                                setTypeValue(value);
                                setDropdownStates(prev => ({ ...prev, type: false }));
                              }}
                            />
                          </div>
                          <div className='flex-1'></div> {/* < div 삭제하지마세요. 가로 사이즈 맞춤 빈여백 채우기 */}
                        </UIUnitGroup>
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
                    <div className='flex-shrink-0 w-[160px]'>
                      <UIDropdown
                        value={String(pageSize)}
                        options={[
                          { value: '12', label: '12개씩 보기' },
                          { value: '36', label: '36개씩 보기' },
                          { value: '60', label: '60개씩 보기' },
                        ]}
                        isOpen={dropdownStates.pageSize}
                        onClick={() => handleDropdownToggle('pageSize')}
                        onSelect={(value: string) => {
                          const newPageSize = Number(value);
                          setPageSize(newPageSize);
                          setCurrentPage(1);
                          setActualSearchParams(prev => ({
                            ...prev,
                            size: newPageSize,
                            page: 1,
                          }));
                          setDropdownStates(prev => ({ ...prev, pageSize: false }));
                        }}
                        height={40}
                        variant='dataGroup'
                        width='w-40'
                        disabled={displayData.length === 0}
                      />
                    </div>
                  </div>
                </UIUnitGroup>
              </div>
            </UIListContentBox.Header>
            <UIListContentBox.Body>
              <UIGrid
                type='default'
                rowData={displayData}
                columnDefs={columnDefs}
                moreMenuConfig={moreMenuConfig}
                onClickRow={(params: any) => {
                  // console.log('다중 onClickRow', params);
                  if (params.data && params.data.id) {
                    navigate(`/notice/${params.data.id}`);
                  }
                }}
                /* onCheck={(selectedIds: any[]) => {
                  console.log('다중 onSelect', selectedIds);
                }} */
              />
            </UIListContentBox.Body>
            {/* [참고] classname 관련
                  - 그리드 하단 (삭제) 버튼이 있는 경우 classname 지정 (예시) <UIListContentBox.Footer classname="ui-data-has-btn">
                  - 그리드 하단 (버튼) 없는 경우 classname 없이 (예시) <UIListContentBox.Footer>
                */}
            <UIListContentBox.Footer>
              <UIPagination
                currentPage={currentPage}
                totalPages={Math.ceil(totalCount / pageSize) || 1}
                onPageChange={(page: number) => {
                  setCurrentPage(page);
                  // 페이지 변경 시 페이지 번호만 업데이트
                  setActualSearchParams(prev => ({
                    ...prev,
                    page: page,
                  }));
                }}
                className='flex justify-center'
              />
            </UIListContentBox.Footer>
          </UIListContainer>
        </UIArticle>
      </UIPageBody>
    </section>
  );
};
