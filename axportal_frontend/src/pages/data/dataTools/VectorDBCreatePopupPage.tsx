import { useEffect, useState, useMemo } from 'react';
import { UIButton2, UIRadio2, UITypography } from '@/components/UI/atoms';
import { UIArticle, UIDropdown, UIFormField, UIInput, UIPopupBody, UIPopupFooter, UIPopupHeader, UIUnitGroup } from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { useCreateVectorDB, useGetConnectionArgs } from '@/services/data/tool/dataToolVectorDB.services';
import { useModal } from '@/stores/common/modal';
import { useNavigate } from 'react-router-dom';

interface VectorDBCreatePopupPageProps {
  isOpen: boolean;
  onClose: () => void;
  onCreateSuccess?: () => void;
  onPreviousStep: () => void;
}

export function VectorDBCreatePopupPage({ isOpen, onClose, onCreateSuccess, onPreviousStep }: VectorDBCreatePopupPageProps) {
  const navigate = useNavigate();
  const [vectorDBName, setVectorDBName] = useState('');
  const [isDefault, setIsDefault] = useState(false);
  const [hasAttemptedSave, setHasAttemptedSave] = useState(false);
  const { openAlert } = useModal();

  // 동적 필드 값들을 저장할 state
  const [connectionFields, setConnectionFields] = useState<Record<string, string>>({});
  const [errors, setErrors] = useState<Record<string, string>>({});

  // 1) refetch 받아오기
  const { data: toolConnectArgs, refetch } = useGetConnectionArgs();

  // 2) 팝업이 열릴 때마다 강제로 새 데이터 가져오기
  useEffect(() => {
    if (isOpen) {
      refetch(); // 캐시 무시하고 새로 요청
      setVectorDBName(''); // 이름 초기화
      setIsDefault(false); // 기본값 초기화
      setSelectedTemplate(''); // 선택된 템플릿 초기화
      setConnectionFields({});
      setErrors({});
      setHasAttemptedSave(false);
    }
  }, [isOpen, refetch]);

  // 1) enable true만 - toolConnectArgs 자체가 배열이므로 .data 제거
  const loadersArray = Array.isArray(toolConnectArgs) ? (toolConnectArgs as any[]).filter((l: any) => l.enable === true) : [];

  // 2) dropdown 옵션: label=displayName, value=type
  const toolTypeOptions = loadersArray.map((l: any) => ({
    value: l.type,
    label: l.displayName,
  }));

  // 3) 선택 상태
  const [selectedTemplate, setSelectedTemplate] = useState('');

  // 4) 목록 로드되면 첫 항목으로 자동 선택
  useEffect(() => {
    if (loadersArray.length === 0) return;
    setSelectedTemplate(prev => (prev && loadersArray.some((l: any) => l.type === prev) ? prev : loadersArray[0].type));
  }, [loadersArray]);

  // 5) 선택된 로더
  const selectedLoader = loadersArray.find((l: any) => l.type === selectedTemplate) || null;

  // secure 필드 초기값 설정
  useEffect(() => {
    if (selectedLoader && selectedLoader.connectionInfoArgs) {
      const secureField = Object.keys(selectedLoader.connectionInfoArgs).find(key => key.toLowerCase().includes('secure'));
      if (secureField && !connectionFields[secureField]) {
        setConnectionFields(prev => ({ ...prev, [secureField]: 'false' }));
      }
    }
  }, [selectedLoader, connectionFields]);
  // 필수값 검증 로직
  const isFormValid = useMemo(() => {
    // 이름이 비어있으면 비활성화
    if (!vectorDBName.trim()) {
      return false;
    }

    // selectedLoader가 없으면 비활성화
    if (!selectedLoader || !selectedLoader.connectionInfoArgs) {
      return false;
    }

    // 필수 필드 검증
    const requiredFields = Object.entries(selectedLoader.connectionInfoArgs).filter(([, args]: [string, any]) => args && args.required);
    for (const [key] of requiredFields) {
      const isRadioField = key.toLowerCase().includes('secure') || key.toLowerCase().includes('default');

      if (isRadioField) {
        // 라디오 필드는 값이 있어야 함
        if (!connectionFields[key]) {
          return false;
        }
      } else {
        // 텍스트 필드는 trim()으로 검증
        if (!(connectionFields[key] || '').trim()) {
          return false;
        }
      }
    }

    return true;
  }, [vectorDBName, selectedLoader, connectionFields]);

  // renderConnectionFields 함수 정의
  const renderConnectionFields = (loader: any) => {
    if (!loader?.connectionInfoArgs) return null;

    return Object.entries(loader.connectionInfoArgs).map(([key, args]: [string, any]) => {
      if (!args) return null;

      // 필드명 매핑
      const fieldLabelMap: Record<string, string> = {
        host: 'Host',
        port: 'Port',
        user: 'User',
        password: 'Password',
        secure: 'Secure',
        db_name: 'Database Name',
        endpoint: 'Endpoint',
        api_key: 'API Key',
      };

      const labelText = fieldLabelMap[key] || key.charAt(0).toUpperCase() + key.slice(1);
      const isRadioField = key.toLowerCase().includes('secure') || key.toLowerCase().includes('default');
      const isPasswordField = key.toLowerCase().includes('key') || key.toLowerCase().includes('password') || key.toLowerCase().includes('secret');

      // RadioGroup 필드
      if (isRadioField) {
        const currentValue = connectionFields[key] === 'true';

        return (
          <UIArticle key={key}>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-sb' required={!!args.required}>
                {labelText}
              </UITypography>
              <UIUnitGroup gap={12} direction='row'>
                <UIRadio2
                  name={key}
                  value='true'
                  label='True'
                  checked={currentValue}
                  onChange={() => {
                    setConnectionFields(prev => ({ ...prev, [key]: 'true' }));
                    if (hasAttemptedSave && args.required) {
                      setErrors(prev => ({
                        ...prev,
                        [key]: '',
                      }));
                    }
                  }}
                />
                <UIRadio2
                  name={key}
                  value='false'
                  label='False'
                  checked={!currentValue}
                  onChange={() => {
                    setConnectionFields(prev => ({ ...prev, [key]: 'false' }));
                    if (hasAttemptedSave && args.required) {
                      setErrors(prev => ({
                        ...prev,
                        [key]: '',
                      }));
                    }
                  }}
                />
              </UIUnitGroup>
              {errors[key] && <p className='text-red-500 text-sm mt-1'>{errors[key]}</p>}
            </UIFormField>
          </UIArticle>
        );
      }

      // 일반 TextField 처리
      const isPortField = key.toLowerCase() === 'port';
      const isLimitedField = ['host', 'user', 'password', 'db_name', 'endpoint', 'api_key'].includes(key.toLowerCase());

      return (
        <UIArticle key={key}>
          <UIFormField gap={8} direction='column'>
            <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={!!args.required}>
              {labelText}
            </UITypography>
            {!isPasswordField ? (
              <UIInput.Text
                value={connectionFields[key] || ''}
                maxLength={isPortField ? 10 : isLimitedField ? 255 : undefined}
                onChange={e => {
                  let value = e.target.value;

                  // Port 필드는 정수만 입력 가능
                  if (isPortField) {
                    // 숫자가 아닌 문자는 제거
                    value = value.replace(/[^0-9]/g, '');
                  }

                  setConnectionFields(prev => ({ ...prev, [key]: value }));
                  if (hasAttemptedSave && args.required) {
                    setErrors(prev => ({
                      ...prev,
                      [key]: value.trim() ? '' : `${labelText}를 입력해 주세요.`,
                    }));
                  }
                }}
                placeholder={`${labelText} 입력`}
              />
            ) : (
              <UIInput.Password
                value={connectionFields[key] || ''}
                maxLength={isLimitedField ? 255 : undefined}
                onChange={e => {
                  setConnectionFields(prev => ({ ...prev, [key]: e.target.value }));
                }}
                placeholder={`${labelText} 입력`}
              />
            )}
            {errors[key] && <p className='text-red-500 text-sm mt-1'>{errors[key]}</p>}
          </UIFormField>
        </UIArticle>
      );
    });
  };

  /**
   * 템플릿 선택 및 필드 초기화
   */
  const handleTemplateSelect = (value: string) => {
    setSelectedTemplate(value);
    setHasAttemptedSave(false);
  };

  /**
   * VectorDB 생성
   */
  const { mutate: createVectorDB } = useCreateVectorDB({
    onSuccess: data => {
      // console.log('데이터 도구 - VectorDB 생성 성공 - 전체 응답:', data);

      // 성공 알림 추가
      openAlert({
        title: '완료',
        message: 'Vector DB 만들기를 완료하였습니다.',
        confirmText: '확인',
        onConfirm: () => {
          // onClose();
          onCreateSuccess?.();

          // 벡터 DB 목록 새로고침을 위한 이벤트 발생
          // window.dispatchEvent(
          //   new CustomEvent('vector-db-created', {
          //     detail: {
          //       vectorDbId: (data as any).data?.vectorDbId,
          //       vectorDbName: vectorDBName,
          //       message: 'Vector DB가 성공적으로 생성되었습니다.',
          //     },
          //   })
          // );

          // detail 페이지로 이동
          navigate(`/data/dataTools/vectorDB/${data.data.vectorDbId}`);

        },
      });
    },
    onError: /* error */ () => {
      // console.error('데이터 도구 - VectorDB 생성 실패:', error);
    },
  });

  /**
   * 데이터 도구 저장
   */
  const handleSave = () => {
    setHasAttemptedSave(true);

    const newErrors: Record<string, string> = {};
    if (!vectorDBName.trim()) newErrors.name = '이름을 입력해 주세요.';

    // selectedLoader가 존재하고 connectionInfoArgs가 있을 때만 처리
    if (selectedLoader && selectedLoader.connectionInfoArgs) {
      Object.entries(selectedLoader.connectionInfoArgs).forEach(([key, args]: [string, any]) => {
        if (!args) return;
        const labelText = key.charAt(0).toUpperCase() + key.slice(1);
        const isRadioField = key.toLowerCase().includes('secure') || key.toLowerCase().includes('default');

        if (args.required) {
          if (isRadioField) {
            // 라디오 필드는 값이 있으면 통과 (secure는 기본값 'false'가 있음)
            if (!connectionFields[key]) {
              newErrors[key] = `${labelText}를 선택해 주세요.`;
            }
          } else {
            // 텍스트 필드는 trim()으로 검증
            if (!(connectionFields[key] || '').trim()) {
              newErrors[key] = `${labelText}를 입력해 주세요.`;
            }
          }
        }
      });
    }

    setErrors(newErrors);
    if (Object.keys(newErrors).length > 0) return;

    // 타입별 connectionInfo 구조 생성
    let connectionInfo: any = {};

    if (selectedTemplate === 'Milvus') {
      // 밀버스 타입 필드 구조
      connectionInfo = {
        host: connectionFields.host || '',
        port: connectionFields.port || '',
        user: connectionFields.user || '',
        password: connectionFields.password || '',
        secure: connectionFields.secure || 'False',
        dbName: connectionFields.dbName || 'default',
      };
    } else if (selectedTemplate === 'ElasticSearch') {
      // 엘라스틱서치 타입 필드 구조 - 실제 필드명에 따라 매핑
      connectionInfo = {
        apiKey: connectionFields.apiKey || connectionFields.api_key || '',
        endpoint: connectionFields.endpoint || '',
      };
    } else {
      // 기타 타입은 기존 방식 유지
      connectionInfo = { ...connectionFields };
    }

    // 통과 시 생성 호출
    createVectorDB({
      name: vectorDBName.trim(),
      isDefault: isDefault ? 'True' : 'False', // 문자열로 변경
      type: selectedTemplate,
      connectionInfo: connectionInfo,
    } as any); // 타입 단언으로 임시 해결
  };

  const handleClose = () => {
    setVectorDBName('');
    setSelectedTemplate('');
    setHasAttemptedSave(false);
    setConnectionFields({});
    setErrors({});
    setIsDefault(false);
    onClose();
  };

  const handleCancel = () => {
    handleClose();
  };

  const handlePreviousStep = () => {
    onPreviousStep();
  };

  return (
    <UILayerPopup
      isOpen={isOpen}
      onClose={handleClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        /* 좌측 Step 영역 콘텐츠 */
        <UIPopupAside>
          {/* 레이어 팝업 헤더 */}
          <UIPopupHeader title={<>벡터 DB 만들기</>} description='' position='left' />
          {/* 레이어 팝업 바디 */}
          <UIPopupBody>{/* <UIArticle>바디 영역</UIArticle> */}</UIPopupBody>
          {/* 레이어 팝업 바디 : [참고] 이 페이지에는 왼쪽 body 영역 없음. */}
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handleCancel}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={!isFormValid} onClick={handleSave}>
                  만들기
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      {/* 👉 가운데 콘텐츠 부분만 새 디자인 적용 */}
      <section className='section-popup-content'>
        <UIPopupBody>
          {/* 이름 입력 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                이름
              </UITypography>
              <UIInput.Text
                value={vectorDBName}
                maxLength={50}
                onChange={e => {
                  setVectorDBName(e.target.value);
                  if (hasAttemptedSave) {
                    setErrors(prev => ({
                      ...prev,
                      name: e.target.value.trim() ? '' : '이름을 입력해 주세요.',
                    }));
                  }
                }}
                placeholder='이름 입력'
              />
              {errors.name && <p className='text-red-500 text-sm mt-1'>{errors.name}</p>}
            </UIFormField>
          </UIArticle>

          {/* 유형 선택 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4'>유형 선택</UITypography>
              <UIDropdown value={selectedTemplate} onSelect={handleTemplateSelect} placeholder='유형 선택' options={toolTypeOptions} />
            </UIFormField>
          </UIArticle>

          {/* 동적 필드 */}
          {loadersArray.length === 0 ? <p>사용 가능한 유형이 없습니다.</p> : selectedLoader ? renderConnectionFields(selectedLoader) : <p>로딩 중...</p>}

          {/* Default 라디오 */}
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' required>
                Default
              </UITypography>
              <UIUnitGroup gap={12} direction='row'>
                <UIRadio2 name='default' value='true' label='True' checked={isDefault} onChange={() => setIsDefault(true)} />
                <UIRadio2 name='default' value='false' label='False' checked={!isDefault} onChange={() => setIsDefault(false)} />
              </UIUnitGroup>
            </UIFormField>
          </UIArticle>
        </UIPopupBody>

        {/* Footer 영역 */}
        <UIPopupFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='start'>
              <UIButton2 className='btn-secondary-gray' onClick={handlePreviousStep}>
                이전
              </UIButton2>
            </UIUnitGroup>
          </UIArticle>
        </UIPopupFooter>
      </section>
    </UILayerPopup>
  );
}
