import { useEffect, useState } from 'react';

import { Button } from '@/components/common/auth';
import { UIRadio2, UIToggle, UITypography } from '@/components/UI/atoms';
import { UICode } from '@/components/UI/atoms/UICode';
import { UIArticle, UIFormField, UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UITextArea2, UIUnitGroup } from '@/components/UI/molecules';
import { UIDropdown } from '@/components/UI/molecules/dropdown/UIDropdown';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { AUTH_KEY } from '@/constants/auth/auth.constants';
import type { LayerPopupProps } from '@/hooks/common/layer';
import { useUpdateAgentToolById } from '@/services/agent/tool/agentTool.services';
import { useModal } from '@/stores/common/modal';
interface AgentToolEditPopupPageProps extends LayerPopupProps {
  toolId?: string;
  toolName?: string;
  toolDisplayName?: string;
  toolDescription?: string;
  toolType?: string;
  method?: string;
  serverUrl?: string;
  headerParams?: any;
  apiParams?: string;
  code?: string;
  onUpdateSuccess?: () => void;
}

export function AgentToolEditPopupPage({
  currentStep,
  onClose,
  toolId,
  toolName = '',
  toolDisplayName = '',
  toolDescription = '',
  toolType: initialToolType = 'custom_api',
  method: initialMethod = 'GET',
  serverUrl = '',
  headerParams: initialHeaderParams = [],
  apiParams: initialApiParams = '',
  code: initialCode = '',
  onUpdateSuccess,
}: AgentToolEditPopupPageProps) {
  const { openConfirm, openAlert } = useModal();

  const [name, setName] = useState(toolName);
  const [displayName, setDisplayName] = useState(toolDisplayName);
  const [description, setDescription] = useState(toolDescription);
  const [toolType, setToolType] = useState(initialToolType);
  const [method, setMethod] = useState(initialMethod);
  const [apiUrl, setApiUrl] = useState(serverUrl);
  const [headerParams, setHeaderParams] = useState<Array<{ name: string; value: string }>>([{ name: '', value: '' }]);
  const [apiParams, setApiParams] = useState<Array<{ name: string; value: string; isDynamic: boolean }>>([{ name: '', value: '', isDynamic: false }]);
  const [bodyParams, setBodyParams] = useState<Array<{ name: string; value: string; isDynamic: boolean }>>([{ name: '', value: '', isDynamic: false }]);

  const methodOptions = [
    { value: 'GET', label: 'GET' },
    { value: 'POST', label: 'POST' },
    { value: 'PUT', label: 'PUT' },
    { value: 'DELETE', label: 'DELETE' },
  ];

  const [code, setCode] = useState('');

  const ensureContentTypeFirst = (params: Array<{ name: string; value: string }>) => {
    const contentTypeIndex = params.findIndex(p => p.name === 'Content-Type' && p.value === 'application/json');
    if (contentTypeIndex > 0) {
      const contentType = params[contentTypeIndex];
      const rest = params.filter((_, i) => i !== contentTypeIndex);
      return [contentType, ...rest];
    }
    return params;
  };

  useEffect(() => {
    setName(toolName);
    setDisplayName(toolDisplayName);
    setDescription(toolDescription);
    setToolType(initialToolType);
    setMethod(initialMethod);
    setApiUrl(serverUrl);
    const initialHeaders = initialHeaderParams.length > 0 ? initialHeaderParams : [{ name: '', value: '' }];
    setHeaderParams(ensureContentTypeFirst(initialHeaders));
    setCode(initialCode);

    if (initialHeaderParams.length === 0 && initialApiParams) {
      const parsed = JSON.parse(initialApiParams);
      const headers = (parsed?.headers || parsed?.header || {}) as Record<string, unknown>;
      if (headers && typeof headers === 'object' && !Array.isArray(headers)) {
        const headerEntries = Object.entries(headers).map(([name, value]) => ({ name, value: String(value ?? '') }));
        if (headerEntries.length > 0) {
          setHeaderParams(ensureContentTypeFirst(headerEntries));
        }
      }
    }

    if (initialApiParams) {
      try {
        const parsedApiParams = JSON.parse(initialApiParams);
        const combinedParams: Array<{ name: string; value: string; isDynamic: boolean }> = [];

        if (parsedApiParams.params) {
          Object.entries(parsedApiParams.params).forEach(([name, value]) => {
            const isDynamic = value === 'str';
            combinedParams.push({ name, value: String(value), isDynamic });
          });
        } else {
          if (parsedApiParams.static_params) {
            Object.entries(parsedApiParams.static_params).forEach(([name, value]) => {
              combinedParams.push({ name, value: String(value), isDynamic: false });
            });
          }

          if (parsedApiParams.dynamic_params) {
            Object.entries(parsedApiParams.dynamic_params).forEach(([name, value]) => {
              combinedParams.push({ name, value: String(value), isDynamic: true });
            });
          }
        }

        setApiParams(combinedParams.length > 0 ? combinedParams : [{ name: '', value: '', isDynamic: false }]);
      } catch (error) {
        setApiParams([{ name: '', value: '', isDynamic: false }]);
      }
    } else {
      setApiParams([{ name: '', value: '', isDynamic: false }]);
    }

    if (initialApiParams) {
      try {
        const parsedApiParams = JSON.parse(initialApiParams);
        const combinedBodyParams: Array<{ name: string; value: string; isDynamic: boolean }> = [];

        if (parsedApiParams.body) {
          Object.entries(parsedApiParams.body).forEach(([name, value]) => {
            const isDynamic = value === 'str';
            combinedBodyParams.push({ name, value: String(value), isDynamic });
          });
        } else {
          if (parsedApiParams.static_body) {
            Object.entries(parsedApiParams.static_body).forEach(([name, value]) => {
              combinedBodyParams.push({ name, value: String(value), isDynamic: false });
            });
          }

          if (parsedApiParams.dynamic_body) {
            Object.entries(parsedApiParams.dynamic_body).forEach(([name, value]) => {
              combinedBodyParams.push({ name, value: String(value), isDynamic: true });
            });
          }
        }

        setBodyParams(combinedBodyParams.length > 0 ? combinedBodyParams : [{ name: '', value: '', isDynamic: false }]);
      } catch (error) {
        setBodyParams([{ name: '', value: '', isDynamic: false }]);
      }
    } else {
      setBodyParams([{ name: '', value: '', isDynamic: false }]);
    }
  }, [toolName, toolDescription, initialToolType, initialMethod, serverUrl, initialHeaderParams, initialApiParams, initialCode]);

  const { mutate: updateAgentToolMutation, isPending } = useUpdateAgentToolById({
    onSuccess: () => {
      openAlert({
        title: '완료',
        message: `수정사항이 저장되었습니다.`,
        onConfirm: () => {
          onUpdateSuccess?.();
          handleClose();
        },
      });
    },
    onError: () => { },
  });

  const handleClose = () => {
    onClose();
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
      onCancel: () => { },
    });
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

  const hasChanges = () => {
    if (name.trim() !== toolName.trim() || displayName.trim() !== toolDisplayName.trim()) {
      return true;
    }
    if (description.trim() !== toolDescription.trim()) {
      return true;
    }
    if (toolType !== initialToolType) {
      return true;
    }
    if (method !== initialMethod) {
      return true;
    }
    if (apiUrl.trim() !== serverUrl.trim()) {
      return true;
    }
    if (code.trim() !== initialCode.trim()) {
      return true;
    }

    let initialHeaders: Array<{ name: string; value: string }> = [];
    if (initialHeaderParams.length > 0) {
      initialHeaders = initialHeaderParams;
    } else if (initialApiParams) {
      try {
        const parsed = JSON.parse(initialApiParams);
        const headers = (parsed.headers || parsed.header || {}) as Record<string, unknown>;
        initialHeaders = Object.entries(headers).map(([name, value]) => ({ name, value: String(value ?? '') }));
      } catch {
        initialHeaders = [];
      }
    }
    const validCurrentHeaders = headerParams.filter(param => param.name.trim() || param.value.trim());
    const validInitialHeaders = initialHeaders.filter(param => param.name.trim() || param.value.trim());
    if (validCurrentHeaders.length !== validInitialHeaders.length) {
      return true;
    }
    const headersChanged =
      validCurrentHeaders.some((param, index) => {
        const initialParam = validInitialHeaders[index];
        return !initialParam || param.name !== initialParam.name || param.value !== initialParam.value;
      }) ||
      validInitialHeaders.some((param, index) => {
        const currentParam = validCurrentHeaders[index];
        return !currentParam || param.name !== currentParam.name || param.value !== currentParam.value;
      });
    if (headersChanged) {
      return true;
    }

    let initialApiParamsArray: Array<{ name: string; value: string; isDynamic: boolean }> = [];
    if (initialApiParams) {
      try {
        const parsedApiParams = JSON.parse(initialApiParams);
        if (parsedApiParams.params) {
          Object.entries(parsedApiParams.params).forEach(([name, value]) => {
            const isDynamic = value === 'str';
            initialApiParamsArray.push({ name, value: String(value), isDynamic });
          });
        } else {
          if (parsedApiParams.static_params) {
            Object.entries(parsedApiParams.static_params).forEach(([name, value]) => {
              initialApiParamsArray.push({ name, value: String(value), isDynamic: false });
            });
          }
          if (parsedApiParams.dynamic_params) {
            Object.entries(parsedApiParams.dynamic_params).forEach(([name, value]) => {
              initialApiParamsArray.push({ name, value: String(value), isDynamic: true });
            });
          }
        }
      } catch {
        initialApiParamsArray = [];
      }
    }
    const validCurrentApiParams = apiParams.filter(param => param.name.trim() || param.value.trim());
    const validInitialApiParams = initialApiParamsArray.filter(param => param.name.trim() || param.value.trim());
    if (validCurrentApiParams.length !== validInitialApiParams.length) {
      return true;
    }
    const apiParamsChanged =
      validCurrentApiParams.some((param, index) => {
        const initialParam = validInitialApiParams[index];
        return !initialParam || param.name !== initialParam.name || param.value !== initialParam.value || param.isDynamic !== initialParam.isDynamic;
      }) ||
      validInitialApiParams.some((param, index) => {
        const currentParam = validCurrentApiParams[index];
        return !currentParam || param.name !== currentParam.name || param.value !== currentParam.value || param.isDynamic !== currentParam.isDynamic;
      });
    if (apiParamsChanged) {
      return true;
    }

    let initialBodyParamsArray: Array<{ name: string; value: string; isDynamic: boolean }> = [];
    if (initialApiParams) {
      try {
        const parsedApiParams = JSON.parse(initialApiParams);
        if (parsedApiParams.body) {
          Object.entries(parsedApiParams.body).forEach(([name, value]) => {
            const isDynamic = value === 'str';
            initialBodyParamsArray.push({ name, value: String(value), isDynamic });
          });
        } else {
          if (parsedApiParams.static_body) {
            Object.entries(parsedApiParams.static_body).forEach(([name, value]) => {
              initialBodyParamsArray.push({ name, value: String(value), isDynamic: false });
            });
          }
          if (parsedApiParams.dynamic_body) {
            Object.entries(parsedApiParams.dynamic_body).forEach(([name, value]) => {
              initialBodyParamsArray.push({ name, value: String(value), isDynamic: true });
            });
          }
        }
      } catch {
        initialBodyParamsArray = [];
      }
    }
    const validCurrentBodyParams = bodyParams.filter(param => param.name.trim() || param.value.trim());
    const validInitialBodyParams = initialBodyParamsArray.filter(param => param.name.trim() || param.value.trim());
    if (validCurrentBodyParams.length !== validInitialBodyParams.length) {
      return true;
    }
    const bodyParamsChanged =
      validCurrentBodyParams.some((param, index) => {
        const initialParam = validInitialBodyParams[index];
        return !initialParam || param.name !== initialParam.name || param.value !== initialParam.value || param.isDynamic !== initialParam.isDynamic;
      }) ||
      validInitialBodyParams.some((param, index) => {
        const currentParam = validCurrentBodyParams[index];
        return !currentParam || param.name !== currentParam.name || param.value !== currentParam.value || param.isDynamic !== currentParam.isDynamic;
      });
    if (bodyParamsChanged) {
      return true;
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

    let hasError = false;

    if (!name.trim()) {
      hasError = true;
    } else if (name.length > 50) {
      hasError = true;
    }

    if (!displayName.trim()) {
      hasError = true;
    } else if (displayName.length > 50) {
      hasError = true;
    }

    if (!description.trim()) {
      hasError = true;
    } else if (description.length > 100) {
      hasError = true;
    }

    if (toolType === 'custom_api' && !apiUrl.trim()) {
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

      const requestData = {
        toolId: toolId || '',
        name: name.trim(),
        displayName: displayName.trim().length > 0 ? displayName.trim() : name.trim(),
        description: description.trim(),
        toolType,
        method,
        serverUrl: apiUrl.trim(),
        apiParam: {
          headers: headerData,
          params: paramsData,
          body: bodyData,
        } as any,
        tags: [],
      };

      updateAgentToolMutation(requestData);
    } else {
      const requestData = {
        toolId: toolId || '',
        name: name.trim(),
        displayName: displayName.trim().length > 0 ? displayName.trim() : name.trim(),
        description: description.trim(),
        toolType,
        code: code.trim(),
        tags: [],
      };

      updateAgentToolMutation(requestData);
    }
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

  return (
    <UILayerPopup
      isOpen={currentStep === 1}
      onClose={handleClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        <UIPopupAside>
          <UIPopupHeader title='Tool 수정' description='' position='left' />
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <Button className='btn-tertiary-gray !w-[80px]' onClick={handleCancel}>
                  취소
                </Button>
                <Button auth={AUTH_KEY.AGENT.TOOL_UPDATE} className='btn-tertiary-blue !w-[80px]' onClick={handleSave} disabled={isPending || !isFormValid()}>
                  저장
                </Button>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      <section className='section-popup-content'>
        <UIPopupHeader title='기본 정보' description='' position='right' />
        <UIPopupBody>
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-title-4-sb'>
                이름
              </UITypography>
              <UIInput.Text value={name} placeholder='영문명 입력' disabled maxLength={50} />
            </UIFormField>
          </UIArticle>
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb'>
                표시 이름
              </UITypography>
              <UIInput.Text
                value={displayName}
                placeholder='표시 이름 입력'
                onChange={e => {
                  setDisplayName(e.target.value);
                }}
                maxLength={50}
              />
            </UIFormField>
          </UIArticle>

          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' required={true} className='secondary-neutral-800 text-title-4-sb'>
                설명
              </UITypography>
              <UITextArea2
                value={description}
                placeholder='설명 입력'
                onChange={e => {
                  setDescription(e.target.value);
                }}
                maxLength={100}
              />
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
                <UIRadio2 name='toolType' label='Custom API' value='custom_api' checked={toolType === 'custom_api'} disabled />
                <UIRadio2 name='toolType' label='Custom Code' value='custom_code' checked={toolType === 'custom_code'} disabled />
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
                      setApiUrl(e.target.value);
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
                          <Button className='ic-system-48-delete cursor-pointer' onClick={() => (headerParams.length === 1 || isContentType ? null : removeHeaderParam(index))} />
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
