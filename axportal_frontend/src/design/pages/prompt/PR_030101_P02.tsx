import React, { useMemo, useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIInput } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';

import { UIGrid } from '../../../components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '../../../components/UI/molecules/list';
import { UITextLabel, UITypography } from '@/components/UI/atoms';
import { UIDropdown } from '../../../components/UI/molecules/dropdown/UIDropdown';
import { useModal } from '@/stores/common/modal';

export const PR_030101_P02: React.FC = () => {
  const [searchValue, setSearchValue] = useState('');
  const { openAlert } = useModal();

  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    condition: false,
    menu: false,
    pageSize: false,
  });

  // 각 드롭다운 값 상태
  // 251128_퍼블수정 속성값 수정
  const [searchTypeValue, setSearchTypeValue] = useState('전체');

  // 드롭다운 핸들러
  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    setDropdownStates(prev => ({
      ...Object.keys(prev).reduce((acc, k) => ({ ...acc, [k]: false }), {} as typeof prev),
      [key]: !prev[key],
    }));
  };

  // 더보기 메뉴 설정
  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          label: '실행',
          action: 'run',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `테스트 "${rowData.name}" 실행을 시작합니다.`,
            });
          },
        },
        {
          label: '수정',
          action: 'modify',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `테스트 "${rowData.name}" 수정 팝업을 엽니다.`,
            });
          },
        },
        {
          label: '복사',
          action: 'copy',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `테스트 "${rowData.name}" 복사가 완료되었습니다.`,
            });
          },
        },
        {
          label: '삭제',
          action: 'delete',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `테스트 "${rowData.name}" 삭제 화면으로 이동합니다.`,
            });
          },
        },
      ],
      isActive: () => true, // 모든 테스트에 대해 활성화
    }),
    []
  );

  // 데이터
  // 251110_퍼블수정 그리드 컬럼 속성 '생성일시, 최종수정일시' 영역 추가
  const projectData = [
    {
      id: '1',
      no: 1,
      name: '예적금 상품 Q&A 세트',
      tags: ['가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자'],
      createdDate: '2025.03.24 17:42:11',
      modifiedDate: '2025.03.24 17:42:11',
    },
    {
      id: '2',
      no: 2,
      name: '모바일뱅킹 이용 가이드',
      tags: ['가나다라마바사아', '가나다라마바사아'],
      createdDate: '2025.03.24 17:42:11',
      modifiedDate: '2025.03.24 17:42:11',
    },
    {
      id: '3',
      no: 3,
      name: 'ATM/창구 업무 안내 문서',
      tags: ['language', 'serverless'],
      createdDate: '2025.03.24 17:42:11',
      modifiedDate: '2025.03.24 17:42:11',
    },
    {
      id: '4',
      no: 4,
      name: '외화 송금 및 환율 상담 로그',
      tags: ['language', 'self-hosting'],
      createdDate: '2025.03.24 17:42:11',
      modifiedDate: '2025.03.24 17:42:11',
    },
    {
      id: '5',
      no: 5,
      name: '상품 비교형 답변 데이터',
      tags: ['language', 'serverless'],
      createdDate: '2025.03.24 17:42:11',
      modifiedDate: '2025.03.24 17:42:11',
    },
    {
      id: '6',
      no: 6,
      name: '상품 비교형 답변 데이터',
      tags: ['language', 'serverless'],
      createdDate: '2025.03.24 17:42:11',
      modifiedDate: '2025.03.24 17:42:11',
    },
    // [251111_퍼블수정] : 그리드 6개 노출 수정
  ];

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
      // 251113_퍼블수정 그리드 컬럼 속성 '이름' 영역 수정 S
      {
        headerName: '이름',
        field: 'name' as const,
        flex: 1,
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
      // 251113_퍼블수정 그리드 컬럼 속성 '이름' 영역 수정 E
      // 251128_퍼블수정 그리드 컬럼 속성 '태그' 영역 수정 S
      {
        headerName: '태그',
        field: 'tags' as const,
        sortable: false,
        width: 230,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: (params: any) => {
          if (!params.value || !Array.isArray(params.value) || params.value.length === 0) {
            return null;
          }
          const tagText = params.value.join(', ');
          return (
            <div title={tagText}>
              <div className='flex gap-1'>
                {params.value.slice(0, 2).map((tag: string, index: number) => (
                  <UITextLabel key={index} intent='tag' className='nowrap'>
                    {tag}
                  </UITextLabel>
                ))}
              </div>
            </div>
          );
        },
      },
      // 251128_퍼블수정 그리드 컬럼 속성 '태그' 영역 수정 E
      // 251110_퍼블수정 그리드 컬럼 속성 '생성일시, 최종수정일시' 영역 추가 S
      {
        headerName: '생성일시',
        field: 'createdDate',
        width: 180,
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      // 251110_퍼블수정 그리드 컬럼 속성 '생성일시, 최종수정일시' 영역 추가 E
    ],
    []
  );

  return (
    <section className='section-modal'>
      <UIArticle className='article-grid'>
        <UIListContainer>
          <UIListContentBox.Header>
            <div className='flex items-center w-full mb-2'>
              <div className='flex-shrink-0'>
                <div style={{ width: '102px', paddingRight: '12px' }}>
                  <UIDataCnt count={projectData.length} prefix='총' unit='건' />
                </div>
              </div>
              <div className='flex gap-2 items-center'>
                <UITypography variant='body-1' className='secondary-neutral-900'>
                  태그
                </UITypography>
                {/* 251128_퍼블수정 속성값 수정 */}
                <div className='w-[180px]'>
                  <UIDropdown
                    value={searchTypeValue}
                    placeholder='조회 조건 선택'
                    options={[
                      { value: '전체', label: '전체' },
                      { value: '이름', label: '이름' },
                      { value: '아이디', label: '아이디' },
                      { value: '이메일', label: '이메일' },
                      { value: '부서', label: '부서' },
                    ]}
                    isOpen={dropdownStates.searchType}
                    height={40}
                    onClick={() => handleDropdownToggle('searchType')}
                    onSelect={(value: string) => {
                      setSearchTypeValue(value);
                      setDropdownStates(prev => ({ ...prev, searchType: false }));
                    }}
                  />
                </div>
              </div>
              <div className='w-[360px] h-[40px] ml-auto'>
                <UIInput.Search
                  value={searchValue}
                  placeholder='이름 입력'
                  onChange={e => {
                    setSearchValue(e.target.value);
                  }}
                />
              </div>
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            <UIGrid
              type='single-select'
              rowData={projectData}
              columnDefs={columnDefs}
              moreMenuConfig={moreMenuConfig}
              onClickRow={(_params: any) => {}}
              onCheck={(_selectedIds: any[]) => {}}
            />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination currentPage={1} totalPages={3} onPageChange={() => {}} className='flex justify-center mt-5' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </section>
  );
};
