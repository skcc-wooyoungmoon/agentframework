import { useModal } from '@/stores/common/modal';
import { SelectAgentAppPop } from '@/components/agents/builder/pages/modal/SelectAgentAppPop';
import { isChangeAgentAppAtom, selectedAgentAppIdRepoAtom, selectedAgentAppNameRepoAtom } from '@/components/agents/builder/atoms/AgentAtom.ts';
import { CustomAccordionItem } from '@/components/agents/builder/common/Button/CustomAccordionItem';
import { useGetAgentAppList } from '@/services/deploy/agent/agentDeploy.services';
import { useAtom } from 'jotai';
import { useNavigate } from 'react-router-dom';

interface AgentAppProps {
  selectedAgentAppId: string | null;
  nodeId: string;
  nodeType?: string;
  asAccordionItem?: boolean;
  title?: React.ReactNode;
  readOnly?: boolean;
}

export const SelectAgentApp = ({ selectedAgentAppId, nodeId, nodeType, asAccordionItem = false, title, readOnly = false }: AgentAppProps) => {
  const { openModal } = useModal();
  const navigate = useNavigate();
  const [selectedAgentAppIdRepo, setSelectedAgentAppIdRepo] = useAtom(selectedAgentAppIdRepoAtom);
  const [selectedAgentAppNameRepo] = useAtom(selectedAgentAppNameRepoAtom);
  const [, setChangeAgentApp] = useAtom(isChangeAgentAppAtom);

  const actualSelectedAgentAppId = selectedAgentAppIdRepo[nodeId] !== undefined ? selectedAgentAppIdRepo[nodeId] : selectedAgentAppId;
  const actualSelectedAgentAppName = selectedAgentAppNameRepo[nodeId];

  const { data: agentAppListData } = useGetAgentAppList(
    {
      page: 0,
      size: 12,
      sort: 'created_at,desc',
      filter: 'deployment_status:Available',
      targetType: 'agent',
      search: '',
    },
    {
      enabled: false,
    }
  );

  const handleClickSearch = () => {
    if (readOnly) return;

    openModal({
      title: '에이전트 APP',
      type: 'large',
      body: <SelectAgentAppPop modalId={`select-agentapp-pop_${nodeId}`} nodeId={nodeId} nodeType={nodeType} readOnly={readOnly} />,
      showFooter: true,
      confirmText: '확인',
      confirmDisabled: false,
      onConfirm: () => {
        if ((window as any).agentAppApplyHandler) {
          (window as any).agentAppApplyHandler();
        }
      },
    });
  };

  const handleRemoveAgentApp = () => {
    setChangeAgentApp(true);
    setSelectedAgentAppIdRepo(prev => ({
      ...prev,
      [nodeId]: null,
    }));
  };

  const openAgentApp = (e?: React.MouseEvent) => {
    if (e) {
      e.stopPropagation();
    }
    if (actualSelectedAgentAppId) {
      navigate(`/deploy/agentDeploy/${actualSelectedAgentAppId}`);
    }
  };

  const hasSelectedAgentApp = actualSelectedAgentAppId && actualSelectedAgentAppName;
  const placeholderText =
    agentAppListData?.content && agentAppListData.content.length > 0 ? `사용 가능한 Agent App: ${agentAppListData.content.length}개` : '호출할 Agent를 선택하세요.';

  const content = (
    <div className='flex w-full flex-col gap-3'>
      <div className='flex w-full items-center gap-2 rounded-lg bg-white p-2 rounded-lg border border-gray-300' onClick={hasSelectedAgentApp ? openAgentApp : undefined}>
        <div className={hasSelectedAgentApp ? 'rounded-lg bg-gray-100 px-3 py-1 text-gray-700 max-w-[200px] truncate hover:bg-gray-200 transition-colors cursor-pointer' : ''}>
          <span className={hasSelectedAgentApp ? 'text-sm text-gray-800' : 'h-[36px] leading-[36px] text-sm text-gray-500'} title={actualSelectedAgentAppName || placeholderText}>
            {hasSelectedAgentApp ? actualSelectedAgentAppName : placeholderText}
          </span>
        </div>
        {hasSelectedAgentApp ? (
          <button
            type='button'
            onClick={e => {
              e.stopPropagation();
              handleRemoveAgentApp();
            }}
            className='btn-icon btn btn-sm btn-light text-primary btn-node-action ml-auto'
          >
            <img alt='ico-system-24-outline-gray-trash' className='w-[20px] h-[20px]' src='/assets/images/system/ico-system-24-outline-gray-trash.svg' />
          </button>
        ) : null}
      </div>
      {!readOnly && (
        <div className='flex justify-end'>
          <button
            onClick={handleClickSearch}
            className='h-[44px] min-w-[84px] rounded-lg bg-blue-500 px-4 text-sm font-semibold text-white transition-colors duration-200 hover:bg-blue-600'
          >
            검색
          </button>
        </div>
      )}
    </div>
  );

  if (asAccordionItem) {
    const accordionTitle = (
      <>
        {title}
        {hasSelectedAgentApp && <span className='ml-2 text-gray-500'>{actualSelectedAgentAppName}</span>}
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
