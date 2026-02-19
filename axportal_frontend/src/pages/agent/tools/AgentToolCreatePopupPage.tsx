import { useState } from 'react';

import { Button } from '@/components/common/auth';
import { UIRadio2, UIToggle, UITypography } from '@/components/UI/atoms';
import { UICode } from '@/components/UI/atoms/UICode';
import { UIArticle, UIFormField, UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UITextArea2, UIUnitGroup } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { useCreateAgentTool } from '@/services/agent/tool/agentTool.services';
import { useNavigate } from 'react-router-dom';
interface AgentToolCreatePopupPageProps {
  isOpen: boolean;
  onClose: () => void;
  onCreateSuccess?: () => void;
}

export function AgentToolCreatePopupPage({ isOpen, onClose, onCreateSuccess: _ }: AgentToolCreatePopupPageProps) {
  const navigate = useNavigate();
  const { showComplete, showCancelConfirm } = useCommonPopup();
  const [name, setName] = useState('');
  const [displayName, setDisplayName] = useState('');
  const [description, setDescription] = useState('');
  const [toolType, setToolType] = useState('custom_api');
  const [code, setCode] = useState('');
  const [method, setMethod] = useState('GET');
  const [apiUrl, setApiUrl] = useState('');
  const [headerParams, setHeaderParams] = useState<Array<{ name: string; value: string }>>([{ name: 'Content-Type', value: 'application/json' }]);
  const [apiParams, setApiParams] = useState<Array<{ name: string; value: string; isDynamic: boolean }>>([{ name: '', value: '', isDynamic: false }]);
  const [bodyParams, setBodyParams] = useState<Array<{ name: string; value: string; isDynamic: boolean }>>([{ name: '', value: '', isDynamic: false }]);

  const [errorName, setErrorName] = useState<boolean>(false);
  const [errorDescription, setErrorDescription] = useState<boolean>(false);
  const [errorApiUrl, setErrorApiUrl] = useState<boolean>(false);

  const initValues = () => {
    setName('');
    setDisplayName('');
    setDescription('');
    setToolType('custom_api');
    setCode('');
    setMethod('GET');
    setApiUrl('');
    setHeaderParams([{ name: 'Content-Type', value: 'application/json' }]);
    setApiParams([{ name: '', value: '', isDynamic: false }]);
    setBodyParams([{ name: '', value: '', isDynamic: false }]);

    setErrorName(false);
    setErrorDescription(false);
    setErrorApiUrl(false);
  };

  const handleClose = () => {
    initValues();
    onClose();
  };

  const handleCancel = () => {
    showCancelConfirm({
      onConfirm: () => {
        handleClose();
      }
    });
  };

  const handleNameChange = (value: string) => {
    const allowedPattern = /^[a-zA-Z0-9\s\-_]*$/;

    if (allowedPattern.test(value)) {
      setName(value);
      if (errorName) {
        setErrorName(false);
      }
    }
  };

  const handleDescriptionChange = (value: string) => {
    setDescription(value);
    if (errorDescription) {
      setErrorDescription(false);
    }
  };

  const handleToolTypeChange = (value: string) => {
    setToolType(value);

    setCode('');
    setHeaderParams([{ name: 'Content-Type', value: 'application/json' }]);
    setApiParams([{ name: '', value: '', isDynamic: false }]);
    setBodyParams([{ name: '', value: '', isDynamic: false }]);
    setApiUrl('');
    setMethod('GET');
  };

  const methodOptions = [
    { value: 'GET', label: 'GET' },
    { value: 'POST', label: 'POST' },
    { value: 'PUT', label: 'PUT' },
    { value: 'DELETE', label: 'DELETE' },
  ];

  const handleApiUrlChange = (value: string) => {
    setApiUrl(value);
    if (errorApiUrl) {
      setErrorApiUrl(false);
    }
  };

  const ensureContentTypeFirst = (params: Array<{ name: string; value: string }>) => {
    const contentTypeIndex = params.findIndex(p => p.name === 'Content-Type' && p.value === 'application/json');
    if (contentTypeIndex > 0) {
      const contentType = params[contentTypeIndex];
      const rest = params.filter((_, i) => i !== contentTypeIndex);
      return [contentType, ...rest];
    }
    return params;
  };

  const addHeaderParam = () => {
    const updated = [...(headerParams || []), { name: '', value: '' }];
    setHeaderParams(ensureContentTypeFirst(updated));
  };

  const removeHeaderParam = (index: number) => {
    const filtered = (headerParams || []).filter((_, i) => i !== index);
    setHeaderParams(ensureContentTypeFirst(filtered));
  };

  const updateHeaderParam = (index: number, field: 'name' | 'value', value: string) => {
    const updated = [...(headerParams || [])];
    updated[index][field] = value;
    setHeaderParams(ensureContentTypeFirst(updated));
  };

  const addApiParam = () => {
    setApiParams([...(apiParams || []), { name: '', value: '', isDynamic: false }]);
  };

  const removeApiParam = (index: number) => {
    setApiParams((apiParams || []).filter((_, i) => i !== index));
  };

  const updateApiParam = (index: number, field: 'name' | 'value' | 'isDynamic', value: string | boolean) => {
    const updated = [...(apiParams || [])];
    if (field === 'isDynamic') {
      updated[index].isDynamic = value as boolean;
    } else {
      updated[index][field] = value as string;
    }
    setApiParams(updated);
  };

  const addBodyParam = () => {
    setBodyParams([...(bodyParams || []), { name: '', value: '', isDynamic: false }]);
  };

  const removeBodyParam = (index: number) => {
    setBodyParams((bodyParams || []).filter((_, i) => i !== index));
  };

  const updateBodyParam = (index: number, field: 'name' | 'value' | 'isDynamic', value: string | boolean) => {
    const updated = [...(bodyParams || [])];
    if (field === 'isDynamic') {
      updated[index].isDynamic = value as boolean;
    } else {
      updated[index][field] = value as string;
    }
    setBodyParams(updated);
  };

  const isFormValid = () => {
    if (!name.trim() || name.length > 50) {
      return false;
    }
    if (!description.trim() || description.length > 100) {
      return false;
    }
    if (toolType === 'custom_api') {
      if (!apiUrl.trim()) {
        return false;
      }
    } else if (toolType === 'custom_code') {
      if (!code.trim()) {
        return false;
      }
    }

    return true;
  };

  const { mutate: createAgentTool, isPending } = useCreateAgentTool({
    onSuccess: ({ data: { agentToolUuid } }) => {
      showComplete({
        itemName: '에이전트 도구 생성을',
        onConfirm: () => {
          // onCreateSuccess?.();
          navigate(`/agent/tools/${agentToolUuid}`);
          handleClose();
        }
      });
    },
    onError: () => { },
  });

  const handleSave = () => {
    setErrorName(false);
    setErrorDescription(false);
    setErrorApiUrl(false);

    let hasError = false;

    if (!name.trim()) {
      setErrorName(true);
      hasError = true;
    } else if (name.length > 50) {
      setErrorName(true);
      hasError = true;
    }

    if (!description.trim()) {
      setErrorDescription(true);
      hasError = true;
    } else if (description.length > 100) {
      setErrorDescription(true);
      hasError = true;
    }

    if (toolType === 'custom_api' && !apiUrl.trim()) {
      setErrorApiUrl(true);
      hasError = true;
    }

    if (hasError) {
      return;
    }

    if (toolType === 'custom_api') {
      const validHeaderParams = headerParams.filter(param => param.name.trim() || param.value.trim());
      const headerData = validHeaderParams.reduce(
        (acc, param) => {
          const key = param.name.trim() || param.name;
          acc[key] = param.value;
          return acc;
        },
        {} as Record<string, string>
      );

      const validApiParams = apiParams.filter(param => param.name.trim() || param.value.trim());
      const staticParamsData = validApiParams
        .filter(param => !param.isDynamic && param.value.trim())
        .reduce(
          (acc, param) => {
            acc[param.name] = param.value;
            return acc;
          },
          {} as Record<string, string>
        );

      const dynamicParamsData = validApiParams
        .filter(param => param.isDynamic)
        .reduce(
          (acc, param) => {
            acc[param.name] = 'str';
            return acc;
          },
          {} as Record<string, string>
        );

      const paramsData = {
        ...staticParamsData,
        ...dynamicParamsData,
      };

      const validBodyParams = bodyParams.filter(param => param.name.trim() || param.value.trim());
      const staticBodyData = validBodyParams
        .filter(param => !param.isDynamic && param.value.trim())
        .reduce(
          (acc, param) => {
            acc[param.name] = param.value;
            return acc;
          },
          {} as Record<string, string>
        );

      const dynamicBodyData = validBodyParams
        .filter(param => param.isDynamic)
        .reduce(
          (acc, param) => {
            acc[param.name] = 'str';
            return acc;
          },
          {} as Record<string, string>
        );

      const bodyData = {
        ...staticBodyData,
        ...dynamicBodyData,
      };

      createAgentTool({
        name: name.trim(),
        displayName: displayName.trim().length > 0 ? displayName.trim() : name.trim(),
        description: description.trim(),
        toolType: 'custom_api',
        method,
        serverUrl: apiUrl.trim(),
        apiParam: {
          headers: headerData,
          params: paramsData,
          body: bodyData,
        } as any,
        tags: [],
      });
    } else {
      createAgentTool({
        name: name.trim(),
        displayName: displayName.trim().length > 0 ? displayName.trim() : name.trim(),
        description: description.trim(),
        toolType: 'custom_code',
        code: code.trim(),
        tags: [],
      });
    }
  };

  return (
    <UILayerPopup
      isOpen={isOpen}
      onClose={handleClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        <UIPopupAside>
          <UIPopupHeader title='Tool 등록' description='' position='left' />
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <Button className='btn-tertiary-gray !w-[80px]' onClick={handleCancel}>
                  취소
                </Button>
                <Button auth={AUTH_KEY.AGENT.TOOL_CREATE} className='btn-tertiary-blue !w-[80px]' onClick={handleSave} disabled={isPending || !isFormValid()}>
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
            <UITypography variant='title-3' className='secondary-neutral-900 text-sb'>
              기본 정보
            </UITypography>
          </UIArticle>
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-title-4-sb'>
                이름
              </UITypography>
              <UIInput.Text
                value={name}
                placeholder='영문명 입력'
                onChange={e => {
                  handleNameChange(e.target.value);
                }}
                maxLength={50}
              />
            </UIFormField>
          </UIArticle>
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' required={false} className='secondary-neutral-800 text-sb'>
                표시이름
              </UITypography>
            </UIFormField>
            <UIInput.Text
              value={displayName}
              placeholder='표시 이름 입력'
              onChange={e => {
                setDisplayName(e.target.value);
              }}
              maxLength={50}
            />
          </UIArticle>
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-title-4-sb'>
                설명
              </UITypography>
              <UITextArea2 value={description} placeholder='설명 입력' maxLength={100} onChange={e => handleDescriptionChange(e.target.value)} />
            </UIFormField>
          </UIArticle>
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-3' className='secondary-neutral-900 text-sb'>
                Tool 정보
              </UITypography>
            </UIFormField>
          </UIArticle>
          <UIArticle className='mb-8'>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-title-4-sb'>
                Tool 유형
              </UITypography>
              <UIUnitGroup gap={16} direction='column' align='start'>
                <UIRadio2
                  name='toolType'
                  value='custom_api'
                  label='Custom API'
                  checked={toolType === 'custom_api'}
                  onChange={(checked, value) => {
                    if (checked) handleToolTypeChange(value);
                  }}
                />
                <UIRadio2
                  name='toolType'
                  value='custom_code'
                  label='Custom Code'
                  checked={toolType === 'custom_code'}
                  onChange={(checked, value) => {
                    if (checked) handleToolTypeChange(value);
                  }}
                />
              </UIUnitGroup>
            </UIFormField>
          </UIArticle>
          {toolType === 'custom_api' && (
            <>
              <UIArticle>
                <UIFormField gap={8} direction='column'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={true}>
                    메소드
                  </UITypography>
                  <UIDropdown required={true} placeholder='메소드를 선택하세요' value={method} options={methodOptions} isOpen={false} onClick={() => { }} onSelect={setMethod} />
                </UIFormField>
              </UIArticle>
              <UIArticle>
                <UIFormField gap={8} direction='column'>
                  <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-sb'>
                    API URL
                  </UITypography>
                  <UIInput.Text
                    value={apiUrl}
                    placeholder='API URL 입력'
                    onChange={e => {
                      handleApiUrlChange(e.target.value);
                    }}
                  />
                </UIFormField>
              </UIArticle>
              <UIArticle>
                <UITypography variant='title-3' className='secondary-neutral-900 text-sb'>
                  파라미터 구성
                </UITypography>
              </UIArticle>

              <UIArticle>
                <UIFormField gap={8} direction='column'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                    헤더 피라미터 설정
                  </UITypography>
                  <UIUnitGroup gap={8} direction='column'>
                    {headerParams.map((param, index) => {
                      const isContentType = param.name === 'Content-Type' && param.value === 'application/json';
                      return (
                        <div key={index} className='flex gap-2 items-center'>
                          <div className='flex-1'>
                            <UIInput.Text
                              value={param.name}
                              placeholder='파라미터 이름 입력'
                              onChange={e => updateHeaderParam(index, 'name', e.target.value)}
                              disabled={isContentType}
                            />
                          </div>
                          <div className='flex-1'>
                            <UIInput.Text
                              value={param.value}
                              placeholder='파라미터 값 입력'
                              onChange={e => updateHeaderParam(index, 'value', e.target.value)}
                              disabled={isContentType}
                            />
                          </div>
                          <Button className='button-plus-blue cursor-pointer' onClick={addHeaderParam} />
                          <Button className='ic-system-48-delete cursor-pointer' onClick={() => (index === 0 || isContentType ? null : removeHeaderParam(index))} />
                        </div>
                      );
                    })}
                  </UIUnitGroup>
                </UIFormField>
              </UIArticle>

              <UIArticle>
                <UIFormField gap={8} direction='column'>
                  <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                    Query 파라미터 설정
                  </UITypography>

                  <UIUnitGroup gap={8} direction='column'>
                    {apiParams.map((param, index) => (
                      <div key={index} className='flex gap-2 items-center'>
                        <div className='flex-1'>
                          <UIInput.Text value={param.name} placeholder='파라미터 이름 입력' onChange={e => updateApiParam(index, 'name', e.target.value)} />
                        </div>
                        <div className='flex-1'>
                          <UIInput.Text
                            value={param.isDynamic ? 'Tool 사용 시점에 값이 결정됩니다.' : param.value}
                            placeholder={param.isDynamic ? 'Tool 사용 시점에 값이 결정됩니다.' : '파라미터 값 입력'}
                            onChange={e => updateApiParam(index, 'value', e.target.value)}
                            disabled={param.isDynamic}
                          />
                        </div>
                        <UITypography variant='body-2' className='secondary-neutral-600 text-body-2'>
                          다이나믹
                        </UITypography>
                        <UIToggle
                          size='small'
                          checked={param.isDynamic}
                          onChange={() => {
                            updateApiParam(index, 'isDynamic', !param.isDynamic);
                          }}
                        />
                        <Button className='button-plus-blue cursor-pointer' onClick={addApiParam} />
                        <Button className='ic-system-48-delete cursor-pointer' onClick={() => (apiParams.length === 1 ? null : removeApiParam(index))} />
                      </div>
                    ))}
                  </UIUnitGroup>
                </UIFormField>
              </UIArticle>
              {(method === 'POST' || method === 'PUT') && (
                <UIArticle>
                  <UIFormField gap={8} direction='column'>
                    <UITypography variant='title-4' className='secondary-neutral-800 text-sb'>
                      Body 파라미터 설정
                    </UITypography>
                    <UIUnitGroup gap={8} direction='column'>
                      {bodyParams.map((param, index) => (
                        <div key={index} className='flex gap-2 items-center'>
                          <div className='flex-1'>
                            <UIInput.Text value={param.name} placeholder='파라미터 이름 입력' onChange={e => updateBodyParam(index, 'name', e.target.value)} />
                          </div>
                          <div className='flex-1'>
                            <UIInput.Text
                              value={param.isDynamic ? 'Tool 사용 시점에 값이 결정됩니다.' : param.value}
                              placeholder={param.isDynamic ? 'Tool 사용 시점에 값이 결정됩니다.' : '파라미터 값 입력'}
                              onChange={e => updateBodyParam(index, 'value', e.target.value)}
                              disabled={param.isDynamic}
                            />
                          </div>
                          <UITypography variant='body-2' className='secondary-neutral-600 text-body-2'>
                            다이나믹
                          </UITypography>
                          <UIToggle
                            size='small'
                            checked={param.isDynamic}
                            onChange={() => {
                              updateBodyParam(index, 'isDynamic', !param.isDynamic);
                            }}
                          />
                          <Button className='button-plus-blue cursor-pointer' onClick={addBodyParam} />
                          <Button className='ic-system-48-delete cursor-pointer' onClick={() => (bodyParams.length === 1 ? null : removeBodyParam(index))} />
                        </div>
                      ))}
                    </UIUnitGroup>
                  </UIFormField>
                </UIArticle>
              )}
            </>
          )}

          {toolType === 'custom_code' && (
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-3' className='secondary-neutral-900 text-sb' required={true}>
                  코드
                </UITypography>
                <UICode value={code} onChange={setCode} language='python' theme='dark' width='100%' minHeight='472px' height='472px' maxHeight='472px' readOnly={false} />
              </UIFormField>
            </UIArticle>
          )}
        </UIPopupBody>
      </section>
    </UILayerPopup>
  );
}
