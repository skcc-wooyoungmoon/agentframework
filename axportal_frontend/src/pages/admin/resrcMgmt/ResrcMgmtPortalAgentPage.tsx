import { useMemo, useState } from 'react';

import { UIDropdown } from '../../../components/UI/molecules/dropdown/UIDropdown';

import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIBox, UIButton2, UIIcon2, UITypography } from '@/components/UI/atoms';
import { UIGroup, UIInput } from '@/components/UI/molecules';
import { UIDataCnt } from '@/components/UI';
import { UICircleChart } from '@/components/UI/molecules/chart';
import { useGetAgentPods, useGetCommonProjects } from '@/services/admin/resrcMgmt';

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

export const ResrcMgmtPortalAgentPage = () => {
  const { data: agentPods } = useGetAgentPods(undefined, {
    refetchOnMount: 'always',
    staleTime: 0,
  });

  const { data: projectList } = useGetCommonProjects({
    refetchOnMount: 'always',
    staleTime: 0,
  });

  const parsedPods = agentPods?.pods && Array.isArray(agentPods.pods) ? agentPods.pods : [];

  const projectOptions = useMemo(() => {
    const options = [{ value: '전체', label: '전체' }];
    if (projectList && Array.isArray(projectList) && projectList.length > 0) {
      projectList.forEach(project => {
        options.push({ value: project.prjNm, label: project.prjNm });
      });
    }
    return options;
  }, [projectList]);

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

  const [searchValue1, setSearchValue1] = useState('');

  const [appliedFilters, setAppliedFilters] = useState({
    podId: '전체',
    projectType: '전체',
    searchKeyword: '',
  });

  const filteredPods = useMemo(() => {
    if (!parsedPods || parsedPods.length === 0) return [];

    return parsedPods.filter((pod: any) => {
      if (appliedFilters.podId !== '전체' && pod.builderName !== appliedFilters.podId) {
        return false;
      }

      if (appliedFilters.projectType !== '전체' && (pod.lstPrjNm || '') !== appliedFilters.projectType) {
        return false;
      }

      if (appliedFilters.searchKeyword && !(pod.name || '').toLowerCase().includes(appliedFilters.searchKeyword.toLowerCase())) {
        return false;
      }

      return true;
    });
  }, [parsedPods, appliedFilters]);

  const totals = useMemo(() => {
    return filteredPods.reduce(
      (acc, pod: any) => {
        acc.cpu += parseFloat(pod.cpu_limit || 0);
        acc.memory += parseFloat(pod.memory_limit || 0);
        return acc;
      },
      { cpu: 0, memory: 0 }
    );
  }, [filteredPods]);

  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    projectType: false,
    status: false,
    agentType: false,
  });

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

  const handleSearch = () => {
    setAppliedFilters({
      podId: searchValues.searchType,
      projectType: searchValues.projectType,
      searchKeyword: searchValue1.trim(),
    });
  };

  const hasMultipleRows = filteredPods.length > 2;

  return (
    <section className='section-page'>
      <UIPageHeader title='에이전트 배포 자원 현황 조회' description='' />

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
                      <td>
                        <div>
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
                        <UIDropdown
                          value={searchValues.projectType}
                          placeholder='프로젝트 선택'
                          options={projectOptions}
                          isOpen={dropdownStates.projectType}
                          onClick={() => handleDropdownToggle('projectType')}
                          onSelect={value => handleDropdownSelect('projectType', value)}
                        />
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
              <div style={{ width: '128px' }}>
                <UIButton2 className='btn-secondary-blue' style={{ width: '100%' }} onClick={handleSearch}>
                  조회
                </UIButton2>
              </div>
            </UIGroup>
          </UIBox>
        </UIArticle>

        <UIArticle className='article-grid'>
          <div className='article-body'>
            <div className='mb-4 flex items-center gap-4'>
              <UIDataCnt count={filteredPods.length} prefix='총' unit='건' />
              <UITypography variant='body-1' className='secondary-neutral-700'>
                CPU : {totals.cpu}Core, Memory : {(totals.memory / (1024 * 1024 * 1024)).toFixed(1)}GiB
              </UITypography>
            </div>
            {filteredPods && filteredPods.length > 0 ? (
              <div className='chart-container grid-col2 mt-4' style={hasMultipleRows ? { rowGap: '32px' } : undefined}>
                {filteredPods.map((pod: any, index: number) => (
                  <div key={pod.podName} className={`chart-item flex-1${index % 2 === 0 ? ' !p-5' : ''}`}>
                    <div className='chart-header mb-4'>
                      <UIGroup gap={8} direction='column'>
                        <UITypography variant='title-3' className='text-sb'>
                          {pod.name} (Ver.{pod.pod_version})
                        </UITypography>
                        <UITypography variant='body-2' className='secondary-neutral-500'>
                          {pod.pod_name}
                        </UITypography>
                        <UITypography variant='body-1' className='secondary-neutral-700'>
                          {pod.lstPrjNm}
                        </UITypography>
                      </UIGroup>
                    </div>
                    <div className='flex chart-graph h-[210px] gap-x-20 justify-center !bg-transparent'>
                      <UICircleChart.Half
                        type='CPU'
                        value={parseFloat(parseFloat(pod.cpu_usage || 0).toFixed(1)) || 0}
                        total={parseFloat(parseFloat(pod.cpu_limit || 0).toFixed(1)) || 0}
                      />
                      <UICircleChart.Half
                        type='Memory'
                        value={parseFloat((parseFloat(pod.memory_usage || 0) / (1024 * 1024 * 1024)).toFixed(1)) || 0}
                        total={parseFloat((parseFloat(pod.memory_limit || 0) / (1024 * 1024 * 1024)).toFixed(1)) || 0}
                      />
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              /* 데이터 없을 경우 */
              <div className='flex-1 flex items-center justify-center h-[220px]'>
                <div className='flex flex-col justify-center items-center gap-3'>
                  <span className='ico-nodata'>
                    <UIIcon2 className='ic-system-80-default-nodata' />
                  </span>
                  <span className='text-body-1 secondary-neutral-500'>조회된 결과가 없습니다.</span>
                </div>
              </div>
            )}
          </div>
        </UIArticle>
      </UIPageBody>
    </section>
  );
};
