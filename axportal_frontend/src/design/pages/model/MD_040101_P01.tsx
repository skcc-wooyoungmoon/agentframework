import React, { useState } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIInput } from '@/components/UI/molecules';
import { UILabel } from '@/components/UI/atoms';
import { UIArticle } from '@/components/UI/molecules/UIArticle';

import { UIGrid } from '../../../components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '../../../components/UI/molecules/list';

// AG_010102_P25 페이지
export const MD_040101_P01: React.FC = () => {
  const [searchValue, setSearchValue] = useState('');

  // 데이터
  const projectData = [
    {
      id: '1',
      no: 1,
      deployName: '예적금 상품 Q&A 세트',
      modelName: 'GPT/text-embedding-3-large',
      status: '이용가능',
      description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담사 답변 데이터',
      modelType: 'language',
      deployType: 'serverless',
      isDeployed: '배포됨',
      publicScope: '전체공유',
      createdDate: '2025.03.24 18:32:43',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '2',
      no: 2,
      deployName: '모바일뱅킹 이용 가이드',
      modelName: 'Cohere/embed-multilingual',
      status: '이용가능',
      description: '모바일뱅킹 앱 사용법 및 주요 기능 안내 문서',
      modelType: 'language',
      deployType: 'self-hosting',
      isDeployed: '배포됨',
      publicScope: '부서공유',
      createdDate: '2025.03.23 14:15:32',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '3',
      no: 3,
      deployName: 'ATM/창구 업무 안내 문서',
      modelName: 'Google/text-bison-001',
      status: '이용가능',
      description: 'ATM 및 창구에서의 업무 처리 절차 및 안내 매뉴얼',
      modelType: 'language',
      deployType: 'serverless',
      isDeployed: '배포됨',
      publicScope: '전체공유',
      createdDate: '2025.03.22 09:45:21',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '4',
      no: 4,
      deployName: '외화 송금 및 환율 상담 로그',
      modelName: 'HuggingFace/bert-base-korean',
      status: '실패',
      description: '외화 송금 절차 및 환율 관련 고객 상담 이력 데이터',
      modelType: 'language',
      deployType: 'self-hosting',
      isDeployed: '미배포',
      publicScope: '개인',
      createdDate: '2025.03.21 16:30:15',
      modifiedDate: '2025.03.24 18:32:43',
    },
    {
      id: '5',
      no: 5,
      deployName: '상품 비교형 답변 데이터',
      modelName: 'OpenAI/gpt-4-turbo',
      status: '이용가능',
      description: '여러 금융 상품을 비교 분석하여 고객에게 제공하는 답변 데이터',
      modelType: 'language',
      deployType: 'serverless',
      isDeployed: '배포됨',
      publicScope: '전체공유',
      createdDate: '2025.03.20 12:00:00',
      modifiedDate: '2025.03.24 18:32:43',
    },
  ];

  // 그리드 컬럼 정의
  const columnDefs: any = React.useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no' as const,
        width: 56,
        minWidth: 56,
        maxWidth: 56,
        cellClass: 'text-center',
        headerClass: 'text-center',
        cellStyle: {
          textAlign: 'center',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        },
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: '배포명',
        field: 'deployName' as const,
        width: 272,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '모델명',
        field: 'modelName' as const,
        width: 272,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '상태',
        field: 'status' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: React.memo((params: any) => {
          const getStatusIntent = (status: string) => {
            switch (status) {
              case '실패':
                return 'error';
              case '이용가능':
                return 'complete';
              default:
                return 'complete';
            }
          };
          return (
            <UILabel variant='badge' intent={getStatusIntent(params.value)}>
              {params.value}
            </UILabel>
          );
        }),
      },
      {
        headerName: '설명',
        field: 'description' as const,
        flex: 1,
        minWidth: 392,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '모델유형',
        field: 'modelType' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '배포유형',
        field: 'deployType' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '운영배포 여부',
        field: 'isDeployed' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '공개범위',
        field: 'publicScope' as const,
        width: 120,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '생성일시',
        field: 'createdDate' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '최종 수정일시',
        field: 'modifiedDate' as const,
        width: 180,
        cellStyle: { paddingLeft: '16px' },
      },
    ],
    []
  );

  return (
    <section className='section-modal'>
      <UIArticle className='article-grid'>
        <UIListContainer>
          <UIListContentBox.Header>
            <div className='flex justify-between items-center w-full'>
              <div className='flex-shrink-0'>
                <div style={{ width: '168px', paddingRight: '8px' }}>
                  <UIDataCnt count={projectData.length} prefix='총' unit='건' />
                </div>
              </div>
              <div className='w-[360px] h-[40px]'>
                <UIInput.Search
                  value={searchValue}
                  placeholder='배포명, 모델명, 설명 입력'
                  onChange={e => {
                    setSearchValue(e.target.value);
                  }}
                />
              </div>
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            <UIGrid type='single-select' rowData={projectData} columnDefs={columnDefs} onClickRow={(_params: any) => {}} onCheck={(_selectedIds: any[]) => {}} />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination currentPage={1} totalPages={3} onPageChange={() => {}} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </section>
  );
};
