import { useEffect, useMemo, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { UIButton2, UIPagination, UITypography } from '@/components/UI/atoms';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import { UIPageHeader } from '@/components/UI/molecules/UIPageHeader';
import { UIUnitGroup } from '@/components/UI/molecules';
import { useModal } from '@/stores/common/modal';
import { KnowledgeChunkInfoPopupContent } from './KnowledgeChunkInfoPopup';
import { useGetExternalReposFileChunks } from '@/services/knowledge/knowledge.services';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';

type PhraseDataType = {
  id: string;
  title: string;
  content: string;
  metadata?: string;
};

export const KnowledgeFileDetailPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { openAlert, openModal } = useModal();
  const [currentPage, setCurrentPage] = useState<number>(1);
  const pageSize = 12;

  // 이전 페이지에서 전달받은 파일 데이터
  const fileData = location.state?.fileData;

  // console.log('fileData........', fileData);

  // External Knowledge 지식데이터(MD) 목록 조회 - Backend API 연동
  // indexName?: string;
  // docPathAnony?: string;
  // page?: number;
  // countPerPage?: number;
  const {
    data: externalReposFileChunks,
    error,
    refetch,
  } = useGetExternalReposFileChunks({
    indexName: fileData?.indexName,
    docPathAnony: fileData.docPathAnony,
    page: currentPage,
    countPerPage: pageSize,
  });

  // console.log('externalReposFileChunks........', externalReposFileChunks);

  const phrasesData = useMemo(() => {
    if (!externalReposFileChunks?.page?.content) {
      return [];
    }

    return externalReposFileChunks?.page?.content.map((item: any, index: number) => {
      return {
        // 그리드 표시용 필드
        no: (currentPage - 1) * pageSize + index + 1,
        id: item?.id,
        title: `phrases_#${item?.source?.chunk_seq}`,
        content: item?.source?.chunk_conts,
        metadata: item?.source,
      };
    });
  }, [externalReposFileChunks, currentPage]);

  // phrase 클릭 핸들러
  const handlePhraseClick = async (phrase: PhraseDataType) => {
    // console.log('phrase........', phrase);

    await openModal({
      type: 'large',
      title: '청크 정보',
      showFooter: false,
      body: <KnowledgeChunkInfoPopupContent content={phrase.content} metadata={JSON.stringify(phrase.metadata, null, 2)} />,
    });
  };

  // 그리드 페이지 이동
  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  // state가 없으면 이전 페이지로 리다이렉트
  useEffect(() => {
    if (!fileData) {
      openAlert({
        title: '알림',
        message: '파일 정보를 찾을 수 없습니다.',
      }).then(() => {
        navigate(-1); // 이전 페이지로 이동
      });
    }
  }, [fileData, navigate, openAlert]);

  // searchValues 변경 시 refetch
  useEffect(() => {
    refetch();
  }, [currentPage]);

  // 데이터가 없으면 렌더링하지 않음
  if (!fileData) {
    return null;
  }

  // 에러 처리
  if (error) {
    // console.error('API 에러:', error);
  }

  return (
    <>
      {/* 섹션 페이지 */}
      <section className='section-page'>
        {/* 페이지 헤더 */}
        <UIPageHeader title='지식파일 상세' description='' />
        {/* 페이지 바디 */}
        <UIPageBody>
          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                지식데이터 정보
              </UITypography>
            </div>
            <div className='article-body'>
              <div className='border-t border-black'>
                <table className='tbl-v'>
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
                          MD파일 명
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {fileData.dataName || ''}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          타이틀
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {fileData.title || ''}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          첨부파일 이름
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {fileData.attachName || ''}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          UUID
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {fileData.uuid || ''}
                        </UITypography>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          지식데이터
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {fileData.type || ''}
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>

          <UIArticle>
            <UIListContainer>
              <UIListContentBox.Body className='flex-col'>
                {/* phrases 데이터 동적 렌더링 */}
                {phrasesData.map(phrase => (
                  <UIArticle key={phrase.id} className='w-full'>
                    <UIUnitGroup gap={8} direction='column' align='start'>
                      {/* [참고] 타이틀영역은 버튼이 아니라고합니다 */}
                      <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                        {phrase.title}
                      </UITypography>
                      {/* 내용 영역 클릭 가능하게 */}
                      <div className='bg-gray-100 rounded-xl px-5 py-5'>
                        <UIButton2 className='cursor-pointer text-left' onClick={() => handlePhraseClick(phrase)}>
                          <UITypography variant='body-2' className='secondary-neutral-600 mt-1'>
                            {phrase.content}
                          </UITypography>
                        </UIButton2>
                      </div>
                    </UIUnitGroup>
                  </UIArticle>
                ))}
              </UIListContentBox.Body>
              <UIListContentBox.Footer>
                <UIPagination
                  currentPage={currentPage}
                  totalPages={externalReposFileChunks?.page?.totalPages || 1}
                  onPageChange={handlePageChange}
                  className='flex justify-center'
                />
              </UIListContentBox.Footer>
            </UIListContainer>
          </UIArticle>
        </UIPageBody>
        {/* <UIPageFooter></UIPageFooter> */}
      </section>
    </>
  );
};
