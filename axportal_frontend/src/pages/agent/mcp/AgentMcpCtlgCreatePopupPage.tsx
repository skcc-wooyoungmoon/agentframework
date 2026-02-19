import { useState } from 'react';

import { Button } from '@/components/common/auth';
import { UIRadio2, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIFormField, UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UITextArea2, UIUnitGroup } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown/component';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { useCreateAgentMcpCtlg, useTestConnectionAgentMcp } from '@/services/agent/mcp/agentMcp.services';
import type { McpAuthType, McpTransportType } from '@/services/agent/mcp/types';
import { useModal } from '@/stores/common/modal/useModal';
import { useNavigate } from 'react-router-dom';

interface AgentMcpCtlgCreatePopupPageProps {
  isOpen: boolean;
  onClose: () => void;
  onCreateSuccess?: () => void;
}

export const AgentMcpCtlgCreatePopupPage = ({ isOpen, onClose, onCreateSuccess: _ }: AgentMcpCtlgCreatePopupPageProps) => {
  const navigate = useNavigate();
  const { openAlert, openConfirm } = useModal();

  const [nameValue, setNameValue] = useState('');
  const [descriptionValue, setDescriptionValue] = useState('');
  const [selectedToolType, setSelectedToolType] = useState<McpTransportType>('streamable-http');
  const [apiUrlValue, setApiUrlValue] = useState('');
  const [selectedAuthType, setSelectedAuthType] = useState<McpAuthType>('none');
  const [tags, setTags] = useState<string[]>([]);
  const [basicUsername, setBasicUsername] = useState('');
  const [basicPassword, setBasicPassword] = useState('');
  const [bearerToken, setBearerToken] = useState('');
  const [headerKey, setHeaderKey] = useState('');
  const [headerValue, setHeaderValue] = useState('');

  const [errorApiUrl, setErrorApiUrl] = useState<boolean>(false);
  const [errorBasicUsername, setErrorBasicUsername] = useState<boolean>(false);
  const [errorBasicPassword, setErrorBasicPassword] = useState<boolean>(false);
  const [errorBearerToken, setErrorBearerToken] = useState<boolean>(false);
  const [errorHeaderKey, setErrorHeaderKey] = useState<boolean>(false);
  const [errorHeaderValue, setErrorHeaderValue] = useState<boolean>(false);

  const [hasAttemptedSave, setHasAttemptedSave] = useState<boolean>(false);
  const [hasAttemptedTest, setHasAttemptedTest] = useState<boolean>(false);
  const [isConnectionTested, setIsConnectionTested] = useState<boolean>(false);

  const { mutate: createAgentMcpCtlg, isPending } = useCreateAgentMcpCtlg({
    onSuccess: ({ data: { id } }) => {
      openAlert({
        title: '완료',
        message: 'MCP 서버 등록을 완료하였습니다.',
        confirmText: '확인',
        onConfirm: () => {
          handleClose();
          navigate(`/agent/mcp/${id}`);
          // onCreateSuccess?.();
        },
      });
    },
  });

  const handleToolTypeChange = (value: string) => {
    setSelectedToolType(value as McpTransportType);
    setIsConnectionTested(false);
  };

  const handleAuthTypeChange = (value: string) => {
    setSelectedAuthType(value as McpAuthType);
    setIsConnectionTested(false);
  };

  const { mutate: testConnection, isPending: isTestPending } = useTestConnectionAgentMcp({
    onSuccess: (data: any) => {
      if (data?.data?.isConnected === true) {
        setIsConnectionTested(true);
        openAlert({
          title: '안내',
          message: 'MCP 서버 연결에 성공하였습니다.',
        });
      } else {
        setIsConnectionTested(false);
        openAlert({
          title: '안내',
          message: 'MCP 서버 연결에 실패하였습니다.\n 입력값을 다시 확인 해 주세요.',
        });
      }
    },
    onError: () => {
      setIsConnectionTested(false);
    },
    onSettled: () => {
      setHasAttemptedTest(false);
    },
  });

  const isFormValid = () => {
    if (!nameValue.trim() || nameValue.length > 50) {
      return false;
    }
    if (descriptionValue.length > 100) {
      return false;
    }

    if (selectedToolType === 'streamable-http' && !apiUrlValue.trim()) {
      return false;
    }

    if (selectedAuthType === 'basic') {
      if (!basicUsername.trim() || !basicPassword.trim()) {
        return false;
      }
    } else if (selectedAuthType === 'bearer') {
      if (!bearerToken.trim()) {
        return false;
      }
    } else if (selectedAuthType === 'custom-header') {
      if (!headerKey.trim() || !headerValue.trim()) {
        return false;
      }
    }

    return true;
  };

  const handleTestConnection = () => {
    setHasAttemptedTest(true);
    setErrorApiUrl(false);
    setErrorBasicUsername(false);
    setErrorBasicPassword(false);
    setErrorBearerToken(false);
    setErrorHeaderKey(false);
    setErrorHeaderValue(false);

    let hasError = false;

    if (!apiUrlValue.trim()) {
      setErrorApiUrl(true);
      hasError = true;
    }

    let authConfig: any = null;
    if (selectedAuthType === 'basic') {
      if (!basicUsername.trim()) {
        setErrorBasicUsername(true);
        hasError = true;
      }
      if (!basicPassword.trim()) {
        setErrorBasicPassword(true);
        hasError = true;
      }
      if (!hasError) {
        authConfig = { username: basicUsername.trim(), password: basicPassword.trim() };
      }
    } else if (selectedAuthType === 'bearer') {
      if (!bearerToken.trim()) {
        setErrorBearerToken(true);
        hasError = true;
      }
      if (!hasError) {
        authConfig = { token: bearerToken.trim() };
      }
    } else if (selectedAuthType === 'custom-header') {
      if (!headerKey.trim()) {
        setErrorHeaderKey(true);
        hasError = true;
      }
      if (!headerValue.trim()) {
        setErrorHeaderValue(true);
        hasError = true;
      }
      if (!hasError) {
        authConfig = { key: headerKey.trim(), value: headerValue.trim() };
      }
    } else {
      authConfig = null;
    }

    if (hasError) {
      return;
    }

    const testRequest: any = {
      serverUrl: apiUrlValue.trim(),
      transportType: selectedToolType,
      authType: selectedAuthType,
    };

    if (authConfig !== null) {
      testRequest.authConfig = authConfig;
    }

    testConnection(testRequest);
  };

  const handleSave = () => {
    setHasAttemptedSave(true);

    setErrorApiUrl(false);
    setErrorBasicUsername(false);
    setErrorBasicPassword(false);
    setErrorBearerToken(false);
    setErrorHeaderKey(false);
    setErrorHeaderValue(false);

    let hasError = false;

    if (!nameValue.trim()) {
      hasError = true;
    } else if (nameValue.length > 50) {
      hasError = true;
    }

    if (descriptionValue.length > 100) {
      hasError = true;
    }

    if (selectedToolType === 'streamable-http' && !apiUrlValue.trim()) {
      setErrorApiUrl(true);
      hasError = true;
    }

    let authConfig: any = null;
    if (selectedAuthType === 'basic') {
      if (!basicUsername.trim()) {
        setErrorBasicUsername(true);
        hasError = true;
      }
      if (!basicPassword.trim()) {
        setErrorBasicPassword(true);
        hasError = true;
      }
      if (!hasError) {
        authConfig = { username: basicUsername.trim(), password: basicPassword.trim() };
      }
    } else if (selectedAuthType === 'bearer') {
      if (!bearerToken.trim()) {
        setErrorBearerToken(true);
        hasError = true;
      }
      if (!hasError) {
        authConfig = { token: bearerToken.trim() };
      }
    } else if (selectedAuthType === 'custom-header') {
      if (!headerKey.trim()) {
        setErrorHeaderKey(true);
        hasError = true;
      }
      if (!headerValue.trim()) {
        setErrorHeaderValue(true);
        hasError = true;
      }
      if (!hasError) {
        authConfig = { key: headerKey.trim(), value: headerValue.trim() };
      }
    } else {
      authConfig = null;
    }

    if (hasError) {
      return;
    }

    const requestData: any = {
      name: nameValue.trim(),
      description: descriptionValue.trim(),
      type: 'serverless',
      serverUrl: apiUrlValue.trim(),
      authType: selectedAuthType,
      tags: tags,
      transportType: selectedToolType,
    };

    if (authConfig !== null) {
      requestData.authConfig = authConfig;
    }

    createAgentMcpCtlg(requestData);
  };

  const resetAllStates = () => {
    setNameValue('');
    setDescriptionValue('');
    setSelectedToolType('streamable-http');
    setApiUrlValue('');
    setSelectedAuthType('none');
    setBasicUsername('');
    setBasicPassword('');
    setBearerToken('');
    setHeaderKey('');
    setHeaderValue('');
    setTags([]);

    setErrorApiUrl(false);
    setErrorBasicUsername(false);
    setErrorBasicPassword(false);
    setErrorBearerToken(false);
    setErrorHeaderKey(false);
    setErrorHeaderValue(false);

    setHasAttemptedSave(false);
    setHasAttemptedTest(false);
    setIsConnectionTested(false);
  };

  const handleClose = () => {
    resetAllStates();
    onClose();
  };

  const handleCancel = () => {
    openConfirm({
      title: '안내',
      message: '화면을 나가시겠어요?\n입력한 정보가 저장되지 않을 수 있습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        resetAllStates();
        onClose();
      },
      onCancel: () => {},
    });
  };

  return (
    <>
      <UILayerPopup
        isOpen={isOpen}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          <UIPopupAside>
            <UIPopupHeader title='MCP 서버 등록' description='' position='left' />
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <Button className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                    취소
                  </Button>
                  <Button
                    auth={AUTH_KEY.AGENT.MCP_SERVER_CREATE}
                    className='btn-tertiary-blue !w-[80px]'
                    onClick={handleSave}
                    disabled={isPending || !isConnectionTested || !isFormValid()}
                  >
                    등록
                  </Button>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        <section className='section-popup-content'>
          <UIPopupHeader title='MCP 서버 등록' description='포탈에 등록할 MCP서버 연동 정보를 입력 해 주세요.' position='right' />
          <UIPopupBody>
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                  서버 이름
                </UITypography>
                <UIInput.Text
                  value={nameValue}
                  placeholder='영문, 숫자, 띄어쓰기만 입력 가능합니다.'
                  onChange={e => {
                    const value = e.target.value;
                    // 영문, 숫자, 띄어쓰기만 허용
                    const filteredValue = value.replace(/[^a-zA-Z0-9 ]/g, '');
                    setNameValue(filteredValue);
                  }}
                  maxLength={50}
                />
              </UIFormField>
            </UIArticle>

            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb'>
                  설명
                </UITypography>
                <UITextArea2 value={descriptionValue} placeholder='설명 입력' onChange={e => setDescriptionValue(e.target.value)} maxLength={100} required={true} />
              </UIFormField>
            </UIArticle>
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                  전송 유형
                </UITypography>
                <UIUnitGroup gap={12} direction='column' align='center'>
                  <UIRadio2
                    name='toolType'
                    label='Streamable HTTP'
                    value='streamable-http'
                    checked={selectedToolType === 'streamable-http'}
                    onChange={(checked, value) => {
                      if (checked) handleToolTypeChange(value);
                    }}
                  />
                  {/* <UIRadio2
                    name='toolType'
                    label='SSE'
                    value='sse'
                    checked={selectedToolType === 'sse'}
                    onChange={(checked, value) => {
                      if (checked) handleToolTypeChange(value);
                    }}
                  /> */}
                </UIUnitGroup>
              </UIFormField>
            </UIArticle>
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                  서버 URL
                </UITypography>
                <UIInput.Text
                  value={apiUrlValue}
                  placeholder='서버 URL 입력'
                  onChange={e => {
                    setApiUrlValue(e.target.value);
                    setIsConnectionTested(false);
                  }}
                  error={(hasAttemptedSave || hasAttemptedTest) && errorApiUrl ? '서버 URL을 입력해 주세요.' : ''}
                />
              </UIFormField>
            </UIArticle>
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb'>
                  인증 유형
                </UITypography>
                <UITypography variant='body-2' className='secondary-neutral-600'>
                  각 유형에 맞는 설정값을 입력한 뒤, 인증 유형 우축의 [연결] 버튼으로 연결에 성공해야 [등록] 버튼이 활성화됩니다.
                </UITypography>
                <UIUnitGroup gap={8} direction='row'>
                  <UIDropdown
                    value={selectedAuthType}
                    options={[
                      { value: 'none', label: 'None' },
                      { value: 'basic', label: 'Basic' },
                      { value: 'bearer', label: 'Bearer' },
                      { value: 'custom-header', label: 'Custom Header' },
                    ]}
                    isOpen={false}
                    onClick={() => {}}
                    onSelect={(value: string) => {
                      handleAuthTypeChange(value);
                    }}
                  />
                  <div>
                    <Button
                      auth={AUTH_KEY.AGENT.MCP_SERVER_TEST}
                      className='btn-secondary-outline !min-w-[64px] !font-semibold'
                      onClick={handleTestConnection}
                      disabled={isTestPending}
                    >
                      연결
                    </Button>
                  </div>
                </UIUnitGroup>
              </UIFormField>
            </UIArticle>
            {selectedAuthType === 'basic' && (
              <UIArticle>
                <UIFormField gap={8} direction='column'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                    User name
                  </UITypography>
                  <UIInput.Text
                    value={basicUsername}
                    placeholder='User name 입력'
                    onChange={e => {
                      setBasicUsername(e.target.value);
                      setIsConnectionTested(false);
                    }}
                    error={(hasAttemptedSave || hasAttemptedTest) && errorBasicUsername ? 'User name을 입력해 주세요.' : ''}
                  />
                </UIFormField>
              </UIArticle>
            )}
            {selectedAuthType === 'basic' && (
              <UIArticle>
                <UIFormField gap={8} direction='column'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                    Password
                  </UITypography>
                  <UIInput.Password
                    value={basicPassword}
                    onChange={e => {
                      setBasicPassword(e.target.value);
                      setIsConnectionTested(false);
                    }}
                    placeholder='Password 입력'
                    error={(hasAttemptedSave || hasAttemptedTest) && errorBasicPassword ? 'Password를 입력해 주세요.' : ''}
                  />
                </UIFormField>
              </UIArticle>
            )}
            {selectedAuthType === 'bearer' && (
              <UIArticle>
                <UIFormField gap={8} direction='column'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                    Bearer Token
                  </UITypography>
                  <UIInput.Text
                    value={bearerToken}
                    placeholder='Bearer Token 입력'
                    onChange={e => {
                      setBearerToken(e.target.value);
                      setIsConnectionTested(false);
                    }}
                    error={(hasAttemptedSave || hasAttemptedTest) && errorBearerToken ? 'Bearer Token을 입력해 주세요.' : ''}
                  />
                </UIFormField>
              </UIArticle>
            )}
            {selectedAuthType === 'custom-header' && (
              <UIArticle>
                <UIFormField gap={8} direction='column'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                    Key
                  </UITypography>
                  <UIInput.Text
                    value={headerKey}
                    placeholder='Key 입력'
                    onChange={e => {
                      setHeaderKey(e.target.value);
                      setIsConnectionTested(false);
                    }}
                    error={(hasAttemptedSave || hasAttemptedTest) && errorHeaderKey ? 'Header Key를 입력해 주세요.' : ''}
                  />
                </UIFormField>
              </UIArticle>
            )}
            {selectedAuthType === 'custom-header' && (
              <UIArticle>
                <UIFormField gap={8} direction='column'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                    Value
                  </UITypography>
                  <UIInput.Text
                    value={headerValue}
                    placeholder='Value 입력'
                    onChange={e => {
                      setHeaderValue(e.target.value);
                      setIsConnectionTested(false);
                    }}
                    error={(hasAttemptedSave || hasAttemptedTest) && errorHeaderValue ? 'Header Value를 입력해 주세요.' : ''}
                  />
                </UIFormField>
              </UIArticle>
            )}
            <UIArticle>
              <UIInput.Tags tags={tags} onChange={newTags => setTags(newTags.slice(0, 7))} label='태그' placeholder='태그 입력' />
            </UIArticle>
          </UIPopupBody>
        </section>
      </UILayerPopup>
    </>
  );
};
