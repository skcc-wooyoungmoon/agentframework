import { useEffect, useMemo, useRef } from 'react';
import { UIButton2, UITypography, UICheckbox2, UIIcon2, UITooltip, UIToggle } from '@/components/UI/atoms';
import { UIArticle, UIFormField, UIGroup } from '@/components/UI/molecules';
import { UIUnitGroup } from '@/components/UI/molecules';
import { UIInput } from '@/components/UI/molecules/input';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { useGetEmbeddingModels, useGetVectorDBs } from '@/services/knowledge/knowledge.services';

type KnowledgeEmbeddingSettingPageProps = {
  embeddingModel: string;
  embeddingModelId: string;
  vectorDB: string;
  vectorDBId: string;
  syncEnabled: boolean;
  syncTargets: string[];
  isCustomKnowledge?: boolean; // 사용자 정의 지식 여부
  indexName?: string; // 인덱스명 (사용자 정의 지식에서 직접 입력)
  onEmbeddingModelChange: (value: string, id: string) => void;
  onVectorDBChange: (value: string, id: string) => void;
  onSyncEnabledChange: (checked: boolean) => void;
  onSyncTargetsChange: (targets: string[]) => void;
  onIndexNameChange?: (value: string) => void; // 인덱스명 변경 핸들러
};

