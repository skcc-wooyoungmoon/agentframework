import React, { useEffect, useMemo, useRef } from 'react';
import { UIArticle, UIFormField, UIGroup, UIInput } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { useGetChunkingModules } from '@/services/knowledge/knowledge.services';
import { UITypography } from '@/components/UI/atoms';

type KnowledgeChunkingSettingPageProps = {
  chunkingMethod: string;
  chunkingMethodId: string;
  chunkSize: string;
  sentenceOverlap: string;
  onChunkingMethodChange: (value: string, id: string) => void;
  onChunkSizeChange: (value: string) => void;
  onSentenceOverlapChange: (value: string) => void;
};

export const KnowledgeChunkingSettingPage: React.FC<KnowledgeChunkingSettingPageProps> = ({
  chunkingMethod,
  // chunkingMethodId,
  chunkSize,
  sentenceOverlap,
  onChunkingMethodChange,
  onChunkSizeChange,
  onSentenceOverlapChange,
}) => {
  // 청킹 모듈 목록 조회 - 이 화면에 진입할 때만 호출
  const { data: chunkingModules = [] } = useGetChunkingModules();

  // 청킹 모듈 로드 시 첫 번째 항목을 기본값으로 "정말 한 번만" 설정
  const initRef = useRef(false);
  useEffect(() => {
    if (initRef.current) return;
    if (chunkingModules.length > 0 && !chunkingMethod) {
      initRef.current = true; // StrictMode로 인한 두 번 마운트 방지
      const first = chunkingModules[0];
      // console.log('✅ 첫 번째 청킹 모듈 설정:', first);
      onChunkingMethodChange(first.chunkNm, first.chunkId);
    }
  }, [chunkingModules, chunkingMethod, onChunkingMethodChange]);

  // 드롭다운 옵션 변환
  const chunkingOptions = useMemo(
    () =>
      chunkingModules.map(m => ({
        value: m.chunkNm,
        label: m.chunkNm,
        id: m.chunkId,
      })),
    [chunkingModules]
  );

  return (
    <>
      <UIArticle>
        <UIFormField gap={8} direction='column'>
          <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
            청킹 방법
          </UITypography>
          <UIDropdown
            // label='청킹 방법'
            required
            value={chunkingMethod || ''} // value가 undefined 되지 않도록 가드
            readonly={false}
            options={chunkingOptions}
            onSelect={(value: string) => {
              // value(=chunkNm)로 해당 모듈의 ID를 찾아서 함께 전달
              const selected = chunkingModules.find(m => m.chunkNm === value);
              if (selected) {
                onChunkingMethodChange(value, selected.chunkId);
              }
              // 청크 사이즈, 청크 오버랩 초기화
              onChunkSizeChange('');
              onSentenceOverlapChange('');
            }}
          />
        </UIFormField>
      </UIArticle>

      {/* {chunkingMethodId === 'kss' && (
        <> */}
      <UIArticle>
        <UIFormField gap={8} direction='column'>
          <UIGroup direction='column' gap={4}>
            <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
              청크 사이즈
            </UITypography>
            <UITypography variant='body-2' className='secondary-neutral-600'>
              별도로 입력하지 않을 경우 기본값인 300으로 적용됩니다.
            </UITypography>
          </UIGroup>
          <div>
            <UIInput.Text
              value={chunkSize}
              onChange={e => {
                let value = e.target.value.replace(/[^0-9]/g, '');
                // 숫자로 변환후 다시 문자열로 변환 (앞의 0 자동 제거)
                if (value) {
                  value = String(Number(value));
                }
                onChunkSizeChange(value);
              }}
              placeholder='청크 사이즈 입력'
              maxLength={10}
            />
          </div>
        </UIFormField>
      </UIArticle>
      <UIArticle>
        <UIFormField gap={8} direction='column'>
          <UIGroup direction='column' gap={4}>
            <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={false}>
              청킹 오버랩
            </UITypography>
            <UITypography variant='body-2' className='secondary-neutral-600'>
              별도로 입력하지 않을 경우 기본값인 0으로 적용됩니다.
              {/* 문장 오버랩은 인접한 청크끼리 공유할 문장 개수입니다. 예를들어 1로 설정하면, 생성된 청크들의 앞뒤로 1문장씩은 중복된다는 의미입니다. */}
            </UITypography>
          </UIGroup>
          <div>
            <UIInput.Text
              value={sentenceOverlap}
              onChange={e => {
                let value = e.target.value.replace(/[^0-9]/g, '');
                // 숫자로 변환후 다시 문자열로 변환 (앞의 0 자동 제거)
                if (value) {
                  value = String(Number(value));
                }
                onSentenceOverlapChange(value);
              }}
              placeholder='청킹 오버랩 입력'
              maxLength={10}
            />
          </div>
        </UIFormField>
      </UIArticle>
      {/* </>
      )} */}
    </>
  );
};
