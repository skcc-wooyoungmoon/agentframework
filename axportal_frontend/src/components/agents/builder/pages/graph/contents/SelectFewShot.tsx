import { isChangeFewShotAtom, nodesAtom, selectedFewShotDataRepoAtom, selectedFewShotIdRepoAtom, selectedFewShotNameRepoAtom } from '@/components/agents/builder/atoms/AgentAtom';
import { CustomAccordionItem } from '@/components/agents/builder/common/Button/CustomAccordionItem';
import { useModal } from '@/stores/common/modal';
import { SelectFewShotPop } from '@/components/agents/builder/pages/modal/SelectFewShotPop.tsx';
import { useAtom } from 'jotai/index';
import { useMemo, useCallback } from 'react';
import React, { useEffect, useState, useRef } from 'react';
import { api } from '@/configs/axios.config';
import { useNavigate } from 'react-router-dom';

interface FewShotProps {
  selectedFewShotId: string | null;
  nodeId: string;
  asAccordionItem?: boolean;
  title?: React.ReactNode;
  readOnly?: boolean;
}

export const SelectFewShot = ({ selectedFewShotId, nodeId, asAccordionItem = false, title, readOnly = false }: FewShotProps) => {
  const { openModal } = useModal();
  const navigate = useNavigate();
  const [, setChangeFewShot] = useAtom(isChangeFewShotAtom);
  const [selectedFewShotIdRepo, setSelectedFewShotIdRepo] = useAtom(selectedFewShotIdRepoAtom);
  const [selectedFewShotNameRepo, setSelectedFewShotNameRepo] = useAtom(selectedFewShotNameRepoAtom);
  const [selectedFewShotDataRepo, setSelectedFewShotDataRepo] = useAtom(selectedFewShotDataRepoAtom);
  const [, setNodes] = useAtom(nodesAtom);
  const [_fewShotNameCache] = useState<Record<string, string>>({});

  const actualSelectedFewShotId = useMemo(() => {
    return selectedFewShotIdRepo[nodeId] || selectedFewShotId;
  }, [selectedFewShotIdRepo, nodeId, selectedFewShotId]);

  const [fewShotInfo, setFewShotInfo] = useState<any>(null);

  const stableFewShotInfo = useMemo(() => {
    if (!actualSelectedFewShotId || actualSelectedFewShotId.trim() === '') {
      return null;
    }
    const savedData = selectedFewShotDataRepo[nodeId];
    const savedName = selectedFewShotNameRepo[nodeId];

    if (savedData && (savedData.uuid === actualSelectedFewShotId || savedData.id === actualSelectedFewShotId)) {
      return savedData;
    }

    const finalFewShotName = savedName || fewShotInfo?.name || `Few-shot (ID: ${actualSelectedFewShotId.substring(0, 8)})`;

    return {
      id: actualSelectedFewShotId,
      uuid: actualSelectedFewShotId,
      name: finalFewShotName,
      description: fewShotInfo?.description || savedData?.description || '선택된 퓨샷',
      messages: savedData?.messages || fewShotInfo?.messages || [],
      variables: savedData?.variables || fewShotInfo?.variables || [],
    };
  }, [actualSelectedFewShotId, selectedFewShotDataRepo, selectedFewShotNameRepo, nodeId, fewShotInfo]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<any>(null);

  const fetchFewShotInfo = useCallback(
    async (fewShotId: string): Promise<void> => {
      if (!fewShotId) {
        setFewShotInfo(null);
        return;
      }

      setLoading(true);
      setError(null);

      try {
        const token = sessionStorage.getItem('access_token');
        let fewShotName = null;
        try {
          const listResponse = await api.get('/fewShot', {
            headers: {
              Authorization: token ? `Bearer ${token}` : undefined,
            },
          });

          const fewShots = listResponse.data?.data?.content || listResponse.data?.data || listResponse.data?.content || listResponse.data;
          if (Array.isArray(fewShots)) {
            const foundFewShot = fewShots.find((shot: any) => shot.uuid === fewShotId || shot.id === fewShotId);
            if (foundFewShot) {
              fewShotName = foundFewShot.name || foundFewShot.title || foundFewShot.fewShotName;
            }
          }
        } catch (listErr) {}

        const response = await api.get(`/fewShot/${fewShotId}`, {
          headers: {
            Authorization: token ? `Bearer ${token}` : undefined,
          },
        });
        const responseData = response.data?.data?.data || response.data?.data || response.data;

        if (responseData) {
          const possibleNames = [
            responseData.name,
            fewShotName,
            responseData.title,
            responseData.fewShotName,
            responseData.displayName,
            responseData.label,
            responseData.fewShotTitle,
            responseData.fewShotLabel,
          ].filter(name => {
            if (!name) return false;
            if (name.includes('(ID:') || name.includes('ID:') || name.includes('Few-shot (ID:')) {
              return false;
            }
            if (/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(name)) {
              return false;
            }
            return true;
          });

          const selectedName = possibleNames[0] || 'Few-shot';

          const fewShotData = {
            ...responseData,
            name: selectedName,
            id: responseData.uuid || fewShotId,
            uuid: responseData.uuid || fewShotId,
            messages: responseData.messages || [],
            variables: responseData.variables || [],
          };
          setFewShotInfo(fewShotData);
          setError(null);

          setSelectedFewShotNameRepo(prev => ({
            ...prev,
            [nodeId]: selectedName,
          }));
          setSelectedFewShotDataRepo(prev => ({
            ...prev,
            [nodeId]: fewShotData,
          }));
        } else {
          setFewShotInfo(null);
          setError(null);
        }
      } catch (err: any) {
        if (err?.response?.status === 500 || err?.response?.status === 502) {
          setError(err);
          setFewShotInfo({
            id: fewShotId,
            uuid: fewShotId,
            name: 'Few-shot',
            description: '상세 정보를 불러올 수 없습니다.',
          });
        } else {
          setError(err);
          setFewShotInfo({
            id: fewShotId,
            uuid: fewShotId,
            name: 'Few-shot',
            description: '상세 정보를 불러올 수 없습니다.',
          });
        }
      } finally {
        setLoading(false);
      }
    },
    [nodeId, setSelectedFewShotNameRepo, setSelectedFewShotDataRepo]
  );
  const prevFewShotIdRef = useRef<string | null>(null);
  const isLoadingRef = useRef<boolean>(false);

  useEffect(() => {
    if (prevFewShotIdRef.current === actualSelectedFewShotId) {
      return;
    }

    if (isLoadingRef.current) {
      return;
    }

    if (!actualSelectedFewShotId || !selectedFewShotIdRepo[nodeId]) {
      setFewShotInfo(null);
      prevFewShotIdRef.current = null;
      return;
    }

    if (fewShotInfo && (fewShotInfo.uuid === actualSelectedFewShotId || fewShotInfo.id === actualSelectedFewShotId)) {
      prevFewShotIdRef.current = actualSelectedFewShotId;
      return;
    }

    if (actualSelectedFewShotId && selectedFewShotIdRepo[nodeId]) {
      const savedData = selectedFewShotDataRepo[nodeId];
      if (savedData && (savedData.uuid === actualSelectedFewShotId || savedData.id === actualSelectedFewShotId)) {
        setFewShotInfo(savedData);
        prevFewShotIdRef.current = actualSelectedFewShotId;
        return;
      }

      const savedName = selectedFewShotNameRepo[nodeId];

      if (savedName && savedName.trim() !== '' && savedName !== 'Few-shot') {
        setFewShotInfo({
          id: actualSelectedFewShotId,
          uuid: actualSelectedFewShotId,
          name: savedName,
          description: '선택된 퓨샷',
          messages: [],
          variables: [],
        });
        prevFewShotIdRef.current = actualSelectedFewShotId;
      } else {
        if (actualSelectedFewShotId && actualSelectedFewShotId.trim() !== '') {
          isLoadingRef.current = true;
          fetchFewShotInfo(actualSelectedFewShotId).finally(() => {
            isLoadingRef.current = false;
            prevFewShotIdRef.current = actualSelectedFewShotId;
          });
        } else {
          setFewShotInfo({
            id: actualSelectedFewShotId,
            uuid: actualSelectedFewShotId,
            name: savedName || 'Few-shot',
            description: '선택된 퓨샷',
            messages: [],
            variables: [],
          });
          prevFewShotIdRef.current = actualSelectedFewShotId;
        }
      }
    } else {
      setFewShotInfo(null);
      prevFewShotIdRef.current = null;
    }
  }, [actualSelectedFewShotId, selectedFewShotIdRepo, selectedFewShotNameRepo, selectedFewShotDataRepo, nodeId, fewShotInfo, fetchFewShotInfo]);

  useEffect(() => {
    if (error && actualSelectedFewShotId) {
    }
  }, [error, actualSelectedFewShotId]);

  const handleClickSearch = () => {
    if (readOnly) return;

    const tempSelectedRef = { current: null as any };

    const handleFewShotSelection = (fewShot: any) => {
      tempSelectedRef.current = fewShot;
    };

    openModal({
      title: '퓨샷 선택',
      type: 'large',
      body: <SelectFewShotPop nodeId={nodeId} readOnly={readOnly} onSelectFewShot={handleFewShotSelection} />,
      showFooter: true,
      confirmText: '확인',
      confirmDisabled: false,
      onConfirm: () => {
        if (tempSelectedRef.current) {
          const fewShotId = tempSelectedRef.current.uuid || tempSelectedRef.current.id;
          const fewShotName = tempSelectedRef.current.name || tempSelectedRef.current.title || 'Few-shot';

          setSelectedFewShotIdRepo(prev => ({
            ...prev,
            [nodeId]: fewShotId,
          }));

          setSelectedFewShotNameRepo(prev => ({
            ...prev,
            [nodeId]: fewShotName,
          }));

          const fewShotData = {
            ...tempSelectedRef.current,
            messages: tempSelectedRef.current.messages || [],
            variables: tempSelectedRef.current.variables || [],
          };
          setFewShotInfo(fewShotData);

          setSelectedFewShotDataRepo(prev => ({
            ...prev,
            [nodeId]: fewShotData,
          }));

          setChangeFewShot(true);
        } else {
        }
      },
      onCancel: () => {},
    });
  };

  const handleRemoveFewShot = () => {
    prevFewShotIdRef.current = null;

    setFewShotInfo(null);
    setError(null);
    setSelectedFewShotIdRepo(prev => ({
      ...prev,
      [nodeId]: null,
    }));

    setNodes(prev =>
      prev.map(node => {
        if (node.id === nodeId) {
          return {
            ...node,
            data: {
              ...node.data,
              fewshot_id: '',
              fewshot_ids: [],
            },
          };
        }
        return node;
      })
    );

    setChangeFewShot(false);
  };

  const content = (
    <>
      {loading && (
        <div className='mb-3 p-3 bg-blue-50 border border-blue-200 rounded-lg'>
          <div className='text-sm text-blue-600'>Few-shot 정보를 불러오는 중...</div>
        </div>
      )}

      <div className='flex w-full items-center gap-2 rounded-lg bg-white p-2 rounded-lg border border-gray-300'>
        <div className='flex-1 items-center'>
          {stableFewShotInfo ? (
            <div className='flex items-center gap-2 flex-1'>
              <button
                className='rounded-lg bg-gray-100 px-3 py-1 text-gray-700 max-w-[200px] truncate hover:bg-gray-200 transition-colors cursor-pointer'
                title={stableFewShotInfo.name}
                onClick={e => {
                  e.stopPropagation();
                  if (actualSelectedFewShotId) {
                    navigate(`/prompt/fewShot/${actualSelectedFewShotId}`);
                  }
                }}
              >
                {stableFewShotInfo.name || 'Few-shot'}
              </button>
            </div>
          ) : (
            <div className='h-[36px] leading-[36px] text-sm text-gray-500'>Few-shot을 선택해주세요</div>
          )}
        </div>
        {stableFewShotInfo && (
          <button onClick={handleRemoveFewShot} className='btn-icon btn btn-sm btn-light text-primary btn-node-action ml-auto' title='삭제'>
            <img alt='ico-system-24-outline-gray-trash' className='w-[20px] h-[20px]' src='/assets/images/system/ico-system-24-outline-gray-trash.svg' />
          </button>
        )}
      </div>
      {!readOnly && (
        <div className='flex justify-end'>
          <button onClick={handleClickSearch} className='bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded-lg border-0 transition-colors duration-200 mt-3'>
            검색
          </button>
        </div>
      )}
    </>
  );

  if (asAccordionItem) {
    const accordionTitle = (
      <>
        {title}
        {stableFewShotInfo && stableFewShotInfo.name && <span className='ml-2 text-gray-500 font-medium'>{stableFewShotInfo.name}</span>}
      </>
    );
    return (
      <CustomAccordionItem title={accordionTitle} defaultOpen={false}>
        {content}
      </CustomAccordionItem>
    );
  }

  return content;
};