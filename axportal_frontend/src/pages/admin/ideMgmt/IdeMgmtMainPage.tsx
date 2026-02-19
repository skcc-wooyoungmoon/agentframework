import { useState } from 'react';

import { UITabs } from '@/components/UI/organisms';
import { UIButton2 } from '@/components/UI/atoms';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIArticle } from '@/components/UI/molecules/UIArticle';

import { IdeImageListPage } from './IdeImageListPage';
import { IdeDwAccountListPage } from './IdeDwAccountListPage';
import { IdeResourceSettingPopup } from './IdeResourceSettingPopup.tsx';

/**
 * 관리 > IDE 관리 메인 페이지
 *
 * - 이미지 관리, DW 계정 관리 탭으로 구성
 */
export const IdeMgmtMainPage = () => {
  const [activeTab, setActiveTab] = useState('Tab1');

  // 환경설정 팝업 상태
  const [isSettingPopupOpen, setIsSettingPopupOpen] = useState(false);

  // 탭 옵션 정의
  const tabOptions = [
    { id: 'Tab1', label: '이미지 관리' },
    { id: 'Tab2', label: 'DW 계정 관리' },
  ];

  return (
    <div>
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='IDE 관리'
          description='IDE 환경에서 사용할 개발 환경 이미지를 등록 및 관리하며, 포탈에 등록된 DW 계정을 확인할 수 있습니다.'
          actions={
            <UIButton2
              className='btn-text-14-semibold-point'
              leftIcon={{ className: 'ic-system-24-outline-blue-setting', children: '' }}
              onClick={() => setIsSettingPopupOpen(true)}
            >
              환경 설정
            </UIButton2>
          }
        />

        {/* 페이지 바디 */}
        <UIPageBody>
          {/* 탭 영역 */}
          <UIArticle className='article-tabs'>
            <div className='flex'>
              <UITabs items={tabOptions} activeId={activeTab} onChange={setActiveTab} size='large' />
            </div>
          </UIArticle>

          {/* 탭별 컨텐츠 */}
          {activeTab === 'Tab1' && <IdeImageListPage />}
          {activeTab === 'Tab2' && <IdeDwAccountListPage />}
        </UIPageBody>
      </section>

      {/* 환경설정 팝업 */}
      {isSettingPopupOpen && <IdeResourceSettingPopup isOpen={isSettingPopupOpen} onClose={() => setIsSettingPopupOpen(false)} />}
    </div>
  );
};
