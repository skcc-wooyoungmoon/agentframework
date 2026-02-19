import { useLayerPopup } from '@/hooks/common/layer/useLayerPopup';
import { useDeleteVectorDB, useGetVectorDBById } from '@/services/data/tool/dataToolVectorDB.services';
import { useModal } from '@/stores/common/modal';
import { useNavigate, useParams } from 'react-router-dom';
import { VectorDBEditPopupPage } from './VectorDBEditPopupPage';
import { UIArticle, UIPageBody, UIPageFooter, UIPageHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UITextLabel, UITypography } from '@/components/UI';
import { ManagerInfoBox } from '@/components/common/manager';
import { Button } from '@/components/common/auth';
import { AUTH_KEY } from '@/constants/auth/auth.constants';

export function VectorDBDetailPage() {
  const { id } = useParams<{ id: string }>(); // Vector DB 고유 아이디
  // const location = useLocation();

  // location.state에서 담당자 정보 가져오기
  // const getStateData = () => {
  //   try {
  //     return location.state || {};
  //   } catch (error) {
  //     // console.warn('location.state 접근 중 오류:', error);
  //     return {};
  //   }
  // };

  // const stateData = getStateData();
  // const { createdAt, createdBy, updatedAt, updatedBy } = stateData;

  // console.log('stateData..', stateData);

  const { openConfirm, openAlert } = useModal();
  const navigate = useNavigate();
  const layerPopupOne = useLayerPopup();

  /**
   * Vector DB 데이터 조회
   */
  const { data: vectorDBData, refetch } = useGetVectorDBById(id || '');

  const isDefault = vectorDBData?.isDefault;
  // console.log('vectorDBData..', vectorDBData);

  /**
   * 데이터 도구 - VectorDB 삭제
   */
  const { mutate: deleteVectorDB } = useDeleteVectorDB({
    onSuccess: () => {
      // console.log('데이터 도구 - VectorDB 삭제 성공');
    },
    onError: /* (error: any) */ () => {
      // console.error('데이터 도구 - VectorDB 삭제 실패:', error);
    },
  });

  /**
   * 삭제 버튼 클릭
   */
  const handleDelete = async () => {
    if (!id || !vectorDBData) {
      // console.error('ID 또는 데이터가 없습니다.');
      return;
    }

    if (isDefault === true) {
      await openConfirm({
        title: '',
        message: 'Default 데이터는 삭제할 수 없습니다.',
        confirmText: '확인',
        cancelText: '',
        onConfirm: () => {
          // console.log('확인됨');
        },
        onCancel: () => {
          // console.log('취소됨');
        },
      });
    } else {
      await openConfirm({
        title: '삭제',
        message: '삭제하시겠어요?\n삭제된 정보는 복구할 수 없습니다.',
        confirmText: '예',
        cancelText: '아니오',
        onConfirm: () => {
          deleteVectorDB(
            { vectorDbId: id },
            {
              onSuccess: () => {
                // console.log(`데이터 도구 - VectorDB 삭제 성공`);
                // 삭제 성공 시 완료 알림 표시
                openAlert({
                  title: '완료',
                  message: 'Vector DB가 삭제되었습니다.',
                  confirmText: '확인',
                  onConfirm: () => {
                    navigate('/data/dataTools?tab=vector', { replace: true });
                  },
                });
              },
              onError: /* (error: any) */ () => {
                // console.error(`데이터 도구 - VectorDB 삭제 실패:`, error);
              },
            }
          );
        },
        onCancel: () => {
          // console.log('취소됨');
        },
      });
    }
  };

  const handleVectorDBUpdatePopup = () => {
    if (!vectorDBData) {
      // console.warn('VectorDB 데이터가 아직 로드되지 않았습니다.');
      return;
    }
    layerPopupOne.onOpen();
  };

  return (
    <section className='section-page'>
      {/* 페이지 헤더 */}
      <UIPageHeader title='데이터도구 조회' description='' />

      {/* 페이지 바디 */}
      <UIPageBody>
        {/* 벡터DB 정보 */}
        <UIArticle>
          <div className='article-header'>
            <UITypography variant='title-4' className='secondary-neutral-900'>
              벡터DB
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
                  {/* Milvus 타입일 경우 */}
                  {vectorDBData?.type === 'Milvus' ? (
                    <>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            이름
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {vectorDBData?.name}
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
                            {vectorDBData?.type}
                          </UITypography>
                        </td>
                      </tr>

                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            Host
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {vectorDBData?.connectionInfo?.host || ''}
                          </UITypography>
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            Port
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {vectorDBData?.connectionInfo?.port || ''}
                          </UITypography>
                        </td>
                      </tr>

                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            User
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {vectorDBData?.connectionInfo?.user || ''}
                          </UITypography>
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            Password
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {vectorDBData?.connectionInfo?.password ? '●'.repeat(vectorDBData.connectionInfo.password.length) : ''}
                          </UITypography>
                        </td>
                      </tr>

                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            Secure
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UITextLabel intent={vectorDBData?.connectionInfo?.secure === 'True' ? 'blue' : 'gray'}>
                            {vectorDBData?.connectionInfo?.secure?.toLowerCase() || 'false'}
                          </UITextLabel>
                        </td>
                      </tr>

                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            Database Name
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {vectorDBData?.connectionInfo?.dbName || ''}
                          </UITypography>
                        </td>
                      </tr>

                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            Default
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UITextLabel intent={isDefault ? 'blue' : 'gray'}>{isDefault?.toString() || 'false'}</UITextLabel>
                        </td>
                      </tr>
                    </>
                  ) : (
                    /* Milvus 외 타입 */
                    <>
                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            이름
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {vectorDBData?.name}
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
                            {vectorDBData?.type}
                          </UITypography>
                        </td>
                      </tr>

                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            Endpoint
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {vectorDBData?.connectionInfo?.endpoint || ''}
                          </UITypography>
                        </td>
                      </tr>

                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            Key
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            {vectorDBData?.connectionInfo?.apiKey ? '●'.repeat(vectorDBData.connectionInfo.apiKey.length) : ''}
                          </UITypography>
                        </td>
                      </tr>

                      <tr>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            Default
                          </UITypography>
                        </th>
                        <td colSpan={3}>
                          <UITextLabel intent={isDefault ? 'blue' : 'gray'}>{isDefault?.toString() || 'false'}</UITextLabel>
                        </td>
                      </tr>
                    </>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </UIArticle>

        {/* 담당자 정보 */}
        <ManagerInfoBox
          type='uuid'
          people={[
            { userId: vectorDBData?.createdBy || '', datetime: vectorDBData?.createdAt || '' },
            { userId: vectorDBData?.updatedBy || '', datetime: vectorDBData?.updatedAt || '' },
          ]}
        />
      </UIPageBody>

      {/* 페이지 footer */}
      <UIPageFooter>
        <UIArticle>
          <UIUnitGroup gap={8} direction='row' align='center'>
            <Button auth={AUTH_KEY.DATA.VECTOR_DB_DELETE} className='btn-primary-gray' onClick={handleDelete}>
              삭제
            </Button>
            <Button auth={AUTH_KEY.DATA.VECTOR_DB_UPDATE} className='btn-primary-blue' onClick={handleVectorDBUpdatePopup}>
              수정
            </Button>
          </UIUnitGroup>
        </UIArticle>
      </UIPageFooter>

      {/* 편집 팝업 */}
      {vectorDBData && (
        <VectorDBEditPopupPage
          currentStep={layerPopupOne.currentStep}
          onNextStep={layerPopupOne.onNextStep}
          onPreviousStep={layerPopupOne.onPreviousStep}
          onClose={layerPopupOne.onClose}
          vectorDbId={id || ''}
          vectorDBData={vectorDBData}
          isDefault={isDefault}
          onUpdateSuccess={() => refetch()}
        />
      )}
    </section>
  );
}
