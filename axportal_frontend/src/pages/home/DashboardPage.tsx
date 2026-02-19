import { useMemo, useState } from 'react';

import { useNavigate } from 'react-router-dom';

import { Button } from '@/components/common/auth';
import { UIButton2, UIIcon2, UITooltip, UITypography } from '@/components/UI/atoms';
import { UIImage } from '@/components/UI/atoms/UIImage';
import { UIPageHeader, UIProfileBadgeGroup, UIUnitGroup } from '@/components/UI/molecules';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { AUTH_KEY } from '@/constants/auth';
import { env, RUN_MODE_TYPES } from '@/constants/common/env.constants.ts';
import { ProjCreWizard, ProjJoinWizard } from '@/pages/home/project';
import { useGetProjectUsers } from '@/services/admin/projMgmt';
import { useGetNotices } from '@/services/notice';
import type { NoticeItem } from '@/services/notice/types';
import { useUser } from '@/stores/auth/useUser';
import dateUtils from '@/utils/common/date.utils.ts';
import { DashboardAgentSection } from './dashboard/DashboardAgentSection';
import { DashboardKnowledgeSection } from './dashboard/DashboardKnowledgeSection';
import { DashboardModelSection } from './dashboard/DashboardModelSection';

export const DashboardPage = () => {
  const { user } = useUser();
  const navigate = useNavigate();

  const isProd = env.VITE_RUN_MODE === RUN_MODE_TYPES.PROD;

  const [projectPopupOpen, setProjectPopupOpen] = useState<'NONE' | 'CREATE_PROJECT' | 'JOIN_PROJECT'>('NONE');

  // 공지사항 조회
  const { data: noticeData, isError: isNoticeError } = useGetNotices(
    {
      page: 1,
      size: 2,
      sort: 'createdDate,desc',
    },
    {
      staleTime: 0, // 데이터를 항상 stale로 취급
      refetchOnMount: 'always', // 마운트 시 항상 refetch
    }
  );
  const noticeList = useMemo(() => {
    if (isNoticeError) {
      return [];
    }
    if (noticeData && noticeData?.content?.length > 0) {
      const tempNoticeList: NoticeItem[] = (noticeData?.content as unknown as NoticeItem[]) || [];
      return tempNoticeList?.map(item => ({
        ...item,
        // displayCreateDate: formatTimeAgo(item.modifiedDate),
        displayCreateDate: dateUtils.formatDate(item.createdDate, 'custom', { pattern: 'yyyy.MM.dd HH:mm', useKoreanLocale: true }),
      }));
    } else {
      return [];
    }
  }, [noticeData, isNoticeError]);

  // 프로젝트 구성원 목록 조회
  const currentProject = useMemo(() => {
    if (!user?.projectList || user.projectList.length === 0) {
      return undefined;
    }
    return user?.projectList?.find(item => item.active) || undefined;
  }, [user]);

  const { data: projectUserData } = useGetProjectUsers(
    currentProject?.prjUuid || '',
    {
      page: 1,
      size: 50000,
    },
    { enabled: !!currentProject?.prjUuid && currentProject?.prjUuid !== 'project-public' }
  );

  const profileUsers = useMemo(() => {
    if (projectUserData?.content && projectUserData.content.length > 0) {
      const bgColors = ['#005DF9', '#576072', '#5F81DB', '#9B59B6', '#F39C12'];

      // 1순위 프로젝트관리자목록 (projectUserData.content중 roleUuid === 'role-uuid-project-admin-004') item.joinedAt내림차순
      const adminList = projectUserData.content
        .filter(item => item.roleUuid === 'role-uuid-project-admin-004' && item.uuid !== user.userInfo.adxpUserId)
        .sort((a, b) => {
          const dateA = a.joinedAt ? new Date(a.joinedAt).getTime() : 0;
          const dateB = b.joinedAt ? new Date(b.joinedAt).getTime() : 0;
          return dateB - dateA; // 내림차순
        });

      // 2순위 나 (projectUserData.content중 uuid === user.userInfo.adxpUserId) item.joinedAt내림차순
      const meItem = projectUserData.content
        .filter(item => item.uuid === user.userInfo.adxpUserId)
        .sort((a, b) => {
          const dateA = a.joinedAt ? new Date(a.joinedAt).getTime() : 0;
          const dateB = b.joinedAt ? new Date(b.joinedAt).getTime() : 0;
          return dateB - dateA; // 내림차순
        });

      // 3순위 나머지 (1순위, 2순위를 제외한 projectUserData.content) item.joinedAt내림차순
      const adminUuids = new Set(adminList.map(item => item.uuid));
      const meUuids = new Set(meItem.map(item => item.uuid));
      const elseList = projectUserData.content
        .filter(item => !adminUuids.has(item.uuid) && !meUuids.has(item.uuid))
        .sort((a, b) => {
          const dateA = a.joinedAt ? new Date(a.joinedAt).getTime() : 0;
          const dateB = b.joinedAt ? new Date(b.joinedAt).getTime() : 0;
          return dateB - dateA; // 내림차순
        });

      // adminList + meItem + elseList
      const totalList = [...adminList, ...meItem, ...elseList];

      return totalList.map((item, index) => ({
        id: item.uuid,
        name: item.jkwNm,
        fullName: item.jkwNm,
        bgColor: bgColors[index % 5],
        isAdmin: item.roleUuid === 'role-uuid-project-admin-004',
        department: item.deptNm + `${item.uuid === user.userInfo.adxpUserId ? ' (나)' : ''}`,
      }));
    }
    return [];
  }, [projectUserData, user]);

  // 공지사항 클릭 핸들러
  const handleNoticeClick = (id: string) => {
    navigate(`/notice/${id}`);
  };

  const handleProjectPopupClose = () => {
    setProjectPopupOpen('NONE');
  };

  const handleCreateProjectPopupOpen = () => {
    setProjectPopupOpen('CREATE_PROJECT');
  };

  const handleJoinProjectPopupOpen = () => {
    setProjectPopupOpen('JOIN_PROJECT');
  };

  const handleMoveModelLogPage = () => {
    navigate(`/log/modelDeployLog`);
  };

  const handleUserGuideBannerClick = () => {
    if (isProd) {
      navigate(`/notice/noticeList`);
    } else {
      navigate(`/notice/160`);
    }
  };

  const handlePlaygroundBannerClick = () => {
    navigate(`/model/pg`);
  };

  const handleAgentBuilderBannerClick = () => {
    navigate(`/agent/builder`);
  };

  const handleAPIKeyBannerClick = () => {
    navigate(`/deploy/apiKey`);
  };

  return (
    <>
      <section className='section-page'>
        <UIPageHeader
          title='대시보드'
          description={[
            '현재 선택된 프로젝트에서 진행 중인 작업과 주요 정보를 한눈에 볼 수 있습니다.',
            '선택된 프로젝트 기준으로 데이터, 모델, 에이전트의 최근 작업 현황을 확인해 보세요.',
          ]}
        />

        {/* 페이지 바디 */}
        <UIPageBody>
          {/* 상단 영역 - 그룹 정보 */}
          <div className='flex items-center justify-between mb-3'>
            <div className='flex items-center gap-3'>
              <UITypography variant='title-2' className='secondary-neutral-900'>
                {`${currentProject?.prjNm} ${currentProject?.prjSeq !== '-999' ? `(${currentProject?.prjSeq})` : ''}`}
              </UITypography>
              <UITypography variant='title-4' className='secondary-neutral-600'>
                프로젝트
              </UITypography>
            </div>
            {/* 그룹 참여자 영역 */}
            {profileUsers.length > 0 && (
              <div className='flex items-center gap-4'>
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
                  <UIProfileBadgeGroup users={profileUsers} maxVisible={3} showDropdown={true} maxDropdownDisplay={5} />
                </div>
              </div>
            )}
          </div>

          {/* 레이이아웃 Top */}
          <div className='dash-top box-group flex gap-6'>
            <div className={'box-group-item ' + (isProd ? 'box-main-prod' : 'box-main-dev')}>
              {/* 게발방 */}
              {!isProd && (
                <div className='item-header p-1'>
                  <div className='left'>
                    <UIUnitGroup gap={16} direction='column' vAlign='start'>
                      <UITypography variant='title-2' className='secondary-neutral-900 text-sb'>
                        프로젝트를 생성하거나 기존 프로젝트에 참여해보세요.
                      </UITypography>
                      <UIUnitGroup gap={16} direction='row' vAlign='start'>
                        <Button
                          auth={AUTH_KEY.HOME.PROJECT_CREATE}
                          className='btn-text-16'
                          rightIcon={{ className: 'ic-system-12-arrow-right-black', children: '' }}
                          onClick={handleCreateProjectPopupOpen}
                        >
                          프로젝트 생성
                        </Button>
                        <Button className='btn-text-16' rightIcon={{ className: 'ic-system-12-arrow-right-black', children: '' }} onClick={handleJoinProjectPopupOpen}>
                          프로젝트 참여
                        </Button>
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
                        <UIButton2 className='btn-text-16' rightIcon={{ className: 'ic-system-12-arrow-right-black', children: '' }} onClick={handleMoveModelLogPage}>
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
                {noticeList && noticeList?.length > 0 ? (
                  <div className='notice-container'>
                    {noticeList.map(notice => (
                      <div key={notice.id} className='notice-item'>
                        <UITypography variant='body-2' className='label secondary-neutral-500'>
                          {notice.type}
                        </UITypography>
                        <UIButton2
                          onClick={() => {
                            handleNoticeClick(notice.id);
                          }}
                          className='cursor-pointer'
                        >
                          <UITypography variant='title-4' className='title secondary-neutral-700 text-sb'>
                            {notice.title}
                          </UITypography>
                        </UIButton2>
                        <UITypography variant='body-2' className='datetime secondary-neutral-600'>
                          {notice.displayCreateDate}
                        </UITypography>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div className='no-date'>
                    <UIImage src='/assets/images/system/ico-system-80-default-nodata.svg' alt='No data' className='w-20 h-20' />
                    <p className='text-base text-gray-500'>등록된 공지사항이 없습니다.</p>
                  </div>
                )}
              </div>
            </div>
          </div>

          <div className='card-banner mt-6'>
            <div className='card-banner-item cursor-pointer' onClick={handleUserGuideBannerClick}>
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
            {isProd ? (
              <div className='card-banner-item cursor-pointer' onClick={handleAPIKeyBannerClick}>
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
            ) : (
              <div className='card-banner-item cursor-pointer' onClick={handleAgentBuilderBannerClick}>
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
            )}
            <div className='card-banner-item cursor-pointer' onClick={handlePlaygroundBannerClick}>
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
          </div>

          {/* 레이이아웃 Bottom */}
          <div className='box-group flex gap-6'>
            {
              !env.VITE_NO_PRESSURE_MODE && <>
                <DashboardKnowledgeSection />
                <DashboardModelSection />
                <DashboardAgentSection />
              </>
            }
          </div>
        </UIPageBody>
      </section>

      {projectPopupOpen === 'CREATE_PROJECT' && <ProjCreWizard onClose={handleProjectPopupClose} />}
      {projectPopupOpen === 'JOIN_PROJECT' && <ProjJoinWizard onClose={handleProjectPopupClose} />}
    </>
  );
};
