import { useState } from 'react';

interface FileItem {
  fileName: string;
  progress?: number;
  status?: 'error' | 'none';
}

import { UIBox, UIButton2, UIFileBox, UIIcon2, UILabel, UIProgress, UIRadio2, UITooltip, UITypography } from '@/components/UI/atoms';
import { UITextLabel } from '@/components/UI/atoms/UITextLabel';
import { UICardBox, UIDataList, UIFormField, UIGroup, UIList, UIUnitGroup } from '@/components/UI/molecules';
import { UIGridCard } from '@/components/UI/molecules/card/UIGridCard';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageFooter } from '@/components/UI/molecules/UIPageFooter';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';

import { DesignLayout } from '../components/DesignLayout';

export const SampleTemplateList = () => {
  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
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

  /** 파일 업로드  ---------*/
  const [files, setFiles] = useState<FileItem[]>([
    { fileName: 'test-file.pdf', progress: 50 },
    { fileName: 'document.docx', progress: 0, status: 'error' },
  ]);
  const handleFileRemove = (index: number) => {
    setFiles(prev => prev.filter((_, i) => i !== index));
  };
  /** 파일 업로드  ---------*/

  /** 그리드 카드리스트 */
  const [selectedValue1, setSelectedValue1] = useState<string>('');

  return (
    <DesignLayout>
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='페이지 샘플'
          description='이 페이지는 테이블 컨텐츠의 간격 가이드 입니다.'
          actions={
            <>
              <UIButton2 className='btn-text-14-semibold-point' leftIcon={{ className: 'ic-system-24-add', children: '' }}>
                데이터 만들기
              </UIButton2>
            </>
          }
        />

        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                인증
              </UITypography>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  <colgroup>
                    <col style={{ width: '128px' }} />
                    <col style={{ width: 'calc(50% - 128px)' }} />
                    <col style={{ width: '128px' }} />
                    <col style={{ width: 'calc(50% - 128px)' }} />
                  </colgroup>
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          이름
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          대출약관
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          데이터 유형
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          Markdown
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          생성일
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          2025.06.24 18:23:43
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          최종 수정일시
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          2025.06.24 18:23:43
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          카테고리
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          대출
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          유형
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          MD데이터
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>
          <UIArticle className='article-filter'>
            <UIBox className='box-filter'>
              <UIGroup gap={40} direction='row'>
                <div style={{ width: 'calc(100% - 168px)' }}>
                  <table className='tbl_type_b'>
                    <tbody>
                      <tr>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            조회 조건
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UIUnitGroup gap={32} direction='row'>
                            <div style={{ width: '540px' }}>
                              <UIDropdown
                                value={'이름'}
                                placeholder='조회 조건 선택'
                                options={[
                                  { value: '이름', label: '이름' },
                                  { value: '아이디', label: '아이디' },
                                  { value: '이메일', label: '이메일' },
                                  { value: '부서', label: '부서' },
                                ]}
                                isOpen={dropdownStates.searchType}
                                onClick={() => handleDropdownToggle('searchType')}
                                onSelect={value => handleDropdownSelect('searchType', value)}
                              />
                            </div>
                            <div style={{ width: 'calc(100% - 580px)' }}>
                              <div className='form-group'>
                                <div className='textfield'>
                                  <UIIcon2 className='ic-system-24-outline-search' />

                                  <input type='text' id='textfield1-1' title='텍스트 필드 타이틀' placeholder='검색어 입력' defaultValue='검색어 입력완료' />

                                  <button type='button' className='textfield-clear'>
                                    <UIIcon2 className='ic-system-24-clear' />
                                  </button>
                                </div>
                              </div>
                            </div>
                          </UIUnitGroup>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            검색
                          </UITypography>
                        </th>
                        <td>
                          <div className='form-group'>
                            <div className='textfield'>
                              <UIIcon2 className='ic-system-24-outline-search' />

                              <input type='text' id='textfield1-2' title='텍스트 필드 타이틀' placeholder='검색어 입력' defaultValue='검색어 입력완료' />

                              <button type='button' className='textfield-clear'>
                                <UIIcon2 className='ic-system-24-clear' />
                              </button>
                            </div>
                          </div>
                        </td>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            태그
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
                            이름
                          </UITypography>
                        </th>
                        <td>
                          <div className='form-group'>
                            <div className='textfield'>
                              <UIIcon2 className='ic-system-24-outline-search' />

                              <input type='text' id='textfield1-3' title='텍스트 필드 타이틀' placeholder='검색어 입력' defaultValue='검색어 입력완료' />

                              <button type='button' className='textfield-clear'>
                                <UIIcon2 className='ic-system-24-clear' />
                              </button>
                            </div>
                          </div>
                        </td>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            부서
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
                            인사상태
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
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            행번
                          </UITypography>
                        </th>
                        <td>
                          <div className='form-group'>
                            <div className='textfield'>
                              <UIIcon2 className='ic-system-24-outline-search' />

                              <input type='text' id='textfield1-4' title='텍스트 필드 타이틀' placeholder='검색어 입력' defaultValue='검색어 입력완료' />

                              <button type='button' className='textfield-clear'>
                                <UIIcon2 className='ic-system-24-clear' />
                              </button>
                            </div>
                          </div>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            역할명
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
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            그룹명
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
                            조회 기간
                          </UITypography>
                        </th>
                        <td>
                          <UIUnitGroup gap={8} direction='row' vAlign='center'>
                            <div style={{ width: 'calc(50% - 14px)' }}>
                              <div className='form-group'>
                                <div className='textfield'>
                                  <input type='text' id='textfield6-2' title='텍스트 필드 타이틀' placeholder='날짜 입력' defaultValue='2025.09.05' />

                                  <button type='button' className='textfield-clear'>
                                    <UIIcon2 className='ic-system-24-clear' />
                                  </button>

                                  <button type='button' className='textfield-date'>
                                    <UIIcon2 className='ic-system-24-calender' />
                                  </button>
                                </div>
                              </div>
                            </div>
                            <div style={{ width: '28px', textAlign: 'center' }}>
                              <UITypography variant='body-1' className='secondary-neutral-p'>
                                ~
                              </UITypography>
                            </div>
                            <div style={{ width: 'calc(50% - 14px)' }}>
                              <div className='form-group'>
                                <div className='textfield'>
                                  <input type='text' id='textfield6-2' title='텍스트 필드 타이틀' placeholder='날짜 입력' defaultValue='2025.09.05' />

                                  <button type='button' className='textfield-clear'>
                                    <UIIcon2 className='ic-system-24-clear' />
                                  </button>

                                  <button type='button' className='textfield-date'>
                                    <UIIcon2 className='ic-system-24-calender' />
                                  </button>
                                </div>
                              </div>
                            </div>
                          </UIUnitGroup>
                        </td>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            모델명
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
            <div className='article-header'>
              <div className='grid-header-left'>
                <UITypography variant='body-1' className='text-grid-header'>
                  구성 파일 총 <strong>99,999</strong>건
                </UITypography>
              </div>
              <div className='grid-header-right'>
                <div className='form-group'>
                  <div className='textfield'>
                    <UIIcon2 className='ic-system-24-outline-search' />

                    <input type='text' id='textfield5-2' title='텍스트 필드 타이틀' placeholder='검색어 입력' defaultValue='검색어 입력완료' />

                    <button type='button' className='textfield-clear'>
                      <UIIcon2 className='ic-system-24-clear' />
                    </button>
                  </div>
                </div>
              </div>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  <colgroup>
                    <col style={{ width: '128px' }} />
                    <col style={{ width: 'calc(50% - 128px)' }} />
                    <col style={{ width: '128px' }} />
                    <col style={{ width: 'calc(50% - 128px)' }} />
                  </colgroup>
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          이름
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          대출약관
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          데이터 유형
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          Markdown
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          생성일
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          2025.06.24 18:23:43
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          최종 수정일시
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          2025.06.24 18:23:43
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          카테고리
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          대출
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          유형
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          MD데이터
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                TextField + 버튼 조합
              </UITypography>
            </div>
            <div className='article-body'>
              <UIUnitGroup gap={8} direction='row'>
                <div>
                  <div className='form-group'>
                    <div className='textfield'>
                      <input type='text' id='textfield1-1' title='자연어 입력 입니다.' placeholder='자연어 입력' />
                      <button type='button' className='textfield-clear'>
                        <UIIcon2 className='ic-system-24-clear' />
                      </button>
                    </div>
                  </div>
                </div>
                <div>
                  <UIButton2 className='btn-secondary-outline !min-w-[64px] !font-semibold'>추가</UIButton2>
                </div>
              </UIUnitGroup>
            </div>
          </UIArticle>
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                데이터 리스트
              </UITypography>
            </div>
            <div className='article-body'>
              <UIDataList
                gap={12}
                direction='column'
                datalist={[
                  { dataName: '이름', dataValue: '홍길동 | 홍길동' },
                  { dataName: '유형', dataValue: '120,000개' },
                  { dataName: '생성 일시', dataValue: '2025.03.02 20:12:24' },
                  { dataName: '최종 수정일', dataValue: '2025.05.02 11:29:23' },
                  { dataName: '연락처', dataValue: <a href='tel:010-1234-5678'>000-0000-0000</a> },
                ]}
              >
                {null}
              </UIDataList>
            </div>
          </UIArticle>

          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                폼 필드
              </UITypography>
            </div>
            <div className='article-body'>
              <UIFormField gap={8} direction='column'>
                <div>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                    개인정보 포함 여부
                  </UITypography>
                  <UIUnitGroup gap={16} direction='row' align='start'>
                    <UIRadio2 name='basic1' value='option1' label='미포함' />
                    <UIRadio2 name='basic1' value='option2' label='포함' />
                  </UIUnitGroup>
                </div>
                <div>
                  <div className='form-group'>
                    <div className='textfield multi-line'>
                      <textarea placeholder='계약서, 약관 등 금융문서 요약 전용' maxLength={100} rows={4} className=''></textarea>
                    </div>
                  </div>
                </div>
              </UIFormField>
            </div>
          </UIArticle>
          <UIArticle>
            <UIGroup gap={12} direction='column' align='start'>
              <div className='inline-flex items-center'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                  개인정보 포함 여부
                </UITypography>
                <UITooltip
                  trigger='click'
                  position='bottom-start'
                  type='notice'
                  title='학습데이터 파일 양식'
                  items={[
                    '학습데이터 유형에 따른 칼럼값을 확인해 주세요.',
                    '지도학습 데이터 필수 칼럼값 : no, system, user, assistant',
                    '부가설명의 Reg, gray 타입입니다. (2depth)',
                  ]}
                  bulletType='dash'
                  showArrow={false}
                  showCloseButton={true}
                  className='tooltip-wrap ml-1'
                >
                  <UIButton2 className='btn-ic'>
                    <UIIcon2 className='ic-system-20-info' />
                  </UIButton2>
                </UITooltip>
              </div>
              <div>
                <UIUnitGroup gap={16} direction='row' align='start'>
                  <UIRadio2 name='basic1' value='option1' label='미포함' />
                  <UIRadio2 name='basic1' value='option2' label='포함' />
                </UIUnitGroup>
              </div>
            </UIGroup>
          </UIArticle>
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                파일업로드 샘플
              </UITypography>
            </div>
            <div className='article-body'>
              <UIGroup gap={16} direction='column'>
                <div>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                    파일 업로드
                  </UITypography>
                </div>
                <div>
                  <UIButton2 className='btn-tertiary-outline download'>파일 업로드</UIButton2>
                </div>
                <div>
                  {/* 파일 목록 */}
                  {files.length > 0 && (
                    <div className='space-y-3'>
                      {files.map((item, index) => (
                        <UIFileBox
                          key={index}
                          variant='default'
                          size='full'
                          fileName={item.fileName}
                          fileSize={99}
                          onFileRemove={() => handleFileRemove(index)}
                          className='w-full'
                          progress={item.progress}
                          status={item.status}
                        />
                      ))}
                    </div>
                  )}
                </div>
                <div>
                  <UIList
                    gap={4}
                    direction='column'
                    className='ui-list_bullet'
                    data={[
                      {
                        dataItem: (
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            암호화 설정된 파일은 업로드가 불가능하니 암호화 해제 후 파일 업로드를 해주세요.
                          </UITypography>
                        ),
                      },
                      {
                        dataItem: (
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            지원되는 파일 확장자 : .xls, .xlsx, .csv, .xlt, .xltx, .xlsm, .xlsb, .xltm
                          </UITypography>
                        ),
                      },
                    ]}
                  />
                </div>
              </UIGroup>
            </div>
          </UIArticle>
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                그리드 상태값 라벨
              </UITypography>
            </div>
            <div className='article-body'>
              <ul>
                <li>
                  <UILabel variant='badge' intent='progress'>
                    반입중
                  </UILabel>
                </li>
                <li>
                  <UILabel variant='badge' intent='error'>
                    이용불가
                  </UILabel>
                </li>
                <li>
                  <UILabel variant='badge' intent='progress'>
                    1차결제중
                  </UILabel>
                </li>
                <li>
                  <UILabel variant='badge' intent='progress'>
                    반입중
                  </UILabel>
                </li>
                <li>
                  <UILabel variant='badge' intent='progress'>
                    2차결제중
                  </UILabel>
                </li>
                <li>
                  <UILabel variant='badge' intent='complete'>
                    이용가능
                  </UILabel>
                </li>
                <li>
                  <UILabel variant='badge' intent='progress'>
                    업로드중
                  </UILabel>
                </li>
                <li>
                  <UILabel variant='badge' intent='choice'>
                    업로드완료
                  </UILabel>
                </li>
                <li>
                  <UILabel variant='badge' intent='choice'>
                    분할완료
                  </UILabel>
                </li>
                <li>
                  <UILabel variant='badge' intent='complete'>
                    임베딩완료
                  </UILabel>
                </li>
                <li>
                  <UILabel variant='badge' intent='error'>
                    실패
                  </UILabel>
                </li>
                <li>
                  <UILabel variant='badge' intent='stop'>
                    취소
                  </UILabel>
                </li>
                <li>
                  <UILabel variant='badge' intent='warning'>
                    중지
                  </UILabel>
                </li>
                <li>
                  <UILabel variant='badge' intent='progress'>
                    배포중
                  </UILabel>
                </li>
                \
                <li>
                  <UILabel variant='badge' intent='neutral'>
                    종료
                  </UILabel>
                </li>
                <li>
                  <UILabel variant='badge' intent='neutral'>
                    초기화
                  </UILabel>
                </li>
                <li>
                  <UILabel variant='badge' intent='progress'>
                    시작중
                  </UILabel>
                </li>
                <li>
                  <UILabel variant='badge' intent='warning'>
                    중지중
                  </UILabel>
                </li>
                <li>
                  <UILabel variant='badge' intent='progress'>
                    할당중
                  </UILabel>
                </li>
                <li>
                  <UILabel variant='badge' intent='choice'>
                    할당완료
                  </UILabel>
                </li>
                <li>
                  <UILabel variant='badge' intent='progress'>
                    학습중
                  </UILabel>
                </li>
                <li>
                  <UILabel variant='badge' intent='complete'>
                    학습완료
                  </UILabel>
                </li>
                <li>
                  <UILabel variant='badge' intent='progress'>
                    학습중
                  </UILabel>
                </li>
                <li>
                  <UILabel variant='badge' intent='complete'>
                    학습완료
                  </UILabel>
                </li>
              </ul>
            </div>
          </UIArticle>
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                일반 상태값 라벨
              </UITypography>
            </div>
            <div className='article-body'>
              <ul>
                <li>
                  <UILabel variant='badge' intent='neutral'>
                    중립
                  </UILabel>
                </li>
                <li>
                  <UILabel variant='badge' intent='progress'>
                    안전
                  </UILabel>
                </li>
                <li>
                  <UILabel variant='badge' intent='complete'>
                    주의
                  </UILabel>
                </li>
                <li>
                  <UILabel variant='badge' intent='stop'>
                    경고
                  </UILabel>
                </li>
                <li>
                  <UILabel variant='badge' intent='warning'>
                    경고2
                  </UILabel>
                </li>
                <li>
                  <UILabel variant='badge' intent='error'>
                    위험
                  </UILabel>
                </li>
                <li>
                  <UILabel variant='badge' intent='choice'>
                    특수
                  </UILabel>
                </li>
                <li>과부하 - 느낌표 아이콘 다름 추가</li>
                <li>
                  <UILabel variant='badge' intent='overload'>
                    과부하
                  </UILabel>
                </li>
              </ul>
            </div>
          </UIArticle>
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                그리드 진행률
              </UITypography>
            </div>
            <div className='article-body'>
              <ul>
                <li>
                  <UIProgress value={50} status='normal' showPercent={true} />
                </li>
                <li>
                  <UIProgress value={80} status='error' showPercent={true} />
                </li>
              </ul>
            </div>
          </UIArticle>
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                그리드 버전
              </UITypography>
            </div>
            <div className='article-body'>
              <ul>
                <li>
                  <UITextLabel intent='blue'>Release Ver.1</UITextLabel>
                </li>
                <li>
                  <UITextLabel intent='gray'>Lastest.1</UITextLabel>
                </li>
                <li>
                  <UITextLabel intent='tag'>문서요약</UITextLabel>
                </li>
              </ul>
            </div>
          </UIArticle>
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                카드
              </UITypography>
            </div>
            <div className='article-body'>
              <ul>
                <li>
                  <UIGridCard
                    progressValue={84}
                    title={'타이틀 영역 타이틀 영역 타이틀 영역 타이틀 영역 타이틀 영역'}
                    caption={'캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역'}
                    rows={[
                      {
                        label: '이름',
                        value: '예적금 상품 Q&A 세트',
                      },
                      {
                        label: '벡터DB',
                        value: 'AzureDocumentIntelligence',
                      },
                      {
                        label: '임베딩 모델',
                        value: 'AzureDocumentIntelligence',
                      },
                      {
                        label: '청킹방법',
                        value: 'GIP/text-embedding-3-large-new',
                      },
                    ]}
                    statusArea={
                      <>
                        <UIGroup gap={8} direction={'row'}>
                          <UITextLabel intent='blue'>Release Ver.1</UITextLabel>
                          <UITextLabel intent='gray'>Lastest.1</UITextLabel>
                        </UIGroup>
                      </>
                    }
                  />
                </li>
              </ul>
            </div>
          </UIArticle>
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                카드 리스트
              </UITypography>
            </div>

            {/*  ######################################################################

              [참고] 카드 리스트 컴포넌트 사용시
              flexType="none"
              flexType="shrink"
              flexType="grow"
            
              - 'none': flex: 0 0 auto (크기 고정)
              - 'shrink': flex: 0 1 auto (축소만 가능)
              - 'grow': flex: 1 1 auto (확장/축소 모두 가능)


              [참고] grid 박스 영역 가로사이즈 수정시 width='506px'
              <UIGridCard width='506px'></UIGridCard>

              ######################################################################
            */}
            <div className='article-body'>
              <UICardBox flexType='grow' className='w-[1551px]'>
                {' '}
                {/* [참고] flex타입 세팅가능  / 카드 전체 width 지정시 고정가능 className='w-[1551px]' */}
                <li>
                  {/* UIGridCard - widt='506px' 세팅 가능 [참고] */}
                  <UIGridCard
                    id='option1'
                    checkbox={{
                      checked: selectedValue1 === 'option1',
                      onChange: (checked, value) => {
                        if (checked) setSelectedValue1(value);
                      },
                    }}
                    progressValue={84}
                    title={'타이틀 영역 타이틀 영역 타이틀 영역 타이틀 영역 타이틀 영역'}
                    caption={'캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역'}
                    rows={[
                      {
                        label: '이름',
                        value: '예적금 상품 Q&A 세트',
                      },
                      {
                        label: '벡터DB',
                        value: 'AzureDocumentIntelligence',
                      },
                      {
                        label: '임베딩 모델',
                        value: 'AzureDocumentIntelligence',
                      },
                      {
                        label: '청킹방법',
                        value: 'GIP/text-embedding-3-large-new',
                      },
                    ]}
                    statusArea={
                      <>
                        <UIGroup gap={8} direction={'row'}>
                          <UITextLabel intent='blue'>Release Ver.1</UITextLabel>
                          <UITextLabel intent='gray'>Lastest.1</UITextLabel>
                        </UIGroup>
                      </>
                    }
                  />
                </li>
                <li>
                  <UIGridCard
                    id='option2'
                    checkbox={
                      {
                        checked: selectedValue1 === 'option2',
                        onChange: (checked, value) => {
                          if (checked) setSelectedValue1(value);
                        },
                      }
                      // <>
                      //   <UICheckbox2
                      //     name='basic1'
                      //     value='option1'
                      //     className='box'
                      //     checked={selectedValue1 === 'option1'}
                      //     onChange={(checked, value) => {
                      //       if (checked) setSelectedValue1(value);
                      //     }}
                      //   />
                      // </>
                    }
                    progressValue={84}
                    title={'타이틀 영역 타이틀 영역 타이틀 영역 타이틀 영역 타이틀 영역'}
                    caption={'캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역'}
                    rows={[
                      {
                        label: '이름',
                        value: '예적금 상품 Q&A 세트',
                      },
                      {
                        label: '벡터DB',
                        value: 'AzureDocumentIntelligence',
                      },
                      {
                        label: '임베딩 모델',
                        value: 'AzureDocumentIntelligence',
                      },
                      {
                        label: '청킹방법',
                        value: 'GIP/text-embedding-3-large-new',
                      },
                    ]}
                    statusArea={
                      <>
                        <UIGroup gap={8} direction={'row'}>
                          <UITextLabel intent='blue'>Release Ver.1</UITextLabel>
                          <UITextLabel intent='gray'>Lastest.1</UITextLabel>
                        </UIGroup>
                      </>
                    }
                  />
                </li>
                <li>
                  <UIGridCard
                    id='option3'
                    checkbox={
                      {
                        checked: selectedValue1 === 'option3',
                        onChange: (checked, value) => {
                          if (checked) setSelectedValue1(value);
                        },
                      }
                      // <>
                      //   <UICheckbox2
                      //     name='basic1'
                      //     value='option1'
                      //     className='box'
                      //     checked={selectedValue1 === 'option1'}
                      //     onChange={(checked, value) => {
                      //       if (checked) setSelectedValue1(value);
                      //     }}
                      //   />
                      // </>
                    }
                    progressValue={84}
                    title={'타이틀 영역 타이틀 영역 타이틀 영역 타이틀 영역 타이틀 영역'}
                    caption={'캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역'}
                    rows={[
                      {
                        label: '이름',
                        value: '예적금 상품 Q&A 세트',
                      },
                      {
                        label: '벡터DB',
                        value: 'AzureDocumentIntelligence',
                      },
                      {
                        label: '임베딩 모델',
                        value: 'AzureDocumentIntelligence',
                      },
                      {
                        label: '청킹방법',
                        value: 'GIP/text-embedding-3-large-new',
                      },
                    ]}
                    statusArea={
                      <>
                        <UIGroup gap={8} direction={'row'}>
                          <UITextLabel intent='blue'>Release Ver.1</UITextLabel>
                          <UITextLabel intent='gray'>Lastest.1</UITextLabel>
                        </UIGroup>
                      </>
                    }
                  />
                </li>
                <li>
                  <UIGridCard
                    id='option4'
                    checkbox={
                      {
                        checked: selectedValue1 === 'option4',
                        onChange: (checked, value) => {
                          if (checked) setSelectedValue1(value);
                        },
                      }
                      // <>
                      //   <UICheckbox2
                      //     name='basic1'
                      //     value='option1'
                      //     className='box'
                      //     checked={selectedValue1 === 'option1'}
                      //     onChange={(checked, value) => {
                      //       if (checked) setSelectedValue1(value);
                      //     }}
                      //   />
                      // </>
                    }
                    progressValue={84}
                    title={'타이틀 영역 타이틀 영역 타이틀 영역 타이틀 영역 타이틀 영역'}
                    caption={'캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역'}
                    rows={[
                      {
                        label: '이름',
                        value: '예적금 상품 Q&A 세트',
                      },
                      {
                        label: '벡터DB',
                        value: 'AzureDocumentIntelligence',
                      },
                      {
                        label: '임베딩 모델',
                        value: 'AzureDocumentIntelligence',
                      },
                      {
                        label: '청킹방법',
                        value: 'GIP/text-embedding-3-large-new',
                      },
                    ]}
                    statusArea={
                      <>
                        <UIGroup gap={8} direction={'row'}>
                          <UITextLabel intent='blue'>Release Ver.1</UITextLabel>
                          <UITextLabel intent='gray'>Lastest.1</UITextLabel>
                        </UIGroup>
                      </>
                    }
                  />
                </li>
                <li>
                  <UIGridCard
                    id='option5'
                    checkbox={
                      {
                        checked: selectedValue1 === 'option5',
                        onChange: (checked, value) => {
                          if (checked) setSelectedValue1(value);
                        },
                      }
                      // <>
                      //   <UICheckbox2
                      //     name='basic1'
                      //     value='option1'
                      //     className='box'
                      //     checked={selectedValue1 === 'option1'}
                      //     onChange={(checked, value) => {
                      //       if (checked) setSelectedValue1(value);
                      //     }}
                      //   />
                      // </>
                    }
                    progressValue={84}
                    title={'타이틀 영역 타이틀 영역 타이틀 영역 타이틀 영역 타이틀 영역'}
                    caption={'캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역'}
                    rows={[
                      {
                        label: '이름',
                        value: '예적금 상품 Q&A 세트',
                      },
                      {
                        label: '벡터DB',
                        value: 'AzureDocumentIntelligence',
                      },
                      {
                        label: '임베딩 모델',
                        value: 'AzureDocumentIntelligence',
                      },
                      {
                        label: '청킹방법',
                        value: 'GIP/text-embedding-3-large-new',
                      },
                    ]}
                    statusArea={
                      <>
                        <UIGroup gap={8} direction={'row'}>
                          <UITextLabel intent='blue'>Release Ver.1</UITextLabel>
                          <UITextLabel intent='gray'>Lastest.1</UITextLabel>
                        </UIGroup>
                      </>
                    }
                  />
                </li>
                <li>
                  <UIGridCard
                    id='option6'
                    checkbox={
                      {
                        checked: selectedValue1 === 'option6',
                        onChange: (checked, value) => {
                          if (checked) setSelectedValue1(value);
                        },
                      }
                      // <>
                      //   <UICheckbox2
                      //     name='basic1'
                      //     value='option1'
                      //     className='box'
                      //     checked={selectedValue1 === 'option1'}
                      //     onChange={(checked, value) => {
                      //       if (checked) setSelectedValue1(value);
                      //     }}
                      //   />
                      // </>
                    }
                    progressValue={84}
                    title={'타이틀 영역 타이틀 영역 타이틀 영역 타이틀 영역 타이틀 영역'}
                    caption={'캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역 캡션 영역'}
                    rows={[
                      {
                        label: '이름',
                        value: '예적금 상품 Q&A 세트',
                      },
                      {
                        label: '벡터DB',
                        value: 'AzureDocumentIntelligence',
                      },
                      {
                        label: '임베딩 모델',
                        value: 'AzureDocumentIntelligence',
                      },
                      {
                        label: '청킹방법',
                        value: 'GIP/text-embedding-3-large-new',
                      },
                    ]}
                    statusArea={
                      <>
                        <UIGroup gap={8} direction={'row'}>
                          <UITextLabel intent='blue'>Release Ver.1</UITextLabel>
                          <UITextLabel intent='gray'>Lastest.1</UITextLabel>
                        </UIGroup>
                      </>
                    }
                  />
                </li>
              </UICardBox>
            </div>
          </UIArticle>
          <UIArticle>
            <UIUnitGroup gap={4} direction='row'>
              <UIIcon2 name='ico-logo-24-google' />
              <UITypography variant='body-2' className='secondary-neutral-600'>
                Google
              </UITypography>
            </UIUnitGroup>
          </UIArticle>
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                안내 문구 박스
              </UITypography>
            </div>
            <div className='article-body'>
              <div className='box-fill'></div>
            </div>
          </UIArticle>
          <UIArticle className='article-grid'>
            <div className='article-header'>
              <div className='grid-header-left'>
                <UIGroup gap={8} direction='row' vAlign={'center'}>
                  <div style={{ width: '140px' }}>
                    <UITypography variant='body-1' className='text-grid-header'>
                      총 <strong>12</strong>건
                    </UITypography>
                  </div>
                  <div>
                    <UIFormField gap={8} direction='row' vAlign={'center'}>
                      <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb'>
                        구분
                      </UITypography>
                      <div>
                        <div className='form-group'>
                          <UIDropdown
                            value={'이름'}
                            placeholder='조회 조건 선택'
                            options={[
                              { value: '이름', label: '이름' },
                              { value: '아이디', label: '아이디' },
                              { value: '이메일', label: '이메일' },
                              { value: '부서', label: '부서' },
                            ]}
                            isOpen={dropdownStates.searchType}
                            onClick={() => handleDropdownToggle('searchType')}
                            onSelect={value => handleDropdownSelect('searchType', value)}
                          />
                        </div>
                      </div>
                    </UIFormField>
                  </div>
                </UIGroup>
              </div>
              <div className='grid-header-right'>
                <div className='form-group'>
                  <div className='textfield'>
                    <UIIcon2 className='ic-system-24-outline-search' />

                    <input type='text' id='textfield5-2' title='텍스트 필드 타이틀' placeholder='검색어 입력' defaultValue='검색어 입력완료' />

                    <button type='button' className='textfield-clear'>
                      <UIIcon2 className='ic-system-24-clear' />
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </UIArticle>
        </UIPageBody>

        {/* 페이지 footer */}
        <UIPageFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='center'>
              <UIButton2 className='btn-primary-gray'>취소</UIButton2>
              <UIButton2 className='btn-primary-blue'>확인</UIButton2>
            </UIUnitGroup>
          </UIArticle>
        </UIPageFooter>
      </section>
    </DesignLayout>
  );
};
