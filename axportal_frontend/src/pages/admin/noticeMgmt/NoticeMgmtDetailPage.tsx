import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { UIFileBox, UILabel, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIGroup, UIPageBody, UIPageFooter, UIPageHeader, UIUnitGroup } from '@/components/UI/molecules';
import { NoticeMgmtUpdatePage } from './NoticeMgmtUpdatePage.tsx';
import { downloadFileWithProgress, useDeleteNotice, useGetNoticeById } from '@/services/admin/noticeMgmt/noticeMgmt.services.ts';
import { useNoticeMgmt } from '@/stores/admin/noticeMgmt/noticeMgmt.atoms';
import { useModal } from '@/stores/common/modal';

import { Button } from '@/components/common/auth';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { STORAGE_KEYS } from '@/constants/common/storage.constants';

export const NoticeMgmtDetailPage = () => {
  const navigate = useNavigate();
  const { id: urlNotiId } = useParams<{ id: string }>();

  const { selectedNoticeDetail } = useNoticeMgmt();

  const { openAlert, openConfirm } = useModal();
  const showAlert = (message: string, title = '안내') => {
    openAlert({
      message: message,
      title,
      confirmText: '확인',
    });
  };

  const { mutate: deleteNotice } = useDeleteNotice();

  // URL 파라미터 우선, 없을 경우 selectedNoticeDetail 또는 로컬스토리지에서 가져오기
  const apiNotiId = urlNotiId || selectedNoticeDetail?.notiId || localStorage.getItem(STORAGE_KEYS.SEARCH_VALUES.NOTICE_MGMT_DETAIL_NOTI_ID) || '';

  const { data: noticeDetail, refetch } = useGetNoticeById(
    {
      notiId: apiNotiId || '',
    },
    {
      refetchOnMount: true,
      refetchOnWindowFocus: false,
      staleTime: 0,
    }
  );

  const [showUpdateModal, setShowUpdateModal] = useState(false);

  // 다운로드 진행률 상태 (fileId -> progress)
  const [downloadProgress, setDownloadProgress] = useState<Record<number, number>>({});
  const [downloadingFileId, setDownloadingFileId] = useState<number | null>(null);

  const [noticeData, setNoticeData] = useState({
    notiId: '',
    title: '제목 없음',
    useYn: 'N',
    type: '시스템 점검',
    expFrom: '',
    expTo: '',
    modifiedDate: '',
    msg: '',
    createBy: '',
    createAt: '',
    createdByName: '',
    createdByDepts: '',
    updateBy: '',
    updatedByName: '',
    updatedByDepts: '',
    files: [] as Array<{
      fileId: number;
      originalFilename: string;
      storedFilename: string;
      fileSize: number;
      contentType: string;
      uploadDate: string;
      useYn: string;
    }>,
  });

  useEffect(() => {
    if (noticeDetail) {
      setNoticeData({
        notiId: String(noticeDetail.notiId) || '',
        title: noticeDetail.title || '제목 없음',
        useYn: noticeDetail.useYn || 'N',
        type: noticeDetail.type || '일반',
        expFrom: noticeDetail.expFrom || '',
        expTo: noticeDetail.expTo || '',
        modifiedDate: noticeDetail.updateAt || '',
        msg: noticeDetail.msg || '',
        createBy: noticeDetail.createBy || '',
        createAt: noticeDetail.createAt || '',
        createdByName: noticeDetail.createdByName || '',
        createdByDepts: noticeDetail.createdByDepts || '',
        updateBy: noticeDetail.updateBy || '',
        updatedByName: noticeDetail.updatedByName || '',
        updatedByDepts: noticeDetail.updatedByDepts || '',
        files: noticeDetail.files || [],
      });
    }
  }, [noticeDetail]);

  const formatDate = (dateStr: string) => {
    if (!dateStr || dateStr === '-') return '-';

    try {
      const date = new Date(dateStr);
      if (isNaN(date.getTime())) {
        // 이미 포맷된 문자열인 경우 처리
        if (dateStr.includes('-')) {
          return dateStr.replace(/-/g, '.');
        }
        return dateStr;
      }

      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}.${month}.${day}`;
    } catch (error) {
      return dateStr;
    }
  };

  const formatDateTime = (dateTimeStr: string) => {
    if (!dateTimeStr || dateTimeStr === '-') return '-';

    try {
      if (dateTimeStr.includes('T')) {
        const date = new Date(dateTimeStr);
        if (!isNaN(date.getTime())) {
          const year = date.getFullYear();
          const month = String(date.getMonth() + 1).padStart(2, '0');
          const day = String(date.getDate()).padStart(2, '0');
          const hours = String(date.getHours()).padStart(2, '0');
          const minutes = String(date.getMinutes()).padStart(2, '0');
          const seconds = String(date.getSeconds()).padStart(2, '0');
          return `${year}.${month}.${day} ${hours}:${minutes}:${seconds}`;
        }
      }

      if (dateTimeStr.includes('.')) {
        const parts = dateTimeStr.split(' ');
        if (parts.length >= 2) {
          const timePart = parts[1];
          return `${parts[0]} ${timePart}`;
        } else {
          return parts[0];
        }
      }

      if (dateTimeStr.includes('-')) {
        const parts = dateTimeStr.split(' ');
        if (parts.length >= 2) {
          const datePart = parts[0].replace(/-/g, '.');
          const timePart = parts[1];
          return `${datePart} ${timePart}`;
        } else {
          return dateTimeStr.replace(/-/g, '.');
        }
      }

      return dateTimeStr;
    } catch (error) {
      return dateTimeStr;
    }
  };

  const calculateNoticeStatus = (item: any) => {
    const now = new Date();
    const expFrom = item.expFrom ? new Date(item.expFrom) : null;
    const expTo = item.expTo ? new Date(item.expTo) : null;
    const useYn = item.useYn;

    if (useYn === 'N') {
      return '임시저장';
    }

    if (useYn === 'Y') {
      if (!expFrom || !expTo) {
        return '게시';
      }

      const isWithinPeriod = now >= expFrom && now <= expTo;

      if (isWithinPeriod) {
        return '게시';
      } else {
        return '만료';
      }
    }

    return '임시저장';
  };

  const getStatusIntent = (status: string) => {
    switch (status) {
      case '게시':
        return 'complete';
      case '임시저장':
        return 'progress';
      case '만료':
        return 'error';
      default:
        return 'complete';
    }
  };

  const handleFileDownload = async (file: {
    fileId: number;
    originalFilename: string;
    storedFilename: string;
    fileSize: number;
    contentType: string;
    uploadDate: string;
    useYn: string;
  }) => {
    if (!noticeData.notiId) {
      showAlert('공지사항 ID를 찾을 수 없습니다.', '안내');
      return;
    }

    // 이미 다운로드 중인 경우 무시
    if (downloadingFileId !== null) return;

    try {
      setDownloadingFileId(file.fileId);
      setDownloadProgress(prev => ({ ...prev, [file.fileId]: 0 }));

      await downloadFileWithProgress(noticeData.notiId, file.fileId, progress => {
        setDownloadProgress(prev => ({ ...prev, [file.fileId]: progress }));
      });

      // 다운로드 완료 후 진행률 상태 초기화
      setDownloadProgress(prev => {
        const newState = { ...prev };
        delete newState[file.fileId];
        return newState;
      });
    } catch (error) {
      showAlert(`파일 다운로드에 실패했습니다: ${error instanceof Error ? error.message : '알 수 없는 오류'}`, '실패');
    } finally {
      setDownloadingFileId(null);
    }
  };

  const handleDelete = () => {
    const status = calculateNoticeStatus(noticeData);
    if (status === '게시') {
      showAlert('게시 중인 항목은 삭제할 수 없습니다.\n\n상태 변경 후 삭제해주세요.', '실패');
      return;
    }

    openConfirm({
      message: '삭제하시겠어요?\n삭제한 정보는 복구할 수 없습니다.',
      title: '안내',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        if (noticeData.notiId) {
          deleteNotice(
            { notiId: noticeData.notiId },
            {
              onSuccess: () => {
                showAlert('공지사항이 삭제되었습니다.', '완료');
                navigate('/admin/notice-mgmt');
              },
              onError: () => {
                showAlert('삭제에 실패했습니다.', '오류');
              },
            }
          );
        }
      },
    });
  };

  const handleUpdate = () => {
    setShowUpdateModal(true);
  };

  return (
    <>
      {showUpdateModal && (
        <NoticeMgmtUpdatePage
          open={showUpdateModal}
          onClose={async updatedData => {
            setShowUpdateModal(false);
            if (updatedData) {
              await refetch();
            }
          }}
          selectedRowData={{
            notiId: noticeData.notiId,
            title: noticeData.title,
            msg: noticeData.msg,
            type: noticeData.type,
            useYn: noticeData.useYn,
            expFrom: noticeData.expFrom,
            expTo: noticeData.expTo,
            createBy: noticeData.createBy,
            createAt: noticeData.createAt,
            createdByName: noticeData.createdByName,
            createdByDepts: noticeData.createdByDepts,
            updateBy: noticeData.updateBy,
            updatedByName: noticeData.updatedByName,
            updatedByDepts: noticeData.updatedByDepts,
            modifiedDate: noticeData.modifiedDate,
            files: noticeData.files,
          }}
        />
      )}

      <section className='section-page'>
        <UIPageHeader title='공지사항 조회' description='' />
        <UIPageBody>
          <UIArticle>
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
                        제목
                      </UITypography>
                    </th>
                    <td colSpan={3}>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {noticeData.title}
                      </UITypography>
                    </td>
                  </tr>
                  <tr>
                    <th>
                      <UITypography variant='body-2' className='secondary-neutral-900'>
                        유형
                      </UITypography>
                    </th>
                    <td colSpan={3}>
                      <UITypography variant='body-2' className='secondary-neutral-600'>
                        {noticeData.type}
                      </UITypography>
                    </td>
                  </tr>
                  {noticeData.files && noticeData.files.length > 0 && (
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          첨부파일
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UIGroup gap={8} direction={'column'}>
                          {noticeData.files.map((file, index) => (
                            <UIFileBox
                              key={file.fileId ? `file-${file.fileId}` : `file-${index}`}
                              variant={downloadingFileId === file.fileId ? 'default' : 'link'}
                              fileName={file.originalFilename}
                              progress={downloadProgress[file.fileId]}
                              onClickFileName={() => {
                                handleFileDownload(file);
                              }}
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

          <UIArticle className='pl-3 pt-2 pb-14 border-b border-[#DCE2ED]'>
            <UITypography variant='body-2' className='secondary-neutral-600' style={{ wordBreak: 'break-word', whiteSpace: 'pre-wrap' }}>
              {noticeData.msg}
            </UITypography>
          </UIArticle>

          <UIArticle>
            <div className='article-header'>
              <UITypography variant='title-4' className='secondary-neutral-900'>
                게시 정보
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
                          상태
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UILabel variant='badge' intent={getStatusIntent(calculateNoticeStatus(noticeData))}>
                          {calculateNoticeStatus(noticeData)}
                        </UILabel>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          게시 기간
                        </UITypography>
                      </th>
                      <td colSpan={3}>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {noticeData.expFrom && noticeData.expTo ? `${formatDate(noticeData.expFrom)} ~ ${formatDate(noticeData.expTo)}` : '-'}
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
              <UITypography variant='title-4' className='secondary-neutral-900'>
                담당자 정보
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
                          생성자
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {noticeData.createdByName && noticeData.createdByDepts ? `${noticeData.createdByName} | ${noticeData.createdByDepts}` : noticeData.createBy || '-'}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          생성일시
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {formatDateTime(noticeData.createAt)}
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
                          {noticeData.updatedByName && noticeData.updatedByDepts ? `${noticeData.updatedByName} | ${noticeData.updatedByDepts}` : noticeData.updateBy || '-'}
                        </UITypography>
                      </td>
                      <th>
                        <UITypography variant='body-2' className='secondary-neutral-900'>
                          최종 수정일시
                        </UITypography>
                      </th>
                      <td>
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {formatDateTime(noticeData.modifiedDate)}
                        </UITypography>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </UIArticle>
        </UIPageBody>
        <UIPageFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='center'>
              <Button auth={AUTH_KEY.ADMIN.NOTICE_DELETE} className='btn-primary-gray' onClick={handleDelete}>
                삭제
              </Button>
              <Button auth={AUTH_KEY.ADMIN.NOTICE_UPDATE} className='btn-primary-blue' onClick={handleUpdate}>
                수정
              </Button>
            </UIUnitGroup>
          </UIArticle>
        </UIPageFooter>
      </section>
    </>
  );
};
