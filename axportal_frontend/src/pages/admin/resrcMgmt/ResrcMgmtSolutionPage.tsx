import { useState, useEffect, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { useSetAtom } from 'jotai';

import { UIGroup, UIDropdown, UIInput } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UITypography, UIButton2, UIBox, UILabel } from '@/components/UI/atoms';
import { UICircleChart } from '@/components/UI/molecules/chart';
import { useGetSolutionResources } from '@/services/admin/resrcMgmt';
import { selectedSolutionAtom } from '@/stores/admin/resrcMgmt';
import { UIIcon2 } from '@/components/UI/atoms/UIIcon2';

interface SearchValues {
  searchType: string;
  solutionName: string;
}

export const ResrcMgmtSolutionPage = () => {
  const navigate = useNavigate();
  const setSelectedSolution = useSetAtom(selectedSolutionAtom);

  const { data: solutionResources } = useGetSolutionResources(undefined, {
    refetchOnMount: 'always',
    staleTime: 0,
  });

  const [filteredSolutions, setFilteredSolutions] = useState<any[]>([]);
  const [isFiltered, setIsFiltered] = useState(false);

  const [searchValues, setSearchValues] = useState<SearchValues>({
    searchType: 'all',
    solutionName: 'val1',
  });

  const [searchValue1, setSearchValue1] = useState('');

  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    solutionName: false,
    status: false,
    publicRange: false,
  });

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

  const applyFilters = () => {
    if (!solutionResources?.solutionList) {
      setFilteredSolutions([]);
      return;
    }

    const solutions = solutionResources.solutionList;

    const filtered = solutions.filter((solution: any) => {
      const namespaceMatch = searchValue1 === '' || (solution.namespaces && solution.namespaces.some((ns: string) => ns.toLowerCase().includes(searchValue1.toLowerCase())));

      let matchesSolution = true;
      if (searchValues.searchType === 'all') {
        matchesSolution = true;
      } else {
        matchesSolution = solution.name === searchValues.searchType;
      }

      const cpuUsageRate = solution.cpu_limit > 0 ? (solution.cpu_usage / solution.cpu_limit) * 100 : 0;
      const memoryUsageRate = solution.memory_limit > 0 ? (solution.memory_usage / solution.memory_limit) * 100 : 0;
      const isOverloaded = cpuUsageRate > 100 || memoryUsageRate > 100;

      let matchesStatus = true;
      if (searchValues.solutionName === 'val2') {
        matchesStatus = !isOverloaded;
      } else if (searchValues.solutionName === 'val3') {
        matchesStatus = isOverloaded;
      }

      return namespaceMatch && matchesSolution && matchesStatus;
    });

    setFilteredSolutions(filtered);
    setIsFiltered(true);
  };

  const handleSearch = () => {
    applyFilters();
  };

  useEffect(() => {
    if (solutionResources?.solutionList && !isFiltered) {
      setFilteredSolutions(solutionResources.solutionList);
    }
  }, [solutionResources?.solutionList, isFiltered]);

  const solutionNameOptions = useMemo(() => {
    if (!solutionResources?.solutionList) {
      return [{ value: 'all', label: '전체' }];
    }

    const uniqueSolutions = Array.from(new Map(solutionResources.solutionList.map((solution: any) => [solution.name, { name: solution.name, id: solution.id }])).values());

    return [
      { value: 'all', label: '전체' },
      ...uniqueSolutions.map((solution: any) => ({
        value: solution.name,
        label: solution.name,
      })),
    ];
  }, [solutionResources?.solutionList]);

  return (
    <>
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
                          placeholder='네임스페이스명 입력'
                          onChange={e => {
                            setSearchValue1(e.target.value);
                          }}
                          onKeyDown={e => {
                            if (e.key === 'Enter') {
                              handleSearch();
                            }
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
                      <UIDropdown
                        value={searchValues.searchType}
                        placeholder='조회 조건 선택'
                        options={solutionNameOptions}
                        isOpen={dropdownStates.searchType}
                        onClick={() => handleDropdownToggle('searchType')}
                        onSelect={value => handleDropdownSelect('searchType', value)}
                      />
                    </td>
                  </tr>
                  <tr>
                    <th>
                      <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                        상태
                      </UITypography>
                    </th>
                    <td>
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
                    </td>
                    <th></th>
                    <td></td>
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

      <UIArticle>
        <div className='article-body'>
          {filteredSolutions && filteredSolutions.length > 0 ? (
            <>
              {Array.from({ length: Math.ceil(filteredSolutions.length / 2) }, (_, rowIndex) => (
                <div key={rowIndex} className='chart-container grid-col2 mt-4'>
                  {filteredSolutions.slice(rowIndex * 2, rowIndex * 2 + 2).map((solution: any, index: number) => {
                    const namespace = solution.namespaces && solution.namespaces.length > 0 ? solution.namespaces[0] : 'Unknown';

                    const cpuUsageRate = solution.cpu_limit > 0 ? (solution.cpu_usage / solution.cpu_limit) * 100 : 0;
                    const memoryUsageRate = solution.memory_limit > 0 ? (solution.memory_usage / solution.memory_limit) * 100 : 0;
                    const isOverloaded = cpuUsageRate > 100 || memoryUsageRate > 100;

                    return (
                      <div
                        key={`${solution.id}-${index}`}
                        className={`chart-item flex-1${index === 0 ? ' !p-5' : ''}`}
                        onClick={() => {
                          const solutionStatus = isOverloaded ? 'overloaded' : 'normal';

                          setSelectedSolution({
                            solutionName: solution.name,
                            namespace: namespace,
                            solutionStatus: solutionStatus,
                            solutionData: solution,
                          });

                          navigate('/admin/resrc-mgmt/solution-detail');
                        }}
                      >
                        <div className='chart-header flex !items-center mb-4'>
                          <UIGroup gap={8} direction='column' className='basis-[400px]'>
                            <UITypography variant='title-3' className='text-sb'>
                              {solution.name}
                            </UITypography>
                            <UITypography variant='body-1' className='secondary-neutral-700'>
                              {namespace}
                            </UITypography>
                          </UIGroup>
                          <UILabel variant='badge' intent={isOverloaded ? 'error' : 'complete'}>
                            {isOverloaded ? '과부하' : '정상'}
                          </UILabel>
                        </div>
                        <div className='flex chart-graph h-[210px] gap-x-20 justify-center !bg-transparent'>
                          <UICircleChart.Half type='CPU' 
                          value={parseFloat((solution.cpu_usage || 0).toFixed(1))} 
                          total={parseFloat((solution.cpu_limit || 0).toFixed(1))} />
                          <UICircleChart.Half
                            type='MemoryMB'
                            value={parseFloat(((solution.memory_usage || 0)).toFixed(1))}
                            total={parseFloat(((solution.memory_limit || 0)).toFixed(1))}
                          />
                        </div>
                      </div>
                    );
                  })}
                </div>
              ))}
            </>
          ) : (
            <div className='w-full flex items-center justify-center h-[115px] py-[146px] text-gray-500'>
                <div className='text-center'>
                  <UIIcon2 className='ic-system-80-default-nodata mb-3' />
                  <div className='text-base font-normal leading-6 text-[#7E889B]'>조회된 결과가 없습니다.</div>
                </div>
              </div>
          )}
        </div>
      </UIArticle>
    </>
  );
};
