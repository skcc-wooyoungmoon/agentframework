import React, { useMemo, useState } from 'react';

import { UIButton2, UIDataCnt, UILabel, UITypography } from '@/components/UI/atoms';
import { UIIcon2 } from '@/components/UI/atoms/UIIcon2';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGroup, UIUnitGroup } from '@/components/UI/molecules';
import { UICircleChart } from '@/components/UI/molecules/chart';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UITostRenderer } from '@/components/UI/molecules/toast/UITostRenderer/components';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageFooter } from '@/components/UI/molecules/UIPageFooter';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { useToast } from '@/hooks/common/toast/useToast';
import { useModal } from '@/stores/common/modal';
import { UITabs } from '../../../components/UI/organisms/UITabs';
import { DesignLayout } from '../../components/DesignLayout';

export const DP_020102 = () => {
  const [, setActiveTab] = useState('tab1');
  // 필터 상태
  const { openAlert } = useModal();
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

  // UIGrid 컬럼 설정
  const columnDefs: any = useMemo(
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
      {
        headerName: '노드명',
        field: 'nodeName' as any,
        width: 392,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '노드 종류',
        field: 'nodeType' as any,
        width: 392,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '지식명',
        field: 'knowledgeName' as any,
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    []
  );

  // UIGrid 로우 데이터 (사용 모델)
  const rowData = [
    {
      id: '1',
      nodeName: 'RAG 노드',
      nodeType: 'Knowledge',
      knowledgeName: '신한 금융상품 지식',
    },
  ];

  // API Key 그리드 컬럼 설정
  const apiKeyColumnDefs: any = useMemo(
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
      {
        headerName: '노드명',
        field: 'nodeName' as any,
        width: 272,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '상태',
        field: 'status' as any,
        width: 120,
        cellStyle: {
          paddingLeft: '16px',
        },
        cellRenderer: React.memo((params: any) => {
          const getStatusIntent = (status: string) => {
            switch (status) {
              case '사용가능':
                return 'complete';
              case '사용차단':
                return 'error';
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
        headerName: '구분',
        field: 'nodeType' as any,
        width: 230,
        cellStyle: {
          paddingLeft: '16px',
        },
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
        field: 'apiKeyName' as any,
        width: 150,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '호출횟수',
        field: 'createdDate' as any,
        width: 150,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
    ],
    []
  );

  // API Key 그리드 로우 데이터
  const apiKeyRowData = [
    {
      id: '1',
      nodeName: '김수민',
      status: '사용가능',
      nodeType: '에이전트 (이건 모델에만?)',
      apiKey: 'shinhan-319bd91213e8446834d',
      apiKeyName: '20회 / 1일',
      createdDate: '10회 / 1일',
    },
    {
      id: '2',
      nodeName: '슈퍼쏠',
      status: '사용차단',
      nodeType: '에이전트 (이건 모델에만?)',
      apiKey: 'shinhan-319bd91213e8446834d',
      apiKeyName: '20회 / 1일',
      createdDate: '10회 / 1일',
    },
  ];

  // 사용 모델 그리드 컬럼 설정
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
      {
        headerName: '버전',
        field: 'version' as any,
        width: 272,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: 'A.X 배포 상태',
        field: 'statusType' as any,
        width: 470,
        cellStyle: {
          paddingLeft: '16px',
        },
        cellRenderer: React.memo((params: any) => {
          const getStatusIntent = (status: string) => {
            switch (status) {
              case '이용 가능':
                return 'complete';
              case '성공':
                return 'complete';
              case '실패':
                return 'error';
              case '중지':
                return 'warning';
              case '배포중':
                return 'progress';
              case '진행중':
                return 'progress';
              case '종료':
                return 'neutral';
              case '알수없음':
                return 'neutral';
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
        headerName: '운영계 배포 여부',
        field: 'release' as any,
        flex: 1,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '배포일',
        field: 'deployDate' as any,
        width: 180,
        maxWidth: 180,
        cellStyle: {
          paddingLeft: '16px',
        },
      },
      {
        headerName: '',
        field: 'more', // 더보기 컬럼 필드명 (고정)
        width: 56,
      },
    ],
    []
  );

  // 더보기 메뉴 설정
  const moreMenuConfig = useMemo(
    () => ({
      items: [
        {
          label: '시작',
          action: 'run',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `테스트 "${rowData.name}" 실행을 시작합니다.`,
            });
          },
        },
        {
          label: '중지',
          action: 'modify',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `테스트 "${rowData.name}" 중지 팝업을 엽니다.`,
            });
          },
        },
        {
          label: '삭제',
          action: 'delete',
          onClick: (rowData: any) => {
            openAlert({
              title: '안내',
              message: `테스트 "${rowData.name}" 삭제 화면으로 이동합니다.`,
            });
          },
        },
      ],
      isActive: () => true, // 모든 테스트에 대해 활성화
    }),
    []
  );

  // 사용 모델 그리드 로우 데이터
  const modelRowData = [
    {
      id: '1',
      version: 'v1.2.0',
      statusType: '이용 가능',
      development: 'shared',
      release: '배포',
      deployDate: '2025.03.24 18:23:43',
      more: 'more',
    },
    {
      id: '2',
      version: 'v2.1.0',
      statusType: '중지',
      development: 'shared',
      release: '미배포',
      deployDate: '2025.03.24 15:30:21',
      more: 'more',
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
      <UITostRenderer position='bottom-center' />
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='에이전트 배포 조회'
          description=''
          actions={
            <>
              <UIButton2 className='btn-tertiary-outline line-only-blue'>채팅 테스트</UIButton2>
            </>
          }
        />

        {/* 페이지 바디 */}
        <UIPageBody>
          {/* 테이블 */}
          <UIArticle>
            <UIArticle className='article-tabs'>
              {/* 아티클 탭 */}
              <UITabs items={tabItems} activeId='tab1' size='large' onChange={setActiveTab} />
            </UIArticle>
            {/* 테이블 */}
            <UIArticle></UIArticle>
            <div className='article-header'>
              <UIGroup direction='column' gap={8}>
                <UIUnitGroup direction='row' align='space-between' gap={0}>
                  <UITypography variant='title-4' className='secondary-neutral-900'>
                    에이전트 배포 정보
                  </UITypography>
                  <UIButton2 className='btn-option-outlined'>재시도</UIButton2>
                </UIUnitGroup>
                <UITypography variant='body-2' className='secondary-neutral-600'>
                  A.X 배포 상태와 API Gateway 배포 상태는 APP 버전의 상태값입니다.
                </UITypography>
              </UIGroup>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  {/*  [251105_퍼블수정] width값 수정 */}
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
                          신한 스마트콜봇
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          배포유형
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          기본
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          빌더
                        </UITypography>
                      </th>
                      <td>
                        <UIUnitGroup gap={16} direction='row' vAlign='center'>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            0509 신한 스마트콜봇 캔버스
                          </UITypography>
                          <UIButton2 className='btn-text-14-point' rightIcon={{ className: 'ic-system-12-arrow-right-blue', children: '' }}>
                            빌더 바로가기
                          </UIButton2>
                        </UIUnitGroup>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          설명
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          신한 스마트콜봇(RAG, 고객센터 챗봇 예제 사용), 금융상품 설명봇 참고용
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          A.X 배포상태
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          <UILabel variant='badge' intent='complete'>
                            이용가능
                          </UILabel>
                          {/*
                          [라벨 - 아이콘 참고]
                          '이용 가능':'complete'
                          '성공':'complete'
                          '실패':'error'
                          '중지':'warning'
                          '배포중':'progress'
                          '진행중':'progress'
                          '종료':'neutral'
                          '알수없음':'neutral'
                           */}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          API Gateway
                          <br />
                          배포상태
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
                          운영계 배포 여부
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          배포
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
                            <a href='#none' onClick={() => handleCopy('토스트 팝업입니다. 최대 글자수는 20글자')}>
                              <UIIcon2 className='ic-system-20-copy-gray' style={{ display: 'block' }} />
                            </a>
                          </div>
                        </UITypography>
                      </td>
                    </tr>
                    {/* <tr>
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
                    </tr> */}
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          cURL 코드
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          <UIButton2
                            className='btn-text-14-underline-point imp-underline_16'
                            rightIcon={{ className: 'ic-16 ic-system-24-outline-blue-export ipt-16', children: '' }}
                          >
                            코드 확인하기
                          </UIButton2>
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          Python 코드
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          <UIButton2
                            className='btn-text-14-underline-point imp-underline_16'
                            rightIcon={{ className: 'ic-16 ic-system-24-outline-blue-export ipt-16', children: '' }}
                          >
                            코드 확인하기
                          </UIButton2>
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
                    <div className='flex chart-graph h-[218px] gap-x-10 justify-between'>
                      <div className='w-[280px] flex items-center justify-center'>
                        <UICircleChart.Half type='CPU' value={50} total={98} showLabel={true} /> {/* [참고] showLabel : 그래프 하단의 라벨(사용중인 자원) 숨김 처리 */}
                        {/* #### 기획 확인후 수정필요 ####  [251219_퍼블수정필요] : type='CPU' >  type='CPU(Core)' 로 수정해야하는데 공통으로 모든 반원 차트가 바뀌는건지? type 명이 개별적으로 바뀌는건지? (체크 필요) */}
                      </div>
                      <div className='w-[280px] flex items-center justify-center'>
                        <UICircleChart.Half type='Memory' value={20} total={100} showLabel={true} />
                        {/* #### 기획 확인후 수정필요 ####  [251219_퍼블수정필요] : type='CPU' >  type='CPU(Core)' 로 수정해야하는데 공통으로 모든 반원 차트가 바뀌는건지? type 명이 개별적으로 바뀌는건지? (체크 필요) */}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </UIArticle>

          {/* 그리드 : 1번 */}
          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='w-full'>
                  <UIUnitGroup gap={16} direction='column'>
                    <div className='flex justify-between w-full items-center'>
                      <div className='w-full'>
                        <UIGroup gap={12} direction='row' align='start'>
                          <div style={{ width: '200px', display: 'flex', alignItems: 'center' }}>
                            <UIDataCnt count={10} prefix='사용 모델 총' unit='건' />
                          </div>
                        </UIGroup>
                      </div>
                    </div>
                  </UIUnitGroup>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid rowData={rowData} columnDefs={columnDefs} onClickRow={(_params: any) => {}} />
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination currentPage={1} totalPages={5} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>

          {/* 그리드 : 1번 */}
          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='w-full'>
                  <UIUnitGroup gap={16} direction='column'>
                    <div className='flex justify-between w-full items-center'>
                      <div className='w-full'>
                        <UIGroup gap={12} direction='row' align='start'>
                          <div style={{ width: '200px', display: 'flex', alignItems: 'center' }}>
                            <UIDataCnt count={10} prefix='학습데이터 총' unit='건' />
                          </div>
                        </UIGroup>
                      </div>
                    </div>
                  </UIUnitGroup>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid rowData={rowData} columnDefs={columnDefs} onClickRow={(_params: any) => {}} />
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination currentPage={1} totalPages={5} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>

          {/* 그리드 : 2번 */}
          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='w-full'>
                  <UIUnitGroup gap={16} direction='column'>
                    <div className='flex justify-between w-full items-center'>
                      <div className='w-full'>
                        <UIGroup gap={12} direction='row' align='start'>
                          <div className='flex justify-between w-full items-center'>
                            <div style={{ width: '200px', display: 'flex', alignItems: 'center' }}>
                              <UIDataCnt count={10} prefix='API Key 총' unit='건' />
                            </div>
                            <div>
                              <UIButton2 className='btn-tertiary-outline'>발급</UIButton2>
                            </div>
                          </div>
                        </UIGroup>
                      </div>
                    </div>
                  </UIUnitGroup>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid type='multi-select' rowData={apiKeyRowData} columnDefs={apiKeyColumnDefs} onClickRow={(_params: any) => {}} />
              </UIListContentBox.Body>
              <UIListContentBox.Footer className='ui-data-has-btn'>
                <UIButton2 className='btn-option-outlined' style={{ width: '40px' }}>
                  삭제
                </UIButton2>
                <UIPagination currentPage={1} totalPages={5} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>

          {/* 그리드 : 3번 */}
          <UIArticle className='article-grid'>
            <UIListContainer>
              <UIListContentBox.Header>
                <div className='w-full'>
                  <UIUnitGroup gap={16} direction='column'>
                    <div className='flex justify-between w-full items-center'>
                      <div className='w-full'>
                        <UIGroup gap={12} direction='row' align='start'>
                          <div style={{ width: '200px', display: 'flex', alignItems: 'center' }}>
                            <UIDataCnt count={10} prefix='버전 정보 총' unit='건' />
                          </div>
                        </UIGroup>
                      </div>
                    </div>
                  </UIUnitGroup>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid rowData={modelRowData} columnDefs={modelColumnDefs} moreMenuConfig={moreMenuConfig} onClickRow={(_params: any) => {}} />
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination currentPage={1} totalPages={5} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
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
                  {/*  [251105_퍼블수정] width값 수정 */}
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
                  {/*  [251105_퍼블수정] width값 수정 */}
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
                          공개범위
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          전체공 | 공개 프로젝트
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
