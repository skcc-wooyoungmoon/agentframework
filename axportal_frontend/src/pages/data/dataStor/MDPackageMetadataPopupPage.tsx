import { UICode } from '@/components/UI/atoms/UICode';

interface MDPackageMetadataPopupPageProps {
  metadata?: Record<string, any> | null;
}

export function MDPackageMetadataPopupPage({ metadata }: MDPackageMetadataPopupPageProps) {
  // 메타데이터를 JSON 형태로 포맷팅
  const formatMetadata = (metadata: any) => {
    if (!metadata || metadata === null || metadata === undefined) {
      return '메타데이터가 없습니다.';
    }
    
    try {
      // 빈 객체인 경우 처리
      if (typeof metadata === 'object' && Object.keys(metadata).length === 0) {
        return '메타데이터가 없습니다.';
      }
      
      return JSON.stringify(metadata, null, 2);
    } catch (error) {
      // console.error('메타데이터 포맷팅 오류:', error);
      return '메타데이터를 표시할 수 없습니다.';
    }
  };

  return (
    <div className='flex h-full'>
      {/* 소스코드 영역 */}
      <UICode 
        value={formatMetadata(metadata)} 
        language='json' 
        theme='dark' 
        width='100%' 
        minHeight='472px' 
        maxHeight='472px' 
        readOnly={true} 
      />
    </div>
  );
}
