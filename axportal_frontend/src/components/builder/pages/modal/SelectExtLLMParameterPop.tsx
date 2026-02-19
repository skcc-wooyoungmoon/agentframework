import { UIDataCnt, UIPagination, UITextLabel } from '@/components/UI';
import { UIInput } from '@/components/UI/molecules';
import { UIArticle } from '@/components/UI/molecules/UIArticle';
import { UIGrid } from '@/components/UI/molecules/grid';
import { UIListContainer, UIListContentBox } from '@/components/UI/molecules/list';
import { keyTableAtom, nodesAtom } from '@/components/builder/atoms/AgentAtom';
import { type KeyTableData } from '@/components/builder/types/Agents';
import { useAtom } from 'jotai';
import React, { type FC, memo, useEffect, useMemo, useState } from 'react';

interface ExtLLMParameter {
  id: string;
  name: string;
  nodeName: string;
  description: string;
  key: string;
  keytable_id?: string;
  nodeId: string;
  originalData: KeyTableData & { description?: string };
}

interface SelectExtLLMParameterPopProps {
  nodeId?: string;
  selectedParams?: Array<{ key: string; keytable_id: string; nodeId: string }>; // 이미 선택된 파라미터들
  onSelect?: (selectedParams: ExtLLMParameter[]) => void;
}

