import { UICode } from '@/components/UI/atoms/UICode';

interface AgentMcpCtlgToolDetailPopupPageProps {
  tool: string;
}

export function AgentMcpCtlgToolDetailPopupPage({ tool }: AgentMcpCtlgToolDetailPopupPageProps) {
  return (
    <div className='flex h-full'>
      <UICode value={tool} language='json' theme='dark' width='100%' minHeight='472px' maxHeight='472px' readOnly={true} />
    </div>
  );
}
