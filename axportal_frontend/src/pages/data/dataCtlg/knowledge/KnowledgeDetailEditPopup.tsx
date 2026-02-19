import { useState, useEffect, useMemo, useRef } from 'react';
import { UICode } from '@/components/UI/atoms/UICode';

import { UIButton2, UITypography } from '@/components/UI/atoms';
import { UIInput, UITextArea2 } from '@/components/UI/molecules/input';

import { UIArticle, UIFormField, UIPopupBody, UIPopupFooter, UIPopupHeader } from '@/components/UI/molecules';

import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';

import { UIUnitGroup } from '@/components/UI/molecules';
import { useUpdateExternalKnowledge } from '@/services/knowledge/knowledge.services';

import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';

export interface KnowledgeDetailEditData {
  expKnwId?: string;
  knwId?: string;
  id?: string;
  name?: string;
  description?: string;
  embeddingModel?: string;
  vectorDB?: string;
  indexName?: string;
  script?: string;
  isCustomKnowledge?: boolean;
}

interface KnowledgeDetailEditPopupProps {
  isOpen: boolean;
  onClose: () => void;
  onSave?: (updatedData: KnowledgeDetailEditData) => void;
  initialData?: KnowledgeDetailEditData;
}

export const KnowledgeDetailEditPopup: React.FC<KnowledgeDetailEditPopupProps> = ({ isOpen, onClose, onSave, initialData }) => {
  const { showCancelConfirm, showNoEditContent } = useCommonPopup();

  // 초기화 여부를 추적 (같은 팝업 세션에서 한 번만 초기화)
  const hasInitializedRef = useRef(false);

  // 팝업이 열렸을 때만 상세 조회 (script 포함)
  const repoId = useMemo(() => initialData?.knwId || '', [initialData]);

  // 수정 mutation
  const updateMutation = useUpdateExternalKnowledge(repoId, {
    onSuccess: () => {
      onSave?.(formData);
    },
  });

  // 폼 데이터 상태
  const [formData, setFormData] = useState<KnowledgeDetailEditData>({
    name: '',
    description: '',
    embeddingModel: '',
    vectorDB: '',
    indexName: '',
    script: '',
    isCustomKnowledge: false,
  });

  // 1. isOpen 변경 관리: 팝업이 닫힐 때 초기화 플래그 리셋
  useEffect(() => {
    // 팝업이 닫히면 초기화 플래그 리셋
    if (!isOpen) {
      hasInitializedRef.current = false;
    }
  }, [isOpen]);

  // 2. initialData 변경 관리: initialData가 있을 때 formData 초기화
  useEffect(() => {
    // isOpen이 true이고, initialData가 있고, 아직 초기화하지 않았을 때 초기화
    if (initialData && !hasInitializedRef.current) {
      setFormData({
        name: initialData.name || '',
        description: initialData.description || '',
        embeddingModel: initialData.embeddingModel || '',
        vectorDB: initialData.vectorDB || '',
        indexName: initialData.indexName || '',
        script: initialData.script || '',
        isCustomKnowledge: initialData.isCustomKnowledge || false,
      });
      hasInitializedRef.current = true;
    }
  }, [initialData]);

  /* 취소 */
  const handleClose = () => {
    showCancelConfirm({
      onConfirm: () => {
        onClose();
      },
    });
  };

  const isDisabled = useMemo(() => {
    // 필수 필드가 비어있으면 disabled
    return !formData.name?.trim() || !formData.description?.trim() || !formData.script?.trim() || (formData.isCustomKnowledge && !formData.indexName?.trim());
  }, [formData]);

  /* 저장 */
  const handleSave = async () => {
    // 수정된 내용이 있는지 확인 (initialData와 비교)
    const baseName = initialData?.name || '';
    const baseDescription = initialData?.description || '';
    const baseScript = initialData?.script || '';
    const baseIndexName = initialData?.indexName || '';

    const hasChanges =
      formData.name !== baseName ||
      formData.description !== baseDescription ||
      formData.script !== baseScript ||
      (formData.isCustomKnowledge && formData.indexName !== baseIndexName);

    if (!hasChanges) {
      showNoEditContent({});
      return;
    }

    // 변경된 필드만 전송 (성능 최적화)
    const updatePayload: any = {
      name: formData.name, // 이름은 항상 전송
      description: formData.description, // 설명은 항상 전송
    };

    // 스크립트가 변경된 경우에만 전송
    if (formData.script !== baseScript) {
      updatePayload.script = formData.script;
    }

    // 인덱스명이 변경된 경우에만 전송 (사용자 정의 지식만)
    if (formData.isCustomKnowledge && formData.indexName !== baseIndexName) {
      updatePayload.indexName = formData.indexName;
    }

    // console.log('📤 수정 요청 데이터:', updatePayload);

    // 수정 API 호출
    await updateMutation.mutateAsync(updatePayload);
  };

  return (
    <UILayerPopup
      isOpen={isOpen}
      onClose={handleClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        /* 좌측 Step 영역 콘텐츠 */
        <UIPopupAside>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title='지식 수정' description='' position='left' />
          <UIPopupBody></UIPopupBody>
          {/* 레이어 팝업 바디 : [참고] 이 페이지에는 왼쪽 body 영역 없음. */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleClose}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} onClick={handleSave} disabled={isDisabled}>
                  저장
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      {/* 우측 Contents 영역 콘텐츠 */}
      {/* 콘텐츠 영역 */}
      <section className='section-popup-content'>
        {/* 레이어 팝업 바디 */}
        <UIPopupBody>
          {/* 이름 입력 필드 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                이름
              </UITypography>
              <div>
                <UIInput.Text value={formData.name} placeholder='이름 입력' onChange={e => setFormData({ ...formData, name: e.target.value })} maxLength={30} />
              </div>
            </UIFormField>
          </UIArticle>

          {/* 설명 입력 필드 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                설명
              </UITypography>
              <div>
                <UITextArea2
                  value={formData.description as string}
                  placeholder='설명 입력'
                  maxLength={100}
                  onChange={e => setFormData({ ...formData, description: e.target.value })}
                />
              </div>
            </UIFormField>
          </UIArticle>

          {/* 임베딩모델 입력 필드 - 항상 읽기전용 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                임베딩 모델
              </UITypography>
              <UIDropdown required={true} value={formData.embeddingModel as string} readonly={true} options={[]} isOpen={false} onSelect={() => {}} />
            </UIFormField>
          </UIArticle>

          {/* 벡터 DB 입력 필드 - 항상 읽기전용 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                벡터 DB
              </UITypography>
              <UIDropdown required={true} readonly={true} value={formData.vectorDB as string} options={[]} isOpen={false} onSelect={() => {}} />
            </UIFormField>
          </UIArticle>

          {/* 인덱스명 입력 필드 - isCustomKnowledge가 true일 때만 수정 가능 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                인덱스명
              </UITypography>
              <UIInput.Text
                value={formData.indexName}
                placeholder='인덱스명 입력'
                readOnly={!formData.isCustomKnowledge}
                onChange={e => setFormData({ ...formData, indexName: e.target.value })}
                maxLength={52}
              />
            </UIFormField>
          </UIArticle>

          {/* Script 영역 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                Script
              </UITypography>
              {/* 소스코드 영역 */}
              <UICode
                value={formData.script || ''}
                language='python'
                theme='dark'
                width='100%'
                minHeight='300px'
                maxHeight='500px'
                // readOnly={false}
                onChange={value => setFormData({ ...formData, script: value })}
              />
            </UIFormField>
          </UIArticle>
        </UIPopupBody>
      </section>
    </UILayerPopup>
  );
};
