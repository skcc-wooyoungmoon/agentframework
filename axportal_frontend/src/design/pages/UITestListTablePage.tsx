import { memo, useMemo, useState } from 'react';

import { UIProgress, UITextLabel, UITypography } from '@/components/UI/atoms';
import { UIGridCard, UIGroup } from '@/components/UI/molecules';

import { UIButton2, UIDataCnt, UILabel, UIToggle } from '@/components/UI';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown/component';
import { useModal } from '@/stores/common/modal';

import { UIGrid } from '@/components/UI/molecules/grid/UIGrid/component';
import { UIListContainer } from '@/components/UI/molecules/list/UIListContainer/component';
import { UIListContentBox } from '@/components/UI/molecules/list/UIListContentBox';
import type { ColDef } from 'ag-grid-community';
import { UICardList } from '../../components/UI/molecules/card/UICardList';

const rowData = [
  {
    no: 1,
    id: '1',
    modelName: 'GPT-4',
    description:
      'OpenAI의 최신 대화형 AI 모델입니다. 자연어 이해와 생성 능력이 뛰어납니다.OpenAI의 최신 대화형 AI 모델입니다. 자연어 이해와 생성 능력이 뛰어납니다.OpenAI의 최신 대화형 AI 모델입니다. 자연어 이해와 생성 능력이 뛰어납니다.OpenAI의 최신 대화형 AI 모델입니다. 자연어 이해와 생성 능력이 뛰어납니다.OpenAI의 최신 대화형 AI 모델입니다. 자연어 이해와 생성 능력이 뛰어납니다.OpenAI의 최신 대화형 AI 모델입니다. 자연어 이해와 생성 능력이 뛰어납니다.',
    deployType: 'serverless',
    modelType: 'LLM',
    permission: 'Public',
    createdDate: '2024-01-15 09:30:00',
    modifiedDate: '2024-01-20 14:25:00',
    // more: 'more',
    isActive: true,
  },
  {
    no: 2,
    id: '2',
    modelName: 'Claude-3',
    description: 'Anthropic의 고성능 AI 모델로, 창의적 글쓰기와 분석에 특화되어 있습니다.',
    deployType: 'self_hosting',
    modelType: 'LLM',
    permission: 'Private',
    createdDate: '2024-01-10 11:15:00',
    modifiedDate: '2024-01-18 16:40:00',
    more: 'more',
    isActive: false,
  },
  {
    no: 3,
    id: '3',
    modelName: 'DALL-E 3',
    description: '텍스트를 이미지로 변환하는 AI 모델입니다. 고품질 이미지 생성을 제공합니다.',
    deployType: 'serverless',
    modelType: 'Image Generation',
    permission: 'Public',
    createdDate: '2024-01-05 13:45:00',
    modifiedDate: '2024-01-22 10:20:00',
    more: 'more',
    isActive: true,
  },
  {
    no: 4,
    id: '4',
    modelName: 'Whisper',
    description: '음성을 텍스트로 변환하는 음성 인식 모델입니다. 다국어를 지원합니다.',
    deployType: 'self_hosting',
    modelType: 'Speech Recognition',
    permission: 'Internal',
    createdDate: '2024-01-12 08:30:00',
    modifiedDate: '2024-01-19 15:50:00',
    more: 'more',
    isActive: false,
  },
  {
    no: 5,
    id: '5',
    modelName: 'BERT-base',
    description: 'Google의 양방향 트랜스포머 모델로, 자연어 처리 작업에 널리 사용됩니다.',
    deployType: 'serverless',
    modelType: 'NLP',
    permission: 'Public',
    createdDate: '2024-01-08 16:20:00',
    modifiedDate: '2024-01-21 12:35:00',
    more: 'more',
    isActive: true,
  },
];

