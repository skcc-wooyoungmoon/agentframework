import { useEffect, useState } from 'react';

import { UIButton2, UIFileBox, UIRadio2, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIFormField, UIGroup, UIInput, UIList, UIPopupBody, UIPopupFooter, UIPopupHeader, UITextArea2, UIUnitGroup } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import type { LayerPopupProps } from '@/hooks/common/layer';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import type { UpdateWorkFlowFormData } from '@/services/prompt/workFlow/types';
import { useUpdateWorkFlow } from '@/services/prompt/workFlow/workFlow.services';
import { useModal } from '@/stores/common/modal';

interface WorkFlowEditPopupPageProps extends LayerPopupProps {
  workFlowId?: string;
  onEditSuccess?: () => void;
  // 기존 데이터
  initialName?: string;
  initialXmlText?: string;
  initialTags?: string[];
}

export const WorkFlowEditPopupPage = ({ currentStep, onClose, workFlowId, onEditSuccess, initialName = '', initialXmlText = '', initialTags = [] }: WorkFlowEditPopupPageProps) => {
  const [workflowName, setWorkflowName] = useState(initialName);
  const [registrationMethod, setRegistrationMethod] = useState<'file' | 'direct'>('direct');
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [xmlContent, setXmlContent] = useState(initialXmlText);
  const [tags, setTags] = useState<string[]>(initialTags);
  const [hasAttemptedSave, setHasAttemptedSave] = useState<boolean>(false);

  // 에러 상태
  const [errorName, setErrorName] = useState<boolean>(false);
  const [errorContent, setErrorContent] = useState<boolean>(false);

  // 팝업이 열릴 때 기존 데이터로 초기화
  useEffect(() => {
    if (currentStep === 1) {
      setWorkflowName(initialName);
      setXmlContent(initialXmlText);
      setTags(initialTags);
      setRegistrationMethod(initialXmlText ? 'direct' : 'file');
    }
  }, [currentStep, initialName, initialXmlText, initialTags]);

  // xmlContent 값이 비어있지 않을 때 자동으로 '직접 입력' 선택
  useEffect(() => {
    if (xmlContent && xmlContent.trim() !== '') {
      setRegistrationMethod('direct');
    }
  }, [xmlContent]);

  // 워크플로우 수정 mutation
  const { openConfirm, openAlert } = useModal();
  const { showNoEditContent } = useCommonPopup();
  const updateWorkFlowMutation = useUpdateWorkFlow({
    onSuccess: () => {
      openAlert({
        title: '완료',
        message: '수정사항이 저장되었습니다.',
        confirmText: '확인',
        onConfirm: () => {
          onEditSuccess?.();
        },
      });
    },
    onError: () => {
      openAlert({
        title: '오류',
        message: '워크플로우 수정에 실패했습니다. 다시 시도해주세요.',
        confirmText: '확인',
      });
    },
    onSettled: () => {
      handleClose();
    },
  });

  // 퍼블리싱 구조를 유지한 파일 업로드 트리거
  const handleUploadClick = () => {
    const input = document.createElement('input');
    input.type = 'file';
    input.accept = '.xml';
    input.onchange = e => {
      const target = e.target as HTMLInputElement;
      const file = target.files?.[0] || null;
      if (!file) return;

      // 파일 크기 검증 (10MB)
      const maxSize = 10 * 1024 * 1024;
      if (file.size > maxSize) {
        openAlert({
          title: '안내',
          message: '파일 크기는 10MB 미만이어야 합니다.',
          confirmText: '확인',
        });
        return;
      }

      // 파일 확장자/타입 검증
      const fileName = file.name.toLowerCase();
      const isXmlExtension = fileName.endsWith('.xml');
      const isXmlMimeType = file.type === 'text/xml' || file.type === 'application/xml' || file.type === '';

      if (!isXmlExtension || !isXmlMimeType) {
        openAlert({
          title: '안내',
          message: 'XML 파일만 업로드 가능합니다. .xml 확장자를 가진 파일을 선택해주세요.',
          confirmText: '확인',
        });
        return;
      }

      setSelectedFile(file);
      // 파일 업로드 방식 선택 시 직접 입력 내용은 초기화
      setXmlContent('');
    };
    input.click();
  };

  // 파일 삭제 핸들러
  const handleFileRemove = () => {
    setSelectedFile(null);
  };

  // 태그 변경 시 제약사항 유지: 중복 제거, 각 태그 8자 이내, 총 길이 100자 이내
  const handleTagsChange = (nextTags: string[]) => {
    // 공백 제거 및 빈 태그 제거
    const trimmed = nextTags.map(t => t.trim()).filter(t => t.length > 0);

    // 각 태그 길이 제한 적용 (8자 초과 제거)
    const tooLong = trimmed.filter(t => t.length > 8);
    let valid = trimmed.filter(t => t.length <= 8);
    if (tooLong.length > 0) {
      openAlert({
        title: '안내',
        message: '태그는 8자 이하로 입력해주세요.',
        confirmText: '확인',
      });
    }

    // 순서 유지하며 중복 제거
    const seen = new Set<string>();
    valid = valid.filter(t => (seen.has(t) ? false : (seen.add(t), true)));

    // 총 길이 제한(콤마 포함) 100자 이내로 자르기
    const result: string[] = [];
    let total = 0;
    for (let i = 0; i < valid.length; i++) {
      const tag = valid[i];
      const addLen = (result.length > 0 ? 1 : 0) + tag.length; // 콤마 포함
      if (total + addLen <= 100) {
        result.push(tag);
        total += addLen;
      } else {
        openAlert({
          title: '안내',
          message: '태그 총 길이는 100자 이하로 입력해주세요.',
          confirmText: '확인',
        });
        break;
      }
    }

    setTags(result);
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
      message: '취소하시겠어요?\n취소할 경우 저장된 데이터가 삭제됩니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        handleClose();
      },
    });
  };

  /**
   * 워크플로우 저장
   */
  const handleSave = () => {
    const isSameStringArray = (a: string[], b: string[]) => a.length === b.length && a.every((v, i) => v === b[i]);

    const currentName = (workflowName || initialName).trim();
    const initialTrimmedName = initialName.trim();

    const isNoEdit =
      currentName === initialTrimmedName &&
      isSameStringArray(tags, initialTags) &&
      ((registrationMethod === 'direct' && xmlContent === initialXmlText && !selectedFile) || (registrationMethod === 'file' && !selectedFile && !initialXmlText));

    if (isNoEdit) {
      showNoEditContent();
      return;
    }

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

    // 수정자 정보 구성 (하드코딩)
    const updatedByInfo = 'admin | Data기획Unit';

    // UpdateWorkFlowFormData 타입에 맞는 데이터 구성
    const requestData: UpdateWorkFlowFormData = {
      form: {
        workFlowId: workFlowId || '',
        workflowName: workflowName.trim(),
        versionNo: 1,
        xmlText: registrationMethod === 'direct' ? xmlContent : undefined,
        description: undefined,
        isActive: 'Y' as const,
        tag: tagsString,
        projectScope: undefined,
        updatedBy: updatedByInfo,
      },
      xmlFile: registrationMethod === 'file' ? selectedFile || undefined : undefined,
    };

    // 실제 워크플로우 수정 API 호출
    updateWorkFlowMutation.mutate(requestData);
  };

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
          <UIPopupHeader title='워크플로우 수정' position='left' />
          {/* 레이어 팝업 바디 */}
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
                  disabled={
                    workflowName.trim() === '' ||
                    (registrationMethod === 'file' ? !selectedFile : xmlContent.trim() === '') ||
                    updateWorkFlowMutation.isPending ||
                    workflowName?.length > 50
                  }
                >
                  저장
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      {/* 우측 Contents 영역 콘텐츠 - 기존 컴포넌트 사용 */}
      <section className='section-popup-content'>
        {/* 레이어 팝업 헤더 */}
        {/* <UIPopupHeader title='' position='right' /> */}

        {/* 레이어 팝업 바디 */}
        <UIPopupBody>
          <UIArticle>
            <UIUnitGroup gap={8} direction='column'>
              <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-sb'>
                이름
              </UITypography>
              <UIInput.Text
                value={workflowName || initialName}
                onChange={e => {
                  setWorkflowName(e.target.value);
                }}
                placeholder='이름 입력'
                error={errorName ? '이름을 입력해 주세요.' : ''}
                maxLength={50}
              />
            </UIUnitGroup>
          </UIArticle>

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
                        key={0}
                        variant='default'
                        size='full'
                        fileName={selectedFile.name}
                        fileSize={Math.round(selectedFile.size / 1024)}
                        onFileRemove={() => handleFileRemove()}
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
          {/* 직접 입력 필드 */}
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
          {/* 태그 입력 필드 */}
          <UIArticle>
            <UIInput.Tags tags={tags} onChange={handleTagsChange} placeholder='태그 입력' label='태그' />
          </UIArticle>
        </UIPopupBody>
        {/* 레이어 팝업 footer */}
        {/* <UIPopupFooter></UIPopupFooter> */}
      </section>
    </UILayerPopup>
  );
};
