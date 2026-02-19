import React, { useState, useMemo, memo } from 'react';

import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIButton2, UITypography, UIDataCnt, UILabel } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGroup, UIUnitGroup } from '@/components/UI/molecules';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { DesignLayout } from '../../components/DesignLayout';
import { UIPageFooter } from '@/components/UI/molecules/UIPageFooter';
import { UITextLabel } from '@/components/UI/atoms/UITextLabel';
import { UITabs } from '../../../components/UI/organisms/UITabs';
import { UIIcon2 } from '@/components/UI/atoms/UIIcon2';
import { UICode } from '@/components/UI/atoms/UICode';
import { UICircleChart } from '@/components/UI/molecules/chart';
import { useToast } from '@/hooks/common/toast/useToast';

export const DP_010102 = () => {
  const [, setActiveTab] = useState('tab1');
  // 탭 아이템 정의
  const tabItems = [
    { id: 'tab1', label: '기본 정보' },
    { id: 'tab2', label: '시스템로그' },
    { id: 'tab3', label: '모니터링' },
  ];

  const { toast } = useToast();

  const handleCopy = (message: string) => {
    // toast(message);
    // toast.error(message);
    toast.success(message);
  };
  // 그리드 컬럼 설정
  const modelColumnDefs: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id' as any,
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
        } as any,
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      // 251113_퍼블수정 그리드 컬럼 속성 '이름' 영역 수정 S
      {
        headerName: '이름',
        field: 'name' as any,
        width: 272,
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
        headerName: '설명',
        field: 'description' as any,
        minWidth: 392,
        flex: 1,
        showTooltip: true,
        cellRenderer: memo((params: any) => {
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
        headerName: '유형',
        field: 'catogory',
        width: 120,
      },
      // 251107_퍼블수정 그리드 컬럼 속성 '태그' 영역 수정 S
      {
        headerName: '태그',
        field: 'tags' as const,
        width: 230,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: (params: any) => {
          if (!params.value || !Array.isArray(params.value) || params.value.length === 0) {
            return null;
          }
          const tagText = params.value.join(', ');
          return (
            <div title={tagText}>
              <div className='flex gap-1'>
                {params.value.slice(0, 2).map((tag: string, index: number) => (
                  <UITextLabel key={index} intent='tag' className='nowrap'>
                    {tag}
                  </UITextLabel>
                ))}
              </div>
            </div>
          );
        },
      },
      // 251107_퍼블수정 그리드 컬럼 속성 '태그' 영역 수정 E
    ],
    []
  );

  // 그리드 컬럼 설정2
  const modelColumnDefs2: any = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'id' as any,
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
        } as any,
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      // 251113_퍼블수정 그리드 컬럼 속성 '이름' 영역 수정 S
      {
        headerName: '이름',
        field: 'name' as any,
        width: 272,
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
      // 251113_퍼블수정 그리드 컬럼 속성 '이름' 영역 수정 E
      {
        headerName: '구분',
        field: 'type' as any,
        width: 230,
      },
      {
        headerName: 'Key',
        field: 'apiKey' as any,
        flex: 1,
        cellRenderer: React.memo((params: any) => {
          return (
            <div className='flex align-center gap-1'>
              {params.value}
              <a href='#none' onClick={() => handleCopy('복사가 완료되었습니다.')}>
                <UIIcon2 className='ic-system-20-copy-gray' style={{ display: 'block' }} />
              </a>
            </div>
          );
        }),
        cellStyle: {
          paddingLeft: '16px',
        },
        tooltipValueGetter: () => null,
      },
      {
        headerName: 'Quota',
        field: 'quota',
        width: 180,
      },
      {
        headerName: '호출 횟수',
        field: 'callcount',
        width: 180,
      },
    ],
    []
  );

  // 그리드 데이터
  const modelRowData = [
    {
      id: '1',
      name: '예적금 상품 Q&A 세트',
      status: '업로드중',
      description: '정기예금 등 기본 수신상품에 대한 고객 질문과 상담',
      tags: ['가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자', '가나다라마바사아자'],
      publicRange: '전체공유',
      catogory: '지도학습',
      deployDate: '2025.03.24 18:23:43',
    },
    {
      id: '2',
      name: '신용대출 조건 분류 데이터',
      status: '취소',
      description: '대출 가능성 분류 라벨이 포함된 데이터',
      tags: ['가나다라마바사아', '가나다라마바사아'],
      publicRange: '전체공유',
      catogory: '지도학습',
      deployDate: '2025.03.24 15:30:21',
    },
  ];

  // 그리드 데이터2
  const modelRowData2 = [
    {
      id: '1',
      name: '예적금 상품 Q&A 세트',
      type: '사용자',
      apiKey: 'shinhan-319bd91213e8446834d',
      quota: '20회 / 1일',
      callcount: '10회 / 1일',
    },
    {
      id: '2',
      name: '신용대출 조건 분류 데이터',
      type: '기타',
      apiKey: 'shinhan-319bd91213e8446834d',
      quota: '20회 / 1일',
      callcount: '10회 / 1일',
    },
    {
      id: '3',
      name: '신용대출 조건 분류 데이터',
      type: '기타',
      apiKey: 'shinhan-319bd91213e8446834d',
      quota: '20회 / 1일',
      callcount: '10회 / 1일',
    },
  ];

  return (
    <DesignLayout
      initialMenu={{ id: 'data', label: '데이터' }}
      initialSubMenu={{
        id: 'data-catalog',
        label: '지식/학습 데이터 관리', // [251111_퍼블수정] 타이틀명칭 변경 : 데이터 카탈로그 > 지식/학습 데이터 관리
        icon: 'ico-lnb-menu-20-data-catalog',
      }}
    >
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='모델 배포 조회'
          description=''
          actions={
            <>
              <UIButton2 className='btn-tertiary-outline line-only-blue'>모델 상세</UIButton2>
            </>
          }
        />

        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle className='article-tabs'>
            {/* 아티클 탭 */}
            <UITabs items={tabItems} activeId='tab1' size='large' onChange={setActiveTab} />
          </UIArticle>
          {/* 테이블 */}
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                모델 정보
              </UITypography>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  {/* [251105_퍼블수정] width값 수정 */}
                  <colgroup>
                    <col style={{ width: '10%' }} />
                    <col style={{ width: '40%' }} />
                    <col style={{ width: '10%' }} />
                    <col style={{ width: '40%' }} />
                  </colgroup>
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          모델명
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          GPT-4 Callcenter-Tuned
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          표시 이름
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          GPT-4-Callcenter-Tuned
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          설명
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          GPT-4-Callcenter-Tuned
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          공급사
                        </UITypography>
                      </th>
                      <td>
                        <UIUnitGroup gap={4} direction='row' vAlign='center'>
                          <UIIcon2 className='ic-system-24-google' />
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            Google
                          </UITypography>
                        </UIUnitGroup>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          모델타입
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          language
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          배포타입
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          self-hosting
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>

          {/* 테이블 */}
          <UIArticle>
            <div className='article-header'>
              <UIUnitGroup direction='row' align='space-between' gap={0}>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  배포 정보
                </UITypography>
                <UIButton2 className='btn-option-outlined'>재시도</UIButton2>
              </UIUnitGroup>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  {/* [251105_퍼블수정] width값 수정 */}
                  <colgroup>
                    <col style={{ width: '10%' }} />
                    <col style={{ width: '40%' }} />
                    <col style={{ width: '10%' }} />
                    <col style={{ width: '40%' }} />
                  </colgroup>
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          배포명
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          콜센터 응대 특화 모델
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          운영계 배포 여부
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          배포
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          설명
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          고객 상담 응대 문장 생성에 최적화된 대형 언어모델
                        </UITypography>
                      </td>
                    </tr>
                    <tr style={{ height: '68px' }}>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          A.X 배포상태
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          <UILabel variant='badge' intent='complete'>
                            이용 가능
                          </UILabel>
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          API Gateway <br /> 배포 상태
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          <UILabel variant='badge' intent='error'>
                            실패
                          </UILabel>
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          Inflight Quantization
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          Y
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          복제 인스터스 수
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          1
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          프레임워워크
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          vLLM
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          Endpoint
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          <div className='flex align-center gap-2'>
                            https://aip.sktai.io/api/v1/model_gateway/d8826b97-7373-4dba-973a-6d5228025aaa
                            <a href='#none'>
                              <UIIcon2 className='ic-system-20-copy-gray' style={{ display: 'block' }} />
                            </a>
                          </div>
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          입력 세이프티 필터
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          의료/건강 민감어, 정치적 민감어
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          출력 세이프티 필터
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          의료/건강 민감어, 정치적 민감어
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>

          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                자원 할당량 및 사용률
              </UITypography>
            </div>
            {/*  리소스 정보 + 리소스 차트 */}
            <div className='flex items-center gap-[80px]'>
              <div className='flex-1 flex justify-center'>
                <div className='flex justify-between items-center'>
                  <div className='chart-item flex-1'>
                    <div className='flex chart-graph  h-[218px] gap-x-10 justify-self-center'>
                      <div className='w-[280px] flex items-center justify-center'>
                        <UICircleChart.Half type='CPU' value={50} total={98} />
                      </div>
                      <div className='w-[280px] flex items-center justify-center'>
                        <UICircleChart.Half type='Memory' value={20} total={100} />
                      </div>
                      <div className='w-[280px] flex items-center justify-center'>
                        <UICircleChart.Half type='GPU' value={128.72} total={1007.4} />
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </UIArticle>

          {/* 그리드 */}
          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='w-full'>
                  <UIUnitGroup gap={16} direction='column'>
                    <div className='flex justify-between w-full items-center'>
                      <div className='w-full'>
                        <UIGroup gap={12} direction='row' align='start'>
                          <div style={{ width: '200px', display: 'flex', alignItems: 'center' }}>
                            <UIDataCnt count={2} prefix='학습 데이터세트 총' unit='건' />
                          </div>
                        </UIGroup>
                      </div>
                    </div>
                  </UIUnitGroup>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid rowData={modelRowData} columnDefs={modelColumnDefs} onClickRow={(_params: any) => {}} />
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination currentPage={1} totalPages={1} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>

          {/* 그리드 */}
          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='w-full'>
                  <UIUnitGroup gap={16} direction='column'>
                    <div className='flex justify-between w-full items-center'>
                      <div className='w-full'>
                        <UIGroup gap={12} direction='row' align='start'>
                          <div style={{ width: '200px', display: 'flex', alignItems: 'center' }}>
                            <UIDataCnt count={3} prefix='API Key 총' unit='건' />
                          </div>
                        </UIGroup>
                      </div>
                      <div>
                        <UIButton2 className='btn-tertiary-outline'>발급</UIButton2>
                      </div>
                    </div>
                  </UIUnitGroup>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid type='default' rowData={modelRowData2} columnDefs={modelColumnDefs2} onClickRow={(_params: any) => {}} />
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination currentPage={1} totalPages={1} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>

          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                Advanced Setting
              </UITypography>
            </div>
            <div className='article-body'>
              {/* 소스코드 영역 */}
              <UICode value={'여기는 에디터 화면입니다. 테스트 테스트'} language='python' theme='dark' width='100%' minHeight='300px' maxHeight='500px' readOnly={false} />
            </div>
          </UIArticle>

          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                예시 코드
              </UITypography>
            </div>
            <div className='article-body'>
              {/* 소스코드 영역 */}
              <UICode value={'여기는 에디터 화면입니다. 테스트 테스트'} language='python' theme='dark' width='100%' minHeight='300px' maxHeight='500px' readOnly={false} />
            </div>
          </UIArticle>

          {/* 테이블 */}
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                담당자 정보
              </UITypography>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  {/* [251105_퍼블수정] width값 수정 */}
                  <colgroup>
                    <col style={{ width: '10%' }} />
                    <col style={{ width: '40%' }} />
                    <col style={{ width: '10%' }} />
                    <col style={{ width: '40%' }} />
                  </colgroup>
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          생성자
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          [퇴사] 김신한 ㅣ Data기획Unit
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          생성일시
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          2025.03.24 18:23:43
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          최종 수정자
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          [재직] 박신한 | AI Unit
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          최종 수정일시
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          2025.06.24 18:23:43
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>

          {/* 테이블 */}
          <UIArticle>
            <div className='article-header'>
              <UIUnitGroup direction='row' align='space-between' gap={0}>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  프로젝트 정보
                </UITypography>
                <UIButton2 className='btn-option-outlined'>공개 설정</UIButton2>
              </UIUnitGroup>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  {/* [251105_퍼블수정] width값 수정 */}
                  <colgroup>
                    <col style={{ width: '10%' }} />
                    <col style={{ width: '40%' }} />
                    <col style={{ width: '10%' }} />
                    <col style={{ width: '40%' }} />
                  </colgroup>
                  <tbody>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          생성자
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          전체공 ㅣ 공개 그룹
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          권한 수정자
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          권신태ㅣ AI Unit
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>
        </UIPageBody>

        {/* 페이지 footer */}
        <UIPageFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='center'>
              <UIButton2 className='btn-primary-gray'>삭제</UIButton2>
              <UIButton2 className='btn-primary-blue'>수정</UIButton2>
            </UIUnitGroup>
          </UIArticle>
        </UIPageFooter>
      </section>
    </DesignLayout>
  );
};
