import React, { useState, useMemo } from 'react';

import { UITypography, UIButton2, UIDataCnt, UIPagination, UIBox, UILabel } from '@/components/UI/atoms';
import { UIInput, UIPageHeader, UIPageBody, UIArticle, UIUnitGroup, UIDropdown, UIGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { useModal } from '@/stores/common/modal';

import { DesignLayout } from '../../components/DesignLayout';

export const LO_030103: React.FC = () => {
  const {} = useModal();

  const datasetData = [
    {
      id: '1',
      timestamp: '2025.03.24 18:23:43',
      status: '정상',
      sessionId: '340ms',
      description:
        '  "request_id": "req_20250812_001", "input_text": "오늘 날씨 어때?", "processing_status": "completed", "processing_log": "[{\"step_name\":\"input_received\ \"timestamp\":\"2025-08-12T10:45:32.124Z\"},{\"step_name\":\"intent_detection\",\"result\":\"weather_query\",\"timestamp\":\"2025-08-12T10:45:32.150Z\"},{\"step_name\":\"api_call_to_weather_service\",\"response_time_ms\":120,\"timestamp\":\"2025-08-12T10:45:32.280Z\"},{\"step_name\":\"response_generation\",\"timestamp\":\"2025-08-12T10:45:32.300Z\"}]", "output_text": "서울은 오늘 맑고 기온은 25도입니다.", "error": null',
      callKeyName: '김신한',
    },
    {
      id: '2',
      timestamp: '2025.03.24 18:22:15',
      status: '정상',
      sessionId: '340ms',
      description:
        '  "request_id": "req_20250812_001", "input_text": "오늘 날씨 어때?", "processing_status": "completed", "processing_log": "[{\"step_name\":\"input_received\ \"timestamp\":\"2025-08-12T10:45:32.124Z\"},{\"step_name\":\"intent_detection\",\"result\":\"weather_query\",\"timestamp\":\"2025-08-12T10:45:32.150Z\"},{\"step_name\":\"api_call_to_weather_service\",\"response_time_ms\":120,\"timestamp\":\"2025-08-12T10:45:32.280Z\"},{\"step_name\":\"response_generation\",\"timestamp\":\"2025-08-12T10:45:32.300Z\"}]", "output_text": "서울은 오늘 맑고 기온은 25도입니다.", "error": null',
      callKeyName: '김신한',
    },
    {
      id: '3',
      timestamp: '2025.03.24 18:21:30',
      status: '실패',
      sessionId: '340ms',
      description:
        '  "request_id": "req_20250812_001", "input_text": "오늘 날씨 어때?", "processing_status": "completed", "processing_log": "[{\"step_name\":\"input_received\ \"timestamp\":\"2025-08-12T10:45:32.124Z\"},{\"step_name\":\"intent_detection\",\"result\":\"weather_query\",\"timestamp\":\"2025-08-12T10:45:32.150Z\"},{\"step_name\":\"api_call_to_weather_service\",\"response_time_ms\":120,\"timestamp\":\"2025-08-12T10:45:32.280Z\"},{\"step_name\":\"response_generation\",\"timestamp\":\"2025-08-12T10:45:32.300Z\"}]", "output_text": "서울은 오늘 맑고 기온은 25도입니다.", "error": null',
      callKeyName: '김신한',
    },
    {
      id: '4',
      timestamp: '2025.03.24 18:20:45',
      status: '정상',
      sessionId: '340ms',
      description: 'AI 모델 응답 완료 - 텍스트 분석 결과',
      callKeyName: '김신한',
    },
    {
      id: '5',
      timestamp: '2025.03.24 18:19:12',
      status: '실패',
      sessionId: '340ms',
      description: 'AI 모델 호출 오류 - 잘못된 요청 파라미터',
      callKeyName: '김신한',
    },
    {
      id: '6',
      timestamp: '2025.03.24 18:18:33',
      status: '정상',
      sessionId: '340ms',
      description: 'AI 모델 응답 완료 - 이미지 분석 결과',
      callKeyName: '김신한',
    },
  ];

  // 현재 페이지에 표시할 데이터
  const processedData = datasetData;

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
        headerName: '요청 일시',
        field: 'timestamp',
        width: 180,
      },
      {
        headerName: '상태',
        field: 'status',
        width: 120,
        cellRenderer: (params: any) => {
          const colorMap: { [key: string]: string } = {
            정상: 'complete',
            실패: 'error',
          };
          return (
            <div className='h-full flex items-center'>
              <UILabel variant='badge' intent={colorMap[params.value] as any}>
                {params.value}
              </UILabel>
            </div>
          );
        },
      },
      {
        headerName: '총 소요시간',
        field: 'sessionId',
        width: 120,
      },
      {
        headerName: '내용',
        field: 'description',
        flex: 1,
        cellClass: 'auto-height-cell',
        cellRenderer: (params: any) => {
          return (
            <div
              style={
                {
                  display: '-webkit-box',
                  WebkitLineClamp: 4,
                  WebkitBoxOrient: 'vertical',
                  overflow: 'hidden',
                  textOverflow: 'ellipsis',
                  whiteSpace: 'pre-wrap',
                  wordBreak: 'break-word',
                  lineHeight: '1.4',
                  margin: '14px 16px',
                } as React.CSSProperties
              }
            >
              {params.value}
            </div>
          );
        },
        autoHeight: true,
        cellStyle: {
          padding: 0,
        } as any,
      },
      {
        headerName: '거래 식별자',
        field: 'callKeyName',
        width: 120,
      },
    ],
    []
  );

  // date 타입
  const [dateValueStart, setDateValueStart] = useState('2025.06.29');
  const [dateValueEnd, setDateValueEnd] = useState('2025.06.30');

  // 페이지네이션 상태
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(6); // 페이지당 6개 항목 표시

  // 검색 상태
  const [searchValues, setSearchValues] = useState({
    searchKeyword: '',
    logType: '전체',
  });

  // 드롭다운 상태
  const [dropdownStates, setDropdownStates] = useState({
    logType: false,
  });

  const handleDropdownToggle = (key: string) => {
    setDropdownStates(prev => ({
      ...prev,
      [key]: !prev[key as keyof typeof prev],
    }));
  };

  const handleDropdownSelect = (key: string, value: string) => {
    setSearchValues(prev => ({ ...prev, [key]: value }));
    setDropdownStates(prev => ({ ...prev, [key]: false }));
  };

  const handleSearch = () => {};

  // 현재 페이지에 표시할 데이터 계산
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const currentData = processedData.slice(startIndex, endIndex);
  const totalPages = Math.ceil(processedData.length / itemsPerPage);

  // 탭 아이템 정의
  /*const tabItems = [
    { id: 'log_system', label: '시스템 로그' },
    { id: 'log_use', label: '사용 로그' },
  ];*/

  return (
    <DesignLayout>
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader title='에이전트사용 로그 조회' description='' />
        {/* [251111_퍼블수정] 타이틀명칭 변경 : 에이전트배포 로그 > 에이전트사용 로그 */}
        <UIPageBody>
          <UIArticle className='pb-4'>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                에이전트 배포 정보
              </UITypography>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  <colgroup>
                    <col style={{ width: '10%' }} />
                    <col style={{ width: '40%' }} />
                    <col style={{ width: '10%' }} />
                    <col style={{ width: '40%' }} />
                  </colgroup>
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          배포명
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          콜센터 응대 특화 모델
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          배포유형
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          기본
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          빌더
                        </UITypography>
                      </th>
                      <td>
                        <UIUnitGroup gap={16} direction='row' vAlign='center'>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            0509 신한 스마트콜봇 캔버스
                          </UITypography>
                          <UIButton2 className='btn-text-14-point' rightIcon={{ className: 'ic-system-12-arrow-right-blue', children: '' }}>
                            빌더 바로가기
                          </UIButton2>
                        </UIUnitGroup>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          설명
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          고객 상담 응대 문장 생성에 최적화된 대형 언어모델
                        </UITypography>
                      </td>
                    </tr>
                    {/* <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          입력 세이프티 필터
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          의료/건강 민감어, 정치적 민감어
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          출력 세이프티 필터
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          의료/건강 민감어, 정치적 민감어
                        </UITypography>
                      </td>
                    </tr> */}
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>
          {/* 검색 영역 */}
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
                          <UIUnitGroup gap={32} direction='row'>
                            <div className='flex-1'>
                              <UIInput.Search
                                value={searchValues.searchKeyword}
                                placeholder='내용 입력'
                                onChange={e => setSearchValues(prev => ({ ...prev, searchKeyword: e.target.value }))}
                              />
                            </div>
                          </UIUnitGroup>
                        </td>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            조회 기간
                          </UITypography>
                        </th>
                        <td>
                          <UIUnitGroup gap={32} direction='row'>
                            <div className='flex-1' style={{ zIndex: '10' }}>
                              <UIUnitGroup gap={8} direction='row' vAlign='center'>
                                <div className='flex-1'>
                                  <UIInput.Date
                                    value={dateValueStart}
                                    onChange={e => {
                                      setDateValueStart(e.target.value);
                                    }}
                                  />
                                </div>

                                <UITypography variant='body-1' className='secondary-neutral-p w-[11px] justify-center'>
                                  ~
                                </UITypography>

                                <div className='flex-1'>
                                  <UIInput.Date
                                    value={dateValueEnd}
                                    onChange={e => {
                                      setDateValueEnd(e.target.value);
                                    }}
                                  />
                                </div>
                              </UIUnitGroup>
                            </div>
                          </UIUnitGroup>
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
                            value={searchValues.logType}
                            placeholder='로그 종류 선택'
                            options={[
                              { value: '전체', label: '전체' },
                              { value: '정상', label: '정상' },
                              { value: '실패', label: '실패' },
                            ]}
                            isOpen={dropdownStates.logType}
                            onClick={() => handleDropdownToggle('logType')}
                            onSelect={value => handleDropdownSelect('logType', value)}
                          />
                        </td>
                        {/* 검색영역 리사이징 간격유지위해 th,td 삭제 X */}
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

          {/* 그리드 영역 */}
          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='w-full'>
                  <UIUnitGroup gap={16} direction='column'>
                    <div className='flex justify-between w-full items-center'>
                      <div className='flex-shrink-0'>
                        <div style={{ width: '168px', paddingRight: '8px' }}>
                          <UIDataCnt count={processedData.length} prefix='사용 로그 총' unit='건' />
                        </div>
                      </div>
                      {/* [251202_퍼블수정] 로그 전체 다운로드 버튼 삭제함 */}
                    </div>
                  </UIUnitGroup>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid type='multi-select' rowData={currentData} columnDefs={columnDefs} />
              </UIListContentBox.Body>
              <UIListContentBox.Footer className='ui-data-has-btn'>
                <UIButton2 className='btn-option-outlined' style={{ width: '65px' }}>
                  다운로드
                </UIButton2>
                <UIPagination currentPage={currentPage} totalPages={totalPages} onPageChange={setCurrentPage} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
