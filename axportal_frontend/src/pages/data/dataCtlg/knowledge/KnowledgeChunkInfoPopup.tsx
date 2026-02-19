import { UICode } from '@/components/UI/atoms/UICode';
import { UITypography } from '@/components/UI/atoms';
import { UIArticle, UIFormField } from '@/components/UI/molecules';
import { UITextArea2 } from '@/components/UI/molecules/input';

type KnowledgeChunkInfoPopupContentProps = {
  content: string;
  metadata?: string;
};

/*
 *
 * 청크 정보 팝업 (DT_020303_P06)
 *
 * */
export const KnowledgeChunkInfoPopupContent = ({ content, metadata }: KnowledgeChunkInfoPopupContentProps) => {
  return (
    <section className='section-modal'>
      {/* 내용 필드 */}
      <UIArticle>
        <UIFormField gap={8} direction='column'>
          <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
            내용
          </UITypography>
          <UITextArea2 value={content} placeholder='내용' readOnly />
        </UIFormField>
      </UIArticle>
      {/* 메타데이터 필드 */}
      <UIArticle>
        <UIFormField gap={8} direction='column'>
          <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
            메타데이터
          </UITypography>
          {/* 소스코드 영역 */}
          <UICode value={metadata || '메타데이터 없음'} language='json' theme='dark' width='100%' minHeight='200px' maxHeight='200px' readOnly={true} />
        </UIFormField>
      </UIArticle>
    </section>
  );
};
