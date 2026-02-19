import React, { useMemo, useState } from 'react';

import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIGroup } from '@/components/UI/molecules';
import { UIDataCnt, UILabel, UIToggle } from '@/components/UI';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIInput, UIUnitGroup } from '@/components/UI/molecules';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIGrid } from '@/components/UI/molecules/grid/UIGrid/component';
import { UIListContainer } from '@/components/UI/molecules/list/UIListContainer/component';
import { UIListContentBox } from '@/components/UI/molecules/list/UIListContentBox';
import { UIDropdown } from '../../../components/UI/molecules/dropdown/UIDropdown';
import { UIBox, UIButton2, UITextLabel, UITypography } from '@/components/UI/atoms';
import { UICardList } from '@/components/UI/molecules/card/UICardList';
import { DesignLayout } from '../../components/DesignLayout';
import { UIGridCard } from '@/components/UI/molecules/card/UIGridCard';

export const MD_010101 = () => {
  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    status: false,
    publicRange: false,
  });

  // 드롭다운 핸들러
  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    setDropdownStates(prev => ({
      ...Object.keys(prev).reduce((acc, k) => ({ ...acc, [k]: false }), {} as typeof prev),
      [key]: !prev[key],
    }));
  };

  const handleDropdownSelect = (key: string, _value: string) => {
    setDropdownStates(prev => ({ ...prev, [key]: false }));
  };

  // search 타입
  const [searchValue, setSearchValue] = useState('');

  // 샘플 데이터 (rowData로 변수명 변경)
  const rowData = [
    {
      id: '1',
      accountStatus: '이용 가능',
      userName: '김신한',
      department: 'Data기획Unit',
      status: '이용 가능',
      description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담',
      tags: ['가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자'],
      catogory: '지도학습',
      version: 'self_hosting',
      publicRange: '배포',
      publicStatus: '전체공유',
      createdDate: '2025.03.24 18:23:43',
      modifiedDate: '2025.03.24 18:23:43',
      more: 'more',
    },
    {
      id: '2',
      accountStatus: '이용 불가',
      userName: '이영희',
      department: 'AI개발팀',
      status: '이용 가능',
      description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담',
      tags: ['가나다라마바사아', '가나다라마바사아'],
      catogory: '지도학습',
      version: 'self_hosting',
      publicRange: '배포',
      publicStatus: '전체공유',
      createdDate: '2025.03.23 14:15:32',
      modifiedDate: '2025.03.23 14:15:32',
      more: 'more',
    },
    {
      id: '3',
      accountStatus: '이용 가능',
      userName: '박철수',
      department: 'Data기획Unit',
      status: '휴직',
      description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담',
      tags: ['test'],
      catogory: '지도학습',
      version: 'self_hosting',
      publicRange: '배포',
      publicStatus: '전체공유',
      createdDate: '2025.03.22 09:45:21',
      modifiedDate: '2025.03.22 09:45:21',
      more: 'more',
    },
    {
      id: '4',
      accountStatus: '진행중',
      userName: '최민수',
      department: 'AI개발팀',
      status: '재직',
      description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담',
      tags: ['test'],
      catogory: '지도학습',
      version: 'self_hosting',
      publicRange: '배포',
      publicStatus: '전체공유',
      createdDate: '2025.03.24 16:30:15',
      modifiedDate: '2025.03.24 16:30:15',
      more: 'more',
    },
    {
      id: '5',
      accountStatus: '실패',
      userName: '정다은',
      department: 'Data분석팀',
      status: '퇴사',
      description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담',
      tags: ['abcdefg', 'abcdefg', 'abcdefg', 'abcdefg'],
      catogory: '지도학습',
      version: 'self_hosting',
      publicRange: '배포',
      publicStatus: '전체공유',
      createdDate: '2025.03.20 11:20:43',
      modifiedDate: '2025.03.20 11:20:43',
      more: 'more',
    },
    {
      id: '6',
      accountStatus: '취소',
      userName: '홍길동',
      department: 'Data분석팀',
      status: '재직',
      description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담',
      tags: ['test', 'test', 'test', 'test', 'test', 'test', 'test', 'test', 'test', 'test', 'test'],
      catogory: '지도학습',
      version: 'self_hosting',
      publicRange: '배포',
      publicStatus: '전체공유',
      createdDate: '2025.03.24 13:55:28',
      modifiedDate: '2025.03.24 13:55:28',
      more: 'more',
    },
    {
      id: '7',
      accountStatus: '실패',
      userName: '김미영',
      department: 'AI개발팀',
      status: '재직',
      description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담',
      tags: ['태그태태그', '태그', '태그태태그', '태그', '태그태태그'],
      catogory: '지도학습',
      version: 'self_hosting',
      publicRange: '배포',
      publicStatus: '전체공유',
      createdDate: '2025.03.24 17:42:11',
      modifiedDate: '2025.03.24 17:42:11',
      more: 'more',
    },
  ];

  // 더보기 메뉴 설정  (참고 : 케이스별 버튼 노출이 다릅니다. 피그마참고)
  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          label: '중지',
          action: 'run',
          onClick: (_rowData: any) => {},
        },
        {
          label: '수정',
          action: 'modify',
          onClick: (_rowData: any) => {},
        },
        {
          label: '시작',
          action: 'copy',
          onClick: (_rowData: any) => {},
        },
        {
          label: '수정',
          action: 'copy',
          onClick: (_rowData: any) => {},
        },
        {
          label: '삭제',
          action: 'delete',
          onClick: (_rowData: any) => {},
        },
      ],
      isActive: () => true, // 모든 테스트에 대해 활성화
    }),
    []
  );

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
        headerName: '모델명',
        field: 'userName' as any,
        width: 272,
      },
      {
        headerName: '유효성',
        field: 'accountStatus' as any,
        width: 120,
        cellRenderer: React.memo((params: any) => {
          const statusColors = {
            이용가능: 'complete',
            실패: 'error',
            진행중: 'progress',
            취소: 'stop',
          } as const;
          return (
            <UILabel variant='badge' intent={statusColors[params.value as keyof typeof statusColors]}>
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
        headerName: '모델유형',
        field: 'catogory',
        width: 120,
      },
      {
        headerName: '배포유형',
        field: 'version',
        width: 120,
      },
      {
        headerName: '배포여부',
        field: 'publicRange',
        width: 120,
      },
      {
        headerName: '공개범위',
        field: 'publicStatus',
        width: 120,
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
  const [value, setValue] = useState('12개씩 보기');
  const [view, setView] = useState('grid');
  const [checkedCards, setCheckedCards] = useState<{ [key: string]: boolean }>({});

  // 그리드(카드형)
  const rowCardData = [
    {
      no: 1,
      id: '1',
      modelName: 'GPT-NeoX-Finance-QA',
      description: '고객 상담 응대 문장 생성에 최적화된 대형 언어모델',
      deployType: '이용가능',
      tags: '태그는최대8글자',
      modelType: 'labguage',
      deployList: 'language',
      permission: 'Public',
      more: 'more',
    },
    {
      no: 2,
      id: '2',
      modelName: 'GPT-NeoX-Finance-QA',
      description: '고객 상담 응대 문장 생성에 최적화된 대형 언어모델',
      deployType: '진행중',
      tags: '태그는최대8글자',
      modelType: 'labguage',
      deployList: 'language',
      permission: 'Private',
      more: 'more',
    },
    {
      no: 3,
      id: '3',
      modelName: 'GPT-NeoX-Finance-QA',
      description: '고객 상담 응대 문장 생성에 최적화된 대형 언어모델',
      deployType: '실패',
      tags: '태그는최대8글자',
      modelType: 'labguage',
      deployList: 'language',
      permission: 'Public',
      more: 'more',
    },
    {
      no: 4,
      id: '4',
      modelName: 'GPT-NeoX-Finance-QA',
      description: '고객 상담 응대 문장 생성에 최적화된 대형 언어모델',
      deployType: '이용가능',
      tags: '태그는최대8글자',
      modelType: 'labguage',
      deployList: 'language',
      permission: 'Internal',
      more: 'more',
    },
    {
      no: 5,
      id: '5',
      modelName: 'GPT-NeoX-Finance-QA',
      description: 'Google의 양방향 트랜스포머 모델로, 자연어 처리 작업에 널리 사용됩니다.',
      deployType: '이용가능',
      tags: '태그는최대8글자',
      modelType: 'labguage',
      deployList: 'language',
      permission: 'Public',
      more: 'more',
    },
  ];

  return (
    <DesignLayout
      initialMenu={{ id: 'model', label: '모델' }}
      initialSubMenu={{
        id: 'fine-tuning',
        label: '파인튜닝',
        icon: 'ico-lnb-menu-20-fine-tuning',
      }}
    >
      {/* 섹션 페이지 */}
      <section className='section-page'>
        <UIPageHeader
          title='모델 관리'
          description={[
            '내부로 반입된 모델의 상세정보와 개발망 배포 여부를 확인할 수 있습니다.',
            '모델을 클릭한 뒤, 업무 목적에 맞는 파인튜닝을 하거나 개발망에 배포해 활용해 보세요.',
          ]}
          actions={
            <>
              {/* [251222_퍼블수정]: btn-text-14-semibold-point > btn-text-18-semibold-point  (클래스명 변경 : 폰트 크기 수정함) */}
              <UIButton2 className='btn-text-18-semibold-point' leftIcon={{ className: 'ic-system-24-add', children: '' }}>
                Serverless 모델등록
              </UIButton2>
              {/* <UIButton2 className='btn-text-14-semibold-point' leftIcon={{ className: 'ic-system-24-add', children: '' }}>
                모델 배포
              </UIButton2> */}
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
                            검색
                          </UITypography>
                        </th>
                        {/* 25110_퍼블 속성값 수정 */}
                        <td>
                          <UIInput.Search
                            value={searchValue}
                            onChange={e => {
                              setSearchValue(e.target.value);
                            }}
                            placeholder='모델명, 설명, 태그 입력'
                          />
                        </td>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            모델유형
                          </UITypography>
                        </th>
                        <td>
                          <UIDropdown
                            value={'전체'}
                            placeholder='조회 조건 선택'
                            options={[
                              { value: '1', label: '전체' },
                              { value: '2', label: '아이템1' },
                              { value: '3', label: '아이템2' },
                              { value: '4', label: '아이템3' },
                            ]}
                            isOpen={dropdownStates.searchType}
                            onClick={() => handleDropdownToggle('searchType')}
                            onSelect={value => handleDropdownSelect('searchType', value)}
                          />
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            배포유형
                          </UITypography>
                        </th>
                        <td>
                          <UIUnitGroup gap={32} direction='row'>
                            <div className='flex-1'>
                              <UIDropdown
                                value={'전체'}
                                placeholder='조회 조건 선택'
                                options={[
                                  { value: '1', label: '전체' },
                                  { value: '2', label: '아이템1' },
                                  { value: '3', label: '아이템2' },
                                  { value: '4', label: '아이템3' },
                                ]}
                                isOpen={dropdownStates.searchType}
                                onClick={() => handleDropdownToggle('searchType')}
                                onSelect={value => handleDropdownSelect('searchType', value)}
                              />
                            </div>
                          </UIUnitGroup>
                        </td>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            유효성
                          </UITypography>
                        </th>
                        <td>
                          <UIDropdown
                            value={'전체'}
                            placeholder='조회 조건 선택'
                            options={[
                              { value: '1', label: '전체' },
                              { value: '2', label: '아이템1' },
                              { value: '3', label: '아이템2' },
                              { value: '4', label: '아이템3' },
                            ]}
                            isOpen={dropdownStates.searchType}
                            onClick={() => handleDropdownToggle('searchType')}
                            onSelect={value => handleDropdownSelect('searchType', value)}
                          />
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            태그
                          </UITypography>
                        </th>
                        <td>
                          <div>
                            <UIDropdown
                              value={'전체'}
                              placeholder='조회 조건 선택'
                              options={[
                                { value: '1', label: '전체' },
                                { value: '2', label: '이용가능' },
                                { value: '3', label: '실패' },
                                { value: '4', label: '취소' },
                                { value: '5', label: '진행중' },
                              ]}
                              isOpen={dropdownStates.searchType}
                              onClick={() => handleDropdownToggle('searchType')}
                              onSelect={value => handleDropdownSelect('searchType', value)}
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
                            checked: checkedCards[item.id] || false,
                            onChange: (checked: boolean, _value: string) => {
                              setCheckedCards(prev => ({
                                ...prev,
                                [item.id]: checked,
                              }));
                            },
                          }}
                          rows={[
                            { label: '태그', value: item.tags },
                            { label: '모델유형', value: item.modelType },
                            { label: '배포유형', value: item.deployList },
                            { label: '공개범위', value: item.permission },
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
      </section>
    </DesignLayout>
  );
};
