import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';

import { UIButton2, UIFileBox, UIRadio2, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIFormField, UIGroup, UIInput, UIList, UIPopupBody, UIPopupFooter, UIPopupHeader, UITextArea2, UIUnitGroup } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import type { LayerPopupProps } from '@/hooks/common/layer';
import type { CreateWorkFlowRegistryFormData, CreateWorkFlowRegistryRequest } from '@/services/prompt/workFlow/types';
import { useCreateWorkFlowRegistry } from '@/services/prompt/workFlow/workFlow.services';
import { useModal } from '@/stores/common/modal';

interface WorkFlowCreatePopupPageProps extends LayerPopupProps {
  onCreateSuccess?: () => void;
}

export const WorkFlowCreatePopupPage = ({ currentStep, onClose, onCreateSuccess: _ }: WorkFlowCreatePopupPageProps) => {
  const [workflowName, setWorkflowName] = useState('');
  const [registrationMethod, setRegistrationMethod] = useState<'file' | 'direct'>('file');
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [xmlContent, setXmlContent] = useState('');
  const [tags, setTags] = useState<string[]>([]);
  const [hasAttemptedSave, setHasAttemptedSave] = useState<boolean>(false);
  const { openConfirm, openAlert } = useModal();
  const navigate = useNavigate();

  // 에러 상태
  const [errorName, setErrorName] = useState<boolean>(false);
  const [errorContent, setErrorContent] = useState<boolean>(false);

  // 워크플로우 레지스트리 생성 mutation
  const createWorkFlowRegistryMutation = useCreateWorkFlowRegistry({
    onSuccess: ({ workFlowId }) => {
      openAlert({
        title: '안내',
        message: '워크플로우 등록을 완료하였습니다.',
        onConfirm: () => {
          handleClose();
          navigate(`${workFlowId}`);
        },
      });
    },
  });

  // 파일 선택 공통 처리
  const validateAndSetSelectedFile = (file: File) => {
    // 파일 크기 검증 (10MB)
    const maxSize = 10 * 1024 * 1024;
    if (file.size > maxSize) {
      openAlert({
        title: '안내',
        message: '파일 크기는 10MB 미만이어야 합니다.',
        confirmText: '확인',
      });
      setSelectedFile(null);
      return false;
    }

    // 파일 확장자 검증
    const fileName = file.name.toLowerCase();
    const isXmlExtension = fileName.endsWith('.xml');
    const isXmlMimeType = file.type === 'text/xml' || file.type === 'application/xml';

    if (isXmlExtension && (isXmlMimeType || file.type === '')) {
      setSelectedFile(file);
      return true;
    } else {
      openAlert({
        title: '안내',
        message: 'XML 파일만 업로드 가능합니다. .xml 확장자를 가진 파일을 선택해주세요.',
        confirmText: '확인',
      });
      setSelectedFile(null);
      return false;
    }
  };

  // 파일 업로드 버튼 클릭 - 퍼블리싱 구조 유지(임시 input 생성)
  const handleUploadClick = () => {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = '.xml';
    input.onchange = e => {
      const target = e.target as HTMLInputElement;
      const file = target.files?.[0];
      if (file) {
        validateAndSetSelectedFile(file);
      }
    };
    input.click();
  };

  // 파일 삭제 핸들러
  const handleFileRemove = () => {
    setSelectedFile(null);
  };

  // save 버튼을 한 번 누른 후에는 실시간으로 유효성 검사
  useEffect(() => {
    if (hasAttemptedSave) {
      const isNameEmpty = workflowName.trim() === '';
      const isContentEmpty = registrationMethod === 'file' ? !selectedFile : !xmlContent || xmlContent.trim() === '';

      setErrorName(isNameEmpty);
      setErrorContent(isContentEmpty);
    }
  }, [hasAttemptedSave, workflowName, registrationMethod, selectedFile, xmlContent]);

  const handleClose = () => {
    // 모든 상태 초기화
    setWorkflowName('');
    setRegistrationMethod('file');
    setSelectedFile(null);
    setXmlContent('');
    setTags([]);
    setHasAttemptedSave(false);
    setErrorName(false);
    setErrorContent(false);

    onClose();
  };

  const handleCancel = () => {
    openConfirm({
      title: '안내',
      message: '화면을 나가시겠어요?\n입력한 정보가 저장되지 않을 수 있습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        handleClose();
      },
      onCancel: () => {
        // console.log('취소됨');
      },
    });
  };

  /**
   * 워크플로우 저장
   */
  const handleSave = () => {
    // 저장 시도 플래그 설정
    setHasAttemptedSave(true);

    // 기본 유효성 검사
    const isNameEmpty = workflowName.trim() === '';
    const isContentEmpty = registrationMethod === 'file' ? !selectedFile : !xmlContent || xmlContent.trim() === '';

    // 에러가 있으면 저장하지 않음
    if (isNameEmpty || isContentEmpty) {
      return;
    }

    // 워크플로우 이름 길이 검증
    if (workflowName.length > 50) {
      openAlert({
        title: '안내',
        message: '워크플로우 이름은 50자 이하로 입력해주세요.',
        confirmText: '확인',
      });
      return;
    }

    // 태그를 문자열로 연결
    const tagsString = tags.length > 0 ? tags.join(',') : undefined;
    if (tagsString && tagsString.length > 100) {
      openAlert({
        title: '안내',
        message: '태그 총 길이는 100자 이하로 입력해주세요.',
        confirmText: '확인',
      });
      return;
    }

    // 생성자 정보 구성 (하드코딩)
    const createdByInfo = 'admin | Data기획Unit';

    // 백엔드 DTO에 맞는 form 데이터 구성
    const formData: CreateWorkFlowRegistryRequest = {
      workflowName: workflowName.trim(),
      xmlText: registrationMethod === 'direct' ? xmlContent : undefined,
      description: undefined,
      isActive: 'Y',
      tag: tagsString,
      projectScope: undefined,
      createdBy: createdByInfo,
    };

    // multipart/form-data 요청 데이터 구성
    const requestData: CreateWorkFlowRegistryFormData = {
      form: formData,
      xmlFile: registrationMethod === 'file' && selectedFile ? selectedFile : undefined,
    };

    // 실제 워크플로우 생성 API 호출
    createWorkFlowRegistryMutation.mutate(requestData);
  };

  // 태그 변경 핸들러(UIInput.Tags용) - 퍼블리싱 구조 준수
  // const handleTagsChange = (nextTags: string[]) => {
  //   // 공백 제거 및 중복 제거
  //   const cleaned = Array.from(new Set(nextTags.map(t => t.trim()).filter(Boolean)));

  //   // 개별 태그 길이 검사
  //   if (cleaned.some(t => t.length > 8)) {
  //     alert('태그는 8자 이하로 입력해주세요.');
  //     return;
  //   }

  //   // 총 길이 검사(콤마로 조인)
  //   const totalLen = cleaned.join(',').length;
  //   if (totalLen > 100) {
  //     alert('태그 총 길이가 100자를 초과합니다. 더 짧은 태그를 입력해주세요.');
  //     return;
  //   }

  //   setTags(cleaned);
  // };

  return (
    <UILayerPopup
      isOpen={currentStep === 1}
      onClose={handleClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        /* 좌측 Step 영역 콘텐츠 */
        <UIPopupAside>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='워크플로우 등록' position='left' />
          {/* 레이어 팝업 footer */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: 80 }} onClick={handleCancel}>
                  취소
                </UIButton2>
                <UIButton2
                  className='btn-tertiary-blue'
                  style={{ width: 80 }}
                  onClick={handleSave}
                  disabled={workflowName.trim() === '' || (registrationMethod === 'file' ? !selectedFile : xmlContent.trim() === '') || createWorkFlowRegistryMutation.isPending}
                >
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
        {/* 레이어 팝업 바디 */}
        <UIPopupBody>
          {/* 이름 */}
          <UIArticle>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-sb'>
                이름
              </UITypography>
              <UIInput.Text
                value={workflowName}
                onChange={e => {
                  setWorkflowName(e.target.value);
                }}
                placeholder='이름 입력'
                error={errorName ? '이름을 입력해 주세요.' : ''}
                maxLength={51}
              />
            </UIUnitGroup>
          </UIArticle>

          {/* 등록 방법 선택 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <div className='inline-flex items-center'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  등록 방법 선택
                </UITypography>
              </div>
              <div>
                <UIUnitGroup gap={12} direction='column' align='start'>
                  <UIRadio2 name='basic1' value='option1' label='파일 업로드' checked={registrationMethod === 'file'} onChange={() => setRegistrationMethod('file')} />
                  <UIRadio2 name='basic1' value='option2' label='직접 입력' checked={registrationMethod === 'direct'} onChange={() => setRegistrationMethod('direct')} />
                </UIUnitGroup>
              </div>
            </UIFormField>
          </UIArticle>

          {/* 파일 업로드 */}
          {registrationMethod === 'file' && (
            <UIArticle>
              <UIGroup gap={16} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                  파일 업로드
                </UITypography>
                <div>
                  <UIButton2 className='btn-tertiary-outline download' onClick={handleUploadClick}>
                    파일 업로드
                  </UIButton2>
                </div>
                <div>
                  {selectedFile && (
                    <div className='space-y-3'>
                      <UIFileBox
                        variant='default'
                        size='full'
                        fileName={selectedFile.name}
                        fileSize={Math.round(selectedFile.size / 1024)}
                        onFileRemove={handleFileRemove}
                        className='w-full'
                      />
                    </div>
                  )}
                </div>
                <div>
                  <UIList
                    gap={4}
                    direction='column'
                    className='ui-list_bullet'
                    data={[
                      {
                        dataItem: (
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            암호화 설정된 파일은 업로드가 불가능하니 암호화 해제 후 파일 업로드를 해주세요.
                          </UITypography>
                        ),
                      },
                      {
                        dataItem: (
                          <UITypography variant='body-2' className='secondary-neutral-600'>
                            지원되는 파일 확장자 : .xml
                          </UITypography>
                        ),
                      },
                    ]}
                  />
                </div>
              </UIGroup>
            </UIArticle>
          )}

          {/* 직접 입력 */}
          {registrationMethod === 'direct' && (
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
                  직접 입력
                </UITypography>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <div className='flex-1'>
                    <UITextArea2
                      value={xmlContent}
                      placeholder=''
                      maxLength={6600}
                      style={{ height: '394px' }}
                      onChange={e => setXmlContent(e.target.value)}
                      error={errorContent && registrationMethod === 'direct' ? 'XML 내용을 입력해 주세요.' : ''}
                    />
                  </div>
                </UIUnitGroup>
              </UIFormField>
            </UIArticle>
          )}

          {/* 태그 입력 */}
          <UIArticle>
            <UIInput.Tags tags={tags} onChange={setTags} placeholder='태그 입력' label='태그' />
          </UIArticle>
        </UIPopupBody>
      </section>
    </UILayerPopup>
  );
};
