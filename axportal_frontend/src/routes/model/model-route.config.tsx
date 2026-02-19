import { ModelFineTuningDetailPage } from '@/pages/model/fineTuning/ModelFineTuningDetailPage';
import { FineTuningListPage } from '@/pages/model/fineTuning/ModelFineTuningListPage';
import { ModelFineTuningMetricsPage } from '@/pages/model/fineTuning/ModelFineTuningMetricsPage';
import { MdGdnListPage, MdlGdnDetailPage } from '@/pages/model/mdlGdn';
import { ModelCtlgDetailPage, ModelCtlgListPage } from '@/pages/model/modelCtlg';
import { PlaygroundPage } from '@/pages/model/playground/PlaygroundPage';
import type { RouteType } from '@/routes/types';

/**
 * path: 경로
 * label : 메뉴 명 // TODO : 제거 예정
 * element : 레이아웃 컴포넌트
 * children : 하위 메뉴 리스트
 */
export const modelRouteConfig: RouteType[] = [
  {
    id: 'model-main',
    path: '',
    label: '모델',
    // element: <Navigate to='modelCtlg' />,
  },
  {
    id: 'modelgarden',
    path: 'modelgarden',
    label: '모델 탐색',
    children: [
      {
        id: 'modelgarden-list',
        path: '',
        label: '',
        element: <MdGdnListPage />,
      },
      {
        id: 'modelgarden-detail',
        path: ':id',
        label: '모델 탐색 조회',
        element: <MdlGdnDetailPage />,
      },
    ],
  },
  {
    id: 'model-catalog',
    label: '모델 관리',
    path: 'modelCtlg',
    children: [
      {
        id: 'model-catalog-list',
        path: '',
        label: '',
        element: <ModelCtlgListPage />,
      },

      {
        id: 'model-catalog-detail',
        path: ':id',
        label: '모델 조회',
        element: <ModelCtlgDetailPage />,
      },
    ],
  },
  {
    id: 'finetuning',
    label: '파인튜닝',
    path: 'finetuning',
    children: [
      {
        id: 'finetuning-list',
        path: '',
        label: '',
        element: <FineTuningListPage />,
      },
      {
        id: 'finetuning-detail',
        path: ':id',
        label: '파인튜닝 조회',
        element: <ModelFineTuningDetailPage />,
      },
      {
        id: 'finetuning-metrics',
        path: 'metrics',
        label: '매트릭 뷰',
        element: <ModelFineTuningMetricsPage />,
      },
    ],
  },
  {
    id: 'playground',
    path: 'pg',
    label: '플레이그라운드',
    element: <PlaygroundPage />,
  },
];
