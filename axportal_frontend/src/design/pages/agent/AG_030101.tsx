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
import { UIGroup, UIPageFooter } from '@/components/UI/molecules';
import { UITextLabel } from '@/components/UI/atoms/UITextLabel';
import { UICardList } from '@/components/UI/molecules/card/UICardList';

export const AG_030101 = () => {
  const [searchValue, setSearchValue] = useState('');

  const [selectedCardIds, setSelectedCardIds] = useState<string[]>([]);

  const handleMoreClick = (_itemId: string) => {
    // 추후 더보기 메뉴 또는 모달 표시 로직 추가
  };

  // rowData 정의
  const rowData = [
    {
      id: 1,
      deployName: '고객 상담 에이전트 v2.1',
      serverName: '김철수',
      status: '이용 가능',
      description: '계약서, 약관 등 금융문서 요약 전용',
      catogory: 'basic',
      tags: ['가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자'],
      connectedAgent: '전체공유',
      createdDate: '2024.09.05 14:30',
      modifiedDate: '2024.09.12 09:15',
    },
    {
      id: 2,
      deployName: '문서 요약 에이전트',
      serverName: '이영희',
      status: '진행중',
      description: '계약서, 약관 등 금융문서 요약 전용',
      catogory: 'basic',
      tags: ['가나다라마바사아', '가나다라마바사아'],
      connectedAgent: '전체공유',
      createdDate: '2024.09.03 10:22',
      modifiedDate: '2024.09.11 16:45',
    },
    {
      id: 3,
      deployName: '코드 리뷰 어시스턴트',
      serverName: '박민수',
      status: '실패',
      description: '계약서, 약관 등 금융문서 요약 전용',
      catogory: 'basic',
      tags: ['문서요약'],
      connectedAgent: '전체공유',
      createdDate: '2024.09.01 11:15',
      modifiedDate: '2024.09.10 14:20',
    },
    {
      id: 4,
      deployName: '회의록 생성기',
      serverName: '정하나',
      status: '진행중',
      description: '계약서, 약관 등 금융문서 요약 전용',
      catogory: 'basic',
      tags: ['문서요약'],
      connectedAgent: '전체공유',
      createdDate: '2024.08.28 15:40',
      modifiedDate: '2024.09.08 13:25',
    },
    {
      id: 5,
      deployName: '데이터 분석 도우미',
      serverName: '최준호',
      status: '이용 가능',
      description: '계약서, 약관 등 금융문서 요약 전용',
      catogory: 'basic',
      tags: ['문서요약'],
      connectedAgent: '전체공유',
      createdDate: '2024.08.25 09:10',
      modifiedDate: '2024.09.12 08:30',
    },
    {
      id: 6,
      deployName: '번역 에이전트',
      serverName: '김소영',
      status: '이용 가능',
      description: '계약서, 약관 등 금융문서 요약 전용',
      catogory: 'basic',
      tags: ['문서요약'],
      connectedAgent: '전체공유',
      createdDate: '2024.08.20 13:55',
      modifiedDate: '2024.09.11 17:10',
    },
    {
      id: 7,
      deployName: '이메일 자동 분류기',
      serverName: '장민석',
      status: '진행중',
      description: '계약서, 약관 등 금융문서 요약 전용',
      catogory: 'basic',
      tags: ['문서요약'],
      connectedAgent: '전체공유',
      createdDate: '2024.08.15 16:20',
      modifiedDate: '2024.09.09 11:45',
    },
    {
      id: 8,
      deployName: '프로젝트 일정 관리자',
      serverName: '신유진',
      status: '이용 가능',
      description: '계약서, 약관 등 금융문서 요약 전용',
      catogory: 'basic',
      tags: ['문서요약'],
      connectedAgent: '전체공유',
      createdDate: '2024.08.10 10:30',
      modifiedDate: '2024.09.07 15:55',
    },
    {
      id: 9,
      deployName: '보고서 템플릿 생성기',
      serverName: '조성민',
      status: '실패',
      description: '계약서, 약관 등 금융문서 요약 전용',
      catogory: 'basic',
      tags: ['문서요약'],
      connectedAgent: '전체공유',
      createdDate: '2024.08.05 14:15',
      modifiedDate: '2024.09.06 12:40',
    },
    {
      id: 10,
      deployName: '소셜미디어 관리자',
      serverName: '한지원',
      status: '이용 가능',
      description: '계약서, 약관 등 금융문서 요약 전용',
      catogory: 'basic',
      tags: ['문서요약'],
      connectedAgent: '전체공유',
      createdDate: '2024.07.30 11:25',
      modifiedDate: '2024.09.05 16:30',
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
        headerName: '서버명',
        field: 'serverName',
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
        minWidth: 470,
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
        headerName: '인증유형',
        field: 'catogory',
        width: 120,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      // 251107_퍼블수정 그리드 컬럼 속성 '태그' 영역 수정 S
      {
        headerName: '태그',
        field: 'tags' as const,
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
      // 251107_퍼블수정 그리드 컬럼 속성 '태그' 영역 수정 E
      {
        headerName: '공개범위',
        field: 'connectedAgent',
        width: 120,
        cellStyle: {
          paddingLeft: '16px',
        },
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
    ],
    [handleMoreClick]
  );

  // 그리드(카드형)

  const rowCardData = [
    {
      no: 1,
      id: '1',
      modelName: '신한 MCP 서버 1',
      description:
        'OpenAI의 최신 대화형 AI 모델입니다. 자연어 이해와 생성 능력이 뛰어납니다.OpenAI의 최신 대화형 AI 모델입니다. 자연어 이해와 생성 능력이 뛰어납니다.OpenAI의 최신 대화형 AI 모델입니다. 자연어 이해와 생성 능력이 뛰어납니다.OpenAI의 최신 대화형 AI 모델입니다. 자연어 이해와 생성 능력이 뛰어납니다.OpenAI의 최신 대화형 AI 모델입니다. 자연어 이해와 생성 능력이 뛰어납니다.OpenAI의 최신 대화형 AI 모델입니다. 자연어 이해와 생성 능력이 뛰어납니다.',
      deployType: '이용가능',
      tagName: 'LLM',
      permission: 'Public',
      createdDate: '2024-01-15 09:30:00',
      modifiedDate: '2024-01-20 14:25:00',
      // more: 'more',
      isActive: true,
    },
    {
      no: 2,
      id: '2',
      modelName: '신한 MCP 서버 1',
      description: 'Anthropic의 고성능 AI 모델로, 창의적 글쓰기와 분석에 특화되어 있습니다.',
      deployType: '이용가능',
      tagName: 'LLM',
      permission: 'Private',
      createdDate: '2024-01-10 11:15:00',
      modifiedDate: '2024-01-18 16:40:00',
      more: 'more',
      isActive: false,
    },
    {
      no: 3,
      id: '3',
      modelName: '신한 MCP 서버 1',
      description: '텍스트를 이미지로 변환하는 AI 모델입니다. 고품질 이미지 생성을 제공합니다.',
      deployType: '진행중',
      tagName: 'Image Generation',
      permission: 'Public',
      createdDate: '2024-01-05 13:45:00',
      modifiedDate: '2024-01-22 10:20:00',
      more: 'more',
      isActive: true,
    },
    {
      no: 4,
      id: '4',
      modelName: '신한 MCP 서버 1',
      description: '음성을 텍스트로 변환하는 음성 인식 모델입니다. 다국어를 지원합니다.',
      deployType: '실패',
      tagName: 'Speech Recognition',
      permission: 'Internal',
      createdDate: '2024-01-12 08:30:00',
      modifiedDate: '2024-01-19 15:50:00',
      more: 'more',
      isActive: false,
    },
    {
      no: 5,
      id: '5',
      modelName: '신한 MCP 서버 1',
      description: 'Google의 양방향 트랜스포머 모델로, 자연어 처리 작업에 널리 사용됩니다.',
      deployType: '이용가능',
      tagName: 'NLP',
      permission: 'Public',
      createdDate: '2024-01-08 16:20:00',
      modifiedDate: '2024-01-21 12:35:00',
      more: 'more',
      isActive: true,
    },
  ];

  const [value, setValue] = useState('12개씩 보기');
  const [view, setView] = useState('grid');

  return (
    <DesignLayout>
      {/* 섹션 페이지 */}
      <section className='section-page'>
        <UIPageHeader
          title='MCP 서버'
          description={['MCP 서버를 등록하고 관리할 수 있습니다.', '다양한 업무팀에서 개발한 MCP 서버를 등록하고 빌더에서 사용해 보세요.']}
          actions={
            <>
              {/* [251222_퍼블수정]: btn-text-14-semibold-point > btn-text-18-semibold-point  (클래스명 변경 : 폰트 크기 수정함) */}
              <UIButton2 className='btn-text-18-semibold-point' leftIcon={{ className: 'ic-system-24-add', children: '' }}>
                MCP 서버 등록
              </UIButton2>
            </>
          }
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
            {/* 다중 선택 그리드 */}
            <UIListContainer>
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
              <UIListContentBox.Body>
                {view === 'grid' ? (
                  <UIGrid<any> type='multi-select' rowData={rowData} columnDefs={columnDefs} onClickRow={(_params: any) => {}} onCheck={(_selectedIds: any[]) => {}} />
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
                          statusArea={
                            <UILabel variant='badge' intent={getStatusIntent(item.deployType)}>
                              {item.deployType}
                            </UILabel>
                          }
                          checkbox={{
                            checked: selectedCardIds.includes(item.id),
                            onChange: (checked: boolean, _value: string) => {
                              if (checked) {
                                setSelectedCardIds([...selectedCardIds, item.id]);
                              } else {
                                setSelectedCardIds(selectedCardIds.filter(id => id !== item.id));
                              }
                            },
                          }}
                          rows={[
                            { label: '태그명', value: item.tagName },
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
        <UIPageFooter></UIPageFooter>
      </section>
    </DesignLayout>
  );
};
