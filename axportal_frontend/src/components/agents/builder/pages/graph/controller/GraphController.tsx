import { useAtom } from 'jotai';

import { agentAtom } from '@/components/agents/builder/atoms/AgentAtom';
import KeyTables from '@/components/agents/builder/pages/graph/controller/KeyTables.tsx';
import { useCallback, useState } from 'react';
import { getPhoenixProjectId, exportAgentGraphCode, type PhoenixProjectResponse } from '@/services/agent/builder/agentBuilder.services';
import { useModal } from '@/stores/common/modal';
import { UICode } from '@/components/UI/atoms/UICode';
import { env } from '@/constants/common/env.constants';

interface GraphControllerProps {
  onDeployClick?: () => void;
  onSaveClick?: () => Promise<boolean> | boolean | void;
  onChatClick?: () => void;
  onKeyTableClick?: () => void;
  onLayoutClick?: (direction: 'TB' | 'LR') => void;
  readOnly?: boolean;
  unsavedChanges?: boolean;
}

export const GraphController = ({ onDeployClick, onSaveClick: _onSaveClick, onChatClick, onLayoutClick, readOnly = false, unsavedChanges = false }: GraphControllerProps) => {
  const [agent] = useAtom(agentAtom);
  const { openAlert, openModal } = useModal();
  const [exportCode, setExportCode] = useState<string>('');

  const handleDeployButton = () => {
    if (onDeployClick) onDeployClick();
  };

  const handleExportButton = async () => {
    if (!agent?.id) {
      openAlert({
        title: '안내',
        message: '에이전트 ID가 없습니다.',
        confirmText: '확인',
      });
      return;
    }

    // 🔥 Export 전에 저장 먼저 실행
    if (_onSaveClick) {
      const saveSuccess = await _onSaveClick();
      if (!saveSuccess) {
        // 저장 실패 시 Export 중단
        return;
      }
    }

    try {
      const code = await exportAgentGraphCode(agent.id, 'token');
      if (code) {
        setExportCode(code);

        // 코드 로드 후 모달 열기
        openModal({
          title: 'Python Code 내보내기',
          type: 'large',
          body: (
            <div className='flex h-full overflow-hidden'>
              <UICode value={code} language='python' theme='dark' width='100%' readOnly={true} minHeight='450px' maxHeight='450px' />
            </div>
          ),
          showFooter: true,
          confirmText: '내보내기',
          onConfirm: () => {
            handleDownloadCode(code);
          },
        });
      } else {
        openAlert({
          title: '안내',
          message: '코드를 가져오는데 실패했습니다.',
          confirmText: '확인',
        });
      }
    } catch (error: any) {
      openAlert({
        title: '안내',
        message: `코드를 가져오는데 실패했습니다: ${error?.message || '알 수 없는 오류'}`,
        confirmText: '확인',
      });
    }
  };

  const handleDownloadCode = (code?: string) => {
    const codeToDownload = code || exportCode;
    if (!codeToDownload || codeToDownload.trim() === '') {
      openAlert({
        title: '안내',
        message: '다운로드할 코드가 없습니다.',
        confirmText: '확인',
      });
      return;
    }

    // 파일명 생성 (에이전트 이름 또는 ID 사용)
    const agentName = agent?.name || 'agent';
    const sanitizedName = agentName.replace(/[^a-zA-Z0-9가-힣_-]/g, '_');
    const timestamp = new Date().toISOString().replace(/[:.]/g, '-').slice(0, -5);
    const filename = `${sanitizedName}_${timestamp}.py`;

    // Blob 생성
    const blob = new Blob([codeToDownload], { type: 'text/x-python' });
    const url = URL.createObjectURL(blob);

    // 다운로드 링크 생성 및 클릭
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();

    // 정리
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
  };

  const handleSaveClick = async () => {
    // 🔥 Graph.tsx의 handleSave를 호출하여 동일한 검증 로직 사용
    // 채팅 테스트와 저장 버튼이 동일한 검증을 거치도록 통일
    // 🔥 저장 성공 시 Toast는 handleSave 내부에서 표시하므로 여기서는 표시하지 않음 (중복 방지)
    if (_onSaveClick) {
      await _onSaveClick();
    }
  };

  const handleChatClick = () => {
    if (onChatClick) onChatClick();
  };

  const fetchPhoenixProjectId = useCallback(async (): Promise<PhoenixProjectResponse | null> => {
    if (!agent?.id) {
      return null;
    }

    return await getPhoenixProjectId(agent.id);
  }, [agent?.id]);

  return (
    <div
      className={'flex flex-row justify-end gap-2 py-4 mr-4'}
      style={{ pointerEvents: 'auto', zIndex: 1001 }}
      onMouseDown={e => e.stopPropagation()}
      onClick={e => e.stopPropagation()}
      onMouseUp={e => e.stopPropagation()}
      onTouchStart={e => e.stopPropagation()}
      onTouchEnd={e => e.stopPropagation()}
    >
      <KeyTables readOnly={readOnly} />
      <div className='flex items-center gap-2' style={{ opacity: readOnly ? 0.6 : 1 }}>
        {/* Deploy Button - 개선된 스타일 적용 */}

        {/* Auto Layout Button */}
{onLayoutClick && (
          <button
            type='button'
            className='h-8 px-2 p-0 text-sm font-normal bg-white border border-[#DCE2ED] ag-btn-hover cursor-pointer flex items-center justify-center rounded-[6px] text-[#242A34]'
         style={{
              pointerEvents: readOnly ? 'none' : 'auto',
              zIndex: 1002,
              cursor: readOnly ? 'not-allowed' : 'pointer',
            }}
            disabled={readOnly}
            onMouseDown={e => e.stopPropagation()}
            onClick={e => {
              if (readOnly) return;
              e.stopPropagation();
              onLayoutClick('LR'); // 가로 레이아웃
            }}
            onMouseUp={e => e.stopPropagation()}
            title='Auto Layout'
          >
            노드 정렬
          </button>
        )}
        
        <button
          type='button'
          className='h-8 p-0 px-2 text-sm font-normal bg-white border border-[#DCE2ED] ag-btn-hover cursor-pointer flex items-center justify-center rounded-[6px] text-[#242A34]'
          style={{
            pointerEvents: readOnly ? 'none' : 'auto',
            zIndex: 1002,
            cursor: readOnly ? 'not-allowed' : 'pointer',
          }}
          disabled={readOnly}
          onMouseDown={e => e.stopPropagation()}
          onClick={e => {
            if (readOnly) return;
            e.stopPropagation();
            handleExportButton();
          }}
          onMouseUp={e => e.stopPropagation()}
        >
          Export
        </button>



        <button
          type='button'
          className='h-8 p-0 w-[41px] text-sm font-normal bg-white border border-[#DCE2ED] ag-btn-hover cursor-pointer flex items-center justify-center rounded-[6px] text-[#242A34]'
          style={{
            pointerEvents: readOnly ? 'none' : 'auto',
            zIndex: 1002,
            cursor: readOnly ? 'not-allowed' : 'pointer',
          }}
          disabled={readOnly}
          onMouseDown={e => e.stopPropagation()}
          onClick={e => {
            if (readOnly) return;
            e.stopPropagation();
            handleDeployButton();
          }}
          onMouseUp={e => e.stopPropagation()}
        >
          배포
        </button>

        {/* Save Button - 개선된 스타일 적용 */}
        <button
          type='button'
          className={`h-8 p-0 w-[41px] text-sm font-normal border cursor-pointer flex items-center justify-center rounded-[6px] text-[#242A34] ${'bg-white border-[#DCE2ED] ag-btn-hover'}`}
          style={{
            pointerEvents: readOnly ? 'none' : 'auto',
            zIndex: 1002,
            cursor: readOnly ? 'not-allowed' : 'pointer',
          }}
          disabled={readOnly}
          onMouseDown={e => e.stopPropagation()}
          onClick={e => {
            if (readOnly) return;
            e.stopPropagation();
            handleSaveClick();
          }}
          onMouseUp={e => e.stopPropagation()}
          title={unsavedChanges ? '저장되지 않은 변경사항이 있습니다' : '모든 변경사항이 저장되었습니다'}
        >
          저장
          {/* {unsavedChanges ? '저장*' : '저장'} */}
        </button>

        {/* Chat Test Button - 개선된 스타일 적용 */}
        <button
          type='button'
          className='h-8 px-2 min-w-[80px] text-sm font-normal bg-white border border-[#DCE2ED] ag-btn-hover cursor-pointer rounded-[6px] text-[#242A34]'
          style={{
            pointerEvents: readOnly ? 'none' : 'auto',
            zIndex: 1002,
            cursor: readOnly ? 'not-allowed' : 'pointer',
          }}
          disabled={readOnly}
          onMouseDown={e => e.stopPropagation()}
          onClick={e => {
            if (readOnly) return;
            e.stopPropagation();
            handleChatClick();
          }}
          onMouseUp={e => e.stopPropagation()}
        >
          채팅 테스트
        </button>

        

        <button
          type='button'
          className='h-8 px-3 min-w-[76px] text-sm font-semibold text-white bg-blue-600 border border-blue-600 rounded-md hover:bg-blue-700 cursor-pointer flex items-center justify-center'
          style={{
            pointerEvents: readOnly ? 'none' : 'auto',
            zIndex: 1002,
            cursor: readOnly ? 'not-allowed' : 'pointer',
          }}
          disabled={readOnly}
          onMouseDown={e => e.stopPropagation()}
          onClick={async e => {
            if (readOnly) return;
            e.stopPropagation();

            // 🔥 Phoenix 버튼은 저장하지 않고 바로 Phoenix 프로젝트 정보 조회
            const phoenixProjectInfo = await fetchPhoenixProjectId();

            // Phoenix 인증 활성화 여부는 백엔드에서 전달받음 (향후 확장 가능)
            // const enableAuth = phoenixProjectInfo?.enableAuth ?? false;

            // Phoenix 프로젝트 ID (API 응답 우선, 없으면 에이전트 데이터에서)
            const phoenixProjectId = phoenixProjectInfo?.projectId || (agent as any)?.phoenixProjectId;

            // 백엔드에서 제공한 Phoenix URL 사용 (있으면 우선 사용)
            let phoenixUrl: string = phoenixProjectInfo?.phoenixUrl || '';

            // 백엔드에서 URL을 제공하지 않은 경우에만 직접 구성
            if (!phoenixUrl) {
              const phoenixBaseUrl = env.VITE_PHOENIX_BASE_URL;
              const baseUrlWithoutProjects = phoenixBaseUrl.replace(/\/projects\/?$/, '').replace(/\/$/, '');

              if (phoenixProjectId && typeof phoenixProjectId === 'string') {
                // Trace 데이터가 있는 경우: projects/{base64값}/spans 형태로 구성
                // base64 인코딩된 값을 그대로 사용 (예: UHJvamVjdDozOQ==)
                phoenixUrl = `${baseUrlWithoutProjects}/projects/${phoenixProjectId}/spans`;
              } else {
                // Trace 데이터가 없는 경우: projects 페이지로 이동
                phoenixUrl = `${baseUrlWithoutProjects}/projects`;
              }
            }

            // 새 창에서 Phoenix 열기 (항상 새 창에서 열림)
            const newWindow = window.open(phoenixUrl, '_blank', 'noopener,noreferrer');

            if (newWindow) {
              // 새 창이 성공적으로 열린 경우 포커스
              newWindow.focus();
            } else {
              // 팝업이 차단된 경우 사용자에게 알림
            }
          }}
          onMouseUp={e => e.stopPropagation()}
        >
          Phoenix
        </button>
      </div>

    </div>
  );
};

export default GraphController;
