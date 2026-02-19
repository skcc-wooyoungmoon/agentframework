import { useState } from 'react';

import { UITabs } from '@/components/UI/organisms';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { useBackRestoredState } from '@/hooks/common/navigation';

import { ProcList, VectorDBList } from '@/components/data';
import { ToolCreatePopupPage } from './ToolCreatePopupPage';
import { VectorDBCreatePopupPage } from './VectorDBCreatePopupPage';

import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
// import { usePermissionCheck } from '@/hooks/common/auth';
import { Button } from '@/components/common/auth';
import { AUTH_KEY } from '@/constants/auth/auth.constants';

export const ToolListPage = () => {
  // string을 객체로 감싸서 사용
  const { filters: tabState, updateFilters: setTabState } = useBackRestoredState<{ activeTab: string }>(STORAGE_KEYS.SEARCH_VALUES.DATA_TOOL_LIST, { activeTab: 'vector' });

  // activeTab과 setActiveTab을 추출
  const activeTab = tabState.activeTab;
  const setActiveTab = (newTab: string) => setTabState({ activeTab: newTab });

  const [popupStep, setPopupStep] = useState<'create' | 'vectorDB' | null>(null);
  // 공통 팝업 훅
  const { showCancelConfirm } = useCommonPopup();
  //  const { checkPermissionAndShowAlert } = usePermissionCheck();
  const handleToolCreatePopup = () => {
    setPopupStep('create');
  };

  const onNext = (accessType: string) => {
    // accessType에 따라 적절한 팝업 열기
    setPopupStep(accessType as 'create' | 'vectorDB');
  };

  /**
   * 데이터 도구 만들기 팝업 닫기
   */
  const handleCreatePopupClose = () => {
    setPopupStep(null);
  };

  /**
   * VectorDB 만들기 팝업 닫기
   */
  const handleVectorDBCreatePopupClose = () => {
    showCancelConfirm({
      onConfirm: () => {
        setPopupStep(null);
      },
    });
  };

  // 탭 옵션 정의
  const tabOptions = [
    { id: 'vector', label: '벡터 DB' },
    { id: 'processer', label: '프로세서' },
  ];

  const handlePreviousStep = () => {
    setPopupStep('create');
  };

  return (
    <>
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='데이터 도구'
          description={[
            '학습 데이터세트에 사용할 프로세서 조회와 지식에 사용할 벡터DB를 관리할 수 있습니다.',
            '벡터 DB의 경우 기본 지식은 기본 벡터DB를 사용하며, 사용자 정의 지식은 별도의 벡터DB를 등록해야 합니다.',
          ]}
          actions={
            <>
              <Button
                auth={AUTH_KEY.DATA.VECTOR_DB_CREATE}
                className='btn-text-18-semibold-point'
                leftIcon={{ className: 'ic-system-24-add', children: '' }}
                onClick={() => handleToolCreatePopup()}
              >
                데이터도구 만들기
              </Button>
            </>
          }
        />

        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle className='article-tabs'>
            {/* 아티클 탭 */}
            <UITabs items={tabOptions} activeId={activeTab} size='large' onChange={setActiveTab} />
          </UIArticle>
          {activeTab === 'vector' && <VectorDBList isActiveTab={activeTab === 'vector'} />}
          {activeTab === 'processer' && <ProcList isActiveTab={activeTab === 'processer'} />}
        </UIPageBody>
      </section>

      {/* 팝업들 */}
      <ToolCreatePopupPage isOpen={popupStep === 'create'} onClose={handleCreatePopupClose} onNext={onNext} />
      <VectorDBCreatePopupPage
        isOpen={popupStep === 'vectorDB'}
        onClose={handleVectorDBCreatePopupClose}
        onCreateSuccess={() => {
          // console.log('VectorDB 생성 완료, 데이터 새로고침');
          setPopupStep(null);
          // setSearchParams({ tab: 'vector' });
          // refetchVector();
        }}
        onPreviousStep={handlePreviousStep}
      />
    </>
  );
};