// 다중 테스트 그리드용 데이터 - GRID.md 지침에 따른 구조
/* const rowData1 = [
  {
    no: 1,
    id: '1',
    name: '사용자 인증 테스트',
    category: '인증',
    dataType: 'JSON',
    createdDate: '2024-01-15 09:30:00',
    modifiedDate: '2024-01-20 14:25:00',
  },
  {
    no: 2,
    id: '2',
    name: '데이터 검증 테스트',
    category: '검증',
    dataType: 'XML',
    createdDate: '2024-01-10 11:15:00',
    modifiedDate: '2024-01-18 16:40:00',
  },
  {
    no: 3,
    id: '3',
    name: 'API 연동 테스트',
    category: 'API',
    dataType: 'JSON',
    createdDate: '2024-01-05 13:45:00',
    modifiedDate: '2024-01-22 10:20:00',
  },
  {
    no: 4,
    id: '4',
    name: '데이터베이스 연결 테스트',
    category: 'DB',
    dataType: 'SQL',
    createdDate: '2024-01-12 08:30:00',
    modifiedDate: '2024-01-19 15:50:00',
  },
  {
    no: 5,
    id: '5',
    name: '파일 업로드 테스트',
    category: '파일',
    dataType: 'Binary',
    createdDate: '2024-01-08 16:20:00',
    modifiedDate: '2024-01-21 12:35:00',
  },
  {
    no: 6,
    id: '6',
    name: '성능 측정 테스트',
    category: '성능',
    dataType: 'JSON',
    createdDate: '2024-01-14 10:15:00',
    modifiedDate: '2024-01-23 09:45:00',
  },
  {
    no: 7,
    id: '7',
    name: '보안 검증 테스트',
    category: '보안',
    dataType: 'XML',
    createdDate: '2024-01-11 14:20:00',
    modifiedDate: '2024-01-24 11:30:00',
  },
  {
    no: 8,
    id: '8',
    name: 'UI 컴포넌트 테스트',
    category: 'UI',
    dataType: 'JSON',
    createdDate: '2024-01-09 16:45:00',
    modifiedDate: '2024-01-25 13:15:00',
  },
]; */

// 프로젝트 관리 그리드용 데이터
const rowData2 = [
  {
    no: 1,
    id: 'proj-001',
    projectName: 'AI 챗봇 개발',
    manager: '김신한',
    team: 'AI개발팀',
    status: 'in_progress',
    progress: 65,
    startDate: '2024-01-01',
    endDate: '2024-03-31',
  },
  {
    no: 2,
    id: 'proj-002',
    projectName: '데이터 분석 플랫폼',
    manager: '김신한',
    team: '데이터팀',
    status: 'completed',
    progress: 100,
    startDate: '2023-10-01',
    endDate: '2024-01-15',
  },
  {
    no: 3,
    id: 'proj-003',
    projectName: 'UI/UX 개선',
    manager: '김신한',
    team: '디자인팀',
    status: 'planning',
    progress: 15,
    startDate: '2024-02-01',
    endDate: '2024-04-30',
  },
  {
    no: 4,
    id: 'proj-004',
    projectName: '보안 강화 프로젝트',
    manager: '김신한',
    team: '보안팀',
    status: 'in_progress',
    progress: 80,
    startDate: '2023-12-01',
    endDate: '2024-02-28',
  },
  {
    no: 5,
    id: 'proj-005',
    projectName: '모바일 앱 개발',
    manager: '김신한',
    team: '모바일팀',
    status: 'on_hold',
    progress: 40,
    startDate: '2024-01-15',
    endDate: '2024-06-30',
  },
];

