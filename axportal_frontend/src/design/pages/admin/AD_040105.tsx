import { useState } from 'react';

import { UIDropdown } from '../../../components/UI/molecules/dropdown/UIDropdown';
import { DesignLayout } from '../../components/DesignLayout';

import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UITypography, UIBox, UIButton2 } from '@/components/UI/atoms';
import { UIGroup, UIInput } from '@/components/UI/molecules';
import { UIDataCnt } from '@/components/UI';
import { UICircleChart } from '@/components/UI/molecules/chart';

interface SearchValues {
  dateType: string;
  dateRange: { startDate?: string; endDate?: string };
  searchType: string;
  projectType: string;
  searchKeyword: string;
  status: string;
  agentType: string;
  startTime: string;
  endTime: string;
}

export const AD_040105 = () => {
  const [searchValues, setSearchValues] = useState<SearchValues>({
    dateType: '생성일시',
    dateRange: { startDate: '2025.06.30', endDate: '2025.07.30' },
    searchType: '전체',
    projectType: '전체',
    searchKeyword: '',
    status: '전체',
    agentType: '전체',
    startTime: '',
    endTime: '',
  });

  // search 타입
  const [searchValue1, setSearchValue1] = useState('');

  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    projectType: false,
    status: false,
    agentType: false,
  });

  // 드롭다운 핸들러
  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    setDropdownStates(prev => ({
      ...Object.keys(prev).reduce((acc, k) => ({ ...acc, [k]: false }), {} as typeof prev),
      [key]: !prev[key],
    }));
  };
  const handleDropdownSelect = (key: keyof SearchValues, value: string) => {
    setSearchValues(prev => ({ ...prev, [key]: value }));
    setDropdownStates(prev => ({ ...prev, [key]: false }));
  };

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
        <UIPageHeader title='에이전트 배포 자원 현황 조회' description='' />

        {/* 페이지 바디 */}
        <UIPageBody>
          {/* 검색 영역 */}
          {/* [251105_퍼블수정] 검색영역 수정  */}
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
                              value={searchValue1}
                              placeholder='배포명 입력'
                              onChange={e => {
                                setSearchValue1(e.target.value);
                              }}
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
                              value={searchValues.searchType}
                              placeholder='조회 조건 선택'
                              options={[
                                { value: 'val1', label: '프로젝트명1' },
                                { value: 'val2', label: '프로젝트명2' },
                                { value: 'val3', label: '프로젝트명3' },
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

          {/* Chart 영역 */}
          <UIArticle className='article-grid'>
            <div className='article-body'>
              <div className='mb-4'>
                <UIDataCnt count={4} prefix='총' unit='건' />
              </div>
              {/* [퍼블수정] class 추가 : grid-col2 */}
              <div className='chart-container grid-col2 mt-4'>
                {/* Chart 영역 구분 */}
                <div className='chart-item flex-1 !p-5'>
                  <div className='chart-header mb-4'>
                    <UIGroup gap={8} direction='column'>
                      <UITypography variant='title-3' className='text-sb'>
                        신한 스마트콜봇
                      </UITypography>
                      <UITypography variant='body-1' className='secondary-neutral-700'>
                        대출 상품 추천
                      </UITypography>
                    </UIGroup>
                  </div>
                  <div className='flex chart-graph h-[210px] gap-x-20 justify-center !bg-transparent'>
                    {/* [251229_퍼블수정] usedLabel, availableLabel 값이 있으면 명칭 변경 / 없으면 default 값 사용 */}
                    <UICircleChart.Half type='CPU' value={50} total={98} />
                    <UICircleChart.Half type='Memory' value={20} total={100} />
                  </div>
                </div>
                {/* Chart 영역 구분 */}
                <div className='chart-item flex-1'>
                  <div className='chart-header mb-4'>
                    <UIGroup gap={8} direction='column'>
                      <UITypography variant='title-3' className='text-sb'>
                        준법 감수 문장 필터
                      </UITypography>
                      <UITypography variant='body-1' className='secondary-neutral-700'>
                        쏠봇 개발
                      </UITypography>
                    </UIGroup>
                  </div>
                  <div className='flex chart-graph h-[210px] gap-x-20 justify-center !bg-transparent'>
                    <UICircleChart.Half type='CPU' value={50} total={98} />
                    <UICircleChart.Half type='Memory' value={20} total={100} />
                  </div>
                </div>
              </div>
              {/* [퍼블수정] class 추가 : grid-col2 */}
              <div className='chart-container grid-col2 mt-4'>
                {/* Chart 영역 구분 */}
                <div className='chart-item flex-1 !p-5'>
                  <div className='chart-header mb-4'>
                    <UIGroup gap={8} direction='column'>
                      <UITypography variant='title-3' className='text-sb'>
                        시각자료 요약봇
                      </UITypography>
                      <UITypography variant='body-1' className='secondary-neutral-700'>
                        대출 상품 추천
                      </UITypography>
                    </UIGroup>
                  </div>
                  <div className='flex chart-graph h-[210px] gap-x-20 justify-center !bg-transparent'>
                    <UICircleChart.Half type='CPU' value={50} total={98} />
                    <UICircleChart.Half type='Memory' value={20} total={100} />
                  </div>
                </div>
                {/* Chart 영역 구분 */}
                <div className='chart-item flex-1'>
                  <div className='chart-header mb-4'>
                    <UIGroup gap={8} direction='column'>
                      <UITypography variant='title-3' className='text-sb'>
                        한영 번역 에이전트
                      </UITypography>
                      <UITypography variant='body-1' className='secondary-neutral-700'>
                        쏠봇 개발
                      </UITypography>
                    </UIGroup>
                  </div>
                  <div className='flex chart-graph h-[210px] gap-x-20 justify-center !bg-transparent'>
                    <UICircleChart.Half type='CPU' value={50} total={98} />
                    <UICircleChart.Half type='Memory' value={20} total={100} />
                  </div>
                </div>
              </div>
            </div>
          </UIArticle>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
