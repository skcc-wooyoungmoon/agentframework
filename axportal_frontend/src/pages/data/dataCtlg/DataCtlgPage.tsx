import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UITabs } from '@/components/UI/organisms';
import { useState } from 'react';
import { DataCtlgCreatePagePopup } from '.';
import { DataSetListPage } from './dataset/DataSetListPage';
import { KnowledgeListPage } from './knowledge/KnowledgeListPage';

import { Button } from '@/components/common/auth';
import { AUTH_KEY } from '@/constants/auth';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { useBackRestoredState } from '@/hooks/common/navigation';
import { useEnvCheck } from '@/hooks/common/util';

export const DataCtlgPage = () => {
  // string을 객체로 감싸서 사용
  const { filters: tabState, updateFilters: setTabState } = useBackRestoredState<{ activeTab: string }>(STORAGE_KEYS.SEARCH_VALUES.DATA_CTLG_LIST, { activeTab: 'knowledge' });

  // activeTab과 setActiveTab을 추출
  const activeTab = tabState.activeTab;
  const setActiveTab = (newTab: string) => setTabState({ activeTab: newTab });

  const [isOpenDataCtlgCreatePagePopup, setIsOpenDataCtlgCreatePagePopup] = useState(false);
  // 숨김 모드 처리 여부
  const { isProd } = useEnvCheck();
  // 디버깅용 로그
  // console.log('isOpenDataCtlgCreatePagePopup: ', isOpenDataCtlgCreatePagePopup);

  // 탭 아이템 리스트
  const tabItems = !isProd
    ? [
        { id: 'knowledge', label: '지식' },
        { id: 'datasets', label: '학습 데이터세트' },
      ]
    : [{ id: 'knowledge', label: '지식' }];

  // 탭에 따라 출력할 화면 변경
  const renderTabContent = () => {
    switch (activeTab) {
      case 'datasets':
        return <DataSetListPage />;
      case 'knowledge':
        return <KnowledgeListPage />;
      default:
        return <KnowledgeListPage />;
    }
  };

  return (
    <>
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='지식/학습 데이터 관리'
          description={[
            '생성형 AI 모델에 사용할 지식(RAG)과 학습을 위한 데이터 세트를 생성할 수 있습니다.',
            '데이터 만들기 버튼을 통해 데이터를 만들고 목록을 클릭하여 상세정보를 조회해 보세요.',
          ]}
          actions={
            <Button
              auth={AUTH_KEY.DATA.DATA_CREATE}
              className='btn-text-18-semibold-point'
              leftIcon={{ className: 'ic-system-24-add', children: '' }}
              onClick={() => {
                setIsOpenDataCtlgCreatePagePopup(true);
              }}
            >
              데이터 만들기
            </Button>
          }
        />

        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle className='article-tabs'>
            <UITabs items={tabItems} activeId={activeTab} size='large' onChange={setActiveTab} />
          </UIArticle>
          {renderTabContent()}
        </UIPageBody>
      </section>

      <DataCtlgCreatePagePopup isOpen={isOpenDataCtlgCreatePagePopup === true} onClose={() => setIsOpenDataCtlgCreatePagePopup(false)} />
    </>
  );
};
