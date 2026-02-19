import React, { useMemo, useState } from 'react';

import { UIBox, UIButton2, UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGroup, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { useModal } from '@/stores/common/modal';
import { UIDropdown } from '../../../components/UI/molecules/dropdown/UIDropdown';
import { DesignLayout } from '../../components/DesignLayout';
import { UIInput } from '@/components/UI/molecules/input';

export const DP_040101 = () => {
  const [value, setValue] = useState('12개씩 보기');
  const { openAlert } = useModal();

  // search 타입
  const [searchValue, setSearchValue] = useState('');

  // 더보기 메뉴 설정
  const moreMenuConfig = useMemo(
    () => ({
      items: [
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

  // 샘플 데이터 (rowData로 변수명 변경)
  const rowData = [
    {
      id: '1',
      category: '부적절한 언어',
      bannedWord: ' 불법낙태, 불법시술, 마약처방, 불법의약품, 자살약, 다이어트약, 도핑, 스테로이드, 불법백신, 불법수술',
      scope: '전체공유',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
      more: 'more',
    },
    {
      id: '2',
      category: '개인정보',
      bannedWord: ' 불법낙태, 불법시술, 마약처방, 불법의약품, 자살약, 다이어트약, 도핑, 스테로이드, 불법백신, 불법수술',
      scope: '전체공유',
      createdDate: '2025.03.23 14:15:32',
      modifiedDate: '2025.03.24 10:30:15',
      more: 'more',
    },
    {
      id: '3',
      category: '금융정보',
      bannedWord: ' 불법낙태, 불법시술, 마약처방, 불법의약품, 자살약, 다이어트약, 도핑, 스테로이드, 불법백신, 불법수술',
      scope: '전체공유',
      createdDate: '2025.03.22 09:45:21',
      modifiedDate: '2025.03.23 16:20:30',
      more: 'more',
    },
    {
      id: '4',
      category: '보안정보',
      bannedWord: ' 불법낙태, 불법시술, 마약처방, 불법의약품, 자살약, 다이어트약, 도핑, 스테로이드, 불법백신, 불법수술',
      scope: '전체공유',
      createdDate: '2025.03.24 16:30:15',
      modifiedDate: '2025.03.24 16:30:15',
      more: 'more',
    },
    {
      id: '5',
      category: '불법행위',
      bannedWord: ' 불법낙태, 불법시술, 마약처방, 불법의약품, 자살약, 다이어트약, 도핑, 스테로이드, 불법백신, 불법수술',
      scope: '전체공유',
      createdDate: '2025.03.20 11:20:43',
      modifiedDate: '2025.03.22 14:45:20',
      more: 'more',
    },
    {
      id: '6',
      category: '스팸성 내용',
      bannedWord: ' 불법낙태, 불법시술, 마약처방, 불법의약품, 자살약, 다이어트약, 도핑, 스테로이드, 불법백신, 불법수술',
      scope: '전체공유',
      createdDate: '2025.03.24 13:55:28',
      modifiedDate: '2025.03.24 13:55:28',
      more: 'more',
    },
    {
      id: '7',
      category: '폭력적 내용',
      bannedWord: ' 불법낙태, 불법시술, 마약처방, 불법의약품, 자살약, 다이어트약, 도핑, 스테로이드, 불법백신, 불법수술',
      scope: '전체공유',
      createdDate: '2025.03.24 17:42:11',
      modifiedDate: '2025.03.24 17:42:11',
      more: 'more',
    },
  ];

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
      },
      {
        headerName: '분류',
        field: 'category' as any,
        width: 272,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '금지어',
        field: 'bannedWord' as any,
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
        // [251110_퍼블수정] 컬럼속성 말줄임 추가
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
        headerName: '공개범위',
        field: 'scope' as any,
        width: 120,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '생성일시',
        field: 'createdDate' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate' as any,
        width: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '',
        field: 'more', // 더보기 컬럼 필드명 (고정)
        width: 56,
      },
    ],
    [rowData]
  );

  return (
    <DesignLayout
      initialMenu={{ id: 'data', label: '데이터' }}
      initialSubMenu={{
        id: 'data-catalog',
        label: '지식/학습 데이터 관리', // [251111_퍼블수정] 타이틀명칭 변경 : 데이터 카탈로그 > 지식/학습 데이터 관리
        icon: 'ico-lnb-menu-20-data-catalog',
      }}
    >
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='세이프티 필터'
          description={['서비스 응답의 품질과 안정성을 보장하기 위해 금지어를 설정하고 관리합니다.', '세이프티 필터 생성 버튼을 클릭해 생성형 AI의 안정성을 높여 보세요.']}
          actions={
            <>
              {/* [251222_퍼블수정]: btn-text-14-semibold-point > btn-text-18-semibold-point  (클래스명 변경 : 폰트 크기 수정함) */}
              <UIButton2 className='btn-text-18-semibold-point' leftIcon={{ className: 'ic-system-24-add', children: '' }}>
                세이프티 필터 생성
              </UIButton2>
            </>
          }
        />

        {/* 페이지 바디 */}
        <UIPageBody>
          {/* [251105_퍼블수정] 검색영역 수정 */}
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
                        <td colSpan={3}>
                          <div className='flex-1'>
                            <UIInput.Search
                              value={searchValue}
                              placeholder='분류, 금지어 입력'
                              onChange={e => {
                                setSearchValue(e.target.value);
                              }}
                            />
                          </div>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
                <div style={{ width: '128px' }}>
                  <UIButton2 className='btn-secondary-blue' style={{ width: '100%' }}>
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
                          <UIDataCnt count={rowData.length} prefix='총' unit='건' />
                        </div>
                      </div>
                      <div className='flex' style={{ gap: '12px' }}>
                        <div style={{ width: '160px', flexShrink: 0 }}>
                          <UIDropdown
                            value={String(value)}
                            disabled={true}
                            options={[
                              { value: '1', label: '12개씩 보기' },
                              { value: '2', label: '36개씩 보기' },
                              { value: '3', label: '60개씩 보기' },
                            ]}
                            onSelect={(value: string) => {
                              setValue(value);
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
                <UIGrid
                  type='multi-select'
                  rowData={rowData}
                  columnDefs={columnDefs}
                  moreMenuConfig={moreMenuConfig}
                  onClickRow={(_params: any) => {}}
                  onCheck={(_selectedIds: any[]) => {}}
                />
              </UIListContentBox.Body>
              {/* [참고] classname 관련
                  - 그리드 하단 (삭제) 버튼이 있는 경우 classname 지정 (예시) <UIListContentBox.Footer classname="ui-data-has-btn">
                  - 그리드 하단 (버튼) 없는 경우 classname 없이 (예시) <UIListContentBox.Footer>
                */}
              <UIListContentBox.Footer className='ui-data-has-btn'>
                <UIButton2 className='btn-option-outlined' style={{ width: '40px' }}>
                  삭제
                </UIButton2>
                <UIPagination currentPage={1} totalPages={10} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
