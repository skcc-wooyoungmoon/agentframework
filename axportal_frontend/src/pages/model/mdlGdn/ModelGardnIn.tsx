import { type LayerPopupProps } from '@/hooks/common/layer';
import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { type ModelGardenInfo } from '@/services/model/garden/types';
import { useEffect, useState } from 'react';
import { MdGdnImpStep1TypePickPopup } from './MdGdnImpStep1TypePickPopup';
import { MdGdnImpStep2Popup } from './MdGdnImpStep2Popup';
import { MdGdnImpStep2SvlsPopup } from './MdGdnImpStep2SvlsPopup';
import { MdGdnImpStep3Popup } from './MdGdnImpStep3Popup';

export type ModelGardenInStepProps = ModelGardenInProps & {
  info: ModelGardenInfo;
  onSetInfo: (value: Partial<ModelGardenInfo>) => void;
  onComplete?: (id: string) => void;
};

export type ModelGardenInProps = LayerPopupProps & {
  onComplete?: (id: string) => void;
};

const initialInfo: ModelGardenInfo = {
  id: '',
  artifact_id: '',
  revision_id: '',
  name: '',
  description: '',
  size: '0',
  param_size: '',
  serving_type: 'self-hosting',
  version: '',
  provider: '',
  providerId: '',
  type: '',
  license: '',
  readme: '',
  tags: '',
  langauges: '',
  url: '',
  identifier: '',
  statusNm: '',
  doipAt: '',
  doipMn: '',
  chkAt: '',
  chkMn: '',
  created_at: '',
  updated_at: '',
  created_by: '',
  updated_by: '',
  deleted: '',
};

/**
 * @author SGO1032948
 * @description 모델가든 반입 팝업
 *
 * MD_050101_P04
 * MD_050101_P02
 * MD_050101_P03
 * MD_050101_P07
 */
export const ModelGardnIn = ({ selectedData, ...props }: ModelGardenInProps & { selectedData: ModelGardenInfo | undefined }) => {
  const [info, setInfo] = useState<ModelGardenInfo>(selectedData ? selectedData : initialInfo);
  const { showCancelConfirm } = useCommonPopup();

  useEffect(() => {
    if (props.currentStep === 0 || props.currentStep === 1) {
      setInfo(initialInfo);
    }
  }, [props.currentStep]);

  const handleSetInfo = (value: Partial<ModelGardenInfo>) => {
    setInfo({
      ...(info as ModelGardenInfo),
      ...value,
    });
  };

  const handleComplete = (id: string) => {
    props.onComplete?.(id);
    props.onClose();
  };

  const handleClose = () => {
    showCancelConfirm({
      onConfirm: () => {
        props.onClose();
      },
    });
  };

  return (
    <>
      <MdGdnImpStep1TypePickPopup {...props} info={info} onSetInfo={handleSetInfo} onClose={handleClose} />
      {info.serving_type === 'self-hosting' ? (
        <>
          {props.currentStep === 2 && <MdGdnImpStep2Popup {...props} info={info} onSetInfo={handleSetInfo} onClose={handleClose} />}
          {props.currentStep === 3 && <MdGdnImpStep3Popup {...props} info={info} onSetInfo={handleSetInfo} onClose={handleClose} onComplete={handleComplete} />}
        </>
      ) : (
        <>{props.currentStep === 2 && <MdGdnImpStep2SvlsPopup {...props} info={info} onSetInfo={handleSetInfo} onClose={handleClose} onComplete={handleComplete} />}</>
      )}
    </>
  );
};
