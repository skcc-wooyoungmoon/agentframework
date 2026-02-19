import { useEffect, useMemo, useRef } from 'react';
import { UIButton2, UIToggle, UITypography } from '@/components/UI/atoms';
import { UIIcon2 } from '@/components/UI/atoms/UIIcon2';
import { UICode } from '@/components/UI/atoms/UICode';
import { UIArticle, UIFormField, UIGroup, UIList, UIUnitGroup } from '@/components/UI/molecules';
import { UIInput } from '@/components/UI/molecules/input';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { useGetEmbeddingModels, useGetVectorDBs } from '@/services/knowledge/knowledge.services';

type KnowledgeRegistrationPageProps = {
  embeddingModel: string;
  vectorDB: string;
  indexName: string;
  script: string;
  isCustomKnowledge?: boolean; // 사용자 정의 지식 여부
  embeddingModelId?: string;
  vectorDBId?: string;
  isEmbeddingDropdownOpen?: boolean;
  isVectorDBDropdownOpen?: boolean;
  toggleChecked?: boolean;
  onToggleChange?: (checked: boolean) => void;
  onScriptChange: (value: string) => void;
  onTest?: () => void;
  onEmbeddingModelChange?: (value: string, id: string) => void;
  onVectorDBChange?: (value: string, id: string) => void;
  onIndexNameChange?: (value: string) => void;
};

