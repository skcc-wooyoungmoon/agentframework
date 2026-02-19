import { useState } from 'react';

import { UITypography, UIButton2 } from '@/components/UI/atoms';
import { UIArticle, UIFormField, UIUnitGroup, UIPopupHeader, UIPopupBody, UIPopupFooter, UIGroup } from '@/components/UI/molecules';
import { UIInput } from '@/components/UI/molecules/input/UIInput';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { DesignLayout } from '../../components/DesignLayout';

export const AD_140101_P01: React.FC = () => {
  const [isPopupOpen] = useState(true);
  const [jupyterCount, setJupyterCount] = useState('1');
  const [vscodeCount, setVscodeCount] = useState('1');

  const handleClose = () => {
    // 팝업 닫기 동작 제거 (디자인 페이지이므로 항상 열려있음)
    //
  };

  const handleCancel = () => {
    //
    // 취소 동작 처리
  };

  const handleCreate = () => {
    //
    // 만들기 동작 처리
  };

  return (
    <>
      {/* DesignLayout 기본 구조 */}
      <DesignLayout
        initialMenu={{ id: 'model', label: '모델' }}
        initialSubMenu={{
          id: 'model-catalog',
          label: '모델카탈로그 조회',
          icon: 'ico-lnb-menu-20-model-catalog',
        }}
      >
        <div className='flex items-center justify-center h-full'>
          <div className='text-center'>
            <UITypography variant='title-1' className='secondary-neutral-800 text-sb'>
              모델카탈로그 조회
            </UITypography>
            <UITypography variant='body-1' className='secondary-neutral-600'>
              모델 수정 진행 중...
            </UITypography>
          </div>
        </div>
      </DesignLayout>

      {/* DesignLayout 위에 높은 z-index로 뜨는 UILayerPopup */}
      <UILayerPopup
        isOpen={isPopupOpen}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          /* 좌측 Step 영역 콘텐츠 */
          <UIPopupAside>
            <UIPopupHeader title='환경설정' description='' position='left' />
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                    취소
                  </UIButton2>
                  <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} onClick={handleCreate}>
                    저장
                  </UIButton2>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        {/* 우측 Contents 영역 콘텐츠 */}
        <section className='section-popup-content'>
          <UIPopupBody>
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UIGroup gap={4} direction='column'>
                  <UITypography variant='body-1' className='secondary-neutral-800 text-body-1-sb' required={false}>
                    환경 설정
                  </UITypography>
                  <UITypography variant='body-2' className='secondary-neutral-600'>
                    Jupyter, VS Code의 기본 생성 개수를 설정할 수 있습니다. 사용자 1명 기준으로 적용됩니다.
                  </UITypography>
                </UIGroup>
              </UIFormField>
            </UIArticle>

            <UIArticle>
              <div className='article-header'>
                <UITypography variant='title-4' className='secondary-neutral-900'>
                  도구별 생성 개수
                </UITypography>
              </div>
              <div className='article-body'>
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
                            Jupyter Notebook
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            1개
                          </UITypography>
                        </td>
                        <th>
                          <UITypography variant='body-2' className='secondary-neutral-900'>
                            VS Code
                          </UITypography>
                        </th>
                        <td>
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            1개
                          </UITypography>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </UIArticle>

            <UIArticle>
              <UIUnitGroup gap={26} direction='row' align='start'>
                <UIFormField gap={8} direction='column'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                    Jupyter Notebook
                  </UITypography>
                  <UIInput.Text value={jupyterCount} placeholder='1' onChange={e => setJupyterCount(e.target.value)} readOnly={false} />
                </UIFormField>
                <UIFormField gap={8} direction='column'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                    VS Code
                  </UITypography>
                  <UIInput.Text value={vscodeCount} placeholder='1' onChange={e => setVscodeCount(e.target.value)} readOnly={false} />
                </UIFormField>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupBody>
          {/* <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }}>
                  이전
                </UIButton2>
                <UIButton2 className='btn-secondary-blue' style={{ width: '80px' }}>
                  다음
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter> */}
        </section>
      </UILayerPopup>
    </>
  );
};
