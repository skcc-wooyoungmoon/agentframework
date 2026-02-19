import React, { useMemo, useState } from 'react';

import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIGroup } from '@/components/UI/molecules';
import { UILabel } from '../../../components/UI/atoms/UILabel';
import { UIDataCnt } from '@/components/UI';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIInput } from '@/components/UI/molecules';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIGrid } from '@/components/UI/molecules/grid/UIGrid/component';
import { UIListContainer } from '@/components/UI/molecules/list/UIListContainer/component';
import { UIListContentBox } from '@/components/UI/molecules/list/UIListContentBox';
import { UIDropdown } from '../../../components/UI/molecules/dropdown/UIDropdown';

import { UIBox, UIButton2, UITypography } from '@/components/UI/atoms';
import { DesignLayout } from '../../components/DesignLayout';

export const DP_030101 = () => {
  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    status: false,
    publicRange: false,
  });

  const handleMoreClick = (_itemId: string) => {
    // 추후 더보기 메뉴 또는 모달 표시 로직 추가
  };

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

  // rowData 정의
  const rowData = [
    {
      id: 1,
      name: '고객 상담 에이전트 v2.1',
      status: '사용가능',
      projcetname: 'Pubic',
      type: '외부 시스템',
      permission: '콜센터 응대 특화모델',
    },
    {
      id: 2,
      name: '문서 요약 에이전트',
      status: '사용가능',
      projcetname: 'Pubic',
      type: '외부 시스템',
      permission: '콜센터 응대 특화모델',
    },
    {
      id: 3,
      name: '코드 리뷰 어시스턴트',
      status: '사용가능',
      projcetname: 'Pubic',
      type: '외부 시스템',
      permission: '콜센터 응대 특화모델',
    },
    {
      id: 4,
      name: '회의록 생성기',
      status: '사용차단',
      projcetname: 'Pubic',
      type: '외부 시스템',
      permission: '콜센터 응대 특화모델',
    },
    {
      id: 5,
      name: '데이터 분석 도우미',
      status: '사용가능',
      projcetname: 'Pubic',
      type: '외부 시스템',
      permission: '콜센터 응대 특화모델',
    },
    {
      id: 6,
      name: '번역 에이전트',
      status: '사용가능',
      projcetname: 'Pubic',
      type: '외부 시스템',
      permission: '콜센터 응대 특화모델',
    },
    {
      id: 7,
      name: '이메일 자동 분류기',
      status: '사용차단',
      projcetname: 'Pubic',
      type: '외부 시스템',
      permission: '콜센터 응대 특화모델',
    },
    {
      id: 8,
      name: '프로젝트 일정 관리자',
      status: '사용차단',
      projcetname: 'Pubic',
      type: '외부 시스템',
      permission: '콜센터 응대 특화모델',
    },
    {
      id: 9,
      name: '보고서 템플릿 생성기',
      status: '사용차단',
      projcetname: 'Pubic',
      type: '외부 시스템',
      permission: '콜센터 응대 특화모델',
    },
    {
      id: 10,
      name: '소셜미디어 관리자',
      status: '사용차단',
      projcetname: 'Pubic',
      type: '구분 test 말줄임 테스테스트구분 test 말줄임 테스테스트구분 test 말줄임 테스테스트구분 test 말줄임 테스테스트구분 test 말줄임 테스테스트',
      permission: 'Test test 입니다.test 입니다.test 입니다.test 입니다.test 입니다.test 입니다.test 입니다.',
    },
    {
      id: 11,
      name: '재고 관리 시스템',
      status: '사용차단',
      projcetname: 'Pubic',
      type: '구분 test 말줄임 테스테스트구분 test 말줄임 테스테스트구분 test 말줄임 테스테스트구분 test 말줄임 테스테스트구분 test 말줄임 테스테스트',
      permission: 'Test test 입니다.test 입니다.test 입니다.test 입니다.test 입니다.test 입니다.test 입니다.',
    },
    {
      id: 12,
      name: '학습 콘텐츠 추천기',
      status: '사용차단',
      projcetname: 'Pubic',
      type: '구분 test 말줄임 테스테스트구분 test 말줄임 테스테스트구분 test 말줄임 테스테스트구분 test 말줄임 테스테스트구분 test 말줄임 테스테스트',
      permission: 'feata skdnksd akdnaksd aksdn kansdk ansdk naskdnaksndkasndkasndkasndka nda kdanskd naksdn',
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
      // [251112_퍼블수정] API 그리드 컬럼 width 수정 / 말줄임 cellrender 추가 [S]
      // 251113_퍼블수정 그리드 컬럼 속성 '이름' 영역 수정 S
      {
        headerName: '이름',
        field: 'name',
        width: 272,
        minWidth: 272,
        maxWidth: 272,
        suppressSizeToFit: true,
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
        headerName: '상태',
        field: 'status',
        width: 120,
        cellRenderer: React.memo((params: any) => {
          const colorMap: { [key: string]: string } = {
            사용가능: 'complete',
            사용차단: 'error',
          };
          return (
            <UILabel variant='badge' intent={colorMap[params.value] as any}>
              {params.value}
            </UILabel>
          );
        }),
      },
      {
        headerName: '프로젝트명',
        field: 'projcetname',
        width: 360,
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
        headerName: '구분',
        field: 'type',
        width: 320,
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
        // [251202_퍼블수정] 연결 대상 문구 수정
        headerName: '연결 대상',
        field: 'permission',
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
    ],
    // [251112_퍼블수정] API 그리드 컬럼 width 수정 / 말줄임 cellrender 추가 [End]
    [handleMoreClick]
  );

  return (
    <DesignLayout>
      {/* 섹션 페이지 */}
      <section className='section-page'>
        <UIPageHeader title='API Key' description={['모델 및 에이전트 배포화면에서 발급한 API Key를 조회할 수 있습니다.']} />
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
                        <td>
                          <div className='flex-1'>
                            <UIInput.Search
                              value={searchValue}
                              onChange={e => {
                                setSearchValue(e.target.value);
                              }}
                              placeholder='이름, 연결 대상 입력'
                              // [251202_퍼블수정] 플레이스홀더 수정
                            />
                          </div>
                        </td>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            프로젝트명
                          </UITypography>
                        </th>
                        <td>
                          <div className='flex-1'>
                            <UIDropdown
                              value={'전체'}
                              placeholder='조회 조건 선택'
                              options={[
                                { value: '1', label: '전체' },
                                { value: '2', label: '사용자' },
                                { value: '3', label: '에이전트' },
                                { value: '4', label: '기타' },
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
                      value={'12개씩 보기'}
                      options={[
                        { value: '1', label: '12개씩 보기' },
                        { value: '2', label: '36개씩 보기' },
                        { value: '3', label: '60개씩 보기' },
                      ]}
                      onSelect={(_value: string) => {}}
                      onClick={() => {}}
                      height={40}
                      variant='dataGroup'
                    />
                  </div>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid type='default' rowData={rowData} columnDefs={columnDefs} onClickRow={(_params: any) => {}} onCheck={(_selectedIds: any[]) => {}} />
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination currentPage={1} totalPages={3} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
