import { useQuery } from '@tanstack/react-query';
import { getSourceSystems } from '@/services/data/sourceSystem.services';

export const useSourceSystems = (enabled: boolean = true) => {
  return useQuery({
    queryKey: ['sourceSystems'],
    queryFn: getSourceSystems,
    staleTime: 5 * 60 * 1000, // 5분
    gcTime: 10 * 60 * 1000, // 10분
    enabled, // 팝업이 열릴 때만 실행
  });
};
