import { useState } from 'react';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UICode } from '@/components/UI/atoms/UICode';
import { UITabs } from '../../../components/UI/organisms/UITabs';
import { DesignLayout } from '../../components/DesignLayout';
import { UIUnitGroup } from '@/components/UI/molecules';

export const DP_020301 = () => {
  const [activeTab, setActiveTab] = useState('Tab2');

  // 탭 옵션 정의
  const tabOptions = [
    { id: 'Tab1', label: '기본 정보' },
    { id: 'Tab2', label: '시스템로그' },
    { id: 'Tab3', label: '모니터링' },
  ];

  return (
    <DesignLayout
      initialMenu={{ id: 'agent', label: '에이전트' }}
      initialSubMenu={{
        id: 'agent-tools',
        label: '에이전트의 도구',
        icon: 'ico-lnb-menu-20-agent-tools',
      }}
    >
      {/* 섹션 페이지 */}
      <section className='section-page'>
        <UIPageHeader title='에이전트 배포 조회' description='' />

        {/* 페이지 바디 */}
        <UIPageBody>
          {/* 탭 영역 */}
          <UIArticle>
            <UITabs items={tabOptions} activeId={activeTab} onChange={setActiveTab} size='large' />
          </UIArticle>

          <UIArticle>
            <div className='article-header'>
              <UIUnitGroup direction='row' align='space-between' gap={0}>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  시스템로그
                </UITypography>
                <UIButton2 className='btn-option-outlined'>다운로드</UIButton2>
              </UIUnitGroup>
            </div>
            <div className='article-body'>
              {/* 
                  [참고]
                  minHeight, height, maxHeight = 모두 동일한 높이 사이즈 넣기
                */}
              <UICode
                value={` 

#################################### [스크롤] ####################################

여기는 에디터 화면입니다. 테스트 test
여기는 에디터 화면입니다. 테스트 test
  여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test
    여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test
        여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test
          여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test
            여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test
              여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test
                  여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test여기는 에디터 화면입니다. 테스트 test 여기는 에디터 화면입니다. 테스트 test
                여기는 에디터 화면입니다. 테스트 test
                    여기는 에디터 화면입니다. 테스트 test
여기는 에디터 화면입니다. 테스트 test
    여기는 에디터 화면입니다. 테스트 test
    여기는 에디터 화면입니다. 테스트 test
        여기는 에디터 화면입니다. 테스트 test
        여기는 에디터 화면입니다. 테스트 test
              여기는 에디터 화면입니다. 테스트 test
              여기는 에디터 화면입니다. 테스트 test
여기는 에디터 화면입니다. 테스트 test
111111
2222222
33333
4444
5555555
5555555
5555555
5555555
5555555
5555555
5555555`}
                language='python'
                theme='dark'
                width='100%'
                minHeight='512px'
                height='512px'
                maxHeight='512px'
                readOnly={false}
              />
            </div>
          </UIArticle>
        </UIPageBody>
      </section>
    </DesignLayout>
  );
};
