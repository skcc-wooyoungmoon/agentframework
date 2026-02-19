import React, { memo } from 'react';

import { UIDataCnt, UIPagination } from '@/components/UI';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIButton2, /* UILabel */ UIToggle, UITypography } from '@/components/UI';

import { UIGrid } from '../../../components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '../../../components/UI/molecules/list';
import { UIUnitGroup } from '@/components/UI/molecules';

// AG_010102_P33 페이지
export const AG_010102_P33: React.FC = () => {
  // 데이터
  const projectData = [
    {
      id: '1',
      no: 1,
      name: '예적금 상품 Q&A 세트',
      description: '계약서, 약관 등 금융문서 요약 전용',
      isActive: true,
    },
    {
      id: '2',
      no: 2,
      name: '모바일뱅킹 이용 가이드',
      description: '보험 약관 구조 분석 및 의미 설명 AI',
      isActive: true,
    },
    {
      id: '3',
      no: 3,
      name: 'ATM/창구 업무 안내 문서',
      description: '보험 약관 구조 분석 및 의미 설명 AI',
      isActive: true,
    },
    {
      id: '4',
      no: 4,
      name: '외화 송금 및 환율 상담 로그',
      description: '보험 약관 구조 분석 및 의미 설명 AI',
      isActive: true,
    },
    {
      id: '5',
      no: 5,
      name: '상품 비교형 답변 데이터',
      description: '예금, 적금, 외환 등 은행상품 상담 특화',
      isActive: true,
    },
    {
      id: '6',
      no: 6,
      name: '상품 비교형 답변 데이터',
      description: '콜센터/챗봇/이메일 등 다양한 채널 대응 자동화',
      isActive: true,
    },
  ];

  // 그리드 컬럼 정의 [251105_퍼블수정] : 텍스트 수정
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
        headerName: '툴 이름',
        field: 'name' as const,
        width: 272,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: '설명',
        field: 'description' as const,
        minWidth: 609,
        flex: 1,
        showTooltip: true,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: React.memo((params: any) => {
          return (
            <div
              style={{
                overflow: 'hidden',
                textOverflow: 'ellipsis',
                whiteSpace: 'nowrap',
              }}
            >
              {params.value}
            </div>
          );
        }),
      },
      {
        headerName: '활성화',
        field: 'button' as const,
        width: 85,
        cellRenderer: memo(({ data }: any) => {
          return (
            <UIToggle
              checked={data.isActive}
              onChange={() => {
                // 개발자 로직 처리
              }}
            />
          );
        }),
      },
    ],
    []
  );

  // 모든 도구 활성화 토글 상태
  const [allActive, setAllActive] = React.useState(true);

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
              <div>
                {/* [251105_퍼블수정] : 텍스트 및 속성 수정 */}
                <UIUnitGroup gap={8} direction='row' vAlign='center'>
                  <UITypography variant='body-1' className='secondary-neutral-800'>
                    모든 도구 활성화
                  </UITypography>
                  <UIToggle
                    checked={allActive}
                    onChange={() => {
                      setAllActive(prev => !prev);
                      // 개발자 로직 처리
                    }}
                  />
                </UIUnitGroup>
              </div>
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            <UIGrid type='default' rowData={projectData} columnDefs={columnDefs} />
          </UIListContentBox.Body>
          <UIListContentBox.Footer className='ui-data-has-btn'>
            <UIButton2 className='btn-option-outlined' style={{ width: '122px' }}>
              MCP서버 상세가기
            </UIButton2>
            <UIPagination currentPage={1} totalPages={3} onPageChange={() => {}} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </section>
  );
};
