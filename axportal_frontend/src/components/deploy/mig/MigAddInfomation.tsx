import { UITypography } from '@/components/UI/atoms';
import { UIListNoData } from '@/components/UI/molecules/list';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIPageBody } from '@/components/UI/molecules/UIPageBody';
import type { GetMigMasWithMapResponseItem } from '@/services/deploy/mig/types';
import { useMemo } from 'react';

interface MigAddInfomationProps {
  dataList?: GetMigMasWithMapResponseItem[];
}

// mapAsstG별 한글 라벨 매핑
const MAP_ASST_G_LABEL: Record<string, string> = {
  AGENT_GRAPH: '에이전트 그래프',
  FEW_SHOT: 'Few Shot',
  MCP: 'MCP',
  PROMPT: '프롬프트',
  MODEL: '모델',
  VECTOR_DB: '벡터 DB',
  EMBEDDING_MODEL: '임베딩 모델',
};

// JSON 파싱 헬퍼 함수
const parseJsonSafely = (jsonString?: string): any => {
  if (!jsonString || jsonString.trim() === '') return null;
  try {
    return JSON.parse(jsonString);
  } catch {
    return jsonString; // 파싱 실패 시 원본 문자열 반환
  }
};

// 개발/운영 값 추출 헬퍼 함수 (mapDvlpDtlCtnt, mapUnyungDtlCtnt에서 직접 추출)
const extractValue = (data: any): string => {
  if (typeof data === 'object' && data !== null) {
    // 객체인 경우 JSON 문자열로 변환
    try {
      return JSON.stringify(data);
    } catch {
      return String(data);
    }
  }
  if (typeof data === 'string') {
    return data;
  }
  return String(data || '');
};

export function MigAddInfomation({ dataList = [] }: MigAddInfomationProps) {
  // 값이 있는 항목만 필터링하는 헬퍼 함수
  const hasValue = (item: GetMigMasWithMapResponseItem): boolean => {
    const devValue = extractValue(parseJsonSafely(item.mapDvlpDtlCtnt));
    const prodValue = extractValue(parseJsonSafely(item.mapUnyungDtlCtnt));
    // 개발 또는 운영 값 중 하나라도 값이 있으면 true
    const hasDevValue = typeof devValue === 'string' && devValue.trim() !== '' && devValue !== '-';
    const hasProdValue = typeof prodValue === 'string' && prodValue.trim() !== '' && prodValue !== '-';
    return hasDevValue || hasProdValue;
  };

  // 값이 있는 항목만 필터링
  const filteredDataList = useMemo(() => {
    return dataList.filter(item => hasValue(item));
  }, [dataList]);

  // mapAsstG별로 그룹화 (값이 있는 항목만)
  const groupedData = useMemo(() => {
    const groups: Record<string, GetMigMasWithMapResponseItem[]> = {};

    filteredDataList.forEach(item => {
      const groupKey = item.mapAsstG || 'OTHER';
      if (!groups[groupKey]) {
        groups[groupKey] = [];
      }
      groups[groupKey].push(item);
    });

    return groups;
  }, [filteredDataList]);

  // 그룹 순서 정의
  const groupOrder = ['AGENT_GRAPH', 'FEW_SHOT', 'PROMPT', 'MODEL', 'MCP', 'VECTOR_DB', 'EMBEDDING_MODEL'];
  // 그룹 순서대로 정렬된 키 배열
  const sortedGroupKeys = useMemo(() => {
    const ordered: string[] = [];
    const unordered: string[] = [];

    groupOrder.forEach(key => {
      if (groupedData[key]) {
        ordered.push(key);
      }
    });

    Object.keys(groupedData).forEach(key => {
      if (!groupOrder.includes(key)) {
        unordered.push(key);
      }
    });

    return [...ordered, ...unordered];
  }, [groupedData]);

  // 값이 있는 데이터가 없으면 "데이터 없음" 표시
  if (filteredDataList.length === 0) {
    return (
      <section className='section-page'>
        <UIPageBody>
          <div className='flex items-center justify-center min-h-[400px]'>
            <UIListNoData noDataMessage='입력한 추가 정보가 없습니다.' />
          </div>
        </UIPageBody>
      </section>
    );
  }

  return (
    <>
      <section className='section-page'>
        <UIPageBody>
          {sortedGroupKeys.map((groupKey, groupIndex) => {
            const items = groupedData[groupKey];
            const groupLabel = MAP_ASST_G_LABEL[groupKey] || groupKey;

            return (
              <UIArticle key={groupKey} className={groupIndex === 0 ? 'mt-8' : ''}>
                <div className='article-header'>
                  <UITypography variant='title-4' className='secondary-neutral-900'>
                    {groupLabel}
                  </UITypography>
                </div>
                <div className='article-body'>
                  {items.map((item, index) => {
                    const devValue = extractValue(parseJsonSafely(item.mapDvlpDtlCtnt));
                    const prodValue = extractValue(parseJsonSafely(item.mapUnyungDtlCtnt));

                    return (
                      <div key={`${item.mapSeqNo}-${index}`}>
                        <div className={index == 0 ? 'border-t border-black' : ''}>
                          <table className='tbl-v'>
                            <colgroup>
                              <col style={{ width: '10%' }} />
                              <col style={{ width: '40%' }} />
                              <col style={{ width: '10%' }} />
                              <col style={{ width: '40%' }} />
                            </colgroup>
                            <tbody>
                              <tr>
                                <th>
                                  <UITypography variant='body-2' className='secondary-neutral-900'>
                                    {item.mapMigMapNm ? `${item.mapMigMapNm} (개발)` : '개발'}
                                  </UITypography>
                                </th>
                                <td>
                                  <UITypography variant='body-2' className='secondary-neutral-600'>
                                    {devValue || '-'}
                                  </UITypography>
                                </td>
                                <th>
                                  <UITypography variant='body-2' className='secondary-neutral-900'>
                                    {item.mapMigMapNm ? `${item.mapMigMapNm} (운영)` : '운영'}
                                  </UITypography>
                                </th>
                                <td>
                                  <UITypography variant='body-2' className='secondary-neutral-600'>
                                    {prodValue || '-'}
                                  </UITypography>
                                </td>
                              </tr>
                            </tbody>
                          </table>
                        </div>
                      </div>
                    );
                  })}
                </div>
              </UIArticle>
            );
          })}
        </UIPageBody>
      </section>
    </>
  );
}
