import { useEffect, useState } from 'react';

import { Button } from '@/components/common/auth';
import { UITypography } from '@/components/UI/atoms';
import { UIRadio2 } from '@/components/UI/atoms/UIRadio2';
import { UIArticle, UIFormField, UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UITextArea2, UIUnitGroup } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown/component';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { useTestConnectionAgentMcp, useUpdateAgentMcpCtlg } from '@/services/agent/mcp/agentMcp.services';
import { useModal } from '@/stores/common/modal/useModal';

interface AgentMcpCtlgEditPopupPageProps {
  isOpen?: boolean;
  onClose?: () => void;
  mcpId?: string;
  name?: string;
  description?: string;
  serverUrl?: string;
  transportType?: 'streamable-http' | 'sse';
  authType?: 'none' | 'basic' | 'bearer' | 'custom-header';
  authConfig?: any;
  tags?: string[];
  onUpdateSuccess?: () => void;
}

export function AgentMcpCtlgEditPopupPage(initialProps: AgentMcpCtlgEditPopupPageProps = {}) {
  const { openConfirm, openAlert } = useModal();

  const [nameValue, setNameValue] = useState(initialProps.name || '');
  const [descriptionValue, setDescriptionValue] = useState(initialProps.description || '');
  const [selectedToolType, setSelectedToolType] = useState(initialProps.transportType || 'streamable-http'); // 썌
  const [apiUrlValue, setApiUrlValue] = useState(initialProps.serverUrl || '');
  const [selectedAuthType, setSelectedAuthType] = useState(initialProps.authType || 'none');
  const [basicUsername, setBasicUsername] = useState(initialProps.authConfig?.username || '');
  const [basicPassword, setBasicPassword] = useState(initialProps.authConfig?.password || '');
  const [bearerToken, setBearerToken] = useState(initialProps.authConfig?.token || '');
  const [customKey, setCustomKey] = useState(initialProps.authConfig?.key || '');
  const [customValue, setCustomValue] = useState(initialProps.authConfig?.value || '');
  const [tags, setTags] = useState<string[]>(initialProps.tags || []);

  const [errorApiUrl, setErrorApiUrl] = useState<boolean>(false);
  const [errorBasicUsername, setErrorBasicUsername] = useState<boolean>(false);
  const [errorBasicPassword, setErrorBasicPassword] = useState<boolean>(false);
  const [errorBearerToken, setErrorBearerToken] = useState<boolean>(false);
  const [errorHeaderKey, setErrorHeaderKey] = useState<boolean>(false);
  const [errorHeaderValue, setErrorHeaderValue] = useState<boolean>(false);

  const [hasAttemptedSave, setHasAttemptedSave] = useState<boolean>(false);
  const [hasAttemptedTest, setHasAttemptedTest] = useState<boolean>(false);
  const [isConnectionTested, setIsConnectionTested] = useState<boolean>(false);

  useEffect(() => {
    if (!initialProps.isOpen) return;
    // 팝업이 처음 열릴 때만 초기값 설정
    if (initialProps.name !== undefined) {
      const nameValue = initialProps.name;
      const convertedName = typeof nameValue === 'string' ? nameValue : (nameValue as any)?.name || '';
      setNameValue(convertedName);
    }
    if (initialProps.description !== undefined) {
      setDescriptionValue(initialProps.description || '');
    }
    if (initialProps.transportType !== undefined) {
      setSelectedToolType((initialProps.transportType as any) || 'streamable-http');
    }
    if (initialProps.serverUrl !== undefined) {
      setApiUrlValue(initialProps.serverUrl || '');
    }
    if (initialProps.authType !== undefined) {
      setSelectedAuthType((initialProps.authType as any) || 'none');
    }
    if (initialProps.authConfig !== undefined) {
      const cfg: any = initialProps.authConfig || {};
      setBasicUsername(cfg.username || '');
      setBasicPassword(cfg.password || '');
      setBearerToken(cfg.token || '');
      setCustomKey(cfg.key || '');
      setCustomValue(cfg.value || '');
    }
    if (initialProps.tags !== undefined) {
      const tagsArray = initialProps.tags || [];
      const convertedTags = Array.isArray(tagsArray) ? tagsArray.map((tag: any) => (typeof tag === 'string' ? tag : tag?.name || '')) : [];
      setTags(convertedTags);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [initialProps.isOpen]);

  useEffect(() => {
    if (initialProps.isOpen) {
      resetAllStates();
    }
  }, [initialProps.isOpen]);

  const handleToolTypeChange = (value: string) => {
    setSelectedToolType(value as 'streamable-http' | 'sse');
    setIsConnectionTested(false);
  };

  const handleAuthTypeChange = (value: string) => {
    setSelectedAuthType(value as 'none' | 'basic' | 'bearer' | 'custom-header');
    setIsConnectionTested(false);
  };

  const resetErrorStates = () => {
    setErrorApiUrl(false);
    setErrorBasicUsername(false);
    setErrorBasicPassword(false);
    setErrorBearerToken(false);
    setErrorHeaderKey(false);
    setErrorHeaderValue(false);
  };

  const resetAllStates = () => {
    setHasAttemptedSave(false);
    setHasAttemptedTest(false);
    setIsConnectionTested(false);
    resetErrorStates();
  };

  const { mutate: testConnection, isPending: isTestPending } = useTestConnectionAgentMcp({
    onSuccess: (data: any) => {
      if (data?.data?.isConnected === true) {
        setIsConnectionTested(true);
        openAlert({
          title: '완료',
          message: '에이전트 MCP 서버가 연결을 확인하였습니다.',
        });
      } else {
        setIsConnectionTested(false);
      }
    },
    onError: () => {
      setIsConnectionTested(false);
    },
    onSettled: () => {
      setHasAttemptedTest(false);
    },
  });

  const handleTestConnection = () => {
    setHasAttemptedTest(true);

    resetErrorStates();

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
        authConfig = {
          username: basicUsername.trim(),
          password: basicPassword.trim(),
        };
      }
    } else if (selectedAuthType === 'bearer') {
      if (!bearerToken.trim()) {
        setErrorBearerToken(true);
        hasError = true;
      }
      if (!hasError) {
        authConfig = {
          token: bearerToken.trim(),
        };
      }
    } else if (selectedAuthType === 'custom-header') {
      if (!customKey.trim()) {
        setErrorHeaderKey(true);
        hasError = true;
      }
      if (!customValue.trim()) {
        setErrorHeaderValue(true);
        hasError = true;
      }
      if (!hasError) {
        authConfig = {
          key: customKey.trim(),
          value: customValue.trim(),
        };
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

  const { mutate: updateAgentMcpCtlg, isPending } = useUpdateAgentMcpCtlg({
    onSuccess: () => {
      openAlert({
        title: '안내',
        message: 'MCP 서버 연결에 성공하였습니다.',
        onConfirm: () => {
          initialProps.onClose?.();
          initialProps.onUpdateSuccess?.();
        },
      });
    },
    onError: () => {},
  });

  const handleClose = () => {
    initialProps.onClose?.();
  };

  const handleCancel = () => {
    openConfirm({
      title: '안내',
      message: '화면을 나가시겠어요?\n입력한 정보가 저장되지 않을 수 있습니다.',
      confirmText: '예',
      cancelText: '아니요',
      onConfirm: () => {
        handleClose();
      },
      onCancel: () => {},
    });
  };

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
      if (!customKey.trim() || !customValue.trim()) {
        return false;
      }
    }

    return true;
  };

  const hasChanges = () => {
    if (nameValue.trim() !== (initialProps.name || '').trim()) {
      return true;
    }
    if (descriptionValue.trim() !== (initialProps.description || '').trim()) {
      return true;
    }
    if (selectedToolType !== (initialProps.transportType || 'streamable-http')) {
      return true;
    }
    if (apiUrlValue.trim() !== (initialProps.serverUrl || '').trim()) {
      return true;
    }
    if (selectedAuthType !== (initialProps.authType || 'none')) {
      return true;
    }

    const initialTagsArray = initialProps.tags || [];
    const initialTags = Array.isArray(initialTagsArray) ? initialTagsArray.map((tag: any) => (typeof tag === 'string' ? tag : tag?.name || '')) : [];
    if (tags.length !== initialTags.length) {
      return true;
    }
    const tagsChanged = tags.some((tag, index) => tag !== initialTags[index]) || initialTags.some((tag, index) => tag !== tags[index]);
    if (tagsChanged) {
      return true;
    }

    const cfg: any = initialProps.authConfig || {};
    if (selectedAuthType === 'basic') {
      if (basicUsername.trim() !== (cfg.username || '').trim() || basicPassword.trim() !== (cfg.password || '').trim()) {
        return true;
      }
    } else if (selectedAuthType === 'bearer') {
      if (bearerToken.trim() !== (cfg.token || '').trim()) {
        return true;
      }
    } else if (selectedAuthType === 'custom-header') {
      if (customKey.trim() !== (cfg.key || '').trim() || customValue.trim() !== (cfg.value || '').trim()) {
        return true;
      }
    } else {
      if (initialProps.authType !== 'none' || cfg.username || cfg.password || cfg.token || cfg.key || cfg.value) {
        return true;
      }
    }

    return false;
  };

  const handleSave = async () => {
    if (!hasChanges()) {
      await openAlert({
        title: '안내',
        message: '수정된 내용이 없습니다.',
      });
      return;
    }
    setHasAttemptedSave(true);

    resetErrorStates();

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
        authConfig = {
          username: basicUsername.trim(),
          password: basicPassword.trim(),
        };
      }
    } else if (selectedAuthType === 'bearer') {
      if (!bearerToken.trim()) {
        setErrorBearerToken(true);
        hasError = true;
      }
      if (!hasError) {
        authConfig = {
          token: bearerToken.trim(),
        };
      }
    } else if (selectedAuthType === 'custom-header') {
      if (!customKey.trim()) {
        setErrorHeaderKey(true);
        hasError = true;
      }
      if (!customValue.trim()) {
        setErrorHeaderValue(true);
        hasError = true;
      }
      if (!hasError) {
        authConfig = {
          key: customKey.trim(),
          value: customValue.trim(),
        };
      }
    } else {
      authConfig = null;
    }

    if (hasError) {
      return;
    }

    const requestData: any = {
      name: nameValue.trim(),
      displayName: nameValue.trim(),
      description: descriptionValue.trim(),
      type: 'serverless',
      serverUrl: apiUrlValue.trim(),
      authType: selectedAuthType,
      tags: tags,
      transportType: selectedToolType,
    };

    if (authConfig !== null) {
      if (selectedAuthType === 'basic') {
        requestData.username = authConfig.username;
        requestData.password = authConfig.password;
      } else if (selectedAuthType === 'bearer') {
        requestData.token = authConfig.token;
      } else if (selectedAuthType === 'custom-header') {
        requestData.headerKey = authConfig.key;
        requestData.headerValue = authConfig.value;
      }
    }

    updateAgentMcpCtlg({ mcpId: initialProps.mcpId || '', ...requestData });
  };

  return (
    <>
      <UILayerPopup
        isOpen={initialProps.isOpen}
        onClose={handleClose}
        size='fullscreen'
        showOverlay={true}
        leftContent={
          <UIPopupAside>
            <UIPopupHeader title='MCP서버 수정' description='' position='left' />
            <UIPopupFooter>
              <UIArticle>
                <UIUnitGroup gap={8} direction='row' align='start'>
                  <Button className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                    취소
                  </Button>
                  <Button
                    auth={AUTH_KEY.AGENT.MCP_CTLG_UPDATE}
                    className='btn-tertiary-blue !w-[80px]'
                    onClick={handleSave}
                    disabled={isPending || !isConnectionTested || !isFormValid()}
                  >
                    저장
                  </Button>
                </UIUnitGroup>
              </UIArticle>
            </UIPopupFooter>
          </UIPopupAside>
        }
      >
        <section className='section-popup-content'>
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
                  각 유형에 맞는 설정값을 입력한 뒤, 인증 유형 우축의 [연결] 버튼으로 연결에 성공해야 [저장] 버튼이 활성화됩니다.
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
                    <Button className='btn-secondary-outline !min-w-[64px] !font-semibold' onClick={handleTestConnection} disabled={isTestPending}>
                      {isTestPending ? '연결 중' : '연결'}
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
                    disabled={isPending}
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
                  <UIInput.Password
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
                    value={customKey}
                    placeholder='Key 입력'
                    onChange={e => {
                      setCustomKey(e.target.value);
                      setIsConnectionTested(false);
                    }}
                    error={(hasAttemptedSave || hasAttemptedTest) && errorHeaderKey ? 'Key를 입력해 주세요.' : ''}
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
                  <UIInput.Password
                    value={customValue}
                    placeholder='Value 입력'
                    onChange={e => {
                      setCustomValue(e.target.value);
                      setIsConnectionTested(false);
                    }}
                    error={(hasAttemptedSave || hasAttemptedTest) && errorHeaderValue ? 'Value를 입력해 주세요.' : ''}
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
}
