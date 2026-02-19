import { TemplateService } from '@/services/agent/builder/agentBuilder.services';
import type { Template } from '@/services/agent/builder/types';
import { useCallback, useEffect, useState } from 'react';

export interface UseAgentTemplatesReturn {
  templates: Template[];
  isLoading: boolean;
  error: string | null;
  selectedTemplate: Template | null;
  fetchTemplates: () => Promise<void>;
  selectTemplate: (template: Template) => void;
  clearSelection: () => void;
}

export const useAgentTemplates = (): UseAgentTemplatesReturn => {
  const [templates, setTemplates] = useState<Template[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [selectedTemplate, setSelectedTemplate] = useState<Template | null>(null);

  const fetchTemplates = useCallback(async () => {
    setIsLoading(true);
    setError(null);

    try {
      // console.log('=== 템플릿 데이터 가져오기 시작 ===');
      // console.log('🔐 API에서 템플릿 데이터를 가져옵니다.');
      const apiTemplates = await TemplateService.getTemplates();
      // console.log('📦 TemplateService에서 반환된 데이터:', apiTemplates);
      // console.log('📦 반환된 데이터 타입:', typeof apiTemplates);
      // console.log('📦 반환된 데이터가 배열인지:', Array.isArray(apiTemplates));
      setTemplates(apiTemplates);
      // console.log('✅ API 데이터 설정됨:', apiTemplates);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : '알 수 없는 오류가 발생했습니다.';
      setError(errorMessage);
      // console.error('❌ 템플릿 데이터 가져오기 실패:', err);
    } finally {
      setIsLoading(false);
    }
  }, []);

  const selectTemplate = useCallback((template: Template) => {
    setSelectedTemplate(template);
    // console.log('📋 템플릿 선택됨:', template);
  }, []);

  const clearSelection = useCallback(() => {
    setSelectedTemplate(null);
    // console.log('<img alt="ico-system-24-outline-gray-trash" class="w-[24px] h-[24px]  " src="/assets/images/system/ico-system-24-outline-gray-trash.svg" /> 템플릿 선택 해제됨');
  }, []);

  // 컴포넌트 마운트 시 템플릿 데이터 가져오기
  useEffect(() => {
    fetchTemplates();
  }, [fetchTemplates]);

  return {
    templates,
    isLoading,
    error,
    selectedTemplate,
    fetchTemplates,
    selectTemplate,
    clearSelection,
  };
};
