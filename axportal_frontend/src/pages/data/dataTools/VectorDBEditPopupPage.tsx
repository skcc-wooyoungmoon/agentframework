import { useEffect, useState, useMemo } from 'react';

import { UIArticle, UIFormField, UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import type { LayerPopupProps } from '@/hooks/common/layer';
import { useGetConnectionArgs, useUpdateVectorDB } from '@/services/data/tool/dataToolVectorDB.services';
import { UIButton2, UIRadio2, UITypography } from '@/components/UI';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';

interface VectorDBEditPopupPageProps extends LayerPopupProps {
  vectorDbId: string;
  vectorDBData?: any;
  isDefault?: boolean;
  onUpdateSuccess?: () => void;
}

export function VectorDBEditPopupPage({ vectorDbId, vectorDBData, isDefault, currentStep, onClose, onUpdateSuccess }: VectorDBEditPopupPageProps) {
  const [vectorDBName, setVectorDBName] = useState(vectorDBData?.name || '');
  const [isDefaultValue, setIsDefaultValue] = useState(isDefault || false);

  // 동적 필드 값들을 저장할 state - vectorDBData의 connectionInfo로 초기화
  const [connectionFields, setConnectionFields] = useState<Record<string, string>>(vectorDBData?.connectionInfo || {});

  // connectionArgs 호출
  const { data: toolConnectArgs, refetch } = useGetConnectionArgs();

  // 공통 팝업 훅
  const { showCancelConfirm, showEditComplete, showNoEditContent } = useCommonPopup();

  // 팝업이 열릴 때마다 최신 데이터로 상태 업데이트
  useEffect(() => {
    if (currentStep === 1) {
      // 최신 데이터로 상태 업데이트
      setVectorDBName(vectorDBData?.name || '');
      setIsDefaultValue(isDefault || false);

      // connectionInfo의 키를 connectionInfoArgs의 키로 매핑
      const connectionInfo = vectorDBData?.connectionInfo || {};
      const mappedConnectionFields: Record<string, string> = { ...connectionInfo };

      Object.entries(connectionInfo).forEach(([key, value]) => {
        // secure 필드는 대문자로 변환 ('true' -> 'True', 'false' -> 'False')
        if (key.toLowerCase() === 'secure') {
          const stringValue = String(value).toLowerCase();
          mappedConnectionFields[key] = stringValue === 'true' ? 'True' : 'False';
        } else {
          mappedConnectionFields[key] = value as string;
        }
      });

      setConnectionFields(mappedConnectionFields);
      // connectionArgs도 새로고침
      refetch();
    }
  }, [currentStep, vectorDBData, isDefault, refetch]);

  // enable true만 필터링
  const loadersArray = Array.isArray(toolConnectArgs) ? (toolConnectArgs as any[]).filter((l: any) => l.enable === true) : [];

  // 선택된 로더 (vectorDBData의 type으로 초기화)
  const rawSelectedLoader = loadersArray.find((l: any) => l.type === vectorDBData?.type) || null;

  // connectionInfoArgs의 키를 변환한 selectedLoader 생성
  const selectedLoader = useMemo(() => {
    if (!rawSelectedLoader || !rawSelectedLoader.connectionInfoArgs) return rawSelectedLoader;

    // connectionInfoArgs의 키 변환 (snake_case -> camelCase)
    const transformedConnectionInfoArgs: Record<string, any> = {};
    Object.entries(rawSelectedLoader.connectionInfoArgs).forEach(([key, value]) => {
      // api_key -> apiKey, db_name -> dbName 변환
      const transformedKey = key === 'api_key' ? 'apiKey' : key === 'db_name' ? 'dbName' : key;
      transformedConnectionInfoArgs[transformedKey] = value;
    });

    return {
      ...rawSelectedLoader,
      connectionInfoArgs: transformedConnectionInfoArgs,
    };
  }, [rawSelectedLoader]);

  // 선택된 로더 (vectorDBData의 type으로 초기화)
  // const selectedLoader = loadersArray.find((l: any) => l.type === vectorDBData?.type) || null;
  // console.log('>?>>>>>selectedLoader:', selectedLoader);

  // renderConnectionFields 함수 정의
  const renderConnectionFields = (loader: any) => {
    // console.log('>?>>>>>loader:', loader);
    if (!loader?.connectionInfoArgs) return null;

    return Object.entries(loader.connectionInfoArgs).map(([key, args]: [string, any]) => {
      if (!args) return null;

      // 필드명 매핑
      // 키별 maxLength 매핑
      const fieldLabelMap: Record<string, string> = {
        host: 'Host',
        port: 'Port',
        user: 'User',
        password: 'Password',
        secure: 'Secure',
        dbName: 'Database Name',
        endpoint: 'Endpoint',
        apiKey: 'API Key',
      };

      // 키별 maxLength 매핑
      const fieldMaxLengthMap: Record<string, number> = {
        host: 255,
        port: 10,
        user: 255,
        password: 255,
        dbName: 255,
        endpoint: 255,
        apiKey: 255,
      };
      const labelText = fieldLabelMap[key] || key.charAt(0).toUpperCase() + key.slice(1);
      const isPasswordField = key.toLowerCase().includes('key') || key.toLowerCase().includes('password') || key.toLowerCase().includes('secret');
      const isRadioField = key.toLowerCase().includes('secure') || key.toLowerCase().includes('default');
      const maxLength = fieldMaxLengthMap[key] || 255;

      // RadioGroup 필드 처리
      if (isRadioField) {
        const isSecureField = key.toLowerCase().includes('secure');

        // secure는 대문자 'True'/'False'로 맞춤
        const radioOptions = isSecureField
          ? [
              { value: 'True', label: 'True' },
              { value: 'False', label: 'False' },
            ]
          : [
              { value: 'true', label: 'true' },
              { value: 'false', label: 'false' },
            ];

        // secure 필드는 대소문자 변환 처리
        let currentValue: string;
        if (isSecureField) {
          const rawValue = connectionFields[key] ?? 'False';
          // 소문자로 온 경우 대문자로 변환
          const normalizedValue = typeof rawValue === 'string' ? rawValue.toLowerCase() : String(rawValue).toLowerCase();
          currentValue = normalizedValue === 'true' ? 'True' : 'False';
        } else {
          currentValue = connectionFields[key] ?? 'false';
        }

        return (
          <UIArticle key={key}>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={!!args.required}>
                {labelText}
              </UITypography>
              <UIUnitGroup gap={12} direction='row' align='start'>
                {radioOptions.map(opt => (
                  <UIRadio2
                    key={opt.value}
                    name={key}
                    value={opt.value}
                    label={opt.label}
                    checked={currentValue === opt.value}
                    onChange={() => {
                      setConnectionFields(prev => ({ ...prev, [key]: opt.value }));
                    }}
                  />
                ))}
              </UIUnitGroup>
            </UIFormField>
          </UIArticle>
        );
      }

      // 일반 TextField 처리
      return (
        <UIArticle key={key}>
          <UIFormField gap={8} direction='column'>
            <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={!!args.required}>
              {labelText}
            </UITypography>
            {isPasswordField ? (
              <UIInput.Password
                value={connectionFields[key] || ''}
                maxLength={maxLength}
                onChange={e => {
                  setConnectionFields(prev => ({ ...prev, [key]: e.target.value }));
                }}
                placeholder={`${labelText} 입력`}
              />
            ) : (
              <UIInput.Text
                value={connectionFields[key] || ''}
                maxLength={maxLength}
                onChange={e => {
                  let value = e.target.value;

                  if (key === 'port') {
                    value = value.replace(/[^0-9]/g, '');
                  }
                  setConnectionFields(prev => ({ ...prev, [key]: value }));
                }}
                placeholder={`${labelText} 입력`}
              />
            )}
          </UIFormField>
        </UIArticle>
      );
    });
  };

  /**
   * VectorDB 수정
   */
  const { mutate: updateVectorDB } = useUpdateVectorDB({
    onSuccess: /* data */ () => {
      // console.log('데이터 도구 - VectorDB 수정 성공 - 전체 응답:', data);
      showEditComplete({
        onConfirm: () => {
          handleClose();
          onUpdateSuccess?.();
        },
      });
    },
    onError: /* error */ () => {
      // console.error('데이터 도구 - VectorDB 수정 실패:', error);
    },
  });

  /**
   * 데이터 도구 저장
   */
  const handleSave = () => {
    // console.log('저장 버튼 클릭');
    const isDefaultUnchanged = isDefaultValue === (isDefault || false);
    if (vectorDBName.trim() === vectorDBData?.name && JSON.stringify(connectionFields) === JSON.stringify(vectorDBData?.connectionInfo) && isDefaultUnchanged) {
      showNoEditContent({});
      return;
    } // 수정 내용이 있는지 확인
    const hasNameChanged = vectorDBName.trim() !== (vectorDBData?.name || '').trim();
    const hasDefaultChanged = isDefaultValue !== (isDefault || false);

    // connectionInfo 비교
    const originalConnectionInfo = vectorDBData?.connectionInfo || {};
    const hasConnectionInfoChanged = JSON.stringify(connectionFields) !== JSON.stringify(originalConnectionInfo);

    // 변경사항이 없으면 알림 표시
    if (!hasNameChanged && !hasDefaultChanged && !hasConnectionInfoChanged) {
      showNoEditContent({
        onConfirm: () => {},
      });
      return;
    }

    const newErrors: Record<string, string> = {};
    if (!vectorDBName.trim()) newErrors.name = '이름을 입력해 주세요.';

    // selectedLoader가 존재하고 connectionInfoArgs가 있을 때만 처리
    if (selectedLoader && selectedLoader.connectionInfoArgs) {
      Object.entries(selectedLoader.connectionInfoArgs).forEach(([key, args]: [string, any]) => {
        if (!args) return;
        const labelText = key.charAt(0).toUpperCase() + key.slice(1);
        if (args.required && !(connectionFields[key] || '').trim()) {
          newErrors[key] = `${labelText}를 입력해 주세요.`;
        }
      });
    }

    if (Object.keys(newErrors).length > 0) {
      // console.log('newErrors:', newErrors);
      return;
    }

    // 통과 시 수정 호출
    updateVectorDB({
      vectorDbId: vectorDbId,
      name: vectorDBName.trim(),
      isDefault: isDefaultValue ? 'True' : 'False',
      type: vectorDBData?.type || '',
      connectionInfo: connectionFields,
    } as any);
  };

  const handleClose = () => {
    onClose();
  };

  const handleCancel = () => {
    // console.log('취소 버튼 클릭');
    showCancelConfirm({
      onConfirm: () => {
        onClose();
      },
    });
  };
  return (
    <UILayerPopup
      isOpen={currentStep === 1}
      onClose={handleClose}
      size='fullscreen'
      showOverlay
      leftContent={
        <UIPopupAside>
          <UIPopupHeader title={<>벡터 DB 수정</>} description='' position='left' />
          <UIPopupBody />
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={false} onClick={handleSave}>
                  저장
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      <section className='section-popup-content'>
        <UIPopupBody>
          {/* 이름 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required>
                이름
              </UITypography>
              <UIInput.Text
                value={vectorDBName}
                maxLength={50}
                onChange={e => {
                  setVectorDBName(e.target.value);
                }}
                placeholder='이름 입력'
              />
            </UIFormField>
          </UIArticle>

          {/* 유형 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb'>
                유형
              </UITypography>
              <UIInput.Text value={vectorDBData?.type || ''} readOnly placeholder='유형' />
            </UIFormField>
          </UIArticle>

          {/* 연결 필드 */}
          {selectedLoader && renderConnectionFields(selectedLoader)}

          {/* Default */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required>
                Default
              </UITypography>
              <UIUnitGroup gap={12} direction='row' align='start'>
                <UIRadio2 name='default' value='true' label='True' checked={isDefaultValue} onChange={() => setIsDefaultValue(true)} />
                <UIRadio2 name='default' value='false' label='False' checked={!isDefaultValue} onChange={() => setIsDefaultValue(false)} />
              </UIUnitGroup>
            </UIFormField>
          </UIArticle>
        </UIPopupBody>
      </section>
    </UILayerPopup>
  );
}
