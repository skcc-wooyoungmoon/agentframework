import React, { useMemo, useState } from 'react';

import { UIButton2, UIDataCnt, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIInput } from '@/components/UI/molecules';
import { UIGroup, UIPopupBody, UIPopupFooter, UIPopupHeader, UIStepper, UIUnitGroup } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown/component';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { DesignLayout } from '../../components/DesignLayout';

export const AD_120301_P02: React.FC = () => {
  const [isPopupOpen] = useState(true); // 팝업이므로 항상 열려있음
  // const [searchText, setSearchText] = useState('');

  // 필터 상태
  const [hrStatusFilter, setHrStatusFilter] = useState('전체');
  const [isHrStatusOpen, setIsHrStatusOpen] = useState(false);
  const [value, setValue] = useState('전체');

  // search 타입
  const [searchValue, setSearchValue] = useState('');

  const handleClose = () => {
    // 팝업 닫기 동작 제거 (디자인 페이지이므로 항상 열려있음)
  };

  // UIGrid 컬럼 설정
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
        headerName: '하위 메뉴명',
        field: 'childMenuName' as any,
        width: 272,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '권한명',
        field: 'authorityName' as any,
        width: 272,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      // [251106_퍼블수정] : 그리드컬럼 속성 수정 - 말줄임처리
      {
        headerName: '상세 권한',
        field: 'detailedPermissions' as any,
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
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
    []
  );

  // UIGrid 로우 데이터
  const rowData = [
    {
      id: '1',
      childMenuName: '사용자 관리',
      authorityName: '권한명',
      detailedPermissions: '원천 데이터 목록 조회,원천 데이터 상세 조회',
    },
    {
      id: '2',
      childMenuName: '프로젝트 관리',
      authorityName: '권한명',
      detailedPermissions: '데이터세트 목록 조회, 데이터세트 상세 조회, 지식 목록 조회, 지식 상세 조회, 검색 성능평가 ',
    },
    {
      id: '3',
      childMenuName: '모델 관리 조회', // [251111_퍼블수정] 타이틀명칭 변경 : 모델 카탈로그 > 모델 관리
      authorityName: '권한명',
      detailedPermissions: '데이터세트 목록 조회, 데이터세트 상세 조회, 지식 목록 조회, 지식 상세 조회, 검색 성능평가 ',
    },
    {
      id: '4',
      parentMenuName: '데이터',
      childMenuName: '데이터세트 조회',
      authorityName: '권한명',
      detailedPermissions: '데이터세트 목록 조회, 데이터세트 상세 조회, 지식 목록 조회, 지식 상세 조회, 검색 성능평가 ',
    },
    {
      id: '5',
      parentMenuName: '데이터',
      childMenuName: '지식/학습 데이터 관리', // [251111_퍼블수정] 타이틀명칭 변경 : 데이터 카탈로그 > 지식/학습 데이터 관리
      authorityName: '권한명',
      detailedPermissions: '데이터세트 목록 조회, 데이터세트 상세 조회, 지식 목록 조회, 지식 상세 조회, 검색 성능평가 ',
    },
    {
      id: '6',
      parentMenuName: '모델',
      childMenuName: '모델 관리', // [251111_퍼블수정] 타이틀명칭 변경 : 모델 카탈로그 > 모델 관리
      authorityName: '권한명',
      detailedPermissions: '데이터세트 목록 조회, 데이터세트 상세 조회, 지식 목록 조회, 지식 상세 조회, 검색 성능평가 ',
    },
    {
      id: '7',
      parentMenuName: '모델',
      childMenuName: '파인튜닝',
      authorityName: '권한명',
      detailedPermissions: '데이터세트 목록 조회, 데이터세트 상세 조회, 지식 목록 조회, 지식 상세 조회, 검색 성능평가 ',
    },
    {
      id: '8',
      parentMenuName: '에이전트',
      childMenuName: '에이전트 조회',
      authorityName: '권한명',
      detailedPermissions: '데이터세트 목록 조회, 데이터세트 상세 조회, 지식 목록 조회, 지식 상세 조회, 검색 성능평가 ',
    },
    {
      id: '9',
      parentMenuName: '에이전트',
      childMenuName: '에이전트 도구',
      authorityName: '권한명',
      detailedPermissions: '데이터세트 목록 조회, 데이터세트 상세 조회, 지식 목록 조회, 지식 상세 조회, 검색 성능평가 ',
    },
    {
      id: '10',
      parentMenuName: '프롬프트',
      childMenuName: '프롬프트 관리',
      authorityName: '권한명',
      detailedPermissions: '데이터세트 목록 조회, 데이터세트 상세 조회, 지식 목록 조회, 지식 상세 조회, 검색 성능평가 ',
    },
  ];

  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'admin', label: '관리' }}
        initialSubMenu={{
          id: 'admin-roles',
          label: '',
          icon: 'ico-lnb-menu-20-admin-role',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              기본 정보
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              지식 만들기 진행 중...
            </UITypography>
          </div>
        </div>
      </DesignLayout>

      {/* DesignLayout 위에 높은 z-index로 뜨는 UILayerPopup */}
      <UILayerPopup
        isOpen={isPopupOpen}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            {/* 레이어 팝업 헤더 */}
            <UIPopupHeader title='새 역할 만들기' description='' position='left' />
            {/* 레이어 팝업 바디 */}
            <UIPopupBody>
              {/* 스테퍼 영역 */}
              <UIStepper
                items={[
                  { id: 'step1', step: 1, label: '기본 정보 입력' },
                  { id: 'step2', step: 2, label: '메뉴 진입 설정' },
                  { id: 'step3', step: 3, label: '권한 추가하기' },
                ]}
                currentStep={3}
                direction='vertical'
              />
            </UIPopupBody>
            {/* 레이어 팝업 바디 : [참고] 이 페이지에는 왼쪽 body 영역 없음. */}
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={false}>
                    만들기
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 */}
        {/* 콘텐츠 영역 */}
        <section className='section-popup-content'>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='권한 추가하기' description='원하는 권한을 선택 후 만들기 버튼을 눌러주세요.' position='right' />

          {/* 레이어 팝업 바디 */}
          <UIPopupBody>
            <UIArticle className='article-grid'>
              {/* 추가영역 */}
              <UIListContainer>
                <UIListContentBox.Header>
                  <UIUnitGroup gap={16} direction='column'>
                    <div className='flex justify-between w-full items-center'>
                      <div className='w-full'>
                        <UIGroup gap={8} direction='row' align='start'>
                          <div className='flex' style={{ alignItems: 'center', width: '102px', paddingRight: '12px' }}>
                            <UIDataCnt count={100} prefix='총' unit='건' />
                          </div>
                          {/* 상태 필터 */}
                          <div className='flex items-center gap-[12px]'>
                            <div className='flex items-center gap-2'>
                              <UITypography variant='body-1' className='secondary-neutral-900'>
                                하위 메뉴명
                              </UITypography>
                              <div className='w-[180px]'>
                                <UIDropdown
                                  value={hrStatusFilter}
                                  options={[
                                    { value: '전체', label: '전체' },
                                    { value: '재직', label: '재직' },
                                    { value: '퇴사', label: '퇴사' },
                                    { value: '휴직', label: '휴직' },
                                  ]}
                                  isOpen={isHrStatusOpen}
                                  onClick={() => setIsHrStatusOpen(!isHrStatusOpen)}
                                  onSelect={(value: string) => {
                                    setHrStatusFilter(value);
                                    setIsHrStatusOpen(false);
                                  }}
                                  height={40}
                                />
                              </div>
                            </div>
                          </div>

                          <div style={{ display: 'flex', marginLeft: 'auto', gap: '0 12px' }}>
                            {/* [251105_퍼블수정] width값 수정 */}
                            <div className='w-[160px]'>
                              <UIDropdown
                                value={String(value)}
                                options={[
                                  { value: '전체', label: '전체' },
                                  { value: '하위메뉴명', label: '하위메뉴명' },
                                  { value: '권한명', label: '권한명' },
                                  { value: '상세 권한', label: '상세 권한' },
                                ]}
                                onSelect={(value: string) => {
                                  setValue(value);
                                }}
                                onClick={() => {}}
                                height={40}
                                variant='dataGroup'
                              />
                            </div>
                            <div className='w-[360px]'>
                              {/* 검색 입력 */}
                              <UIInput.Search
                                value={searchValue}
                                onChange={e => {
                                  setSearchValue(e.target.value);
                                }}
                                placeholder='검색어 입력'
                              />
                            </div>
                          </div>
                        </UIGroup>
                      </div>
                    </div>
                  </UIUnitGroup>
                </UIListContentBox.Header>
                <UIListContentBox.Body>
                  <UIGrid type='multi-select' rowData={rowData} columnDefs={columnDefs} />
                </UIListContentBox.Body>
                <UIListContentBox.Footer>
                  <UIPagination currentPage={1} totalPages={5} onPageChange={() => {}} className='flex justify-center' />
                </UIListContentBox.Footer>
              </UIListContainer>
            </UIArticle>
          </UIPopupBody>
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray'>이전</UIButton2>
                {/* <UIButton2 className='btn-secondary-blue' disabled={true}>다음</UIButton2> */}
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </section>
      </UILayerPopup>
    </>
  );
};