export function UITestListTablePage() {
  const { openAlert } = useModal();
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

  // 프로젝트 관리 그리드용 더보기 메뉴 설정
  const moreMenuConfig2 = useMemo(
    () => ({
      items: [
        {
          label: '상세보기',
          action: 'view',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `프로젝트 "${rowData.projectName}" 상세 정보를 확인합니다.`,
            });
          },
        },
        {
          label: '수정',
          action: 'edit',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `프로젝트 "${rowData.projectName}" 수정 화면으로 이동합니다.`,
            });
          },
        },
        {
          label: '삭제',
          action: 'delete',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `프로젝트 "${rowData.projectName}"를 삭제하시겠습니까?`,
            });
          },
        },
      ],
      isActive: () => true,
    }),
    [openAlert]
  );

  const columnDefs: ColDef[] = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no',
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
      },
      {
        headerName: '모델명',
        field: 'modelName',
        maxWidth: 200,
      },
      {
        headerName: '이름',
        field: 'productName',
        maxWidth: 270,
      },
      {
        headerName: '상태',
        field: 'status',
        maxWidth: 120,
      },
      {
        headerName: '설명',
        field: 'description',
        flex: 1,
        showTooltip: true, // 툴팁 표시 여부
      },
      {
        headerName: '진행률',
        field: 'progress',
        width: 370,
        cellRenderer: memo((params: any) => {
          return <UIProgress value={params.value} status={params.status} showPercent={true} />;
        }),
      },
      {
        headerName: '배포유형',
        field: 'deployType',
        width: 120,
        cellRenderer: memo((params: any) => {
          const colorMap: { [key: string]: string } = {
            serverless: 'complete',
            self_hosting: 'warning',
          };
          return (
            <UILabel variant='badge' intent={colorMap[params.value] as any}>
              {params.value}
            </UILabel>
          );
        }),
      },
      {
        headerName: '버전',
        field: 'version',
        width: 238,
        cellRenderer: memo((params: any) => {
          return <UITextLabel intent={params.color}>{params.text}</UITextLabel>;
        }),
      },
      {
        headerName: '기본설정',
        field: 'setting',
        width: 238,
        cellRenderer: memo((params: any) => {
          return <UITextLabel intent={params.color}>{params.text}</UITextLabel>;
        }),
      },
      {
        headerName: '업무',
        field: 'works',
        width: 120,
      },
      {
        headerName: '공개범위',
        field: 'permission',
        width: 120,
      },
      {
        headerName: '최근 생성일',
        field: 'createdDate',
        width: 200,
      },
      {
        headerName: '최근 수정일',
        field: 'modifiedDate',
        width: 200,
      },
      {
        // 버튼 컬럼 처리
        headerName: '활성화',
        field: 'button', // 버튼 형태 컬럼 필드명 (고정)
        width: 120,
        cellRenderer: memo(({ data }: any) => {
          return (
            <UIToggle
              checked={data.isActive}
              onChange={() => {
                // 개발자 로직 처리
              }}
            />
          );
        }),
      },
      {
        headerName: '',
        field: 'more', // 더보기 컬럼 필드명 (고정)
        width: 56,
      },
    ],
    []
  );

  // 프로젝트 관리 그리드용 컬럼 정의
  const columnDefs2: ColDef[] = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no',
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
      },
      {
        headerName: '프로젝트명',
        field: 'projectName',
        width: 250,
        flex: 1,
      },
      {
        headerName: '담당자',
        field: 'manager',
        width: 120,
      },
      {
        headerName: '팀',
        field: 'team',
        width: 150,
      },
      {
        headerName: '상태',
        field: 'status',
        width: 120,
        cellRenderer: memo((params: any) => {
          const statusMap: { [key: string]: { label: string; intent: string } } = {
            planning: { label: '테스트', intent: 'tag' },
            in_progress: { label: '진행중', intent: 'progress' },
            completed: { label: '완료', intent: 'complete' },
            on_hold: { label: '중지', intent: 'warning' },
          };
          const status = statusMap[params.value] || { label: params.value, intent: 'tag' };
          return (
            <UILabel variant='badge' intent={status.intent as any}>
              {status.label}
            </UILabel>
          );
        }),
      },
      {
        headerName: '진행률',
        field: 'progress',
        width: 200,
        cellRenderer: memo((params: any) => {
          const getStatus = (value: number): 'normal' | 'error' => {
            if (value < 50) return 'error';
            return 'normal';
          };
          return <UIProgress value={params.value} status={getStatus(params.value)} showPercent={true} />;
        }),
      },
      {
        headerName: '시작일',
        field: 'startDate',
        width: 120,
      },
      {
        headerName: '종료일',
        field: 'endDate',
        width: 120,
      },
      {
        headerName: '',
        field: 'more',
        width: 56,
      },
    ],
    []
  );

  // 다중 테스트 그리드용 컬럼 정의 - GRID.md 지침에 따른 설정
  /* const columnDefs1: ColDef[] = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no' as keyof (typeof rowData1)[0],
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
      },
      {
        headerName: '이름',
        field: 'name' as keyof (typeof rowData1)[0],
        width: 300,
        maxWidth: 300,
      },
      {
        headerName: '카테고리',
        field: 'category' as keyof (typeof rowData1)[0],
        width: 120,
        maxWidth: 120,
        cellRenderer: memo((params: any) => {
          const colorMap: { [key: string]: string } = {
            인증: 'complete',
            검증: 'progress',
            API: 'warning',
            DB: 'error',
            파일: 'tag',
            성능: 'blue',
            보안: 'red',
            UI: 'green',
          };
          return (
            <UILabel variant='badge' intent={colorMap[params.value] as any}>
              {params.value}
            </UILabel>
          );
        }),
      },
      {
        headerName: '데이터 유형',
        field: 'dataType' as keyof (typeof rowData1)[0],
        width: 120,
        maxWidth: 120,
        cellRenderer: memo((params: any) => {
          const colorMap: { [key: string]: string } = {
            JSON: 'blue',
            XML: 'green',
            SQL: 'purple',
            Binary: 'gray',
          };
          return <UITextLabel intent={colorMap[params.value] as any}>{params.value}</UITextLabel>;
        }),
      },
      {
        headerName: '생성일시',
        field: 'createdDate' as keyof (typeof rowData1)[0],
        width: 180,
        maxWidth: 180,
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate' as keyof (typeof rowData1)[0],
        width: 180,
        maxWidth: 180,
      },
    ],
    []
  ); */

  const [value, setValue] = useState('10');
  const [view, setView] = useState('grid');
  const [skeletonView, setSkeletonView] = useState('grid'); // 스켈레톤 영역용 view state

  const [checked1, setChecked1] = useState(false);
  const [checkedSkeletonCards, setCheckedSkeletonCards] = useState<{ [key: string]: boolean }>({});
  return (
    <div className='flex flex-col gap-4 p-[30px] h-screen overflow-hidden overflow-y-scroll gap-12'>
      {/* 전체 데이터 없음 */}
      <div className='group-grid-item flex-1 shrink-0 [flex-basis:0]'>
        <div className='font-semibold text-lg bg-gray-300 p-[10px] mb-3'>데이터 없음</div>
        <UIListContainer className=''>
          {' '}
          {/* h-full 을 prop으로 받기 높이 full 아니라면 '' 빈값  */}
          <UIListContentBox.Header>
            <div className='flex-shrink-0'>
              <UIGroup gap={8} direction='row' align='start'>
                <div style={{ width: '168px', paddingRight: '8px' }}>
                  <UIDataCnt count={0} prefix='총' unit='건' />
                </div>
              </UIGroup>
            </div>
            <div className='flex items-center gap-2'>
              <div className='flex-shrink-0'>버튼 (영역)</div>
              <div className='flex-shrink-0'>셀렉트박스 & 검색어입력 (영역)</div>
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            {view === 'grid' ? (
              <UIGrid<any>
                rowData={[]} // [참고] 데이터값 없을때 : data 값 [] 빈값
                columnDefs={columnDefs} // [참고] 데이터값 없을때 : 상단 컬럼은 있어야함.
                moreMenuConfig={moreMenuConfig}
                domLayout={'autoHeight'}
                onClickRow={(_params: any) => {}}
                onCheck={(_selectedIds: any[]) => {}}
              />
            ) : (
              <UICardList
                rowData={[]}
                card={(item: any) => (
                  <UIGridCard
                    id={item.id}
                    title={item.modelName}
                    caption={item.description}
                    moreMenuConfig={moreMenuConfig}
                    statusArea={
                      <UILabel variant='badge' intent={item.deployType === 'serverless' ? 'complete' : 'warning'}>
                        {item.deployType}
                      </UILabel>
                    }
                    checkbox={{
                      checked: false,
                      onChange: (_checked: boolean, _value: string) => {},
                    }}
                    rows={[
                      { label: '모델명', value: item.modelName },
                      { label: '모델유형', value: item.modelType },
                      { label: '배포유형', value: item.deployType },
                      { label: '공개범위', value: 'TODO' },
                    ]}
                  />
                )}
              />
            )}
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIButton2 className='btn-tertiary-gray'>삭제</UIButton2>
            <UIPagination currentPage={1} totalPages={10} onPageChange={(_page) => {}} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </div>

      {/* ############################################################################################# */}

      {/* 전체 데이터 목록 */}
      <div className='group-grid-item flex-1 shrink-0 [flex-basis:0]'>
        <div className='font-semibold text-lg bg-gray-300 p-[10px] mb-3'>기본</div>
        <UIListContainer className=''>
          {' '}
          {/* h-full 을 prop으로 받기 높이 full 아니라면 '' 빈값  */}
          <UIListContentBox.Header>
            <div className='flex-shrink-0'>
              <UIGroup gap={8} direction='row' align='start'>
                <div style={{ width: '168px', paddingRight: '8px' }}>
                  <UIDataCnt count={0} prefix='지식데이터 총' unit='건' />
                </div>
                <UITypography variant='body-1' className='secondary-neutral-p'>
                  동기화
                </UITypography>
                <UIToggle
                  size='medium'
                  checked={checked1}
                  onChange={() => {
                    setChecked1(!checked1);
                    // 개발자 로직 처리
                  }}
                />
              </UIGroup>
            </div>
            <div className='flex items-center gap-2'>
              <div className='flex-shrink-0'>
                <UIButton2 className='btn-tertiary-outline' onClick={() => {}}>
                  메트릭 뷰
                </UIButton2>
              </div>
              <div className='flex-shrink-0'>
                <UIDropdown
                  value={String(value)}
                  options={[
                    { value: '10', label: '10' },
                    { value: '20', label: '20' },
                  ]}
                  onSelect={(value: string) => {setValue(value);
                  }}
                  onClick={() => {}}
                  height={40}
                  variant='dataGroup'
                  width='w-40'
                />
              </div>
              <UIToggle variant='dataView' checked={view === 'card'} onChange={checked => setView(checked ? 'card' : 'grid')} />
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            {view === 'grid' ? (
              <UIGrid<any>
                // rowData={rowData}
                rowData={[]} // 데이터 없을시
                columnDefs={columnDefs}
                moreMenuConfig={moreMenuConfig}
                onClickRow={(_params: any) => {}}
                onCheck={(_selectedIds: any[]) => {}}
              />
            ) : (
              <UICardList
                // rowData={rowData} 데이터 있을경우
                rowData={[]} // 데이터 없을시
                card={(item: any) => (
                  <UIGridCard
                    id={item.id}
                    title={item.modelName}
                    caption={item.description}
                    data={item} // 카드형 더보기 추가시
                    moreMenuConfig={moreMenuConfig} // 카드형 더보기 추가시
                    statusArea={
                      <UILabel variant='badge' intent={item.deployType === 'serverless' ? 'complete' : 'warning'}>
                        {item.deployType}
                      </UILabel>
                    }
                    checkbox={{
                      checked: false,
                      onChange: (_checked: boolean, _value: string) => {},
                    }}
                    rows={[
                      { label: '모델명', value: item.modelName },
                      { label: '모델유형', value: item.modelType },
                      { label: '배포유형', value: item.deployType },
                      { label: '공개범위', value: 'TODO' },
                    ]}
                  />
                )}
              />
            )}
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIButton2 className='btn-tertiary-gray'>삭제</UIButton2>
            <UIPagination currentPage={1} totalPages={10} onPageChange={(_page) => {}} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </div>

      <div className='group-grid-item flex-1 shrink-0 [flex-basis:0]'>
        {/* 스켈레톤 */}
        <div className='font-semibold text-lg bg-gray-300 p-[10px] mb-3'>스켈레톤</div>
        <UIListContainer className=''>
          {' '}
          {/* h-full 을 prop으로 받기 높이 full 아니라면 '' 빈값  */}
          <UIListContentBox.Header>
            <div className='flex-shrink-0'>
              <UIGroup gap={8} direction='row' align='start'>
                <div style={{ width: '168px', paddingRight: '8px' }}>
                  <UIDataCnt count={5} prefix='총' unit='건' />
                </div>
              </UIGroup>
            </div>
            <div className='flex items-center gap-2'>
              <UIToggle variant='dataView' checked={skeletonView === 'card'} onChange={checked => setSkeletonView(checked ? 'card' : 'grid')} />
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            {skeletonView === 'grid' ? (
              <UIGrid<any>
                type='multi-select'
                rowData={rowData2}
                columnDefs={columnDefs2}
                moreMenuConfig={moreMenuConfig2}
                loading={true} // 스켈레톤 상태 ture / false
                onClickRow={(_params: any) => {}}
                onCheck={(_selectedIds: any[]) => {}}
              />
            ) : (
              <UICardList
                rowData={rowData2}
                flexType='none'
                loading={true} // 스켈레톤 상태 ture / false
                card={(item: any) => {
                  const getStatusIntent = (status: string) => {
                    switch (status) {
                      case 'in_progress':
                        return 'progress';
                      case 'completed':
                        return 'complete';
                      case 'planning':
                        return 'tag';
                      case 'on_hold':
                        return 'warning';
                      default:
                        return 'complete';
                    }
                  };
                  return (
                    <UIGridCard
                      id={item.id}
                      title={item.projectName}
                      caption={item.manager}
                      data={item}
                      moreMenuConfig={moreMenuConfig2}
                      statusArea={
                        <UILabel variant='badge' intent={getStatusIntent(item.status)}>
                          {item.status === 'in_progress' ? '진행중' : item.status === 'completed' ? '완료' : item.status === 'planning' ? '테스트' : '중지'}
                        </UILabel>
                      }
                      checkbox={{
                        checked: checkedSkeletonCards[item.id] || false,
                        onChange: (checked: boolean, _value: string) => {
                          setCheckedSkeletonCards(prev => ({
                            ...prev,
                            [item.id]: checked,
                          }));
                        },
                      }}
                      rows={[
                        { label: '팀', value: item.team },
                        { label: '시작일', value: item.startDate },
                        { label: '종료일', value: item.endDate },
                        { label: '진행률', value: `${item.progress}%` },
                      ]}
                    />
                  );
                }}
              />
            )}
          </UIListContentBox.Body>
        </UIListContainer>
      </div>

      {/* ############################################################################################# */}

      {/* 단일 선택그리드 */}
      <div className='group-grid-item flex-1 shrink-0 [flex-basis:0]'>
        <div className='font-semibold text-lg bg-gray-300 p-[10px] mb-3'>단일</div>
        <UIListContainer className=''>
          {' '}
          {/* h-full 을 prop으로 받기 높이 full 아니라면 '' 빈값  */}
          <UIListContentBox.Body>
            <UIGrid<any>
              type='single-select'
              rowData={rowData}
              columnDefs={columnDefs}
              moreMenuConfig={moreMenuConfig}
              onClickRow={(_params: any) => {}}
              onCheck={(_selectedIds: any[]) => {}}
            />
          </UIListContentBox.Body>
        </UIListContainer>
      </div>

      <div className='group-grid-item flex-1 shrink-0 [flex-basis:0]'>
        {/* 다중 선택 그리드 */}
        <div className='font-semibold text-lg bg-gray-300 p-[10px] mb-3'>다중</div>
        <UIListContainer className=''>
          {' '}
          {/* h-full 을 prop으로 받기 높이 full 아니라면 '' 빈값  */}
          <UIListContentBox.Body>
            <UIGrid<any>
              type='multi-select'
              rowData={rowData}
              columnDefs={columnDefs}
              moreMenuConfig={moreMenuConfig}
              onClickRow={(_params: any) => {}}
              onCheck={(_selectedIds: any[]) => {}}
            />
          </UIListContentBox.Body>
        </UIListContainer>
      </div>
    </div>
  );
}
