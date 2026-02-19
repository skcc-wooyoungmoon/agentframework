import { UIButton2, UIRadio2, UITypography } from '@/components/UI/atoms';
import {
  UIArticle,
  UIDropdown,
  UIFormField,
  UIInput,
  UIPopupBody,
  UIPopupFooter,
  UIPopupHeader,
  UIStepper,
  UITextArea2,
  UIUnitGroup,
  type UIStepperItem,
} from '@/components/UI/molecules';
import { UILayerPopup, UIPopupAside } from '@/components/UI/organisms';
import { useAtom } from 'jotai';
import { DataAtom } from '@/stores/data/dataStore';
import type { DataType } from '@/stores/data/type';

{
  /* 학습 데이터세트 생성 Step 1 컴포넌트 */
}
interface DataSetStep1DataDetailPopupPageProps {
  isOpen: boolean;
  stepperItems: UIStepperItem[];
  handlePopupClose: () => void;
  onNextStep: () => void;
  onPreviousStep: () => void;
}

export function DataSetStep1DataDetailPopupPage({ isOpen, stepperItems = [], handlePopupClose, onNextStep, onPreviousStep }: DataSetStep1DataDetailPopupPageProps) {
  const [dataForm, setDataForm] = useAtom(DataAtom);

  const handleRadioChange = (value: string) => {
    setDataForm(prev => ({
      ...prev,
      dataType: value as DataType,
      dataset: value === 'customDataSet' ? 'Custom' : '지도학습',
      files: [],
      uploadedFiles: [],
      selectedStorageData: [],
      uploadedFileInfos: [],
    }));
  };

  return (
    <UILayerPopup
      isOpen={isOpen}
      onClose={handlePopupClose}
      size='fullscreen'
      showOverlay={true}
      leftContent={
        <UIPopupAside>
          <UIPopupHeader title='학습 데이터세트 생성' description='' position='left' />
          <UIPopupBody>
            <UIArticle>
              <UIStepper items={dataForm.dataType === 'basicDataSet' ? stepperItems : stepperItems.slice(0, 3)} currentStep={1} direction='vertical' />
            </UIArticle>
          </UIPopupBody>
          <UIPopupFooter>
            <UIArticle>
              <UIUnitGroup gap={8} direction='row' align='start'>
                <UIButton2 className='btn-tertiary-gray' style={{ width: '80px' }} onClick={handlePopupClose}>
                  취소
                </UIButton2>
                <UIButton2 className='btn-tertiary-blue' style={{ width: '80px' }} disabled={true}>
                  만들기
                </UIButton2>
              </UIUnitGroup>
            </UIArticle>
          </UIPopupFooter>
        </UIPopupAside>
      }
    >
      <section className='section-popup-content'>
        <UIPopupHeader title='데이터 정보입력' description='' position='right' />
        <UIPopupBody>
          <UIArticle>
            <UIFormField gap={12} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                학습 데이터세트 생성 방식
              </UITypography>
              <UIUnitGroup gap={12} direction='column' align='start'>
                <UIRadio2
                  name='basicDataSet'
                  value='basicDataSet'
                  label='기본 학습 데이터세트'
                  checked={dataForm.dataType === 'basicDataSet'}
                  onChange={(checked, value) => {
                    if (checked) handleRadioChange(value);
                  }}
                />
                <UIRadio2
                  name='customDataSet'
                  value='customDataSet'
                  label='Custom 학습 데이터세트'
                  checked={dataForm.dataType === 'customDataSet'}
                  onChange={(checked, value) => {
                    if (checked) handleRadioChange(value);
                  }}
                />
              </UIUnitGroup>
            </UIFormField>
          </UIArticle>
          {/* 데이터 세트 유형 드롭다운 - Custom 학습 데이터세트가 아닐 때만 표시 */}
          {dataForm.dataType !== 'customDataSet' && (
            <UIArticle>
              <UIFormField gap={8} direction='column'>
                <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                  데이터 세트 유형
                </UITypography>
                <UIDropdown
                  value={dataForm.dataset}
                  options={
                    dataForm.dataType === 'basicDataSet'
                      ? [
                          { value: '지도학습', label: '지도학습' },
                          { value: '비지도학습', label: '비지도학습' },
                          { value: 'DPO', label: 'DPO' },
                        ]
                      : [
                          { value: '지도학습', label: '지도학습' },
                          { value: '비지도학습', label: '비지도학습' },
                          { value: 'DPO', label: 'DPO' },
                          { value: 'Custom', label: 'Custom' },
                        ]
                  }
                  onSelect={(value: string) => {
                    setDataForm(prev => ({
                      ...prev,
                      dataset: value,
                      files: [],
                      uploadedFiles: [],
                      selectedStorageData: [],
                      uploadedFileInfos: [],
                    }));
                  }}
                  placeholder='데이터 세트 유형 선택'
                />
              </UIFormField>
            </UIArticle>
          )}

          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb' required={true}>
                이름
              </UITypography>
              <UIInput.Text value={dataForm.name} placeholder='이름 입력' onChange={e => setDataForm({ ...dataForm, name: e.target.value })} maxLength={30} />
            </UIFormField>
          </UIArticle>
          <UIArticle>
            <UIFormField gap={8} direction='column'>
              <UITypography variant='title-4' className='secondary-neutral-800 text-title-4-sb'>
                설명
              </UITypography>
              <UITextArea2 value={dataForm.description} onChange={e => setDataForm({ ...dataForm, description: e.target.value })} placeholder='설명 입력' maxLength={100} />
            </UIFormField>
          </UIArticle>

          <UIArticle>
            <UIInput.Tags tags={dataForm.tags} onChange={tags => setDataForm({ ...dataForm, tags })} label='태그' />
          </UIArticle>
        </UIPopupBody>
        <UIPopupFooter>
          <UIArticle>
            <UIUnitGroup gap={8} direction='row' align='start'>
              <UIButton2 className='btn-secondary-gray' style={{ width: '80px' }} onClick={onPreviousStep}>
                이전
              </UIButton2>
              <UIButton2 className='btn-secondary-blue' style={{ width: '80px' }} disabled={!dataForm.name || dataForm.name.trim() === ''} onClick={onNextStep}>
                다음
              </UIButton2>
            </UIUnitGroup>
          </UIArticle>
        </UIPopupFooter>
      </section>
    </UILayerPopup>
  );
}
