import { agentAtom, isChangePromptAtom, nodesAtom, selectedPromptIdRepoAtom, selectedPromptNameRepoAtom } from '@/components/agents/builder/atoms/AgentAtom';
import { CustomAccordionItem } from '@/components/agents/builder/common/Button/CustomAccordionItem';
import { useModal } from '@/stores/common/modal';
import { SelectPromptPop } from '@/components/agents/builder/pages/modal/SelectPromptPop.tsx';
import { api } from '@/configs/axios.config';
import { useAtom, useSetAtom } from 'jotai';
import React, { useEffect, useMemo, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';

interface PromptProps {
  selectedPromptId: string | null;
  nodeId: string;
  nodeType?: string;
  asAccordionItem?: boolean;
  title?: React.ReactNode;
  readOnly?: boolean;
}

export const SelectPrompt = ({ selectedPromptId, nodeId, nodeType, asAccordionItem = false, title, readOnly = false }: PromptProps) => {
  const { openModal } = useModal();

  const navigate = useNavigate();
  const [selectedPromptIdRepo, setSelectedPromptIdRepo] = useAtom(selectedPromptIdRepoAtom);
  const [selectedPromptNameRepo, setSelectedPromptNameRepo] = useAtom(selectedPromptNameRepoAtom);
  const [isChangePrompt, setChangePrompt] = useAtom(isChangePromptAtom);
  const [promptNameCache, setPromptNameCache] = useState<Record<string, string>>({});
  const [_agent] = useAtom(agentAtom);
  const setNodes = useSetAtom(nodesAtom);
  const actualSelectedPromptId = useMemo(() => {
    return selectedPromptIdRepo[nodeId] || selectedPromptId;
  }, [selectedPromptIdRepo, nodeId, selectedPromptId]);

  interface PromptInfo {
    id?: string;
    uuid?: string;
    name?: string;
    description?: string;
    ptype?: number;
    [key: string]: unknown;
  }
  const [promptInfo, setPromptInfo] = useState<PromptInfo | null>(null);
  const [, setLoading] = useState(false);
  const [error, setError] = useState<unknown>(null);

  const stablePromptInfo = useMemo(() => {
    if (!actualSelectedPromptId || actualSelectedPromptId.trim() === '') {
      return null;
    }

    const atomPromptName = selectedPromptNameRepo[nodeId];
    const cachedPromptName = promptNameCache[nodeId];
    const finalPromptName = atomPromptName || cachedPromptName || promptInfo?.name || `Prompt (ID: ${actualSelectedPromptId.substring(0, 8)})`;

    return {
      id: actualSelectedPromptId,
      uuid: actualSelectedPromptId,
      name: finalPromptName,
      description: promptInfo?.description || '기존 선택된 프롬프트',
      ptype: promptInfo?.ptype,
    };
  }, [actualSelectedPromptId, selectedPromptNameRepo, promptNameCache, nodeId, promptInfo]);

  const fetchPromptInfo = async (promptId: string) => {
    if (!promptId) {
      setPromptInfo(null);
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const token = sessionStorage.getItem('access_token');

      const response = await api.get(`/inference-prompts/${promptId}`, {
        headers: {
          Authorization: token ? `Bearer ${token}` : undefined,
        },
      });

      const responseData = response.data?.data?.data || response.data?.data || response.data;
      if (responseData) {
        setPromptInfo(responseData);
        if (responseData.name) {
          setSelectedPromptNameRepo(prev => ({
            ...prev,
            [nodeId]: responseData.name,
          }));
          setPromptNameCache(prev => ({
            ...prev,
            [nodeId]: responseData.name,
          }));
        }
      } else {
        setPromptInfo(null);
      }
    } catch (err: any) {
      if (err?.response?.status === 500 || err?.response?.status === 502 || err?.response?.status === 404) {
        setError(err);
        const atomPromptName = selectedPromptNameRepo[nodeId];
        const cachedPromptName = promptNameCache[nodeId];
        const defaultName = atomPromptName || cachedPromptName || `Prompt (ID: ${promptId.substring(0, 8)})`;

        const defaultPromptInfo: PromptInfo = {
          id: promptId,
          uuid: promptId,
          name: defaultName,
          description: '상세 정보를 불러올 수 없습니다.',
        };
        setPromptInfo(defaultPromptInfo);
        if (!atomPromptName) {
          setSelectedPromptNameRepo(prev => ({
            ...prev,
            [nodeId]: defaultName,
          }));
        }
        if (!cachedPromptName) {
          setPromptNameCache(prev => ({
            ...prev,
            [nodeId]: defaultName,
          }));
        }
      } else {
        setError(err);
        const atomPromptName = selectedPromptNameRepo[nodeId];
        const cachedPromptName = promptNameCache[nodeId];
        const defaultName = atomPromptName || cachedPromptName || `Prompt (ID: ${promptId.substring(0, 8)})`;

        const defaultPromptInfo: PromptInfo = {
          id: promptId,
          uuid: promptId,
          name: defaultName,
          description: '상세 정보를 불러올 수 없습니다.',
        };
        setPromptInfo(defaultPromptInfo);

        if (!atomPromptName) {
          setSelectedPromptNameRepo(prev => ({
            ...prev,
            [nodeId]: defaultName,
          }));
        }
        if (!cachedPromptName) {
          setPromptNameCache(prev => ({
            ...prev,
            [nodeId]: defaultName,
          }));
        }
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (isChangePrompt && actualSelectedPromptId) {
      fetchPromptInfo(actualSelectedPromptId);
      setChangePrompt(false);
    }
  }, [isChangePrompt, actualSelectedPromptId, nodeId]);

  const prevPromptIdRef = useRef<string | null>(null);

  useEffect(() => {
    if (prevPromptIdRef.current !== actualSelectedPromptId) {
      prevPromptIdRef.current = actualSelectedPromptId;

      if (!actualSelectedPromptId) {
        setPromptInfo(null);
        return;
      }
      if (actualSelectedPromptId) {
        const atomPromptName = selectedPromptNameRepo[nodeId];
        const cachedPromptName = promptNameCache[nodeId];

        if (atomPromptName || cachedPromptName) {
          const savedPromptName = atomPromptName || cachedPromptName;
          const promptInfo = {
            id: actualSelectedPromptId,
            uuid: actualSelectedPromptId,
            name: savedPromptName,
            description: '기존 선택된 프롬프트',
          };
          setPromptInfo(promptInfo);
        } else {
          fetchPromptInfo(actualSelectedPromptId);
        }
      }
    }
  }, [actualSelectedPromptId, selectedPromptIdRepo, nodeId]);

  useEffect(() => {
    if (error && actualSelectedPromptId) {
    }
  }, [error, actualSelectedPromptId]);

  const handleClickSearch = () => {
    if (readOnly) return;

    openModal({
      title: '프롬프트 선택',
      type: 'large',
      body: <SelectPromptPop nodeId={nodeId} nodeType={nodeType} />,
      showFooter: true,
      confirmText: '확인',
      confirmDisabled: false,
      onConfirm: () => {
        if ((window as any).promptApplyHandler) {
          (window as any).promptApplyHandler();
        }
        setChangePrompt(true);
      },
    });
  };

  const handleRemovePrompt = (e?: React.MouseEvent) => {
    if (e) {
      e.preventDefault();
      e.stopPropagation();
    }

    prevPromptIdRef.current = null;
    setPromptInfo(null);
    setError(null);

    setSelectedPromptIdRepo(prev => ({
      ...prev,
      [nodeId]: null,
    }));
    setSelectedPromptNameRepo(prev => {
      const updated = { ...prev };
      delete updated[nodeId];
      return updated;
    });

    setPromptNameCache(prev => {
      const updated = { ...prev };
      delete updated[nodeId];
      return updated;
    });

    setNodes(prev =>
      prev.map(node => {
        if (node.id === nodeId) {
          return {
            ...node,
            data: {
              ...node.data,
              prompt_id: '',
              promptId: '',
              prompt_uuid: '',
              promptUuid: '',
            },
          };
        }
        return node;
      })
    );

    setChangePrompt(false);
  };

  const openPrompt = (e?: React.MouseEvent) => {
    if (e) {
      e.stopPropagation();
    }
    if (actualSelectedPromptId) {
      navigate(`/prompt/inferPrompt/${actualSelectedPromptId}`);
    }
  };

  const content = (
    <>
      <div className='flex w-full items-center gap-2 rounded-lg bg-white p-2 rounded-lg border border-gray-300'>
        <div className='flex-1 items-center'>
          {stablePromptInfo ? (
            <div className='flex items-center gap-2 flex-1'>
              <button
                className='rounded-lg bg-gray-100 px-3 py-1 text-gray-700 max-w-[200px] truncate hover:bg-gray-200 transition-colors cursor-pointer'
                title={stablePromptInfo.name}
                onClick={openPrompt}
              >
                {stablePromptInfo.name}
              </button>
            </div>
          ) : (
            <div className='h-[36px] leading-[36px] text-sm text-gray-500'>Prompt을 선택해주세요</div>
          )}
        </div>
        {stablePromptInfo && (
          <button onClick={e => handleRemovePrompt(e)} className='btn-icon btn btn-sm btn-light text-primary btn-node-action ml-auto' title='삭제'>
            <img alt='ico-system-24-outline-gray-trash' className='w-[20px] h-[20px]' src='/assets/images/system/ico-system-24-outline-gray-trash.svg' />
          </button>
        )}
      </div>
      {!readOnly && (
        <div className='flex justify-end'>
          <button onClick={handleClickSearch} className='bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 px-4 rounded-lg border-0 transition-colors duration-200  mt-3'>
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
        {stablePromptInfo && stablePromptInfo.name && <span className='ml-2 text-gray-500 font-medium'>{stablePromptInfo.name}</span>}
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