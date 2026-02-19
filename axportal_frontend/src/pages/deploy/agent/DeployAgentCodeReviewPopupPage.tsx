import { Button } from '@/components/common/auth';
import { UITypography } from '@/components/UI/atoms';
import { UICode } from '@/components/UI/atoms/UICode';
import { UIGroup } from '@/components/UI/molecules';
import { UIAlarm, UIAlarmGroup } from '@/components/UI/organisms';
import type { LayerPopupProps } from '@/hooks/common/layer';
import { useCopyHandler } from '@/hooks/common/util';

interface DeployAgentCodeReviewPageProps extends LayerPopupProps {
  viewType: 'curl' | 'python';
  endPoint?: string;
}

export function DeployAgentCodeReviewPopupPage({ currentStep, onClose, endPoint = '', viewType = 'curl' }: DeployAgentCodeReviewPageProps) {
  const { handleCopy } = useCopyHandler();
  // endPoint가 없으면 기본값 사용
  const finalEndPoint = endPoint;

  const curlInvokeCommand = `curl -X POST "${finalEndPoint}/invoke" \\
  -H "accept: application/json" \\
  -H "Content-Type: application/json" \\
  -H "Authorization: <api-key>" \\
  -d '{
    "config": {},
    "input": {
      "messages": [
        {
          "content": "hello",
          "type": "human"
        }
      ]
    },
    "kwargs": {}
  }'`;

  const curlStreamCommand = `curl -X POST "${finalEndPoint}/stream" \\
  -H "accept: application/json" \\
  -H "Content-Type: application/json" \\
  -H "Authorization: <api-key>" \\
  -d '{
    "config": {},
    "input": {
      "messages": [
        {
          "content": "hello",
          "type": "human"
        }
      ]
    },
    "kwargs": {}
  }'`;

  const agentInvokeCode = `import ssl
import truststore

ssl_context = truststore.SSLContext(ssl.PROTOCOL_TLS_CLIENT)

## 사내 인증서 ssl_context 설정 ##

from langserve import RemoteRunnable

headers = {
    "Authorization": "<api-key>",
}

agent = RemoteRunnable(
    "${endPoint}",
    headers=headers,
    verify=ssl_context,  ## verify에 생성한 ssl_context 사용 
)
response = agent.invoke(
    {
        "messages": [
            {
                "content": "wiki에서 2024년 한국의 GDP 찾아줘",
                "type": "human"
            }
        ],
        "additional_kwargs": {
            "<input-key>": "<input-value>"
        }
    }
)

print(response)`;

  const agentStreamCode = `import ssl
import truststore

ssl_context = truststore.SSLContext(ssl.PROTOCOL_TLS_CLIENT)

## 사내 인증서 ssl_context 설정 ##

from langserve import RemoteRunnable

from langserve.serialization import Serializer
import orjson
from typing import Any

class StreamResponseSerializer(Serializer):
    def dumps(self, obj: Any) -> bytes:
        return orjson.dumps(obj)

    def loadd(self, obj: Any) -> dict:
        if isinstance(obj, dict):
            return obj
        return orjson.loads(obj)

headers = {
    "Authorization": "<api-key>",
}

agent = RemoteRunnable(
    "${endPoint}",
    headers=headers,
    serializer=StreamResponseSerializer(),
    verify=ssl_context,  ## verify에 생성한 ssl_context 사용 
)

for chunk in agent.stream(
    {
        "messages": [
            {
                "content": "hello",
                "type": "human"
            }
        ],
        "additional_kwargs": {
            "<input-key>": "<input-value>"
        }
    },
):
    if chunk.get("progress"):
		    # 실행 시작한 노드의 description
        print("🍎", chunk)
    elif chunk.get("llm"):
		    # LLM 응답
        print("🍌", chunk)
    elif chunk.get("updates"):
        # 노드별 실행 결과
        print("💨", chunk)
    elif chunk.get("final_result"):
        # 최종 결과
        print("⭐️", chunk)
    elif chunk.get("error"):
        # 에러메세지
        print("💥", chunk)
`;

  // viewType에 따른 동적 설정
  const config =
    viewType === 'curl'
      ? {
        title: 'cURL 확인하기',
        invokeCode: curlInvokeCommand,
        streamCode: curlStreamCommand,
      }
      : {
        title: 'Python 코드 확인하기',
        invokeCode: agentInvokeCode,
        streamCode: agentStreamCode,
      };

  /**
   * 팝업 닫기
   */
  const handleClose = () => {
    onClose();
  };

  // /**
  //  * 취소 버튼 클릭
  //  */
  // const handleCancel = () => {
  //   handleClose();
  // };

  return (
    <>
      <UIAlarm size='large' isVisible={currentStep > 0} onClose={handleClose} title={config.title}>
        <UIAlarmGroup>
          <div className='side-code'>
            <div className='side-code-item'>
              <div className='code-head'>
                <UIGroup gap={8} direction='column'>
                  <div className='code-head-top'>
                    <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                      Invoke
                    </UITypography>
                    <Button className='btn-text-14-underline-point' onClick={() => handleCopy(config.invokeCode)}>
                      복사
                    </Button>
                  </div>
                  <UITypography variant='body-1' className='secondary-neutral-700'>
                    요청에 대한 응답을 한 번에 받아올 때 사용합니다.
                  </UITypography>
                </UIGroup>
              </div>
              <div className='code-edit mt-4'>
                <UICode value={config.invokeCode} language='python' theme='dark' width='100%' minHeight='350px' height='350px' maxHeight='350px' readOnly={false} />
              </div>
            </div>
            <div className='side-code-item'>
              <div className='code-head'>
                <UIGroup gap={8} direction='column'>
                  <div className='code-head-top'>
                    <UITypography variant='title-4' className='secondary-neutral-900 text-sb'>
                      Stream
                    </UITypography>
                    <Button className='btn-text-14-underline-point' onClick={() => handleCopy(config.streamCode)}>
                      복사
                    </Button>
                  </div>
                  <UITypography variant='body-1' className='secondary-neutral-700'>
                    응답을 실시간 스트리밍 형식으로 받아올 때 사용합니다.
                  </UITypography>
                </UIGroup>
              </div>
              <div className='code-edit mt-4'>
                <UICode value={config.streamCode} language='python' theme='dark' width='100%' minHeight='350px' height='350px' maxHeight='350px' readOnly={false} />
              </div>
            </div>
          </div>
        </UIAlarmGroup>
      </UIAlarm>
    </>
  );
}
