import { useState } from 'react';

import type { LayerPopupProps } from '@/hooks/common/layer';
import type { ModelGardenArtifactInfo } from '@/services/model/garden/types';

import { useCommonPopup } from '@/hooks/common/popup/useCommonPopup';
import { MdGdnAddStep1AddPickPopup } from './MdGdnAddStep1AddPickPopup';
import { MdGdnAddStep2InfoChkPopup } from './MdGdnAddStep2InfoChkPopup';

export type ModelGardenSearchStepProps = ModelGardenSearchProps & {
  data: ModelGardenArtifactInfo | undefined;
  setData?: (data: ModelGardenArtifactInfo) => void;
};

export type ModelGardenSearchProps = LayerPopupProps & {
  onComplete?: (id: string) => void;
};

/**
 * @author SGO1032948
 * @description 모델가든 검색 팝업
 *
 * MD_050101_P05
 * MD_050101_P06
 */
export const MdGdnAdd = ({ ...props }: ModelGardenSearchProps) => {
  const [data, setData] = useState<ModelGardenArtifactInfo | undefined>(undefined);
  const { showCancelConfirm } = useCommonPopup();

  const handleClose = () => {
    showCancelConfirm({
      onConfirm: () => {
        props.onClose();
      },
    });
  };

  const handleComplete = (id: string) => {
    props.onClose();
    props.onComplete?.(id);
  };

  return (
    <>
      <MdGdnAddStep1AddPickPopup {...props} data={data} setData={setData} onClose={handleClose} />
      <MdGdnAddStep2InfoChkPopup {...props} data={data} onClose={handleClose} onComplete={handleComplete} />
    </>
  );
};