export const KnowledgeEmbeddingSettingPage: React.FC<KnowledgeEmbeddingSettingPageProps> = ({
  embeddingModel,
  // embeddingModelId,
  vectorDB,
  // vectorDBId,
  syncEnabled,
  syncTargets,
  isCustomKnowledge = false,
  indexName = '',
  onEmbeddingModelChange,
  onVectorDBChange,
  onSyncEnabledChange,
  onSyncTargetsChange,
  onIndexNameChange,
}) => {
  // 임베딩 모델 목록 조회
  const { data: embeddingModelsData } = useGetEmbeddingModels(0, 100);

  // 벡터DB 목록 조회
  const { data: vectorDBsData } = useGetVectorDBs();

  // 임베딩 모델 목록 추출 (임시 데이터 추가)
  const embeddingModels = useMemo(() => {
    return embeddingModelsData?.content || [];
  }, [embeddingModelsData]);

  // 벡터DB 목록 추출
  const vectorDBs = useMemo(() => {
    return vectorDBsData?.content || [];
  }, [vectorDBsData]);

  // 임베딩 모델 로드 시 첫 번째 항목을 기본값으로 설정
  const embeddingInitRef = useRef(false);

  useEffect(() => {
    if (embeddingInitRef.current) return;
    if (embeddingModels.length > 0 && !embeddingModel) {
      const first = embeddingModels.find((m: any) => {
        const baseName = m?.name ?? m?.modelName ?? m?.displayName;
        return typeof baseName === 'string' && baseName.trim() !== '';
      });
      if (first) {
        embeddingInitRef.current = true;
        const firstName = first.name ?? first.modelName ?? first.displayName;
        // console.log('✅ 첫 번째 임베딩 모델 설정:', first, '사용할 name:', firstName);
        onEmbeddingModelChange(firstName, first.id ?? first.servingId);
      } else {
        // console.warn('⚠️ name이 있는 임베딩 모델을 찾을 수 없습니다:', embeddingModels);
      }
    }
  }, [embeddingModels, embeddingModel, onEmbeddingModelChange]);

  // 벡터DB 로드 시 첫 번째 항목을 기본값으로 설정
  const vectorDBInitRef = useRef(false);

  useEffect(() => {
    if (vectorDBInitRef.current) return;
    if (vectorDBs.length > 0 && !vectorDB) {
      vectorDBInitRef.current = true;
      const first = vectorDBs[0];
      // console.log('✅ 첫 번째 벡터DB 설정:', first);
      onVectorDBChange(first.name, first.id);
    }
  }, [vectorDBs, vectorDB, onVectorDBChange]);

  // 임베딩 모델 드롭다운 옵션 변환
  const embeddingOptions = useMemo(() => {
    return embeddingModels.map((model: any) => {
      const baseName = model.name ?? model.modelName;
      return {
        value: baseName,
        label: baseName,
        id: model.id ?? model.servingId,
      };
    });
  }, [embeddingModels]);

  // 벡터DB 드롭다운 옵션 변환
  const vectorDBOptions = useMemo(() => {
    return vectorDBs.map((db: any) => ({
      value: db.name,
      label: db.name,
      id: db.id,
    }));
  }, [vectorDBs]);

  const handleSyncTargetChange = (checked: boolean, value: string) => {
    let newTargets: string[];

    if (checked) {
      newTargets = [...syncTargets, value];
    } else {
      newTargets = syncTargets.filter(v => v !== value);
    }

    onSyncTargetsChange(newTargets);
  };

  return (
    <>
      <UIArticle>
        <UIFormField gap={8} direction='column'>
          <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
            임베딩 모델
          </UITypography>
          <UIDropdown
            required={true}
            value={embeddingModel || ''}
            onSelect={(value: string) => {
              // value는 model.name이므로 name으로 검색
              const selected = embeddingModels.find((m: any) => {
                const baseName = m.name ?? m.modelName ?? m.displayName;
                return baseName === value;
              });
              // console.log(' onEmbeddingModelChange ... : ', selected, value, embeddingModels);
              if (selected) {
                const selectedId = selected.id ?? selected.servingId;
                // console.log('🎯 임베딩 모델 선택:', { value, id: selectedId, selected });
                onEmbeddingModelChange(value, selectedId);
              } else {
                // console.warn('⚠️ 임베딩 모델 찾기 실패 - value:', value, 'embeddingModels:', embeddingModels);
              }
            }}
            options={embeddingOptions}
          />
        </UIFormField>
      </UIArticle>

      <UIArticle>
        <UIFormField gap={8} direction='column'>
          <UIGroup direction='column' gap={4}>
            <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
              벡터DB
            </UITypography>
            <UITypography variant='body-2' className='secondary-neutral-600'>
              올바른 벡터DB를 선택해야 지식이 정상적으로 동작합니다. 기본 지식과 사용자 정의 지식은 사용하는 벡터DB가 다를 수 있으니, 선택한 벡터DB를 한 번 더 확인해주세요.
            </UITypography>
          </UIGroup>
          <UIDropdown
            required={true}
            value={vectorDB || ''}
            onSelect={(value: string) => {
              const selected = vectorDBs.find((db: any) => db.name === value);
              if (selected) {
                onVectorDBChange(value, selected.id);
              }
            }}
            options={vectorDBOptions}
          />
        </UIFormField>
      </UIArticle>

      {isCustomKnowledge && (
        <UIArticle>
          <UIFormField gap={8} direction='column'>
            <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
              인덱스명
            </UITypography>
            <UIInput.Text value={indexName} onChange={e => onIndexNameChange?.(e.target.value)} placeholder='인덱스명 입력 (예: my_knowledge_index)' />
          </UIFormField>
        </UIArticle>
      )}

      {!isCustomKnowledge && (
        <UIArticle>
          <UIFormField gap={8} direction='column'>
            <UIGroup gap={4} direction={'column'}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '2px' }}>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                  동기화 여부
                </UITypography>
                <UITooltip
                  trigger='click'
                  position='bottom-start'
                  type='notice'
                  title='동기화 여부'
                  items={[
                    '비정형데이터플랫폼의 원천 MD데이터가 변경시 해당 지식 REPO내의 지식 데이터 동기화 여부를 선택해주세요.',
                    '한 번 설정한 동기화 여부는 수정할 수 없습니다.',
                  ]}
                  bulletType='default'
                  showArrow={false}
                  showCloseButton={true}
                  className='tooltip-wrap ml-1'
                >
                  <UIButton2 className='btn-ic'>
                    <UIIcon2 className='ic-system-20-info' />
                  </UIButton2>
                </UITooltip>
              </div>
              <UITypography variant='body-2' className='secondary-neutral-600'>
                동기화 선택시 해당 지식 Repo는 실시간으로 데이터 동기화를 진행합니다.
              </UITypography>
            </UIGroup>
            <UIUnitGroup gap={0}>
              <UIToggle size='medium' checked={syncEnabled} onChange={() => onSyncEnabledChange(!syncEnabled)} />
            </UIUnitGroup>
          </UIFormField>
        </UIArticle>
      )}

      {!isCustomKnowledge && syncEnabled && (
        <UIArticle>
          <UIFormField gap={12} direction='column'>
            <div style={{ display: 'flex', alignItems: 'center', gap: '2px' }}>
              <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                동기화 대상
              </UITypography>
            </div>
            <UIUnitGroup gap={12} direction='column' align='start' className='group-radio'>
              <UICheckbox2
                name='syncTarget'
                value='option1'
                label='개발계'
                className='chk box'
                checked={syncTargets.includes('option1')}
                onChange={(checked, value) => handleSyncTargetChange(checked, value)}
              />
              <UICheckbox2
                name='syncTarget'
                value='option2'
                label='운영계'
                className='chk box'
                checked={syncTargets.includes('option2')}
                onChange={(checked, value) => handleSyncTargetChange(checked, value)}
              />
            </UIUnitGroup>
          </UIFormField>
        </UIArticle>
      )}
    </>
  );
};
