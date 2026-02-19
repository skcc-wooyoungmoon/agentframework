import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UITabs } from '@/components/UI/organisms/UITabs';
import { MigAddInfomation, MigBaseInfomation } from '@/components/deploy/mig/index';
import { useGetMigMasWithMap } from '@/services/deploy/mig/mig.services';
import type { GetMigMasWithMapResponseItem } from '@/services/deploy/mig/types';
import { useMemo, useState } from 'react';
import { useLocation, useParams } from 'react-router-dom';

export const MigDeployDetailPage = () => {
  const { migId } = useParams<{ migId: string }>();
  const location = useLocation();
  const state = location.state as { asstG?: string; seqNo?: number | string; uuid?: string } | null;
  const asstG = state?.asstG || '';
  const seqNo = state?.seqNo || '';
  const uuid = state?.uuid || migId || '';

  // 운영 이행 관리 조회 (Map 포함) - 전체 리스트 조회
  const { data: migMasWithMapData } = useGetMigMasWithMap(
    {
      asstG,
      sequence: seqNo ? Number(seqNo) : undefined,
      uuid,
    },
    {
      enabled: !!asstG && !!seqNo && !!uuid,
    }
  );

  // migId에 해당하는 첫 번째 데이터 (기본 정보용)
  const selectedData = useMemo<GetMigMasWithMapResponseItem | undefined>(() => {
    if (!migMasWithMapData || !uuid) return undefined;
    return migMasWithMapData.find(item => item.masUuid === uuid);
  }, [migMasWithMapData, uuid]);

  // migId에 해당하는 모든 데이터 (추가 정보용 - mapAsstG별 그룹화)
  const allRelatedData = useMemo<GetMigMasWithMapResponseItem[]>(() => {
    if (!migMasWithMapData || !uuid) return [];
    return migMasWithMapData.filter(item => item.masUuid === uuid);
  }, [migMasWithMapData, uuid]);

  // TAB 설정
  const tabOptions = [
    { id: 'migTab1', label: '기본정보' },
    { id: 'migTab2', label: '추가정보' },
  ];

  const [activeTab, setActiveTab] = useState('migTab1');

  return (
    <section className='section-page'>
      {/* 페이지 헤더 */}
      <UIPageHeader title='운영 이행 이력 조회' description='' />
      {/* 페이지 바디 */}
      <UIPageBody>
        <UIArticle className='article-tabs'>
          {/* 아티클 탭 */}
          <UITabs items={tabOptions} activeId={activeTab} size='large' onChange={setActiveTab} />
        </UIArticle>
        {activeTab === 'migTab1' && <MigBaseInfomation data={selectedData} />}
        {activeTab === 'migTab2' && <MigAddInfomation dataList={allRelatedData} />}
      </UIPageBody>
    </section>
  );
};