export const KnowledgeRegistrationPage: React.FC<KnowledgeRegistrationPageProps> = ({
  embeddingModel,
  vectorDB,
  indexName,
  script,
  isCustomKnowledge = false,
  // embeddingModelId = '',
  // vectorDBId = '',
  toggleChecked = false,
  onToggleChange,
  onScriptChange,
  onEmbeddingModelChange,
  onVectorDBChange,
  onIndexNameChange,
}) => {
  // 임베딩 모델 목록은 항상 조회 (displayName 표시를 위해)
  const { data: embeddingModelsData } = useGetEmbeddingModels(0, 100);

  const { data: vectorDBsData } = useGetVectorDBs({ enabled: isCustomKnowledge });

  // 임베딩 모델 목록 추출 (백엔드 API에서 filter=type:embedding 조건으로 조회)
  const embeddingModels = useMemo(() => {
    return embeddingModelsData?.content || [];
  }, [embeddingModelsData]);

  // 벡터DB 목록 추출
  const vectorDBs = useMemo(() => {
    if (!isCustomKnowledge) return [];
    return vectorDBsData?.content || [];
  }, [vectorDBsData, isCustomKnowledge]);

  // 임베딩 모델 로드 시 첫 번째 항목을 기본값으로 설정
  const embeddingInitRef = useRef(false);
  useEffect(() => {
    if (!isCustomKnowledge || embeddingInitRef.current) return;
    if (embeddingModels.length > 0 && !embeddingModel && onEmbeddingModelChange) {
      const first = embeddingModels.find((m: any) => {
        const baseName = m?.name ?? m?.modelName ?? m?.displayName;
        return typeof baseName === 'string' && baseName.trim() !== '';
      });
      if (first) {
        embeddingInitRef.current = true;
        const firstName = first.name ?? first.modelName ?? first.displayName;
        onEmbeddingModelChange(firstName, first.id ?? first.servingId);
      }
    }
  }, [embeddingModels, embeddingModel, onEmbeddingModelChange, isCustomKnowledge]);

  // 벡터DB 로드 시 첫 번째 항목을 기본값으로 설정
  const vectorDBInitRef = useRef(false);
  useEffect(() => {
    if (!isCustomKnowledge || vectorDBInitRef.current) return;
    if (vectorDBs.length > 0 && !vectorDB && onVectorDBChange) {
      vectorDBInitRef.current = true;
      const first = vectorDBs[0];
      onVectorDBChange(first.name, first.id);
    }
  }, [vectorDBs, vectorDB, onVectorDBChange, isCustomKnowledge]);

  // 드롭다운 옵션 변환
  const embeddingOptions = useMemo(() => {
    return embeddingModels
      .map((model: any) => {
        const baseName = model.name ?? model.modelName ?? model.displayName;
        if (!baseName || (typeof baseName === 'string' && baseName.trim() === '')) {
          return null;
        }

        return {
          value: baseName,
          label: baseName,
          id: model.id ?? model.servingId,
        };
      })
      .filter(Boolean);
  }, [embeddingModels]);

  const vectorDBOptions = useMemo(() => {
    return vectorDBs.map((db: any) => ({
      value: db.name,
      label: db.name,
      id: db.id,
    }));
  }, [vectorDBs]);
  // 선택된 임베딩 모델의 displayName을 찾아서 표시
  const selectedEmbeddingLabel = useMemo(() => {
    if (!embeddingModel) return '';
    const selected = embeddingOptions.find((opt: any) => opt.value === embeddingModel);
    return selected ? selected.label : embeddingModel;
  }, [embeddingModel, embeddingOptions]);

  return (
    <>
      {/* 지식 활성화 안내사항 */}
      <UIArticle>
        <div className='box-fill'>
          <UIUnitGroup gap={8} direction='column' align='start'>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0 6px' }}>
              <UIIcon2 className='ic-system-16-info-gray' />
              <UITypography variant='body-2' className='secondary-neutral-600 text-sb'>
                정상 작동하는 ‘활성화’ 상태로 지식을 생성하려면 아래 사항을 반드시 확인해주세요.
              </UITypography>
            </div>
            <div style={{ paddingLeft: '22px' }}>
              <UIUnitGroup gap={8} direction='column' align='start'>
                <UIList
                  gap={4}
                  direction='column'
                  className='ui-list_dash'
                  data={[
                    {
                      dataItem: (
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {`선택한 임베딩 모델이 정상 동작하는 모델(File/API)인지, 모델 배포 목록에서 상태가 ‘이용가능’인지 확인해주세요.`}
                        </UITypography>
                      ),
                    },
                  ]}
                />
                <UIList
                  gap={4}
                  direction='column'
                  className='ui-list_dash'
                  data={[
                    {
                      dataItem: (
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {`선택한 벡터 DB가 맞는지 다시 한 번 확인해주세요. 기본 지식과 사용자 정의 지식은 사용하는 벡터 DB가 다를 수 있습니다.`}
                        </UITypography>
                      ),
                    },
                  ]}
                />
                <UIList
                  gap={4}
                  direction='column'
                  className='ui-list_dash'
                  data={[
                    {
                      dataItem: (
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {`Default Script를 수정한 경우, 입력한 Script 내용이 올바른지 확인해주세요.`}
                        </UITypography>
                      ),
                    },
                  ]}
                />
                <UIList
                  gap={4}
                  direction='column'
                  className='ui-list_dash'
                  data={[
                    {
                      dataItem: (
                        <UITypography variant='body-2' className='secondary-neutral-600'>
                          {`사용자 정의 지식은 우측 상단 [테스트] 실행 결과가 실패한 상태에서 생성하면, 비활성화 상태로 생성되므로 테스트 성공 이후 등록 해 주세요.`}
                        </UITypography>
                      ),
                    },
                  ]}
                />
              </UIUnitGroup>
            </div>
          </UIUnitGroup>
        </div>
      </UIArticle>
      {/* 임베딩 모델 입력 필드 */}
      <UIArticle>
        <UIFormField gap={8} direction='column'>
          <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
            임베딩 모델
          </UITypography>
          {isCustomKnowledge ? (
            <UIDropdown
              required={true}
              value={selectedEmbeddingLabel}
              onSelect={(value: string) => {
                // value는 model.name이므로 name으로 검색
                const selected = embeddingModels.find((m: any) => {
                  const baseName = m.name ?? m.modelName ?? m.displayName;
                  return baseName === value;
                });

                if (selected && onEmbeddingModelChange) {
                  const selectedId = selected.id ?? selected.servingId;
                  // console.log('🎯 임베딩 모델 선택:', { value, id: selectedId, selected });
                  onEmbeddingModelChange(value, selectedId);
                } else {
                  // console.warn('⚠️ 임베딩 모델 찾기 실패 - value:', value, 'embeddingModels:', embeddingModels);
                }
              }}
              options={embeddingOptions}
            />
          ) : (
            <UIDropdown
              required={true}
              value={selectedEmbeddingLabel}
              readonly={true}
              options={[{ value: embeddingModel, label: selectedEmbeddingLabel }]}
              onClick={() => {}}
              onSelect={() => {}}
            />
          )}
        </UIFormField>
      </UIArticle>

      {/* 벡터 DB 입력 필드 */}
      <UIArticle>
        <UIFormField gap={8} direction='column'>
          <UIGroup direction='column' gap={4}>
            <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
              벡터 DB
            </UITypography>
            <UITypography variant='body-2' className='secondary-neutral-600'>
              올바른 벡터DB를 선택해야 지식이 정상적으로 동작합니다. 기본 지식과 사용자 정의 지식은 사용하는 벡터DB가 다를 수 있으니, 선택한 벡터DB를 한 번 더 확인해주세요.
            </UITypography>
            {isCustomKnowledge ? (
              <UIDropdown
                required={true}
                value={vectorDB || ''}
                onSelect={(value: string) => {
                  const selected = vectorDBs.find((db: any) => db.name === value);
                  if (selected && onVectorDBChange) {
                    onVectorDBChange(value, selected.id);
                  }
                }}
                options={vectorDBOptions}
              />
            ) : (
              <div>
                <div className='form-group'>
                  <UIInput.Text value={vectorDB} placeholder='벡터 DB 입력' disabled={true} />
                </div>
              </div>
            )}
          </UIGroup>
        </UIFormField>
      </UIArticle>

      {/* 인덱스명 입력 필드 */}
      <UIArticle>
        <UIFormField gap={8} direction='column'>
          <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
            인덱스명
          </UITypography>
          <div className='form-group'>
            <UIInput.Text
              value={indexName}
              onChange={isCustomKnowledge && onIndexNameChange ? e => onIndexNameChange(e.target.value) : undefined}
              placeholder='인덱스명 입력'
              maxLength={52}
              disabled={!isCustomKnowledge}
            />
          </div>
        </UIFormField>
      </UIArticle>

      {/* Script 영역 */}
      <UIArticle>
        <UIFormField gap={4} direction='column'>
          <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
            Script
          </UITypography>
          <UITypography variant='body-2' className='secondary-neutral-600'>
            토글을 끄면 시스템 기본 스크립트를 사용하고, 토글을 켜면 직접 편집한 스크립트를 사용합니다.
          </UITypography>
          <div className='flex' style={{ marginBottom: '4px' }}>
            <UIToggle
              size='medium'
              checked={toggleChecked}
              onChange={() => {
                onToggleChange?.(!toggleChecked);
                // console.log('활성화 토글 클릭:', !toggleChecked);
              }}
            />
          </div>
          {/* 소스코드 영역 */}
          {toggleChecked && <UICode value={script} language='python' theme='dark' width='100%' minHeight='700px' maxHeight='700px' readOnly={false} onChange={onScriptChange} />}
        </UIFormField>
      </UIArticle>
    </>
  );
};

// 지식 등록 페이지 헤더 액션 (테스트 버튼)
export const KnowledgeRegistrationPageActions: React.FC<{ onTest?: () => void }> = ({ onTest }) => {
  return (
    <UIButton2 className='btn-tertiary-sky-blue' onClick={onTest}>
      테스트
    </UIButton2>
  );
};
