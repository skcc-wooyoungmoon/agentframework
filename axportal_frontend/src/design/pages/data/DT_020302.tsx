import React from 'react';
import { UIDataCnt, UILabel } from '@/components/UI';
import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIPagination } from '@/components/UI/atoms/UIPagination';
import { UIGroup, UIInput, UIUnitGroup, UIPercentBar } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageFooter } from '@/components/UI/molecules/UIPageFooter';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';

import { useMemo, useState } from 'react';
import { DesignLayout } from '../../components/DesignLayout';

const rowData = [
  {
    no: 1,
    id: '1',
    mdFileName: '입출금이자유로운예금_상품설명서.md',
    title: '입출금이자유로운예금 상품 안내',
    attachmentFileName: '입출금이자유로운예금_상품설명서.pdf',
    uuid: '23ㄴdac10b-58cc-4372-a567',
    knowledgeDataName: '신용대출 상품 설명서',
  },
  {
    no: 2,
    id: '2',
    mdFileName: '신한_신용대출_대상조건표.md',
    title: '신한 신용대출 대상조건 안내',
    attachmentFileName: '신한_신용대출_대상조건표.xlsx',
    uuid: '23ㄴdac10b-58cc-4372-a567',
    knowledgeDataName: '신용대출 상품 설명서',
  },
  {
    no: 3,
    id: '3',
    mdFileName: '신한_모바일대출_이용가이드.md',
    title: '신한 모바일대출 이용 가이드',
    attachmentFileName: '신한_모바일대출_이용가이드.pdf',
    uuid: '23ㄴdac10b-58cc-4372-a567',
    knowledgeDataName: '신용대출 상품 설명서',
  },
  {
    no: 4,
    id: '4',
    mdFileName: '신한_비상금대출_FAQ.md',
    title: '신한 비상금대출 자주 묻는 질문',
    attachmentFileName: '신한_비상금대출_FAQ.pdf',
    uuid: '23ㄴdac10b-58cc-4372-a567',
    knowledgeDataName: '신용대출 상품 설명서',
  },
  {
    no: 5,
    id: '5',
    mdFileName: '신한은행_신용대출_약정서.md',
    title: '신한은행 신용대출 약정서',
    attachmentFileName: '신한은행_신용대출_약정서.pdf',
    uuid: '23ㄴdac10b-58cc-4372-a567',
    knowledgeDataName: '신용대출 상품 설명서',
  },
  {
    no: 6,
    id: '6',
    mdFileName: '신한은행_직군별대출가능현황.md',
    title: '신한은행 직군별 대출 가능 현황',
    attachmentFileName: '신한은행_직군별대출가능현황.xlsx',
    uuid: '23ㄴdac10b-58cc-4372-a567',
    knowledgeDataName: '신용대출 상품 설명서',
  },
];

