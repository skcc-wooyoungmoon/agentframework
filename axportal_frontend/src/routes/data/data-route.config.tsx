import { Navigate } from 'react-router';

import { DataCtlgPage } from '@/pages/data/dataCtlg/DataCtlgPage';
import { DataSetDetailPage } from '@/pages/data/dataCtlg/dataset/DataSetDetailPage';
import { KnowledgeDetailPage } from '@/pages/data/dataCtlg/knowledge/KnowledgeDetailPage';
import { KnowledgeFileDetailPage } from '@/pages/data/dataCtlg/knowledge/KnowledgeFileDetailPage';
import { DataStoragePage, MDPackageDetailPage } from '@/pages/data/dataStor';
import { ProcDetailPage, ToolListPage, VectorDBDetailPage } from '@/pages/data/dataTools';
import type { RouteType } from '@/routes/types';

// 임시 컴포넌트들 (실제 페이지 컴포넌트가 생성되면 교체 필요)
// const DataCatalogPage = () => <div>데이터 카탈로그 페이지</div>;
//const DataToolsPage = () => <div>데이터 도구 페이지</div>;

/**
 * path: 경로
 * label : 메뉴 명
 * element : 레이아웃 컴포넌트
 * children : 하위 메뉴 리스트
 */

export const dataRouteConfig: RouteType[] = [
  {
    id: 'data-main',
    path: '',
    label: 'data',
    element: <Navigate to='dataStor' replace />,
  },
  {
    id: 'data-storage',
    label: '데이터 탐색',
    path: 'dataStor',
    children: [
      {
        id: 'data-storage-main',
        label: '',
        path: '',
        element: <DataStoragePage />,
      },
      {
        id: 'data-storage-md-package-detail',
        label: '데이터 탐색 조회',
        path: 'md-package/:id',
        element: <MDPackageDetailPage />,
      },
    ],
  },
  {
    id: 'data-catalog',
    label: '지식/학습 데이터 관리',
    path: 'dataCtlg',
    children: [
      {
        id: 'data-catalog-main',
        label: '',
        path: '',
        element: <DataCtlgPage />,
      },
      {
        id: 'data-catalog-dataset-detail',
        label: '지식/학습 데이터 관리 조회',
        path: 'dataset/:datasetId',
        element: <DataSetDetailPage />,
      },
      {
        id: 'data-catalog-knowledge-detail-new',
        label: '지식/학습 데이터 관리 조회',
        path: 'knowledge/detail/:knwId',
        element: <KnowledgeDetailPage />,
      },
      {
        id: 'data-catalog-knowledge-file-detail',
        label: '지식파일 상세',
        path: 'knowledge/file/:fileId',
        element: <KnowledgeFileDetailPage />,
      },
    ],
  },
  {
    id: 'data-tools',
    label: '데이터 도구',
    path: 'dataTools',
    children: [
      {
        id: 'data-tools-main',
        label: '',
        path: '',
        element: <ToolListPage />,
      },
      {
        id: 'data-tools-proc-detail',
        label: '데이터 도구 조회',
        path: 'proc/:id',
        element: <ProcDetailPage />,
      },
      {
        id: 'data-tools-vectorDB-detail',
        label: '데이터 도구 조회',
        path: 'vectorDB/:id',
        element: <VectorDBDetailPage />,
      },
    ],
  },
];
