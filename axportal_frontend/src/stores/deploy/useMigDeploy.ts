import { atom, useAtom } from 'jotai';
import type { MigDeployData } from './types';
import { MIG_DEPLOY_CATEGORY_MAP } from './types';

// 초기 데이터
const initialMigDeployData: MigDeployData = {
  prjSeq: '',
  prjNm: '',
  uuidList: [] as string[],
  category: 'PROJECT',
  categoryName: MIG_DEPLOY_CATEGORY_MAP.PROJECT,
  name: '',
};

// 배포 데이터 atom
export const migDeployDataAtom = atom<MigDeployData>(initialMigDeployData);

// 배포 데이터 업데이트 atom
export const updateMigDeployDataAtom = atom(null, (get, set, update: Partial<MigDeployData>) => {
  const currentData = get(migDeployDataAtom);
  set(migDeployDataAtom, { ...currentData, ...update });
});

// 배포 데이터 초기화 atom
export const resetMigDeployDataAtom = atom(null, (_, set) => {
  set(migDeployDataAtom, initialMigDeployData);
});

// 커스텀 훅
export const useMigDeploy = () => {
  const [migDeployData, setMigDeployData] = useAtom(migDeployDataAtom);
  const [, updateMigDeployData] = useAtom(updateMigDeployDataAtom);
  const [, resetMigDeployData] = useAtom(resetMigDeployDataAtom);

  return {
    migDeployData,
    setMigDeployData,
    updateMigDeployData,
    resetMigDeployData,
    getFinalMigDeployData: () => migDeployData,
  };
};