export const DT_020302 = () => {
  const [percentValue] = useState(50);
  const [percentStatus] = useState<'success' | 'error' | 'default'>('default');

  // dropdown 상태
  const [value, setValue] = useState('1');

  // 색상 결정 로직
  const getPercentColor = () => {
    if (percentStatus === 'error') {
      return '#D61111';
    }
    if (percentStatus === 'success') {
      return '#005DF9';
    }
    if (percentStatus === 'default') {
      if (percentValue === 100) {
        return '#005DF9';
      }
      if (percentValue === 0) {
        return '#8B95A9';
      }
      if (percentValue >= 1 && percentValue <= 99) {
        return '#545454';
      }
    }
    return '#545454';
  };

  const columnDefs = useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no',
        width: 56,
        minWidth: 56,
        maxWidth: 56,
        cellClass: 'text-center',
        headerClass: 'text-center',
        cellStyle: () => ({
          textAlign: 'center' as const,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        }),
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: 'MD파일명',
        field: 'mdFileName',
        width: 418,
        sortable: false,
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
        headerName: '타이틀',
        field: 'title',
        flex: 1,
        sortable: false,
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
        headerName: '첨부파일 이름',
        field: 'attachmentFileName',
        width: 280,
        sortable: false,
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
        headerName: 'UUID',
        field: 'uuid',
        width: 320,
        sortable: false,
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
        headerName: '지식 데이터명',
        field: 'knowledgeDataName',
        width: 180,
        sortable: false,
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
    ],
    []
  );

  // search 타입
  const [searchValue, setSearchValue] = useState('');

  return (
    <DesignLayout>
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader
          title='지식 조회' // [251111_퍼블수정] 타이틀명칭 변경 : 지식/학습 데이터 관리 > 지식 조회
          description=''
          // actions={
          //   <>
          //     <UIButton2 className='btn-tertiary-outline line-only-blue'>데이터 가져오기</UIButton2>
          //   </>
          // }
        />

        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle>
            <div className='article-header'>
              <UIGroup gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  지식 정보
                </UITypography>
                <UITypography variant='body-2' className='secondary-neutral-600'>
                  지식 상태는 Script의 정합성에 의해 결정됩니다. 상태가 비활성화인 경우, Script와 지식 설정값이 올바르게 입력되었는지 지식 수정 화면에서 확인하세요.
                </UITypography>
              </UIGroup>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  {/* [251106_퍼블수정] width값 수정 */}
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
                          이름
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          신용대출 상품 설명서
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          지식 상태
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          <UILabel variant='badge' intent='complete'>
                            활성화
                          </UILabel>
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
                          연체가능한 예측 모델
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          벡터 DB
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          [비정형데이터플랫폼] Elasticsearch
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          임베딩 모델
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          GIP/text-embedding-3-large-new
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          청킹방법
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          RecursiveCharacter
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          지식 유형
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          기본지식
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          인덱스명
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          인덱스명12345
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          데이터 최초 적재 상태
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          진행중
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>

          <UIArticle>
            <div className='flex article-header items-end'>
              <UIGroup gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  데이터 최초 적재 현황
                </UITypography>
                <UITypography variant='body-2' className='secondary-neutral-600'>
                  데이터 적재 진행 중 인덱싱 작업에서 데이터 오류 등으로 적재에 실패한 파일은 상단의 [적재 실패 로그] 버튼을 통해 조회할 수 있습니다.
                </UITypography>
              </UIGroup>
              <div className='justify-end'>
                <UIButton2 className='btn-tertiary-gray col-gray'>적재 실패 로그</UIButton2>
              </div>
            </div>

            <div className='article-body'>
              {/* 데이터 카드영역 추가 */}
              <div className='card-default type-progress'>
                <div className='card-list-wrapper'>
                  <div className='card-list'>
                    <div className='card-list-progress'>
                      {/*
                        default : 노말상태
                        fail : 실패한경우
                        success : 성공한경우
                      */}
                      <UIPercentBar value={percentValue} color='#005DF9' height={8} status={percentStatus as any} />
                    </div>
                  </div>
                  <div className='card-list'>
                    {/* 프로그래스바 - 로딩 상태 영역 추가 : card-loading 노출/비노출 처리필요 */}
                    <div className='card-loading'>
                      <div className='loading-sm'></div>
                    </div>
                    <UIGroup direction='column' gap={6} vAlign='center'>
                      <UITypography variant='body-1' className='secondary-neutral-500'>
                        진행률
                      </UITypography>
                      <UITypography variant='title-3' className='primary-800' style={{ color: getPercentColor() }}>
                        {percentValue}%
                      </UITypography>
                    </UIGroup>
                  </div>
                </div>
              </div>
            </div>
          </UIArticle>

          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                동기화 정보
              </UITypography>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  {/* [251106_퍼블수정] width값 수정 */}
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
                          동기화 여부
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          Y
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          동기화 대상
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          개발계
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          동기화 상태
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          <UILabel variant='badge' intent='complete'>
                            정상
                          </UILabel>
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>

          <UIArticle className='article-grid'>
            {/* 다중 선택 그리드 */}
            <UIListContainer>
              <UIListContentBox.Header>
                <UIGroup gap={8} direction='row' align='start'>
                  <div style={{ width: '168px', paddingRight: '8px' }}>
                    <UIDataCnt count={6} prefix='지식데이터 총' unit='건' />
                  </div>
                </UIGroup>
                <div className='flex items-center gap-2'>
                  <div style={{ width: '160px', flexShrink: 0 }}>
                    <UIDropdown
                      value={String(value)}
                      options={[
                        { value: '1', label: '파일명' },
                        { value: '2', label: 'UUID' },
                      ]}
                      onSelect={(value: string) => {
                        setValue(value);
                      }}
                      onClick={() => {}}
                      height={40}
                      variant='dataGroup'
                      disabled={false}
                    />
                  </div>
                  <div className='w-[360px]'>
                    <UIInput.Search
                      value={searchValue}
                      onChange={e => {
                        setSearchValue(e.target.value);
                      }}
                      placeholder='검색어 입력'
                    />
                  </div>
                </div>
              </UIListContentBox.Header>
              <UIListContentBox.Body>
                <UIGrid type='default' rowData={rowData} columnDefs={columnDefs as any} onClickRow={(_params: any) => {}} onCheck={(_selectedIds: any[]) => {}} />
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination currentPage={1} totalPages={3} onPageChange={() => {}} className='flex justify-center' />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                담당자 정보
              </UITypography>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  {/* [251106_퍼블수정] width값 수정 */}
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
                          김신한
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
                          홍길동
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          최종 수정일시
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          2025.05.10 18:23:43
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
              <UIUnitGroup direction='row' align='space-between' gap={0}>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  프로젝트 정보
                </UITypography>
                <UIButton2 className='btn-option-outlined'>공개설정</UIButton2>
              </UIUnitGroup>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
                  {/* [251106_퍼블수정] width값 수정 */}
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
                          전체공유 | 공개 그룹
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          권한 수정자
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          권신태 ㅣ AI Unit
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
