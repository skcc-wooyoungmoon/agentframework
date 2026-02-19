import React, { useMemo, useState } from 'react';

import { UITabs } from '../../../components/UI/organisms';
import { DesignLayout } from '../../components/DesignLayout';
import { UIGroup, UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIBox, UIButton2, UITypography, UIDataCnt } from '@/components/UI/atoms';
import { UIDropdown } from '../../../components/UI/molecules/dropdown/UIDropdown';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { useModal } from '@/stores/common/modal';

export const PR_030101 = () => {
  const [value, setValue] = useState('12개씩 보기');
  const { openAlert } = useModal();
  const [activeTab, setActiveTab] = useState('Tab2');

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
      guardrailName: '부적절한 언어 필터',
      description: '질병명, 진단명, 치료 방법 등 개인의 건강 상태와 관련된 민감한 의학적 정보를 포함한 응답은 생성하지 않으며, 건강 관련 전문적 판단이나 진단을 대신',
      publicRange: '전체공유',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.25 10:15:20',
      more: 'more',
    },
    {
      id: '2',
      guardrailName: '개인정보 보호',
      description: '주민등록번호, 신용카드번호 등 개인정보 노출을 방지합니다',
      publicRange: '전체공유',
      createdDate: '2025.03.23 14:15:32',
      modifiedDate: '2025.03.24 09:30:45',
      more: 'more',
    },
    {
      id: '3',
      guardrailName: '금융 규정 준수',
      description: '금융 관련 법규 및 규정에 맞는 응답만 생성하도록 제한합니다',
      publicRange: '전체공유',
      createdDate: '2025.03.22 09:45:21',
      modifiedDate: '2025.03.23 16:20:10',
      more: 'more',
    },
    {
      id: '4',
      guardrailName: '사실 정확성 검증',
      description: '부정확하거나 허위 정보가 포함된 응답을 차단합니다',
      publicRange: '전체공유',
      createdDate: '2025.03.24 16:30:15',
      modifiedDate: '2025.03.25 11:45:30',
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
      // 251113_퍼블수정 그리드 컬럼 속성 '이름' 영역 수정 S
      {
        headerName: '이름',
        field: 'guardrailName',
        width: 272,
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
      {
        headerName: '설명',
        field: 'description',
        minWidth: 632,
        flex: 1,
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
        headerName: '공개범위',
        field: 'publicRange',
        width: 120,
      },
      {
        headerName: '생성일시',
        field: 'createdDate',
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate',
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

  // 탭 옵션 정의
  const tabOptions = [
    { id: 'Tab1', label: '가드레일 프롬프트 관리' },
    { id: 'Tab2', label: '가드레일 관리' },
  ];

  return (
    <DesignLayout
      initialMenu={{ id: 'agent', label: '에이전트' }}
      initialSubMenu={{
        id: 'agent-tools',
        label: '에이전트의 도구',
        icon: 'ico-lnb-menu-20-agent-tools',
      }}
    >
      {/* 섹션 페이지 */}
      <section className='section-page'>
        <UIPageHeader title='가드레일' description='서비스 응답의 품질과 안전성을 보장하기 위해 가드레일을 설정하고 관리합니다.' />

        {/* 페이지 바디 */}
        <UIPageBody>
          {/* 탭 영역 */}
          <UIArticle className='article-tabs'>
            <div className='flex'>
              <UITabs items={tabOptions} activeId={activeTab} onChange={setActiveTab} size='large' />
            </div>
          </UIArticle>
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
                              placeholder='이름, 설명 입력'
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
                <UIUnitGroup gap={16} direction='column'>
                  <div className='flex justify-between w-full items-center'>
                    <div className='flex-shrink-0'>
                      <div style={{ width: '168px', paddingRight: '8px' }}>
                        <UIDataCnt count={rowData.length} prefix='총' unit='건' />
                      </div>
                    </div>
                    <div className='flex items-center gap-2'>
                      <UIButton2 className='btn-tertiary-outline' onClick={() => {}}>
                        가드레일 생성
                      </UIButton2>
                      <div style={{ width: '160px', flexShrink: 0 }}>
                        <UIDropdown
                          value={String(value)}
                          options={[
                            { value: '1', label: '12개씩 보기' },
                            { value: '2', label: '36개씩 보기' },
                            { value: '3', label: '60개씩 보기' },
                          ]}
                          onSelect={(value: string) => {setValue(value);
                          }}
                          onClick={() => {}}
                          height={40}
                          variant='dataGroup'
                        />
                      </div>
                    </div>
                  </div>
                </UIUnitGroup>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid
                  type='multi-select'
                  rowData={rowData}
                  columnDefs={columnDefs}
                  moreMenuConfig={moreMenuConfig}
                  onClickRow={(_params: any) => {}}
                  onCheck={(_selectedIds: any[]) => {
                  }}
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
