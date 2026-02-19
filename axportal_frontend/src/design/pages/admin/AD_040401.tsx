import { useState } from 'react';

import { UITabs } from '../../../components/UI/organisms';
import { DesignLayout } from '../../components/DesignLayout';
import { UIGroup, UIDropdown, UIInput } from '@/components/UI/molecules';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UITypography, UIButton2, UIBox, UILabel } from '@/components/UI/atoms';
import { UICircleChart } from '@/components/UI/molecules/chart';

interface SearchValues {
  searchType: string;
  solutionName: string;
}

export const AD_040401 = () => {
  const [activeTab, setActiveTab] = useState('Tab3');
  const [searchValues, setSearchValues] = useState<SearchValues>({
    searchType: '전체',
    solutionName: '전체',
  });

  // search 타입
  const [searchValue1, setSearchValue1] = useState('');

  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    solutionName: false,
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

  const handleDropdownSelect = (key: keyof SearchValues, value: string) => {
    setSearchValues(prev => ({ ...prev, [key]: value }));
    setDropdownStates(prev => ({ ...prev, [key as keyof typeof dropdownStates]: false }));
  };

  // 탭 옵션 정의
  const tabOptions = [
    { id: 'Tab1', label: '포탈 자원 현황' },
    { id: 'Tab2', label: 'GPU 노드별 자원 현황' },
    { id: 'Tab3', label: '솔루션 자원 현황' },
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
        <UIPageHeader title='자원 관리' description='포탈, GPU 노드, 솔루션 네임스페이스별 자원 할당량과 사용률을 한눈에 확인할 수 있습니다.' />

        {/* 페이지 바디 */}
        <UIPageBody>
          {/* 탭 영역 */}
          <UIArticle className='article-tabs'>
            <div className='flex'>
              <UITabs items={tabOptions} activeId={activeTab} onChange={setActiveTab} size='large' />
            </div>
          </UIArticle>

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
                              value={searchValue1}
                              placeholder='네임스페이스명 입력'
                              onChange={e => {
                                setSearchValue1(e.target.value);
                              }}
                            />
                          </div>
                        </td>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            솔루션명
                          </UITypography>
                        </th>
                        <td>
                          <div className='flex-1'>
                            <UIDropdown
                              value={searchValues.searchType}
                              placeholder='조회 조건 선택'
                              options={[
                                { value: 'val1', label: '솔루션명1' },
                                { value: 'val2', label: '솔루션명2' },
                                { value: 'val3', label: '솔루션명3' },
                              ]}
                              isOpen={dropdownStates.searchType}
                              onClick={() => handleDropdownToggle('searchType')}
                              onSelect={value => handleDropdownSelect('searchType', value)}
                            />
                          </div>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            상태
                          </UITypography>
                        </th>
                        <td>
                          <div className='flex-1'>
                            <UIDropdown
                              value={searchValues.solutionName}
                              placeholder='조회 조건 선택'
                              options={[
                                { value: 'val1', label: '전체' },
                                { value: 'val2', label: '정상' },
                                { value: 'val3', label: '과부하' },
                              ]}
                              isOpen={dropdownStates.solutionName}
                              onClick={() => handleDropdownToggle('solutionName')}
                              onSelect={value => handleDropdownSelect('solutionName', value)}
                            />
                          </div>
                        </td>
                        <th></th>
                        {/* [참고] 간격 비율조정을 위해 꼭 빈공간 th/td 남겨주세요. */}
                        <td></td>
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

          <UIArticle>
            <div className='article-body'>
              {/* [퍼블수정] class 추가 : grid-col2 */}
              <div className='chart-container grid-col2 mt-4'>
                {/* Chart 영역 구분 */}
                <div className='chart-item flex-1 !p-5'>
                  {/* [251110_퍼블 수정 클래스명 수정] */}
                  <div className='chart-header flex !items-start mb-4'>
                    <UIGroup gap={8} direction='column' className='basis-[400px]'>
                      <UITypography variant='title-3' className='text-sb'>
                        API G/W
                      </UITypography>
                      <UITypography variant='body-1' className='secondary-neutral-700'>
                        ns-apigw-dev
                      </UITypography>
                    </UIGroup>
                    <UILabel variant='badge' intent='complete'>
                      정상
                    </UILabel>
                  </div>
                  <div className='flex chart-graph h-[210px] gap-x-20 justify-center !bg-transparent'>
                    <UICircleChart.Half type='CPU' value={50} total={98} />
                    <UICircleChart.Half type='Memory' value={20} total={100} />
                  </div>
                </div>
                {/* Chart 영역 구분 */}
                <div className='chart-item flex-1'>
                  {/* [251110_퍼블 수정 클래스명 수정] */}
                  <div className='chart-header flex !items-start mb-4'>
                    <UIGroup gap={8} direction='column' className='basis-[400px]'>
                      <UITypography variant='title-3' className='text-sb'>
                        Datumo
                      </UITypography>
                      <UITypography variant='body-1' className='secondary-neutral-700'>
                        ns-datumo-analytics
                      </UITypography>
                    </UIGroup>
                    <UILabel variant='badge' intent='error'>
                      과부하
                    </UILabel>
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
                  {/* [251110_퍼블 수정 클래스명 수정] */}
                  <div className='chart-header flex !items-start mb-4'>
                    <UIGroup gap={8} direction='column' className='basis-[400px]'>
                      <UITypography variant='title-3' className='text-sb'>
                        ADXP
                      </UITypography>
                      <UITypography variant='body-1' className='secondary-neutral-700'>
                        ns-adxp-dashboard
                      </UITypography>
                    </UIGroup>
                    <UILabel variant='badge' intent='complete'>
                      정상
                    </UILabel>
                  </div>
                  <div className='flex chart-graph h-[210px] gap-x-20 justify-center !bg-transparent'>
                    <UICircleChart.Half type='CPU' value={50} total={98} />
                    <UICircleChart.Half type='Memory' value={20} total={100} />
                  </div>
                </div>
                {/* Chart 영역 구분 */}
                <div className='chart-item flex-1'>
                  {/* [251110_퍼블 수정 클래스명 수정] */}
                  <div className='chart-header flex !items-start mb-4'>
                    <UIGroup gap={8} direction='column' className='basis-[400px]'>
                      <UITypography variant='title-3' className='text-sb'>
                        포탈
                      </UITypography>
                      <UITypography variant='body-1' className='secondary-neutral-700'>
                        ns-portal-admin
                      </UITypography>
                    </UIGroup>
                    <UILabel variant='badge' intent='complete'>
                      정상
                    </UILabel>
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
