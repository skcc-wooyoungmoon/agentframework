import { useState } from 'react';
import { useParams } from 'react-router-dom';

import { UIFileBox, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIGroup, UIPageBody, UIPageHeader } from '@/components/UI/molecules';
import { downloadNoticeFile, useGetNoticeById } from '@/services/notice';
import { useModal } from '@/stores/common/modal';

const formatDateTime = (input?: string | number | Date) => {
  if (!input) return '';
  const d = new Date(input);
  if (isNaN(d.getTime())) return '';
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  const hh = String(d.getHours()).padStart(2, '0');
  const mm = String(d.getMinutes()).padStart(2, '0');
  const ss = String(d.getSeconds()).padStart(2, '0');
  return `${y}.${m}.${day} ${hh}:${mm}:${ss}`;
};

export const NotiDetailPage = () => {
  const { id } = useParams<{ id: string }>();

  // 다운로드 진행률 상태 (fileId -> progress)
  const [downloadProgress, setDownloadProgress] = useState<Record<number, number>>({});
  const [downloadingFileId, setDownloadingFileId] = useState<number | null>(null);

  const { openAlert } = useModal();
  const showAlert = (message: string) => {
    openAlert({
      message,
      title: '알림',
      confirmText: '확인',
    });
  };

  // API 호출
  const { data: noticeDetail } = useGetNoticeById(id ? { noticeId: id } : undefined);

  // 파일 다운로드 핸들러
  const handleFileDownload = async (noticeId: number, fileId: number) => {
    // 이미 다운로드 중인 경우 무시
    if (downloadingFileId !== null) return;

    try {
      setDownloadingFileId(fileId);
      setDownloadProgress(prev => ({ ...prev, [fileId]: 0 }));

      await downloadNoticeFile(noticeId, fileId, progress => {
        setDownloadProgress(prev => ({ ...prev, [fileId]: progress }));
      });

      // 다운로드 완료 후 진행률 상태 초기화
      setDownloadProgress(prev => {
        const newState = { ...prev };
        delete newState[fileId];
        return newState;
      });
    } catch (error) {
      showAlert('파일 다운로드에 실패했습니다.');
    } finally {
      setDownloadingFileId(null);
    }
  };
  // TODO: 탭 기능 구현 예정
  // const [activeTab, setActiveTab] = useState('basic');
  // const tabItems = [
  //   { id: 'basic', label: '기본 정보' },
  //   { id: 'role', label: '역할 정보' },
  //   { id: 'members', label: '구성원 정보' },
  // ];

  return (
    <section className='section-page'>
      <UIPageHeader title='공지사항 조회' description='' />
      <UIPageBody>
        <UIArticle>
          <div className='border-t border-black'>
            <table className='tbl-v'>
              <colgroup>
                <col style={{ width: '152px' }} />
                <col style={{ width: 'calc(50% - 152px)' }} />
                <col style={{ width: '152px' }} />
                <col style={{ width: 'calc(50% - 152px)' }} />
              </colgroup>
              <tbody>
                <tr>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      제목
                    </UITypography>
                  </th>
                  <td colSpan={3}>
                    <UITypography variant='body-2' className='secondary-neutral-600 !text-[#005DF9]'>
                      {noticeDetail?.title || '제목 로딩 중...'}
                    </UITypography>
                  </td>
                </tr>
                <tr>
                  <th>
                    <UITypography variant='body-2' className='secondary-neutral-900'>
                      유형
                    </UITypography>
                  </th>
                  <td>
                    <UITypography variant='body-2' className='secondary-neutral-600'>
                      {noticeDetail?.type || '유형 로딩 중...'}
                    </UITypography>
                  </td>
                </tr>
                {noticeDetail?.files && noticeDetail.files.length > 0 && (
                  <tr>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        첨부파일
                      </UITypography>
                    </th>
                    <td>
                      <UIGroup gap={8} direction={'column'}>
                        {noticeDetail.files.map(file => (
                          <UIFileBox
                            key={file.fileId}
                            variant={downloadingFileId === file.fileId ? 'default' : 'link'}
                            fileName={file.originalFilename}
                            progress={downloadProgress[file.fileId]}
                            onClickFileName={() => handleFileDownload(noticeDetail.notiId, file.fileId)}
                          />
                        ))}
                      </UIGroup>
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </UIArticle>

        <UIArticle>
          <div className='pt-[16px] pb-[56px] border-b border-[#DCE2ED]'>
            <UITypography variant='body-2' className='secondary-neutral-900 whitespace-pre-wrap'>
              {noticeDetail?.msg || '내용 로딩 중...'}
            </UITypography>
          </div>
        </UIArticle>

        <UIArticle className='pt-[16px]'>
          <div className='article-header'>
            <UITypography variant='title-4' className='secondary-neutral-900'>
              담당자 정보
            </UITypography>
          </div>
          <div className='article-body'>
            <div className='border-t border-black'>
              <table className='tbl-v'>
                <colgroup>
                  <col style={{ width: '152px' }} />
                  <col style={{ width: '624px' }} />
                  <col style={{ width: '152px' }} />
                  <col style={{ width: '624px' }} />
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
                        {noticeDetail?.createByName || '생성자 정보 없음'}
                      </UITypography>
                    </td>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        생성일시
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {noticeDetail?.createAt ? formatDateTime(noticeDetail.createAt) : '생성일시 정보 없음'}
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
                        {noticeDetail?.updateByName || '수정자 정보 없음'}
                      </UITypography>
                    </td>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        최종 수정일시
                      </UITypography>
                    </th>
                    <td>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {noticeDetail?.updateAt ? formatDateTime(noticeDetail.updateAt) : '수정일시 정보 없음'}
                      </UITypography>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </UIArticle>
      </UIPageBody>
    </section>
  );
};
