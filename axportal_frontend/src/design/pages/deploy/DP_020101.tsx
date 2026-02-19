import React, { useMemo, useState } from 'react';

import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIDataCnt, UILabel, UIToggle } from '@/components/UI';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIGrid } from '@/components/UI/molecules/grid/UIGrid/component';
import { UIListContainer } from '@/components/UI/molecules/list/UIListContainer/component';
import { UIListContentBox } from '@/components/UI/molecules/list/UIListContentBox';
import { UIDropdown } from '../../../components/UI/molecules/dropdown/UIDropdown';
import { UIButton2 } from '@/components/UI/atoms';
import { UIGridCard } from '@/components/UI/molecules/card/UIGridCard';
import { DesignLayout } from '../../components/DesignLayout';
import { UIBox, UITypography } from '@/components/UI/atoms';
import { UIInput } from '@/components/UI/molecules/input';
import { UIGroup } from '@/components/UI/molecules';
import { useModal } from '@/stores/common/modal';
import { UICardList } from '@/components/UI/molecules/card/UICardList';

export const DP_020101 = () => {
  const { openAlert } = useModal();
  const [searchValue, setSearchValue] = useState('');

  const handleMoreClick = (_itemId: string) => {
    // 추후 더보기 메뉴 또는 모달 표시 로직 추가
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
              message: `배포 "${rowData.name}" 실행을 시작합니다.`,
            });
          },
        },
        {
          label: '수정',
          action: 'modify',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `배포 "${rowData.name}" 수정 팝업을 엽니다.`,
            });
          },
        },
        {
          label: '복사',
          action: 'copy',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `배포 "${rowData.name}" 복사가 완료되었습니다.`,
            });
          },
        },
        {
          label: '삭제',
          action: 'delete',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `배포 "${rowData.name}" 삭제 화면으로 이동합니다.`,
            });
          },
        },
      ],
      isActive: () => true, // 모든 배포에 대해 활성화
    }),
    [openAlert]
  );

  // rowData 정의
  // [251113_퍼블수정] 그리드컬럼 속성 수정
  const rowData = [
    {
      id: 1,
      name: '고객 상담 에이전트 v2.1',
      builderName: '김철수',
      status: '이용 가능',
      description: '고객 문의에 대한 자동 응답 시스템입니다. 24시간 실시간 고객 지원을 제공하며, 다양한 질문 유형에 대해 정확한 답변을 제공합니다.',
      deployType: '기본',
      version: 'v2.1.3',
      publicRange: '전체 공개',
      release: '배포',
      createdDate: '2024.09.05 14:30',
      modifiedDate: '2024.09.12 09:15',
      more: 'more',
    },
    {
      id: 2,
      name: '문서 요약 에이전트',
      builderName: '이영희',
      status: '진행중',
      description: '긴 문서를 자동으로 요약하여 핵심 내용만을 추출하는 AI 에이전트입니다. PDF, Word, 텍스트 파일을 지원합니다.',
      deployType: '사용자 정의',
      version: 'v1.2.0',
      publicRange: '부서 공개',
      release: '배포',
      createdDate: '2024.09.03 10:22',
      modifiedDate: '2024.09.11 16:45',
      more: 'more',
    },
    {
      id: 3,
      name: '코드 리뷰 어시스턴트',
      builderName: '박민수',
      status: '실패',
      description: '프로그래밍 코드의 품질을 검토하고 개선 사항을 제안하는 에이전트입니다. 다양한 프로그래밍 언어를 지원합니다.',
      deployType: '기본',
      version: 'v0.9.5',
      publicRange: '개인 전용',
      release: '배포',
      createdDate: '2024.09.01 11:15',
      modifiedDate: '2024.09.10 14:20',
      more: 'more',
    },
    {
      id: 4,
      name: '회의록 생성기',
      builderName: '정하나',
      status: '취소',
      description: '음성 회의 내용을 텍스트로 변환하고 주요 안건과 결정사항을 정리하여 회의록을 자동 생성하는 에이전트입니다.',
      deployType: '사용자 정의',
      version: 'v1.0.1',
      publicRange: '팀 공개',
      release: '배포',
      createdDate: '2024.08.28 15:40',
      modifiedDate: '2024.09.08 13:25',
      more: 'more',
    },
    {
      id: 5,
      name: '데이터 분석 도우미',
      builderName: '최준호',
      status: '이용 가능',
      description: 'Excel, CSV 데이터를 분석하여 인사이트를 제공하고 시각화 차트를 생성하는 AI 에이전트입니다.',
      deployType: '기본',
      version: 'v3.2.1',
      publicRange: '전체 공개',
      release: '배포',
      createdDate: '2024.08.25 09:10',
      modifiedDate: '2024.09.12 08:30',
      more: 'more',
    },
    {
      id: 6,
      name: '번역 에이전트',
      builderName: '김소영',
      status: '이용 가능',
      description: '다국어 번역 서비스를 제공하는 에이전트입니다. 한국어, 영어, 일본어, 중국어 등 20개 언어를 지원합니다.',
      deployType: '기본',
      version: 'v2.5.0',
      publicRange: '전체 공개',
      release: '배포',
      createdDate: '2024.08.20 13:55',
      modifiedDate: '2024.09.11 17:10',
      more: 'more',
    },
    {
      id: 7,
      name: '이메일 자동 분류기',
      builderName: '장민석',
      status: '진행중',
      description: '받은 이메일을 자동으로 분류하고 중요도에 따라 우선순위를 설정하는 에이전트입니다.',
      deployType: '사용자 정의',
      version: 'v1.1.2',
      publicRange: '부서 공개',
      release: '배포',
      createdDate: '2024.08.15 16:20',
      modifiedDate: '2024.09.09 11:45',
      more: 'more',
    },
    {
      id: 8,
      name: '프로젝트 일정 관리자',
      builderName: '신유진',
      status: '이용 가능',
      description: '프로젝트의 일정과 마일스톤을 관리하고 팀원들에게 알림을 보내는 스마트 관리 에이전트입니다.',
      deployType: '기본',
      version: 'v1.8.4',
      publicRange: '팀 공개',
      release: '배포',
      createdDate: '2024.08.10 10:30',
      modifiedDate: '2024.09.07 15:55',
      more: 'more',
    },
    {
      id: 9,
      name: '보고서 템플릿 생성기',
      builderName: '조성민',
      status: '실패',
      description: '업무 보고서를 위한 다양한 템플릿을 자동 생성하고 데이터를 입력하여 완성된 보고서를 작성하는 에이전트입니다.',
      deployType: '사용자 정의',
      version: 'v0.7.1',
      publicRange: '개인 전용',
      release: '배포',
      createdDate: '2024.08.05 14:15',
      modifiedDate: '2024.09.06 12:40',
      more: 'more',
    },
    {
      id: 10,
      name: '소셜미디어 관리자',
      builderName: '한지원',
      status: '이용 가능',
      description: '기업의 소셜미디어 계정을 관리하고 콘텐츠를 자동으로 게시하며 반응을 모니터링하는 에이전트입니다.',
      deployType: '기본',
      version: 'v2.0.6',
      publicRange: '전체 공개',
      release: '배포',
      createdDate: '2024.07.30 11:25',
      modifiedDate: '2024.09.05 16:30',
      more: 'more',
    },
    {
      id: 11,
      name: '재고 관리 시스템',
      builderName: '이동현',
      status: '진행중',
      description: '창고의 재고 상황을 실시간으로 모니터링하고 재주문 시점을 알려주는 스마트 재고 관리 에이전트입니다.',
      deployType: '사용자 정의',
      version: 'v1.4.2',
      publicRange: '부서 공개',
      release: '배포',
      createdDate: '2024.07.25 13:10',
      modifiedDate: '2024.09.04 10:20',
      more: 'more',
    },
    {
      id: 12,
      name: '학습 콘텐츠 추천기',
      builderName: '김현정',
      status: '이용 가능',
      description: '사용자의 학습 패턴을 분석하여 개인 맞춤형 교육 콘텐츠를 추천하는 AI 에이전트입니다.',
      deployType: '기본',
      version: 'v3.1.0',
      publicRange: '전체 공개',
      release: '배포',
      createdDate: '2024.07.20 15:45',
      modifiedDate: '2024.09.03 14:15',
      more: 'more',
    },
  ];

  // 그리드 컬럼 정의
  const columnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id',
        width: 56,
        minWidth: 56,
        maxWidth: 56,
        cellClass: 'text-center',
        headerClass: 'text-center',
        cellStyle: {
          textAlign: 'center' as const,
          display: 'flex' as const,
          alignItems: 'center' as const,
          justifyContent: 'center' as const,
        },
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '배포명',
        field: 'name',
        width: 272,
        minWidth: 272,
        maxWidth: 272,
        suppressSizeToFit: true,
      },
      {
        headerName: '빌더명',
        field: 'builderName',
        width: 272,
      },
      {
        headerName: '상태',
        field: 'status',
        width: 120,
        cellRenderer: React.memo((params: any) => {
          const getStatusIntent = (status: string) => {
            switch (status) {
              case '이용 가능':
                return 'complete';
              case '진행중':
                return 'progress';
              case '실패':
                return 'error';
              case '취소':
                return 'stop';
              default:
                return 'complete';
            }
          };
          return (
            <UILabel variant='badge' intent={getStatusIntent(params.value)}>
              {params.value}
            </UILabel>
          );
        }),
      },
      {
        headerName: '설명',
        field: 'description',
        minWidth: 392,
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
      // [251113_퍼블수정] 그리드컬럼 속성 수정 S
      {
        headerName: '배포 유형',
        field: 'deployType',
        width: 120,
      },
      // [251113_퍼블수정] 그리드컬럼 속성 수정 E
      {
        headerName: '버전',
        field: 'version',
        width: 120,
      },
      {
        headerName: '운영 배포 여부',
        field: 'release' as any,
        width: 120,
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
        cellStyle: {
          paddingLeft: 16,
        },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate',
        width: 180,
        cellStyle: {
          paddingLeft: 16,
        },
      },
      {
        headerName: '',
        field: 'more', // 더보기 컬럼 필드명 (고정)
        width: 56,
      },
    ],
    [handleMoreClick]
  );
  const [value, setValue] = useState('12개씩 보기');
  const [view, setView] = useState('grid');

  // 그리드(카드형)

  const rowCardData = [
    {
      no: 1,
      id: '1',
      modelName: '콜센터 응대 특화 모델',
      description: '고객 상담 응대 문장 생성에 최적화된 대형 언어모델',
      deployType: '이용가능',
      version: 'ver. 6',
      deployStatus: '배포',
      permission: 'Public',
      createdDate: '2024-01-15 09:30:00',
      modifiedDate: '2024-01-20 14:25:00',
      // more: 'more',
      isActive: true,
    },
    {
      no: 2,
      id: '2',
      modelName: '콜센터 응대 특화 모델',
      description: '고객 상담 응대 문장 생성에 최적화된 대형 언어모델',
      deployType: '이용가능',
      version: 'ver. 6',
      deployStatus: '배포',
      permission: 'Private',
      createdDate: '2024-01-10 11:15:00',
      modifiedDate: '2024-01-18 16:40:00',
      more: 'more',
      isActive: false,
    },
    {
      no: 3,
      id: '3',
      modelName: '콜센터 응대 특화 모델',
      description: '고객 상담 응대 문장 생성에 최적화된 대형 언어모델',
      deployType: '진행중',
      version: 'ver. 6',
      deployStatus: '배포',
      permission: 'Public',
      createdDate: '2024-01-05 13:45:00',
      modifiedDate: '2024-01-22 10:20:00',
      more: 'more',
      isActive: true,
    },
    {
      no: 4,
      id: '4',
      modelName: '콜센터 응대 특화 모델',
      description: '고객 상담 응대 문장 생성에 최적화된 대형 언어모델',
      deployType: '실패',
      version: 'ver. 6',
      deployStatus: '배포',
      permission: 'Internal',
      createdDate: '2024-01-12 08:30:00',
      modifiedDate: '2024-01-19 15:50:00',
      more: 'more',
      isActive: false,
    },
    {
      no: 5,
      id: '5',
      modelName: '콜센터 응대 특화 모델',
      description: '고객 상담 응대 문장 생성에 최적화된 대형 언어모델',
      deployType: '이용가능',
      version: 'ver. 6',
      deployStatus: '배포',
      permission: 'Public',
      createdDate: '2024-01-08 16:20:00',
      modifiedDate: '2024-01-21 12:35:00',
      more: 'more',
      isActive: true,
    },
  ];

  return (
    <DesignLayout>
      {/* 섹션 페이지 */}
      <section className='section-page'>
        <UIPageHeader
          title='에이전트 배포'
          // [251118_퍼블수정] : 텍스트 줄바꿈시 , (콤마) 로 처리하여 수정했습니다. (참고 : UIPageHeader 컴포넌트는 직접 수정후 main 머지 했습니다.)
          description={['배포한 에이전트의 정보를 확인하고 관리할 수 있습니다.', '배포 에이전트를 선택하여 간단한 사용방법과 시스템 로그를 확인해보세요.']}
        />
        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle className='article-filter'>
            <UIBox className='box-filter'>
              <UIGroup gap={40} direction='row'>
                {/* 테이블 th = 80px 일 경우  */}
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
                              placeholder='검색어 입력'
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
            <UIListContentBox.Header>
              <div className='flex-shrink-0'>
                <UIGroup gap={8} direction='row' align='start'>
                  <div style={{ width: '168px', paddingRight: '8px' }}>
                    <UIDataCnt count={99} prefix='총' unit='건' />
                  </div>
                </UIGroup>
              </div>
              <div className='flex items-center gap-2'>
                <div style={{ width: '160px', flexShrink: 0 }}>
                  <UIDropdown
                    value={String(value)}
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
                <UIToggle variant='dataView' checked={view === 'card'} onChange={checked => setView(checked ? 'card' : 'grid')} />
              </div>
            </UIListContentBox.Header>
            {/* 다중 선택 그리드 */}
            <UIListContainer>
              <UIListContentBox.Body>
                {view === 'grid' ? (
                  <UIGrid
                    type='multi-select'
                    rowData={rowData}
                    columnDefs={columnDefs}
                    moreMenuConfig={moreMenuConfig}
                    onClickRow={(_params: any) => {}}
                    onCheck={(_selectedIds: any[]) => {}}
                  />
                ) : (
                  <UICardList
                    rowData={rowCardData}
                    flexType='none'
                    card={(item: any) => {
                      const getStatusIntent = (status: string) => {
                        switch (status) {
                          case '이용 가능':
                            return 'complete';
                          case '진행중':
                            return 'progress';
                          case '실패':
                            return 'error';
                          default:
                            return 'complete';
                        }
                      };
                      return (
                        <UIGridCard
                          id={item.id}
                          title={item.modelName}
                          caption={item.description}
                          data={item}
                          moreMenuConfig={moreMenuConfig}
                          statusArea={
                            <UILabel variant='badge' intent={getStatusIntent(item.deployType)}>
                              {item.deployType}
                            </UILabel>
                          }
                          checkbox={{
                            checked: false,
                            onChange: (_checked: boolean, _value: string) => {},
                          }}
                          rows={[
                            { label: '버전', value: item.version },
                            { label: '운영배포 여부', value: item.deployStatus },
                            { label: '생성일시', value: item.createdDate },
                            { label: '최종수정일시', value: item.modifiedDate },
                          ]}
                        />
                      );
                    }}
                  />
                )}
              </UIListContentBox.Body>
              <UIListContentBox.Footer className='ui-data-has-btn'>
                <UIButton2 className='btn-option-outlined' style={{ width: '40px' }}>
                  삭제
                </UIButton2>
                <UIPagination currentPage={1} totalPages={3} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
        {/* <UIPageFooter></UIPageFooter> */}
      </section>
    </DesignLayout>
  );
};
