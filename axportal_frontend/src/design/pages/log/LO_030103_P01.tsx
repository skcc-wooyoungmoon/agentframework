import React, { useState } from 'react';

import { UITypography, UIButton2, UILabel, UIBox } from '@/components/UI/atoms';
import { UICode } from '@/components/UI/atoms/UICode';
import { UIInput, UIPageHeader, UIPageBody, UIArticle, UIUnitGroup, UIPopupBody, UIDataList, UIGroup, UIDropdown } from '@/components/UI/molecules';
import { UIAlarm, UIAlarmGroup, UITabs } from '@/components/UI/organisms';
import { useModal } from '@/stores/common/modal';

import { DesignLayout } from '../../components/DesignLayout';

export const LO_030103_P01: React.FC = () => {
  const {} = useModal();

  // 탭 상태
  const [activeTab, setActiveTab] = useState('Request');

  // date 타입
  const [dateValueStart, setDateValueStart] = useState('2025.06.29');
  const [dateValueEnd, setDateValueEnd] = useState('2025.06.30');

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

  // 팝업 핸들러
  const handleClose = () => {};

  // 탭 아이템 정의
  const tabOptions = [
    { id: 'Request', label: 'Request' },
    { id: 'Response', label: 'Response' },
    // { id: 'Tools', label: 'Tools' },
    // { id: 'MCPs', label: 'MCPs' },
  ];

  return (
    <>
      <DesignLayout>
        <section className='section-page'>
          {/* 페이지 헤더 */}
          <UIPageHeader title='모델사용 로그 조회' description='' />
          {/* [251111_퍼블수정] 타이틀명칭 변경 : 모델배포 로그 > 모델사용 로그 */}
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
                          <td colSpan={4}>
                            <UIUnitGroup gap={32} direction='row'>
                              <div style={{ width: '540px' }}>
                                <UIInput.Search
                                  value={searchValues.searchKeyword}
                                  placeholder='검색어 입력'
                                  onChange={e => setSearchValues(prev => ({ ...prev, searchKeyword: e.target.value }))}
                                />
                              </div>
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
                            <div style={{ width: '540px' }}>
                              <UIDropdown
                                value={searchValues.logType}
                                placeholder='로그 종류 선택'
                                options={[
                                  { value: '전체', label: '전체' },
                                  { value: '시스템 로그', label: '시스템 로그' },
                                  { value: '사용 로그', label: '사용 로그' },
                                  { value: '에러 로그', label: '에러 로그' },
                                ]}
                                isOpen={dropdownStates.logType}
                                onClick={() => handleDropdownToggle('logType')}
                                onSelect={value => handleDropdownSelect('logType', value)}
                              />
                            </div>
                          </td>
                          <th>
                            <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                              호출 종류
                            </UITypography>
                          </th>
                          <td>
                            <UIDropdown
                              value={searchValues.logType}
                              placeholder='로그 종류 선택'
                              options={[
                                { value: '전체', label: '전체' },
                                { value: 'request', label: 'request' },
                                { value: 'response', label: 'response' },
                              ]}
                              isOpen={dropdownStates.logType}
                              onClick={() => handleDropdownToggle('logType')}
                              onSelect={value => handleDropdownSelect('logType', value)}
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
          </UIPageBody>
        </section>
      </DesignLayout>

      {/* 사용로그 상세 */}
      <UIAlarm onClose={handleClose} title='사용로그 상세'>
        <UIAlarmGroup>
          <section className='section-popup-content' style={{ padding: '0' }}>
            <UIPopupBody>
              <UIArticle>
                <UIDataList
                  gap={12}
                  direction='column'
                  datalist={[
                    { dataName: '요청시간', dataValue: '2025.03.24 18:23:43' },
                    { dataName: '응답시간', dataValue: '2025.03.24 18:23:43' },
                    { dataName: '총 소요시간', dataValue: '340ms' },
                    {
                      dataName: '상태',
                      dataValue: (
                        <UILabel variant='badge' intent='complete'>
                          정상
                        </UILabel>
                      ),
                    },
                    { dataName: '토큰합계', dataValue: '1,040' },
                    { dataName: '거래 식별자', dataValue: '김신한' },
                  ]}
                >
                  {null}
                </UIDataList>
              </UIArticle>
              <UIArticle>
                <UITabs items={tabOptions} activeId={activeTab} onChange={setActiveTab} size='medium' variant='body-2' />
              </UIArticle>
              {/* 
              해당영역 삭제
              <UIArticle className='!mt-[24px]'>
                <UIGroup gap={8} direction={'column'}>
                  <UITextArea2 value={' MCP call contents'} style={{ pointerEvents: 'none' }} onChange={_e => {}} placeholder='내용이 표시됩니다...' />
                  <UITextArea2 value={' MCP call contents'} style={{ pointerEvents: 'none' }} onChange={_e => {}} placeholder='내용이 표시됩니다...' />
                  <UITextArea2 value={' Hello~'} style={{ pointerEvents: 'none' }} onChange={_e => {}} placeholder='내용이 표시됩니다...' />
                  <UIGroup gap={8} direction={'row'}>
                    <UIButton2 className='btn-subgray secondary-neutral-600' style={{ height: '24px', borderRadius: '4px', padding: '2px 6px', fontSize: '12px' }}>
                      max_tokens : 4090
                    </UIButton2>
                    <UIButton2 className='btn-subgray secondary-neutral-600' style={{ height: '24px', borderRadius: '4px', padding: '2px 6px', fontSize: '12px' }}>
                      temperature: 1
                    </UIButton2>
                    <UIButton2 className='btn-subgray secondary-neutral-600' style={{ height: '24px', borderRadius: '4px', padding: '2px 6px', fontSize: '12px' }}>
                      top_p: 1
                    </UIButton2>
                    <UIButton2 className='btn-subgray secondary-neutral-600' style={{ height: '24px', borderRadius: '4px', padding: '2px 6px', fontSize: '12px' }}>
                      입력 토큰 : 350
                    </UIButton2>
                  </UIGroup>
                </UIGroup>
              </UIArticle>
              */}

              <UIArticle>
                <UIUnitGroup gap={16} direction='column' align='center'>
                  <div className='flex justify-between w-full'>
                    {activeTab === 'response' ? (
                      <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                        JSON | DIctionary
                      </UITypography>
                    ) : (
                      <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                        JSON
                      </UITypography>
                    )}
                    <UIButton2 className='btn-option-outline'>로그 다운로드</UIButton2>
                  </div>
                  <div>
                    {/* 실제 에디트 코드 영역 */}
                    <UICode
                      value={`여기는 에디터 화면입니다. 테스트 test
    테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
     테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
        테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
             테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
                  테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test테스트 test입니다. 테스트 test
`}
                      language='python'
                      theme='dark'
                      width='100%'
                      minHeight='640px'
                      height='640px'
                      maxHeight='640px'
                      disabled={true}
                    />
                  </div>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupBody>
          </section>
        </UIAlarmGroup>
      </UIAlarm>
    </>
  );
};
