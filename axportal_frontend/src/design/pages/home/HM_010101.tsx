import { UIButton2, UIIcon2, UITooltip, UITypography } from '@/components/UI/atoms';
import { UITextLabel } from '@/components/UI/atoms/UITextLabel';
import { UIGroup, UIPageHeader, UIProfileBadgeGroup, UIUnitGroup } from '@/components/UI/molecules';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { DesignLayout } from '../../components/DesignLayout';

export const HM_010101 = () => {
  // const [selectedResourceTab, setSelectedResourceTab] = useState('finetuning');

  // 환경 상태 (true: 운영망, false: 개발망)
  const isProd = false;

  // UIButton2 클릭 핸들러
  const handleLinkEvent = () => {};

  // 그룹 참여자 프로필 데이터
  const profileUsers = [
    { id: '1', name: '신한', fullName: '김신한', bgColor: '#005DF9', isAdmin: true, department: '부서명최대열두글자노출(나)' }, //  [251223_퍼블수정] 관리자 사용자 경우 isAdmin 속성 추가 / department 부서명 추가
    { id: '2', name: '길동', fullName: '홍길동', bgColor: '#576072', isAdmin: true, department: '슈퍼SOL플랫폼부' }, //  [251223_퍼블수정] 관리자 사용자 경우 isAdmin 속성 추가 / department 부서명 추가
    { id: '3', name: '수민', fullName: '이수민', bgColor: '#5F81DB', department: '부서명 최대열두글자 노출됨' },
    { id: '4', name: '지훈', fullName: '박지훈', bgColor: '#9B59B6', department: '개발1팀' },
    { id: '5', name: '민호', fullName: '정민호', bgColor: '#F39C12', department: '기획팀' },
    { id: '6', name: '가나', fullName: '가나다라', bgColor: '#F06F35', department: '운영팀' },
    { id: '7', name: '테스', fullName: '테스트', bgColor: '#099990', department: 'QA팀' },
    { id: '8', name: '마스', fullName: '마스터', bgColor: '#0047E7', department: '관리팀' },
  ];

  return (
    <DesignLayout
      initialMenu={{ id: 'home', label: '홈' }}
      initialSubMenu={{
        id: 'home-dashboard',
        label: '대시보드',
        icon: 'ico-lnb-menu-20-home',
      }}
    >
      <section className='section-page'>
        <UIPageHeader
          title='대시보드'
          // [251211_퍼블수정] : 문구 수정
          description={[
            '현재 선택된 프로젝트에서의 진행 중인 작업과 주요 정보를 한눈에 볼 수 있습니다.',
            '선택된 프로젝트 기준으로 데이터, 모델, 에이전트의 최근 작업 현황을 확인해 보세요.',
          ]}
        />

        {/* 페이지 바디 */}
        <UIPageBody>
          {/* 상단 영역 - 그룹 정보 */}
          <div className='flex items-center justify-between mb-3'>
            <div className='flex items-center gap-3'>
              <UITypography variant='title-2' className='secondary-neutral-900'>
                대출 상품 분석 (<span>123</span>)
              </UITypography>
              <UITypography variant='title-4' className='secondary-neutral-600'>
                프로젝트
              </UITypography>
            </div>
            {/* 그룹 참여자 영역 */}
            <div className='flex items-center gap-4'>
              {/* [251223_퍼블수정] : 프로젝트 참여자 안내 툴팁 추가 S */}
              <UIUnitGroup gap={4} direction='row' vAlign='center'>
                <UITypography variant='body-1' className='secondary-neutral-700'>
                  프로젝트 참여자
                </UITypography>
                <UITooltip
                  trigger='click'
                  position='bottom-end'
                  type='notice'
                  title='프로젝트 참여자 안내'
                  items={['선택한 프로젝트에 참여 중인 사용자 목록입니다.', '빨간색 점이 표기된 사용자는 프로젝트 관리자로, 프로젝트 전반을 운영합니다.']}
                  bulletType='dash'
                  showArrow={false}
                  showCloseButton={true}
                  className='tooltip-wrap ml-1'
                >
                  <UIButton2 className='btn-text-only-16 p-0'>
                    <UIIcon2 className='ic-system-20-info' />
                  </UIButton2>
                </UITooltip>
              </UIUnitGroup>
              <div className='flex-shrink-0'>
                <UIProfileBadgeGroup users={profileUsers} showDropdown={true} maxDropdownDisplay={5} />
              </div>
              {/* [251223_퍼블수정] : 프로젝트 참여자 안내 툴팁 추가 E */}
            </div>
          </div>

          {/* ########################################## [참고] 대시보드 레이아웃 수정  ########################################## */}

          {/* 레이이아웃 Top */}
          <div className='dash-top box-group flex gap-6'>
            <div className={'box-group-item ' + (isProd ? 'box-main-prod' : 'box-main-dev')}>
              {/* [260107_퍼블수정] :
                - box-fixed 클래스는 꼭 삭제 !
                - 개발망일경우(class) : box-main-dev
                - 운영망일경우(class) : box-main-prod
              */}
              {/* 게발방 */}
              {!isProd && (
                <div className='item-header p-1'>
                  <div className='left'>
                    <UIUnitGroup gap={16} direction='column' vAlign='start'>
                      <UITypography variant='title-2' className='secondary-neutral-900 text-sb'>
                        프로젝트를 생성하거나 기존 프로젝트에 참여해보세요.
                      </UITypography>
                      <UIUnitGroup gap={16} direction='row' vAlign='start'>
                        <UIButton2 className='btn-text-16' rightIcon={{ className: 'ic-system-12-arrow-right-black', children: '' }}>
                          프로젝특 생성
                        </UIButton2>
                        <UIButton2 className='btn-text-16' rightIcon={{ className: 'ic-system-12-arrow-right-black', children: '' }}>
                          프로젝특 참여
                        </UIButton2>
                      </UIUnitGroup>
                    </UIUnitGroup>
                  </div>
                </div>
              )}
              {/* 운영망 */}
              {isProd && (
                <div className='item-header p-1'>
                  <div className='left'>
                    <UIUnitGroup gap={16} direction='column' vAlign='start'>
                      <UITypography variant='title-2' className='secondary-neutral-900 text-sb'>
                        배포한 에이전트와 모델의 사용로그를 조회해보세요.
                      </UITypography>
                      <UIUnitGroup gap={16} direction='row' vAlign='start'>
                        <UIButton2 className='btn-text-16' rightIcon={{ className: 'ic-system-12-arrow-right-black', children: '' }}>
                          사용 로그 조회
                        </UIButton2>
                      </UIUnitGroup>
                    </UIUnitGroup>
                  </div>
                </div>
              )}
            </div>
            <div className='box-group-item'>
              <div className='item-header'>
                <div className='left flex items-center'>
                  <UIIcon2 className='ic-system-24-outline-black-alarm mr-1' />
                  <UITypography variant='title-4' className='secondary-neutral-900'>
                    공지사항
                  </UITypography>
                </div>
              </div>
              <div className='item-cont flex mt-4'>
                <div className='notice-container'>
                  <div className='notice-item'>
                    <UITypography variant='body-2' className='label secondary-neutral-500'>
                      시스템 점검
                    </UITypography>
                    <UIButton2 onClick={handleLinkEvent} className='cursor-pointer'>
                      <UITypography variant='title-4' className='title secondary-neutral-700 text-sb'>
                        8월 5일(월) 정기 시스템 점검 예정 안내8월 5일(월) 정기 시스템 점검 예정 안내8월 5일(월) 정기 시스템 점검 예정 안내8월 5일(월) 정기 시스템 점검 예정 안내8월
                        5일(월) 정기 시스템 점검 예정 안내
                      </UITypography>
                    </UIButton2>
                    <UITypography variant='body-2' className='datetime secondary-neutral-600'>
                      방금
                    </UITypography>
                  </div>
                  <div className='notice-item'>
                    <UITypography variant='body-2' className='label secondary-neutral-500'>
                      시스템 점검
                    </UITypography>
                    <UIButton2 onClick={handleLinkEvent} className='cursor-pointer'>
                      <UITypography variant='title-4' className='title secondary-neutral-700 text-sb'>
                        긴급 서버 점검으로 인한 일시 중단긴급 서버 점검으로 인한 일시 중단긴급 서버 점검으로 인한 일시 중단긴급 서버 점검으로 인한 일시 중단긴급 서버 점검으로 인한
                        일시 중단
                      </UITypography>
                    </UIButton2>
                    <UITypography variant='body-2' className='datetime secondary-neutral-600'>
                      5분 전
                    </UITypography>
                  </div>
                </div>
                {/* 데이터 없을시.. */}
                {/* <div className='no-date'>
                  <UIImage src='/assets/images/system/ico-system-80-default-nodata.svg' alt='No data' className='w-20 h-20' />
                  <UITypography variant='body-1' className='text-gray-500'>등록된 공지사항이 없습니다.</UITypography>
                </div> */}
              </div>
            </div>
          </div>

          <div className='card-banner mt-6'>
            <div className='card-banner-item cursor-pointer'>
              <UIUnitGroup gap={8} direction='column' align='start'>
                <UITypography variant='body-1' className='secondary-neutral-600'>
                  생성형 AI 플랫폼 사용 방법을 한눈에 확인하세요.
                </UITypography>
                <UITypography variant='title-3' className='secondary-neutral-800 text-sb'>
                  사용자 가이드
                </UITypography>
              </UIUnitGroup>
              <UIIcon2 className='ic-system-48-ai' />
            </div>
            {/* 개발망 */}
            {!isProd && (
              <>
                <div className='card-banner-item cursor-pointer'>
                  <UIUnitGroup gap={8} direction='column' align='start'>
                    <UITypography variant='body-1' className='secondary-neutral-600'>
                      나만의 AI 에에이전트를 생성해 보세요.
                    </UITypography>
                    <UITypography variant='title-3' className='secondary-neutral-800 text-sb'>
                      에이전트 빌더
                    </UITypography>
                  </UIUnitGroup>
                  <UIIcon2 className='ic-system-48-builder' />
                </div>
                <div className='card-banner-item cursor-pointer'>
                  <UIUnitGroup gap={8} direction='column' align='start'>
                    <UITypography variant='body-1' className='secondary-neutral-600'>
                      모델을 직접 선택하고 자유롭게 실험해보세요.
                    </UITypography>
                    <UITypography variant='title-3' className='secondary-neutral-800 text-sb'>
                      플레이그라운드
                    </UITypography>
                  </UIUnitGroup>
                  <UIIcon2 className='ic-system-48-msg' />
                </div>
              </>
            )}

            {/* 운영망 */}
            {isProd && (
              <>
                <div className='card-banner-item cursor-pointer'>
                  <UIUnitGroup gap={8} direction='column' align='start'>
                    <UITypography variant='body-1' className='secondary-neutral-600'>
                      모델을 직접 선택하고 자유롭게 실험해보세요.
                    </UITypography>
                    <UITypography variant='title-3' className='secondary-neutral-800 text-sb'>
                      플레이그라운드
                    </UITypography>
                  </UIUnitGroup>
                  <UIIcon2 className='ic-system-48-msg' />
                </div>
                <div className='card-banner-item cursor-pointer'>
                  <UIUnitGroup gap={8} direction='column' align='start'>
                    <UITypography variant='body-1' className='secondary-neutral-600'>
                      발급한 API Key를 확인하고 안전하게 관리해보세요.
                    </UITypography>
                    <UITypography variant='title-3' className='secondary-neutral-800 text-sb'>
                      API Key 조회
                    </UITypography>
                  </UIUnitGroup>
                  <UIIcon2 className='ic-system-48-key' />
                </div>
              </>
            )}
          </div>

          {/* 레이이아웃 Bottom */}
          <div className='box-group flex gap-6'>
            <div className='box-group-item'>
              <div className='item-header'>
                <div className='left'>
                  <UITypography variant='title-4' className='secondary-neutral-900'>
                    사용 가능한 지식 데이터
                  </UITypography>
                </div>
              </div>
              <div className='item-cont mt-4'>
                <ul className='recent-data-list'>
                  <li className='item'>
                    <div className='item-left'>
                      <UIGroup direction='column' gap={4}>
                        <UIButton2 onClick={handleLinkEvent} className='cursor-pointer'>
                          <UITypography variant='title-4' className='secondary-neutral-700 text-sb'>
                            예적금 상품 Q&A 세트예적금 상품 Q&A 세트예적금 상품 Q&A 세트예적금 상품 Q&A 세트예적금 상품 Q&A 세트예적금 상품 Q&A 세트
                          </UITypography>
                        </UIButton2>
                        <UIGroup direction='row' gap={4} vAlign='center' className='justify-between'>
                          <UITypography variant='body-2' className='text-gray-500'>
                            데이터세트
                          </UITypography>
                          <UITypography variant='body-2' className='text-gray-500'>
                            2026.01.08 18:23
                          </UITypography>
                        </UIGroup>
                      </UIGroup>
                    </div>
                  </li>
                  <li className='item'>
                    <div className='item-left'>
                      <UIGroup direction='column' gap={4}>
                        <UIButton2 onClick={handleLinkEvent} className='cursor-pointer'>
                          <UITypography variant='title-4' className='secondary-neutral-700 text-sb'>
                            신한은행 예적금약관
                          </UITypography>
                        </UIButton2>
                        <UIGroup direction='row' gap={4} vAlign='center' className='justify-between'>
                          <UITypography variant='body-2' className='text-gray-500'>
                            데이터세트
                          </UITypography>
                          <UITypography variant='body-2' className='text-gray-500'>
                            2026.01.08 18:23
                          </UITypography>
                        </UIGroup>
                      </UIGroup>
                    </div>
                  </li>
                  <li className='item'>
                    <div className='item-left'>
                      <UIGroup direction='column' gap={4}>
                        <UIButton2 onClick={handleLinkEvent} className='cursor-pointer'>
                          <UITypography variant='title-4' className='secondary-neutral-700 text-sb'>
                            abcdefsadasdasdasdasdasdadsasdawdqeqwerqweasdasdasdasdasd
                          </UITypography>
                        </UIButton2>
                        <UIGroup direction='row' gap={4} vAlign='center' className='justify-between'>
                          <UITypography variant='body-2' className='text-gray-500'>
                            데이터세트
                          </UITypography>
                          <UITypography variant='body-2' className='text-gray-500'>
                            2026.01.08 18:23
                          </UITypography>
                        </UIGroup>
                      </UIGroup>
                    </div>
                  </li>
                  <li className='item'>
                    <div className='item-left'>
                      <UIGroup direction='column' gap={4}>
                        <UIButton2 onClick={handleLinkEvent} className='cursor-pointer'>
                          <UITypography variant='title-4' className='secondary-neutral-700 text-sb'>
                            신용대출 조건 분류 데이터
                          </UITypography>
                        </UIButton2>
                        <UIGroup direction='row' gap={4} vAlign='center' className='justify-between'>
                          <UITypography variant='body-2' className='text-gray-500'>
                            데이터세트
                          </UITypography>
                          <UITypography variant='body-2' className='text-gray-500'>
                            2026.01.08 18:23
                          </UITypography>
                        </UIGroup>
                      </UIGroup>
                    </div>
                  </li>
                  <li className='item'>
                    <div className='item-left'>
                      <UIGroup direction='column' gap={4}>
                        <UIButton2 onClick={handleLinkEvent} className='cursor-pointer'>
                          <UITypography variant='title-4' className='secondary-neutral-700 text-sb'>
                            ATM 출금안내문
                          </UITypography>
                        </UIButton2>
                        <UIGroup direction='row' gap={4} vAlign='center' className='justify-between'>
                          <UITypography variant='body-2' className='text-gray-500'>
                            데이터세트
                          </UITypography>
                          <UITypography variant='body-2' className='text-gray-500'>
                            2026.01.08 18:23
                          </UITypography>
                        </UIGroup>
                      </UIGroup>
                    </div>
                  </li>
                </ul>
                {/* <div className='no-date'>
                  <UIImage src='/assets/images/system/ico-system-80-default-nodata.svg' alt='No data' className='w-20 h-20' />
                  <UITypography variant='body-1' className='text-gray-500'>등록된 공지사항이 없습니다.</UITypography>
                </div> */}
              </div>
            </div>
            <div className='box-group-item'>
              <div className='item-header'>
                <div className='left'>
                  <UITypography variant='title-4' className='secondary-neutral-900'>
                    사용 가능한 모델
                  </UITypography>
                </div>
              </div>
              <div className='item-cont mt-4'>
                <ul className='recent-data-list'>
                  <li className='item'>
                    <div className='item-left'>
                      <UIGroup direction='column' gap={4}>
                        <UIButton2 onClick={handleLinkEvent} className='cursor-pointer'>
                          <UITypography variant='title-4' className='secondary-neutral-700 text-sb'>
                            abcdefsadasdasdasdasdasdadsasdawdqeqwerqweasdasdasdasd
                          </UITypography>
                        </UIButton2>
                        <UIGroup direction='row' gap={4} vAlign='center' className='justify-between'>
                          <UITypography variant='body-2' className='text-gray-500'>
                            데이터세트
                          </UITypography>
                          <UITypography variant='body-2' className='text-gray-500'>
                            2026.01.08 18:23
                          </UITypography>
                        </UIGroup>
                      </UIGroup>
                    </div>
                  </li>
                  <li className='item'>
                    <div className='item-left'>
                      <UIGroup direction='column' gap={4}>
                        <UIButton2 onClick={handleLinkEvent} className='cursor-pointer'>
                          <UITypography variant='title-4' className='secondary-neutral-700 text-sb'>
                            금융 Q&A 응답 모델
                          </UITypography>
                        </UIButton2>
                        <UIGroup direction='row' gap={4} vAlign='center' className='justify-between'>
                          <UITypography variant='body-2' className='text-gray-500'>
                            데이터세트
                          </UITypography>
                          <UITypography variant='body-2' className='text-gray-500'>
                            2026.01.08 18:23
                          </UITypography>
                        </UIGroup>
                      </UIGroup>
                    </div>
                  </li>
                  <li className='item'>
                    <div className='item-left'>
                      <UIGroup direction='column' gap={4}>
                        <UIButton2 onClick={handleLinkEvent} className='cursor-pointer'>
                          <UITypography variant='title-4' className='secondary-neutral-700 text-sb'>
                            약관해석기_보험형약관해석기_보험형약관해석기_보험형약관해석기_보험형약관해석기_보험형약관해석기_보험형약관해석기_보험형
                          </UITypography>
                        </UIButton2>
                        <UIGroup direction='row' gap={4} vAlign='center' className='justify-between'>
                          <UITypography variant='body-2' className='text-gray-500'>
                            데이터세트
                          </UITypography>
                          <UITypography variant='body-2' className='text-gray-500'>
                            2026.01.08 18:23
                          </UITypography>
                        </UIGroup>
                      </UIGroup>
                    </div>
                  </li>
                  <li className='item'>
                    <div className='item-left'>
                      <UIGroup direction='column' gap={4}>
                        <UIButton2 onClick={handleLinkEvent} className='cursor-pointer'>
                          <UITypography variant='title-4' className='secondary-neutral-700 text-sb'>
                            약관해석기_보험형
                          </UITypography>
                        </UIButton2>
                        <UIGroup direction='row' gap={4} vAlign='center' className='justify-between'>
                          <UITypography variant='body-2' className='text-gray-500'>
                            데이터세트
                          </UITypography>
                          <UITypography variant='body-2' className='text-gray-500'>
                            2026.01.08 18:23
                          </UITypography>
                        </UIGroup>
                      </UIGroup>
                    </div>
                  </li>
                  <li className='item'>
                    <div className='item-left'>
                      <UIGroup direction='column' gap={4}>
                        <UIButton2 onClick={handleLinkEvent} className='cursor-pointer'>
                          <UITypography variant='title-4' className='secondary-neutral-700 text-sb'>
                            업무 보고서 요약기
                          </UITypography>
                        </UIButton2>
                        <UIGroup direction='row' gap={4} vAlign='center' className='justify-between'>
                          <UITypography variant='body-2' className='text-gray-500'>
                            데이터세트
                          </UITypography>
                          <UITypography variant='body-2' className='text-gray-500'>
                            2026.01.08 18:23
                          </UITypography>
                        </UIGroup>
                      </UIGroup>
                    </div>
                  </li>
                </ul>
                {/* <div className='no-date'>
                  <UIImage src='/assets/images/system/ico-system-80-default-nodata.svg' alt='No data' className='w-20 h-20' />
                  <UITypography variant='body-1' className='text-gray-500'>등록된 공지사항이 없습니다.</UITypography>
                </div> */}
              </div>
            </div>
            <div className='box-group-item'>
              <div className='item-header'>
                <div className='left'>
                  <UITypography variant='title-4' className='secondary-neutral-900'>
                    사용 가능한 에이전트
                  </UITypography>
                </div>
              </div>
              <div className='item-cont mt-4'>
                <ul className='recent-data-list'>
                  <li className='item'>
                    <div className='item-left'>
                      <UIGroup direction='column' gap={4}>
                        <UIButton2 onClick={handleLinkEvent} className='cursor-pointer'>
                          <UITypography variant='title-4' className='secondary-neutral-700 text-sb'>
                            대출 상담 자동화 에이전트대출 상담 자동화 에이전트대출 상담 자동화 에이전트대출 상담 자동화 에이전트대출 상담 자동화 에이전트
                          </UITypography>
                        </UIButton2>
                        <UIGroup direction='row' gap={4} vAlign='center' className='justify-between'>
                          <UITypography variant='body-2' className='text-gray-500'>
                            데이터세트
                          </UITypography>
                          <UITypography variant='body-2' className='text-gray-500'>
                            2026.01.08 18:23
                          </UITypography>
                        </UIGroup>
                      </UIGroup>
                    </div>
                  </li>
                  <li className='item'>
                    <div className='item-left'>
                      <UIGroup direction='column' gap={4}>
                        <UIButton2 onClick={handleLinkEvent} className='cursor-pointer'>
                          <UITypography variant='title-4' className='secondary-neutral-700 text-sb'>
                            금융 약관 번역
                          </UITypography>
                        </UIButton2>
                        <UIGroup direction='row' gap={4} vAlign='center' className='justify-between'>
                          <UITypography variant='body-2' className='text-gray-500'>
                            데이터세트
                          </UITypography>
                          <UITypography variant='body-2' className='text-gray-500'>
                            2026.01.08 18:23
                          </UITypography>
                        </UIGroup>
                      </UIGroup>
                    </div>
                  </li>
                  <li className='item'>
                    <div className='item-left'>
                      <UIGroup direction='column' gap={4}>
                        <UITypography variant='title-4' className='secondary-neutral-700 text-sb'>
                          {/* [참고] title - Ver 태그 추가로 div 추가함 */}
                          <div className='flex gap-2'>
                            <UIButton2 onClick={handleLinkEvent} className='cursor-pointer'>
                              <UITypography variant='title-4' className='secondary-neutral-700 text-sb'>
                                신한 스마트콜봇신한 스마트콜봇신한 스마트콜봇신한 스마트콜봇신한 스마트콜봇신한 스마트콜봇신한 스마트콜봇
                              </UITypography>
                            </UIButton2>
                            <UITextLabel intent='blue' className='!border-0'>
                              Ver.1
                            </UITextLabel>
                          </div>
                        </UITypography>
                        <UIGroup direction='row' gap={4} vAlign='center' className='justify-between'>
                          <UITypography variant='body-2' className='text-gray-500'>
                            데이터세트
                          </UITypography>
                          <UITypography variant='body-2' className='text-gray-500'>
                            2026.01.08 18:23
                          </UITypography>
                        </UIGroup>
                      </UIGroup>
                    </div>
                  </li>
                  <li className='item'>
                    <div className='item-left'>
                      <UIGroup direction='column' gap={4}>
                        <UITypography variant='title-4' className='secondary-neutral-700 text-sb'>
                          {/* [참고] title - Ver 태그 추가로 div 추가함 */}
                          <div className='flex gap-2'>
                            <UIButton2 onClick={handleLinkEvent} className='cursor-pointer'>
                              <UITypography variant='title-4' className='secondary-neutral-700 text-sb'>
                                신한 스마트콜봇
                              </UITypography>
                            </UIButton2>
                            <UITextLabel intent='blue' className='!border-0'>
                              Ver.2
                            </UITextLabel>
                          </div>
                        </UITypography>
                        <UIGroup direction='row' gap={4} vAlign='center' className='justify-between'>
                          <UITypography variant='body-2' className='text-gray-500'>
                            데이터세트
                          </UITypography>
                          <UITypography variant='body-2' className='text-gray-500'>
                            2026.01.08 18:23
                          </UITypography>
                        </UIGroup>
                      </UIGroup>
                    </div>
                  </li>
                  <li className='item'>
                    <div className='item-left'>
                      <UIGroup direction='column' gap={4}>
                        <UIButton2 onClick={handleLinkEvent} className='cursor-pointer'>
                          <UITypography variant='title-4' className='secondary-neutral-700 text-sb'>
                            금융상품 설명봇
                          </UITypography>
                        </UIButton2>
                        <UIGroup direction='row' gap={4} vAlign='center' className='justify-between'>
                          <UITypography variant='body-2' className='text-gray-500'>
                            데이터세트
                          </UITypography>
                          <UITypography variant='body-2' className='text-gray-500'>
                            2026.01.08 18:23
                          </UITypography>
                        </UIGroup>
                      </UIGroup>
                    </div>
                  </li>
                </ul>

                {/* <div className='no-date'>
                  <UIImage src='/assets/images/system/ico-system-80-default-nodata.svg' alt='No data' className='w-20 h-20' />
                  <UITypography variant='body-1' className='text-gray-500'>등록된 공지사항이 없습니다.</UITypography>
                </div> */}
              </div>
            </div>
          </div>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