const ExtLLMParameterList: FC<SelectExtLLMParameterPopProps> = ({ onSelect, selectedParams: initialSelectedParams }) => {
  const [page, setPage] = useState(1);
  const size = 6;
  const [tempSearchValue, setTempSearchValue] = useState(''); // UI 입력용
  const [appliedSearchValue, setAppliedSearchValue] = useState(''); // 실제 필터링용
  
  const [nodes] = useAtom(nodesAtom);
  const [keyTableList] = useAtom(keyTableAtom);

  const [tempSelectedParams, setTempSelectedParams] = useState<ExtLLMParameter[]>([]);

  // InputNode에서 등록한 LLM parameters만 필터링
  // nodesAtom에서 최신 노드 데이터를 가져와서 사용
  const llmParameters = useMemo(() => {
    const result: (KeyTableData & { description?: string })[] = [];
    const seenIds = new Set<string>();

    // nodes에서 InputNode들을 직접 찾기 (최신 데이터)
    const inputNodes = nodes.filter(node => node.type === 'input__basic');
    if (inputNodes.length === 0) {
      return result;
    }

    // 각 InputNode의 input_keys를 순회하면서 LLM parameters 찾기
    inputNodes.forEach(node => {
      if (node.data?.input_keys) {
        const inputKeys = node.data.input_keys as any[];

        inputKeys.forEach((inputKey: any) => {
          // object_type이 'LLM parameters'인 것만
          if (inputKey.object_type === 'LLM parameters') {
            const keytableId = inputKey.keytable_id || `${node.id}_${inputKey.name}`;

            // 중복 제거
            if (seenIds.has(keytableId)) {
              return;
            }
            seenIds.add(keytableId);

            // keytable_id로 keyTableList에서 해당 항목 찾기
            const keyTableItem = keyTableList.find(kt => kt.id === keytableId);

            // fixed_value가 객체인 경우 문자열로 변환
            let valueStr = '';
            if (inputKey.fixed_value) {
              if (typeof inputKey.fixed_value === 'object') {
                valueStr = JSON.stringify(inputKey.fixed_value);
              } else {
                valueStr = String(inputKey.fixed_value);
              }
            }

            // inputKey.name을 우선 사용 (keyTableItem의 name은 다른 형식일 수 있음)
            const itemName = inputKey.name || '';
            const itemKey = inputKey.name || '';

            if (keyTableItem) {
              result.push({
                ...keyTableItem,
                name: itemName, // inputKey.name으로 덮어쓰기
                key: itemKey, // inputKey.name으로 덮어쓰기
                description: inputKey.description || '',
                node: node, // 최신 노드 데이터로 업데이트
              });
            } else {
              // keyTableList에 없으면 node 정보로 직접 생성
              const newItem = {
                id: keytableId,
                name: itemName,
                key: itemKey,
                value: valueStr,
                nodeId: node.id,
                nodeType: node.type || 'input__basic',
                nodeName: (node.data?.name as string) || '',
                node: node,
                description: inputKey.description || '',
              };
              result.push(newItem);
            }
          }
        });
      }
    });

    return result;
  }, [nodes, keyTableList]);

  // 검색 필터링
  const filteredData = useMemo(() => {
    if (!appliedSearchValue.trim()) {
      return llmParameters;
    }
    const lowerSearchTerm = appliedSearchValue.toLowerCase();
    return llmParameters.filter(
      item =>
        item.name?.toLowerCase().includes(lowerSearchTerm) ||
        item.nodeName?.toLowerCase().includes(lowerSearchTerm) ||
        item.key?.toLowerCase().includes(lowerSearchTerm) ||
        item.description?.toLowerCase().includes(lowerSearchTerm)
    );
  }, [llmParameters, appliedSearchValue]);

  // 페이지네이션
  const paginatedData = useMemo(() => {
    const startIndex = (page - 1) * size;
    const endIndex = startIndex + size;
    return filteredData.slice(startIndex, endIndex);
  }, [filteredData, page, size]);

  const totalPages = Math.ceil(filteredData.length / size);

  // 그리드 컬럼 정의
  const columnDefs: any = React.useMemo(
    () => [
      {
        headerName: 'NO',
        field: 'no' as const,
        width: 56,
        minWidth: 56,
        maxWidth: 56,
        cellClass: 'text-center',
        headerClass: 'text-center',
        cellStyle: {
          textAlign: 'center',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        },
        sortable: false,
        suppressHeaderMenuButton: true,
        suppressSizeToFit: true,
      },
      {
        headerName: 'Name',
        field: 'name' as const,
        minWidth: 200,
        flex: 1,
        cellStyle: { paddingLeft: '16px' },
        cellRenderer: memo((params: any) => {
          return (
            <div className='flex items-center gap-2'>
              <span>{params.value}</span>
              <UITextLabel intent='blue'>LLM parameters</UITextLabel>
            </div>
          );
        }),
      },
      {
        headerName: '노드 이름',
        field: 'nodeName' as const,
        width: 200,
        cellStyle: { paddingLeft: '16px' },
      },
      {
        headerName: 'KeyTable ID',
        field: 'keytable_id' as const,
        minWidth: 150,
        flex: 1,
        cellStyle: { paddingLeft: '16px' },
      },
    ],
    []
  );

  // 그리드용 데이터 변환
  const gridData = useMemo(
    () =>
      paginatedData.map((item, index) => {
        // keytable_id 가져오기: input_keys에서 찾거나 item.id 사용
        let keytableId = item.id || '';
        if (item.node?.data?.input_keys) {
          const inputKeys = item.node.data.input_keys as any[];
          const llmKey = inputKeys.find((key: any) => key.name === item.key && key.object_type === 'LLM parameters');
          if (llmKey?.keytable_id) {
            keytableId = llmKey.keytable_id;
          }
        }

        return {
          id: item.id || `${item.nodeId}_${item.key}`,
          no: (page - 1) * size + index + 1,
          name: item.name || item.key,
          nodeName: item.nodeName || '-',
          description: item.description || '',
          key: item.key || '',
          keytable_id: keytableId,
          originalData: item,
        };
      }),
    [paginatedData, page, size]
  );

  // 초기 선택 상태 설정 (이미 선택된 파라미터가 있으면 그리드에서 선택 상태로 표시)
  useEffect(() => {
    if (!initialSelectedParams || initialSelectedParams.length === 0) {
      setTempSelectedParams([]);
      return;
    }

    // gridData에서 initialSelectedParams와 매칭되는 항목 찾기
    const matchedItems = gridData.filter(item => {
      return initialSelectedParams.some(selected => {
        // keytable_id로 매칭 (가장 정확)
        if (selected.keytable_id && item.keytable_id === selected.keytable_id) {
          return true;
        }
        // key와 nodeId로 매칭 (fallback)
        if (selected.key === item.key && selected.nodeId === item.originalData?.nodeId) {
          return true;
        }
        return false;
      });
    });

    if (matchedItems.length > 0) {
      const selected = matchedItems.map(item => ({
        id: item.id,
        name: item.name,
        nodeName: item.nodeName,
        description: item.description,
        key: item.key,
        keytable_id: item.keytable_id || '',
        nodeId: item.originalData?.nodeId || '',
        originalData: item.originalData,
      }));
      setTempSelectedParams(selected);
      // 초기 선택 상태도 onSelect 호출하여 상위 컴포넌트의 ref 업데이트
      if (onSelect) {
        onSelect(selected);
      }
    }
  }, [gridData, initialSelectedParams, onSelect]);

  // 선택된 그리드 데이터
  const selectedGridData = useMemo(() => {
    if (tempSelectedParams.length === 0) return [];
    return gridData.filter(item => tempSelectedParams.some(selected => selected.id === item.id));
  }, [tempSelectedParams, gridData]);

  // 페이지네이션 핸들러
  const handlePageChange = (newPage: number) => {
    setPage(newPage);
  };

  // 검색 핸들러 (Enter 키 입력 시)
  const handleSearch = () => {
    setAppliedSearchValue(tempSearchValue);
    setPage(1);
  };

  // 그리드 선택 핸들러
  const handleGridSelection = (selectedItems: any[]) => {
    const selected = selectedItems.map(item => ({
      id: item.id,
      name: item.name,
      nodeName: item.nodeName,
      description: item.description,
      key: item.key,
      keytable_id: item.keytable_id || '',
      nodeId: item.originalData?.nodeId || '',
      originalData: item.originalData,
    }));
    setTempSelectedParams(selected);
    // 선택이 변경될 때마다 onSelect 호출하여 상위 컴포넌트의 ref 업데이트
    if (onSelect) {
      onSelect(selected);
    }
  };

  return (
    <section className='section-modal'>
      <div className='mb-4 p-4 bg-blue-50 border border-blue-200 rounded-lg'>
        <p className='text-sm text-gray-700'>InputNode에서 등록한 LLM parameters 항목을 선택하세요.</p>
      </div>
      <UIArticle className='article-grid'>
        <UIListContainer>
          <UIListContentBox.Header>
            <div className='flex justify-between items-center w-full'>
              <div className='flex-shrink-0'>
                <div style={{ width: '168px', paddingRight: '8px' }}>
                  <UIDataCnt count={filteredData.length} prefix='총' unit='건' />
                </div>
              </div>
              <div>
                <div className='w-[360px]'>
                  <UIInput.Search
                    value={tempSearchValue}
                    placeholder='Name, 노드 이름 입력'
                    onChange={e => {
                      setTempSearchValue(e.target.value);
                    }}
                    onKeyDown={e => {
                      if (e.key === 'Enter') {
                        handleSearch();
                      }
                    }}
                  />
                </div>
              </div>
            </div>
          </UIListContentBox.Header>
          <UIListContentBox.Body>
            <UIGrid type='single-select' rowData={gridData} columnDefs={columnDefs} selectedDataList={selectedGridData} onCheck={handleGridSelection} />
          </UIListContentBox.Body>
          <UIListContentBox.Footer>
            <UIPagination currentPage={page} totalPages={totalPages} onPageChange={handlePageChange} className='flex justify-center' />
          </UIListContentBox.Footer>
        </UIListContainer>
      </UIArticle>
    </section>
  );
};

export const SelectExtLLMParameterPop: FC<SelectExtLLMParameterPopProps> = ({ nodeId, selectedParams, onSelect }) => {
  return <ExtLLMParameterList nodeId={nodeId} selectedParams={selectedParams} onSelect={onSelect} />;
};
