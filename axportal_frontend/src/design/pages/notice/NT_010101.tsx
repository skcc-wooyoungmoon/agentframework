import React, { useMemo, useState } from 'react';

import { UIBox, UIButton2, UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGroup, UIUnitGroup, UIInput } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { useModal } from '@/stores/common/modal';

import { UIDropdown } from '../../../components/UI/molecules/dropdown/UIDropdown';
import { DesignLayout } from '../../components/DesignLayout';

export const NT_010101 = () => {
  const { openAlert } = useModal();

  // 드롭다운 상태 관리
  const [dropdownStates, setDropdownStates] = useState({
    dateType: false,
    searchType: false,
    condition: false,
    menu: false,
    pageSize: false,
  });

  // search 타입
  const [searchValue, setSearchValue] = useState('');

  // 각 드롭다운 값 상태
  const [searchTypeValue, setSearchTypeValue] = useState('최종 수정일시');
  const [conditionValue, setConditionValue] = useState('전체');
  const [menuValue, setMenuValue] = useState('전체');

  // 더보기 메뉴 설정
  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          label: '수정',
          action: 'modify',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `"${rowData.name}" 수정 팝업을 엽니다.`,
            });
          },
        },
        {
          label: '삭제',
          action: 'delete',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `"${rowData.name}" 삭제 화면으로 이동합니다.`,
            });
          },
        },
      ],
    }),
    [openAlert]
  );

  // 샘플 데이터 (rowData로 변수명 변경)
  const rowData = [
    {
      id: '1',
      title: '포털 서비스 점검 안내',
      type: '시스템',
      description: '2025년 3월 25일 02:00~06:00 포털 서비스 정기 점검이 있을 예정입니다.',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '2',
      title: '신규 AI 모델 출시 공지',
      type: '서비스',
      description: 'GPT-4 기반의 새로운 AI 모델이 출시되었습니다. 자세한 내용은 공지사항을 확인해주세요.',
      modifiedDate: '2025.03.24 15:20:15',
    },
    {
      id: '3',
      title: '보안 정책 업데이트',
      type: '보안',
      description: '사용자 보안 강화를 위한 새로운 정책이 적용됩니다. 비밀번호 규칙이 변경됩니다.',
      modifiedDate: '2025.03.23 14:45:22',
    },
    {
      id: '4',
      title: '데이터 백업 완료 안내',
      type: '시스템',
      description: '정기 데이터 백업이 성공적으로 완료되었습니다. 모든 사용자 데이터가 안전하게 보관되었습니다.',
      modifiedDate: '2025.03.22 09:30:00',
    },
    {
      id: '5',
      title: '사용자 교육 프로그램 안내',
      type: '교육',
      description: 'AI 포털 활용을 위한 사용자 교육 프로그램이 진행됩니다. 참여 신청은 내부 포털에서 가능합니다.',
      modifiedDate: '2025.03.21 16:15:30',
    },
    {
      id: '6',
      title: '모델 성능 최적화 완료',
      type: '서비스',
      description: 'AI 모델 응답 속도가 기존 대비 30% 향상되었습니다. 더욱 빠르고 정확한 서비스를 이용하실 수 있습니다.',
      modifiedDate: '2025.03.20 11:25:45',
    },
    {
      id: '7',
      title: '개인정보 처리방침 개정',
      type: '정책',
      description: '개인정보 보호법 개정에 따라 개인정보 처리방침이 개정되었습니다. 변경 사항을 확인해주세요.',
      modifiedDate: '2025.03.19 13:40:12',
    },
    {
      id: '8',
      title: '개인정보 처리방침 개정',
      type: '정책',
      description: '개인정보 보호법 개정에 따라 개인정보 처리방침이 개정되었습니다. 변경 사항을 확인해주세요.',
      modifiedDate: '2025.03.19 13:40:12',
    },
    {
      id: '9',
      title: '개인정보 처리방침 개정',
      type: '정책',
      description: '개인정보 보호법 개정에 따라 개인정보 처리방침이 개정되었습니다. 변경 사항을 확인해주세요.',
      modifiedDate: '2025.03.19 13:40:12',
    },
    {
      id: '10',
      title: '개인정보 처리방침 개정',
      type: '정책',
      description: '개인정보 보호법 개정에 따라 개인정보 처리방침이 개정되었습니다. 변경 사항을 확인해주세요.',
      modifiedDate: '2025.03.19 13:40:12',
    },
    {
      id: '11',
      title: '개인정보 처리방침 개정',
      type: '정책',
      description: '개인정보 보호법 개정에 따라 개인정보 처리방침이 개정되었습니다. 변경 사항을 확인해주세요.',
      modifiedDate: '2025.03.19 13:40:12',
    },
    {
      id: '12',
      title: '개인정보 처리방침 개정',
      type: '정책',
      description: '개인정보 보호법 개정에 따라 개인정보 처리방침이 개정되었습니다. 변경 사항을 확인해주세요.',
      modifiedDate: '2025.03.19 13:40:12',
    },
  ];

  // 드롭다운 핸들러
  const handleDropdownToggle = (key: keyof typeof dropdownStates) => {
    setDropdownStates(prev => ({
      ...Object.keys(prev).reduce((acc, k) => ({ ...acc, [k]: false }), {} as typeof prev),
      [key]: !prev[key],
    }));
  };

  // date 타입
  const [dateValueStart, setDateValueStart] = useState('2025.06.29');
  const [dateValueEnd, setDateValueEnd] = useState('2025.06.30');
  const [value, setValue] = useState('12개씩 보기');

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
        headerName: '제목',
        field: 'title',
        width: 480,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '유형',
        field: 'type',
        width: 240,
      },
      {
        headerName: '내용',
        field: 'description',
        minWidth: 560,
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
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate',
        width: 216,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    [rowData]
  );

  return (
    <DesignLayout
      initialMenu={{ id: 'admin', label: '관리' }}
      initialSubMenu={{
        id: 'admin-user',
        label: '사용자 관리',
        icon: 'ico-lnb-menu-20-admin-user',
      }}
    >
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='공지사항'
          description='플랫폼 내 게시된 공지사항을 확인할 수 있습니다.'
          actions={
            <>
              {/* [251222_퍼블수정]: btn-text-14-semibold-point > btn-text-18-semibold-point  (클래스명 변경 : 폰트 크기 수정함) */}
              <UIButton2 className='btn-text-18-semibold-point' leftIcon={{ className: 'ic-system-24-outline-blue-setting', children: '' }}>
                공지사항 관리
              </UIButton2>
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
                            조회 기간
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UIUnitGroup gap={32} direction='row'>
                            <div className='flex-1'>
                              <UIDropdown
                                value={searchTypeValue}
                                placeholder='조회 조건 선택'
                                options={[{ value: '최종 수정일시', label: '최종 수정일시' }]}
                                isOpen={dropdownStates.searchType}
                                onClick={() => handleDropdownToggle('searchType')}
                                onSelect={(value: string) => {
                                  setSearchTypeValue(value);
                                  setDropdownStates(prev => ({ ...prev, searchType: false }));
                                }}
                              />
                            </div>
                            <div className='flex-1'>
                              <UIUnitGroup gap={8} direction='row' vAlign='center'>
                                <div className='flex-1'>
                                  <UIInput.Date
                                    value={dateValueStart}
                                    onChange={e => {
                                      setDateValueStart(e.target.value);
                                    }}
                                    maxDate='2025.12.01'
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
                                    maxDate='2025.12.01'
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
                            조회 조건
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UIUnitGroup gap={32} direction='row'>
                            <div className='flex-1'>
                              <UIDropdown
                                value={conditionValue}
                                placeholder='조회 조건 선택'
                                options={[
                                  { value: '전체', label: '전체' },
                                  { value: '시스템', label: '시스템' },
                                  { value: '서비스', label: '서비스' },
                                  { value: '보안', label: '보안' },
                                  { value: '교육', label: '교육' },
                                  { value: '정책', label: '정책' },
                                ]}
                                isOpen={dropdownStates.condition}
                                onClick={() => handleDropdownToggle('condition')}
                                onSelect={(value: string) => {
                                  setConditionValue(value);
                                  setDropdownStates(prev => ({ ...prev, condition: false }));
                                }}
                              />
                            </div>
                            <div className='flex-1'>
                              <UIInput.Search
                                value={searchValue}
                                placeholder='검색어 입력'
                                onChange={e => {
                                  setSearchValue(e.target.value);
                                }}
                              />
                            </div>
                          </UIUnitGroup>
                        </td>
                      </tr>
                      <tr>
                        <th>
                          <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb'>
                            유형
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UIUnitGroup gap={32} direction='row'>
                            <div className='flex-1'>
                              <UIDropdown
                                value={menuValue}
                                placeholder='조회 조건 선택'
                                options={[
                                  { value: '전체', label: '전체' },
                                  { value: '공지사항', label: '공지사항' },
                                  { value: 'FAQ', label: 'FAQ' },
                                  { value: '업데이트', label: '업데이트' },
                                ]}
                                isOpen={dropdownStates.menu}
                                onClick={() => handleDropdownToggle('menu')}
                                onSelect={(value: string) => {
                                  setMenuValue(value);
                                  setDropdownStates(prev => ({ ...prev, menu: false }));
                                }}
                              />
                            </div>
                            <div className='flex-1'></div> {/* < div 삭제하지마세요. 가로 사이즈 맞춤 빈여백 채우기 */}
                          </UIUnitGroup>
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
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='w-full'>
                  <UIUnitGroup gap={16} direction='column'>
                    <div className='flex justify-between w-full items-center'>
                      <div className='flex-shrink-0'>
                        <div style={{ width: '168px', paddingRight: '8px' }}>
                          <UIDataCnt count={rowData.length} prefix='총' unit='건' />
                        </div>
                      </div>
                      <div className='flex-shrink-0 w-[160px]'>
                        <UIDropdown
                          value={String(value)}
                          options={[
                            { value: '1', label: '12개씩 보기' },
                            { value: '2', label: '36개씩 보기' },
                            { value: '3', label: '60개씩 보기' },
                          ]}
                          isOpen={dropdownStates.pageSize}
                          onClick={() => handleDropdownToggle('pageSize')}
                          onSelect={(value: string) => {
                            setValue(value);
                            setDropdownStates(prev => ({ ...prev, pageSize: false }));
                          }}
                          height={40}
                          variant='dataGroup'
                          width='w-40'
                        />
                      </div>
                    </div>
                  </UIUnitGroup>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid type='default' rowData={rowData} columnDefs={columnDefs} moreMenuConfig={moreMenuConfig} />
              </UIListContentBox.Body>
              {/* [참고] classname 관련
                  - 그리드 하단 (삭제) 버튼이 있는 경우 classname 지정 (예시) <UIListContentBox.Footer classname="ui-data-has-btn">
                  - 그리드 하단 (버튼) 없는 경우 classname 없이 (예시) <UIListContentBox.Footer>
                */}
              <UIListContentBox.Footer>
                <UIPagination currentPage={1} totalPages={10} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
