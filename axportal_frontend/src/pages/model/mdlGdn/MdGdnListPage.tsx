import { useRef, useState } from 'react';

import { GardenListSelfHosting, GardenListServerless } from '@/components/model/garden';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UITabs } from '@/components/UI/organisms/UITabs';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';
import { usePermissionCheck } from '@/hooks/common/auth';
import { useLayerPopup } from '@/hooks/common/layer';
import { useBackRestoredState } from '@/hooks/common/navigation';
import type { GetModelGardenRequest, ModelGardenInfo } from '@/services/model/garden/types';

import { Button } from '@/components/common/auth';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { useNavigate } from 'react-router';
import { MdGdnAdd } from './MdGdnAdd';
import { ModelGardnIn } from './ModelGardnIn';

/**
 * @author SGO1032948
 * @description 모델가든 리스트 페이지
 *
 * MD_050101
 * MD_050201
 */
export const MdGdnListPage = () => {
  const modelInPopup = useLayerPopup();
  const modelSearchPopup = useLayerPopup();
  const navigate = useNavigate();
  const { checkPermissionAndShowAlert } = usePermissionCheck();

  const selfHostingRef = useRef<{ refetch: () => void }>(null);
  const serverlessRef = useRef<{ refetch: () => void }>(null);

  const [selectedData, setSelectedData] = useState<ModelGardenInfo | undefined>(undefined);

  // 탭 상태 관리
  const { filters: tabState, updateFilters: setTabState } = useBackRestoredState<{ activeTab: string }>(STORAGE_KEYS.SEARCH_VALUES.MODEL_GARDEN_LIST_TAB, { activeTab: 'tab1' });

  // self-hosting 필터 관리
  const { filters: selfHostingFilters, updateFilters: setSelfHostingFilters } = useBackRestoredState<Omit<GetModelGardenRequest, 'dplyTyp' | 'type'>>(
    STORAGE_KEYS.SEARCH_VALUES.MODEL_GARDEN_LIST_SELF_HOSTING,
    {
      page: 1,
      size: 12,
      status: '전체',
      search: '',
    }
  );

  // serverless 필터 관리
  const { filters: serverlessFilters, updateFilters: setServerlessFilters } = useBackRestoredState<Omit<GetModelGardenRequest, 'dplyTyp' | 'status'>>(
    STORAGE_KEYS.SEARCH_VALUES.MODEL_GARDEN_LIST_SERVERLESS,
    {
      page: 1,
      size: 12,
      type: '전체',
      search: '',
    }
  );

  // 탭 옵션 정의
  const tabOptions = [
    { id: 'tab1', label: 'self-hosting' },
    { id: 'tab2', label: 'serverless' },
  ];

  // 모델 반입 완료 시 리스트 refetch
  const handleModelGardenCreateComplete = (id: string) => {
    // if (tabState.activeTab === 'tab1') selfHostingRef.current?.refetch();
    // else serverlessRef.current?.refetch();
    navigate(`/model/modelgarden/${id}`);
  };

  return (
    <>
      <section className='section-page'>
        <UIPageHeader
          title='모델 탐색'
          description={['Public 프로젝트에서 Hugging Face에 등록된 모델을 검색하고 은행 내부로 가져올 수 있습니다.', '모델 검색과 반입 버튼을 통해 필요한 모델을 손쉽게 반입하세요.']}
          actions={
            <>
              <Button
                auth={AUTH_KEY.MODEL.SELF_HOSTING_MODEL_SEARCH_ADD}
                className='btn-text-18-semibold-point'
                leftIcon={{ className: 'ic-system-24-add', children: '' }}
                onClick={() => checkPermissionAndShowAlert(() => modelSearchPopup.onOpen())}
              >
                모델 검색
              </Button>
              <Button
                auth={tabState.activeTab === 'tab1' ? AUTH_KEY.MODEL.SELF_HOSTING_MODEL_IMPORT : AUTH_KEY.MODEL.SERVERLESS_MODEL_IMPORT}
                className='btn-text-18-semibold-point'
                leftIcon={{ className: 'ic-system-24-download' }}
                onClick={() => checkPermissionAndShowAlert(() => modelInPopup.onOpen())}
              >
                모델 반입
              </Button>
            </>
          }
        />

        {/* 페이지 바디 */}
        <UIPageBody>
          {/* 탭 영역 */}
          <UIArticle className='article-tabs'>
            <div className='flex'>
              <UITabs items={tabOptions} activeId={tabState.activeTab} onChange={tabId => setTabState({ activeTab: tabId })} size='large' />
            </div>
          </UIArticle>
          {tabState.activeTab === 'tab1' && (
            <GardenListSelfHosting
              ref={selfHostingRef}
              onOpenInPopup={(data: ModelGardenInfo) => {
                setSelectedData(data);
                modelInPopup.setCustomStep(3);
              }}
              filters={selfHostingFilters}
              updateFilters={setSelfHostingFilters}
            />
          )}
          {tabState.activeTab === 'tab2' && <GardenListServerless ref={serverlessRef} filters={serverlessFilters} updateFilters={setServerlessFilters} />}
        </UIPageBody>
      </section>
      {/* 모델 가든 검색 팝업 */}
      {modelSearchPopup.currentStep > 0 && <MdGdnAdd {...modelSearchPopup} onComplete={handleModelGardenCreateComplete} />}

      {/* 모델 가든 반입 팝업 */}
      {modelInPopup.currentStep > 0 && <ModelGardnIn {...modelInPopup} selectedData={selectedData} onComplete={handleModelGardenCreateComplete} />}
    </>
  );
};
